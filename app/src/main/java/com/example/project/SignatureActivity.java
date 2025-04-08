package com.example.project;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.crypto.SecretKey;

/**
 * SignatureActivity 类是用于处理签名的活动，包括签名的创建、加密和保存。
 * 用户可以在这个界面上签名，并将签名加密后保存到设备的存储中。
 */
public class SignatureActivity extends AppCompatActivity {

    private SignatureView signatureView;  // 签名视图组件，用于用户签名
    private Button cancelButton;          // 取消按钮
    private AESUtil aesUtil;             // 加密工具类，虽然在代码中未直接使用
    private SecretKey secretKey;         // 用于加密的密钥

    /**
     * 在活动创建时调用，设置界面并初始化相关组件和事件。
     * @param savedInstanceState 保存活动状态的对象，可用于恢复之前的状态
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);  // 设置使用的布局文件

        signatureView = findViewById(R.id.signature_view);  // 获取签名视图
        Button clearButton = findViewById(R.id.clear_button);  // 获取清除按钮
        Button saveButton = findViewById(R.id.save_button);  // 获取保存按钮
        cancelButton = findViewById(R.id.cancel_button);  // 获取取消按钮

        try {
            aesUtil = new AESUtil();  // 实例化AES工具类
            secretKey = AESUtil.generateKey();  // 生成加密用的密钥
        } catch (Exception e) {
            Toast.makeText(this, "密钥生成错误: " + e.getMessage(), Toast.LENGTH_LONG).show();  // 显示密钥生成错误信息
            finish();  // 密钥生成失败时结束活动
        }

        clearButton.setOnClickListener(v -> signatureView.clear());  // 设置清除按钮的点击事件，清除签名视图中的签名

        saveButton.setOnClickListener(v -> {
            if (signatureView.isEmpty()) {  // 检查签名视图是否为空
                Toast.makeText(SignatureActivity.this, "您尚未签名", Toast.LENGTH_SHORT).show();  // 提示用户尚未签名
            } else {
                Bitmap signatureBitmap = signatureView.getSignatureBitmap();  // 获取签名的位图
                saveSignature(signatureBitmap);  // 保存签名
                finish();  // 完成签名后结束活动
            }
        });

        cancelButton.setOnClickListener(v -> finish());  // 设置取消按钮的点击事件，点击后结束活动
    }

    /**
     * 保存密钥到SharedPreferences。
     * @param fileName 文件名，作为键使用
     * @param secretKey 密钥，将被保存
     */
    private void saveKey(String fileName, SecretKey secretKey) {
        SharedPreferences preferences = getSharedPreferences("加密签名", MODE_PRIVATE);  // 获取SharedPreferences实例
        SharedPreferences.Editor editor = preferences.edit();  // 获取编辑器
        editor.putString(fileName, Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT));  // 将密钥编码后保存
        editor.apply();  // 提交修改
    }

    /**
     * 将签名保存到设备存储。
     * @param signature 签名的位图
     */
    private void saveSignature(Bitmap signature) {
        try {
            SecretKey secretKey = AESUtil.generateKey();  // 再次生成一个新的密钥
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  // 创建字节输出流
            signature.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);  // 将签名位图压缩为PNG格式
            byte[] signatureBytes = byteArrayOutputStream.toByteArray();  // 将压缩后的数据转换为字节数组
            byte[] encryptedBytes = AESUtil.encrypt(signatureBytes, secretKey);  // 使用密钥对签名数据进行加密

            String fileName = "加密签名_" + System.currentTimeMillis() + ".png";  // 生成文件名
            saveKey(fileName, secretKey);  // 保存密钥

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);  // 设置文件名
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");  // 设置MIME类型
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EncryptedSignatures");  // 设置保存目录
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // 插入内容提供者，创建文件

            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                outputStream.write(encryptedBytes);  // 写入加密数据到文件
                Toast.makeText(this, "加密签名已保存", Toast.LENGTH_LONG).show();  // 提示用户加密签名已保存
            } catch (Exception e) {
                Toast.makeText(this, "保存加密签名失败: " + e.getMessage(), Toast.LENGTH_LONG).show();  // 保存失败时提示用户
                getContentResolver().delete(uri, null, null);  // 删除创建的文件
            }
        } catch (Exception e) {
            Toast.makeText(this, "加密失败: " + e.getMessage(), Toast.LENGTH_LONG).show();  // 加密失败时提示用户
        }
    }
}