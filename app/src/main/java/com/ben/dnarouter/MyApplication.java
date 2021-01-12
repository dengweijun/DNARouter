package com.ben.dnarouter;


import android.os.Build;

import com.ben.lib_arouter.DNRouter;
import com.ben.lib_common.base.BaseApplication;

import androidx.annotation.RequiresApi;

public class MyApplication extends BaseApplication {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        DNRouter.init(this);
    }

}