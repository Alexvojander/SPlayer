package com.dmplayer.ui.externalprofile.layout;

import android.content.Context;
import android.widget.Button;

import com.dmplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogInViewChild extends ChildForSwapping {
    @BindView(R.id.external_login)
    Button logIn;

    public LogInViewChild(Context context, int layoutId) {
        super(context, layoutId);
        init();
    }

    void init() {
        ButterKnife.bind(this);
    }

    public void setOnLogInListener(OnClickListener l) {
        logIn.setOnClickListener(l);
    }
}
