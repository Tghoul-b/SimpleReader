package com.project.reader.ui.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reader.R;
import com.project.reader.Config;
import com.project.reader.entity.fontFamilyBean;
import com.project.reader.ui.util.Setting;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.Themetools;

import java.util.Collection;

import es.dmoral.toasty.Toasty;

public class fontFamilyAdapter extends CommonAdapter<fontFamilyBean,CommonAdapter.ViewHolder> {
    private Setting mSetting;
    private String []ttfRes;
    public fontFamilyAdapter(Context context, int layoutId) {

        super(context, layoutId);
        mSetting=new Setting(context);
        ttfRes=mSetting.getTtfRes();
    }
    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void addAll(Collection<? extends fontFamilyBean> collection) {
        super.addAll(collection);
        setStatus();//加入的时候重新设置一下status
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBind(CommonAdapter.ViewHolder holder, fontFamilyBean data) {
        LinearLayout linearLayout=holder.getView(R.id.font_listview_adapter);
        holder.setText(R.id.font_family_sample_text,data.getFontName());
        holder.setText(R.id.font_family_size,data.getFontSize());
        if(data.getFontName().equals("方正楷体"))
            System.out.println("get here");
        TextView textView=holder.getView(R.id.font_family_download);
        switch (data.getStatus()){
            case 0:
                holder.setText(R.id.font_family_download,"下载");
                Themetools.changeFontUse(textView,false);
                break;
            case 1:
                textView.setText("使用");
                Themetools.changeFontUse(textView,true);
                textView.setTextColor(Color.WHITE);
                break;
            case 2:
                textView.setTextColor(mContext.getResources().getColor(R.color.black));
                textView.setBackground(mContext.getResources().getDrawable(R.drawable.font_icon_nothing));
                textView.setText("使用中");
                break;
            case 3:
                holder.setText(R.id.font_family_download,"下载中");
                Themetools.changeFontUse(textView,false);

        }
        if(data.getStatus()==3){
            getHttpRes(data,textView);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = (String) textView.getText();
                if (s.equals("下载")) {
                    getHttpRes(data,textView);
                } else if (s.equals("使用")) {
                    TextView sampleText = holder.getView(R.id.font_family_sample_text);
                    String text = (String) sampleText.getText();
                    aCache.put("useTypeFace", text);
                    textView.setTextColor(mContext.getResources().getColor(R.color.black));
                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.font_icon_nothing));
                    textView.setText("使用中");
                }
            }
        });
    }
    private void getHttpRes(fontFamilyBean data,TextView textView){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                data.DownLoadFileFormUrl(success -> {
                    if(success){
                        App.runOnUiThread(()->{
                            Themetools.changeFontUse(textView,true);
                            textView.setText("使用");
                            textView.setTextColor(Color.WHITE);
                            Toasty.success(mContext,"字体下载成功",Toasty.LENGTH_SHORT).show();
                        });
                        String []tmp=mSetting.getTtfRes();
                        mSetting.setTtfRes(tmp);
                        mSetting.saveAllConfig();
                    }
                    else{
                        App.runOnUiThread(()->{
                            textView.setText("下载");
                            Themetools.changeFontUse(textView,false);
                        });

                    }
                });

            }
        });
        thread.start();
        textView.setText("下载中");
    }
    private void setStatus(){
       String useTypeFaceName=aCache.getAsString("useTypeFace");
        if(TextUtils.isEmpty(useTypeFaceName))
            useTypeFaceName="默认字体";
        for(int i=0;i<mDatas.size();i++){
            fontFamilyBean bean=mDatas.get(i);
            if(bean.getFontName().equals(useTypeFaceName))
                bean.setStatus(2);
            else if(bean.isInCache()){
                if(i>0&&ttfRes[i-1].equals("failure"))
                    bean.setStatus(3);
                else
                    bean.setStatus(1);
            }
            else if(bean.getFontName().equals("默认字体")){
                bean.setStatus(1);
            }
        }
    }

    public static class ViewHolder extends CommonAdapter.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
