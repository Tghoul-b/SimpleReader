package com.project.reader.ui.util.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.reader.R;
import com.project.reader.entity.DaoMaster;
import com.project.reader.entity.DaoSession;
import com.squareup.picasso.Picasso;

import org.litepal.LitePal;

import io.alterac.blurkit.BlurKit;

public class App extends Application {
    private  static DaoSession daoSession;
    private static  final Handler handler=new Handler();
    private static Application application;
    public static  void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
    public static Handler getHandler(){
        return handler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        application=this;
        BlurKit.init(this);
        setupDatabase();
    }
    public static Application getApplication() {
        return application;
    }
    private void setupDatabase(){
        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(this,"bookInfo.db",null);
        SQLiteDatabase db=helper.getWritableDatabase();
        DaoMaster daoMaster=new DaoMaster(db);
        daoSession=daoMaster.newSession();
    }
    public static DaoSession getDaoSession(){
        return  daoSession;
    }
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
    public  void updateUi(View view,Context context,String name,String url){//更新login之后的视图
        if(name!=null&&url!=null) {
            TextView textView = view.findViewById(R.id.login_title);
            textView.setText(name);
            Uri parse = Uri.parse(url);
            String levelTitle = "Lv." + name.length();
            TextView Level = view.findViewById(R.id.level_text);
            Level.setText(levelTitle);
            ImageView level_img = view.findViewById(R.id.level_img);
            level_img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_queue_bold));
            view.findViewById(R.id.coin_img).setVisibility(View.VISIBLE);
            view.findViewById(R.id.coin_text).setVisibility(View.VISIBLE);
            ImageView imageView = view.findViewById(R.id.head_img);
            Picasso.with(context).load(parse).fit().into(imageView);
        }
    }
    public static boolean ViewEmptyContent(View view){
        if(view instanceof TextView){
            String s=(String)((TextView) view).getText().toString();
            if(s==null||s.length()==0)
                return true;
            else
                return false;
        }
        return false;
    }


}
