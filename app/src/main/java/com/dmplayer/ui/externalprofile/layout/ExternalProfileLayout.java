package com.dmplayer.ui.externalprofile.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dmplayer.R;
import com.dmplayer.ui.externalprofile.ExternalProfileModel;
import com.dmplayer.ui.externalprofile.ExternalProfileView;
import com.dmplayer.ui.externalprofile.ExternalProfileViewCallbacks;

public class ExternalProfileLayout extends LinearLayout implements ExternalProfileView {
    private LogInViewChild welcomeLayout;
    private ProfileViewChild profileLayout;
    private ChildForSwapping loadingLayout;

    public LogInViewChild getWelcomeLayout() {
        return welcomeLayout;
    }

    public ProfileViewChild getProfileLayout() {
        return profileLayout;
    }

    public ChildForSwapping getLoadingLayout() {
        return loadingLayout;
    }

    public ExternalProfileLayout(Context context) {
        super(context);
    }

    public ExternalProfileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExternalProfileLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ExternalProfileLayout, 0, 0);
        int welcomeLayoutRes;
        int profileLayoutRes;
        int loadingLayoutRes;
        try {
            welcomeLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_starting_layout, -1);
            profileLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_swapping_layout, -1);
            loadingLayoutRes = a.getResourceId(R.styleable.ExternalProfileLayout_loading_layout, -1);
        } finally {
            a.recycle();
        }

        welcomeLayout = new LogInViewChild(getContext(), welcomeLayoutRes);
        profileLayout = new ProfileViewChild(getContext(), profileLayoutRes);
        loadingLayout = new ChildForSwapping(getContext(), loadingLayoutRes);
    }

    @Override
    public void setCallbacks(final ExternalProfileViewCallbacks callbacks) {
        welcomeLayout.setOnLogInListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onLogIn();
            }
        });
        profileLayout.setOnRefreshListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onRefresh();
            }
        });
        profileLayout.setOnLogOutListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onLogOut();
            }
        });
    }

    @Override
    public void showProfile(ExternalProfileModel profile) {
        removeAllViews();
        addView(profileLayout);
        getProfileLayout().setProfile(profile);
    }

    @Override
    public void showLogIn() {
        removeAllViews();
        addView(welcomeLayout);
    }

    @Override
    public void showLoading() {
        removeAllViews();
        addView(loadingLayout);
    }
}
