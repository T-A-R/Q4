package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.view.activity.ScreenActivity;
import pro.quizer.quizer3.model.OptionsOpenType;
//import pro.quizer.quizer3.utils.CollectionUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
//import pro.quizer.quizerexit.view.CustomCheckableButton;

public class TableQuestionAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> implements OnItemClickListener {

    private static final int HALF = 2;
    private static final int CELL_COUNT = 3;
    private final LayoutInflater mLayoutInflater;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mHeaderHeight;
    private final int mHeaderWidth;
    private List<ElementItemR> mLeftSide;
    private List<ElementItemR> mTopSide;
    private List<ElementItemR> mAnswers;
    private List<AnswerState> mAnswersState;
    private List<ElementItemR> mQuestions;
    private Runnable mRefreshRunnable;
    private ElementItemR mCurrentElement;
    private boolean mIsFlipColsAndRows;
    //    private BaseActivity mBaseActivity;
    private MainActivity mContext;
//    private HashMap<Integer, ElementItemR> mMap;

//    public TableQuestionAdapter(final ElementItemR pCurrentElement, final Context context, final List<ElementItemR> pQuestions, final Runnable pRefreshRunnable, final HashMap<Integer, ElementItemR> pMap) {
    public TableQuestionAdapter(final ElementItemR pCurrentElement, final Context context, final List<ElementItemR> pQuestions) {
        mCurrentElement = pCurrentElement;
        setOnItemClickListener(this);
//        mRefreshRunnable = pRefreshRunnable;
        mLayoutInflater = LayoutInflater.from(context);
        final Resources res = context.getResources();
        final ElementOptionsR optionsModel = pCurrentElement.getElementOptionsR();
        mIsFlipColsAndRows = optionsModel.isFlip_cols_and_rows();
        mContext = (MainActivity) context;
//        mMap = pMap;

        mQuestions = pQuestions;

//        if (optionsModel.isRotation()) {
//            CollectionUtils.shuffleElements(mCurrentElement, mQuestions);
//        }
//
//        if (optionsModel.isRotationAnswers()) {
//            CollectionUtils.shuffleTableAnswers(mCurrentElement, mQuestions);
//        }

//        if (mQuestions.get(0) != null) {
//            mQuestions.add(0, null);
//        }

//        for (final ElementItemR question : mQuestions) {
//            if (question != null) {
//                final List<ElementItemR> answers = question.getElements();
//                Log.d(TAG, "TableQuestionAdapter: " + question.getRelative_id());
//                if (answers.get(0) != null) {
//                    answers.add(0, null);
//                }
//            }
//        }

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
        mHeaderWidth = UiUtils.getDisplayWidth(context) / widthIndex;
        mColumnWidth = UiUtils.getDisplayWidth(context) / widthIndex;
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

        final ElementItemR currentElement = getElement(row, column);

        if (currentElement != null) {
            final ElementOptionsR optionsModel = currentElement.getElementOptionsR();

            if (!OptionsOpenType.CHECKBOX.equals(optionsModel.getOpen_type())) {
                vh.mOpenAnswerFrame.setVisibility(View.VISIBLE);
            } else {
                vh.mOpenAnswerFrame.setVisibility(View.GONE);
            }

            if (getQuestion(row, column).getElementOptionsR().isPolyanswer()) {
                vh.mTableItemCheckBox.setVisibility(View.VISIBLE);
                vh.mTableItemRadioButton.setVisibility(View.GONE);
            } else {
                vh.mTableItemCheckBox.setVisibility(View.GONE);
                vh.mTableItemRadioButton.setVisibility(View.VISIBLE);
            }

            //TODO Установка отмеченных
//            setChecked(vh, currentElement.isChecked());
            setChecked(vh, false);

            //TODO Отключение элементов

//            if (currentElement.getElementOptionsR().isCanShow(mBaseActivity, mMap, currentElement)) {
//                vh.mDisableFrame.setVisibility(View.GONE);
//            } else {
//                vh.mDisableFrame.setVisibility(View.VISIBLE);
//            }
        }
    }

    private void setChecked(final TableItemViewHolder vh, final boolean pIsChecked) {
        if(pIsChecked) {
            vh.mTableItemCheckBox.setImageResource(R.drawable.checkbox_checked);
            vh.mTableItemRadioButton.setImageResource(R.drawable.radio_button_checked);
        } else {
            vh.mTableItemCheckBox.setImageResource(R.drawable.checkbox_unchecked);
            vh.mTableItemRadioButton.setImageResource(R.drawable.radio_button_unchecked);
        }
//        vh.mTableItemCheckBox.setChecked(pIsChecked);
//        vh.mTableItemRadioButton.setChecked(pIsChecked);
        vh.mOpenAnswerEditText.setText(pIsChecked ? "✓" : "");
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull final ViewHolderImpl viewHolder, final int column) {
        final TableHeaderColumnViewHolder vh = (TableHeaderColumnViewHolder) viewHolder;
        final ElementOptionsR optionsModel = mTopSide.get(column).getElementOptionsR();

//        UiUtils.setTextOrHide(vh.mHeaderColumnTextView, optionsModel.getTitle(mBaseActivity, mMap));
        UiUtils.setTextOrHide(vh.mHeaderColumnTextView, optionsModel.getTitle());
        UiUtils.setTextOrHide(vh.mHeaderColumnDescriptionTextView, optionsModel.getDescription());
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull final ViewHolderImpl viewHolder, final int row) {
        final TableHeaderRowViewHolder vh = (TableHeaderRowViewHolder) viewHolder;
        final ElementOptionsR optionsModel = mLeftSide.get(row).getElementOptionsR();

//        UiUtils.setTextOrHide(vh.mHeaderRowTextView, optionsModel.getTitle(mBaseActivity, mMap));
        UiUtils.setTextOrHide(vh.mHeaderRowTextView, optionsModel.getTitle());
        UiUtils.setTextOrHide(vh.mHeaderRowDescriptionTextView, optionsModel.getDescription());

    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull final ViewHolderImpl viewHolder) {
        final TableHeaderLeftTopViewHolder vh = (TableHeaderLeftTopViewHolder) viewHolder;
        UiUtils.setTextOrHide(vh.mHeaderLeftTopTextView, mContext.getString(R.string.table_label));
    }

    @Override
    public int getColumnWidth(final int column) {
//        if (mIsFlipColsAndRows) {
//            final ElementItemR question = mQuestions.get(column);
//            if (!question.getElementOptionsR().isCanShow(mBaseActivity, mMap, question)) {
//                return 0;
//            } else {
//                question.setQuestionShowing(true);
//
//                return mColumnWidth;
//            }
//        } else {
            return mColumnWidth;
//        }
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(final int row) {
//        if (!mIsFlipColsAndRows) {
//            final ElementModel question = mQuestions.get(row);
//            if (!question.getOptions().isCanShow(mBaseActivity, mMap, question)) {
//                return 0;
//            } else {
//                question.setQuestionShowing(true);
//
//                return mRowHeight;
//            }
//        } else {
            return mRowHeight;
//        }
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }

    private ElementItemR getElement(final int row, final int column) {
        final int questionIndex;
        final int answerIndex;
        final ElementItemR element;

        if (mIsFlipColsAndRows) {
            questionIndex = column;
            answerIndex = row;

            element = mTopSide.get(questionIndex);
        } else {
            questionIndex = row;
            answerIndex = column;

            element = mLeftSide.get(questionIndex);
        }

        if (element != null && element.getElements().size() >0) {
            return element.getElements().get(answerIndex);
        } else {
            return null;
        }
    }

    private ElementItemR getQuestion(final int row, final int column) {
        if (mIsFlipColsAndRows) {
            return mTopSide.get(column);
        } else {
            return mLeftSide.get(row);
        }
    }

    private Calendar mCalendar = Calendar.getInstance();

    // отображаем диалоговое окно для выбора даты
    public void setDate(final EditText pEditText) {
        if (!mContext.isFinishing()) {
        new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
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
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(final EditText pEditText) {
        if (!mContext.isFinishing()) {
        new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
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
        final ElementItemR clickedElement = getElement(row, column);

        if (clickedElement == null) {
            return;
        }

        final ElementItemR clickedQuestion = getQuestion(row, column);
        final boolean isPolyanswer = clickedQuestion.getElementOptionsR().isPolyanswer();

//        if (!clickedElement.getElementOptionsR().isCanShow(mBaseActivity, mMap, clickedElement)) {
//            mBaseActivity.showToast(mBaseActivity.getString(R.string.NOTIFICATION_ANSWER_NOT_AVAILABLE_TABLE_QUESTION));
//
//            return;
//        }

        final boolean isElementChecked = false; //TODO Добавить таблицу ответов
//        final boolean isElementChecked = clickedElement.isChecked();
        final ElementOptionsR options = clickedElement.getElementOptionsR();
        final String openType = options.getOpen_type();

        if (!OptionsOpenType.CHECKBOX.equals(openType)) {
//            final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mBaseActivity);
            final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
            final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_user_input_box, null);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            dialog.setView(mView);

            final EditText mEditText = mView.findViewById(R.id.answer_edit_text);

            final String placeholder = options.getPlaceholder();

            //TODO Выставить текст ответа
//            final String textAnswer = clickedElement.getTextAnswer();
            final String textAnswer = "";

            mEditText.setVisibility(View.VISIBLE);
            mEditText.setHint(StringUtils.isEmpty(placeholder) ? mContext.getString(R.string.default_placeholder) : placeholder);
            mEditText.setText(textAnswer);

            switch (options.getOpen_type()) {
                case OptionsOpenType.TIME:
                    mEditText.setFocusableInTouchMode(false);
                    mEditText.setHint(mContext.getString(R.string.hint_time));
                    mEditText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setTime(mEditText);
                        }
                    });
                    break;
                case OptionsOpenType.DATE:
                    mEditText.setFocusableInTouchMode(false);
                    mEditText.setHint(mContext.getString(R.string.hint_date));
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
                    .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {

                        public void onClick(final DialogInterface dialogBox, final int id) {
                            final String answer = mEditText.getText().toString();

                            if (StringUtils.isEmpty(answer)) {
                                mContext.showToastfromActivity(mContext.getString(R.string.fill_input_warning));

                                return;
                            }

                            //TODO Добавить ответы.
//                            clickedElement.setTextAnswer(answer);
//                            clickedElement.setChecked(true);
                            notifyItemChanged(row, column);
                            mRefreshRunnable.run();
                        }
                    })

                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                public void onClick(final DialogInterface dialogBox, final int id) {

                                    //TODO Добавить ответы.
//                                    clickedElement.setTextAnswer(Constants.Strings.EMPTY);
//                                    clickedElement.setChecked(false);
                                    dialogBox.cancel();
                                    notifyItemChanged(row, column);
                                    mRefreshRunnable.run();
                                }
                            });

            final AlertDialog alertDialog = dialog.create();

            if (!mContext.isFinishing()) {
                alertDialog.show();
            }
        } else {
            //TODO Добавить ответы.
//            clickedElement.setChecked(!isElementChecked);
        }

        //TODO Добавить ответы.
//        if (!isPolyanswer && clickedElement.isFullySelected()) {
//            unselectOther(row, column, clickedQuestion, clickedElement);
//        }

        notifyItemChanged(row, column);
    }

    private void unselectOther(final int row, final int column, final ElementItemR pQuestion, final ElementItemR pClickedElement) {
        final int clickedRelativeId = pClickedElement.getRelative_id();

        for (final ElementItemR answer : pQuestion.getElements()) {
            if (answer != null && answer.getRelative_id() != clickedRelativeId) {

                //TODO Добавить ответы.
//                answer.setChecked(false);
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
        showAdditionalInfoDialog(mLeftSide.get(row).getElementOptionsR());
    }

    @Override
    public void onColumnHeaderClick(final int column) {
        showAdditionalInfoDialog(mTopSide.get(column).getElementOptionsR());
    }

    @Override
    public void onLeftTopHeaderClick() {

    }

    private void showAdditionalInfoDialog(final ElementOptionsR pOptionsModel) {
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_table_question_additional_info, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        dialog.setView(mView);

        final TextView title = mView.findViewById(R.id.title);
        final TextView description = mView.findViewById(R.id.description);

        UiUtils.setTextOrHide(title, pOptionsModel.getTitle());
        UiUtils.setTextOrHide(description, pOptionsModel.getDescription());

        dialog.setCancelable(false)
                .setPositiveButton(R.string.view_OK, new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialogBox, final int id) {
                        dialogBox.cancel();
                    }
                });

        final AlertDialog alertDialog = dialog.create();

        if (!mContext.isFinishing()) {
            alertDialog.show();
        }
    }

    private static class TableItemViewHolder extends ViewHolderImpl {

        FrameLayout mOpenAnswerFrame;
        EditText mOpenAnswerEditText;
        ImageView mTableItemRadioButton;
        ImageView mTableItemCheckBox;
        View mDisableFrame;

        private TableItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            mOpenAnswerFrame = itemView.findViewById(R.id.open_answer_frame);
            mOpenAnswerEditText = itemView.findViewById(R.id.open_answer_edittext);
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