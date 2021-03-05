package pro.quizer.quizer3.adapter;

import android.util.Log;
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
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.Fonts;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.PageSelectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private List<SelectItem> answersFullList;
    private List<SelectItem> answers;
    private boolean isMulti;
    private String log = "";
    private long time = 0L;

    public SelectAdapter(List<SelectItem> answers, boolean isMulti, OnAnswerClickListener onAnswerClickListener) {
        this.answers = answers;
        this.answersFullList = answers;
        this.isMulti = isMulti;
        this.onAnswerClickListener = onAnswerClickListener;
        this.time = DateUtils.getFullCurrentTime();
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
            log = log + DateUtils.getFullCurrentTime() + ": 0." + position + " | ";
            cont.setOnClickListener(v -> checkItem(position));
        }

        public void checkItem(int position) {
            log = log + DateUtils.getFullCurrentTime() + ": Press checkbox | ";
            Log.d("T-L.SelectAdapter", "checkItem: " + log);
            if (isMulti) {
                SelectItem item = answers.get(position);
                item.setChecked(!item.isChecked());
                answers.set(position, item);
            } else {
                if (!answers.get(position).isChecked()) {
                    log = log + DateUtils.getFullCurrentTime() + ": 1 | ";
                    for (SelectItem item : answers) {
                        item.setChecked(false);
                    }
                    log = log + DateUtils.getFullCurrentTime() + ": 2 | ";
                    for (int i = 0; i < answersFullList.size(); i++) {
//                        answers.get(i).setChecked(i == position);
                        if (answersFullList.get(i).getTitle().equals(answers.get(position).getTitle())) {
                            answersFullList.get(i).setChecked(true);
                            answers.get(position).setChecked(true);
                        } else {
                            answersFullList.get(i).setChecked(false);
                        }
                        log = log + DateUtils.getFullCurrentTime() + ": 3." + i + " | ";
                    }
                }
            }
            notifyDataSetChanged();
            log = log + DateUtils.getFullCurrentTime() + ": 4 | ";
            onItemClickListener.onAnswerClick(answers);
            log = log + DateUtils.getFullCurrentTime() + ": 5 | ";
        }

    }

    public interface OnAnswerClickListener {
        void onAnswerClick(List<SelectItem> answers);
    }

    public List<SelectItem> getAnswers() {
        return answersFullList;
    }

    public void setAnswers(List<SelectItem> answers) {
        this.answers = answers;
        notifyDataSetChanged();
    }

    public String getLog() {
        return log;
    }
}
