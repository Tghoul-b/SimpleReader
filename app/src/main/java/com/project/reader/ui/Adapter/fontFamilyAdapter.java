package com.project.reader.ui.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.example.reader.R;
import com.project.reader.entity.fontFamilyBean;

public class fontFamilyAdapter extends CommonAdapter<fontFamilyBean,CommonAdapter.ViewHolder> {
    public fontFamilyAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }
    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
    @Override
    public void onBind(CommonAdapter.ViewHolder holder, fontFamilyBean data) {
        LinearLayout linearLayout=holder.getView(R.id.font_listview_adapter);
        holder.setText(R.id.font_family_sample_text,data.getFontName());
        holder.setText(R.id.font_family_size,data.getFontSize());
        holder.setText(R.id.font_family_download,"下载");
    }

    public static class ViewHolder extends CommonAdapter.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
