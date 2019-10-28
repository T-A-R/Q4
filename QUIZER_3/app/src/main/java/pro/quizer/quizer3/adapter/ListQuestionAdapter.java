package pro.quizer.quizer3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.utils.Fonts;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;

import android.util.Log;

public class ListQuestionAdapter extends RecyclerView.Adapter<ListQuestionAdapter.ListObjectViewHolder> {

    private OnAnswerClickListener onAnswerClickListener;
    private ElementItemR question;
    private List<ElementItemR> answersList;
    private List<AnswerState> answersState;
//    private String[] mDataset;
    private boolean[] mAnswerChecked;
    private int lastSelectedPosition = -1;
    public boolean isOpen = false;
    public boolean isMulti;
    private String openType;

    public ListQuestionAdapter(ElementItemR question, List<ElementItemR> answersList, OnAnswerClickListener onAnswerClickListener) {
        this.question = question;
        this.answersList = answersList;
        this.onAnswerClickListener = onAnswerClickListener;
//        this.mDataset = new String[answersList.size()];
//        this.mAnswerChecked = new boolean[answersList.size()];
        if (question.getElementOptionsR().getOpen_type() != null) {
            this.isOpen = true;
            this.openType = question.getElementOptionsR().getOpen_type();
        }
        this.isMulti = question.getElementOptionsR().isPolyanswer();
        this.answersState = new ArrayList<>();
        for (int i = 0; i < answersList.size(); i++) {
            this.answersState.add(new AnswerState(answersList.get(i).getRelative_id(), false, ""));
        }
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
        if (answersState == null) {
            holder.bind(answersList.get(position), null);
        } else {
            holder.bind(answersList.get(position), answersState.get(position));
        }
        if (!isMulti) {
            if (position == lastSelectedPosition) {
                holder.editButton.setVisibility(View.GONE);
                holder.button.setImageResource(R.drawable.radio_button_checked);
                holder.answerEditText.requestFocus();
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
            if (answersState.get(position).isChecked()) {
                holder.button.setImageResource(R.drawable.checkbox_checked);
            } else {
                holder.button.setImageResource(R.drawable.checkbox_unchecked);
            }
        }

        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
//        if (mDataset != null)
        holder.answerEditText.setText(answersState.get(holder.getAdapterPosition()).getData());

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
        RelativeLayout openQuestionCont;
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
            openQuestionCont = (RelativeLayout) itemView.findViewById(R.id.open_question);
            openAnswerCont = (RelativeLayout) itemView.findViewById(R.id.open_cont);
            cont = (LinearLayout) itemView.findViewById(R.id.answer_cont);

            answerTitle.setTypeface(Fonts.getFuturaPtBook());
            answerDesc.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

            this.onUserClickListener = onUserClickListener;

            myCustomEditTextListener = new MyCustomEditTextListener();
            this.answerEditText.addTextChangedListener(myCustomEditTextListener);
            cont.setOnClickListener(this);
            editButton.setOnClickListener(this);

        }

        public void bind(final ElementItemR item, AnswerState answerState) {
            answerTitle.setText(item.getElementOptionsR().getTitle());
            if (item.getElementOptionsR().getDescription() != null) {
                answerDesc.setVisibility(View.VISIBLE);
                answerDesc.setText(item.getElementOptionsR().getDescription());
            } else {
                answerDesc.setVisibility(View.GONE);
            }

            if (item.getElementOptionsR().getOpen_type().equals(CHECKBOX)) {
                openAnswerCont.setVisibility(View.GONE);
            } else {
                openAnswerCont.setVisibility(View.VISIBLE);
            }

            if (item.getElementOptionsR().isUnchecker()) {
                if (answerState.isChecked()) {
                    for (int i = 0; i < answersState.size(); i++) {
                        if (i != getAdapterPosition()) {
                            answersState.get(i).setChecked(false);
                        }
                    }
                }
            }

            if (answerState != null) {
                setChecked(answerState);
            }
        }

        public void setChecked(AnswerState answerState) {
            if (answerState.isChecked()) {
                editButton.setVisibility(View.GONE);
                answerEditText.setVisibility(View.VISIBLE);
                answerEditText.setText(answerState.getData());
//                answerEditText.requestFocus();
            }
        }

        @Override
        public void onClick(View v) {

            lastSelectedPosition = getAdapterPosition();
            notifyDataSetChanged();

            if (isMulti && !answersList.get(lastSelectedPosition).getElementOptionsR().isUnchecker()) {
                if (answersState.get(lastSelectedPosition).isChecked()) {
                    answersState.get(lastSelectedPosition).setChecked(false);
                } else {
                    answersState.get(lastSelectedPosition).setChecked(true);
                }
            } else {
                for (int i = 0; i < answersState.size(); i++) {
                    answersState.get(i).setChecked(false);
                    answersState.get(i).setData("");
                }
                answersState.get(lastSelectedPosition).setChecked(true);
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
            onUserClickListener.onAnswerClick(lastSelectedPosition, isChecked, answer);
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
            answersState.get(position).setData(charSequence.toString());
//            mDataset[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

//    public String[] getOpenAnswersText() {
//        return mDataset;
//    }

//    public boolean[] getAnswersChecked() {
//        return mAnswerChecked;
//    }

    public List<AnswerState> getAnswers() {
        return answersState;
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.lastSelectedPosition = lastSelectedPosition;
        Log.d(TAG, "setLastSelectedPosition: " + lastSelectedPosition);
    }

    public int getLastSelectedPosition() {
        Log.d(TAG, "getLastSelectedPosition: " + lastSelectedPosition);
        return lastSelectedPosition;
    }

    public void setAnswers(List<AnswerState> answers) {
        if (answers != null) {
            for (int i = 0; i < answers.size(); i++) {
                Log.d(TAG, "setAnswers: " + answers.get(i).getRelative_id() + " : " + answers.get(i).getData() + " : " + answers.get(i).isChecked());
            }
            this.answersState = answers;
        } else {
            Log.d(TAG, "setAnswers: NULL");
        }
    }
}
