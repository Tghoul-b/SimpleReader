package com.project.reader.ui.widget.Page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.widget.animation.CoverAnimation;
import com.project.reader.ui.widget.animation.HorizonPageAnim;
import com.project.reader.ui.widget.animation.PageAnimation;
import com.project.reader.ui.widget.animation.ScrollAnimation;
import com.project.reader.ui.widget.animation.SimulationAnimation;
import com.project.reader.ui.widget.animation.SlideAnimation;

public class PageView extends View {
    private int mViewWidth,mViewHeight;
    public boolean isPrepare=false;
    private PageLoader mPageLoader;
    private PageAnimation mPageAni;
    private PageMode mPageMode;
    private boolean canTouch=true;//用来区别触摸是让slide滑进去还是翻页之类的操作
    private TouchListener mTouchListener;
    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove=false;
    private RectF mCenterRect = null;//唤醒菜单的区域
    private PageAnimation.OnPageChangeListener mPageAnimListener=new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return PageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            return PageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
                  mPageLoader.cancelPage();                        //这个是用来提示左滑右滑取消
        }
    };
    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        isPrepare = true;
        if (mPageLoader != null) {
            mPageLoader.prepareDisplay(w, h);
        }
    }
    void setPageMode(PageMode pageMode){
        mPageMode=pageMode;
        if(mViewHeight==0||mViewWidth==0)  return ;
        switch (pageMode){
            case COVER:
                mPageAni = new CoverAnimation(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case SCROLL:
                mPageAni=new ScrollAnimation(mViewWidth,mViewHeight,0,0,0,this,mPageAnimListener);
                break;
            case SIMULATION:
                mPageAni=new SimulationAnimation(mViewWidth,mViewHeight,this,mPageAnimListener);
                break;
            case SLIDE:
                mPageAni=new SlideAnimation(mViewWidth,mViewHeight,this,mPageAnimListener);
                break;
        }
    }

    /**
     *
     * @param darkMode
     * 显示页面
     */
    public void drawCurPage(boolean darkMode){
        mPageLoader.drawPage(getNextBitMap(),darkMode);  //然后就相当于开始drawPage
    }
    public void drawNextPage(boolean darkMode){   //这个是个很关键的
        if (mPageAni instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAni).changePage();
        }
        mPageLoader.drawPage(getNextBitMap(),darkMode);
    }
    public Bitmap getNextBitMap(){
        if(mPageAni==null)  return null;
        return mPageAni.getNextBitmap();
    }
    public Bitmap getBgBitMap(){
        if(mPageAni==null)  return null;
        return mPageAni.getNextBitmap();
    }
    public PageLoader getPageLoader(BookdetailBean bookdetailBean,BookChapterBean bean, BookChapterDB bookChapterDB){
        mPageLoader=new PageLoader(this,bookdetailBean,bean,bookChapterDB,getContext());
        if (mViewWidth != 0 || mViewHeight != 0) {
            // 初始化 PageLoader 的屏幕大小
            mPageLoader.prepareDisplay(mViewWidth, mViewHeight);
        }
        return mPageLoader;
    }
    @Override
    public void onDraw(Canvas canvas){
       mPageAni.draw(canvas);//这句话是必须的
    }
    @Override
    public void computeScroll(){
        mPageAni.scrollAnim();   //这句话是必须的
        super.computeScroll();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
      super.onTouchEvent(event);
      int x=(int)event.getX();
      int y=(int)event.getY();
      canTouch=mTouchListener.onTouch();
      if(!canTouch)
          return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                isMove = false;
                mPageAni.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }

                // 如果滑动了，则进行翻页。
                if (isMove) {
                    mPageAni.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 5, mViewHeight / 3,
                                mViewWidth * 4 / 5, mViewHeight * 2 / 3);
                    }
                    if (mCenterRect.contains(x, y)) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        return true;
                    }
                }
                mPageAni.onTouchEvent(event);
                break;
        }
        return  true;
    }
    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }
    public boolean autoNextPage() {
        startPageAnim(PageAnimation.Direction.NEXT);
        return true;
    }
    private void startPageAnim(PageAnimation.Direction direction) {
        if (direction == PageAnimation.Direction.NEXT) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAni.setStartPoint(x, y);
            //设置点击点
            mPageAni.setTouchPoint(x, y);
            //设置方向
            Boolean hasNext = hasNextPage();

            mPageAni.setDirection(direction);
            if (!hasNext) {
                return;
            }
        } else {
            int x = 0;
            int y = mViewHeight;
            //初始化动画
            mPageAni.setStartPoint(x, y);
            //设置点击点
            mPageAni.setTouchPoint(x, y);
            mPageAni.setDirection(direction);
            //设置方向方向
            Boolean hashPrev = hasPrevPage();
            if (!hashPrev) {
                return;
            }
        }
        mPageAni.startAnim();
        this.postInvalidate();
    }
    private boolean hasNextPage() {
        mTouchListener.nextPage();
        return mPageLoader.next();
    }
    private boolean hasPrevPage(){
        mTouchListener.prePage();
        return mPageLoader.prev(true);
    }

    public interface TouchListener {
        boolean onTouch();
        void center();
        void prePage();
        void nextPage();
        void cancel();
    }
}
