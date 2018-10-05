package com.geek.huanxinimdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geek.huanxinimdemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private EditText mUserName;

    private EditText mPassword;

    private Button mLogin;

    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);

        mUserName = findViewById(R.id.ed_username);
        mPassword = findViewById(R.id.ed_password);
        mLogin = findViewById(R.id.btn_login);
        mRegister = findViewById(R.id.btn_register);

        login();

        register();
    }

    /**
     * 注册分为开放注册 和 授权注册（由 app 服务端来进行注册）
     */
    private void register() {
        mRegister.setOnClickListener(v -> {
            String userName = mUserName.getText().toString();
            String passWord = mPassword.getText().toString();
            if (userName.isEmpty() || passWord.isEmpty()) {
                Toast.makeText(
                        this,
                        "用户名或密码不能为空!",
                        Toast.LENGTH_SHORT).show();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().createAccount(userName.trim(), passWord.trim());
                        Log.d(TAG, "注册成功");
                    } catch (HyphenateException e) {
                        Log.e(TAG, "注册失败 "+  e.getErrorCode() + " , " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();

        });
    }

    private void login() {

    }
}
