package com.project.reader.entity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;

import com.project.reader.ui.util.tools.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class fontFamilyBean implements Serializable {
    private static final String TAG ="downFontFile" ;
    private String fontName;//字体名称
    private String fontSize;//字体大小
    private int status;//0代表下载,1代表启用，2代表使用中,3代表正在下载中请稍候
    private final String baseUrl="http://106.52.12.54/fonts/";
    private Typeface typeface;
    private final String savaDirPath="fontFiles";

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public boolean  isInCache(){
        String pathSd = App.getApplication().getExternalFilesDir(null).toString();  //获取 SD 卡路径
        File fileRealDirectoty  = new File(pathSd+ File.separator + savaDirPath);
        File file = new File(fileRealDirectoty,fontName+".ttf");//检验这个是否存在内存中
       return file.exists();
    }
    public boolean DownLoadFileFormUrl(onDownLoadListener loadListener){
        String urlLoadPath=baseUrl+fontName+".ttf";
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.i(TAG," 未安装 SD 卡");
            return  false;
        }
        String pathSd = App.getApplication().getExternalFilesDir(null).toString();  //获取 SD 卡路径
        File fileRealDirectoty  = new File(pathSd + File.separator + savaDirPath);
        Log.i(TAG,"文件保存的真正目录： " + fileRealDirectoty);
        if (!fileRealDirectoty.exists()) {  //如果目录 不存在 ，就创建
            Log.i(TAG,"创建 存储文件夹");
            fileRealDirectoty.mkdirs();
        }
        //获取要下载的文件名称，在这里可以更改下载的文件名
        try {
            URL url = new URL(urlLoadPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == 200 ) {
                String fileName = urlLoadPath.substring(urlLoadPath.lastIndexOf("/") + 1);
                //String fileName = "test.zip";
                //创建 这个文件名 命名的 file 对象
                File file = new File(fileRealDirectoty,fileName);
                Log.i(TAG,"file: " + file);
                if (!file.exists()) {  //倘若没有这个文件
                    try {
                        Log.i(TAG,"创建文件");
                        file.createNewFile();  //创建这个文件
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                int fileSize = httpURLConnection.getContentLength();
                Log.i(TAG,"文件大小： " + fileSize);
                inputStream = httpURLConnection.getInputStream();
                fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int tem = 0;
                while ((tem = inputStream.read(b))!= -1) {
                    fileOutputStream.write(b,0,tem);
                }
                loadListener.downSuccess(true);
            } else {
                loadListener.downSuccess(false);
            }
            //根据响应获取文件大小

        } catch (MalformedURLException e) {
            e.printStackTrace();
            loadListener.downSuccess(false);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            loadListener.downSuccess(false);
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                loadListener.downSuccess(false);
            }
        }
        Log.i(TAG,"文件下载 成功");
        return true;
    }
    public interface onDownLoadListener{
        public void downSuccess(boolean success);
    }
}
