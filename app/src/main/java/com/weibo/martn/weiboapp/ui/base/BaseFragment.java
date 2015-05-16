/**
 * Title: BaseFragment
 * Package: com.weibo.martn.weiboapp.ui.base
 * Description: (碎片的基类)
 * Date 2015/5/16 17:19
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.ui.base;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.app.AppContext;
import com.weibo.martn.weiboapp.interf.BaseFragmentInterface;
import com.weibo.martn.weiboapp.ui.dialog.DialogControl;
import com.weibo.martn.weiboapp.ui.dialog.WaitDialog;


public class BaseFragment extends Fragment implements
        android.view.View.OnClickListener, BaseFragmentInterface {
    /***一些fragment的状态--根据状态会有一些现实效果的不同  TODO:这部分状态其实该分离---***/
    public static final int STATE_NONE = 0;  //什么都没有
    public static final int STATE_REFRESH = 1;  //处于刷新
    public static final int STATE_LOADMORE = 2; //处于加载
    public static final int STATE_NOMORE = 3; //没有更多
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;

    //视图加载器
    protected LayoutInflater mInflater;

    public AppContext getApplication() {
        return (AppContext) getActivity().getApplication();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.mInflater = inflater;
        //显示view参数传入获取view
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 根据layout--id得到视图
     * @param resId
     * @return
     */
    protected View inflateView(int resId) {
        return this.mInflater.inflate(resId, null);
    }


    //根据ac中对dialog的具体实现---来处理dialog的效果
    protected void hideWaitDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            ((DialogControl) activity).hideWaitDialog();
        }
    }

    protected WaitDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    /**
     * 显示等待dialog
     * @param resid
     * @return
     */
    protected WaitDialog showWaitDialog(int resid) {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            return ((DialogControl) activity).showWaitDialog(resid);
        }
        return null;
    }

    protected WaitDialog showWaitDialog(String resid) {
        FragmentActivity activity = getActivity();
        if (activity instanceof DialogControl) {
            return ((DialogControl) activity).showWaitDialog(resid);
        }
        return null;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
