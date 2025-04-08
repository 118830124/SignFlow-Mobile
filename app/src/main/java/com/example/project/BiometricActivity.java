package com.example.project;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

/**
 * BiometricActivity 类用于管理生物识别身份验证流程。
 */
public class BiometricActivity extends AppCompatActivity {

    private BiometricPrompt biometricPrompt; // 生物识别提示
    private BiometricPrompt.PromptInfo promptInfo; // 生物识别提示信息

    /**
     * 在Activity创建时调用，设置界面并初始化生物识别。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
        setContentView(R.layout.activity_biometric); // 设置布局文件

        setupBiometricAuthentication(); // 设置生物识别身份验证
    }

    /**
     * 设置生物识别身份验证。
     */
    private void setupBiometricAuthentication() {
        Executor executor = ContextCompat.getMainExecutor(this); // 获取主线程执行器
        biometricPrompt = new BiometricPrompt(BiometricActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            // 生物识别认证回调函数
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                // 认证错误时调用
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(BiometricActivity.this, "取消生物识别: " + errString, Toast.LENGTH_LONG).show();
                finish(); // 结束Activity
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                // 认证成功时调用
                super.onAuthenticationSucceeded(result);
                startMainActivity(); // 启动主Activity
            }

//            @Override
//            public void onAuthenticationFailed() {
//                // 认证失败时调用
//
//                super.onAuthenticationFailed();
//                Toast.makeText(BiometricActivity.this, "关闭软件", Toast.LENGTH_SHORT).show();
//            }
        });

        // 构建生物识别提示信息对象，设置标题、副标题和允许设备凭证
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("生物识别")
                .setSubtitle("请验证您的指纹或锁屏密码")
                .setDeviceCredentialAllowed(true)
                .build();

        // 启动生物识别身份验证
        biometricPrompt.authenticate(promptInfo);
    }

    /**
     * 启动主Activity并结束当前Activity。
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 结束当前Activity，返回到主界面
    }

    /**
     * 处理物理返回键事件，确保返回时关闭当前Activity。
     */
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}
