package com.geek.huanxinimdemo.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.geek.huanxinimdemo.utils.Checkers;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by huangxiaojing on 2018/10/5.
 */

public class EMBaseActivity extends BaseActivity {

    private EMLoginListener mEMLoginListener;

    public Handler mainHandler = new Handler(Looper.getMainLooper());

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

    public void realLogin(String userName, String password) {
        if (!Checkers.loginChecker(userName, password)) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEMLoginListener != null) {
                        mEMLoginListener.onArgumentFail();
                    }
                }
            });
        }

        EMClient.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEMLoginListener != null) {
                            mEMLoginListener.onSuccess();
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEMLoginListener != null) {
                            mEMLoginListener.onError(i, s);
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEMLoginListener != null) {
                            mEMLoginListener.onError(i, s);
                        }
                    }
                });
            }
        });
    }

    public void register() {

    }

    public interface EMLoginListener {

        void onArgumentFail();

        void onSuccess();

        void onError(int i, String s);

        void onProgress(int i, String s);
    }

    public void setOnEMLoginListener(EMLoginListener l) {
        this.mEMLoginListener = l;
    }
}
