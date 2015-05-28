package com.weibo.martn.weiboapp.ui.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.app.AppContext;
import com.weibo.martn.weiboapp.app.ConfigManager;
import com.weibo.martn.weiboapp.cache.DataClearManager;
import com.weibo.martn.weiboapp.bean.sina.AccountBean;
import com.weibo.martn.weiboapp.bean.sina.TokenInfo;

import com.weibo.martn.weiboapp.fragment.NavigationDrawerFragment;
import com.weibo.martn.weiboapp.interf.BaseViewInterface;
import com.weibo.martn.weiboapp.sdk.SinaSDK;
import com.weibo.martn.weiboapp.base.BaseActivity;
import com.weibo.martn.weiboapp.utils.DoubleClickExitHelper;
import com.weibo.martn.weiboapp.view.UpDownRefershListView;
import com.weibo.martn.weiboapp.widget.BadgeView;


import org.kymjs.kjframe.http.KJAsyncTask;

import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MainActivity extends BaseActivity implements UpDownRefershListView.RequestRefershImpl
    ,BaseViewInterface,View.OnTouchListener,View.OnClickListener,TabHost.OnTabChangeListener {

    public static final String FRAGMENT_TAG = "MainFragment";

    private Fragment mContent;
    //在头部用于显示信息的加载
    private SmoothProgressBar pb;
    //数据统一处理，本地化
    private ConfigManager configManager;
    // 展示用户信息的窗口
    private Fragment infoFragment;

    //显示侧边Menu的fragment
    private NavigationDrawerFragment menuFragment;

    @InjectView(R.id.drawer_main_layout)
    private DrawerLayout mDrawerLayout;

    //浮动按钮
    @InjectView(R.id.fab)
    private FloatingActionButton btnFab;
    private int fabType = -1;

    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    //双击退出
    private DoubleClickExitHelper mDoubleClickExit;

    //侧边fragment区域
    private NavigationDrawerFragment mNavigationDrawerFragment;
    //显示提示消息的数量
    private BadgeView mBvNotice;
    public static final String ACTION_LOGIN = "org.martn.sina.weibo.ACTION_LOGIN";

    /**
     * 主要用于在fg中启动ac
     */
    public static void login() {
        Intent intent = new Intent(AppContext.context(), MainActivity.class);
        //设置启动的-----
        intent.setAction(ACTION_LOGIN);
        //设置启动方式----清理栈顶和新建任务栈
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        AppContext.context().startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        configManager = new ConfigManager(getApplicationContext());
        setLayout(savedInstanceState);
        initView();
    }

    /**
     * 根据系统设置的主题选择layout
     * @param savedInstanceState
     */
    private void setLayout(Bundle savedInstanceState) {
        //获取homefragment
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        if (mContent == null)
//            mContent = new FragmentHome();

        // 设置主视图界面
        if (configManager.getThemeMod() == 1) {
            //主题的绑定还需要修改
            this.setTheme(android.R.style.Theme_Holo);
            setContentView(R.layout.activity_main_night);
        } else
            //目前只会到这里
            setContentView(R.layout.activity_main);


//        if (Build.VERSION.SDK_INT >= 19) {
//            ViewGroup drawerRoot = (ViewGroup) findViewById(R.id.fl_draw_root);
//            drawerRoot.setPadding(drawerRoot.getPaddingLeft(),
//                    SystemBarUtils.getStatusBarHeight(this),
//                    drawerRoot.getPaddingRight(),
//                    drawerRoot.getBottom());
//        }


        //绑定fragment显示位置
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();

        mToolbar = getToolbar();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolbar(),
                R.string.draw_open, R.string.draw_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                if (isToolbarShown())
                    btnFab.show(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                btnFab.hide(true);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        this.pb = (SmoothProgressBar) findViewById(R.id.pb_main);
    }


    //activity显示在acbar的title
    private CharSequence mTitle;
    @Override
    public void initView() {
        mDoubleClickExit = new DoubleClickExitHelper(this);
        //获得左边抽屉---导航侧滑栏
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
//        //设置侧滑导航栏
//        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));
//
//        //设置底部导航---tab
//        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
//        if (android.os.Build.VERSION.SDK_INT > 10) {
//            mTabHost.getTabWidget().setShowDividers(0);
//        }

//        initTabs();
//
//        // 中间按键图片触发
//        mAddBt.setOnClickListener(this);
//
//        mTabHost.setCurrentTab(0);
//        mTabHost.setOnTabChangedListener(this);

        //第一次启动有特殊操作-----打开drawerMenu---标记first的处理
        if (AppContext.isFristStart()) {
//            mNavigationDrawerFragment.openDrawerMenu();
//            DataClearManager.cleanInternalCache(AppContext.getInstance());
            AppContext.setFristStart(false);
        }
    }

//    /**
//     * 初始化底部tabHost显示
//     */
//    private void initTabs() {
//        MainTab[] tabs = MainTab.values();
//        final int size = tabs.length;
//        for (int i = 0; i < size; i++) {
//            MainTab mainTab = tabs[i];
//            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
//            View indicator = LayoutInflater.from(getApplicationContext())
//                    .inflate(R.layout.tab_indicator, null);
//            //每一个tab-item项
//            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
//            Drawable drawable = this.getResources().getDrawable(
//                    mainTab.getResIcon());
//            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
//                    null);
//            //中间位置特别设置
//            if (i == 2) {
//                indicator.setVisibility(View.INVISIBLE);
//                //记录这个特殊位置tab
//                mTabHost.setNoTabChangedTag(getString(mainTab.getResName()));
//            }
//            title.setText(getString(mainTab.getResName()));
//
//            tab.setIndicator(indicator);
//            tab.setContent(new TabHost.TabContentFactory() {
//
//                @Override
//                public View createTabContent(String tag) {
//                    return new View(MainActivity.this);
//                }
//            });
//            //添加tab
//            mTabHost.addTab(tab, mainTab.getClz(), null);
//
//            //对个人中心的特殊处理----通知小点--red
//            if (mainTab.equals(MainTab.ME)) {
//                View cn = indicator.findViewById(R.id.tab_mes);
//                //针对目标view来创建
//                mBvNotice = new BadgeView(MainActivity.this, cn);
//                //显示位置
//                mBvNotice.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//                mBvNotice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//                mBvNotice.setBackgroundResource(R.drawable.notification_bg);
//                mBvNotice.setGravity(Gravity.CENTER);
//            }
//            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
//        }
//    }


    @Override
    public void initData() {

    }



//    /**
//     * 初始化滑动菜单
//     */
//    private void initSlidingMenu(Bundle savedInstanceState) {
//
////        创建一个slidingmenu
//        sm = new SlidingMenu(this);
//        sm.setShadowWidthRes(R.dimen.shadow_width);
//        sm.setShadowDrawable(R.drawable.shadow);
//        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        sm.setFadeDegree(0.35f);
//        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        sm.setMode(SlidingMenu.LEFT);
//        sm.setMenu(R.layout.menu_frame);
//        sm.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
//        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        //getToolbar();
//        // 设置滑动菜单视图界面
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.menu_frame, new FragmentMenu()).commit();
//    }

    /**
     * 切换Fragment，也是切换视图的内容
     */
    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
//        sm.showContent();

    }

//    /**
//     * 显示用户信息窗口
//     */
//    public void showUserFlost() {
//        if (infoFragment == null) {
//            infoFragment = new FragmentFloat();
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(R.anim.view_push_down_in,
//                            R.anim.view_push_down_out)
//                    .replace(R.id.content_frame_float, infoFragment).commit();
//        } else {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(R.anim.view_push_down_in,
//                            R.anim.view_push_down_out).show(infoFragment)
//                    .commit();
//        }
//    }

    public void removeUserFloat() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.view_push_down_out,
                        R.anim.view_push_down_out).hide(infoFragment)
                .commit();
    }


    /**
     * 主要处理单例-----保证应用栈中一个mainac的实例
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //handleIntent(intent); TODO:对其他传入intent的处理
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //对默认广播的处理
        //NoticeUtils.unbindFromService(this);
        //unregisterReceiver(mReceiver);
        //mReceiver = null;
        //NoticeUtils.tryToShutDown(this);
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
//            sm.toggle();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int configTheme() {
        return 0;
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
//            sm.toggle();
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


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTabChanged(String tabId) {

    }

    /******静态方法区*************/
    public static Fragment getContentFragment(MainActivity activity) {
//        return activity.getFragmentManager().findFragmentByTag("MainFragment");
        return null;
    }


    class GetTokenInfoTask extends KJAsyncTask<Void, Void, TokenInfo> {

        AccountBean account;

        public GetTokenInfoTask(AccountBean account) {
            this.account = account;
        }
        @Override
        protected TokenInfo doInBackground(Void... voids) {
            TokenInfo tokenInfo = null;
//            tokenInfo = SinaSDK.getInstance(account.getToken(), null).getTokenInfo(account.get_token());
            if (tokenInfo != null) {
                return tokenInfo;
            }
            return null;
        }

        @Override
        protected void onPostExecute(TokenInfo tokenInfo) {
            super.onPostExecute(tokenInfo);
        }
    }


    private boolean isToolbarShown() {
        return mToolbar != null && mToolbar.getTranslationY() >= 0;
    }

//    public void hideToolbar() {
//        if (isToolbarShown()) {
//            toggleToolbarShown(false);
//        }
//    }
//
//    public void showToolbar() {
//        if (!isToolbarShown()) {
//            toggleToolbarShown(true);
//        }
//    }




//    /**
//     * 获取AuthInfo的操作
//     */
//    class GetTokenInfoTask extends AsyncTask<Void, Void, TokenInfo> {
//
//        AccountBean account;
//
//        public GetTokenInfoTask(AccountBean account) {
//            this.account = account;
//        }
//        @Override
//        public TokenInfo workInBackground(Void... params) throws TaskException {
//            //完成需要在后台对参数的处理
//            TokenInfo info = null;
//            try{
//            info = SinaSDK.getInstance(account.getToken()).getTokenInfo(account.get_token());
//            } catch (TaskException e) {
//                e.printStackTrace();
//                if ("21327".equals(e.getCode()) ||
//                        "21317".equals(e.getCode())) {
//                    info = new TokenInfo();
//                    info.setCreate_at("0");
//                    info.setExpire_in("0");
//                }
//            }
//            return null;
//        }
//    }
}
