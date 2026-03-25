"""
Validation script to compare ONNX inference results with Ultralytics Python inference.
Run this to get baseline results that should match the Android app output.

Usage:
    python validate_onnx.py --image path/to/test_image.jpg
    python validate_onnx.py --image path/to/test_image.jpg --conf 0.25 --iou 0.45
"""

import argparse
import numpy as np
import onnxruntime as ort
from ultralytics import YOLO
from PIL import Image


def letterbox(image, new_shape=640, color=(114, 114, 114)):
    """Letterbox resize matching Ultralytics implementation."""
    img = np.array(image)
    h, w = img.shape[:2]

    scale = min(new_shape / w, new_shape / h)
    new_w, new_h = round(w * scale), round(h * scale)

    # Resize
    resized = np.array(Image.fromarray(img).resize((new_w, new_h), Image.BILINEAR))

    # Create padded image
    padded = np.full((new_shape, new_shape, 3), color, dtype=np.uint8)
    pad_w = (new_shape - new_w) // 2
    pad_h = (new_shape - new_h) // 2
    padded[pad_h:pad_h + new_h, pad_w:pad_w + new_w] = resized

    return padded, scale, pad_w, pad_h


def nms(boxes, scores, iou_threshold):
    """Non-Maximum Suppression."""
    if len(boxes) == 0:
        return []

    x1 = boxes[:, 0]
    y1 = boxes[:, 1]
    x2 = boxes[:, 2]
    y2 = boxes[:, 3]
    areas = (x2 - x1) * (y2 - y1)
    order = scores.argsort()[::-1]

    keep = []
    while order.size > 0:
        i = order[0]
        keep.append(i)

        xx1 = np.maximum(x1[i], x1[order[1:]])
        yy1 = np.maximum(y1[i], y1[order[1:]])
        xx2 = np.minimum(x2[i], x2[order[1:]])
        yy2 = np.minimum(y2[i], y2[order[1:]])

        w_inter = np.maximum(0.0, xx2 - xx1)
        h_inter = np.maximum(0.0, yy2 - yy1)
        inter = w_inter * h_inter

        iou = inter / (areas[i] + areas[order[1:]] - inter + 1e-6)
        inds = np.where(iou <= iou_threshold)[0]
        order = order[inds + 1]

    return keep


def run_onnx_inference(onnx_path, image_path, conf_threshold=0.25, iou_threshold=0.45):
    """Run inference using ONNX Runtime (same logic as Android)."""
    print("=" * 60)
    print("ONNX Runtime Inference (matches Android)")
    print("=" * 60)

    image = Image.open(image_path).convert("RGB")
    orig_w, orig_h = image.size

    # Preprocess (letterbox)
    padded, scale, pad_w, pad_h = letterbox(image, 640)

    # Normalize and convert to CHW
    input_data = padded.astype(np.float32) / 255.0
    input_data = np.transpose(input_data, (2, 0, 1))  # HWC -> CHW
    input_data = np.expand_dims(input_data, axis=0)     # Add batch dim

    # Run ONNX inference
    session = ort.InferenceSession(onnx_path)
    input_name = session.get_inputs()[0].name
    output = session.run(None, {input_name: input_data})

    # Output shape: [1, 56, 8400]
    data = output[0][0]  # [56, 8400]
    num_detections = data.shape[1]

    # Post-process
    boxes = []
    scores = []
    class_ids = []

    for i in range(num_detections):
        cx, cy, w, h = data[0, i], data[1, i], data[2, i], data[3, i]
        class_scores = data[4:, i]
        max_score = np.max(class_scores)
        max_class = np.argmax(class_scores)

        if max_score < conf_threshold:
            continue

        # Center to corner format
        x1 = cx - w / 2
        y1 = cy - h / 2
        x2 = cx + w / 2
        y2 = cy + h / 2

        # Remove letterbox padding and scale to original
        x1 = (x1 - pad_w) / scale
        y1 = (y1 - pad_h) / scale
        x2 = (x2 - pad_w) / scale
        y2 = (y2 - pad_h) / scale

        # Clip
        x1 = max(0, min(x1, orig_w))
        y1 = max(0, min(y1, orig_h))
        x2 = max(0, min(x2, orig_w))
        y2 = max(0, min(y2, orig_h))

        boxes.append([x1, y1, x2, y2])
        scores.append(float(max_score))
        class_ids.append(int(max_class))

    # NMS per class
    if boxes:
        boxes = np.array(boxes)
        scores = np.array(scores)
        class_ids = np.array(class_ids)

        final_indices = []
        for cls in np.unique(class_ids):
            cls_mask = class_ids == cls
            cls_boxes = boxes[cls_mask]
            cls_scores = scores[cls_mask]
            cls_original_indices = np.where(cls_mask)[0]
            kept = nms(cls_boxes, cls_scores, iou_threshold)
            final_indices.extend(cls_original_indices[kept].tolist())

        # Sort by score
        final_indices.sort(key=lambda i: scores[i], reverse=True)

        # Load labels
        labels = load_labels()

        print(f"\nDetected {len(final_indices)} objects:")
        for idx in final_indices:
            label = labels.get(class_ids[idx], f"class_{class_ids[idx]}")
            print(f"  [{class_ids[idx]:2d}] {label}")
            print(f"       Confidence: {scores[idx]:.6f}")
            print(f"       Box: ({boxes[idx][0]:.2f}, {boxes[idx][1]:.2f}, "
                  f"{boxes[idx][2]:.2f}, {boxes[idx][3]:.2f})")
    else:
        print("No detections above threshold.")

    return boxes, scores, class_ids


def run_ultralytics_inference(model_path, image_path, conf_threshold=0.25, iou_threshold=0.45):
    """Run inference using Ultralytics Python API (reference baseline)."""
    print("=" * 60)
    print("Ultralytics Python Inference (reference baseline)")
    print("=" * 60)

    model = YOLO(model_path)
    results = model.predict(image_path, conf=conf_threshold, iou=iou_threshold, verbose=False)

    for result in results:
        boxes = result.boxes
        print(f"\nDetected {len(boxes)} objects:")
        for box in boxes:
            cls_id = int(box.cls[0])
            conf = float(box.conf[0])
            xyxy = box.xyxy[0].cpu().numpy()
            label = model.names[cls_id]
            print(f"  [{cls_id:2d}] {label}")
            print(f"       Confidence: {conf:.6f}")
            print(f"       Box: ({xyxy[0]:.2f}, {xyxy[1]:.2f}, {xyxy[2]:.2f}, {xyxy[3]:.2f})")


def load_labels():
    """Load labels from yolo_labels.txt."""
    labels = {}
    try:
        with open("app/src/main/assets/yolo_labels.txt", "r", encoding="utf-8") as f:
            for i, line in enumerate(f):
                line = line.strip()
                if line:
                    labels[i] = line
    except FileNotFoundError:
        pass
    return labels


def main():
    parser = argparse.ArgumentParser(description="Validate ONNX vs Ultralytics inference")
    parser.add_argument("--image", required=True, help="Path to test image")
    parser.add_argument("--conf", type=float, default=0.25, help="Confidence threshold")
    parser.add_argument("--iou", type=float, default=0.45, help="NMS IoU threshold")
    args = parser.parse_args()

    print(f"Image: {args.image}")
    print(f"Confidence threshold: {args.conf}")
    print(f"IoU threshold: {args.iou}")
    print()

    # 1. Ultralytics reference
    run_ultralytics_inference("model/best.pt", args.image, args.conf, args.iou)
    print()

    # 2. ONNX (matches Android implementation)
    run_onnx_inference("model/best.onnx", args.image, args.conf, args.iou)


if __name__ == "__main__":
    main()
