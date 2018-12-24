package pro.quizer.quizerexit.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.ElementModel;

public class TableQuestionAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> implements OnItemClickListener {

    private final LayoutInflater mLayoutInflater;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mHeaderHeight;
    private final int mHeaderWidth;
    private final ElementModel mElement;
    private Context mContext;
    private List<ElementModel> mQuestions;
    private List<ElementModel> mAnswers;
    private Runnable mRefreshRunnable;

    public List<ElementModel> processNext() throws Exception {
        return new ArrayList<>();
    }

    public TableQuestionAdapter(Context context, final ElementModel pElement, final Runnable pRefreshRunnable) {
        // TODO: 24.12.2018 call run method
        setOnItemClickListener(this);
        mRefreshRunnable = pRefreshRunnable;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mElement = pElement;
        Resources res = context.getResources();
        mColumnWidth = res.getDimensionPixelSize(R.dimen.column_width);
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);
        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);
        mHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width);

        mQuestions = mElement.getSubElementsByType(ElementType.QUESTION);
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

        clickedElement.setChecked(!isElementChecked);

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