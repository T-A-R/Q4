package pro.quizer.quizer3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.utils.Fonts;

public class QuestionAdapter  extends RecyclerView.Adapter<QuestionAdapter.ListObjectViewHolder>  {

    private OnAnswerClickListener onAnswerClickListener;
    private List<ElementItemR> answersList;

    public QuestionAdapter(List<ElementItemR> answersList, OnAnswerClickListener onAnswerClickListener) {
        this.answersList = answersList;
        this.onAnswerClickListener = onAnswerClickListener;
    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_answer_list, viewGroup, false);
        return new ListObjectViewHolder(view, onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListObjectViewHolder holder, int position) {
        holder.bind(answersList.get(position));
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class ListObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView answerTitle;
        TextView answerDesc;
        EditText answerEditText;
        ImageView button;
        RelativeLayout openAnswerCont;
        LinearLayout cont;
        boolean isChecked = false;

        OnAnswerClickListener onUserClickListener;

        public ListObjectViewHolder(@NonNull View itemView, OnAnswerClickListener onUserClickListener) {
            super(itemView);

            answerTitle = (TextView) itemView.findViewById(R.id.answer);
            answerDesc = (TextView) itemView.findViewById(R.id.answer_desc);
            answerEditText = (EditText) itemView.findViewById(R.id.edit_answer);
            button = (ImageView) itemView.findViewById(R.id.radio_button);
            openAnswerCont = (RelativeLayout) itemView.findViewById(R.id.open_question);
            cont = (LinearLayout) itemView.findViewById(R.id.answer_cont);

            answerTitle.setTypeface(Fonts.getFuturaPtBook());
            answerDesc.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setTypeface(Fonts.getFuturaPtBook());

            this.onUserClickListener = onUserClickListener;
//            button.setOnClickListener(this);
            cont.setOnClickListener(this);
        }

        public void bind(final ElementItemR item) {
            answerTitle.setText(item.getElementOptionsR().getTitle());
            if(item.getElementOptionsR().getDescription() != null) {
                answerDesc.setVisibility(View.VISIBLE);
                answerDesc.setText(item.getElementOptionsR().getDescription());
            }
        }

        @Override
        public void onClick(View v) {
            if(isChecked) {
                isChecked = false;
                button.setImageResource( R.drawable.radio_button_unchecked);
            } else {
                isChecked = true;
                button.setImageResource( R.drawable.radio_button_checked);
                answerEditText.requestFocus();
            }

            String answer = null;
            if(answerEditText.getText() != null)
                answer = answerEditText.getText().toString();
            onUserClickListener.onAnswerClick(getAdapterPosition(), isChecked, answer);
        }
    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int position, boolean enabled, String answer);
    }
}
