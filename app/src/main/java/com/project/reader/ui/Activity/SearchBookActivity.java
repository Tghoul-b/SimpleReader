package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.reader.R;
import com.example.reader.databinding.ActivitySearchBookBinding;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.Adapter.SearchListAdapter;
import com.project.reader.ui.util.Engine.SearchEngine;
import com.project.reader.ui.util.Scrapy;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.cache.SpUtils;
import com.project.reader.ui.widget.CoverImageView;
import com.squareup.picasso.Picasso;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.gujun.android.taggroup.TagGroup;

public class SearchBookActivity extends AppCompatActivity {

    private int index;//推荐列表开始的index。
    private int maxlen=10;
    private ActivitySearchBookBinding binding;//定义dataBinding
    private String[]SuggestionList;
    private String[]InitialSuggestion={"太古神王","不灭龙帝","遮天","斗破苍穹","逆天邪神","万古神帝","元尊","武动乾坤","大主宰","完美世界"};
    private Scrapy scrapy;
    private Integer startIndex=0;
    private List<String>history=new ArrayList<String>();
    private String SearchKey;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SearchEngine searchEngine;
    private  SearchListAdapter mAdapter;
    private List<SearchBookBean>  listBean;
    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrapy=new Scrapy();
        bindview();
        initData();
        initWidget();
        initClick();
    }
    protected void bindview() {
        binding= ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
    public void initWidget() {
       toolbar=findViewById(R.id.toolbar);
       toolbar.setTitle("搜索");
       setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        InitHistoryTagGroup();
        SetTextSuggestion(index);
    }
    public  void initData(){
        searchEngine=new SearchEngine(this);
        recyclerView=findViewById(R.id.rv_search_books_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ACache aCache=ACache.get(this);
        String ans=aCache.getAsString("SuggestionList");
        if(ans==null||ans==""){
            SuggestionList=new String[10];
            for(int i=0;i<10;i++){
                SuggestionList[i]=InitialSuggestion[i];
            }
        }
        else{
           SuggestionList=ans.split(",");
        }
        ans=aCache.getAsString("SearchHistory");
        if(ans!=""&&ans!=null){
            String []tmp=ans.split(",");
            for(int i=0;i<tmp.length;i++)
                 history.add(tmp[i]);
        }
        int max=SuggestionList.length;
        int min=0;
        index=(int)(min+Math.random()*(max-min+1));
        index-=index%10;
        index%=max;
        searchEngine.setOnSearchListener(new SearchEngine.OnSearchListener() {
            @Override
            public void loadMoreFinish(Boolean isAll) {

            }

            @Override
            public void loadMoreSearchBook(List<SearchBookBean> beans) {
                mAdapter.addAll(beans,SearchKey);
            }

            @Override
            public void searchBookError(Throwable throwable) {

            }
        });
    }
    private void SetTextSuggestion(int startIndex){
        for(int i=startIndex;i<startIndex+10;i++){
            String s="suggest_btn_"+(i+1-startIndex);
            Integer id=getResources().getIdentifier(s, "id", getPackageName());
            TextView textView=findViewById(id);
            String t=SuggestionList[i];
            while(t.length()!=0&&t.charAt(0)==' ')  //解决一些空表字符串的问题
                t=t.substring(1);
            if(t.length()==0) {
                t="太古神王";
                SuggestionList[i]=t;
            }
            if(t.length()>maxlen)
                t=t.substring(0,maxlen)+"...";
            String d=String.format("%d    %s",i+1-startIndex,t);
            textView.setText(d);
        }
    }
    private void InitHistoryTagGroup(){
        TagGroup taggroup=findViewById(R.id.search_history);
        taggroup.setTags(history);
    }
    boolean checkHistory(String s){
        for(String t:history)
            if(t.equals(s))
                return true;
            return false;
    }
    private  void initClick(){
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SearchKey==null||SearchKey==""||SearchKey.length()==0)
                    Toasty.error(getApplicationContext(),"搜索关键字不能为空",Toast.LENGTH_LONG,true).show();
                Search();
            }
        });
        toolbar.setNavigationOnClickListener(
                (v)->finish()
        );
        binding.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SearchKey=s.toString();
            }
        });
        binding.changeSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index=(index+10)%SuggestionList.length;
                SetTextSuggestion(index);
            }
        });
        binding.suggestBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+0])) {
                    history.add(SuggestionList[index + 0]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+1])) {
                    history.add(SuggestionList[index + 1]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+2])) {
                    history.add(SuggestionList[index + 2]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+3])) {
                    history.add(SuggestionList[index + 3]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+4])) {
                    history.add(SuggestionList[index + 4]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+5])) {
                    history.add(SuggestionList[index + 5]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+6])) {
                    history.add(SuggestionList[index + 6]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+7])) {
                    history.add(SuggestionList[index + 7]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.suggestBtn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+8])) {
                    history.add(SuggestionList[index + 8]);
                    InitHistoryTagGroup();

                }
            }
        });
        binding.suggestBtn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+9])) {
                    history.add(SuggestionList[index +9]);
                    InitHistoryTagGroup();
                }
            }
        });
        binding.clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history=new ArrayList<>();
                InitHistoryTagGroup();
            }
        });
    }
    private void Search(){
        if(SearchKey==null||SearchKey==""||SearchKey.length()==0){
            binding.rvSearchBooksList.setVisibility(View.GONE);
            binding.suggestPlace.setVisibility(View.VISIBLE);
            binding.rvSearchBooksList.setAdapter(null);
            binding.historyArea.setVisibility(View.VISIBLE);
        }
        else {
            listBean=new ArrayList<>();
            mAdapter=new SearchListAdapter<SearchBookBean>(R.layout.listview_search_book) {
                @SuppressLint("SetTextI18n")
                @Override
                public void bindView(ViewHolder holder, SearchBookBean obj) {
                    CoverImageView imageView=holder.getView(R.id.tv_book_img);
                    String url=obj.getImgUrl();
                    imageView.load(url,obj.getName(),obj.getAuthor());
                    holder.setText(R.id.tv_book_name,getSpanString(obj.getName()));
                    holder.setText(R.id.tv_book_author,obj.getAuthor());
                    holder.setText(R.id.tv_book_desc,"简介:"+obj.getDesc());
                    holder.setText(R.id.tv_book_newest_chapter,"最新章节: "+obj.getLastChapter());
                    String status=obj.getStatus();
                    TagFlowLayout tagFlowLayout=holder.getView(R.id.tv_book_tag);
                    SetTagList(tagFlowLayout,status);
                }

            };
            searchEngine.Search(SearchKey);
            binding.suggestPlace.setVisibility(View.GONE);
            binding.rvSearchBooksList.setVisibility(View.VISIBLE);
            binding.historyArea.setVisibility(View.GONE);
            binding.rvSearchBooksList.setAdapter(mAdapter);
        }
    }
    private void SetTagList(TagFlowLayout tagFlowLayout,String status){
        List<String> mVals=new ArrayList<>();
        mVals.add("test");
        if(status!=null&&status.length()>0){
            if(status.equals("连载")||status.equals("新书上传")) {
                System.out.println("get here");
                tagFlowLayout.setAdapter(new TagAdapter<String>(mVals) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tvTagName = (TextView) View.inflate(SearchBookActivity.this, R.layout.item_book_tag, null);
                        tvTagName.setText("连载中");
                        return tvTagName;
                    }
                });
            }
        }
    }
    @Override
    public void onBackPressed() {
        if(SearchKey==null||SearchKey==""||SearchKey.length()==0){
            super.onBackPressed();
        }
        else{
            binding.searchEdit.setText("");
            Search();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String t= TextUtils.join(", ", history);
        ACache aCache=ACache.get(this);
        aCache.put("SearchHistory",t);
    }
    private SpannableString getSpanString(String name){
        int start=name.indexOf(SearchKey);
        SpannableString spannableString = new SpannableString(name);
        if(start==-1){
            return spannableString;
        }

        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                start, start + SearchKey.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
