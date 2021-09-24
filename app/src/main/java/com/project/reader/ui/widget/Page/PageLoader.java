package com.project.reader.ui.widget.Page;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.project.reader.Config;
import com.project.reader.Thread.ChapterThread;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookContentDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Adapter.BookChapterAdapter;
import com.project.reader.ui.Adapter.CommonAdapter;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.Setting;
import com.project.reader.ui.util.ToastyUtils;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.widget.View.BatteryView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private PageMode mPageMode;
    protected int mStatus;
    protected boolean isPrepareChapterList=false;
    int mBgColor;
    private BookChapterBean bookChapterBean;
    private List<BookChapterDB> listChapter;
    private BookChapterDB bookChapterDB;
    private ContentChapter chapterContent;
    private Paint mTitlePaint;
    private int mTitleSize;
    private int mTitleColor;
    private int mTitleInterval;
    private List<ContentPage>  CurlistPages;
    private int curPagePosition;
    int curChapterNumber;
    private float marginTitle,marginSmallTitle;//用来标题上面留白
    private ContentChapter curContentChapter,prevContentChapter,nextContentChapter;
    private  baseCrawler crawler;
    private int marginTop,marginBottom,marginWidth;
    private int mDisplayHeight,mDisplayWidth;
    private Paint mSmallTitlePaint;
    private int mSmallTitleSize;
    private Typeface mTypeFace;//字体类型
    private int lastChapterNum;//记录上一章的num,用于取消滑动时使用
    private int lastPageNum;//记录上一页的num,用于取消滑动时使用
    private Context mContext;//上下文信息
    private BatteryView batteryView;
    private TextView batteryLevel;//电池电量
    private  BookChapterAdapter mAdapter;
    private int isPositive=1;//侧滑栏中是否是正序
    private Setting mSetting;//定义一个设置类
    private BookdetailBean bookdetailBean;
    public PageLoader(PageView pageView,BookdetailBean bookdetailBean, BookChapterBean bean,BookChapterDB bookChapterDB,Context context){
        mPageView=pageView;
        this.bookChapterBean=bean;
        this.bookChapterDB=bookChapterDB;
        this.mContext=context;
        this.bookdetailBean=bookdetailBean;
        initData();
        initPageView();
        initPaint();
        curContentChapter=new ContentChapter();
        prevContentChapter=new ContentChapter();
        nextContentChapter=new ContentChapter();
    }
    private void initData(){
        mSetting=new Setting(mContext);
        mAdapter=new BookChapterAdapter(mContext,R.layout.bookchapteradapter);
        batteryView=((Activity)mContext).findViewById(R.id.Battery);
        batteryLevel=((Activity)mContext).findViewById(R.id.BatteryLevel);
        mBgColor=mContext.getResources().getColor(mSetting.getBac_colorIds()[mSetting.getReadStyle()]);;
        curPagePosition=0;
        int mode=mSetting.getPageMode();
        mPageMode=PageMode.intToEnum(mode);
        mTextSize=mSetting.getReadTextSize();//默认值
        mTextInterval=Math.min(mTextSize/2,30);
        mTextColor=mContext.getResources().getColor(mSetting.getBac_color_text_colors()[mSetting.getReadStyle()]);//默认值(这些值马上需要一个类来存储)
        mTipSize=70;//默认值
        marginTitle=Config.READ_MARGIN_BIG_TITLE;
        marginSmallTitle=Config.READ_MARGIN_SMALL_TITLE;
        mTipColor=App.getApplication().getResources().getColor(R.color.read_text_color);
        mTitleSize=mSetting.getReadTitleSize();//默认值
        mTitleColor=mContext.getResources().getColor(mSetting.getBac_color_text_colors()[mSetting.getReadStyle()]);
        mTitleInterval=mTitleSize/2;
        curChapterNumber=bookChapterBean.getChapterNum();
        crawler= CrawlerHandler.getCrawler(bookChapterBean.getSourceClass());
        marginTop= Config.READ_MARGIN_TOP;
        marginBottom=Config.READ_MARGIN_BOTTOM;
        marginWidth=Config.READ_MARGIN_WIDTH;
        mSmallTitleSize=40;//默认值
        mStatus=STATUS_LOADING;
        int nightMode=mSetting.getNightMode();
        if(nightMode==1){
            mBgColor=mContext.getResources().getColor(R.color.read_appbar_bg);
            mTextColor=Color.WHITE;
            mTitleColor=Color.WHITE;
        }
    }

    public int getCurChapterNumber() {
        return curChapterNumber;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }
    public void cancelPage(){
        if(curChapterNumber>lastChapterNum){  //当前已经加载了下一章

            nextContentChapter=new ContentChapter(curContentChapter);
            curContentChapter=new ContentChapter(prevContentChapter);
            LoadPageList(curContentChapter);
            preLoadPrevChapter(true);
            curPagePosition=CurlistPages.size()-1;
        }
        else if(curChapterNumber<lastChapterNum){
            prevContentChapter=new ContentChapter(curContentChapter);
            curContentChapter=new ContentChapter(nextContentChapter);
            preLoadNextChapter(true);
            LoadPageList(curContentChapter);
            curPagePosition=0;
        }
        if(curPagePosition>lastPageNum){
            curPagePosition--;
        }
        else if(curPagePosition<lastPageNum){
            curPagePosition++;
        }
    }
    public void initPaint(){
        mTypeFace=BaseApi.loadTypeface(mContext);
        TextPaint=new TextPaint();
        TextPaint.setColor(mTextColor);
        TextPaint.setTextSize(mTextSize);
        TextPaint.setTypeface(mTypeFace);
        mTipPaint=new TextPaint();
        mTipPaint.setTextSize(mTipSize);
        mTipPaint.setColor(mTipColor);
        mTitlePaint=new TextPaint();
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.create(mTypeFace,Typeface.BOLD));
        mSmallTitlePaint=new Paint();
        mSmallTitlePaint.setColor(mTextColor);
        mSmallTitlePaint.setTextSize(mSmallTitleSize);
        mSmallTitlePaint.setTypeface(mTypeFace);
    }
    public void prepareDisplay(int w,int h){
        mViewWidth=w;
        mViewHeight=h;
        mDisplayHeight=mViewHeight-marginTop-marginBottom;
        mDisplayWidth=mViewWidth-2*marginWidth;
        mPageView.setPageMode(mPageMode);
        mPageView.drawCurPage(false);  //这里是第一个页面的开始地方
    }
    private void initPageView(){
        int initBattery=BaseApi.getBatteryPower();
        batteryLevel.setText(initBattery+"%");
        batteryView.setPower(initBattery);
        mPageView.setPageMode(mPageMode);//这里是为页面设置翻页模式
    }
    public void drawPage(Bitmap bitmap,boolean darkMode){
        drawBackground(mPageView.getBgBitMap(),darkMode);//显示背景
        drawContent(bitmap);//显示内容
        mPageView.invalidate();//更新页面
    }

    private void drawBackground(Bitmap bitmap,boolean darkMode){
        Canvas canvas=new Canvas(bitmap);
        canvas.drawColor(mBgColor);//显示背景颜色
        if(darkMode)  //如果是夜间模式
            canvas.drawColor(App.getApplication().getResources().getColor(R.color.read_appbar_bg));  //背景显示成黑色
    }
    private void drawContent(Bitmap bitmap){
        updateTime();//更新左下角的时间
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
            SetTextBar();
            float top;
            top = -TextPaint.getFontMetrics().top;
            canvas.drawText(curContentChapter.getTitle(),marginWidth,top,mSmallTitlePaint);
            top=top+marginTop+marginSmallTitle;
            Paint.FontMetrics fontMetricsForTitle = mTitlePaint.getFontMetrics();
            Paint.FontMetrics fontMetrics = TextPaint.getFontMetrics();
            //设置总距离
            float interval = mTextInterval + TextPaint.getTextSize();
            float titleInterval = mTitleInterval + mTitlePaint.getTextSize();
            float startx=marginWidth,starty=top;
            ContentPage curPage=CurlistPages.get(curPagePosition);
            if(curPage.titleLines!=0)
                starty+=marginTitle;
            int i=0;
            for(;i<curPage.titleLines;i++){
                String str=curPage.getLines().get(i);
                startx=(mDisplayWidth-mTitlePaint.measureText(str))/2;
                canvas.drawText(str,startx,starty,mTitlePaint);
                starty+=titleInterval;
            }
            startx=marginWidth;
            for(;i<curPage.lines.size();i++){
                String str=curPage.getLines().get(i);
                boolean flag=false;
                if(str.endsWith("<p>")) {
                    flag=true;
                    int len=str.length();
                    str=str.substring(0,len-3);
                }
                canvas.drawText(str,startx,starty,TextPaint);
                starty+=interval;
                if(flag)
                    starty+=mTextInterval;
            }
            updatePageProcess();
        }
    }
    private void SetTextBar(){
        Toolbar toolbar=((Activity)mContext).findViewById(R.id.read_book_name_bar);
        toolbar.setTitle(bookChapterBean.getBookName());
        TextView tvChapterName=((Activity)mContext).findViewById(R.id.tv_chapter_title_top);
        tvChapterName.setText(listChapter.get(curChapterNumber-1).getChapterName());
        TextView tvChapterUrl=((Activity)mContext).findViewById(R.id.tv_chapter_url);
        tvChapterUrl.setText(bookChapterBean.getUrl());
        SeekBar seekBar=((Activity)mContext).findViewById(R.id.tv_read_page_process);
        float f=(curPagePosition+1)*1.0f/CurlistPages.size()*100;
        seekBar.setProgress((int)f);
    }
    public void refreshChapterList(){
        listChapter= LitePal.where("bookId = ?",Long.toString(bookChapterDB.getBookId())).find(BookChapterDB.class);
        if(listChapter==null||listChapter.size()==0) {
            baseCrawler crawler=CrawlerHandler.getCrawler(bookChapterBean.getSourceClass());
            new Thread(()->{
                crawler.getChapterList(bookdetailBean,updated -> {
                    App.runOnUiThread(()->{
                        listChapter= LitePal.where("bookId = ?",Long.toString(bookChapterDB.getBookId())).find(BookChapterDB.class);
                        initDrawerLayout();
                        isPrepareChapterList=true;
                        openChapter();
                    });
                });
            }).start();
        }
        else {
            initDrawerLayout();
            isPrepareChapterList = true;
            openChapter();
        }
    }
    private void printTest(){
        if(prevContentChapter!=null)
        System.out.println("preChapter is :"+prevContentChapter.getTitle());
        if(curContentChapter!=null)
        System.out.println("curChapter is :"+curContentChapter.getTitle());
        if(nextContentChapter!=null)
        System.out.println("nextChapter is :"+nextContentChapter.getTitle());
    }
    public boolean next(){
        if(!isPrepareChapterList)
             return false;
        lastPageNum=curPagePosition;
        if(curPagePosition+1<CurlistPages.size()) {
            curPagePosition++;
            mPageView.drawNextPage(false);
        }
        else{
            if(curChapterNumber==listChapter.size()&&curPagePosition==CurlistPages.size()-1)  //当前章节号如果已经是list的size了
            {
                ToastyUtils.makeTextWarning(App.getApplication(),"已经是最后一页了",Toast.LENGTH_SHORT);
                return false;
            }
            else{
                lastChapterNum=curChapterNumber;
                prevContentChapter=new ContentChapter(curContentChapter);//这一章变成上一章
                curContentChapter=new ContentChapter(nextContentChapter);//下一章变成这一章
                preLoadNextChapter(true);//预加载下一章
                LoadPageList(curContentChapter);
                curPagePosition=0;
                mPageView.drawNextPage(false);
            }
        }
        //printTest();
        return true;
    }
    public boolean prev(boolean isFocus){
        if(!isPrepareChapterList)
            return false;
        lastPageNum=curPagePosition;
        if(curPagePosition>=1) {
            curPagePosition--;
            mPageView.drawNextPage(false);
        }
        else{
            if(curChapterNumber==1&&curPagePosition==0){
                ToastyUtils.makeTextWarning(App.getApplication(),"已经是第一页了",Toast.LENGTH_SHORT);
                return false;
            }
            else{
                lastChapterNum=curChapterNumber;
                nextContentChapter=new ContentChapter(curContentChapter);
                curContentChapter=new ContentChapter(prevContentChapter);
                preLoadPrevChapter(true);//预加载上一章
                LoadPageList(curContentChapter);
                if(isFocus)
                    curPagePosition=CurlistPages.size()-1;
                else
                    curPagePosition=0;
                mPageView.drawNextPage(false);
            }
        }

        return  true;
    }
    public void openChapter(){
        if(!mPageView.isPrepare)
            return ;
        if(!isPrepareChapterList){
             mStatus=STATUS_LOADING_CHAPTER;
             mPageView.drawCurPage(false);
             return;
        }
        if(parseCurChapter()) {
            mPageView.drawCurPage(false);
        }
    }
    public boolean skipToNextPage(){
        return mPageView.autoNextPage();
    }
    public void skipToChapter(int pos){
        openChapter();
    }
    boolean parseCurChapter() {
        dealLoadPageList (curChapterNumber);
        // 预加载上一页和下一页面
        preLoadPrevChapter(false);
        preLoadNextChapter(false);
        if(mStatus!=STATUS_FINISH)//    如果此时不是成功
            mStatus=STATUS_LOADING;//此时还在加载
        return true;
    }
    void preLoadPrevChapter(boolean isPrev){
        if(isPrev)  curChapterNumber--;
        //章节号为1的下标为0
        if(curChapterNumber>1)  {
            BookChapterDB  prebookChapterDB=listChapter.get(curChapterNumber-2);
            BookContentDB bookContentDB=new BookContentDB();
            bookContentDB.setBookId(Objects.hash(bookChapterDB.getBookId(),curChapterNumber-1));
            List<BookContentDB> tmpBookContent=LitePal.where("bookId = ? ",Long.toString(bookContentDB.getBookId())).find(BookContentDB.class);
            if(tmpBookContent==null||tmpBookContent.size()==0) {
                ChapterThread preChapterThread = new ChapterThread(prebookChapterDB, crawler);//这一章之前有章节才预加载之前的章节
                preChapterThread.getChapterContent.start();
                preChapterThread.setOnThreadFinish(new ChapterThread.OnThreadFinish() {
                    @Override
                    public void loadChapterContent(ContentChapter contentChapter) {
                        if(contentChapter!=null)
                             prevContentChapter = new ContentChapter(contentChapter);
                        else{
                            prevContentChapter=new ContentChapter();
                        }
                    }
                });
            }
            else{
                bookContentDB=tmpBookContent.get(0);
                prevContentChapter.setTitle(prebookChapterDB.getChapterName()) ;
                prevContentChapter.setContent(bookContentDB.getContent());
                mStatus=STATUS_FINISH;
            }
        }
        else{
            prevContentChapter=new ContentChapter();
        }
    }
    void preLoadNextChapter(boolean isNext){
        if(isNext)  curChapterNumber++;
        if(curChapterNumber+1<=listChapter.size())  {
            BookChapterDB  NextChapterDB=listChapter.get(curChapterNumber);
            BookContentDB bookContentDB=new BookContentDB();
            bookContentDB.setBookId(Objects.hash(bookChapterDB.getBookId(),curChapterNumber));
            List<BookContentDB> tmpBookContent=LitePal.where("bookId = ? ",Long.toString(bookContentDB.getBookId())).find(BookContentDB.class);
            if(tmpBookContent==null||tmpBookContent.size()==0) {
                ChapterThread NextChapterThread = new ChapterThread(NextChapterDB, crawler);//这一章之前有章节才预加载之前的章节
                NextChapterThread.getChapterContent.start();
                NextChapterThread.setOnThreadFinish(new ChapterThread.OnThreadFinish() {
                    @Override
                    public void loadChapterContent(ContentChapter contentChapter) {
                        if(contentChapter!=null)
                            nextContentChapter = new ContentChapter(contentChapter);
                        else
                            nextContentChapter=new ContentChapter();
                    }
                });
            }
            else{
                bookContentDB=tmpBookContent.get(0);
                nextContentChapter.setTitle(NextChapterDB.getChapterName()) ;
                nextContentChapter.setContent(bookContentDB.getContent());
                mStatus=STATUS_FINISH;
            }
        }
        else{
            nextContentChapter=new ContentChapter();
        }
    }
    /**
     * 这个函数是先查询有没有存储的章节内容存在数据库中
     * @param pos
     */
    void dealLoadPageList(int pos){
        bookChapterDB=listChapter.get(pos-1);
        BookContentDB bookContentDB=new BookContentDB();
        //设置章节的固定Id
        bookContentDB.setBookId(Objects.hash(bookChapterDB.getBookId(),pos));
        List<BookContentDB> tmpBookContent=LitePal.where("bookId = ? ",Long.toString(bookContentDB.getBookId())).find(BookContentDB.class); //先查询当前书籍章节是否在数据库中存储
        if(tmpBookContent==null||tmpBookContent.size()==0){   //如果不存在该内容
            ChapterThread thread=new ChapterThread(bookChapterDB,crawler);
            mStatus=STATUS_LOADING;
            mPageView.drawCurPage(false);//加载内容显示出来，显示提示文字
            thread.getChapterContent.start();//开启搜索线程
            BookContentDB finalBookContentDB = new BookContentDB();
            finalBookContentDB.setBookId(Objects.hash(bookChapterDB.getBookId(),pos));
            thread.setOnThreadFinish(new ChapterThread.OnThreadFinish() {
                @Override
                public void loadChapterContent(ContentChapter contentChapter) {
                    chapterContent=contentChapter;
                       App.runOnUiThread(()->{   //在主线程中渲染页面
                           if(contentChapter==null||contentChapter.getContent()==null||contentChapter.getContent().length()==0){
                               mStatus=STATUS_ERROR;
                               mPageView.drawCurPage(false);
                           }else {
                               curContentChapter=contentChapter;
                               mStatus = STATUS_FINISH;//加载完成
                               LoadPageList(contentChapter);//将加载好的内容分页
                               mPageView.drawCurPage(false);//渲染页面
                               finalBookContentDB.setContent(curContentChapter.getContent());
                               finalBookContentDB.save();//存储到数据库中
                           }
                       });
                }
            });
        }else{
            bookContentDB=tmpBookContent.get(0);//直接从数据库中读取
            curContentChapter.setTitle(bookChapterDB.getChapterName()) ;
            curContentChapter.setContent(bookContentDB.getContent());
            LoadPageList(curContentChapter);
            mStatus=STATUS_FINISH;//状态位加载完成
            mPageView.drawCurPage(false);//渲染页面
        }
    }
    private void LoadPageList(ContentChapter contentChapter) {
        CurlistPages = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        if(curContentChapter==null||TextUtils.isEmpty(curContentChapter.getTitle())||TextUtils.isEmpty(curContentChapter.getContent())) {
            mStatus=STATUS_ERROR;
            return;
        }
        String title = contentChapter.getTitle();
        title = title.trim() ;
        float rHeight = mDisplayHeight-marginTitle-marginSmallTitle;
        ContentPage contentPage = new ContentPage();//用来记录每一页的内容
        int wordCount = 0;//每次记录这一行的词的数量是多少
        while (title.length() > 0) {
            wordCount = mTitlePaint.breakText(title, true, mDisplayWidth, null);
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
                wordCount = TextPaint.breakText(paragraph, true, mDisplayWidth, null);
                if (rHeight <=0) {
                    contentPage.lines = new ArrayList<>(lines);
                    CurlistPages.add(contentPage);
                    lines.clear();//清空准备下一页
                    contentPage = new ContentPage();
                    rHeight = mDisplayHeight-marginSmallTitle;//重置为页面高度
                    continue;//重置
                }
                String subStr = paragraph.substring(0, wordCount);

                paragraph = paragraph.substring(wordCount);
                if(paragraph==null||paragraph.length()==0){
                    subStr+="<p>";//作为段落结束的标志
                }
                lines.add(subStr);//将这一行加入到这一页中
            }  //这是相当于把这一段加入了lines中
            rHeight-=mTextInterval;//扩大段落之间的间距
        }
     if (lines.size() != 0) {  //最后一页可能不会占据全部屏幕
        ContentPage page = new ContentPage();
        page.setLines(lines);
        CurlistPages.add(page);
    }
    }
    public void updateBattery(int level){
        batteryView.setPower(level);
        batteryLevel.setText(level+"%");
    }
    public void updateTime(){
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int hour = t.hour;
        int minute=t.minute;
        TextView textView=((Activity)mContext).findViewById(R.id.system_time);
        String s=String.format("%02d:%02d",hour,minute);
        textView.setText(s);
    }
    public void updatePageProcess(){
        TextView textView=((Activity)mContext).findViewById(R.id.page_process);
        String s=String.format("%d/%d页",curPagePosition+1,CurlistPages.size());
        textView.setText(s);
    }
    public void skipToPreChapter(){
        curPagePosition=0;
        prev(false);
    }
    public void skipToNextChapter(){
        curPagePosition=CurlistPages.size()-1;
        next();
    }
    private void initDrawerLayout(){  //自定义侧滑栏
        int position=bookChapterBean.getChapterNum();
        String name=listChapter.get(position-1).getChapterName();
        bookChapterBean.setChapterName(name);
        bookChapterDB.setChapterName(name);
        LinearLayout linearLayout=((Activity)mContext).findViewById(R.id.main_slide_layout);
        TextView tv_title=linearLayout.findViewById(R.id.tv_slide_bookName);
        tv_title.setText(bookChapterBean.getBookName());//书名
        ListView listView=linearLayout.findViewById(R.id.tv_slide_bookChapterList);
        List<BookChapterBean> list=new ArrayList<>();
        int cnt=1;
        for(BookChapterDB bookChapterDB:listChapter){
            BookChapterBean bookChapterBean=new BookChapterBean();
            bookChapterBean.setChapterName(bookChapterDB.getChapterName());
            bookChapterBean.setChapterNum(cnt);
            list.add(bookChapterBean);
            cnt++;
        }
        mAdapter.addAll(list);
        listView.setAdapter(mAdapter);
        mAdapter.setClickListener(new CommonAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BookChapterBean bean) {
                curChapterNumber=bean.getChapterNum();
                curPagePosition=0;//每次跳到第一页
                parseCurChapter();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }
    public void changeTextSize(int dif){
        mTextSize+=dif;
        mTitleSize+=dif;
        initPaint();
        LoadPageList(curContentChapter);
        mPageView.drawCurPage(false);
        mSetting.setReadTextSize(mTextSize);
        mSetting.setReadTitleSize(mTitleSize);
        mSetting.saveAllConfig();//保存所有的设置
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void changeListChapterOrder(){
        isPositive^=1;
        mAdapter.reverseAll();
        ImageView imageView=((Activity)mContext).findViewById(R.id.main_slide_layout).findViewById(R.id.tv_slide_orderChange);
        if(isPositive==1){
            imageView.setImageDrawable(mContext.
                    getResources().getDrawable(R.drawable.ic_positiveseq));
        }
        else{
            imageView.setImageDrawable(mContext.
                    getResources().getDrawable(R.drawable.ic_reverseseq));
        }
    }
    public void changeFontFamily(String s){
        mTypeFace=BaseApi.loadTypeface(mContext,s);
        initPaint();
        initPaint();
        mPageView.drawCurPage(false);
    }
    public void changeReadStyle(int bacColorId, int textColorId){
       mBgColor=mContext.getResources().getColor(bacColorId);
       mTextColor=mContext.getResources().getColor(textColorId);
       mTitleColor=mContext.getResources().getColor(textColorId);
       initPaint();
       mPageView.drawCurPage(false);
    }
    public void changePageMode(int pageMode){
        mPageMode=PageMode.intToEnum(pageMode);
        mPageView.setPageMode(mPageMode);
        mPageView.drawCurPage(false);
    }
    public void changeReadNightMode(int nightMode){
        if(nightMode==1){
            mBgColor=mContext.getResources().getColor(R.color.read_appbar_bg);
            mTextColor=mContext.getResources().getColor(R.color.white);
            mTitleColor=mContext.getResources().getColor(R.color.white);
            initPaint();
            mPageView.drawCurPage(false);
        }else{
            mSetting.initConfig();//重新加载mSetting
            mBgColor=mContext.getResources().getColor(mSetting.getBac_colorIds()[mSetting.getReadStyle()]);
            mTextColor=mContext.getResources().getColor(mSetting.getBac_color_text_colors()[mSetting.getReadStyle()]);
            mTitleColor=mContext.getResources().getColor(mSetting.getBac_color_text_colors()[mSetting.getReadStyle()]);
            initPaint();
            mPageView.drawCurPage(false);
        }
    }
}
