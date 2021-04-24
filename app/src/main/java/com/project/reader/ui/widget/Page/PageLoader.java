package com.project.reader.ui.widget.Page;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.CaseMap;
import android.text.TextPaint;
import android.widget.TextView;

import com.project.reader.Thread.ChapterThread;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookContentDB;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.App;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class PageLoader {
    public static final int STATUS_LOADING = 1;         // 正在加载
    public static final int STATUS_LOADING_CHAPTER = 2; // 正在加载目录
    public static final int STATUS_FINISH = 3;          // 加载完成
    public static final int STATUS_ERROR = 4;           // 加载错误 (一般是网络加载情况)
    public static final int STATUS_EMPTY = 5;           // 空数据
    public static final int STATUS_PARING = 6;          // 正在解析 (装载本地数据)
    public static final int STATUS_PARSE_ERROR = 7;     // 本地文件解析错误(暂未被使用)
    public static final int STATUS_CATEGORY_EMPTY = 8;  // 获取到的目录为空
    public static  final String TAG="PageLoader";
    private Paint mTipPaint;
    private int mTipSize;
    private int mTipColor;
    private  Paint TextPaint;
    private int mTextSize;//正文字体大小
    private int mTextInterval;//正文行间距
    private int mTextColor;//正文字体颜色
    private int mViewWidth,mViewHeight;
    private PageView mPageView;
    private PageMode mPageMode=PageMode.COVER;
    protected int mStatus=STATUS_LOADING;
    protected boolean isPrepareChapterList=false;
    int mBgColor=-3226980;
    private BookChapterBean bookChapterBean;
    private List<BookChapterDB> listChapter;
    private int mCurChapterPos;//这个是当前页面章节，如果是从目录点击进来那就是由点击进来的编号决定否则就是由历史阅读记录决定
    private BookChapterDB bookChapterDB;
    private ContentChapter chapterContent;
    private Paint mTitlePaint;
    private int mTitleSize;
    private int mTitleColor;
    private int mTitleInterval;
    private List<ContentPage>  CurlistPages;
    private int curPagePosition=0;
    int curPages=0;//当前页
    private ContentChapter curContentChapter,prevContentChapter,nexrContentChapter;
    public PageLoader(PageView pageView, BookChapterBean bean,BookChapterDB bookChapterDB){
        mPageView=pageView;
        this.bookChapterBean=bean;
        this.bookChapterDB=bookChapterDB;
        initPageView();
        initData();
        initPaint();
    }
    private void initData(){
        mTextSize=55;
        mTextInterval=mTextSize/2;
        mTextColor=Color.BLACK;
        mTipSize=70;
        mTipColor=Color.BLACK;
        if(bookChapterBean!=null)
            mCurChapterPos=bookChapterBean.getChapterNum();
        mTitleSize=66;
        mTitleColor=Color.BLACK;
        mTitleInterval=mTitleSize/2;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    private void initPaint(){
        TextPaint=new TextPaint();
        TextPaint.setColor(mTextColor);
        TextPaint.setTextSize(mTextSize);
        mTipPaint=new TextPaint();
        mTipPaint.setTextSize(mTipSize);
        mTipPaint.setColor(mTipColor);
        mTitlePaint=new TextPaint();
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextSize(mTitleSize);
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
        drawContent(bitmap);
        mPageView.invalidate();
    }
    private void drawBackground(Bitmap bitmap){
        Canvas canvas=new Canvas(bitmap);
        canvas.drawColor(mBgColor);
    }
    private void drawContent(Bitmap bitmap){
        Canvas canvas=new Canvas(bitmap);
        String tip="";
        if (mStatus != STATUS_FINISH) {
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "正在加载章节内容...";
                    break;
                case STATUS_LOADING_CHAPTER:
                    tip = "正在加载目录列表...";
                    break;
                case STATUS_ERROR:
                    tip = "章节内容加载失败\n" ;
                    break;
                case STATUS_EMPTY:
                    tip = "章节内容为空";
                    break;
                case STATUS_PARING:
                    tip = "正在拆分章节请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
                case STATUS_CATEGORY_EMPTY:
                    tip = "目录列表为空";
                    break;
            }
            Paint.FontMetrics fontMetrics = mTipPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTipPaint.measureText(tip);
            float pivotX = (mViewWidth - textWidth) / 2;
            float pivotY = (mViewHeight - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTipPaint);
        }
        else{
            float top;
            top = -TextPaint.getFontMetrics().top+50f;
            Paint.FontMetrics fontMetricsForTitle = mTitlePaint.getFontMetrics();
            Paint.FontMetrics fontMetrics = TextPaint.getFontMetrics();
            //设置总距离
            float interval = mTextInterval + TextPaint.getTextSize();
            float para = TextPaint.getTextSize();
            float titleInterval = mTitleInterval + mTitlePaint.getTextSize();
            float titlePara = TextPaint.getTextSize();
            float startx=0,starty=top;
            ContentPage curPage=CurlistPages.get(curPagePosition);
            int i=0;
            for(;i<curPage.titleLines;i++){
                String str=curPage.getLines().get(i);
                System.out.println(str);
                startx=(mViewWidth-mTitlePaint.measureText(str))/2;
                canvas.drawText(str,startx,starty,mTitlePaint);
                starty+=titleInterval;
            }
            for(;i<curPage.lines.size();i++){
                String str=curPage.getLines().get(i);
                canvas.drawText(str,10f,starty,TextPaint);
                starty+=interval;
            }
        }
    }
    public void refreshChapterList(){
        listChapter= LitePal.findAll(BookChapterDB.class);
        if(listChapter==null)  return ;
        isPrepareChapterList=true;
        openChapter();
    }
    public boolean next(){
        if(curPagePosition+1<CurlistPages.size()) {
            curPagePosition++;
            mPageView.drawNextPage();
        }
        return true;
    }
    public boolean prev(){
        if(curPagePosition>=1) {
            curPagePosition--;
            mPageView.drawNextPage();
        }
        else{

        }

        return  true;
    }
    public void openChapter(){
        if(!mPageView.isPrepare)
            return ;
        if(!isPrepareChapterList){
             mStatus=STATUS_LOADING_CHAPTER;
             mPageView.drawCurPage();
             return;
        }
        if(parseCurChapter()) {
            mPageView.drawCurPage();
        }
    }
    public boolean skipToNextPage(){
        return mPageView.autoNextPage();
    }
    public void skipToChapter(int pos){
        openChapter();
    }
    boolean parseCurChapter() {
        dealLoadPageList (mCurChapterPos);
        // 预加载上一页和下一页面
//        preLoadPrevChapter();
//        preLoadNextChapter();
        mStatus=STATUS_LOADING;
        return true;
    }

    /**
     * 这个函数是先查询有没有存储的章节内容存在数据库中
     * @param pos
     */
    void dealLoadPageList(int pos){
        BookContentDB bookContentDB=LitePal.find(BookContentDB.class,bookChapterDB.getBookId());
        if(bookContentDB==null){
            baseCrawler crawler= CrawlerHandler.getCrawler(bookChapterBean.getSourceClass());
            ChapterThread thread=new ChapterThread(bookChapterDB,crawler);
            mStatus=STATUS_LOADING;
            mPageView.drawCurPage();//加载内容显示出来
            thread.getChapterContent.start();
            thread.setOnThreadFinish(new ChapterThread.OnThreadFinish() {
                @Override
                public void loadChapterContent(ContentChapter contentChapter) {
                    chapterContent=contentChapter;
                       App.runOnUiThread(()->{
                           if(contentChapter.getContent()==null||contentChapter.getContent().length()==0){
                               mStatus=STATUS_ERROR;
                               mPageView.drawCurPage();
                           }else {
                               curContentChapter=contentChapter;
                               mStatus = STATUS_FINISH;//加载完成
                               LoadPageList(contentChapter);
                               mPageView.drawCurPage();
                           }
                       });
                }
            });
        }
    }
    private void LoadPageList(ContentChapter contentChapter) {
        CurlistPages = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        String title = contentChapter.getTitle();
        title = title.trim() + "\n";
        float rHeight = mViewHeight;
        ContentPage contentPage = new ContentPage();//用来记录每一页的内容
        int wordCount = 0;//每次记录这一行的词的数量是多少
        while (title.length() > 0) {
            wordCount = mTitlePaint.breakText(title, true, mViewWidth, null);
            rHeight -= mTitlePaint.getTextSize();
            rHeight -= mTitleInterval;
            contentPage.titleLines++;
            String substr = title.substring(0, wordCount);
            lines.add(substr);
            title = title.substring(wordCount);
        }
        String[] paragraphs = contentChapter.getContent().split("splitRegex");
        for (int i = 0; i < paragraphs.length; i++) {  //这是按段落分的
            String paragraph = paragraphs[i];
            while (paragraph.length() > 0) {
                rHeight -= TextPaint.getTextSize();//减去文字的高度
                rHeight -= mTextInterval;//减去行间距
                wordCount = TextPaint.breakText(paragraph, true, mViewWidth, null);
                if (rHeight <= 0) {
                    contentPage.lines = new ArrayList<>(lines);
                    CurlistPages.add(contentPage);
                    lines.clear();//清空准备下一页
                    contentPage = new ContentPage();
                    rHeight = mViewHeight;//重置为页面高度
                    continue;//重置
                }
                String subStr = paragraph.substring(0, wordCount);
                lines.add(subStr);//将这一行加入到这一页中
                paragraph = paragraph.substring(wordCount);
            }
        }
     if (lines.size() != 0) {  //最后一页可能不会占据全部屏幕
        ContentPage page = new ContentPage();
        page.setLines(lines);
        listPages.add(page);
    }
    }

}
