package com.project.reader.ui.util;


import android.content.Context;
import android.widget.Toast;

import com.project.reader.ui.util.tools.App;

import es.dmoral.toasty.Toasty;

public class ToastyUtils {
    private static Toast mToast = null;

    public static void makeTextWarning(Context context, String content, int duration) {
        try {
            //Toast为空时，直接显示
            if (null == mToast) {
                mToast=Toasty.warning(context,content,duration);
            } else {
                //Toast为不空时，直接改变当前toast的文字
                mToast.setText(content);
                mToast.setDuration(duration);
            }
            mToast.show();
            App.getHandler().postDelayed(()->{
                Destory();
            },1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void Destory(){
        mToast=null;
    }

}
