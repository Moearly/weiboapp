package com.weibo.martn.weiboapp.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.app.AppManager;
import com.weibo.martn.weiboapp.app.ConfigManager;
import com.weibo.martn.weiboapp.fragment.FragmentFloat;
import com.weibo.martn.weiboapp.fragment.FragmentHome;
import com.weibo.martn.weiboapp.fragment.FragmentMenu;
import com.weibo.martn.weiboapp.view.UpDownRefershListView;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class MainActivity extends FragmentActivity implements UpDownRefershListView.RequestRefershImpl {

    private Fragment mContent;
    //在头部用于显示信息的加载
    private SmoothProgressBar pb;
    //数据统一处理，本地化
    private ConfigManager configManager;
    // 展示用户信息的窗口
    private Fragment infoFragment;
    // actionbar初始化
     private static ActionBar actionBar;
    //slidingmenu
    private SlidingMenu sm;

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
        ViewUtils.inject(this);
        AppManager.getAppManager().addActivity(this);

        //绑定fragment显示位置
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();

        initSlidingMenu(savedInstanceState);

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        this.pb = (SmoothProgressBar) findViewById(R.id.pb_main);
    }

    /**
     * 初始化滑动菜单
     */
    private void initSlidingMenu(Bundle savedInstanceState) {

//        创建一个slidingmenu
        sm = new SlidingMenu(this);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setMode(SlidingMenu.LEFT);
        sm.setMenu(R.layout.menu_frame);
        sm.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 设置滑动菜单视图界面
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new FragmentMenu()).commit();
    }

    /**
     * 切换Fragment，也是切换视图的内容
     */
    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
        sm.showContent();

    }

    /**
     * 显示用户信息窗口
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
            sm.toggle();
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
            sm.toggle();
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
