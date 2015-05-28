/**
 * Title: NavigationDrawerFragment
 * Package: com.weibo.martn.weiboapp.fragment
 * Description: (新版的menu--fragment)
 * Date 2015/5/15 09:38
 *
 * @author MartnLei MartnLei_163_com
 * @version V2.0
 */
package com.weibo.martn.weiboapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.interf.NavigationDrawerCallbacks;
import com.weibo.martn.weiboapp.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NavigationDrawerFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private NavigationDrawerCallbacks mCallbacks;
    //当前选中的位置
    private int mCurrentSelectPosition;
    //position数据来自的位置
    private boolean mFromSavedInstanceState;
    //侧滑栏的根视图
    private View mDrawerMenuView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationDrawerCallbacks) {
            //是否有实现回调
            mCallbacks = (NavigationDrawerCallbacks)activity;
        } else {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //取出保存的之前的状态
            mCurrentSelectPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @InjectView(R.id.lv_menu_main)
    private ListView mDrawerMenuList;
    @InjectView(R.id.menu_item_setting)
    private LinearLayout menu_item_setting;
    @InjectView(R.id.menu_item_exit)
    private LinearLayout menu_item_exit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerMenuView = inflater.inflate(R.layout.fg_navi_drawer, container, false);
        ButterKnife.inject(this, mDrawerMenuView);
        initView(mDrawerMenuView);
        initData();
        return mDrawerMenuView;
    }


    @Override
    public void initView(View view) {
        mDrawerMenuList.setOnItemClickListener(this);
        menu_item_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        menu_item_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void initData() {
        //处理显示所需要的数据，为---app准备数据
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * listview中每一个item显示的数据
     */
    class MenuItemView {

    }
}
