package com.project.reader.ui.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.reader.R;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.Themetools;

public class feedbackActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private final String url="https://www.wjx.cn/vj/OmuyQuw.aspx";
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
    @SuppressLint("SetJavaScriptEnabled")
    private void initWidget(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("意见反馈");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        WebView webView=findViewById(R.id.feedback_online);

        WebSettings webSettings=webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100&&!App.isDestroy(feedbackActivity.this)){
                    findViewById(R.id.loadingView).setVisibility(View.GONE);
                }
            }
        });
        Themetools.changeActivityTheme(this);
    }
    private void  initClick(){
        toolbar.setNavigationOnClickListener(v->finish());
    }
}