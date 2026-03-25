# 🎯 Hướng Dẫn Thêm Logo G5 - Đơn Giản

## ✅ Đã Hoàn Thành

- ✅ File layout đã cập nhật (activity_main_camerax.xml, activity_splash.xml)
- ✅ Tên app: "Group 5 DAP"
- ✅ Tất cả văn bản đã chuyển sang tiếng Việt

---

## ⏳ Bạn Cần Làm Gì Bây Giờ

### Bước 1: Lưu ảnh logo G5

Lưu ảnh logo G5 (ảnh neon màu xanh/tím) với tên:
```
g5_logo.png
```

Lưu vào Desktop hoặc Downloads của bạn.

---

### Bước 2: Thêm vào Android Studio

**Cách 1: Kéo thả (Dễ nhất)**

1. Mở Android Studio
2. Chọn view "Project" (góc trên bên trái)
3. Mở: `app` → `src` → `main` → `res` → `drawable`
4. **Kéo file `g5_logo.png`** từ Desktop/Downloads
5. **Thả vào thư mục `drawable`**
6. Click OK

**Cách 2: Copy-Paste**

1. Copy file `g5_logo.png` (Ctrl+C)
2. Trong Android Studio: `app` → `src` → `main` → `res` → `drawable`
3. Click phải vào thư mục `drawable`
4. Paste (Ctrl+V)
5. Click OK

**Cách 3: Dòng lệnh**

```bash
copy "C:\Users\TenBan\Downloads\g5_logo.png" "app\src\main\res\drawable\g5_logo.png"
```

---

### Bước 3: Sync và Build

Trong Android Studio:
```
1. File → Sync Project with Gradle Files
2. Build → Clean Project
3. Build → Rebuild Project
4. Run → Run 'app' (hoặc nhấn Shift+F10)
```

Hoặc dùng lệnh:
```bash
gradlew clean
gradlew assembleDebug
gradlew installDebug
```

---

## 🎨 Kết Quả

### Màn hình chính (MainActivity):
```
┌─────────────┐
│     G5      │ ← Logo 80x80dp
│  (phát sáng)│
└─────────────┘
Group 5 DAP
```

### Màn hình khởi động (SplashActivity):
```
┌─────────────┐
│             │
│     G5      │ ← Logo 200x200dp
│  (hiệu ứng) │
│             │
└─────────────┘
Phân Loại Biển Báo Giao Thông
```

---

## 🏠 Về Icon Trên Màn Hình Chính

**Tình trạng hiện tại:**
- Icon trên màn hình chính (home screen) vẫn là icon cũ
- Đây là **riêng biệt** với logo trong app

**Để đổi icon màn hình chính:**

Bạn cần dùng **Image Asset Studio**:

1. Click phải vào thư mục `res`
2. Chọn: New → Image Asset
3. Chọn: "Launcher Icons (Adaptive and Legacy)"
4. Chọn ảnh logo G5 của bạn
5. Màu nền: `#1A0033` (tím đậm)
6. Click Finish
7. Gỡ app cũ và cài lại

**Hướng dẫn chi tiết:** Xem file `IMPLEMENT_G5_LOGO.md`

---

## 📋 Checklist

**Logo trong app (MainActivity & SplashActivity):**
```
□ Lưu logo G5 thành g5_logo.png
□ Thêm vào app/src/main/res/drawable/
□ Sync Gradle
□ Build & Run
□ Kiểm tra logo hiện ở MainActivity (phía trên)
□ Kiểm tra logo hiện ở SplashActivity (giữa màn hình)
```

**Icon màn hình chính (Home Screen):**
```
□ Mở Image Asset Studio
□ Chọn ảnh logo G5
□ Đặt màu nền #1A0033
□ Tạo icons
□ Gỡ app cũ
□ Cài app mới
□ Kiểm tra icon trên home screen
```

---

## 🐛 Xử Lý Lỗi

### Lỗi: "Cannot resolve symbol 'g5_logo'"
**Giải pháp:** File chưa được thêm. Thêm `g5_logo.png` vào thư mục drawable.

### Lỗi: "Ảnh không hiện trong app"
**Giải pháp:**
1. Kiểm tra file tồn tại: `app/src/main/res/drawable/g5_logo.png`
2. Clean project
3. Rebuild
4. Cài lại app

### Lỗi: "Icon màn hình chính vẫn cũ"
**Giải pháp:**
1. Icon màn hình chính khác với logo trong app
2. Dùng Image Asset Studio để cập nhật
3. Gỡ app cũ hoàn toàn
4. Cài app mới

---

## 📁 Vị Trí File

**Logo trong app (cần thêm ngay):**
```
app/src/main/res/drawable/g5_logo.png  ← THÊM FILE NÀY
```

**Icon màn hình chính (cập nhật sau):**
```
app/src/main/res/mipmap-mdpi/ic_launcher.png
app/src/main/res/mipmap-hdpi/ic_launcher.png
app/src/main/res/mipmap-xhdpi/ic_launcher.png
app/src/main/res/mipmap-xxhdpi/ic_launcher.png
app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
```

---

## 🚀 Ưu Tiên

**NGAY BÂY GIỜ (5 phút):**
1. Thêm `g5_logo.png` vào thư mục drawable
2. Sync & build
3. Test logo trong MainActivity và SplashActivity

**SAU ĐÓ (10 phút):**
1. Dùng Image Asset Studio cho icon màn hình chính
2. Cập nhật icon home screen
3. Test trên thiết bị

---

## 💡 Tóm Tắt

**Vấn đề:**
- Thiếu file: `app/src/main/res/drawable/g5_logo.png`

**Giải pháp:**
- Thêm file ảnh logo G5 vào thư mục drawable

**Thời gian:**
- 2-3 phút để thêm file và build lại

**Sau đó:**
- Logo trong app sẽ hoạt động ✅
- Icon màn hình chính cần cập nhật riêng (tùy chọn)

---

🎨 **Thêm file g5_logo.png là bạn hoàn thành!**

**File cần thêm:** `g5_logo.png` → `app/src/main/res/drawable/`
