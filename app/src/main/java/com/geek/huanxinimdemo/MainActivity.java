package com.geek.huanxinimdemo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geek.huanxinimdemo.base.BaseActivity;
import com.geek.huanxinimdemo.base.EMBaseActivity;
import com.geek.huanxinimdemo.ui.LoginActivity;
import com.geek.huanxinimdemo.ui.VideoCallActivity;
import com.geek.huanxinimdemo.ui.VoiceCallActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.EMServiceNotReadyException;

import java.util.List;

public class MainActivity extends EMBaseActivity implements BaseActivity.EMLogoutListener {

    EditText mMessage;

    EditText mUserId;

    Button mConversation;

    Button mVoice;

    Button mVideo;

    TextView mReceiveMessage;

    Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessage = findViewById(R.id.ed_message);
        mUserId = findViewById(R.id.ed_userid);
        mConversation = findViewById(R.id.btn_goMessage);
        mVoice = findViewById(R.id.btn_voice);
        mVideo = findViewById(R.id.btn_video);

        mReceiveMessage = findViewById(R.id.tv_receive_message);
        mLogout = findViewById(R.id.btn_logout);

        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOnEmLogoutListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setOnEmLogoutListener(null);
    }

    private void initListener() {
        mConversation.setOnClickListener(v -> {
            String message = mMessage.getText().toString();
            String userid = mUserId.getText().toString();
            if (message.isEmpty() || userid.isEmpty()) {
                Toast.makeText(MainActivity.this, "聊天用户和聊天内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }


            sendMessage(message.trim(), userid.trim());
        });

        mLogout.setOnClickListener(v -> {
            logoutAsync();
        });

        mVoice.setOnClickListener(view -> {
            checkPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
            String userName = mUserId.getText().toString();
            if (userName.isEmpty()) {
                Toast.makeText(MainActivity.this, "聊天用户不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            makeVoiceCall(userName.trim());
        });


        mVideo.setOnClickListener(view -> {
            checkPermission(MainActivity.this, Manifest.permission.CAMERA);
            String userName = mUserId.getText().toString();
            if (userName.isEmpty()) {
                Toast.makeText(MainActivity.this, "聊天用户不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            makeVideoCall(userName);
        });

    }

    public void makeVoiceCall(String userName) {
        Intent intent = new Intent(MainActivity.this, VoiceCallActivity.class);
        intent.putExtra("username", userName);
        intent.putExtra("isComingCall", false);
        startActivity(intent);
    }

    public void makeVideoCall(String userName) {
        Intent intent = new Intent(MainActivity.this, VideoCallActivity.class);
        intent.putExtra("username", userName);
        intent.putExtra("isComingCall", false);
        startActivity(intent);
    }

    /**
     * 暂时只支持文字
     */
    public void sendMessage(String message, String chatUserName) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(message, chatUserName);
        EMClient.getInstance().chatManager().sendMessage(emMessage);
    }

    @Override
    protected void onMsgReceived(List<EMMessage> messages) {
       // messages.forEach(message -> {
         //   Log.d(TAG, "onMessageReceived: " + message.getUserName() + " , " + message.getBody().toString());
        //});

        for (int i = 0; i < messages.size(); i++) {
            EMMessage emMessage = messages.get(i);
            Log.d(TAG, "onMessageReceived: " + emMessage.getUserName() + " , " + emMessage.getBody().toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mReceiveMessage.setText("");
                    mReceiveMessage.setText(emMessage.getBody().toString());
                }
            });
        }
    }

    @Override
    public void onSuccess() {
        startNewActivity(MainActivity.this, LoginActivity.class);
        finish();
    }

    @Override
    public void onError(int i, String s) {
        Toast.makeText(MainActivity.this,
                "退出登陆失败: " + s,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgress(int i, String s) {

    }
}
