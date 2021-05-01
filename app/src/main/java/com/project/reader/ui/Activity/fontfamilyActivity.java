package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.example.reader.databinding.ActivityFontfamilyBinding;
import com.project.reader.entity.fontFamilyBean;
import com.project.reader.ui.Adapter.fontFamilyAdapter;

public class fontfamilyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActivityFontfamilyBinding binding;
    private fontFamilyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        initData();
        initClick();
        initWidget();
    }
    private void bindView(){
        binding=ActivityFontfamilyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    private void initData(){
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("阅读字体");
        setSupportActionBar(toolbar);
        mAdapter=new fontFamilyAdapter(this,R.layout.font_listview_adapter_layout);

    }
    private void initClick(){
        toolbar.setNavigationOnClickListener(
                (v)->finish()
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initWidget(){
        ListView listView=binding.fontFamilyListview;
        fontFamilyBean bean=new fontFamilyBean();
        bean.setFontName("方正楷体");
        mAdapter.add(bean);
        listView.setAdapter(mAdapter);
    }
}