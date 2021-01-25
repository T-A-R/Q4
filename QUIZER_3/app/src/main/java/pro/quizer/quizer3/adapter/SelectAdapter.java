package pro.quizer.quizer3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.state.SelectItem;
import pro.quizer.quizer3.utils.Fonts;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.PageSelectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private List<SelectItem> answers;
    private boolean isMulti;

    public SelectAdapter(List<SelectItem> answers, boolean isMulti, OnAnswerClickListener onAnswerClickListener) {
        this.answers = answers;
        this.isMulti = isMulti;
        this.onAnswerClickListener = onAnswerClickListener;
    }

    @NonNull
    @Override
    public PageSelectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_select_avia, viewGroup, false);
        return new PageSelectViewHolder(view, onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PageSelectViewHolder holder, int position) {
        holder.bind(answers.get(position), position);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class PageSelectViewHolder extends RecyclerView.ViewHolder {

        TextView answerText;
        ImageView checker;
        View cont;

        OnAnswerClickListener onItemClickListener;

        public PageSelectViewHolder(@NonNull View itemView, OnAnswerClickListener onItemClickListener) {
            super(itemView);

            cont = itemView.findViewById(R.id.cont_select_holder);
            answerText = itemView.findViewById(R.id.select_answer_title);
            checker = itemView.findViewById(R.id.select_checker);
            answerText.setTypeface(Fonts.getFuturaPtBook());

            this.onItemClickListener = onItemClickListener;
        }

        public void bind(final SelectItem item, int position) {
            answerText.setText(item.getTitle());
            answerText.setEnabled(item.isEnabled());
            checker.setImageResource(isMulti ? item.isChecked() ? R.drawable.checkbox_checked_red : R.drawable.checkbox_unchecked_dark_gray
                    : item.isChecked() ? R.drawable.radio_button_checked_red : R.drawable.radio_button_dark_gray);

            cont.setOnClickListener(v -> checkItem(position));
        }

        public void checkItem(int position) {
            if (isMulti) {
                SelectItem item = answers.get(position);
                item.setChecked(!item.isChecked());
                answers.set(position, item);
            } else {
                if (!answers.get(position).isChecked()) {
                    for (int i = 0; i < answers.size(); i++) {
                        answers.get(i).setChecked(i == position);
                    }
                }
            }
            notifyDataSetChanged();
            onItemClickListener.onAnswerClick(answers);
        }

    }

    public interface OnAnswerClickListener {
        void onAnswerClick(List<SelectItem> answers);
    }

    public List<SelectItem> getAnswers() {
        return answers;
    }

}
