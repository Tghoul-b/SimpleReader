package com.project.reader.ui.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.reader.R;
import com.project.reader.Thread.ChapterThread;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Activity.ReadActivity;
import com.project.reader.ui.Adapter.BookChapterAdapter;
import com.project.reader.ui.Adapter.CommonAdapter;

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
        mAdapter=new BookChapterAdapter(getContext(),R.layout.bookchapteradapter);
        initWidget();
        initClick();

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
        ListView listView=view.findViewById(R.id.bookChapterList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                final int position;
                if(isPositive==1)    //正序
                {
                    position=i;
                }else{
                    position=mAdapter.mDatas.size()-1-i;
                }
                Intent intent=new Intent();
                intent.putExtra("chapter_page",position+1);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        });
    }
    private void initWidget(){
        TextView titleView=view.findViewById(R.id.bookName);
        titleView.setText(bookdetailBean.getBookName());
        ListView listView=view.findViewById(R.id.bookChapterList);
        ChapterThread chapterThread=new ChapterThread(getContext(),listRes,mAdapter,bookdetailBean,listView);//新加载这个线程类
        chapterThread.chapterFromNextWork.start();
    }
}