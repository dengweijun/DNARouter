package com.ben.lib_common.base;

import android.app.Application;

import com.ben.lib_common.utils.DisplayUtils;
import com.ben.skinsupport.SkinManager;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayUtils.init(this);
        SkinManager.init(this);
    }
}
