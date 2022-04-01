package pro.quizer.quizer3.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.*;

import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraImageFormat;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import pro.quizer.quizer3.database.models.PhotoAnswersR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.PhoneFormatter;

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
    private final List<List<Integer>> passedQuotaBlock;
    private final ElementItemR[][] quotaTree;
    private final Context mContext;
    private int counter = 1;
    private final List<String> titles;
    private final Map<Integer, TitleModel> titlesMap;
    boolean mFromPenButton = false;

    CameraService[] myCameras = null;
    CameraManager mCameraManager = null;
    final int CAMERA1 = 0;
    final int CAMERA2 = 1;
    HandlerThread mBackgroundThread;
    Handler mBackgroundHandler = null;

    AlertDialog photoDialog;

    public ListAnswersAdapter(final Context context, ElementItemR question, List<ElementItemR> answersList, List<List<Integer>> passedQuotaBlock, ElementItemR[][] quotaTree, Map<Integer, TitleModel> titlesMap, OnAnswerClickListener onAnswerClickListener) {
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
            AnswerState state = new AnswerState(answersList.get(i).getRelative_id(), isAutoChecked(i), "");
            if (answersList.get(i).getElementOptionsR().isPhoto_answer() && answersList.get(i).getElementOptionsR().isPhoto_answer_required())
                state.setIsPhotoAnswer(true);
            this.answersState.add(state);
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

        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        startBackgroundThread();
    }

    public void onPause() {
        try {
            if (myCameras[CAMERA1].isOpen()) {
                myCameras[CAMERA1].closeCamera();
            }
            if (myCameras[CAMERA2].isOpen()) {
                myCameras[CAMERA2].closeCamera();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopBackgroundThread();
    }

    public void onResume() {
        startBackgroundThread();
    }

    public boolean isAutoChecked(int position) {
        return answersList.get(position).getElementOptionsR().isAutoChecked();
    }

    public boolean isUnChecker(int position) {
        return isMulti && answersList.get(position).getElementOptionsR().isUnchecker();
    }

    public boolean isOpen(int position) {
        return !answersList.get(position).getElementOptionsR().getOpen_type().equals("checkbox");
    }

    public boolean isChecked(int position) {
        return answersState.get(position).isChecked();
    }

    public boolean isHelper(int position) {
        return answersList.get(position).getElementOptionsR().isHelper();
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
        return new ListObjectViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(MainActivity.AVIA ? R.layout.holder_answer_list_avia : mAutoZoom ? R.layout.holder_answer_list_auto : R.layout.holder_answer_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListObjectViewHolder holder, int position) {
        if (!isHelper(position)) {
            holder.cont.setVisibility(View.VISIBLE);
            holder.bind(answersList.get(position), position);
        } else {
            holder.cont.setVisibility(View.GONE);
        }
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
        ImageView penButton;
        Button addPicButton;
        ImageView cancelPicButton;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        RelativeLayout openQuestionCont;
        RelativeLayout openAnswerCont;
        LinearLayout contentCont;
        LinearLayout cont;
        PhoneFormatter phoneFormatter = new PhoneFormatter();
        String mPhone = "";

        public ListObjectViewHolder(@NonNull View itemView) {
            super(itemView);

            answerPosition = itemView.findViewById(R.id.position);
            answerTitle = itemView.findViewById(R.id.answer);
            answerDesc = itemView.findViewById(R.id.answer_desc);
            answerEditText = itemView.findViewById(R.id.edit_answer);
            button = itemView.findViewById(R.id.radio_button);
            editButton = itemView.findViewById(R.id.edit_button);
            penButton = itemView.findViewById(R.id.pen_button);
            addPicButton = itemView.findViewById(R.id.btn_add_photo);
            cancelPicButton = itemView.findViewById(R.id.btn_pic_cancel);
            image1 = itemView.findViewById(R.id.answer_image_1);
            image2 = itemView.findViewById(R.id.answer_image_2);
            image3 = itemView.findViewById(R.id.answer_image_3);
            openQuestionCont = itemView.findViewById(R.id.open_question);
            openAnswerCont = itemView.findViewById(R.id.open_cont);
            cont = itemView.findViewById(R.id.answer_cont);
            contentCont = itemView.findViewById(R.id.answer_images_cont);

            answerTitle.setTypeface(Fonts.getFuturaPtBook());
            answerPosition.setTypeface(Fonts.getFuturaPtBook());
//            answerDesc.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setTypeface(Fonts.getFuturaPtBook());
            answerEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        }

        public void bind(final ElementItemR item, int position) {
            File file = new File(getAnswerImagePath(position));
            if (file.exists()) {
                answersState.get(position).setHasPhoto(true);
            }

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

            if (item.getElementOptionsR().isPhoto_answer()) {
                addPicButton.setVisibility(View.VISIBLE);
                if (answersState.get(getAdapterPosition()).hasPhoto()) {
                    addPicButton.setTextColor(mActivity.getResources().getColor(R.color.brand_color));
                    addPicButton.setText(R.string.button_view_photo);
                } else {
                    addPicButton.setTextColor(mActivity.getResources().getColor(R.color.gray));
                    addPicButton.setText(R.string.button_add_photo);
                }
            } else {
                addPicButton.setVisibility(View.GONE);
            }

            if (!canShow(quotaTree, passedQuotaBlock, item.getRelative_id(), question.getElementOptionsR().getOrder())) {
                answerTitle.setTextColor(Color.parseColor("#AAAAAA"));
                item.setEnabled(false);
            }

            if (item.isEnabled()) {
//                cont.setOnClickListener(v -> onClick(answerEditText, position, false));
                editButton.setOnClickListener(v -> onClick(answerEditText, position, false));
                answerEditText.setOnClickListener(v -> onClick(answerEditText, position, false));
                penButton.setOnClickListener(v -> onClick(answerEditText, position, true));
                addPicButton.setOnClickListener(v -> showPictureDialog(position));
                cancelPicButton.setOnClickListener(v -> clearPicture());
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
                cont.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.shadow_2));
                answerTitle.setTextColor(mActivity.getResources().getColor(R.color.black));
                answerDesc.setTextColor(mActivity.getResources().getColor(R.color.black));
            }

//            answerEditText.setText(answersState.get(position).getData());
            UiUtils.setTextOrHide(answerEditText, answersState.get(position).getData());

            setEnabled(position);
        }

        private void clearPicture() {
        }

        public void setChecked(int position) {
            if (!isMulti) {
                if (isChecked(position)) {
                    editButton.setVisibility(View.GONE);
                    button.setImageResource(MainActivity.AVIA ? R.drawable.radio_button_checked_red : R.drawable.radio_button_checked);
                    if (isOpen(position)) {
                        if (answersState.get(position).getData() != null && answersState.get(position).getData().length() > 0) {
                            answerEditText.setVisibility(View.VISIBLE);
                            penButton.setVisibility(View.VISIBLE);
                            editButton.setVisibility(View.GONE);
                        } else {
                            answerEditText.setVisibility(View.GONE);
                            penButton.setVisibility(View.GONE);
                            editButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        answerEditText.setVisibility(View.GONE);
                        editButton.setVisibility(View.GONE);
                        penButton.setVisibility(View.GONE);
                    }
                } else {
                    button.setImageResource(MainActivity.AVIA ? R.drawable.radio_button_unchecked_red : R.drawable.radio_button_unchecked);
                    if (isOpen(position)) {
                        answerEditText.setVisibility(View.GONE);
                        penButton.setVisibility(View.GONE);
                        editButton.setVisibility(View.VISIBLE);
                    } else {
                        answerEditText.setVisibility(View.GONE);
                        editButton.setVisibility(View.GONE);
                        penButton.setVisibility(View.GONE);
                    }
                }
            } else {
                if (isChecked(position)) {
                    button.setImageResource(MainActivity.AVIA ? R.drawable.checkbox_checked_red : R.drawable.checkbox_checked);
                    if (answersState.get(position).getData() != null && !answersState.get(position).getData().equals("")) editButton.setVisibility(View.GONE);
                } else {
                    button.setImageResource(MainActivity.AVIA ? R.drawable.checkbox_unchecked_red : R.drawable.checkbox_unchecked);
                }
            }

            if (isChecked(position)) {
                if (answersState.get(position).getData() != null && !answersState.get(position).getData().equals("")) {
                    editButton.setVisibility(View.GONE);
                    answerEditText.setVisibility(View.VISIBLE);
                    penButton.setVisibility(View.VISIBLE);
//                    answerEditText.setText(answersState.get(position).getData());
                    UiUtils.setTextOrHide(answerEditText, answersState.get(position).getData());
                }
            } else {
                answerEditText.setVisibility(View.GONE);
                penButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        }

        public boolean canShow(ElementItemR[][] tree, List<List<Integer>> passedElementsId, int relativeId, int order) {

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
//                        if (tree[i][k].getRelative_id().equals(passedElementsId.get(i))) {
                        if (passedElementsId.get(i).contains(tree[i][k].getRelative_id())) {
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
                cont.setOnClickListener(v -> onClick(answerEditText, position, false));
                editButton.setOnClickListener(v -> onClick(answerEditText, position, false));
            }

//            showEnabled();
        }

        public void onClick(TextView cardInput, int position, boolean isPenButton) {
            if ((isOpen(position) && !isChecked(position))
                    || (isOpen(position) && isAutoChecked(position))
                    || isOpen(position) && isPenButton
                    || (isOpen(position) && (answersState.get(position).getData() == null || answersState.get(position).getData().length() == 0))) {
                mFromPenButton = isPenButton;
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
                    case "phone_ru":
                        showPhoneDialog(cardInput, position);
                        break;
                }
//                showPhoneDialog(cardInput, position);
            } else {
                checkItem(position);
                onAnswerClickListener.onAnswerClick(position, isChecked(position), answersState.get(position).getData());
            }

        }

        @SuppressLint("ClickableViewAccessibility")
        private void showPhoneDialog(final TextView pEditText, int position) {
            final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mActivity);
            final View mView = layoutInflaterAndroid.inflate(mActivity.isAutoZoom() ? R.layout.dialog_input_phone_auto : R.layout.dialog_input_phone, null);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
            dialog.setView(mView);

            final EditText inputPhone = mView.findViewById(R.id.phone);
            final EditText labelPhone = mView.findViewById(R.id.phone_info);
            final View mNextBtn = mView.findViewById(R.id.view_ok);

//            if (answersList.get(position).getElementOptionsR().getPlaceholder() != null && !answersList.get(position).getElementOptionsR().getPlaceholder().equals(""))
//                labelPhone.setText(answersList.get(position).getElementOptionsR().getPlaceholder());

            if (answersList.get(position).getElementOptionsR().getOpen_type().equals(NUMBER)) {
                inputPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            inputPhone.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    inputPhone.setText("+7(");
                } else {
                    mActivity.hideKeyboardFrom(view);
                }
            });

            inputPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    phoneFormatter.beforeTextChanged(start, count);
                }

                @Override
                public void onTextChanged(CharSequence cs, int cursorPosition, int before, int count) {
                    if (!cs.toString().equals(phoneFormatter.getPhone())) {
                        phoneFormatter.onTextChanged(cs.toString(), cursorPosition, before, count);
                        inputPhone.setText(phoneFormatter.getPhone());
                        inputPhone.setSelection(phoneFormatter.getSelection());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            inputPhone.setOnTouchListener((v, event) -> {
                if (inputPhone.getText().length() > 2) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        inputPhone.post(new Runnable() {
                            public void run() {
                                Editable sb = inputPhone.getText();
                                int currentPos = inputPhone.getSelectionStart();

                                inputPhone.setSelection(inputPhone.getText().length());
                            }
                        });
                    }
                    return false;
                }
                return false;
            });

            String hint = answersList.get(position).getElementOptionsR().getPlaceholder();
            String answer = answersState.get(position).getData();
            if (answer != null && answer.length() > 0) {
                inputPhone.setText(answersState.get(position).getData());
            } else {
                if (hint != null && hint.length() > 0) {
                    inputPhone.setHint(hint);
                }
            }

            inputPhone.setFocusable(true);
            inputPhone.requestFocus();
            inputPhone.setSelection(phoneFormatter.getSelection());
            mActivity.showKeyboard();

            dialog.setCancelable(false);
            final AlertDialog alertDialog = dialog.create();

            inputPhone.setSelection(inputPhone.getText().length());

            mNextBtn.setOnClickListener(v -> {
                mPhone = "7" + phoneFormatter.cleaned(inputPhone.getText().toString());
                if ((phoneFormatter.getPhone().length() == 16) || (answersList.get(position).getElementOptionsR().isUnnecessary_fill_open() && phoneFormatter.getPhone().length() == 3)) {
                    if (answersList.get(position).getElementOptionsR().isUnnecessary_fill_open() && phoneFormatter.getPhone().length() == 3) {
                        answersState.get(position).setData("");
                    } else {
                        answersState.get(position).setData(phoneFormatter.getPhone());
                        pEditText.setText(inputPhone.getText().toString());
                    }
                    onAnswerClickListener.onAnswerClick(position, isChecked(position), answersState.get(position).getData());
                    checkItem(position);
                    if (!mActivity.isFinishing()) {
                        mActivity.hideKeyboardFrom(inputPhone);
                        alertDialog.dismiss();
                    }
                } else if (answersList.get(position).getElementOptionsR().isUnnecessary_fill_open()) {
                    mActivity.showToastLongFromActivity(mActivity.getString(R.string.not_full_phone_warning));
                } else {
                    mActivity.showToastLongFromActivity(mActivity.getString(R.string.empty_phone_warning));
                }
            });

            if (!mActivity.isFinishing()) {
                alertDialog.show();
            }
        }

        private void showPictureDialog(int position) {
            final LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mActivity);
            final View mView = layoutInflaterAndroid.inflate(mActivity.isAutoZoom() ? R.layout.dialog_photo_answer : R.layout.dialog_photo_answer, null);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            dialog.setView(mView);

            final Button btnPhoto = mView.findViewById(R.id.btn_photo);
            final Button btnBack = mView.findViewById(R.id.btn_back);
            final ImageView photoView = mView.findViewById(R.id.photo_image);
            final TextureView cameraCont = mView.findViewById(R.id.camera_cont);

            btnPhoto.setTypeface(Fonts.getFuturaPtBook());
            btnBack.setTypeface(Fonts.getFuturaPtBook());

            boolean hasPhoto = false;

            cameraCont.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                    UiUtils.setButtonEnabled(btnPhoto, true);
                    mCameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
                    try {
                        myCameras = new CameraService[mCameraManager.getCameraIdList().length];

                        for (String cameraID : mCameraManager.getCameraIdList()) {
                            int id = Integer.parseInt(cameraID);
                            myCameras[id] = new CameraService(mCameraManager, cameraID, surface, position);
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                    if (myCameras[CAMERA1] != null) {
                        if (!myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].openCamera();
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

                }
            });

            dialog.setCancelable(true);
            final AlertDialog alertDialog = dialog.create();
            photoDialog = alertDialog;
            UiUtils.setButtonEnabled(btnPhoto, false);

            hasPhoto = answersState.get(position).hasPhoto();
            if (hasPhoto) {
                photoView.setVisibility(View.VISIBLE);
                cameraCont.setVisibility(View.INVISIBLE);
                File image = new File(getAnswerImagePath(position));
                Picasso.with(mActivity)
                        .load(image)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(photoView);
                btnPhoto.setText("Переделать");
                UiUtils.setButtonEnabledRed(btnPhoto, true);
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(v -> alertDialog.dismiss());
            } else {
                photoView.setVisibility(View.INVISIBLE);
                cameraCont.setVisibility(View.VISIBLE);
            }

            if (!mActivity.isFinishing()) {
                alertDialog.show();
            }

//        Camera.PictureCallback mPictureCallback = (bytes, camera1) -> {
//
//            new Thread(() -> {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                UserModelR user = mActivity.getCurrentUser();
//                String token = mActivity.getToken();
//
//                //            [admin]^[project_id]^[user_login]^[token]^[answer_id].[extension]
//
//                try {
//                    File dir = new File(FileUtils.getAnswersStoragePath(mActivity) + File.separator
//                            + user.getUser_id());
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//
//                try {
//                    File dir = new File(FileUtils.getAnswersStoragePath(mActivity) + File.separator
//                            + user.getUser_id() + File.separator + token);
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//
//                String path = FileUtils.getAnswersStoragePath(mActivity) + File.separator
//                        + user.getUser_id() + File.separator
//                        + token + File.separator
//                        + user.getConfigR().getLoginAdmin()
//                        + "^" + user.getConfigR().getProjectInfo().getProjectId()
//                        + "^" + user.getLogin()
//                        + "^" + token
//                        + "^" + answersState.get(position).getRelative_id();
//
//                CameraConfig mCameraConfig = new CameraConfig()
//                        .getBuilder(mActivity)
//                        .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
//                        .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
//                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
//                        .setImageRotation(CameraRotation.ROTATION_270)
//                        .buildForReg(path);
//
//                Bitmap rotatedBitmap;
//                rotatedBitmap = flip(bitmap);
//                bitmap = null;
//
//                //Save image to the file.
//                if (HiddenCameraUtils.saveImageFromFile(rotatedBitmap,
//                        mCameraConfig.getImageFile(),
//                        mCameraConfig.getImageFormat())) {
//                    answersState.get(position).setHasPhoto(true);
//                    addPhotoName(getAnswerImagePath(position), getAnswerImageName(position));
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!mActivity.isFinishing()) {
//                                if (finalCamera != null) {
//                                    try {
//                                        finalCamera.stopPreview();
//                                        finalCamera.release();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                alertDialog.dismiss();
//                                notifyItemChanged(position);
//                            }
//
//                        }
//                    });
//                }
//
//            }).start();
//        };

            if (!hasPhoto) {
                btnPhoto.setOnClickListener(v -> {
                    if (myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].makePhoto();
                    if (myCameras[CAMERA2].isOpen()) myCameras[CAMERA2].makePhoto();
//                capturePhoto(finalCamera, mPictureCallback);
                    if (!answersState.get(position).isChecked())
                        checkItem(position);
                });
            } else {
                btnPhoto.setOnClickListener(v -> {

                    UserModelR user = mActivity.getCurrentUser();
                    String token = mActivity.getToken();
                    File file = new File(
                            FileUtils.getAnswersStoragePath(mActivity) + File.separator
                                    + mActivity.getCurrentUserId() + File.separator
                                    + token + File.separator
                                    + user.getConfigR().getLoginAdmin()
                                    + "^" + user.getConfigR().getProjectInfo().getProjectId()
                                    + "^" + user.getLogin()
                                    + "^" + token
                                    + "^" + answersState.get(position).getRelative_id() + ".jpeg"

                    );
                    deleteRecursive(file);
                    mActivity.getMainDao().clearPhotoAnswersByName(getAnswerImageName(position));
                    answersState.get(position).setHasPhoto(false);

                    try {
                        notifyItemChanged(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    alertDialog.dismiss();
                    showPictureDialog(position);
                });
            }
        }
    }

    private void checkItem(int position) {
        new Thread(() -> {
            if (isChecked(position) && !isMulti) {
                mActivity.runOnUiThread(() -> notifyItemChanged(position));
                return;
            }
            if (isMulti && mFromPenButton) {
                mFromPenButton = false;
                return;
            }
            if (isAutoChecked(position)) {
                mActivity.runOnUiThread(this::notifyDataSetChanged);
//                notifyDataSetChanged();
                return;
            }
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
            mActivity.runOnUiThread(this::notifyDataSetChanged);
        }).start();
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
                answersList.get(i).setEnabled(true);
                if (answersList.get(i).getElementOptionsR().isPhoto_answer() && answersList.get(i).getElementOptionsR().isPhoto_answer_required())
                    answers.get(i).setIsPhotoAnswer(true);
            }
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
        final boolean isNumber = (answersList.get(position).getElementOptionsR().getOpen_type() != null && answersList.get(position).getElementOptionsR().getOpen_type().equals(NUMBER));
        final Integer min = answersList.get(position).getElementOptionsR().getMin_number();
        final Integer max = answersList.get(position).getElementOptionsR().getMax_number();

        if (isNumber) {
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
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
            if (!isNumber) {
                if ((text.length() > 0) || answersList.get(position).getElementOptionsR().isUnnecessary_fill_open()) {
                    answersState.get(position).setData(text);
                    pEditText.setText(text);
                    onAnswerClickListener.onAnswerClick(position, isChecked(position), answersState.get(position).getData());
                    checkItem(position);
                    if (!mActivity.isFinishing()) {
                        mActivity.hideKeyboardFrom(mEditText);
                        alertDialog.dismiss();
                    }
                } else {
                    mActivity.showToastfromActivity(mActivity.getString(R.string.empty_input_warning));
                }
            } else {
                if (answersList.get(position).getElementOptionsR().isUnnecessary_fill_open() && text.equals("")) {
                    answersState.get(position).setData(text);
                    pEditText.setText(text);
                    onAnswerClickListener.onAnswerClick(position, isChecked(position), answersState.get(position).getData());
                    checkItem(position);
                    if (!mActivity.isFinishing()) {
                        mActivity.hideKeyboardFrom(mEditText);
                        alertDialog.dismiss();
                    }
                } else {
                    if (text.equals("")) mActivity.showToastfromActivity(mActivity.getString(R.string.empty_input_warning));
                    else if (checkNumber(text, min, max, position, mEditText)) {
                        answersState.get(position).setData(text);
                        pEditText.setText(text);
                        onAnswerClickListener.onAnswerClick(position, isChecked(position), answersState.get(position).getData());
                        checkItem(position);
                        if (!mActivity.isFinishing()) {
                            mActivity.hideKeyboardFrom(mEditText);
                            alertDialog.dismiss();
                        }
                    }
                }
            }
        });

        if (!mActivity.isFinishing()) {
            alertDialog.show();
        }
    }

    private boolean checkNumber(String text, Integer min, Integer max, int position, EditText mInput) {
        Integer number = null;
        try {
            number = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return false;
        }

        if (min != null && number < min) {
            mActivity.showToastfromActivity(String.format(mActivity.getString(R.string.limits_warning_start), String.valueOf (position + 1)) + " " +
                            String.format(mActivity.getString(R.string.limits_warning_min), String.valueOf (min)));
            mInput.setText(String.valueOf(min));
            try {
                mInput.setSelection(mInput.getText().length());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        if (max != null && number > max) {
            mActivity.showToastfromActivity(String.format(mActivity.getString(R.string.limits_warning_start), String.valueOf (position + 1)) + " " +
                    String.format(mActivity.getString(R.string.limits_warning_max), String.valueOf (max)));
            mInput.setText(String.valueOf(max));
            try {
                mInput.setSelection(mInput.getText().length());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static Bitmap flip(Bitmap src) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(src, src.getWidth(), src.getHeight(), true);
        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    private void capturePhoto(Camera camera, Camera.PictureCallback mPictureCallback) {
        if (camera != null) {
            boolean isSafeToTakePic = false;
            try {
                camera.startPreview();
                isSafeToTakePic = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isSafeToTakePic) {
                try {
                    camera.startFaceDetection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    camera.takePicture(null, null, mPictureCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getAnswerImagePath(int position) {
        UserModelR user = mActivity.getCurrentUser();
        String token = mActivity.getToken();
        return FileUtils.getAnswersStoragePath(mActivity) + File.separator
                + mActivity.getCurrentUserId() + File.separator
                + token + File.separator
                + user.getConfigR().getLoginAdmin()
                + "^" + user.getConfigR().getProjectInfo().getProjectId()
                + "^" + user.getLogin()
                + "^" + token
                + "^" + answersState.get(position).getRelative_id() + ".jpeg";
    }

    private String getAnswerImageName(int position) {
        UserModelR user = mActivity.getCurrentUser();
        String token = mActivity.getToken();
        return user.getConfigR().getLoginAdmin()
                + "^" + user.getConfigR().getProjectInfo().getProjectId()
                + "^" + user.getLogin()
                + "^" + token
                + "^" + answersState.get(position).getRelative_id() + ".jpeg";
    }

    private void addPhotoName(String path, String name) {
        mActivity.getMainDao().insertPhotoAnswerR(new PhotoAnswersR(mActivity.getToken(), path, name, Constants.SmsStatus.NOT_SENT));
    }

    public class CameraService {

        private File mFile;
        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;
        private ImageReader mImageReader;
        private SurfaceTexture texture;
        int position;

        public CameraService(CameraManager cameraManager, String cameraID, SurfaceTexture texture, int position) {

            mCameraManager = cameraManager;
            mCameraID = cameraID;
            this.texture = texture;
            this.position = position;
            mFile = new File(getAnswerImagePath(position));
        }

        public void makePhoto() {

            try {
                // This is the CaptureRequest.Builder that we use to take a picture.
                final CaptureRequest.Builder captureBuilder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(mImageReader.getSurface());
                CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                   @NonNull CaptureRequest request,
                                                   @NonNull TotalCaptureResult result) {

                        Log.d("T-L.ListAnswersAdapter", "onCaptureCompleted: ");
                    }
                };

                mCaptureSession.stopRepeating();
                mCaptureSession.abortCaptures();
                mCaptureSession.capture(captureBuilder.build(), CaptureCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile, position));
            }
        };

        private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice camera) {
                mCameraDevice = camera;
                createCameraPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                mCameraDevice.close();

                mCameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                Log.d("T-L.ListAnswersAdapter", "error! camera id:" + camera.getId() + " error:" + error);
            }
        };

        private void createCameraPreviewSession() {

            mImageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 1);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
            texture.setDefaultBufferSize(1920, 1080);
            Surface surface = new Surface(texture);

            try {
                final CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(surface);

                mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {

                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                try {
                                    mCaptureSession.setRepeatingRequest(builder.build(), null, mBackgroundHandler);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                            }
                        }, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        public boolean isOpen() {
            if (mCameraDevice == null) {
                return false;
            } else {
                return true;
            }
        }

        @SuppressLint("MissingPermission")
        public void openCamera() {
            try {
                mCameraManager.openCamera(mCameraID, mCameraCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        public void closeCamera() {

            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
    }

    private class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;
        private final int position;

        ImageSaver(Image image, File file, int position) {
            mImage = image;
            mFile = file;
            this.position = position;
        }

        @Override
        public void run() {
//            [admin]^[project_id]^[user_login]^[token]^[answer_id].[extension]

            UserModelR user = mActivity.getCurrentUser();
            String token = mActivity.getToken();

            try {
                File dir = new File(FileUtils.getAnswersStoragePath(mActivity) + File.separator
                        + user.getUser_id());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                File dir = new File(FileUtils.getAnswersStoragePath(mActivity) + File.separator
                        + user.getUser_id() + File.separator + token);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                Bitmap myBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                HiddenCameraUtils.saveImageFromFile(flip(myBitmap), mFile, CameraImageFormat.FORMAT_JPEG);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                try {
                    answersState.get(position).setHasPhoto(true);
                    addPhotoName(getAnswerImagePath(position), getAnswerImageName(position));
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            photoDialog.dismiss();
                            notifyItemChanged(position);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}