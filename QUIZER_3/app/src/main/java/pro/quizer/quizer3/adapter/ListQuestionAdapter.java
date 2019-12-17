package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.quota.QuotaUtils;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;
import static pro.quizer.quizer3.model.OptionsOpenType.DATE;
import static pro.quizer.quizer3.model.OptionsOpenType.NUMBER;
import static pro.quizer.quizer3.model.OptionsOpenType.TEXT;
import static pro.quizer.quizer3.model.OptionsOpenType.TIME;

import android.util.Log;
import android.widget.TimePicker;

import com.squareup.picasso.Picasso;

public class ListQuestionAdapter extends RecyclerView.Adapter<ListQuestionAdapter.ListObjectViewHolder> {

    private OnAnswerClickListener onAnswerClickListener;
    private ElementItemR question;
    private List<ElementItemR> answersList;
    private List<AnswerState> answersState;
    private int lastSelectedPosition = -1;
    public boolean isOpen = false;
    public boolean isMulti;
    public boolean isPressed = false;
    private String openType;
    private MainActivity mActivity;
    private List<Integer> passedQuotaBlock;
    private ElementItemR[][] quotaTree;

    public ListQuestionAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, List<Integer> passedQuotaBlock, ElementItemR[][] quotaTree, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.question = question;
        this.passedQuotaBlock = passedQuotaBlock;
        this.quotaTree = quotaTree;


        if (question.getElementOptionsR() != null && question.getElementOptionsR().isRotation()) {
            List<ElementItemR> shuffleList = new ArrayList<>();
            for (ElementItemR elementItemR : answersList) {
                if (elementItemR.getElementOptionsR() != null && !elementItemR.getElementOptionsR().isFixed_order()) {
                    shuffleList.add(elementItemR);
                }
            }
            Collections.shuffle(shuffleList, new Random());
            int k = 0;

            for (int i = 0; i < answersList.size(); i++) {
                if (answersList.get(i).getElementOptionsR() != null && !answersList.get(i).getElementOptionsR().isFixed_order()) {
                    answersList.set(i, shuffleList.get(k));
                    k++;
                }
            }
        }
        this.answersList = answersList;
        Log.d(TAG, "ListQuestionAdapter: answers size= " + this.answersList.size());

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
//                holder.answerEditText.requestFocus();
                if (isOpen) {
                    holder.answerEditText.setVisibility(View.VISIBLE);
//                    holder.answerEditText.requestFocus();
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

        if (!answersList.get(position).isEnabled()) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.cont.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.bg_gray_shadow));
            } else {
                holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_gray_shadow));
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.cont.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.bg_shadow));
            } else {
                holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_shadow));
            }
        }

        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
//        showAnswers();
//        Log.d(TAG, "(1) !!!===!!!: " + holder.getAdapterPosition() + "/" + position + " " + answersState.get(position).getData());
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
        ImageView image1;
        ImageView image2;
        ImageView image3;
        RelativeLayout openQuestionCont;
        RelativeLayout openAnswerCont;
        LinearLayout contentCont;
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
            image1 = (ImageView) itemView.findViewById(R.id.answer_image_1);
            image2 = (ImageView) itemView.findViewById(R.id.answer_image_2);
            image3 = (ImageView) itemView.findViewById(R.id.answer_image_3);
            openQuestionCont = (RelativeLayout) itemView.findViewById(R.id.open_question);
            openAnswerCont = (RelativeLayout) itemView.findViewById(R.id.open_cont);
            cont = (LinearLayout) itemView.findViewById(R.id.answer_cont);
            contentCont = (LinearLayout) itemView.findViewById(R.id.answer_images_cont);

            answerTitle.setTypeface(Fonts.getFuturaPtBook());
            answerDesc.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

            this.onUserClickListener = onUserClickListener;

            myCustomEditTextListener = new MyCustomEditTextListener();
            this.answerEditText.addTextChangedListener(myCustomEditTextListener);
//            cont.setOnClickListener(this);
//            editButton.setOnClickListener(this);

        }

        public void bind(final ElementItemR item, int position) {
            answerTitle.setText(item.getElementOptionsR().getTitle());
            if (item.getElementOptionsR().getDescription() != null) {
                answerDesc.setVisibility(View.VISIBLE);
                answerDesc.setText(item.getElementOptionsR().getDescription());
            } else {
                answerDesc.setVisibility(View.GONE);
            }

            showContent(item, contentCont);

            if (item.getElementOptionsR().getOpen_type().equals(CHECKBOX)) {
                openAnswerCont.setVisibility(View.GONE);
            } else {
                openAnswerCont.setVisibility(View.VISIBLE);
            }

            if (!QuotaUtils.canShow(quotaTree, passedQuotaBlock, item.getRelative_id(), question.getElementOptionsR().getOrder())) {
                answerTitle.setTextColor(Color.parseColor("#AAAAAA"));
                item.setEnabled(false);
                Log.d(TAG, "ELEMENT DISABLED: " + item.getElementOptionsR().getTitle());
            } else {
                answerTitle.setTextColor(Color.parseColor("#000000"));
                item.setEnabled(true);
                Log.d(TAG, "ELEMENT ENABLED: " + item.getElementOptionsR().getTitle());
            }

            if (item.isEnabled()) {
//                Log.d(TAG, "bind enable: " + position);
                cont.setOnClickListener(this);
                editButton.setOnClickListener(this);
            } else {
//                Log.d(TAG, "bind disable: " + position);
            }

//            Log.d(TAG, "XXXXX ENABLED: " + item.getElementOptionsR().getTitle() + " : " +
//                    QuotaUtils.canShow(quotaTree, passedQuotaBlock, item.getRelative_id(), question.getElementOptionsR().getOrder()));

//            if (!QuotaUtils.canShow(quotaTree, passedQuotaBlock, item.getRelative_id())) {
//                showDisableToLog(item.getElementOptionsR().getTitle());
//            }

//            setEnabled(item, position);
            setChecked(item, position);

        }

        private void showContent(ElementItemR element, View cont) {
            final List<ElementContentsR> contents = element.getElementContentsR();

            if (contents != null && !contents.isEmpty()) {
                String data1 = null;
                String data2 = null;
                String data3 = null;
                data1 = contents.get(0).getData();
                if (contents.size() > 1)
                    data2 = contents.get(1).getData();
                if (contents.size() > 2)
                    data3 = contents.get(2).getData();

                    if (data1 != null) showPic(contentCont, image1, data1);
                    if (data2 != null) showPic(contentCont, image2, data2);
                    if (data3 != null) showPic(contentCont, image3, data3);

            } else {
                contentCont.setVisibility(View.GONE);
                image1.setVisibility(View.GONE);
                image2.setVisibility(View.GONE);
                image3.setVisibility(View.GONE);
            }
        }

        private void showPic(View cont, ImageView view, String data) {

            final String filePhotooPath = getFilePath(data);

            Log.d(TAG, "initViews: PICTURES: " + data + " " + filePhotooPath);
            if (StringUtils.isEmpty(filePhotooPath)) {
                return;
            }

            cont.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);

            Picasso.with(mActivity)
                    .load(new File(filePhotooPath))
                    .into(view);
        }

        private String getFilePath(final String data) {
            final String path = FileUtils.getFilesStoragePath(mActivity);
            final String url = data;

            if (StringUtils.isEmpty(url)) {
                return Constants.Strings.EMPTY;
            }

            final String fileName = FileUtils.getFileName(url);

            return path + FileUtils.FOLDER_DIVIDER + fileName;
        }

        public void setChecked(final ElementItemR item, int position) {
            if (answersState.get(position).isChecked()) {
                editButton.setVisibility(View.GONE);
                answerEditText.setVisibility(View.VISIBLE);
//                showAnswers();
//                Log.d(TAG, "(2) !!!===!!!: " + position + " " + answersState.get(position).getData());
                answerEditText.setText(answersState.get(position).getData());
                if (isPressed) {
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
//                            showAnswers();
                            answerEditText.setEnabled(false);
                        } else if (item.getElementOptionsR().getOpen_type().equals(DATE)) {
                            setDate(answerEditText);
                            answersState.get(position).setData(answerEditText.getText().toString());
                            answerEditText.setEnabled(false);
                        }
                    } else {
                        answerEditText.setEnabled(false);
                    }
                }
            } else {
                answerEditText.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        }

        public void setEnabled(int position) {
            if (answersList.get(position).getElementOptionsR().isUnchecker()) {
                if (answersState.get(position).isChecked()) {
                    for (int i = 0; i < answersState.size(); i++) {
                        if (i != position) {
                            Log.d(TAG, "set false 4: " + i);
                            answersList.get(i).setEnabled(false);
                        }
                    }
                } else {
                    for (int i = 0; i < answersState.size(); i++) {
                        Log.d(TAG, "set false 5: " + i);
                        answersList.get(i).setEnabled(true);
                    }
                }
            }

            if (!answersList.get(position).isEnabled()) {
                cont.setEnabled(false);
                editButton.setEnabled(false);
            } else {
                cont.setEnabled(true);
                editButton.setEnabled(true);
                cont.setOnClickListener(this);
                editButton.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
//            Log.d(TAG, "onClick: OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOKKKKKKKKKKKKKKKK!!!!!!!!!!!!");
            isPressed = true;
            lastSelectedPosition = getAdapterPosition();
            if (answersList.get(lastSelectedPosition).isEnabled()) {
                notifyItemChanged(lastSelectedPosition);

                if (isMulti && !answersList.get(lastSelectedPosition).getElementOptionsR().isUnchecker()) {

                    if (answersState.get(lastSelectedPosition).isChecked()) {
                        answersState.get(lastSelectedPosition).setChecked(false);

                    } else {
                        answersState.get(lastSelectedPosition).setChecked(true);
                    }
                } else if (isMulti && answersList.get(lastSelectedPosition).getElementOptionsR().isUnchecker()) {
                    notifyDataSetChanged();
                    if (!answersState.get(lastSelectedPosition).isChecked()) {
                        answersState.get(lastSelectedPosition).setChecked(true);
                        unselectOther(lastSelectedPosition);
                    } else {
                        answersState.get(lastSelectedPosition).setChecked(false);
                    }
                    setEnabled(lastSelectedPosition);
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
    }

    public void unselectOther(int position) {
        for (int i = 0; i < answersState.size(); i++) {
            if (i != position)
                answersState.get(i).setChecked(false);
            //TODO Enable if have to clear text
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
            if (lastSelectedPosition != -1 && isPressed)
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
        Log.d(TAG, "=============================");
        if (answers != null) {
            for (int i = 0; i < answers.size(); i++) {
                Log.d(TAG, i + ": " + answers.get(i).getRelative_id() + " : " + answers.get(i).getData() + " : " + answers.get(i).isChecked());
            }
            this.answersState = answers;
            for (int i = 0; i < answers.size(); i++) {
                if (answersList.get(i).getElementOptionsR().isUnchecker() && answers.get(i).isChecked()) {
                    for (int k = 0; k < answersList.size(); k++) {
                        if (k != i) {
                            Log.d(TAG, "set false 5: " + k);
                            answersList.get(k).setEnabled(false);
                        }
                    }
                }
            }
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
//        Log.d(TAG, "??????????????????: " + lastSelectedPosition);
        answersState.get(lastSelectedPosition).setData(dateFormat.format(mCalendar.getTime()));
    }

    public void showAnswers() {
        Log.d(TAG, "============== Answers ==============");
        for (int i = 0; i < answersState.size(); i++) {
            Log.d(TAG, "answer: (" + i + ") " + answersState.get(i).isChecked() + " " + answersState.get(i).getData());
        }
    }

    private void showDisableToLog(String name) {
        Log.d(TAG, "XXXXXX Disabled: " + name);
    }
}
