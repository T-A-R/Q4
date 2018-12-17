package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.SimpleTextWatcher;
import pro.quizer.quizerexit.model.AttributeOpenType;
import pro.quizer.quizerexit.model.config.AttributesModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.StringUtils;

public class QuestionListAdapter extends AbstractQuestionAdapter<QuestionListAdapter.AnswerListViewHolder> {

    private static final int EMPTY_COUNT_ANSWER = 1;
    private static final int DEFAULT_MIN_ANSWERS = 1;

    private final boolean mIsPolyAnswers;
    private final String mDefaultPlaceHolder;
    private Runnable mRefreshRunnable;

    public QuestionListAdapter(final Context pContext,
                               final List<ElementModel> pAnswers,
                               final int pMaxAnswer,
                               final int pMinAnswer,
                               final Runnable pRefreshRunnable) {
        super(pContext, pAnswers, pMaxAnswer, pMinAnswer);

        mRefreshRunnable = pRefreshRunnable;
        mDefaultPlaceHolder = pContext.getString(R.string.default_placeholder);
        mIsPolyAnswers = pMaxAnswer == 1 && pMinAnswer == 1;
    }

    @NonNull
    @Override
    public AnswerListViewHolder onCreateViewHolder(@NonNull final ViewGroup pViewGroup, final int pPosition) {
        if (mIsPolyAnswers) {
            final View itemView = LayoutInflater.from(pViewGroup.getContext()).inflate(R.layout.adapter_answer_list_radio, pViewGroup, false);
            return new AnswerListViewHolder(itemView);
        } else {
            final View itemView = LayoutInflater.from(pViewGroup.getContext()).inflate(R.layout.adapter_answer_list_checkbox, pViewGroup, false);
            return new AnswerListViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerListViewHolder pAnswerListViewHolder, int pPosition) {
        pAnswerListViewHolder.onBind(getModel(pPosition), pPosition);
    }

    class AnswerListViewHolder extends AbstractViewHolder {

        TextView mAnswer;
        CompoundButton mCheckBox;
        EditText mEditText;

        AnswerListViewHolder(final View itemView) {
            super(itemView);
            mAnswer = itemView.findViewById(R.id.answer_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
            mEditText = itemView.findViewById(R.id.answer_edit_text);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pAnswer, final int pPosition) {
            final AttributesModel attributes = pAnswer.getOptions();

            final String openType = attributes.getOpenType();
            final boolean isChecked = pAnswer.isChecked();
            final boolean isEnabled = pAnswer.isEnabled();

            if (!AttributeOpenType.CHECKBOX.equals(openType)) {
                final String placeholder = attributes.getPlaceholder();
                final String textAnswer = pAnswer.getTextAnswer();

                mEditText.setVisibility(View.VISIBLE);
                mEditText.setHint(StringUtils.isEmpty(placeholder) ? mDefaultPlaceHolder : placeholder);
                mEditText.setTag(pPosition);
                mEditText.setText(textAnswer);
                mEditText.addTextChangedListener(new ElementTextWatcher(pAnswer));

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
                    final CompoundButton checkBox = (CompoundButton) view;
                    final boolean isChecked = checkBox.isChecked();
                    final int minAnswers = getMinAnswer();
                    final int maxAnswers = getMaxAnswer();
                    final int checkedItemsCount = getCheckedItemsCount();

                    if (isChecked && minAnswers == DEFAULT_MIN_ANSWERS && minAnswers == maxAnswers) {
                        unselectAll();

                        pAnswer.setChecked(true);

                        if (mRefreshRunnable != null) {
                            mRefreshRunnable.run();
                        }
                    } else if (isChecked && maxAnswers != EMPTY_COUNT_ANSWER && checkedItemsCount >= maxAnswers) {
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

                        if (mRefreshRunnable != null) {
                            mRefreshRunnable.run();
                        }
                    } else {
                        if (isChecked) {
                            setCheckedItemsCount(checkedItemsCount + 1);
                        } else {
                            setCheckedItemsCount(checkedItemsCount - 1);
                        }

                        pAnswer.setChecked(isChecked);

                        if (mRefreshRunnable != null) {
                            mRefreshRunnable.run();
                        }
                    }
                }
            });

//            mEditText.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        itemView.performClick();
//                        mEditText.requestFocus();
//                    }
//
//                    return false;
//                }
//            });

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

    public class ElementTextWatcher extends SimpleTextWatcher {

        private ElementModel mAnswer;

        ElementTextWatcher(final ElementModel pAnswer) {
            mAnswer = pAnswer;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);

            mAnswer.setTextAnswer(s.toString());
            Log.d("thecriser", mAnswer.getOptions().getTitle() + " = " + s.toString());
        }
    }
}