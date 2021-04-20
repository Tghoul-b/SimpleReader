package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.reader.R;
import com.example.reader.databinding.ActivityBookChapterListBinding;
import com.example.reader.databinding.ActivityBookDetailedBinding;
import com.example.reader.databinding.BookchapteradapterBinding;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Adapter.BookChapterAdapter;
import com.project.reader.ui.Fragment.BookChapterFragment;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.widget.StatusBarUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class bookChapterListActivity extends AppCompatActivity {

    private BookdetailBean bookdetailBean;
    private ActivityBookChapterListBinding binding;
    private BookChapterFragment ChapterFragment;
    private List<Fragment>  fragmentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        initData();
        initWidget();
    }
    private void initData(){
        bookdetailBean=(BookdetailBean)getIntent().getSerializableExtra("bookInfo");
        ChapterFragment=new BookChapterFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("bookdetailBean",bookdetailBean);
        ChapterFragment.setArguments(bundle);
        fragmentList=new ArrayList<>();
        fragmentList.add(ChapterFragment);
    }
    private void bindView(){
        binding= ActivityBookChapterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            StatusBarUtil.setLightStatusBar(this,true,true);
        }
    }
    private void initWidget(){
        binding.mainPageViewer.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
    }
}