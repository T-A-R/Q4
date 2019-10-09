package pro.quizer.quizer3.view.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementFragment extends ScreenFragment implements View.OnClickListener {

    private Toolbar toolbar;
    private Button btnNext;
    private Button btnPrev;
    private Button btnExit;
    private RelativeLayout cont;
    private LinearLayout titleCont1;
    private LinearLayout titleCont2;
    private LinearLayout titleImagesCont1;
    private LinearLayout titleImagesCont2;
    private LinearLayout questionCont;
    private LinearLayout questionImagesCont;
    private TextView tvTitle1;
    private TextView tvTitle2;
    private TextView tvQuestion;
    private TextView tvTitleDesc1;
    private TextView tvTitleDesc2;
    private TextView tvQuestionDesc;
    private RecyclerView rvAnswers;
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

    public ElementFragment() {
        super(R.layout.fragment_element);
    }

    @Override
    protected void onReady() {

        toolbar = findViewById(R.id.toolbar);
        cont = (RelativeLayout) findViewById(R.id.cont_element_fragment);
        titleCont1 = (LinearLayout) findViewById(R.id.title_cont_1);
        titleCont2 = (LinearLayout) findViewById(R.id.title_cont_2);
        titleImagesCont1 = (LinearLayout) findViewById(R.id.title_images_cont_1);
        titleImagesCont2 = (LinearLayout) findViewById(R.id.title_images_cont_2);
        questionCont = (LinearLayout) findViewById(R.id.question_cont);
        questionImagesCont = (LinearLayout) findViewById(R.id.question_images_cont);
        rvAnswers = (RecyclerView) findViewById(R.id.answers_recyclerview);
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

        tvTitle1.setTypeface(Fonts.getFuturaPtBook());
        tvTitle2.setTypeface(Fonts.getFuturaPtBook());
        tvTitleDesc1.setTypeface(Fonts.getFuturaPtBook());
        tvTitleDesc2.setTypeface(Fonts.getFuturaPtBook());
        tvQuestion.setTypeface(Fonts.getFuturaPtBook());
        tvQuestionDesc.setTypeface(Fonts.getFuturaPtBook());
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
        initQuestion();
    }

    @Override
    public void onClick(View view) {
        if (view == btnNext) {
            onReady();

            if (!isNextBtnPressed) {
                isNextBtnPressed = true;
            }
        } else if (view == btnPrev) {
            showScreensaver(false);

            if (!isPrevBtnPressed) {
                isPrevBtnPressed = true;
            }
        } else if (view == btnExit) {
            replaceFragment(new HomeFragment());

            if (!isExitBtnPressed) {
                isExitBtnPressed = true;
            }
        } else if (view == closeImage1) {
            titleCont1.setVisibility(View.GONE);
            toolbar.showInfoView();
        } else if (view == closeImage2) {
            titleCont2.setVisibility(View.GONE);
            toolbar.showInfoView();
        }
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initQuestion() {

        if (getCurrentElements() != null && getCurrentElements().size() > 0) {
            for (int i = 0; i < getCurrentElements().size(); i++) {
                if (!getCurrentElements().get(i).getType().equals(ElementType.BOX)) {
                    currentQuestionId = getCurrentElements().get(i).getRelative_id();
                    currentElement = getCurrentElements().get(i);
                    break;
                }
            }
        }

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
                if (parentElement.getType().equals(ElementType.BOX)) {
                    titleCont2.setVisibility(View.VISIBLE);
                    tvTitleDesc2.setVisibility(View.VISIBLE);
                    tvTitle2.setText(parentElement.getElementOptionsR().getTitle());
                    if (parentElement.getElementOptionsR().getDescription() != null)
                        tvTitleDesc2.setText(parentElement.getElementOptionsR().getDescription());

                    if (parentElement.getRelative_parent_id() != null) {
                        ElementItemR parentElement2 = null;
                        try {
                            parentElement2 = getElement(parentElement.getRelative_parent_id());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (parentElement2 != null) {
                            if (parentElement2.getType().equals(ElementType.BOX)) {
                                titleCont1.setVisibility(View.VISIBLE);
                                tvTitleDesc1.setVisibility(View.VISIBLE);
                                tvTitle1.setText(parentElement2.getElementOptionsR().getTitle());
                                if (parentElement2.getElementOptionsR().getDescription() != null)
                                    tvTitleDesc1.setText(parentElement2.getElementOptionsR().getDescription());
                            }
                        }

                    }
                }
            }
        }


    }
}

