package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.SimpleTextWatcher;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.OptionsOpenType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.CustomCheckableButton;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class QuestionListAdapter extends AbstractQuestionAdapter<QuestionListAdapter.AnswerListViewHolder> {

    private static final int EMPTY_COUNT_ANSWER = 1;
    private static final int DEFAULT_MIN_ANSWERS = 1;

    private final boolean mIsPolyAnswers;
    private final String mDefaultPlaceHolder;
    private UserModelR mUser;
    private List<QuotaModel> mQuotas;
    private HashMap<Integer, ElementModel> mMap;

    private boolean radioBtnIsPressed = false;
    private boolean onBind;
    private int focusPos;

    TimePickerDialog.OnTimeSetListener myCallBack;

    public QuestionListAdapter(final HashMap<Integer, ElementModel> pMap,
                               final ElementModel pCurrentElement,
                               final BaseActivity pContext,
                               final List<ElementModel> pAnswers,
                               final int pMaxAnswer,
                               final int pMinAnswer,
                               final UserModelR pUser
    ) {
        super(pMap, pCurrentElement, pContext, pAnswers, pMaxAnswer, pMinAnswer);

        mDefaultPlaceHolder = pContext.getString(R.string.TEXT_HINT_DEFAULT_PLACEHOLDER);
        mIsPolyAnswers = pMaxAnswer == 1 && pMinAnswer == 1;

        mUser = pUser;
        mQuotas = mUser.getQuotasR();
        mMap = getMap();
    }

    @NonNull
    @Override
    public AnswerListViewHolder onCreateViewHolder(@NonNull final ViewGroup pViewGroup, final int pPosition) {
        if (ElementSubtype.SCALE.equals(getCurrentElement().getSubtype())) {
            if (getCurrentElement().getOptions().isMedia()) {
                final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_answer_list_scale_media, pViewGroup, false);
                return new AnswerListViewHolder(itemView, mBaseActivity);
            } else {
                final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_answer_list_scale, pViewGroup, false);
                return new AnswerListViewHolder(itemView, mBaseActivity);
            }
        } else if (mIsPolyAnswers) {
            final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_answer_list_radio, pViewGroup, false);
            return new AnswerListViewHolder(itemView, mBaseActivity);
        } else {
            final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_answer_list_checkbox, pViewGroup, false);
            return new AnswerListViewHolder(itemView, mBaseActivity);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerListViewHolder pAnswerListViewHolder, int pPosition) {
        final ElementModel elementModel = getModel(pPosition);

        if (focusPos == pPosition) {
            pAnswerListViewHolder.mEditText.setFocusable(true);
            pAnswerListViewHolder.mEditText.setFocusableInTouchMode(true);
            pAnswerListViewHolder.mEditText.requestFocus();

            pAnswerListViewHolder.mAnswer.performClick();
        }
        if (pAnswerListViewHolder.mEmptyRadioButton != null)
            pAnswerListViewHolder.mEmptyRadioButton.setVisibility(View.VISIBLE);


        if (elementModel != null) {
            pAnswerListViewHolder.onBind(getModel(pPosition), pPosition);
        }

        if (elementModel == null || !elementModel.getOptions().isCanShow(mBaseActivity, getMap(), elementModel) || elementModel.getQueryVisibility() == View.GONE) {
            pAnswerListViewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        } else {
            pAnswerListViewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    class AnswerListViewHolder extends AbstractViewHolder {

        TextView mAnswer;
        TextView mEmptyButton;
        TextView mEmptyRadioButton;
        EditText editText;
        View mAnswerFrame;
        CustomCheckableButton mCheckBox;
        RecyclerView mContentsRecyclerView;
        View mClickableArea;

        AnswerListViewHolder(final View itemView, final BaseActivity pBaseActivity) {
            super(itemView, pBaseActivity);
            mAnswer = itemView.findViewById(R.id.answer_text);
            mEmptyButton = itemView.findViewById(R.id.empty_button);
            mEmptyRadioButton = itemView.findViewById(R.id.empty_button_radio);
            editText = itemView.findViewById(R.id.answer_edit_text);
            mCheckBox = itemView.findViewById(R.id.answer_checkbox);
            mContentsRecyclerView = itemView.findViewById(R.id.contents_recycler_view);
            mAnswerFrame = itemView.findViewById(R.id.answer_frame);
            mClickableArea = itemView.findViewById(R.id.clickableArea);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pAnswer, final int pPosition) {
            final OptionsModel options = pAnswer.getOptions();
            final String openType = options.getOpenType();
            final boolean isChecked = pAnswer.isChecked();
            final boolean isEnabled = pAnswer.isEnabled(mQuotas, QuestionListAdapter.this.mBaseActivity, mMap, pAnswer);
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
                        mEditText.setFocusableInTouchMode(true);
                        mEditText.setFocusable(true);
                        mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    default:
                        // неизвестный тип open_type
                }
            } else {
                mEditText.setVisibility(View.GONE);
            }

            final String title = options.getTitle(mBaseActivity, mMap);
            final String description = options.getDescription();

            if (StringUtils.isNotEmpty(description)) {
                final String titleAndDescription = title + " <span style='color:gray'><i>" + description + "</i></span>";
                UiUtils.setTextOrHide(mAnswer, titleAndDescription);
            } else {
                UiUtils.setTextOrHide(mAnswer, title);
            }

            final List<ElementModel> contents = pAnswer.getContents();
            // TODO: 6/23/2019 Самый плохой костыль в моей жизни который я видел
            final List<ElementModel> contentsWithStatusImage = new ArrayList<>();
            final ElementModel statusImage = options.getStatusImage();
            contentsWithStatusImage.addAll(contents);

            if (statusImage != null) {
                contentsWithStatusImage.add(options.getStatusImage());
            }

            if (!contentsWithStatusImage.isEmpty()) {
                mContentsRecyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
                mContentsRecyclerView.setHasFixedSize(true);
                ContentElementsAdapter mAdapter = new ContentElementsAdapter(getCurrentElement(), pAnswer, mBaseActivity, contentsWithStatusImage);
                mContentsRecyclerView.setAdapter(mAdapter);
                mContentsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mContentsRecyclerView.setVisibility(View.GONE);
            }

            mContentsRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckBox.isEnabled()) {
                        mCheckBox.performClickProgramatically();
                    }
                }
            });

            mCheckBox.setChecked(isChecked);
            mCheckBox.setEnabled(isEnabled);
            mCheckBox.setChecked(isChecked);
            mCheckBox.setTag(pPosition);

            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 3");

                    if (mEmptyButton != null)
                        mEmptyButton.setVisibility(View.VISIBLE);
                    if (mEmptyRadioButton != null)
                        mEmptyRadioButton.setVisibility(View.VISIBLE);
                    final CustomCheckableButton checkBox = (CustomCheckableButton) view;
                    final boolean isChecked = checkBox.isChecked();
                    final int minAnswers = getMinAnswer();
                    final int maxAnswers = getMaxAnswer();
                    final int checkedItemsCount = getCheckedItemsCount();


                    if (isChecked && minAnswers == DEFAULT_MIN_ANSWERS && minAnswers == maxAnswers) {
                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 4");
                        unselectAnother(pPosition);

                        pAnswer.setChecked(true);
                        radioBtnIsPressed = true;

                        Log.d(TAG, "?????????????????????????? onClick: " + options.getOpenType());

                        if (pAnswer.isChecked())
                            switch (options.getOpenType()) {
                                case OptionsOpenType.TIME:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 5");
                                    hideKeyboardFrom(view);
                                    setTime(view);
                                    break;
                                case OptionsOpenType.DATE:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 6");
                                    hideKeyboardFrom(view);
                                    setDate(view);
                                    break;
                                case OptionsOpenType.NUMBER:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 7");
                                    mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    refresh();
                                    showKeyboard();
                                    break;
                                case OptionsOpenType.TEXT:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 8");
                                    mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                                    refresh();
                                    showKeyboard();
                                    break;
                                case OptionsOpenType.CHECKBOX:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 8.1");
                                    refresh();
                                    break;
                                default:
                                    // неизвестный тип open_type
                            }

                    } else if (isChecked && maxAnswers != EMPTY_COUNT_ANSWER && checkedItemsCount >= maxAnswers) {
                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 9");
                        checkBox.setChecked(false);

                        QuestionListAdapter.this.mBaseActivity.showToast(String.format(QuestionListAdapter.this.mBaseActivity.getString(R.string.NOTIFICATION_MAX_ANSWERS), String.valueOf(maxAnswers)));
                    } else if (options.isUnchecker()) {
                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 10");
                        if (isChecked) {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 11");
                            setCheckedItemsCount(1);
                            unselectAll();
                            disableOther(pAnswer.getRelativeID());
                        } else {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 12");
                            setCheckedItemsCount(0);
                            enableAll();
                        }

                        pAnswer.setChecked(isChecked);

                        refresh();
                    } else {
                        if (isChecked) {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 13");
                            setCheckedItemsCount(checkedItemsCount + 1);
                        } else {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 14");
                            setCheckedItemsCount(checkedItemsCount - 1);
                        }

                        pAnswer.setChecked(isChecked);

//                        refresh();

                        if (pAnswer.isChecked())
                            switch (options.getOpenType()) {
                                case OptionsOpenType.TIME:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 15");
                                    setTime(view);
                                    break;
                                case OptionsOpenType.DATE:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 16");
                                    setDate(view);
                                    break;
                                case OptionsOpenType.NUMBER:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 17");
                                    mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    break;
                                case OptionsOpenType.TEXT:
                                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 18");
                                    mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                                    break;
                                default:
                                    // неизвестный тип open_type
                            }

                        if (mEditText.isEnabled() && mEditText.getText() == null) {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 19");
                            mEditText.setBackgroundResource(R.drawable.edit_text_red_border);
                            mEditText.requestFocus();
                        } else {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 20");
                            mEditText.setBackgroundResource(R.drawable.edit_text_transparent_border);
                            mEditText.requestFocus();
                        }

                        if (mEditText.hasFocus()) {
                            if (!options.getOpenType().equals(OptionsOpenType.TIME) && !options.getOpenType().equals(OptionsOpenType.DATE)) {
                                showKeyboard();
                            } else
                                hideKeyboardFrom(mEditText);
                        }

                        if (isChecked) {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 21");
                            mEditText.setEnabled(true);
                            mEditText.setFocusable(true);
                            mEditText.setFocusableInTouchMode(true);
                            mEditText.requestFocus();

                            if (mEditText.isEnabled() && mEditText.getText().toString().matches("")) {
                                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 22");
                                mEditText.setBackgroundResource(R.drawable.edit_text_red_border);
                                mEditText.requestFocus();
                            } else {
                                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 23");
                                mEditText.setBackgroundResource(R.drawable.edit_text_transparent_border);
                                mEditText.requestFocus();
                            }
                        } else {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 24");
                            mEditText.setEnabled(false);
                        }
                    }


                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View pView) {
                    if (mCheckBox.isEnabled()) {
                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 25");
                        mCheckBox.performClickProgramatically();
                    }
                }
            });

            if (mAnswerFrame != null) {
                mAnswerFrame.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View pView) {
                        if (mCheckBox.isEnabled()) {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 26");
                            mCheckBox.performClickProgramatically();
                        }
                    }
                });
            }

            if (mEmptyButton != null) {
                mEmptyButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View pView) {
                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 27");
                        if (mCheckBox.isEnabled()) {
                            if (mEditText.hasFocus()) {
                                showKeyboard();
                            }
                            mEmptyButton.setVisibility(View.GONE); //TODO Обработать включение
                            mEditText.setEnabled(true);
                            mEditText.setFocusable(true);
                            mEditText.setFocusableInTouchMode(true);
                            mEditText.requestFocus();
                            mCheckBox.performClickProgramatically();

                        }
                    }
                });
            }

            if (mEmptyRadioButton != null) {
                mEmptyRadioButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View pView) {
                        focusPos = pPosition;
                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 1");
                        if (mCheckBox.isEnabled()) {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClick: 2");
                            if (mEditText.hasFocus()) {
                                showKeyboard();
                            }
                            mEmptyRadioButton.setVisibility(View.GONE);
                            mEditText.setEnabled(true);
                            mEditText.setFocusable(true);
                            mEditText.setFocusableInTouchMode(true);
                            mEditText.requestFocus();

                            mCheckBox.performClickProgramatically();
                        }
                    }
                });
            }

            if (mClickableArea != null) {
                mClickableArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCheckBox.isEnabled()) {
                            mCheckBox.performClickProgramatically();
                        }
                    }
                });
            }
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public void setInitialDateTime(final boolean pIsDate) {
            SimpleDateFormat dateFormat;

            if (pIsDate) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            } else {
                dateFormat = new SimpleDateFormat("HH:mm");
            }

            dateFormat.setTimeZone(getCalendar().getTimeZone());
            mEditText.setText(dateFormat.format(getCalendar().getTime()));
            refresh();
        }
    }

    private void refresh() {
        notifyDataSetChanged();
    }

    public class ElementTextWatcher extends SimpleTextWatcher {

        private ElementModel mAnswer;
        private EditText zmEditText;

        ElementTextWatcher(final ElementModel pAnswer, final EditText pEditText) {
            mAnswer = pAnswer;
            zmEditText = pEditText;
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);

            final String answer = s.toString();
            mAnswer.setTextAnswer(answer);

            if (zmEditText.isEnabled() && StringUtils.isEmpty(answer)) {
                zmEditText.setBackgroundResource(R.drawable.edit_text_red_border);
                zmEditText.requestFocus();
            } else {
                zmEditText.setBackgroundResource(R.drawable.edit_text_transparent_border);
                zmEditText.requestFocus();
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            super.beforeTextChanged(s, start, count, after);

        }
    }


    public void showKeyboard() {
        ((InputMethodManager) mBaseActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void hideKeyboardFrom(View view) {
        ((InputMethodManager) mBaseActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}