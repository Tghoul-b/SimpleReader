package com.project.reader.ui.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.project.reader.Config;
import com.project.reader.ui.util.cache.ACache;

/**
 * 阅读的一些相关配置
 */
public class Setting {
    private int brightProgress;//记录上次的亮度进度条的进度
    private int ReadTextSize;
    private int ReadTitleSize;
    private Context mContext;
    private  ACache aCache;
    public Setting(Context context){
        this.mContext=context;
        aCache=ACache.get(mContext);
        initConfig();
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

    public void saveAllConfig(){
        aCache.put("brightProgress",Integer.toString(brightProgress));
        aCache.put("ReadTextSize",Integer.toString(ReadTextSize));
        aCache.put("ReadTitleSize",Integer.toString(ReadTitleSize));
    }
}
