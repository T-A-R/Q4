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
    private int lastCheckedElement = -103;
    public boolean isOpen = false;
    public boolean isMulti;
    public List<Boolean> isPressed;
    public boolean isRestored = false;
    private String openType;
    private MainActivity mActivity;
    private List<Integer> passedQuotaBlock;
    private ElementItemR[][] quotaTree;
    private Context mContext;
    private String textBefore;
    private String textAfter;
//    private QuotaUtils quotaUtils;

    public ListQuestionAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, List<Integer> passedQuotaBlock, ElementItemR[][] quotaTree, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.question = question;
        this.passedQuotaBlock = passedQuotaBlock;
        this.quotaTree = quotaTree;
        this.mContext = context;
//        this.quotaUtils = new QuotaUtils();

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
//        Log.d(TAG, "ListQuestionAdapter: answers size= " + this.answersList.size());

        this.onAnswerClickListener = onAnswerClickListener;
        if (question.getElementOptionsR().getOpen_type() != null) {
            this.isOpen = true;
            this.openType = question.getElementOptionsR().getOpen_type();
        }
        this.isMulti = question.getElementOptionsR().isPolyanswer();
        this.answersState = new ArrayList<>();
        this.isPressed = new ArrayList<>();
        for (int i = 0; i < answersList.size(); i++) {
            this.answersState.add(new AnswerState(answersList.get(i).getRelative_id(), false, ""));
            this.isPressed.add(false);
        }
    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        MainActivity activity = (MainActivity) mContext;
        boolean mAutoZoom = true;
        if (activity != null) {
            mAutoZoom = activity.isAutoZoom();
        }
        View view;
        if (mAutoZoom)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_answer_list_auto, viewGroup, false);
        else
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_answer_list, viewGroup, false);
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
            if (!isMulti) {
                holder.button.setImageResource(R.drawable.radio_button_disabled);
            } else {
                holder.button.setImageResource(R.drawable.checkbox_disabled);
                holder.answerTitle.setTextColor(mActivity.getResources().getColor(R.color.gray));
                holder.answerDesc.setTextColor(mActivity.getResources().getColor(R.color.gray));
            }
            if (!mActivity.isDarkkMode()) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.cont.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.bg_gray_shadow));
                } else {
                    holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_gray_shadow));
                }
            } else {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.cont.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.bg_dark_gray_shadow));
                } else {
                    holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_dark_gray_shadow));
                }
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.cont.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.bg_shadow));
            } else {
                holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_shadow));
            }
            holder.answerTitle.setTextColor(mActivity.getResources().getColor(R.color.black));
            holder.answerDesc.setTextColor(mActivity.getResources().getColor(R.color.black));
        }

        holder.myCustomEditTextListener.updatePosition(position);
//        showAnswers();
//        Log.d(TAG, "(1) !!!===!!!: " + holder.getAdapterPosition() + "/" + position + " " + answersState.get(position).getData());
        holder.answerEditText.setText(answersState.get(position).getData());
        Log.d(TAG, "======== SET TEXT 1: " + position + " / " + answersState.get(position).getData());

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

            if (!canShow(quotaTree, passedQuotaBlock, item.getRelative_id(), question.getElementOptionsR().getOrder())) {
                answerTitle.setTextColor(Color.parseColor("#AAAAAA"));
                item.setEnabled(false);
//                Log.d(TAG, "ELEMENT DISABLED: " + item.getElementOptionsR().getTitle());
            } else {
//                answerTitle.setTextColor(Color.parseColor("#000000"));
//                item.setEnabled(true);
//                Log.d(TAG, "ELEMENT ENABLED: " + item.getElementOptionsR().getTitle());
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

        public boolean canShow(ElementItemR[][] tree, List<Integer> passedElementsId, int relativeId, int order) {
//            try {
//                Log.d(TAG, "canShow: " + passedElementsId.size() + " " + relativeId + " " + order);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            if (tree == null) {
                return true;
            }

            if (order == 1) {
                for (int k = 0; k < tree[0].length; k++) {
                    if (tree[0][k].getRelative_id().equals(relativeId)) {
                        if (tree[0][k].isEnabled())
                            return true;
                    }
                }
//                Log.d(TAG, "canShow false: 1");
                return false;
            } else {
                int endPassedElement = order - 1;

                for (int k = 0; k < tree[0].length; k++) {
                    for (int i = 0; i < endPassedElement; ) {
//                        Log.d(TAG, "canShow: FOR " + tree[i][k].getRelative_id() + " " + passedElementsId.get(i));
                        if (tree[i][k].getRelative_id().equals(passedElementsId.get(i))) {
//                            Log.d(TAG, "canShow: FOUND ID");
                            if (i == (endPassedElement - 1)) { // Если последний, то
                                if (tree[i + 1][k].getRelative_id().equals(relativeId)) { // Если следующий за последним равен Relative ID
//                                    Log.d(TAG, "canShow: FOUND NEXT");
                                    if (tree[i + 1][k].isEnabled()) {
                                        return true;
                                    }
                                }
                            }
                            i++;
                        } else break;
                    }
                }
            }
//            Log.d(TAG, "canShow false: 2");
            return false;
        }

        private void showContent(ElementItemR element, View cont) {
//            final List<ElementContentsR> contents = element.getElementContentsR();
            final List<ElementContentsR> contents = mActivity.getMainDao().getElementContentsR(element.getRelative_id());

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

//            Log.d(TAG, "initViews: PICTURES: " + data + " " + filePhotooPath);
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
                answerEditText.setText(answersState.get(position).getData());
                Log.d(TAG, "======== SET TEXT 2: " + position + " / " + answersState.get(position).getData());
                if (isPressed.get(position)) {
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
                            Log.d(TAG, "======== SET TEXT 3: " + position + " / " + answersState.get(position).getData());
//                            showAnswers();
                            answerEditText.setEnabled(false);
                        } else if (item.getElementOptionsR().getOpen_type().equals(DATE)) {
                            setDate(answerEditText);
                            answersState.get(position).setData(answerEditText.getText().toString());
                            Log.d(TAG, "======== SET TEXT 4: " + position + " / " + answersState.get(position).getData());
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
//                            Log.d(TAG, "set false 4: " + i);
                            answersList.get(i).setEnabled(false);
                        }
                    }
                } else {
                    for (int i = 0; i < answersState.size(); i++) {
//                        Log.d(TAG, "set false 5: " + i);
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
            boolean emptyAnswersState = true;
            for (AnswerState answer : answersState) {
                if (answer.isChecked()) emptyAnswersState = false;

            }
            if (emptyAnswersState || isFilled() || answersState.get(getAdapterPosition()).isChecked()) {
                isPressed.set(getAdapterPosition(), true);

                Log.d(TAG, "onClick: " + getAdapterPosition() + " / " + answersList.get(getAdapterPosition()).isEnabled());

//                if (lastSelectedPosition == -1 || !answersList.get(getAdapterPosition()).isEnabled())
                int oldPosition = lastSelectedPosition;
                lastSelectedPosition = getAdapterPosition();


                if (lastSelectedPosition != -1) {
                    notifyItemChanged(lastSelectedPosition);
                    if (answersList.get(lastSelectedPosition).isEnabled()) {
                        notifyItemChanged(lastSelectedPosition);

                        if (isMulti && !answersList.get(lastSelectedPosition).getElementOptionsR().isUnchecker()) {

                            if (answersState.get(lastSelectedPosition).isChecked()) {
                                answersState.get(lastSelectedPosition).setChecked(false);
                                lastSelectedPosition = oldPosition;
                                isPressed.set(getAdapterPosition(), false);
                            } else {
                                answersState.get(lastSelectedPosition).setChecked(true);
                                lastCheckedElement = lastSelectedPosition;
                            }
                        } else if (isMulti && answersList.get(lastSelectedPosition).getElementOptionsR().isUnchecker()) {
                            notifyDataSetChanged();
                            if (!answersState.get(lastSelectedPosition).isChecked()) {
                                answersState.get(lastSelectedPosition).setChecked(true);
                                lastCheckedElement = lastSelectedPosition;
                                unselectOther(lastSelectedPosition);
                            } else {
                                answersState.get(lastSelectedPosition).setChecked(false);
                            }
                            setEnabled(lastSelectedPosition);
                        } else {
                            notifyDataSetChanged();
                            answersState.get(lastSelectedPosition).setChecked(true);
                            lastCheckedElement = lastSelectedPosition;
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
            } else {
                mActivity.showToastfromActivity(mActivity.getString(R.string.empty_string_warning));
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
        private boolean canDelete = false;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.length() == 1) canDelete = true;
            else canDelete = false;
            if (lastSelectedPosition != -1) {
//                for(int k =0; k < answersState.size();k++) {
//                    Log.d(TAG, "state: " + k + " " + answersState.get(k).getData());
//                }
//                Log.d(TAG, "beforeTextChanged: position " + lastSelectedPosition);
//                Log.d(TAG, "beforeTextChanged: textBefore " + answersState.get(lastSelectedPosition).getData());
                textBefore = answersState.get(getLastSelectedPosition()).getData();

            }
//            Log.d(TAG, "beforeTextChanged: textBefore 2" + textBefore);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            Log.d(TAG, "onTextChanged isPressed: " + isPressed + " " + charSequence.length());
            if (isOpen && lastSelectedPosition != -1 && isPressed.get(lastSelectedPosition) && charSequence != null && charSequence.length() > 0) {
                answersState.get(lastSelectedPosition).setData(charSequence.toString());
                Log.d(TAG, "======== SET TEXT 5: " + lastSelectedPosition + " / " + answersState.get(lastSelectedPosition).getData());
//                clearOldPassed();
            }
            if (charSequence == null || charSequence.length() == 0)
                if (canDelete) {
                    answersState.get(lastSelectedPosition).setData(charSequence.toString());
                    Log.d(TAG, "======== SET TEXT 6: " + lastSelectedPosition + " / " + answersState.get(lastSelectedPosition).getData());
                }
//            mDataset[position] = charSequence.toString();
//            if (lastSelectedPosition != -1) {
//                Log.d(TAG, "onTextChanged canDelete: " + canDelete);
//                Log.d(TAG, "onTextChanged charSequence: " + lastSelectedPosition + " " + charSequence.toString());
//                Log.d(TAG, "onTextChanged answersState: " + lastSelectedPosition + " " + answersState.get(lastSelectedPosition).getData());
//            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//            if (lastSelectedPosition != -1)
//            Log.d(TAG, "afterTextChanged: " + lastSelectedPosition + " " + textBefore + " / " + answersState.get(lastSelectedPosition).getData());
            if (lastSelectedPosition != -1 && !answersState.get(lastSelectedPosition).getData().equals(textBefore) && !answersList.get(lastSelectedPosition).getElementOptionsR().getOpen_type().equals("checkbox")) {
//                Log.d(TAG, "afterTextChanged: CLEAR");
                clearOldPassed();
            }
        }
    }

    public List<AnswerState> getAnswers() {
        return answersState;
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.lastSelectedPosition = lastSelectedPosition;
//        Log.d(TAG, "setLastSelectedPosition: " + lastSelectedPosition);
    }

    public int getLastSelectedPosition() {
//        Log.d(TAG, "getLastSelectedPosition: " + lastSelectedPosition);
        return lastSelectedPosition;
    }

    public int getLastCheckedElement() {
//        Log.d(TAG, "getLastSelectedPosition: " + lastSelectedPosition);
        return lastCheckedElement;
    }

    public void setAnswers(List<AnswerState> answers) {
        Log.d(TAG, "=============================");
        if (answers != null) {
//            for (int i = 0; i < answers.size(); i++) {
//                Log.d(TAG, i + ": " + answers.get(i).getRelative_id() + " : " + answers.get(i).getData() + " : " + answers.get(i).isChecked());
//            }
            this.answersState = answers;
            for (int i = 0; i < answers.size(); i++) {
                if (answersList.get(i).getElementOptionsR().isUnchecker() && answers.get(i).isChecked()) {
                    for (int k = 0; k < answersList.size(); k++) {
                        if (k != i) {
//                            Log.d(TAG, "set false 5: " + k);
                            answersList.get(k).setEnabled(false);
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "setAnswers: NULL");
        }
    }

    public void setPressed() {
        for(int i = 0; i < answersState.size(); i++) {
            isPressed.set(i, answersState.get(i).isChecked());
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

    public void setRestored(boolean restored) {
        isRestored = restored;
    }

    private void clearOldPassed() {
        if (isRestored) {
            try {
                int id = mActivity.getMainDao().getElementPassedR(mActivity.getCurrentQuestionnaire().getToken(), question.getRelative_id()).getId();
                mActivity.getMainDao().deleteOldElementsPassedR(id);
                mActivity.showToastfromActivity(mActivity.getString(R.string.data_changed));
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRestored = false;
        }
    }

    public boolean isFilled() {
//        Log.d(TAG, "isFilled: " + isOpen + " " + lastSelectedPosition);
        if (!isOpen) {
            return true;
        }
        if (!isMulti) {
            return true;
        }
        if (lastSelectedPosition == -1) {
            return true;
        }
//        Log.d(TAG, "isFilled: " + answersList.get(lastSelectedPosition).getElementOptionsR().getOpen_type() +
//                " " + answersList.get(lastSelectedPosition).getRelative_id() +
//                " " + answersState.get(lastSelectedPosition).getRelative_id());
//        if (answersList.get(lastSelectedPosition).getElementOptionsR().getOpen_type().equals("checkbox") || answersState.get(lastCheckedElement).getData().length() > 0) {
        if (answersList.get(lastSelectedPosition).getElementOptionsR().getOpen_type().equals("checkbox") || isAllHaveData()) {
            return true;
        } else return false;
    }

    private boolean isAllHaveData() {
        boolean ok = true;
        for (int i = 0; i < answersState.size(); i++) {
            if (!answersList.get(i).getElementOptionsR().getOpen_type().equals("checkbox")
                    && answersState.get(i).isChecked()
                    && (answersState.get(i).getData() == null || answersState.get(i).getData().length() == 0)) {
                ok = false;
            }
        }
        return ok;
    }
}
