package com.project.reader.ui.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.project.reader.ui.Activity.SearchBookActivity;

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
        toolbar=view.findViewById(R.id.toolbar);
        initWidget();
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid=item.getItemId();
        if(itemid==R.id.search_book){
            Intent intent=new Intent(getActivity(), SearchBookActivity.class);
            startActivity(intent);
        }
        return false;
    }
    public void initWidget(){
        toolbar.setTitle("书架");
        toolbar.inflateMenu(R.menu.menu_book_group);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }
}