//package pro.quizer.quizer3.view.fragment;
//
//import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.LinearLayoutManager;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.WebView;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
//import com.squareup.picasso.Picasso;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import pro.quizer.quizer3.Constants;
//import pro.quizer.quizer3.MainActivity;
//import pro.quizer.quizer3.R;
//import pro.quizer.quizer3.adapter.ListQuestionAdapter;
//import pro.quizer.quizer3.adapter.ScaleQuestionAdapter;
//import pro.quizer.quizer3.adapter.TableQuestionAdapter;
//import pro.quizer.quizer3.database.models.ElementContentsR;
//import pro.quizer.quizer3.database.models.ElementItemR;
//import pro.quizer.quizer3.database.models.ElementOptionsR;
//import pro.quizer.quizer3.database.models.ElementPassedR;
//import pro.quizer.quizer3.database.models.PrevElementsR;
//import pro.quizer.quizer3.model.ElementSubtype;
//import pro.quizer.quizer3.model.ElementType;
//import pro.quizer.quizer3.model.state.AnswerState;
//import pro.quizer.quizer3.utils.ConditionUtils;
//import pro.quizer.quizer3.utils.DateUtils;
//import pro.quizer.quizer3.utils.FileUtils;
//import pro.quizer.quizer3.utils.StringUtils;
//import pro.quizer.quizer3.utils.UiUtils;
//import pro.quizer.quizer3.view.Anim;
//import pro.quizer.quizer3.view.Toolbar;
//
//import static pro.quizer.quizer3.MainActivity.TAG;
//
//public class PageElementFragment extends ScreenFragment implements View.OnClickListener, ListQuestionAdapter.OnAnswerClickListener, ScaleQuestionAdapter.OnAnswerClickListener, TableQuestionAdapter.OnTableAnswerClickListener, AdapterView.OnItemSelectedListener {
//
//    private Toolbar toolbar;
//    private Button btnNext;
//    private Button btnPrev;
//    private Button btnExit;
//    private RelativeLayout cont;
//
//    private boolean isNextBtnPressed = false;
//    private boolean isExitBtnPressed = false;
//    private boolean isPrevBtnPressed = false;
//    private boolean isExit = false;
//    private int currentQuestionId;
//    private ElementItemR currentElement = null;
//    private ElementItemR pageElement1 = null;
//    private ElementItemR pageElement2 = null;
//    private ElementItemR pageElement3 = null;
//    private ElementItemR pageElement4 = null;
//    private ElementItemR pageElement5 = null;
//    private List<ElementItemR> answersList;
//    private List<ElementItemR> pageElementsList;
//    private Long startTime;
//    private Integer startElementId;
//    private Integer nextElementId;
//    private Integer prevElementId;
//    private String answerType;
//    private int spinnerSelection = -1;
//    private Boolean[] isTitle1Hided = {false, false, false, false, false};
//    private Boolean[] isTitle2Hided = {false, false, false, false, false};
//    private boolean isRestored = false;
//
//    private ListQuestionAdapter adapterList;
//    private ScaleQuestionAdapter adapterScale;
//    private ArrayAdapter adapterSpinner;
//    private TableQuestionAdapter adapterTable;
//    private List<AnswerState> savedAnswerStates;
//    private List<Integer> quotaBlock;
//
//    private final String KEY_RECYCLER_STATE = "recycler_state";
//
//    public PageElementFragment() {
//        super(R.layout.fragment_page_element);
//    }
////    public PageElementFragment() {
////        super(R.layout.fragment_element);
////    }
//
//    public PageElementFragment setStartElement(Integer startElementId) {
//        this.startElementId = startElementId;
//        return this;
//    }
//
//    public PageElementFragment setStartElement(Integer startElementId, boolean restored) {
//        this.startElementId = startElementId;
//        this.isRestored = restored;
//        return this;
//    }
//
//    @Override
//    protected void onReady() {
//
//        viewsArray = new View[5][33];
//
////        initPageCont1();
//        initCurrentElements();
//        loadResumedData();
//        initQuestion();
//        initPage();
//
//        toolbar = findViewById(R.id.toolbar);
//        cont = (RelativeLayout) findViewById(R.id.cont_element_fragment);
//        btnNext = (Button) findViewById(R.id.next_btn);
//        btnPrev = (Button) findViewById(R.id.back_btn);
//        btnExit = (Button) findViewById(R.id.exit_btn);
//
//        btnNext.setTransformationMethod(null);
//        btnPrev.setTransformationMethod(null);
//        btnExit.setTransformationMethod(null);
//
//        btnNext.setOnClickListener(this);
//        btnPrev.setOnClickListener(this);
//        btnExit.setOnClickListener(this);
//
//        toolbar.setTitle(getCurrentUser().getConfigR().getProjectInfo().getName());
//        toolbar.showOptionsView(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                MainFragment.showDrawer();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                titleCont1.setVisibility(View.VISIBLE);
//                titleCont2.setVisibility(View.VISIBLE);
//                toolbar.hideInfoView();
//            }
//        });
//
//        cont.startAnimation(Anim.getAppear(getContext()));
//        btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));
//        btnPrev.startAnimation(Anim.getAppearSlide(getContext(), 500));
//        btnExit.startAnimation(Anim.getAppearSlide(getContext(), 500));
//
//        MainFragment.enableSideMenu(false);
//
//        for (int i = 0; i < pageElementsList.size(); i++) {
//            initPageElement(pageElementsList.get(i), i);
//        }
//
//        initExitReminder();
//    }
//
//    private void initPageElement(ElementItemR element, int position) {
//        if (checkConditions(element)) {
//            answerType = setQuestionType(element);
//            initPageViews(element, position);
//            updateCurrentQuestionnaire();
//            initRecyclerView();
//            if (isRestored || wasReloaded()) {
//                loadSavedData();
//            }
//        }
//    }
//
//    private void initExitReminder() {
//        try {
//            getMainActivity().activateExitReminder();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean wasReloaded() {
//        List<ElementPassedR> elements = null;
//
//        try {
//            elements = getDao().getAllElementsPassedR(getQuestionnaire().getToken());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (elements != null && elements.size() > 0)
//            for (ElementPassedR element : elements) {
//                if (element.getRelative_id() == currentElement.getRelative_id()) {
//                    isRestored = true;
//                    return true;
//                }
//            }
//        return false;
//    }
//
//    @Override
//    public void onClick(View view) {
//        if (view == btnNext) {
////            if (!isNextBtnPressed) {
////                isNextBtnPressed = true;
//            if (saveElement()) {
////                Log.d(TAG, "onClick NEXT: " + nextElementId);
//                if (nextElementId == 0) {
//                    saveQuestionnaire();
//                } else if (nextElementId == -1) {
//                    exitQuestionnaire();
//                } else {
//                    TransFragment fragment = new TransFragment();
//                    fragment.setStartElement(nextElementId);
//                    replaceFragment(fragment);
//                }
//            } else {
////                Log.d(TAG, "saveElement: FALSE");
////                showToast("Выберите ответ.");
//            }
////            }
//        } else if (view == btnPrev) {
//            TransFragment fragment = new TransFragment();
//            List<PrevElementsR> prevList;
//            if (prevElementId != 0) {
//                prevList = getQuestionnaire().getPrev_element_id();
//                prevElementId = prevList.get(prevList.size() - 1).getPrevId();
//                prevList.remove(prevList.size() - 1);
//                try {
//                    getDao().setPrevElement(prevList);
//                } catch (Exception e) {
//                    showToast(getString(R.string.set_last_element_error));
//                    return;
//                }
//                fragment.setStartElement(prevElementId, true);
//                replaceFragmentBack(fragment);
//            } else {
//                showExitPoolAlertDialog();
//            }
//
//
//        } else if (view == btnExit) {
//            showExitPoolAlertDialog();
//        }
////        else if (view == viewsArray[0][31]) {
////            titleCont1.setVisibility(View.GONE);
////            unhideCont.setVisibility(View.VISIBLE);
////            isTitle1Hided = true;
////        } else if (view == viewsArray[0][32]) {
////            titleCont2.setVisibility(View.GONE);
////            unhideCont.setVisibility(View.VISIBLE);
////            isTitle2Hided = true;
////        } else if (view == viewsArray[0][0]) {
////            if (isTitle1Hided) {
////                isTitle1Hided = false;
////                titleCont1.setVisibility(View.VISIBLE);
////            }
////            if (isTitle2Hided) {
////                isTitle2Hided = false;
////                titleCont2.setVisibility(View.VISIBLE);
////            }
////            unhideCont.setVisibility(View.GONE);
////        }
//    }
//
//    private void initPage() {
//        if (pageElementsList == null) {
//            pageElementsList = new ArrayList<>();
//            pageElementsList.add(currentElement);
//        }
////        pageElementsList = currentElement.getElements();
//        if (pageElementsList.size() == 1) {
//            initPageCont1();
//            pageElement1 = currentElement;
//        }
//        if (pageElementsList.size() > 1) {
//            initPageCont2();
//            pageElement2 = pageElementsList.get(1);
//        }
//        if (pageElementsList.size() > 2) {
//            initPageCont3();
//            pageElement3 = pageElementsList.get(2);
//        }
//        if (pageElementsList.size() > 3) {
//            initPageCont4();
//            pageElement4 = pageElementsList.get(3);
//        }
//        if (pageElementsList.size() > 4) {
//            initPageCont5();
//            pageElement5 = pageElementsList.get(4);
//        }
//
//    }
//
//    private void initQuestion() {
//
//        startTime = DateUtils.getCurrentTimeMillis();
//
//        List<PrevElementsR> prevList;
//        if (getQuestionnaire() == null) {
//            initCurrentElements();
//        }
//        if (getQuestionnaire().getPrev_element_id() != null && getQuestionnaire().getPrev_element_id().size() > 0) {
//            prevList = getQuestionnaire().getPrev_element_id();
//            prevElementId = prevList.get(prevList.size() - 1).getPrevId();
//        } else {
//            prevElementId = 0;
//        }
//
//        if (startElementId == null) startElementId = 0;
//
//        if (getCurrentElements() != null && getCurrentElements().size() > 0) {
//
//            if (startElementId != 0) {
//                currentElement = getElement(startElementId);
//            } else {
//                currentElement = getCurrentElements().get(0);
//            }
//
//            currentElement = getQuestion(currentElement);
//            startElementId = currentElement.getRelative_id();
//
////            Log.d(TAG, "??????? CURRENT initQuestion: " + currentElement.getRelative_id());
//
////            boolean found = false;
////
////            for (int i = 0; i < getCurrentElements().size(); i++) {
////                if (getCurrentElements().get(i).getRelative_id() == currentElement.getRelative_id()) {
////                    found = true;
////                }
////                if (found) {
////                    if (!getCurrentElements().get(i).getType().equals(ElementType.BOX)
////                            || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.TABLE)
////                            || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.PAGE)) {
////                        startElementId = getCurrentElements().get(i).getRelative_id();
////                        currentElement = getCurrentElements().get(i);
////                        if (currentElement.getSubtype().equals(ElementSubtype.PAGE)) {
////                            initPage();
////                        }
////                        break;
////                    }
////                }
////            }
//        } else {
//            Log.d(TAG, "initQuestions: ERROR! (empty list)");
//        }
//    }
//
//    private ElementItemR getQuestion(ElementItemR elementItemR) {
//        boolean found = false;
//        ElementItemR question = null;
//
//        for (int i = 0; i < getCurrentElements().size(); i++) {
//            if (getCurrentElements().get(i).getRelative_id() == elementItemR.getRelative_id()) {
//                found = true;
//            }
//            if (found) {
//                if (!getCurrentElements().get(i).getType().equals(ElementType.BOX)
//                        || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.TABLE)
//                        || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.PAGE)) {
//                    question = getCurrentElements().get(i);
//                    if (question.getSubtype().equals(ElementSubtype.PAGE)) {
//                        pageElementsList = question.getElements(); // Без вложенных Page!!!
//                    }
//                    break;
//                }
//            }
//        }
//        return question;
//    }
//
//    private String setQuestionType(ElementItemR element) {
//        String pageQuestionType = null;
//
//        if (element.getSubtype().equals(ElementSubtype.LIST)) {
//            if (element.getRelative_parent_id() != null && element.getRelative_parent_id() != 0 &&
//                    getElement(element.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
//                pageQuestionType = ElementSubtype.QUOTA;
//            } else {
//                pageQuestionType = ElementSubtype.LIST;
//            }
//        } else if (element.getSubtype().equals(ElementSubtype.SELECT)) {
//            pageQuestionType = ElementSubtype.SELECT;
//        } else if (element.getSubtype().equals(ElementSubtype.TABLE)) {
//            pageQuestionType = ElementSubtype.TABLE;
//        } else if (element.getSubtype().equals(ElementSubtype.SCALE)) {
//            pageQuestionType = ElementSubtype.SCALE;
//        } else if (element.getSubtype().equals(ElementSubtype.HTML)) {
//            pageQuestionType = ElementSubtype.HTML;
//        }
//        return pageQuestionType;
//    }
//
//    private void initPageViews(ElementItemR element, int position) {
//        viewsArray[position][31].setOnClickListener(v -> {
//            viewsArray[position][1].setVisibility(View.GONE);
//            viewsArray[position][0].setVisibility(View.VISIBLE);
//            isTitle1Hided[position] = true;
//        });
//        viewsArray[position][32].setOnClickListener(v -> {
//            viewsArray[position][2].setVisibility(View.GONE);
//            viewsArray[position][0].setVisibility(View.VISIBLE);
//            isTitle2Hided[position] = true;
//        });
//        viewsArray[position][0].setOnClickListener(v -> {
//            if (isTitle1Hided[position]) {
//                isTitle1Hided[position] = false;
//                viewsArray[position][1].setVisibility(View.VISIBLE);
//            }
//            if (isTitle2Hided[position]) {
//                isTitle2Hided[position] = false;
//                viewsArray[position][2].setVisibility(View.VISIBLE);
//            }
//            viewsArray[position][0].setVisibility(View.GONE);
//        });
//
//        TextView tvQuestion = (TextView) viewsArray[position][19];
//        tvQuestion.setText(element.getElementOptionsR().getTitle());
//
//        if (element.getElementOptionsR().getDescription() != null) {
//            TextView tvQuestionDesc = (TextView) viewsArray[position][20];
//            tvQuestionDesc.setVisibility(View.VISIBLE);
//            tvQuestionDesc.setText(element.getElementOptionsR().getDescription());
//        }
//        if (element.getRelative_parent_id() != null) {
//            ElementItemR parentElement = null;
//            try {
//                parentElement = getElement(element.getRelative_parent_id());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (parentElement != null) {
//                if (parentElement.getType().equals(ElementType.BOX) && !parentElement.isWas_shown()) {
//                    getDao().setWasElementShown(true, parentElement.getRelative_id(), parentElement.getUserId(), parentElement.getProjectId());
//                    titleCont2.setVisibility(View.VISIBLE);
//                    UiUtils.setTextOrHide(tvTitle2, parentElement.getElementOptionsR().getTitle());
//                    if (parentElement.getElementOptionsR().getDescription() != null) {
//                        tvTitleDesc2.setVisibility(View.VISIBLE);
//                        tvTitleDesc2.setText(parentElement.getElementOptionsR().getDescription());
//                    }
//
//                    showContent(parentElement, titleImagesCont1);
//
//                    if (parentElement.getRelative_parent_id() != null) {
//                        ElementItemR parentElement2 = null;
//                        try {
//                            parentElement2 = getElement(parentElement.getRelative_parent_id());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        if (parentElement2 != null) {
//                            if (parentElement2.getType().equals(ElementType.BOX) && !parentElement2.isWas_shown()) {
//                                getDao().setWasElementShown(true, parentElement2.getRelative_id(), parentElement2.getUserId(), parentElement2.getProjectId());
//                                titleCont1.setVisibility(View.VISIBLE);
//                                UiUtils.setTextOrHide(tvTitle1, parentElement2.getElementOptionsR().getTitle());
//                                if (parentElement2.getElementOptionsR().getDescription() != null) {
//                                    tvTitleDesc1.setVisibility(View.VISIBLE);
//                                    tvTitleDesc1.setText(parentElement2.getElementOptionsR().getDescription());
//                                }
//
//                                showContent(parentElement2, titleImagesCont2);
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//
//        showContent(element, questionImagesCont);
//
//        if (getCurrentUser().getConfigR().isPhotoQuestionnaire() && currentElement.getElementOptionsR().isTake_photo()) {
//            shotPicture(getLoginAdmin(), getQuestionnaire().getToken(), currentElement.getRelative_id(), getCurrentUserId(), getQuestionnaire().getProject_id(), getCurrentUser().getLogin());
//        }
//    }
//
//    private void showContent(ElementItemR element, View cont) {
//        final List<ElementContentsR> contents = element.getElementContentsR();
//
//        if (contents != null && !contents.isEmpty()) {
//            String data1 = null;
//            String data2 = null;
//            String data3 = null;
//            data1 = contents.get(0).getData();
//            if (contents.size() > 1)
//                data2 = contents.get(1).getData();
//            if (contents.size() > 2)
//                data3 = contents.get(2).getData();
//
//            if (cont.equals(questionImagesCont)) {
//                if (data1 != null) showPic(questionImagesCont, questionImage1, data1);
//                if (data2 != null) showPic(questionImagesCont, questionImage2, data2);
//                if (data3 != null) showPic(questionImagesCont, questionImage3, data3);
//            } else if (cont.equals(titleImagesCont1)) {
//                if (data1 != null) showPic(titleImagesCont1, title1Image1, data1);
//                if (data2 != null) showPic(titleImagesCont1, title1Image2, data2);
//                if (data3 != null) showPic(titleImagesCont1, title1Image3, data3);
//            } else if (cont.equals(titleImagesCont2)) {
//                if (data1 != null) showPic(titleImagesCont2, title2Image1, data1);
//                if (data2 != null) showPic(titleImagesCont2, title2Image2, data2);
//                if (data3 != null) showPic(titleImagesCont2, title2Image3, data3);
//            }
//        }
//    }
//
//    private void showPic(View cont, ImageView view, String data) {
//        final String filePhotooPath = getFilePath(data);
//
//        if (StringUtils.isEmpty(filePhotooPath)) {
//            return;
//        }
//
//        cont.setVisibility(View.VISIBLE);
//        view.setVisibility(View.VISIBLE);
//
//        Picasso.with(getActivity())
//                .load(new File(filePhotooPath))
//                .into(view);
//    }
//
//    private void initRecyclerView() {
//        answersList = new ArrayList<>();
//        List<String> itemsList = new ArrayList<>();
//
//        if (answerType.equals(ElementSubtype.LIST) || answerType.equals(ElementSubtype.QUOTA)) {
//            rvAnswers.setVisibility(View.VISIBLE);
//        } else if (answerType.equals(ElementSubtype.SELECT)) {
//            spinnerCont.setVisibility(View.VISIBLE);
//        } else if (answerType.equals(ElementSubtype.TABLE)) {
//            tableCont.setVisibility(View.VISIBLE);
//        } else if (answerType.equals(ElementSubtype.HTML)) {
//            questionCont.setVisibility(View.GONE);
//            infoCont.setVisibility(View.VISIBLE);
//            infoText.loadData(currentElement.getElementOptionsR().getData(), "text/html; charset=UTF-8", null);
//        } else if (answerType.equals(ElementSubtype.SCALE)) {
//            rvScale.setVisibility(View.VISIBLE);
//        }
//
////        for (ElementItemR element : getCurrentElements()) {
////            if (element.getRelative_parent_id() == currentElement.getRelative_id()) {
////                answersList.add(element);
////                itemsList.add(element.getElementOptionsR().getTitle());
////            }
////        }
//
//        answersList = currentElement.getElements();
//        List<ElementItemR> checkedAnswersList = new ArrayList<>();
//
//        for (int a = 0; a < answersList.size(); a++) {
//            boolean check = checkConditions(answersList.get(a));
////            Log.d(TAG, "check: " + check);
//            if (check) {
//                checkedAnswersList.add(answersList.get(a));
//            }
//        }
////        Log.d(TAG, "checkedAnswersList: " + checkedAnswersList.size());
//
//        if (checkedAnswersList == null || checkedAnswersList.size() == 0) {
//            nextElementId = currentElement.getElementOptionsR().getJump();
//            if (nextElementId == null) {
//                nextElementId = answersList.get(0).getElementOptionsR().getJump();
//            }
//            if (nextElementId == 0 || nextElementId == null) {
//                saveQuestionnaire();
//                exitQuestionnaire();
//            }
//        } else {
////            Log.d(TAG, "answersList: " + answersList.size());
//            answersList = checkedAnswersList;
//        }
//
//        for (ElementItemR element : answersList) {
//            itemsList.add(element.getElementOptionsR().getTitle());
//        }
//
////        Log.d(TAG, "?????????? initRecyclerView: " + answerType);
//        if (answerType.equals(ElementSubtype.LIST)) {
//            adapterList = new ListQuestionAdapter(getActivity(), currentElement, answersList,
//                    null, null, this);
//            rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
//            rvAnswers.setAdapter(adapterList);
//        } else if (answerType.equals(ElementSubtype.QUOTA)) {
////            Log.d(TAG, "initRecyclerView: quota answers size= " + answersList.size());
//            MainActivity activity = (MainActivity) getActivity();
//            adapterList = new ListQuestionAdapter(getActivity(), currentElement, answersList,
//                    getPassedQuotasBlock(), activity.getTree(null), this);
//            rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
//            rvAnswers.setAdapter(adapterList);
//        } else if (answerType.equals(ElementSubtype.SELECT)) {
//            itemsList.add(getString(R.string.select_spinner));
//            adapterSpinner = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemsList) {
//                public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                    return super.getDropDownView(position + 1, convertView, parent);
//                }
//
//                public int getCount() {
//                    return (itemsList.size() - 1);
//                }
//            };
//            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerAnswers.setAdapter(adapterSpinner);
//            spinnerAnswers.setSelection(itemsList.size() - 1);
//            spinnerAnswers.setOnItemSelectedListener(this);
//        } else if (answerType.equals(ElementSubtype.TABLE)) {
////            List<ElementItemR> questions = null;
////            questions = currentElement.getElements();
//            //answersList = questionsList for TABLE
//            adapterTable = new TableQuestionAdapter(currentElement, answersList, getActivity(), mRefreshRecyclerViewRunnable, this);
////            Log.d(TAG, "??????? answers initRecyclerView: " + answersList.size());
//            tableLayout.setAdapter(adapterTable);
//            tableLayout.setDrawingCacheEnabled(true);
//        } else if (answerType.equals(ElementSubtype.SCALE)) {
//            adapterScale = new ScaleQuestionAdapter(getActivity(), currentElement, answersList,
//                    this);
//            rvScale.setLayoutManager(new LinearLayoutManager(getContext()));
//            rvScale.setAdapter(adapterScale);
//        }
//    }
//
//    private void updateCurrentQuestionnaire() {
//        try {
//            getDao().setCurrentElement(startElementId);
//            getDao().setQuestionTime(DateUtils.getCurrentTimeMillis());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean checkConditions(ElementItemR element) {
////        Log.d(TAG, "checkConditions: " + element);
//        final ElementOptionsR options = element.getElementOptionsR();
//        if (options != null && options.getPre_condition() != null) {
//            final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(true), (MainActivity) getActivity());
////            Log.d(TAG, "!!!!!!!!!!!!!!!!!! showValue: " + showValue);
//            if (showValue != ConditionUtils.CAN_SHOW) {
//                if (showValue != ConditionUtils.CANT_SHOW) {
//                    nextElementId = showValue;
////                    Log.d(TAG, "checkConditions: 1");
//                } else {
////                    Log.d(TAG, "checkConditions: 2");
//                    if (!element.equals(currentElement)) {
////                        Log.d(TAG, "checkConditions: 3");
//                        return false;
//                    }
//                    nextElementId = options.getJump();
//                    if (nextElementId == null) {
////                        Log.d(TAG, "checkConditions: 4");
//                        List<ElementItemR> answers = element.getElements();
//                        if (answers != null && answers.size() > 0) {
////                            Log.d(TAG, "checkConditions: 5");
//                            try {
//                                nextElementId = answers.get(0).getElementOptionsR().getJump();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (nextElementId == null) {
////                            Log.d(TAG, "checkConditions: 6");
//                            nextElementId = 0;
//                        }
//                    }
//                }
//                if (nextElementId == 0) {
////                    Log.d(TAG, "checkConditions: 7");
//                    saveQuestionnaire();
//                    exitQuestionnaire();
//                    return false;
//                } else if (nextElementId == -1) {
////                    Log.d(TAG, "checkConditions: 8");
//                    exitQuestionnaire();
//                    return false;
//                } else {
////                    Log.d(TAG, "checkConditions: 9 / " + nextElementId);
////                    onClick(btnNext);
////                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
//                    showNextFragment(nextElementId);
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//    private void showNextFragment(int id) {
//        TransFragment fragment = new TransFragment();
//        fragment.setStartElement(id);
//        replaceFragment(fragment);
//    }
//
//    private boolean saveElement() {
//        boolean saved = false;
////        if(!isRestored) {
//        if (answerType.equals(ElementSubtype.LIST) || answerType.equals(ElementSubtype.QUOTA) || answerType.equals(ElementSubtype.SCALE)) {
//            List<AnswerState> answerStates = null;
//            if (answerType.equals(ElementSubtype.SCALE)) {
//                answerStates = adapterScale.getAnswers();
//            } else {
//                answerStates = adapterList.getAnswers();
//            }
//            if (answerStates != null && notEmpty(answerStates)) {
//                for (AnswerState answerState : answerStates) {
//                    if (answerState.isChecked()) {
//                        nextElementId = getElement(answerState.getRelative_id()).getElementOptionsR().getJump();
//                    }
//                }
//
////                    ElementItemR nextElement = getElement(nextElementId);
////                    final ElementOptionsR options = nextElement.getElementOptionsR();
////                    final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMap(true), (MainActivity) getActivity());
////                    Log.d(TAG, "!!!!!!!!!!!!!!!!!! showValue: " + showValue);
////                    if (showValue != ConditionUtils.CAN_SHOW) {
////                        nextElementId = options.getJump();
////                        if(nextElementId == null) {
////                            List<ElementItemR> answers = nextElement.getElements();
////                            if(answers != null && answers.size() >0) {
////                                try {
////                                    nextElementId = answers.get(0).getElementOptionsR().getJump();
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
////                            }
////                            if(nextElementId == null) {
////                                nextElementId = 0;
////                            }
////                        }
////                    }
//
////                if (currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
////                    //TODO Переход из контейнера с ротацией
////                    nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
////                } else if (nextElementId == null || nextElementId == 0) {
////                    nextElementId = getElement(answerStates.get(0).getRelative_id()).getElementOptionsR().getJump();
////                }
//
//                ElementPassedR elementPassedR = new ElementPassedR();
//                elementPassedR.setRelative_id(currentElement.getRelative_id());
//                elementPassedR.setProject_id(currentElement.getProjectId());
//                elementPassedR.setToken(getQuestionnaire().getToken());
//                elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
//
//                elementPassedR.setFrom_quotas_block(false);
//
//                try {
//                    if (!isRestored) {
//                        getDao().insertElementPassedR(elementPassedR);
//                        getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
//                    }
//                    saved = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    saved = false;
//                }
//
//                for (int i = 0; i < answerStates.size(); i++) {
//                    if (answerStates.get(i).isChecked()) {
//
//                        ElementPassedR answerPassedR = new ElementPassedR();
//                        answerPassedR.setRelative_id(answerStates.get(i).getRelative_id());
//                        answerPassedR.setProject_id(currentElement.getProjectId());
//                        answerPassedR.setToken(getQuestionnaire().getToken());
//                        answerPassedR.setValue(answerStates.get(i).getData());
//                        if (answerType.equals(ElementSubtype.QUOTA)) {
//                            answerPassedR.setFrom_quotas_block(true);
//                        } else {
//                            answerPassedR.setFrom_quotas_block(false);
//                        }
//
//                        try {
//                            if (!isRestored) {
//                                getDao().insertElementPassedR(answerPassedR);
//                            }
//                            saved = true;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            saved = false;
//                            return saved;
//                        }
//                    }
//                }
//            }
//        } else if (answerType.equals(ElementSubtype.SELECT)) {
//            if (spinnerSelection != -1) {
//                ElementPassedR elementPassedR = new ElementPassedR();
//                nextElementId = answersList.get(spinnerSelection).getElementOptionsR().getJump();
//                ElementItemR nextElement = getElement(nextElementId);
//                final ElementOptionsR options = nextElement.getElementOptionsR();
//                final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), (MainActivity) getActivity());
//
//                if (showValue != ConditionUtils.CAN_SHOW) {
//                    nextElementId = options.getJump();
//                }
//                elementPassedR.setRelative_id(currentElement.getRelative_id());
//                elementPassedR.setProject_id(currentElement.getProjectId());
//                elementPassedR.setToken(getQuestionnaire().getToken());
//                elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
//
//                try {
//                    if (!isRestored) {
//                        getDao().insertElementPassedR(elementPassedR);
//                        getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
//                    }
//                    saved = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    saved = false;
//                    return saved;
//                }
//
//                ElementPassedR answerPassedR = new ElementPassedR();
//                answerPassedR.setRelative_id(answersList.get(spinnerSelection).getRelative_id());
//                answerPassedR.setProject_id(currentElement.getProjectId());
//                answerPassedR.setToken(getQuestionnaire().getToken());
//
//                try {
//                    if (!isRestored) {
//                        getDao().insertElementPassedR(answerPassedR);
//                    }
//                    saved = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    saved = false;
//                    return saved;
//                }
//            }
//        } else if (answerType.equals(ElementSubtype.TABLE)) {
//            AnswerState[][] answerStates = adapterTable.getmAnswersState();
//            if (answerStates != null && answerStates[0][0].getRelative_id() != null && adapterTable.isCompleted()) {
//                if (currentElement.getElementOptionsR().getJump() != null)
//                    nextElementId = currentElement.getElementOptionsR().getJump();
//                else
//                    nextElementId = getElement(answerStates[0][0].getRelative_id()).getElementOptionsR().getJump();
//
//                ElementItemR nextElement = getElement(nextElementId);
//                if (nextElementId != 0 && nextElementId != -1) {
//                    final ElementOptionsR options = nextElement.getElementOptionsR();
//                    final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), (MainActivity) getActivity());
//
//                    if (showValue != ConditionUtils.CAN_SHOW) {
//                        nextElementId = options.getJump();
//                    }
//                }
//
//                ElementPassedR elementPassedR = new ElementPassedR();
//                elementPassedR.setRelative_id(currentElement.getRelative_id());
//                elementPassedR.setProject_id(currentElement.getProjectId());
//                elementPassedR.setToken(getQuestionnaire().getToken());
//                elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
//                try {
//                    if (!isRestored) {
//                        getDao().insertElementPassedR(elementPassedR);
//                        getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
//                    }
//                    saved = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    saved = false;
//                    return saved;
//                }
//
//                for (int i = 0; i < answerStates.length; i++) {
//                    for (int k = 0; k < answerStates[i].length; k++) {
//                        if (answerStates[i][k].isChecked()) {
//                            ElementPassedR answerPassedR = new ElementPassedR();
//                            answerPassedR.setRelative_id(answerStates[i][k].getRelative_id());
//                            answerPassedR.setValue(answerStates[i][k].getData());
//                            answerPassedR.setProject_id(currentElement.getProjectId());
//                            answerPassedR.setToken(getQuestionnaire().getToken());
//                            try {
//                                if (!isRestored) {
//                                    getDao().insertElementPassedR(answerPassedR);
//                                }
//                                saved = true;
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                saved = false;
//                                return saved;
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (answerType.equals(ElementSubtype.HTML)) {
//            ElementPassedR elementPassedR = new ElementPassedR();
//            nextElementId = currentElement.getElementOptionsR().getJump();
//
//            ElementItemR nextElement = getElement(nextElementId);
//            final ElementOptionsR options = nextElement.getElementOptionsR();
//            final int showValue = ConditionUtils.evaluateCondition(options.getPre_condition(), getMainActivity().getMap(false), (MainActivity) getActivity());
//
//            if (showValue != ConditionUtils.CAN_SHOW) {
//                nextElementId = options.getJump();
//            }
//
//            elementPassedR.setRelative_id(currentElement.getRelative_id());
//            elementPassedR.setProject_id(currentElement.getProjectId());
//            elementPassedR.setToken(getQuestionnaire().getToken());
//            elementPassedR.setDuration(DateUtils.getCurrentTimeMillis() - startTime);
//
//            try {
//                if (!isRestored) {
//                    getDao().insertElementPassedR(elementPassedR);
//                    getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
//                }
//                saved = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                saved = false;
//                return saved;
//            }
//        }
////        } else {
////            saved = true;
////        }
//
//        if (saved) {
//            updatePrevElement();
//        }
////        showPassed();
//        return saved;
//    }
//
//    public boolean notEmpty(List<AnswerState> answerStates) {
//
//        int counter = 0;
//
//        for (AnswerState state : answerStates) {
//
//            ElementItemR element = null;
//            boolean openType = false;
//            try {
//                element = getElement(state.getRelative_id());
//                if (!element.getElementOptionsR().getOpen_type().equals("checkbox")) {
//                    openType = true;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
////            Log.d(TAG, "notEmpty: " + openType + " " +  state.getData());
//            if (state.isChecked() && openType) {
//                if (state.getData().equals("") || state.getData() == null) {
//                    showToast(getString(R.string.empty_string_warning));
//                    return false;
//                }
//            }
//            if (state.isChecked()) {
//                counter++;
//            }
//        }
//
//        Integer min = currentElement.getElementOptionsR().getMin_answers();
//        Integer max = currentElement.getElementOptionsR().getMax_answers();
//        if (counter > 0) {
//            if (min != null && counter < min) {
//                showToast("Выберите минимум " + min + " ответа.");
//                return false;
//            }
//            if (max != null && counter > max) {
//                showToast("Выберите максимум " + max + " ответ(а).");
//                return false;
//            }
//        } else {
//            if (min != null)
//                showToast("Выберите минимум " + min + " ответа.");
//            else
//                showToast("Выберите минимум 1 ответ.");
//            return false;
//        }
//        return true;
//    }
//
//    public void showPassed() {
//        List<ElementPassedR> elementPassedRS = getDao().getAllElementsPassedR(getQuestionnaire().getToken());
//        Log.d(TAG, "==========================================");
//        for (ElementPassedR element : elementPassedRS) {
//            Log.d(TAG, ">>>>>>>>>>>>>>> showPassed: " + element.getId());
//        }
//    }
//
//    private void updatePrevElement() {
//        List<PrevElementsR> prevList;
//        if (getQuestionnaire().getPrev_element_id() != null) {
//            prevList = getQuestionnaire().getPrev_element_id();
//            prevList.add(new PrevElementsR(startElementId, nextElementId));
//
//        } else {
//            prevList = new ArrayList<>();
//            prevList.add(new PrevElementsR(startElementId, nextElementId));
//        }
//        try {
//            getDao().setPrevElement(prevList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
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
//                        showToast("Deleted!");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        showToast("Ошибка очистки таблицы элементов.");
//                    }
//                }
//            }
//            spinnerSelection = position;
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        showToast("Выберите ответ (не выбрано)");
//    }
//
//    @Override
//    public void onAnswerClick(int position, boolean enabled, String answer) {
//        if (isRestored) {
////            int id = currentElement.getId();
//            try {
//                isRestored = false;
//                int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
//                getDao().deleteOldElementsPassedR(id);
//                showToast("Deleted!");
//            } catch (Exception e) {
//                e.printStackTrace();
//                showToast("Ошибка очистки таблицы элементов.");
//            }
//        }
//    }
//
//    @Override
//    public void onAnswerClick(int row, int column) {
//        if (isRestored) {
//            try {
//                isRestored = false;
//                int id = getDao().getElementPassedR(getQuestionnaire().getToken(), currentElement.getRelative_id()).getId();
//                getDao().deleteOldElementsPassedR(id);
//                showToast("Deleted!");
////                showPassed();
//            } catch (Exception e) {
//                e.printStackTrace();
//                showToast("Ошибка очистки таблицы элементов.");
//            }
//        }
//    }
//
//    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
//        @Override
//        public void run() {
//            UiUtils.hideKeyboard(getContext(), getView());
//        }
//    };
//
//    public void loadResumedData() {
//        if (isRestored) {
//            try {
//                List<PrevElementsR> prevList = getQuestionnaire().getPrev_element_id();
//                if (prevList != null && prevList.size() > 0) {
//                    PrevElementsR lastPassedElement = prevList.get(prevList.size() - 1);
//                    startElementId = lastPassedElement.getNextId();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void loadSavedData() {
//        if (answerType.equals(ElementSubtype.LIST) || answerType.equals(ElementSubtype.QUOTA)) {
//            List<AnswerState> answerStatesAdapter = adapterList.getAnswers();
//            List<AnswerState> answerStatesRestored = new ArrayList<>();
//            int lastSelectedPosition = 0;
//            for (int i = 0; i < answerStatesAdapter.size(); i++) {
//                AnswerState answerStateNew = new AnswerState();
//                ElementPassedR answerStateRestored = null;
//                try {
//                    answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answerStatesAdapter.get(i).getRelative_id());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (answerStateRestored != null) {
//                    answerStateNew.setChecked(true);
//                    answerStateNew.setData(answerStateRestored.getValue());
//                    lastSelectedPosition = i;
//                } else {
//                    answerStateNew.setChecked(false);
//                    answerStateNew.setData("");
//                }
//
//                answerStateNew.setRelative_id(answerStatesAdapter.get(i).getRelative_id());
//                answerStatesRestored.add(answerStateNew);
//            }
//            adapterList.setAnswers(answerStatesRestored);
//            adapterList.setLastSelectedPosition(lastSelectedPosition);
//            adapterList.notifyDataSetChanged();
//
//        } else if (answerType.equals(ElementSubtype.SELECT)) {
//            spinnerSelection = -1;
//
//            for (int i = 0; i < answersList.size(); i++) {
//                ElementPassedR answerStateRestored = null;
//                try {
//                    answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answersList.get(i).getRelative_id());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (answerStateRestored != null) {
//                    spinnerSelection = i;
//                    spinnerAnswers.setSelection(spinnerSelection);
//                }
//            }
//
//        } else if (answerType.equals(ElementSubtype.TABLE)) {
//            AnswerState[][] answersTableState = adapterTable.getmAnswersState();
//
//            for (int i = 0; i < answersTableState.length; i++) {
//                for (int k = 0; k < answersTableState[i].length; k++) {
//                    ElementPassedR answerStateRestored = null;
//                    try {
//                        answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answersTableState[i][k].getRelative_id());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (answerStateRestored != null) {
//                        answersTableState[i][k].setChecked(true);
//                        answersTableState[i][k].setData(answerStateRestored.getValue());
//                    }
//                }
//            }
//
//            adapterTable.setmAnswersState(answersTableState);
////            adapterTable.
//        } else if (answerType.equals(ElementSubtype.SCALE)) {
//            List<AnswerState> answerStatesAdapter = adapterScale.getAnswers();
//            List<AnswerState> answerStatesRestored = new ArrayList<>();
//            int lastSelectedPosition = 0;
//            for (int i = 0; i < answerStatesAdapter.size(); i++) {
//                AnswerState answerStateNew = new AnswerState();
//                ElementPassedR answerStateRestored = null;
//                try {
//                    answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answerStatesAdapter.get(i).getRelative_id());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (answerStateRestored != null) {
//                    answerStateNew.setChecked(true);
//                    answerStateNew.setData(answerStateRestored.getValue());
//                    lastSelectedPosition = i;
//                } else {
//                    answerStateNew.setChecked(false);
//                    answerStateNew.setData("");
//                }
//
//                answerStateNew.setRelative_id(answerStatesAdapter.get(i).getRelative_id());
//                answerStatesRestored.add(answerStateNew);
//            }
//            adapterScale.setAnswers(answerStatesRestored);
//            adapterScale.setLastSelectedPosition(lastSelectedPosition);
//            adapterScale.notifyDataSetChanged();
//        }
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        MainActivity mActivity = (MainActivity) getActivity();
//        assert mActivity != null;
//        if (!mActivity.checkPermission()) {
//            mActivity.requestPermission();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        //TODO save startTime
//    }
//
//    @Override
//    public boolean onBackPressed() {
//        onClick(btnPrev);
////        if (isExit) {
////            stopRecording();
////            replaceFragment(new HomeFragment());
////        } else {
////            Toast.makeText(getContext(), getString(R.string.exit_questionaire_warning), Toast.LENGTH_SHORT).show();
////            isExit = true;
////        }
//        return true;
//    }
//
//    public boolean saveQuestionnaire() {
//        stopRecording();
//        if (saveQuestionnaireToDatabase(getQuestionnaire(), false)) {
//            try {
//                getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            showToast("Анкета сохранена");
//            replaceFragment(new HomeFragment());
//        } else {
//            showToast("Ошибка сохранения анкеты. Попробуйте еще раз");
//        }
//        return true;
//    }
//
//    public void exitQuestionnaire() {
//        stopRecording();
//        replaceFragment(new HomeFragment());
//    }
//
//    private void stopRecording() {
//        MainActivity activity = (MainActivity) getActivity();
//        try {
//            addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.stop_audio_recording), Constants.LogResult.ATTEMPT, getString(R.string.stop_audio_recording_attempt), null);
//            activity.stopRecording();
//        } catch (Exception e) {
//            addLog(getCurrentUser().getLogin(), Constants.LogType.FILE, Constants.LogObject.AUDIO, getString(R.string.stop_audio_recording), Constants.LogResult.ERROR, getString(R.string.stop_audio_recording_error), e.toString());
//            e.printStackTrace();
//        }
//    }
//
//    private String getFilePath(final String data) {
//        final String path = FileUtils.getFilesStoragePath((MainActivity) getActivity());
//        final String url = data;
//
//        if (StringUtils.isEmpty(url)) {
//            return Constants.Strings.EMPTY;
//        }
//
//        final String fileName = FileUtils.getFileName(url);
//
//        return path + FileUtils.FOLDER_DIVIDER + fileName;
//    }
//
//    public void showExitPoolAlertDialog() {
//        MainActivity activity = getMainActivity();
//        addLog(getCurrentUser().getLogin(), Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_exit), null);
//        if (activity != null && !activity.isFinishing()) {
//            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
//                    .setCancelable(false)
//                    .setTitle(R.string.exit_quiz_header)
//                    .setMessage(R.string.exit_questionaire_warning)
//                    .setPositiveButton(R.string.view_yes, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(final DialogInterface dialog, final int which) {
//                            if (getCurrentUser().getConfigR().isSaveAborted()) {
//                                showScreensaver(true);
//                                saveQuestionnaire();
//                                showToast(getString(R.string.save_questionnaire));
//                            }
//                            try {
//                                getDao().clearCurrentQuestionnaireR();
//                                getDao().clearElementPassedR();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            dialog.dismiss();
//                            exitQuestionnaire();
//                        }
//                    })
//                    .setNegativeButton(R.string.view_no, null).show();
//        }
//    }
//
//
//}
//
