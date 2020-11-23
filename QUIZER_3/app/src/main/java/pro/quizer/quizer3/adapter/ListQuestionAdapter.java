package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;
import static pro.quizer.quizer3.model.OptionsOpenType.DATE;
import static pro.quizer.quizer3.model.OptionsOpenType.NUMBER;
import static pro.quizer.quizer3.model.OptionsOpenType.TEXT;
import static pro.quizer.quizer3.model.OptionsOpenType.TIME;

import android.util.Log;

import com.squareup.picasso.Picasso;

public class ListQuestionAdapter extends RecyclerView.Adapter<ListQuestionAdapter.ListObjectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private final ElementItemR question;
    private List<ElementItemR> answersList;
    private List<AnswerState> answersState;
    private int lastSelectedPosition = -1;
    private int lastCheckedElement = -103;
    public boolean isOpen = false;
    public boolean isMulti;
    public boolean isRank = false;
    public Integer isPressed;
    public boolean isRestored = false;
    private final MainActivity mActivity;
    private final List<Integer> passedQuotaBlock;
    private final ElementItemR[][] quotaTree;
    private final Context mContext;
    private int counter = 1;
    private final List<String> titles;
    private final Map<Integer, TitleModel> titlesMap;

    public ListQuestionAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, List<Integer> passedQuotaBlock, ElementItemR[][] quotaTree, Map<Integer, TitleModel> titlesMap, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.question = question;
        this.passedQuotaBlock = passedQuotaBlock;
        this.quotaTree = quotaTree;
        this.mContext = context;
        this.titlesMap = titlesMap;

        if (question.getSubtype().equals(ElementSubtype.RANK)) isRank = true;

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

        this.onAnswerClickListener = onAnswerClickListener;
        if (question.getElementOptionsR() != null && question.getElementOptionsR().getOpen_type() != null) {
            this.isOpen = true;
        }
        this.isMulti = Objects.requireNonNull(question.getElementOptionsR()).isPolyanswer();
        this.answersState = new ArrayList<>();
        for (int i = 0; i < answersList.size(); i++) {
            this.answersState.add(new AnswerState(answersList.get(i).getRelative_id(), isAutoChecked(answersList.get(i)), ""));
        }

        titles = new ArrayList<>();

        for (ElementItemR element : answersList) {
            if (element.getElementOptionsR().isShow_in_card()) {
                String text = counter + ". " + Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle();
                titles.add(text);
                counter++;
            } else {
                titles.add(Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle());
            }
        }
    }

    public boolean isAutoChecked(ElementItemR element) {
        return element.getElementOptionsR().isAutoChecked();
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
        return new ListObjectViewHolder(view, onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListObjectViewHolder holder, int position) {
        holder.bind(answersList.get(position), position);
        if (isRank) {
            holder.button.setVisibility(View.GONE);
            holder.answerPosition.setVisibility(View.VISIBLE);
            holder.answerPosition.setText(String.valueOf(position + 1));
        }

        if (!isMulti) {
            if (position == lastSelectedPosition) {
                holder.editButton.setVisibility(View.GONE);
                holder.button.setImageResource(R.drawable.radio_button_checked);
                if (isOpen) {
                    holder.answerEditText.setVisibility(View.VISIBLE);
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
                holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_gray_shadow));
            } else {
                holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_dark_gray_shadow));
            }
        } else {
            holder.cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_shadow));
            holder.answerTitle.setTextColor(mActivity.getResources().getColor(R.color.black));
            holder.answerDesc.setTextColor(mActivity.getResources().getColor(R.color.black));
        }

        holder.answerEditText.setText(answersState.get(position).getData());
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class ListObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView answerPosition;
        TextView answerTitle;
        TextView answerDesc;
        public TextView answerEditText;
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

        OnAnswerClickListener onUserClickListener;

        public ListObjectViewHolder(@NonNull View itemView, OnAnswerClickListener onUserClickListener) {
            super(itemView);

            answerPosition = itemView.findViewById(R.id.position);
            answerTitle = itemView.findViewById(R.id.answer);
            answerDesc = itemView.findViewById(R.id.answer_desc);
            answerEditText = itemView.findViewById(R.id.edit_answer);
            button = itemView.findViewById(R.id.radio_button);
            editButton = itemView.findViewById(R.id.edit_button);
            image1 = itemView.findViewById(R.id.answer_image_1);
            image2 = itemView.findViewById(R.id.answer_image_2);
            image3 = itemView.findViewById(R.id.answer_image_3);
            openQuestionCont = itemView.findViewById(R.id.open_question);
            openAnswerCont = itemView.findViewById(R.id.open_cont);
            cont = itemView.findViewById(R.id.answer_cont);
            contentCont = itemView.findViewById(R.id.answer_images_cont);

            answerTitle.setTypeface(Fonts.getFuturaPtBook());
            answerPosition.setTypeface(Fonts.getFuturaPtBook());
            answerDesc.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

            this.onUserClickListener = onUserClickListener;
        }

        public void bind(final ElementItemR item, int position) {

            UiUtils.setTextOrHide(answerTitle, titles.get(position));
            if (item.getElementOptionsR().getDescription() != null && titlesMap.get(item.getRelative_id()) != null) {
                answerDesc.setVisibility(View.VISIBLE);
                UiUtils.setTextOrHide(answerDesc, Objects.requireNonNull(titlesMap.get(item.getRelative_id())).getDescription());
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
            }

            if (item.isEnabled()) {
                cont.setOnClickListener(this);
                editButton.setOnClickListener(this);
                answerEditText.setOnClickListener(this);
            }

            setChecked(item, position);

        }

        public boolean canShow(ElementItemR[][] tree, List<Integer> passedElementsId, int relativeId, int order) {

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
                return false;
            } else {
                int endPassedElement = order - 1;

                for (int k = 0; k < tree[0].length; k++) {
                    for (int i = 0; i < endPassedElement; ) {
                        if (tree[i][k].getRelative_id().equals(passedElementsId.get(i))) {
                            if (i == (endPassedElement - 1)) { // Если последний, то
                                if (tree[i + 1][k].getRelative_id().equals(relativeId)) { // Если следующий за последним равен Relative ID
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
            return false;
        }

        private void showContent(ElementItemR element, View cont) {
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

            if (StringUtils.isEmpty(data)) {
                return Constants.Strings.EMPTY;
            }

            final String fileName = FileUtils.getFileName(data);

            return path + FileUtils.FOLDER_DIVIDER + fileName;
        }

        public void setChecked(final ElementItemR item, int position) {

            if (answersState.get(position).isChecked()) {
                if (answersState.get(position).getData() != null && !answersState.get(position).getData().equals("")) {
                    editButton.setVisibility(View.GONE);
                    answerEditText.setVisibility(View.VISIBLE);
                    answerEditText.setText(answersState.get(position).getData());
                }
                if (isPressed != null && isPressed.equals(position)) {
                    if (position == lastSelectedPosition) {
                        if (item.getElementOptionsR().getOpen_type().equals(TEXT)) {
                            showInputDialog(answerEditText, position);
                            answerEditText.setFocusable(false);
                            answerEditText.setFocusableInTouchMode(false);
                        } else if (item.getElementOptionsR().getOpen_type().equals(NUMBER)) {
                            showInputDialog(answerEditText, position);
                            answerEditText.setFocusable(false);
                            answerEditText.setFocusableInTouchMode(false);
                        } else if (item.getElementOptionsR().getOpen_type().equals(TIME)) {
                            setTime(answerEditText);
                            answersState.get(position).setData(answerEditText.getText().toString());
                            answerEditText.setEnabled(false);
                        } else if (item.getElementOptionsR().getOpen_type().equals(DATE)) {
                            setDate(answerEditText);
                            answersState.get(position).setData(answerEditText.getText().toString());
                            answerEditText.setEnabled(false);
                        }
                        hideEditButton(position);
                    } else {
                        answerEditText.setEnabled(false);
                    }
                    isPressed = null;
                }
            } else {
                answerEditText.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        }

        private void hideEditButton(int position) {
//            if(answersState.get(position).getData() != null && !answersState.get(position).getData().equals("")) {
            editButton.setVisibility(View.GONE);
            answerEditText.setVisibility(View.VISIBLE);
            answerEditText.setText(answersState.get(position).getData());
//            }
        }

        public void setEnabled(int position) {
            if (answersList.get(position).getElementOptionsR().isUnchecker()) {
                if (answersState.get(position).isChecked()) {
                    for (int i = 0; i < answersState.size(); i++) {
                        if (i != position && !isAutoChecked(answersList.get(i))) {
                            answersList.get(i).setEnabled(false);
                        }
                    }
                } else {
                    for (int i = 0; i < answersState.size(); i++) {
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
                if (answer.isChecked()) {
                    emptyAnswersState = false;
                    break;
                }
            }
            if (emptyAnswersState || isFilled() || answersState.get(getAdapterPosition()).isChecked()) {
                isPressed = getAdapterPosition();
                int oldPosition = lastSelectedPosition;
                lastSelectedPosition = getAdapterPosition();

                if (lastSelectedPosition != -1) {
                    notifyItemChanged(lastSelectedPosition);
                    if (answersList.get(lastSelectedPosition).isEnabled()) {
                        notifyItemChanged(lastSelectedPosition);
                        if (isMulti && !answersList.get(lastSelectedPosition).getElementOptionsR().isUnchecker()) {
                            if (answersState.get(lastSelectedPosition).isChecked() && !isAutoChecked(answersList.get(lastSelectedPosition))) {
                                answersState.get(lastSelectedPosition).setChecked(false);
                                lastSelectedPosition = oldPosition;
                                isPressed = null;
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

                        isChecked = !isChecked;

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
            if (i != position && !isAutoChecked(answersList.get(position))) {
                answersState.get(i).setChecked(false);
            }
        }
    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int position, boolean enabled, String answer);
    }

    public List<AnswerState> getAnswers() {
        return answersState;
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.lastSelectedPosition = lastSelectedPosition;
    }

    public void setData(List<ElementItemR> elements) {
        this.answersList = elements;
    }

    public void setAnswers(List<AnswerState> answers) {
        if (answers != null) {
            this.answersState = answers;
            for (int i = 0; i < answers.size(); i++) {
                if (answersList.get(i).getElementOptionsR().isUnchecker() && answers.get(i).isChecked()) {
                    for (int k = 0; k < answersList.size(); k++) {
                        if (k != i && !isAutoChecked(answersList.get(k))) {
                            answersList.get(k).setEnabled(false);
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "setAnswers: NULL");
        }
    }

    private final Calendar mCalendar = Calendar.getInstance();

    public void setDate(final TextView pEditText) {
        if (!mActivity.isFinishing()) {
            new DatePickerDialog(mActivity, (view, year, monthOfYear, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime(pEditText, true);
            },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    public void setTime(final TextView pEditText) {
        if (!mActivity.isFinishing()) {
            new TimePickerDialog(mActivity, (view, hourOfDay, minute) -> {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                setInitialDateTime(pEditText, false);
            },
                    mCalendar.get(Calendar.HOUR_OF_DAY),
                    mCalendar.get(Calendar.MINUTE), true)
                    .show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime(final TextView mEditText, final boolean pIsDate) {
        SimpleDateFormat dateFormat;

        if (pIsDate) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("HH:mm");
        }

        dateFormat.setTimeZone(mCalendar.getTimeZone());
        mEditText.setText(dateFormat.format(mCalendar.getTime()));
        answersState.get(lastSelectedPosition).setData(dateFormat.format(mCalendar.getTime()));
    }

    // FOR TESTS
    public void showAnswers() {
        Log.d(TAG, "============== Answers ==============");
        for (int i = 0; i < answersState.size(); i++) {
            Log.d(TAG, "answer: (" + i + ") " + answersState.get(i).isChecked() + " / " + answersState.get(i).getData());
        }
    }

    // FOR TESTS
    private void showDisableToLog(String name) {
        Log.d(TAG, "XXXXXX Disabled: " + name);
    }

    public void setRestored(boolean restored) {
        isRestored = restored;
    }

    public boolean isFilled() {
        if (!isOpen) {
            return true;
        }
        if (!isMulti) {
            return true;
        }
        if (lastSelectedPosition == -1) {
            return true;
        }
        if (answersList.get(lastSelectedPosition).getElementOptionsR().isUnnecessary_fill_open()) {
            return true;
        }
        return answersList.get(lastSelectedPosition).getElementOptionsR().getOpen_type().equals("checkbox") || isAllHaveData();
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

    private void showInputDialog(final TextView pEditText, int position) {
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mActivity);
        final View mView = layoutInflaterAndroid.inflate(mActivity.isAutoZoom() ? R.layout.dialog_input_answer_auto : R.layout.dialog_input_answer, null);
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        dialog.setView(mView);

        final EditText mEditText = mView.findViewById(R.id.input_answer);
        final View mNextBtn = mView.findViewById(R.id.view_ok);

        if (answersList.get(position).getElementOptionsR().getOpen_type().equals(NUMBER)) {
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        String hint = answersList.get(position).getElementOptionsR().getPlaceholder();
        String answer = answersState.get(position).getData();
        if (answer != null && answer.length() > 0) {
            mEditText.setText(answersState.get(position).getData());
        } else {
            if (hint != null && hint.length() > 0) {
                mEditText.setHint(hint);
            } else {
                mEditText.setHint("Введите ответ");
            }
        }

        mEditText.setFocusable(true);
        mEditText.requestFocus();
        mActivity.showKeyboard();

        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.create();

        mNextBtn.setOnClickListener(v -> {
            if ((mEditText.getText() != null && mEditText.getText().length() > 0) || answersList.get(position).getElementOptionsR().isUnnecessary_fill_open()) {
                answersState.get(position).setChecked(true);
                answersState.get(position).setData(mEditText.getText().toString());
                pEditText.setText(mEditText.getText().toString());
                if (!mActivity.isFinishing()) {
                    mActivity.hideKeyboardFrom(mEditText);
                    alertDialog.dismiss();
                }
            } else
                mActivity.showToastfromActivity(mActivity.getString(R.string.empty_input_warning));

        });

        if (!mActivity.isFinishing()) {
            alertDialog.show();
        }
    }
}