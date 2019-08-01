package com.sayan.rnd.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class CustomCardview extends CardView {
    public CustomCardview(@NonNull Context context) {
        super(context);
    }

    public CustomCardview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCardview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        FrameLayout frame = new FrameLayout(getContext());
        frame.setLayoutParams(params);
        frame.setBackground(getResources().getDrawable(R.drawable.card_gradient));
        frame.setPadding(0,0,0,0);

        while (getChildCount() > 0) {
            View child = getChildAt(0);

            FrameLayout.LayoutParams childParams =
                    (FrameLayout.LayoutParams) child.getLayoutParams();

            removeView(child);
            frame.addView(child);

            child.setLayoutParams(childParams);
        }

        addView(frame);
    }
}
