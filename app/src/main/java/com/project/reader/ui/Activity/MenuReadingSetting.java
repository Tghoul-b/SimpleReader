package com.project.reader.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reader.R;
import com.example.reader.databinding.ReadingSettingMenuBinding;
import com.project.reader.Config;
import com.project.reader.ui.util.Setting;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.BrightUtils;

public class MenuReadingSetting extends FrameLayout {
    private Setting setting;
    private Context mContext;
    private Callback callback;
    public ReadingSettingMenuBinding binding;
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
        binding=ReadingSettingMenuBinding.inflate(LayoutInflater.from(context),this,true);
        initClick();
        initWidget();
    }
    private void initWidget(){
        SeekBar seekBar=(SeekBar)binding.tvSettingMenuProgress;
        seekBar.setProgress(setting.getBrightProgress());
        Integer textSize=(setting.getReadTextSize()- Config.initReadTextSize)/4+20;
        TextView textView=binding.tvTextSize;
        textView.setText(Integer.toString(textSize));
    }
    private void initClick(){
        binding.tvSettingMenuProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    BrightUtils.setBrightness((ReadActivity)mContext,progress);
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
        binding.tvReduceTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=(String)binding.tvTextSize.getText();
                Integer textSize=Integer.parseInt(s);
                if(textSize<=12)  return ;//20->55对应55的话
                textSize--;
                binding.tvTextSize.setText(Integer.toString(textSize));
                callback.changeSize(-4);
            }
        });
        binding.tvIncreaseTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=(String)binding.tvTextSize.getText();
                Integer textSize=Integer.parseInt(s);
                if(textSize>=30)  return ;//20->55对应55的话
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
    }
    public interface  Callback{
        public void changeSize(int dif);
        public void startFontActivity();
    }
}
