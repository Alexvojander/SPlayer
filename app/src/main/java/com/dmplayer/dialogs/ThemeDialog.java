package com.dmplayer.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.dmplayer.R;

import butterknife.BindView;
import butterknife.OnClick;


public class ThemeDialog extends BaseDialogFragment {
    @BindView(R.id.theme_card_blue)
    CardView themeBlue;
    @BindView(R.id.theme_card_red)
    CardView themeRed;
    @BindView(R.id.theme_card_green)
    CardView themeGreen;
    @BindView(R.id.theme_card_orange)
    CardView themeOrange;
    @BindView(R.id.theme_card_pink)
    CardView themePink;
    @BindView(R.id.theme_card_indigo)
    CardView themeIndigo;
    @BindView(R.id.theme_card_brown)
    CardView themeBrown;
    @BindView(R.id.theme_card_blue_grey)
    CardView themeBlueGray;
    @BindView(R.id.theme_card_falcon)
    CardView themeFalcon;
    @BindView(R.id.theme_card_light_blue)
    CardView themeLightBlue;

    @BindView(R.id.button_agree)
    Button buttonAgree;
    @BindView(R.id.button_disagree)
    Button buttonDisagree;

    private SharedPreferences sharedPreferences;
    private int currentTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_theme;
    }

    private void init() {
        sharedPreferences = getActivity().getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        currentTheme = sharedPreferences.getInt("THEME", 0);
    }

    @OnClick(R.id.theme_card_blue)
    public void setThemeBlue() {
        currentTheme = 1;
    }

    @OnClick(R.id.theme_card_red)
    public void setThemeRed() {
        currentTheme = 2;
    }

    @OnClick(R.id.theme_card_green)
    public void setThemeGreen() {
        currentTheme = 3;
    }

    @OnClick(R.id.theme_card_orange)
    public void setThemeOrange() {
        currentTheme = 4;
    }

    @OnClick(R.id.theme_card_pink)
    public void setThemePink() {
        currentTheme = 5;
    }

    @OnClick(R.id.theme_card_indigo)
    public void setThemeIndigo() {
        currentTheme = 6;
    }

    @OnClick(R.id.theme_card_brown)
    public void setThemeBrown() {
        currentTheme = 7;
    }

    @OnClick(R.id.theme_card_blue_grey)
    public void setThemeBlueGray() {
        currentTheme = 8;
    }

    @OnClick(R.id.theme_card_falcon)
    public void setThemeFalcon() {
        currentTheme = 9;
    }

    @OnClick(R.id.theme_card_light_blue)
    public void setThemeLightBlue() {
        currentTheme = 10;
    }

    @OnClick(R.id.button_agree)
    public void agree() {
        sharedPreferences.edit()
                .putInt("THEME", currentTheme)
                .apply();

        if (onWorkDone != null) {
            onWorkDone.onAgree();
        }
        dismiss();
    }

    @OnClick(R.id.button_disagree)
    public void disagree() {
        if (onWorkDone != null) {
            onWorkDone.onRefuse();
        }
        dismiss();
    }


    private OnWorkDone onWorkDone;
    public void setOnItemChoose(OnWorkDone onWorkDone) {
        this.onWorkDone = onWorkDone;
    }
}