# Branding Quick Reference - Group 5 DAP

## ✅ Status: App Name Updated

**Current Status:**
- ✅ App name: "Group 5 DAP"
- ✅ strings.xml updated
- ✅ AndroidManifest configured
- 🎨 Icon: Pending (needs logo image)

---

## 🚀 Quick Start: Update Icon in 3 Steps

### Step 1: Prepare Logo (5 minutes)

**Option A: Use Canva (Easiest)**
```
1. Go to canva.com
2. Create 1024x1024 design
3. Add "G5 DAP" text
4. Background: #00897B
5. Text color: White
6. Download PNG
```

**Option B: Use existing logo**
```
- Format: PNG
- Size: 1024x1024 px minimum
- Background: Transparent (for foreground)
```

### Step 2: Generate Icons (2 minutes)

**In Android Studio:**
```
1. Right-click: app/src/main/res
2. New → Image Asset
3. Icon Type: Launcher Icons
4. Foreground: [Your logo]
5. Background: Color #E0F2F1
6. Next → Finish
```

### Step 3: Test (3 minutes)

```bash
./gradlew clean
./gradlew installDebug
# Check launcher icon
```

**Total Time: ~10 minutes**

---

## 📁 Files Changed

### ✅ Already Updated

```
app/src/main/res/values/strings.xml
  └─ <string name="app_name">Group 5 DAP</string>

app/src/main/AndroidManifest.xml
  └─ android:label="@string/app_name"
```

### 🎨 To Be Created (After logo ready)

```
app/src/main/res/
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml          [NEW]
│   └── ic_launcher_round.xml    [NEW]
├── mipmap-mdpi/
│   ├── ic_launcher.png          [REPLACE]
│   └── ic_launcher_foreground.png [NEW]
├── mipmap-hdpi/
│   ├── ic_launcher.png          [REPLACE]
│   └── ic_launcher_foreground.png [NEW]
├── mipmap-xhdpi/
│   ├── ic_launcher.png          [REPLACE]
│   └── ic_launcher_foreground.png [NEW]
├── mipmap-xxhdpi/
│   ├── ic_launcher.png          [REPLACE]
│   └── ic_launcher_foreground.png [NEW]
├── mipmap-xxxhdpi/
│   ├── ic_launcher.png          [REPLACE]
│   └── ic_launcher_foreground.png [NEW]
└── values/
    └── ic_launcher_background.xml [NEW]
```

---

## 🎨 Design Specs

### Logo Requirements

```
Size:       1024x1024 px (minimum)
Format:     PNG with transparency
Safe Zone:  660x660 px (center)
Colors:     Match app theme
```

### Recommended Colors

```
Background: #E0F2F1 (Light teal) or #00897B (Dark teal)
Foreground: White (#FFFFFF) or #00897B
Text:       Bold, sans-serif font
```

### Simple Design Ideas

**Idea 1: Text Only**
```
"G5 DAP" or "G5"
White text on teal background
```

**Idea 2: Badge**
```
Circle with "G5" inside
White on teal gradient
```

**Idea 3: Icon + Text**
```
Traffic sign icon + "G5"
White on teal background
```

---

## 🛠️ Tools & Resources

### Online Icon Generators (No design skills needed)

1. **Android Asset Studio** (Best)
   - URL: https://romannurik.github.io/AndroidAssetStudio/
   - Upload logo → Generate all sizes
   - Free, instant

2. **Easy App Icon**
   - URL: https://easyappicon.com/
   - Upload → Select Android → Download
   - Free

3. **App Icon Generator**
   - URL: https://appicon.co/
   - Upload → Generate → Download
   - Free

### Design Tools (If creating from scratch)

1. **Canva** (Easiest)
   - URL: https://www.canva.com/
   - Templates available
   - Free tier sufficient

2. **Figma** (Professional)
   - URL: https://www.figma.com/
   - Vector-based
   - Free for personal use

3. **Photopea** (Photoshop alternative)
   - URL: https://www.photopea.com/
   - Free, browser-based
   - No account needed

---

## 📋 Testing Checklist

After updating icon:

```
□ Uninstall old app
□ Clean project (./gradlew clean)
□ Rebuild (./gradlew assembleDebug)
□ Install (./gradlew installDebug)
□ Check launcher icon
□ Check app name: "Group 5 DAP"
□ Check recent apps
□ Check settings → apps
□ Test on different launchers (if possible)
```

---

## 🐛 Common Issues & Fixes

### Issue 1: Icon doesn't change
```bash
# Solution:
adb uninstall com.trafficsignsclassification
./gradlew clean
./gradlew installDebug
```

### Issue 2: Icon looks blurry
```
# Solution:
- Use higher resolution source (1024x1024+)
- Regenerate with Image Asset Studio
```

### Issue 3: Icon is cropped
```
# Solution:
- Reduce logo size to fit safe zone
- In Image Asset Studio: Resize to 60-70%
```

### Issue 4: Background color wrong
```
# Solution:
- Check values/ic_launcher_background.xml
- Verify color code: #E0F2F1 or #00897B
```

---

## 💡 Quick Tips

### Tip 1: Start Simple
Create a basic version first, test it, then improve.

### Tip 2: Use App Colors
Stick to existing app colors: #E0F2F1, #00897B

### Tip 3: Test on Real Device
Emulator may not show accurate rendering.

### Tip 4: Keep Original Files
Save 1024x1024 source files for future edits.

### Tip 5: Backup Before Changes
Commit to git before making icon changes.

---

## 📞 Need Help?

### If you have a logo image:
1. See: `APP_BRANDING_GUIDE.md` → BƯỚC 3
2. Use Image Asset Studio
3. 10 minutes to complete

### If you need to create a logo:
1. See: `ICON_TEMPLATES.md` → Design Templates
2. Use Canva or online tool
3. 15-20 minutes to complete

### If you want ready-made templates:
1. See: `ICON_TEMPLATES.md` → File Templates
2. Copy XML files
3. Generate images with online tool

---

## 🎯 Next Actions

### Immediate (Required)
1. [ ] Prepare logo image (1024x1024 PNG)
2. [ ] Use Image Asset Studio to generate icons
3. [ ] Test on device

### Optional (Recommended)
1. [ ] Test on multiple devices
2. [ ] Test on different launchers
3. [ ] Get feedback from team
4. [ ] Refine design if needed

### Future (Nice to have)
1. [ ] Create themed icon for Android 12+
2. [ ] Add app shortcuts with custom icons
3. [ ] Create notification icon
4. [ ] Design splash screen logo

---

## 📊 Summary

**What's Done:**
- ✅ App name: "Group 5 DAP"
- ✅ Configuration: Complete
- ✅ Documentation: Ready

**What's Needed:**
- 🎨 Logo image (1024x1024 PNG)
- 🎨 Icon generation (10 minutes)
- 🎨 Testing (5 minutes)

**Total Time Remaining:** ~15-20 minutes

---

## 🔗 Related Documents

- `APP_BRANDING_GUIDE.md` - Complete guide with all details
- `ICON_TEMPLATES.md` - Templates and design specs
- `BRANDING_QUICK_REFERENCE.md` - This file (quick reference)

---

## ✅ Verification Commands

```bash
# Check app name in strings.xml
cat app/src/main/res/values/strings.xml | grep app_name

# Check AndroidManifest
cat app/src/main/AndroidManifest.xml | grep android:label

# List current icons
ls -la app/src/main/res/mipmap-*/ic_launcher*

# Build and install
./gradlew clean assembleDebug installDebug

# Check installed app name
adb shell pm list packages -f | grep trafficsigns
```

---

🎉 **App name updated to "Group 5 DAP"!**
📱 **Ready for icon update - just need logo image!**

**Estimated time to complete icon update: 10-20 minutes**
