package com.geek.huanxinimdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geek.huanxinimdemo.base.BaseActivity;
import com.geek.huanxinimdemo.ui.LoginActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class MainActivity extends BaseActivity implements BaseActivity.EMLogoutListener {

    EditText mMessage;

    Button mConversation;

    Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessage = findViewById(R.id.ed_message);
        mConversation = findViewById(R.id.btn_goMessage);
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
        mLogout.setOnClickListener(v -> {
            logoutAsync();
        });
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
