package com.project.reader.ui.widget.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.sql.SQLOutput;

public abstract class HorizonPageAnim extends PageAnimation{

    private static  final  String TAG="HorizonPageAnim";
    protected  Bitmap mCurBitmap;
    protected Bitmap mNextBitmap;
    protected  boolean isCancel=false;//是否取消翻页
    private  int mMoveX=0,mMoveY=0;
    private boolean isMove=false;//是否移动了
    private  boolean isNext=false;//是否翻阅到下一页,true表示下一页,false表示上一页
    private boolean noNext=false;//是否没有下一页

    public HorizonPageAnim(int w, int h, View view, OnPageChangeListener listener) {
        this(w, h, 0, 0,0, view, listener);
    }

    public HorizonPageAnim(int w, int h, int marginWidth, int marginHeight,int marginBottom,
                           View view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight,marginBottom, view,listener);
        //创建图片
        mCurBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
        mNextBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
    }

    /**
     * 转换页面
     */
    public  void changePage(){
        Bitmap bitmap=mCurBitmap;
        mCurBitmap=mNextBitmap;
        mNextBitmap=bitmap;
    }
    public abstract void drawStatic(Canvas canvas);

    public abstract void drawMove(Canvas canvas);
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x=(int)event.getX();
        int y=(int)event.getY();
        setTouchPoint(x,y);//设置为触摸点
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mMoveX=0;
                mMoveY=0;
                isMove=false;
                noNext=false;
                isRunning=false;
                isCancel=false;
                setStartPoint(x,y);
                abortAnim();
                break;
                case MotionEvent.ACTION_MOVE:
                    final int slop= ViewConfiguration.get(mView.getContext()).getScaledEdgeSlop();
                    //手移动的距离大于这个slop才开始移动组件
                    if(!isMove){
                        isMove=Math.abs(mStartX-x)>slop||Math.abs(mStartY-x)>slop;
                    }
                    if(isMove){
                        if(mMoveX==0&&mMoveY==0){
                            if(x-mStartX>0){
                                isNext=false;
                                boolean hasPrev=mListener.hasPrev();
                                setDirection(Direction.PRE);
                                if(!hasPrev){
                                    noNext=true;
                                    return true;
                                }
                            }
                            else{
                                isNext=true;
                                boolean hasNext=mListener.hasNext();
                                setDirection(Direction.NEXT);
                                if(!hasNext){
                                    noNext=true;
                                    return true;
                                }
                            }
                        }
                        else{
                            if(isNext){
                                if(x-mMoveX>0)
                                    isCancel=true;
                                else{
                                    isCancel=false;
                                }
                            }
                            else{
                                if(x-mMoveX<0)
                                    isCancel=true;
                                else
                                     isCancel=false;
                            }
                        }
                    }
                    mMoveX=x;
                    mMoveY=y;
                    isRunning=true;
                    mView.invalidate();
                    break;
            case MotionEvent.ACTION_UP:
                if(!isMove) {
                    if (x < mScreenWidth / 2)
                        isNext = false;
                    else
                        isNext = true;
                    if (isNext) {
                        boolean hasNext = mListener.hasNext();
                        setDirection(Direction.NEXT);
                        if (!hasNext) return true;

                    } else {
                        boolean hasPrev = mListener.hasPrev();
                        setDirection(Direction.PRE);
                        if (!hasPrev)
                            return true;
                    }
                }
                if(isCancel)
                    mListener.pageCancel();
                if(!noNext){
                    startAnim();
                    mView.invalidate();
                }
                break;
        }
        return true;
    }


    @Override
    public void draw(Canvas canvas) {
        if(isRunning)
            drawMove(canvas);
        else{
            if(isCancel){
                mNextBitmap=mCurBitmap.copy(Bitmap.Config.RGB_565,true);
            }
            drawStatic(canvas);
        }
    }

    @Override
    public void scrollAnim() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            setTouchPoint(x, y);

            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y){
                isRunning = false;
            }
            mView.postInvalidate();
        }
    }
    @Override
    public void abortAnim() {
        if(!mScroller.isFinished()){
            mScroller.abortAnimation();
            isRunning=false;
            setTouchPoint(mScroller.getFinalX(),mScroller.getFinalY());
            mView.postInvalidate();
        }
    }
    @Override
    public Bitmap getBgBitmap() {
        return mNextBitmap;
    }

    @Override
    public Bitmap getNextBitmap() {
        return mNextBitmap;
    }
}
