package com.project.reader.ui.widget.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.viewpager.widget.ViewPager;

//翻页动画抽象类
public abstract class PageAnimation {
    static final int animationSpeed = 300;
    protected View mView;//正在使用的代码
    protected Scroller mScroller;//滑动装置
    protected OnPageChangeListener mListener;
    protected Direction mDirection= Direction.NONE;
    protected boolean isRunning=false;//正在运行
    protected  int mScreenWidth,mScreenHeight;//屏幕的尺寸
    protected int mMarginWidth,mMarginHeight;//屏幕的间距
    protected  int mViewWidth,mViewHeight;//视图的尺寸
    protected float mStartX,mStartY;//起始点
    protected float mTouchX,mTouchY;//触摸点
    protected float mLastX,mLastY;//上一个触碰点
    public PageAnimation(int w, int h, View view, OnPageChangeListener listener){
        this(w, h, 0, 0,0, view,listener);
    }
    public PageAnimation(int w, int h, int marginWidth, int marginHeight, int marginBottom,View view, OnPageChangeListener listener){
        mScreenWidth = w;
        mScreenHeight = h;

        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        mViewWidth = mScreenWidth - mMarginWidth * 2;
        mViewHeight = mScreenHeight - marginBottom;

        mView = view;
        mListener = listener;

        mScroller = new Scroller(mView.getContext(), new LinearInterpolator());
    }
    public void setStartPoint(float x,float y){
        mStartX = x;
        mStartY = y;

        mLastX = mStartX;
        mLastY = mStartY;
    }

    public void setTouchPoint(float x,float y){
        mLastX = mTouchX;
        mLastY = mTouchY;

        mTouchX = x;
        mTouchY = y;
    }

    public boolean isRunning(){
        return isRunning;
    }
    public void startAnim(){
        if (isRunning){
            return;
        }
        isRunning = true;
    }

    public void setDirection(Direction direction){
        mDirection = direction;
    }

    public Direction getDirection(){
        return mDirection;
    }

    public void clear(){
        mView = null;
    }
    public  abstract  boolean onTouchEvent(MotionEvent event);

    public abstract void draw(Canvas canvas);//画图事件

    public abstract  void scrollAnim();//滚动动画
    public abstract void abortAnim();//暂停动画

    public abstract Bitmap getBgBitmap();//得到背景图

    public abstract Bitmap getNextBitmap();//得到内容显示版面(没搞懂)

    public enum Direction {
        NONE(true),NEXT(true), PRE(true), UP(false), DOWN(false);

        public final boolean isHorizontal;

        Direction(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }
    }

    public interface OnPageChangeListener {
        boolean hasPrev();
        boolean hasNext();
        void pageCancel();
    }
}
