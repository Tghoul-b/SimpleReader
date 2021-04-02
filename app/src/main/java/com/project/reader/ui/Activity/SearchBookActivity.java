package com.project.reader.ui.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.example.reader.R;
import com.example.reader.databinding.ActivitySearchBookBinding;

import me.gujun.android.taggroup.TagGroup;

public class SearchBookActivity extends AppCompatActivity {

    private ActivitySearchBookBinding binding;//定义dataBinding
    private String []suggestion={"太古神王","不灭龙帝","遮天","斗破苍穹","逆天邪神","万古神帝","元尊","武动乾坤","大主宰","完美世界"};
    private String[]history={"太古神王","遮天"};
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindview();
        InitData();
        InitHistoryTagGroup();
        initWidget();
        initClick();
    }

    /**
     * 重写父类bindingview函数
     */
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

    }
    private void InitData(){
        for(int i=1;i<=10;i++){
            String s="suggest_btn_"+i;

            int stringID = getResources().getIdentifier(s, "id", getPackageName());
            TextView temp=findViewById(stringID);
            String d=String.format("%d     %s",i,suggestion[i-1]);
            temp.setText(d);
        }
    }
    private void InitHistoryTagGroup(){
        TagGroup taggroup=findViewById(R.id.search_history);
        taggroup.setTags(suggestion);
    }
    private  void initClick(){
        toolbar.setNavigationOnClickListener(
                (v)->finish()
        );
    }

}
