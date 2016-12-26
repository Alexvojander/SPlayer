package com.dmplayer.ui.externalprofile.layout;


import android.content.Context;
import android.widget.LinearLayout;

public class ChildForSwapping extends LinearLayout {
    private int layoutId;

    public ChildForSwapping(Context context, int layoutId) {
        super(context);
        this.layoutId = layoutId;

        setLayoutId(layoutId);
        inflate(getContext(), layoutId, this);
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
