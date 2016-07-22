package com.tedkim.android.tcamera.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tedkim.android.tcamera.R;
import com.tedkim.android.tcamera.adapter.PhotoAdapter;
import com.tedkim.android.tcamera.interfaces.DialogListener;
import com.tedkim.android.tcamera.interfaces.OnRecyclerViewItemClickListener;

import java.util.ArrayList;

/**
 * Created by ted
 */
public class SelectPhotoDialog extends Dialog {

    private static final String TAG = SelectPhotoDialog.class.getSimpleName();

    public static final int SELECT_CAMERA = 0;
    public static final int SELECT_GALLERY = 1;
    public static final int SELECT_DEFAULT_IMAGE = 2;

    private Activity mActivity;
    private DialogListener mPopupListener;
    private PhotoAdapter mAdapter;

    private TextView textTitle;
    private TextView textCancel;
    private RecyclerView recyclerPhoto;

    private boolean isDefaultImage = false;

    private String mTitle;
    private String mCancelButton;
    private String mCamera;
    private String mGallery;
    private String mDefaultImage;

    public SelectPhotoDialog(Activity activity, boolean defaultImage, DialogListener listener) {
        super(activity, R.style.MaterialDialog);
        setContentView(R.layout.dialog_select_photo);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mActivity = activity;
        mPopupListener = listener;
        isDefaultImage = defaultImage;

        setOnCancelListener(mCancelListener);
        setOnKeyListener(mOnKeyListener);

        initLayout();
    }

    private void initLayout() {
        recyclerPhoto = (RecyclerView) findViewById(R.id.recyclerPhoto);
        textTitle = (TextView) findViewById(R.id.textTitle);
        textCancel = (TextView) findViewById(R.id.textCancel);
        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupListener.onCancel();
            }
        });
    }

    private void initData() {
        ArrayList<String> listPhotoType = new ArrayList<>();
        listPhotoType.add(mCamera);
        listPhotoType.add(mGallery);
        if (isDefaultImage)
            listPhotoType.add(mDefaultImage);

        textTitle.setText(mTitle);
        textCancel.setText(mCancelButton);

        mAdapter = new PhotoAdapter(mActivity);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerPhoto.setHasFixedSize(true);
        recyclerPhoto.setLayoutManager(mLinearLayoutManager);
        recyclerPhoto.setItemAnimator(new DefaultItemAnimator());
        recyclerPhoto.setAdapter(mAdapter);
        mAdapter.setOnRecyclerViewItemClickListener(mOnRecyclerViewItemClickListener);

        mAdapter.addAllData(listPhotoType);

        // Set RecyclerView height
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerPhoto.getLayoutParams();
        layoutParams.height = (int) mActivity.getResources().getDimension(R.dimen.common_row_height) * mAdapter.getItemCount() +
                (int) mActivity.getResources().getDimension(R.dimen.common_margin);
        recyclerPhoto.setLayoutParams(layoutParams);
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setCancelButton(String mCancelButton) {
        this.mCancelButton = mCancelButton;
    }

    public void setCameraText(String mCamera) {
        this.mCamera = mCamera;
    }

    public void setGalleryText(String mGallery) {
        this.mGallery = mGallery;
    }

    public void setDefaultImageText(String mDefaultImage) {
        this.mDefaultImage = mDefaultImage;
    }

    public void show(){
        initData();
        super.show();
    }

    private OnKeyListener mOnKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                if (getLoadingState()) {
//                    return true;
//                }
//            }
            return false;
        }
    };

    private OnCancelListener mCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            mPopupListener.onCancel();
        }
    };

    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener = new OnRecyclerViewItemClickListener() {

        @Override
        public void onItemClick(View view, final int position) {
            mAdapter.setSelectedItem(position);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPopupListener.onConfirm(position);
                }
            }, 500);
        }
    };

}

