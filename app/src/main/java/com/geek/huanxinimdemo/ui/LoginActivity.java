package com.geek.huanxinimdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geek.huanxinimdemo.MainActivity;
import com.geek.huanxinimdemo.R;
import com.geek.huanxinimdemo.base.BaseActivity;
import com.geek.huanxinimdemo.base.EMBaseActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends EMBaseActivity implements EMBaseActivity.EMLoginListener {

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

    @Override
    protected void onResume() {
        super.onResume();
        setOnEMLoginListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setOnEMLoginListener(null);
    }

    /**
     * 注册分为开放注册 和 授权注册（由 app 服务端来进行注册）
     */
    public void register() {
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
        mLogin.setOnClickListener(v -> {
            realLogin(mUserName.getText().toString().trim(),
                    mPassword.getText().toString().trim());
        });
    }

    @Override
    public void onArgumentFail() {
        Toast.makeText(
                this,
                "用户名或密码不能为空!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
        startNewActivity(LoginActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onError(int i, String s) {
        Toast.makeText(LoginActivity.this, "登陆失败：" + s,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProgress(int i, String s) {
        Toast.makeText(LoginActivity.this, "登陆中: " + s, Toast.LENGTH_SHORT).show();

    }
}
