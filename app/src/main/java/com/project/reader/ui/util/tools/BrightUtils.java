package com.project.reader.ui.util.tools;

import android.content.ContentResolver;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class BrightUtils {
    public static int getScreenBrightness(AppCompatActivity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }
    public static void setBrightness(AppCompatActivity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    public static int brightToProgress(int brightness){
        return (int)(Float.valueOf(brightness) * (1f / 255f) * 100);
    }

    public static int progressToBright(int progress){
        return progress  * 255 / 100;
    }

    /**
     * 亮度跟随系统
     * @param activity
     */
    public static void followSystemBright(AppCompatActivity activity){
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness =   WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        activity.getWindow().setAttributes(lp);
    }

}