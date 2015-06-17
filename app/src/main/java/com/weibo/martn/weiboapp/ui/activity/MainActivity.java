package com.weibo.martn.weiboapp.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.app.AppManager;
import com.weibo.martn.weiboapp.app.ConfigManager;
import com.weibo.martn.weiboapp.fragment.FragmentFloat;
import com.weibo.martn.weiboapp.fragment.FragmentHome;
import com.weibo.martn.weiboapp.fragment.FragmentMenu;
import com.weibo.martn.weiboapp.view.UpDownRefershListView;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class MainActivity extends FragmentActivity implements UpDownRefershListView.RequestRefershImpl {

    private Fragment mContent;
    //在头部用于显示信息的加载
    //数据统一处理，本地化
    private ConfigManager configManager;
    // 展示用户信息的窗口
    private Fragment infoFragment;
    // actionbar初始化
     private static ActionBar actionBar;

    @InjectView(R.id.drawer_main_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.pb_main)
    SmoothProgressBar pb;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        configManager = new ConfigManager(getApplicationContext());
        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {
        //获取homefragment
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new FragmentHome();

        // 设置主视图界面
        if (configManager.getThemeMod() == 1) {
            //主题的绑定还需要修改
            this.setTheme(android.R.style.Theme_Holo);
            setContentView(R.layout.activity_main_night);
        } else
            setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);

        //绑定fragment显示位置
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();


        // 如果设备有实体MENU按键，overflow菜单不会再显示
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this);
        if (viewConfiguration.hasPermanentMenuKey()) {
            try {
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(viewConfiguration, false);
            } catch (Exception e) {
            }
        }

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }



    /**
     * 切换Fragment，也是切换视图的内容
     */
    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
        mDrawerLayout.closeDrawers();

    }

    /**
     * 显示用户信息窗口---广告
     */
    public void showUserFlost() {
        if (infoFragment == null) {
            infoFragment = new FragmentFloat();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.view_push_down_in,
                            R.anim.view_push_down_out)
                    .replace(R.id.content_frame_float, infoFragment).commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.view_push_down_in,
                            R.anim.view_push_down_out).show(infoFragment)
                    .commit();
        }
    }

    public void removeUserFloat() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.view_push_down_out,
                        R.anim.view_push_down_out).hide(infoFragment)
                .commit();
    }

    /**
     * 保存Fragment的状态
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START))
                mDrawerLayout.closeDrawers();
            else
                mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int id = item.getItemId();
        // 跳转
        if (id == R.id.menu_post) {
            final Intent intent = new Intent();
            intent.setClass(MainActivity.this, PostActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.flip_horizontal_in,
                    R.anim.flip_horizontal_out);
            return true;
        }
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START))
                mDrawerLayout.closeDrawers();
            else
                mDrawerLayout.openDrawer(GravityCompat.START);
//            if (configManager.getThemeMod() == 0)
//                configManager.setThemeMod(1);
//            else {
//                configManager.setThemeMod(0);
//            }
//            // 切换主题
//            final Intent intent = new Intent();
//            intent.setClass(MainActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestRefersh() {
        // TODO Auto-generated method stub
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefershComplete() {
        // TODO Auto-generated method stub
        pb.setVisibility(View.GONE);
    }
}
