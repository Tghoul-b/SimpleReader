package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.reader.R;
import com.example.reader.databinding.ActivityBookDetailedBinding;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Glide.UtilityBlur;
import com.project.reader.ui.util.Engine.SearchEngine;

import net.qiujuer.genius.graphics.Blur;

import java.util.ArrayList;
import java.util.List;


public class BookDetailedActivity extends AppCompatActivity {
    private List<BookdetailBean> aBooks;
    private BookdetailBean DetailBean;
    private SearchEngine searchEngine;
    private ActivityBookDetailedBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindview();
        aBooks = (ArrayList<BookdetailBean>) getIntent().getSerializableExtra("bookDetails");
        searchEngine=new SearchEngine(this);
        initDetail(0);
    }
    protected void bindview() {
        binding= ActivityBookDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    private void initDetail(int index){
        DetailBean=aBooks.get(index);
        if(DetailBean.NeedInfo()){   //这个可能是前一个界面还没有加载完全信息,用户就点击了。
            searchEngine.initOtherinfo(DetailBean.getSourceClass(),DetailBean,success -> {
                if(success){
                    InitWidget();
                }
            });
        }
        else{
            InitWidget();
        }
    }
    private  void InitWidget(){
        binding.tvBookName.setText(DetailBean.getBookName());
        binding.tvBookAuthor.setText("作者: "+DetailBean.getAuthor());
        binding.bookStatus.setText("状态: "+DetailBean.getStatus());
        binding.tvBookType.setText("类型: "+DetailBean.getNovelType());
        float len1=DetailBean.getBookName().length();
        float len2=DetailBean.getAuthor().length();
        float point=(len1+len2)/(len1*len2)*5;
        if(point>5.0f)
               point=5.0f;
        String result = String.format("%.1f",point);//保留一位小数
        point=Float.parseFloat(result);
        binding.starView.setRating(point);
        binding.pointText.setText(point+"分");
        ExpandableTextView expTv1 = (ExpandableTextView) findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);
        expTv1.setText(DetailBean.getDesc());
        if(DetailBean.getDrawable()!=null){   //这个是因为可能前面已经加载好了这张图片，直接传过来，就不用加载。
            Bitmap bitmap= BitmapFactory.decodeByteArray(DetailBean.getDrawable(),0,DetailBean.getDrawable().length);
            binding.rivDpCoverImg.setImageBitmap(bitmap);
            Bitmap bm=bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bm=Blur.onStackBlur(bm,50);
            binding.ivDpBgBlur.setImageBitmap(bm);
        }
        else{
            Bitmap  bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_default);
            binding.rivDpCoverImg.setImageBitmap(bitmap);
            bitmap=Blur.onStackBlur(bitmap,50);
            binding.ivDpBgBlur.setImageBitmap(bitmap);
        }
    }

}