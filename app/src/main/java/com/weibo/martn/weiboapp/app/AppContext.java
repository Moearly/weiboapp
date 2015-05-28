package com.weibo.martn.weiboapp.app;


import java.util.List;
import java.util.Properties;
import java.util.UUID;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.weibo.martn.weiboapp.app.base.BaseApplication;
import com.weibo.martn.weiboapp.bean.sina.AccessToken;
import com.weibo.martn.weiboapp.bean.sina.AccountBean;
import com.weibo.martn.weiboapp.bean.sina.WeiBoUser;
import com.weibo.martn.weiboapp.database.SinaDB;
import com.weibo.martn.weiboapp.utils.StringUtils;

/**
 * 上下文相关信息，主要封装一些常用的方法
 * 
 * @version 1.0
 */
public class AppContext extends BaseApplication {

    private static AccountBean accountBean;// 当前登录的用户信息

    private static AccessToken advancedToken; //记录当前授权信息

    public static final String KEY_FRITST_START = "KEY_FRIST_START";
    private static final String KEY_APP_ID = "key_app_id";


    /**
     * 登陆操作
     * @param accountBean
     */
    public static void login(AccountBean accountBean) {
//        boolean startUnreadService = AppContext.accountBean == null ||
//                !AppContext.accountBean.getUser().getIdstr().equals(accountBean.getUser().getIdstr());
//
        AppContext.accountBean = accountBean;
//
//        // 未读消息重置
//        if (AppContext.getUnreadCount() == null || startUnreadService) {
//            AppContext.unreadCount = UnreadService.getUnreadCount();
//        }
//        if (AppContext.unreadCount == null)
//            AppContext.unreadCount = new UnreadCount();
//
//        // 开启未读服务
//        if (startUnreadService)
//            UnreadService.startService();
//
//        // 检查更新变化
////        CheckChangedUtils.check(AppContext.getUser(), AppContext.getToken());
//
//        // 刷新定时任务
//        MyApplication.refreshPublishAlarm();
//
//        // 处理点赞数据
//        DoLikeAction.refreshLikeCache();

        if (accountBean.getAdvancedToken() != null)
            AppContext.setAdvancedToken(accountBean.getAdvancedToken());
        else {
            // 读取高级token
            List<AccessToken> token = SinaDB.getSqlite().findAll(AccessToken.class);
            if (token.size() > 0)
                AppContext.setAdvancedToken(token.get(0));
        }
    }

    public static void logout() {
//        // 停止未读服务
//        UnreadService.stopService();
//        // 移除定时任务
//        MyApplication.removeAllPublishAlarm();
        // 退出账号
        accountBean = null;
    }

    /**
     * 判断是否有用户已经授权---登陆
     * @return
     */
    public static boolean isLogedin() {
        return accountBean != null;
    }

    public static void setAdvancedToken(AccessToken token) {
        advancedToken = token;
    }
    /*****************************静态方法区*********************************************/
    /**
     * 放入已读文章列表中---提供的公共的一些方法
     * @param prefFileName
     * @param key
     * @param value
     */
    public static void putReadedPostList(String prefFileName, String key,
                                         String value) {
        SharedPreferences preferences = getPreferences(prefFileName);
        int size = preferences.getAll().size();
        SharedPreferences.Editor editor = preferences.edit();
        if (size >= 100) {
            //对存放记录会有一个阀值-----TODO：可以静态化
            editor.clear();
        }
        editor.putString(key, value);
        apply(editor);
    }


    /**
     * 判断---读取是否是已读的文章列表
     * @param prefFileName
     * @param key
     * @return
     */
    public static boolean isOnReadedPostList(String prefFileName, String key) {
        return getPreferences(prefFileName).contains(key);
    }
    //    private static String PREF_NAME = "creativelocker.pref";



    //存放上次刷新时间的sp
    private static String LAST_REFRESH_TIME = "last_refresh_time.pref";
    /**
     * 记录列表上次刷新时间
     *
     * @param key
     * @param value
     * @return void
     */
    public static void putLastRefreshTime(String key, String value) {
        SharedPreferences preferences = getPreferences(LAST_REFRESH_TIME);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        apply(editor);
    }

    /**
     * 获取列表的上次刷新时间
     *
     * @param key
     * @return
     */
    public static String getLastRefreshTime(String key) {
        return getPreferences(LAST_REFRESH_TIME).getString(key, StringUtils.getCurTimeStr());
    }


    /********************app配置信息的操作****************************/
    /**
     * 判断property是否存在
     * @param key
     * @return
     */
    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    /**
     * 获取所有的app的配置prop
     * @return
     */
    public Properties getProperties() {
        return AppConfig.getAppConfig(this).getProps();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取对应key的配置项
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }


    /**
     * 判断当前版本是否兼容目标版本的方法
     * 
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }


    /**
     * 判断是否是第一次启动
     * @return
     */
    public static boolean isFristStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    /**
     * 设置是否是第一次启动
     * @param frist
     */
    public static void setFristStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }


    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(KEY_APP_ID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(KEY_APP_ID, uniqueID);
        }
        return uniqueID;
    }

    /****************************账户相关信息获取**************************************/

    /**
     * 获取账户信息-----当前登陆的account
     * @return
     */
    public static WeiBoUser getUser() {
        if (!isLogedin())
            return null;

        return accountBean.getUser();
    }
}
