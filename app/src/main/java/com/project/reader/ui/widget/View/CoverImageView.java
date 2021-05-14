package com.project.reader.ui.widget.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatImageView;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.reader.R;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {1, 1, 18},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u00002\u00020\u0001B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007B\u001f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ$\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\f2\b\u0010\u0018\u001a\u0004\u0018\u00010\f2\b\u0010\u000b\u001a\u0004\u0018\u00010\fJ\u0010\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020#H\u0014J0\u0010$\u001a\u00020\u001f2\u0006\u0010%\u001a\u00020\u00172\u0006\u0010&\u001a\u00020\t2\u0006\u0010'\u001a\u00020\t2\u0006\u0010(\u001a\u00020\t2\u0006\u0010)\u001a\u00020\tH\u0014J\u0018\u0010*\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020\t2\u0006\u0010,\u001a\u00020\tH\u0014J\u000e\u0010-\u001a\u00020\u001f2\u0006\u0010\u0011\u001a\u00020\tJ\u001c\u0010.\u001a\u00020\u001f2\b\u0010\u0018\u001a\u0004\u0018\u00010\f2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0002R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0011\u001a\u00020\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u001b\u001a\u00020\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u0013\"\u0004\b\u001d\u0010\u0015¨\u0006/"},
        d2 = {"Lcom/project/reader/ui/widget/CoverImageView;", "Landroidx/appcompat/widget/AppCompatImageView;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "author", "", "authorHeight", "", "authorPaint", "Landroid/text/TextPaint;", "height", "getHeight$Reader_app", "()F", "setHeight$Reader_app", "(F)V", "loadFailed", "", "name", "nameHeight", "namePaint", "width", "getWidth$Reader_app", "setWidth$Reader_app", "load", "", "path", "onDraw", "canvas", "Landroid/graphics/Canvas;", "onLayout", "changed", "left", "top", "right", "bottom", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "setHeight", "setText", "Reader.app"}
)
public final class CoverImageView extends AppCompatImageView {
    private float width;
    private float height;
    private float nameHeight;
    private float authorHeight;
    private final TextPaint namePaint;
    private final TextPaint authorPaint;
    private String name;
    private String author;
    private boolean loadFailed;
    private onLoadFailListener listener;

    public onLoadFailListener getListener() {
        return listener;
    }

    public void setListener(onLoadFailListener listener) {
        this.listener = listener;
    }

    public boolean isLoadFailed() {
        return loadFailed;
    }

    public void setLoadFailed(boolean loadFailed) {
        this.loadFailed = loadFailed;
    }

    public final float getWidth$Reader_app() {
        return this.width;
    }

    public final void setWidth$Reader_app(float var1) {
        this.width = var1;
    }

    public final float getHeight$Reader_app() {
        return this.height;
    }

    public final void setHeight$Reader_app(float var1) {
        this.height = var1;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = measuredWidth * 7 / 5;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.width = (float)this.getWidth();
        this.height = (float)this.getHeight();
        this.namePaint.setTextSize(this.width / (float)6);
        this.namePaint.setStrokeWidth(this.namePaint.getTextSize() / (float)10);
        this.authorPaint.setTextSize(this.width / (float)9);
        this.authorPaint.setStrokeWidth(this.authorPaint.getTextSize() / (float)10);
        this.nameHeight = this.height / (float)2;
        this.authorHeight = this.nameHeight + this.authorPaint.getFontSpacing();
    }

    protected void onDraw(@NotNull Canvas canvas) {
        Intrinsics.checkParameterIsNotNull(canvas, "canvas");
        if (this.width >= (float)10 && this.height > (float)10) {
            Path path = new Path();
            path.moveTo(10.0F, 0.0F);
            path.lineTo(this.width - (float)10, 0.0F);
            path.quadTo(this.width, 0.0F, this.width, 10.0F);
            path.lineTo(this.width, this.height - (float)10);
            path.quadTo(this.width, this.height, this.width - (float)10, this.height);
            path.lineTo(10.0F, this.height);
            path.quadTo(0.0F, this.height, 0.0F, this.height - (float)10);
            path.lineTo(0.0F, 10.0F);
            path.quadTo(0.0F, 0.0F, 10.0F, 0.0F);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
        if (this.loadFailed) {
            String var10000 = this.name;
            boolean var3;
            boolean var4;
            boolean var6;
            String var7;
            if (var10000 != null) {
                var7 = var10000;
                var3 = false;
                var4 = false;
                var6 = false;
                this.namePaint.setColor(-1);
                this.namePaint.setStyle(Style.STROKE);
                canvas.drawText(var7, this.width / (float)2, this.nameHeight, (Paint)this.namePaint);
                this.namePaint.setColor(-65536);
                this.namePaint.setStyle(Style.FILL);
                canvas.drawText(var7, this.width / (float)2, this.nameHeight, (Paint)this.namePaint);
            }

            var10000 = this.author;
            if (var10000 != null) {
                var7 = var10000;
                var3 = false;
                var4 = false;
                var6 = false;
                this.authorPaint.setColor(-1);
                this.authorPaint.setStyle(Style.STROKE);
                canvas.drawText(var7, this.width / (float)2, this.authorHeight, (Paint)this.authorPaint);
                this.authorPaint.setColor(-65536);
                this.authorPaint.setStyle(Style.FILL);
                canvas.drawText(var7, this.width / (float)2, this.authorHeight, (Paint)this.authorPaint);
            }

        }
    }

    public final void setHeight(int height) {
        int width = height * 5 / 7;
        this.setMinimumWidth(width);
    }

    public final void setText(String name, String author) {
        String var10001;
        String var10002;
        byte var4;
        byte var5;
        boolean var6;
        StringBuilder var7;
        if (name == null) {
            var10001 = null;
        } else if (name.length() > 5) {
            var7 = new StringBuilder();
            var4 = 0;
            var5 = 4;
            var6 = false;
            var10002 = name.substring(var4, var5);
            Intrinsics.checkExpressionValueIsNotNull(var10002, "(this as java.lang.Strin…ing(startIndex, endIndex)");
            var10001 = var7.append(var10002).append("…").toString();
        } else {
            var10001 = name;
        }

        this.name = var10001;
        if (author == null) {
            var10001 = null;
        } else if (author.length() > 8) {
            var7 = new StringBuilder();
            var4 = 0;
            var5 = 7;
            var6 = false;
            var10002 = author.substring(var4, var5);
            Intrinsics.checkExpressionValueIsNotNull(var10002, "(this as java.lang.Strin…ing(startIndex, endIndex)");
            var10001 = var7.append(var10002).append("…").toString();
        } else {
            var10001 = author;
        }

        this.author = var10001;
    }

    public final void load(@Nullable String path, @Nullable String name, @Nullable String author) {
        this.setText(name, author);
        ImageLoader var10000 = ImageLoader.INSTANCE;
        Context var10001 = this.getContext();
        Intrinsics.checkExpressionValueIsNotNull(var10001, "context");
        ((RequestBuilder)((RequestBuilder)((RequestBuilder)var10000.load(var10001, path).placeholder(R.mipmap.ic_default)).error(R.mipmap.ic_default)).listener((RequestListener)(new RequestListener() {
            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @Nullable Target target, boolean isFirstResource) {
                CoverImageView.this.loadFailed = true;
                return false;
            }

            public boolean onResourceReady(@Nullable Drawable resource, @Nullable Object model, @Nullable Target target, @Nullable DataSource dataSource, boolean isFirstResource) {
                CoverImageView.this.loadFailed = false;
                return false;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean onResourceReady(Object var1, Object var2, Target var3, DataSource var4, boolean var5) {
                return this.onResourceReady((Drawable)var1, var2, var3, var4, var5);
            }
        })).centerCrop()).into((ImageView)this);
    }

    public CoverImageView(@NotNull Context context) {
        super(context);
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.namePaint = new TextPaint();
        this.authorPaint = new TextPaint();
        this.namePaint.setTypeface(Typeface.DEFAULT_BOLD);
        this.namePaint.setAntiAlias(true);
        this.namePaint.setTextAlign(Align.CENTER);
        this.namePaint.setTextSkewX(-0.2F);
        this.authorPaint.setTypeface(Typeface.DEFAULT);
        this.authorPaint.setAntiAlias(true);
        this.authorPaint.setTextAlign(Align.CENTER);
        this.authorPaint.setTextSkewX(-0.1F);
    }

    public CoverImageView(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(attrs, "attrs");

        this.namePaint = new TextPaint();
        this.authorPaint = new TextPaint();
        this.namePaint.setTypeface(Typeface.DEFAULT_BOLD);
        this.namePaint.setAntiAlias(true);
        this.namePaint.setTextAlign(Align.CENTER);
        this.namePaint.setTextSkewX(-0.2F);
        this.authorPaint.setTypeface(Typeface.DEFAULT);
        this.authorPaint.setAntiAlias(true);
        this.authorPaint.setTextAlign(Align.CENTER);
        this.authorPaint.setTextSkewX(-0.1F);
    }

    public CoverImageView(@NotNull Context context, @NotNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(attrs, "attrs");

        this.namePaint = new TextPaint();
        this.authorPaint = new TextPaint();
        this.namePaint.setTypeface(Typeface.DEFAULT_BOLD);
        this.namePaint.setAntiAlias(true);
        this.namePaint.setTextAlign(Align.CENTER);
        this.namePaint.setTextSkewX(-0.2F);
        this.authorPaint.setTypeface(Typeface.DEFAULT);
        this.authorPaint.setAntiAlias(true);
        this.authorPaint.setTextAlign(Align.CENTER);
        this.authorPaint.setTextSkewX(-0.1F);
    }

    // $FF: synthetic method
    public static final boolean access$getLoadFailed$p(CoverImageView $this) {
        return $this.loadFailed;
    }
    public  interface onLoadFailListener{
        public void LoadFailure(boolean isFailure);
    }
}
