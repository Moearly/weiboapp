package com.weibo.martn.weiboapp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.utils.TDevice;

/**
 * 两种基始 dialog
 */
public class WaitDialog extends Dialog {

	private TextView _messageTv;
	
	public WaitDialog(Context context) {
		super(context);
		init(context);
	}

	public WaitDialog(Context context, int defStyle) {
		super(context, defStyle);
		init(context);
	}

	protected WaitDialog(Context context, boolean cancelable,OnCancelListener listener) {
		super(context, cancelable, listener);
		init(context);
	}

	private void init(Context context) {
		setCancelable(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_wait, null);
		_messageTv = (TextView) view.findViewById(R.id.waiting_tv);
		setContentView(view);
	}


	/**
	 * 关闭当前dialog
	 * @param dialog
	 * @return
	 */
	public static boolean dismiss(WaitDialog dialog) {
		if (dialog != null) {
			dialog.dismiss();
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (TDevice.isTablet()) {
			int i = (int) TDevice.dpToPixel(360F);
			if (i < TDevice.getScreenWidth()) {
				WindowManager.LayoutParams params = getWindow()
						.getAttributes();
				params.width = i;
				getWindow().setAttributes(params);
			}
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.dismiss();
	}

	/**
	 * 对dialog的隐藏处理，逻辑交给对应的窗口管理
	 * @param context
	 */
	public static void hide(Context context) {
		if (context instanceof DialogControl)
			((DialogControl) context).hideWaitDialog();
	}

	public static boolean hide(WaitDialog dialog) {
		if (dialog != null) {
			dialog.hide();
			return false;
		} else {
			return true;
		}
	}


	public static void show(Context context) {
		if (context instanceof DialogControl)
			((DialogControl) context).showWaitDialog();
	}

	public static boolean show(WaitDialog waitdialog) {
		boolean flag;
		if (waitdialog != null) {
			waitdialog.show();
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}



	public void setMessage(int message) {
		_messageTv.setText(message);
	}

	public void setMessage(String message) {
		_messageTv.setText(message);
	}
	
	public void hideMessage() {
	    _messageTv.setVisibility(View.GONE);
	}
}
