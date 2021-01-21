package pro.quizer.quizer3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;

import static pro.quizer.quizer3.MainActivity.TAG;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageObjectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private final ElementItemR question;
    private final List<ElementItemR> answersList;
    private List<AnswerState> answersState;
    private int lastSelectedPosition = -1;
    public boolean isPressed = false;
    private final MainActivity mActivity;
    private String typeBehavior;
    public boolean show_scale;
    public boolean show_images;

    public PageAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.question = question;
        this.answersList = answersList;
        this.onAnswerClickListener = onAnswerClickListener;
        if (question.getElementOptionsR().getType_behavior() != null) {
            this.typeBehavior = question.getElementOptionsR().getType_behavior();
        }
        this.show_scale = question.getElementOptionsR().isShow_scale();
        this.show_images = question.getElementOptionsR().isShow_images();
        this.answersState = new ArrayList<>();
        for (int i = 0; i < answersList.size(); i++) {
            this.answersState.add(new AnswerState(answersList.get(i).getRelative_id(), false, ""));
        }
    }

    @NonNull
    @Override
    public PageObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(MainActivity.AVIA ? R.layout.holder_answer_scale_avia : mActivity.isAutoZoom() ? R.layout.holder_answer_scale_auto : R.layout.holder_answer_scale, viewGroup, false);
        return new PageObjectViewHolder(view, onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PageObjectViewHolder holder, int position) {
        holder.bind(answersList.get(position), position, typeBehavior);
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class PageObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView scaleText;
        TextView divider;
        ImageView scaleImage;
        View cont;

        OnAnswerClickListener onUserClickListener;

        public PageObjectViewHolder(@NonNull View itemView, OnAnswerClickListener onUserClickListener) {
            super(itemView);

            cont = itemView.findViewById(R.id.scale_cont);
            scaleText = itemView.findViewById(R.id.scale_text);
            divider = itemView.findViewById(R.id.divider);
            scaleImage = itemView.findViewById(R.id.scale_image);
            scaleText.setTypeface(Fonts.getFuturaPtBook());

            this.onUserClickListener = onUserClickListener;
        }

        public void bind(final ElementItemR item, int position, String type) {
            cont.setOnClickListener(this);
            if (show_scale) {
                scaleText.setText(item.getElementOptionsR().getTitle());
            } else {
                scaleText.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            }

            if (show_images) {
                scaleImage.setVisibility(View.VISIBLE);
            } else {
                scaleImage.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            }
        }


        @Override
        public void onClick(View v) {
            isPressed = true;
            lastSelectedPosition = getAdapterPosition();

            for (int a = 0; a < answersState.size(); a++) {
                answersState.get(a).setChecked(a == lastSelectedPosition);
            }

            notifyDataSetChanged();
            onUserClickListener.onAnswerClick(lastSelectedPosition, true, null);
        }
    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int position, boolean enabled, String answer);
    }

    public List<AnswerState> getAnswers() {
        return answersState;
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.lastSelectedPosition = lastSelectedPosition;
        Log.d(TAG, "setLastSelectedPosition: " + lastSelectedPosition);
    }

    public void setAnswers(List<AnswerState> answers) {
        Log.d(TAG, "=============================");
        if (answers != null) {
            for (int i = 0; i < answers.size(); i++) {
                Log.d(TAG, i + ": " + answers.get(i).getRelative_id() + " : " + answers.get(i).getData() + " : " + answers.get(i).isChecked());
            }
            this.answersState = answers;
            for (int i = 0; i < answers.size(); i++) {
                if (answersList.get(i).getElementOptionsR().isUnchecker() && answers.get(i).isChecked()) {
                    for (int k = 0; k < answersList.size(); k++) {
                        if (k != i) {
                            Log.d(TAG, "set false 5: " + k);
                            answersList.get(k).setEnabled(false);
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "setAnswers: NULL");
        }
    }
}
