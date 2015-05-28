/**
 * Title: SplashActivity
 * Package: com.weibo.martn.weiboapp.ui.base
 * Description: (引导界面，对用户登陆状态的判断---微博授权判断)
 * TODO：1,加入app启动动画（修改） 2.加入更新提醒功能
 * Date 2015/5/15 10:00
 *
 * @author MartnLei MartnLei_163_com
 * @version V2.0
 */
package com.weibo.martn.weiboapp.ui.base;

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
import com.weibo.martn.weiboapp.app.AppContext;
import com.weibo.martn.weiboapp.app.base.BaseApplication;
import com.weibo.martn.weiboapp.ui.user.fragment.AccountFragment;
import com.weibo.martn.weiboapp.sdk.AccessTokenKeeper;
import com.weibo.martn.weiboapp.sdk.Constants;
import com.weibo.martn.weiboapp.ui.user.LoginActivity;

public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:去掉这个界面----之后修改动画时需要修改
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

        // 初始化imageLoader---
        initImageLoader(getApplicationContext());
    }

    /**
     * 标识应用版权信息
     */
    private void initFontType() {
        TextView splash = (TextView) findViewById(R.id.tv_splash_text);
        splash.setText("CopyRight@MartnLei 2015");
    }

    /**
     * 获取登录状态，处理用户登录信息
     */
    private void getLoginState() {

        if (AppContext.isLogedin()) {
            ToMain();
        } else {
            toAccount();
        }
    }

    /**
     * 选择授权
     */
    private void toAccount() {
        AccountFragment.launch(this);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finish();
    }

    /**
     * 已经有了授权---直接登陆主界面
     */
    private void ToMain() {
        Intent intent = new Intent(BaseApplication.context(), MainActivity.class);
        BaseApplication.context().startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finish();
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
