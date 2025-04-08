package com.example.project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity 类是应用的主界面，它负责管理底部导航，根据选择的导航项显示相应的Fragment。
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 当Activity被创建时调用，初始化界面和底部导航。
     * @param savedInstanceState 如果Activity被重建（例如屏幕旋转），这个参数会包含之前Activity的数据。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 设置Activity的布局

        // 获取定义在布局文件中的BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 为BottomNavigationView设置项选择监听器
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId(); // 获取被点击的菜单项的ID
            // 根据ID判断哪个菜单项被选择，并进行界面切换
            if (id == R.id.navigation_files) {
                // 如果选择了"文件"，则显示FilesFragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FilesFragment()).commit();
            } else if (id == R.id.navigation_sign) {
                // 如果选择了"签名"，则显示SignFragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SignFragment()).commit();
            }
            return true; // 返回true表示已处理选择事件
        });

        // 如果是Activity首次创建，则默认显示FilesFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FilesFragment()).commit();
            bottomNav.setSelectedItemId(R.id.navigation_files); // 设置底部导航当前选中项为"文件"
        }
    }
}
