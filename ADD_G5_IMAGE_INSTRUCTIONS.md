# Add G5 Logo Image - Quick Instructions

## ✅ Layout Files Updated!

I've already updated both layout files:
- ✅ `activity_main_camerax.xml` - Logo 80x80dp
- ✅ `activity_splash.xml` - Logo 200x200dp

---

## 📸 Now You Need To: Add the G5 Image File

### Step 1: Save G5 Logo Image

**Save the G5 neon logo image as:**
```
g5_logo.png
```

**Location:** Save to your Downloads or Desktop

---

### Step 2: Add to Android Studio

**Method A: Drag & Drop (Easiest)**

```
1. In Android Studio, open Project view
2. Navigate to: app → src → main → res → drawable
3. Drag g5_logo.png from your file explorer
4. Drop it into the drawable folder
5. Click OK when prompted
```

**Method B: Copy-Paste**

```
1. Copy g5_logo.png (Ctrl+C / Cmd+C)
2. In Android Studio: app → src → main → res → drawable
3. Right-click drawable folder
4. Paste (Ctrl+V / Cmd+V)
5. Click OK
```

**Method C: Manual File Copy**

```bash
# Windows
copy "C:\Users\YourName\Downloads\g5_logo.png" "app\src\main\res\drawable\g5_logo.png"

# Mac/Linux
cp ~/Downloads/g5_logo.png app/src/main/res/drawable/g5_logo.png
```

---

### Step 3: Verify File Added

**Check in Android Studio:**
```
Project view:
app
└── src
    └── main
        └── res
            └── drawable
                └── g5_logo.png ✅ Should be here
```

**Or check file system:**
```bash
ls app/src/main/res/drawable/g5_logo.png
# Should show: app/src/main/res/drawable/g5_logo.png
```

---

### Step 4: Sync & Build

```
1. File → Sync Project with Gradle Files
2. Build → Clean Project
3. Build → Rebuild Project
4. Run → Run 'app' (Shift+F10)
```

**Or via command line:**
```bash
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
```

---

## 🎯 What Will Change

### Before:
```
MainActivity:
┌─────────────┐
│   [old]     │ ← 50x50 ic_launcher
│   icon      │
└─────────────┘

SplashActivity:
┌─────────────┐
│             │
│   [old]     │ ← 150x150 ic_launcher
│   icon      │
│             │
└─────────────┘
```

### After:
```
MainActivity:
┌─────────────┐
│     G5      │ ← 80x80 neon logo
│   (neon)    │
└─────────────┘

SplashActivity:
┌─────────────┐
│             │
│     G5      │ ← 200x200 neon logo
│  (glowing)  │
│             │
└─────────────┘
```

---

## 🐛 Troubleshooting

### Issue: "Cannot resolve symbol 'g5_logo'"

**Solution:**
```
1. Verify file exists: app/src/main/res/drawable/g5_logo.png
2. File name must be lowercase, no spaces
3. Sync Gradle: File → Sync Project with Gradle Files
4. Invalidate caches: File → Invalidate Caches → Restart
```

### Issue: Image not showing in app

**Solution:**
```
1. Clean project: Build → Clean Project
2. Rebuild: Build → Rebuild Project
3. Uninstall old app from device
4. Install fresh: Run → Run 'app'
```

### Issue: Image looks stretched/distorted

**Solution:**
```
Already fixed in layout with:
android:scaleType="fitCenter"

If still issues, try:
- android:scaleType="centerInside"
- Or adjust width/height to maintain aspect ratio
```

### Issue: File name error

**Solution:**
```
File name must be:
✅ g5_logo.png (correct)
❌ G5_logo.png (wrong - uppercase)
❌ g5 logo.png (wrong - space)
❌ g5-logo.png (wrong - hyphen, use underscore)
```

---

## 📋 Complete Checklist

```
□ G5 logo image saved as g5_logo.png
□ File added to app/src/main/res/drawable/
□ File name is lowercase with underscore
□ Gradle synced
□ Project cleaned
□ Project rebuilt
□ App installed on device
□ MainActivity shows G5 logo (80x80)
□ SplashActivity shows G5 logo (200x200)
□ Logo looks sharp and clear
□ No stretching or distortion
```

---

## 🎨 Image Requirements

**Format:**
- PNG (recommended)
- Transparent background (optional but looks better)
- RGB color mode

**Size:**
- Minimum: 512x512 px
- Recommended: 1024x1024 px or larger
- Your G5 logo should be fine as-is

**Quality:**
- High resolution
- No compression artifacts
- Sharp edges on text

---

## 💡 Pro Tips

### Tip 1: Keep Original File
```
Save original high-res G5 logo
Easy to regenerate if needed
```

### Tip 2: Test on Device
```
Emulator may not show colors accurately
Test on real Android device for best results
```

### Tip 3: Check Both Screens
```
Test:
1. Splash screen (when app starts)
2. Main screen (top logo)
Both should show G5 neon logo
```

### Tip 4: Adjust Size if Needed
```
If logo too small/large:
- MainActivity: Change 80dp to 60dp or 100dp
- SplashActivity: Change 200dp to 150dp or 250dp
```

---

## 🚀 Quick Summary

**What I did:**
1. ✅ Updated activity_main_camerax.xml
2. ✅ Updated activity_splash.xml
3. ✅ Changed size to 80dp (main) and 200dp (splash)
4. ✅ Added scaleType="fitCenter"

**What you need to do:**
1. ⏳ Save G5 logo as g5_logo.png
2. ⏳ Add to app/src/main/res/drawable/
3. ⏳ Sync Gradle
4. ⏳ Build & run

**Time:** 2-3 minutes

---

🎨 **After adding the image file, your G5 neon logo will appear in the app!**

**Next step: Add g5_logo.png to drawable folder!**
