package com.project.reader.ui.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.example.reader.R;
import com.project.reader.Config;
import com.project.reader.ui.util.cache.ACache;

import java.util.List;

/**
 * 阅读的一些相关配置
 */
public class Setting {
    private int brightProgress;//记录上次的亮度进度条的进度
    private int ReadTextSize;
    private int ReadTitleSize;
    private Context mContext;
    private  ACache aCache;
    private int readStyle;//阅读风格
    private int follow_sys_checked;//跟随系统按钮是否按下
    private int HorizontalScreen=0;//0是竖屏，1是横屏
    private int pageMode;//翻页模式
    private int nightMode;//夜间模式
    private int themeIdx;//1-10
    private String[] ttfRes;
    private Integer[]bac_colorIds={R.color.read_default_bac,R.color.read_dark_bac,R.color.read_green_bac,R.color.read_brown_bac
            ,R.color.read_black_bac,R.color.read_light_yellow_bac};
    private Integer[]bac_color_text_colors={R.color.read_default_bac_text_color,R.color.read_dark_bac_text_color,R.color.read_green_bac_text_color
            ,R.color.read_brown_bac_text_color,R.color.read_black_bac_text_color,R.color.read_light_yellow_bac_text_color};
    public Setting(Context context){
        this.mContext=context;
        aCache=ACache.get(mContext);
        initConfig();
    }

    public String[] getTtfRes() {
        return ttfRes;
    }

    public void setTtfRes(String[] ttfRes) {
        this.ttfRes = ttfRes;
    }

    public int getNightMode() {
        return nightMode;
    }

    public void setNightMode(int nightMode) {
        this.nightMode = nightMode;
    }

    public int getPageMode() {
        return pageMode;
    }

    public void setPageMode(int pageMode) {
        this.pageMode = pageMode;
    }

    public void initConfig(){
        String s=aCache.getAsString("brightProgress");
        if(!TextUtils.isEmpty(s))
            this.brightProgress= Integer.parseInt(s);
        else
            this.brightProgress= Config.initBrightNess;
        s=aCache.getAsString("ReadTextSize");
        if(!TextUtils.isEmpty(s))
            this.ReadTextSize=Integer.parseInt(s);
        else
            this.ReadTextSize=Config.initReadTextSize;
        s=aCache.getAsString("ReadTitleSize");
        if(!TextUtils.isEmpty(s))
            this.ReadTitleSize=Integer.parseInt(s);
        else
            this.ReadTitleSize=Config.initTitleTextSize;
        s=aCache.getAsString("follow_sys_checked");
        if(!TextUtils.isEmpty(s))
            this.follow_sys_checked=Integer.parseInt(s);
        else
            this.follow_sys_checked=0;
        s=aCache.getAsString("readStyle");
        if(!TextUtils.isEmpty(s))
            this.readStyle=Integer.parseInt(s);
        else
            this.readStyle=0;
        s=aCache.getAsString("HorizontalScreen");
        if(!TextUtils.isEmpty(s))
            this.HorizontalScreen=Integer.parseInt(s);
        else
            this.HorizontalScreen=0;
        s=aCache.getAsString("pageMode");
        if(!TextUtils.isEmpty(s))
            this.pageMode=Integer.parseInt(s);
        else
            this.pageMode=0;
        s=aCache.getAsString("nightMode");
        if(!TextUtils.isEmpty(s))
            this.nightMode=Integer.parseInt(s);
        else
            this.nightMode=0;
        s=aCache.getAsString("themeIdx");
        if(!TextUtils.isEmpty(s))
            this.themeIdx=Integer.parseInt(s);
        else
            this.themeIdx=0;
        s=aCache.getAsString("ttfRes");
        if(!TextUtils.isEmpty(s))
            ttfRes=s.split(",");
        else{
            ttfRes=new String[]{"failure","failure","failure","failure"};
        }
    }

    public Integer[] getBac_colorIds() {
        return bac_colorIds;
    }


    public Integer[] getBac_color_text_colors() {
        return bac_color_text_colors;
    }

    public int getBrightProgress() {
        return brightProgress;
    }

    public void setBrightProgress(int brightProgress) {
        this.brightProgress = brightProgress;
    }

    public int getReadTextSize() {
        return ReadTextSize;
    }

    public void setReadTextSize(int readTextSize) {
        ReadTextSize = readTextSize;
    }

    public int getReadTitleSize() {
        return ReadTitleSize;
    }

    public void setReadTitleSize(int readTitleSize) {
        ReadTitleSize = readTitleSize;
    }

    public int getFollow_sys_checked() {
        return follow_sys_checked;
    }

    public void setFollow_sys_checked(int follow_sys_checked) {
        this.follow_sys_checked = follow_sys_checked;
    }

    public int getThemeIdx() {
        return themeIdx;
    }

    public void setThemeIdx(int themeIdx) {
        this.themeIdx = themeIdx;
    }

    public int getReadStyle() {
        return readStyle;
    }

    public void setReadStyle(int readStyle) {
        this.readStyle = readStyle;
    }

    public int getHorizontalScreen() {
        return HorizontalScreen;
    }

    public void setHorizontalScreen(int horizontalScreen) {
        HorizontalScreen = horizontalScreen;
    }
    public void saveAllConfig(){
        aCache.put("brightProgress",Integer.toString(brightProgress));
        aCache.put("ReadTextSize",Integer.toString(ReadTextSize));
        aCache.put("ReadTitleSize",Integer.toString(ReadTitleSize));
        aCache.put("follow_sys_checked",Integer.toString(follow_sys_checked));
        aCache.put("readStyle",Integer.toString(readStyle));
        aCache.put("HorizontalScreen",Integer.toString(HorizontalScreen));
        aCache.put("pageMode",Integer.toString(pageMode));
        aCache.put("nightMode",Integer.toString(nightMode));
        aCache.put("themeIdx",Integer.toString(themeIdx));
        String s="";
        for(int i=0;i<ttfRes.length;i++){
            s+=ttfRes[i];
            if(i<ttfRes.length-1)
                s+=",";
        }
        aCache.put("ttfRes",s);

    }
}
