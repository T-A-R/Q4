package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.multispinner.MultiSelectSpinner;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.QToken;
import pro.quizer.quizer3.API.models.request.OnlineQuotasRequestModel;
import pro.quizer.quizer3.API.models.response.OnlineQuotaResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.CardAdapter;
import pro.quizer.quizer3.adapter.ListAnswersAdapter;
import pro.quizer.quizer3.adapter.RankQuestionAdapter;
import pro.quizer.quizer3.adapter.ScaleQuestionAdapter;
import pro.quizer.quizer3.adapter.TableCardAdapter;
import pro.quizer.quizer3.adapter.TableQuestionAdapter;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.OnlineQuotaR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.model.CardItem;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.state.RotationBoxState;
import pro.quizer.quizer3.model.ui.AnswerItem;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.objectbox.models.ElementPassedOB;
import pro.quizer.quizer3.objectbox.models.PrevElementsO;
import pro.quizer.quizer3.utils.ConditionUtils;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.ExpressionUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.Internet;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.AVIA;
import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementFragment extends ScreenFragment implements View.OnClickListener, ListAnswersAdapter.OnAnswerClickListener, RankQuestionAdapter.OnAnswerClickListener, ScaleQuestionAdapter.OnAnswerClickListener, TableQuestionAdapter.OnTableAnswerClickListener {

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
    private LinearLayout spinnerCont;
    private LinearLayout infoCont;
    private FrameLayout tableCont;
    private TextView tvHiddenTitle;
    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvQuestion;
    private TextView tvHiddenQuestion;
    private TextView tvTitleDesc1;
    private TextView tvTitleDesc2;
    private TextView tvQuestionDesc;
    private WebView infoText;
    private RecyclerView rvAnswers;
    private RecyclerView rvScale;
    private SearchableSpinner spinnerAnswers;
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

    private boolean isQuestionHided = false;
    private boolean hasQuestionImage = false;
    private boolean isRandomBox = false;
    private boolean isRotationBox = false;
    private ElementItemR currentElement = null;
    List<ElementItemR> answersList;
    private Long startTime;
    private Integer startElementId;
    private Integer nextElementId;
    private Integer prevElementId;
    private String answerType;
    private int spinnerSelection = -1;
    private List<Integer> spinnerMultipleSelection;
    List<ElementItemR> multiSelectedList = new ArrayList<>();
    private boolean isTitle1Hided = false;
    private boolean isRestored = false;
    private boolean isMultiSpinner = false;
    private boolean isQuota = false;
    private boolean canBack = true;
    private int titles = 0;

    private ListAnswersAdapter adapterList;
    private RankQuestionAdapter adapterRank;
    private ScaleQuestionAdapter adapterScale;
    private ArrayAdapter adapterSpinner;
    private TableQuestionAdapter adapterTable;
    private MultiSelectSpinner multiSelectionSpinner;
    private List<PrevElementsO> prevList = null;
    private Map<Integer, TitleModel> titlesMap;
    private CompositeDisposable disposables;
    private boolean isInHiddenQuotaDialog = false;
    private boolean isFragmentLoading = false;
    private boolean passedQuotaQuestion = false;

    private Integer hiddenId;

    private List<ElementItemR> quotaElementsList = new ArrayList<>();

    public ElementFragment() {
        super(R.layout.fragment_element);
    }

    public ElementFragment setStartElement(Integer startElementId, boolean restored) {
        this.startElementId = startElementId;
        this.isRestored = restored;
        return this;
    }

    @Override
    protected void onReady() {
        st("onReady() +++");
        disposables = new CompositeDisposable();
        setRetainInstance(true);
//        showScreensaver(R.string.please_wait_quiz_element, true);

        initViews();
        initElements();
        loadResumedData();
        initQuestion();
        getQuestionnaire().setIn_uik_question(false);
        getDao().setCurrentQuestionnaireInUikQuestion(false);
        if (currentElement != null) {
            Log.d("T-A-R", "onReady: " + currentElement.getRelative_id());
            initCurrentElement();
        } else {
            exitQuestionnaire();
        }
        Log.d("T-A-R.ElementFragment", "??? getQuestionnaire(): <<<<<<<<<<<<<<<<<<<<<< " + getQuestionnaire());
        if (getQuestionnaire().getFirst_element_id() == 0 || getQuestionnaire().getFirst_element_id().equals(currentElement.getRelative_id())) {
            btnPrev.setVisibility(View.INVISIBLE);
            getQuestionnaire().setFirst_element_id(currentElement.getRelative_id());
            getDao().setCurrentQuestionnaireFirstElementId(currentElement.getRelative_id());
        }
//        else btnPrev.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                checkOnlineQuotas();
            }
        });

        st("onReady() ---");
    }

    private void initViews() {
        st("initViews() +++");
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
        spinnerCont = findViewById(R.id.spinner_cont);
        infoCont = findViewById(R.id.info_cont);
        tableCont = findViewById(R.id.table_cont);
        rvAnswers = findViewById(R.id.answers_recyclerview);
        rvScale = findViewById(R.id.scale_recyclerview);
        tableLayout = findViewById(R.id.table_question_layout);
        tvTitle1 = findViewById(R.id.title_1);
        tvTitle2 = findViewById(R.id.title_2);
        tvTitleDesc1 = findViewById(R.id.title_desc_1);
        tvTitleDesc2 = findViewById(R.id.title_desc_2);
        tvQuestion = findViewById(R.id.question);
        tvHiddenQuestion = findViewById(R.id.hidden_question_title);
        tvQuestionDesc = findViewById(R.id.question_desc);
        infoText = findViewById(R.id.info_text);
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

        if (isAvia()) {
            btnNext.setTypeface(Fonts.getAviaText());
            btnPrev.setTypeface(Fonts.getAviaText());
            btnExit.setTypeface(Fonts.getAviaButton());
            tvTitle1.setTypeface(Fonts.getAviaText());
            tvTitle2.setTypeface(Fonts.getAviaText());
            tvTitleDesc1.setTypeface(Fonts.getAviaText());
            tvTitleDesc2.setTypeface(Fonts.getAviaText());
            tvQuestion.setTypeface(Fonts.getAviaText());
            tvHiddenQuestion.setTypeface(Fonts.getAviaText());
            tvQuestionDesc.setTypeface(Fonts.getAviaText());
        } else {
            btnNext.setTransformationMethod(null);
            btnPrev.setTransformationMethod(null);
            btnExit.setTransformationMethod(null);
        }

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
        if (!isAvia()) {
            toolbar.setTitle(getString(R.string.app_name));
            toolbar.showOptionsView(v -> MainFragment.showDrawer(), v -> showInfoDialog());
            toolbar.showInfoView();
            MainFragment.enableSideMenu(false, getMainActivity().isExit());
        }

        try {
            prevList = getObjectBoxDao().getPrevElementsR();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (prevList == null || prevList.size() == 0) {
            prevList = new ArrayList<>();
            btnPrev.setVisibility(View.INVISIBLE);
        }

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
        st("initCurrentElement() +++");
//        Log.d("T-A-R", "initCurrentElement: ");
        setQuestionType();
        if (checkConditions(currentElement)) {
//            Log.d("T-A-R", "checkConditions: PASS");
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

            if (currentElement.getSubtype().equals(ElementSubtype.TABLE)) {
                for (ElementItemR element : answersList) {
                    for (ElementItemR answer : element.getElements()) {
                        titlesMap.put(answer.getRelative_id(), new TitleModel(answer.getElementOptionsR().getTitle(), answer.getElementOptionsR().getDescription()));
                    }
                }
            }

            if (currentElement.getSubtype().equals(ElementSubtype.HIDDEN)) {
                checkHidden();
            }

            Disposable subscribeTitles = getMainActivity().getConvertedTitles(titlesMap).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> disposables.add(disposable))
                    .subscribe(
                            (Map<Integer, TitleModel> convertedTitles) -> {
                                titlesMap = convertedTitles;

                                initRecyclerView();
                                if (isRestored || wasReloaded()) {
                                    loadSavedData();
                                }
                                hideScreensaver();
                                activateButtons();
                            },
                            Throwable::printStackTrace,
                            () -> {
                            });

        } else {
//            Log.d("T-A-R", "checkConditions: NOT PASS");
            if (isQuota) passedQuotaQuestion = true;
            Log.d("T-A-R.ElementFragment", "checkAndLoadNxt() ON: 1");
            checkAndLoadNext();
        }

        try {
            getMainActivity().activateExitReminder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        st("initCurrentElement() ---");
    }

    public boolean wasReloaded() {
        st("wasReloaded() +++");
        List<ElementPassedOB> elements = null;

        try {
//            elements = getDao().getAllElementsPassedR(getQuestionnaire().getToken());
            elements = getObjectBoxDao().getAllElementsPassedR(getQuestionnaire().getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (elements != null && elements.size() > 0) {
//            Log.d("T-A-R", "++++++++++ elements: " + elements.size());
            for (ElementPassedOB element : elements) {
                if (element.getRelative_id().equals(currentElement.getRelative_id())) {
                    isRestored = true;
                    st("wasReloaded() ---");
                    return true;
                }
            }
        }
        st("wasReloaded() ---");
        return false;
    }

    @Override
    public void onClick(View view) {
        st("onClick +++");
        if (view == btnNext) {
            if (answerType.equals(ElementSubtype.END)) {
                nextElementId = currentElement.getElementOptionsR().getJump();
            }
            DoNext next = new DoNext();
            next.execute();
        } else if (view == btnPrev) {
            deactivateButtons();
            if (prevElementId != 0) {
                loadPreviousElement();
            } else {
                showExitPoolAlertDialog();
            }
        } else if (view == btnExit) {
            deactivateButtons();
            checkAbortedBox();
            MainActivity activity = getMainActivity();
            boolean isInAbortedBox = false;
            if (activity != null) {
                CurrentQuestionnaireR quiz = activity.getCurrentQuestionnaireForce();
                if (quiz != null) {
                    isInAbortedBox = quiz.isIn_aborted_box();
                }

                if (activity.getConfig().isSaveAborted()
                        && hasAbortedBox()
                        && !isInAbortedBox) {
                    try {
                        getDao().setCurrentQuestionnaireInAbortedBox(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stopRecording();
                    transfer(0, getAbortedBoxRelativeId(), false);
                } else {
                    showExitPoolAlertDialog();
                }
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
        st("onClick ---");
    }

    private void loadPreviousElement() {
        st("loadPreviousElement() +++");
        prevElementId = prevList.get(prevList.size() - 1).getPrevId();
        prevList.remove(prevList.size() - 1);
        try {
            getObjectBoxDao().clearPrevElementsR();
            getObjectBoxDao().setPrevElement(prevList);

        } catch (Exception e) {
            showToast(getString(R.string.set_last_element_error));
            return;
        }
        TransFragment fragment = new TransFragment();
        fragment.setStartElement(prevElementId, true);
        stopRecording();
        st("loadPreviousElement() ---");
        replaceFragmentBack(fragment);
    }

    private void initQuestion() {
        st("initQuestion() +++");
        Log.d("T-A-R.ElementFragment", "initQuestion: <<<<<");
        startTime = DateUtils.getCurrentTimeMillis();

        if (getQuestionnaire() == null) {
            initElements();
        }
        if (getQuestionnaire() != null)
            if (prevList != null && prevList.size() > 0) {
                prevElementId = prevList.get(prevList.size() - 1).getPrevId();
            } else {
                prevElementId = 0;
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
                    isRandomBox = curEl.getElementOptionsR().getShowRandomQuestion() != null && curEl.getElementOptionsR().getShowRandomQuestion();
                    isRotationBox = curEl.getElementOptionsR().isRotation();
                    boolean isTable = curEl.getSubtype().equals(ElementSubtype.TABLE);
                    if (!isBox || isTable) {
                        startElementId = curEl.getRelative_id();
                        currentElement = curEl;
                        break;
                    } else {
                        Log.d("T-A-R.ElementFragment", "isBox: " + isBox);
                        if (!checkConditions(curEl)) {
                            Log.d("T-A-R.ElementFragment", "checkAndLoadNxt() ON: 2");
                            checkAndLoadNext();
                            break;
                        } else if (isRandomBox) {
                            Log.d("T-A-R.ElementFragment", "isRandom: <<<<<<<<<<<<<");
                            currentElement = curElQuestions.get(new Random().nextInt(curElQuestions.size()));
                            startElementId = currentElement.getRelative_id();
                            break;
                        } else if (isRotationBox) {
                            Log.d("T-A-R.ElementFragment", "isRotationBox: <<<<<<<<<<<<< BOX:" + currentElement.getRelative_id());
                            currentElement = makeRotationInBox(curEl);
                            if (checkConditions(currentElement)) {
                                startElementId = currentElement.getRelative_id();
                            } else {
                                nextElementId = getJumpInRotationBox(curEl.getRelative_id(), currentElement.getRelative_id());
//                                Log.d("T-A-R.ElementFragment", "checkAndLoadNxt() ON: 3");
//                                checkAndLoadNext();
                            }
                            break;
                        }
                    }
                }
            }
//            }
        } else {
            Log.d(TAG, "initQuestions: ERROR! (empty list)");
        }
        st("initQuestion() ---");
    }

    private ElementItemR makeRotationInBox(ElementItemR box) {

        List<ElementItemR> questions = box.getElements();
        List<Integer> ids = new ArrayList<>();

        List<ElementItemR> shuffleList = new ArrayList<>();
        for (ElementItemR elementItemR : questions) {
            if (!elementItemR.getElementOptionsR().isFixed_order()) {
                shuffleList.add(elementItemR);
            }
        }
        Collections.shuffle(shuffleList, new Random());
        int k = 0;

        for (int i = 0; i < questions.size(); i++) {
            if (!questions.get(i).getElementOptionsR().isFixed_order()) {
                questions.set(i, shuffleList.get(k));
                k++;
            }
        }

        for (ElementItemR question : questions) {
            ids.add(question.getRelative_id());
        }

        Log.d("T-A-R.ElementFragment", "makeRotationInBox: " + new Gson().toJson(ids));

        String states = getMainActivity().getCurrentQuestionnaire().getRotation_state();
        RotationBoxState rotationBoxState = null;
        if (states == null || states.isEmpty()) {
            rotationBoxState = new RotationBoxState();
        } else {
            try {
                rotationBoxState = new Gson().fromJson(states, RotationBoxState.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (rotationBoxState == null) rotationBoxState = new RotationBoxState();
        }

        Map<Integer, List<Integer>> rotationsStates = rotationBoxState.getRotationsStates();
        rotationsStates.put(box.getRelative_id(), ids);
        rotationBoxState.setRotationsStates(rotationsStates);

        getDao().setRotationState(new Gson().toJson(rotationBoxState));
        getMainActivity().getCurrentQuestionnaireForce();

        return questions.get(0);
    }

    private int getJumpInRotationBox(int parentId, int currentId) {
        List<Integer> ids = null;
        int nextId = 0;
        try {
            ids = new Gson().fromJson(getMainActivity().getCurrentQuestionnaire().getRotation_state(), RotationBoxState.class).getRotationsStates().get(parentId);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (ids != null) {
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i) == currentId) {
                    if (i < ids.size() - 1)
                        nextId = ids.get(i + 1);
                    else nextId = getElement(parentId).getElementOptionsR().getJump();
                    break;
                }
            }
        } else nextId = getElement(parentId).getElementOptionsR().getJump();
        Log.d("T-A-R.ElementFragment", "getJumpInRotationBox: " + nextId);
        return nextId;
    }

    private boolean checkRotationBox(int parentId, int id) {
        List<Integer> ids = null;
        try {
            String rotationState = getMainActivity().getCurrentQuestionnaire().getRotation_state();
            if (rotationState != null && !rotationState.isEmpty())
                try {
                    ids = new Gson().fromJson(rotationState, RotationBoxState.class).getRotationsStates().get(parentId);
                } catch (JsonSyntaxException e) {
                    Log.d("T-A-R.ElementFragment", "checkRotationBox: ids = null");
                }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (ids != null) {
            return true;
        }
        return false;
    }

    private void setQuestionType() {
        st("setQuestionType() +++");

        isQuota = currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA);
        switch (currentElement.getSubtype()) {
            case ElementSubtype.LIST:
                answerType = ElementSubtype.LIST;
                break;
            case ElementSubtype.SELECT:
                answerType = ElementSubtype.SELECT;
                break;
            case ElementSubtype.TABLE:
                answerType = ElementSubtype.TABLE;
                break;
            case ElementSubtype.SCALE:
                answerType = ElementSubtype.SCALE;
                break;
            case ElementSubtype.HTML:
                answerType = ElementSubtype.HTML;
                break;
            case ElementSubtype.END:
                answerType = ElementSubtype.END;
                break;
            case ElementSubtype.RANK:
                answerType = ElementSubtype.RANK;
                break;
            case ElementSubtype.HIDDEN:
                answerType = ElementSubtype.HIDDEN;
                break;
        }
        st("setQuestionType() ---");
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

    private List<ElementItemR> checkTableAnswers(List<ElementItemR> elements) {
        List<ElementItemR> checkedAnswersList = new ArrayList<>();
        for (int a = 0; a < elements.size(); a++) {
            boolean check = checkConditions(elements.get(a));
            if (check) {
                checkedAnswersList.add(elements.get(a));
            }
        }
        return checkedAnswersList;
    }

    private void initRecyclerView() {
        st("initRecyclerView() +++");
        List<String> itemsList = new ArrayList<>();
        switch (answerType) {
            case ElementSubtype.LIST:
            case ElementSubtype.RANK:
                rvAnswers.setVisibility(View.VISIBLE);
                break;
            case ElementSubtype.SELECT:
                spinnerCont.setVisibility(View.VISIBLE);
                break;
            case ElementSubtype.TABLE:
                tableCont.setVisibility(View.VISIBLE);
//                tableLayout.setVisibility(View.VISIBLE);
                break;
            case ElementSubtype.HTML:
                questionCont.setVisibility(View.GONE);
                infoCont.setVisibility(View.VISIBLE);
                infoText.loadData(currentElement.getElementOptionsR().getData(), "text/html; charset=UTF-8", null);
                break;
            case ElementSubtype.END:
                questionCont.setVisibility(View.GONE);
                infoCont.setVisibility(View.VISIBLE);
                infoText.loadData(currentElement.getElementOptionsR().getData(), "text/html; charset=UTF-8", null);
                btnExit.setVisibility(View.GONE);
                btnPrev.setVisibility(View.GONE);
                btnNext.setText(R.string.button_finish);
                canBack = false;
                break;
            case ElementSubtype.SCALE:
                rvScale.setVisibility(View.VISIBLE);
                break;
        }

        for (ElementItemR element : answersList) {
            itemsList.add(Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle());
        }

        switch (answerType) {
            case ElementSubtype.LIST:
                MainActivity activity = getMainActivity();
                if (isQuota) {
                    removeHelper();
                    adapterList = new ListAnswersAdapter(activity, currentElement, answersList,
                            getMultiPassedQuotasBlock(currentElement.getElementOptionsR().getOrder()), activity.getTree(null), titlesMap, this);
                } else {
//                    Log.d("T-A-R", "initRecyclerView: NOT isQuota");
                    adapterList = new ListAnswersAdapter(activity, currentElement, answersList,
                            null, null, titlesMap, this);
                }

                rvAnswers.setItemAnimator(null);
                rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
                rvAnswers.setAdapter(adapterList);
                break;
            case ElementSubtype.RANK:
                removeHelper();
                adapterRank = new RankQuestionAdapter(getActivity(), currentElement, answersList,
                        null, null, titlesMap, this);
                rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
                rvAnswers.setAdapter(adapterRank);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                    private boolean mOrderChanged;
                    private List<AnswerState> answers = null;

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                        int positionDragged = dragged.getAdapterPosition();
                        int positionTarget = target.getAdapterPosition();

                        answers = adapterRank.getAnswers();
                        mOrderChanged = true;
                        Collections.swap(answersList, positionDragged, positionTarget);
                        Collections.swap(answers, positionDragged, positionTarget);
                        adapterRank.notifyItemMoved(positionDragged, positionTarget);

                        return false;
                    }

                    @Override
                    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                        super.onSelectedChanged(viewHolder, actionState);

                        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                            adapterRank.setLastSelectedPosition(-1);
                            adapterRank.setAnswers(answers);
                            adapterRank.notifyDataSetChanged();
                            adapterRank.clearOldPassed();
                            mOrderChanged = false;
                        }
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                    }
                });
                itemTouchHelper.attachToRecyclerView(rvAnswers);
                break;

            case ElementSubtype.SELECT:
                removeHelper();
                if (currentElement != null && currentElement.getElementOptionsR() != null && currentElement.getElementOptionsR().isRotation()) {
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

                itemsList.clear();

                List<Integer> uncheckers = new ArrayList<>();
                for (int i = 0; i < answersList.size(); i++) {
                    itemsList.add(Objects.requireNonNull(titlesMap.get(answersList.get(i).getRelative_id())).getTitle());
                    if (answersList.get(i).getElementOptionsR().isUnchecker()) uncheckers.add(i);
                }

                if (currentElement != null && currentElement.getElementOptionsR() != null && currentElement.getElementOptionsR().isPolyanswer()) {
                    isMultiSpinner = true;
                    multiSelectionSpinner = findViewById(R.id.answers_multi_spinner);
                    multiSelectionSpinner.setVisibility(View.VISIBLE);
                    multiSelectionSpinner.setItems(itemsList);
                    if (uncheckers.size() > 0)
                        multiSelectionSpinner.hasNoneOption(true, uncheckers);
                    multiSelectionSpinner.setSelection(new int[]{});

                    multiSelectionSpinner.setListener(new MultiSelectSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                            if (isRestored) {
                                if (!indices.equals(spinnerMultipleSelection)) {
                                    try {
                                        isRestored = false;
                                        long id = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                                        getObjectBoxDao().deleteOldElementsPassedR(id);
                                        showToast(getString(R.string.data_changed));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            spinnerMultipleSelection = indices;
                            multiSelectedList = new ArrayList<>();
                            for (Integer index : spinnerMultipleSelection) {
                                multiSelectedList.add(answersList.get(index));
                            }
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {

                        }
                    });
//            ===============================================================================================
                } else {
                    isMultiSpinner = false;
                    List<Boolean> enabled = new ArrayList<>();

                    if (isQuota) {
                        List<List<Integer>> passedQuotaBlock = getMultiPassedQuotasBlock(currentElement.getElementOptionsR().getOrder());
                        ElementItemR[][] quotaTree = getMainActivity().getTree(null);

                        Integer order = currentElement.getElementOptionsR().getOrder();
                        for (ElementItemR item : answersList) {
                            enabled.add(canShow(quotaTree, passedQuotaBlock, item.getRelative_id(), order));
                        }
                    } else {
                        for (ElementItemR ignored : answersList) {
                            enabled.add(true);
                        }
                    }

                    spinnerAnswers = new SearchableSpinner(getMainActivity(), null, enabled);
                    spinnerAnswers = findViewById(R.id.answers_spinner);
                    spinnerAnswers.setVisibility(View.VISIBLE);

                    itemsList.add(getString(R.string.select_spinner));

                    adapterSpinner = new ArrayAdapter<String>(getMainActivity(), android.R.layout.simple_spinner_item, itemsList) {
                        public int getCount() {
                            return (itemsList.size() - 1);
                        }
                    };
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAnswers.setVisibility(View.VISIBLE);
                    spinnerAnswers.setEnabledList(enabled);
                    spinnerAnswers.setAdapter(adapterSpinner);
                    spinnerAnswers.setSelection(itemsList.size() - 1);
                    spinnerAnswers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long selectionId) {
                            Log.d("T-A-R.ElementFragment", "onItemSelected: SPINNER SELECTED: " + position);
                            if (position != answersList.size()) {
                                if (isRestored) {
                                    if (position != spinnerSelection) {
                                        try {
                                            isRestored = false;
                                            long id = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                                            getObjectBoxDao().deleteOldElementsPassedR(id);
                                            showToast(getString(R.string.data_changed));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                spinnerSelection = position;
                                Log.d("T-A-R.ElementFragment", "onItemSelected: spinnerSelection = " + spinnerSelection);

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            showToast(getString(R.string.enter_answer_empty));
                        }
                    });
                }
                break;
            case ElementSubtype.TABLE:
                initTable();
                break;
            case ElementSubtype.SCALE:
                removeHelper();
                adapterScale = new ScaleQuestionAdapter(getActivity(), currentElement, answersList,
                        this);
                if (isAvia())
                    rvScale.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                else
                    rvScale.setLayoutManager(new LinearLayoutManager(getContext()));
                rvScale.setAdapter(adapterScale);
                break;
        }
        st("initRecyclerView() ---");
    }

    private void removeHelper() {
        st("removeHelper() +++");
        int helperId = -1;
        for (int i = 0; i < answersList.size(); i++) {
            if (answersList.get(i).getElementOptionsR().isHelper()) {
                helperId = i;
                break;
            }
        }
        if (helperId != -1) answersList.remove(helperId);
        st("removeHelper() ---");
    }

    private void initTable() {
        st("initTable() +++");
        if (currentElement.getElementOptionsR().getTitle() == null || currentElement.getElementOptionsR().getTitle().length() == 0)
            questionTitleBox.setVisibility(View.GONE);
        adapterTable = new TableQuestionAdapter(currentElement, answersList, titlesMap, getActivity(), mRefreshRecyclerViewRunnable, this);
        tableLayout.setAdapter(adapterTable);
        tableLayout.setLongClickable(false);
        tableLayout.setDrawingCacheEnabled(true);
        st("initTable() ---");
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
        Log.d("T-A-R", "saveElement: " + answerType + " ID: " + currentElement.getRelative_id() + " JUMP: " + currentElement.getElementOptionsR().getJump());
        st("saveElement() +++");
        boolean saved = false;
        List<ElementPassedOB> elementsForSave = new ArrayList<>();
        switch (answerType) {
            case ElementSubtype.LIST:
            case ElementSubtype.QUOTA:
            case ElementSubtype.SCALE:
            case ElementSubtype.RANK: {
                Log.d("T-A-R", "saveElement passedQuotaQuestion: " + passedQuotaQuestion);
                List<AnswerState> answerStates;
                if (passedQuotaQuestion) {
                    Log.d("T-A-R", "saveElement: =================================================");
                    answerStates = new ArrayList<>();
                    for (ElementItemR item : currentElement.getElements()) {
                        if (item.getRelative_id() > 99999) {
                            answerStates.add(new AnswerState(item.getRelative_id(), true, null));
                            Log.d("T-A-R", "saveElement: ADDDDDDDDDDD PASSSED");
                            break;
                        }

                    }
                } else if (answerType.equals(ElementSubtype.SCALE)) {
                    answerStates = adapterScale.getAnswers();
                } else if (answerType.equals(ElementSubtype.RANK)) {
                    answerStates = adapterRank.getAnswers();
                } else {
                    answerStates = adapterList.getAnswers();
                }

                if (answerStates != null && notEmpty(answerStates) && answersHavePhoto(answerStates)
                        || (currentElement.getElementOptionsR().getOptional_question() != null && currentElement.getElementOptionsR().getOptional_question())) {
                    Log.d("T-A-R.ElementFragment", "saveElement: 5");
                    if (answerType.equals(ElementSubtype.RANK)) {
                        Log.d("T-A-R.ElementFragment", "saveElement: 6");
                        List<AnswerState> answerStatesRang = new ArrayList<>();
                        if (answersList != null && !answersList.isEmpty())
                            for (int i = 0; i < answersList.size(); i++) {
                                Integer id = answersList.get(i).getRelative_id();
                                String data = "";
                                for (int k = 0; k < answerStates.size(); k++) {
                                    if (id.equals(answerStates.get(k).getRelative_id())) {
                                        data = answerStates.get(k).getData();
                                    }
                                }
                                answerStatesRang.add(new AnswerState(id, true, data));
                            }
                        answerStates = answerStatesRang;
                        if (answerStates == null || answerStates.isEmpty()) {
                            Log.d("T-A-R.ElementFragment", "saveElement: 7");
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        } else if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                            Log.d("T-A-R.ElementFragment", "saveElement: 8");
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        } else {
                            Log.d("T-A-R.ElementFragment", "saveElement: 9");
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        }
                        if (nextElementId == null) {
                            Log.d("T-A-R.ElementFragment", "saveElement: 10");
                            nextElementId = answersList.get(0).getElementOptionsR().getJump();
                        }
                    } else {
                        Log.d("T-A-R.ElementFragment", "saveElement: 11");
                        if (answerStates == null || answerStates.isEmpty()) {
                            Log.d("T-A-R.ElementFragment", "saveElement: 12");
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        } else if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                            Log.d("T-A-R.ElementFragment", "saveElement: 14");
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();

                        } else {
                            boolean found = false;
                            Log.d("T-A-R.ElementFragment", "saveElement: 15");
                            for (int i = 0; i < answerStates.size(); i++) {
                                if (answerStates.get(i).isChecked()) {
                                    found = true;
                                    int id = answerStates.get(i).getRelative_id();
                                    nextElementId = getElement(id).getElementOptionsR().getJump();
                                    break;
                                }
                            }
                            if (!found) nextElementId = currentElement.getElementOptionsR().getJump();
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

//                    getDao().insertElementPassedR(elementPassedR);
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
//                                getDao().insertElementPassedR(answerPassedR);
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
                break;
            }
            case ElementSubtype.SELECT: {
                if (passedQuotaQuestion) {
                    Log.d("T-A-R", "saveElement: =================================================");

                    for (ElementItemR item : currentElement.getElements()) {
                        if (item.getRelative_id() > 99999) {
                            ElementPassedOB answerPassedR = new ElementPassedOB();
                            answerPassedR.setRelative_id(item.getRelative_id());
                            answerPassedR.setParent_id(getElement(item.getRelative_id()).getRelative_parent_id());
                            answerPassedR.setProject_id(currentElement.getProjectId());
                            answerPassedR.setToken(getQuestionnaire().getToken());
                            answerPassedR.setFrom_quotas_block(true);
                            answerPassedR.setIs_question(false);

                            try {
                                if (!isRestored) {
                                    elementsForSave.add(answerPassedR);
                                }
                                saved = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                st("saveElement().5 ---");
                                Log.d("T-A-R.ElementFragment", "saveElement (3): false");
                                return false;
                            }
                            break;
                        }

                    }

                } else if (isMultiSpinner) {
                    Log.d("T-A-R.ElementFragment", "saveElement: isMultiSpinner");
                    if (checkMultipleSpinner()) {
                        ElementPassedOB elementPassedR = new ElementPassedOB();
                        if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {

                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        } else
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                            if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                    && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                                    && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                                    && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {

                                nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                            } else
                                nextElementId = currentElement.getElementOptionsR().getJump();
                            if (nextElementId == null || nextElementId.equals(-2)) {
                                nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                            }
                        }
                        if (currentElement.getElementOptionsR().getOptional_question() != null && currentElement.getElementOptionsR().getOptional_question()
                                && (spinnerMultipleSelection == null || spinnerMultipleSelection.isEmpty() || spinnerMultipleSelection.get(0) < 0)) {
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        } else if (nextElementId == null) {
                            nextElementId = answersList.get(spinnerMultipleSelection.get(0)).getElementOptionsR().getJump();
                        }
                        elementPassedR.setRelative_id(currentElement.getRelative_id());
                        elementPassedR.setProject_id(currentElement.getProjectId());
                        elementPassedR.setToken(getQuestionnaire().getToken());
                        elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
                        elementPassedR.setIs_question(true);

                        try {
                            if (!isRestored) {
//                                getDao().insertElementPassedR(elementPassedR);
                                elementsForSave.add(elementPassedR);
                                setCondComp(currentElement.getRelative_id());
                                getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                            }
                            saved = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            saved = false;
                            st("saveElement().2 ---");
                            Log.d("T-A-R.ElementFragment", "saveElement (4): " + saved);
                            return saved;
                        }

                        if (spinnerMultipleSelection != null && spinnerMultipleSelection.size() > 0) {
                            for (int i = 0; i < spinnerMultipleSelection.size(); i++) {

                                ElementPassedOB answerPassedR = new ElementPassedOB();
                                answerPassedR.setRelative_id(answersList.get(spinnerMultipleSelection.get(i)).getRelative_id());
                                answerPassedR.setProject_id(currentElement.getProjectId());
                                answerPassedR.setToken(getQuestionnaire().getToken());
                                answerPassedR.setIs_question(false);


                                try {
                                    if (!isRestored) {
//                                    getDao().insertElementPassedR(answerPassedR);
                                        elementsForSave.add(answerPassedR);
                                    }
                                    saved = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    saved = false;
                                    st("saveElement().3 ---");
                                    Log.d("T-A-R.ElementFragment", "saveElement (5): " + saved);
                                    return saved;
                                }
                            }
                        }
                    }

                } else {
                    Log.d("T-A-R.ElementFragment", "saveElement: Spinner: " + spinnerSelection);
                    if (spinnerSelection != -1 || (currentElement.getElementOptionsR().getOptional_question() != null && currentElement.getElementOptionsR().getOptional_question())) {
                        ElementPassedOB elementPassedR = new ElementPassedOB();
                        if (spinnerSelection == -1 && currentElement.getElementOptionsR().getOptional_question() != null && currentElement.getElementOptionsR().getOptional_question()) {
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        } else if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        } else
                            nextElementId = answersList.get(spinnerSelection).getElementOptionsR().getJump();
                        if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                            if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                    && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                                    && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                                    && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                                nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                            } else
                                nextElementId = currentElement.getElementOptionsR().getJump();
                            if (nextElementId == null) {
                                nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                            } else if (nextElementId.equals(-2)) {
                                nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                            }
                        }

                        elementPassedR.setRelative_id(currentElement.getRelative_id());
                        elementPassedR.setProject_id(currentElement.getProjectId());
                        elementPassedR.setToken(getQuestionnaire().getToken());
                        elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
                        elementPassedR.setIs_question(true);

                        try {
                            if (!isRestored) {
//                                getDao().insertElementPassedR(elementPassedR);
                                elementsForSave.add(elementPassedR);
                                setCondComp(currentElement.getRelative_id());
                                getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                            }
                            saved = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            st("saveElement().4 ---");
                            Log.d("T-A-R.ElementFragment", "saveElement (6): false");
                            return false;
                        }

                        if (spinnerSelection >= 0) {
                            Log.d("T-A-R.ElementFragment", "saveElement: SPINNER ANSWERS");
                            ElementPassedOB answerPassedR = new ElementPassedOB();
                            answerPassedR.setRelative_id(answersList.get(spinnerSelection).getRelative_id());
                            answerPassedR.setParent_id(getElement(answersList.get(spinnerSelection).getRelative_id()).getRelative_parent_id());
                            answerPassedR.setProject_id(currentElement.getProjectId());
                            answerPassedR.setToken(getQuestionnaire().getToken());
                            answerPassedR.setFrom_quotas_block(isQuota);
                            answerPassedR.setIs_question(false);

                            try {
                                if (!isRestored) {
//                                getDao().insertElementPassedR(answerPassedR);
                                    elementsForSave.add(answerPassedR);
                                }
                                saved = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                st("saveElement().5 ---");
                                Log.d("T-A-R.ElementFragment", "saveElement (7): false");
                                return false;
                            }
                        } else Log.d("T-A-R.ElementFragment", "saveElement: SPINNER ANSWERS EMPTY !!!");
                    }
                }
                break;
            }
            case ElementSubtype.TABLE: {
                AnswerState[][] answerStates = adapterTable.getmAnswersState();
                if (answerStates != null && answerStates[0] != null) {
                    for (int i = 0; i < answerStates.length; i++) {
                        String text = i + ": ";
                        for (int k = 0; k < answerStates[0].length; k++) {
                            text = text.concat(answerStates[i][k].getRelative_id() + "/" + answerStates[i][k].isChecked() + " ");
                        }
                    }
                }
                Log.d("T-A-R.ElementFragment", "saveElement: check TABLE");
                if (adapterTable.isCompleted()) {
                    if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                            && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                            && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                            && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                        nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                    } else if (currentElement.getElementOptionsR().getJump() != null)
                        nextElementId = currentElement.getElementOptionsR().getJump();
//                    else
//                        nextElementId = getElement(answerStates[0][0].getRelative_id()).getElementOptionsR().getJump();

                    if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                        if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                                && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        } else
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        if (nextElementId == null || nextElementId == -2) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        }
                    }
                    if (nextElementId == null) {
                        try {
                            nextElementId = getElement(answerStates[0][0].getRelative_id()).getElementOptionsR().getJump();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (nextElementId == null) nextElementId = 0;

                    ElementPassedOB elementPassedR = new ElementPassedOB();
                    elementPassedR.setRelative_id(currentElement.getRelative_id());
                    elementPassedR.setProject_id(currentElement.getProjectId());
                    elementPassedR.setToken(getQuestionnaire().getToken());
                    elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
                    elementPassedR.setIs_question(true);
                    elementPassedR.setCard_showed(currentElement.getShowed_in_card());

                    try {
                        if (!isRestored) {
//                            getDao().insertElementPassedR(elementPassedR);
                            elementsForSave.add(elementPassedR);
                            setCondComp(currentElement.getRelative_id());
                            getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                        }
                        saved = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        saved = false;
                        st("saveElement().6 ---");
                        Log.d("T-A-R.ElementFragment", "saveElement (8): " + saved);
                        return saved;
                    }

                    if (answerStates != null && answerStates.length > 0)
                        for (int i = 0; i < answerStates.length; i++) {
                            for (int k = 0; k < answerStates[i].length; k++) {
                                if (answerStates[i][k].isChecked()) {
                                    ElementPassedOB answerPassedR = new ElementPassedOB();
                                    answerPassedR.setRelative_id(answerStates[i][k].getRelative_id());
                                    answerPassedR.setValue(answerStates[i][k].getData());
                                    answerPassedR.setProject_id(currentElement.getProjectId());
                                    answerPassedR.setToken(getQuestionnaire().getToken());
                                    answerPassedR.setIs_question(false);
                                    answerPassedR.setChecked_in_card(answerStates[i][k].isCheckedInCard());
                                    try {
                                        answerPassedR.setHelper(getElement(answerStates[i][k].getRelative_id()).getElementOptionsR().isHelper());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        if (!isRestored) {
                                            elementsForSave.add(answerPassedR);
//                                        getDao().insertElementPassedR(answerPassedR);
                                        }
                                        saved = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        saved = false;
                                        st("saveElement().7 ---");
                                        Log.d("T-A-R.ElementFragment", "saveElement (9): " + saved);
                                        return saved;
                                    }
                                }
                            }
                        }
                }
                break;
            }
            case ElementSubtype.HTML:
            case ElementSubtype.END:
                ElementPassedOB elementPassedR = new ElementPassedOB();
                if (answerType.equals(ElementSubtype.END)) {
                    if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                            && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                            && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                            && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                        nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                    } else
                        nextElementId = currentElement.getElementOptionsR().getType_end();
                } else {
                    if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                            && getElement(currentElement.getRelative_parent_id()).getType().equals(ElementType.BOX)
                            && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion() != null
                            && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getShowRandomQuestion()) {
                        nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                    } else
                        nextElementId = currentElement.getElementOptionsR().getJump();
                    if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                        nextElementId = currentElement.getElementOptionsR().getJump();
                        if (nextElementId == null) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        } else if (nextElementId == -2) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        }
                    }

                    if (nextElementId > 0) {
                        ElementItemR nextElement = getElement(nextElementId);
                        final ElementOptionsR options = nextElement.getElementOptionsR();
                        final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), (MainActivity) getActivity());

                        if (showValue != ConditionUtils.CAN_SHOW) {
                            nextElementId = options.getJump();
                        }
                    }

                }

                elementPassedR.setRelative_id(currentElement.getRelative_id());
                elementPassedR.setProject_id(currentElement.getProjectId());
                elementPassedR.setToken(getQuestionnaire().getToken());
                elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
                elementPassedR.setIs_question(true);

                try {
                    if (!isRestored) {
//                        getDao().insertElementPassedR(elementPassedR);
                        elementsForSave.add(elementPassedR);
                        setCondComp(currentElement.getRelative_id());
                        getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                    }
                    saved = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    saved = false;
                    st("saveElement().8 ---");
                    Log.d("T-A-R.ElementFragment", "saveElement (10): " + saved);
                    return saved;
                }
                break;
        }
        if (!elementsForSave.isEmpty()) {
//            List<ElementPassedOB> list = new ArrayList<>();
//            MapperQuizer mapper = new MapperQuizer();
//            for(ElementPassedR element : elementsForSave) {
//                list.add(mapper.mapElementPassedOB(element));
//            }
//            getDao().insertElementPassedR(elementsForSave);
            Log.d("T-A-R.TransFragment", ">>> savePassedElement 7: " + new Gson().toJson(elementsForSave));
            getObjectBoxDao().insertElementPassedR(elementsForSave);
        } else Log.d("T-A-R.ElementFragment", ">>> saveElement: elementsForSave.isEmpty()");
        st("saveElement().9 ---");
        Log.d("T-A-R.ElementFragment", "nextElementId: " + nextElementId);
        Log.d("T-A-R.ElementFragment", "saveElement (11): " + saved);
        return saved;
    }

    private boolean checkMultipleSpinner() {
        st("checkMultipleSpinner() +++");
        if (currentElement.getElementOptionsR().getOptional_question() != null && currentElement.getElementOptionsR().getOptional_question()) {
            return true;
        }
        if (multiSelectedList == null || multiSelectedList.size() < 1) {
            return false;
        }
        if (currentElement.getElementOptionsR().getMin_answers() != null) {
            for (ElementItemR element : multiSelectedList) {
                Log.d("T-A-R.ElementFragment", ">>>>>>> checkMultipleSpinner: " + element.getRelative_id());
                if (element.getElementOptionsR() != null && element.getElementOptionsR().isUnchecker()) {
                    return true;
                }
            }
            if (multiSelectedList.size() < currentElement.getElementOptionsR().getMin_answers()) {
                showToast("Выберите минимум " + currentElement.getElementOptionsR().getMin_answers() + " ответов");
                return false;
            }
        }
        if (currentElement.getElementOptionsR().getMax_answers() != null) {
            if (multiSelectedList.size() > currentElement.getElementOptionsR().getMax_answers()) {
                showToast("Выберите максимум " + currentElement.getElementOptionsR().getMax_answers() + " ответов");
                return false;
            }
        }
        return true;
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

    //TODO FOR TESTS!
    public void showPassed() {
//        List<ElementPassedR> elementPassedRS = getDao().getAllElementsPassedR(getQuestionnaire().getToken());
        List<ElementPassedOB> elementPassedRS = getObjectBoxDao().getAllElementsPassedR(getQuestionnaire().getToken());
        Log.d(TAG, "==========================================");
        for (ElementPassedOB element : elementPassedRS) {
            Log.d(TAG, ">>>>>>>>>>>>>>> showPassed: " + element.getId());
        }
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
        st("onAnswerClick().1 +++");
        st("ADAPTER CLICK 3:");
        clearSaved(false);
        st("ADAPTER CLICK 4:");
        st("onAnswerClick() ---");
    }

    @Override
    public void onAnswerClick(int row, int column) {
        st("onAnswerClick().2 +++");
        clearSaved(false);
        st("onAnswerClick() ---");
    }

    private void clearSaved(boolean force) {
        st("clearSaved() +++");
        if (isRestored || force) {
            new Thread(() -> {
                try {
                    isRestored = false;
                    long id = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                    getObjectBoxDao().deleteOldElementsPassedR(id);
                    if (!force)
                        getMainActivity().runOnUiThread(() -> showToast(getString(R.string.data_changed)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        st("clearSaved() ---");
    }

    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
        @Override
        public void run() {
            UiUtils.hideKeyboard(getContext(), getView());
        }
    };

    public void loadResumedData() {
        st("loadResumedData() +++");
        if (isRestored) {
            try {
                if (prevList != null && prevList.size() > 0) {
                    PrevElementsO lastPassedElement = prevList.get(prevList.size() - 1);
                    startElementId = lastPassedElement.getNextId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        st("loadResumedData() ---");
    }

    public void loadSavedData() {
        st("loadSavedData() +++");
        switch (answerType) {
            case ElementSubtype.LIST:
            case ElementSubtype.QUOTA: {
                List<AnswerState> answerStatesAdapter = adapterList.getAnswers();
                List<AnswerState> answerStatesRestored = new ArrayList<>();
                int lastSelectedPosition = -1;
                for (int i = 0; i < answerStatesAdapter.size(); i++) {
                    AnswerState answerStateNew = new AnswerState();
                    ElementPassedOB answerStateRestored = null;
                    try {
                        answerStateRestored = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), answerStatesAdapter.get(i).getRelative_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (answerStateRestored != null || adapterList.isAutoChecked(i)) {
                        answerStateNew.setChecked(true);
                        answerStateNew.setData(answerStateRestored != null ? answerStateRestored.getValue() : "");
                        lastSelectedPosition = i;
                    } else {
                        answerStateNew.setChecked(false);
                        answerStateNew.setData("");
                    }

                    answerStateNew.setRelative_id(answerStatesAdapter.get(i).getRelative_id());
                    answerStatesRestored.add(answerStateNew);
                }
                adapterList.setAnswers(answerStatesRestored);
                adapterList.setRestored(true);
                adapterList.notifyDataSetChanged();
                break;
            }
            case ElementSubtype.RANK: {
                List<ElementPassedOB> elementPassedRList = new ArrayList<>();
                List<AnswerState> answerStatesRestored = new ArrayList<>();
                List<ElementItemR> answerListRestored = new ArrayList<>();
                for (int i = 0; i < answersList.size(); i++) {
                    ElementPassedOB answerPassedRestored;
                    answerPassedRestored = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), answersList.get(i).getRelative_id());
                    if (answerPassedRestored != null) {
                        elementPassedRList.add(answerPassedRestored);
                    }
                }
                if (elementPassedRList.size() > 0) {
                    Collections.sort(elementPassedRList, (lhs, rhs) -> lhs.getRank().compareTo(rhs.getRank()));

                    for (ElementPassedOB itemR : elementPassedRList) {
                        answerStatesRestored.add(new AnswerState(itemR.getRelative_id(), true, itemR.getValue()));
                        for (int i = 0; i < answersList.size(); i++) {
                            if (answersList.get(i).getRelative_id().equals(itemR.getRelative_id())) {
                                answerListRestored.add(answersList.get(i));
                            }
                        }
                    }
                    answersList = answerListRestored;
                    adapterRank.setData(answersList);
                    adapterRank.setAnswers(answerStatesRestored);
                    adapterRank.setRestored(true);
                    adapterRank.notifyDataSetChanged();
                }
                break;
            }
            case ElementSubtype.SELECT:

                spinnerSelection = -1;
                spinnerMultipleSelection = new ArrayList<>();

                for (int i = 0; i < answersList.size(); i++) {
                    ElementPassedOB answerStateRestored = null;
                    try {
                        answerStateRestored = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), answersList.get(i).getRelative_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (answerStateRestored != null) {
                        if (isMultiSpinner) {
                            spinnerMultipleSelection.add(i);
                        } else {
                            spinnerSelection = i;
                            spinnerAnswers.setSelection(spinnerSelection);
                        }
                    }

                }
                if (isMultiSpinner) {
                    int[] array = new int[spinnerMultipleSelection.size()];
                    for (int i = 0; i < spinnerMultipleSelection.size(); i++) {
                        array[i] = spinnerMultipleSelection.get(i);
                    }
                    multiSelectionSpinner.setSelection(array);
                }
                break;
            case ElementSubtype.TABLE:
                AnswerState[][] answersTableState = adapterTable.getmAnswersState();

                for (int i = 0; i < answersTableState.length; i++) {
                    for (int k = 0; k < answersTableState[i].length; k++) {
                        ElementPassedOB answerStateRestored = null;
                        try {
                            answerStateRestored = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), answersTableState[i][k].getRelative_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (answerStateRestored != null) {
                            answersTableState[i][k].setChecked(true);
                            answersTableState[i][k].setData(answerStateRestored.getValue());
                        }
                    }
                }

                adapterTable.setmAnswersState(answersTableState);
                break;
            case ElementSubtype.SCALE: {
                List<AnswerState> answerStatesAdapter = adapterScale.getAnswers();
                List<AnswerState> answerStatesRestored = new ArrayList<>();
                int lastSelectedPosition = 0;
                for (int i = 0; i < answerStatesAdapter.size(); i++) {
                    AnswerState answerStateNew = new AnswerState();
                    ElementPassedOB answerStateRestored = null;
                    try {
                        answerStateRestored = getObjectBoxDao().getElementPassedR(getQuestionnaire().getToken(), answerStatesAdapter.get(i).getRelative_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (answerStateRestored != null) {
                        answerStateNew.setChecked(true);
                        answerStateNew.setData(answerStateRestored.getValue());
                        lastSelectedPosition = i;
                    } else {
                        answerStateNew.setChecked(false);
                        answerStateNew.setData("");
                    }

                    answerStateNew.setRelative_id(answerStatesAdapter.get(i).getRelative_id());
                    answerStatesRestored.add(answerStateNew);
                }
                adapterScale.setAnswers(answerStatesRestored);
                adapterScale.setLastSelectedPosition(lastSelectedPosition);
                adapterScale.notifyDataSetChanged();
                break;
            }
        }
        st("loadSavedData() ---");
    }

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
        if (canBack && canGoBack)
            onClick(btnPrev);
        return true;
    }

    public boolean saveQuestionnaire(boolean aborted) {
        st("saveQuestionnaire() +++");
//        showScreensaver("Идет сохрание анкеты", true);

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
            Log.d("T-A-R.", "CLEAR Questionnaire: 10");
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
                    Log.d("T-A-R.", "CLEAR Questionnaire: 9");
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
                Log.d("T-A-R.", "CLEAR Questionnaire: 8");
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
//                                progressBar.setVisibility(View.GONE);
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
//        Log.d("T-A-R", "<<<<< deactivateButtons: ");
        final MainActivity activity = getMainActivity();

        if (activity != null) {
            try {
//                progressBar.setVisibility(View.VISIBLE);
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

    private void showInfoDialog() {
        st("showInfoDialog() +++");
        dialogBuilder = new AlertDialog.Builder(getMainActivity());
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_info_auto : R.layout.dialog_info, null);
        TextView dQuota1 = layoutView.findViewById(R.id.quota_1);
        TextView dQuota2 = layoutView.findViewById(R.id.quota_2);
        TextView dQuota3 = layoutView.findViewById(R.id.quota_3);
        LinearLayout cont = layoutView.findViewById(R.id.cont);

        cont.setOnClickListener(v -> {
            st("showInfoDialog() ---");
            try {
                infoDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        dQuota1.setTextColor(getResources().getColor(R.color.black));
        dQuota2.setTextColor(getResources().getColor(R.color.black));
        dQuota1.setText(getString(R.string.label_login, getMainActivity().getCurrentUser().getLogin()));
        dQuota2.setText(getString(R.string.label_inter, getUserName()));
        UiUtils.setTextOrHide(dQuota3, getString(R.string.label_project, getMainActivity().getConfig().getProjectInfo().getName()));

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        try {
            infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing())
            infoDialog.show();
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
                        if (answerType.equals(ElementSubtype.END)) {
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        }
                        Log.d("T-A-R.ElementFragment", "?????????: 1");
                        // Ротация =======================================================================================================================
                        try {
                            if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0
                                    && checkRotationBox(currentElement.getRelative_parent_id(), currentElement.getRelative_id())) {
                                Log.d("T-A-R.ElementFragment", "?????????: 2");
                                nextElementId = getJumpInRotationBox(currentElement.getRelative_parent_id(), currentElement.getRelative_id());
                            } else {
                                Log.d("T-A-R.ElementFragment", "?????????: 2.1");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // ===============================================================================================================================
                        Log.d("T-A-R.ElementFragment", "?????????: 2.2");
                        if (nextElementId == null) {
                            Log.d("T-A-R.ElementFragment", "?????????: 3");
                            showRestartDialog();
                        } else if (nextElementId == 0) {
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
                try {
                    if (currentElement.getRelative_parent_id() != null
                            && currentElement.getRelative_parent_id() != 0
                            && checkRotationBox(currentElement.getRelative_parent_id(), currentElement.getRelative_id())) {
                        nextElementId = getJumpInRotationBox(currentElement.getRelative_parent_id(), currentElement.getRelative_id());
                        Log.d("T-A-R.ElementFragment", "Next Rotation Jump (1): " + nextElementId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (isRandomBox) try {
//                    nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
//                    Log.d("T-A-R.ElementFragment", "Next Rotation Jump: " + nextElementId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                if (nextElementId != null && !nextElementId.equals(0) && !nextElementId.equals(-1)) {
                    Log.d("T-A-R.ElementFragment", "??? checkAndLoadNext: 1 : " + nextElementId);
                    if (checkConditions(getElement(nextElementId))) {
                        Log.d("T-A-R.ElementFragment", "??? checkAndLoadNext: 2");
                        st("checkAndLoadNext() 1: +++");

                        checkHidden();
                        Log.d("T-A-R.ElementFragment", "??? checkAndLoadNext: 3");
                        st("checkAndLoadNext() 2: +++");

                        if (nextElementId == 0) {
                            if (saveQuestionnaire(false)) {
                                exitQuestionnaire();
                            } else {
                                activateButtons();
                            }
                        } else if (nextElementId == -1) {
                            if (getMainActivity().getConfig().isSaveAborted()) {
                                if (saveQuestionnaire(true)) {
                                    exitQuestionnaire();
                                } else {
                                    activateButtons();
                                }
                            } else {
                                exitQuestionnaire();
                            }
                        } else if (!isInHiddenQuotaDialog) {
                            if (!isFragmentLoading) {
                                isFragmentLoading = true;
                                Integer startId;
                                Log.d("T-A-R", "passedQuotaQuestion: " + passedQuotaQuestion);
                                Log.d("T-A-R.ElementFragment", "SAVE IN CHECKANDLOADNEXT: ");
                                if (passedQuotaQuestion) saveElement();
                                st("checkAndLoadNext() 2: +++");
                                if (answerType != null && answerType.equals(ElementSubtype.HIDDEN)) startId = hiddenId;
                                else startId = currentElement.getRelative_id();
                                transfer(startId, nextElementId, false);
                            }
                        }
                    } else if (currentElement.getRelative_parent_id() != null
                            && currentElement.getRelative_parent_id() != 0
                            && checkRotationBox(currentElement.getRelative_parent_id(), currentElement.getRelative_id())) {
                        currentElement = getElement(nextElementId);
                        checkAndLoadNext();
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
                        if (getMainActivity().getConfig().isSaveAborted()) {
                            if (saveQuestionnaire(true)) {
                                exitQuestionnaire();
                            } else {
                                activateButtons();
                            }
                        } else {
                            exitQuestionnaire();
                        }
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
        if (currentElement.getSubtype().equals(ElementSubtype.TABLE)) {
            List<CardItem> items = new ArrayList<>();
            for (ElementItemR element : answersList) {
                if (element.getElementOptionsR() != null && element.getElementOptionsR().isShow_in_card()) {
                    CardItem item = new CardItem();
                    item.setTitle(counter + ". " + titlesMap.get(element.getRelative_id()).getTitle());
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

            TableCardAdapter adapter = new TableCardAdapter(getMainActivity(), getMainActivity().isAutoZoom() ? R.layout.holder_table_card_auto : R.layout.holder_table_card, items);

            listView.setAdapter(adapter);
            mCloseBtn.setOnClickListener(v -> {
                infoDialog.dismiss();
            });
        } else if (currentElement.getSubtype().equals(ElementSubtype.LIST)) {
            List<CardItem> items = new ArrayList<>();
            Map<Integer, AnswerState> answerStateMap = convertAnswersListToMap(adapterList.getAnswers());

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
                clearSaved(false);
                loadFromCard(adapter.getItems());
                infoDialog.dismiss();
            });
        } else {
            List<CardItem> items = new ArrayList<>();
            Map<Integer, AnswerState> answerStateMap = convertAnswersListToMap(adapterList.getAnswers());
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
                clearSaved(false);
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

    public void showRestartDialog() {
        MainActivity activity = getMainActivity();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (!activity.isFinishing()) {
                    new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                            .setCancelable(false)
                            .setTitle("Ошибка обработки анкеты.")
                            .setMessage("Продолжить с перезагрузкой приложения или попробовать продолжить без перезапуска?")
                            .setPositiveButton("Продолжить", (dialog, which) -> {
                                dialog.dismiss();
                                getDao().insertCrashLog(new CrashLogs(DateUtils.getCurrentTimeMillis(), "Ошибка обработки анкеты. NextElementId не обнаружен.", true));
                                replaceFragment(new HomeFragment());
                            })
                            .setNegativeButton("Перезапустить", (dialog, which) -> {
                                dialog.dismiss();
                                getDao().insertCrashLog(new CrashLogs(DateUtils.getCurrentTimeMillis(), "Ошибка обработки анкеты. NextElementId не обнаружен.", true));
                                activity.restartActivity();
                            })
                            .show();
                }
            }
        });
    }

    public boolean canShow(ElementItemR[][] tree, List<List<Integer>> passedElementsId, Integer relativeId, Integer order) {
        st("canShow() +++");
        if (tree == null || order == null || relativeId == null) {
            st("canShow().1 ---");
            return true;
        }

        if (order == 1) {
            for (int k = 0; k < tree[0].length; k++) {
                if (tree[0][k].getRelative_id().equals(relativeId)) {
                    if (tree[0][k].isEnabled()) {
                        st("canShow().2 ---");
                        return true;
                    }
                }
            }
            st("canShow().3 ---");
            return false;
        } else {
            int endPassedElement = order - 1;

            for (int k = 0; k < tree[0].length; k++) {
                for (int i = 0; i < endPassedElement; ) {
                    if (passedElementsId.get(i).contains(tree[i][k].getRelative_id())) {
                        if (i == (endPassedElement - 1)) { // Если последний, то
                            if (tree[i + 1][k].getRelative_id().equals(relativeId)) { // Если следующий за последним равен Relative ID
                                if (tree[i + 1][k].isEnabled()) {
                                    st("canShow().4 ---");
                                    return true;
                                }
                            }
                        }
                        i++;
                    } else {
                        break;
                    }
                }
            }
        }
        st("canShow().5 ---");
        return false;
    }

    public boolean canShowFromMapBlock(ElementItemR[][] tree, Map<Integer, List<Integer>> passedElementsId, Integer relativeId, Integer order) {
        st("canShowFromMapBlock() +++");
//        Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: " + (tree != null) + " / " + (passedElementsId != null) + " / " + passedElementsId.size() + " / " + relativeId + " / " + order);
        if (tree == null || order == null || relativeId == null) {
            st("canShowFromMapBlock().1 ---");
            return true;
        }

        if (order == 1) {
//            Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: 1");
            for (int k = 0; k < tree[0].length; k++) {
                if (tree[0][k].getRelative_id().equals(relativeId)) {
                    if (tree[0][k].isEnabled()) {
                        st("canShowFromMapBlock().2 ---");
                        return true;
                    }
                }
            }
//            Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: 2");
            st("canShowFromMapBlock().3 ---");
            return false;
        } else {
            if (relativeId == 14) Log.d("T-A-R.ElementFragment", "??? 14 order: " + order);
//            Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: 3 : " + tree[0].length + "." + tree.length + "/" + (order - 1));
            int endPassedElement = order - 1;

            for (int k = 0; k < tree[0].length; k++) {

//                Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: 3.1: " + k);
                for (int i = 0; i < endPassedElement; ) {
//                    Log.d("T-A-R.ElementFragment", "??? canShowFromMapBlock: " + k +"/" + i + " : " + passedElementsId.get(i));
                    boolean equals = false;
                    if (passedElementsId.get(i) != null && passedElementsId.get(i).size() > 0) {
                        for (Integer passedId : passedElementsId.get(i)) {

                            if (tree[i][k].getRelative_id().equals(passedId)) {
                                if (i == (endPassedElement - 1)) { // Если последний, то
                                    if (tree[i + 1][k].getRelative_id().equals(relativeId)) { // Если следующий за последним равен Relative ID
//                                        if((i+1) < tree.length)
//                                        for()
                                        if (tree[i + 1][k].isEnabled() && !tree[i + 1][k].getElementOptionsR().isHelper()) {
//                                            st("canShowFromMapBlock().4 ---");
                                            if (relativeId == 14) {
                                                Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: TRUE " + (i + 1) + "/" + tree[i + 1][k].getRelative_id());
                                                try {
                                                    Log.d("T-A-R.ElementFragment", "1:" + tree[0][k].getRelative_id() + "." + tree[0][k].isEnabled() + " 2:" + tree[1][k].getRelative_id() + "." + tree[1][k].isEnabled() + " 3:" + tree[2][k].getRelative_id() + "." + tree[2][k].isEnabled() + " 4:" + tree[3][k].getRelative_id() + "." + tree[3][k].isEnabled() + " 5:" + tree[4][k].getRelative_id() + "." + tree[4][k].isEnabled() + " 6:" + tree[5][k].getRelative_id() + "." + tree[5][k].isEnabled());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            return true;
                                        }
                                    }
                                }
                                i++;
                                equals = true;
                            } else {
//                                Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: BREAK 1");
                                break;
                            }
                        }
                        if (!equals) {
//                            Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: BREAK 2");
                            break;
                        }
                    }
                }
            }
//            Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: 4");
        }
//        Log.d("T-A-R.ElementFragment", "canShowFromMapBlock: 5");
        st("canShowFromMapBlock().5 ---");
        return false;
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
        spinnerCont = null;
        infoCont = null;
        tableCont = null;
        tvTitle1 = null;
        tvTitle2 = null;
        tvQuestion = null;
        tvTitleDesc1 = null;
        tvTitleDesc2 = null;
        tvQuestionDesc = null;
        infoText = null;
        rvAnswers = null;
        rvScale = null;
        spinnerAnswers = null;
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
        adapterRank = null;
        adapterScale = null;
        adapterSpinner = null;
        adapterTable = null;
        multiSelectionSpinner = null;
    }

    private void checkHidden() {
        Log.d("T-A-R.ElementFragment", "checkHidden: 1");
        st("checkHidden() +++");
        ElementItemR nextElement = null;
        if (answerType == null || answerType.equals(ElementSubtype.HIDDEN)) nextElement = currentElement;
        else nextElement = getElement(nextElementId);
        Log.d("T-A-R.ElementFragment", "checkHidden: 2");
        if (nextElement.getSubtype().equals(ElementSubtype.HIDDEN)) {
            Log.d("T-A-R.ElementFragment", "checkHidden: 3");
            boolean saved = false;
            List<AnswerState> answerStatesHidden = new ArrayList<>();
            for (PrevElementsO prevElement : prevList) {
                if (prevElement.getPrevId().equals(nextElementId)) {
                    saved = true;
                    break;
                }
            }
            Log.d("T-A-R.ElementFragment", "checkHidden: 4");
            if (!saved) {
                Log.d("T-A-R.ElementFragment", "checkHidden: 5");
                List<ElementItemR> answers = nextElement.getElements();
                List<ElementItemR> answersHidden = new ArrayList<>();
                ExpressionUtils expressionUtils = new ExpressionUtils(getMainActivity());
                boolean hiddenInQuotaBox = false;
                int enabledCounter = 0;
                int canShowCounter = 0;

                //=========================================================================================================
                if (nextElement.getRelative_parent_id() != null && nextElement.getRelative_parent_id() != 0 &&
                        getElement(nextElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
                    Log.d("T-A-R.ElementFragment", "checkHidden: 6");
                    hiddenInQuotaBox = true;
                    Map<Integer, List<Integer>> passedQuotaBlock = getPassedQuotasMap(nextElement.getElementOptionsR().getOrder());
                    Log.d("T-A-R.ElementFragment", "checkHidden: 6.1");
                    ElementItemR[][] quotaTree = getMainActivity().getTree(null);
                    Log.d("T-A-R.ElementFragment", "checkHidden: 6.2");
                    Integer order = nextElement.getElementOptionsR().getOrder();
                    Log.d("T-A-R.ElementFragment", "checkHidden: 6.3");
                    for (int index = 0; index < answers.size(); index++) {
                        Boolean enabled = canShowFromMapBlock(quotaTree, passedQuotaBlock, answers.get(index).getRelative_id(), order);
                        Log.d("T-A-R.ElementFragment", "checkHidden: 6.4: " + answers.get(index).getRelative_id() + " / " + enabled);
                        answers.get(index).setEnabled(enabled);
                    }
                }
                Log.d("T-A-R.ElementFragment", "checkHidden: 7");
                for (ElementItemR answer : answers) {
                    Log.d("T-A-R.ElementFragment", "checkHidden: 7.1 - " + answer.getRelative_id());
                    if (answer.isEnabled()) {
                        Log.d("T-A-R.ElementFragment", "checkHidden: 7.3 - " + answer.getRelative_id());
                        canShowCounter++;
                    }
                    String expression = answer.getElementOptionsR().getPrev_condition();

                    if (expression == null || expression.length() == 0 || expressionUtils.checkHiddenExpression(expression)) {
                        answer.setChecked(true);
                        if (hiddenInQuotaBox) {
                            if (!answer.getElementOptionsR().isHelper()) {
                                answersHidden.add(answer);
                                if (answer.isEnabled()) {
                                    Log.d("T-A-R.ElementFragment", "checkHidden: 7.2 - " + answer.getRelative_id());
                                    enabledCounter++;
                                }
                            }
                        }
                        answerStatesHidden.add(new AnswerState(answer.getRelative_id(), answer.isChecked(), true, answer.getElementOptionsR().getTitle(), answer.isEnabled()));

                    }
                }
                Log.d("T-A-R.ElementFragment", "checkHidden: 8");


                //TODO Если все выбранные квотные в скрытом закрыты, то не пускаем далее. Если все не выбраны то пускаем далее


                if (hiddenInQuotaBox) {
                    Log.d("T-A-R.ElementFragment", "checkHidden: 9");
                    boolean hasQuota = false;
                    try {
                        for (QuotaModel quota : getMainActivity().quotas) {
                            Set<Integer> idList = quota.getSet();
                            if (idList.contains(nextElement.getRelative_id())) {
                                hasQuota = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("T-A-R.ElementFragment", "checkHidden: 10 - " + answersHidden.size() + " / " + enabledCounter);

                    if (answersHidden.size() == 1 && enabledCounter == 0) {
                        Log.d("T-A-R.ElementFragment", "checkHidden: 11");
                        String message = "Квота по варианту ответа \u2022 \"" + answersHidden.get(0).getElementOptionsR().getTitle() + "\" закончилась";
                        showScreensaver(true);
                        isInHiddenQuotaDialog = true;
                        showHiddenExitAlertDialog(message);
                    } else if (answersHidden.size() != enabledCounter) {
                        Log.d("T-A-R.ElementFragment", "checkHidden: 12");
                        String message = "";
                        for (ElementItemR answer : answersHidden) {
                            if (answer.isEnabled())
                                message = message + "\u2022 \"" + answer.getElementOptionsR().getTitle() + "\" - будет выбран\n";
                            else
                                message = message + "\u2022 \"" + answer.getElementOptionsR().getTitle() + "\" квота закончилась - не будет выбран\n";
                        }
                        isInHiddenQuotaDialog = true;
                        if (enabledCounter == 0) {
                            Log.d("T-A-R.ElementFragment", "checkHidden: 14");
                            showScreensaver(true);
                            showHiddenExitAlertDialog(message);
                        } else {
                            Log.d("T-A-R.ElementFragment", "checkHidden: 15");
                            showHiddenAlertDialog(message, nextElement, answerStatesHidden, answersHidden, true);
                        }
                        return;
                    } else if (isQuota && hasQuota && answersHidden.size() == 0 && enabledCounter == 0 && canShowCounter > 0) {
//                        showScreensaver(true);
                        Log.d("T-A-R.ElementFragment", "checkHidden: 16");
                        isInHiddenQuotaDialog = true;
//                        answerStatesHidden.add(helper);
//                        saveHidden(nextElement, answerStatesHidden);
                        showHiddenAlertDialog("Ни один вариант ответа не подходит под условие", nextElement, answerStatesHidden, answersHidden, false);

//                        showHiddenExitAlertDialog("Нет вариантов ответов для продолжения");
                        return;
                    } else {
                        Log.d("T-A-R.ElementFragment", "checkHidden: 17");
                        if (answerStatesHidden.size() == 1) {
                            Log.d("T-A-R.ElementFragment", "checkHidden: 18");
                            answerStatesHidden.get(0).setChecked(true);
                        }
                        saveHidden(nextElement, answerStatesHidden);
                    }
                    Log.d("T-A-R.ElementFragment", "checkHidden: 19");
                } else {
                    Log.d("T-A-R.ElementFragment", "checkHidden: 20");
                    saveHidden(nextElement, answerStatesHidden);
                }
            }
            Log.d("T-A-R.ElementFragment", "checkHidden: 21");
            if (!isInHiddenQuotaDialog) {
                Log.d("T-A-R.ElementFragment", "checkHidden: 22");
                if (answerStatesHidden.size() > 0)
                    nextElementId = getElement(answerStatesHidden.get(0).getRelative_id()).getElementOptionsR().getJump();
                else nextElementId = nextElement.getElementOptionsR().getJump();
                if (!answerType.equals(ElementSubtype.HIDDEN)) {
                    Log.d("T-A-R.ElementFragment", "checkHidden: 23");
//                    Log.d("T-A-R", "checkHidden: 11111111111");
                    currentElement = nextElement;
                } else {
                    Log.d("T-A-R.ElementFragment", "checkHidden: 24");
//                    Log.d("T-A-R", "checkHidden: 222222222222");
//                    Log.d("T-A-R", "checkHidden: CUR ID = " + currentElement.getRelative_id());
                    hiddenId = currentElement.getRelative_id();
                    currentElement = getElement(nextElementId);
                }
                Log.d("T-A-R.ElementFragment", "checkHidden: 25");
                checkAndLoadNext();
                Log.d("T-A-R.ElementFragment", "checkHidden: 26");
            }
        }
        Log.d("T-A-R.ElementFragment", "checkHidden: 27");
        st("checkHidden() ---");
    }

    private void saveHidden(ElementItemR nextElement, List<AnswerState> answerStatesHidden) {
        st("saveHidden() +++");
        ElementPassedOB elementPassedR = new ElementPassedOB();
        elementPassedR.setRelative_id(nextElement.getRelative_id());
        elementPassedR.setProject_id(nextElement.getProjectId());
        elementPassedR.setToken(getQuestionnaire().getToken());
        elementPassedR.setDuration(1L);
        elementPassedR.setFrom_quotas_block(false);
        elementPassedR.setIs_question(false);

        Log.d("T-A-R.TransFragment", ">>> savePassedElement 6: " + elementPassedR.getRelative_id());
        getObjectBoxDao().insertElementPassedR(elementPassedR);
        setCondComp(elementPassedR.getRelative_id());
        getDao().setWasElementShown(true, nextElement.getRelative_id(), nextElement.getUserId(), nextElement.getProjectId());

        if (answerStatesHidden.size() > 0) {
            for (int i = 0; i < answerStatesHidden.size(); i++) {
//                if (found && getElement(answerStatesHidden.get(i).getRelative_id()).getElementOptionsR().isHelper()) break;
                if (getElement(answerStatesHidden.get(i).getRelative_id()).getElementOptionsR().isHelper() && answerStatesHidden.size() != 1)
                    continue;
                if (answerStatesHidden.get(i).isChecked()) {
                    ElementPassedOB answerPassedR = new ElementPassedOB();
                    answerPassedR.setRelative_id(answerStatesHidden.get(i).getRelative_id());
                    answerPassedR.setParent_id(getElement(answerStatesHidden.get(i).getRelative_id()).getRelative_parent_id());
                    answerPassedR.setProject_id(nextElement.getProjectId());
                    answerPassedR.setToken(getQuestionnaire().getToken());
                    answerPassedR.setValue(answerStatesHidden.get(i).getData());
                    answerPassedR.setFrom_quotas_block(isQuota);
                    answerPassedR.setIs_question(false);
                    try {
                        answerPassedR.setHelper(getElement(answerStatesHidden.get(i).getRelative_id()).getElementOptionsR().isHelper());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
//                        getDao().insertElementPassedR(answerPassedR);
                        Log.d("T-A-R.TransFragment", ">>> savePassedElement 5: " + elementPassedR.getRelative_id());
                        getObjectBoxDao().insertElementPassedR(answerPassedR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        st("saveHidden() ---");
    }

    private void showHiddenExitAlertDialog(String message) {
        if (getMainActivity() != null && !getMainActivity().isFinishing()) {
            getMainActivity().runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                            .setCancelable(false)
                            .setTitle(R.string.hidden_quotas_header)
                            .setMessage(message)
                            .setPositiveButton(R.string.view_OK, (dialog, which) -> {
                                stopAllRecording();
                                saveAndExit(false);
                                exitQuestionnaire();
                            })
                            .show();
                }
            });
        }

    }

    private void showHiddenAlertDialog(String message, ElementItemR nextElement, List<AnswerState> answerStatesHidden, List<ElementItemR> answersHidden, boolean showTitle) {
        MainActivity activity = getMainActivity();
        final String title;
        if (showTitle) title = "Превышение пределов квот в скрытом вопросе";
        else title = "";
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (!activity.isFinishing()) {
                    new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                            .setCancelable(false)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(R.string.view_continue, (dialog, which) -> {
                                List<AnswerState> answersStatesEnabled = new ArrayList<>();
                                for (int index = 0; index < answerStatesHidden.size(); index++) {
                                    if (answerStatesHidden.get(index).isEnabled()) {
                                        answerStatesHidden.get(index).setChecked(true);
                                        answersStatesEnabled.add(answerStatesHidden.get(index));
                                    }
                                }

                                updatePrevElement();
                                saveHidden(nextElement, answersStatesEnabled);
                                if (answersStatesEnabled.size() > 0)
                                    nextElementId = getElement(answersStatesEnabled.get(0).getRelative_id()).getElementOptionsR().getJump();
                                else
                                    nextElementId = currentElement.getElementOptionsR().getJump();

                                if (nextElementId == null) nextElementId = nextElement.getElementOptionsR().getJump();
                                isInHiddenQuotaDialog = false;
                                dialog.dismiss();
                                currentElement = nextElement;
                                checkAndLoadNext();
                            })
                            .setNegativeButton(R.string.view_cancel, (dialog, which) -> {
                                isInHiddenQuotaDialog = false;
                                clearSaved(true);
                                dialog.dismiss();
                            })
                            .setNeutralButton(R.string.view_finish_quiz, (dialog, which) -> {
                                stopAllRecording();
                                saveAndExit(true);
                            })
                            .show();
                }
            }
        });
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

    private void checkOnlineQuotas() {
        if (getMainActivity().getConfig().isCheckQuotasOnline() != null && getMainActivity().getConfig().isCheckQuotasOnline()) {

//            new Handler(Looper.getMainLooper()).post(() -> {
            String token = getQuestionnaire().getToken();
            OnlineQuotaR onlineQuotaR = getDao().getOnlineQuota(token);
            List<ElementPassedOB> passedElements = null;
            try {
                passedElements = getObjectBoxDao().getAllElementsPassedR(getQuestionnaire().getToken());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (passedElements != null && !passedElements.isEmpty()) {
                int counter = 0;
                for (ElementPassedOB passed : passedElements) {
                    if (passed.getIs_question()) counter++;
                }
                if (getMainActivity().getConfig().getCheckQuotasOnlineLimit() != null
                        && counter <= getMainActivity().getConfig().getCheckQuotasOnlineLimit()) {
                    boolean isLastCheck = counter == getMainActivity().getConfig().getCheckQuotasOnlineLimit();
                    if (isQuota) {
                        if (onlineQuotaR == null) {
                            OnlineQuotaR newOnlineQuotaR = new OnlineQuotaR();
                            newOnlineQuotaR.setToken(token);
                            getDao().insertOnlineQuota(newOnlineQuotaR);
                        }
                    } else if (onlineQuotaR != null) {
                        String stringQuota = onlineQuotaR.getQuotas(); //TODO REMOVE!!!!!!!!!!!!!
//                            String stringQuota = null;
                        List<Integer> quotas;
                        if (stringQuota == null) {
                            quotas = getClosedQuotas(passedElements);
                            getDao().updateOnlineQuotas(token, new Gson().toJson(quotas));
                        } else {
                            quotas = StringUtils.getListIntFromString(stringQuota);
                        }

                        if (!quotas.isEmpty()) {
                            sendOnlineQuotas(quotas, isLastCheck);
                        }
                    }
                }
            }
//            });

        }
    }

    private List<Integer> getClosedQuotas(List<ElementPassedOB> passedElements) {
        List<QuotaModel> closedQuotasList = new ArrayList<>();
        List<Integer> closedQuotasIds = new ArrayList<>();
        List<Integer> passedIds = new ArrayList<>();

        for (ElementPassedOB elementPassedR : passedElements) {
            passedIds.add(elementPassedR.getRelative_id());
        }

        if (getMainActivity().quotas != null && getMainActivity().quotas.size() > 0) // TODO Проверить когда он налл!
            for (QuotaModel quota : getMainActivity().quotas) {
                boolean closed = false;
                Set<Integer> idList = quota.getSet();
                for (Integer elementId : idList) {
                    closed = passedIds.contains(elementId);
                    if (!closed) break;
                }
                if (closed) {
                    closedQuotasList.add(quota);
                    closedQuotasIds.add(quota.getQuota_id());
                }
            }


        return closedQuotasIds;
    }

    private void sendOnlineQuotas(List<Integer> quotas, boolean isLastCheck) {

        String loginAdmin = getMainActivity().getConfig().getLoginAdmin();
//        String token = SmsUtils.encode(new Gson().toJson(new QToken(loginAdmin)));
        String token = new Gson().toJson(new QToken(loginAdmin));
        String url = getQuotaUrl();
        String login = getCurrentUser().getLogin();
        if (Internet.hasConnection(getMainActivity())) {
//            getMainActivity().showToastLongFromActivity("" + quotas.get(0));
            OnlineQuotasRequestModel request = new OnlineQuotasRequestModel(login, quotas);
            QuizerAPI.checkOnlineQuota(url, token, login, quotas, isLastCheck, (data, isLast) -> {
                if (data != null) {
                    String responseJson;
                    try {
                        responseJson = data.string();
                        Log.d("T-A-R", "sendOnlineQuotas: " + responseJson);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (isLastCheck) {
                            getQuestionnaire().setQuotas_online_checking_failed(true);
                        }
                        return;
                    }

                    OnlineQuotaResponseModel responseModel;
                    try {
                        responseModel = new GsonBuilder().create().fromJson(responseJson, OnlineQuotaResponseModel.class);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        if (isLastCheck) {
                            getQuestionnaire().setQuotas_online_checking_failed(true);
                        }
                        return;
                    }

                    if (responseModel != null
                            && responseModel.isQuotasExpired()
                            && responseModel.getQuotasAlertsText() != null
                            && !responseModel.getQuotasAlertsText().isEmpty()
                    ) {
                        try {
                            showHtmlDialog(responseModel.getQuotasAlertsText(), "Завершить", null,
                                    new ICallback() {
                                        @Override
                                        public void onStarting() {

                                        }

                                        @Override
                                        public void onSuccess() {
                                            saveAndExit(true);
                                        }

                                        @Override
                                        public void onError(Exception pException) {

                                        }
                                    });
                        } catch (Exception e) {
                            if (isLastCheck) {
                                getQuestionnaire().setQuotas_online_checking_failed(true);
                            }
                            e.printStackTrace();
                        }
                    } else if (responseModel == null) {
                        if (isLastCheck) {
                            getQuestionnaire().setQuotas_online_checking_failed(true);
                        }
                        Log.d("T-A-R", "sendOnlineQuotas: RESPONSE MODEL = NULL");
                    }

                } else {
                    if (isLastCheck) {
                        getQuestionnaire().setQuotas_online_checking_failed(true);
                    }
                    Log.d("T-A-R", "sendOnlineQuotas: SERVER RESPONSE = NULL");
                }
            });
        } else {
            if (isLastCheck) {
                getQuestionnaire().setQuotas_online_checking_failed(true);
            }
        }
    }

    private boolean checkQuotaJump(int relativeId) {
        st("checkQuotaJump() +++");
        boolean isQuota = false;
//        Log.d("T-A-R.TransFragment", "======= checkQuotaJump: " + relativeId);
        ElementItemR currentElement = getElement(relativeId);
        if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
            isQuota = true;
            quotaElementsList = getElement(currentElement.getRelative_parent_id()).getElements();
        }
        return isQuota;
    }

    private void fillPassedQuotas(int start, int next) {
        Log.d("T-A-R", "fillPassedQuotas start: " + start + " " + next);
        int startPosition = -1;
        for (int i = 0; i < quotaElementsList.size(); i++) {
            if (quotaElementsList.get(i).getRelative_id().equals(start)) {
                startPosition = i;
                break;
            }
        }
        Log.d("T-A-R", "startPosition: " + startPosition);
        if (startPosition == -1) {
            for (int i = 0; i < quotaElementsList.size(); i++) {
                if (!quotaElementsList.get(i).getRelative_id().equals(next)) {
                    Log.d("T-A-R", "savePassedElement: (-1)");
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        } else if (startPosition == 0) {
            for (int i = 1; i < quotaElementsList.size(); i++) {
                if (!quotaElementsList.get(i).getRelative_id().equals(next)) {
                    Log.d("T-A-R", "savePassedElement: (0)");
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        } else if (startPosition != (quotaElementsList.size() - 1)) {
            for (int i = startPosition + 1; i < quotaElementsList.size(); i++) {
                Log.d("T-A-R", "i: " + i);
                if (!quotaElementsList.get(i).getRelative_id().equals(next)) {
                    Log.d("T-A-R", "savePassedElement: (2)");
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        }

        st("fillPassedQuotas() ---");
    }

    private void savePassedElement(int id) {
        CurrentQuestionnaireR quiz = getQuestionnaire();
        Log.d("T-A-R.TransFragment", "><><><><><: " + id);

        st("savePassedElement() +++");

        ElementItemR currentElement = getElement(id);
        ElementPassedOB elementPassedR = new ElementPassedOB();
        elementPassedR.setRelative_id(id);
        elementPassedR.setProject_id(currentElement.getProjectId());
        elementPassedR.setToken(getQuestionnaire().getToken());
        elementPassedR.setHelper(true);
        elementPassedR.setFrom_quotas_block(false);
        elementPassedR.setIs_question(false);
        if (currentElement.getElementOptionsR().isWith_card()) {
            elementPassedR.setCard_showed(currentElement.getShowed_in_card());
        }
//        getDao().insertElementPassedR(elementPassedR);
        Log.d("T-A-R.TransFragment", ">>> savePassedElement 4: " + elementPassedR.getRelative_id());
        getObjectBoxDao().insertElementPassedR(elementPassedR);
        setCondComp(elementPassedR.getRelative_id());


        List<ElementItemR> answers = currentElement.getElements();
        for (ElementItemR answer : answers) {
            if (answer.getElementOptionsR().isHelper()) {
                elementPassedR.setRelative_id(answer.getRelative_id());
                elementPassedR.setParent_id(answer.getRelative_parent_id());
                elementPassedR.setFrom_quotas_block(true);
                elementPassedR.setIs_question(false);
                if (currentElement.getElementOptionsR().isWith_card()) {
                    elementPassedR.setChecked_in_card(currentElement.getChecked_in_card());
                }
                Log.d("T-A-R.ElementFragment", "savePassedElement 10: " + elementPassedR.getRelative_id());
//                getDao().insertElementPassedR(elementPassedR);
                Log.d("T-A-R.TransFragment", ">>> savePassedElement 3: " + elementPassedR.getRelative_id());
                getObjectBoxDao().insertElementPassedR(elementPassedR);
                break;
            }
        }
        st("savePassedElement() ---");
    }

    void transfer(int start, int next, boolean restored) {

        if (!AVIA) {
            ElementFragment fragment = new ElementFragment();
            fragment.setStartElement(next, restored);
            if (!restored) {
                Log.d("T-A-R.ElementFragment", "transfer checkQuotaJump(next): " + checkQuotaJump(next));
                if (next != 0 && next != -1 && checkQuotaJump(next)) fillPassedQuotas(start, next);

                replaceFragment(fragment);
            } else
                replaceFragmentBack(fragment);
        } else {
            ElementAviaFragment fragment = new ElementAviaFragment();
            fragment.setStartElement(next, restored);
            if (!restored)
                replaceFragment(fragment);
            else
                replaceFragmentBack(fragment);
        }


    }

}

