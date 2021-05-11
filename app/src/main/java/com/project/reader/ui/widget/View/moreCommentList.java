package com.project.reader.ui.widget.View;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reader.databinding.MoreCommentListBinding;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.ui.Activity.CommentActivity;
import com.project.reader.ui.util.tools.App;

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
    }

}
