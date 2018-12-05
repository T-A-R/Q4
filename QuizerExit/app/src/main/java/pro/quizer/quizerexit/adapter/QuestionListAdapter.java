package pro.quizer.quizerexit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.RecyclerViewActivity;
import pro.quizer.quizerexit.model.AttributeOpenType;
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
        EditText mEditText;

        AnswerListViewHolder(final View itemView) {
            super(itemView);
            mAnswer = itemView.findViewById(R.id.answer_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
            mEditText = itemView.findViewById(R.id.answer_edit_text);
        }

        @Override
        public void onBind(final ElementModel pAnswer, final int pPosition) {
            final AttributesModel attributes = pAnswer.getAttributes();

            final String openType = attributes.getOpenType();
            final boolean isChecked = pAnswer.isChecked();
            final boolean isEnabled = pAnswer.isEnabled();

            if (!AttributeOpenType.CHECKBOX.equals(openType)) {
                mEditText.setVisibility(View.VISIBLE);
                mEditText.setHint(attributes.getPlaceholder());

                switch (attributes.getOpenType()) {
                    case AttributeOpenType.TIME:
                        mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);

                        break;
                    case AttributeOpenType.DATE:
                        mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);

                        break;
                    case AttributeOpenType.NUMBER:
                        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        break;
                    case AttributeOpenType.TEXT:
                        mEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                        break;
                    default:
                        // неизвестный тип open_type
                }
            } else {
                mEditText.setVisibility(View.GONE);
            }

            mEditText.setEnabled(isChecked && isEnabled);
            mAnswer.setText(attributes.getTitle());
            mCheckBox.setChecked(isChecked);
            mCheckBox.setTag(pPosition);
            mCheckBox.setEnabled(isEnabled);
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