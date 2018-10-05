package com.geek.huanxinimdemo.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.geek.huanxinimdemo.utils.Checkers;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;

import java.util.List;

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
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().addConnectionListener(null);
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
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
                   // Toast.makeText(EMBaseActivity.this, "连接聊天服务器成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "连接聊天服务器成功");
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

    EMMessageListener emMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            onMsgReceived(messages);
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }
        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    protected void onMsgReceived(List<EMMessage> messages) {
        //收到消息
    }
}
