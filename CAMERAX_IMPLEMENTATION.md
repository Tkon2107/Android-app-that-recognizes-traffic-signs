# CameraX Implementation - Traffic Signs Classification

## Tổng quan

Đã triển khai thành công chức năng chụp ảnh và phân tích biển báo giao thông sử dụng CameraX API.

## Các tính năng mới

### 1. **Chế độ Trực tiếp (Live Mode)**
- Phân tích liên tục các khung hình từ camera
- Hiển thị kết quả real-time
- Cập nhật ngay lập tức khi phát hiện biển báo

### 2. **Chế độ Chụp ảnh (Capture Mode)**
- Nút "Chụp ảnh" để chụp khung hình hiện tại
- Dừng phân tích real-time
- Hiển thị ảnh đã chụp với kết quả phân loại
- Hiển thị top 3 dự đoán với độ tin cậy

### 3. **Nút Chụp lại (Retake)**
- Quay lại chế độ trực tiếp
- Tiếp tục phân tích real-time
- Xóa ảnh đã chụp

## Cấu trúc File

### Files mới được tạo:

1. **MainActivityCameraX.java**
   - Activity chính sử dụng CameraX
   - Quản lý camera lifecycle
   - Xử lý chuyển đổi giữa Live và Capture mode

2. **TrafficSignClassifier.java**
   - Class xử lý phân loại biển báo
   - Preprocessing: grayscale, histogram equalization, resize
   - TFLite inference
   - Trả về top 3 predictions

3. **ImageUtils.java**
   - Chuyển đổi ImageProxy sang Bitmap
   - Xử lý rotation (EXIF)
   - Hỗ trợ nhiều format: YUV_420_888, JPEG

4. **activity_main_camerax.xml**
   - Layout mới với PreviewView (CameraX)
   - ImageView cho ảnh đã chụp
   - Nút Chụp ảnh và Chụp lại
   - Loading indicator

### Files được cập nhật:

1. **app/build.gradle**
   - Thêm CameraX dependencies
   - Thêm Kotlin coroutines (cho CameraX)
   - Enable ViewBinding

2. **strings.xml**
   - Thêm strings cho capture mode
   - Strings cho top predictions
   - Strings cho loading state

3. **AndroidManifest.xml**
   - Đăng ký MainActivityCameraX
   - Giữ MainActivity cũ (Camera2) để tham khảo

4. **SplashActivity.java**
   - Chuyển sang khởi chạy MainActivityCameraX

## Cách hoạt động

### Live Mode (Mặc định)
```
Camera Preview → ImageAnalysis → Classifier → Update UI (real-time)
```

### Capture Mode
```
User taps "Chụp ảnh" → ImageCapture → Freeze frame → Classifier → Show results + top 3
```

### Return to Live
```
User taps "Chụp lại" → Hide captured image → Resume ImageAnalysis → Live mode
```

## Dependencies

```gradle
// CameraX
def camerax_version = "1.3.1"
implementation "androidx.camera:camera-core:${camerax_version}"
implementation "androidx.camera:camera-camera2:${camerax_version}"
implementation "androidx.camera:camera-lifecycle:${camerax_version}"
implementation "androidx.camera:camera-view:${camerax_version}"

// Kotlin Coroutines (required by CameraX)
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
```

## Permissions

Đã có trong AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature android:name="android.hardware.camera.flash" android:required="false" />
```

## Performance Optimizations

1. **Background Processing**
   - ImageAnalysis chạy trên ExecutorService riêng
   - Classifier chạy off UI thread
   - UI updates qua Handler

2. **Backpressure Strategy**
   - `STRATEGY_KEEP_ONLY_LATEST`: Chỉ phân tích frame mới nhất
   - Bỏ qua frames cũ nếu đang xử lý

3. **State Management**
   - `isLiveMode`: Kiểm soát chế độ
   - `isAnalyzing`: Tránh phân tích đồng thời
   - Pause/Resume ImageAnalysis khi chuyển mode

## UI/UX Features

1. **Loading Indicator**
   - Hiển thị khi đang chụp và phân tích
   - Ẩn khi hoàn thành

2. **Color Coding**
   - Xanh lá: Độ tin cậy cao (> 70%)
   - Đỏ: Không nhận diện được (< 70%)

3. **Top 3 Predictions**
   - Chỉ hiển thị trong Capture mode
   - Format: "1. Label: 95.5%"

4. **Smooth Transitions**
   - Fade animations
   - Smooth mode switching

## Testing Plan

### 1. Live Mode Testing
- [ ] Mở app, camera preview hiển thị ngay
- [ ] Di chuyển camera đến biển báo
- [ ] Kết quả cập nhật real-time
- [ ] Độ tin cậy hiển thị chính xác

### 2. Capture Mode Testing
- [ ] Tap "Chụp ảnh"
- [ ] Preview dừng lại
- [ ] Ảnh đã chụp hiển thị
- [ ] Top 3 predictions hiển thị
- [ ] Loading indicator hoạt động

### 3. Retake Testing
- [ ] Tap "Chụp lại"
- [ ] Quay lại live preview
- [ ] Real-time analysis tiếp tục
- [ ] Top 3 predictions ẩn đi

### 4. Flash Testing
- [ ] Toggle flash trong live mode
- [ ] Flash tắt khi pause app
- [ ] Flash hoạt động trong capture mode

### 5. Permission Testing
- [ ] Từ chối permission → Hiển thị message
- [ ] "Mở Cài Đặt" button hoạt động
- [ ] Cấp permission → Camera khởi động

### 6. Rotation Testing
- [ ] Xoay thiết bị
- [ ] Ảnh chụp đúng orientation
- [ ] UI không bị lỗi

### 7. Performance Testing
- [ ] Không lag khi phân tích real-time
- [ ] Memory không leak
- [ ] Battery consumption hợp lý

## Troubleshooting

### Issue: Camera không khởi động
**Solution**: Kiểm tra permission trong Settings

### Issue: Ảnh bị xoay sai
**Solution**: ImageUtils.rotateBitmap() xử lý rotation từ EXIF

### Issue: Phân tích chậm
**Solution**: 
- Giảm resolution nếu cần
- Kiểm tra model size
- Optimize preprocessing

### Issue: App crash khi chụp ảnh
**Solution**: 
- Kiểm tra memory
- Ensure ImageProxy được close()
- Check bitmap null

## So sánh Camera2 vs CameraX

| Feature | Camera2 (Old) | CameraX (New) |
|---------|--------------|---------------|
| API Complexity | Cao | Thấp |
| Lifecycle Management | Manual | Automatic |
| Image Capture | TextureView.getBitmap() | ImageCapture API |
| Preview | TextureView | PreviewView |
| Analysis | Manual frame processing | ImageAnalysis |
| Code Lines | ~500 lines | ~300 lines |
| Maintenance | Khó | Dễ |

## Migration Notes

- MainActivity cũ (Camera2) vẫn được giữ lại trong project
- Có thể chuyển về Camera2 bằng cách đổi trong SplashActivity
- Tất cả preprocessing logic giữ nguyên
- Model và labels không thay đổi

## Future Enhancements

1. **Gallery Selection**: Chọn ảnh từ thư viện
2. **History**: Lưu lịch sử phân loại
3. **Batch Processing**: Phân tích nhiều ảnh cùng lúc
4. **Export Results**: Xuất kết quả ra file
5. **Settings**: Điều chỉnh threshold, model selection

## Kết luận

Implementation hoàn chỉnh với:
- ✅ CameraX integration
- ✅ Live mode với real-time analysis
- ✅ Capture mode với still image
- ✅ Top 3 predictions
- ✅ Loading indicators
- ✅ Proper rotation handling
- ✅ Background processing
- ✅ Clean UI/UX
- ✅ Vietnamese localization

App sẵn sàng để build và test trên thiết bị thật!
