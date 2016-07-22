package com.tedkim.android.tcamera.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tedkim.android.tcamera.TCamera;

/**
 * Camera broadcast receiver
 */
public class CameraReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(TCamera.SUCCESS, false))
            TCamera.getInstance(context).success(intent);
        else
            TCamera.getInstance(context).fail();
    }
}