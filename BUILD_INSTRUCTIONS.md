# Hướng dẫn Build và Test App

## Yêu cầu

- Android Studio Hedgehog (2023.1.1) hoặc mới hơn
- JDK 11 hoặc mới hơn
- Android SDK API 24+ (Android 7.0)
- Thiết bị Android thật hoặc emulator có camera

## Bước 1: Sync Project

1. Mở Android Studio
2. File → Open → Chọn thư mục project
3. Đợi Gradle sync hoàn tất
4. Nếu có lỗi, click "Sync Now"

## Bước 2: Kiểm tra Dependencies

Đảm bảo `app/build.gradle` có các dependencies sau:

```gradle
// CameraX
def camerax_version = "1.3.1"
implementation "androidx.camera:camera-core:${camerax_version}"
implementation "androidx.camera:camera-camera2:${camerax_version}"
implementation "androidx.camera:camera-lifecycle:${camerax_version}"
implementation "androidx.camera:camera-view:${camerax_version}"

// Kotlin Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
```

## Bước 3: Build Project

### Option A: Build từ Android Studio
1. Build → Make Project (Ctrl+F9)
2. Đợi build hoàn tất
3. Kiểm tra Build Output không có lỗi

### Option B: Build từ Command Line
```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

## Bước 4: Chạy trên Thiết bị

### Thiết bị thật (Khuyến nghị):
1. Bật Developer Options trên điện thoại
2. Bật USB Debugging
3. Kết nối điện thoại với máy tính
4. Run → Run 'app' (Shift+F10)
5. Chọn thiết bị của bạn

### Emulator:
1. Tools → Device Manager
2. Tạo Virtual Device với:
   - API Level 24+
   - System Image có Google APIs
   - Enable Camera
3. Run app trên emulator

## Bước 5: Test Các Tính Năng

### Test 1: Live Mode
1. Mở app
2. Cấp quyền camera khi được hỏi
3. Camera preview hiển thị
4. Di chuyển camera đến biển báo
5. Xem kết quả real-time

**Expected**: 
- Preview mượt mà
- Kết quả cập nhật liên tục
- Độ tin cậy hiển thị

### Test 2: Capture Mode
1. Trong live mode, tap "Chụp ảnh"
2. Preview dừng lại
3. Ảnh đã chụp hiển thị
4. Xem top 3 predictions

**Expected**:
- Loading indicator hiển thị
- Ảnh đúng orientation
- Top 3 predictions hiển thị
- Nút "Chụp lại" xuất hiện

### Test 3: Retake
1. Từ capture mode, tap "Chụp lại"
2. Quay lại live preview

**Expected**:
- Preview tiếp tục
- Real-time analysis hoạt động
- Top 3 ẩn đi

### Test 4: Flash
1. Tap nút flash
2. Flash bật/tắt

**Expected**:
- Icon thay đổi
- Flash hoạt động
- Flash tắt khi pause app

### Test 5: Permission
1. Gỡ cài đặt app
2. Cài lại
3. Từ chối permission
4. Tap "Mở Cài Đặt"
5. Cấp permission
6. Quay lại app

**Expected**:
- Message hiển thị khi từ chối
- Settings mở đúng
- Camera khởi động sau khi cấp quyền

## Troubleshooting

### Lỗi: "Cannot resolve symbol 'PreviewView'"
**Solution**: 
```bash
File → Invalidate Caches → Invalidate and Restart
```

### Lỗi: "Manifest merger failed"
**Solution**: 
Kiểm tra AndroidManifest.xml không có duplicate activities

### Lỗi: "Camera initialization failed"
**Solution**:
- Kiểm tra permission đã được cấp
- Restart app
- Kiểm tra camera không bị app khác sử dụng

### Lỗi: "TFLite model not found"
**Solution**:
Đảm bảo `model_trained.tflite` và `labels.txt` có trong `app/src/main/assets/`

### App chạy chậm
**Solution**:
- Build Release variant thay vì Debug
- Test trên thiết bị thật, không phải emulator
- Kiểm tra model size

## Build Release APK

### Bước 1: Tạo Keystore (lần đầu)
```bash
keytool -genkey -v -keystore traffic-signs.keystore -alias traffic-signs -keyalg RSA -keysize 2048 -validity 10000
```

### Bước 2: Cấu hình Signing
Thêm vào `app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file("traffic-signs.keystore")
            storePassword "your-password"
            keyAlias "traffic-signs"
            keyPassword "your-password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### Bước 3: Build Release
```bash
# Windows
gradlew.bat assembleRelease

# Linux/Mac
./gradlew assembleRelease
```

APK sẽ được tạo tại: `app/build/outputs/apk/release/app-release.apk`

## Performance Testing

### Memory Profiler
1. Run app
2. View → Tool Windows → Profiler
3. Click "+" → Select app process
4. Monitor memory usage
5. Chụp nhiều ảnh và kiểm tra memory leak

**Expected**: Memory ổn định, không tăng liên tục

### CPU Profiler
1. Profiler → CPU
2. Record
3. Sử dụng app (live mode + capture)
4. Stop recording
5. Analyze

**Expected**: CPU usage hợp lý, không spike quá cao

### Battery
1. Settings → Battery → Battery Usage
2. Sử dụng app 10 phút
3. Kiểm tra battery consumption

**Expected**: < 5% battery trong 10 phút

## Checklist trước khi Release

- [ ] Tất cả tests pass
- [ ] Không có memory leak
- [ ] Performance tốt trên thiết bị thật
- [ ] UI responsive
- [ ] Permissions hoạt động đúng
- [ ] Flash hoạt động
- [ ] Rotation xử lý đúng
- [ ] Crash-free trong 30 phút sử dụng
- [ ] ProGuard rules đúng
- [ ] APK size hợp lý (< 50MB)
- [ ] Tested trên nhiều thiết bị khác nhau

## Devices Tested

Khuyến nghị test trên:
- [ ] Samsung Galaxy (Android 11+)
- [ ] Google Pixel (Android 12+)
- [ ] Xiaomi (MIUI)
- [ ] Oppo/Vivo (ColorOS)
- [ ] Low-end device (2GB RAM)

## Known Issues

1. **Emulator camera**: Có thể không hoạt động tốt, khuyến nghị dùng thiết bị thật
2. **First launch**: Có thể chậm do load model lần đầu
3. **Low light**: Độ chính xác giảm trong điều kiện ánh sáng yếu

## Support

Nếu gặp vấn đề:
1. Check logcat: `adb logcat | grep "TrafficSign"`
2. Clean project: Build → Clean Project
3. Rebuild: Build → Rebuild Project
4. Invalidate caches: File → Invalidate Caches

## Next Steps

Sau khi test thành công:
1. Optimize model nếu cần
2. Add more features (gallery, history)
3. Improve UI/UX
4. Add analytics
5. Prepare for Play Store release
