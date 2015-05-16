/**
 * Title: DataClearManager
 * Package: com.weibo.martn.weiboapp.app.cache
 * Description: (缓存数据的清理)
 * Date 2015/5/15 16:58
 *
 * @author MartnLei MartnLei_163_com
 * @version V1.0
 */
package com.weibo.martn.weiboapp.app.cache;

import com.weibo.martn.weiboapp.app.AppContext;

import java.io.File;

public class DataClearManager {

    /**
     * 清除数据库信息
     * @param appContext
     */
    public static void cleanDatabases(AppContext appContext) {
        deleteFilesByDirectory(new File("/data/data/"
                + appContext.getPackageName() + "/databases"));
    }

    /**
     * 删除某个目录下的所以文件，但不删除文件夹，也不删除指定的单个文件
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    //递归
                    deleteFilesByDirectory(item);
                }
                //删除file
                item.delete();
            }
        }
    }

    /**
     * 清除内部缓存----对应data/data..package_name../files和cache目录
     * @param appContext
     */
    public static void cleanInternalCache(AppContext appContext) {
        deleteFilesByDirectory(appContext.getCacheDir());
        deleteFilesByDirectory(appContext.getFilesDir());
    }

    /**
     * 清除指定的file的目录
     * @param file
     */
    public static void cleanCustomCache(File file) {
        deleteFilesByDirectory(file);
    }
}
