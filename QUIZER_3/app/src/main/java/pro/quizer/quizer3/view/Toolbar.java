package pro.quizer.quizer3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;

import pro.quizer.quizer3.R;


public class Toolbar extends RelativeLayout implements Serializable {

    private ImageView mIcon;
    private ImageView mLogo;
    private TextView mTitle;
    private View mCloseView;
    private View mInfoView;
    private View mOptionsView;

    public Toolbar(final Context context) {
        super(context);
        init();
    }

    public Toolbar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Toolbar(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTitle(final String pTitle) {
        mIcon.setVisibility(View.VISIBLE);
        mLogo.setVisibility(View.GONE);
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(pTitle);
    }

    public void showLogo() {
        mIcon.setVisibility(View.GONE);
        mLogo.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.GONE);
    }

    private boolean containsFlag(final int flagSet, final int flag) {
        final int result = flagSet | flag;

        return result == flagSet;
    }

    private void inflate() {
        inflate(getContext(), R.layout.view_toolbar, this);

        mLogo = findViewById(R.id.toolbar_view_logo);
        mIcon = findViewById(R.id.icon);
        mTitle = findViewById(R.id.toolbar_view_title);
        mCloseView = findViewById(R.id.toolbar_view_close);
        mInfoView = findViewById(R.id.toolbar_view_info);
        mOptionsView = findViewById(R.id.toolbar_view_options);
    }

    private void init() {
        inflate();
    }

    public void showOptionsView(final OnClickListener pOnClickListener, final OnClickListener pOnInfoClickListener) {
        mCloseView.setVisibility(View.GONE);
        mInfoView.setVisibility(View.GONE);
        mOptionsView.setVisibility(View.VISIBLE);
        mOptionsView.setOnClickListener(pOnClickListener);
        mInfoView.setOnClickListener(pOnInfoClickListener);
    }

    public void showCloseView(final OnClickListener pOnClickListener) {
        mOptionsView.setVisibility(View.GONE);
        mInfoView.setVisibility(View.GONE);
        mCloseView.setVisibility(View.VISIBLE);
        mCloseView.setOnClickListener(pOnClickListener);
    }

    public void showInfoView() {
        mInfoView.setVisibility(View.VISIBLE);
    }

    public void hideInfoView() {
        mInfoView.setVisibility(View.GONE);
    }
}