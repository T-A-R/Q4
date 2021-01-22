package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    public class PageSelectViewHolder extends RecyclerView.ViewHolder {

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
            for (AnswerState answerState : pageAnswersStates.get(item.getRelative_id())) {
                if (answerState.isChecked() && answerState.getData() != null && answerState.getData().length() > 0) {
                    if (answerTextBuilder == null) {
                        answerTextBuilder = new StringBuilder();
                    } else {
                        answerTextBuilder.append("; ");
                    }
                    answerTextBuilder.append(answerState.getData());
                }
            }
            if (answerTextBuilder != null) {
                answerText.setText(answerTextBuilder.toString());
            }

            cont.setOnClickListener(v -> showSelectDialog(item));
        }

        @SuppressLint("RestrictedApi")
        public void showSelectDialog(final ElementItemR item) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
            View layoutView = mActivity.getLayoutInflater().inflate(R.layout.dialog_select, null);
            View mOkBtn = layoutView.findViewById(R.id.select_ok_button);
            RecyclerView recyclerView = layoutView.findViewById(R.id.select_recyclerview);
            TextView questionTitle = layoutView.findViewById(R.id.select_title);
            questionTitle.setText(item.getElementOptionsR().getTitle());
            EditText selectInput = layoutView.findViewById(R.id.select_input);
            SelectAdapter selectAdapter = new SelectAdapter(mActivity, item.getElements(), new SelectAdapter.OnAnswerClickListener() {
                @Override
                public void onAnswerClick(int position, boolean enabled, String answer) {
                    List<AnswerState> answerStates = pageAnswersStates.get(item.getRelative_id());
                    AnswerState state = answerStates.get(position);
                    state.setChecked(enabled);
                    state.setData(answer);
                    answerStates.set(position, state);
                    pageAnswersStates.put(item.getRelative_id(), answerStates);
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            recyclerView.setAdapter(selectAdapter);

            dialogBuilder.setView(layoutView, 10, 40, 10, 10);
            AlertDialog selectDialog = dialogBuilder.create();
            selectDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            selectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
            selectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            mOkBtn.setOnClickListener(v -> {
                selectDialog.dismiss();
                notifyDataSetChanged();
                onItemClickListener.onAnswerClick(item.getRelative_id(), true, null);
            });

            if (mActivity != null && !mActivity.isFinishing()) selectDialog.show();
        }
    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int relativeId, boolean enabled, String answer);
    }

    public Map<Integer, List<AnswerState>> getAnswers() {
        return pageAnswersStates;
    }

    public void setAnswers(Map<Integer, List<AnswerState>> pageAnswersStates) {
        this.pageAnswersStates = pageAnswersStates;
    }
}
