# Complete Integration Example

This document shows a complete example of MainActivityCameraX with both penalty lookup and performance monitoring integrated.

## Complete MainActivityCameraX.java (Integrated Version)

```java
package com.trafficsignsclassification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.trafficsignsclassification.penalty.ui.PenaltyDetailActivity;
import com.trafficsignsclassification.evaluation.runtime.PerformanceMonitor;
import com.trafficsignsclassification.evaluation.runtime.PerformanceOverlayView;
import com.trafficsignsclassification.evaluation.runtime.InferenceTimer;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivityCameraX extends AppCompatActivity {

    private static final String TAG = "MainActivityCameraX";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    // UI Components
    private PreviewView previewView;
    private ImageView capturedImageView;
    private TextView classTextView;
    private TextView probabilityTextView;
    private TextView topPredictionsTextView;
    private MaterialButton captureButton;
    private MaterialButton retakeButton;
    private MaterialButton viewPenaltyButton;
    private ImageButton flashToggleButton;
    private ProgressBar loadingIndicator;
    private LinearLayout permissionLayout;

    // Camera
    private Camera camera;
    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalysis;
    private ExecutorService cameraExecutor;

    // ML Classifier
    private TrafficSignClassifier classifier;

    // State
    private boolean isCaptureMode = false;
    private Bitmap capturedBitmap = null;
    private boolean isFlashOn = false;
    private String currentSignLabel = "";

    // Performance Monitoring (Debug only)
    private PerformanceMonitor performanceMonitor;
    private PerformanceOverlayView performanceOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camerax);

        // Initialize UI components
        initializeViews();

        // Initialize classifier
        try {
            classifier = new TrafficSignClassifier(this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize classifier", e);
            Toast.makeText(this, "Lỗi khởi tạo classifier", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Initialize performance monitor (debug only)
        if (BuildConfig.ENABLE_PERFORMANCE_OVERLAY) {
            performanceMonitor = new PerformanceMonitor();
            setupPerformanceOverlay();
        }

        // Check camera permission
        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }

        // Setup button listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        classTextView = findViewById(R.id.classTextView);
        probabilityTextView = findViewById(R.id.probabilityTextView);
        topPredictionsTextView = findViewById(R.id.topPredictionsTextView);
        captureButton = findViewById(R.id.captureButton);
        retakeButton = findViewById(R.id.retakeButton);
        viewPenaltyButton = findViewById(R.id.viewPenaltyButton);
        flashToggleButton = findViewById(R.id.flashToggleButton);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        permissionLayout = findViewById(R.id.permissionLayout);
    }

    private void setupButtonListeners() {
        captureButton.setOnClickListener(v -> captureImage());
        retakeButton.setOnClickListener(v -> retakeImage());
        flashToggleButton.setOnClickListener(v -> toggleFlash());
    }

    private void setupPerformanceOverlay() {
        performanceOverlay = findViewById(R.id.performanceOverlay);
        if (performanceOverlay != null) {
            performanceOverlay.setVisibility(View.VISIBLE);
            startPerformanceUpdates();
        }
    }

    private void startPerformanceUpdates() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (performanceMonitor != null && performanceOverlay != null) {
                    performanceOverlay.updateMetrics(
                        performanceMonitor.getMetrics().getValue(),
                        performanceMonitor.getMemoryUsageMB()
                    );
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                showPermissionDeniedUI();
            }
        }
    }

    private void showPermissionDeniedUI() {
        permissionLayout.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.GONE);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(this, "Lỗi khởi động camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        // Preview
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image Analysis
        imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        // Camera Selector
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Bind to lifecycle
        try {
            cameraProvider.unbindAll();
            camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
            );
        } catch (Exception e) {
            Log.e(TAG, "Error binding camera use cases", e);
        }
    }

    private void analyzeImage(ImageProxy imageProxy) {
        if (isCaptureMode) {
            imageProxy.close();
            return;
        }

        // Start timing (performance monitoring)
        InferenceTimer timer = null;
        if (performanceMonitor != null) {
            timer = performanceMonitor.startInference();
        }

        try {
            // Convert ImageProxy to Bitmap
            Bitmap bitmap = ImageUtils.imageProxyToBitmap(imageProxy);

            // Run classification
            List<Pair<String, Float>> results = classifier.classifyImage(bitmap);

            // Record inference completion
            if (performanceMonitor != null && timer != null) {
                performanceMonitor.recordInference(timer);
            }

            // Update UI with results
            if (!results.isEmpty()) {
                String label = results.get(0).first;
                float confidence = results.get(0).second;
                currentSignLabel = label;
                updateUI(label, confidence, false);
            }

            bitmap.recycle();
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing image", e);
        } finally {
            imageProxy.close();
        }
    }

    private void captureImage() {
        if (isCaptureMode) return;

        // Show loading
        loadingIndicator.setVisibility(View.VISIBLE);

        // Pause image analysis
        isCaptureMode = true;

        // Capture current frame
        cameraExecutor.execute(() -> {
            try {
                // Get current frame from preview
                // Note: In production, use ImageCapture use case for better quality
                Thread.sleep(100); // Small delay to ensure frame is ready

                runOnUiThread(() -> {
                    // For now, we'll analyze the next frame as captured
                    // In production, implement proper ImageCapture
                    Toast.makeText(this, "Đã chụp ảnh", Toast.LENGTH_SHORT).show();
                    loadingIndicator.setVisibility(View.GONE);

                    // Show retake button, hide capture button
                    captureButton.setVisibility(View.GONE);
                    retakeButton.setVisibility(View.VISIBLE);

                    // Show penalty button if we have a label
                    if (!currentSignLabel.isEmpty()) {
                        showPenaltyButton(currentSignLabel);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error capturing image", e);
                runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi chụp ảnh", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void retakeImage() {
        // Resume live mode
        isCaptureMode = false;

        // Show capture button, hide retake button
        captureButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.GONE);

        // Hide penalty button
        viewPenaltyButton.setVisibility(View.GONE);

        // Hide captured image, show preview
        capturedImageView.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);

        // Show probability again
        probabilityTextView.setVisibility(View.VISIBLE);
        topPredictionsTextView.setVisibility(View.GONE);
    }

    private void showPenaltyButton(String signLabel) {
        viewPenaltyButton.setVisibility(View.VISIBLE);
        viewPenaltyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PenaltyDetailActivity.class);
            intent.putExtra(PenaltyDetailActivity.EXTRA_SIGN_LABEL, signLabel);
            startActivity(intent);
        });
    }

    private void updateUI(String label, float confidence, boolean isCaptureMode) {
        runOnUiThread(() -> {
            if (isCaptureMode) {
                // Capture mode: only show label
                classTextView.setText("Loại: " + label);
                probabilityTextView.setVisibility(View.GONE);
                topPredictionsTextView.setVisibility(View.GONE);
            } else {
                // Live mode: show label and confidence
                classTextView.setText("Loại: " + label);
                probabilityTextView.setText(String.format("Độ chính xác: %.2f%%", confidence * 100));
                probabilityTextView.setVisibility(View.VISIBLE);
                topPredictionsTextView.setVisibility(View.GONE);
            }
        });
    }

    private void toggleFlash() {
        if (camera == null || !camera.getCameraInfo().hasFlashUnit()) {
            Toast.makeText(this, "Đèn pin không được hỗ trợ", Toast.LENGTH_SHORT).show();
            return;
        }

        isFlashOn = !isFlashOn;
        camera.getCameraControl().enableTorch(isFlashOn);

        // Update button icon
        if (isFlashOn) {
            flashToggleButton.setImageResource(R.drawable.outline_flash_on_24);
        } else {
            flashToggleButton.setImageResource(R.drawable.outline_flash_off_24);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (classifier != null) {
            classifier.close();
        }
        if (performanceMonitor != null) {
            performanceMonitor.reset();
        }
    }
}
```

---

## Key Integration Points

### 1. Performance Monitoring

```java
// Initialize (onCreate)
if (BuildConfig.ENABLE_PERFORMANCE_OVERLAY) {
    performanceMonitor = new PerformanceMonitor();
    setupPerformanceOverlay();
}

// Measure inference (analyzeImage)
InferenceTimer timer = null;
if (performanceMonitor != null) {
    timer = performanceMonitor.startInference();
}

// ... run classification ...

if (performanceMonitor != null && timer != null) {
    performanceMonitor.recordInference(timer);
}
```

### 2. Penalty Lookup

```java
// Show penalty button after capture
private void showPenaltyButton(String signLabel) {
    viewPenaltyButton.setVisibility(View.VISIBLE);
    viewPenaltyButton.setOnClickListener(v -> {
        Intent intent = new Intent(this, PenaltyDetailActivity.class);
        intent.putExtra(PenaltyDetailActivity.EXTRA_SIGN_LABEL, signLabel);
        startActivity(intent);
    });
}

// Call after capture
if (!currentSignLabel.isEmpty()) {
    showPenaltyButton(currentSignLabel);
}
```

### 3. Mode Switching

```java
// Capture mode
private void captureImage() {
    isCaptureMode = true;
    captureButton.setVisibility(View.GONE);
    retakeButton.setVisibility(View.VISIBLE);
    showPenaltyButton(currentSignLabel);
}

// Live mode
private void retakeImage() {
    isCaptureMode = false;
    captureButton.setVisibility(View.VISIBLE);
    retakeButton.setVisibility(View.GONE);
    viewPenaltyButton.setVisibility(View.GONE);
}
```

---

## Layout Updates Required

Add to `activity_main_camerax.xml`:

```xml
<!-- Penalty Button -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/viewPenaltyButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/view_penalty"
    android:textAllCaps="false"
    android:fontFamily="@font/netflix_bold"
    android:textColor="@color/white"
    app:backgroundTint="#FF5722"
    app:cornerRadius="50dp"
    android:visibility="gone"
    android:layout_below="@id/classTextView"
    android:layout_centerHorizontal="true"
    android:layout_marginTop="16dp" />

<!-- Performance Overlay (Debug Only) -->
<com.trafficsignsclassification.evaluation.runtime.PerformanceOverlayView
    android:id="@+id/performanceOverlay"
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    android:layout_alignParentTop="true"
    android:layout_alignParentEnd="true"
    android:layout_margin="8dp"
    android:visibility="gone" />
```

---

## Testing the Integration

### Test 1: Penalty Lookup

1. Run app
2. Point camera at traffic sign
3. Tap "Chụp ảnh"
4. Verify "Xem mức phạt" button appears
5. Tap button
6. Verify penalty details screen opens
7. Check all information displays correctly

### Test 2: Performance Monitor (Debug Build)

1. Build debug APK
2. Run app
3. Verify performance overlay appears in top-right
4. Point camera at signs
5. Verify metrics update in real-time:
   - FPS
   - Latency (mean, P95, P99)
   - Memory usage
6. Verify no crashes or UI blocking

### Test 3: Mode Switching

1. Start in live mode
2. Verify continuous classification
3. Tap "Chụp ảnh"
4. Verify mode switches to capture
5. Verify "Chụp lại" button appears
6. Tap "Chụp lại"
7. Verify returns to live mode

---

## Common Issues and Solutions

### Issue: Penalty button doesn't appear

**Solution:**
- Check `currentSignLabel` is not empty
- Verify `showPenaltyButton()` is called after capture
- Check button visibility in layout

### Issue: Performance overlay not visible

**Solution:**
- Ensure running debug build
- Check `BuildConfig.ENABLE_PERFORMANCE_OVERLAY` is true
- Verify overlay view exists in layout
- Check visibility is set to VISIBLE

### Issue: App crashes on capture

**Solution:**
- Check camera permissions granted
- Verify classifier is initialized
- Check ImageUtils.imageProxyToBitmap() works
- Add try-catch around capture logic

---

## Next Steps

1. **Test thoroughly** on multiple devices
2. **Populate penalty data** in `penalties.json`
3. **Optimize performance** based on metrics
4. **Add error handling** for edge cases
5. **Prepare for production** release

---

**This completes the integration! Your app now has penalty lookup and performance monitoring fully integrated.** 🚀
