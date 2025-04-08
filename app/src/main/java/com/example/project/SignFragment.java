package com.example.project;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

// SignFragment类用于展示和管理签名文件，提供创建新签名、查看、删除签名等功能。
public class SignFragment extends Fragment {
    // 定义用于存储签名信息的ArrayList
    private ArrayList<String> signatureInfo = new ArrayList<>();
    // 定义ListView，用于在界面上显示签名列表
    private ListView listView;
    // 定义删除按钮
    private Button deleteButton;
    // 定义取消按钮
    private Button cancelButton;

    // 当Fragment的视图被创建时调用
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 通过布局填充器加载fragment_sign布局
        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        // 从布局中找到ListView控件
        listView = view.findViewById(R.id.signaturesListView);
        // 从布局中找到删除按钮
        deleteButton = view.findViewById(R.id.delete_button);
        // 从布局中找到取消按钮
        cancelButton = view.findViewById(R.id.cancel_button);
        // 从布局中找到新建签名按钮，并设置其点击事件，点击时启动SignatureActivity
        Button newSignatureButton = view.findViewById(R.id.newSignatureButton);
        newSignatureButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignatureActivity.class)));

        // 设置删除按钮的点击事件，用于确认删除操作
        deleteButton.setOnClickListener(v -> confirmDelete());
        // 设置取消按钮的点击事件，用于取消当前选择的签名
        cancelButton.setOnClickListener(v -> clearSelections());

        // 调用方法加载并显示签名信息
        loadAndDisplaySignatures();
        return view;
    }

    // 当Fragment恢复时调用，重新加载签名信息
    @Override
    public void onResume() {
        super.onResume();
        loadAndDisplaySignatures();
    }

    // 显示删除确认对话框
    private void confirmDelete() {
        new AlertDialog.Builder(getContext())
                .setTitle("删除签名")
                .setMessage("确定要删除选中的签名吗？")
                .setPositiveButton("是", (dialog, which) -> deleteSelectedSignatures()) // 用户确认后删除签名
                .setNegativeButton("否", null) // 用户取消后不做任何操作
                .show();
    }

    // 执行删除选中的签名操作
    private void deleteSelectedSignatures() {
        // TODO: 实现删除选中签名的具体逻辑
        Toast.makeText(getContext(), "签名已删除", Toast.LENGTH_SHORT).show();
        // 清除ListView的选择状态
        clearSelections();
    }

    // 加载并显示签名信息，从设备存储查询签名文件信息
    private void loadAndDisplaySignatures() {
        // 清空旧数据
        signatureInfo.clear();
        // 如果上下文为空，不进行操作
        if (getContext() == null) return;

        // 设置查询条件，查找特定路径下的图片
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };
        String selection = MediaStore.Images.Media.RELATIVE_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{"%Encryptedsignatures%"};

        // 执行查询操作
        try (Cursor cursor = getContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null)) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            // 遍历查询结果
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String displayName = cursor.getString(displayNameColumn);
                long dateAdded = cursor.getLong(dateAddedColumn);
                // 格式化显示信息并加入列表
                String displayInfo = id + "\n" + displayName + "\nSaved on: " + convertTimestampToDate(dateAdded);
                signatureInfo.add(displayInfo);
            }
        } catch (Exception e) {
            // 查询过程出错，打印堆栈信息
            e.printStackTrace();
        }

        // 创建SignatureAdapter适配器，并设置给ListView
        SignatureAdapter adapter = new SignatureAdapter(getContext(), signatureInfo);
        listView.setAdapter(adapter);
    }

    // 将时间戳转换为日期字符串
    private String convertTimestampToDate(long timestamp) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // 格式化时间戳为日期字符串并返回
        return formatter.format(new java.util.Date(timestamp * 1000L));
    }

    // 清除ListView的选择状态
    private void clearSelections() {
        // 隐藏删除按钮和取消按钮
        deleteButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        // 如果使用适配器管理签名列表项的选择状态，则需要在适配器中清除选择标记
    }
}
