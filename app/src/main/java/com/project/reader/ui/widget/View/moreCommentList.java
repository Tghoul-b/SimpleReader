package com.project.reader.ui.widget.View;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
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
import com.example.reader.databinding.MoreCommentListBinding;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.RemoteDBbean;
import com.project.reader.ui.Activity.CommentActivity;
import com.project.reader.ui.Adapter.moreCommentResponseAdapter;
import com.project.reader.ui.util.DataHandler;
import com.project.reader.ui.util.cache.SpUtils;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.SoftInputUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class moreCommentList extends FrameLayout {
    public MoreCommentListBinding binding;
    private Context mContext;
    public moreCommentList(@NonNull Context context) {
        super(context);
        init(context);
    }

    public moreCommentList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public moreCommentList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public moreCommentList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    private void init(Context context){
        binding=MoreCommentListBinding.inflate(LayoutInflater.from(context),this,true);
        mContext=context;
        initClick();
    }
    private void initClick(){
        SoftInputUtils softInputUtils=new SoftInputUtils();
        EditText editText=binding.moreCommentInputArea.binding.moreCommentEditArea;
        softInputUtils.attachSoftInput(editText, new SoftInputUtils.ISoftInputChanged() {
            @Override
            public void onChanged(boolean isSoftInputShow, int softInputHeight, int viewOffset) {
                if (isSoftInputShow) {
                    LinearLayout linearLayout=binding.moreCommentInputArea.binding.getRoot();
                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)linearLayout.getLayoutParams();
                    layoutParams.setMargins(0,0,0,softInputHeight);
                    linearLayout.setLayoutParams(layoutParams);
                } else {
                    LinearLayout linearLayout=binding.moreCommentInputArea.binding.getRoot();
                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)linearLayout.getLayoutParams();
                    layoutParams.setMargins(0,0,0,0);
                    linearLayout.setLayoutParams(layoutParams);
                }
            }
        });

    }
}
