package pro.quizer.quizer3.view.fragment;

import static pro.quizer.quizer3.MainActivity.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.CardAdapter;
import pro.quizer.quizer3.adapter.ListAnswersAdapter;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.model.CardItem;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.ui.AnswerItem;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.objectbox.models.ElementPassedOB;
import pro.quizer.quizer3.objectbox.models.PrevElementsO;
import pro.quizer.quizer3.utils.ConditionUtils;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class ExitElementFragment extends ScreenFragment implements View.OnClickListener, ListAnswersAdapter.OnAnswerClickListener {

    private Button btnNext;
    private Button btnPrev;
    private Button btnExit;
    private ProgressBar progressBar;
    private ImageView btnHideTitle;
    private RelativeLayout titleCont;
    private RelativeLayout questionTitleBox;
    private LinearLayout titleBox;
    private LinearLayout titleCont1;
    private LinearLayout titleCont2;
    private LinearLayout titleImagesCont1;
    private LinearLayout titleImagesCont2;
    private LinearLayout questionCont;
    private LinearLayout questionImagesCont;
    private TextView tvHiddenTitle;
    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvQuestion;
    private TextView tvHiddenQuestion;
    private TextView tvTitleDesc1;
    private TextView tvTitleDesc2;
    private TextView tvQuestionDesc;
    private RecyclerView rvAnswers;
    private AdaptiveTableLayout tableLayout;
    private ImageView title1Image1;
    private ImageView title1Image2;
    private ImageView title1Image3;
    private ImageView title2Image1;
    private ImageView title2Image2;
    private ImageView title2Image3;
    private ImageView questionImage1;
    private ImageView questionImage2;
    private ImageView questionImage3;
    private NestedScrollView questionBox;
    private ImageView closeQuestion;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog infoDialog;
    private Toolbar toolbar;

    private int titles = 0;
    private boolean hasQuestionImage = false;
    private ElementItemR currentElement = null;
    List<ElementItemR> answersList;
    private Long startTime;
    private Integer startElementId;
    private Integer nextElementId;
    private String answerType;
    private boolean isQuota = false;
    private boolean canBack = true;

    private ListAnswersAdapter adapterList;
    private List<PrevElementsO> prevList = null;
    private Map<Integer, TitleModel> titlesMap;
    private CompositeDisposable disposables;
    private boolean isInHiddenQuotaDialog = false;
    private boolean isFragmentLoading = false;
    private boolean isSearch = false;

    private List<ElementItemR> quotaElementsList = new ArrayList<>();

    public ExitElementFragment() {
        super(R.layout.fragment_element);
    }

    public ExitElementFragment setStartElement(Integer startElementId, boolean restored) {
        this.startElementId = startElementId;
        return this;
    }

    @Override
    protected void onReady() {
        disposables = new CompositeDisposable();
        setRetainInstance(true);

        initViews();
        initElements();
        initQuestion();
        getQuestionnaire().setIn_uik_question(true);
        getDao().setCurrentQuestionnaireInUikQuestion(true);
        if (currentElement != null) {
            Log.d("T-A-R", "onReady: " + currentElement.getRelative_id());
            initCurrentElement();
        } else {
            exitQuestionnaire();
        }

    }

    private void initViews() {
        toolbar = new Toolbar(getMainActivity());
        toolbar = findViewById(R.id.toolbar);
        RelativeLayout cont = findViewById(R.id.cont_element_fragment);
        titleCont = findViewById(R.id.title_cont);
        titleBox = findViewById(R.id.title_box);
        titleCont1 = findViewById(R.id.title_cont_1);
        tvHiddenTitle = findViewById(R.id.hidden_title);
        titleCont2 = findViewById(R.id.title_cont_2);
        titleImagesCont1 = findViewById(R.id.title_images_cont_1);
        titleImagesCont2 = findViewById(R.id.title_images_cont_2);
        questionCont = findViewById(R.id.question_cont);
        questionBox = findViewById(R.id.question_box);
        questionImagesCont = findViewById(R.id.question_images_cont);
        questionTitleBox = findViewById(R.id.question_title_box);
        rvAnswers = findViewById(R.id.answers_recyclerview);
        tableLayout = findViewById(R.id.table_question_layout);
        tvTitle1 = findViewById(R.id.title_1);
        tvTitle2 = findViewById(R.id.title_2);
        tvTitleDesc1 = findViewById(R.id.title_desc_1);
        tvTitleDesc2 = findViewById(R.id.title_desc_2);
        tvQuestion = findViewById(R.id.question);
        tvHiddenQuestion = findViewById(R.id.hidden_question_title);
        tvQuestionDesc = findViewById(R.id.question_desc);
        title1Image1 = findViewById(R.id.title_1_image_1);
        title1Image2 = findViewById(R.id.title_1_image_2);
        title1Image3 = findViewById(R.id.title_1_image_3);
        title2Image1 = findViewById(R.id.title_2_image_1);
        title2Image2 = findViewById(R.id.title_2_image_2);
        title2Image3 = findViewById(R.id.title_2_image_3);
        questionImage1 = findViewById(R.id.question_image_1);
        questionImage2 = findViewById(R.id.question_image_2);
        questionImage3 = findViewById(R.id.question_image_3);
        closeQuestion = findViewById(R.id.question_close);
        btnHideTitle = findViewById(R.id.hide_btn);
        btnNext = findViewById(R.id.next_btn);
        btnPrev = findViewById(R.id.back_btn);
        btnExit = findViewById(R.id.exit_btn);
        progressBar = findViewById(R.id.progressBar);

        btnNext.setTransformationMethod(null);
        btnPrev.setTransformationMethod(null);
        btnExit.setTransformationMethod(null);

        progressBar.setVisibility(View.GONE);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        closeQuestion.setOnClickListener(this);
        cont.setOnClickListener(this);
        tvQuestion.setOnClickListener(this);
        btnHideTitle.setOnClickListener(this);
        tvQuestionDesc.setOnClickListener(this);
        tvHiddenQuestion.setOnClickListener(this);
        tableLayout.setOnClickListener(this);
        tvTitle1.setOnClickListener(this);
        tvTitleDesc1.setOnClickListener(this);
        tvTitle2.setOnClickListener(this);
        tvTitleDesc2.setOnClickListener(this);
        tvHiddenTitle.setOnClickListener(this);
        titleBox.setOnClickListener(this);

        deactivateButtons();

        toolbar.setTitle(getString(R.string.app_name));
        toolbar.showInfoView();
        MainFragment.enableSideMenu(false, getMainActivity().isExit());

        prevList = new ArrayList<>();
        btnPrev.setVisibility(View.INVISIBLE);


        if (prevList.size() == 0 || prevList.size() == 1) {
            btnPrev.setVisibility(View.INVISIBLE);
            cont.startAnimation(Anim.getAppear(getContext()));
            btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));
        } else {
            cont.startAnimation(Anim.getAppear(getContext()));
            btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnPrev.startAnimation(Anim.getAppearSlide(getContext(), 500));
        }
        btnExit.startAnimation(Anim.getAppearSlide(getContext(), 500));
        st("initViews() ---");
    }

    private void initCurrentElement() {
        setQuestionType();
        if (checkConditions(currentElement)) {
            startRecording();

            initTitles();
            if (currentElement.getElementOptionsR().isWith_card()) {
                toolbar.showCardView(v -> showCardDialog());
            }
            updateCurrentQuestionnaire();
            setAnswersList();

            titlesMap = new HashMap<>();
            for (ElementItemR element : answersList) {
                titlesMap.put(element.getRelative_id(), new TitleModel(element.getElementOptionsR().getTitle(), element.getElementOptionsR().getDescription()));
            }

            Disposable subscribeTitles = getMainActivity().getConvertedTitles(titlesMap).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> disposables.add(disposable))
                    .subscribe(
                            (Map<Integer, TitleModel> convertedTitles) -> {
                                titlesMap = convertedTitles;

                                initRecyclerView();
                                hideScreensaver();
                                activateButtons();
                            },
                            Throwable::printStackTrace,
                            () -> {
                            });

        } else {
            checkAndLoadNext();
        }

        try {
            getMainActivity().activateExitReminder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        st("initCurrentElement() ---");
    }

    @Override
    public void onClick(View view) {
        if (view == btnNext) {
            DoNext next = new DoNext();
            next.execute();
        } else if (view == btnExit) {
            deactivateButtons();
            MainActivity activity = getMainActivity();
            if (activity != null) {
                showExitPoolAlertDialog();
            }
        } else if (view == titleBox || view == btnHideTitle || view == tvHiddenTitle || view == tvTitle1 || view == tvTitleDesc1 || view == tvTitle2 || view == tvTitleDesc2) {
            if (titleBox.getVisibility() == View.VISIBLE) {
                titleBox.setVisibility(View.GONE);
                UiUtils.setTextOrHide(tvHiddenTitle, tvTitle1.getText().length() > 0 ? tvTitle1.getText().toString() : tvTitle2.getText().toString());
                tvHiddenTitle.setVisibility(View.VISIBLE);
                btnHideTitle.setImageResource(R.drawable.plus_green);
            } else {
                titleBox.setVisibility(View.VISIBLE);
                tvHiddenTitle.setVisibility(View.GONE);
                btnHideTitle.setImageResource(R.drawable.minus_green);
            }
        } else if (view == closeQuestion || view == tvQuestionDesc || view == tvQuestion || view == tvHiddenQuestion) {
            if (questionBox.getVisibility() == View.VISIBLE) {
                questionBox.setVisibility(View.GONE);
                UiUtils.setTextOrHide(tvHiddenQuestion, tvQuestion.getText().toString());
                tvHiddenQuestion.setVisibility(View.VISIBLE);
                closeQuestion.setImageResource(R.drawable.plus_white);
                tableRedrawEverything();
            } else {
                questionBox.setVisibility(View.VISIBLE);
                tvHiddenQuestion.setVisibility(View.GONE);
                closeQuestion.setImageResource(R.drawable.minus_white);
            }
        }
    }

    private void initQuestion() {
        startTime = DateUtils.getCurrentTimeMillis();

        if (getQuestionnaire() == null) {
            initElements();
        }

        if (startElementId == null) startElementId = 0;

        if (getCurrentElements() != null && getCurrentElements().size() > 0) {
            if (startElementId != 0) {
                currentElement = getElement(startElementId);
            } else {
                currentElement = getCurrentElements().get(0);
            }
            boolean found = false;
            for (int i = 0; i < getCurrentElements().size(); i++) {
                ElementItemR curEl = getCurrentElements().get(i);
                List<ElementItemR> curElQuestions = curEl.getElements();
                if (curEl.getRelative_id().equals(currentElement.getRelative_id())) {
                    found = true;
                }
                if (found) {
                    boolean isBox = curEl.getType().equals(ElementType.BOX);

                    if (!isBox) {
                        startElementId = curEl.getRelative_id();
                        currentElement = curEl;
                        break;
                    }
                }
            }
        } else {
            Log.d(TAG, "initQuestions: ERROR! (empty list)");
        }
    }

    private void setQuestionType() {
        answerType = ElementSubtype.LIST;
    }

    private void initTitles() {
        st("initTitles() +++");
        Disposable subscribeTitle;
        Disposable subscribeDecs;
        if (currentElement.getElementOptionsR().getTitle() != null)
            subscribeTitle = getMainActivity().getConvertedTitle(currentElement.getElementOptionsR().getTitle()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> disposables.add(disposable))
                    .subscribe(
                            (String convertedText) -> UiUtils.setTextOrHide(tvQuestion, convertedText),
                            Throwable::printStackTrace,
                            () -> {
                            });

        if (currentElement.getElementOptionsR().getDescription() != null) {
            tvQuestionDesc.setVisibility(View.VISIBLE);
            subscribeDecs = getMainActivity().getConvertedTitle(currentElement.getElementOptionsR().getDescription()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> disposables.add(disposable))
                    .subscribe(
                            (String convertedText) -> UiUtils.setTextOrHide(tvQuestionDesc, convertedText),
                            Throwable::printStackTrace,
                            () -> {
                            });
        }
        if (currentElement.getRelative_parent_id() != null) {
            ElementItemR parentElement = null;
            try {
                parentElement = getElement(currentElement.getRelative_parent_id());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (parentElement != null) {
                if (parentElement.getType().equals(ElementType.BOX)
                        && parentElement.getElementOptionsR() != null
                        && parentElement.getElementOptionsR().getTitle() != null
                        && parentElement.getElementOptionsR().getTitle().length() > 0
                        && (!parentElement.isWas_shown() || parentElement.getShown_at_id().equals(-102) || parentElement.getShown_at_id().equals(currentElement.getRelative_id()))) {
                    titleCont.setVisibility(View.VISIBLE);
                    titles = 1;
                    getDao().setWasElementShown(true, parentElement.getRelative_id(), parentElement.getUserId(), parentElement.getProjectId());
                    getDao().setShownId(currentElement.getRelative_id(), parentElement.getRelative_id(), parentElement.getUserId(), parentElement.getProjectId());
                    titleCont2.setVisibility(View.VISIBLE);

                    Disposable subscribeTitle2 = getMainActivity().getConvertedTitle(parentElement.getElementOptionsR().getTitle()).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> disposables.add(disposable))
                            .subscribe(
                                    (String convertedText) -> UiUtils.setTextOrHide(tvTitle2, convertedText),
                                    Throwable::printStackTrace,
                                    () -> {
                                    });

                    if (parentElement.getElementOptionsR().getDescription() != null) {
                        Disposable subscribeTitleDesc2 = getMainActivity().getConvertedTitle(parentElement.getElementOptionsR().getDescription()).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(disposable -> disposables.add(disposable))
                                .subscribe(
                                        (String convertedText) -> UiUtils.setTextOrHide(tvTitleDesc2, convertedText),
                                        Throwable::printStackTrace,
                                        () -> {
                                        });
                        tvTitleDesc2.setVisibility(View.VISIBLE);
                    }

                    showContent(parentElement, titleImagesCont2);

                    if (parentElement.getRelative_parent_id() != null) {
                        ElementItemR parentElement2 = null;
                        try {
                            parentElement2 = getElement(parentElement.getRelative_parent_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (parentElement2 != null) {
                            if (parentElement2.getType().equals(ElementType.BOX)
                                    && parentElement2.getElementOptionsR() != null
                                    && parentElement2.getElementOptionsR().getTitle() != null
                                    && parentElement2.getElementOptionsR().getTitle().length() > 0
                                    && (!parentElement2.isWas_shown() || parentElement2.getShown_at_id().equals(-102) || parentElement2.getShown_at_id().equals(currentElement.getRelative_id()))) {
                                titles = 2;
                                getDao().setWasElementShown(true, parentElement2.getRelative_id(), parentElement2.getUserId(), parentElement2.getProjectId());
                                getDao().setShownId(currentElement.getRelative_id(), parentElement2.getRelative_id(), parentElement2.getUserId(), parentElement2.getProjectId());
                                titleCont1.setVisibility(View.VISIBLE);

                                Disposable subscribeTitle1 = getMainActivity().getConvertedTitle(parentElement2.getElementOptionsR().getTitle()).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnSubscribe(disposable -> disposables.add(disposable))
                                        .subscribe(
                                                (String convertedText) -> UiUtils.setTextOrHide(tvTitle1, convertedText),
                                                Throwable::printStackTrace,
                                                () -> {
                                                });

                                if (parentElement2.getElementOptionsR().getDescription() != null) {
                                    Disposable subscribeTitleDesc1 = getMainActivity().getConvertedTitle(parentElement2.getElementOptionsR().getDescription()).subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnSubscribe(disposable -> disposables.add(disposable))
                                            .subscribe(
                                                    (String convertedText) -> UiUtils.setTextOrHide(tvTitleDesc1, convertedText),
                                                    Throwable::printStackTrace,
                                                    () -> {
                                                    });
                                    tvTitleDesc1.setVisibility(View.VISIBLE);
                                }
                                showContent(parentElement2, titleImagesCont1);
                            }
                        }
                    }
                } else titleCont.setVisibility(View.GONE);
            }
        }

        showContent(currentElement, questionImagesCont);

        if (getMainActivity().getConfig().isPhotoQuestionnaire() && currentElement.getElementOptionsR().isTake_photo()) {
            shotPicture(getLoginAdmin(), getQuestionnaire().getToken(), currentElement.getRelative_id(), getCurrentUserId(), getQuestionnaire().getProject_id(), getCurrentUser().getLogin());
        }
        st("initTitles() ---");
    }

    private void showContent(ElementItemR element, View cont) {
        st("showContent() +++");
        final List<ElementContentsR> contents = getDao().getElementContentsR(element.getRelative_id());

        if (contents != null && !contents.isEmpty()) {
            String data1 = contents.get(0).getData_small();
            String data2 = null;
            String data3 = null;
            if (contents.size() > 1)
                data2 = contents.get(1).getData_small();
            if (contents.size() > 2)
                data3 = contents.get(2).getData_small();

            hasQuestionImage = true;
            if (cont.equals(questionImagesCont)) {
                if (data1 != null) showPic(questionImagesCont, questionImage1, data1);
                if (data2 != null) showPic(questionImagesCont, questionImage2, data2);
                if (data3 != null) showPic(questionImagesCont, questionImage3, data3);
            } else if (cont.equals(titleImagesCont1)) {
                if (data1 != null) showPic(titleImagesCont1, title1Image1, data1);
                if (data2 != null) showPic(titleImagesCont1, title1Image2, data2);
                if (data3 != null) showPic(titleImagesCont1, title1Image3, data3);
            } else if (cont.equals(titleImagesCont2)) {
                if (data1 != null) showPic(titleImagesCont2, title2Image1, data1);
                if (data2 != null) showPic(titleImagesCont2, title2Image2, data2);
                if (data3 != null) showPic(titleImagesCont2, title2Image3, data3);
            }
        }
        st("showContent() ---");
    }

    private void showPic(View cont, ImageView view, String data) {
        st("showPic() +++");
        final String filePhotoPath = getFilePath(data);
        if (StringUtils.isEmpty(filePhotoPath)) {
            return;
        }
        cont.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        try {
            Picasso.with(getActivity()).load(new File(filePhotoPath)).into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        st("showPic() ---");
    }

    private void setAnswersList() {
        st("setAnswersList() +++");
        answersList = new ArrayList<>();

        answersList = currentElement.getElements();
        List<ElementItemR> checkedAnswersList = new ArrayList<>();
        for (int a = 0; a < answersList.size(); a++) {
            boolean check = checkConditions(answersList.get(a));
            if (check) {
                checkedAnswersList.add(answersList.get(a));
            }
        }

        if (checkedAnswersList.size() == 0) {
            try {
                nextElementId = answersList.get(0).getElementOptionsR().getJump();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (nextElementId == null) {
                nextElementId = currentElement.getElementOptionsR().getJump();
            }
        } else {
            answersList = checkedAnswersList;
        }
        st("setAnswersList() ---");
    }

    private void initRecyclerView() {
        st("initRecyclerView() +++");
        List<String> itemsList = new ArrayList<>();
        rvAnswers.setVisibility(View.VISIBLE);

        for (ElementItemR element : answersList) {
            itemsList.add(Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle());
        }

        MainActivity activity = getMainActivity();
        adapterList = new ListAnswersAdapter(activity, currentElement, answersList,
                null, null, titlesMap, this);


        rvAnswers.setItemAnimator(null);
        rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAnswers.setAdapter(adapterList);

        st("initRecyclerView() ---");
    }

    private void updateCurrentQuestionnaire() {
        st("updateCurrentQuestionnaire() +++");
        try {
            getDao().setCurrentElement(startElementId);
            getDao().setQuestionTime(DateUtils.getCurrentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        st("updateCurrentQuestionnaire() ---");
    }

    private boolean checkConditions(ElementItemR element) {
        st("checkConditions() +++");
        if (element != null) {
            final ElementOptionsR options = element.getElementOptionsR();
            if (options != null && options.getPre_condition() != null) {
                String conditions = options.getPre_condition();
                Log.d("T-A-R.ElementFragment", "checkConditions: " + conditions);
                final int showValue = ConditionUtils.evaluateCondition(conditions, getMainActivity().getMap(false), getMainActivity());
                if (showValue != ConditionUtils.CAN_SHOW) {
                    if (showValue != ConditionUtils.CANT_SHOW) {
                        nextElementId = showValue;
                    } else {
                        if (!element.equals(currentElement)) {
                            st("checkConditions().1 ---");
                            return false;
                        }
                        nextElementId = options.getJump();
                        if (nextElementId == null) {
                            List<ElementItemR> answers = element.getElements();
                            if (answers != null && answers.size() > 0) {
                                try {
                                    nextElementId = answers.get(0).getElementOptionsR().getJump();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (nextElementId == null) {
                                nextElementId = 0;
                            }
                        }
                    }
                    if (nextElementId.equals(0)) {
                        if (saveQuestionnaire(false)) {
                            exitQuestionnaire();
                        } else {
                            activateButtons();
                        }
                        st("checkConditions().2 ---");
                        return false;
                    } else if (nextElementId.equals(-1)) {
                        exitQuestionnaire();
                        st("checkConditions().3 ---");
                        return false;
                    } else {
                        st("checkConditions().4 ---");
                        return false;
                    }
                }
            }
            st("checkConditions().5 ---");
            return true;
        } else {
            st("checkConditions().6 ---");
            return false;
        }
    }

    private boolean saveElement() {
        boolean saved = false;
        List<ElementPassedOB> elementsForSave = new ArrayList<>();
        List<AnswerState> answerStates;

        answerStates = adapterList.getAnswers();


        if (answerStates != null && notEmpty(answerStates) && answersHavePhoto(answerStates)
                || (currentElement.getElementOptionsR().getOptional_question() != null && currentElement.getElementOptionsR().getOptional_question())) {

            if (answerStates == null || answerStates.isEmpty()) {
                Log.d("T-A-R.ElementFragment", "saveElement: 12");
                nextElementId = currentElement.getElementOptionsR().getJump();
            } else {
                boolean found = false;
                ElementItemR answer = null;
                Log.d("T-A-R.ElementFragment", "saveElement: 15");
                for (int i = 0; i < answerStates.size(); i++) {
                    if (answerStates.get(i).isChecked()) {
                        found = true;
                        answer = getElement(answerStates.get(i).getRelative_id());
                        int id = answerStates.get(i).getRelative_id();
                        nextElementId = getElement(id).getElementOptionsR().getJump();
                        break;
                    }
                }
                if (!found) {
                    nextElementId = currentElement.getElementOptionsR().getJump();
                } else {
                    isSearch = false;
                    if (answer.getElementOptionsR().getIs_cancel_survey() != null && answer.getElementOptionsR().getIs_cancel_survey()) {
                        nextElementId = -1;
                        return true;
                    }

                    if (answer.getElementOptionsR().getIs_use_absentee() != null && answer.getElementOptionsR().getIs_use_absentee()) {
                        isSearch = true;
                    }
                }
            }


            ElementPassedOB elementPassedR = new ElementPassedOB();
            elementPassedR.setRelative_id(currentElement.getRelative_id());
            elementPassedR.setProject_id(currentElement.getProjectId());
            elementPassedR.setToken(getQuestionnaire().getToken());
            elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
            elementPassedR.setFrom_quotas_block(false);
            elementPassedR.setIs_question(true);
            elementPassedR.setHelper(answerStates.size() == 1 && getElement(answerStates.get(0).getRelative_id()).getElementOptionsR().isHelper());

            elementsForSave.add(elementPassedR);
            setCondComp(currentElement.getRelative_id());

            getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
            saved = true;

            int checkedAnswers = 0;
            if (answerStates != null && !answerStates.isEmpty()) {
                Log.d("T-A-R.ElementFragment", "saveElement: 16");
                for (int i = 0; i < answerStates.size(); i++) {
                    if (answerStates.get(i).isChecked()) {
                        checkedAnswers++;
                        ElementPassedOB answerPassedR = new ElementPassedOB();
                        answerPassedR.setRelative_id(answerStates.get(i).getRelative_id());
                        answerPassedR.setParent_id(getElement(answerStates.get(i).getRelative_id()).getRelative_parent_id());
                        answerPassedR.setProject_id(currentElement.getProjectId());
                        answerPassedR.setToken(getQuestionnaire().getToken());
                        answerPassedR.setValue(answerStates.get(i).getData());
                        answerPassedR.setRank(i + 1);
                        answerPassedR.setFrom_quotas_block(isQuota);
                        answerPassedR.setIs_question(false);
                        answerPassedR.setChecked_in_card(answerStates.get(i).isCheckedInCard());
                        try {
                            answerPassedR.setHelper(getElement(answerStates.get(i).getRelative_id()).getElementOptionsR().isHelper());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            elementsForSave.add(answerPassedR);
                            saved = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            saved = false;
                            st("saveElement().1 ---");
                            Log.d("T-A-R.ElementFragment", "saveElement (1): " + saved);
                            return saved;
                        }
                    }
                }
            } else {
                Log.d("T-A-R.ElementFragment", "saveElement (2): true");
                return true;
            }
        }

        if (!elementsForSave.isEmpty()) {
            getObjectBoxDao().insertElementPassedR(elementsForSave);
        }
        return saved;
    }

    private boolean answersHavePhoto(List<AnswerState> answerStates) {
        st("answersHavePhoto() +++");
        for (AnswerState answerState : answerStates) {
            if (answerState.isChecked() && answerState.isIsPhotoAnswer() && !answerState.hasPhoto()) {
                showToast("Пожалуйта сделайте фото");
                return false;
            }
        }
        return true;
    }

    public boolean notEmpty(List<AnswerState> answerStates) {
        st("notEmpty() +++");
        int counter = 0;

        for (AnswerState state : answerStates) {
            if (state.isChecked()) {
                ElementItemR element = null;
                boolean openType = false;
                try {
                    element = getElement(state.getRelative_id());
                    if (!element.getElementOptionsR().getOpen_type().equals("checkbox")) {
                        openType = true;
                    }
                    if (element.getElementOptionsR().isUnchecker()) {
                        st("notEmpty().1 ---");
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (openType && !element.getElementOptionsR().isUnnecessary_fill_open()) {
                    if (state.getData() == null || state.getData().equals("")) {
                        if (answerType.equals(ElementSubtype.RANK))
                            showToast(getString(R.string.empty_rank_warning));
                        else showToast(getString(R.string.empty_string_warning));
                        st("notEmpty().2 ---");
                        return false;
                    }
                }
                counter++;
            }
        }

        Integer min = currentElement.getElementOptionsR().getMin_answers();
        Integer max = currentElement.getElementOptionsR().getMax_answers();
        if (counter > 0) {
            if (min != null && counter < min) {
                showToast("Выберите минимум " + min + " ответа.");
                st("notEmpty().3 ---");
                return false;
            }
            if (max != null && counter > max) {
                showToast("Выберите максимум " + max + " ответ(а).");
                st("notEmpty().4 ---");
                return false;
            }
        } else if (currentElement.getElementOptionsR().getOptional_question() != null && currentElement.getElementOptionsR().getOptional_question()) {
            return true;
        } else {
            if (min != null)
                showToast("Выберите минимум " + min + " ответа.");
            else
                showToast("Выберите минимум 1 ответ.");
            st("notEmpty().5 ---");
            return false;
        }
        st("notEmpty().6 ---");
        return true;
    }

    private void updatePrevElement() {
        st("updatePrevElement() +++");
        if (prevList != null) {
            prevList = getObjectBoxDao().getPrevElementsR();
        } else {
            prevList = new ArrayList<>();
        }
        getObjectBoxDao().insertPrevElementsR(new PrevElementsO(startElementId, nextElementId));
        st("updatePrevElement() ---");
    }

    @Override
    public void onAnswerClick(int position, boolean enabled, String answer) {

    }

    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
        @Override
        public void run() {
            UiUtils.hideKeyboard(getContext(), getView());
        }
    };

    private void loadFromCard(List<CardItem> items) {
        st("loadFromCard() +++");
        List<AnswerState> answerStatesAdapter = adapterList.getAnswers();
        List<AnswerState> answerStatesRestored = new ArrayList<>();
        Map<Integer, CardItem> itemsMap = convertCardsListToMap(items);
        if (itemsMap != null && itemsMap.size() > 0) {
            for (int i = 0; i < answerStatesAdapter.size(); i++) {
                AnswerState answerStateNew = new AnswerState();

                if (itemsMap.get(answerStatesAdapter.get(i).getRelative_id()) != null || adapterList.isAutoChecked(i)) {
                    answerStateNew.setChecked(itemsMap.get(answerStatesAdapter.get(i).getRelative_id()).isChecked());
                    if (itemsMap.get(answerStatesAdapter.get(i).getRelative_id()).getData() != null
                            && !itemsMap.get(answerStatesAdapter.get(i).getRelative_id()).getData().equals("")) {
                        answerStateNew.setData(itemsMap.get(answerStatesAdapter.get(i).getRelative_id()).getData());
                    }
                    answerStateNew.setCheckedInCard(itemsMap.get(answerStatesAdapter.get(i).getRelative_id()).isCheckedInCard());
                } else {
                    answerStateNew.setChecked(false);
                    answerStateNew.setCheckedInCard(false);
                    answerStateNew.setData("");
                }

                answerStateNew.setRelative_id(answerStatesAdapter.get(i).getRelative_id());
                answerStatesRestored.add(answerStateNew);
            }

            adapterList.setAnswers(answerStatesRestored);
            adapterList.setRestored(true);
            adapterList.notifyDataSetChanged();
        }
        st("loadFromCard() ---");
    }

    private Map<Integer, CardItem> convertCardsListToMap(List<CardItem> list) {
        st("convertCardsListToMap() +++");
        Map<Integer, CardItem> map = new HashMap<>();
        for (CardItem cardItem : list) {
            map.put(cardItem.getId(), cardItem);
        }
        st("convertCardsListToMap() ---");
        return map;
    }

    private Map<Integer, AnswerState> convertAnswersListToMap(List<AnswerState> list) {
        st("convertAnswersListToMap() +++");
        Map<Integer, AnswerState> map = new HashMap<>();
        for (AnswerState cardItem : list) {
            map.put(cardItem.getRelative_id(), cardItem);
        }
        st("convertAnswersListToMap() ---");
        return map;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mActivity = (MainActivity) getActivity();
        assert mActivity != null;
        if (!mActivity.checkPermission()) {
            mActivity.requestPermission();
        }
        if (adapterList != null) {
            adapterList.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapterList != null) {
            adapterList.onPause();
        }
    }

    @Override
    public boolean onBackPressed() {
        exitQuestionnaire();
        return true;
    }

    public boolean saveQuestionnaire(boolean aborted) {
        st("saveQuestionnaire() +++");

        stopAllRecording();
        if (!aborted || getMainActivity().getConfig().isSaveAborted()) {
            if (saveQuestionnaireToDatabase(getQuestionnaire(), aborted)) {
                try {
                    getMainActivity().saveTimings();
                    getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showToast(getString(R.string.quiz_saved));

            } else {
                showToast(getString(R.string.quiz_save_error));
                return false;
            }
        }
        st("saveQuestionnaire() ---");
        return true;
    }

    public void exitQuestionnaire() {
        st("exitQuestionnaire() +++");
        stopAllRecording();
        try {
            getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
            getDao().deleteOnlineQuota(getQuestionnaire().getToken());
            Log.d("T-A-R.", "CLEAR Questionnaire: 1");
            getDao().clearCurrentQuestionnaireR();
            getObjectBoxDao().clearElementPassedR();
            getObjectBoxDao().clearPrevElementsR();
            getMainActivity().setCurrentQuestionnaireNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
        st("exitQuestionnaire() ---");
        getMainActivity().restartHome();
    }

    private void stopAllRecording() {
        try {
            getMainActivity().stopRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFilePath(final String data) {
        final String path = FileUtils.getFilesStoragePath(getMainActivity());

        if (StringUtils.isEmpty(data)) {
            return Constants.Strings.EMPTY;
        }

        final String fileName = FileUtils.getFileName(data);

        return path + FileUtils.FOLDER_DIVIDER + fileName;
    }

    public void showExitPoolAlertDialog() {
        st("showExitPoolAlertDialog() +++");
        activateButtons();
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.exit_quiz_header)
                    .setMessage(getMainActivity().getConfig().isSaveAborted() ? R.string.exit_questionnaire_with_saving_warning : R.string.exit_questionnaire_warning)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> {
                        st("showExitPoolAlertDialog() ---");
                        saveAndExit(true);
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    private void saveAndExit(boolean exit) {
        st("saveAndExit() +++");
        if (getMainActivity().getConfig().isSaveAborted()) {
            showScreensaver(true);
            String token = getQuestionnaire().getToken();

            if (saveQuestionnaire(true)) {
                showToast(getString(R.string.save_questionnaire));

                try {
                    getDao().deleteOnlineQuota(token);
                    Log.d("T-A-R.", "CLEAR Questionnaire: 2");
                    getDao().clearCurrentQuestionnaireR();
                    getObjectBoxDao().clearElementPassedR();
                    getObjectBoxDao().clearPrevElementsR();
                    getMainActivity().setCurrentQuestionnaireNull();
                } catch (Exception e) {
                    e.printStackTrace();
                    hideScreensaver();
                    activateButtons();
                }
                if (exit)
                    exitQuestionnaire();
            } else {
                hideScreensaver();
                activateButtons();
            }
        } else {
            try {
                getDao().deleteElementDatabaseModelByToken(getMainActivity().getCurrentQuestionnaireForce().getToken());
                getDao().deleteOnlineQuota(getQuestionnaire().getToken());
                Log.d("T-A-R.", "CLEAR Questionnaire: 3");
                getDao().clearCurrentQuestionnaireR();
                getObjectBoxDao().clearElementPassedR();
                getObjectBoxDao().clearPrevElementsR();
                getMainActivity().setCurrentQuestionnaireNull();
            } catch (Exception e) {
                e.printStackTrace();
                hideScreensaver();
                activateButtons();
            }
            if (exit)
                exitQuestionnaire();
        }
        st("saveAndExit() ---");
    }

    private void activateButtons() {
        try {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                getMainActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (btnPrev != null && btnExit != null && btnNext != null)
                            try {
                                activity.hideProgressBar();
                                btnPrev.setEnabled(true);
                                btnExit.setEnabled(true);
                                btnNext.setEnabled(true);

                                btnPrev.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(activity), R.drawable.button_background_green));
                                btnExit.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(activity), isAvia() ? R.drawable.button_background_green : R.drawable.button_background_red));
                                btnNext.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(activity), R.drawable.button_background_green));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                });
            } else {
                Log.d(TAG, "activateButtons: ERROR! ACTIVITY = NULL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deactivateButtons() {
        final MainActivity activity = getMainActivity();

        if (activity != null) {
            try {
                activity.showProgressBar();
                btnPrev.setEnabled(false);
                btnExit.setEnabled(false);
                btnNext.setEnabled(false);

                btnPrev.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
                btnExit.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
                btnNext.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DoNext extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            st("DoNext() +++");
            deactivateButtons();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("T-A-R.ElementFragment", "SAVE IN DONEXT: " + nextElementId);
                if (saveElement()) {
                    Log.d("T-A-R.ElementFragment", "doInBackground: " + nextElementId);
                    try {
                        // ===============================================================================================================================
                        if (nextElementId == 0) {
                            Log.d("T-A-R.ElementFragment", "?????????: 4");
                            if (saveQuestionnaire(false)) {
                                exitQuestionnaire();
                            } else {
                                activateButtons();
                            }
                        } else if (nextElementId == -1) {
                            Log.d("T-A-R.ElementFragment", "?????????: 5");
                            if (getMainActivity().getConfig().isSaveAborted()) {
                                Log.d("T-A-R.ElementFragment", "?????????: 6");
                                if (saveQuestionnaire(true)) {
                                    exitQuestionnaire();
                                } else {
                                    activateButtons();
                                }
                            } else {
                                Log.d("T-A-R.ElementFragment", "?????????: 7");
                                exitQuestionnaire();
                            }
                        } else {
                            Log.d("T-A-R.ElementFragment", "checkAndLoadNxt() ON: 4");
                            checkAndLoadNext();
                            if (!isInHiddenQuotaDialog) {
                                updatePrevElement();
                            }
                        }
                    } catch (Exception e) {
                        Log.d("T-A-R.ElementFragment", "?????????: 8");
                        e.printStackTrace();
                        activateButtons();
                    }
                } else {
                    Log.d("T-A-R.ElementFragment", "doInBackground: SAVE FALSE. WTF?");
                    activateButtons();
                }
            } catch (Exception e) {
                Log.d("T-A-R.ElementFragment", "?????????: 9");
                e.printStackTrace();
                getMainActivity().addLog(Constants.LogObject.WARNINGS, "press NEXT", Constants.LogResult.ERROR, "Cant go next Element", e.toString());
                activateButtons();

            }
            st("DoNext() ---");
            Log.d("T-A-R.ElementFragment", "?????????: 10");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void checkAndLoadNext() {
        Log.d("T-A-R", "checkAndLoadNext: " + currentElement.getRelative_id());
        st("checkAndLoadNext() +++");
        try {
            if (!isInHiddenQuotaDialog) {

                if (nextElementId != null && !nextElementId.equals(0) && !nextElementId.equals(-1)) {
                    if (checkConditions(getElement(nextElementId))) {
                        if (nextElementId == 0) {
                            if (saveQuestionnaire(false)) {
                                exitQuestionnaire();
                            } else {
                                activateButtons();
                            }
                        } else if (nextElementId == -1) {
                            exitQuestionnaire();
                        } else {
                            if (!isFragmentLoading) {
                                isFragmentLoading = true;
                                Integer startId;
                                startId = currentElement.getRelative_id();
                                transfer(startId, nextElementId, isSearch);
                            }
                        }
                    } else {
                        Log.d("T-A-R.ElementFragment", "checkAndLoadNxt() ON: 5");
                        checkAndLoadNext(); // Тут ошибка показа ротации
                    }
                } else {
                    if (nextElementId == 0) {
                        if (saveQuestionnaire(false)) {
                            exitQuestionnaire();
                        } else {
                            activateButtons();
                        }
                    } else if (nextElementId == -1) {
                        exitQuestionnaire();
                    }
                }
            }
        } catch (Exception e) {

            Log.d("T-A-R", "checkAndLoadNext: ERROR");
            e.printStackTrace();
            getMainActivity().addLog(Constants.LogObject.WARNINGS, "press NEXT 2", Constants.LogResult.ERROR, "Cant go next Element", e.toString());
            activateButtons();
        }
        st("checkAndLoadNext() ---");
    }

    private void startRecording() {
        st("startRecording() +++");
        MainActivity activity = getMainActivity();
        if (activity != null && activity.getConfig().isAudio() && currentElement.getElementOptionsR().isRecord_sound() && !activity.getConfig().isAudioRecordAll()) {
            try {
                activity.startRecording(currentElement.getRelative_id(), getQuestionnaire().getToken());
            } catch (Exception e) {
                activity.addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", e.toString());
                e.printStackTrace();
            }
        }
        if (activity != null && activity.getConfig().isAudio() && activity.getConfig().isAudioRecordAll()) {
            try {
                activity.startRecording(0, getQuestionnaire().getToken());
            } catch (Exception e) {
                activity.addLog(Constants.LogObject.AUDIO, "startRecording", Constants.LogResult.ERROR, "Cant start audio", e.toString());
                e.printStackTrace();
            }
        }
        st("startRecording() ---");
    }

    private void stopRecording() {
        st("stopRecording() +++");
        MainActivity activity = getMainActivity();
        if (activity != null && activity.getConfig().isAudio() && currentElement.getElementOptionsR().isRecord_sound() && !activity.getConfig().isAudioRecordAll()) {
            try {
                activity.stopRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        st("stopRecording() ---");
    }

    @SuppressLint("RestrictedApi")
    private void showCardDialog() {
        currentElement.setShowed_in_card(true);
        dialogBuilder = new AlertDialog.Builder(getMainActivity());
        View layoutView = getLayoutInflater().inflate(R.layout.dialog_card, null);

        View mCloseBtn = layoutView.findViewById(R.id.view_close);
        ListView listView = layoutView.findViewById(R.id.card_list);

        mCloseBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
        });

        int counter = 1;
        List<CardItem> items = new ArrayList<>();
        Map<Integer, AnswerState> answerStateMap = convertAnswersListToMap(adapterList.getAnswers());
        if (currentElement.getSubtype().equals(ElementSubtype.LIST)) {

            for (AnswerItem element : adapterList.getUiAnswersList()) {
                if (element.isShow_in_card()) {
                    String title = counter + ". " + Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle();
                    CardItem item = new CardItem(element.getRelative_id(), title, "",
                            element.isUnchecker(),
                            false,
                            element.getOpen_type(),
                            element.getPlaceholder(),
                            element.isUnnecessary_fill_open(),
                            element.isAutoChecked(),
                            element.isHelper(),
                            element.getMin_number(),
                            element.getMax_number(),
                            element.isCheckedInCard()
                    );
                    if (element.getContents() != null && element.getContents().size() > 0) {
                        List<String> pics = new ArrayList<>();
                        for (ElementContentsR content : element.getContents()) {
                            if (content.getData() != null && !content.getData().equals("")) pics.add(content.getData());
                        }
                        item.setPic(pics);
                        item.setThumb(element.getContents().get(0).getData_thumb());
                    }
                    items.add(item);
                    counter++;
                }
            }

            if (answerStateMap.size() > 0)
                for (int i = 0; i < items.size(); i++) {
                    if (answerStateMap.get(items.get(i).getId()) != null) {
                        items.get(i).setChecked(answerStateMap.get(items.get(i).getId()).isChecked());
                        items.get(i).setData(answerStateMap.get(items.get(i).getId()).getData());
                    }
                }

            CardAdapter adapter = new CardAdapter(getMainActivity(),
                    getMainActivity().isAutoZoom() ? R.layout.holder_card_auto : R.layout.holder_card, items, currentElement.getElementOptionsR().isPolyanswer());
            listView.setAdapter(adapter);
            mCloseBtn.setOnClickListener(v -> {
                loadFromCard(adapter.getItems());
                infoDialog.dismiss();
            });
        } else {
            for (ElementItemR element : answersList) {
                if (element.getElementOptionsR() != null && element.getElementOptionsR().isShow_in_card()) {
                    String title = counter + ". " + Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle();
                    CardItem item = new CardItem(element.getRelative_id(), title, "",
                            element.getElementOptionsR().isUnchecker(),
                            false,
                            element.getElementOptionsR().getOpen_type(),
                            element.getElementOptionsR().getPlaceholder(),
                            element.getElementOptionsR().isUnnecessary_fill_open(),
                            element.getElementOptionsR().isAutoChecked(),
                            element.getElementOptionsR().isHelper(),
                            element.getElementOptionsR().getMin_number(),
                            element.getElementOptionsR().getMax_number(),
                            element.getChecked_in_card()

                    );
                    if (element.getElementContentsR() != null && element.getElementContentsR().size() > 0) {
                        List<String> pics = new ArrayList<>();
                        for (ElementContentsR content : element.getElementContentsR()) {
                            if (content.getData() != null && !content.getData().equals("")) pics.add(content.getData());
                        }
                        item.setPic(pics);
                        item.setThumb(element.getElementContentsR().get(0).getData_thumb());
                    }
                    items.add(item);
                    counter++;
                }
            }

            if (answerStateMap.size() > 0)
                for (int i = 0; i < items.size(); i++) {
                    if (answerStateMap.get(items.get(i).getId()) != null) {
                        items.get(i).setChecked(answerStateMap.get(items.get(i).getId()).isChecked());
                        items.get(i).setData(answerStateMap.get(items.get(i).getId()).getData());
                    }
                }

            CardAdapter adapter = new CardAdapter(getMainActivity(),
                    getMainActivity().isAutoZoom() ? R.layout.holder_card_auto : R.layout.holder_card, items, currentElement.getElementOptionsR().isPolyanswer());
            listView.setAdapter(adapter);
            mCloseBtn.setOnClickListener(v -> {
                loadFromCard(adapter.getItems());
                infoDialog.dismiss();
            });
        }

        dialogBuilder.setView(layoutView, 10, 40, 10, 10);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing())
            infoDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearViews();
        disposables.clear();
    }

    private void clearViews() {
        btnNext = null;
        btnPrev = null;
        btnExit = null;
        btnHideTitle = null;
        titleCont1 = null;
        titleCont2 = null;
        titleImagesCont1 = null;
        titleImagesCont2 = null;
        questionCont = null;
        questionImagesCont = null;
        tvTitle1 = null;
        tvTitle2 = null;
        tvQuestion = null;
        tvTitleDesc1 = null;
        tvTitleDesc2 = null;
        tvQuestionDesc = null;
        rvAnswers = null;
        tableLayout = null;
        title1Image1 = null;
        title1Image2 = null;
        title1Image3 = null;
        title2Image1 = null;
        title2Image2 = null;
        title2Image3 = null;
        questionImage1 = null;
        questionImage2 = null;
        questionImage3 = null;
        closeQuestion = null;
        dialogBuilder = null;
        infoDialog = null;
        adapterList = null;
    }

    private void tableRedrawEverything() {
        st("tableRedrawEverything() +++");
        if (answerType.equals(ElementSubtype.TABLE))
            try {
                tableLayout.scrollTo(1, 1);
                tableLayout.requestApplyInsets();
                tableLayout.invalidate();
                tableLayout.refreshDrawableState();
            } catch (Exception e) {
                e.printStackTrace();
            }
        st("tableRedrawEverything() ---");
    }

    void transfer(int start, int next, boolean search) {
        Log.d("T-A-R.ExitElement", "transfer to: " + next + " search: " + search);
        if (!search) {
            ElementFragment fragment = new ElementFragment();
            fragment.setStartElement(next, false);
            replaceFragment(fragment);
        } else {
            UikSelectFragment fragment = new UikSelectFragment();
            fragment.setStartElement(next);
            replaceFragment(fragment);
        }
    }



}

