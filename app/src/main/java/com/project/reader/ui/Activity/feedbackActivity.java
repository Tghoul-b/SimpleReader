package com.project.reader.ui.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.reader.R;

public class feedbackActivity extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initData();
        initWidget();
        initClick();
    }
    private void initData(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    private void initWidget(){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    private void  initClick(){
        toolbar.setNavigationOnClickListener(v->finish());
    }
}