package pro.quizer.quizer3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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

import static pro.quizer.quizer3.MainActivity.TAG;
import android.util.Log;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ListObjectViewHolder> {

    private OnAnswerClickListener onAnswerClickListener;
    private ElementItemR question;
    private List<ElementItemR> answersList;
    private String[] mDataset;
    private boolean[] mAnswerChecked;
    private int lastSelectedPosition = -1;
    public boolean isOpen;
    public boolean isMulti;
    private String openType;

    public QuestionAdapter(ElementItemR question, List<ElementItemR> answersList, OnAnswerClickListener onAnswerClickListener) {
        this.question = question;
        this.answersList = answersList;
        this.onAnswerClickListener = onAnswerClickListener;
        this.mDataset = new String[answersList.size()];
        this.mAnswerChecked = new boolean[answersList.size()];
        if (question.getElementOptionsR().getOpen_type() != null) {
            this.isOpen = true;
            this.openType = question.getElementOptionsR().getOpen_type();
        }
        this.isMulti = question.getElementOptionsR().isPolyanswer();
    }

    public void checkUnchecker() {
        //TODO UNCHECKER
    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_answer_list, viewGroup, false);
        ListObjectViewHolder vh = new ListObjectViewHolder(view, onAnswerClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListObjectViewHolder holder, int position) {
        holder.bind(answersList.get(position));
        if (!isMulti) {
            if (position == lastSelectedPosition) {
                holder.editButton.setVisibility(View.GONE);
                holder.button.setImageResource(R.drawable.radio_button_checked);
                if (isOpen) {
                    holder.answerEditText.setVisibility(View.VISIBLE);
                    holder.answerEditText.requestFocus();
                } else {
                    holder.answerEditText.setVisibility(View.GONE);
                    holder.editButton.setVisibility(View.GONE);
                }
            } else {
                holder.button.setImageResource(R.drawable.radio_button_unchecked);
                if (isOpen) {
                    holder.answerEditText.setVisibility(View.GONE);
                    holder.editButton.setVisibility(View.VISIBLE);
                } else {
                    holder.answerEditText.setVisibility(View.GONE);
                    holder.editButton.setVisibility(View.GONE);
                }
            }
        } else {
            if (mAnswerChecked[position]) {
                holder.button.setImageResource(R.drawable.checkbox_checked);
            } else {
                holder.button.setImageResource(R.drawable.checkbox_unchecked);
            }
        }

        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        if (mDataset != null)
            holder.answerEditText.setText(mDataset[holder.getAdapterPosition()]);

    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class ListObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView answerTitle;
        TextView answerDesc;
        public EditText answerEditText;
        ImageView button;
        ImageView editButton;
        RelativeLayout openAnswerCont;
        LinearLayout cont;
        boolean isChecked = false;

        public MyCustomEditTextListener myCustomEditTextListener;
        OnAnswerClickListener onUserClickListener;

        public ListObjectViewHolder(@NonNull View itemView, OnAnswerClickListener onUserClickListener) {
            super(itemView);

            answerTitle = (TextView) itemView.findViewById(R.id.answer);
            answerDesc = (TextView) itemView.findViewById(R.id.answer_desc);
            answerEditText = (EditText) itemView.findViewById(R.id.edit_answer);
            button = (ImageView) itemView.findViewById(R.id.radio_button);
            editButton = (ImageView) itemView.findViewById(R.id.edit_button);
            openAnswerCont = (RelativeLayout) itemView.findViewById(R.id.open_question);
            cont = (LinearLayout) itemView.findViewById(R.id.answer_cont);

            answerTitle.setTypeface(Fonts.getFuturaPtBook());
            answerDesc.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setTypeface(Fonts.getFuturaPtBook());

            this.onUserClickListener = onUserClickListener;

            myCustomEditTextListener = new MyCustomEditTextListener();
            this.answerEditText.addTextChangedListener(myCustomEditTextListener);
            cont.setOnClickListener(this);
            editButton.setOnClickListener(this);

        }

        public void bind(final ElementItemR item) {
            answerTitle.setText(item.getElementOptionsR().getTitle());
            if (item.getElementOptionsR().getDescription() != null) {
                answerDesc.setVisibility(View.VISIBLE);
                answerDesc.setText(item.getElementOptionsR().getDescription());
            } else {
                answerDesc.setVisibility(View.GONE);
            }
            if(item.getElementOptionsR().isUnchecker()) {
                if(mAnswerChecked[getAdapterPosition()]) {
                    for(int i = 0; i < mAnswerChecked.length; i++) {
                        if(i != getAdapterPosition()) {
                            mAnswerChecked[i] = false;
                        }
                    }
                }
            }
        }

        @Override
        public void onClick(View v) {

            lastSelectedPosition = getAdapterPosition();
            notifyDataSetChanged();

            if (isMulti) {
                if (mAnswerChecked[getAdapterPosition()]) {
                    mAnswerChecked[getAdapterPosition()] = false;
                } else {
                    mAnswerChecked[getAdapterPosition()] = true;
                }
            }

            if (isChecked) {
                isChecked = false;
//                button.setImageResource( R.drawable.radio_button_unchecked);
            } else {
                isChecked = true;
//                button.setImageResource( R.drawable.radio_button_checked);
//                answerEditText.requestFocus();
            }

            String answer = null;
            if (answerEditText.getText() != null)
                answer = answerEditText.getText().toString();
            onUserClickListener.onAnswerClick(getAdapterPosition(), isChecked, answer);
        }
    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int position, boolean enabled, String answer);
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            mDataset[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public String[] getOpenAnswersText() {
        return mDataset;
    }

    public boolean[] getAnswersChecked() {
        return mAnswerChecked;
    }
}
