package com.ben.lib_arouter.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.ben.lib_arouter.thread.DefaultPoolExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.RequiresApi;
import dalvik.system.DexFile;

/**
 * @author: BD
 */
public class ClassUtils {
    /**
     * 获得程序所有的apk(instant run会产生很多split apk)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<String> getSourcePaths(Context context)
            throws PackageManager.NameNotFoundException {
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        List<String> sourcePathList = new ArrayList<>();
        sourcePathList.add(applicationInfo.sourceDir);
        //instant run
        if (null != applicationInfo.splitSourceDirs) {
            sourcePathList.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
        }
        return sourcePathList;
    }

    /**
     * 路由表
     *
     * @param context
     * @param packageName
     * @return
     * @throws PackageManager.NameNotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Set<String> getFileNameByPackageName(Application context, final String packageName)
            throws PackageManager.NameNotFoundException, InterruptedException {
        final Set<String> classNameSet = new HashSet<>();
        List<String> pathList = getSourcePaths(context);
        // 使用同步计数器判断，来处理完成
        CountDownLatch parserCtl = new CountDownLatch(pathList.size());
        ThreadPoolExecutor threadPoolExecutor = DefaultPoolExecutor.newDefaultPoolExecutor(pathList.size());
        for (String path : pathList) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // 加载apk中dex，并遍历获得所有包名为{packageName}的类
                    DexFile dexFile = null;
                    try {
                        dexFile = new DexFile(path);
                        Enumeration<String> dexEntries = dexFile.entries();
                        while (dexEntries.hasMoreElements()) {
                            String className = dexEntries.nextElement();
                            if (className.startsWith(packageName)) {
                                classNameSet.add(className);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (null != dexFile) {
                            try {
                                dexFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // 同步计数器释放1个
                        parserCtl.countDown();
                    }
                }
            });
        }
        // 等待执行完成
        parserCtl.await();
        return classNameSet;
    }

}
