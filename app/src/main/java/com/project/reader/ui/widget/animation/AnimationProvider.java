package com.project.reader.ui.widget.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.widget.Scroller;

public  abstract class AnimationProvider {
    public  enum Direction{
        NONE(true),NEXT(true),PRE(true),UP(false),DOWN(false);
        public final boolean isHorizontal;

        Direction(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }
    }
    protected Bitmap mCurPageBitmap,mNextPageBitmap;
    protected float myStartX,myStartY;
    protected int myEndX,myEndY;
    protected Direction myDirection;
    protected int mScreenWidth,mScreenHeight;
    protected PointF mTouch=new PointF();//拖曳点
    private Direction direction=Direction.NONE;
    private boolean isCancel=false;
    public AnimationProvider(int width,int height){
        this.mCurPageBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        this.mNextPageBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        this.mScreenWidth=width;
        this.mScreenHeight=height;
    }
    //绘制滑动页面
    public  abstract void drawMove(Canvas canvas);
    //绘制静态页面
    public abstract  void drawStatic(Canvas canvas);
    //设置开始拖曳点
    public void setStartPoint(float x,float y){
        myStartX=x;
        myStartY=y;
    }
    public void setTouchPoint(float x,float y){
        mTouch.x=x;
        mTouch.y=y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }
    public abstract void startAnimation(Scroller scroller);

    public void changePage(){  //这个显示下一章，必须调用此方法（有点搞不懂)
        Bitmap bitmap=mCurPageBitmap;
        mCurPageBitmap=mNextPageBitmap;
        mNextPageBitmap=bitmap;
    }

    public Bitmap getmCurPageBitmap() {
        return mCurPageBitmap;
    }

    public Bitmap getmNextPageBitmap() {
        return mNextPageBitmap;
    }

}
