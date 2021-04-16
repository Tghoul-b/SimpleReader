package com.project.reader.ui.Glide;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.reader.R;
import com.project.reader.ui.util.tools.BaseApi;

import net.qiujuer.genius.graphics.Blur;

public class UtilityBlur {
    /**
     * 使用 资源图片 模糊
     *
     * @param imageID 资源图片 id
     */
    public static void blur(ImageView imageView, int imageID) {
        try {
            Bitmap overlay = BitmapFactory.decodeResource(imageView.getContext().getResources(), imageID);
            imageView.setImageBitmap(overlay);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    /**
     * 使用 网络图片 模糊
     *
     * @param imgUrl 图片地址
     */
    public static void blur(Context context,final ImageView imageView, String imgUrl) {
        Glide.with(imageView.getContext()).
                asBitmap().load(imgUrl)
                .listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                resource=Blur.onStackBlur(resource,70);
                return false;
            }
        }).into(imageView);
    }
    }
