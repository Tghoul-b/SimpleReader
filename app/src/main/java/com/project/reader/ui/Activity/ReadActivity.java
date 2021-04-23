package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.reader.R;
import com.project.reader.ui.widget.Page.PageLoader;
import com.project.reader.ui.widget.Page.PageView;

public class ReadActivity extends AppCompatActivity  {
    public PageView mPageView;
    private PageLoader mPageLoader;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        initData();
        initClick();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(true) {
                    return true;
                }
                    break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(true) {
                    return mPageLoader.skipToNextPage();  //这个没什么用
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mPageView=findViewById(R.id.bookPageView);
        mPageLoader=mPageView.getPageLoader();
    }
    private void initClick(){
        mPageView.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return false;
            }

            @Override
            public void center() {

            }

            @Override
            public void prePage() {

            }

            @Override
            public void nextPage() {

            }

            @Override
            public void cancel() {

            }
        });
    }

}