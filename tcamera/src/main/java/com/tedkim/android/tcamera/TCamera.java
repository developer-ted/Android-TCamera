package com.tedkim.android.tcamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.tedkim.android.tcamera.interfaces.OnTCameraListener;
import com.tedkim.android.tcamera.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ted
 */
public class TCamera {

    private static final String TAG = TCamera.class.getSimpleName();

    public static final String SUCCESS = "SUCCESS";
    public static final String SUCCESS_TYPE = "SUCCESS_TYPE";
    public static final String IMAGE_URI = "IMAGE_URI";
    public static final String VIDEO_URI = "VIDEO_URI";
    public static final String SUCCESS_TYPE_SELECT_PHOTO = "SUCCESS_TYPE_SELECT_PHOTO";
    public static final String SUCCESS_TYPE_SELECT_VIDEO = "SUCCESS_TYPE_SELECT_VIDEO";
    public static final String SUCCESS_TYPE_DEFAULT_IMAGE = "SUCCESS_TYPE_DEFAULT_IMAGE";

    public static TCamera mInstance;
    private Context mContext;

    private OnTCameraListener mOnTCameraListener;

    private boolean isDefaultImage;
    private boolean isShowDialog;

    private String mToastFailCamera;
    private String mToastFailGallery;
    private String mToastFailCropImage;

    private String mDialogTitle;
    private String mDialogCancel;
    private String mDialogCameraText;
    private String mDialogGalleryText;
    private String mDialogDefaultImageText;

    private int mRatioX = 1;
    private int mRatioY = 1;
    private int mImageWidth = 300;
    private int mImageHeight = 300;

    public static TCamera getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TCamera();
        }
        mInstance.initTCamera(context);
        return mInstance;
    }

    public TCamera() {
        isShowDialog = false;
    }

    public void initTCamera(Context context) {
        this.mContext = context;
    }

    /**
     * Set OnTCameraListener
     *
     * @param listener listener
     * @return TCamera
     */
    public TCamera setListener(OnTCameraListener listener) {
        mOnTCameraListener = listener;
        return this;
    }

    /**
     * Cropped image ratio
     *
     * @param ratioX image x ratio
     * @param ratioY image y ratio
     * @return TCamera
     */
    public TCamera setCropRatio(int ratioX, int ratioY) {
        mRatioX = ratioX;
        mRatioY = ratioY;
        return this;
    }

    /**
     * Cropped image size
     *
     * @param sizeWidth  image width
     * @param sizeHeight image height
     * @return TCamera
     */
    public TCamera setCropImageSize(int sizeWidth, int sizeHeight) {
        mImageWidth = sizeWidth;
        mImageHeight = sizeHeight;
        return this;
    }

    /**
     * Toast camera failure message
     *
     * @param message toast message
     * @return TCamera
     */
    public TCamera setToastFailCamera(String message) {
        mToastFailCamera = message;
        return this;
    }

    /**
     * Toast gallery failure message
     *
     * @param message toast message
     * @return TCamera
     */
    public TCamera setToastFailGallery(String message) {
        mToastFailGallery = message;
        return this;
    }

    /**
     * Toast crop image failure message
     *
     * @param message toast message
     * @return TCamera
     */
    public TCamera setToastFailCropImage(String message) {
        mToastFailCropImage = message;
        return this;
    }

    /**
     * Default image used in Dialog
     *
     * @param defaultImage true : default image used, false : default image not used
     * @return TCamera
     */
    public TCamera setDialogDefaultImage(boolean defaultImage) {
        isDefaultImage = defaultImage;
        return this;
    }

    /**
     * Title in dialog
     *
     * @param title title
     * @return TCamera
     */
    public TCamera setDialogTitle(String title) {
        this.mDialogTitle = title;
        return this;
    }

    /**
     * Cancel button text in dialog
     *
     * @param text button text
     * @return TCamera
     */
    public TCamera setDialogCancel(String text) {
        this.mDialogCancel = text;
        return this;
    }

    /**
     * Dialog camera select text
     *
     * @param text dialog text
     * @return TCamera
     */
    public TCamera setDialogCameraText(String text) {
        this.mDialogCameraText = text;
        return this;
    }

    /**
     * Dialog gallery select text
     *
     * @param text dialog text
     * @return TCamera
     */
    public TCamera setDialogGalleryText(String text) {
        this.mDialogGalleryText = text;
        return this;
    }

    /**
     * Dialog default image select text
     *
     * @param text dialog text
     * @return TCamera
     */
    public TCamera setDialogDefaultImageText(String text) {
        this.mDialogDefaultImageText = text;
        return this;
    }

    /**
     * Show dialog
     */
    public void build() {
        if (mOnTCameraListener == null)
            throw new NullPointerException("You must setListener()");

        if (!isShowDialog) {
            isShowDialog = true;
            Intent intent = new Intent(mContext, TCameraActivity.class);
            intent.putExtra(TCameraActivity.DEFAULT_IMAGE, isDefaultImage);
            intent.putExtra(TCameraActivity.RATIO_X, mRatioX);
            intent.putExtra(TCameraActivity.RATIO_Y, mRatioY);
            intent.putExtra(TCameraActivity.IMAGE_WIDTH, mImageWidth);
            intent.putExtra(TCameraActivity.IMAGE_HEIGHT, mImageHeight);
            intent.putExtra(TCameraActivity.TOAST_FAIL_CAMERA, mToastFailCamera);
            intent.putExtra(TCameraActivity.TOAST_FAIL_GALLERY, mToastFailGallery);
            intent.putExtra(TCameraActivity.TOAST_FAIL_CROP_IMAGE, mToastFailCropImage);
            intent.putExtra(TCameraActivity.DIALOG_TITLE, mDialogTitle);
            intent.putExtra(TCameraActivity.DIALOG_CANCEL, mDialogCancel);
            intent.putExtra(TCameraActivity.DIALOG_CAMERA_TEXT, mDialogCameraText);
            intent.putExtra(TCameraActivity.DIALOG_GALLERY_TEXT, mDialogGalleryText);
            intent.putExtra(TCameraActivity.DIALOG_DEFAULT_IMAGE_TEXT, mDialogDefaultImageText);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    public void success(Intent intent) {
        isShowDialog = false;
        if (intent.getStringExtra(SUCCESS_TYPE).equals(SUCCESS_TYPE_DEFAULT_IMAGE)) {
            if (mOnTCameraListener != null)
                mOnTCameraListener.onDefaultImage();

            if (mInstance != null)
                mInstance = null;
        }
        else if (intent.getStringExtra(SUCCESS_TYPE).equals(SUCCESS_TYPE_SELECT_VIDEO)) {
            String uri = FileUtils.getPathFromURI(mContext, Uri.parse(intent.getStringExtra(VIDEO_URI)));

            if (mOnTCameraListener != null)
                mOnTCameraListener.onSuccess(uri);
        }
        else {
            Bundle extras = intent.getExtras();
            Bitmap bitmap = null;
            File mFile;
            String mFilePath;
            String uri = intent.getStringExtra(IMAGE_URI);

            if (extras != null) {
                bitmap = extras.getParcelable("data");
            } else {
                try {
                    Uri imageUri = intent.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mFilePath = FileUtils.getPathFromURI(mContext, Uri.parse(uri));
            mFile = FileUtils.getFileFromBitmap(bitmap, mFilePath);

            if (mOnTCameraListener != null)
                mOnTCameraListener.onSuccess(mFilePath, bitmap, mFile);
        }
    }

    public void fail() {
        isShowDialog = false;
        if (mOnTCameraListener != null)
            mOnTCameraListener.onFail();

        if (mInstance != null)
            mInstance = null;
    }

    /**
     * Remove image
     *
     * @param file delete file
     */
    public void removeImage(File file) {
        isShowDialog = false;
        if (file != null && file.exists()) {
            file.delete();
        }

        if (mInstance != null)
            mInstance = null;
    }
}
