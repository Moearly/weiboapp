/**
 * Title: AListFragment
 * Package: com.weibo.martn.weiboapp.fragment.base
 * Description: (以list形式数据呈现的fragment---主要处理下拉刷新和listview)
 * Date 2015/5/15 09:55
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.base;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.adapter.base.ListBaseAdapter;
import com.weibo.martn.weiboapp.app.AppContext;
import com.weibo.martn.weiboapp.bean.Base;
import com.weibo.martn.weiboapp.bean.Notice;
import com.weibo.martn.weiboapp.cache.CacheManager;
import com.weibo.martn.weiboapp.utils.StringUtils;
import com.weibo.martn.weiboapp.utils.TDevice;
import com.weibo.martn.weiboapp.utils.UIHelper;


import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.InjectView;

public abstract class MListFragment extends MBaseFragment implements PullToRefreshBase.OnRefreshListener<ListView>,
        PullToRefreshBase.OnLastItemVisibleListener, AdapterView.OnItemClickListener {

    //要想使用下拉涮新的listview
    @InjectView(R.id.lv_comm_container)
    protected ListView mListView;

    @InjectView(R.id.srl_refresh)
    protected SwipeRefreshLayout mSwipeRefresh;

    protected ListBaseAdapter mAdapter;

    //缓存表示信息----主要是缓存的内型
    protected String mCatalog = "default";
//    protected int mCatalog = NewsList.CATALOG_ALL;

    /**
     * 读取缓存的任务
     */
    private AsyncTask<String, Void, ListEntity> mCacheTask;
    //对返回数据请求的解析数据的处理
    private ParserTask mParserTask;

    protected int mCurrentPage = 0;

//    接口
    public interface ListEntity extends Serializable {

        public List<?> getList();

    }
    /**********************************/

    /**
     * listView组件下拉的处理
     */
    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (mState == STATE_NONE && mAdapter != null
                    && mAdapter.getDataSize() > 0
                    && mListView.getLastVisiblePosition() == (mListView
                    .getCount() - 1)) {
                //处理滑动---当下拉到达最后一个，然后就让底部的下拉更多显示
                onLastItemVisible();
            }
        }
    };

    @Override
    protected void initView(LayoutInflater inflater, Bundle savedInstanceSate) {
        /**
         * 下拉刷新view组件初始化
         */
        mSwipeRefresh.setColorSchemeColors(R.array.srf_colors);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MListFragment.this.onRefresh(null);
            }
        });

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(mScrollListener);

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);

        } else {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);

            if (requestDataIfViewCreated()) {
                mCurrentPage = 0;
                mState = STATE_REFRESH;
                requestData();
            } else {
                //TODO:
            }
        }

        if (mAdapter == null || mAdapter.getDataSize() == 0) {
            //还是没有数据
        }


    }

    @Override
    public void onLastItemVisible() {
        if (mState == STATE_NONE) {
            if (mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
                mCurrentPage++;
                mState = STATE_LOADMORE;
                requestData();
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        //下拉刷新时触发
        //此时肯定是第一页
        mCurrentPage = 0;
        mState = STATE_REFRESH;//所处的状态也是刷新状态----
        //请求数据
        requestData();
    }


    /**
     * 异步请求网络数据---给子类使用
     */
    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              byte[] responseBytes) {
            //在已经完成ac与fg绑定的情况下
            if (isAdded()) {
                //界面处在需要---重新刷新的状态下
                if (mState == STATE_REFRESH) {
                    onRefreshNetworkSuccess();
                    AppContext.putLastRefreshTime(getCacheKey(),
                            StringUtils.getCurTimeStr());
                }
                executeParserTask(responseBytes);
            }
        }

        private void executeParserTask(byte[] data) {
            cancelParserTask();
            mParserTask = new ParserTask(data);
            mParserTask.execute();
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            if (isAdded()) {
                readCacheData(getCacheKey());
            }
        }
    };

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    class ParserTask extends AsyncTask<Void, Void, String> {

        private byte[] reponseData;
        private boolean parserError;
        private List<?> list;

        public ParserTask(byte[] data) {
            this.reponseData = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ListEntity data = parseList(new ByteArrayInputStream(
                        reponseData));
                if (data instanceof Base) {
                    //解析后的数据类型判断---
                    Notice notice = ((Base) data).getNotice();
                    if (notice != null) {
                        //并根据是否携带通知消息---发送通知
                        UIHelper.sendBroadCast(getActivity(), notice, "list");
                    }
                }
                new SaveCacheTask(getActivity(), data, getCacheKey()).execute();
                list = data.getList();
            } catch (Exception e) {
                e.printStackTrace();
                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (parserError) {
                //解析发生错误时---读取本地缓存
                readCacheData(getCacheKey());
            } else {
                //网络获取数据成功----且对数据的解析也成功
                executeOnLoadDataSuccess(list);
                executeOnLoadFinish();
            }
        }
    }

    protected void executeOnLoadDataSuccess(List<?> data) {
        if (mState == STATE_REFRESH)
            mAdapter.clear();
        mAdapter.addData(data);
        if (data.size() == 0 && mState == STATE_REFRESH) {
            //没有新的数据---切处于刷新
            //提示已经是最新的了
            AppContext.showToast(R.string.data_has_new);
        } else if (data.size() < TDevice.getPageSize()) {
//            数据小于1页
            if (mState == STATE_REFRESH)
                mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
            else
                mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
        } else {
            mAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
        }
    }

    protected void executeOnLoadFinish() {
        //数据请求完成后停止下拉刷新组件
        mSwipeRefresh.setRefreshing(false);
        mState = STATE_NONE;
    }

    /**
     * 建立缓存索引key
     * @return
     */
    protected String getCacheKey() {
        return new StringBuffer(getCacheKeyPrefix()).append(mCatalog)
                .append("_").append(mCurrentPage).append("_")
                .append(TDevice.getPageSize()).toString();
    }

    public void setmCatalog(String catalog) {
        mCatalog = catalog;
    }
/***********************************************************************************/
    /**
     * 主要是子类具体实现-----getRealView
     * @return
     */
    protected abstract ListBaseAdapter getListAdapter();

    protected boolean requestDataIfViewCreated() {
        return true;
    }

    protected ListEntity readList(Serializable seri) {
        return null;
    }

    /**
     *
     * @param is
     * @return
     * @throws Exception
     */
    protected ListEntity parseList(InputStream is) throws Exception {
        return null;
    }

    /**
     * 如果需要定制缓存的key前缀
     * @return
     */
    protected String getCacheKeyPrefix() {
        return null;
    }

    /**
     * 实际情况请求数据
     */
    protected void sendRequestData() {

    }



    /**
     * 重新刷新网络成功
     */
    protected void onRefreshNetworkSuccess() {

    }

    /**
     * 默认在没有网络的情况下才读取本地缓存
     */
    @Override
    public void requestData() {
        String key = getCacheKey();
        if (TDevice.hasInternet()
                && (!CacheManager.isReadDataCache(getActivity(), key))) {
            sendRequestData();
        } else {
            readCacheData(key);
        }
    }




    /**
     * 缓存任务异步处理
     */
    private class CacheTask extends AsyncTask<String, Void, ListEntity> {
        private WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewStateChanged(MBaseViewState.prepare, null);
        }

        @Override
        protected ListEntity doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return readList(seri);
            }
        }

        @Override
        protected void onPostExecute(ListEntity list) {
            super.onPostExecute(list);
            if (list != null) {
                viewStateChanged(MBaseViewState.success, null);
                setContentEmpty(false);
            } else {
                viewStateChanged(MBaseViewState.falid, null);
                setContentEmpty(true);
            }
            viewStateChanged(MBaseViewState.finished, null);
        }

        @Override
        protected void onCancelled(ListEntity list) {
            super.onCancelled(list);
            viewStateChanged(MBaseViewState.canceled, null);
        }
    }

    /**
     * 读取缓存数据
     * @param cacheKey
     */
    private void readCacheData(String cacheKey) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    /**
     * 取消读取缓存的renwu
     */
    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }



    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> mContext;
        private Serializable seri;
        private String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

}
