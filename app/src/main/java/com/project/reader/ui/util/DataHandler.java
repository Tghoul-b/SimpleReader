package com.project.reader.ui.util;

import android.util.Log;

import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.RemoteDBbean;
import com.project.reader.entity.ReplyDetailBean;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {
    private static  CURDCallback callback;

    public static void setCallback(CURDCallback callback) {
        DataHandler.callback = callback;
    }

    public static void getCommentData(long bookId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String params="bookId="+bookId;
                String AbsoluteUrl="http://106.52.12.54:8080/getAllInfo?"+params;
                try{
                    URL url=new URL(AbsoluteUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    connection.setRequestMethod("GET");

                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode==200){
                        Log.d("数据库","获取数据成功");
                        String s =connection.getResponseMessage();
                        System.out.println(s);
                        callback.getDataCallback(new ArrayList<>());
                    }
                    else{
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void InsertReplyInfo(RemoteDBbean remoteDBbean){
         new Thread(new Runnable() {
            @Override
            public void run() {
                String params="bookId="+remoteDBbean.getBookId()+"&username="+remoteDBbean.getNickName()+"&content="+remoteDBbean.getContent()
                        +"&date="+remoteDBbean.getCreateDate()+"&replyPerson="+remoteDBbean.getReplyPerson();
                String AbsoluteUrl="http://106.52.12.54:8080/InsertInfo?"+params;
                try {
                    URL url = new URL(AbsoluteUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    connection.setRequestMethod("GET");

                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode==200){
                        Log.d("数据库","添加成功");
                    }
                    else{
                        System.out.println("get here error");
                    }
                }catch (Exception e){
                    System.out.println("get here error");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public interface CURDCallback{
        public void getDataCallback(List<CommentDetailBean> list);
    }
}
