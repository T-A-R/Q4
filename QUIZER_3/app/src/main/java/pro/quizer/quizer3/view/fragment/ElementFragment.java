package pro.quizer.quizer3.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.ListQuestionAdapter;
import pro.quizer.quizer3.adapter.TableQuestionAdapter;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.state.AnswerState;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementFragment extends ScreenFragment implements View.OnClickListener, ListQuestionAdapter.OnAnswerClickListener, AdapterView.OnItemSelectedListener {

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
    private FrameLayout tableCont;
    private TextView tvUnhide;
    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvQuestion;
    private TextView tvTitleDesc1;
    private TextView tvTitleDesc2;
    private TextView tvQuestionDesc;
    private RecyclerView rvAnswers;
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

    private boolean isNextBtnPressed = false;
    private boolean isExitBtnPressed = false;
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
    private boolean isTitle1Hided = false;
    private boolean isTitle2Hided = false;
    private boolean isRestored = false;

    private ListQuestionAdapter adapterList;
    private ArrayAdapter adapterSpinner;
    private TableQuestionAdapter adapterTable;
    private List<AnswerState> savedAnswerStates;

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
        tableCont = (FrameLayout) findViewById(R.id.table_cont);
        rvAnswers = (RecyclerView) findViewById(R.id.answers_recyclerview);
        spinnerAnswers = (Spinner) findViewById(R.id.answers_spinner);
        tableLayout = (AdaptiveTableLayout) findViewById(R.id.table_question_layout);
        tvUnhide = (TextView) findViewById(R.id.unhide_title);
        tvTitle1 = (TextView) findViewById(R.id.title_1);
        tvTitle2 = (TextView) findViewById(R.id.title_2);
        tvTitleDesc1 = (TextView) findViewById(R.id.title_desc_1);
        tvTitleDesc2 = (TextView) findViewById(R.id.title_desc_2);
        tvQuestion = (TextView) findViewById(R.id.question);
        tvQuestionDesc = (TextView) findViewById(R.id.question_desc);
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
        btnNext = (Button) findViewById(R.id.next_btn);
        btnPrev = (Button) findViewById(R.id.back_btn);
        btnExit = (Button) findViewById(R.id.exit_btn);

        tvUnhide.setTypeface(Fonts.getFuturaPtBook());
        tvTitle1.setTypeface(Fonts.getFuturaPtBook());
        tvTitle2.setTypeface(Fonts.getFuturaPtBook());
        tvQuestion.setTypeface(Fonts.getFuturaPtBook());
        btnNext.setTypeface(Fonts.getFuturaPtBook());
        btnPrev.setTypeface(Fonts.getFuturaPtBook());
        btnExit.setTypeface(Fonts.getFuturaPtBook());
        btnNext.setTransformationMethod(null);
        btnPrev.setTransformationMethod(null);
        btnExit.setTransformationMethod(null);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        closeImage1.setOnClickListener(this);
        closeImage2.setOnClickListener(this);
        unhideCont.setOnClickListener(this);

        toolbar.setTitle(getCurrentUser().getConfigR().getProjectInfo().getName());
        toolbar.showOptionsView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MainFragment.showDrawer();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                titleCont1.setVisibility(View.VISIBLE);
                titleCont2.setVisibility(View.VISIBLE);
                toolbar.hideInfoView();
            }
        });

        cont.startAnimation(Anim.getAppear(getContext()));
        btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnPrev.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnExit.startAnimation(Anim.getAppearSlide(getContext(), 500));

        MainFragment.enableSideMenu();
        initCurrentElements();
        loadResumedData();
        initQuestion();
        setQuestionType();
        initViews();
        updateCurrentQuestionnaire();
        initRecyclerView();
        if (isRestored) {
            loadSavedData();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnNext) {
//            if (!isNextBtnPressed) {
//                isNextBtnPressed = true;
            if (saveElement()) {
                Log.d(TAG, "onClick NEXT: " + nextElementId);
                if (nextElementId == 0) {
                    saveQuestionnaire();
                } else if (nextElementId == -1) {
                    exitQuestionnaire();
                } else {
                    TransFragment fragment = new TransFragment();
                    fragment.setStartElement(nextElementId);
                    replaceFragment(fragment);
                }
            } else {
//                showToast("Выберите ответ.");
            }
//            }
        } else if (view == btnPrev) {
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
                onClick(btnExit);
            }


        } else if (view == btnExit) {
//            if (!isExitBtnPressed) {
//                isExitBtnPressed = true;
            try {
                getDao().clearCurrentQuestionnaireR();
                getDao().clearElementPassedR();
            } catch (Exception e) {
                isExitBtnPressed = false;
                e.printStackTrace();
            }
            replaceFragment(new HomeFragment());
//            }
        } else if (view == closeImage1) {
            titleCont1.setVisibility(View.GONE);
            unhideCont.setVisibility(View.VISIBLE);
            isTitle1Hided = true;
        } else if (view == closeImage2) {
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
        }
    }

    private void initQuestion() {

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

            if (startElementId != 0) {
                currentElement = getElement(startElementId);
            } else {
                currentElement = getCurrentElements().get(0);
            }

            boolean found = false;

            for (int i = 0; i < getCurrentElements().size(); i++) {
                if (getCurrentElements().get(i).getRelative_id() == currentElement.getRelative_id()) {
                    found = true;
                }
                if (found) {
                    if (!getCurrentElements().get(i).getType().equals(ElementType.BOX) || getCurrentElements().get(i).getSubtype().equals(ElementSubtype.TABLE)) {
                        startElementId = getCurrentElements().get(i).getRelative_id();
                        currentElement = getCurrentElements().get(i);
                        break;
                    }
                }
            }


            Log.d(TAG, "==================== initQuestion: " + currentElement.getRelative_id());
            showElementsQuery();
        }
    }

    private void setQuestionType() {
        if (currentElement.getSubtype().equals(ElementSubtype.LIST)) {
            answerType = ElementSubtype.LIST;
        } else if (currentElement.getSubtype().equals(ElementSubtype.SELECT)) {
            answerType = ElementSubtype.SELECT;
        } else if (currentElement.getSubtype().equals(ElementSubtype.TABLE)) {
            answerType = ElementSubtype.TABLE;
        } else if (currentElement.getSubtype().equals(ElementSubtype.SCALE)) {
            answerType = ElementSubtype.SCALE;
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
                    tvTitle2.setText(parentElement.getElementOptionsR().getTitle());
                    if (parentElement.getElementOptionsR().getDescription() != null) {
                        tvTitleDesc2.setVisibility(View.VISIBLE);
                        tvTitleDesc2.setText(parentElement.getElementOptionsR().getDescription());
                    }

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
                                tvTitle1.setText(parentElement2.getElementOptionsR().getTitle());
                                if (parentElement2.getElementOptionsR().getDescription() != null) {
                                    tvTitleDesc1.setVisibility(View.VISIBLE);
                                    tvTitleDesc1.setText(parentElement2.getElementOptionsR().getDescription());
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private void initRecyclerView() {
        answersList = new ArrayList<>();
        List<String> itemsList = new ArrayList<>();

        if (answerType.equals(ElementSubtype.LIST)) {
            rvAnswers.setVisibility(View.VISIBLE);
        } else if (answerType.equals(ElementSubtype.SELECT)) {
            spinnerCont.setVisibility(View.VISIBLE);
        } else if (answerType.equals(ElementSubtype.TABLE)) {
            tableCont.setVisibility(View.VISIBLE);
        }

        for (ElementItemR element : getCurrentElements()) {
            if (element.getRelative_parent_id() == currentElement.getRelative_id()) {
                answersList.add(element);
                itemsList.add(element.getElementOptionsR().getTitle());
            }
        }

        if (answerType.equals(ElementSubtype.LIST)) {
            adapterList = new ListQuestionAdapter(currentElement, answersList, this);
            rvAnswers.setLayoutManager(new LinearLayoutManager(getContext()));
            rvAnswers.setAdapter(adapterList);
        } else if (answerType.equals(ElementSubtype.SELECT)) {
            itemsList.add(getString(R.string.select_spinner));
            adapterSpinner = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, itemsList) {
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    return super.getDropDownView(position + 1, convertView, parent);
                }

                public int getCount() {
                    return (itemsList.size() - 1);
                }
            };
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAnswers.setAdapter(adapterSpinner);
            spinnerAnswers.setSelection(itemsList.size() - 1);
            spinnerAnswers.setOnItemSelectedListener(this);
        } else if (answerType.equals(ElementSubtype.TABLE)) {
            adapterTable = new TableQuestionAdapter(currentElement, getActivity(), mRefreshRecyclerViewRunnable);
            tableLayout.setAdapter(adapterTable);
            tableLayout.setDrawingCacheEnabled(true);
        }
    }

    @Override
    public void onAnswerClick(int position, boolean enabled, String answer) {

    }

    private void updateCurrentQuestionnaire() {
        try {
            getDao().setCurrentElement(startElementId);
            getDao().setQuestionTime(DateUtils.getCurrentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveElement() {
        boolean saved = false;
        if (answerType.equals(ElementSubtype.LIST)) {
            List<AnswerState> answerStates = adapterList.getAnswers();
            if (answerStates != null) {

                if (currentElement.getRelative_parent_id() != null && getElement(currentElement.getRelative_parent_id()).getElementOptionsR().isRotation()) {
                    //TODO Переход из контейнера с ротацией
                    nextElementId = getElement(currentElement.getRelative_parent_id()).getElementOptionsR().getJump();
                } else if (nextElementId == null || nextElementId == 0) {
                    nextElementId = getElement(answerStates.get(0).getRelative_id()).getElementOptionsR().getJump();
                }

                ElementPassedR elementPassedR = new ElementPassedR();
                elementPassedR.setRelative_id(currentElement.getRelative_id());
                elementPassedR.setProject_id(currentElement.getProjectId());
                elementPassedR.setToken(getQuestionnaire().getToken());
                elementPassedR.setDuration(startTime - DateUtils.getCurrentTimeMillis());

                try {
                    getDao().insertElementPassedR(elementPassedR);
                    getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
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
        } else if (answerType.equals(ElementSubtype.SELECT)) {
            if (spinnerSelection != -1) {
                ElementPassedR elementPassedR = new ElementPassedR();
                nextElementId = answersList.get(spinnerSelection).getElementOptionsR().getJump();
                elementPassedR.setRelative_id(currentElement.getRelative_id());
                elementPassedR.setProject_id(currentElement.getProjectId());
                elementPassedR.setToken(getQuestionnaire().getToken());
                elementPassedR.setDuration(startTime - DateUtils.getCurrentTimeMillis());

                try {
                    getDao().insertElementPassedR(elementPassedR);
                    getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
                    saved = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    saved = false;
                    return saved;
                }

                ElementPassedR answerPassedR = new ElementPassedR();
                answerPassedR.setRelative_id(answersList.get(spinnerSelection).getRelative_id());
                answerPassedR.setProject_id(currentElement.getProjectId());
                answerPassedR.setToken(getQuestionnaire().getToken());

                try {
                    getDao().insertElementPassedR(answerPassedR);
                    saved = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    saved = false;
                    return saved;
                }
            }
        } else if (answerType.equals(ElementSubtype.TABLE)) {
            AnswerState[][] answerStates = adapterTable.getmAnswersState();
            if (answerStates != null && answerStates[0][0].getRelative_id() != null && adapterTable.isCompleted()) {
                if (currentElement.getElementOptionsR().getJump() != null)
                    nextElementId = currentElement.getElementOptionsR().getJump();
                else
                    nextElementId = getElement(answerStates[0][0].getRelative_id()).getElementOptionsR().getJump();
                ElementPassedR elementPassedR = new ElementPassedR();
                elementPassedR.setRelative_id(currentElement.getRelative_id());
                elementPassedR.setProject_id(currentElement.getProjectId());
                elementPassedR.setToken(getQuestionnaire().getToken());
                elementPassedR.setDuration(startTime - DateUtils.getCurrentTimeMillis());
                try {
                    getDao().insertElementPassedR(elementPassedR);
                    getDao().setWasElementShown(true, startElementId, currentElement.getUserId(), currentElement.getProjectId());
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
            }
        }

        if (saved) {
            updatePrevElement();
        }
        return saved;
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position != answersList.size()) {
            spinnerSelection = position;
            showToast(answersList.get(position).getElementOptionsR().getTitle());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        showToast("Выберите ответ (не выбрано)");
    }

    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
        @Override
        public void run() {
            UiUtils.hideKeyboard(getContext(), getView());
        }
    };


//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Parcelable listState = Objects.requireNonNull(rvAnswers.getLayoutManager()).onSaveInstanceState();
//        outState.putParcelable(KEY_RECYCLER_STATE, listState);
//        outState.putSerializable("LIST", (ArrayList<AnswerState>) adapterList.getAnswers());
//        outState.putLong("startTime", startTime);
//        outState.putInt("startElementId", startElementId);
//        Log.d(TAG, "onSaveInstanceState: " + nextElementId);
//        if (nextElementId != null)
//            outState.putInt("nextElementId", nextElementId);
//        outState.putInt("prevElementId", prevElementId);
//        outState.putString("answerType", answerType);
//        outState.putBoolean("isTitle1Hided", isTitle1Hided);
//        outState.putBoolean("isTitle2Hided", isTitle2Hided);
//        outState.putInt("spinnerSelection", spinnerSelection);
//        outState.putInt("lastSelectedPosition", adapterList.getLastSelectedPosition());
//    }
//
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        restoreViews(savedInstanceState);
//        restoreData(savedInstanceState);
//    }

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
        List<PrevElementsR> prevList = getQuestionnaire().getPrev_element_id();
        if (prevList != null && prevList.size() > 0) {
            PrevElementsR lastPassedElement = prevList.get(prevList.size() - 1);
            startElementId = lastPassedElement.getNextId();
        }
    }

    public void loadSavedData() {
        if (answerType.equals(ElementSubtype.LIST)) {
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

            for (int i = 0; i < answersList.size(); i++) {
                ElementPassedR answerStateRestored = null;
                try {
                    answerStateRestored = getDao().getElementPassedR(getQuestionnaire().getToken(), answersList.get(i).getRelative_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (answerStateRestored != null) {
                    spinnerSelection = i;
                    spinnerAnswers.setSelection(spinnerSelection);
                }
            }

        } else if (answerType.equals(ElementSubtype.TABLE)) {
            AnswerState[][] answersTableState = adapterTable.getmAnswersState();
            Log.d(TAG, ">>>>>>>>>>>>>>> load from table: " + answersTableState.length + "." + answersTableState[0].length);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO save startTime
    }

    @Override
    public boolean onBackPressed() {
        if (isExit) {
            replaceFragment(new HomeFragment());
        } else {
            Toast.makeText(getContext(), getString(R.string.exit_questionaire_warning), Toast.LENGTH_SHORT).show();
            isExit = true;
        }
        return true;
    }

    public boolean saveQuestionnaire() {
        showToast("Анкета сохранена");
        replaceFragment(new HomeFragment());
        return true;
    }

    public void exitQuestionnaire() {
        showToast("Выход без сохранения.");
        replaceFragment(new HomeFragment());
    }
}

