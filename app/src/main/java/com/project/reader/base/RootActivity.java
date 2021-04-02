package com.project.reader.base;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reader.R;
import com.example.reader.databinding.ActivityMainBinding;

public abstract  class RootActivity extends AppCompatActivity {
    /**
     * onCreate函数
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 初始化参数，后代重写的方法
     * @param savedInstanceState
     */
    protected void initData(Bundle savedInstanceState){
    }
    protected abstract void bindview();

    protected void initListener(){

    }

}
