package com.geek.huanxinimdemo.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.geek.huanxinimdemo.utils.Checkers;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

/**
 * Created by huangxiaojing on 2018/10/5.
 */

public class EMBaseActivity extends BaseActivity {

    public static final String TAG = "EMBaseActivity";

    public Handler mainHandler = new Handler(Looper.getMainLooper());

    private MyConnectionListener myConnectionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myConnectionListener = new MyConnectionListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().addConnectionListener(myConnectionListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().addConnectionListener(null);
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

            return;
        }

        EMClient.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
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

    public void realRegister(String userName, String passWord) {
        if (!Checkers.loginChecker(userName, passWord)) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEMLoginListener != null) {
                        mEMLoginListener.onArgumentFail();
                    }
                }
            });

            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(userName.trim(), passWord.trim());
                    Log.d(TAG, "注册成功");
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mEMLoginListener != null) {
                                mEMLoginListener.onRegisterSuccess();
                            }
                        }
                    });
                } catch (HyphenateException e) {
                    Log.e(TAG, "注册失败 "+  e.getErrorCode() + " , " + e.getMessage());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mEMLoginListener != null) {
                                mEMLoginListener.onRegisterFail();
                            }
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EMBaseActivity.this, "连接聊天服务器成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onDisconnected(int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(error == EMError.USER_REMOVED){
                        // 显示帐号已经被移除
                        Toast.makeText(EMBaseActivity.this, "帐号错误", Toast.LENGTH_SHORT).show();
                    }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(EMBaseActivity.this, "您的帐号在其他设备上登陆", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NetUtils.hasNetwork(EMBaseActivity.this)) {
                            Toast.makeText(EMBaseActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

}
