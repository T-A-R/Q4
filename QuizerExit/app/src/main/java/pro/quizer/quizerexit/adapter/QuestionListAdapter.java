package pro.quizer.quizerexit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.RecyclerViewActivity;
import pro.quizer.quizerexit.model.config.AttributesModel;
import pro.quizer.quizerexit.model.config.ElementModel;

public class QuestionListAdapter extends AbstractQuestionAdapter<QuestionListAdapter.AnswerListViewHolder> {

    public QuestionListAdapter(final Context pContext, final List<ElementModel> pAnswers, final int pMaxAnswer, final int pMinAnswer) {
        super(pContext, pAnswers, pMaxAnswer, pMinAnswer);
    }

    @NonNull
    @Override
    public AnswerListViewHolder onCreateViewHolder(@NonNull final ViewGroup pViewGroup, final int pPosition) {
        final View itemView = LayoutInflater.from(pViewGroup.getContext()).inflate(R.layout.adapter_answer_list, pViewGroup, false);
        return new AnswerListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerListViewHolder pAnswerListViewHolder, int pPosition) {
        pAnswerListViewHolder.onBind(getModel(pPosition), pPosition);
    }

    class AnswerListViewHolder extends AbstractViewHolder {

        TextView mAnswer;
        CheckBox mCheckBox;

        AnswerListViewHolder(final View itemView) {
            super(itemView);
            mAnswer = itemView.findViewById(R.id.answer_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
        }

        @Override
        public void onBind(final ElementModel pAnswer, final int pPosition) {
            final AttributesModel attributes = pAnswer.getAttributes();

            mAnswer.setText(attributes.getText());
            mCheckBox.setChecked(pAnswer.isChecked());
            mCheckBox.setTag(pPosition);
            mCheckBox.setEnabled(pAnswer.isEnabled());
            mCheckBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View view) {
                    final Context context = view.getContext();
                    final CheckBox checkBox = (CheckBox) view;
                    final boolean isChecked = checkBox.isChecked();
                    final int minAnswers = getMinAnswer();
                    final int maxAnswers = getMaxAnswer();
                    final int checkedItemsCount = getCheckedItemsCount();

                    if (isChecked && minAnswers == RecyclerViewActivity.DEFAULT_MIN_ANSWERS && minAnswers == maxAnswers) {
                        unselectAll();

                        pAnswer.setChecked(true);

                        notifyAdapter();
                    } else if (isChecked && maxAnswers != RecyclerViewActivity.EMPTY_COUNT_ANSWER && checkedItemsCount >= maxAnswers) {
                        checkBox.setChecked(false);

                        Toast.makeText(context,
                                String.format(context.getString(R.string.incorrect_max_selected_answers), String.valueOf(maxAnswers)),
                                Toast.LENGTH_LONG).show();
                    } else if (attributes.isUnchecker()) {
                        if (isChecked) {
                            setCheckedItemsCount(1);
                            unselectAll();
                            disableOther(pAnswer.getRelativeID());
                        } else {
                            setCheckedItemsCount(0);
                            enableAll();
                        }

                        pAnswer.setChecked(isChecked);
                        notifyAdapter();
                    } else {
                        if (isChecked) {
                            setCheckedItemsCount(checkedItemsCount + 1);
                        } else {
                            setCheckedItemsCount(checkedItemsCount - 1);
                        }

                        pAnswer.setChecked(isChecked);

                        notifyAdapter();
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View pView) {
                    if (mCheckBox.isEnabled()) {
                        mCheckBox.performClick();
                    }
                }
            });
        }
    }
}