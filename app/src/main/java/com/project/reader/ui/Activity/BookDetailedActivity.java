package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.reader.R;
import com.example.reader.databinding.ActivityBookDetailedBinding;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.project.reader.Config;
import com.project.reader.entity.BookCaseDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.BrowserBookBean;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.ReplyDetailBean;
import com.project.reader.ui.Adapter.CommentExpandAdapter;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.DataHandler;
import com.project.reader.ui.util.Engine.SearchEngine;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.Themetools;
import com.project.reader.ui.widget.utils.StatusBarUtil;


import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookDetailedActivity extends AppCompatActivity {
    private List<BookdetailBean> aBooks;
    private BookdetailBean DetailBean;
    private SearchEngine searchEngine;
    private ActivityBookDetailedBinding binding;
    private  ACache aCache;
    private CommentExpandAdapter adapter;
    private boolean isCollected=false;
    private LoadDataSuccess loadDataSuccess;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    InitDrawable();
                    break;
                case 2:
                    if(!isCollected){
                        binding.layoutBottom.bookDetailTvAdd.setText("添加书籍");
                        binding.layoutBottom.bookDetailTvOpen.setText("开始阅读");
                    }else{
                        binding.layoutBottom.bookDetailTvAdd.setText("移除书籍");
                        binding.layoutBottom.bookDetailTvOpen.setText("继续阅读");
                    }
                    break;
            }
        }
    };

    public void setLoadDataSuccess(LoadDataSuccess loadDataSuccess) {
        this.loadDataSuccess = loadDataSuccess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        aCache=ACache.get(this);
        aBooks = (ArrayList<BookdetailBean>) getIntent().getSerializableExtra("bookDetails");
        ACache aCache=ACache.get(this);
        searchEngine=new SearchEngine(this);
        Themetools.changeIconTheme(this);
        initDetail(0);
        initClick();
    }
    protected void bindView() {
        binding= ActivityBookDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            StatusBarUtil.setLightStatusBar(this,true,true);//状态栏设置成黑色
        }
    }
    private void initClick(){
        binding.bookChapterArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),bookChapterListActivity.class);
                intent.putExtra("bookInfo",DetailBean);
                startActivityForResult(intent, Config.CHAPTER_PAGE_REQ);
            }
        });
        binding.writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),CommentActivity.class);
                intent.putExtra("bookInfo",DetailBean);
                startActivity(intent);
            }
        });
        binding.layoutBottom.flAddBookcase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendMessage(mHandler.obtainMessage(2));
                if(isCollected){
                    BookCaseDB bookCaseDB=new BookCaseDB(DetailBean);
                    LitePal.deleteAll(BookCaseDB.class,"bookId = ?",Long.toString(bookCaseDB.getBookId()));
                    isCollected=false;
                    DetailBean.setLastReadPosition(0);//重新归0
                }
                else{
                    BookCaseDB bookCaseDB=new BookCaseDB(DetailBean);
                    baseCrawler crawler= CrawlerHandler.getCrawler(DetailBean.getSourceClass());
                    new Thread(()->{
                        crawler.getChapterAndTime(DetailBean.getInfoUrl(),DetailBean);
                        BookCaseDB bookCaseDB1=new BookCaseDB(DetailBean);
                        bookCaseDB1.save();
                    }).start();
                    isCollected=true;
                }
            }
        });
        binding.layoutBottom.flOpenBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goReadActivity();

            }
        });
    }
    private void initDetail(int index){
        DetailBean=aBooks.get(index);
        List<BookCaseDB> list=LitePal.findAll(BookCaseDB.class);
        for(BookCaseDB bookCaseDB:list){
            System.out.println(bookCaseDB);
        }
        List<BookCaseDB> bookCaseDBList=LitePal.where("bookId = ?",Long.toString(DetailBean.hashCode())).find(BookCaseDB.class);
        if(bookCaseDBList==null||bookCaseDBList.size()==0)
            isCollected=false;
        else {
            DetailBean.setLastReadPosition(bookCaseDBList.get(0).getLastChapterNum());
            isCollected = true;
        }

        InitWidget();
        if(DetailBean.NeedInfo()){   //这个可能是前一个界面还没有加载完全信息,用户就点击了。
            searchEngine.initOtherinfo(DetailBean.getSourceClass(),DetailBean,success -> {
                if(success){
                   InitOtherInfo();
                }
            });
        }
        else{
            InitOtherInfo();
        }
        DataHandler.getCommentData(DetailBean.hashCode());
        binding.bookDetailPageLvComment.setGroupIndicator(null);
       DataHandler.setCallback(new DataHandler.CURDCallback() {
           @Override
           public void getDataCallback(List<CommentDetailBean> list) {
               App.runOnUiThread(()->{
                   CommentExpandAdapter commentExpandAdapter=new CommentExpandAdapter(BookDetailedActivity.this,
                           list);
                   binding.bookDetailPageLvComment.setAdapter(commentExpandAdapter);
               });
           }

           @Override
           public void loadCurId(int curId) {

           }
       });

    }
    private void InitDrawable(){
        Glide.with(this).load(DetailBean.getImgUrl()).placeholder(R.mipmap.ic_default).error(R.mipmap.ic_default)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap bitmap= BaseApi.drawableToBitamp(resource);
                        Bitmap bm=bitmap.copy(Bitmap.Config.ARGB_8888,true);
                        bm=BaseApi.blur(getApplicationContext(),bm);
                        binding.ivDpBgBlur.setImageBitmap(bm);
                        return false;
                    }
                }).into(binding.rivDpCoverImg);
    }
    private synchronized void  WriteBitMap(Bitmap bitmap,Bitmap bm){

        String label=DetailBean.getBookName()+DetailBean.getAuthor()+DetailBean.getSourceName();
        aCache.put(label,bitmap,6000);
        aCache.put(label+"_bac",bm,6000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case Config.CHAPTER_PAGE_REQ:
                    int chapterNum=data.getIntExtra("chapter_page",0);
                    DetailBean.setLastReadPosition(chapterNum);
                    goReadActivity();
                    break;
                case Config.READ_REQ:
                    isCollected=data.getBooleanExtra("isCollected",false);
                    int lastChapter=data.getIntExtra("lastChapter",1);
                    DetailBean.setLastReadPosition(lastChapter);
                    mHandler.sendMessage(mHandler.obtainMessage(2));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void goReadActivity(){
        Intent intent=new Intent(BookDetailedActivity.this,ReadActivity.class);
        if(DetailBean.getLastReadPosition()<=0)
            DetailBean.setLastReadPosition(1);
        intent.putExtra("BOOK",DetailBean);
        startActivityForResult(intent,Config.READ_REQ);
    }
    private void initContent(){
        binding.tvBookName.setText(DetailBean.getBookName());
        binding.tvBookAuthor.setText("作者: "+DetailBean.getAuthor());
        binding.bookStatus.setText("状态: "+DetailBean.getStatus());
        binding.tvBookType.setText("类型: "+DetailBean.getNovelType());
        binding.updateTime.setText("最近更新: "+DetailBean.getUpdate_time());
        binding.lastChapter.setText(DetailBean.getLastChapter());
        ExpandableTextView expTv1 = (ExpandableTextView) findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);
        expTv1.setText(DetailBean.getDesc());
    }
    private  void InitWidget(){
        initContent();
        float len1=DetailBean.getBookName().length();
        float len2=DetailBean.getAuthor().length();
        float point=(len1+len2)/(len1*len2)*5;
        if(point>5.0f)
               point=5.0f;
        String result = String.format("%.1f",point);//保留一位小数
        point=Float.parseFloat(result);
        binding.starView.setRating(point);
        binding.pointText.setText(point+"分");
        mHandler.sendMessage(mHandler.obtainMessage(2));
    }
    private void InitOtherInfo(){
        initContent();
        mHandler.sendMessage(mHandler.obtainMessage(1));
        binding.bookStatus.setText("状态: "+DetailBean.getStatus());
        binding.tvBookType.setText("类型: "+DetailBean.getNovelType());
        binding.updateTime.setText("最近更新: "+DetailBean.getUpdate_time());
        binding.lastChapter.setText(DetailBean.getLastChapter());
        BrowserBookBean browserBookBean=new BrowserBookBean(DetailBean);
        List<BrowserBookBean> listTmp=LitePal.where("bookId=?",Long.toString(browserBookBean.getBookId()))
                .find(BrowserBookBean.class);
        if(listTmp==null||listTmp.size()==0){
            browserBookBean.save();
        }
        else{
            browserBookBean.updateAll("bookId=?",Long.toString(browserBookBean.getBookId()));
        }
    }
    public interface LoadDataSuccess{
        public void SuccessCallback(List<CommentDetailBean> list);
    }

}