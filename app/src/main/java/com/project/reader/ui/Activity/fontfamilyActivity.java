package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
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

import java.io.File;
import java.util.List;

public class fontfamilyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityFontfamilyBinding binding;
    private fontFamilyAdapter mAdapter;
    private ACache aCache;
    private List<fontFamilyBean> list;
    private Typeface CurTypeFace;
    private String useTypeFaceName;

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
    }

    private void initData() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("阅读字体");
        setSupportActionBar(toolbar);
        aCache = ACache.get(this);
        mAdapter = new fontFamilyAdapter(this, R.layout.font_listview_adapter_layout);
    }

    private void initClick() {
        toolbar.setNavigationOnClickListener(
                (v) -> finish()
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initWidget() {
        ListView listView = binding.fontFamilyListview;
        list=BaseApi.parseFont(this);
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

    public Typeface getCurTypeFace() {
        return CurTypeFace;
    }

    public void setCurTypeFace(Typeface curTypeFace) {
        CurTypeFace = curTypeFace;
    }

}