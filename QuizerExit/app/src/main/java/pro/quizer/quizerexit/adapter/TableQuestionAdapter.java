package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.OptionsOpenType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.CollectionUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.CustomCheckableButton;

public class TableQuestionAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> implements OnItemClickListener, IAdapter {

    private static final int HALF = 2;
    private static final int CELL_COUNT = 3;
    private final LayoutInflater mLayoutInflater;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mHeaderHeight;
    private final int mHeaderWidth;
    private List<ElementModel> mLeftSide;
    private List<ElementModel> mTopSide;
    private List<ElementModel> mAnswers;
    private List<ElementModel> mQuestions;
    private Runnable mRefreshRunnable;
    private ElementModel mCurrentElement;
    private boolean mIsFlipColsAndRows;
    private BaseActivity mBaseActivity;
    private HashMap<Integer, ElementModel> mMap;

    @Override
    public int processNext() throws Exception {
        for (final ElementModel question : mQuestions) {
            if (question != null && question.getCountOfSelectedSubElements() == 0 && question.getOptions().isCanShow(mBaseActivity, mMap, question)) {
                throw new Exception(mBaseActivity.getString(R.string.NOTIFICATION_MIN_ANSWERS_TABLE));
            }
        }

        for (int index = 0; index < mAnswers.size(); index++) {
            final ElementModel model = mAnswers.get(index);

            if (model != null && model.isFullySelected()) {
                return model.getOptions().getJump();
            }
        }

        throw new Exception(mBaseActivity.getString(R.string.NOTIFICATION_NEXT_ELEMENT_CALCULATION_ERROR));
    }

    public TableQuestionAdapter(final ElementModel pCurrentElement, final Context context, final List<ElementModel> pQuestions, final Runnable pRefreshRunnable) {
        mCurrentElement = pCurrentElement;
        setOnItemClickListener(this);
        mRefreshRunnable = pRefreshRunnable;
        mLayoutInflater = LayoutInflater.from(context);
        final Resources res = context.getResources();
        final OptionsModel optionsModel = pCurrentElement.getOptions();
        mIsFlipColsAndRows = optionsModel.isFlipColsAndRows();
        mBaseActivity = (BaseActivity) context;
        mMap = mBaseActivity.getMap();

        mQuestions = pQuestions;

        if (optionsModel.isRotation()) {
            CollectionUtils.shuffleElements(mCurrentElement, mQuestions);
        }

        if (optionsModel.isRotationAnswers()) {
            CollectionUtils.shuffleTableAnswers(mCurrentElement, mQuestions);
        }

        if (mQuestions.get(0) != null) {
            mQuestions.add(0, null);
        }

        for (final ElementModel question : mQuestions) {
            if (question != null) {
                final List<ElementModel> answers = question.getElements();

                if (answers.get(0) != null) {
                    answers.add(0, null);
                }
            }
        }

        mAnswers = mQuestions.get(1).getElements();

        if (mIsFlipColsAndRows) {
            mTopSide = mQuestions;
            mLeftSide = mAnswers;
        } else {
            mTopSide = mAnswers;
            mLeftSide = mQuestions;
        }

        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);

        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);

        final int widthIndex = mTopSide.size() >= CELL_COUNT ? CELL_COUNT : HALF;
        mHeaderWidth = UiUtils.getDisplayWidth(mBaseActivity) / widthIndex;
        mColumnWidth = UiUtils.getDisplayWidth(mBaseActivity) / widthIndex;
    }

    @Override
    public int getRowCount() {
        return mLeftSide.size();
    }

    @Override
    public int getColumnCount() {
        return mTopSide.size();
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull final ViewGroup parent) {
        return new TableItemViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_main, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull final ViewGroup parent) {
        return new TableHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_header_column, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull final ViewGroup parent) {
        return new TableHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_header_row, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull final ViewGroup parent) {
        return new TableHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_header_left_top, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderImpl viewHolder, final int row, final int column) {
        final TableItemViewHolder vh = (TableItemViewHolder) viewHolder;

        final ElementModel currentElement = getElement(row, column);

        if (currentElement != null) {
            if (getQuestion(row, column).getOptions().isPolyanswer()) {
                vh.mTableItemCheckBox.setVisibility(View.VISIBLE);
                vh.mTableItemRadioButton.setVisibility(View.GONE);
            } else {
                vh.mTableItemCheckBox.setVisibility(View.GONE);
                vh.mTableItemRadioButton.setVisibility(View.VISIBLE);
            }

            setChecked(vh, currentElement.isChecked());

            if (currentElement.getOptions().isCanShow(mBaseActivity, mMap, currentElement)) {
                vh.mDisableFrame.setVisibility(View.GONE);
            } else {
                vh.mDisableFrame.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setChecked(final TableItemViewHolder vh, final boolean pIsChecked) {
        vh.mTableItemCheckBox.setChecked(pIsChecked);
        vh.mTableItemRadioButton.setChecked(pIsChecked);
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull final ViewHolderImpl viewHolder, final int column) {
        final TableHeaderColumnViewHolder vh = (TableHeaderColumnViewHolder) viewHolder;
        final OptionsModel optionsModel = mTopSide.get(column).getOptions();

        UiUtils.setTextOrHide(vh.mHeaderColumnTextView, optionsModel.getTitle(mBaseActivity));
        UiUtils.setTextOrHide(vh.mHeaderColumnDescriptionTextView, optionsModel.getDescription());
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull final ViewHolderImpl viewHolder, final int row) {
        final TableHeaderRowViewHolder vh = (TableHeaderRowViewHolder) viewHolder;
        final OptionsModel optionsModel = mLeftSide.get(row).getOptions();

        UiUtils.setTextOrHide(vh.mHeaderRowTextView, optionsModel.getTitle(mBaseActivity));
        UiUtils.setTextOrHide(vh.mHeaderRowDescriptionTextView, optionsModel.getDescription());

    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull final ViewHolderImpl viewHolder) {
        final TableHeaderLeftTopViewHolder vh = (TableHeaderLeftTopViewHolder) viewHolder;
        UiUtils.setTextOrHide(vh.mHeaderLeftTopTextView, mBaseActivity.getString(R.string.VIEW_QUESTION_ANSWER_TABLE_LABEL));
    }

    @Override
    public int getColumnWidth(final int column) {
        if (mIsFlipColsAndRows) {
            final ElementModel question = mQuestions.get(column);
            if (!question.getOptions().isCanShow(mBaseActivity, mMap, question)) {
                return 0;
            } else {
                question.setQuestionShowing(true);

                return mColumnWidth;
            }
        } else {
            return mColumnWidth;
        }
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(final int row) {
        if (!mIsFlipColsAndRows) {
            final ElementModel question = mQuestions.get(row);
            if (!question.getOptions().isCanShow(mBaseActivity, mMap, question)) {
                return 0;
            } else {
                question.setQuestionShowing(true);

                return mRowHeight;
            }
        } else {
            return mRowHeight;
        }
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }

    private ElementModel getElement(final int row, final int column) {
        final int questionIndex;
        final int answerIndex;
        final ElementModel element;

        if (mIsFlipColsAndRows) {
            questionIndex = column;
            answerIndex = row;

            element = mTopSide.get(questionIndex);
        } else {
            questionIndex = row;
            answerIndex = column;

            element = mLeftSide.get(questionIndex);
        }

        if (element != null) {
            return element.getElements().get(answerIndex);
        } else {
            return null;
        }
    }

    private ElementModel getQuestion(final int row, final int column) {
        if (mIsFlipColsAndRows) {
            return mTopSide.get(column);
        } else {
            return mLeftSide.get(row);
        }
    }

    private Calendar mCalendar = Calendar.getInstance();

    // отображаем диалоговое окно для выбора даты
    public void setDate(final EditText pEditText) {
        new DatePickerDialog(mBaseActivity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime(pEditText, true);
            }
        },
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(final EditText pEditText) {
        new TimePickerDialog(mBaseActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                setInitialDateTime(pEditText, false);
            }
        },
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE), true)
                .show();
    }

    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime(final EditText mEditText, final boolean pIsDate) {
        SimpleDateFormat dateFormat;

        if (pIsDate) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("HH:mm");
        }

        dateFormat.setTimeZone(mCalendar.getTimeZone());
        mEditText.setText(dateFormat.format(mCalendar.getTime()));
    }

    @Override
    public void onItemClick(final int row, final int column) {
        final ElementModel clickedElement = getElement(row, column);

        if (clickedElement == null) {
            return;
        }

        final ElementModel clickedQuestion = getQuestion(row, column);
        final boolean isPolyanswer = clickedQuestion.getOptions().isPolyanswer();

        if (!clickedElement.getOptions().isCanShow(mBaseActivity, mMap, clickedElement)) {
            mBaseActivity.showToast(mBaseActivity.getString(R.string.NOTIFICATION_ANSWER_NOT_AVAILABLE_TABLE_QUESTION));

            return;
        }

        final boolean isElementChecked = clickedElement.isChecked();
        final OptionsModel options = clickedElement.getOptions();
        final String openType = options.getOpenType();

        if (!OptionsOpenType.CHECKBOX.equals(openType)) {
            final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mBaseActivity);
            final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input_box, null);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mBaseActivity);
            dialog.setView(mView);

            final EditText mEditText = mView.findViewById(R.id.answer_edit_text);

            final String placeholder = options.getPlaceholder();
            final String textAnswer = clickedElement.getTextAnswer();

            mEditText.setVisibility(View.VISIBLE);
            mEditText.setHint(StringUtils.isEmpty(placeholder) ? mBaseActivity.getString(R.string.TEXT_HINT_DEFAULT_PLACEHOLDER) : placeholder);
            mEditText.setText(textAnswer);

            switch (options.getOpenType()) {
                case OptionsOpenType.TIME:
                    mEditText.setFocusableInTouchMode(false);
                    mEditText.setHint(R.string.TEXT_HINT_TIME);
                    mEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setTime(mEditText);
                        }
                    });
                    break;
                case OptionsOpenType.DATE:
                    mEditText.setFocusableInTouchMode(false);
                    mEditText.setHint(R.string.TEXT_HINT_DATE);
                    mEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setDate(mEditText);
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

            dialog.setCancelable(false)
                    .setPositiveButton(R.string.VIEW_APPLY, new DialogInterface.OnClickListener() {

                        public void onClick(final DialogInterface dialogBox, final int id) {
                            final String answer = mEditText.getText().toString();

                            if (StringUtils.isEmpty(answer)) {
                                mBaseActivity.showToast(mBaseActivity.getString(R.string.NOTIFICATION_FILL_INPUT));

                                return;
                            }

                            clickedElement.setTextAnswer(answer);
                            clickedElement.setChecked(true);
                            notifyItemChanged(row, column);
                            mRefreshRunnable.run();
                        }
                    })

                    .setNegativeButton(R.string.VIEW_CANCEL,
                            new DialogInterface.OnClickListener() {

                                public void onClick(final DialogInterface dialogBox, final int id) {
                                    clickedElement.setTextAnswer(Constants.Strings.EMPTY);
                                    clickedElement.setChecked(false);
                                    dialogBox.cancel();
                                    notifyItemChanged(row, column);
                                    mRefreshRunnable.run();
                                }
                            });

            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        } else {
            clickedElement.setChecked(!isElementChecked);
        }

        if (!isPolyanswer && clickedElement.isFullySelected()) {
            unselectOther(row, column, clickedQuestion, clickedElement);
        }

        notifyItemChanged(row, column);
    }

    private void unselectOther(final int row, final int column, final ElementModel pQuestion, final ElementModel pClickedElement) {
        final int clickedRelativeId = pClickedElement.getRelativeID();

        for (final ElementModel answer : pQuestion.getElements()) {
            if (answer != null && answer.getRelativeID() != clickedRelativeId) {
                answer.setChecked(false);
            }
        }

        final int answersSize = mAnswers.size();

        if (mIsFlipColsAndRows) {
            for (int i = 0; i < answersSize; i++) {
                notifyItemChanged(i, column);
            }
        } else {
            for (int i = 0; i < answersSize; i++) {
                notifyItemChanged(row, i);
            }
        }
    }

    @Override
    public void onRowHeaderClick(final int row) {
        showAdditionalInfoDialog(mLeftSide.get(row).getOptions());
    }

    @Override
    public void onColumnHeaderClick(final int column) {
        showAdditionalInfoDialog(mTopSide.get(column).getOptions());
    }

    @Override
    public void onLeftTopHeaderClick() {

    }

    private void showAdditionalInfoDialog(final OptionsModel pOptionsModel) {
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mBaseActivity);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_table_question_additional_info, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mBaseActivity);
        dialog.setView(mView);

        final TextView title = mView.findViewById(R.id.title);
        final TextView description = mView.findViewById(R.id.description);

        UiUtils.setTextOrHide(title, pOptionsModel.getTitle(mBaseActivity));
        UiUtils.setTextOrHide(description, pOptionsModel.getDescription());

        dialog.setCancelable(false)
                .setPositiveButton(R.string.VIEW_OK, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialogBox, final int id) {
                        dialogBox.cancel();
                    }
                });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    private static class TableItemViewHolder extends ViewHolderImpl {

        CustomCheckableButton mTableItemRadioButton;
        CustomCheckableButton mTableItemCheckBox;
        View mDisableFrame;

        private TableItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            mTableItemRadioButton = itemView.findViewById(R.id.table_item_radio_button);
            mTableItemCheckBox = itemView.findViewById(R.id.table_item_check_box);
            mDisableFrame = itemView.findViewById(R.id.disable_frame);
        }
    }

    private static class TableHeaderColumnViewHolder extends ViewHolderImpl {

        TextView mHeaderColumnTextView;
        TextView mHeaderColumnDescriptionTextView;

        private TableHeaderColumnViewHolder(@NonNull final View itemView) {
            super(itemView);
            mHeaderColumnTextView = itemView.findViewById(R.id.table_header_column_text_view);
            mHeaderColumnDescriptionTextView = itemView.findViewById(R.id.table_header_column_description_text_view);
        }
    }

    private static class TableHeaderRowViewHolder extends ViewHolderImpl {

        TextView mHeaderRowTextView;
        TextView mHeaderRowDescriptionTextView;

        TableHeaderRowViewHolder(@NonNull final View itemView) {
            super(itemView);
            mHeaderRowTextView = itemView.findViewById(R.id.table_header_row_text_view);
            mHeaderRowDescriptionTextView = itemView.findViewById(R.id.table_header_row_description_text_view);
        }
    }

    private static class TableHeaderLeftTopViewHolder extends ViewHolderImpl {

        TextView mHeaderLeftTopTextView;

        private TableHeaderLeftTopViewHolder(@NonNull final View itemView) {
            super(itemView);

            mHeaderLeftTopTextView = itemView.findViewById(R.id.table_header_left_top_text_view);
        }
    }
}