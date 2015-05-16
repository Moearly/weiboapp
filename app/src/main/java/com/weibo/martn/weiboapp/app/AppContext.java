package com.weibo.martn.weiboapp.app;


import java.util.Properties;
import java.util.UUID;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.utils.KJLoger;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.weibo.martn.weiboapp.BuildConfig;
import com.weibo.martn.weiboapp.app.cache.DataClearManager;
import com.weibo.martn.weiboapp.utils.MethodsCompat;
import com.weibo.martn.weiboapp.utils.StringUtils;
import com.weibo.martn.weiboapp.utils.TLog;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppContext extends BaseApplication {

    public static final String KEY_LOAD_IMAGE = "KEY_LOAD_IMAGE";
    public static final String KEY_TWEET_DRAFT = "KEY_TWEET_DRAFT";
    public static final String KEY_FRITST_START = "KEY_FRIST_START";

    public static final int PAGE_SIZE = 20;// 默认分页大小

    private static AppContext instance;

    private int loginUid;

    private boolean login;



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        //这种全局处理登录的做法因人而异
        //initLogin();
        // Thread.setDefaultUncaughtExceptionHandler(AppException
        // .getAppExceptionHandler(this));
        //UIHelper.sendBroadcastForNotice(this);
    }

    private void init() {
        // 初始化网络请求 ---- android-async-http-1.4.6
//        AsyncHttpClient client = new AsyncHttpClient();
//        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
//        client.setCookieStore(myCookieStore);
//        ApiHttpClient.setHttpClient(client);
//        ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));

        // Log控制器-----都是别人框架提供的内容
        KJLoger.openDebutLog(true);
        TLog.DEBUG = BuildConfig.DEBUG;

        // Bitmap缓存地址
        BitmapConfig.CACHEPATH = "Weiboapp/imagecache";
    }

    /**
     * 初始化用户登录-----属于需要用户登录逻辑才会有的逻辑----
     * 原本也是可以放在splash中处理的
     */
//    private void initLogin() {
//        User user = getLoginUser();
//        if (null != user && user.getId() > 0) {
//            login = true;
//            loginUid = user.getId();
//        } else {
//            this.cleanLoginInfo();
//        }
//    }

    /**
     * 清除登录信息
     */
//    public void cleanLoginInfo() {
//        this.loginUid = 0;
//        this.login = false;
//        removeProperty("user.uid", "user.name", "user.face", "user.location",
//                "user.followers", "user.fans", "user.score",
//                "user.isRememberMe", "user.gender", "user.favoritecount");
//    }
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

    /**
     * 获得当前app运行的AppContext
     * 
     * @return
     */
    public static AppContext getInstance() {
        return instance;
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
     * 获取App唯一标识---产生app的唯一标识
     * 
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
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

//    /**
//     * 保存登录信息
//     *
//     * @param username
//     * @param pwd
//     */
//    @SuppressWarnings("serial")
//    public void saveUserInfo(final User user) {
//        this.loginUid = user.getId();
//        this.login = true;
//        setProperties(new Properties() {
//            {
//                setProperty("user.uid", String.valueOf(user.getId()));
//                setProperty("user.name", user.getName());
//                setProperty("user.face", user.getPortrait());// 用户头像-文件名
//                setProperty("user.account", user.getAccount());
//                setProperty("user.pwd",
//                        CyptoUtils.encode("oschinaApp", user.getPwd()));
//                setProperty("user.location", user.getLocation());
//                setProperty("user.followers",
//                        String.valueOf(user.getFollowers()));
//                setProperty("user.fans", String.valueOf(user.getFans()));
//                setProperty("user.score", String.valueOf(user.getScore()));
//                setProperty("user.favoritecount",
//                        String.valueOf(user.getFavoritecount()));
//                setProperty("user.gender", String.valueOf(user.getGender()));
//                setProperty("user.isRememberMe",
//                        String.valueOf(user.isRememberMe()));// 是否记住我的信息
//            }
//        });
//    }

//    /**
//     * 更新用户信息
//     *
//     * @param user
//     */
//    @SuppressWarnings("serial")
//    public void updateUserInfo(final User user) {
//        setProperties(new Properties() {
//            {
//                setProperty("user.name", user.getName());
//                setProperty("user.face", user.getPortrait());// 用户头像-文件名
//                setProperty("user.followers",
//                        String.valueOf(user.getFollowers()));
//                setProperty("user.fans", String.valueOf(user.getFans()));
//                setProperty("user.score", String.valueOf(user.getScore()));
//                setProperty("user.favoritecount",
//                        String.valueOf(user.getFavoritecount()));
//                setProperty("user.gender", String.valueOf(user.getGender()));
//            }
//        });
//    }
//
//    /**
//     * 获得登录用户的信息
//     *
//     * @return
//     */
//    public User getLoginUser() {
//        User user = new User();
//        user.setId(StringUtils.toInt(getProperty("user.uid"), 0));
//        user.setName(getProperty("user.name"));
//        user.setPortrait(getProperty("user.face"));
//        user.setAccount(getProperty("user.account"));
//        user.setLocation(getProperty("user.location"));
//        user.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
//        user.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
//        user.setScore(StringUtils.toInt(getProperty("user.score"), 0));
//        user.setFavoritecount(StringUtils.toInt(
//                getProperty("user.favoritecount"), 0));
//        user.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
//        user.setGender(getProperty("user.gender"));
//        return user;
//    }
//
//
//
//    public int getLoginUid() {
//        return loginUid;
//    }
//
//    public boolean isLogin() {
//        return login;
//    }
//
//    /**
//     * 用户注销
//     */
//    public void Logout() {
//        cleanLoginInfo();
//        ApiHttpClient.cleanCookie();
//        this.cleanCookie();
//        this.login = false;
//        this.loginUid = 0;
//
//        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
//        sendBroadcast(intent);
//    }

    /**
     * 清除保存的缓存
     */
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    /**
     * 清除app缓存-----清除app的缓存
     */
    public void clearAppCache() {
        DataClearManager.cleanDatabases(this);
        // 清除数据缓存
        DataClearManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataClearManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        new KJBitmap().cleanCache();
    }


    /**
     * 设置是否加载的image
     * @param flag
     */
    public static void setLoadImage(boolean flag) {
        set(KEY_LOAD_IMAGE, flag);
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

//    public static String getTweetDraft() {
//        return getPreferences().getString(
//                KEY_TWEET_DRAFT + getInstance().getLoginUid(), "");
//    }
//
//    public static void setTweetDraft(String draft) {
//        set(KEY_TWEET_DRAFT + getInstance().getLoginUid(), draft);
//    }
//
//    public static String getNoteDraft() {
//        return getPreferences().getString(
//                AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), "");
//    }
//
//    public static void setNoteDraft(String draft) {
//        set(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), draft);
//    }

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
}
