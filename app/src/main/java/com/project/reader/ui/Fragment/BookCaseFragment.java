package com.project.reader.ui.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.project.reader.Config;
import com.project.reader.entity.BookCaseDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Activity.ReadActivity;
import com.project.reader.ui.Activity.SearchBookActivity;
import com.project.reader.ui.Adapter.BookCaseItemAdapter;
import com.project.reader.ui.util.tools.Themetools;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookCaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookCaseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
   private Toolbar toolbar;
   private ListView listView;
   private List<BookCaseDB> bookCaseDBList;
   private List<BookdetailBean> bookdetailBeanList;
   private BookCaseItemAdapter mAdapter;
   private int curSelect=-1;
    public BookCaseFragment() {
        // Required empty public constructor
    }
    private View view;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookCaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookCaseFragment newInstance(String param1, String param2) {
        BookCaseFragment fragment = new BookCaseFragment();
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
        setHasOptionsMenu(true);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_book_group, menu);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_book_case, container, false);
        }
        initData();
        initWidget();
        initClick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initWidget();
    }

    private void initData(){
        toolbar=view.findViewById(R.id.toolbar);
        listView=view.findViewById(R.id.book_case_list_main);
        bookCaseDBList= LitePal.findAll(BookCaseDB.class);
        if(mAdapter==null)
            mAdapter=new BookCaseItemAdapter(getContext(),R.layout.layout_book_case_item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==Activity.RESULT_OK){
            switch (requestCode) {
                case Config.CASE_REQ:
                    int lastChapter = data.getIntExtra("lastChapter", 1);
                    bookCaseDBList.get(curSelect).setLastChapterNum(lastChapter);
                    bookCaseDBList.get(curSelect).save();//保存下来
                    mAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid=item.getItemId();
        if(itemid==R.id.search_book){
            Intent intent=new Intent(getActivity(), SearchBookActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        return false;
    }
    public void initWidget(){
        toolbar.setTitle("书架");
        toolbar.inflateMenu(R.menu.menu_book_group);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mAdapter.addAll(bookCaseDBList);
        listView.setAdapter(mAdapter);
        Themetools.changeActivityTheme(getActivity());
    }
    private void initClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curSelect=position;
                BookdetailBean bean=new BookdetailBean(bookCaseDBList.get(position));
                if(bean.getLastReadPosition()<=0){
                    bean.setLastReadPosition(1);
                }
                Intent intent=new Intent(getContext(), ReadActivity.class);
                intent.putExtra("BOOK",bean);
                startActivityForResult(intent, Config.CASE_REQ);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setTitle("提示").setMessage("是否删除该书籍?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BookCaseDB bookCaseDB=bookCaseDBList.get(position);
                                bookCaseDB.delete();
                                List<BookCaseDB> list=new ArrayList<>(bookCaseDBList);
                                bookCaseDBList=new ArrayList<>();
                                for(int i=0;i<list.size();i++){
                                    if(i!=position)
                                        bookCaseDBList.add(list.get(i));
                                }
                                mAdapter.clear();
                                mAdapter.addAll(bookCaseDBList);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){


                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        } ).show();
                return true;
            }
        });
    }
}