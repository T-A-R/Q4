package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
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
import pro.quizer.quizer3.utils.UiUtils;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.PageSelectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private List<SelectItem> answersFullList;
    private List<SelectItem> answers;
    private boolean isMulti;
    private boolean isAvia;
    private String log = "";
    private long time = 0L;

    public SelectAdapter(List<SelectItem> answers, boolean isMulti, boolean isAvia, OnAnswerClickListener onAnswerClickListener) {
        this.answers = answers;
        this.answersFullList = answers;
        this.isMulti = isMulti;
        this.isAvia = isAvia;
        this.onAnswerClickListener = onAnswerClickListener;
        this.time = DateUtils.getFullCurrentTime();
//        answersFullList.get(0).setEnabled(false); //TODO FOR TESTS
    }

    @NonNull
    @Override
    public PageSelectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_select_auto, viewGroup, false);
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

        @SuppressLint("ResourceAsColor")
        public void bind(final SelectItem item, int position) {
            answerText.setText(item.getTitle());
            if(!item.isEnabled()) {
                answerText.setTextColor(R.color.lightGray);
                answerText.setClickable(false);
            }
//            else {
//                answerText.setTextColor(R.color.brand_color_dark);
//            }
            Log.d("T-L.SelectAdapter", "bind ENABLED: " + position + "." + item.isEnabled());
            if(!item.isEnabled()) {
                answerText.setTextColor(R.color.lightGray);
                answerText.setClickable(false);
                checker.setImageResource(isMulti ? R.drawable.checkbox_unchecked_dark_gray : R.drawable.radio_button_dark_gray);
            } else {
                checker.setImageResource(
                        isMulti ?
                                item.isChecked() ?
                                        isAvia ? R.drawable.checkbox_checked_red : R.drawable.checkbox_checked :
                                        isAvia ? R.drawable.checkbox_unchecked_dark_gray : R.drawable.checkbox_unchecked
                                : item.isChecked() ?
                                isAvia ? R.drawable.radio_button_checked_red : R.drawable.radio_button_checked :
                                isAvia ? R.drawable.radio_button_dark_gray : R.drawable.radio_button_unchecked);
            }
            cont.setOnClickListener(v -> checkItem(position));
        }

        public void checkItem(int position) {
            if(!answers.get(position).isEnabled()) return;
            if (isMulti) {
                SelectItem item = answers.get(position);
                item.setChecked(!item.isChecked());
                answers.set(position, item);
            } else {
                if (!answers.get(position).isChecked()) {
                    for (SelectItem item : answers) {
                        item.setChecked(false);
                    }
                    for (int i = 0; i < answersFullList.size(); i++) {
                        if (answersFullList.get(i).getTitle().equals(answers.get(position).getTitle())) {
                            answersFullList.get(i).setChecked(true);
                            answers.get(position).setChecked(true);
                        } else {
                            answersFullList.get(i).setChecked(false);
                        }
                    }
                }
            }
            notifyDataSetChanged();
//            onItemClickListener.onAnswerClick(answers); //TODO REFACTOR
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
