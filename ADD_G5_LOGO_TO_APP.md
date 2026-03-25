# Add G5 Logo to App ImageViews

## 🎯 Goal
Replace ImageView logos in MainActivity and SplashActivity with G5 neon logo

---

## 📍 Current ImageViews

### 1. MainActivity (activity_main_camerax.xml)
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:src="@drawable/ic_launcher"  ← Change this
    .../>
```

### 2. SplashActivity (activity_splash.xml)
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="150dp"
    android:layout_height="150dp"
    android:src="@drawable/ic_launcher"  ← Change this
    .../>
```

---

## 🚀 Implementation Steps

### Step 1: Save G5 Logo to Drawable

**Option A: Using Android Studio (Easiest)**

```
1. Save G5 logo image as: g5_logo.png
2. In Android Studio:
   - Right-click: app/src/main/res/drawable
   - New → Image Asset (or just paste file)
3. If using Image Asset:
   - Asset Type: Image
   - Name: g5_logo
   - Path: [Browse to g5_logo.png]
   - Click Next → Finish
4. Or simply:
   - Copy g5_logo.png
   - Paste into: app/src/main/res/drawable/
   - Rename to: g5_logo.png
```

**Option B: Manual Copy**

```bash
# Copy logo to drawable folder
cp /path/to/g5_logo.png app/src/main/res/drawable/g5_logo.png
```

**Result:**
```
app/src/main/res/drawable/g5_logo.png ✅
```

---

### Step 2: Update MainActivity Layout

**File:** `app/src/main/res/layout/activity_main_camerax.xml`

**Change from:**
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:src="@drawable/ic_launcher"
    android:layout_centerHorizontal="true"
    android:layout_marginTop="20dp"
    android:transitionName="app_logo_transition"
    android:contentDescription="@string/app_name" />
```

**Change to:**
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:src="@drawable/g5_logo"
    android:layout_centerHorizontal="true"
    android:layout_marginTop="20dp"
    android:transitionName="app_logo_transition"
    android:scaleType="fitCenter"
    android:contentDescription="@string/app_name" />
```

**Changes:**
- ✅ `android:src="@drawable/g5_logo"` - Use G5 logo
- ✅ `android:layout_width="80dp"` - Larger size (was 50dp)
- ✅ `android:layout_height="80dp"` - Larger size (was 50dp)
- ✅ `android:scaleType="fitCenter"` - Scale properly

---

### Step 3: Update SplashActivity Layout

**File:** `app/src/main/res/layout/activity_splash.xml`

**Change from:**
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="150dp"
    android:layout_height="150dp"
    android:src="@drawable/ic_launcher"
    android:layout_centerInParent="true" />
```

**Change to:**
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:src="@drawable/g5_logo"
    android:layout_centerInParent="true"
    android:scaleType="fitCenter" />
```

**Changes:**
- ✅ `android:src="@drawable/g5_logo"` - Use G5 logo
- ✅ `android:layout_width="200dp"` - Larger size (was 150dp)
- ✅ `android:layout_height="200dp"` - Larger size (was 150dp)
- ✅ `android:scaleType="fitCenter"` - Scale properly

---

### Step 4: Sync & Build

```bash
# Sync Gradle
# File → Sync Project with Gradle Files

# Clean
./gradlew clean

# Build
./gradlew assembleDebug

# Install
./gradlew installDebug
```

---

## 🎨 Size Recommendations

### MainActivity (Top Logo)
```
Recommended: 80x80 dp
- Visible but not too large
- Fits well with text below
- Professional appearance

Alternative sizes:
- Small: 60x60 dp
- Medium: 80x80 dp (recommended)
- Large: 100x100 dp
```

### SplashActivity (Center Logo)
```
Recommended: 200x200 dp
- Large and prominent
- Good for splash screen
- Eye-catching

Alternative sizes:
- Medium: 150x150 dp
- Large: 200x200 dp (recommended)
- Extra Large: 250x250 dp
```

---

## 🎯 Visual Preview

### MainActivity (After change):
```
┌─────────────────────────────┐
│                             │
│          ┌────┐             │
│          │ G5 │ 80x80       │
│          └────┘             │
│    Phân loại biển báo       │
│                             │
│      [Camera Preview]       │
│                             │
└─────────────────────────────┘
```

### SplashActivity (After change):
```
┌─────────────────────────────┐
│                             │
│                             │
│        ┌──────────┐         │
│        │          │         │
│        │    G5    │ 200x200 │
│        │  (neon)  │         │
│        │          │         │
│        └──────────┘         │
│                             │
│  Phân Loại Biển Báo...      │
│                             │
│    Version: 1.0 (Beta)      │
└─────────────────────────────┘
```

---

## 🎨 Optional: Add Background to Logo

If you want logo to have a background:

### Option A: Add background in XML
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:src="@drawable/g5_logo"
    android:background="@drawable/logo_background"
    android:padding="8dp"
    android:scaleType="fitCenter"
    .../>
```

**Create:** `app/src/main/res/drawable/logo_background.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#1A0033"/>
    <corners android:radius="16dp"/>
</shape>
```

### Option B: Add circular background
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:src="@drawable/g5_logo"
    android:background="@drawable/logo_circle_background"
    android:padding="12dp"
    android:scaleType="fitCenter"
    .../>
```

**Create:** `app/src/main/res/drawable/logo_circle_background.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <solid android:color="#1A0033"/>
</shape>
```

---

## ✅ Files Changed Summary

```
✓ app/src/main/res/drawable/g5_logo.png [ADDED]
✓ app/src/main/res/layout/activity_main_camerax.xml [UPDATED]
✓ app/src/main/res/layout/activity_splash.xml [UPDATED]
✓ (Optional) drawable/logo_background.xml [ADDED]
```

---

## 🐛 Troubleshooting

### Issue: Logo looks stretched
```
Solution:
- Add: android:scaleType="fitCenter"
- Or: android:scaleType="centerInside"
- Adjust width/height to maintain aspect ratio
```

### Issue: Logo too small/large
```
Solution:
- MainActivity: Try 60dp, 80dp, or 100dp
- SplashActivity: Try 150dp, 200dp, or 250dp
- Adjust based on your preference
```

### Issue: Logo not showing
```
Solution:
- Verify file exists: app/src/main/res/drawable/g5_logo.png
- Check file name (no spaces, lowercase)
- Sync Gradle: File → Sync Project
- Clean and rebuild
```

### Issue: Logo has white background
```
Solution:
- Ensure PNG has transparency
- Or add dark background in XML (see Optional section)
- Or edit image to remove white background
```

---

## 💡 Pro Tips

### Tip 1: Use Vector Drawable (Optional)
```
If you have SVG version of G5 logo:
1. Right-click drawable → New → Vector Asset
2. Local file → Browse to g5_logo.svg
3. Name: g5_logo
4. Finish
Result: Scalable, no pixelation
```

### Tip 2: Add Animation (Optional)
```xml
<!-- In layout -->
<ImageView
    android:id="@+id/appLogo"
    ...
    android:alpha="0"/>

<!-- In Activity onCreate -->
appLogo.animate()
    .alpha(1f)
    .setDuration(500)
    .start();
```

### Tip 3: Different Logo for Light/Dark Theme
```
Create:
- drawable/g5_logo.png (for light theme)
- drawable-night/g5_logo.png (for dark theme)

Android will automatically use correct version
```

---

## 🎯 Quick Reference

### MainActivity Logo:
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:src="@drawable/g5_logo"
    android:scaleType="fitCenter"
    .../>
```

### SplashActivity Logo:
```xml
<ImageView
    android:id="@+id/appLogo"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:src="@drawable/g5_logo"
    android:scaleType="fitCenter"
    .../>
```

### Build Commands:
```bash
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
```

---

🎨 **Your G5 neon logo will look amazing in the app!**

**Estimated time: 5 minutes**
