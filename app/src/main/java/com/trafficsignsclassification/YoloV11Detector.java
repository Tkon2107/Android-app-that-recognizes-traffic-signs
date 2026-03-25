package com.trafficsignsclassification;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * YOLOv11 object detector using ONNX Runtime.
 * Preprocessing and postprocessing match Ultralytics Python implementation
 * to minimize inference differences.
 */
public class YoloV11Detector {
    private static final String TAG = "YoloV11Detector";

    private static final String MODEL_FILE = "best.onnx";
    private static final String LABELS_FILE = "yolo_labels.txt";
    private static final int INPUT_SIZE = 640;
    private static final int NUM_CHANNELS = 3;

    // Post-processing thresholds (match Ultralytics defaults)
    private float confThreshold = 0.25f;
    private float iouThreshold = 0.45f;

    private OrtEnvironment env;
    private OrtSession session;
    private List<String> labels;
    private int numClasses;

    public YoloV11Detector(Context context) {
        loadModel(context);
        loadLabels(context);
    }

    private void loadModel(Context context) {
        try {
            env = OrtEnvironment.getEnvironment();

            // Load model from assets
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(MODEL_FILE);
            byte[] modelBytes = new byte[is.available()];
            int totalRead = 0;
            while (totalRead < modelBytes.length) {
                int bytesRead = is.read(modelBytes, totalRead, modelBytes.length - totalRead);
                if (bytesRead == -1) break;
                totalRead += bytesRead;
            }
            is.close();

            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);
            // Use NNAPI delegate for hardware acceleration on Android
            // options.addNnapi(); // Uncomment if NNAPI gives consistent results

            session = env.createSession(modelBytes, options);
            Log.d(TAG, "ONNX model loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error loading ONNX model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLabels(Context context) {
        labels = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(LABELS_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    labels.add(line);
                }
            }
            reader.close();
            numClasses = labels.size();
            Log.d(TAG, "Labels loaded: " + numClasses + " classes");
        } catch (IOException e) {
            Log.e(TAG, "Error loading labels: " + e.getMessage());
        }
    }

    /**
     * Run detection on a Bitmap.
     * Returns list of Detection objects with bounding boxes in original image coordinates.
     */
    public List<Detection> detect(Bitmap bitmap) {
        if (bitmap == null || session == null) {
            return Collections.emptyList();
        }

        try {
            int origWidth = bitmap.getWidth();
            int origHeight = bitmap.getHeight();

            // Step 1: Letterbox preprocessing (matches Ultralytics exactly)
            LetterboxInfo letterbox = letterboxPreprocess(bitmap);

            // Step 2: Run inference
            float[][][] rawOutput = runInference(letterbox.inputBuffer);

            // Step 3: Post-process (transpose, filter, NMS)
            List<Detection> detections = postProcess(rawOutput, letterbox, origWidth, origHeight);

            return detections;

        } catch (Exception e) {
            Log.e(TAG, "Error during detection: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Letterbox resize matching Ultralytics implementation.
     * Maintains aspect ratio with gray padding (114/255).
     */
    private LetterboxInfo letterboxPreprocess(Bitmap bitmap) {
        int origW = bitmap.getWidth();
        int origH = bitmap.getHeight();

        // Calculate scale to fit within INPUT_SIZE while maintaining aspect ratio
        float scale = Math.min((float) INPUT_SIZE / origW, (float) INPUT_SIZE / origH);

        int newW = Math.round(origW * scale);
        int newH = Math.round(origH * scale);

        // Padding to center the image
        int padW = (INPUT_SIZE - newW) / 2;
        int padH = (INPUT_SIZE - newH) / 2;

        // Resize bitmap
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, newW, newH, true);

        // Create padded image with gray fill (114, 114, 114) - Ultralytics default
        float padValue = 114.0f / 255.0f;
        FloatBuffer buffer = FloatBuffer.allocate(NUM_CHANNELS * INPUT_SIZE * INPUT_SIZE);

        // Fill with padding value
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(padValue);
        }

        // Get resized pixel data
        int[] pixels = new int[newW * newH];
        resized.getPixels(pixels, 0, newW, 0, 0, newW, newH);

        // Fill in the actual image data (CHW format, RGB, normalized to [0, 1])
        // Channel order: R, G, B (matching Ultralytics)
        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < newW; x++) {
                int pixel = pixels[y * newW + x];
                float r = ((pixel >> 16) & 0xFF) / 255.0f;
                float g = ((pixel >> 8) & 0xFF) / 255.0f;
                float b = (pixel & 0xFF) / 255.0f;

                int dy = y + padH;
                int dx = x + padW;

                // CHW layout: channel * H * W + y * W + x
                buffer.put(0 * INPUT_SIZE * INPUT_SIZE + dy * INPUT_SIZE + dx, r);
                buffer.put(1 * INPUT_SIZE * INPUT_SIZE + dy * INPUT_SIZE + dx, g);
                buffer.put(2 * INPUT_SIZE * INPUT_SIZE + dy * INPUT_SIZE + dx, b);
            }
        }

        buffer.rewind();

        if (resized != bitmap) {
            resized.recycle();
        }

        return new LetterboxInfo(buffer, scale, padW, padH);
    }

    /**
     * Run ONNX inference.
     * Input: [1, 3, 640, 640], Output: [1, 56, 8400]
     */
    private float[][][] runInference(FloatBuffer inputBuffer) throws Exception {
        long[] inputShape = {1, NUM_CHANNELS, INPUT_SIZE, INPUT_SIZE};
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputBuffer, inputShape);

        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put(session.getInputNames().iterator().next(), inputTensor);

        OrtSession.Result result = session.run(inputs);

        // Output shape: [1, 4+numClasses, 8400]
        float[][][] output = (float[][][]) result.get(0).getValue();

        inputTensor.close();
        result.close();

        return output;
    }

    /**
     * Post-process raw output: transpose, filter by confidence, apply NMS.
     * Matches Ultralytics postprocess logic exactly.
     */
    private List<Detection> postProcess(float[][][] rawOutput, LetterboxInfo letterbox,
                                         int origWidth, int origHeight) {
        // rawOutput shape: [1, 4+numClasses, 8400]
        float[][] data = rawOutput[0]; // [56, 8400]
        int numDetections = data[0].length; // 8400
        int numAttrs = data.length; // 56 = 4 + 52

        List<Detection> candidates = new ArrayList<>();

        for (int i = 0; i < numDetections; i++) {
            // Get box coordinates (center x, center y, width, height) in letterbox coordinates
            float cx = data[0][i];
            float cy = data[1][i];
            float w = data[2][i];
            float h = data[3][i];

            // Find max class score
            float maxScore = 0;
            int maxClassIdx = 0;
            for (int c = 4; c < numAttrs; c++) {
                if (data[c][i] > maxScore) {
                    maxScore = data[c][i];
                    maxClassIdx = c - 4;
                }
            }

            if (maxScore < confThreshold) continue;

            // Convert from center format to corner format (in letterbox coords)
            float x1 = cx - w / 2;
            float y1 = cy - h / 2;
            float x2 = cx + w / 2;
            float y2 = cy + h / 2;

            // Remove letterbox padding and scale back to original image coordinates
            x1 = (x1 - letterbox.padW) / letterbox.scale;
            y1 = (y1 - letterbox.padH) / letterbox.scale;
            x2 = (x2 - letterbox.padW) / letterbox.scale;
            y2 = (y2 - letterbox.padH) / letterbox.scale;

            // Clip to original image bounds
            x1 = Math.max(0, Math.min(x1, origWidth));
            y1 = Math.max(0, Math.min(y1, origHeight));
            x2 = Math.max(0, Math.min(x2, origWidth));
            y2 = Math.max(0, Math.min(y2, origHeight));

            String label = (maxClassIdx < labels.size()) ? labels.get(maxClassIdx) : "class_" + maxClassIdx;
            candidates.add(new Detection(new RectF(x1, y1, x2, y2), maxScore, maxClassIdx, label));
        }

        // Apply NMS
        return nms(candidates, iouThreshold);
    }

    /**
     * Non-Maximum Suppression per class.
     */
    private List<Detection> nms(List<Detection> detections, float iouThresh) {
        if (detections.isEmpty()) return detections;

        // Sort by confidence descending
        Collections.sort(detections, (a, b) -> Float.compare(b.confidence, a.confidence));

        // Group by class
        Map<Integer, List<Detection>> byClass = new HashMap<>();
        for (Detection d : detections) {
            byClass.computeIfAbsent(d.classIndex, k -> new ArrayList<>()).add(d);
        }

        List<Detection> results = new ArrayList<>();
        for (List<Detection> classDets : byClass.values()) {
            List<Detection> kept = new ArrayList<>();
            boolean[] suppressed = new boolean[classDets.size()];

            for (int i = 0; i < classDets.size(); i++) {
                if (suppressed[i]) continue;
                kept.add(classDets.get(i));

                for (int j = i + 1; j < classDets.size(); j++) {
                    if (suppressed[j]) continue;
                    if (iou(classDets.get(i).bbox, classDets.get(j).bbox) > iouThresh) {
                        suppressed[j] = true;
                    }
                }
            }
            results.addAll(kept);
        }

        // Sort final results by confidence
        Collections.sort(results, (a, b) -> Float.compare(b.confidence, a.confidence));
        return results;
    }

    /**
     * Compute Intersection over Union.
     */
    private float iou(RectF a, RectF b) {
        float interLeft = Math.max(a.left, b.left);
        float interTop = Math.max(a.top, b.top);
        float interRight = Math.min(a.right, b.right);
        float interBottom = Math.min(a.bottom, b.bottom);

        float interArea = Math.max(0, interRight - interLeft) * Math.max(0, interBottom - interTop);
        float aArea = (a.right - a.left) * (a.bottom - a.top);
        float bArea = (b.right - b.left) * (b.bottom - b.top);

        return interArea / (aArea + bArea - interArea + 1e-6f);
    }

    public void setConfThreshold(float threshold) {
        this.confThreshold = threshold;
    }

    public void setIouThreshold(float threshold) {
        this.iouThreshold = threshold;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void close() {
        try {
            if (session != null) session.close();
            if (env != null) env.close();
        } catch (Exception e) {
            Log.e(TAG, "Error closing ONNX session: " + e.getMessage());
        }
    }

    // --- Inner classes ---

    public static class Detection {
        public RectF bbox;        // Bounding box in original image coordinates
        public float confidence;  // Detection confidence score
        public int classIndex;    // Class index
        public String label;      // Class label string

        public Detection(RectF bbox, float confidence, int classIndex, String label) {
            this.bbox = bbox;
            this.confidence = confidence;
            this.classIndex = classIndex;
            this.label = label;
        }
    }

    private static class LetterboxInfo {
        FloatBuffer inputBuffer;
        float scale;
        int padW;
        int padH;

        LetterboxInfo(FloatBuffer inputBuffer, float scale, int padW, int padH) {
            this.inputBuffer = inputBuffer;
            this.scale = scale;
            this.padW = padW;
            this.padH = padH;
        }
    }
}
