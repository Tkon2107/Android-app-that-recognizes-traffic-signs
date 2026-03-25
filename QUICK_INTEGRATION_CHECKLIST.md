# Quick Integration Checklist

## ✅ Pre-Integration (Already Done)

- [x] Kotlin plugin added to build.gradle
- [x] Room, Gson dependencies added
- [x] BuildConfig fields configured
- [x] Penalty data JSON created
- [x] All Kotlin files created
- [x] Layout files created
- [x] PenaltyDetailActivity registered in manifest
- [x] Strings updated

---

## 🔧 Integration Steps (You Need To Do)

### Step 1: Sync Gradle (2 minutes)

```
File → Sync Project with Gradle Files
```

Wait for dependencies to download.

---

### Step 2: Initialize Penalty Repository (5 minutes)

**Option A: In SplashActivity.java**

Add after `setContentView()`:

```java
import com.trafficsignsclassification.penalty.data.PenaltyRepository;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;

// In onCreate()
CoroutineScope scope = new CoroutineScope(Dispatchers.getIO());
scope.launch(() -> {
    PenaltyRepository repository = PenaltyRepository.getInstance(this);
    repository.initialize();
    return null;
});
```

**Option B: Create Application class**

Create `MyApplication.java`:

```java
package com.trafficsignsclassification;

import android.app.Application;
import com.trafficsignsclassification.penalty.data.PenaltyRepository;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        CoroutineScope scope = new CoroutineScope(Dispatchers.getIO());
        scope.launch(() -> {
            PenaltyRepository.getInstance(this).initialize();
            return null;
        });
    }
}
```

Then update AndroidManifest.xml:

```xml
<application
    android:name=".MyApplication"
    ...>
```

---

### Step 3: Add Penalty Button to Layout (3 minutes)

Edit `app/src/main/res/layout/activity_main_camerax.xml`

Add before `</RelativeLayout>`:

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
```

---

### Step 4: Add Penalty Button Logic (10 minutes)

Edit `MainActivityCameraX.java`

**Add import:**

```java
import android.content.Intent;
import com.google.android.material.button.MaterialButton;
import com.trafficsignsclassification.penalty.ui.PenaltyDetailActivity;
```

**Add field:**

```java
private MaterialButton viewPenaltyButton;
```

**In onCreate(), after setContentView():**

```java
viewPenaltyButton = findViewById(R.id.viewPenaltyButton);
```

**Update updateUI() method:**

Find your `updateUI()` method and modify it:

```java
private void updateUI(String label, float confidence, boolean isCaptureMode) {
    runOnUiThread(() -> {
        if (isCaptureMode) {
            // Capture mode: only show label
            classTextView.setText("Loại: " + label);
            probabilityTextView.setVisibility(View.GONE);
            topPredictionsTextView.setVisibility(View.GONE);
            
            // Show penalty button
            showPenaltyButton(label);
        } else {
            // Live mode
            classTextView.setText("Loại: " + label);
            probabilityTextView.setText(String.format("Độ chính xác: %.2f%%", confidence * 100));
            probabilityTextView.setVisibility(View.VISIBLE);
            
            // Hide penalty button in live mode
            viewPenaltyButton.setVisibility(View.GONE);
        }
    });
}
```

**Add new method:**

```java
private void showPenaltyButton(String signLabel) {
    viewPenaltyButton.setVisibility(View.VISIBLE);
    viewPenaltyButton.setOnClickListener(v -> {
        Intent intent = new Intent(this, PenaltyDetailActivity.class);
        intent.putExtra(PenaltyDetailActivity.EXTRA_SIGN_LABEL, signLabel);
        startActivity(intent);
    });
}
```

---

### Step 5: Build and Test (5 minutes)

```
Build → Rebuild Project
Run → Run 'app'
```

**Test flow:**
1. App starts
2. Point camera at sign
3. Tap "Chụp ảnh"
4. See result: "Loại: [sign name]"
5. Tap "Xem mức phạt" button
6. Penalty details screen opens
7. Verify information displays correctly

---

## 🎯 Optional: Add Performance Monitor (Debug Only)

### Step 1: Add Overlay to Layout

Edit `activity_main_camerax.xml`, add before `</RelativeLayout>`:

```xml
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

### Step 2: Add Monitor Code

Edit `MainActivityCameraX.java`

**Add imports:**

```java
import com.trafficsignsclassification.BuildConfig;
import com.trafficsignsclassification.evaluation.runtime.PerformanceMonitor;
import com.trafficsignsclassification.evaluation.runtime.PerformanceOverlayView;
import com.trafficsignsclassification.evaluation.runtime.InferenceTimer;
import android.os.Handler;
import android.os.Looper;
```

**Add fields:**

```java
private PerformanceMonitor performanceMonitor;
private PerformanceOverlayView performanceOverlay;
```

**In onCreate(), after setContentView():**

```java
// Initialize performance monitor (debug only)
if (BuildConfig.ENABLE_PERFORMANCE_OVERLAY) {
    performanceMonitor = new PerformanceMonitor();
    performanceOverlay = findViewById(R.id.performanceOverlay);
    performanceOverlay.setVisibility(View.VISIBLE);
    startPerformanceUpdates();
}
```

**Add new method:**

```java
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
```

**Update your image analysis method:**

Find where you call `classifier.classifyImage()` and wrap it:

```java
// Start timing
InferenceTimer timer = null;
if (performanceMonitor != null) {
    timer = performanceMonitor.startInference();
}

try {
    // Your existing classification code
    Bitmap bitmap = ImageUtils.imageProxyToBitmap(imageProxy);
    List<Pair<String, Float>> results = classifier.classifyImage(bitmap);
    
    // Record inference completion
    if (performanceMonitor != null && timer != null) {
        performanceMonitor.recordInference(timer);
    }
    
    // Rest of your code...
    
} catch (Exception e) {
    Log.e(TAG, "Error", e);
}
```

---

## 📊 Testing Checklist

### Penalty Lookup
- [ ] App builds successfully
- [ ] No Gradle errors
- [ ] Capture a sign
- [ ] "Xem mức phạt" button appears
- [ ] Tap button opens penalty screen
- [ ] Penalty details display correctly
- [ ] Car fine shows
- [ ] Motorbike fine shows
- [ ] Legal reference shows
- [ ] Close button works

### Performance Monitor (Debug Build)
- [ ] Build debug APK
- [ ] Performance overlay appears (top-right)
- [ ] FPS updates in real-time
- [ ] Latency metrics update
- [ ] Memory usage displays
- [ ] No crashes or freezes

---

## 🐛 Troubleshooting

### "Cannot resolve symbol PenaltyRepository"
**Solution:** Sync Gradle again, then Rebuild Project

### "BuildConfig.ENABLE_PERFORMANCE_OVERLAY not found"
**Solution:** Rebuild project to generate BuildConfig

### Penalty button doesn't appear
**Solution:** Check that `updateUI()` is called with `isCaptureMode = true`

### Penalty screen shows "Không có thông tin"
**Solution:** 
1. Check sign label matches exactly with `penalties.json`
2. Check repository initialization completed
3. Check database was created

### Performance overlay not visible
**Solution:** 
1. Ensure you're running debug build
2. Check `BuildConfig.ENABLE_PERFORMANCE_OVERLAY` is true
3. Check overlay visibility is set to VISIBLE

---

## 📝 Summary

**Minimum Required Steps:**
1. Sync Gradle ✓
2. Initialize repository ✓
3. Add penalty button to layout ✓
4. Add penalty button logic ✓
5. Build and test ✓

**Time Required:** ~25 minutes

**Optional Steps:**
- Add performance monitor: +15 minutes
- Populate more penalty data: +30 minutes
- Set up offline evaluation: +20 minutes

---

## 🎉 Success Criteria

After integration, you should have:

✅ Penalty lookup working in capture mode
✅ "Xem mức phạt" button appears after capture
✅ Penalty details screen displays correctly
✅ No build errors or crashes
✅ (Optional) Performance overlay in debug builds

---

## 📚 Next Steps

1. **Populate Penalty Data**
   - Edit `assets/penalties.json`
   - Add entries for all 45+ signs from `labels.txt`
   - Match labels exactly

2. **Test All Signs**
   - Test each sign type
   - Verify penalty info is correct
   - Update data as needed

3. **Prepare for Production**
   - Test release build
   - Verify performance overlay is hidden
   - Test on multiple devices

---

**Ready to integrate! Follow the steps above and you'll have penalty lookup working in 25 minutes.** 🚀
