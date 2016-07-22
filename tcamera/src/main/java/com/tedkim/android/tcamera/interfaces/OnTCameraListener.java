package com.tedkim.android.tcamera.interfaces;

import android.graphics.Bitmap;

import java.io.File;

/**
 * TCamera Listener
 * Created by Ted
 */
public interface OnTCameraListener {

    void onSuccess(String filePath, Bitmap bitmap, File file);
    void onDefaultImage();
    void onFail();

}
