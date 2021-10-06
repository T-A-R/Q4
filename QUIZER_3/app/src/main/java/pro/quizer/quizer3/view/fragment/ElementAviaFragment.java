package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
import pro.quizer.quizer3.adapter.PageAdapter;
import pro.quizer.quizer3.adapter.RankQuestionAdapter;
import pro.quizer.quizer3.adapter.ScaleQuestionAdapter;
import pro.quizer.quizer3.adapter.TableQuestionAdapter;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.model.CardItem;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.model.view.TitleModel;
import pro.quizer.quizer3.utils.ConditionUtils;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.ExpressionUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementAviaFragment extends ScreenFragment implements
        View.OnClickListener,
        PageAdapter.OnAnswerClickListener,
        ListAnswersAdapter.OnAnswerClickListener,
        RankQuestionAdapter.OnAnswerClickListener,
        ScaleQuestionAdapter.OnAnswerClickListener,
        TableQuestionAdapter.OnTableAnswerClickListener {

    private Button btnNext;
    private Button btnPrev;
    private Button btnExit;
    private ImageView btnHideTitle;
    private RelativeLayout titleCont;
    private LinearLayout titleBox;
    private LinearLayout titleCont1;
    private LinearLayout titleCont2;
    private LinearLayout titleImagesCont1;
    private LinearLayout titleImagesCont2;
    private LinearLayout questionCont;
    private LinearLayout questionImagesCont;
    private RelativeLayout infoCont;
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
    private RecyclerView rvPage;
    private TextView spinnerAnswers;
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

    private boolean isQuestionHided = false;
    private boolean hasQuestionImage = false;
    private ElementItemR currentElement = null;
    List<ElementItemR> answersList;
    private Long startTime;
    private Integer startElementId;
    private Integer nextElementId;
    private Integer prevElementId;
    private String answerType;
    private int spinnerSelection = -1;
    private List<Integer> spinnerMultipleSelection;
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
    private TextView multiSelectionSpinner;
    private PageAdapter pageAdapter;
    private List<PrevElementsR> prevList = null;
    private Map<Integer, TitleModel> titlesMap;
    private CompositeDisposable disposables;

    public ElementAviaFragment() {
        super(R.layout.fragment_element_avia);
    }

    public ElementAviaFragment setStartElement(Integer startElementId, boolean restored) {
        this.startElementId = startElementId;
        this.isRestored = restored;
        st("setting element");
        return this;
    }

    @Override
    protected void onReady() {
        st("START");
        disposables = new CompositeDisposable();
        setRetainInstance(true);
        Toolbar toolbar = new Toolbar(getMainActivity());
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
        infoCont = findViewById(R.id.info_cont);
        tableCont = findViewById(R.id.table_cont);
        rvAnswers = findViewById(R.id.answers_recyclerview);
        rvScale = findViewById(R.id.scale_recyclerview);
        rvPage = findViewById(R.id.page_recyclerview);
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

        btnNext.setTypeface(Fonts.getAviaButton());
        btnPrev.setTypeface(Fonts.getAviaButton());
        btnExit.setTypeface(Fonts.getAviaButton());
        tvTitle1.setTypeface(Fonts.getAviaText());
        tvTitle2.setTypeface(Fonts.getAviaText());
        tvTitleDesc1.setTypeface(Fonts.getAviaText());
        tvTitleDesc2.setTypeface(Fonts.getAviaText());
        tvQuestion.setTypeface(Fonts.getAviaText());
        tvHiddenQuestion.setTypeface(Fonts.getAviaText());
        tvQuestionDesc.setTypeface(Fonts.getAviaText());
        tvQuestionDesc.setTypeface(tvQuestionDesc.getTypeface(), Typeface.ITALIC);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        closeQuestion.setOnClickListener(this);
        cont.setOnClickListener(this);
        tvQuestion.setOnClickListener(this);
        btnHideTitle.setOnClickListener(this);

        deactivateButtons();
        if (!isAvia()) {
            toolbar.setTitle(getString(R.string.app_name));
            toolbar.showOptionsView(v -> MainFragment.showDrawer(), v -> showInfoDialog());
            toolbar.showInfoView();
            MainFragment.enableSideMenu(false, getMainActivity().isExit());
        }

        st("init views 1");

        try {
            prevList = getDao().getPrevElementsR();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (prevList == null || prevList.size() == 0) {
            prevList = new ArrayList<>();
            btnPrev.setVisibility(isAvia()? View.GONE : View.INVISIBLE);
        }

        if (prevList.size() == 0 || prevList.size() == 1) {
            btnPrev.setVisibility(isAvia()? View.GONE : View.INVISIBLE);
            cont.startAnimation(Anim.getAppear(getContext()));
            btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));
        } else {
            cont.startAnimation(Anim.getAppear(getContext()));
            btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));
            btnPrev.startAnimation(Anim.getAppearSlide(getContext(), 500));
        }
        btnExit.startAnimation(Anim.getAppearSlide(getContext(), 500));

        st("load prev elements");

        initCurrentElements();
        st("init curr element");
        loadResumedData();
        st("load resumed data");
        initQuestion();
        Log.d("T-L.ElementAviaFragment", "initQuestion: " + currentElement.getRelative_id() + " / " + answerType);
        st("init question");
        if (currentElement != null) {
            if (checkConditions(currentElement)) {
                st("check conditions");
                startRecording();
                st("start recording");
                setQuestionType();
                st("set type");
                Log.d("T-L.ElementAviaFragment", "initQuestion 2: " + currentElement.getRelative_id() + " / " + answerType);
                initViews();
                if (currentElement.getElementOptionsR().isWith_card()) {
                    toolbar.showCardView(v -> showCardDialog());
                }
                st("init views 2");
                updateCurrentQuestionnaire();
                st("upd curr quest");

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

                Disposable subscribeTitles = getMainActivity().getConvertedTitles(titlesMap).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> disposables.add(disposable))
                        .subscribe(
                                (Map<Integer, TitleModel> convertedTitles) -> {
                                    titlesMap = convertedTitles;
                                    Log.d("T-L.ElementAviaFragment", "initQuestion 3: " + currentElement.getRelative_id() + " / " + answerType);
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
                checkAndLoadNext();
            }

            st("activ btns");
            try {
                getMainActivity().activateExitReminder();
                st("activ exit rem");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            exitQuestionnaire();
        }
    }

    public boolean wasReloaded() {
        List<ElementPassedR> elements = null;

        try {
            elements = getDao().getAllElementsPassedR(getQuestionnaire().getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (elements != null && elements.size() > 0)
            for (ElementPassedR element : elements) {
                if (element.getRelative_id().equals(currentElement.getRelative_id())) {
                    isRestored = true;
                    return true;
                }
            }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == btnNext) {
            if (answerType.equals(ElementSubtype.END)) {
                nextElementId = currentElement.getElementOptionsR().getJump();
                Log.d("T-L.ElementFragment", "onClick NEXT: " + nextElementId);
            }
            DoNext next = new DoNext();
            next.execute();
        } else if (view == btnPrev) {
            deactivateButtons();
            TransFragment fragment = new TransFragment();
            if (prevElementId != 0) {
                prevElementId = prevList.get(prevList.size() - 1).getPrevId();
                prevList.remove(prevList.size() - 1);
                try {
                    getDao().clearPrevElementsR();
                    getDao().setPrevElement(prevList);
                } catch (Exception e) {
                    showToast(getString(R.string.set_last_element_error));
                    return;
                }
                fragment.setStartElement(prevElementId, true);
                stopRecording();
                replaceFragmentBack(fragment);
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

                    TransFragment fragment = new TransFragment();
                    fragment.setStartElement(getAbortedBoxRelativeId());
                    stopRecording();
                    replaceFragment(fragment);
                } else {
                    showExitPoolAlertDialog();
                }
            }
        } else if (view == btnHideTitle) {
            if (titleBox.getVisibility() == View.VISIBLE) {
                titleBox.setVisibility(View.GONE);
                tvHiddenTitle.setText(tvTitle1.getText().length() > 0 ? tvTitle1.getText() : tvTitle2.getText());
                tvHiddenTitle.setVisibility(View.VISIBLE);
                btnHideTitle.setImageResource(R.drawable.plus_green);
            } else {
                titleBox.setVisibility(View.VISIBLE);
                tvHiddenTitle.setVisibility(View.GONE);
                btnHideTitle.setImageResource(R.drawable.minus_green);
            }
        } else if (view == closeQuestion || view == tvQuestion) {
            if (questionBox.getVisibility() == View.VISIBLE) {
                questionBox.setVisibility(View.GONE);
                tvHiddenQuestion.setText(tvQuestion.getText());
                tvHiddenQuestion.setVisibility(View.VISIBLE);
                closeQuestion.setImageResource(R.drawable.plus_white);
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
            initCurrentElements();
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

                if (getCurrentElements().get(i).getRelative_id().equals(currentElement.getRelative_id())) {
                    found = true;
                }
                if (found) {
                    if (!getCurrentElements().get(i).getType().equals(ElementType.BOX)
                            || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.TABLE)
                            || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.PAGE)) {
                        startElementId = getCurrentElements().get(i).getRelative_id();
                        currentElement = getCurrentElements().get(i);
                        break;
                    }
                }
            }
//            }
        } else {
            Log.d(TAG, "initQuestions: ERROR! (empty list)");
        }
    }

    private void setQuestionType() {
        if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
            isQuota = true;
        }
        switch (currentElement.getSubtype()) {
            case ElementSubtype.LIST:
                answerType = ElementSubtype.LIST;
                List<ElementItemR> answers = currentElement.getElements();
                if (answers != null && answers.size() > 0) {
                    final List<ElementContentsR> contents = getDao().getElementContentsR(answers.get(0).getRelative_id());
                    if (contents != null && !contents.isEmpty()) {
                        String data = contents.get(0).getData();
                        final String filePhotoPath = getFilePath(data);

                        if (!StringUtils.isEmpty(filePhotoPath)) {
                            answerType = ElementSubtype.PAGEVIEW;
                            currentElement.setSubtype(ElementSubtype.PAGEVIEW);
                        }
                    }
                }
                break;
            case ElementSubtype.PAGEVIEW:
                answerType = ElementSubtype.PAGEVIEW;
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
            case ElementSubtype.PAGE:
                answerType = ElementSubtype.PAGE;
                break;
        }

    }

    private void initViews() {

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
                        && (parentElement.getShown_at_id().equals(-102) || parentElement.getShown_at_id().equals(currentElement.getRelative_id()))) {
                    titles = 1;
                    getDao().setWasElementShown(true, parentElement.getRelative_id(), parentElement.getUserId(), parentElement.getProjectId());
                    getDao().setShownId(currentElement.getRelative_id(), parentElement.getRelative_id(), parentElement.getUserId(), parentElement.getProjectId());

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

                    showContent(parentElement, titleImagesCont1);

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
                                    && (parentElement2.getShown_at_id().equals(-102) || parentElement2.getShown_at_id().equals(currentElement.getRelative_id()))) {
                                titles = 2;
                                getDao().setWasElementShown(true, parentElement2.getRelative_id(), parentElement2.getUserId(), parentElement2.getProjectId());
                                getDao().setShownId(currentElement.getRelative_id(), parentElement2.getRelative_id(), parentElement2.getUserId(), parentElement2.getProjectId());

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
                                showContent(parentElement2, titleImagesCont2);
                            }
                        }
                    }
                } else titleCont.setVisibility(View.GONE);
            }
        }

        showContent(currentElement, questionImagesCont);

        if (getMainActivity().getConfig().isPhotoQuestionnaire() && currentElement.getElementOptionsR().isTake_photo()) {
            Log.d("T-L.ElementFragment", "TAKE PHOTO");
            shotPicture(getLoginAdmin(), getQuestionnaire().getToken(), currentElement.getRelative_id(), getCurrentUserId(), getQuestionnaire().getProject_id(), getCurrentUser().getLogin());
        } else {
            Log.d("T-L.ElementFragment", "NOT TAKE PHOTO");
        }
    }

    private void showContent(ElementItemR element, View cont) {
        final List<ElementContentsR> contents = getDao().getElementContentsR(element.getRelative_id());

        if (contents != null && !contents.isEmpty()) {
            String data1 = contents.get(0).getData();
            String data2 = null;
            String data3 = null;
            if (contents.size() > 1)
                data2 = contents.get(1).getData();
            if (contents.size() > 2)
                data3 = contents.get(2).getData();

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
    }

    private void showPic(View cont, ImageView view, String data) {
        final String filePhotoPath = getFilePath(data);

        if (StringUtils.isEmpty(filePhotoPath)) {
            return;
        }
        cont.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        Picasso.with(getActivity())
                .load(new File(filePhotoPath))
                .into(view);
    }

    private void setAnswersList() {
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
            nextElementId = currentElement.getElementOptionsR().getJump();
            if (nextElementId == null) {
                nextElementId = answersList.get(0).getElementOptionsR().getJump();
            }
        } else {
            answersList = checkedAnswersList;
        }
    }

    private void initRecyclerView() {

        List<String> itemsList = new ArrayList<>();

        switch (answerType) {
            case ElementSubtype.LIST:
            case ElementSubtype.RANK:
                rvAnswers.setVisibility(View.VISIBLE);
                break;
            case ElementSubtype.TABLE:
                tableCont.setVisibility(View.VISIBLE);
                break;
            case ElementSubtype.HTML:
                questionCont.setVisibility(View.GONE);
                infoCont.setVisibility(View.VISIBLE);
                infoText.loadData(currentElement.getElementOptionsR().getData(), "text/html; charset=UTF-8", null);
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
            case ElementSubtype.PAGE:
            case ElementSubtype.SELECT:
            case ElementSubtype.PAGEVIEW:
                rvPage.setVisibility(View.VISIBLE);
                break;
        }


        for (ElementItemR element : answersList) {
            itemsList.add(Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle());
        }

        switch (answerType) {
            case ElementSubtype.PAGE:
                pageAdapter = new PageAdapter(getMainActivity(), currentElement.getElements(), this);
                rvPage.setLayoutManager(new LinearLayoutManager(getContext()));
                rvPage.setAdapter(pageAdapter);
                break;
            case ElementSubtype.PAGEVIEW:
            case ElementSubtype.SELECT:
                List<ElementItemR> elementsList = new ArrayList<>();
                elementsList.add(currentElement);
                pageAdapter = new PageAdapter(getMainActivity(), elementsList, this);
                rvPage.setLayoutManager(new LinearLayoutManager(getContext()));
                rvPage.setAdapter(pageAdapter);
                break;
            case ElementSubtype.LIST:
                MainActivity activity = getMainActivity();
                if (isQuota) {

                    adapterList = new ListAnswersAdapter(activity, currentElement, answersList,
                            getPassedQuotasBlock(currentElement.getElementOptionsR().getOrder()), activity.getTree(null), titlesMap, this);
                } else {
                    adapterList = new ListAnswersAdapter(activity, currentElement, answersList,
                            null, null, titlesMap, this);
                }
                rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
                rvAnswers.setAdapter(adapterList);
                break;
            case ElementSubtype.RANK:
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
            case ElementSubtype.TABLE:
                adapterTable = new TableQuestionAdapter(currentElement, answersList, titlesMap, getActivity(), mRefreshRecyclerViewRunnable, this);
                tableLayout.setAdapter(adapterTable);
                tableLayout.setLongClickable(false);
                tableLayout.setDrawingCacheEnabled(true);
                break;
            case ElementSubtype.SCALE:
                adapterScale = new ScaleQuestionAdapter(getActivity(), currentElement, answersList,
                        this);
                if (isAvia())
                    rvScale.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                else
                    rvScale.setLayoutManager(new LinearLayoutManager(getContext()));
                rvScale.setAdapter(adapterScale);
                break;
        }
    }

    private void updateCurrentQuestionnaire() {
        try {
            getDao().setCurrentElement(startElementId);
            getDao().setQuestionTime(DateUtils.getCurrentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkConditions(ElementItemR element) {
        if (element != null) {
            final ElementOptionsR options = element.getElementOptionsR();
            if (options != null && options.getPre_condition() != null) {
                final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), getMainActivity());
                if (showValue != ConditionUtils.CAN_SHOW) {
                    if (showValue != ConditionUtils.CANT_SHOW) {
                        nextElementId = showValue;
                    } else {
                        if (!element.equals(currentElement)) {
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
                        return false;
                    } else if (nextElementId.equals(-1)) {
                        exitQuestionnaire();
                        return false;
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean saveElement() {
        boolean saved = false;
        switch (answerType) {
            case ElementSubtype.LIST:
            case ElementSubtype.QUOTA:
            case ElementSubtype.SCALE:
            case ElementSubtype.RANK: {
                List<AnswerState> answerStates;
                if (answerType.equals(ElementSubtype.SCALE)) {
                    answerStates = adapterScale.getAnswers();
                } else if (answerType.equals(ElementSubtype.RANK)) {
                    answerStates = adapterRank.getAnswers();
                } else {
                    answerStates = adapterList.getAnswers();
                }
                if (answerStates != null && notEmpty(answerStates)) {
                    if (answerType.equals(ElementSubtype.RANK)) {
                        List<AnswerState> answerStatesRang = new ArrayList<>();
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
                        nextElementId = currentElement.getElementOptionsR().getJump();
                        if (nextElementId == null)
                            nextElementId = answersList.get(0).getElementOptionsR().getJump();
                    } else {
                        for (int i = 0; i < answerStates.size(); i++) {
                            if (answerStates.get(i).isChecked()) {
                                int id = answerStates.get(i).getRelative_id();
                                nextElementId = getElement(id).getElementOptionsR().getJump();
                            }
                        }
                    }

                    //TODO вернуть для ротации вопросов
//                if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
//                    nextElementId = currentElement.getElementOptionsR().getJump();
//                    if (nextElementId == -2) {
//                        nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
//                    }
//                    st("init SAVE 3");
//                }

                    ElementPassedR elementPassedR = new ElementPassedR();
                    elementPassedR.setRelative_id(currentElement.getRelative_id());
                    elementPassedR.setProject_id(currentElement.getProjectId());
                    elementPassedR.setToken(getQuestionnaire().getToken());
                    elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
                    elementPassedR.setFrom_quotas_block(false);

                    getDao().insertElementPassedR(elementPassedR);
                    getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                    saved = true;

                    for (int i = 0; i < answerStates.size(); i++) {
                        if (answerStates.get(i).isChecked()) {
                            ElementPassedR answerPassedR = new ElementPassedR();
                            answerPassedR.setRelative_id(answerStates.get(i).getRelative_id());
                            answerPassedR.setProject_id(currentElement.getProjectId());
                            answerPassedR.setToken(getQuestionnaire().getToken());
                            answerPassedR.setValue(answerStates.get(i).getData());
                            answerPassedR.setRank(i + 1);
                            answerPassedR.setFrom_quotas_block(isQuota);

                            try {
                                getDao().insertElementPassedR(answerPassedR);
                                saved = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                saved = false;
                                return saved;
                            }
                        }
                    }
                }
                break;
            }
            case ElementSubtype.TABLE: {
                AnswerState[][] answerStates = adapterTable.getmAnswersState();

                if (answerStates != null && answerStates[0][0].getRelative_id() != null && adapterTable.isCompleted()) {
                    if (currentElement.getElementOptionsR().getJump() != null)
                        nextElementId = currentElement.getElementOptionsR().getJump();
                    else
                        nextElementId = getElement(answerStates[0][0].getRelative_id()).getElementOptionsR().getJump();

                    if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                        nextElementId = currentElement.getElementOptionsR().getJump();
                        if (nextElementId == -2) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                        }
                    }

                    ElementPassedR elementPassedR = new ElementPassedR();
                    elementPassedR.setRelative_id(currentElement.getRelative_id());
                    elementPassedR.setProject_id(currentElement.getProjectId());
                    elementPassedR.setToken(getQuestionnaire().getToken());
                    elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
                    try {
                        if (!isRestored) {
                            getDao().insertElementPassedR(elementPassedR);
                            getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                        }
                        saved = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        saved = false;
                        return saved;
                    }

                    for (int i = 0; i < answerStates.length; i++) {
                        for (int k = 0; k < answerStates[i].length; k++) {
                            if (answerStates[i][k].isChecked()) {
                                ElementPassedR answerPassedR = new ElementPassedR();
                                answerPassedR.setRelative_id(answerStates[i][k].getRelative_id());
                                answerPassedR.setValue(answerStates[i][k].getData());
                                answerPassedR.setProject_id(currentElement.getProjectId());
                                answerPassedR.setToken(getQuestionnaire().getToken());
                                try {
                                    if (!isRestored) {
                                        getDao().insertElementPassedR(answerPassedR);
                                    }
                                    saved = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    saved = false;
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
                ElementPassedR elementPassedR = new ElementPassedR();
                if (answerType.equals(ElementSubtype.END)) {
                    nextElementId = currentElement.getElementOptionsR().getType_end();
                } else {
                    nextElementId = currentElement.getElementOptionsR().getJump();
                    if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                        nextElementId = currentElement.getElementOptionsR().getJump();
                        if (nextElementId == -2) {
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

                try {
                    if (!isRestored) {
                        getDao().insertElementPassedR(elementPassedR);
                        getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                    }
                    saved = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    saved = false;
                    return saved;
                }
                break;
            case ElementSubtype.PAGE:
            case ElementSubtype.PAGEVIEW:
            case ElementSubtype.SELECT:
                boolean isCompleted = true;
                Map<Integer, List<AnswerState>> pageAnswersStates = pageAdapter.getAnswers();
                for (Map.Entry<Integer, List<AnswerState>> answerStates : pageAnswersStates.entrySet()) {
                    boolean localDone = false;
                    for (AnswerState state : answerStates.getValue()) {
                        if (state.isChecked()) {
                            localDone = true;
                            break;
                        }
                    }
                    if (!localDone) {
                        showToast("Пожалуйста, ответьте на все вопросы.");
                        saved = false;
                        isCompleted = false;
                        break;
                    }
                }
                if (isCompleted) {
                    nextElementId = currentElement.getElementOptionsR().getJump();
                    ElementPassedR pagePassed = new ElementPassedR();
                    pagePassed.setRelative_id(currentElement.getRelative_id());
                    pagePassed.setProject_id(currentElement.getProjectId());
                    pagePassed.setToken(getQuestionnaire().getToken());
                    pagePassed.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
                    try {
                        if (!isRestored) {
                            getDao().insertElementPassedR(pagePassed);
                            getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                        }
                        saved = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        saved = false;
                        return saved;
                    }

                    for (Map.Entry<Integer, List<AnswerState>> answerStates : pageAnswersStates.entrySet()) {
                        ElementPassedR questionPassedR = new ElementPassedR();
                        questionPassedR.setRelative_id(answerStates.getKey());
                        questionPassedR.setProject_id(currentElement.getProjectId());
                        questionPassedR.setToken(getQuestionnaire().getToken());
                        try {
                            if (!isRestored) {
                                getDao().insertElementPassedR(questionPassedR);
                            }
                            saved = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            saved = false;
                            return saved;
                        }

                        for (int k = 0; k < answerStates.getValue().size(); k++) {
                            if (answerStates.getValue().get(k).isChecked()) {
                                if (nextElementId == null) {
                                    try {
                                        nextElementId = getDao().getElementById(answerStates.getValue().get(k).getRelative_id()).getElementOptionsR().getJump();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                ElementPassedR answerPassedR = new ElementPassedR();
                                answerPassedR.setRelative_id(answerStates.getValue().get(k).getRelative_id());
                                answerPassedR.setValue(answerStates.getValue().get(k).getData());
                                answerPassedR.setProject_id(currentElement.getProjectId());
                                answerPassedR.setToken(getQuestionnaire().getToken());
                                try {
                                    if (!isRestored) {
                                        getDao().insertElementPassedR(answerPassedR);
                                    }
                                    saved = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    saved = false;
                                    return saved;
                                }
                            }
                        }
                    }
                }
                saved = isCompleted;
                break;
        }
        return saved;
    }

    private boolean checkMultipleSpinner() {
        if (spinnerMultipleSelection == null || spinnerMultipleSelection.size() < 1) {
            return false;
        }
        if (currentElement.getElementOptionsR().getMin_answers() != null) {
            if (spinnerMultipleSelection.size() < currentElement.getElementOptionsR().getMin_answers()) {
                showToast("Выберите минимум " + currentElement.getElementOptionsR().getMin_answers() + " ответов");
                return false;
            }
        }
        if (currentElement.getElementOptionsR().getMax_answers() != null) {
            if (spinnerMultipleSelection.size() > currentElement.getElementOptionsR().getMax_answers()) {
                showToast("Выберите максимум " + currentElement.getElementOptionsR().getMax_answers() + " ответов");
                return false;
            }
        }
        return true;
    }

    public boolean notEmpty(List<AnswerState> answerStates) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (openType && !element.getElementOptionsR().isUnnecessary_fill_open()) {
                    if (state.getData() == null || state.getData().equals("")) {
                        if (answerType.equals(ElementSubtype.RANK))
                            showToast(getString(R.string.empty_rank_warning));
                        else showToast(getString(R.string.empty_string_warning));

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
                return false;
            }
            if (max != null && counter > max) {
                showToast("Выберите максимум " + max + " ответ(а).");
                return false;
            }
        } else {
            if (min != null)
                showToast("Выберите минимум " + min + " ответа.");
            else
                showToast("Выберите минимум 1 ответ.");
            return false;
        }
        return true;
    }

    //TODO FOR TESTS!
    public void showPassed() {
        List<ElementPassedR> elementPassedRS = getDao().getAllElementsPassedR(getQuestionnaire().getToken());
        Log.d(TAG, "==========================================");
        for (ElementPassedR element : elementPassedRS) {
            Log.d(TAG, ">>>>>>>>>>>>>>> showPassed: " + element.getId());
        }
    }

    private void updatePrevElement() {
        if (prevList != null) {
            prevList = getDao().getPrevElementsR();
        } else {
            prevList = new ArrayList<>();
        }
        getDao().insertPrevElementsR(new PrevElementsR(startElementId, nextElementId));
    }

    @Override
    public void onAnswerClick(int position, boolean enabled, String answer) {
        if (isRestored) {
            try {
                isRestored = false;
                int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                getDao().deleteOldElementsPassedR(id);
                showToast(getString(R.string.data_changed));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAnswerClick(int row, int column) {
        if (isRestored) {
            try {
                isRestored = false;
                int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                getDao().deleteOldElementsPassedR(id);
                showToast(getString(R.string.data_changed));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
        @Override
        public void run() {
            UiUtils.hideKeyboard(getContext(), getView());
        }
    };

    public void loadResumedData() {
        if (isRestored) {
            try {
                if (prevList != null && prevList.size() > 0) {
                    PrevElementsR lastPassedElement = prevList.get(prevList.size() - 1);
                    startElementId = lastPassedElement.getNextId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadSavedData() {
        switch (answerType) {
            case ElementSubtype.LIST:
            case ElementSubtype.QUOTA: {
                List<AnswerState> answerStatesAdapter = adapterList.getAnswers();
                List<AnswerState> answerStatesRestored = new ArrayList<>();
                int lastSelectedPosition = -1;
                for (int i = 0; i < answerStatesAdapter.size(); i++) {
                    AnswerState answerStateNew = new AnswerState();
                    ElementPassedR answerStateRestored = null;
                    try {
                        answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answerStatesAdapter.get(i).getRelative_id());
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
//                if (!currentElement.getElementOptionsR().isPolyanswer())
//                    adapterList.setLastSelectedPosition(lastSelectedPosition);
                adapterList.notifyDataSetChanged();
                break;
            }
            case ElementSubtype.RANK: {
                List<ElementPassedR> elementPassedRList = new ArrayList<>();
                List<AnswerState> answerStatesRestored = new ArrayList<>();
                List<ElementItemR> answerListRestored = new ArrayList<>();
                for (int i = 0; i < answersList.size(); i++) {
                    ElementPassedR answerPassedRestored;
                    answerPassedRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answersList.get(i).getRelative_id());
                    if (answerPassedRestored != null) {
                        elementPassedRList.add(answerPassedRestored);
                    }
                }
                if (elementPassedRList.size() > 0) {
                    Collections.sort(elementPassedRList, (lhs, rhs) -> lhs.getRank().compareTo(rhs.getRank()));

                    for (ElementPassedR itemR : elementPassedRList) {
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
//            case ElementSubtype.SELECT:
//
//                spinnerSelection = -1;
//                spinnerMultipleSelection = new ArrayList<>();
//
//                for (int i = 0; i < answersList.size(); i++) {
//                    ElementPassedR answerStateRestored = null;
//                    try {
//                        answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answersList.get(i).getRelative_id());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (answerStateRestored != null) {
//                        if (isMultiSpinner) {
//                            spinnerMultipleSelection.add(i);
//                        } else {
//                            spinnerSelection = i;
//                            spinnerAnswers.setSelection(spinnerSelection);
//                        }
//                    }
//
//                }
//                if (isMultiSpinner) {
//                    int[] array = new int[spinnerMultipleSelection.size()];
//                    for (int i = 0; i < spinnerMultipleSelection.size(); i++) {
//                        array[i] = spinnerMultipleSelection.get(i);
//                    }
//                    multiSelectionSpinner.setSelection(array);
//                }
//                break;
            case ElementSubtype.TABLE:
                AnswerState[][] answersTableState = adapterTable.getmAnswersState();

                for (int i = 0; i < answersTableState.length; i++) {
                    for (int k = 0; k < answersTableState[i].length; k++) {
                        ElementPassedR answerStateRestored = null;
                        try {
                            answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answersTableState[i][k].getRelative_id());
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
            case ElementSubtype.SCALE:
                List<AnswerState> answerStatesAdapter = adapterScale.getAnswers();
                List<AnswerState> answerStatesRestored = new ArrayList<>();
                int lastSelectedPosition = 0;
                for (int i = 0; i < answerStatesAdapter.size(); i++) {
                    AnswerState answerStateNew = new AnswerState();
                    ElementPassedR answerStateRestored = null;
                    try {
                        answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answerStatesAdapter.get(i).getRelative_id());
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
            case ElementSubtype.PAGE:
            case ElementSubtype.PAGEVIEW:
            case ElementSubtype.SELECT:
                Map<Integer, List<AnswerState>> pageAnswersStates = pageAdapter.getAnswers();
                for (Map.Entry<Integer, List<AnswerState>> answerStates : pageAnswersStates.entrySet()) {
                    for (AnswerState state : answerStates.getValue()) {
                        ElementPassedR answerStateRestored = null;
                        try {
                            answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), state.getRelative_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (answerStateRestored != null) {
                            state.setChecked(true);
                        }
                    }
                }
                pageAdapter.setAnswers(pageAnswersStates);
                break;
        }

    }

    private void loadFromCard(List<CardItem> items) {
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
                } else {
                    answerStateNew.setChecked(false);
                    answerStateNew.setData("");
                }

                answerStateNew.setRelative_id(answerStatesAdapter.get(i).getRelative_id());
                answerStatesRestored.add(answerStateNew);
            }

//            for (int i = 0; i < answerStatesRestored.size(); i++) {
//                if(answerStatesRestored.get(i).is)
//            }

            adapterList.setAnswers(answerStatesRestored);
            adapterList.setRestored(true);
            adapterList.notifyDataSetChanged();
        }
    }

    private Map<Integer, CardItem> convertCardsListToMap(List<CardItem> list) {
        Map<Integer, CardItem> map = new HashMap<>();
        for (CardItem cardItem : list) {
            map.put(cardItem.getId(), cardItem);
        }
        return map;
    }

    private Map<Integer, AnswerState> convertAnswersListToMap(List<AnswerState> list) {
        Map<Integer, AnswerState> map = new HashMap<>();
        for (AnswerState cardItem : list) {
            map.put(cardItem.getRelative_id(), cardItem);
        }
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
    }

    @Override
    public boolean onBackPressed() {
//        if (canBack)
//            onClick(btnPrev);
        return true;
    }

    public boolean saveQuestionnaire(boolean aborted) {
        stopAllRecording();
        if (!aborted || getMainActivity().getConfig().isSaveAborted()) {
            if (saveQuestionnaireToDatabase(getQuestionnaire(), aborted)) {
                try {
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
        return true;
    }

    public void exitQuestionnaire() {
        stopAllRecording();
        try {
            getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
            getDao().clearCurrentQuestionnaireR();
            getDao().clearElementPassedR();
            getDao().clearPrevElementsR();
            getMainActivity().setCurrentQuestionnaireNull();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        activateButtons();
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.exit_quiz_header)
                    .setMessage(getMainActivity().getConfig().isSaveAborted() ? R.string.exit_questionnaire_with_saving_warning : R.string.exit_questionnaire_warning)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> {
                        if (getMainActivity().getConfig().isSaveAborted()) {
//                            showScreensaver(true);
                            if (saveQuestionnaire(true)) {
                                showToast(getString(R.string.save_questionnaire));

                                try {
                                    getDao().clearCurrentQuestionnaireR();
                                    getDao().clearElementPassedR();
                                    getDao().clearPrevElementsR();
                                    getMainActivity().setCurrentQuestionnaireNull();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    hideScreensaver();
                                    activateButtons();
                                }
                                exitQuestionnaire();
                            } else {
                                hideScreensaver();
                                activateButtons();
                            }
                        } else {
                            try {
                                getDao().clearCurrentQuestionnaireR();
                                getDao().clearElementPassedR();
                                getDao().clearPrevElementsR();
                                getDao().deleteElementDatabaseModelByToken(activity.getCurrentQuestionnaireForce().getToken());
                                getMainActivity().setCurrentQuestionnaireNull();
                            } catch (Exception e) {
                                e.printStackTrace();
                                hideScreensaver();
                                activateButtons();
                            }
                            dialog.dismiss();
                            exitQuestionnaire();
                        }
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    private void activateButtons() {
        try {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                getMainActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            btnPrev.setEnabled(true);
                            btnExit.setEnabled(true);
                            btnNext.setEnabled(true);

                            btnPrev.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(activity), R.drawable.button_background_avia));
                            btnExit.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(activity), R.drawable.button_background_avia));
                            btnNext.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(activity), R.drawable.button_background_avia));

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
                btnPrev.setEnabled(false);
                btnExit.setEnabled(false);
                btnNext.setEnabled(false);

                btnPrev.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray_avia));
                btnExit.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray_avia));
                btnNext.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray_avia));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showInfoDialog() {
        dialogBuilder = new AlertDialog.Builder(getMainActivity());
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_info_auto : R.layout.dialog_info, null);
        TextView dQuota1 = layoutView.findViewById(R.id.quota_1);
        TextView dQuota2 = layoutView.findViewById(R.id.quota_2);
        TextView dQuota3 = layoutView.findViewById(R.id.quota_3);
        LinearLayout cont = layoutView.findViewById(R.id.cont);

        cont.setOnClickListener(v -> infoDialog.dismiss());

        dQuota1.setTextColor(getResources().getColor(R.color.black));
        dQuota2.setTextColor(getResources().getColor(R.color.black));
        dQuota1.setText(getString(R.string.label_login, getMainActivity().getCurrentUser().getLogin()));
        dQuota2.setText(getString(R.string.label_inter, getUserName()));
        dQuota3.setText(getString(R.string.label_project, getMainActivity().getConfig().getProjectInfo().getName()));

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
            deactivateButtons();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (saveElement()) {
                    try {
                        if (answerType.equals(ElementSubtype.END)) {
                            nextElementId = currentElement.getElementOptionsR().getJump();
                        }
                        if (nextElementId == null) {
                            showRestartDialog();
                        } else if (nextElementId == 0) {
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
                        } else {
                            checkAndLoadNext();
                            updatePrevElement();
                        }
                    } catch (Exception e) {
                        activateButtons();
                    }
                } else {
                    activateButtons();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                activateButtons();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkAndLoadNext() {
        if (nextElementId != null && !nextElementId.equals(0) && !nextElementId.equals(-1)) {
            if (checkConditions(getElement(nextElementId))) {
                checkHidden();
                TransFragment fragment = new TransFragment();
                fragment.setStartElement(nextElementId);
                stopRecording();
                replaceFragment(fragment);
            } else {
                checkAndLoadNext();
            }
        } else {
            hideScreensaver();
            activateButtons();
        }
    }

    private void cyclecheck() {
        if (nextElementId != null && !nextElementId.equals(0) && !nextElementId.equals(-1)) {
            if (checkConditions(getElement(nextElementId))) {
                TransFragment fragment = new TransFragment();
                fragment.setStartElement(nextElementId);
                stopRecording();
                replaceFragment(fragment);
            } else {
                checkAndLoadNext();
            }
        } else {
            activateButtons();
        }
    }

    private void startRecording() {
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
    }

    private void stopRecording() {
        MainActivity activity = getMainActivity();
        if (activity != null && activity.getConfig().isAudio() && currentElement.getElementOptionsR().isRecord_sound() && !activity.getConfig().isAudioRecordAll()) {
            try {
                activity.stopRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void st(String notes) {
//        MainActivity.showTime(notes);
    }

    @SuppressLint("RestrictedApi")
    private void showCardDialog() {
        dialogBuilder = new AlertDialog.Builder(getMainActivity());
        View layoutView = getLayoutInflater().inflate(R.layout.dialog_card, null);

        View mCloseBtn = layoutView.findViewById(R.id.view_close);
        ListView listView = layoutView.findViewById(R.id.card_list);

        mCloseBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
        });

        int counter = 1;
        if (currentElement.getSubtype().equals(ElementSubtype.TABLE)) {
            List<String> values = new ArrayList<>();
            for (ElementItemR element : answersList) {
                if (element.getElementOptionsR() != null && element.getElementOptionsR().isShow_in_card()) {
                    values.add(counter + ". " + titlesMap.get(element.getRelative_id()).getTitle());
                    counter++;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getMainActivity(),
                    getMainActivity().isAutoZoom() ? R.layout.holder_table_card_auto : R.layout.holder_table_card, android.R.id.text1, values);
            listView.setAdapter(adapter);
            mCloseBtn.setOnClickListener(v -> {
                infoDialog.dismiss();
            });
        } else {
            List<CardItem> items = new ArrayList<>();
            Map<Integer, AnswerState> answerStateMap = convertAnswersListToMap(adapterList.getAnswers());
            for (ElementItemR element : answersList) {
                if (element.getElementOptionsR() != null && element.getElementOptionsR().isShow_in_card()) {
                    String title = counter + ". " + Objects.requireNonNull(titlesMap.get(element.getRelative_id())).getTitle();
                    items.add(new CardItem(element.getRelative_id(), title, "",
                            element.getElementOptionsR().isUnchecker(),
                            false,
                            element.getElementOptionsR().getOpen_type(),
                            element.getElementOptionsR().getPlaceholder(),
                            element.getElementOptionsR().isUnnecessary_fill_open(),
                            element.getElementOptionsR().isAutoChecked()));
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

    public boolean canShow(ElementItemR[][] tree, List<Integer> passedElementsId, Integer relativeId, Integer order) {

        if (tree == null || order == null || relativeId == null) {
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
//        ExpressionUtils expressionUtilsTest = new ExpressionUtils(getMainActivity());
//        Log.d("T-L.ElementFragment", "?????? checkHidden: " + expressionUtilsTest.checkHiddenExpression(null));
        //============================================================================
        ElementItemR nextElement = getElement(nextElementId);
        if (nextElement.getSubtype().equals(ElementSubtype.HIDDEN)) {
            boolean saved = false;
            for (PrevElementsR prevElement : prevList) {
                if (prevElement.getPrevId().equals(nextElementId)) {
                    saved = true;
                    break;
                }
            }
            if (!saved) {
                List<AnswerState> answerStatesHidden = new ArrayList<>();
                List<ElementItemR> answers = nextElement.getElements();
                List<ElementItemR> answersHidden = new ArrayList<>();
                ExpressionUtils expressionUtils = new ExpressionUtils(getMainActivity());
                boolean hiddenInQuotaBox = false;
                int enabledCounter = 0;

                //=========================================================================================================

                if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                        getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {

                    hiddenInQuotaBox = true;
                    List<Integer> passedQuotaBlock = getPassedQuotasBlock(nextElement.getElementOptionsR().getOrder());
                    ElementItemR[][] quotaTree = getMainActivity().getTree(null);
                    Integer order = nextElement.getElementOptionsR().getOrder();

                    for (int index = 0; index < answers.size(); index++) {
                        answers.get(index).setEnabled(canShow(quotaTree, passedQuotaBlock, answers.get(index).getRelative_id(), order));
                    }
                }

                for (ElementItemR answer : answers) {
                    String expression = answer.getElementOptionsR().getPrev_condition();
                    if (expression == null || expression.length() == 0 || expressionUtils.checkHiddenExpression(expression)) {
                        if (hiddenInQuotaBox) {
                            answersHidden.add(answer);
                            if (answer.isEnabled()) enabledCounter++;
                        }
                        answerStatesHidden.add(new AnswerState(answer.getRelative_id(), true, answer.getElementOptionsR().getTitle()));
                    }
                }

                if (hiddenInQuotaBox) {
                    if (answersHidden.size() == 1 && enabledCounter == 0) {
                        String message = "Квота по варианту ответа \"" + answersHidden.get(0).getElementOptionsR().getTitle() + "\" закончилась";
                        showHiddenExitAlertDialog(message);
                    } else if (answersHidden.size() != enabledCounter) {
                        String message = "";
                        for (ElementItemR answer : answersHidden) {
                            if (answer.isEnabled())
                                message = message + "\"" + answer.getElementOptionsR().getTitle() + "\" - будет выбран\n";
                            else
                                message = message + "\"" + answer.getElementOptionsR().getTitle() + "\" квота закончилась - не будет выбран\n";
                        }
                        showHiddenAlertDialog(message, nextElement, answerStatesHidden, answersHidden);
                    }
                } else {
                    saveHidden(nextElement, answerStatesHidden);
                }
            }
            nextElementId = nextElement.getElementOptionsR().getJump();
            checkAndLoadNext();
        }
    }

    private void saveHidden(ElementItemR nextElement, List<AnswerState> answerStatesHidden) {
        ElementPassedR elementPassedR = new ElementPassedR();
        elementPassedR.setRelative_id(nextElement.getRelative_id());
        elementPassedR.setProject_id(nextElement.getProjectId());
        elementPassedR.setToken(getQuestionnaire().getToken());
        elementPassedR.setDuration(1L);
        elementPassedR.setFrom_quotas_block(false);

        getDao().insertElementPassedR(elementPassedR);
        getDao().setWasElementShown(true, nextElement.getRelative_id(), nextElement.getUserId(), nextElement.getProjectId());

        if (answerStatesHidden.size() > 0)
            for (int i = 0; i < answerStatesHidden.size(); i++) {
                if (answerStatesHidden.get(i).isChecked()) {
                    ElementPassedR answerPassedR = new ElementPassedR();
                    answerPassedR.setRelative_id(answerStatesHidden.get(i).getRelative_id());
                    answerPassedR.setProject_id(nextElement.getProjectId());
                    answerPassedR.setToken(getQuestionnaire().getToken());
                    answerPassedR.setValue(answerStatesHidden.get(i).getData());
                    answerPassedR.setFrom_quotas_block(false);

                    try {
                        getDao().insertElementPassedR(answerPassedR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        getDao().insertPrevElementsR(new PrevElementsR(nextElementId, nextElement.getElementOptionsR().getJump()));
    }

    private void showHiddenExitAlertDialog(String message) {
        stopAllRecording();
        try {
            getDao().clearCurrentQuestionnaireR();
            getDao().clearElementPassedR();
            getDao().clearPrevElementsR();
            getDao().deleteElementDatabaseModelByToken(getMainActivity().getCurrentQuestionnaireForce().getToken());
            getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
            getMainActivity().setCurrentQuestionnaireNull();
        } catch (Exception e) {
            e.printStackTrace();
            activateButtons();
        }

        if (getMainActivity() != null && !getMainActivity().isFinishing()) {
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.exit_quiz_header)
                    .setMessage(message)
                    .setPositiveButton(R.string.view_OK, (dialog, which) -> {
                        dialog.dismiss();
                        getMainActivity().restartHome();
                    })
                    .show();
        }
    }

    private void showHiddenAlertDialog(String message, ElementItemR nextElement, List<AnswerState> answerStatesHidden, List<ElementItemR> answersHidden) {
        if (getMainActivity() != null && !getMainActivity().isFinishing()) {
            new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.hidden_quotas_header)
                    .setMessage(message)
                    .setPositiveButton(R.string.view_continue, (dialog, which) -> {
                        List<AnswerState> answersStatesEnabled = new ArrayList<>();
                        for (int index = 0; index < answersHidden.size(); index++) {
                            if (answersHidden.get(index).isEnabled()) {
                                answersStatesEnabled.add(answerStatesHidden.get(index));
                            }
                        }
                        saveHidden(nextElement, answersStatesEnabled);
                        nextElementId = nextElement.getElementOptionsR().getJump();
                        dialog.dismiss();
                        checkAndLoadNext();
                    })
                    .setNegativeButton(R.string.view_cancel, (dialog, which) -> dialog.dismiss())
                    .setNeutralButton(R.string.view_finish_quiz, (dialog, which) -> {
                        stopAllRecording();
                        try {
                            getDao().clearCurrentQuestionnaireR();
                            getDao().clearElementPassedR();
                            getDao().clearPrevElementsR();
                            getDao().deleteElementDatabaseModelByToken(getMainActivity().getCurrentQuestionnaireForce().getToken());
                            getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
                            getMainActivity().setCurrentQuestionnaireNull();
                        } catch (Exception e) {
                            e.printStackTrace();
                            activateButtons();
                        }
                        dialog.dismiss();
                        getMainActivity().restartHome();
                    })
                    .show();
        }
    }
}

