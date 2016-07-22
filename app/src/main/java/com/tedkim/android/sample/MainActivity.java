package com.tedkim.android.sample;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tedkim.android.permission.TPermission;
import com.tedkim.android.permission.interfaces.OnPermissionListener;
import com.tedkim.android.tcamera.TCamera;
import com.tedkim.android.tcamera.interfaces.OnTCameraListener;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);
        findViewById(R.id.btnDialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDialog:
                // Check permission
                TPermission.getInstance(getApplicationContext())
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setListener(mOnPermissionListener)
                        .setDescriptionMessage("This app need permission")
                        .setConfirmText("Confirm")
                        .setCancelText("Cancel")
                        .setDenyMessage("If you reject permission, this service you want is not available\\nPlease turn on permissions at [Setting] > [Permission]")
                        .setShowSetting(true)
                        .setSettingText("Setting")
                        .build();
                break;
        }
    }

    private OnPermissionListener mOnPermissionListener = new OnPermissionListener() {
        @Override
        public void onAllowPermission() {
            TCamera.getInstance(MainActivity.this)
                    .setListener(mOnTCameraListener)
                    .setCropRatio(1, 1)
                    .setCropImageSize(200, 200)
                    .setToastFailCamera("It failed to select a photo")
                    .setToastFailGallery("It failed to select a photo")
                    .setToastFailCropImage("It failed to select a photo")
                    .setDialogDefaultImage(true)
                    .setDialogTitle("Photo Select")
                    .setDialogCancel("Cancel")
                    .setDialogCameraText("Shoot new photo")
                    .setDialogGalleryText("Choose from the gallery")
                    .setDialogDefaultImageText("Use the default image")
                    .build();
        }

        @Override
        public void onDenyPermission(String[] permissions) {
            Log.d(TAG, "[onDenyPermission] permissions : " + Arrays.toString(permissions));
        }
    };

    private OnTCameraListener mOnTCameraListener = new OnTCameraListener() {
        @Override
        public void onSuccess(String filePath, Bitmap bitmap, File file) {
            Log.d(TAG, "[onSuccess] FilePath : " + filePath + ", bitmap : " + bitmap + ", file : " + file);
            img.setImageBitmap(bitmap);
            TCamera.getInstance(MainActivity.this).removeImage(file);
        }

        @Override
        public void onDefaultImage() {
            Log.d(TAG, "[onDefaultImage]");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "[onFail]");
        }
    };
}
