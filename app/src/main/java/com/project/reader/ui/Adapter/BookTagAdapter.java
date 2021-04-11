package com.project.reader.ui.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.reader.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

public class BookTagAdapter extends TagAdapter<String> {
    private TextView textView;
    private Context context;
    private int textSize;

    public BookTagAdapter(List<String> datas, Context context, int textSize) {
        super(datas);
        this.context = context;
        this.textSize = textSize;
    }

    @Override
    public View getView(FlowLayout parent, int position, String s) {
        textView=(TextView)View.inflate(context, R.layout.item_book_tag,null);
        if(s==null||s.length()==0)
             return null;
        if(s.startsWith("status0:")){
            textView.setBackground(ContextCompat.getDrawable(context,R.drawable.blue_tag_shape));
        }
        else if(s.startsWith("status1:")){
            textView.setBackground(ContextCompat.getDrawable(context,R.drawable.green_tag_shape));
        }
        textView.setTextSize(textSize);
        textView.setText(s.substring(8));
        return textView;
    }
}
