package com.project.reader.ui.widget.Page;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

public class PageLoader {
    public static  final String TAG="PageLoader";
    private  Paint TextPaint;
    private int mViewWidth,mViewHeight;
    private PageView mPageView;
    private PageMode mPageMode=PageMode.COVER;
    int mBgColor=0xFFF1E5C9;
    public PageLoader(PageView pageView){
        mPageView=pageView;
        initPageView();
        initData();
    }
    private void initData(){
        TextPaint=new TextPaint();
        TextPaint.setColor(Color.BLACK);
        TextPaint.setTextSize(20);
    }
    public void prepareDisplay(int w,int h){
        mViewWidth=w;
        mViewHeight=h;
        mPageView.setPageMode(mPageMode);
        mPageView.drawCurPage();  //这里是第一个页面的开始地方
    }
    private void initPageView(){
        mPageView.setPageMode(mPageMode);//这里是为页面设置翻页模式
    }
    public void drawPage(Bitmap bitmap){
        drawBackground(mPageView.getBgBitMap());
        mPageView.invalidate();
    }
    private void drawBackground(Bitmap bitmap){
        Canvas canvas=new Canvas(bitmap);
        canvas.drawColor(mBgColor);
    }
//    private void drawContent(Bitmap bitmap){
//        Canvas canvas=new Canvas(bitmap);
//    }
    public boolean next(){
        mPageView.drawNextPage();
        return true;
    }
    public boolean prev(){
        mPageView.drawNextPage();
        return  true;
    }
    public boolean skipToNextPage(){
        return mPageView.autoNextPage();
    }
}
