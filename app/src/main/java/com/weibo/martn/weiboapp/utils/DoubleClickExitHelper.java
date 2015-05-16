/**
 * Title: DoubleClickExitHelper
 * Package: com.weibo.martn.weiboapp.utils
 * Description: (双击退出---注意使用上与AppManager相关)
 * Date 2015/5/14 23:38
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.app.AppManager;

public class DoubleClickExitHelper {
    private final Activity mActivity;

    private boolean isOnKeyBacking;
    private Handler mHandler;
    private Toast mBackToast;

    public DoubleClickExitHelper(Activity activity) {
        mActivity = activity;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (isOnKeyBacking) {
            //在2s内再次按下
            mHandler.removeCallbacks(onBackTimeRunnable);
            if (mBackToast != null) {
                mBackToast.cancel();
            }
            //清除所有ac
            AppManager.getAppManager().AppExit(mActivity);
            return true;
        } else {
            //第一次按下退出
            isOnKeyBacking = true;
            if (mBackToast == null) {
                mBackToast = Toast.makeText(mActivity, R.string.tip_double_click_exit, Toast.LENGTH_SHORT);
            }
            mBackToast.show();
            mHandler.postDelayed(onBackTimeRunnable,2000);
            return true;
        }
    }
    private Runnable onBackTimeRunnable = new Runnable() {
        @Override
        public void run() {
            isOnKeyBacking = false;
            if (mBackToast != null) {
                mBackToast.cancel();
            }
        }
    };
}
