package com.project.reader.ui.widget.View;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reader.R;
import com.example.reader.databinding.CommentInputAreaBinding;
import com.project.reader.ui.Activity.CommentActivity;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.SoftInputUtils;

import java.util.Timer;
import java.util.TimerTask;

public class moreCommentinputWidget extends RelativeLayout {
    private Context mContext;
    public CommentInputAreaBinding binding;
    private String commentContent;//评论内容
    private OnInputClicker clicker;

    public void setClicker(OnInputClicker clicker) {
        this.clicker = clicker;
    }

    public moreCommentinputWidget(@NonNull Context context) {
        super(context);
        init(context);
    }

    public moreCommentinputWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public moreCommentinputWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public moreCommentinputWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    private void init(Context context){
        binding=CommentInputAreaBinding.inflate(LayoutInflater.from(context),this,true);
        mContext=context;
        initClick();
    }
    private void initClick(){
        binding.moreCommentEditArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                commentContent=s.toString();
                if(commentContent.length()>=1) {
                    binding.moreCommentInputIcon.setBackgroundColor(getResources().getColor(R.color.purple_500));
                }
                else{
                    binding.moreCommentInputIcon.setBackgroundColor(getResources().getColor(R.color.purple_200));
                }
            }
        });

    }
    public interface OnInputClicker{
        public void SendComment(String s);
    }
}
