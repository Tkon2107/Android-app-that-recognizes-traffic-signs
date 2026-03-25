# ⚡ Hướng Dẫn Nhanh - Thêm Logo G5

## 🎯 Bạn Cần Làm 1 Việc Duy Nhất

**Thêm file ảnh logo G5 vào thư mục drawable**

---

## 📍 Vị Trí Thêm File

```
Dự án của bạn
│
└── app
    └── src
        └── main
            └── res
                └── drawable
                    └── g5_logo.png  ← THÊM FILE VÀO ĐÂY
```

---

## 🖱️ Cách Thêm (Chọn 1 trong 3)

### Cách 1: Kéo Thả ⭐ (Dễ nhất)

```
1. Lưu ảnh logo G5 thành: g5_logo.png
2. Mở Android Studio
3. Tìm thư mục: app/src/main/res/drawable
4. Kéo file g5_logo.png vào thư mục drawable
5. Click OK
```

### Cách 2: Copy-Paste

```
1. Copy file g5_logo.png (Ctrl+C)
2. Trong Android Studio, click phải vào thư mục drawable
3. Paste (Ctrl+V)
4. Click OK
```

### Cách 3: Dòng Lệnh

```bash
copy "đường_dẫn\g5_logo.png" "app\src\main\res\drawable\g5_logo.png"
```

---

## ▶️ Sau Khi Thêm File

```
1. File → Sync Project with Gradle Files
2. Build → Clean Project
3. Build → Rebuild Project
4. Run → Run 'app'
```

---

## ✅ Kiểm Tra

Sau khi chạy app:

**Màn hình khởi động:**
- Logo G5 hiện ở giữa màn hình (200x200dp)

**Màn hình chính:**
- Logo G5 hiện ở phía trên (80x80dp)

---

## ❓ Nếu Có Lỗi

### "Cannot resolve symbol 'g5_logo'"
→ File chưa được thêm hoặc tên file sai
→ Kiểm tra: `app/src/main/res/drawable/g5_logo.png`

### "Ảnh không hiện"
→ Clean project và rebuild
→ Gỡ app cũ, cài lại

---

## 📝 Lưu Ý Quan Trọng

**Tên file phải là:**
- ✅ `g5_logo.png` (đúng)
- ❌ `G5_logo.png` (sai - chữ hoa)
- ❌ `g5 logo.png` (sai - có khoảng trắng)
- ❌ `g5-logo.png` (sai - dấu gạch ngang)

**Định dạng file:**
- PNG (khuyến nghị)
- Nền trong suốt (tùy chọn)
- Kích thước tối thiểu: 512x512 px

---

## 🏠 Icon Màn Hình Chính

**Lưu ý:** Logo trong app ≠ Icon màn hình chính

Để đổi icon trên home screen:
- Dùng Image Asset Studio
- Xem hướng dẫn: `IMPLEMENT_G5_LOGO.md`

---

## ⏱️ Thời Gian

- Thêm file: **1 phút**
- Build & test: **2 phút**
- **Tổng: 3 phút**

---

🎨 **Chỉ cần thêm 1 file là xong!**

**File:** `g5_logo.png`  
**Vị trí:** `app/src/main/res/drawable/`  
**Thời gian:** 3 phút
