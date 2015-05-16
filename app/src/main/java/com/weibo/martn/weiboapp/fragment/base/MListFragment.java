/**
 * Title: AListFragment
 * Package: com.weibo.martn.weiboapp.fragment.base
 * Description: (以list形式数据呈现的fragment---)
 * Date 2015/5/15 09:55
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.fragment.base;

import android.widget.ListView;

import java.io.Serializable;

public abstract class MListFragment<T extends Serializable, Ts extends Serializable> extends MRefreshFragment<T, Ts, ListView> {
}
