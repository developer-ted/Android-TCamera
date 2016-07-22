package com.tedkim.android.tcamera;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tedkim.android.tcamera.broadcast.CameraReceiver;
import com.tedkim.android.tcamera.dialog.SelectPhotoDialog;
import com.tedkim.android.tcamera.interfaces.DialogListener;
import com.tedkim.android.tcamera.utils.ImageUtils;

import java.io.File;

/**
 * TCamera activity
 * Created by Ted
 */
public class TCameraActivity extends AppCompatActivity {

    public static final String CROP_IMAGE_NAME = "crop_image";
    public static final String DEFAULT_IMAGE = "DEFAULT_IMAGE";
    public static final String RATIO_X = "RATIO_X";
    public static final String RATIO_Y = "RATIO_Y";
    public static final String IMAGE_WIDTH = "IMAGE_WIDTH";
    public static final String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    public static final String TOAST_FAIL_CAMERA = "TOAST_FAIL_CAMERA";
    public static final String TOAST_FAIL_GALLERY = "TOAST_FAIL_GALLERY";
    public static final String TOAST_FAIL_CROP_IMAGE = "TOAST_FAIL_CROP_IMAGE";
    public static final String DIALOG_TITLE = "DIALOG_TITLE";
    public static final String DIALOG_CANCEL = "DIALOG_CANCEL";
    public static final String DIALOG_CAMERA_TEXT = "DIALOG_CAMERA_TEXT";
    public static final String DIALOG_GALLERY_TEXT = "DIALOG_GALLERY_TEXT";
    public static final String DIALOG_DEFAULT_IMAGE_TEXT = "DIALOG_DEFAULT_IMAGE_TEXT";

    private static final int PATH_CAMERA = 8242;
    private static final int PATH_GALLERY = 8123;
    private static final int PATH_CROP_IMAGE = 8394;

    private CameraReceiver mCameraReceiver;
    private SelectPhotoDialog selectPhotoDialog;

    private Uri mImageCaptureUri;

    private boolean isDefaultImage;

    private String mToastFailCamera;
    private String mToastFailGallery;
    private String mToastFailCropImage;

    private String mDialogTitle;
    private String mDialogCancel;
    private String mCameraText;
    private String mGalleryText;
    private String mDefaultImageText;

    private int mRatioX;
    private int mRatioY;
    private int mSizeX;
    private int mSizeY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        showSelectList();
    }

    private void initData() {
        mCameraReceiver = new CameraReceiver();
        registerReceiver(mCameraReceiver, new IntentFilter());

        Intent intent = getIntent();
        isDefaultImage = intent.getBooleanExtra(DEFAULT_IMAGE, false);

        mRatioX = intent.getIntExtra(RATIO_X, 1);
        mRatioY = intent.getIntExtra(RATIO_Y, 1);
        mSizeX = intent.getIntExtra(IMAGE_WIDTH, 300);
        mSizeY = intent.getIntExtra(IMAGE_HEIGHT, 300);

        mToastFailCamera = intent.getStringExtra(TOAST_FAIL_CAMERA);
        mToastFailGallery = intent.getStringExtra(TOAST_FAIL_GALLERY);
        mToastFailCropImage = intent.getStringExtra(TOAST_FAIL_CROP_IMAGE);

        mDialogTitle = intent.getStringExtra(DIALOG_TITLE);
        mDialogCancel = intent.getStringExtra(DIALOG_CANCEL);
        mCameraText = intent.getStringExtra(DIALOG_CAMERA_TEXT);
        mGalleryText = intent.getStringExtra(DIALOG_GALLERY_TEXT);
        mDefaultImageText = intent.getStringExtra(DIALOG_DEFAULT_IMAGE_TEXT);

        if (mToastFailCamera == null)
            mToastFailCamera = getString(R.string.toast_fail_photo_select);

        if (mToastFailGallery == null)
            mToastFailGallery = getString(R.string.toast_fail_photo_select);

        if (mToastFailCropImage == null)
            mToastFailCropImage = getString(R.string.toast_fail_photo_select);

        if (mDialogTitle == null)
            mDialogTitle = getString(R.string.dialog_photo_title);

        if (mDialogCancel == null)
            mDialogCancel = getString(R.string.common_cancel);

        if (mCameraText == null)
            mCameraText = getString(R.string.dialog_photo_camera);

        if (mGalleryText == null)
            mGalleryText = getString(R.string.dialog_photo_gallery);

        if (mDefaultImageText == null)
            mDefaultImageText = getString(R.string.dialog_photo_default);
    }

    public void showSelectList() {
        selectPhotoDialog = new SelectPhotoDialog(this, isDefaultImage, new DialogListener() {
            @Override
            public void onConfirm(int type) {
                selectPhotoDialog.dismiss();
                switch (type) {
                    case SelectPhotoDialog.SELECT_CAMERA:
                        intentCamera();
                        break;

                    case SelectPhotoDialog.SELECT_GALLERY:
                        intentGallery();
                        break;

                    case SelectPhotoDialog.SELECT_DEFAULT_IMAGE:
                        Intent intent = new Intent();
                        intent.setAction(getString(R.string.camera_action));
                        intent.putExtra(TCamera.SUCCESS, true);
                        intent.putExtra(TCamera.SUCCESS_TYPE, TCamera.SUCCESS_TYPE_DEFAULT_IMAGE);
                        finishCameraActivity(intent);
                        break;
                }
            }

            @Override
            public void onCancel() {
                selectPhotoDialog.dismiss();
                failCamera();
            }
        });
        selectPhotoDialog.setTitle(mDialogTitle);
        selectPhotoDialog.setCancelButton(mDialogCancel);
        selectPhotoDialog.setCameraText(mCameraText);
        selectPhotoDialog.setGalleryText(mGalleryText);
        selectPhotoDialog.setDefaultImageText(mDefaultImageText);
        selectPhotoDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PATH_CAMERA:
                if (resultCode == RESULT_OK) {
                    intentCropActivity();
                } else {
                    Toast.makeText(TCameraActivity.this, mToastFailCamera, Toast.LENGTH_SHORT).show();
                    showSelectList();
                }
                break;

            case PATH_GALLERY:
                if (data == null) {
                    Toast.makeText(TCameraActivity.this, mToastFailGallery, Toast.LENGTH_SHORT).show();
                    showSelectList();
                    return;
                }

                mImageCaptureUri = data.getData();
                mImageCaptureUri = ImageUtils.getImageUriWithAuthority(this, mImageCaptureUri);

                if (mImageCaptureUri == null) {
                    Toast.makeText(TCameraActivity.this, mToastFailGallery, Toast.LENGTH_SHORT).show();
                    return;
                }

                intentCropActivity();
                break;

            case PATH_CROP_IMAGE:
                if (data == null) {
                    Toast.makeText(TCameraActivity.this, mToastFailCropImage, Toast.LENGTH_SHORT).show();
                    showSelectList();
                    return;
                }

                data.setAction(getString(R.string.camera_action));
                data.putExtra(TCamera.SUCCESS, true);
                data.putExtra(TCamera.SUCCESS_TYPE, TCamera.SUCCESS_TYPE_SELECT_IMAGE);
                data.putExtra(TCamera.IMAGE_URI, mImageCaptureUri.toString());
                finishCameraActivity(data);
                break;
        }
    }

    /**
     * Intent crop activity
     */
    private void intentCropActivity() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, "image/*");
        intent.putExtra("outputX", mSizeX);
        intent.putExtra("outputY", mSizeY);
        intent.putExtra("aspectX", mRatioX);
        intent.putExtra("aspectY", mRatioY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PATH_CROP_IMAGE);
    }

    /**
     * intent Camera
     */
    private void intentCamera() {
        String fileName = CROP_IMAGE_NAME + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PATH_CAMERA);
    }

    /**
     * intent Gallery
     */
    private void intentGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PATH_GALLERY);
    }

    private void failCamera() {
        Intent intent = new Intent();
        intent.setAction(getString(R.string.camera_action));
        intent.putExtra(TCamera.SUCCESS, false);
        finishCameraActivity(intent);
    }

    private void finishCameraActivity(Intent intent) {
        sendBroadcast(intent);
        unregisterReceiver(mCameraReceiver);
        finish();
        overridePendingTransition(0, 0);
    }
}
