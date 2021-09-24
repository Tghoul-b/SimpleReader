package com.project.reader.ui.util.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.shapes.Shape;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.reader.entity.fontFamilyBean;
import com.project.reader.ui.util.Setting;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public class Themetools {
    public final static int []colorIds={R.color.theme1,R.color.theme2,R.color.theme3,R.color.theme4,R.color.theme5,
            R.color.theme6,R.color.theme7,R.color.theme8,R.color.theme9,R.color.theme10};
    public static void changeActivityTheme(Activity activity){
        Setting mSetting=new Setting(activity);
        int idx=mSetting.getThemeIdx();
        Toolbar toolbar=activity.findViewById(R.id.toolbar);
        if(toolbar!=null)
            toolbar.setBackgroundColor(activity.getResources().getColor(colorIds[idx]));
        TextView statusBar=activity.findViewById(R.id.fakestatusbar);
        if(statusBar!=null)
            statusBar.setBackgroundColor(activity.getResources().getColor(colorIds[idx]));
        TextView searchBtn=activity.findViewById(R.id.search_btn);
        if(searchBtn!=null)
            searchBtn.setBackgroundColor(activity.getResources().getColor(colorIds[idx]));
    }
    public static void changeIconTheme(Activity activity){
        Setting mSetting=new Setting(activity);
        int idx=mSetting.getThemeIdx();
        FrameLayout fl_openBook=activity.findViewById(R.id.fl_open_book);
        if(fl_openBook!=null)
            fl_openBook.setBackgroundColor(activity.getResources().getColor(colorIds[idx]));
    }
    public static void changeBottomTheme(Activity activity){
        Setting mSetting=new Setting(activity);
        int idx=mSetting.getThemeIdx();
        BottomNavigationView bottomNavigationView=activity.findViewById(R.id.bottom_navigation);
        ColorStateList colorStateList=new ColorStateList(new int[][]{{android.R.attr.state_checked},
                {}},new int[]{activity.getResources().getColor(colorIds[idx]),
                activity.getResources().getColor(R.color.textSecondary)});
        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);
    }
    public static void changeSeekBarTheme(Activity activity, SeekBar seekBar){
        Setting mSetting=new Setting(activity);
        int idx=mSetting.getThemeIdx();
        LayerDrawable layerDrawable = (LayerDrawable)
                seekBar.getProgressDrawable();
        Drawable dra=layerDrawable.getDrawable(2);
        dra.setColorFilter(activity.getResources().getColor(colorIds[idx]), PorterDuff.Mode.SRC);
        Drawable dra2=seekBar.getThumb();
        seekBar.getThumb().setColorFilter(activity.getResources().getColor(colorIds[idx]), PorterDuff.Mode.SRC_ATOP);
        seekBar.invalidate();
    }
    public static void changeFollowTheme(TextView textView){
        Setting mSetting=new Setting(App.getApplication());
        int idx=mSetting.getThemeIdx();
        textView.setTextColor(App.getApplication().getResources().getColor(colorIds[idx]));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(2);
        drawable.setStroke(1,App.getApplication().getResources().getColor(colorIds[idx]));
        textView.setBackground(drawable);
    }
    public static void changeFontUse(TextView textView,boolean flag){
        Setting mSetting = new Setting(App.getApplication());
        int idx = mSetting.getThemeIdx();
        if(flag) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setStroke(1, App.getApplication().getResources().getColor(colorIds[idx]));
            drawable.setCornerRadius(2);
            textView.setBackground(drawable);
            textView.setBackgroundColor(App.getApplication().getResources().getColor(colorIds[idx]));
        }
        else{
            textView.setTextColor(App.getApplication().getResources().getColor(colorIds[idx]));
            GradientDrawable drawable = new GradientDrawable();
            drawable.setStroke(1, App.getApplication().getResources().getColor(colorIds[idx]));
            drawable.setCornerRadius(2);
            textView.setBackground(drawable);
        }
    }
}
