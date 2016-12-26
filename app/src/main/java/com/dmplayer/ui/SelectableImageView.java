package com.dmplayer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dmplayer.R;

public class SelectableImageView extends ImageView {
    private int tintDefault;
    private int tintPressed;
    private int backgroundColorDefault;
    private int backgroundColorPressed;

    public SelectableImageView(Context context) {
        super(context);
    }

    public SelectableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    public SelectableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectableView,
                defStyleAttr, 0);

        try {
            tintDefault = a.getColor(R.styleable.SelectableView_tint_default, 0);
            tintPressed = a.getColor(R.styleable.SelectableView_tint_pressed, 0);

            backgroundColorDefault =
                    a.getColor(R.styleable.SelectableView_backgroundColor_default, 0);
            backgroundColorPressed =
                    a.getColor(R.styleable.SelectableView_backgroundColor_pressed, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        for (int state : getDrawableState()) {
            if (state == android.R.attr.state_focused || state == android.R.attr.state_pressed) {
                setBackgroundColor(backgroundColorPressed);
                setColorFilter(tintPressed);
                return;
            }
        }

        setBackgroundColor(backgroundColorDefault);
        setColorFilter(tintDefault);
    }
}
