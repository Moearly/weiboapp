package com.weibo.martn.weiboapp.utils;

import android.content.Context;
import android.widget.Toast;

import com.weibo.martn.weiboapp.view.CustomToast;


/**
 * @author guohao
 * 
 */
public class ToastUtils {

	public static void showToast(Context context, String str) {
		CustomToast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
}
