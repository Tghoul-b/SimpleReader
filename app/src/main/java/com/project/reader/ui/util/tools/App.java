package com.project.reader.ui.util.tools;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Handler;


import java.util.logging.LogRecord;

public class App extends Application {
    private static  final Handler handler=new Handler();
    public static  void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
    public static Handler getHandler(){
        return handler;
    }
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
}
