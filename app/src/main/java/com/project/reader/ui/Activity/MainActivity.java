package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reader.R;
import com.example.reader.databinding.ActivityMainBinding;
import com.project.reader.base.RootActivity;
import com.project.reader.ui.Fragment.BookCaseFragment;
import com.project.reader.ui.Fragment.BookSourceFragment;
import com.project.reader.ui.Fragment.MineFragment;
import com.project.reader.ui.util.ImageViewPlus;
import com.project.reader.ui.util.StatusBarUtil;
import com.project.reader.ui.util.cache.SpUtils;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends RootActivity {


    private static final String TAG = "MainActivity";
    private static final String PACKAGE_QQ = "com.tencent.mobileqq";
    private static final String APP_ID = "1111636718";//官方获取的APPID
    private Tencent mTencent;
    private static final String APPID = "1105771437";
    private QQLoginListener mListener;
    private UserInfo mInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFigureurl() {
        return figureurl;
    }

    public void setFigureurl(String figureurl) {
        this.figureurl = figureurl;
    }

    private String name, figureurl;

    private ActivityMainBinding binding;
    private String []titles;
    private Toolbar mToolbar;
    private List<Fragment> fragmentList;//记录Fragment的List
    private BookCaseFragment bookCaseFragment;//书架碎片
    private BookSourceFragment bookSourceFragemnt;//书源碎片
    private MineFragment mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindview();//相当于是把子类的视图传到setContentView当中使其成为当前屏幕上的活动视图
        StatusBarUtil.setLightStatusBar(MainActivity.this,true,false);
        initView();
        initWidget();
        initListener();
        initRight();
    }
    private void initRight() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            //Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
        }
    }
    protected void initView() {
        bookCaseFragment = new BookCaseFragment();
        bookSourceFragemnt = new BookSourceFragment();
        mineFragment = new MineFragment();
        fragmentList=new ArrayList<>();
        fragmentList.add(bookCaseFragment);
        fragmentList.add(bookSourceFragemnt);
        fragmentList.add(mineFragment);
        //initFragment(bookCaseFragment);
        mListener = new QQLoginListener();
        // 实例化Tencent
        if (mTencent == null) {
            mTencent = Tencent.createInstance(APPID, this);
        }
        getUserSaveInfo();
    }


    @Override
    protected void bindview(){
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setLightStatusBar(MainActivity.this,true,false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
   private void initWidget(){
        binding.viewPagerMain.setOffscreenPageLimit(2);
        binding.viewPagerMain.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
    @Override
    protected void initListener() {
        super.initListener();
        binding.bottomNavigation.setOnNavigationItemSelectedListener(menuItem->{
            int position=menuItem.getItemId();//得到Item的id;
//            menuItem.setChecked(true);
            switch (position) {
                case R.id.main_btn_bookshelf: {
//                    initFragment(bookCaseFragment);

                    binding.viewPagerMain.setCurrentItem(0);
                    break;
                }
                case R.id.main_btn_discovery: {
//                    initFragment(bookSourceFragemnt);

                    binding.viewPagerMain.setCurrentItem(1);
                    break;
                }
                case R.id.main_btn_mine: {
//                    initFragment(mineFragment);

                    binding.viewPagerMain.setCurrentItem(2);
                    break;
                }
            }
                return false;
        });
        binding.viewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.bottomNavigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
//    private void hideFragment(FragmentTransaction transaction){
//        if(bookCaseFragment!= null){
//            transaction.hide(bookCaseFragment);
//        }
//        if(bookSourceFragemnt != null){
//            transaction.hide(bookSourceFragemnt);
//        }
//        if(mineFragment != null){
//            transaction.hide(mineFragment);
//        }
//    }

//    public void initFragment(Fragment fragment){
//        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.view_pager_main,fragment);
//            hideFragment(transaction);
//            transaction.show(fragment);
//            transaction.commit();
//    }
    public boolean QQLogin() {
        //如果session不可用，则登录，否则说明已经登录
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", mListener);
            return true;
        }
        else {
            Toast.makeText(MainActivity.this, "请勿重复登录", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    // 实现登录成功与否的接口
    private class QQLoginListener implements IUiListener {

        @Override
        public void onComplete(Object object) { //登录成功
            //获取openid和token
            initOpenIdAndToken(object);
            //获取用户信息
            getUserInfo();
        }

        @Override
        public void onError(UiError uiError) {  //登录失败
        }

        @Override
        public void onCancel() {                //取消登录
        }

        @Override
        public void onWarning(int i) {

        }
    }

    private void initOpenIdAndToken(Object object) {
        JSONObject jb = (JSONObject) object;
        try {
            String openID = jb.getString("openid");  //openid用户唯一标识
            String access_token = jb.getString("access_token");
            String expires = jb.getString("expires_in");

            mTencent.setOpenId(openID);
            mTencent.setAccessToken(access_token, expires);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        QQToken token = mTencent.getQQToken();
        mInfo = new UserInfo(MainActivity.this, token);
        mInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                JSONObject jb = (JSONObject) object;
                try {
                    name = jb.getString("nickname");
                    figureurl = jb.getString("figureurl_qq_2");  //头像图片的url
                   AfterLoginUpdateUi();
                   saveUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onWarning(int i) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTencent.onActivityResultData(requestCode, resultCode, data, mListener);

    }
    public void AfterLoginUpdateUi(){
//登录完之后更新视图
        TextView textView=mineFragment.getView().findViewById(R.id.login_title);
        textView.setText(name);
        Uri parse = Uri.parse(figureurl);

        ImageView imageView=mineFragment.getView().findViewById(R.id.head_img);
        Picasso.with(MainActivity.this).load(parse).fit().into(imageView);

        String levelTitle="Lv."+name.length();
        TextView Level=mineFragment.getView().findViewById(R.id.level_text);
        Level.setText(levelTitle);

        ImageView level_img=mineFragment.getView().findViewById(R.id.level_img);
        level_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_queue_bold));

        mineFragment.getView().findViewById(R.id.coin_img).setVisibility(View.VISIBLE);

        mineFragment.getView().findViewById(R.id.coin_text).setVisibility(View.VISIBLE);
    }
    private void saveUser(){
        SpUtils.getInstance(this).setString("username",name,600);
        SpUtils.getInstance(this).setString("figureurl",figureurl,600);
    }
    private void getUserSaveInfo()  {
        name=SpUtils.getInstance(this).getString("username",null);
        System.out.println("username is :"+name);
        figureurl=SpUtils.getInstance(this).getString("figureurl",null);

    }

}

