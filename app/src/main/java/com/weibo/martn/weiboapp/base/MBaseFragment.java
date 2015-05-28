/**
 * Title: MBaseFragment
 * Package: com.weibo.martn.weiboapp.fragment.base
 * Description: (自定义fragment的基本类---1.任务管理)
 * Date 2015/5/15 10:00
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.utils.ViewUtils;


import java.io.Serializable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class MBaseFragment extends Fragment {
    static final String TAG = MBaseFragment.class.getSimpleName();

    //执行任务状态
    protected enum MBaseViewState {
        none, prepare, falid, success, finished, canceled
    }
    
    
    //控制下拉刷新状态
    protected static final int STATE_NONE = 0; //什么都没有
    protected static final int STATE_REFRESH = 1;
    protected static final int STATE_LOADMORE = 2;
    protected int mState = STATE_NONE;

    //构建是图的不同加载状态
    @InjectView(R.layout.comm_lay_loading)
    View loadingLayout;// 加载中视图
    @InjectView(R.layout.comm_lay_loadfailed)
    View loadFailureLayout;// 加载失败视图
    @InjectView(R.layout.comm_ui_fragment_container)
    View contentLayout;// 内容视图
    @InjectView(R.layout.comm_lay_emptyview)
    View emptyLayout;// 空视图

    //一般的基类抽象都会有一个根视图
    private ViewGroup rootView;// 根视图
   // private TaskManager taskManager;// 管理线程

    // 标志是否ContentView是否为空
    private boolean contentEmpty = true;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof BaseActivity)
            ((BaseActivity) activity).addFragment(toString(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (returnresID() > 0) {
            rootView = (ViewGroup) inflater.inflate(returnresID(), null);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            initLoadingView(inflater, savedInstanceState);

            initView(inflater, savedInstanceState);

            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null)
            requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
     * A*Fragment重写这个方法
     *
     * @param inflater
     * @param savedInstanceSate
     */
    private void initLoadingView(LayoutInflater inflater, Bundle savedInstanceSate) {
        ButterKnife.inject(rootView);
        if (emptyLayout != null) {
            View reloadView = emptyLayout.findViewById(R.id.layoutReload);
            if (reloadView != null)
                setViewOnClick(reloadView);
        }

        if (loadFailureLayout != null) {
            View reloadView = loadFailureLayout.findViewById(R.id.layoutReload);
            if (reloadView != null)
                setViewOnClick(reloadView);
        }

        setViewVisiable(loadingLayout, View.GONE);
        setViewVisiable(loadFailureLayout, View.GONE);
        setViewVisiable(emptyLayout, View.GONE);
        if (isContentEmpty()) {
            //在--显示视图--为空的情况下
            if (savedInstanceSate != null) {
                requestData();
            } else {
                //第一次肯定是进入这里
                setViewVisiable(emptyLayout, View.VISIBLE);
                setViewVisiable(contentLayout, View.GONE);
            }
        } else {
            //再有contentView的情况下
            setViewVisiable(contentLayout, View.VISIBLE);
        }
    }

    protected void setViewOnClick(View v) {
        if (v == null)
            return;
        //根据需求为不同的view绑定onclick
        v.setOnClickListener(innerOnClickListener);
    }

    public void setContentEmpty(boolean empty) {
        this.contentEmpty = empty;
    }

    public boolean isContentEmpty() {
        return contentEmpty;
    }

    private void setViewVisiable(View v, int visibility) {
        if (v != null)
            v.setVisibility(visibility);
    }

    /**
     * 根视图
     *
     * @return
     */
    public ViewGroup getRootView() {
        return rootView;
    }

    //内部点击
    View.OnClickListener innerOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onViewClicked(v);
        }

    };



    /**
     * 根据 实际业务状态 的加载状态，刷新视图----子类需要根据实际的状态来改变视图
     *
     * @param state
     *
     * @param tag
     */
    protected void viewStateChanged(MBaseViewState state, Serializable tag) {

        // 数据从网络获取---正在加载中---准备
        if (state == MBaseViewState.prepare) {
            if (isContentEmpty()) {
                setViewVisiable(loadingLayout, View.VISIBLE);

                setViewVisiable(contentLayout, View.GONE);
            } else {
                setViewVisiable(loadingLayout, View.GONE);

                setViewVisiable(contentLayout, View.VISIBLE);
            }

            setViewVisiable(emptyLayout, View.GONE);
            setViewVisiable(loadFailureLayout, View.GONE);
        }
        // Task成功
        else if (state == MBaseViewState.success) {
            setViewVisiable(loadingLayout, View.GONE);

            if (isContentEmpty()) {
                setViewVisiable(emptyLayout, View.VISIBLE);
            } else {
                setViewVisiable(contentLayout, View.VISIBLE);
            }
        }
        // 取消Task
        else if (state == MBaseViewState.canceled) {
            if (isContentEmpty()) {
                setViewVisiable(loadingLayout, View.GONE);
                setViewVisiable(emptyLayout, View.VISIBLE);
            }
        }
        // Task失败
        else if (state == MBaseViewState.falid) {
            if (isContentEmpty()) {
                setViewVisiable(emptyLayout, View.GONE);
                setViewVisiable(loadingLayout, View.GONE);
                setViewVisiable(loadFailureLayout, View.VISIBLE);
                if (tag != null && loadFailureLayout != null)
                    ViewUtils.setTextViewValue(loadFailureLayout, R.id.txtLoadFailed, tag.toString());
            }
        }
        // Task结束
        else if (state == MBaseViewState.finished) {

        }
    }

        /*********自定义抽象结果************************************************************************/

        /**
         * 当前视图resid
         *
         * @return
         */
        abstract protected int returnresID();

        /**
         * 子类重写这个方法，初始化视图----初始化视图代码
         *
         * @param inflater
         * @param savedInstanceSate
         */
        abstract protected void initView(LayoutInflater inflater, Bundle savedInstanceSate);

        /*********自定义抽象结果***********************************************************************/


        /*******************子类具体实现的类容***************/

        /**
         * 视图点击回调，子类重写
         *
         * @param view
         */
        public void onViewClicked(View view) {
            if (view.getId() == R.id.layoutReload)
                requestData();
//        else if (view.getId() == R.id.layoutRefresh)
//            requestData();
        }

        /**
         * 初次创建时默认会调用一次----请求数据
         */
        abstract public void requestData();



        /**
         * Action的home被点击了
         *
         * @return
         */
        public boolean onHomeClick() {
            return onBackClick();
        }

        /**
         * 返回按键被点击了
         *
         * @return
         */
        public boolean onBackClick() {
            return false;
        }

    }
