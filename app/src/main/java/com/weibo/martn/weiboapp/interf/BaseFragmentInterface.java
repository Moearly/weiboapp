/**
 * Title: BaseFragmentInterface
 * Package: com.weibo.martn.weiboapp.interf
 * Description: (碎片的基类的结构---主要的驱动逻辑---完全从基类中抽离实现代码的可读性)
 * Date 2015/5/16 17:19
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.interf;

import android.view.View;


public interface BaseFragmentInterface {
	
	public void initView(View view);
	
	public void initData();
}
