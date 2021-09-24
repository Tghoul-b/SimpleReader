package com.project.reader.ui.util.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.project.reader.base.HtmlCallBack;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.BookdetailBean;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import io.reactivex.rxjava3.core.Observable;

import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.RemoteDBbean;
import com.project.reader.entity.ReplyDetailBean;
import com.project.reader.entity.fontFamilyBean;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.cache.ACache;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static android.content.Context.BATTERY_SERVICE;

public class BaseApi {
    private static final float SCALE_DEGREE = 0.4f;
    /**
     * 最大模糊度（在0.0到25.0之间）
     */
    private static final float BLUR_RADIUS = 25f;

    /**
     *
     * @param key
     * @param bean
     * @param baseCrawler
     * @param searchRule
     * @return  Observable对象
     * 这个函数相当于是新开一个线程，然后由SearchonEngine观察到结果并处理
     */
    public static Observable<List<BookdetailBean>> SearchObverable(String key,BookSrcBean bean,baseCrawler baseCrawler,String searchRule) {
        return Observable.create(emitter -> {
            try {
                String url=bean.getSourceUrl();
                url=url.replace("{key}",key);
                List<BookdetailBean> c_res=baseCrawler.getSearchResult(url,bean.getSourceClass(),searchRule);
                emitter.onNext(c_res);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }
    public static Observable<BookdetailBean>InitOtherinfo(BookdetailBean bookdetailBean, baseCrawler baseCrawler){
        return Observable.create(emitter -> {
            String url=bookdetailBean.getInfoUrl();
            emitter.onNext(baseCrawler.getInfo(url,bookdetailBean));
        });
    }
    public static String loadConfig(Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open("ReferencesSource.json"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            inputReader.close();
            bufReader.close();
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<BookSrcBean> parseJson(Context context){
        String str=loadConfig(context);
        List<BookSrcBean> list=new ArrayList<>();
        list= JSON.parseArray(str,BookSrcBean.class);
        return list;
    }
    public static String KeyMove(String s,String author,String Searchkey){
        String []keyWords={"笔趣阁","小说阅读","顶点","小说","最新章节阅读","最新章节读","最新章节","章节","列表","最新章","更新","最新","吧",",","，"};
        for(int i=0;i<keyWords.length;i++){
            String key=keyWords[i];
            while(key.indexOf(Searchkey)==-1&&s.indexOf(key)!=-1){  //这个查找的关键字包含这些过滤的词汇，则不过滤
                int t=s.indexOf(key);
                int len=key.length();
                s=s.substring(0,t)+s.substring(t+len);
            }
        }
        if(s.indexOf('(')!=-1){  //去除括号
            int st=s.indexOf('(');
            int end=st;
            for(;end<s.length();end++){
                if(s.charAt(end)==')')  {
                    end++;//过滤掉')'
                    break;
                }
            }
            s=s.substring(0,st)+s.substring(end);
        }
        while(s.indexOf(author)!=-1){
            int t=s.indexOf(author);
            int len=author.length();
            s=s.substring(0,t)+s.substring(t+len);
        }
       return s;
    }
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidht, scaleHeight, x, y;
        Bitmap newbmp;
        Matrix matrix = new Matrix();
        if (width > height) {
            scaleWidht = ((float) h / height);
            scaleHeight = ((float) h / height);
            x = (width - w * height / h) / 2;// 获取bitmap源文件中x做表需要偏移的像数大小
            y = 0;
        } else if (width < height) {
            scaleWidht = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = (height - h * width / w) / 2;// 获取bitmap源文件中y做表需要偏移的像数大小
        } else {
            scaleWidht = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = 0;
        }
        matrix.postScale(scaleWidht, scaleHeight);
        try {
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) (width - x), (int) (height - y), matrix, true);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }
    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public static Bitmap blur(Context context,Bitmap bitmap) {
        //计算图片缩小的长宽
        int width = Math.round(bitmap.getWidth() * SCALE_DEGREE);
        int height = Math.round(bitmap.getHeight() * SCALE_DEGREE);

        //将缩小后的图片作为预渲染的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        //创建一张渲染后的输入图片
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        //创建RenderScript内核对象
        RenderScript renderScript = RenderScript.create(context);
        //创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        /**
         * 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
         * 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
         */
        Allocation inputAllocation = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation outputAllocation = Allocation.createFromBitmap(renderScript, outputBitmap);

        //设置渲染的模糊程度，25f是最大模糊度
        scriptIntrinsicBlur.setRadius(BLUR_RADIUS);
        //设置ScriptIntrinsicBlur对象的输入内存
        scriptIntrinsicBlur.setInput(inputAllocation);
        //将ScriptIntrinsicBlur输出数据保存到输出内存中
        scriptIntrinsicBlur.forEach(outputAllocation);

        //将数据填充到Allocation中
        outputAllocation.copyTo(outputBitmap);

        return outputBitmap;
    }
    public static  void saveBimap(String path,Bitmap bitmap) {
        String name=path;
        File file = new File(name);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public  static Document getHtml(String url) throws Exception{
        try {
            Connection conn = Jsoup.connect(url).timeout(5000);
            conn.userAgent("Mozilla/5.0 (Linux; Android 7.1.1; MI 6 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043807 Mobile Safari/537.36 MicroMessenger/6.6.1.1220(0x26060135) NetType/WIFI Language/zh_CN");
            Document document = conn.get();
            return document;
        }
        catch (Exception e){
            throw new Exception("无法得到最新章节");
        }
    }
    public static  Bitmap getFitAssetsSampleBitmap(AssetManager am,String file,int width,int height){
        InputStream assetFile = null;
        try {
            assetFile = am.open(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(assetFile, null, options);
            options.inSampleSize = getFitInSampleSize(width, height, options);
            options.inJustDecodeBounds = false;
            assetFile.close();
            assetFile = am.open(file);
            Bitmap bm = BitmapFactory.decodeStream(assetFile, null, options);
            assetFile.close();
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (assetFile != null) assetFile.close();
            } catch (Exception ee) {}
            return null;
        }
    }
    public static int getFitInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
        int inSampleSize = 1;
        if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
            int widthRatio = Math.round((float) options.outWidth / (float) reqWidth);
            int heightRatio = Math.round((float) options.outHeight / (float) reqHeight);
            inSampleSize = Math.min(widthRatio, heightRatio);
        }
        return inSampleSize;
    }
    public static String toHexEncoding(int color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("0x");
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }
    public static  int getBatteryPower(){
        BatteryManager batteryManager = (BatteryManager)App.getApplication().getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return battery;
    }
    public static int getSoftButtonsBarSizePort(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
    public static List<fontFamilyBean> parseFont(Context context){
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open("fontsFamily.json"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            inputReader.close();
            bufReader.close();
            List<fontFamilyBean> ans=JSON.parseArray(Result,fontFamilyBean.class);
            return ans;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static Typeface loadTypeface(Context context){
        ACache aCache=ACache.get(context);
        String useTypeFaceName=aCache.getAsString("useTypeFace");
        Typeface CurTypeFace=null;
        if(TextUtils.isEmpty(useTypeFaceName))
            useTypeFaceName="默认字体";
        if (!useTypeFaceName.equals("默认字体"))//如果不是默认字体
        {
            String filePath = App.getApplication().getExternalFilesDir(null).toString() + File.separator + "fontFiles/" + useTypeFaceName + ".ttf";
            File file=new File(filePath);
            try {
                CurTypeFace = Typeface.createFromFile(file);
            }catch (Exception e){
                CurTypeFace=Typeface.DEFAULT;
            }
        }
        else
            CurTypeFace=Typeface.DEFAULT;
        return CurTypeFace;
    }
    public static Typeface loadTypeface(Context context,String s){
        ACache aCache=ACache.get(context);
        Typeface CurTypeFace=null;

        if(TextUtils.isEmpty(s))
            s="默认字体";
        if (!s.equals("默认字体"))//如果不是默认字体
        {
            String filePath =  App.getApplication().getExternalFilesDir(null).toString()+ File.separator + "fontFiles/" + s + ".ttf";
            CurTypeFace = Typeface.createFromFile(new File(filePath));
        }
        else
            CurTypeFace=Typeface.DEFAULT;
        return CurTypeFace;
    }
    public static  boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }
    public static  boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
    public static String parseStrJson(String json){
        String res="[";
        final  String begin="fields";
        while(json.contains(begin)){
            int begin_p=json.indexOf(begin);
            while(json.charAt(begin_p)!='{')  begin_p++;
            int end_p=json.indexOf('}',begin_p);
            String filedTmp=json.substring(begin_p,end_p+1);
            res+=filedTmp;
            json=json.substring(end_p+2);
            if(json.length()>3)
                res+=",";
        }
        return res+"]";
    }
    public static List<CommentDetailBean> GenerateComment(List<RemoteDBbean> list){
        List<CommentDetailBean> ans=new ArrayList<>();
        for(RemoteDBbean bean:list){
            if(bean.getReplyPerson().equals("root")){
               CommentDetailBean commentDetailBean=new CommentDetailBean(bean.getNickName()
               ,bean.getContent(),bean.getCreateDate());
               commentDetailBean.setReplyList(new ArrayList<>());
               commentDetailBean.setNumberFloor(bean.getNumberFloor());
               ans.add(commentDetailBean);
            }
        }
        Collections.sort(ans, new Comparator<CommentDetailBean>() {
            @Override
            public int compare(CommentDetailBean o1, CommentDetailBean o2) {
                return o1.getNumberFloor()-o2.getNumberFloor();
            }
        });
        for(RemoteDBbean bean:list){
            if(!bean.getReplyPerson().equals("root")){
                int number=bean.getNumberFloor();
                ReplyDetailBean replyDetailBean=new ReplyDetailBean(bean.getNickName(),
                        bean.getContent(),bean.getCreateDate(),bean.getReplyPerson());
                ans.get(number).getReplyList().add(replyDetailBean);
            }
        }
        return ans;
    }
    public static  String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseResponsibility(String s){
        String appName=getAppName(App.getApplication());
        int start=0;
       s=s.replaceAll("（app名）",appName);
       return s;
    }
    public static void getBaseHtmlCallBack(String s, HtmlCallBack callBack) throws Exception{
           try{
               Document document = getHtml(s);
               callBack.onFinish(document);
           }catch (Exception e){
               e.printStackTrace();
           }
    }
}
