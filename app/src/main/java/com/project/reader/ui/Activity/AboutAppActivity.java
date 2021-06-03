package com.project.reader.ui.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.reader.R;
import com.example.reader.databinding.ActivityAboutAppBinding;
import com.project.reader.ui.util.OkGoUpdateHttpUtil;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.Themetools;
import com.vector.update_app.UpdateAppManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import me.gujun.android.taggroup.TagGroup;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AboutAppActivity extends AppCompatActivity {
    private String mUpdateUrl="http://106.52.12.54/json.txt";
    private Toolbar toolbar;
    private ActivityAboutAppBinding binding;
    private String[] producerName={"海纳百川","Tghoul呗","甦","不想长大"};
    private TagGroup tagGroup;
    private Map<String,String> map;
    private String flag=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initWidget();
        initClick();
    }
    private void initData(){
        map=new HashMap<>();
        map.put(producerName[0],"2331140640");
        map.put(producerName[1],"917602350");
        map.put(producerName[2],"524738330");
        map.put(producerName[3],"1175166047");
    }
    private void initView(){
        binding=ActivityAboutAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tagGroup=binding.producerInfo;
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            String s=getResources().getString(R.string.app_name)+" v"+version;
            binding.tvVersion.setText(s);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void initWidget(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(  //设置返回键的click
                (v) -> finish()
        );
        tagGroup.setTags(producerName);
        Themetools.changeActivityTheme(this);
    }
    private void initClick(){
        ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);//剪切板管理
        tagGroup.setOnTagClickListener(tag -> {
            String s=map.get(tag)+"@qq.com";
            ClipData clipData=ClipData.newPlainText("label",s);
            assert mClipboardManager!=null;
            mClipboardManager.setPrimaryClip(clipData);
            String text=tag+"的邮箱复制成功";
            Toasty.success(getApplicationContext(),text,Toast.LENGTH_SHORT, true).show();
        } );
        binding.checkupdateArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();
            }
        });
        binding.joinQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String key="GktdxRDvl01N_m3O8DPDHmjhy8VHRgr4";
                intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
                // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // 未安装手Q或安装的版本不支持
                    Toasty.error(getApplicationContext(),"未安装手Q或安装的版本不支持",Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        binding.responsibility.setOnClickListener(v -> {
            String s=getResources().getString(R.string.responsibility_msg);
            s= BaseApi.parseResponsibility(s);
            new AlertDialog.Builder(AboutAppActivity.this)
                    .setTitle("免责声明").setMessage(s)
                    .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        });
    }
    private void download(String mUrl){
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(mUrl)
                //实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                .build()
                .update();
        }
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            download(mUpdateUrl);
        }
    };
    private void checkUpdate() {

        try{
            checkVersion();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            flag=(String)msg.obj;
            if(flag.equals("can't connect")){
                Toasty.error(getApplicationContext(),"网络异常,无法连接",Toast.LENGTH_SHORT,true).show();
            }
            else if(flag.equals("Yes")){
                download(mUpdateUrl);
            }
            else{
                Toasty.success(getApplicationContext(),"当前已是最新版本",Toast.LENGTH_SHORT,true).show();
            }
        }

    };

    public void checkVersion() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(mUpdateUrl).build();
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        try {
                            String json = response.body().string();
                            JSONObject jsonObject = new JSONObject(json);
                            String flag = jsonObject.getString("versionCode");
                            Message message = new Message();
                            PackageInfo packageInfo =getApplicationContext()
                                    .getPackageManager()
                                    .getPackageInfo(getApplicationContext().getPackageName(), 0);
                            int appVersionCode;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                appVersionCode = (int)packageInfo.getLongVersionCode();
                            } else {
                                appVersionCode = packageInfo.versionCode;
                            }
                            int new_v=Integer.parseInt(flag);
                            if(new_v>appVersionCode)
                             message.obj="Yes";
                            else
                                message.obj="No";
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message message=new Message();
                            message.obj="can't connect";
                            handler.sendMessage(message);
                        }
                    }else{
                        Message message=new Message();
                        message.obj="can't connect";
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}