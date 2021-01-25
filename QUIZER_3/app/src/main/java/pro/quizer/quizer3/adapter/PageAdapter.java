package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.state.SelectItem;
import pro.quizer.quizer3.utils.Fonts;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageSelectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private final List<ElementItemR> questions;
    private Map<Integer, List<AnswerState>> pageAnswersStates;
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
        TextView answerSelectText;
        View cont;

        OnAnswerClickListener onItemClickListener;

        public PageSelectViewHolder(@NonNull View itemView, OnAnswerClickListener onItemClickListener) {
            super(itemView);

            cont = itemView.findViewById(R.id.cont_select_holder);
            questionText = itemView.findViewById(R.id.select_answer_title);
            answerText = itemView.findViewById(R.id.selected_answers);
            answerSelectText = itemView.findViewById(R.id.answer_select_text);
            questionText.setTypeface(Fonts.getFuturaPtBook());
            answerText.setTypeface(Fonts.getFuturaPtBook());
            answerSelectText.setTypeface(Fonts.getFuturaPtBook());

            this.onItemClickListener = onItemClickListener;
        }

        public void bind(final ElementItemR item, int position) {
            questionText.setText(item.getElementOptionsR().getTitle());
            StringBuilder answerTextBuilder = null;
            List<AnswerState> answerStates = pageAnswersStates.get(item.getRelative_id());
            List<ElementItemR> answersList = item.getElements();

            for (int i = 0; i < answerStates.size(); i++) {
                if (answerStates.get(i).isChecked()) {
                    if (answerTextBuilder == null) {
                        answerTextBuilder = new StringBuilder();
                        answerText.setVisibility(View.VISIBLE);
                    } else {
                        answerTextBuilder.append("; ");
                    }
                    answerTextBuilder.append(answersList.get(i).getElementOptionsR().getTitle());
                }
            }
            if (answerTextBuilder != null) {
                answerText.setText(answerTextBuilder.toString());
            }

            cont.setOnClickListener(v -> showSelectDialog(item, answerStates));
        }

        @SuppressLint("RestrictedApi")
        public void showSelectDialog(final ElementItemR item, final List<AnswerState> answerStates) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
            View layoutView = mActivity.getLayoutInflater().inflate(R.layout.dialog_select, null);
            View mOkBtn = layoutView.findViewById(R.id.select_ok_button);
            RecyclerView recyclerView = layoutView.findViewById(R.id.select_recyclerview);
            TextView questionTitle = layoutView.findViewById(R.id.select_title);
            questionTitle.setText(item.getElementOptionsR().getTitle());
            EditText selectInput = layoutView.findViewById(R.id.select_input);
            List<ElementItemR> elementItems = item.getElements();
            List<SelectItem> selectItems = new ArrayList<>();
            for (int i = 0; i < elementItems.size(); i++) {
                selectItems.add(new SelectItem(elementItems.get(i).getElementOptionsR().getTitle(), answerStates.get(i).isChecked(), answerStates.get(i).isEnabled()));
            }
            SelectAdapter selectAdapter = new SelectAdapter(selectItems, item.getElementOptionsR().isPolyanswer(), (answers) -> {
                for(int i = 0; i < answerStates.size(); i++) {
                    answerStates.get(i).setChecked(answers.get(i).isChecked());
                }
                pageAnswersStates.put(item.getRelative_id(), answerStates);
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            recyclerView.setAdapter(selectAdapter);

            dialogBuilder.setView(layoutView, 10, 40, 10, 10);
            AlertDialog selectDialog = dialogBuilder.create();
            selectDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            selectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
            selectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            selectDialog.setCancelable(false);

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
