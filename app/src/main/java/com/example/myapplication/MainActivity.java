package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Map<Integer, Fragment> fragmentMap; // 用于缓存Fragment实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化Fragment缓存Map
        fragmentMap = new HashMap<>();

        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化侧边栏
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 添加DrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 设置默认Fragment
        if (savedInstanceState == null) {
            Fragment defaultFragment = new MainFrontPageFragment();
            fragmentMap.put(R.id.nav_front_page, defaultFragment);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, defaultFragment)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_front_page);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        // 检查是否已经有缓存的Fragment
        if (fragmentMap.containsKey(itemId)) {
            selectedFragment = fragmentMap.get(itemId);
        } else {
            // 如果没有缓存的Fragment，则创建新的
            if (itemId == R.id.nav_front_page) {
                selectedFragment = new MainFrontPageFragment();
            } else if (itemId == R.id.nav_account) {
                selectedFragment = new MainUserPageFragment();
            } else if (itemId == R.id.nav_history) {
                // 启动过滤历史活动，重用已存在的实例
                Intent intent = new Intent(this, FilterHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (itemId == R.id.nav_keywords) {
                selectedFragment = new MainAdjustPageFragment();
            } else if (itemId == R.id.nav_logout) {
                selectedFragment = new MainUserPageFragment(); // 之后补充登出页面
            }

            // 将新创建的Fragment添加到缓存中
            if (selectedFragment != null) {
                fragmentMap.put(itemId, selectedFragment);
            }
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}