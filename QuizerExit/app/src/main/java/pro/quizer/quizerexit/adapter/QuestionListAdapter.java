package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.SimpleTextWatcher;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.OptionsOpenType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class QuestionListAdapter extends AbstractQuestionAdapter<QuestionListAdapter.AnswerListViewHolder> {

    private static final int EMPTY_COUNT_ANSWER = 1;
    private static final int DEFAULT_MIN_ANSWERS = 1;

    private final boolean mIsPolyAnswers;
    private final String mDefaultPlaceHolder;
    private Runnable mRefreshRunnable;

    public QuestionListAdapter(final ElementModel pCurrentElement,
                               final Context pContext,
                               final List<ElementModel> pAnswers,
                               final int pMaxAnswer,
                               final int pMinAnswer,
                               final Runnable pRefreshRunnable) {
        super(pCurrentElement, pContext, pAnswers, pMaxAnswer, pMinAnswer);

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
        final ElementModel elementModel = getModel(pPosition);

        pAnswerListViewHolder.onBind(getModel(pPosition), pPosition);

        if (elementModel != null && !elementModel.getOptions().isCanShow(getBaseActivity(), getMap(), elementModel)) {
            pAnswerListViewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        }
    }

    class AnswerListViewHolder extends AbstractViewHolder {

        TextView mAnswer;
        CompoundButton mCheckBox;

        AnswerListViewHolder(final View itemView) {
            super(itemView);
            mAnswer = itemView.findViewById(R.id.answer_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pAnswer, final int pPosition) {
            final OptionsModel options = pAnswer.getOptions();

            final String openType = options.getOpenType();
            final boolean isChecked = pAnswer.isChecked();
            final boolean isEnabled = pAnswer.isEnabled();
            final boolean isEditTextEnabled = isChecked && isEnabled;
            final Context context = mEditText.getContext();

            if (!OptionsOpenType.CHECKBOX.equals(openType)) {
                final String placeholder = options.getPlaceholder();
                final String textAnswer = pAnswer.getTextAnswer();
                final boolean isPicker = options.getOpenType().equals(OptionsOpenType.TIME) || options.getOpenType().equals(OptionsOpenType.DATE);

                mEditText.setVisibility(View.VISIBLE);
                mEditText.setHint(StringUtils.isEmpty(placeholder) ? mDefaultPlaceHolder : placeholder);
                mEditText.setTag(pPosition);
                mEditText.setText(textAnswer);
                mEditText.setEnabled(isEditTextEnabled);
                mEditText.addTextChangedListener(new ElementTextWatcher(pAnswer, mEditText, isPicker));

                switch (options.getOpenType()) {
                    case OptionsOpenType.TIME:
                        mEditText.setFocusableInTouchMode(false);
                        mEditText.setHint(R.string.hint_time);
                        mEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setTime(view);
                            }
                        });
                        break;
                    case OptionsOpenType.DATE:
                        mEditText.setFocusableInTouchMode(false);
                        mEditText.setHint(R.string.hint_date);
                        mEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setDate(view);
                            }
                        });
                        break;
                    case OptionsOpenType.NUMBER:
                        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        break;
                    case OptionsOpenType.TEXT:
                        mEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                        break;
                    default:
                        // неизвестный тип open_type
                }
            } else {
                mEditText.setVisibility(View.GONE);
            }

            UiUtils.setTextOrHide(mAnswer, options.getTitle((BaseActivity) context));
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
                    } else if (options.isUnchecker()) {
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

    public static class ElementTextWatcher extends SimpleTextWatcher {

        private ElementModel mAnswer;
        private EditText mEditText;
        private boolean mIsPicker;

        ElementTextWatcher(final ElementModel pAnswer, final EditText pEditText, final boolean pIsPicker) {
            mAnswer = pAnswer;
            mEditText = pEditText;
            mIsPicker = pIsPicker;
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);

            final String answer = s.toString();
            mAnswer.setTextAnswer(answer);

            if (mEditText.isEnabled() && StringUtils.isEmpty(answer)) {
                mEditText.setBackgroundResource(R.drawable.edit_text_red_border);
            } else {
                mEditText.setBackgroundResource(R.drawable.edit_text_transparent_border);
            }

        }
    }
}