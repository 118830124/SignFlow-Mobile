package com.example.project;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

/**
 * SignatureAdapter 类是一个适配器，用于在列表中显示签名文件的文件名和日期，并提供删除文件的功能。
 * 这个适配器可以将签名文件的信息显示在列表中，用户可以通过点击按钮来删除特定的签名文件。
 */
public class SignatureAdapter extends ArrayAdapter<String> {

    /**
     * 构造函数，创建一个新的 SignatureAdapter 实例。
     * @param context 上下文对象
     * @param objects 数据列表
     */
    public SignatureAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    /**
     * 获取列表项的视图，并设置相应的数据和事件。
     * @param position 列表项的位置
     * @param convertView 可重用的视图
     * @param parent 父视图
     * @return 设置好数据和事件的视图
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 检查现有视图是否被重用，否则为这个视图填充新的布局
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.signature_list_item, parent, false);
        }

        // 查找布局中的TextView和Button
        TextView textViewFileName = convertView.findViewById(R.id.textViewFileName);
        TextView textViewFileDate = convertView.findViewById(R.id.textViewFileDate);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        // 获取当前项的签名信息（文件ID、文件名和时间）
        String item = getItem(position);
        if (item != null) {
            String[] parts = item.split("\n");
            if (parts.length >= 3) {
                // 文件名和日期信息在第二和第三行
                textViewFileName.setText(parts[1]);  // 设置文件名
                textViewFileDate.setText(parts[2]);  // 设置文件日期
            }
        }

        // 设置删除按钮的点击事件
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);  // 显示删除确认对话框
            }
        });

        return convertView;  // 返回设置好数据和事件的视图
    }

    /**
     * 显示删除确认对话框。
     * @param position 要删除的文件在列表中的位置
     */
    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("确认删除")
                .setMessage("是否要删除这项签名？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFile(position);  // 删除文件
                    }
                })
                .setNegativeButton("取消", null)
                .show();  // 显示对话框
    }

    /**
     * 删除文件。
     * @param position 要删除的文件在列表中的位置
     */
    private void deleteFile(int position) {
        String item = getItem(position);
        if (item != null) {
            String[] parts = item.split("\n");
            if (parts.length > 1) {
                long id = Long.parseLong(parts[0]);  // 获取文件ID
                Uri fileUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);  // 根据文件ID创建URI
                try {
                    getContext().getContentResolver().delete(fileUri, null, null);  // 删除文件
                    remove(getItem(position));  // 从列表中移除文件名
                    notifyDataSetChanged();  // 通知适配器数据已更改
                    Toast.makeText(getContext(), "文件已删除", Toast.LENGTH_SHORT).show();  // 提示用户文件已删除
                } catch (Exception e) {
                    Toast.makeText(getContext(), "文件删除失败: " + e.getMessage(), Toast.LENGTH_LONG).show();  // 提示用户删除失败
                }
            }
        }
    }
}
