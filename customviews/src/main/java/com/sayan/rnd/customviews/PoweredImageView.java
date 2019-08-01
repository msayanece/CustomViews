package com.sayan.rnd.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapThumbnailImageViewTarget;

public class PoweredImageView extends AppCompatImageView {
    private String mImageUrl;
    private BitmapThumbnailImageViewTarget mBitmapThumbnailImageViewTarget;

    public PoweredImageView(Context context) {
        super(context);
        init(null);
    }

    public PoweredImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PoweredImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

//    public PoweredImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(attrs);
//    }

    private void init(@Nullable AttributeSet attrs){
        //return if the AttributeSet is null
        if (attrs == null) return;
        //get TypedArray for PoweredImageView for obtaining the attribute value
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PoweredImageView);
        //get the url
        mImageUrl = typedArray.getString(R.styleable.PoweredImageView_imageUrl);
        //free for Garbage Collector
        typedArray.recycle();

        // ready the glide target for loading the image
        mBitmapThumbnailImageViewTarget = new BitmapThumbnailImageViewTarget(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Glide.with(this)
                .asBitmap()
                .load(mImageUrl)
                .into(mBitmapThumbnailImageViewTarget);
    }
}
