/**
 * Title: MRefreshFragment
 * Package: com.weibo.martn.weiboapp.fragment.base
 * Description: (支持下拉刷新的fragment的，基础架构)
 * Date 2015/5/15 09:57
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.fragment.base;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.io.Serializable;

public abstract class MRefreshFragment<T extends Serializable, Ts extends Serializable, V extends View> extends MBaseFragment
        implements AbsListView.RecyclerListener, AbsListView.OnScrollListener, AsToolbar.OnToolbarDoubleClick, AdapterView.OnItemClickListener {
}
