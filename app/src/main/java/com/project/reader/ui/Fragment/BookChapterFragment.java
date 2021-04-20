package com.project.reader.ui.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.reader.R;
import com.example.reader.databinding.ActivityBookChapterListBinding;
import com.project.reader.db.dbUtils;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterBeanDao;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Activity.bookChapterListActivity;
import com.project.reader.ui.Adapter.BookChapterAdapter;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.widget.StatusBarUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookChapterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookChapterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;//父视图
    private Integer isPositive;//是否是正序
    private List<BookChapterBean> listRes;//记录章节的搜索结果
    private BookChapterAdapter mAdapter;
    private BookdetailBean bookdetailBean;
    public BookChapterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookChapterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookChapterFragment newInstance(String param1, String param2) {
        BookChapterFragment fragment = new BookChapterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_book_chapter, container, false);
        isPositive=1;
        bookdetailBean=(BookdetailBean)getArguments().get("bookdetailBean");
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initClick();
        initWidget();
    }
    private void initClick(){
        view.findViewById(R.id.backward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        ImageView imageView=view.findViewById(R.id.orderChange);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPositive^=1;
                if(isPositive==1){
                    imageView.setImageDrawable(getActivity().getBaseContext().
                            getResources().getDrawable(R.drawable.ic_positiveseq));
                }
                else{
                    imageView.setImageDrawable(getActivity().getBaseContext().
                            getResources().getDrawable(R.drawable.ic_reverseseq));
                }
                mAdapter.reverseAll();
            }
        });
    }
    private void initWidget(){
        TextView titleView=view.findViewById(R.id.bookName);
        titleView.setText(bookdetailBean.getBookName());
        mAdapter=new BookChapterAdapter(getContext(),R.layout.bookchapteradapter);
        listRes= dbUtils.queruAllChapters(bookdetailBean.getBookName(),
                bookdetailBean.getAuthor(),
                bookdetailBean.getSourceName(),100);
        if(listRes.size()==0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    baseCrawler crawler = CrawlerHandler.getCrawler(bookdetailBean.getSourceClass());
                    listRes = crawler.getChapterList(bookdetailBean);
                    mAdapter.addAll(listRes);
                    int cnt=0;
                    for(BookChapterBean bean:listRes){
                        bean.setBookName(bookdetailBean.getBookName());
                        bean.setAuthor(bookdetailBean.getAuthor());
                        bean.setSourceName(bookdetailBean.getSourceName());
                        bean.setId(bean.hashCode());
                        bean.setChapterNum(cnt++);
                    }
                    App.runOnUiThread(() -> {
                        ListView listView=view.findViewById(R.id.bookChapterList);
                        listView.setAdapter(mAdapter);
                    });
                    dbUtils.insertAllBookChapter(listRes);//insert所有的类
                }
            }).start();
        }
        else{
            mAdapter.addAll(listRes);
            ListView listView=view.findViewById(R.id.bookChapterList);
            listView.setAdapter(mAdapter);
            App.getDaoSession().clear();
            //开启一个新的线程加载后面章节
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listRes=dbUtils.queruAllChapters(bookdetailBean.getBookName(),
                            bookdetailBean.getAuthor(),bookdetailBean.getSourceName(),10000);
                    App.runOnUiThread(() -> {
                        mAdapter.addAll(listRes);
                        ListView listView=view.findViewById(R.id.bookChapterList);
                        listView.setAdapter(mAdapter);
                    });
                }
            }).start();
        }

    }
}