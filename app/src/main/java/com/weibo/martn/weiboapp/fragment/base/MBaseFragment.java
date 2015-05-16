/**
 * Title: MBaseFragment
 * Package: com.weibo.martn.weiboapp.fragment.base
 * Description: (自定义fragment的基本类---1.任务管理)
 * Date 2015/5/15 10:00
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.fragment.base;

import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.weibo.martn.weiboapp.works.task.ITaskManager;
import com.weibo.martn.weiboapp.works.task.TaskManager;

public abstract class MBaseFragment extends Fragment implements ITaskManager {
    static final String TAG = MBaseFragment.class.getSimpleName();

    //任务栈的状态
    protected enum MBaseTaskState {
        none, prepare, falid, success, finished, canceled
    }

    //一般的基类抽象都会有一个根视图
    private ViewGroup rootView;// 根视图
    private TaskManager taskManager;// 管理线程
}
