package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BlurMaskFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.reader.R;
import com.example.reader.databinding.ActivityBookDetailedBinding;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Glide.UtilityBlur;
import com.project.reader.ui.util.Engine.SearchEngine;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;

import net.qiujuer.genius.graphics.Blur;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.List;

import io.alterac.blurkit.BlurKit;


public class BookDetailedActivity extends AppCompatActivity {
    private List<BookdetailBean> aBooks;
    private BookdetailBean DetailBean;
    private SearchEngine searchEngine;
    private ActivityBookDetailedBinding binding;
    private  ACache aCache;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    System.out.println("get here");
                    InitDrawable();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindview();
        aCache=ACache.get(this);
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
    private void InitDrawable(){
        Glide.with(this).load(DetailBean.getImgUrl()).placeholder(R.mipmap.ic_default).error(R.mipmap.ic_default)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Drawable drawable=getApplicationContext().getResources().getDrawable(R.mipmap.ic_default);
                        Bitmap bitmap= BaseApi.drawableToBitamp(drawable);
                        Bitmap bm=bitmap.copy(Bitmap.Config.ARGB_8888,true);
                        bm=BaseApi.blur(getApplicationContext(),bm);
                        binding.ivDpBgBlur.setImageBitmap(bm);
//                        String path= "/data/"+"blur.jpg";
//                        System.out.println(path);
//                        BaseApi.saveBimap(path,bm);
                        //WriteBitMap(bitmap,bm);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap bitmap= BaseApi.drawableToBitamp(resource);
                       // binding.rivDpCoverImg.setImageBitmap(bitmap);
                        Bitmap bm=bitmap.copy(Bitmap.Config.ARGB_8888,true);
                        bm=BaseApi.blur(getApplicationContext(),bm);
                        binding.ivDpBgBlur.setImageBitmap(bm);
                        //WriteBitMap(bitmap,bm);
                        return false;
                    }
                }).into(binding.rivDpCoverImg);
       // binding.blurLayout.startBlur();
    }
    private synchronized void  WriteBitMap(Bitmap bitmap,Bitmap bm){

        String label=DetailBean.getBookName()+DetailBean.getAuthor()+DetailBean.getSourceName();
        aCache.put(label,bitmap,6000);
        aCache.put(label+"_bac",bm,6000);
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
        String label=DetailBean.getBookName()+DetailBean.getAuthor()+DetailBean.getSourceName();
        Bitmap bitmap=aCache.getAsBitmap(label);
        Bitmap bm=aCache.getAsBitmap(label+"_bac");
        if(bitmap!=null&&bm!=null){
            binding.rivDpCoverImg.setImageBitmap(bitmap);
            binding.ivDpBgBlur.setImageBitmap(bm);
        }
        else
        mHandler.sendMessage(mHandler.obtainMessage(1));
    }

}