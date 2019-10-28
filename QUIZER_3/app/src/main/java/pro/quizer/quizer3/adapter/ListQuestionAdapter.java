package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.OptionsOpenType;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.utils.Fonts;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;
import static pro.quizer.quizer3.model.OptionsOpenType.DATE;
import static pro.quizer.quizer3.model.OptionsOpenType.NUMBER;
import static pro.quizer.quizer3.model.OptionsOpenType.TEXT;
import static pro.quizer.quizer3.model.OptionsOpenType.TIME;

import android.util.Log;
import android.widget.TimePicker;

public class ListQuestionAdapter extends RecyclerView.Adapter<ListQuestionAdapter.ListObjectViewHolder> {

    private OnAnswerClickListener onAnswerClickListener;
    private ElementItemR question;
    private List<ElementItemR> answersList;
    private List<AnswerState> answersState;
    private int lastSelectedPosition = -1;
    public boolean isOpen = false;
    public boolean isMulti;
    private String openType;
    private MainActivity mActivity;

    public ListQuestionAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.question = question;
        this.answersList = answersList;
        this.onAnswerClickListener = onAnswerClickListener;
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

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_answer_list, viewGroup, false);
        ListObjectViewHolder vh = new ListObjectViewHolder(view, onAnswerClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListObjectViewHolder holder, int position) {
//        if (answersState == null) {
//            holder.bind(answersList.get(position), null);
//        } else {
        holder.bind(answersList.get(position), position);
//        }
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

        public void bind(final ElementItemR item, int position) {
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

//            if (item.getElementOptionsR().isUnchecker()) {
//                if (answersState.get(position).isChecked()) {
//                    for (int i = 0; i < answersState.size(); i++) {
//                        if (i != getAdapterPosition()) {
//                            answersState.get(i).setChecked(false);
//                        }
//                    }
//                }
//            }

            setChecked(item, position);

        }

        public void setChecked(final ElementItemR item, int position) {
            if (answersState.get(position).isChecked()) {
                editButton.setVisibility(View.GONE);
                answerEditText.setVisibility(View.VISIBLE);
                answerEditText.setText(answersState.get(position).getData());
                if (position == lastSelectedPosition) {


                    if (item.getElementOptionsR().getOpen_type().equals(TEXT)) {
                        answerEditText.setEnabled(true);
                        answerEditText.requestFocus();
                        answerEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        mActivity.showKeyboard();
                    } else if (item.getElementOptionsR().getOpen_type().equals(NUMBER)) {
                        answerEditText.setEnabled(true);
                        answerEditText.requestFocus();
                        answerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mActivity.showKeyboard();
                    } else if (item.getElementOptionsR().getOpen_type().equals(TIME)) {
                        setTime(answerEditText);
                        answersState.get(position).setData(answerEditText.getText().toString());
                        answerEditText.setEnabled(false);
                    } else if (item.getElementOptionsR().getOpen_type().equals(DATE)) {
                        setDate(answerEditText);
                        answersState.get(position).setData(answerEditText.getText().toString());
                        answerEditText.setEnabled(false);
                    }
                } else {
                    answerEditText.setEnabled(false);
                }

            } else {
                answerEditText.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {

            lastSelectedPosition = getAdapterPosition();
            notifyItemChanged(lastSelectedPosition);

            Log.d(TAG, "===========================");
            for (int i = 0; i < answersState.size(); i++) {
                Log.d(TAG, i + ": " + answersState.get(i).isChecked() + " / " + answersState.get(i).getData());
            }

            if (isMulti && !answersList.get(lastSelectedPosition).getElementOptionsR().isUnchecker()) {

                if (answersState.get(lastSelectedPosition).isChecked()) {
                    answersState.get(lastSelectedPosition).setChecked(false);

                } else {
                    answersState.get(lastSelectedPosition).setChecked(true);
                }
            } else {
                notifyDataSetChanged();
                answersState.get(lastSelectedPosition).setChecked(true);
                unselectOther(lastSelectedPosition);
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

    public void unselectOther(int position) {
        for (int i = 0; i < answersState.size(); i++) {
            if (i != position)
                answersState.get(i).setChecked(false);
            answersState.get(i).setData("");
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
            if (lastSelectedPosition != -1)
                answersState.get(lastSelectedPosition).setData(charSequence.toString());
//            mDataset[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

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

    private Calendar mCalendar = Calendar.getInstance();

    // отображаем диалоговое окно для выбора даты
    public void setDate(final EditText pEditText) {
        if (!mActivity.isFinishing()) {
            new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mCalendar.set(Calendar.YEAR, year);
                    mCalendar.set(Calendar.MONTH, monthOfYear);
                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    setInitialDateTime(pEditText, true);
                }
            },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(final EditText pEditText) {
        if (!mActivity.isFinishing()) {
            new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    mCalendar.set(Calendar.MINUTE, minute);
                    setInitialDateTime(pEditText, false);
                }
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
}
