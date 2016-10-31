package com.tedkim.android.tcamera.interfaces;

import android.graphics.Bitmap;

import java.io.File;

/**
 * TCamera Listener
 * Created by Ted
 */
public class OnTCameraListener {

    public void onSuccess(String filePath){}
    public void onSuccess(String filePath, Bitmap bitmap, File file){}
    public void onDefaultImage(){}
    public void onFail(){}

}
