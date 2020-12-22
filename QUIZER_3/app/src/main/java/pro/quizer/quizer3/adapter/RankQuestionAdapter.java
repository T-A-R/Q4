package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;
import static pro.quizer.quizer3.model.OptionsOpenType.DATE;
import static pro.quizer.quizer3.model.OptionsOpenType.NUMBER;
import static pro.quizer.quizer3.model.OptionsOpenType.TEXT;
import static pro.quizer.quizer3.model.OptionsOpenType.TIME;

public class RankQuestionAdapter extends RecyclerView.Adapter<RankQuestionAdapter.ListObjectViewHolder> {

    private OnAnswerClickListener onAnswerClickListener;
    private ElementItemR question;
    private List<ElementItemR> answersList;
    private List<AnswerState> answersState;
    private int lastSelectedPosition = -1;
    public boolean isMulti;
    public boolean isRank = false;
    public List<Boolean> isPressed;
    public boolean isRestored = false;
    private MainActivity mActivity;
    private List<Integer> passedQuotaBlock;
    private ElementItemR[][] quotaTree;
    private Context mContext;
    private Map<Integer, TitleModel> titlesMap;

    public RankQuestionAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, List<Integer> passedQuotaBlock, ElementItemR[][] quotaTree, Map<Integer, TitleModel> titlesMap, OnAnswerClickListener onAnswerClickListener) {
        this.mActivity = (MainActivity) context;
        this.question = question;
        this.passedQuotaBlock = passedQuotaBlock;
        this.quotaTree = quotaTree;
        this.mContext = context;
        this.titlesMap = titlesMap;

        if (question.getSubtype().equals(ElementSubtype.RANK)) {
            isRank = true;
        }

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

        this.isMulti = Objects.requireNonNull(question.getElementOptionsR()).isPolyanswer();
        this.answersState = new ArrayList<>();
        this.isPressed = new ArrayList<>();
        for (int i = 0; i < answersList.size(); i++) {
            if (isRank)
                this.answersState.add(new AnswerState(answersList.get(i).getRelative_id(), true, ""));
            else
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_answer_rank_auto, viewGroup, false);
        else
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder_answer_rank, viewGroup, false);

        return new ListObjectViewHolder(view, onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListObjectViewHolder holder, int position) {
        holder.bind(answersList.get(position), position);

        holder.answerPosition.setVisibility(View.VISIBLE);
        holder.answerPosition.setText(String.valueOf(position + 1));
        holder.answerEditText.setText(answersState.get(position).getData());
        holder.answerEditText.setFocusableInTouchMode(false);
        holder.answerEditText.clearFocus();
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
        public ImageView editButton;
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
            answerTitle.setText(Objects.requireNonNull(titlesMap.get(item.getRelative_id())).getTitle());
            if (item.getElementOptionsR().getDescription() != null && item.getElementOptionsR().getDescription().length() > 0) {
                answerDesc.setVisibility(View.VISIBLE);
                answerDesc.setText(Objects.requireNonNull(titlesMap.get(item.getRelative_id())).getDescription());
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
                cont.setOnClickListener(this);
                editButton.setOnClickListener(this);
                answerEditText.setOnClickListener(this);
            }

            checkFilled(item, position);
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
                String data1;
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

        public void checkFilled(final ElementItemR item, int position) {
            if (answersState.get(position).getData() != null && answersState.get(position).getData().length() > 0 || lastSelectedPosition == position) {
                editButton.setVisibility(View.GONE);
                answerEditText.setVisibility(View.VISIBLE);
                answerEditText.setText(answersState.get(position).getData());
                if (lastSelectedPosition == position) {
                    switch (item.getElementOptionsR().getOpen_type()) {
                        case TEXT:
                        case NUMBER:
                            showInputDialog(answerEditText, position);
//                            answerEditText.setFocusable(false);
//                            answerEditText.setFocusableInTouchMode(false);
                            break;
                        case TIME:
                            setTime(answerEditText);
                            answersState.get(position).setData(answerEditText.getText().toString());
                            break;
                        case DATE:
                            setDate(answerEditText);
                            answersState.get(position).setData(answerEditText.getText().toString());
                            break;
                    }
                }
            } else {
                editButton.setVisibility(View.VISIBLE);
//                answerEditText.setEnabled(false);
            }
        }

        @Override
        public void onClick(View v) {

            if (v == cont) {
                cont.setFocusable(true);
                cont.requestFocus();
                return;
            }
//            if (lastSelectedPosition != getAdapterPosition()) {
                lastSelectedPosition = getAdapterPosition();
                boolean isOpen = !answersList.get(getAdapterPosition()).getElementOptionsR().getOpen_type().equals(CHECKBOX);

                if (isOpen) {
                    isPressed.set(getAdapterPosition(), true);
                    checkFilled(answersList.get(lastSelectedPosition), lastSelectedPosition);
                    String answer = null;
                    if (answerEditText.getText() != null)
                        answer = answerEditText.getText().toString();
                    onUserClickListener.onAnswerClick(lastSelectedPosition, isChecked, answer);
                }
//            }
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
                        if (k != i) {
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

    public void setRestored(boolean restored) {
        isRestored = restored;
    }

    public void clearOldPassed() {
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
            } else {
                mActivity.showToastfromActivity(mActivity.getString(R.string.empty_input_warning));
//                editButton.setVisibility(View.VISIBLE);
                pEditText.setEnabled(true);
            }
            if (mActivity != null && !mActivity.isFinishing()) {
                mActivity.hideKeyboardFrom(mEditText);
                alertDialog.dismiss();
            }
        });

        if (mActivity != null && !mActivity.isFinishing()) {
            alertDialog.show();
        }
    }
}
