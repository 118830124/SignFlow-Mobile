package com.example.project;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * FilesFragment 类管理文件视图和签名的加解密展示。
 */
public class FilesFragment extends Fragment {
    private static final int REQUEST_OPEN_DOCUMENT = 1; // 文档打开请求码
    private ImageView imageViewSignature; // 显示签名的ImageView
    private Button buttonSaveSignature, buttonCancelDisplay; // 保存和取消显示签名的按钮

    /**
     * 当视图创建时调用，初始化界面和事件监听器。
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        Button buttonOpenFile = view.findViewById(R.id.button_open_file);
        imageViewSignature = view.findViewById(R.id.imageViewSignature);
        buttonSaveSignature = view.findViewById(R.id.button_save_signature);
        buttonCancelDisplay = view.findViewById(R.id.button_cancel_display);
        ImageButton helpButton = view.findViewById(R.id.button_help);

        buttonOpenFile.setOnClickListener(v -> openFile());
        buttonSaveSignature.setOnClickListener(v -> saveSignatureToGallery());
        buttonCancelDisplay.setOnClickListener(v -> hideImageAndButtons());
        helpButton.setOnClickListener(v -> showHelpDialog());

        return view;
    }

    /**
     * 显示帮助对话框。
     */
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("帮助中心")
                .setMessage("\n用户可在签名中心新建签名，然后在解密中心解密刚刚创建的签名并保存，在使用完毕后将保存的未加密签名删除\n\n请勿修改签名文件的文件名")
                .setPositiveButton("关闭", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * 启动系统文件选择器。
     */
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
    }

    /**
     * 处理文件选择结果，解密并展示图像。
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            decryptAndDisplayImage(uri);
        }
    }

    /**
     * 根据文件URI解密并展示签名图像。
     */
    private void decryptAndDisplayImage(Uri uri)
    {
        try {
            String fileName = getFileName(uri); // 从URI获取文件名
            SecretKey secretKey = getKey(fileName); // 根据文件名获取相应的密钥
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri); // 获取文件的输入流
            byte[] fileData = new byte[inputStream.available()]; // 创建缓冲区以接收文件数据
            inputStream.read(fileData); // 读取文件数据到缓冲区
            byte[] decryptedData = AESUtil.decrypt(fileData, secretKey); // 使用密钥解密文件数据
            Bitmap bitmap = BitmapFactory.decodeByteArray(decryptedData, 0, decryptedData.length); // 将解密后的数据转换为Bitmap
            imageViewSignature.setImageBitmap(bitmap); // 在ImageView中显示Bitmap
            imageViewSignature.setVisibility(View.VISIBLE); // 设置ImageView为可见
            buttonSaveSignature.setVisibility(View.VISIBLE); // 显示保存按钮
            buttonCancelDisplay.setVisibility(View.VISIBLE); // 显示取消显示按钮
        } catch (Exception e) {
            Toast.makeText(getContext(), "解密失败: " + e.getMessage(), Toast.LENGTH_LONG).show(); // 解密失败时显示错误信息
        }
    }

    /**
     * 根据URI获取文件的名称。
     * @param uri 文件的URI
     * @return 文件名
     */
    private String getFileName(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null); // 查询文件URI获取文件信息
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME); // 获取文件名所在的列索引
        cursor.moveToFirst(); // 移动到查询结果的第一条记录
        String name = cursor.getString(nameIndex); // 获取文件名
        cursor.close(); // 关闭cursor
        return name;
    }

    /**
     * 根据文件名获取对应的解密密钥。
     * @param fileName 文件名
     * @return 解密所用的密钥
     */
    private SecretKey getKey(String fileName) {
        SharedPreferences preferences = getActivity().getSharedPreferences("加密签名", Context.MODE_PRIVATE); // 获取SharedPreferences实例
        String keyString = preferences.getString(fileName, null); // 根据文件名获取密钥字符串
        if (keyString == null) {
            throw new IllegalArgumentException("No key found for file: " + fileName); // 如果密钥不存在，抛出异常
        }
        byte[] keyBytes = Base64.decode(keyString, Base64.DEFAULT); // 将密钥字符串解码为字节数组
        return new SecretKeySpec(keyBytes, "AES"); // 创建密钥规范并返回
    }

    /**
     * 将签名保存到相册。
     */
    private void saveSignatureToGallery() {
        imageViewSignature.setDrawingCacheEnabled(true); // 启用视图的绘图缓存
        Bitmap bitmap = imageViewSignature.getDrawingCache(); // 获取视图的缓存位图
        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "签名_" + System.currentTimeMillis(), "未加密签名"); // 将位图保存到相册
        Toast.makeText(getContext(), "签名已保存，请在使用后删除", Toast.LENGTH_LONG).show(); // 提示用户签名已保存
        hideImageAndButtons(); // 隐藏ImageView和按钮
    }

    /**
     * 隐藏图片和按钮。
     */
    private void hideImageAndButtons() {
        imageViewSignature.setVisibility(View.GONE); // 隐藏图片显示
        buttonSaveSignature.setVisibility(View.GONE); // 隐藏保存按钮
        buttonCancelDisplay.setVisibility(View.GONE); // 隐藏取消显示按钮
    }
}
