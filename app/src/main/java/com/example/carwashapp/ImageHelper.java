package com.example.carwashapp;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageHelper {

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }
}
