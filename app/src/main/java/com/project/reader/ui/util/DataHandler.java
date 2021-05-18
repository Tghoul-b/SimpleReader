package com.project.reader.ui.util;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.RemoteDBbean;
import com.project.reader.entity.ReplyDetailBean;
import com.project.reader.ui.util.tools.BaseApi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                        BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                        String res="";
                        String s="";
                        while(!TextUtils.isEmpty(s=br.readLine())){
                            res+=s;
                        }
                        JSONObject jsonObj = new JSONObject(res);
                        Object obj =jsonObj.get("AllReplyInfo");
                        String jsonStr=obj.toString();
                        jsonStr=jsonStr.substring(1,jsonStr.length()-1);
                        jsonStr="{"+jsonStr+"}";
                        String deal_res= BaseApi.parseStrJson(jsonStr);
                        List<RemoteDBbean> remoteDBbeanList=JSON.parseArray(deal_res,RemoteDBbean.class);
                        List<CommentDetailBean>  commentDetailBeanList=BaseApi.GenerateComment(remoteDBbeanList);
                        callback.getDataCallback(commentDetailBeanList);
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
                        +"&date="+remoteDBbean.getCreateDate()+"&replyPerson="+remoteDBbean.getReplyPerson()+"&numberFloor="+remoteDBbean.getNumberFloor();
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
    public static  void getCurId(String userId){
        new Thread(()->{
        String url="http://106.52.12.54:8080/QueryUser?userId="+userId;
        try{
            URL url_con = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url_con.openConnection();

            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode==200) {
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                String res="";
                String s="";
                while(!TextUtils.isEmpty(s=br.readLine())){
                    res+=s;
                }
                String t=res.substring(4);
                int d= Integer.parseInt(t);
                callback.loadCurId(d);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        }).start();
    }

    public interface CURDCallback{
        public void getDataCallback(List<CommentDetailBean> list);
        public void loadCurId(int curId);
    }
}
