package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.example.reader.databinding.ActivityFontfamilyBinding;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.fontFamilyBean;
import com.project.reader.ui.Adapter.CommonAdapter;
import com.project.reader.ui.Adapter.fontFamilyAdapter;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.Themetools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fontfamilyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityFontfamilyBinding binding;
    private fontFamilyAdapter mAdapter;
    private ACache aCache;
    private List<fontFamilyBean> list;
    private Typeface CurTypeFace;
    private String useTypeFaceName;
    private List<Thread> threadList;
    public Map<String,Integer> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        initData();
        initClick();
        initWidget();
    }

    private void bindView() {
        binding = ActivityFontfamilyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Themetools.changeActivityTheme(this);
    }

    public List<Thread> getThreadList() {
        return threadList;
    }

    public void setThreadList(List<Thread> threadList) {
        this.threadList = threadList;
    }

    private void initData() {
        map=new HashMap<>();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("阅读字体");
        threadList=new ArrayList<>();
        setSupportActionBar(toolbar);
        aCache = ACache.get(this);
        mAdapter = new fontFamilyAdapter(this, R.layout.font_listview_adapter_layout);
        list=BaseApi.parseFont(this);
        for(int i=1;i<list.size();i++){
            map.put(list.get(i).getFontName(),i-1);
        }
    }

    private void initClick() {
        toolbar.setNavigationOnClickListener(
                (v) -> finish()
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initWidget() {
        ListView listView = binding.fontFamilyListview;
        mAdapter.addAll(list);
        listView.setAdapter(mAdapter);
    }
    public void changeTypeface() {
        CurTypeFace=BaseApi.loadTypeface(this);
        TextView poem_title = findViewById(R.id.poem_title);
        poem_title.setTypeface(CurTypeFace);
        TextView poem_line_1 = findViewById(R.id.poem_line_1);
        poem_line_1.setTypeface(CurTypeFace);
        TextView poem_line_2 = findViewById(R.id.poem_line_2);
        poem_line_2.setTypeface(CurTypeFace);
    }

    @Override
    protected void onDestroy() {
        for(Thread thread:threadList)
            if(thread!=null&&thread.isAlive())
                thread.interrupt();
        super.onDestroy();


    }

    public Typeface getCurTypeFace() {
        return CurTypeFace;
    }
    public void setCurTypeFace(Typeface curTypeFace) {
        CurTypeFace = curTypeFace;
    }

}