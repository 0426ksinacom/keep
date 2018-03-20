package com.demoapplication.uitil;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by Administrator on 2016/10/4.
 * Function:
 */
public class Fileutil {

    /**
     * 从asset路径下读取对应文件转String输出
     * Keep
     *
     * @param mContext
     * @return
     */
    public static String getJson(Context mContext, String fileName) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
        } finally {

            LogMy.info("finally");
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().trim();
    }

    /**
     * 从asset路径下读取成语转String输出
     * 把空行自动过滤
     *
     * @param mContext
     * @return
     */
    public static String getChengyuFromAssets(Context mContext, String fileName) {
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                if (!TextUtils.isEmpty(next)) {
                    next = next + "\n";
                    sb.append(next);
//                    LogMy.info(next);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            LogMy.info("finally");
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static void savefile(Context context, String s) {

        FileOutputStream fos = null;

        try {

            File cachefile = getCacheDirectory(context, true);

            File cachefiledata = new File(cachefile, "key");

            fos = new FileOutputStream(cachefiledata);
            fos.write(s.getBytes());

            // 将数据写入本地

            // 虚拟机内存小于10兆，则清除缓存
            // if (Runtime.getRuntime().freeMemory() < 5L * 1024L * 1024L) {
            // removeAllCache();
            // return;
            // } else if (map.containsKey(key)) {
            // return;
            // }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

//			map.put(key, key);

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        // 下载缓存
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "character");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                L.w("Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".mystore").createNewFile();
            } catch (IOException e) {
                L.i("Can't create \".mystore\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }

    //
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        if (preferExternal && MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }

        // if (appCacheDir == null) {
        // appCacheDir = context.getCacheDir();
        // }

        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/othercache/";
            L.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }

        return appCacheDir;
    }

    /**
     * capy文件
     * @param context
     * @param path
     * @param dirname
     * @throws IOException
     */
    public static void copyAssetDirToFiles(Context context, String path, String dirname)
            throws IOException {
        File dir = new File(path + "/" + dirname);
        dir.mkdir();

        AssetManager assetManager = context.getAssets();
        String[] children = assetManager.list(dirname);
        for (String child : children) {
            child = dirname + '/' + child;
            String[] grandChildren = assetManager.list(child);
            if (0 == grandChildren.length)
                copyAssetFileToFiles(context, path, child);
            else
                copyAssetDirToFiles(context, path, child);
        }
    }

    public static void copyAssetFileToFiles(Context context, String path, String filename)
            throws IOException {
        InputStream is = context.getAssets().open(filename);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();

        File of = new File(path + "/" + filename);
        of.createNewFile();
        FileOutputStream os = new FileOutputStream(of);
        os.write(buffer);
        os.close();
    }


    /**
     * 复制asset文件到指定目录
     * @param oldPath  asset下的路径
     * @param newPath  SD卡下保存路径
     */
    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
