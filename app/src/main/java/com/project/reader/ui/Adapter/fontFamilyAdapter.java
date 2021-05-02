package com.project.reader.ui.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reader.R;
import com.project.reader.Config;
import com.project.reader.entity.fontFamilyBean;
import com.project.reader.ui.Activity.ReadActivity;
import com.project.reader.ui.Activity.fontfamilyActivity;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.tools.App;

import java.util.Collection;

import es.dmoral.toasty.Toasty;

public class fontFamilyAdapter extends CommonAdapter<fontFamilyBean,CommonAdapter.ViewHolder> {
    public fontFamilyAdapter(Context context, int layoutId) {
        super(context, layoutId);
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
        switch (data.getStatus()){
            case 0:
                holder.setText(R.id.font_family_download,"下载");
                break;
            case 1:
                TextView textView=holder.getView(R.id.font_family_download);
                textView.setBackground(mContext.getResources().getDrawable(R.drawable.font_use_shape));
                textView.setText("使用");
                textView.setTextColor(Color.WHITE);
                break;
            case 2:
                TextView textView1=holder.getView(R.id.font_family_download);
                textView1.setTextColor(mContext.getResources().getColor(R.color.black));
                textView1.setBackground(mContext.getResources().getDrawable(R.drawable.font_icon_nothing));
                textView1.setText("使用中");
                break;

        }
        TextView textView=holder.getView(R.id.font_family_download);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = (String) textView.getText();
                if (s.equals("下载")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            data.DownLoadFileFormUrl(success -> {
                                if(success){
                                    App.runOnUiThread(()->{
                                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.font_use_shape));
                                    textView.setText("使用");
                                    textView.setTextColor(Color.WHITE);
                                    Toasty.success(mContext,"下载成功",Toasty.LENGTH_SHORT).show();
                                });
                                }
                                else{
                                    App.runOnUiThread(()->{
                                        textView.setText("下载");
                                        textView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                                        textView.setBackground(mContext.getResources().getDrawable(R.drawable.fontfamily_download));
                                        Toasty.error(mContext,"网络异常",Toasty.LENGTH_SHORT).show();
                                    });

                                }
                            });

                        }
                    }).start();
                    textView.setText("请稍候");
                } else if (s.equals("使用")) {
                    TextView sampleText = holder.getView(R.id.font_family_sample_text);
                    String text = (String) sampleText.getText();
                    aCache.put("useTypeFace", text);
                    textView.setTextColor(mContext.getResources().getColor(R.color.black));
                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.font_icon_nothing));
                    textView.setText("使用中");
                    ((fontfamilyActivity) mContext).changeTypeface();
                    setStatus();
                    Intent intent = new Intent();
                    intent.putExtra(Config.FONT_RES,text);
                    ((AppCompatActivity) (fontfamilyActivity)mContext).setResult(AppCompatActivity.RESULT_OK, intent);
                    notifyDataSetChanged();
                }
            }
        });
    }
    private void setStatus(){
       String useTypeFaceName=aCache.getAsString("useTypeFace");
        for(fontFamilyBean bean:mDatas){
            if(bean.isInCache()||bean.getFontName().equals("默认字体"))
                bean.setStatus(1);
        }
        if(TextUtils.isEmpty(useTypeFaceName))
            useTypeFaceName="默认字体";

        for(fontFamilyBean bean:mDatas){
            if(useTypeFaceName.equals(bean.getFontName()))
                bean.setStatus(2);//使用中
        }
    }

    public static class ViewHolder extends CommonAdapter.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
