package com.geek.huanxinimdemo.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by huangxiaojing on 2018/10/5.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";

    public static final int PERMISSIONS_REQUEST_CODE = 1;

    private boolean doubleBackToExitPressedOnce = false;

    protected EMBaseActivity.EMLoginListener mEMLoginListener;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private EMLogoutListener mEMLogoutListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void checkPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: 获取权限成功" );

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "onRequestPermissionsResult: 获取权限拒绝");
                }
                return;
            }
        }
    }

    public void startNewActivity(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }

    public void logoutSync() {
        if (EMClient.getInstance().isLoggedInBefore()) {
            EMClient.getInstance().logout(true);
        }
    }

    public void logoutAsync() {
        if (!EMClient.getInstance().isLoggedInBefore()) {
            return;
        }
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEMLogoutListener != null) {
                            mEMLogoutListener.onSuccess();
                        }
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEMLogoutListener != null) {
                            mEMLogoutListener.onProgress(progress, status);
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEMLogoutListener != null) {
                            mEMLogoutListener.onError(code, message);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        long last = SystemClock.currentThreadTimeMillis();

        if (doubleBackToExitPressedOnce) {
            logoutSync();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        mainHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    public interface EMLoginListener {

        void onArgumentFail();

        void onRegisterSuccess();

        void onRegisterFail();

        void onSuccess();

        void onError(int i, String s);

        void onProgress(int i, String s);
    }

    public interface EMLogoutListener {
        void onSuccess();

        void onError(int i, String s);

        void onProgress(int i, String s);
    }

    public void setOnEMLoginListener(EMBaseActivity.EMLoginListener l) {
        this.mEMLoginListener = l;
    }

    public void setOnEmLogoutListener(EMLogoutListener l) {
        this.mEMLogoutListener = l;
    }
}
