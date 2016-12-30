package com.dmplayer.ui.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmplayer.R;

import butterknife.ButterKnife;

public class ExpandableLayout extends LinearLayout {
    private static final String ANDROID_SCHEME = "http://schemas.android.com/apk/res/android";

    private static final String SAVE_STATE_EXPANDED = "SAVE_STATE_EXPANDED";
    private static final String SAVE_STATE_LAYOUT = "SAVE_STATE_LAYOUT";

    private RelativeLayout header;
    private ImageView icon;

    private LinearLayout content;

    String titleText;
    String detailsText;
    int imageResource;

    int headerColor;
    int contentColor;


    String originalOrientation;

    boolean isExpanded;

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        originalOrientation = attrs.getAttributeValue(ANDROID_SCHEME, "orientation");
        setOrientation(VERTICAL);

        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ExpandableLayout, defStyleAttr, 0);

        try {
            titleText = a.getString(R.styleable.ExpandableLayout_text_title);
            detailsText = a.getString(R.styleable.ExpandableLayout_text_details);
            imageResource = a.getResourceId(R.styleable.ExpandableLayout_src_image,
                    android.R.color.transparent);
            headerColor = a.getColor(R.styleable.ExpandableLayout_color_header,
                    getResources().getColor(R.color.md_grey_200));
            contentColor = a.getColor(R.styleable.ExpandableLayout_color_content,
                    getResources().getColor(R.color.md_grey_50));
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle savedState = (Bundle) state;
            isExpanded = savedState.getBoolean(SAVE_STATE_EXPANDED, false);
            state = savedState.getParcelable(SAVE_STATE_LAYOUT);
        }
        if (isExpanded()) {
            show();
        } else {
            hide();
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle outState = new Bundle();
        outState.putParcelable(SAVE_STATE_LAYOUT, super.onSaveInstanceState());
        outState.putBoolean(SAVE_STATE_EXPANDED, isExpanded());

        return outState;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        header.setOnClickListener(l);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        content = new LinearLayout(getContext());
        content.setBackgroundColor(contentColor);
        content.setOrientation((originalOrientation.equals("1")) ?
                LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(0);
            removeViewAt(0);
            content.addView(child);
        }

        setupHeader();

        content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(content);
        content.setVisibility(GONE);

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    protected void setupHeader() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.expandable_layout_header, this, false);
        addView(v, 0);


        header = ButterKnife.findById(this, R.id.header);
        header.setBackgroundColor(headerColor);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(titleText);
        title.setTextColor(getResources().getColor(R.color.md_black_1000));

        TextView details = (TextView) findViewById(R.id.exp_item_large_details);
        details.setText(detailsText);
        details.setTextColor(getResources().getColor(R.color.md_grey_600));

        ImageView image = (ImageView) findViewById(R.id.image);
        image.setImageResource(imageResource);

        icon = ButterKnife.findById(this ,R.id.expand_icon);
    }

    int contentPosition = 0;

    public void addContent(View v) {
        content.addView(v, contentPosition++);
    }

    public void eraseContent() {
        for(; contentPosition > 0;) {
            content.removeViewAt(--contentPosition);
        }
    }

    public int getContentAmount() {
        return content.getChildCount() - 1;
    }

    public void show() {
        if (onExpandListener != null) {
            onExpandListener.onExpand(this);
        }
        isExpanded = true;
        content.setVisibility(View.VISIBLE);
    }

    public void hide() {
        isExpanded = false;
        content.setVisibility(View.GONE);
    }

    public void collapse() {
        isExpanded = false;

        animateRotateBackward(icon);
        animateCollapse(content);
    }

    public void expand() {
        if (onExpandListener != null) {
            onExpandListener.onExpand(this);
        }
        isExpanded = true;

        animateRotateStraight(icon);
        animateExpand(content);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private void animateExpand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void animateCollapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void animateRotateStraight(View v) {
        Animation iconRotation = AnimationUtils.loadAnimation(getContext(),
                R.anim.expandable_icon_rotation_straight);
        v.startAnimation(iconRotation);
    }

    private void animateRotateBackward(View v) {
        Animation iconRotation = AnimationUtils.loadAnimation(getContext(),
                R.anim.expandable_icon_rotation_backward);
        v.startAnimation(iconRotation);
    }

    private OnExpandListener onExpandListener;
    public void setOnExpandListener(OnExpandListener l) {
        this.onExpandListener = l;
    }
    public interface OnExpandListener {
        void onExpand(ExpandableLayout v);
    }
}
