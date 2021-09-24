package com.project.reader.ui.widget.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.DrawableRes;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {1, 1, 18},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tJ\u001e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\n\u001a\u0004\u0018\u00010\u0005J\u001e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u000b\u001a\u0004\u0018\u00010\fJ\u001e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\r\u001a\u0004\u0018\u00010\u000eJ\u001e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010J%\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u00072\n\b\u0001\u0010\u0011\u001a\u0004\u0018\u00010\u0012¢\u0006\u0002\u0010\u0013J\u001e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015¨\u0006\u0016"},
        d2 = {"Lcom/project/reader/ui/widget/ImageLoader;", "", "()V", "load", "Lcom/bumptech/glide/RequestBuilder;", "Landroid/graphics/drawable/Drawable;", "context", "Landroid/content/Context;", "bitmap", "Landroid/graphics/Bitmap;", "drawable", "uri", "Landroid/net/Uri;", "file", "Ljava/io/File;", "bytes", "", "resId", "", "(Landroid/content/Context;Ljava/lang/Integer;)Lcom/bumptech/glide/RequestBuilder;", "path", "", "Reader.app"}
)
public final class ImageLoader {
    @NotNull
    public static final ImageLoader INSTANCE;

    @NotNull
    public final RequestBuilder load(@NotNull Context context, @Nullable String path) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        CharSequence var3 = (CharSequence)path;
        boolean var4 = false;
        boolean var5 = false;
        RequestBuilder var10000;
        if (var3 == null || var3.length() == 0) {
            var10000 = Glide.with(context).load(path);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(path)");
        } else if (StringsKt.startsWith(path, "http", true)) {
            var10000 = Glide.with(context).load(path);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(path)");
        } else {
            RequestBuilder var7;
            try {
                var7 = Glide.with(context).load(new File(path));
            } catch (Exception var6) {
                var7 = Glide.with(context).load(path);
            }

            var10000 = var7;
            Intrinsics.checkExpressionValueIsNotNull(var7, "try {\n                Gl….load(path)\n            }");
        }

        return var10000;
    }

    @NotNull
    public final RequestBuilder load(@NotNull Context context, @DrawableRes @Nullable Integer resId) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        RequestBuilder var10000 = Glide.with(context).load(resId);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(resId)");
        return var10000;
    }

    @NotNull
    public final RequestBuilder load(@NotNull Context context, @Nullable File file) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        RequestBuilder var10000 = Glide.with(context).load(file);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(file)");
        return var10000;
    }

    @NotNull
    public final RequestBuilder load(@NotNull Context context, @Nullable Uri uri) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        RequestBuilder var10000 = Glide.with(context).load(uri);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(uri)");
        return var10000;
    }

    @NotNull
    public final RequestBuilder load(@NotNull Context context, @Nullable Drawable drawable) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        RequestBuilder var10000 = Glide.with(context).load(drawable);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(drawable)");
        return var10000;
    }

    @NotNull
    public final RequestBuilder load(@NotNull Context context, @Nullable Bitmap bitmap) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        RequestBuilder var10000 = Glide.with(context).load(bitmap);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(bitmap)");
        return var10000;
    }

    @NotNull
    public final RequestBuilder load(@NotNull Context context, @Nullable byte[] bytes) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        RequestBuilder var10000 = Glide.with(context).load(bytes);
        Intrinsics.checkExpressionValueIsNotNull(var10000, "Glide.with(context).load(bytes)");
        return var10000;
    }

    private ImageLoader() {
    }

    static {
        ImageLoader var0 = new ImageLoader();
        INSTANCE = var0;
    }
}
