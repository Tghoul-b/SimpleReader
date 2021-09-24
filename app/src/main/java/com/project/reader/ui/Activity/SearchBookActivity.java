package com.project.reader.ui.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.reader.R;
import com.example.reader.databinding.ActivitySearchBookBinding;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.Adapter.CommonRecycleAdapter;
import com.project.reader.ui.Adapter.SearchResAdapter;
import com.project.reader.ui.util.Engine.SearchEngine;
import com.project.reader.ui.util.callback.ErrorCallback;
import com.project.reader.ui.util.network.Scrapy;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.tools.Themetools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import es.dmoral.toasty.Toasty;
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
    private  SearchResAdapter mAdapter;
    private List<SearchBookBean>  listBean;
    private ImageLoader imageLoader;
    private String searchRule;
    private String []originList;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        toolbar=findViewById(R.id.toolbar);
       toolbar.setTitle("搜索");
       setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        InitHistoryTagGroup();
        SetTextSuggestion(index);
        Themetools.changeActivityTheme(this);
    }
    public  void initData(){
        searchRule="bookname";
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
        originList=ans.split(",");
            for(int i=0;i<originList.length;i++) {
                history.add(originList[i]);
            }
        }
        int max=SuggestionList.length;
        int min=0;
        index=(int)(min+Math.random()*(max-min+1));
        index-=index%10;
        index%=max;
        searchEngine.setOnSearchListener(new SearchEngine.OnSearchListener() {
            @Override
            public void loadMoreFinish(Integer isAll) {
                binding.refreshBar.setIsAutoLoading(false);
                if(isAll==-1){
                    Toasty.warning(getApplicationContext(),"未搜到相关书籍",Toast.LENGTH_SHORT).show();
                    binding.errorLayout.llError.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void loadMoreSearchBook(Map<SearchBookBean,BookdetailBean> map) {
                   mAdapter.addAll(map,SearchKey);
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
                else {
                    Search();
                }
            }
        });
        InitSuggestClcikGroup();
        binding.errorLayout.llError.findViewById(R.id.retry_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
        binding.searchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode== KeyEvent.KEYCODE_ENTER){
                    Search();
                    return true;
                }
                return false;
            }
        });
        binding.rvSearchBooksList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchBooksList.setItemViewCacheSize(30);
        ((SimpleItemAnimator)binding.rvSearchBooksList.getItemAnimator()).setSupportsChangeAnimations(false);//解决闪烁问题
        toolbar.setNavigationOnClickListener(
                (v)->finish()
        );
        binding.searchHistory.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                binding.searchEdit.setText(tag);
                binding.searchEdit.setSelection(tag.length());
                Search();
            }
        });
        binding.fingAuthor.setOnCheckedChangeListener(new AppCompatRadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    searchRule="author";
                }
            }
        });
        binding.fingBookName.setOnCheckedChangeListener(new AppCompatRadioButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    searchRule="bookname";
                }
            }
        });
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
        binding.clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history=new ArrayList<>();
                InitHistoryTagGroup();
            }
        });
    }
    private void Search() {
        binding.errorLayout.llError.setVisibility(View.INVISIBLE);
        if(SearchKey==null||SearchKey==""||SearchKey.length()==0){
            searchEngine.stopSearch();
            binding.refreshBar.setIsAutoLoading(false);
            binding.rvSearchBooksList.setVisibility(View.GONE);
            binding.suggestPlace.setVisibility(View.VISIBLE);
            binding.rvSearchBooksList.setAdapter(null);
            binding.historyArea.setVisibility(View.VISIBLE);
        }
        else {
            String t=SearchKey;
            while(t.length()>0&&t.charAt(0)==' ') t=t.substring(1);
            while(t.length()>0&&t.charAt(t.length()-1)==' ')t=t.substring(0,t.length()-1);
            SearchKey=t;
            binding.searchEdit.setText(SearchKey);
            if(!checkHistory(SearchKey)&&SearchKey.length()>0) {
                history.add(SearchKey);
                InitHistoryTagGroup();
            }
            listBean=new ArrayList<>();
            binding.refreshBar.setIsAutoLoading(true);
            mAdapter=new SearchResAdapter(R.layout.listview_search_book,this,SearchKey,searchEngine);
            mAdapter.setOnItemClickListener(new SearchResAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    SearchBookBean searchBookBean=mAdapter.getItem(position);

                    ArrayList<BookdetailBean> books=(ArrayList<BookdetailBean>)mAdapter.getBooks(searchBookBean);
//                    Intent intent=new Intent(getApplicationContext(),BookDetailedActivity.class);
//                    intent.putExtra("bookDetails",books);
//                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            searchEngine.Search(SearchKey,searchRule);
            binding.suggestPlace.setVisibility(View.GONE);
            binding.rvSearchBooksList.setVisibility(View.VISIBLE);
            binding.historyArea.setVisibility(View.GONE);
            binding.rvSearchBooksList.setAdapter(mAdapter);
            InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(binding.searchEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        String t= TextUtils.join(",", history);
        ACache aCache=ACache.get(this);
        aCache.put("SearchHistory",t);
        binding.rvSearchBooksList.setAdapter(null);//清除缓存
    }
    private  void InitSuggestClcikGroup(){
        binding.suggestBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+0])) {
                    history.add(SuggestionList[index + 0]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+0]);
                binding.searchEdit.setSelection(SuggestionList[index+0].length());
                Search();

            }
        });
        binding.suggestBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+1])) {
                    history.add(SuggestionList[index + 1]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+1]);
                binding.searchEdit.setSelection(SuggestionList[index+1].length());
                Search();
            }
        });
        binding.suggestBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+2])) {
                    history.add(SuggestionList[index + 2]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+2]);
                binding.searchEdit.setSelection(SuggestionList[index+2].length());
                Search();
            }
        });
        binding.suggestBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+3])) {
                    history.add(SuggestionList[index + 3]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+3]);
                binding.searchEdit.setSelection(SuggestionList[index+3].length());
                Search();
            }
        });
        binding.suggestBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+4])) {
                    history.add(SuggestionList[index + 4]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+4]);
                binding.searchEdit.setSelection(SuggestionList[index+4].length());
                Search();
            }
        });
        binding.suggestBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+5])) {
                    history.add(SuggestionList[index + 5]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+5]);
                binding.searchEdit.setSelection(SuggestionList[index+5].length());
                Search();
            }
        });
        binding.suggestBtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+6])) {
                    history.add(SuggestionList[index + 6]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+6]);
                binding.searchEdit.setSelection(SuggestionList[index+6].length());
                Search();
            }
        });
        binding.suggestBtn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+7])) {
                    history.add(SuggestionList[index + 7]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+7]);
                binding.searchEdit.setSelection(SuggestionList[index+7].length());
                Search();
            }
        });
        binding.suggestBtn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+8])) {
                    history.add(SuggestionList[index + 8]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+8]);
                binding.searchEdit.setSelection(SuggestionList[index+8].length());
                Search();
            }
        });
        binding.suggestBtn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkHistory(SuggestionList[index+9])) {
                    history.add(SuggestionList[index +9]);
                    InitHistoryTagGroup();
                }
                binding.searchEdit.setText(SuggestionList[index+9]);
                binding.searchEdit.setSelection(SuggestionList[index+9].length());
                Search();
            }
        });
    }

}
