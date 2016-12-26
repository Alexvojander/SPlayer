package com.dmplayer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dmplayer.R;

public class SelectableTextView extends TextView {
    private int backgroundColorDefault;
    private int backgroundColorPressed;

    private int textColorDefault;
    private int textColorPressed;

    public SelectableTextView(Context context) {
        super(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectableView,
                defStyleAttr, 0);

        try {
            backgroundColorDefault = a.getColor(R.styleable.SelectableView_backgroundColor_default, 0);
            backgroundColorPressed = a.getColor(R.styleable.SelectableView_backgroundColor_pressed, 0);

            textColorDefault = a.getColor(R.styleable.SelectableView_textColor_default, 0);
            textColorPressed = a.getColor(R.styleable.SelectableView_textColor_pressed, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        for(int state : getDrawableState()) {
            if (state == android.R.attr.state_focused || state == android.R.attr.state_pressed) {
                setBackgroundColor(backgroundColorPressed);
                setTextColor(textColorPressed);
                return;
            }
        }

        setBackgroundColor(backgroundColorDefault);
        setTextColor(textColorDefault);
    }
}
