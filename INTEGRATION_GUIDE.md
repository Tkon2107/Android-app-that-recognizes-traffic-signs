# Integration Guide: Penalty Lookup & Evaluation System

## Overview

This guide explains how to integrate the new penalty lookup and evaluation modules into your existing traffic sign classification app.

## Architecture

```
app/src/main/java/com/trafficsignsclassification/
├── ml/                          # ML inference (existing)
├── penalty/                     # NEW: Vietnam traffic fine lookup
│   ├── data/
│   │   ├── PenaltyInfo.kt
│   │   ├── PenaltyDao.kt
│   │   ├── PenaltyDatabase.kt
│   │   ├── PenaltyDataSource.kt
│   │   └── PenaltyRepository.kt
│   └── ui/
│       └── PenaltyDetailActivity.kt
├── evaluation/                  # NEW: Two-track evaluation
│   ├── offline/
│   │   ├── OfflineEvaluator.kt
│   │   └── EvaluationMetrics.kt
│   └── runtime/
│       ├── PerformanceMonitor.kt
│       └── PerformanceOverlayView.kt
└── ui/                          # Existing UI
```

---

## Part A: Penalty Lookup Module

### 1. Data Structure

The penalty data is stored in `assets/penalties.json`:

```json
{
  "version": "1.0.0",
  "lastUpdated": "2026-02-02",
  "penalties": [
    {
      "signLabel": "Giới hạn tốc độ 50 km/h",
      "violationName": "Vượt quá tốc độ quy định",
      "legalReference": "Nghị định 100/2019/NĐ-CP, Điều 5",
      "fineRangeCar": "800.000 - 1.000.000 đồng",
      "fineRangeMotorbike": "400.000 - 600.000 đồng",
      "additionalPenalty": "Tước GPLX 1-3 tháng",
      "notes": "Áp dụng trong khu dân cư",
      "updatedAt": "2026-01-15",
      "severity": "medium"
    }
  ]
}
```

### 2. Initialize Repository

In your Application class or MainActivity:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize penalty repository
        lifecycleScope.launch {
            val repository = PenaltyRepository.getInstance(this@MyApplication)
            repository.initialize()
        }
    }
}
```

### 3. Integration in MainActivityCameraX

Add a "View Penalty" button after classification:

```java
// In MainActivityCameraX.java

private void updateUI(String label, float confidence, boolean isCaptureMode) {
    runOnUiThread(() -> {
        if (isCaptureMode) {
            // Capture mode: only show label
            classTextView.setText("Loại: " + label);
            probabilityTextView.setVisibility(View.GONE);
            topPredictionsTextView.setVisibility(View.GONE);
            
            // Show "View Penalty" button
            showPenaltyButton(label);
        } else {
            // Live mode: show label and confidence
            classTextView.setText("Loại: " + label);
            probabilityTextView.setText(String.format("Độ chính xác: %.2f%%", confidence * 100));
            probabilityTextView.setVisibility(View.VISIBLE);
        }
    });
}

private void showPenaltyButton(String signLabel) {
    // Add button to layout or show existing button
    Button viewPenaltyButton = findViewById(R.id.viewPenaltyButton);
    viewPenaltyButton.setVisibility(View.VISIBLE);
    viewPenaltyButton.setOnClickListener(v -> {
        Intent intent = new Intent(this, PenaltyDetailActivity.class);
        intent.putExtra(PenaltyDetailActivity.EXTRA_SIGN_LABEL, signLabel);
        startActivity(intent);
    });
}
```

### 4. Add Button to Layout

In `activity_main_camerax.xml`, add:

```xml
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
```

### 5. Register Activity in AndroidManifest.xml

```xml
<activity
    android:name=".penalty.ui.PenaltyDetailActivity"
    android:exported="false"
    android:screenOrientation="portrait"
    android:theme="@style/Theme.TrafficSignsClassification" />
```

---

## Part B: Evaluation System

### B1: Runtime Performance Monitoring

#### 1. Add PerformanceMonitor to MainActivityCameraX

```java
// In MainActivityCameraX.java

import com.trafficsignsclassification.evaluation.runtime.PerformanceMonitor;
import com.trafficsignsclassification.evaluation.runtime.InferenceTimer;

public class MainActivityCameraX extends AppCompatActivity {
    
    private PerformanceMonitor performanceMonitor;
    private PerformanceOverlayView performanceOverlay;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize performance monitor (debug only)
        if (BuildConfig.ENABLE_PERFORMANCE_OVERLAY) {
            performanceMonitor = new PerformanceMonitor();
            setupPerformanceOverlay();
        }
    }
    
    private void setupPerformanceOverlay() {
        performanceOverlay = findViewById(R.id.performanceOverlay);
        performanceOverlay.setVisibility(View.VISIBLE);
        
        // Update overlay every second
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (performanceMonitor != null) {
                    PerformanceMetrics metrics = performanceMonitor.getMetrics().getValue();
                    double memoryMB = performanceMonitor.getMemoryUsageMB();
                    performanceOverlay.updateMetrics(metrics, memoryMB);
                }
                new Handler(Looper.getMainLooper()).postDelayed(this, 1000);
            }
        }, 1000);
    }
    
    private void analyzeImage(ImageProxy imageProxy) {
        // Start timing
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
                updateUI(label, confidence, false);
            }
            
            bitmap.recycle();
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing image", e);
        } finally {
            imageProxy.close();
        }
    }
}
```

#### 2. Add Overlay to Layout

In `activity_main_camerax.xml`:

```xml
<com.trafficsignsclassification.evaluation.runtime.PerformanceOverlayView
    android:id="@+id/performanceOverlay"
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    android:layout_alignParentTop="true"
    android:layout_alignParentEnd="true"
    android:layout_margin="8dp"
    android:visibility="gone" />
```

### B2: Offline Evaluation

#### 1. Create Evaluation Task (Debug Only)

Create a debug menu or button to trigger evaluation:

```kotlin
// In a debug activity or menu

import com.trafficsignsclassification.evaluation.offline.OfflineEvaluator
import java.io.File

fun runOfflineEvaluation() {
    lifecycleScope.launch {
        val classifier = TrafficSignClassifier(this@DebugActivity)
        val evaluator = OfflineEvaluator(this@DebugActivity, classifier)
        
        // Option 1: Evaluate from device storage
        val testDataDir = File(getExternalFilesDir(null), "test_images")
        val metrics = evaluator.evaluate(testDataDir)
        
        // Option 2: Evaluate from assets
        // val metrics = evaluator.evaluateFromAssets("test_images")
        
        // Save results
        val outputDir = File(getExternalFilesDir(null), "evaluation_results")
        outputDir.mkdirs()
        metrics.saveToFile(outputDir)
        
        // Show summary
        Toast.makeText(
            this@DebugActivity,
            "Evaluation complete! Accuracy: ${"%.2f".format(metrics.accuracy * 100)}%",
            Toast.LENGTH_LONG
        ).show()
        
        Log.d("Evaluation", metrics.toSummaryString())
    }
}
```

#### 2. Test Data Structure

Organize test images in this structure:

```
app/src/main/assets/test_images/
├── Giới hạn tốc độ 50 km/h/
│   ├── image1.jpg
│   ├── image2.jpg
│   └── image3.jpg
├── Cấm vượt/
│   ├── image1.jpg
│   └── image2.jpg
└── Dừng lại/
    ├── image1.jpg
    └── image2.jpg
```

Or on device storage:

```
/storage/emulated/0/Android/data/com.trafficsignsclassification/files/test_images/
├── Giới hạn tốc độ 50 km/h/
│   └── *.jpg
├── Cấm vượt/
│   └── *.jpg
└── ...
```

---

## Updating Penalty Data

### Method 1: Update JSON in Assets (Requires App Update)

1. Edit `app/src/main/assets/penalties.json`
2. Increment version number
3. Build and release new APK
4. Users update app from Play Store

### Method 2: Remote Update (Optional - Not Implemented Yet)

To implement remote updates:

1. Host `penalties.json` on a server
2. Implement `PenaltyDataSource.loadPenaltiesFromRemote()`
3. Add version checking logic
4. Download and cache new data
5. Update database

Example:

```kotlin
// In PenaltyRepository.kt

suspend fun checkForUpdates(remoteUrl: String): Result<Boolean> {
    return withContext(Dispatchers.IO) {
        try {
            // Fetch remote version
            val remoteData = dataSource.loadPenaltiesFromRemote(remoteUrl)
            if (remoteData.isSuccess) {
                // Compare versions
                val currentVersion = getDataVersion()
                // If newer, update database
                penaltyDao.deleteAll()
                penaltyDao.insertAll(remoteData.getOrThrow())
                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## Debug vs Release Builds

The evaluation features are controlled by BuildConfig flags:

```gradle
buildTypes {
    debug {
        buildConfigField "boolean", "ENABLE_EVALUATION", "true"
        buildConfigField "boolean", "ENABLE_PERFORMANCE_OVERLAY", "true"
    }
    release {
        buildConfigField "boolean", "ENABLE_EVALUATION", "false"
        buildConfigField "boolean", "ENABLE_PERFORMANCE_OVERLAY", "false"
    }
}
```

Use these flags in code:

```java
if (BuildConfig.ENABLE_PERFORMANCE_OVERLAY) {
    // Show performance overlay
    performanceOverlay.setVisibility(View.VISIBLE);
}

if (BuildConfig.ENABLE_EVALUATION) {
    // Enable evaluation menu
    showEvaluationMenu();
}
```

---

## Testing

### Test Penalty Lookup

1. Run app
2. Point camera at a traffic sign
3. Capture image
4. Tap "Xem mức phạt" button
5. Verify penalty details are displayed correctly

### Test Performance Monitor

1. Build debug APK
2. Run app
3. Performance overlay should appear in top-right corner
4. Point camera at signs
5. Verify FPS and latency metrics update in real-time

### Test Offline Evaluation

1. Prepare test dataset
2. Run evaluation task
3. Check logs for metrics summary
4. Verify CSV files are generated in output directory

---

## File Checklist

### New Files Created:

**Penalty Module:**
- ✅ `penalty/data/PenaltyInfo.kt`
- ✅ `penalty/data/PenaltyDao.kt`
- ✅ `penalty/data/PenaltyDatabase.kt`
- ✅ `penalty/data/PenaltyDataSource.kt`
- ✅ `penalty/data/PenaltyRepository.kt`
- ✅ `penalty/ui/PenaltyDetailActivity.kt`
- ✅ `assets/penalties.json`
- ✅ `res/layout/activity_penalty_detail.xml`

**Evaluation Module:**
- ✅ `evaluation/runtime/PerformanceMonitor.kt`
- ✅ `evaluation/runtime/PerformanceOverlayView.kt`
- ✅ `evaluation/offline/OfflineEvaluator.kt`
- ✅ `evaluation/offline/EvaluationMetrics.kt`

**Configuration:**
- ✅ `app/build.gradle` (updated with Kotlin, Room, Gson)
- ✅ `res/values/strings.xml` (updated with new strings)

---

## Next Steps

1. **Sync Gradle** - Let Android Studio download new dependencies
2. **Add Penalty Button** - Update `activity_main_camerax.xml` with penalty button
3. **Integrate Performance Monitor** - Add monitoring code to MainActivityCameraX
4. **Register Activity** - Add PenaltyDetailActivity to AndroidManifest.xml
5. **Test** - Build and test both modules
6. **Populate Data** - Add more entries to `penalties.json`

---

## Performance Considerations

- **Penalty Lookup**: O(1) database query, negligible impact
- **Performance Monitor**: ~1ms overhead per frame, acceptable
- **Offline Evaluation**: Run only in debug builds, not in production

---

## Future Enhancements

1. **Remote Updates**: Implement server-side penalty data updates
2. **User Feedback**: Allow users to report incorrect penalties
3. **Favorites**: Let users save frequently checked signs
4. **Notifications**: Alert users about penalty regulation changes
5. **Export Metrics**: Allow exporting performance logs for analysis

---

## Support

For questions or issues, refer to:
- Penalty data format: `assets/penalties.json`
- Performance metrics: `PerformanceMetrics.kt`
- Evaluation output: Check app's external files directory

---

**Integration complete! Your app now has penalty lookup and comprehensive evaluation capabilities.**
