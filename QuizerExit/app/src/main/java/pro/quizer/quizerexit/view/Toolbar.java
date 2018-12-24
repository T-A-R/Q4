package pro.quizer.quizerexit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.Serializable;

import pro.quizer.quizerexit.R;

public class Toolbar extends RelativeLayout implements Serializable {

    private ImageView mTitle;
    private View mCloseView;
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

    private boolean containsFlag(final int flagSet, final int flag) {
        final int result = flagSet | flag;

        return result == flagSet;
    }

    private void inflate() {
        inflate(getContext(), R.layout.view_toolbar, this);

        mTitle = findViewById(R.id.toolbar_view_title);
        mCloseView = findViewById(R.id.toolbar_view_close);
        mOptionsView = findViewById(R.id.toolbar_view_options);
    }

    private void init() {
        inflate();
    }

    public void showOptionsView(final OnClickListener pOnClickListener) {
        mCloseView.setVisibility(View.GONE);
        mOptionsView.setVisibility(View.VISIBLE);
        mOptionsView.setOnClickListener(pOnClickListener);
    }

    public void showCloseView(final OnClickListener pOnClickListener) {
        mOptionsView.setVisibility(View.GONE);
        mCloseView.setVisibility(View.VISIBLE);
        mCloseView.setOnClickListener(pOnClickListener);
    }
}