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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageSelectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private final List<ElementItemR> questions;
    private Map<Integer, List<AnswerState>> pageAnswersStates;
    private int lastSelectedPosition = -1;
    public boolean isPressed = false;
    private final MainActivity mActivity;

    public PageAdapter(final Context context, List<ElementItemR> questions, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.questions = questions;
        this.onAnswerClickListener = onAnswerClickListener;
        this.pageAnswersStates = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            List<AnswerState> answersStates = new ArrayList<>();
            List<ElementItemR> answers = questions.get(i).getElements();
            for (int k = 0; k < questions.get(i).getElements().size(); k++) {
                answersStates.add(new AnswerState(answers.get(k).getRelative_id(), false, ""));
            }
            pageAnswersStates.put(questions.get(i).getRelative_id(), answersStates);
        }
    }

    @NonNull
    @Override
    public PageSelectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_select_avia, viewGroup, false);
        return new PageSelectViewHolder(view, onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PageSelectViewHolder holder, int position) {
        holder.bind(questions.get(position), position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class PageSelectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView questionText;
        TextView answerText;
        View cont;

        OnAnswerClickListener onItemClickListener;

        public PageSelectViewHolder(@NonNull View itemView, OnAnswerClickListener onItemClickListener) {
            super(itemView);

            cont = itemView.findViewById(R.id.cont_select_holder);
            questionText = itemView.findViewById(R.id.select_answer_title);
            answerText = itemView.findViewById(R.id.answer_select_text);
            questionText.setTypeface(Fonts.getFuturaPtBook());
            answerText.setTypeface(Fonts.getFuturaPtBook());

            this.onItemClickListener = onItemClickListener;
        }

        public void bind(final ElementItemR item, int position) {
            questionText.setText(item.getElementOptionsR().getTitle());
            StringBuilder answerTextBuilder = null;
            for(AnswerState answerState : pageAnswersStates.get(item.getRelative_id())) {
                if(answerState.isChecked() && answerState.getData() != null && answerState.getData().length() > 0) {
                    if(answerTextBuilder == null) {
                        answerTextBuilder = new StringBuilder();
                    } else {
                        answerTextBuilder.append("; ");
                    }
                    answerTextBuilder.append(answerState.getData());
                }
            }
            if(answerTextBuilder != null) {
                answerText.setText(answerTextBuilder.toString());
            }

            cont.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            isPressed = true;
            lastSelectedPosition = getAdapterPosition();

            for (int a = 0; a < answersState.size(); a++) {
                answersState.get(a).setChecked(a == lastSelectedPosition);
            }

            notifyDataSetChanged();
            onItemClickListener.onAnswerClick(lastSelectedPosition, true, null);
        }
    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int position, boolean enabled, String answer);
    }

    public List<AnswerState> getAnswers() {
        return answersState;
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
