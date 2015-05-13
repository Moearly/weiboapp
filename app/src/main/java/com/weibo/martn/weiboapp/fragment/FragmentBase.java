/**
 * Title: FragmentBase
 * Package: com.weibo.martn.weiboapp.fragment
 * Description: (fragment的基类，封装业务逻辑，和常用方法)
 * Date 2015/5/11 08:51
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.weibo.martn.weiboapp.R;

public class FragmentBase extends Fragment {

    /**
     * 启动另一个新的activity
     * @param intent
     */
    public void startNewActivity(final Intent intent) {
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_up_in,
                R.anim.push_up_out);
    }
}
