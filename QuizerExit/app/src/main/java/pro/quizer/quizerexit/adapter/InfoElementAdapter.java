package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.OptionsType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class InfoElementAdapter extends RecyclerView.Adapter<InfoElementAdapter.InfoElementViewHolder> implements IAdapter {

    private final Context mContext;
    private final List<ElementModel> mContents;

    public InfoElementAdapter(final Context pContext, final List<ElementModel> pContents) {
        this.mContext = pContext;
        this.mContents = pContents;
    }

    @Override
    public int getItemCount() {
        return mContents.size();
    }

    @NonNull
    @Override
    public InfoElementViewHolder onCreateViewHolder(@NonNull final ViewGroup pViewGroup, final int pPosition) {
        final View itemView = LayoutInflater.from(pViewGroup.getContext()).inflate(R.layout.adapter_info_element, pViewGroup, false);
        return new InfoElementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InfoElementViewHolder pAnswerListViewHolder, final int pPosition) {
        pAnswerListViewHolder.onBind(mContents.get(pPosition), pPosition);
    }

    @Override
    public int processNext() throws Exception {
        return 0;
    }

    class InfoElementViewHolder extends AbstractViewHolder {

        TextView mText;

        InfoElementViewHolder(final View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.info_text);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pContent, final int pPosition) {
            final OptionsModel options = pContent.getOptions();

            switch (options.getType()) {
                case OptionsType.TEXT:
                    UiUtils.setTextOrHide(mText, options.getText());

                    break;
                case OptionsType.AUDIO:

                    break;

                case OptionsType.VIDEO:

                    break;
                case OptionsType.PHOTO:

                    break;
                default:

                    break;
            }
        }
    }

}