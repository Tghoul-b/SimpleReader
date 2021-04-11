package com.project.reader.ui.util.tools;

import android.app.Application;
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
}
