/**
 * Title: MyFragmentTabHost
 * Package: com.weibo.martn.weiboapp.widget
 * Description: (一般性--常用的底部bar结构)
 * Date 2015/5/14 23:03
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.widget;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

public class MyFragmentTabHost extends FragmentTabHost {
    //当前的标记
    private String mCurrentTag;
    //没有tab变化的标记
    private String mNoTabChangedTag;

    public MyFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onTabChanged(String tag) {

        if (tag.equals(mNoTabChangedTag)) {
            setCurrentTabByTag(mCurrentTag);
        } else {
            super.onTabChanged(tag);
            mCurrentTag = tag;
        }
    }

    public void setNoTabChangedTag(String tag) {
        this.mNoTabChangedTag = tag;
    }
}
