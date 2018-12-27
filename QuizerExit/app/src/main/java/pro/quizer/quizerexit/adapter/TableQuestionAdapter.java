package pro.quizer.quizerexit.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;
import com.vicmikhailau.maskededittext.MaskedEditText;
import com.vicmikhailau.maskededittext.MaskedFormatter;
import com.vicmikhailau.maskededittext.MaskedWatcher;

import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.OptionsOpenType;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class TableQuestionAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> implements OnItemClickListener, IAdapter {

    private final LayoutInflater mLayoutInflater;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mHeaderHeight;
    private final int mHeaderWidth;
    private Context mContext;
    private List<ElementModel> mQuestions;
    private List<ElementModel> mAnswers;
    private Runnable mRefreshRunnable;
    private ElementModel mCurrentElement;

    @Override
    public int processNext() throws Exception {
        for (final ElementModel question : mQuestions) {
            if (question != null && question.getCountOfSelectedSubElements() == 0) {
                throw new Exception(mContext.getString(R.string.incorrect_select_min_answers_table));
            }
        }

        for (int index = 0; index < mAnswers.size(); index++) {
            final ElementModel model = mAnswers.get(index);

            if (model != null && model.isFullySelected()) {
                mCurrentElement.setEndTime(DateUtils.getCurrentTimeMillis());
                return model.getOptions().getJump();
            }
        }

        throw new Exception(mContext.getString(R.string.error_counting_next_element));
    }

    public TableQuestionAdapter(final ElementModel pCurrentElement, final Context context, final List<ElementModel> pQuestions, final Runnable pRefreshRunnable) {
        mCurrentElement = pCurrentElement;
        setOnItemClickListener(this);
        mRefreshRunnable = pRefreshRunnable;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        final Resources res = context.getResources();
        mColumnWidth = res.getDimensionPixelSize(R.dimen.column_width);
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);
        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);
        mHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width);

        mQuestions = pQuestions;

        if (mQuestions.get(0) != null) {
            mQuestions.add(0, null);
        }

        for (final ElementModel question : mQuestions)
            if (question != null) {
            final List<ElementModel> answers = question.getElements();

            if (answers.get(0) != null) {
                answers.add(0, null);
            }
        }

        mAnswers = mQuestions.get(1).getElements();
    }

    @Override
    public int getRowCount() {
        return mQuestions.size();
    }

    @Override
    public int getColumnCount() {
        return mAnswers.size();
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull final ViewGroup parent) {
        return new TableItemViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item, parent, false));
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
            vh.mTableItemCheckBox.setChecked(currentElement.isChecked());
        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull final ViewHolderImpl viewHolder, final int column) {
        final TableHeaderColumnViewHolder vh = (TableHeaderColumnViewHolder) viewHolder;
        // TODO: 24.12.2018 OR getText()
        UiUtils.setTextOrHide(vh.mHeaderColumnTextView, mAnswers.get(column).getOptions().getTitle());
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull final ViewHolderImpl viewHolder, final int row) {
        final TableHeaderRowViewHolder vh = (TableHeaderRowViewHolder) viewHolder;
        UiUtils.setTextOrHide(vh.mHeaderRowTextView, mQuestions.get(row).getOptions().getTitle());
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull final ViewHolderImpl viewHolder) {
        final TableHeaderLeftTopViewHolder vh = (TableHeaderLeftTopViewHolder) viewHolder;
        UiUtils.setTextOrHide(vh.mHeaderLeftTopTextView, "Вопрос/ответ");
    }

    @Override
    public int getColumnWidth(final int column) {
        return mColumnWidth;
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(final int row) {
        return mRowHeight;
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }

    private ElementModel getElement(final int row, final int column) {
        final int questionIndex = row;
        final int answerIndex = column;

        final ElementModel element = mQuestions.get(questionIndex);

        if (element != null) {
            return element.getElements().get(answerIndex);
        } else {
            return null;
        }
    }

    @Override
    public void onItemClick(final int row, final int column) {
        Toast.makeText(mContext, "Строка: " + row + " столбец: " + column, Toast.LENGTH_LONG).show();
        final ElementModel clickedElement = getElement(row, column);

        if (clickedElement == null) {
            return;
        }
        final boolean isElementChecked = clickedElement.isChecked();
        final OptionsModel options = clickedElement.getOptions();
        final String openType = options.getOpenType();

        if (!OptionsOpenType.CHECKBOX.equals(openType)) {
            final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
            final View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setView(mView);

            final MaskedEditText mEditText = mView.findViewById(R.id.answer_edit_text);

            final String placeholder = options.getPlaceholder();
            final String textAnswer = clickedElement.getTextAnswer();

            mEditText.setVisibility(View.VISIBLE);
            mEditText.setHint(StringUtils.isEmpty(placeholder) ? mContext.getString(R.string.default_placeholder) : placeholder);
            mEditText.setText(textAnswer);

            final Context context = mEditText.getContext();

            switch (options.getOpenType()) {
                case OptionsOpenType.TIME:
                    mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                    mEditText.setHint(R.string.hint_time);
                    final MaskedFormatter timeFormatter = new MaskedFormatter(context.getString(R.string.mask_time));
                    mEditText.addTextChangedListener(new MaskedWatcher(timeFormatter, mEditText));

                    break;
                case OptionsOpenType.DATE:
                    mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                    mEditText.setHint(R.string.hint_date);
                    final MaskedFormatter dateFormatter = new MaskedFormatter(context.getString(R.string.mask_date));
                    mEditText.addTextChangedListener(new MaskedWatcher(dateFormatter, mEditText));

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
                                if (mContext instanceof BaseActivity) {
                                    ((BaseActivity) mContext).showToast(mContext.getString(R.string.fill_input));
                                }

                                return;
                            }

                            clickedElement.setTextAnswer(answer);
                            clickedElement.setChecked(true);
                            notifyDataSetChanged();
                            mRefreshRunnable.run();
                        }
                    })

                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialogBox, final int id) {
                                    clickedElement.setTextAnswer(Constants.Strings.EMPTY);
                                    clickedElement.setChecked(false);
                                    dialogBox.cancel();
                                    notifyDataSetChanged();
                                    mRefreshRunnable.run();
                                }
                            });

            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        } else {
            clickedElement.setChecked(!isElementChecked);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onRowHeaderClick(final int row) {

    }

    @Override
    public void onColumnHeaderClick(final int column) {

    }

    @Override
    public void onLeftTopHeaderClick() {

    }

    private static class TableItemViewHolder extends ViewHolderImpl {

        CheckBox mTableItemCheckBox;

        private TableItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            mTableItemCheckBox = itemView.findViewById(R.id.table_item_check_box);
        }
    }

    private static class TableHeaderColumnViewHolder extends ViewHolderImpl {
        TextView mHeaderColumnTextView;

        private TableHeaderColumnViewHolder(@NonNull final View itemView) {
            super(itemView);
            mHeaderColumnTextView = itemView.findViewById(R.id.table_header_column_text_view);
        }
    }

    private static class TableHeaderRowViewHolder extends ViewHolderImpl {
        TextView mHeaderRowTextView;

        TableHeaderRowViewHolder(@NonNull final View itemView) {
            super(itemView);
            mHeaderRowTextView = itemView.findViewById(R.id.table_header_row_text_view);
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