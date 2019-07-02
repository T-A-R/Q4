package pro.quizer.quizerexit.view.resizeble.margin;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.Serializable;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;

public class MarginView extends RelativeLayout implements Serializable {

    public MarginView(final Context pContext) {
        super(pContext);
        init();
    }

    public MarginView(final Context pContext, final AttributeSet pAttrs) {
        super(pContext, pAttrs);
        init();
    }

    public MarginView(final Context pContext, final AttributeSet pAttrs, final int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        init();
    }

    private void inflate() {
        inflate(getContext(), R.layout.view_margin, this);

        initViews(getRootView());
    }

    private void init() {
        inflate();
    }

    private void initViews(final View pView) {
        final ViewGroup view = pView.findViewById(R.id.margin_view);
        ResizableViewUtils.initViewHeight(view, getContext());
    }

}