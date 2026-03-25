# Launcher Icon Templates - Ready to Use

## 📁 File Templates

### 1. Adaptive Icon - Main

**File:** `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

### 2. Adaptive Icon - Round

**File:** `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

### 3. Background Color

**File:** `app/src/main/res/values/ic_launcher_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Option 1: Light teal (current app background) -->
    <color name="ic_launcher_background">#E0F2F1</color>
    
    <!-- Option 2: Dark teal (current app primary color) -->
    <!-- <color name="ic_launcher_background">#00897B</color> -->
    
    <!-- Option 3: White -->
    <!-- <color name="ic_launcher_background">#FFFFFF</color> -->
    
    <!-- Option 4: Custom color -->
    <!-- <color name="ic_launcher_background">#YOUR_COLOR</color> -->
</resources>
```

### 4. Alternative: Gradient Background

**File:** `app/src/main/res/drawable/ic_launcher_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:angle="135"
        android:startColor="#E0F2F1"
        android:centerColor="#80CBC4"
        android:endColor="#00897B"
        android:type="linear" />
</shape>
```

**Then update adaptive icon to use drawable:**
```xml
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

---

## 🎨 Design Templates

### Template 1: Simple Text Logo

**Concept:** "G5 DAP" text on colored background

**Foreground:**
```
- Text: "G5 DAP" or "G5"
- Font: Bold, sans-serif
- Color: White or #00897B
- Size: Fits in 66x66 dp safe zone
```

**Background:**
```
- Solid color: #E0F2F1 or #00897B
- Or gradient: #E0F2F1 → #00897B
```

**Create with:**
- Canva: https://www.canva.com/
- Figma: https://www.figma.com/
- Photopea: https://www.photopea.com/

### Template 2: Icon + Text

**Concept:** Traffic sign icon + "G5" text

**Foreground:**
```
- Top: Small traffic sign icon
- Bottom: "G5" text
- Colors: White or #00897B
```

**Background:**
```
- Solid: #00897B (dark teal)
```

### Template 3: Badge Style

**Concept:** Circular badge with "G5"

**Foreground:**
```
- Circle outline
- "G5" in center
- Color: White
```

**Background:**
```
- Gradient: #E0F2F1 → #00897B
```

---

## 🖼️ Sample Logo Specifications

### For "Group 5 DAP" App

#### Specification 1: Minimalist
```
Canvas: 1024x1024 px
Safe Zone: 660x660 px (center)

Foreground:
  - Text: "G5"
  - Font: Roboto Bold
  - Size: 400px
  - Color: #FFFFFF
  - Position: Center
  - Shadow: 2px blur, 20% opacity

Background:
  - Type: Gradient
  - Start: #00897B (top-left)
  - End: #004D40 (bottom-right)
  - Angle: 135°
```

#### Specification 2: Professional
```
Canvas: 1024x1024 px
Safe Zone: 660x660 px (center)

Foreground:
  - Shape: Rounded square (512x512 px)
  - Fill: #FFFFFF
  - Text: "G5 DAP"
  - Font: Roboto Medium
  - Size: 120px
  - Color: #00897B
  - Position: Center

Background:
  - Type: Solid
  - Color: #E0F2F1
```

#### Specification 3: Modern
```
Canvas: 1024x1024 px
Safe Zone: 660x660 px (center)

Foreground:
  - Icon: Traffic sign silhouette (400x400 px)
  - Color: #FFFFFF
  - Badge: Circle with "5" (150x150 px)
  - Badge Position: Top-right corner
  - Badge Color: #FF5722

Background:
  - Type: Gradient
  - Start: #00897B
  - End: #00695C
  - Angle: 90°
```

---

## 🛠️ Quick Creation Tools

### Option 1: Canva (Easiest)

**Steps:**
1. Go to https://www.canva.com/
2. Create design → Custom size → 1024x1024 px
3. Add background color: #E0F2F1
4. Add text: "G5 DAP"
   - Font: Montserrat Bold or Roboto Bold
   - Size: Large
   - Color: #00897B
5. Center text
6. Download as PNG (transparent background for foreground)

**For Foreground:**
- Background: Transparent
- Content: Logo/text only

**For Background:**
- Background: Solid color or gradient
- No text/logo

### Option 2: Figma (Professional)

**Steps:**
1. Create frame: 1024x1024 px
2. Add safe zone guide: 660x660 px (center)
3. Design logo within safe zone
4. Export:
   - Foreground: PNG, transparent
   - Background: PNG, with color

### Option 3: Android Asset Studio (Automated)

**URL:** https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html

**Steps:**
1. Upload your logo (1024x1024 PNG)
2. Adjust padding: 10-30%
3. Choose background color
4. Preview on different shapes
5. Download ZIP
6. Extract to res/ folder

---

## 📦 Pre-made Icon Packs

### Icon Pack 1: Simple "G5"

**Download from:** (You'll need to create these)

**Foreground:**
```
- White "G5" text
- Bold font
- Centered
- 1024x1024 PNG with transparency
```

**Background:**
```
- Solid #00897B
- 1024x1024 PNG
```

### Icon Pack 2: Badge Style

**Foreground:**
```
- White circle outline (800x800)
- "G5" text inside
- 1024x1024 PNG with transparency
```

**Background:**
```
- Gradient #E0F2F1 to #00897B
- 1024x1024 PNG
```

---

## 🎯 Step-by-Step: Create Icon in 5 Minutes

### Using Canva (Free)

**1. Create Foreground (2 minutes)**
```
1. Canva → Custom size → 1024x1024
2. Background → Transparent
3. Text → "G5 DAP"
4. Font → Montserrat Bold
5. Color → White (#FFFFFF)
6. Size → Fill safe zone (leave margins)
7. Download → PNG
8. Save as: ic_launcher_foreground_1024.png
```

**2. Create Background (1 minute)**
```
1. New design → 1024x1024
2. Background → Solid color → #00897B
3. Download → PNG
4. Save as: ic_launcher_background_1024.png
```

**3. Generate All Sizes (2 minutes)**
```
1. Go to: https://easyappicon.com/
2. Upload ic_launcher_foreground_1024.png
3. Select: Android
4. Download ZIP
5. Repeat for background
```

**4. Copy to Project**
```
Extract ZIP contents to:
app/src/main/res/mipmap-*/
```

---

## 🎨 Color Palette for "Group 5 DAP"

Based on current app colors:

### Primary Colors
```
Teal Dark:    #00897B  (Main brand color)
Teal Light:   #E0F2F1  (Background)
Teal Medium:  #80CBC4  (Accent)
```

### Accent Colors
```
Orange:       #FF5722  (Retake button)
Blue:         #283593  (Text)
Green:        #4CAF50  (Success)
Red:          #F44336  (Error)
```

### Recommended Combinations

**Combination 1: Professional**
```
Background: #E0F2F1 (Light teal)
Foreground: #00897B (Dark teal) or White
```

**Combination 2: Bold**
```
Background: #00897B (Dark teal)
Foreground: White (#FFFFFF)
```

**Combination 3: Gradient**
```
Background: Gradient #E0F2F1 → #00897B
Foreground: White (#FFFFFF)
```

---

## 📋 Checklist: Icon Creation

### Before Starting
- [ ] Logo concept decided
- [ ] Colors chosen
- [ ] Tools ready (Canva/Figma/etc.)

### During Creation
- [ ] Canvas size: 1024x1024 px
- [ ] Logo fits in safe zone (660x660 px center)
- [ ] Foreground has transparent background
- [ ] Background is solid color or gradient
- [ ] High contrast between foreground and background
- [ ] Text is readable at small sizes

### After Creation
- [ ] Exported as PNG
- [ ] Generated all density sizes
- [ ] Copied to mipmap folders
- [ ] Created adaptive icon XMLs
- [ ] Tested on device
- [ ] Verified on different launchers

---

## 🚀 Ready-to-Use Commands

### After creating icons:

```bash
# 1. Clean project
./gradlew clean

# 2. Sync Gradle
File → Sync Project with Gradle Files

# 3. Rebuild
./gradlew assembleDebug

# 4. Uninstall old app
adb uninstall com.trafficsignsclassification

# 5. Install new app
./gradlew installDebug

# 6. Verify
# Check launcher, recent apps, settings
```

---

## 💡 Pro Tips

### Tip 1: Test Early
Create a simple version first, test it, then refine.

### Tip 2: Keep It Simple
Simple icons work better at small sizes.

### Tip 3: High Contrast
Ensure good contrast between foreground and background.

### Tip 4: Safe Zone
Always keep important elements in the 66x66 dp safe zone.

### Tip 5: Test on Real Device
Emulator may not show accurate icon rendering.

### Tip 6: Multiple Launchers
Test on different launchers (Pixel, Samsung, etc.)

### Tip 7: Backup Original
Keep original 1024x1024 files for future edits.

---

## 📞 Quick Help

### "I don't have design skills"
→ Use Canva templates or Android Asset Studio

### "My logo looks blurry"
→ Ensure you're using high-resolution source (1024x1024+)

### "Icon is too small/large"
→ Adjust padding in Image Asset Studio (50-80%)

### "Colors don't match app"
→ Use color picker from app screenshots

### "Icon doesn't update"
→ Uninstall app, clean project, rebuild, reinstall

---

🎉 **Ready to create your "Group 5 DAP" icon!**

Choose a template, create your logo, and follow the guide!
