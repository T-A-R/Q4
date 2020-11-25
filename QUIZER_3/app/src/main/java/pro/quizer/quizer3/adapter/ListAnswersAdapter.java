package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;
import static pro.quizer.quizer3.model.OptionsOpenType.NUMBER;

public class ListAnswersAdapter extends RecyclerView.Adapter<ListAnswersAdapter.ListObjectViewHolder> {

    private final OnAnswerClickListener onAnswerClickListener;
    private final ElementItemR question;
    private List<ElementItemR> answersList;
    private List<AnswerState> answersState;
    public boolean isMulti;
    public boolean isRestored = false;
    private final MainActivity mActivity;
    private final List<Integer> passedQuotaBlock;
    private final ElementItemR[][] quotaTree;
    private final Context mContext;
    private int counter = 1;
    private final List<String> titles;
    private final Map<Integer, TitleModel> titlesMap;

    public ListAnswersAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, List<Integer> passedQuotaBlock, ElementItemR[][] quotaTree, Map<Integer, TitleModel> titlesMap, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.question = question;
        this.passedQuotaBlock = passedQuotaBlock;
        this.quotaTree = quotaTree;
        this.mContext = context;
        this.titlesMap = titlesMap;
        this.answersList = makeRotation(question, answersList);
        this.onAnswerClickListener = onAnswerClickListener;
        this.isMulti = Objects.requireNonNull(question.getElementOptionsR()).isPolyanswer();
        this.answersState = new ArrayList<>();
        for (int i = 0; i < answersList.size(); i++) {
            this.answersState.add(new AnswerState(answersList.get(i).getRelative_id(), isAutoChecked(i), ""));
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

    public boolean isAutoChecked(int position) {
        return answersList.get(position).getElementOptionsR().isAutoChecked();
    }

    public boolean isUnChecker(int position) {
        return answersList.get(position).getElementOptionsR().isUnchecker();
    }

    public boolean isOpen(int position) {
        return !answersList.get(position).getElementOptionsR().getOpen_type().equals("checkbox");
    }

    public boolean isChecked(int position) {
        return answersState.get(position).isChecked();
    }

    private List<ElementItemR> makeRotation(ElementItemR question, List<ElementItemR> answers) {
        if (question.getElementOptionsR() != null && question.getElementOptionsR().isRotation()) {
            List<ElementItemR> shuffleList = new ArrayList<>();
            for (ElementItemR elementItemR : answers) {
                if (elementItemR.getElementOptionsR() != null && !elementItemR.getElementOptionsR().isFixed_order()) {
                    shuffleList.add(elementItemR);
                }
            }
            Collections.shuffle(shuffleList, new Random());
            int k = 0;

            for (int i = 0; i < answers.size(); i++) {
                if (answers.get(i).getElementOptionsR() != null && !answers.get(i).getElementOptionsR().isFixed_order()) {
                    answers.set(i, shuffleList.get(k));
                    k++;
                }
            }
        }
        return answers;
    }

    @NonNull
    @Override
    public ListObjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MainActivity activity = (MainActivity) mContext;
        boolean mAutoZoom = true;
        if (activity != null) {
            mAutoZoom = activity.isAutoZoom();
        }
        return new ListObjectViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(mAutoZoom ? R.layout.holder_answer_list_auto : R.layout.holder_answer_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListObjectViewHolder holder, int position) {
        holder.bind(answersList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class ListObjectViewHolder extends RecyclerView.ViewHolder {

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

        public ListObjectViewHolder(@NonNull View itemView) {
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

        }

        public void bind(final ElementItemR item, int position) {
            UiUtils.setTextOrHide(answerTitle, titles.get(position));
            if (item.getElementOptionsR().getDescription() != null && titlesMap.get(item.getRelative_id()) != null) {
                answerDesc.setVisibility(View.VISIBLE);
                UiUtils.setTextOrHide(answerDesc, Objects.requireNonNull(titlesMap.get(item.getRelative_id())).getDescription());
            } else {
                answerDesc.setVisibility(View.GONE);
            }

            showContent(item);

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
                cont.setOnClickListener(v -> onClick(answerEditText, position));
                editButton.setOnClickListener(v -> onClick(answerEditText, position));
                answerEditText.setOnClickListener(v -> onClick(answerEditText, position));
            }

            setChecked(position);

            if (!answersList.get(position).isEnabled()) {
                if (!isMulti) {
                    button.setImageResource(R.drawable.radio_button_disabled);
                } else {
                    button.setImageResource(R.drawable.checkbox_disabled);
                    answerTitle.setTextColor(mActivity.getResources().getColor(R.color.gray));
                    answerDesc.setTextColor(mActivity.getResources().getColor(R.color.gray));
                }
                if (!mActivity.isDarkkMode()) {
                    cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_gray_shadow));
                } else {
                    cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_dark_gray_shadow));
                }
            } else {
                cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_shadow));
                answerTitle.setTextColor(mActivity.getResources().getColor(R.color.black));
                answerDesc.setTextColor(mActivity.getResources().getColor(R.color.black));
            }

            answerEditText.setText(answersState.get(position).getData());

            setEnabled(position);
        }

        public void setChecked(int position) {

            if (!isMulti) {
                if (isChecked(position)) {
                    editButton.setVisibility(View.GONE);
                    button.setImageResource(R.drawable.radio_button_checked);
                    if (isOpen(position)) {
                        answerEditText.setVisibility(View.VISIBLE);
                    } else {
                        answerEditText.setVisibility(View.GONE);
                        editButton.setVisibility(View.GONE);
                    }
                } else {
                    button.setImageResource(R.drawable.radio_button_unchecked);
                    if (isOpen(position)) {
                        answerEditText.setVisibility(View.GONE);
                        editButton.setVisibility(View.VISIBLE);
                    } else {
                        answerEditText.setVisibility(View.GONE);
                        editButton.setVisibility(View.GONE);
                    }
                }
            } else {
                if (isChecked(position)) {
                    button.setImageResource(R.drawable.checkbox_checked);
                } else {
                    button.setImageResource(R.drawable.checkbox_unchecked);
                }
            }

            if (isChecked(position)) {
                if (answersState.get(position).getData() != null && !answersState.get(position).getData().equals("")) {
                    editButton.setVisibility(View.GONE);
                    answerEditText.setVisibility(View.VISIBLE);
                    answerEditText.setText(answersState.get(position).getData());
                }
            } else {
                answerEditText.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
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

        private void showContent(ElementItemR element) {
            final List<ElementContentsR> contents = mActivity.getMainDao().getElementContentsR(element.getRelative_id());

            if (contents != null && !contents.isEmpty()) {
                String data1 = contents.get(0).getData();
                String data2 = null;
                String data3 = null;

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

        public void setEnabled(int position) {
            if (!answersList.get(position).isEnabled()) {
                cont.setEnabled(false);
                editButton.setEnabled(false);
            } else {
                cont.setEnabled(true);
                editButton.setEnabled(true);
                cont.setOnClickListener(v -> onClick(answerEditText, position));
                editButton.setOnClickListener(v -> onClick(answerEditText, position));
            }

            showEnabled();
        }

        public void onClick(TextView cardInput, int position) {
            if ((isOpen(position) && !isChecked(position)) || (isOpen(position) && isAutoChecked(position))) {
                switch (answersList.get(position).getElementOptionsR().getOpen_type()) {
                    case "text":
                    case "number":
                        showInputDialog(cardInput, position);
                        break;
                    case "date":
                        setDate(cardInput, position);
                        break;
                    case "time":
                        setTime(cardInput, position);
                        break;
                }
            } else {
                checkItem(position);
            }

        }
    }

    private void checkItem(int position) {
        if (isChecked(position) && !isMulti) {
            return;
        }
        if (isAutoChecked(position)) return;
        answersState.get(position).setChecked(!isChecked(position));
        if (!isMulti || isUnChecker(position)) {
            for (int i = 0; i < answersState.size(); i++) {
                if (i != position) {
                    answersState.get(i).setChecked(false);
                }
            }
        }
        if (isMulti) {
            for (int i = 0; i < answersState.size(); i++) {
                if (i != position && isUnChecker(position)) {
                    answersState.get(i).setChecked(false);
                }
            }
        }

        if (isUnChecker(position)) {
            if (answersState.get(position).isChecked()) {
                for (int i = 0; i < answersState.size(); i++) {
                    if (i != position && !isAutoChecked(i)) {
                        answersList.get(i).setEnabled(false);
                    }
                }
            } else {
                for (int i = 0; i < answersState.size(); i++) {
                    answersList.get(i).setEnabled(true);
                }
            }
        }

        notifyDataSetChanged();
    }

    public interface OnAnswerClickListener {
        void onAnswerClick(int position, boolean enabled, String answer);
    }

    public List<AnswerState> getAnswers() {
        return answersState;
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
                        if (k != i && !isAutoChecked(k)) {
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

    public void setDate(final TextView pEditText, int position) {
        if (!mActivity.isFinishing()) {
            new DatePickerDialog(mActivity, (view, year, monthOfYear, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime(pEditText, true, position);
            },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    public void setTime(final TextView pEditText, int position) {
        if (!mActivity.isFinishing()) {
            new TimePickerDialog(mActivity, (view, hourOfDay, minute) -> {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                setInitialDateTime(pEditText, false, position);
            },
                    mCalendar.get(Calendar.HOUR_OF_DAY),
                    mCalendar.get(Calendar.MINUTE), true)
                    .show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime(final TextView mEditText, final boolean pIsDate, int position) {
        SimpleDateFormat dateFormat;

        if (pIsDate) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("HH:mm");
        }

        dateFormat.setTimeZone(mCalendar.getTimeZone());
        mEditText.setText(dateFormat.format(mCalendar.getTime()));
        answersState.get(position).setData(dateFormat.format(mCalendar.getTime()));
        onAnswerClickListener.onAnswerClick(position, isChecked(position), answersState.get(position).getData());
        checkItem(position);
    }

    public void setRestored(boolean restored) {
        isRestored = restored;
    }

    private void showInputDialog(final TextView pEditText, int position) {
        final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mActivity);
        final View mView = layoutInflaterAndroid.inflate(mActivity.isAutoZoom() ? R.layout.dialog_input_answer_auto : R.layout.dialog_input_answer, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
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
            String text = mEditText.getText().toString();
            if ((text.length() > 0) || answersList.get(position).getElementOptionsR().isUnnecessary_fill_open()) {
                answersState.get(position).setData(text);
                pEditText.setText(text);
                onAnswerClickListener.onAnswerClick(position, isChecked(position), answersState.get(position).getData());
                checkItem(position);
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

    //For Tests
    private void showEnabled() {
        Log.d("T-L.ListAnswersAdapter", "=========================================================");
        for(int i = 0; i < answersList.size(); i++) {
            Log.d("T-L.ListAnswersAdapter", "showEnabled: (" + i + ") " + answersList.get(i).isEnabled());
        }
        Log.d("T-L.ListAnswersAdapter", "=========================================================");
    }
}