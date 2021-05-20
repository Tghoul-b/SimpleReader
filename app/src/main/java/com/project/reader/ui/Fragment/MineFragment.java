package com.project.reader.ui.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reader.R;
import com.project.reader.ui.Activity.AboutAppActivity;
import com.project.reader.ui.Activity.MainActivity;
import com.project.reader.ui.Activity.SkinActivity;
import com.project.reader.ui.Activity.browserActivity;
import com.project.reader.ui.Activity.feedbackActivity;
import com.project.reader.ui.util.cache.SpUtils;
import com.project.reader.ui.util.tools.App;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String Loginname,figureurl;
    private View mFakeStatusBar;
    private final String App_Id="1111636718";
    private Tencent tencent;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MainActivity activity;
    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 2000;

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
    private View view;

   private boolean isClick;
    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
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

        view= inflater.inflate(R.layout.fragment_mine, container, false);
        activity = (MainActivity) getActivity();
        initClick();
        initData();
        updateUi();
        return view;
    }
    private  void initClick(){
        tencent= Tencent.createInstance(App_Id,getActivity().getApplicationContext());
        view.findViewById(R.id.login_area).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                    lastClickTime=System.currentTimeMillis();
                    if(Loginname==null&&activity.QQLogin()) {
                        updateUi();
                    }
                }
                else if(Loginname!=null){
                    Toast.makeText(getActivity(), "请勿重复登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.share_area).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new Share2.Builder(getActivity()).setContentType(ShareContentType.TEXT)
                        .setTextContent(getActivity().getResources().getString(R.string.share_content))
                        .setTitle(getActivity().getResources().getString(R.string.share))
                        .build()
                        .shareBySystem();
            }
        });
        view.findViewById(R.id.recent_browser_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), browserActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.rate_area).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                view.findViewById(R.id.loading_area).setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.findViewById(R.id.loading_area).setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "感谢您对我们的支持", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);

            }
    });
        view.findViewById(R.id.about_app_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AboutAppActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.feedback_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), feedbackActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.change_style_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SkinActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initData(){
        Loginname=SpUtils.getInstance(getActivity()).getString("username",null);
        figureurl= SpUtils.getInstance(getActivity()).getString("figureurl",null);
        activity.SetOnLoginListener(new MainActivity.OnLoginListener() {
            @Override
            public void loadLoginName(String name) {
                Loginname=name;
            }

            @Override
            public void loadLoginUrl(String url) {
                figureurl=url;
            }
        });
    }
    public void updateUi(){
            App app=new App();
            app.updateUi(view,getActivity(),Loginname,figureurl);
    }
}
