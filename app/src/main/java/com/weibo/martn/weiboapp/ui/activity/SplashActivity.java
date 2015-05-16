package com.weibo.martn.weiboapp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.sdk.AccessTokenKeeper;
import com.weibo.martn.weiboapp.sdk.Constants;

/**
 * Created by Administrator on 2015/5/5.
 */
public class SplashActivity extends Activity{

    /** 微博 Web 授权类，提供登陆等功能 */
    private AuthInfo mWeiboAuth;

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ac_splash);
        initFontType();
        //1s的倒计时定时器
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long arg0) {
            }

            @Override
            public void onFinish() {
                getLoginState();
            }
        }.start();

        // 初始化imageLoader
        initImageLoader(getApplicationContext());
    }

    private void initFontType() {
        TextView splash = (TextView) findViewById(R.id.tv_splash_text);
        splash.setText("CopyRight@MartnLei 2015");
    }

    /**
     * 获取登录状态，处理用户登录信息
     */
    private void getLoginState() {

        Intent intent;
        // 创建微博实例

        mWeiboAuth = new AuthInfo(this, Constants.SINA_APP_KEY,
                Constants.SINA_REDIRECT_URL, Constants.SINA_SCOPE);

        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        //只是实现淡入淡出的效果
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        SplashActivity.this.finish();
    }

    /**
     * 初始化图片加载器
     * @param context
     */
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
