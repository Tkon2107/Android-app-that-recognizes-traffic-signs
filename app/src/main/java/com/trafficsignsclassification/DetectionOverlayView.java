package com.trafficsignsclassification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view overlay for drawing YOLO detection bounding boxes on top of camera preview.
 */
public class DetectionOverlayView extends View {

    private List<YoloV11Detector.Detection> detections = new ArrayList<>();
    private final Paint boxPaint;
    private final Paint textPaint;
    private final Paint textBgPaint;

    // Scaling factors to map detection coordinates to view coordinates
    private int sourceWidth = 1;
    private int sourceHeight = 1;

    // Colors for different detections
    private static final int[] COLORS = {
            Color.rgb(255, 0, 0),     // Red
            Color.rgb(0, 255, 0),     // Green
            Color.rgb(0, 0, 255),     // Blue
            Color.rgb(255, 165, 0),   // Orange
            Color.rgb(255, 255, 0),   // Yellow
            Color.rgb(0, 255, 255),   // Cyan
            Color.rgb(255, 0, 255),   // Magenta
            Color.rgb(128, 0, 255),   // Purple
            Color.rgb(0, 128, 255),   // Sky blue
            Color.rgb(255, 128, 0),   // Dark orange
    };

    public DetectionOverlayView(Context context) {
        this(context, null);
    }

    public DetectionOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(4f);
        boxPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32f);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        textBgPaint = new Paint();
        textBgPaint.setStyle(Paint.Style.FILL);
        textBgPaint.setAntiAlias(true);
    }

    /**
     * Update detections and trigger redraw.
     * @param detections List of detections in original image coordinates
     * @param imageWidth Original image width
     * @param imageHeight Original image height
     */
    public void setDetections(List<YoloV11Detector.Detection> detections,
                               int imageWidth, int imageHeight) {
        this.detections = detections != null ? detections : new ArrayList<>();
        this.sourceWidth = imageWidth;
        this.sourceHeight = imageHeight;
        postInvalidate();
    }

    public void clear() {
        this.detections = new ArrayList<>();
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (detections.isEmpty()) return;

        int viewW = getWidth();
        int viewH = getHeight();
        if (viewW == 0 || viewH == 0) return;

        // Calculate scale to map from source image coordinates to view coordinates
        // Use "fill" scaling to match how CameraX PreviewView displays (CENTER_CROP)
        float scaleX = (float) viewW / sourceWidth;
        float scaleY = (float) viewH / sourceHeight;

        for (int i = 0; i < detections.size(); i++) {
            YoloV11Detector.Detection det = detections.get(i);
            int color = COLORS[det.classIndex % COLORS.length];

            // Scale bounding box to view coordinates
            float left = det.bbox.left * scaleX;
            float top = det.bbox.top * scaleY;
            float right = det.bbox.right * scaleX;
            float bottom = det.bbox.bottom * scaleY;

            // Draw bounding box
            boxPaint.setColor(color);
            canvas.drawRect(left, top, right, bottom, boxPaint);

            // Prepare label text
            String text = det.label + " " + String.format("%.0f%%", det.confidence * 100);
            float textWidth = textPaint.measureText(text);
            float textHeight = textPaint.getTextSize();

            // Draw text background
            float bgTop = Math.max(0, top - textHeight - 8);
            textBgPaint.setColor(color);
            canvas.drawRect(left, bgTop, left + textWidth + 12, bgTop + textHeight + 8, textBgPaint);

            // Draw text
            canvas.drawText(text, left + 6, bgTop + textHeight, textPaint);
        }
    }
}
