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
import com.example.reader.databinding.ActivityBookDetailedBinding;
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
        if(DetailBean.getDrawable()!=null){
            Bitmap bitmap= BitmapFactory.decodeByteArray(DetailBean.getDrawable(),0,DetailBean.getDrawable().length);
            binding.rivDpCoverImg.setImageBitmap(bitmap);
            Bitmap bm=bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bm=Blur.onStackBlur(bm,20);
            binding.ivDpBgBlur.setImageBitmap(bm);
        }
        else{
            UtilityBlur.blur(this,binding.ivDpBgBlur,DetailBean.getImgUrl());//加载模糊化的背景图片
        }
    }
}