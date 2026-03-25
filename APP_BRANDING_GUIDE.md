# App Branding Update Guide - Group 5 DAP

## 📋 Tổng quan

Hướng dẫn chi tiết để cập nhật branding cho app:
1. ✅ Thay đổi tên app thành "Group 5 DAP"
2. 🎨 Thay đổi launcher icon
3. 🔧 Tạo adaptive icon cho Android 8.0+

---

## ✅ BƯỚC 1: Thay đổi tên app (ĐÃ HOÀN THÀNH)

### File đã cập nhật:

**`app/src/main/res/values/strings.xml`**
```xml
<string name="app_name">Group 5 DAP</string>
```

### Kết quả:
- ✅ Tên app trên launcher: "Group 5 DAP"
- ✅ Tên app trong recents screen: "Group 5 DAP"
- ✅ Tên app trong system UI: "Group 5 DAP"
- ✅ AndroidManifest đã sử dụng `@string/app_name`

### Verify:
```bash
# Build và install app
./gradlew installDebug

# Kiểm tra trên thiết bị:
# - Launcher icon có label "Group 5 DAP"
# - Recent apps hiển thị "Group 5 DAP"
```

---

## 🎨 BƯỚC 2: Chuẩn bị Logo mới

### Yêu cầu về Logo:

#### Format khuyến nghị:
1. **PNG** (preferred) - Transparent background
   - Resolution: 1024x1024 px (minimum)
   - Format: PNG-24 with alpha channel
   - Background: Transparent hoặc solid color

2. **SVG** (alternative) - Vector format
   - Scalable, không mất chất lượng
   - Dễ chỉnh sửa

#### Kích thước cần thiết:

| Density | Size (px) | Folder |
|---------|-----------|--------|
| mdpi | 48x48 | mipmap-mdpi |
| hdpi | 72x72 | mipmap-hdpi |
| xhdpi | 96x96 | mipmap-xhdpi |
| xxhdpi | 144x144 | mipmap-xxhdpi |
| xxxhdpi | 192x192 | mipmap-xxxhdpi |

#### Adaptive Icon (Android 8.0+):
- **Foreground**: 108x108 dp (safe zone: 66x66 dp center)
- **Background**: 108x108 dp (solid color hoặc image)

---

## 🔧 BƯỚC 3: Tạo Launcher Icon trong Android Studio

### Option A: Sử dụng Image Asset Studio (Khuyến nghị)

#### Bước 1: Mở Image Asset Studio
```
1. Right-click vào app/src/main/res
2. New → Image Asset
3. Hoặc: File → New → Image Asset
```

#### Bước 2: Configure Icon Type
```
Icon Type: Launcher Icons (Adaptive and Legacy)
Name: ic_launcher
```

#### Bước 3: Foreground Layer
```
Source Asset:
  - Path: [Browse to your logo PNG file]
  - Resize: 50-80% (adjust để logo vừa với safe zone)
  - Shape: None (để giữ nguyên hình dạng)
  - Color: [Không cần nếu logo có màu sẵn]
```

#### Bước 4: Background Layer
```
Option 1: Solid Color
  - Source: Color
  - Color: #E0F2F1 (màu nền hiện tại của app)
  - Hoặc: #00897B (màu xanh chủ đạo)

Option 2: Image
  - Source: Image
  - Path: [Browse to background image]
```

#### Bước 5: Legacy Icon
```
Shape: Circle hoặc Square
- Android Studio sẽ tự động tạo legacy icons
```

#### Bước 6: Preview & Generate
```
1. Preview trên các shapes: Circle, Square, Rounded Square, Squircle
2. Preview trên các Android versions
3. Click "Next"
4. Confirm paths
5. Click "Finish"
```

### Option B: Manual Creation (Advanced)

Nếu bạn muốn tạo thủ công:

#### Bước 1: Tạo các file cần thiết

**File structure:**
```
app/src/main/res/
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml
│   └── ic_launcher_round.xml
├── mipmap-mdpi/
│   ├── ic_launcher.png (48x48)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
├── mipmap-hdpi/
│   ├── ic_launcher.png (72x72)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
├── mipmap-xhdpi/
│   ├── ic_launcher.png (96x96)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
├── mipmap-xxhdpi/
│   ├── ic_launcher.png (144x144)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
├── mipmap-xxxhdpi/
│   ├── ic_launcher.png (192x192)
│   ├── ic_launcher_foreground.png
│   └── ic_launcher_round.png
└── values/
    └── ic_launcher_background.xml
```

#### Bước 2: Tạo Adaptive Icon XML

**`mipmap-anydpi-v26/ic_launcher.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

**`mipmap-anydpi-v26/ic_launcher_round.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

#### Bước 3: Tạo Background Color

**`values/ic_launcher_background.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#E0F2F1</color>
</resources>
```

#### Bước 4: Export Logo ở các kích thước

Sử dụng tool online hoặc Photoshop/GIMP:

**Online Tools:**
- https://romannurik.github.io/AndroidAssetStudio/
- https://easyappicon.com/
- https://appicon.co/

**Steps:**
1. Upload logo 1024x1024 px
2. Select "Android" platform
3. Download all sizes
4. Copy vào các thư mục mipmap tương ứng

---

## 🎨 BƯỚC 4: Design Guidelines

### Safe Zone cho Adaptive Icons

```
┌─────────────────────────────────┐
│  108 dp x 108 dp (Full canvas)  │
│  ┌───────────────────────────┐  │
│  │                           │  │
│  │   66 dp x 66 dp           │  │
│  │   (Safe zone - logo here) │  │
│  │                           │  │
│  └───────────────────────────┘  │
│                                 │
└─────────────────────────────────┘
```

**Important:**
- Logo chính phải nằm trong vùng 66x66 dp ở giữa
- Vùng ngoài có thể bị crop tùy theo device/launcher

### Color Scheme Suggestions

Dựa trên màu hiện tại của app:

**Option 1: Light Theme**
```
Background: #E0F2F1 (Light teal)
Foreground: Logo với màu #00897B (Dark teal)
```

**Option 2: Dark Theme**
```
Background: #00897B (Dark teal)
Foreground: Logo màu trắng hoặc #E0F2F1
```

**Option 3: Gradient**
```
Background: Gradient từ #E0F2F1 → #00897B
Foreground: Logo màu trắng
```

---

## 🔍 BƯỚC 5: Testing & Verification

### Test trên Android Studio

#### 1. Preview trong Layout Editor
```
1. Open any layout XML
2. Design tab → Device dropdown
3. Xem icon trên các devices khác nhau
```

#### 2. Run on Emulator/Device
```bash
# Clean và rebuild
./gradlew clean
./gradlew assembleDebug

# Install
./gradlew installDebug

# Hoặc: Run → Run 'app' (Shift+F10)
```

#### 3. Check Launcher
```
- Mở launcher
- Tìm "Group 5 DAP"
- Icon hiển thị đúng?
- Label hiển thị "Group 5 DAP"?
```

#### 4. Check Recent Apps
```
- Mở app
- Press Recent Apps button
- Icon và label hiển thị đúng?
```

#### 5. Check Settings
```
- Settings → Apps → Group 5 DAP
- Icon hiển thị đúng?
```

### Test trên các Android versions

| Version | Test Points |
|---------|-------------|
| Android 7.1 và thấp hơn | Legacy icon (mipmap-*/ic_launcher.png) |
| Android 8.0+ | Adaptive icon với shapes khác nhau |
| Android 12+ | Themed icons (optional) |

### Test trên các Launchers

- **Google Pixel Launcher**: Circle shape
- **Samsung One UI**: Squircle shape
- **OnePlus Launcher**: Rounded square
- **MIUI**: Rounded square
- **Nova Launcher**: User-configurable

---

## 📝 BƯỚC 6: Checklist

### Pre-deployment Checklist

- [ ] App name đã đổi thành "Group 5 DAP"
- [ ] Icon mới đã được tạo ở tất cả densities
- [ ] Adaptive icon (Android 8.0+) hoạt động
- [ ] Legacy icon (Android 7.1-) hoạt động
- [ ] Round icon hoạt động
- [ ] Icon không bị crop/distorted
- [ ] Logo nằm trong safe zone
- [ ] Background color/image phù hợp
- [ ] Test trên ít nhất 3 devices khác nhau
- [ ] Test trên ít nhất 2 launchers khác nhau
- [ ] Icon hiển thị đúng trong Settings
- [ ] Icon hiển thị đúng trong Recent Apps

### Files to Verify

```
✅ app/src/main/res/values/strings.xml
   - app_name = "Group 5 DAP"

✅ app/src/main/AndroidManifest.xml
   - android:label="@string/app_name"
   - android:icon="@mipmap/ic_launcher"
   - android:roundIcon="@mipmap/ic_launcher"

✅ app/src/main/res/mipmap-anydpi-v26/
   - ic_launcher.xml
   - ic_launcher_round.xml

✅ app/src/main/res/mipmap-*/
   - ic_launcher.png (all densities)
   - ic_launcher_foreground.png (all densities)
   - ic_launcher_round.png (all densities)

✅ app/src/main/res/values/
   - ic_launcher_background.xml
```

---

## 🛠️ Troubleshooting

### Issue: Icon không thay đổi sau khi build

**Solution:**
```bash
# 1. Uninstall app cũ
adb uninstall com.trafficsignsclassification

# 2. Clean project
./gradlew clean

# 3. Invalidate caches
File → Invalidate Caches → Invalidate and Restart

# 4. Rebuild
./gradlew assembleDebug

# 5. Install lại
./gradlew installDebug
```

### Issue: Icon bị crop/distorted

**Solution:**
- Kiểm tra logo có nằm trong safe zone (66x66 dp) không
- Resize foreground layer trong Image Asset Studio
- Thử adjust từ 50% đến 80%

### Issue: Background color không đúng

**Solution:**
- Check `values/ic_launcher_background.xml`
- Đảm bảo color code đúng format (#RRGGBB)
- Rebuild project

### Issue: Adaptive icon không hoạt động trên Android 8.0+

**Solution:**
- Kiểm tra `mipmap-anydpi-v26/ic_launcher.xml` tồn tại
- Verify XML syntax đúng
- Ensure foreground và background drawables tồn tại

---

## 📚 Resources

### Online Tools

**Icon Generators:**
- https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html
- https://easyappicon.com/
- https://appicon.co/
- https://icon.kitchen/

**Image Editors:**
- https://www.photopea.com/ (Free Photoshop alternative)
- GIMP (Free, open-source)
- Inkscape (Free, for SVG)

**Color Pickers:**
- https://htmlcolorcodes.com/
- https://coolors.co/

### Documentation

- [Android Launcher Icons Guide](https://developer.android.com/guide/practices/ui_guidelines/icon_design_launcher)
- [Adaptive Icons](https://developer.android.com/guide/practices/ui_guidelines/icon_design_adaptive)
- [Material Design Icons](https://material.io/design/iconography/product-icons.html)

---

## 🎯 Quick Start Guide

### Nếu bạn đã có logo PNG 1024x1024:

1. **Open Image Asset Studio**
   ```
   Right-click app/src/main/res → New → Image Asset
   ```

2. **Configure**
   ```
   Icon Type: Launcher Icons (Adaptive and Legacy)
   Name: ic_launcher
   Foreground Layer:
     - Path: [Your logo.png]
     - Resize: 60%
   Background Layer:
     - Color: #E0F2F1
   ```

3. **Generate**
   ```
   Next → Finish
   ```

4. **Build & Test**
   ```bash
   ./gradlew clean
   ./gradlew installDebug
   ```

5. **Verify**
   ```
   - Check launcher
   - Check recent apps
   - Check settings
   ```

---

## ✅ Summary

**Completed:**
- ✅ App name changed to "Group 5 DAP"
- ✅ strings.xml updated
- ✅ AndroidManifest uses @string/app_name

**Next Steps:**
1. Prepare logo image (1024x1024 PNG)
2. Use Image Asset Studio to generate icons
3. Test on device
4. Verify on different launchers

**Estimated Time:**
- With Image Asset Studio: 10-15 minutes
- Manual creation: 30-45 minutes

---

## 📞 Need Help?

### Common Questions

**Q: Tôi chưa có logo, làm sao?**
A: Có thể:
1. Tạo logo đơn giản với text "G5 DAP"
2. Sử dụng icon generator online
3. Thuê designer trên Fiverr/Upwork

**Q: Logo của tôi không phải vuông, có sao không?**
A: Không sao, Image Asset Studio sẽ tự động center và resize.

**Q: Tôi muốn logo khác nhau cho foreground và background?**
A: Được, chọn Image cho cả foreground và background trong Image Asset Studio.

**Q: Làm sao để icon có gradient background?**
A: Tạo image gradient 1024x1024, chọn làm background layer.

---

🎉 **Branding update complete! App của bạn giờ có tên "Group 5 DAP"!**
