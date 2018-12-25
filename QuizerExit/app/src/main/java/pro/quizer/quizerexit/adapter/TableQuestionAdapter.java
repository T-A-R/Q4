package pro.quizer.quizerexit.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Parcel;
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

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.AttributeOpenType;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.AttributesModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.StringUtils;

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

    @Override
    public List<ElementModel> processNext() throws Exception {
        return new ArrayList<>();
    }

    public TableQuestionAdapter(Context context, final List<ElementModel> pQuestions, final Runnable pRefreshRunnable) {
        setOnItemClickListener(this);

        // TODO: 24.12.2018 call run method
        mRefreshRunnable = pRefreshRunnable;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        Resources res = context.getResources();
        mColumnWidth = res.getDimensionPixelSize(R.dimen.column_width);
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);
        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);
        mHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width);

        mQuestions = pQuestions;
        mAnswers = mQuestions.get(0).getSubElementsByType(ElementType.ANSWER);
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
    public ViewHolderImpl onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return new TableItemViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TableHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_header_column, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TableHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_header_row, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TableHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.adapter_table_item_header_left_top, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, final int row, final int column) {
        final TableItemViewHolder vh = (TableItemViewHolder) viewHolder;

        final ElementModel currentElement = getElement(row, column);

        vh.mTableItemCheckBox.setChecked(currentElement.isChecked());
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TableHeaderColumnViewHolder vh = (TableHeaderColumnViewHolder) viewHolder;
        // TODO: 24.12.2018 OR getText()
        vh.mHeaderColumnTextView.setText(mAnswers.get(column).getOptions().getTitle());
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TableHeaderRowViewHolder vh = (TableHeaderRowViewHolder) viewHolder;
        vh.mHeaderRowTextView.setText(mQuestions.get(row).getOptions().getTitle());
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TableHeaderLeftTopViewHolder vh = (TableHeaderLeftTopViewHolder) viewHolder;
        vh.mHeaderLeftTopTextView.setText("Вопрос/ответ");
    }

    @Override
    public int getColumnWidth(int column) {
        return mColumnWidth;
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(int row) {
        return mRowHeight;
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }

    private ElementModel getElement(int row, int column) {
        final int questionIndex = row - 1;
        final int answerIndex = column - 1;

        return mQuestions.get(questionIndex).getElements().get(answerIndex);
    }

    @Override
    public void onItemClick(int row, int column) {
        Toast.makeText(mContext, "Строка: " + row + " столбец: " + column, Toast.LENGTH_LONG).show();
        final ElementModel clickedElement = getElement(row, column);
        final boolean isElementChecked = clickedElement.isChecked();
        final AttributesModel options = clickedElement.getOptions();
        final String openType = options.getOpenType();

        if (!AttributeOpenType.CHECKBOX.equals(openType)) {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
            View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setView(mView);

            final MaskedEditText mEditText = mView.findViewById(R.id.answer_edit_text);

            final String placeholder = options.getPlaceholder();
            final String textAnswer = clickedElement.getTextAnswer();

            mEditText.setVisibility(View.VISIBLE);
            mEditText.setHint(StringUtils.isEmpty(placeholder) ? mContext.getString(R.string.default_placeholder) : placeholder);
            mEditText.setText(textAnswer);

            final Context context = mEditText.getContext();

            switch (options.getOpenType()) {
                case AttributeOpenType.TIME:
                    mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                    mEditText.setHint(R.string.hint_time);
                    MaskedFormatter timeFormatter = new MaskedFormatter(context.getString(R.string.mask_time));
                    mEditText.addTextChangedListener(new MaskedWatcher(timeFormatter, mEditText));

                    break;
                case AttributeOpenType.DATE:
                    mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                    mEditText.setHint(R.string.hint_date);
                    MaskedFormatter dateFormatter = new MaskedFormatter(context.getString(R.string.mask_date));
                    mEditText.addTextChangedListener(new MaskedWatcher(dateFormatter, mEditText));

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


            dialog.setCancelable(false)
                    .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
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
                        }
                    })

                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    clickedElement.setTextAnswer(Constants.Strings.EMPTY);
                                    clickedElement.setChecked(false);
                                    dialogBox.cancel();
                                    notifyDataSetChanged();
                                }
                            });

            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        } else {
            clickedElement.setChecked(!isElementChecked);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onRowHeaderClick(int row) {

    }

    @Override
    public void onColumnHeaderClick(int column) {

    }

    @Override
    public void onLeftTopHeaderClick() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    private static class TableItemViewHolder extends ViewHolderImpl {

        CheckBox mTableItemCheckBox;

        private TableItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTableItemCheckBox = itemView.findViewById(R.id.table_item_check_box);
        }
    }

    private static class TableHeaderColumnViewHolder extends ViewHolderImpl {
        TextView mHeaderColumnTextView;

        private TableHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            mHeaderColumnTextView = itemView.findViewById(R.id.table_header_column_text_view);
        }
    }

    private static class TableHeaderRowViewHolder extends ViewHolderImpl {
        TextView mHeaderRowTextView;

        TableHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            mHeaderRowTextView = itemView.findViewById(R.id.table_header_row_text_view);
        }
    }

    private static class TableHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView mHeaderLeftTopTextView;

        private TableHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);

            mHeaderLeftTopTextView = itemView.findViewById(R.id.table_header_left_top_text_view);
        }
    }
}