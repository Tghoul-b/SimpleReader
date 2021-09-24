package com.project.reader.ui.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.BrowserBookBean;
import com.project.reader.ui.Adapter.BrowserAdapter;
import com.project.reader.ui.util.tools.Themetools;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class browserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView titleTv;
    private TextView clearTv;
    private ListView listView_browser;
    private List<BrowserBookBean> mDatas;
    private BrowserAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initData();
        initWidget();
        initClick();
    }
    private void initData(){
        toolbar=findViewById(R.id.toolbar);
        titleTv=new TextView(this);
        clearTv=new TextView(this);
        listView_browser=findViewById(R.id.browser_main_area);
        mDatas= LitePal.findAll(BrowserBookBean.class);
        mAdapter=new BrowserAdapter(this,R.layout.layout_browser_item);
        listView_browser.setAdapter(mAdapter);
        setSupportActionBar(toolbar);
    }
    private void initWidget(){
        titleTv.setText("最近浏览");
        titleTv.setTextColor(getResources().getColor(R.color.black));
        titleTv.setTextSize(16);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        titleTv.setLayoutParams(layoutParams);
        toolbar.addView(titleTv);
        clearTv.setText("清空");
        clearTv.setTextColor(getResources().getColor(R.color.white));
        clearTv.setTextSize(16);
        clearTv.setGravity(Gravity.RIGHT);
        Toolbar.LayoutParams layoutParams1=new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity=Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        layoutParams1.setMargins(0,0,40,0);
        clearTv.setLayoutParams(layoutParams1);
        toolbar.addView(clearTv);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        Themetools.changeActivityTheme(this);
    }
    private void initClick(){
        toolbar.setNavigationOnClickListener(
                (v)->finish()
        );
        listView_browser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BrowserBookBean browserBookBean=mDatas.get(position);
                BookdetailBean bean=new BookdetailBean(browserBookBean);
                ArrayList<BookdetailBean > list=new ArrayList<>();
                list.add(bean);
//                Intent intent=new Intent(browserActivity.this,BookDetailedActivity.class);
//                intent.putExtra("bookDetails",list);
//                startActivity(intent);
            }
        });
        clearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
                LitePal.deleteAll(BrowserBookBean.class);
            }
        });
    }
}