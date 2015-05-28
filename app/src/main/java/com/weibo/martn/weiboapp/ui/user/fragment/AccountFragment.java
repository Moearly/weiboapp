/**
 * Title: AccountFragment
 * Package: com.weibo.martn.weiboapp.ui.user.fragment
 * Description: (账户登陆管理界面)
 * Date 2015/5/13 11:36
 *
 * @author MartnLei MartnLei_163_com
 * @version V2.0 本版实现了代理登陆---而非授权快捷按钮登陆
 */
package com.weibo.martn.weiboapp.ui.user.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.app.AppContext;
import com.weibo.martn.weiboapp.app.MyApplication;
import com.weibo.martn.weiboapp.base.BaseActivity;
import com.weibo.martn.weiboapp.base.FragmentContainerActivity;
import com.weibo.martn.weiboapp.adapter.base.ListBaseAdapter;
import com.weibo.martn.weiboapp.base.MListFragment;
import com.weibo.martn.weiboapp.bean.sina.AccessToken;
import com.weibo.martn.weiboapp.bean.sina.AccountBean;
import com.weibo.martn.weiboapp.database.AccountDB;
import com.weibo.martn.weiboapp.ui.base.MainActivity;
import com.weibo.martn.weiboapp.ui.user.adapter.AccountListAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;

public class AccountFragment extends MListFragment {

    @InjectView(R.id.srl_refresh)
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.ll_content_empty)
    private View empty_view;

    private static ArrayList<AccountBean> mData;
    private AccountListAdapter adapter;

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, AccountFragment.class, null);
    }


    @Override
    protected void initView(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.initView(inflater, savedInstanceSate);
        mData = new ArrayList<AccountBean>();
        BaseActivity activity = (BaseActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.title_acount);
        setHasOptionsMenu(true);
        if (empty_view != null) {
            empty_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn_account_add ) {
                        addAccount(null);
                    }
                }
            });
        }

    }

    @Override
    protected ListBaseAdapter getListAdapter() {
        adapter = new AccountListAdapter(R.string.str_loding, R.string.str_no_more,this);
        adapter.setData(mData);
        return adapter;
    }

    @Override
    public void requestData() {
        //重写父类---在这个界面我们并不需要使用网络加载数据和缓存策略，只是需要重数据库中读取已近获得认证的用户
        new AccountTask().execute();
    }

    @Override
    protected int returnresID() {
        return R.layout.fg_ui_account;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 新增授权
        if (item.getItemId() == R.id.add) {
            addAccount(null);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加账户
     * @param v
     */
    void addAccount(View v) {
        LoginFragment.launch(this, 1000);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            //表示登陆成功，返回
            AccessToken token = (AccessToken) data.getSerializableExtra("token");
            String days = String.valueOf(TimeUnit.SECONDS.toDays(token.getExpires_in()));
            //计算token的失效时长---一般未经过审核的开放者是1天
            //提示用户
            new AlertDialogWrapper.Builder(getActivity())
                    .setTitle(R.string.remind)
                    .setMessage(String.format(getString(R.string.account_newaccount_remind), days))
                    .setPositiveButton(R.string.i_know, null)
                    .show();
            //请求数据库--刷新显示
            requestData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AccountBean account = (AccountBean) adapter.getItem(position);
        if (AccountBean.isExpired(account)) {
            new AlertDialogWrapper.Builder(getActivity())
                    .setTitle(R.string.remind)
                    .setMessage(R.string.account_expired)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginFragment.launch(AccountFragment.this, account.getAccount(), account.getPassword(), 1000);
                        }
                    })
                    .show();
            return;
        }

        login(account, true);

        getActivity().finish();
    }

    // 登录账号
    public static void login(AccountBean accountBean, boolean toMain) {
        if (AppContext.isLogedin()) {
//            // 2、清理正在发布的数据
//            PublishService.stopPublish();
//            // 3、重新开始读取未读消息
//            UnreadService.stopService();
//            // 4、清理未读消息
//            UnreadCountNotifier.mCount = new UnreadCount();
//            // 5、清理通知栏
//            PublishNotifier.cancelAll();
//            // 6、清理内存数据
////            TimelineMemoryCacheUtility.clear();
        }

        // 登录该账号
        AppContext.login(accountBean);
        AccountDB.setLogedinAccount(accountBean);

        // 进入首页
        if (toMain)
            MainActivity.login();
    }



    /**
     * 重数据库中获取用户
     */
    class AccountTask extends AsyncTask<Void, Void, ArrayList<AccountBean>> {

        public AccountTask() {
            mState = STATE_REFRESH;
        }

        @Override
        protected ArrayList<AccountBean> doInBackground(Void... params) {

            return (ArrayList<AccountBean>) AccountDB.query();
        }

        @Override
        protected void onPostExecute(ArrayList<AccountBean> accountBeans) {
            super.onPostExecute(accountBeans);
            if(accountBeans == null || accountBeans.size() == 0) {
                //没有数据
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                    empty_view.setVisibility(View.VISIBLE);
                }
            }
            mData = accountBeans;
            mState = STATE_NONE;
        }
    }
}
