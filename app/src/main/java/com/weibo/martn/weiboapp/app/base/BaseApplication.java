package com.weibo.martn.weiboapp.app.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.martn.weiboapp.R;

/**
 * 全局的applicant管理---基类---重复使用
 */

@SuppressLint("InflateParams")
public class BaseApplication extends Application {


	//应用上下文
	private static Context mContext;
	//应用静态资源
	private static Resources mResource;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		mResource = mContext.getResources();
	}

	/**
	 * 获取上下文
	 * @return
	 */
	public static synchronized BaseApplication context() {
		return (BaseApplication) mContext;
	}

	/**
	 * 获取资源文件的使用----因为这些变量在程序中都是经常使用的，所以静态化有比较好
	 * @return
	 */
	public static Resources resources() {
		return mResource;
	}


	//判断本地sdk的版本，决定对sp的editor的使用--是否低于9版本
	private static boolean sIsAtLeastGB;

	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			sIsAtLeastGB = true;
		}
	}


	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void apply(SharedPreferences.Editor editor) {
		if (sIsAtLeastGB) {
			editor.apply();
		} else {
			editor.commit();
		}
	}


	public static void set(String key, boolean value) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(key, value);
		apply(editor);
	}

	public static void set(String key, String value) {
		Editor editor = getPreferences().edit();
		editor.putString(key, value);
		apply(editor);
	}

	/****************settingsp的操作***********/
	public static String getStringSetting(String type) {
		if (getPreferences().contains(type))
			return get(type, "");

		return null;
	}

	/****************封装sp操作*********/
	public static boolean get(String key, boolean defValue) {
		return getPreferences().getBoolean(key, defValue);
	}

	public static String get(String key, String defValue) {
		return getPreferences().getString(key, defValue);
	}

	public static int get(String key, int defValue) {
		return getPreferences().getInt(key, defValue);
	}

	public static long get(String key, long defValue) {
		return getPreferences().getLong(key, defValue);
	}

	public static float get(String key, float defValue) {
		return getPreferences().getFloat(key, defValue);
	}

	/****************封装sp操作*********************************************************************/
	/**
	 * 获得默认的sp
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getPreferences() {
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context());
		return pre;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getPreferences(String prefName) {
		return context().getSharedPreferences(prefName,
				Context.MODE_MULTI_PROCESS);
	}


	/******************获取资源的方法***************************************************************/
	public static String string(int id) {
		return mResource.getString(id);
	}

	public static String string(int id, Object... args) {
		return mResource.getString(id, args);
	}

	/******************显示Toast相关的*************************************************************/

	public static void showToast(int res) {
		String message = mResource.getString(res);
		showToast(message, Toast.LENGTH_LONG, 0);
	}

	public static void showToast(int res, int icon) {
		String message = mResource.getString(res);
		showToast(message, Toast.LENGTH_LONG, icon);
	}

	public static void showToastShort(int res) {
		String message = mResource.getString(res);
		showToast(message, Toast.LENGTH_SHORT, 0);
	}

	public static void showToast(String message) {
		showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM);
	}
	public static void showToast(String message, int icon) {
		showToast(message, Toast.LENGTH_LONG, icon, Gravity.BOTTOM);
	}
	public static void showToastShort(String message) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
	}
	public static void showToast(String message, int duration, int icon) {
		showToast(message, duration, icon, Gravity.BOTTOM);
	}



	private static String lastToast = "";
	private static long lastToastTime;
	/**
	 * 自定义显示Toast的方法----自定义显示Toast的view
	 * @param message
	 * @param duration
	 * @param icon
	 * @param gravity
	 */
	public static void showToast(String message, int duration, int icon,
								 int gravity) {
		if (message != null && !message.equalsIgnoreCase("")) {
			long time = System.currentTimeMillis();
			if (!message.equalsIgnoreCase(lastToast)
					|| Math.abs(time - lastToastTime) > 2000) {
				View view = LayoutInflater.from(context()).inflate(
						R.layout.view_toast, null);
				((TextView) view.findViewById(R.id.title_tv)).setText(message);
				if (icon != 0) {
					//有可显示的icon
					((ImageView) view.findViewById(R.id.icon_iv))
							.setImageResource(icon);
					((ImageView) view.findViewById(R.id.icon_iv))
							.setVisibility(View.VISIBLE);
				}
				Toast toast = new Toast(context());
				toast.setView(view);
				if (gravity == Gravity.CENTER) {
					toast.setGravity(gravity, 0, 0);
				} else {
					toast.setGravity(gravity, 0, 35);
				}

				toast.setDuration(duration);
				toast.show();
				lastToast = message;
				lastToastTime = System.currentTimeMillis();
			}
		}
	}
}
