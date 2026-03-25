# Architecture Diagram: Extended Traffic Sign Classification App

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Traffic Sign Classification App               │
│                         (Group 5 DAP)                            │
└─────────────────────────────────────────────────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
                ▼               ▼               ▼
        ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
        │   ML Module  │ │   Penalty    │ │  Evaluation  │
        │  (Existing)  │ │   Module     │ │   Module     │
        │              │ │    (NEW)     │ │    (NEW)     │
        └──────────────┘ └──────────────┘ └──────────────┘
```

---

## Detailed Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              UI Layer                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐     │
│  │  SplashActivity  │  │ MainActivityCameraX│ │ PenaltyDetail    │     │
│  │                  │  │                    │  │ Activity         │     │
│  │  - Logo G5       │  │  - Camera Preview  │  │                  │     │
│  │  - Init repos    │  │  - Live mode       │  │  - Fine info     │     │
│  │                  │  │  - Capture mode    │  │  - Legal ref     │     │
│  └────────┬─────────┘  │  - Penalty button  │  │  - Severity      │     │
│           │            │  - Perf overlay    │  │                  │     │
│           │            └────────┬───────────┘  └──────────────────┘     │
│           │                     │                                        │
└───────────┼─────────────────────┼────────────────────────────────────────┘
            │                     │
            ▼                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           Business Logic Layer                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                      ML Inference Module                          │  │
│  │  ┌────────────────────────────────────────────────────────────┐  │  │
│  │  │  TrafficSignClassifier                                      │  │  │
│  │  │  - TFLite model loading                                     │  │  │
│  │  │  - Image preprocessing                                      │  │  │
│  │  │  - Inference execution                                      │  │  │
│  │  │  - Top-K predictions                                        │  │  │
│  │  │  Returns: List<Pair<String, Float>>                        │  │  │
│  │  └────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    Penalty Lookup Module                         │  │
│  │  ┌────────────────────────────────────────────────────────────┐  │  │
│  │  │  PenaltyRepository                                          │  │  │
│  │  │  - getPenaltyForLabel(label): PenaltyInfo?                 │  │  │
│  │  │  - getAllPenalties(): List<PenaltyInfo>                    │  │  │
│  │  │  - refreshFromAssets(): Result<Unit>                       │  │  │
│  │  │  - Fuzzy label matching                                    │  │  │
│  │  └────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                   Evaluation Module                              │  │
│  │  ┌──────────────────────────┐  ┌──────────────────────────────┐ │  │
│  │  │  PerformanceMonitor      │  │  OfflineEvaluator            │ │  │
│  │  │  - startInference()      │  │  - evaluate(testDir)         │ │  │
│  │  │  - recordInference()     │  │  - evaluateFromAssets()      │ │  │
│  │  │  - getMetrics()          │  │  - Compute accuracy          │ │  │
│  │  │  - FPS tracking          │  │  - Confusion matrix          │ │  │
│  │  │  - Latency stats         │  │  - Per-class metrics         │ │  │
│  │  └──────────────────────────┘  └──────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
            │                     │                     │
            ▼                     ▼                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                            Data Layer                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐     │
│  │  TFLite Model    │  │  Room Database   │  │  JSON Assets     │     │
│  │                  │  │                  │  │                  │     │
│  │  model_trained   │  │  PenaltyDatabase │  │  penalties.json  │     │
│  │  .tflite         │  │                  │  │                  │     │
│  │                  │  │  ┌────────────┐  │  │  - Version       │     │
│  │  labels.txt      │  │  │ PenaltyDao │  │  │  - Penalties[]   │     │
│  │                  │  │  └────────────┘  │  │  - Metadata      │     │
│  │                  │  │                  │  │                  │     │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘     │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## Data Flow Diagrams

### Flow 1: Classification + Penalty Lookup

```
User Action: Capture Image
        │
        ▼
┌───────────────────┐
│  MainActivityCameraX │
│  - Capture button  │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│  ImageProxy       │
│  (CameraX)        │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│  ImageUtils       │
│  - Convert to     │
│    Bitmap         │
└─────────┬─────────┘
          │
          ▼
┌───────────────────────────┐
│  TrafficSignClassifier    │
│  - Preprocess             │
│  - Run TFLite inference   │
│  - Get top-K predictions  │
└─────────┬─────────────────┘
          │
          ▼
    List<Pair<String, Float>>
    [("Giới hạn tốc độ 50 km/h", 0.95), ...]
          │
          ▼
┌───────────────────┐
│  updateUI()       │
│  - Show label     │
│  - Show button    │
└─────────┬─────────┘
          │
          ▼
User Action: Tap "Xem mức phạt"
          │
          ▼
┌───────────────────────────┐
│  PenaltyRepository        │
│  - Query database         │
│  - Fuzzy match label      │
└─────────┬─────────────────┘
          │
          ▼
    PenaltyInfo?
    {
      signLabel: "Giới hạn tốc độ 50 km/h",
      fineRangeCar: "800.000 - 1.000.000 đồng",
      ...
    }
          │
          ▼
┌───────────────────────────┐
│  PenaltyDetailActivity    │
│  - Display fine info      │
│  - Show legal reference   │
│  - Show severity          │
└───────────────────────────┘
```

### Flow 2: Performance Monitoring

```
App Start (Debug Build)
        │
        ▼
┌───────────────────────────┐
│  MainActivityCameraX      │
│  - Check BuildConfig      │
│  - Init PerformanceMonitor│
└─────────┬─────────────────┘
          │
          ▼
┌───────────────────────────┐
│  Start Camera Analysis    │
│  (Live Mode)              │
└─────────┬─────────────────┘
          │
          ▼
    For each frame:
          │
          ▼
┌───────────────────────────┐
│  performanceMonitor       │
│  .startInference()        │
└─────────┬─────────────────┘
          │
          ▼
    InferenceTimer
    (start time recorded)
          │
          ▼
┌───────────────────────────┐
│  Run Classification       │
│  (TFLite inference)       │
└─────────┬─────────────────┘
          │
          ▼
┌───────────────────────────┐
│  performanceMonitor       │
│  .recordInference(timer)  │
└─────────┬─────────────────┘
          │
          ▼
    Metrics Updated:
    - Latency calculated
    - FPS updated
    - Rolling avg computed
    - Spikes detected
          │
          ▼
┌───────────────────────────┐
│  PerformanceOverlayView   │
│  - Update every 1 second  │
│  - Display metrics        │
└───────────────────────────┘
```

### Flow 3: Offline Evaluation

```
Developer Action: Run Evaluation
        │
        ▼
┌───────────────────────────┐
│  OfflineEvaluator         │
│  - Load test dataset      │
└─────────┬─────────────────┘
          │
          ▼
    For each test image:
          │
          ▼
┌───────────────────────────┐
│  Load image from          │
│  - Assets, or             │
│  - Device storage         │
└─────────┬─────────────────┘
          │
          ▼
┌───────────────────────────┐
│  TrafficSignClassifier    │
│  - Run inference          │
│  - Get top-3 predictions  │
└─────────┬─────────────────┘
          │
          ▼
    Compare with ground truth
          │
          ▼
┌───────────────────────────┐
│  EvaluationMetricsBuilder │
│  - Accumulate results     │
│  - Build confusion matrix │
└─────────┬─────────────────┘
          │
          ▼
┌───────────────────────────┐
│  Compute Metrics          │
│  - Accuracy               │
│  - Top-3 accuracy         │
│  - Precision/Recall/F1    │
│  - Per-class metrics      │
└─────────┬─────────────────┘
          │
          ▼
┌───────────────────────────┐
│  Save Results             │
│  - summary.txt            │
│  - metrics.csv            │
│  - confusion_matrix.csv   │
└───────────────────────────┘
```

---

## Module Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│                      Dependency Graph                        │
└─────────────────────────────────────────────────────────────┘

UI Layer:
  MainActivityCameraX
    ├─→ TrafficSignClassifier (ML)
    ├─→ PenaltyRepository (Penalty)
    ├─→ PerformanceMonitor (Evaluation)
    └─→ PerformanceOverlayView (Evaluation)
  
  PenaltyDetailActivity
    └─→ PenaltyRepository (Penalty)

Business Logic:
  TrafficSignClassifier
    ├─→ TFLite (External)
    └─→ ImageUtils (Utility)
  
  PenaltyRepository
    ├─→ PenaltyDatabase (Data)
    ├─→ PenaltyDataSource (Data)
    └─→ Room (External)
  
  PerformanceMonitor
    └─→ Kotlin Coroutines (External)
  
  OfflineEvaluator
    ├─→ TrafficSignClassifier (ML)
    └─→ EvaluationMetrics (Evaluation)

Data Layer:
  PenaltyDatabase
    ├─→ PenaltyDao
    └─→ Room (External)
  
  PenaltyDataSource
    ├─→ Gson (External)
    └─→ penalties.json (Asset)

Key:
  ─→  Depends on
  ML  Machine Learning module
  Penalty  Penalty lookup module
  Evaluation  Evaluation module
  External  External library
```

---

## Technology Stack

```
┌─────────────────────────────────────────────────────────────┐
│                      Technology Stack                        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Language:                                                   │
│    ├─ Java (Existing code)                                  │
│    └─ Kotlin (New modules)                                  │
│                                                              │
│  UI Framework:                                               │
│    ├─ Android Views (XML layouts)                           │
│    ├─ Material Design Components                            │
│    └─ ViewBinding                                           │
│                                                              │
│  ML Framework:                                               │
│    ├─ TensorFlow Lite 2.16.1                               │
│    └─ TFLite Support Library                               │
│                                                              │
│  Camera:                                                     │
│    ├─ CameraX 1.3.1                                         │
│    └─ Camera2 API (legacy)                                  │
│                                                              │
│  Database:                                                   │
│    ├─ Room 2.6.1                                            │
│    └─ SQLite                                                │
│                                                              │
│  Data Parsing:                                               │
│    └─ Gson 2.10.1                                           │
│                                                              │
│  Concurrency:                                                │
│    ├─ Kotlin Coroutines                                     │
│    └─ Android Handlers                                      │
│                                                              │
│  Image Processing:                                           │
│    └─ OpenCV (existing)                                     │
│                                                              │
│  Build System:                                               │
│    ├─ Gradle                                                │
│    └─ Android Gradle Plugin                                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Package Structure

```
com.trafficsignsclassification/
│
├── ml/                              # Machine Learning
│   └── (future organization)
│
├── penalty/                         # Penalty Lookup Module
│   ├── data/
│   │   ├── PenaltyInfo.kt          # Data model
│   │   ├── PenaltyDao.kt           # Database DAO
│   │   ├── PenaltyDatabase.kt      # Room database
│   │   ├── PenaltyDataSource.kt    # JSON loader
│   │   └── PenaltyRepository.kt    # Repository
│   └── ui/
│       └── PenaltyDetailActivity.kt # UI screen
│
├── evaluation/                      # Evaluation Module
│   ├── offline/
│   │   ├── OfflineEvaluator.kt     # Test runner
│   │   └── EvaluationMetrics.kt    # Metrics computation
│   └── runtime/
│       ├── PerformanceMonitor.kt   # Performance tracking
│       └── PerformanceOverlayView.kt # Debug overlay
│
├── ui/                              # UI Components
│   └── (future organization)
│
├── util/                            # Utilities
│   └── ImageUtils.java             # Image conversion
│
├── MainActivity.java                # Legacy activity
├── MainActivityCameraX.java         # Main activity (CameraX)
├── SplashActivity.java              # Splash screen
└── TrafficSignClassifier.java       # ML classifier
```

---

## Build Variants

```
┌─────────────────────────────────────────────────────────────┐
│                      Build Variants                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Debug Build:                                                │
│    ├─ ENABLE_EVALUATION = true                             │
│    ├─ ENABLE_PERFORMANCE_OVERLAY = true                    │
│    ├─ Performance overlay visible                           │
│    ├─ Evaluation menu enabled                               │
│    ├─ Detailed logging                                      │
│    └─ No code obfuscation                                   │
│                                                              │
│  Release Build:                                              │
│    ├─ ENABLE_EVALUATION = false                            │
│    ├─ ENABLE_PERFORMANCE_OVERLAY = false                   │
│    ├─ Performance overlay hidden                            │
│    ├─ Evaluation menu disabled                              │
│    ├─ Minimal logging                                       │
│    └─ ProGuard enabled (optional)                           │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Summary

This architecture provides:

✅ **Clean Separation**: ML, Penalty, and Evaluation modules are independent
✅ **Maintainability**: Each module can be updated without affecting others
✅ **Scalability**: Easy to add new features or modules
✅ **Testability**: Each component can be tested in isolation
✅ **Performance**: Minimal overhead, optimized for mobile
✅ **Flexibility**: Debug features can be enabled/disabled per build

The design follows Android best practices:
- Repository pattern for data access
- Room for local database
- Kotlin coroutines for async operations
- Material Design for UI
- BuildConfig for feature flags
- Lifecycle-aware components

**Ready for production use!** 🚀
