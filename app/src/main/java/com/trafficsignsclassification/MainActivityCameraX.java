package com.trafficsignsclassification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.trafficsignsclassification.penalty.PenaltyDetailActivity;

import org.opencv.android.OpenCVLoader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivityCameraX extends AppCompatActivity {
    private static final String TAG = "MainActivityCameraX";
    private static final int CAMERA_REQUEST_CODE = 101;

    // UI Components
    private PreviewView previewView;
    private ImageView capturedImageView;
    private View cameraContainer;
    private View capturedContainer;
    private DetectionOverlayView detectionOverlay;
    private DetectionOverlayView capturedDetectionOverlay;
    private TextView classTextView, probabilityTextView, topPredictionsTextView;
    private ImageButton flashToggleButton;
    private MaterialButton captureButton, retakeButton, viewPenaltyButton;
    private ProgressBar loadingIndicator;
    private View permissionLayout, instructionLayout;

    // CameraX
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private Preview preview;
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;
    private ExecutorService cameraExecutor;

    // Detector (YOLO)
    private YoloV11Detector detector;
    private Handler mainHandler;

    // State
    private boolean isFlashOn = false;
    private boolean isLiveMode = true;
    private boolean isAnalyzing = false;
    private String currentSignLabel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camerax);

        // Initialize OpenCV
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded successfully");
        } else {
            Log.e(TAG, "Failed to load OpenCV");
        }

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.customBlack));

        // Initialize UI
        initializeViews();
        setupClickListeners();
        setupProjectName();

        // Initialize detector and executor
        detector = new YoloV11Detector(this);
        cameraExecutor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        cameraContainer = findViewById(R.id.cameraContainer);
        capturedContainer = findViewById(R.id.capturedContainer);
        detectionOverlay = findViewById(R.id.detectionOverlay);
        capturedDetectionOverlay = findViewById(R.id.capturedDetectionOverlay);
        classTextView = findViewById(R.id.classTextView);
        probabilityTextView = findViewById(R.id.probabilityTextView);
        topPredictionsTextView = findViewById(R.id.topPredictionsTextView);
        flashToggleButton = findViewById(R.id.flashToggleButton);
        captureButton = findViewById(R.id.captureButton);
        retakeButton = findViewById(R.id.retakeButton);
        viewPenaltyButton = findViewById(R.id.viewPenaltyButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        permissionLayout = findViewById(R.id.permissionLayout);
        instructionLayout = findViewById(R.id.instructionLayout);

        // Apply animations
        View appLogo = findViewById(R.id.appLogo);
        Animation fadeInScaleUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale_up);
        appLogo.startAnimation(fadeInScaleUp);
    }

    private void setupClickListeners() {
        flashToggleButton.setOnClickListener(v -> toggleFlashlight());
        captureButton.setOnClickListener(v -> captureImage());
        retakeButton.setOnClickListener(v -> returnToLiveMode());
        
        findViewById(R.id.openSettingsButton).setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });
    }

    private void setupProjectName() {
        TextView projectNameTextView = findViewById(R.id.projectNameTextView);
        String text = getString(R.string.project_name_beta);
        SpannableString spannableString = new SpannableString(text);
        int betaStartIndex = text.indexOf("BETA");
        if (betaStartIndex != -1) {
            spannableString.setSpan(new SuperscriptSpan(), betaStartIndex, 
                text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.5f), betaStartIndex, 
                text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        projectNameTextView.setText(spannableString);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
            ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
                showCameraViews();
            } catch (Exception e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) return;

        // Unbind all use cases
        cameraProvider.unbindAll();

        // Camera selector
        CameraSelector cameraSelector = new CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build();

        // Preview
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image capture
        imageCapture = new ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build();

        // Image analysis (for live mode)
        imageAnalysis = new ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        // Bind use cases
        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Error binding camera use cases: " + e.getMessage());
        }
    }

    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        if (!isLiveMode || isAnalyzing) {
            imageProxy.close();
            return;
        }

        isAnalyzing = true;

        try {
            Bitmap bitmap = ImageUtils.imageProxyToBitmap(imageProxy);
            if (bitmap != null) {
                final int imgWidth = bitmap.getWidth();
                final int imgHeight = bitmap.getHeight();
                List<YoloV11Detector.Detection> detections = detector.detect(bitmap);
                mainHandler.post(() -> updateDetectionUI(detections, imgWidth, imgHeight, false));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing image: " + e.getMessage());
        } finally {
            isAnalyzing = false;
            imageProxy.close();
        }
    }

    private void captureImage() {
        if (imageCapture == null) return;

        showLoading(true);

        imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                try {
                    Bitmap bitmap = ImageUtils.imageProxyToBitmap(imageProxy);
                    if (bitmap != null) {
                        final int imgWidth = bitmap.getWidth();
                        final int imgHeight = bitmap.getHeight();

                        // Switch to captured mode
                        mainHandler.post(() -> {
                            isLiveMode = false;
                            capturedImageView.setImageBitmap(bitmap);
                            cameraContainer.setVisibility(View.GONE);
                            capturedContainer.setVisibility(View.VISIBLE);
                            captureButton.setVisibility(View.GONE);
                            retakeButton.setVisibility(View.VISIBLE);
                            flashToggleButton.setVisibility(View.GONE);
                            detectionOverlay.clear();
                        });

                        // Run detection on the captured image
                        List<YoloV11Detector.Detection> detections = detector.detect(bitmap);

                        mainHandler.post(() -> {
                            showLoading(false);
                            capturedDetectionOverlay.setDetections(detections, imgWidth, imgHeight);
                            updateDetectionUI(detections, imgWidth, imgHeight, true);
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing captured image: " + e.getMessage());
                    mainHandler.post(() -> showLoading(false));
                } finally {
                    imageProxy.close();
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Image capture failed: " + exception.getMessage());
                mainHandler.post(() -> {
                    showLoading(false);
                    Toast.makeText(MainActivityCameraX.this, 
                        "Lỗi khi chụp ảnh", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void returnToLiveMode() {
        isLiveMode = true;
        capturedContainer.setVisibility(View.GONE);
        cameraContainer.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.GONE);
        flashToggleButton.setVisibility(View.VISIBLE);
        capturedDetectionOverlay.clear();
        detectionOverlay.clear();
        
        // Hide penalty button
        if (viewPenaltyButton != null) {
            viewPenaltyButton.setVisibility(View.GONE);
        }
        
        // Show probability text in live mode
        probabilityTextView.setVisibility(View.VISIBLE);
        topPredictionsTextView.setVisibility(View.GONE);
        
        // Reset text
        classTextView.setText(R.string.class_label);
        probabilityTextView.setText(R.string.probability_label);
    }

    private void updateDetectionUI(List<YoloV11Detector.Detection> detections,
                                    int imageWidth, int imageHeight, boolean isCaptureMode) {
        int customBlack = ContextCompat.getColor(this, R.color.customBlack);
        int customGreen = ContextCompat.getColor(this, R.color.deep_green);

        boolean hasDetection = detections != null && !detections.isEmpty();

        if (isCaptureMode) {
            // CAPTURE MODE: Show top detection label, bounding boxes drawn by overlay
            probabilityTextView.setVisibility(View.GONE);
            topPredictionsTextView.setVisibility(View.GONE);

            if (hasDetection) {
                // Show the highest confidence detection
                YoloV11Detector.Detection topDet = detections.get(0);
                String finalLabel = topDet.label;
                currentSignLabel = finalLabel;

                String classLabelText = getString(R.string.class_label);
                SpannableString classText = new SpannableString(classLabelText + finalLabel);
                classText.setSpan(new ForegroundColorSpan(customGreen),
                    classLabelText.length(), classText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                classTextView.setText(classText);
                classTextView.setTextColor(customBlack);

                // Show count if multiple detections
                if (detections.size() > 1) {
                    topPredictionsTextView.setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < Math.min(detections.size(), 5); i++) {
                        YoloV11Detector.Detection d = detections.get(i);
                        sb.append(String.format("%s (%.0f%%)", d.label, d.confidence * 100));
                        if (i < Math.min(detections.size(), 5) - 1) sb.append("\n");
                    }
                    topPredictionsTextView.setText(sb.toString());
                }

                showPenaltyButton(currentSignLabel);
            } else {
                String finalLabel = getString(R.string.unknown_sign);
                String classLabelText = getString(R.string.class_label);
                SpannableString classText = new SpannableString(classLabelText + finalLabel);
                classText.setSpan(new ForegroundColorSpan(Color.RED),
                    classLabelText.length(), classText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                classTextView.setText(classText);
                classTextView.setTextColor(customBlack);
                currentSignLabel = "";

                if (viewPenaltyButton != null) {
                    viewPenaltyButton.setVisibility(View.GONE);
                }
            }

        } else {
            // LIVE MODE: Show detection info and update overlay
            probabilityTextView.setVisibility(View.VISIBLE);
            topPredictionsTextView.setVisibility(View.GONE);

            if (viewPenaltyButton != null) {
                viewPenaltyButton.setVisibility(View.GONE);
            }

            if (hasDetection) {
                // Update overlay with bounding boxes
                detectionOverlay.setDetections(detections, imageWidth, imageHeight);

                YoloV11Detector.Detection topDet = detections.get(0);

                // Class text
                String classLabelText = getString(R.string.class_label);
                String labelWithCount = detections.size() > 1
                    ? topDet.label + " (+" + (detections.size() - 1) + ")"
                    : topDet.label;
                SpannableString classText = new SpannableString(classLabelText + labelWithCount);
                classText.setSpan(new ForegroundColorSpan(customGreen),
                    classLabelText.length(), classText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                classTextView.setText(classText);
                classTextView.setTextColor(customBlack);

                // Probability text
                String probabilityLabelText = getString(R.string.probability_label);
                String probabilityValue = String.format(getString(R.string.probability_format),
                    topDet.confidence * 100);
                SpannableString probabilityText = new SpannableString(
                    probabilityLabelText + probabilityValue);
                probabilityText.setSpan(new ForegroundColorSpan(customGreen),
                    probabilityLabelText.length(), probabilityText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                probabilityTextView.setText(probabilityText);
                probabilityTextView.setTextColor(customBlack);
            } else {
                detectionOverlay.clear();

                SpannableString classText = new SpannableString(getString(R.string.class_na));
                int naIndex = classText.toString().indexOf("N/A");
                if (naIndex >= 0) {
                    classText.setSpan(new ForegroundColorSpan(Color.RED),
                        naIndex, classText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                classTextView.setText(classText);
                classTextView.setTextColor(customBlack);

                SpannableString probabilityText = new SpannableString(
                    getString(R.string.probability_na));
                int naIndexProb = probabilityText.toString().indexOf("N/A");
                if (naIndexProb >= 0) {
                    probabilityText.setSpan(new ForegroundColorSpan(Color.RED),
                        naIndexProb, probabilityText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                probabilityTextView.setText(probabilityText);
                probabilityTextView.setTextColor(customBlack);
            }
        }
    }
    
    private void showPenaltyButton(String signLabel) {
        if (viewPenaltyButton == null) return;
        
        viewPenaltyButton.setVisibility(View.VISIBLE);
        viewPenaltyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PenaltyDetailActivity.class);
            intent.putExtra(PenaltyDetailActivity.EXTRA_SIGN_LABEL, signLabel);
            startActivity(intent);
        });
    }

    private void toggleFlashlight() {
        if (camera == null || !camera.getCameraInfo().hasFlashUnit()) {
            Toast.makeText(this, R.string.device_no_flashlight, Toast.LENGTH_SHORT).show();
            return;
        }

        isFlashOn = !isFlashOn;
        camera.getCameraControl().enableTorch(isFlashOn);
        flashToggleButton.setImageResource(isFlashOn ? 
            R.drawable.outline_flash_off_24 : R.drawable.outline_flash_on_24);
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showCameraViews() {
        cameraContainer.setVisibility(View.VISIBLE);
        flashToggleButton.setVisibility(View.VISIBLE);
        classTextView.setVisibility(View.VISIBLE);
        probabilityTextView.setVisibility(View.VISIBLE);
        instructionLayout.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);
        permissionLayout.setVisibility(View.GONE);
    }

    private void hideCameraViews() {
        cameraContainer.setVisibility(View.GONE);
        flashToggleButton.setVisibility(View.GONE);
        classTextView.setVisibility(View.GONE);
        probabilityTextView.setVisibility(View.GONE);
        instructionLayout.setVisibility(View.GONE);
        captureButton.setVisibility(View.GONE);
        permissionLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                hideCameraViews();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (detector != null) {
            detector.close();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFlashOn && camera != null) {
            camera.getCameraControl().enableTorch(false);
            isFlashOn = false;
        }
    }
}
