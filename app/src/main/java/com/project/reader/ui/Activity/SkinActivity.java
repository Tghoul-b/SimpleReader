package com.project.reader.ui.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemeUtils;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reader.R;
import com.project.reader.ui.util.Setting;
import com.project.reader.ui.util.tools.Themetools;

import java.util.ArrayList;
import java.util.List;

public class SkinActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<ImageView> imageViews;
    private final int imgSize=10;
    private Setting setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        initData();
        initWidget();
        initClick();
    }
    private void initData(){
        toolbar=findViewById(R.id.toolbar);
        setting=new Setting(this);
        imageViews=new ArrayList<>();
        for(int i=0;i<imgSize;i++){
            String name="iv_color"+(i+1);
            int id=getResources().getIdentifier(name,"id",getPackageName());
            ImageView imageView=findViewById(id);
            imageViews.add(imageView);
        }
    }
    private void initWidget(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        int themeIdx=setting.getThemeIdx();
        changeTheme(themeIdx);
        setSupportActionBar(toolbar);
        TextView textView=new TextView(this);
        textView.setText("换肤");
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(R.color.white));
        Toolbar.LayoutParams layoutParams=new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        textView.setLayoutParams(layoutParams);
        toolbar.addView(textView);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
    }
    private void initClick(){
        toolbar.setNavigationOnClickListener(v->finish());
        for(int i=0;i<imgSize;i++){
            final int index=i;
            imageViews.get(i).setOnClickListener(v->changeTheme(index));
        }
    }
    private void changeTheme(int idx){
        for(int i=0;i<imgSize;i++){
            imageViews.get(i).setImageDrawable(null);
        }
        imageViews.get(idx).setImageDrawable(getResources().getDrawable(R.drawable.ic_selected));
        setting.setThemeIdx(idx);//0-9之间
        setting.saveAllConfig();
        Themetools.changeActivityTheme(this);
    }
}