package com.trafficsignsclassification;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtils {

    /**
     * Convert ImageProxy to Bitmap
     */
    public static Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        if (imageProxy == null) return null;

        try {
            // Get the image format
            int format = imageProxy.getFormat();

            if (format == ImageFormat.YUV_420_888) {
                return yuv420ToBitmap(imageProxy);
            } else if (format == ImageFormat.JPEG) {
                return jpegToBitmap(imageProxy);
            } else {
                // Fallback: try to convert planes to bitmap
                return planesToBitmap(imageProxy);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert YUV_420_888 to Bitmap
     */
    private static Bitmap yuv420ToBitmap(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image == null) return null;

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, 
            image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 
            100, out);

        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        // Rotate bitmap according to image rotation
        return rotateBitmap(bitmap, imageProxy.getImageInfo().getRotationDegrees());
    }

    /**
     * Convert JPEG to Bitmap
     */
    private static Bitmap jpegToBitmap(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image == null) return null;

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return rotateBitmap(bitmap, imageProxy.getImageInfo().getRotationDegrees());
    }

    /**
     * Fallback: Convert planes to Bitmap
     */
    private static Bitmap planesToBitmap(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image == null) return null;

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * image.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(
            image.getWidth() + rowPadding / pixelStride,
            image.getHeight(),
            Bitmap.Config.ARGB_8888
        );

        bitmap.copyPixelsFromBuffer(buffer);

        // Crop if needed
        if (rowPadding > 0) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight());
        }

        return rotateBitmap(bitmap, imageProxy.getImageInfo().getRotationDegrees());
    }

    /**
     * Rotate bitmap by degrees
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        if (bitmap == null || rotationDegrees == 0) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);

        return Bitmap.createBitmap(bitmap, 0, 0, 
            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
