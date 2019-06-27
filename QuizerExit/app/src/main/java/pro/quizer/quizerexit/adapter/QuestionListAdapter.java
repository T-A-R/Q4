package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.SimpleTextWatcher;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.OptionsOpenType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.CustomCheckableButton;

public class QuestionListAdapter extends AbstractQuestionAdapter<QuestionListAdapter.AnswerListViewHolder> {

    private static final int EMPTY_COUNT_ANSWER = 1;
    private static final int DEFAULT_MIN_ANSWERS = 1;

    private final boolean mIsPolyAnswers;
    private final String mDefaultPlaceHolder;
    private List<QuotaModel> mQuotas;
    private HashMap<Integer, ElementModel> mMap;

    public QuestionListAdapter(final ElementModel pCurrentElement,
                               final BaseActivity pBaseActivity,
                               final List<ElementModel> pAnswers,
                               final int pMaxAnswer,
                               final int pMinAnswer) {
        super(pCurrentElement, pBaseActivity, pAnswers, pMaxAnswer, pMinAnswer);

        mDefaultPlaceHolder = pBaseActivity.getString(R.string.TEXT_HINT_DEFAULT_PLACEHOLDER);
        mIsPolyAnswers = pMaxAnswer == 1 && pMinAnswer == 1;

        UserModel mUser = mBaseActivity.getCurrentUser();
        mQuotas = mUser.getQuotas();
        mMap = getMap();
    }

    @NonNull
    @Override
    public AnswerListViewHolder onCreateViewHolder(@NonNull final ViewGroup pViewGroup, final int pPosition) {
        if (mIsPolyAnswers) {
            final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_answer_list_radio, pViewGroup, false);
            return new AnswerListViewHolder(itemView);
        } else {
            final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_answer_list_checkbox, pViewGroup, false);
            return new AnswerListViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerListViewHolder pAnswerListViewHolder, int pPosition) {
        final ElementModel elementModel = getModel(pPosition);

        if (elementModel != null) {
            pAnswerListViewHolder.onBind(getModel(pPosition), pPosition);
        }

        if (elementModel == null || !elementModel.getOptions().isCanShow(mBaseActivity, getMap(), elementModel)) {
            pAnswerListViewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        } else {
            pAnswerListViewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    class AnswerListViewHolder extends AbstractViewHolder {

        TextView mAnswer;
        CustomCheckableButton mCheckBox;
        RecyclerView mContentsRecyclerView;

        AnswerListViewHolder(final View itemView) {
            super(itemView);
            mAnswer = itemView.findViewById(R.id.answer_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
            mContentsRecyclerView = itemView.findViewById(R.id.contents_recycler_view);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pAnswer, final int pPosition) {
            final OptionsModel options = pAnswer.getOptions();
            final String openType = options.getOpenType();
            final boolean isChecked = pAnswer.isChecked();
            final boolean isEnabled = pAnswer.isEnabled(mQuotas, mBaseActivity, mMap, pAnswer);
            final boolean isEditTextEnabled = isChecked && isEnabled;

            if (!OptionsOpenType.CHECKBOX.equals(openType)) {
                final String placeholder = options.getPlaceholder();
                final String textAnswer = pAnswer.getTextAnswer();

                mEditText.setVisibility(View.VISIBLE);
                mEditText.setHint(StringUtils.isEmpty(placeholder) ? mDefaultPlaceHolder : placeholder);
                mEditText.setTag(pPosition);
                mEditText.setEnabled(isEditTextEnabled);
                mEditText.clearTextChangedListeners();
                mEditText.addTextChangedListener(new ElementTextWatcher(getModel(pPosition), mEditText));
                mEditText.setText(textAnswer);

                switch (options.getOpenType()) {
                    case OptionsOpenType.TIME:
                        mEditText.setFocusableInTouchMode(false);
                        mEditText.setHint(R.string.TEXT_HINT_TIME);
                        mEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setTime(view);
                            }
                        });
                        break;
                    case OptionsOpenType.DATE:
                        mEditText.setFocusableInTouchMode(false);
                        mEditText.setHint(R.string.TEXT_HINT_DATE);
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

            final String title = options.getTitle(mBaseActivity);
            final String description = options.getDescription();

            if (StringUtils.isNotEmpty(description)) {
                final String titleAndDescription = title + " <span style='color:gray'><i>" + description + "</i></span>";
                UiUtils.setTextOrHide(mAnswer, titleAndDescription);
            } else {
                UiUtils.setTextOrHide(mAnswer, title);
            }

            final List<ElementModel> contents = pAnswer.getContents();

            if (contents != null && !contents.isEmpty()) {
                mContentsRecyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
                mContentsRecyclerView.setHasFixedSize(true);
                ContentElementsAdapter mAdapter = new ContentElementsAdapter(mBaseActivity, contents);
                mContentsRecyclerView.setAdapter(mAdapter);
                mContentsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mContentsRecyclerView.setVisibility(View.GONE);
            }

            mCheckBox.setChecked(isChecked);
            mCheckBox.setEnabled(isEnabled);
            mCheckBox.setChecked(isChecked);
            mCheckBox.setTag(pPosition);
            mCheckBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View view) {
                    final CustomCheckableButton checkBox = (CustomCheckableButton) view;
                    final boolean isChecked = checkBox.isChecked();
                    final int minAnswers = getMinAnswer();
                    final int maxAnswers = getMaxAnswer();
                    final int checkedItemsCount = getCheckedItemsCount();

                    if (isChecked && minAnswers == DEFAULT_MIN_ANSWERS && minAnswers == maxAnswers) {
                        unselectAll();

                        pAnswer.setChecked(true);

                        refresh();
                    } else if (isChecked && maxAnswers != EMPTY_COUNT_ANSWER && checkedItemsCount >= maxAnswers) {
                        checkBox.setChecked(false);

                        mBaseActivity.showToast(String.format(mBaseActivity.getString(R.string.NOTIFICATION_MAX_ANSWERS), String.valueOf(maxAnswers)));
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

                        refresh();
                    } else {
                        if (isChecked) {
                            setCheckedItemsCount(checkedItemsCount + 1);
                        } else {
                            setCheckedItemsCount(checkedItemsCount - 1);
                        }

                        pAnswer.setChecked(isChecked);

                        refresh();
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

    private void refresh() {
        notifyDataSetChanged();
//        if (mRefreshRunnable != null) {
//            mRefreshRunnable.run();
//        }
    }

    public class ElementTextWatcher extends SimpleTextWatcher {

        private ElementModel mAnswer;
        private EditText mEditText;

        ElementTextWatcher(final ElementModel pAnswer, final EditText pEditText) {
            mAnswer = pAnswer;
            mEditText = pEditText;
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