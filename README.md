# Android App for Traffic Sign Recognition (YOLOv11 + ONNX)

Ứng dụng Android nhận diện biển báo giao thông theo thời gian thực bằng CameraX và mô hình YOLOv11 chạy on-device qua ONNX Runtime.

Dự án đã tích hợp sẵn module OpenCV trong workspace, không cần tải thêm từ repository khác.

## Tính năng chính

- Nhận diện biển báo giao thông theo thời gian thực (live mode).
- Chụp ảnh và phân tích khung hình tĩnh (capture mode).
- Vẽ bounding box trực tiếp trên ảnh preview/captured.
- Hiển thị nhãn biển báo và độ tin cậy.
- Tra cứu thông tin mức phạt giao thông từ dữ liệu JSON nội bộ.
- Hỗ trợ bật/tắt flash, xử lý quyền camera, màn hình splash.

## Công nghệ sử dụng

- Android SDK (minSdk 24, targetSdk 34)
- Java
- CameraX 1.3.1
- ONNX Runtime Android 1.17.0
- OpenCV (module nội bộ `:opencv`)
- Gson (đọc dữ liệu mức phạt từ JSON)

## Cấu trúc dự án

```text
app/
  src/main/
    java/com/trafficsignsclassification/
      MainActivityCameraX.java
      YoloV11Detector.java
      SplashActivity.java
      DetectionOverlayView.java
      ImageUtils.java
      penalty/
        PenaltyRepository.java
        PenaltyDetailActivity.java
        PenaltyInfo.java
    assets/
      best.onnx
      yolo_labels.txt
      penalties.json
opencv/
model/
```

## Yêu cầu môi trường

- Android Studio Hedgehog (2023.1.1) hoặc mới hơn
- JDK 11+
- Android SDK API 24+
- Thiết bị thật có camera (khuyến nghị) hoặc emulator hỗ trợ camera

## Chạy dự án

1. Mở project trong Android Studio.
2. Chờ Gradle sync hoàn tất.
3. Chạy app bằng cấu hình `app` trên thiết bị/emulator.
4. Cấp quyền camera khi ứng dụng yêu cầu.

## Build từ command line

```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

APK debug sau khi build nằm tại:

```text
app/build/outputs/apk/debug/
```

## Dữ liệu model và nhãn

- Model ONNX: `app/src/main/assets/best.onnx`
- File nhãn: `app/src/main/assets/yolo_labels.txt`
- Dữ liệu mức phạt: `app/src/main/assets/penalties.json`

Lưu ý: trong `app/build.gradle`, dự án đã cấu hình `noCompress "onnx"` để tránh nén model ONNX trong APK.

## Luồng hoạt động chính

1. `SplashActivity` khởi tạo dữ liệu mức phạt.
2. `MainActivityCameraX` mở camera và xử lý luồng preview bằng CameraX.
3. `YoloV11Detector` chạy suy luận YOLOv11 qua ONNX Runtime.
4. `DetectionOverlayView` hiển thị bounding boxes lên UI.
5. Khi có nhãn phù hợp, người dùng có thể mở `PenaltyDetailActivity` để xem mức phạt.

## Tùy chỉnh dữ liệu mức phạt

Bạn có thể cập nhật mức phạt mà không cần huấn luyện lại model:

1. Chỉnh sửa file `app/src/main/assets/penalties.json`.
2. Build lại ứng dụng.

## Tài liệu liên quan trong repo

- `BUILD_INSTRUCTIONS.md`: Hướng dẫn build/test chi tiết.
- `CAMERAX_IMPLEMENTATION.md`: Chi tiết triển khai CameraX.
- `IMPLEMENTATION_SUMMARY.md`: Tổng quan triển khai.
- `NEW_FEATURES_SUMMARY.md`: Mô tả module mức phạt và đánh giá.
- `MIGRATION_GUIDE.md`: Ghi chú chuyển đổi kiến trúc.

## Ghi chú

- Một số tài liệu cũ trong repository có thể vẫn nhắc đến pipeline CNN/TFLite trước đây.
- Mã nguồn hiện tại trong module app đang chạy YOLOv11 ONNX làm pipeline chính.

