package pro.quizer.quizer3.view.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.multispinner.MultiSelectSpinner;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.ListQuestionAdapter;
import pro.quizer.quizer3.adapter.ScaleQuestionAdapter;
import pro.quizer.quizer3.adapter.TableQuestionAdapter;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.utils.ConditionUtils;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementFragment extends ScreenFragment implements View.OnClickListener, ListQuestionAdapter.OnAnswerClickListener, ScaleQuestionAdapter.OnAnswerClickListener, TableQuestionAdapter.OnTableAnswerClickListener {

    private Toolbar toolbar;
    private Button btnNext;
    private Button btnPrev;
    private Button btnExit;
    private RelativeLayout cont;
    private LinearLayout unhideCont;
    private LinearLayout titleCont1;
    private LinearLayout titleCont2;
    private LinearLayout titleImagesCont1;
    private LinearLayout titleImagesCont2;
    private LinearLayout questionCont;
    private LinearLayout questionImagesCont;
    private LinearLayout spinnerCont;
    private LinearLayout infoCont;
    private FrameLayout tableCont;
    private TextView tvUnhide;
    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvQuestion;
    private TextView tvTitleDesc1;
    private TextView tvTitleDesc2;
    private TextView tvQuestionDesc;
    private WebView infoText;
    private RecyclerView rvAnswers;
    private RecyclerView rvScale;
    private Spinner spinnerAnswers;
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
    private ImageView closeImage1;
    private ImageView closeImage2;
    private ImageView closeQuestion;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog infoDialog;

    private boolean isQuestionHided = false;
    private boolean hasQuestionImage = false;
    private boolean isPrevBtnPressed = false;
    private boolean isExit = false;
    private int currentQuestionId;
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
    private boolean isTitle2Hided = false;
    private boolean isRestored = false;
    private boolean isMultiSpinner = false;
    private boolean conditions = false;

    private ListQuestionAdapter adapterList;
    private ScaleQuestionAdapter adapterScale;
    private ArrayAdapter adapterSpinner;
    private TableQuestionAdapter adapterTable;
//    private MultiSelectionSpinner multiSelectionSpinner;
    private MultiSelectSpinner multiSelectionSpinner;
    private List<AnswerState> savedAnswerStates;
    private List<Integer> quotaBlock;


    private final String KEY_RECYCLER_STATE = "recycler_state";

    public ElementFragment() {
        super(R.layout.fragment_element);
    }

    public ElementFragment setStartElement(Integer startElementId) {
        this.startElementId = startElementId;
        return this;
    }

    public ElementFragment setStartElement(Integer startElementId, boolean restored) {
        this.startElementId = startElementId;
        this.isRestored = restored;
        return this;
    }

    @Override
    protected void onReady() {

        toolbar = findViewById(R.id.toolbar);
        cont = (RelativeLayout) findViewById(R.id.cont_element_fragment);
        unhideCont = (LinearLayout) findViewById(R.id.unhide_cont);
        titleCont1 = (LinearLayout) findViewById(R.id.title_cont_1);
        titleCont2 = (LinearLayout) findViewById(R.id.title_cont_2);
        titleImagesCont1 = (LinearLayout) findViewById(R.id.title_images_cont_1);
        titleImagesCont2 = (LinearLayout) findViewById(R.id.title_images_cont_2);
        questionCont = (LinearLayout) findViewById(R.id.question_cont);
        questionImagesCont = (LinearLayout) findViewById(R.id.question_images_cont);
        spinnerCont = (LinearLayout) findViewById(R.id.spinner_cont);
        infoCont = (LinearLayout) findViewById(R.id.info_cont);
        tableCont = (FrameLayout) findViewById(R.id.table_cont);
        rvAnswers = (RecyclerView) findViewById(R.id.answers_recyclerview);
        rvScale = (RecyclerView) findViewById(R.id.scale_recyclerview);
        spinnerAnswers = (Spinner) findViewById(R.id.answers_spinner);
        tableLayout = (AdaptiveTableLayout) findViewById(R.id.table_question_layout);
        tvUnhide = (TextView) findViewById(R.id.unhide_title);
        tvTitle1 = (TextView) findViewById(R.id.title_1);
        tvTitle2 = (TextView) findViewById(R.id.title_2);
        tvTitleDesc1 = (TextView) findViewById(R.id.title_desc_1);
        tvTitleDesc2 = (TextView) findViewById(R.id.title_desc_2);
        tvQuestion = (TextView) findViewById(R.id.question);
        tvQuestionDesc = (TextView) findViewById(R.id.question_desc);
        infoText = (WebView) findViewById(R.id.info_text);
        title1Image1 = (ImageView) findViewById(R.id.title_1_image_1);
        title1Image2 = (ImageView) findViewById(R.id.title_1_image_2);
        title1Image3 = (ImageView) findViewById(R.id.title_1_image_3);
        title2Image1 = (ImageView) findViewById(R.id.title_2_image_1);
        title2Image2 = (ImageView) findViewById(R.id.title_2_image_2);
        title2Image3 = (ImageView) findViewById(R.id.title_2_image_3);
        questionImage1 = (ImageView) findViewById(R.id.question_image_1);
        questionImage2 = (ImageView) findViewById(R.id.question_image_2);
        questionImage3 = (ImageView) findViewById(R.id.question_image_3);
        closeImage1 = (ImageView) findViewById(R.id.image_close_1);
        closeImage2 = (ImageView) findViewById(R.id.image_close_2);
        closeQuestion = (ImageView) findViewById(R.id.question_close);
        btnNext = (Button) findViewById(R.id.next_btn);
        btnPrev = (Button) findViewById(R.id.back_btn);
        btnExit = (Button) findViewById(R.id.exit_btn);

//        tvUnhide.setTypeface(Fonts.getFuturaPtBook());
//        tvTitle1.setTypeface(Fonts.getFuturaPtBook());
//        tvTitle2.setTypeface(Fonts.getFuturaPtBook());
//        tvQuestion.setTypeface(Fonts.getFuturaPtBook());
//        btnNext.setTypeface(Fonts.getFuturaPtBook());
//        btnPrev.setTypeface(Fonts.getFuturaPtBook());
//        btnExit.setTypeface(Fonts.getFuturaPtBook());
        btnNext.setTransformationMethod(null);
        btnPrev.setTransformationMethod(null);
        btnExit.setTransformationMethod(null);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        closeImage1.setOnClickListener(this);
        titleCont1.setOnClickListener(this);
        closeImage2.setOnClickListener(this);
        titleCont2.setOnClickListener(this);
        closeQuestion.setOnClickListener(this);
        unhideCont.setOnClickListener(this);
        cont.setOnClickListener(this);
        tvQuestion.setOnClickListener(this);

        deactivateButtons();

//        toolbar.setTitle(getCurrentUser().getConfigR().getProjectInfo().getName());
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.showOptionsView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MainFragment.showDrawer();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showInfoDialog();
            }
        });
        toolbar.showInfoView();

        cont.startAnimation(Anim.getAppear(getContext()));
        btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnPrev.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnExit.startAnimation(Anim.getAppearSlide(getContext(), 500));

        MainFragment.enableSideMenu(false);
        showScreensaver("Подождите, \nидет загрузка элемента анкеты", true);
//        Log.d(TAG, "====== initCurrentElements() for: " + startElementId);
        initCurrentElements();
//        Log.d(TAG, "====== loadResumedData()");
        loadResumedData();
//        Log.d(TAG, "====== initQuestion();\n");
        initQuestion();
//        Log.d(TAG, "====== checkConditions()");
        if(conditions) {
            if (currentElement != null) {
                if (checkConditions(currentElement)) {
//            Log.d(TAG, "====== setQuestionType()");
                    setQuestionType();
//            Log.d(TAG, "====== initViews()");
                    initViews();
//            Log.d(TAG, "====== updateCurrentQuestionnaire()");
                    updateCurrentQuestionnaire();
//            Log.d(TAG, "====== initRecyclerView()");
                    initRecyclerView();
                    if (isRestored || wasReloaded()) {
//                Log.d(TAG, "====== loadSavedData()");
                        loadSavedData();
                    }
                } // else showNextFragment(nextElementId);
                hideScreensaver();
                activateButtons();
                try {
                    getMainActivity().activateExitReminder();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                exitQuestionnaire();
            }
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
            deactivateButtons();
//            if (!isNextBtnPressed) {
//                isNextBtnPressed = true;
            if (saveElement()) {
//                Log.d(TAG, "onClick NEXT: " + nextElementId);
                if (nextElementId == null || nextElementId == 0) {
                    saveQuestionnaire(false);
                } else if (nextElementId == -1) {
                    if (getCurrentUser().getConfigR().isSaveAborted()) {
                        saveQuestionnaire(true);
                    } else {
                        exitQuestionnaire();
                    }
                } else {
                    TransFragment fragment = new TransFragment();
                    fragment.setStartElement(nextElementId);
                    replaceFragment(fragment);
                }
            } else {
                activateButtons();
            }
//            }
        } else if (view == btnPrev) {
            deactivateButtons();
            TransFragment fragment = new TransFragment();
            List<PrevElementsR> prevList;
            if (prevElementId != 0) {
                prevList = getQuestionnaire().getPrev_element_id();
                prevElementId = prevList.get(prevList.size() - 1).getPrevId();
                prevList.remove(prevList.size() - 1);
                try {
                    getDao().setPrevElement(prevList);
                } catch (Exception e) {
                    showToast(getString(R.string.set_last_element_error));
                    return;
                }
                fragment.setStartElement(prevElementId, true);
                replaceFragmentBack(fragment);
            } else {
                showExitPoolAlertDialog();
            }


        } else if (view == btnExit) {
            deactivateButtons();
            checkAbortedBox();
            if (getCurrentUser().getConfigR().isSaveAborted() && hasAbortedBox() && !getQuestionnaire().isIn_aborted_box()) {
                try {
                    getDao().setCurrentQuestionnaireInAbortedBox(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                TransFragment fragment = new TransFragment();
                fragment.setStartElement(getAbortedBoxRelativeId());
                replaceFragment(fragment);
            } else {
                showExitPoolAlertDialog();
            }
        } else if (view == closeImage1 || view == titleCont1) {
            titleCont1.setVisibility(View.GONE);
            unhideCont.setVisibility(View.VISIBLE);
            isTitle1Hided = true;
        } else if (view == closeImage2 || view == titleCont2) {
            titleCont2.setVisibility(View.GONE);
            unhideCont.setVisibility(View.VISIBLE);
            isTitle2Hided = true;
        } else if (view == unhideCont) {
            if (isTitle1Hided) {
                isTitle1Hided = false;
                titleCont1.setVisibility(View.VISIBLE);
            }
            if (isTitle2Hided) {
                isTitle2Hided = false;
                titleCont2.setVisibility(View.VISIBLE);
            }
            unhideCont.setVisibility(View.GONE);
        } else if (view == closeQuestion || view == tvQuestion) {
            if (!isQuestionHided) {
                closeQuestion.setImageResource(R.drawable.arrow_down_white);
                tvQuestion.setText(getString(R.string.view_reopen));
                questionImagesCont.setVisibility(View.GONE);
                tvQuestionDesc.setVisibility(View.GONE);
                isQuestionHided = true;
            } else {
                tvQuestion.setText(currentElement.getElementOptionsR().getTitle());
                closeQuestion.setImageResource(R.drawable.ico_close_white);
                if (hasQuestionImage) questionImagesCont.setVisibility(View.VISIBLE);
                if (currentElement.getElementOptionsR() != null && currentElement.getElementOptionsR().getDescription() != null)
                    tvQuestionDesc.setVisibility(View.VISIBLE);
                isQuestionHided = false;
            }
        }
//        else if (view == tvQuestion) {
//            if (isQuestionHided) {
//                tvQuestion.setText(currentElement.getElementOptionsR().getTitle());
//                closeQuestion.setImageResource(R.drawable.ico_close_white);
//                if (hasQuestionImage) questionImagesCont.setVisibility(View.VISIBLE);
//                if (currentElement.getElementOptionsR() != null && currentElement.getElementOptionsR().getDescription() != null)
//                    tvQuestionDesc.setVisibility(View.VISIBLE);
//                isQuestionHided = false;
//            }
//        }
    }

    private void initQuestion() {
//        Log.d(TAG, "initQuestion: OK ????????" + startElementId + " " +getCurrentElements().size());
        startTime = DateUtils.getCurrentTimeMillis();

        List<PrevElementsR> prevList;
        if (getQuestionnaire() == null) {
            initCurrentElements();
        }
        if (getQuestionnaire().getPrev_element_id() != null && getQuestionnaire().getPrev_element_id().size() > 0) {
            prevList = getQuestionnaire().getPrev_element_id();
            prevElementId = prevList.get(prevList.size() - 1).getPrevId();
        } else {
            prevElementId = 0;
        }

        if (startElementId == null) startElementId = 0;

        if (getCurrentElements() != null && getCurrentElements().size() > 0) {

//            for(ElementItemR elementItemR : getCurrentElements()) {
//                Log.d(TAG, "??????: element " + elementItemR.getRelative_id());
//            }

            if (startElementId != 0) {
                currentElement = getElement(startElementId);
            } else {
                currentElement = getCurrentElements().get(0);
            }
            conditions = checkConditions(currentElement);
            if (conditions) {

                boolean found = false;

                for (int i = 0; i < getCurrentElements().size(); i++) {
//                if(found) {
//                    Log.d(TAG, "initQuestion: 3 " + getCurrentElements().get(i).getRelative_id());
//                }
                    if (getCurrentElements().get(i).getRelative_id().equals(currentElement.getRelative_id())) {
                        found = true;
//                    Log.d(TAG, "initQuestion 1: ?????" + found);
                    }
                    if (found) {
                        if (!getCurrentElements().get(i).getType().equals(ElementType.BOX) || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.TABLE)) {
                            startElementId = getCurrentElements().get(i).getRelative_id();
                            currentElement = getCurrentElements().get(i);
                            break;
                        }
                    }
                }
//            Log.d(TAG, "initQuestion: 1");
//            if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0) {
//                if (getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
//                    Log.d(TAG, "initQuestion: 2");
//            MainActivity activity = (MainActivity) getActivity();
//                    ElementItemR[][] quotaTree = activity.getTree();
//                    if (quotaTree != null) {
//
//                        Log.d(TAG, "=============== Final Quotas ======================");
//                        try {
//                            for (int i = 0; i < quotaTree[0].length; i++) {
//                                Log.d(TAG, quotaTree[0][i].getElementOptionsR().getTitle() + " " + quotaTree[0][i].getDone() + "/" + quotaTree[0][i].getLimit() + "/" + quotaTree[0][i].isEnabled() + " | " +
//                                        quotaTree[1][i].getElementOptionsR().getTitle() + " " + quotaTree[1][i].getDone() + "/" + quotaTree[1][i].getLimit() + "/" + quotaTree[1][i].isEnabled() + " | " +
//                                        quotaTree[2][i].getElementOptionsR().getTitle() + " " + quotaTree[2][i].getDone() + "/" + quotaTree[2][i].getLimit() + "/" + quotaTree[2][i].isEnabled() + " | "
//
//                                );
//                            }
//                        } catch (Exception e) {
//                            Log.d(TAG, "Не тестовый проект!");
//                        }
//                    }
//
//                        quotaBlock = getQuestionnaire().getPassed_quota_block();

//                    }
//                }
//            }

//            Log.d(TAG, "==================== initQuestion: " + currentElement.getRelative_id());
//            showElementsQuery();
            }
        } else {
            Log.d(TAG, "initQuestions: ERROR! (empty list)");
        }
    }

    private void setQuestionType() {
//        Log.d(TAG, "???????????: " + currentElement.getRelative_id());
        if (currentElement.getSubtype().equals(ElementSubtype.LIST)) {
            if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                    getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
                answerType = ElementSubtype.QUOTA;
            } else {
                answerType = ElementSubtype.LIST;
            }
        } else if (currentElement.getSubtype().equals(ElementSubtype.SELECT)) {
            answerType = ElementSubtype.SELECT;
        } else if (currentElement.getSubtype().equals(ElementSubtype.TABLE)) {
            answerType = ElementSubtype.TABLE;
        } else if (currentElement.getSubtype().equals(ElementSubtype.SCALE)) {
            answerType = ElementSubtype.SCALE;
        } else if (currentElement.getSubtype().equals(ElementSubtype.HTML)) {
            answerType = ElementSubtype.HTML;
        }
    }

    private void initViews() {
        tvQuestion.setText(currentElement.getElementOptionsR().getTitle());
        if (currentElement.getElementOptionsR().getDescription() != null) {
            tvQuestionDesc.setVisibility(View.VISIBLE);
            tvQuestionDesc.setText(currentElement.getElementOptionsR().getDescription());
        }
        if (currentElement.getRelative_parent_id() != null) {
            ElementItemR parentElement = null;
            try {
                parentElement = getElement(currentElement.getRelative_parent_id());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (parentElement != null) {
                if (parentElement.getType().equals(ElementType.BOX) && !parentElement.isWas_shown()) {
                    getDao().setWasElementShown(true, parentElement.getRelative_id(), parentElement.getUserId(), parentElement.getProjectId());
                    titleCont2.setVisibility(View.VISIBLE);
                    UiUtils.setTextOrHide(tvTitle2, parentElement.getElementOptionsR().getTitle());
//                    tvTitle2.setText(parentElement.getElementOptionsR().getTitle());
                    if (parentElement.getElementOptionsR().getDescription() != null) {
                        tvTitleDesc2.setVisibility(View.VISIBLE);
                        tvTitleDesc2.setText(parentElement.getElementOptionsR().getDescription());
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
                            if (parentElement2.getType().equals(ElementType.BOX) && !parentElement2.isWas_shown()) {
                                getDao().setWasElementShown(true, parentElement2.getRelative_id(), parentElement2.getUserId(), parentElement2.getProjectId());
                                titleCont1.setVisibility(View.VISIBLE);
                                UiUtils.setTextOrHide(tvTitle1, parentElement2.getElementOptionsR().getTitle());
//                                tvTitle1.setText(parentElement2.getElementOptionsR().getTitle());
                                if (parentElement2.getElementOptionsR().getDescription() != null) {
                                    tvTitleDesc1.setVisibility(View.VISIBLE);
                                    tvTitleDesc1.setText(parentElement2.getElementOptionsR().getDescription());
                                }

                                showContent(parentElement2, titleImagesCont2);
                            }
                        }

                    }
                }
            }
        }

        showContent(currentElement, questionImagesCont);

        if (getCurrentUser().getConfigR().isPhotoQuestionnaire() && currentElement.getElementOptionsR().isTake_photo()) {
            shotPicture(getLoginAdmin(), getQuestionnaire().getToken(), currentElement.getRelative_id(), getCurrentUserId(), getQuestionnaire().getProject_id(), getCurrentUser().getLogin());
        }
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
        final String filePhotooPath = getFilePath(data);

        if (StringUtils.isEmpty(filePhotooPath)) {
            return;
        }

        cont.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);

        Picasso.with(getActivity())
                .load(new File(filePhotooPath))
                .into(view);
    }

    private void initRecyclerView() {
        answersList = new ArrayList<>();
        List<String> itemsList = new ArrayList<>();

//        Log.d(TAG, "???????====: " + currentElement.getRelative_id() + " " + answerType);
        if (answerType.equals(ElementSubtype.LIST) || answerType.equals(ElementSubtype.QUOTA)) {
            rvAnswers.setVisibility(View.VISIBLE);
        } else if (answerType.equals(ElementSubtype.SELECT)) {
            spinnerCont.setVisibility(View.VISIBLE);
        } else if (answerType.equals(ElementSubtype.TABLE)) {
            tableCont.setVisibility(View.VISIBLE);
        } else if (answerType.equals(ElementSubtype.HTML)) {
            questionCont.setVisibility(View.GONE);
            infoCont.setVisibility(View.VISIBLE);
            infoText.loadData(currentElement.getElementOptionsR().getData(), "text/html; charset=UTF-8", null);
        } else if (answerType.equals(ElementSubtype.SCALE)) {
            rvScale.setVisibility(View.VISIBLE);
        }

//        for (ElementItemR element : getCurrentElements()) {
//            if (element.getRelative_parent_id() == currentElement.getRelative_id()) {
//                answersList.add(element);
//                itemsList.add(element.getElementOptionsR().getTitle());
//            }
//        }

        answersList = currentElement.getElements();
        List<ElementItemR> checkedAnswersList = new ArrayList<>();
        for (int a = 0; a < answersList.size(); a++) {
            boolean check = checkConditions(answersList.get(a));
//            Log.d(TAG, "check: " + check);
            if (check) {
                checkedAnswersList.add(answersList.get(a));
            }
        }
//        Log.d(TAG, "checkedAnswersList: " + checkedAnswersList.size());

        if (checkedAnswersList == null || checkedAnswersList.size() == 0) {
            nextElementId = currentElement.getElementOptionsR().getJump();
            if (nextElementId == null) {
                nextElementId = answersList.get(0).getElementOptionsR().getJump();
            }
            if (nextElementId == 0 || nextElementId == null) {
                saveQuestionnaire(false);
                exitQuestionnaire();
            }
        } else {
//            Log.d(TAG, "answersList: " + answersList.size());
            answersList = checkedAnswersList;
        }

        for (ElementItemR element : answersList) {
            itemsList.add(element.getElementOptionsR().getTitle());
        }

//        Log.d(TAG, "?????????? initRecyclerView: " + answerType);
        if (answerType.equals(ElementSubtype.LIST)) {
            adapterList = new ListQuestionAdapter(getActivity(), currentElement, answersList,
                    null, null, this);
            rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
            rvAnswers.setAdapter(adapterList);
        } else if (answerType.equals(ElementSubtype.QUOTA)) {
//            Log.d(TAG, "initRecyclerView: quota answers size= " + answersList.size());
            MainActivity activity = (MainActivity) getActivity();
            adapterList = new ListQuestionAdapter(getActivity(), currentElement, answersList,
                    getPassedQuotasBlock(), activity.getTree(null), this);
            rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
            rvAnswers.setAdapter(adapterList);
        } else if (answerType.equals(ElementSubtype.SELECT)) {

            if(currentElement != null && currentElement.getElementOptionsR() != null && currentElement.getElementOptionsR().isRotation()) {
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

            Integer unchecker = null;
            for (int i = 0; i < answersList.size(); i++) {
                itemsList.add(answersList.get(i).getElementOptionsR().getTitle());
                if(answersList.get(i).getElementOptionsR().isUnchecker()) unchecker = i;
            }

            if (currentElement != null && currentElement.getElementOptionsR() != null && currentElement.getElementOptionsR().isPolyanswer()) {
                isMultiSpinner = true;

                multiSelectionSpinner = (MultiSelectSpinner) findViewById(R.id.answers_multi_spinner);
                multiSelectionSpinner.setVisibility(View.VISIBLE);
                multiSelectionSpinner.setItems(itemsList);
//                multiSelectionSpinner.setAdapter(getContext());

                multiSelectionSpinner.hasNoneOption(true, unchecker);
                multiSelectionSpinner.setSelection(new int[]{});
                multiSelectionSpinner.setListener(new MultiSelectSpinner.OnMultipleItemsSelectedListener() {
                    @Override
                    public void selectedIndices(List<Integer> indices) {
                        if (isRestored) {
                            if (!indices.equals(spinnerMultipleSelection)) {
                                try {
                                    isRestored = false;
                                    int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                                    getDao().deleteOldElementsPassedR(id);
                                    showToast("Данные изменены");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        spinnerMultipleSelection = indices;
                    }

                    @Override
                    public void selectedStrings(List<String> strings) {

                    }
                });


//                multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.answers_multi_spinner);
//                multiSelectionSpinner.setVisibility(View.VISIBLE);

//                multiSelectionSpinner.setItems(itemsList, unchecker);
//                multiSelectionSpinner.setSelection(new int[]{});
//                multiSelectionSpinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
//                    @Override
//                    public void selectedIndices(List<Integer> indices) {
//                        if (isRestored) {
//                            if (!indices.equals(spinnerMultipleSelection)) {
//                                try {
//                                    isRestored = false;
//                                    int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
//                                    getDao().deleteOldElementsPassedR(id);
//                                    showToast("Данные изменены");
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                        spinnerMultipleSelection = indices;
//                    }
//
//                    @Override
//                    public void selectedStrings(List<String> strings) {
//
//                    }
//
//                });
//            ===============================================================================================
            } else {
                isMultiSpinner = false;

                itemsList.add(0, getString(R.string.select_spinner));
                adapterSpinner = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemsList) {

                    public int getCount() {
                        return (itemsList.size());
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }

                };
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAnswers.setVisibility(View.VISIBLE);
                spinnerAnswers.setAdapter(adapterSpinner);
                spinnerAnswers.setSelection(0);
                spinnerAnswers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long selectionId) {
                        if (position > 0) {
                            if (isRestored) {
                                if (position != spinnerSelection) {
                                    try {
                                        isRestored = false;
                                        int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                                        getDao().deleteOldElementsPassedR(id);
                                        showToast("Данные изменены");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            spinnerSelection = position;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        showToast("Выберите ответ (не выбрано)");
                    }
                });
            }
        } else if (answerType.equals(ElementSubtype.TABLE)) {
//            List<ElementItemR> questions = null;
//            questions = currentElement.getElements();
            //answersList = questionsList for TABLE
            adapterTable = new TableQuestionAdapter(currentElement, answersList, getActivity(), mRefreshRecyclerViewRunnable, this);
            tableLayout.setAdapter(adapterTable);
            tableLayout.setDrawingCacheEnabled(true);
        } else if (answerType.equals(ElementSubtype.SCALE)) {
            adapterScale = new ScaleQuestionAdapter(getActivity(), currentElement, answersList,
                    this);
            rvScale.setLayoutManager(new LinearLayoutManager(getContext()));
            rvScale.setAdapter(adapterScale);
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
//            Log.d(TAG, "checkConditions: " + element.getRelative_id());
            final ElementOptionsR options = element.getElementOptionsR();
            if (options != null && options.getPre_condition() != null) {
                final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), (MainActivity) getActivity());
//                Log.d(TAG, "!!!!!!!!!!!!!!!!!! showValue: " + showValue);
                if (showValue != ConditionUtils.CAN_SHOW) {
                    if (showValue != ConditionUtils.CANT_SHOW) {
                        nextElementId = showValue;
//                        Log.d(TAG, "checkConditions: 1 " + showValue);
                    } else {
//                        Log.d(TAG, "checkConditions: 2");
                        if (!element.equals(currentElement)) {
//                            Log.d(TAG, "checkConditions: 3");
                            return false;
                        }
                        nextElementId = options.getJump();
                        if (nextElementId == null) {
//                            Log.d(TAG, "checkConditions: 4");
                            List<ElementItemR> answers = element.getElements();
                            if (answers != null && answers.size() > 0) {
//                                Log.d(TAG, "checkConditions: 5");
                                try {
                                    nextElementId = answers.get(0).getElementOptionsR().getJump();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (nextElementId == null) {
//                                Log.d(TAG, "checkConditions: 6");
                                nextElementId = 0;
                            }
                        }
                    }
                    if (nextElementId.equals(0)) {
                        saveQuestionnaire(false);
                        exitQuestionnaire();
                        return false;
                    } else if (nextElementId.equals(-1)) {
//                        Log.d(TAG, "checkConditions: 8");
                        exitQuestionnaire();
                        return false;
                    } else {
//                        Log.d(TAG, "checkConditions: 9 / " + nextElementId);
//                    onClick(btnNext);
//                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
                        showNextFragment(nextElementId);
                        return false;
                    }
                }
            }
            return true;
        } else return false;
    }

    private void showNextFragment(int id) {
        TransFragment fragment = new TransFragment();
        fragment.setStartElement(id);
        replaceFragment(fragment);
    }

    private boolean saveElement() {
        boolean saved = false;
//        if(!isRestored) {
        if (answerType.equals(ElementSubtype.LIST) || answerType.equals(ElementSubtype.QUOTA) || answerType.equals(ElementSubtype.SCALE)) {
            List<AnswerState> answerStates = null;
            if (answerType.equals(ElementSubtype.SCALE)) {
                answerStates = adapterScale.getAnswers();
            } else {
                answerStates = adapterList.getAnswers();
            }
            if (answerStates != null && notEmpty(answerStates)) {
                for (AnswerState answerState : answerStates) {
                    if (answerState.isChecked()) {
                        nextElementId = getElement(answerState.getRelative_id()).getElementOptionsR().getJump();
                    }
                }

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

                elementPassedR.setFrom_quotas_block(false);

                try {
                    if (!isRestored) {
                        getDao().insertElementPassedR(elementPassedR);
                        getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                    }
                    saved = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    saved = false;
                }

                for (int i = 0; i < answerStates.size(); i++) {
                    if (answerStates.get(i).isChecked()) {

                        ElementPassedR answerPassedR = new ElementPassedR();
                        answerPassedR.setRelative_id(answerStates.get(i).getRelative_id());
                        answerPassedR.setProject_id(currentElement.getProjectId());
                        answerPassedR.setToken(getQuestionnaire().getToken());
                        answerPassedR.setValue(answerStates.get(i).getData());
                        if (answerType.equals(ElementSubtype.QUOTA)) {
                            answerPassedR.setFrom_quotas_block(true);
                        } else {
                            answerPassedR.setFrom_quotas_block(false);
                        }

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
        } else if (answerType.equals(ElementSubtype.SELECT)) {
            if (isMultiSpinner) {
                if (checkMultipleSpinner()) {
                    ElementPassedR elementPassedR = new ElementPassedR();
                    nextElementId = currentElement.getElementOptionsR().getJump();
                    if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                        nextElementId = currentElement.getElementOptionsR().getJump();
                        if (nextElementId == -2) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
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
                    for (int i = 0; i < spinnerMultipleSelection.size(); i++) {

                        ElementPassedR answerPassedR = new ElementPassedR();
                        answerPassedR.setRelative_id(answersList.get(spinnerMultipleSelection.get(i)).getRelative_id());
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

            } else {
                if (spinnerSelection != 0) {
                    ElementPassedR elementPassedR = new ElementPassedR();
                    nextElementId = answersList.get(spinnerSelection - 1).getElementOptionsR().getJump();
                    if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                        nextElementId = currentElement.getElementOptionsR().getJump();
                        if (nextElementId == -2) {
                            nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
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

                    ElementPassedR answerPassedR = new ElementPassedR();
                    answerPassedR.setRelative_id(answersList.get(spinnerSelection -1).getRelative_id());
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
        } else if (answerType.equals(ElementSubtype.TABLE)) {
            AnswerState[][] answerStates = adapterTable.getmAnswersState();
            if (answerStates != null && answerStates[0] != null) {
                for (int i = 0; i < answerStates.length; i++) {
                    String text = i + ": ";
                    for (int k = 0; k < answerStates[0].length; k++) {
                        text = text.concat(answerStates[i][k].getRelative_id() + "/" + answerStates[i][k].isChecked() + " ");
                    }
                }
            }
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

//                Log.d(TAG, "saveElement NEXT ELEMENT BEFORE: " + nextElementId);
//
//                if (!nextElementId.equals(0) && !nextElementId.equals(-1)) {
//                    Log.d(TAG, "conditions: " + checkConditions(getElement((nextElementId))));
//                    if(!checkConditions(getElement((nextElementId)))){
//                        ElementItemR nextElement = getElement(nextElementId);
//                        final ElementOptionsR options = nextElement.getElementOptionsR();
//                        final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), getMainActivity());
//                        Log.d(TAG, "saveElement CHECK TABLE: " + showValue);
//                        if (showValue != ConditionUtils.CAN_SHOW) {
//                            nextElementId = showValue;
//                        }
//                    }
//                }
//
//                Log.d(TAG, "saveElement NEXT ELEMENT AFTER: " + nextElementId);

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
        } else if (answerType.equals(ElementSubtype.HTML)) {
            ElementPassedR elementPassedR = new ElementPassedR();
            nextElementId = currentElement.getElementOptionsR().getJump();
            if (currentElement.getRelative_parent_id() != 0 && currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                nextElementId = currentElement.getElementOptionsR().getJump();
                if (nextElementId == -2) {
                    nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                }
            }

            ElementItemR nextElement = getElement(nextElementId);
            final ElementOptionsR options = nextElement.getElementOptionsR();
            final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), (MainActivity) getActivity());

            if (showValue != ConditionUtils.CAN_SHOW) {
                nextElementId = options.getJump();
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
        }
//        } else {
//            saved = true;
//        }

        if (saved) {
            updatePrevElement();
        }
//        showPassed();
        return saved;
    }

    private boolean checkMultipleSpinner() {
        if (spinnerMultipleSelection == null || spinnerMultipleSelection.size() < 1) {
            return false;
        }
        if (currentElement.getElementOptionsR().getMin_answers() != null) {
            if (spinnerMultipleSelection.size() < currentElement.getElementOptionsR().getMin_answers()) {
                Toast.makeText(getContext(), "Выберите минимум " + currentElement.getElementOptionsR().getMin_answers() + " ответов", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (currentElement.getElementOptionsR().getMax_answers() != null) {
            if (spinnerMultipleSelection.size() > currentElement.getElementOptionsR().getMax_answers()) {
                Toast.makeText(getContext(), "Выберите максимум " + currentElement.getElementOptionsR().getMax_answers() + " ответов", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public boolean notEmpty(List<AnswerState> answerStates) {

        int counter = 0;

        for (AnswerState state : answerStates) {

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

//            Log.d(TAG, "notEmpty: " + openType + " " +  state.getData());
            if (state.isChecked() && openType) {
                Log.d(TAG, "STATE: " + state.getRelative_id() + " " + state.getData());
                if (state.getData().equals("") || state.getData() == null) {
                    showToast(getString(R.string.empty_string_warning));
                    Log.d(TAG, "Empty: " + state.getRelative_id() + " " + state.getData());
                    return false;
                }
            }
            if (state.isChecked()) {
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
                showToast("Выберите максимум " + min + " ответа.");
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

    public void showPassed() {
        List<ElementPassedR> elementPassedRS = getDao().getAllElementsPassedR(getQuestionnaire().getToken());
        Log.d(TAG, "==========================================");
        for (ElementPassedR element : elementPassedRS) {
            Log.d(TAG, ">>>>>>>>>>>>>>> showPassed: " + element.getId());
        }
    }

    private void updatePrevElement() {
        List<PrevElementsR> prevList;
        if (getQuestionnaire().getPrev_element_id() != null) {
            prevList = getQuestionnaire().getPrev_element_id();
            prevList.add(new PrevElementsR(startElementId, nextElementId));

        } else {
            prevList = new ArrayList<>();
            prevList.add(new PrevElementsR(startElementId, nextElementId));
        }
        try {
            getDao().setPrevElement(prevList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long selectionId) {
//
//        if (position != answersList.size()) {
//            if (isRestored) {
//                if (position != spinnerSelection) {
//                    try {
//                        isRestored = false;
//                        int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
//                        getDao().deleteOldElementsPassedR(id);
//                        showToast("Данные изменены");
//                    } catch (Exception e) {
//                        e.printStackTrace();
////                        showToast("Ошибка очистки таблицы элементов.");
//                    }
//                }
//            }
//            spinnerSelection = position;
//        }
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        showToast("Выберите ответ (не выбрано)");
//    }

    @Override
    public void onAnswerClick(int position, boolean enabled, String answer) {
        if (isRestored) {
//            int id = currentElement.getId();
            try {
                isRestored = false;
                int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
                getDao().deleteOldElementsPassedR(id);
                showToast("Данные изменены");
            } catch (Exception e) {
                e.printStackTrace();
//                showToast("Ошибка очистки таблицы элементов.");
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
                showToast("Данные изменены");
//                showPassed();
            } catch (Exception e) {
                e.printStackTrace();
//                showToast("Ошибка очистки таблицы элементов.");
            }
        }
    }

    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
        @Override
        public void run() {
            UiUtils.hideKeyboard(getContext(), getView());
        }
    };

    public void restoreViews(Bundle bundle) {
        if (bundle != null) {
            Parcelable listState = bundle.getParcelable(KEY_RECYCLER_STATE);
            rvAnswers.getLayoutManager().onRestoreInstanceState(listState);
            adapterList.setAnswers((List<AnswerState>) bundle.getSerializable("LIST"));
            adapterList.setLastSelectedPosition(bundle.getInt("lastSelectedPosition"));
        }
    }

    public void restoreData(Bundle bundle) {
        if (bundle != null) {
            startTime = bundle.getLong("startTime");
            startElementId = bundle.getInt("startElementId");
            nextElementId = bundle.getInt("nextElementId", 0);
            Log.d(TAG, "restoreData: " + nextElementId);
            prevElementId = bundle.getInt("prevElementId");
            spinnerSelection = bundle.getInt("spinnerSelection");
            isTitle1Hided = bundle.getBoolean("isTitle1Hided");
            isTitle2Hided = bundle.getBoolean("isTitle2Hided");
            answerType = bundle.getString("answerType");
        }
    }

    public void loadResumedData() {
        if (isRestored) {
            try {
                List<PrevElementsR> prevList = getQuestionnaire().getPrev_element_id();
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
        if (answerType.equals(ElementSubtype.LIST) || answerType.equals(ElementSubtype.QUOTA)) {
            List<AnswerState> answerStatesAdapter = adapterList.getAnswers();
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
            adapterList.setAnswers(answerStatesRestored);
            adapterList.setLastSelectedPosition(lastSelectedPosition);
            adapterList.notifyDataSetChanged();

        } else if (answerType.equals(ElementSubtype.SELECT)) {

            spinnerSelection = -1;
            spinnerMultipleSelection = new ArrayList<>();

            for (int i = 0; i < answersList.size(); i++) {
                ElementPassedR answerStateRestored = null;
                try {
                    answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answersList.get(i).getRelative_id());
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
        } else if (answerType.equals(ElementSubtype.TABLE)) {
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
//            adapterTable.
        } else if (answerType.equals(ElementSubtype.SCALE)) {
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
        }
    }


    @Override
    public void onPause() {
        super.onPause();
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
    public void onDestroy() {
        super.onDestroy();

        //TODO save startTime
    }

    @Override
    public boolean onBackPressed() {
        onClick(btnPrev);
        return true;
    }

    public boolean saveQuestionnaire(boolean aborted) {
        stopRecording();
//        Log.d(TAG, "!!!!!!!!!!!!!!!!11111111 saveQuestionnaire: ");
        if (saveQuestionnaireToDatabase(getQuestionnaire(), aborted)) {
            try {
                getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
            } catch (Exception e) {
                e.printStackTrace();
            }
            showToast("Анкета сохранена");
            replaceFragment(new HomeFragment());
        } else {
            showToast("Ошибка сохранения анкеты. Попробуйте еще раз");
        }
        return true;
    }

    public void exitQuestionnaire() {
        stopRecording();
        try {
            getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
            getDao().clearCurrentQuestionnaireR();
            getDao().clearElementPassedR();
        } catch (Exception e) {
            e.printStackTrace();
        }
        replaceFragment(new HomeFragment());
    }

    private void stopRecording() {
        MainActivity activity = (MainActivity) getActivity();
        try {
            addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.stop_audio_recording), Constants.LogResult.ATTEMPT, getString(R.string.stop_audio_recording_attempt), null);
            activity.stopRecording();
        } catch (Exception e) {
            addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.stop_audio_recording), Constants.LogResult.ERROR, getString(R.string.stop_audio_recording_error), e.toString());
            e.printStackTrace();
        }
    }

    private String getFilePath(final String data) {
        final String path = FileUtils.getFilesStoragePath(getMainActivity());
        final String url = data;

        if (StringUtils.isEmpty(url)) {
            return Constants.Strings.EMPTY;
        }

        final String fileName = FileUtils.getFileName(url);

        return path + FileUtils.FOLDER_DIVIDER + fileName;
    }

    public void showExitPoolAlertDialog() {
        activateButtons();
        if (!getCurrentUser().getConfigR().isSaveAborted()) {
            MainActivity activity = (MainActivity) getActivity();
            addLog(getCurrentUser().getLogin(), Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_exit), null);
            if (activity != null && !activity.isFinishing()) {
                new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                        .setCancelable(false)
                        .setTitle(R.string.exit_quiz_header)
                        .setMessage(R.string.exit_questionaire_warning)
                        .setPositiveButton(R.string.view_yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {

                                try {
                                    getDao().clearCurrentQuestionnaireR();
                                    getDao().clearElementPassedR();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                                exitQuestionnaire();
                            }
                        })
                        .setNegativeButton(R.string.view_no, null).show();
            }
        } else {
            showScreensaver(true);
            saveQuestionnaire(true);
            showToast(getString(R.string.save_questionnaire));

            try {
                getDao().clearCurrentQuestionnaireR();
                getDao().clearElementPassedR();
            } catch (Exception e) {
                e.printStackTrace();
            }
            exitQuestionnaire();
        }
    }

    private void activateButtons() {
        btnPrev.setEnabled(true);
        btnExit.setEnabled(true);
        btnNext.setEnabled(true);

        final int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            btnPrev.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
            btnExit.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_red));
            btnNext.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
        } else {
            btnPrev.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
            btnExit.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_red));
            btnNext.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
        }
    }

    private void deactivateButtons() {
        btnPrev.setEnabled(false);
        btnExit.setEnabled(false);
        btnNext.setEnabled(false);

        final int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            btnPrev.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
            btnExit.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
            btnNext.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
        } else {
            btnPrev.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
            btnExit.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
            btnNext.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
        }
    }

    private void showInfoDialog() {
        dialogBuilder = new AlertDialog.Builder(getMainActivity());
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_info_auto : R.layout.dialog_info, null);
        TextView dQuota1 = layoutView.findViewById(R.id.quota_1);
        TextView dQuota2 = layoutView.findViewById(R.id.quota_2);
        TextView dQuota3 = layoutView.findViewById(R.id.quota_3);
        LinearLayout cont = layoutView.findViewById(R.id.cont);
        LinearLayout quota1Cont = layoutView.findViewById(R.id.quota_1_cont);
        LinearLayout quota2Cont = layoutView.findViewById(R.id.quota_2_cont);

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });

        dQuota3.setText(getCurrentUser().getConfigR().getProjectInfo().getName());
        quota1Cont.setVisibility(View.GONE);
        quota2Cont.setVisibility(View.GONE);

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.show();
    }
}

