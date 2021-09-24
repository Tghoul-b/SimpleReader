package com.project.reader.ui.widget.View;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reader.R;
import com.example.reader.databinding.ReadingSettingMenuBinding;
import com.project.reader.Config;
import com.project.reader.ui.util.Setting;
import com.project.reader.ui.util.tools.BrightUtils;
import com.project.reader.ui.util.tools.Themetools;
import com.project.reader.ui.widget.CircleImageView;

public class MenuReadingSetting extends FrameLayout {
    private Setting setting;
    private Context mContext;
    private Callback callback;
    public ReadingSettingMenuBinding binding;
    private int follow_sys_checked=0;//跟随系统这个按钮是否被选中
    private Integer[]bac_color_ids;
    private Integer[]bac_color_text_colors;
    private CircleImageView[]imageViews;
    private boolean HorizontalScreen;  //
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public MenuReadingSetting(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MenuReadingSetting(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MenuReadingSetting(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MenuReadingSetting(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        mContext=context;
        setting=new Setting(context);
        binding= ReadingSettingMenuBinding.inflate(LayoutInflater.from(context),this,true);
        initData();
        initClick();
        initWidget();
    }
    private  void initData(){
        imageViews=new CircleImageView[]{binding.defaultBacIcon,binding.darkBacIcon,binding.greenBacIcon,binding.brownBacIcon,
        binding.blackBacIcon,binding.lightYellowBacIcon};
        HorizontalScreen=(setting.getHorizontalScreen()==1);
    }
    private void initWidget(){
        SeekBar seekBar=(SeekBar)binding.tvSettingMenuProgress;
        seekBar.setProgress(setting.getBrightProgress());
        Integer textSize=(setting.getReadTextSize()- Config.initReadTextSize)/4+20;
        TextView textView=binding.tvTextSize;
        textView.setText(Integer.toString(textSize));
        follow_sys_checked=setting.getFollow_sys_checked();
        changeIconSysColor();
        initReadStyle(setting.getReadStyle());
    }
    private  void changeIconSysColor(){
        switch (follow_sys_checked){
            case 0:
                binding.followSystem.setBackground(getResources().getDrawable(R.drawable.menu_icon_shape));
                binding.followSystem.setTextColor(Color.WHITE);

                break;
            case 1:
                Themetools.changeFollowTheme(binding.followSystem);

                break;
        }
        setting.setFollow_sys_checked(follow_sys_checked);
        setting.saveAllConfig();
    }
    private void initClick() {
        binding.tvSettingMenuProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(progress);
                    setting.setBrightProgress(progress);
                    setting.saveAllConfig();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        for (int i = 0; i < imageViews.length; i++) {
            final int styleIdx = i;
            imageViews[i].setOnClickListener(v -> changeReadStyle(styleIdx));
        }
        binding.tvReduceTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = (String) binding.tvTextSize.getText();
                Integer textSize = Integer.parseInt(s);
                if (textSize <= 12) return;//20->55对应55的话
                textSize--;
                binding.tvTextSize.setText(Integer.toString(textSize));
                callback.changeSize(-4);
            }
        });
        binding.tvIncreaseTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = (String) binding.tvTextSize.getText();
                Integer textSize = Integer.parseInt(s);
                if (textSize >= 30) return;//20->55对应55的话
                textSize++;
                binding.tvTextSize.setText(Integer.toString(textSize));
                callback.changeSize(4);
            }
        });
        binding.readFontFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.startFontActivity();
            }
        });
        binding.followSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow_sys_checked ^= 1;
                changeIconSysColor();
            }
        });

    }
    private void initReadStyle(int styleIdx){
        setting.setReadStyle(styleIdx);
        for(int i=0;i<imageViews.length;i++){
            imageViews[i].setBorderColor(mContext.getResources().getColor(R.color.read_menu_text));
        }
        imageViews[styleIdx].setBorderColor(mContext.getResources().getColor(R.color.sys_dialog_setting_word_red));
        if(callback!=null)
            callback.changeReadStyle(setting.getBac_colorIds()[styleIdx],setting.getBac_color_text_colors()[styleIdx]);
    }
    private void changeReadStyle(int styleIdx){
        setting.setReadStyle(styleIdx);
        setting.setNightMode(0);
        setting.saveAllConfig();
        for(int i=0;i<imageViews.length;i++){
            imageViews[i].setBorderColor(mContext.getResources().getColor(R.color.read_menu_text));
        }
        imageViews[styleIdx].setBorderColor(mContext.getResources().getColor(R.color.sys_dialog_setting_word_red));
        if(callback!=null)
            callback.changeReadStyle(setting.getBac_colorIds()[styleIdx],setting.getBac_color_text_colors()[styleIdx]);
    }
    public interface  Callback{
        public void changeSize(int dif);
        public void startFontActivity();
        public void changeReadStyle(int bacColorId,int textColorId);
    }
}
