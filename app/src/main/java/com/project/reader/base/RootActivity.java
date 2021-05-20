package com.project.reader.base;

import android.content.res.Resources;
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

public   class RootActivity extends AppCompatActivity {
    /**
     * onCreate函数
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
    }

    private void setTheme(){

    }


}
