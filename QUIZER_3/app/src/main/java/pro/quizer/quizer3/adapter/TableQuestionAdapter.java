package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.OptionsOpenType;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.element.CustomCheckableButton;

import static pro.quizer.quizer3.MainActivity.TAG;

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
    private AnswerState[][] mAnswersState;
    private boolean[] mLine;
    private List<ElementItemR> mQuestions;
    private Runnable mRefreshRunnable;
    private ElementItemR mCurrentElement;
    private boolean mIsFlipColsAndRows;
    private MainActivity mContext;
    private OnTableAnswerClickListener mOnTableAnswerClickListener;
    private boolean isSpeedMode;

    public TableQuestionAdapter(final ElementItemR pCurrentElement, List<ElementItemR> questions, final Context context, final Runnable pRefreshRunnable, OnTableAnswerClickListener pOnTableAnswerClickListener) {
        mCurrentElement = pCurrentElement;
        mOnTableAnswerClickListener = pOnTableAnswerClickListener;
        setOnItemClickListener(this);
        mRefreshRunnable = pRefreshRunnable;
        mLayoutInflater = LayoutInflater.from(context);
        final Resources res = context.getResources();
        final ElementOptionsR optionsModel = pCurrentElement.getElementOptionsR();
        mIsFlipColsAndRows = optionsModel.isFlip_cols_and_rows();
        mContext = (MainActivity) context;
        isSpeedMode = mContext.isTableSpeedMode();
        mQuestions = questions;
        if(!isSpeedMode) {
            mLine = new boolean[mQuestions.size()];
            for (int i = 0; i < mLine.length; i++) {
                mLine[i] = false;
            }
        }

        if (mCurrentElement != null && mCurrentElement.getElementOptionsR() != null && mCurrentElement.getElementOptionsR().isRotation()) {
            List<ElementItemR> shuffleList = new ArrayList<>();
            for (ElementItemR elementItemR : mQuestions) {
                if (elementItemR.getElementOptionsR() != null && !elementItemR.getElementOptionsR().isFixed_order()) {
                    shuffleList.add(elementItemR);
                }
            }
            Collections.shuffle(shuffleList, new Random());
            int k = 0;

            for (int i = 0; i < mQuestions.size(); i++) {
                if (mQuestions.get(i).getElementOptionsR() != null && !mQuestions.get(i).getElementOptionsR().isFixed_order()) {
                    mQuestions.set(i, shuffleList.get(k));
                    k++;
                }
            }
        }

        if (mQuestions != null)
            mAnswers = mQuestions.get(0).getElements();

        this.mAnswersState = new AnswerState[mQuestions.size()][mAnswers.size()];
        if (mQuestions != null) {
            for (int i = 0; i < mQuestions.size(); i++) {
                List<ElementItemR> pAnswers = mQuestions.get(i).getElements();
                for (int k = 0; k < pAnswers.size(); k++) {
                    mAnswersState[i][k] = new AnswerState(pAnswers.get(k).getRelative_id(), false, "");
                }
            }
        }

        setLine();

        if (mIsFlipColsAndRows) {
            mTopSide = mQuestions;
            mLeftSide = mAnswers;
        } else {
            mTopSide = mAnswers;
            mLeftSide = mQuestions;
        }


        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);

        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);

        final int widthIndex = mTopSide.size() + 1 >= CELL_COUNT ? CELL_COUNT : HALF;
        mHeaderWidth = UiUtils.getDisplayWidth(context) / widthIndex;
        mColumnWidth = UiUtils.getDisplayWidth(context) / widthIndex;
    }

    @Override
    public int getRowCount() {
        return mLeftSide.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return mTopSide.size() + 1;
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull final ViewGroup parent) {
        return new TableItemViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_main, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull final ViewGroup parent) {
//        return new TableHeaderColumnViewHolder(mLayoutInflater.inflate(mContext.isAutoZoom() ? R.layout.adapter_table_item_header_column_auto : R.layout.adapter_table_item_header_column, parent, false));
        return new TableHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_header_column_auto, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull final ViewGroup parent) {
        return new TableHeaderRowViewHolder(mLayoutInflater.inflate(mContext.isAutoZoom() ? R.layout.adapter_table_item_header_row_auto : R.layout.adapter_table_item_header_row, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull final ViewGroup parent) {
        return new TableHeaderLeftTopViewHolder(mLayoutInflater.inflate(mContext.isAutoZoom() ? R.layout.adapter_table_item_header_left_top_auto : R.layout.adapter_table_item_header_left_top, parent, false));
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
            if (!mIsFlipColsAndRows) {
                setChecked(vh, mAnswersState[row - 1][column - 1].isChecked());
                if(!isSpeedMode) {
                    if (mLine[row - 1]) {
                        vh.mCont.setBackgroundColor(mContext.getResources().getColor(R.color.lightGray2));
                    } else {
                        vh.mCont.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    }
                }
            } else {
                setChecked(vh, mAnswersState[column - 1][row - 1].isChecked());
                if(!isSpeedMode) {
                    if (mLine[column - 1]) {
                        vh.mCont.setBackgroundColor(mContext.getResources().getColor(R.color.lightGray2));
                    } else {
                        vh.mCont.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    }
                }
            }
        }
    }

    private void setChecked(final TableItemViewHolder vh, final boolean pIsChecked) {
        vh.mTableItemCheckBox.setChecked(pIsChecked);
        vh.mTableItemRadioButton.setChecked(pIsChecked);
        vh.mOpenAnswerEditText.setText(pIsChecked ? "✓" : "");
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull final ViewHolderImpl viewHolder, final int column) {
        final TableHeaderColumnViewHolder vh = (TableHeaderColumnViewHolder) viewHolder;
        final ElementOptionsR optionsModel = mTopSide.get(column - 1).getElementOptionsR();

        UiUtils.setTextOrHide(vh.mHeaderColumnTextView, optionsModel.getTitle());
//        UiUtils.setTextOrHide(vh.mHeaderColumnDescriptionTextView, optionsModel.getDescription());
        if(!isSpeedMode) {
            if (mIsFlipColsAndRows) {
                if (mLine[column - 1]) {
                    vh.mColumnCont.setBackgroundColor(mContext.getResources().getColor(R.color.lightGray2));
                } else {
                    vh.mColumnCont.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                }
            }
        }
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull final ViewHolderImpl viewHolder, final int row) {
        final TableHeaderRowViewHolder vh = (TableHeaderRowViewHolder) viewHolder;
        final ElementOptionsR optionsModel = mLeftSide.get(row - 1).getElementOptionsR();

        UiUtils.setTextOrHide(vh.mHeaderRowTextView, optionsModel.getTitle());
        UiUtils.setTextOrHide(vh.mHeaderRowDescriptionTextView, optionsModel.getDescription());
        if(!isSpeedMode) {
            if (!mIsFlipColsAndRows) {
                if (mLine[row - 1]) {
                    vh.mRowCont.setBackgroundColor(mContext.getResources().getColor(R.color.lightGray2));
                } else {
                    vh.mRowCont.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                }
            }
        }
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull final ViewHolderImpl viewHolder) {
        final TableHeaderLeftTopViewHolder vh = (TableHeaderLeftTopViewHolder) viewHolder;
        UiUtils.setTextOrHide(vh.mHeaderLeftTopTextView, mContext.getString(R.string.table_label));
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }

    @Override
    public int getColumnWidth(final int column) {
        if (column == 0) return mColumnWidth;
        else return mRowHeight;
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(final int row) {
        return mRowHeight;
    }

    private ElementItemR getElement(final int row, final int column) {
        final int questionIndex;
        final int answerIndex;
        final ElementItemR element;

        if (mIsFlipColsAndRows) {
            questionIndex = column - 1;
            answerIndex = row - 1;

            element = mTopSide.get(questionIndex);
        } else {
            questionIndex = row - 1;
            answerIndex = column - 1;

            element = mLeftSide.get(questionIndex);
        }

        if (element != null && element.getElements().size() > 0) {
            return element.getElements().get(answerIndex);
        } else {
            return null;
        }
    }

    private ElementItemR getQuestion(final int row, final int column) {
        if (mIsFlipColsAndRows) {
            return mTopSide.get(column - 1);
        } else {
            return mLeftSide.get(row - 1);
        }
    }

    private Calendar mCalendar = Calendar.getInstance();

    public void setDate(final EditText pEditText) {
        if (mContext != null && !mContext.isFinishing()) {
            new DatePickerDialog(mContext, (view, year, monthOfYear, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime(pEditText, true);
            },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    public void setTime(final EditText pEditText) {
        if (mContext != null && !mContext.isFinishing()) {
            new TimePickerDialog(mContext, (view, hourOfDay, minute) -> {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                setInitialDateTime(pEditText, false);
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
            Log.d(TAG, "onItemClick: element NULL!");
            return;
        }
        mOnTableAnswerClickListener.onAnswerClick(row, column); //Тут нас обмен строк и столбцов не интересует. Передается в ElementFragment для очистки прохождения
        final ElementItemR clickedQuestion = getQuestion(row, column);
        final boolean isPolyanswer = clickedQuestion.getElementOptionsR().isPolyanswer();
        final boolean isElementChecked = mIsFlipColsAndRows ? mAnswersState[column - 1][row - 1].isChecked() : mAnswersState[row - 1][column - 1].isChecked();
        final ElementOptionsR options = clickedElement.getElementOptionsR();
        final String openType = options.getOpen_type();

        if (!OptionsOpenType.CHECKBOX.equals(openType)) {
            final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
            final View mView = layoutInflaterAndroid.inflate(mContext.isAutoZoom() ? R.layout.dialog_user_input_box_auto : R.layout.dialog_user_input_box, null);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            dialog.setView(mView);

            final EditText mEditText = mView.findViewById(R.id.answer_edit_text);
            final String placeholder = options.getPlaceholder();
            final String textAnswer = mIsFlipColsAndRows ? mAnswersState[column - 1][row - 1].getData() : mAnswersState[row - 1][column - 1].getData();

            mEditText.setVisibility(View.VISIBLE);
            mEditText.setHint(StringUtils.isEmpty(placeholder) ? mContext.getString(R.string.default_placeholder) : placeholder);
            mEditText.setText(textAnswer);

            switch (options.getOpen_type()) {
                case OptionsOpenType.TIME:
                    mEditText.setFocusableInTouchMode(false);
                    mEditText.setHint(mContext.getString(R.string.hint_time));
                    mEditText.setOnClickListener(view -> setTime(mEditText));
                    break;
                case OptionsOpenType.DATE:
                    mEditText.setFocusableInTouchMode(false);
                    mEditText.setHint(mContext.getString(R.string.hint_date));
                    mEditText.setOnClickListener(view -> setDate(mEditText));
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
                    .setPositiveButton(R.string.apply, (dialogBox, id) -> {
                        final String answer = mEditText.getText().toString();

                        if (!StringUtils.isEmpty(answer) || options.isUnnecessary_fill_open()) {

                        } else {
                            Log.d(TAG, "?????????????? notEmpty: 2");
                            mContext.showToastfromActivity(mContext.getString(R.string.empty_string_warning));
                            return;
                        }

                        if (!mIsFlipColsAndRows) {
                            mAnswersState[row - 1][column - 1].setChecked(true);
                            setLine();
                            mAnswersState[row - 1][column - 1].setData(answer);
                            if (!isPolyanswer && mAnswersState[row - 1][column - 1].isChecked()) {
                                unselectOther(row, column, clickedQuestion, clickedElement);
                            }
                            if(!isSpeedMode) {
                                try {
                                    notifyRowChanged(row);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            notifyItemChanged(row, 0);
                        } else {
                            mAnswersState[column - 1][row - 1].setChecked(true);
                            setLine();
                            mAnswersState[column - 1][row - 1].setData(answer);
                            if (!isPolyanswer && mAnswersState[column - 1][row - 1].isChecked()) {
                                unselectOther(row, column, clickedQuestion, clickedElement);
                            }
                            if(!isSpeedMode) {
                                try {
                                    notifyColumnChanged(column);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            notifyItemChanged(0, column);
                        }
                        mRefreshRunnable.run();

                    })

                    .setNegativeButton(R.string.cancel,
                            (dialogBox, id) -> {
                                if (!mIsFlipColsAndRows) {
                                    if(isPolyanswer) {
                                        mAnswersState[row - 1][column - 1].setChecked(false);
                                        setLine();
                                        mAnswersState[row - 1][column - 1].setData(Constants.Strings.EMPTY);
                                        dialogBox.cancel();
                                        try {
                                            notifyRowChanged(row);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        notifyItemChanged(row, 0);
                                    }
                                } else {
                                    if(isPolyanswer) {
                                        mAnswersState[column - 1][row - 1].setChecked(false);
                                        setLine();
                                        mAnswersState[column - 1][row - 1].setData(Constants.Strings.EMPTY);
                                        dialogBox.cancel();
                                        try {
                                            notifyColumnChanged(column);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        notifyItemChanged(0, column);
                                    }
                                }
                                mRefreshRunnable.run();
                            });

            final AlertDialog alertDialog = dialog.create();

            if (mContext != null && !mContext.isFinishing()) {
                alertDialog.show();
            }
        } else {
            if(!isPolyanswer && isElementChecked) {

            } else {
                if (!mIsFlipColsAndRows) {
                    mAnswersState[row - 1][column - 1].setChecked(!isElementChecked);
                } else {
                    mAnswersState[column - 1][row - 1].setChecked(!isElementChecked);
                }
                setLine();
            }
        }

        if (!mIsFlipColsAndRows) {
            if (!isPolyanswer && mAnswersState[row - 1][column - 1].isChecked()) {
                unselectOther(row, column, clickedQuestion, clickedElement);
            }
            for (int i = 0; i < mAnswersState[0].length; i++) {
                notifyItemChanged(row, i + 1);
            }
            notifyItemChanged(row, 0);
        } else {
            if (!isPolyanswer && mAnswersState[column - 1][row - 1].isChecked()) {
                unselectOther(row, column, clickedQuestion, clickedElement);
            }
            for (int i = 0; i < mAnswersState[0].length; i++) {
                notifyItemChanged(i + 1, column);
            }
            notifyItemChanged(0, column);
        }
    }

    private void unselectOther(final int row, final int column, final ElementItemR pQuestion, final ElementItemR pClickedElement) {

        final int clickedRelativeId = pClickedElement.getRelative_id();

        List<ElementItemR> answersList = pQuestion.getElements();
        if (!mIsFlipColsAndRows) {
            for (int i = 0; i < answersList.size(); i++) {
                if (clickedRelativeId != mAnswersState[row - 1][i].getRelative_id()) {
                    mAnswersState[row - 1][i].setChecked(false);
                    setLine();
                }
            }
            if(!isSpeedMode) {
                try {
                    notifyRowChanged(row - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            notifyItemChanged(row - 1, 0);
        } else {
            for (int i = 0; i < answersList.size(); i++) {
                if (clickedRelativeId != mAnswersState[column - 1][i].getRelative_id()) {
                    mAnswersState[column - 1][i].setChecked(false);
                    setLine();
                }
            }
            if(!isSpeedMode) {
                try {
                    notifyColumnChanged(column - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            notifyItemChanged(0, column - 1);
        }

    }

    public void setLine() {
        if(!isSpeedMode) {
            for (int i = 0; i < mAnswersState.length; i++) {
                boolean checked = false;
                for (int k = 0; k < mAnswersState[0].length; k++) {
                    if (mAnswersState[i][k].isChecked()) {
                        checked = true;
                    }
                }
                mLine[i] = checked;
            }
        }
    }

    @Override
    public void onRowHeaderClick(final int row) {
        showAdditionalInfoDialog(mLeftSide.get(row - 1).getElementOptionsR());
    }

    @Override
    public void onColumnHeaderClick(final int column) {
        showAdditionalInfoDialog(mTopSide.get(column - 1).getElementOptionsR());
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
                .setPositiveButton(R.string.view_OK, (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = dialog.create();

        if (mContext != null && !mContext.isFinishing()) {
            alertDialog.show();
        }
    }

    private static class TableItemViewHolder extends ViewHolderImpl {

        RelativeLayout mCont;
        FrameLayout mOpenAnswerFrame;
        EditText mOpenAnswerEditText;
        CustomCheckableButton mTableItemRadioButton;
        CustomCheckableButton mTableItemCheckBox;
        View mDisableFrame;

        private TableItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            mCont = itemView.findViewById(R.id.table_item_cont);
            mOpenAnswerFrame = itemView.findViewById(R.id.open_answer_frame);
            mOpenAnswerEditText = itemView.findViewById(R.id.open_answer_edittext);
            mTableItemRadioButton = itemView.findViewById(R.id.table_item_radio_button);
            mTableItemCheckBox = itemView.findViewById(R.id.table_item_check_box);
            mDisableFrame = itemView.findViewById(R.id.disable_frame);

            mOpenAnswerEditText.setFocusableInTouchMode(false);
        }
    }

    private static class TableHeaderColumnViewHolder extends ViewHolderImpl {

        TextView mHeaderColumnTextView;
        RelativeLayout mColumnCont;
//        TextView mHeaderColumnDescriptionTextView;

        private TableHeaderColumnViewHolder(@NonNull final View itemView) {
            super(itemView);
            mHeaderColumnTextView = itemView.findViewById(R.id.table_header_column_text_view);
            mColumnCont = itemView.findViewById(R.id.column_cont);
//            mHeaderColumnDescriptionTextView = itemView.findViewById(R.id.table_header_column_description_text_view);
        }
    }

    private static class TableHeaderRowViewHolder extends ViewHolderImpl {

        TextView mHeaderRowTextView;
        TextView mHeaderRowDescriptionTextView;
        RelativeLayout mRowCont;

        TableHeaderRowViewHolder(@NonNull final View itemView) {
            super(itemView);
            mHeaderRowTextView = itemView.findViewById(R.id.table_header_row_text_view);
            mHeaderRowDescriptionTextView = itemView.findViewById(R.id.table_header_row_description_text_view);
            mRowCont = itemView.findViewById(R.id.row_cont);
        }
    }

    private static class TableHeaderLeftTopViewHolder extends ViewHolderImpl {

        TextView mHeaderLeftTopTextView;

        private TableHeaderLeftTopViewHolder(@NonNull final View itemView) {
            super(itemView);

            mHeaderLeftTopTextView = itemView.findViewById(R.id.table_header_left_top_text_view);
        }
    }

    public AnswerState[][] getmAnswersState() {
        return mAnswersState;
    }

    public void setmAnswersState(AnswerState[][] mAnswersState) {
        this.mAnswersState = mAnswersState;
        setLine();
    }

    public boolean isCompleted() {
        boolean completed = false;
        for (int i = 0; i < mAnswersState.length; i++) {
            int answersCounter = 0;
            for (int k = 0; k < mAnswersState[i].length; k++) {

                if (mAnswersState[i][k].isChecked()) {
                    answersCounter++;
                }
            }
            if (answersCounter > 0) {
                Integer min = mQuestions.get(i).getElementOptionsR().getMin_answers();
                Integer max = mQuestions.get(i).getElementOptionsR().getMax_answers();
                if (min != null && answersCounter < min) {
                    completed = false;
                    mContext.showToastfromActivity("В строке " + (i + 1) + " выберите минимум " + min + " ответа");
                    return completed;
                } else {
                    completed = true;
                }
                if (max != null && answersCounter > max) {
                    completed = false;
                    mContext.showToastfromActivity("В строке " + (i + 1) + " выберите максимум " + max + " ответа");
                    return completed;
                } else {
                    completed = true;
                }
            } else {
                completed = false;
                mContext.showToastfromActivity("Пожалуйста ответьте на все вопросы");
                return completed;
            }
        }
        return completed;
    }

    public interface OnTableAnswerClickListener {
        void onAnswerClick(int row, int column);
    }
}