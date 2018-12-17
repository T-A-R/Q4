package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.List;

import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.fragment.ElementFragment;
import pro.quizer.quizerexit.model.ElementDatabaseType;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.database.ElementDatabaseModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.GpsUtils;
import pro.quizer.quizerexit.utils.StringUtils;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ElementActivity extends BaseActivity implements NavigationCallback {

    private static int BEST_QUALITY_PHOTO = 4000;

    UserModel mUserModel;
    ConfigModel mConfig;
    ProjectInfoModel mProjectInfo;
    List<ElementModel> mElements;

    private String mToken;
    private String mLoginAdmin;
    private String mLogin;
    private String mPassword;
    private int mQuestionnaireId;
    private int mProjectId;
    private int mUserProjectId;
    private int mUserId;
    private String mGps;
    private String mUserLogin;
    private long mStartDateInterview;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {
        int location = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int writeStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int readStorage = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return location == PackageManager.PERMISSION_GRANTED &&
                camera == PackageManager.PERMISSION_GRANTED &&
                writeStorage == PackageManager.PERMISSION_GRANTED &&
                readStorage == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                ACCESS_FINE_LOCATION,
                CAMERA,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean readStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if ((mConfig.isForceGps() && !locationAccepted) || !cameraAccepted || !writeStorageAccepted || !readStorageAccepted) {
                        showToast(getString(R.string.permission_error));

                        finish();

                        return;
                    }
                }

                initStartValues();

                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        mUserModel = getCurrentUser();
        mConfig = mUserModel.getConfig();
        mProjectInfo = mConfig.getProjectInfo();
        mElements = mProjectInfo.getElements();

        if (!checkPermission()) {
            requestPermission();
        } else {
            initStartValues();
        }
    }

    @SuppressLint("MissingPermission")
    private void initStartValues() {
        mLoginAdmin = mConfig.getLoginAdmin();
        mLogin = mUserModel.login;
        mPassword = mUserModel.password;
        mQuestionnaireId = mProjectInfo.getQuestionnaireId();
        mProjectId = mProjectInfo.getProjectId();
        mUserLogin = mUserModel.login;
        mUserProjectId = mUserModel.user_project_id;
        mUserId = mUserModel.user_id;
        mStartDateInterview = DateUtils.getCurrentTimeMillis();
        mToken = StringUtils.generateToken();

        if (mConfig.isGps()) {
            try {
                mGps = GpsUtils.getCurrentGps(this, mConfig.isForceGps());
                showToast("Current GPS = " + mGps);
            } catch (Exception e) {
                showToast(e.toString());

                if (mConfig.isForceGps()) {
                    finish();
                } else {
                    showNextElement();
                }
            }
        }

        showNextElement();
    }

    private void showNextElement() {
        showNextElement(mElements.get(0).getRelativeID());
    }

    private void showNextElement(final int pNumberOfNextElement) {
        final ElementModel nextElement = getElementByRelativeId(pNumberOfNextElement);

        if (nextElement == null) {
            // it was last element
            saveQuestionnaireToDatabase();

            finish();
            startMainActivity();

            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,
                        ElementFragment.newInstance(
                                nextElement,
                                this,
                                mToken,
                                mLoginAdmin,
                                mUserId,
                                mUserLogin,
                                mProjectId))
                .addToBackStack(nextElement.getOptions().getTitle())
                .commit();
    }

    private ElementModel getElementByRelativeId(final int pRelativeId) {
        if (pRelativeId == 0) {
            return null;
        }

        for (final ElementModel element : mElements) {
            if (element.getRelativeID() == pRelativeId) {
                return element;
            }
        }

        return null;
    }

    @Override
    public void onForward(final ElementModel pElementModel) {
        final StringBuilder sb = new StringBuilder();
        int jumpValue = -1;

        for (int index = 0; index < pElementModel.getElements().size(); index++) {
            final ElementModel model = pElementModel.getElements().get(index);

            if (model.isFullySelected()) {
                sb.append(model.getOptions().getTitle()).append("\n");
                jumpValue = model.getOptions().getJump();
            }
        }

        showToast(sb.toString());

        if (jumpValue == -1) {
            showToast(getString(R.string.error_counting_next_element));
        } else {
            pElementModel.setEndTime(DateUtils.getCurrentTimeMillis());
            showNextElement(jumpValue);
        }
    }

    @Override
    public void onBack() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            onBackPressed();
        }
    }

    @Override
    public void onExit() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        exitPoolAlertDialog();
    }

    public void exitPoolAlertDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.exit_pool_header)
                .setMessage(R.string.exit_pool_body)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startMainActivity();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }

    private void saveQuestionnaireToDatabase() {
        final long endTime = DateUtils.getCurrentTimeMillis();
        final long durationTimeQuestionnaire = endTime - mStartDateInterview;

        final QuestionnaireDatabaseModel questionnaireDatabaseModel = new QuestionnaireDatabaseModel();
        questionnaireDatabaseModel.status = QuestionnaireStatus.NOT_SENT;
        questionnaireDatabaseModel.token = mToken;
        questionnaireDatabaseModel.login_admin = mLoginAdmin;
        questionnaireDatabaseModel.login = mLogin;
        questionnaireDatabaseModel.passw = mPassword;
        questionnaireDatabaseModel.questionnaire_id = mQuestionnaireId;
        questionnaireDatabaseModel.project_id = mProjectId;
        questionnaireDatabaseModel.user_project_id = mUserProjectId;
        questionnaireDatabaseModel.gps = mGps;
        questionnaireDatabaseModel.date_interview = mStartDateInterview;

        questionnaireDatabaseModel.questions_passed = getCountOfShowingQuestions();
        questionnaireDatabaseModel.screens_passed = getCountOfShowingScreens();
        questionnaireDatabaseModel.duration_time_questionnaire = (int) durationTimeQuestionnaire;

        questionnaireDatabaseModel.save();

        for (final ElementModel element : mElements) {
            final ElementDatabaseModel elementDatabaseModel = new ElementDatabaseModel();
            elementDatabaseModel.token = mToken;
            elementDatabaseModel.relative_id = element.getRelativeID();
            elementDatabaseModel.duration = element.getDuration();
            elementDatabaseModel.type = ElementDatabaseType.SCREEN;

            elementDatabaseModel.save();
        }
    }

    // TODO: 10.12.2018 including questions in elements
    private int getCountOfShowingQuestions() {
        int count = 0;

        for (final ElementModel elementModel : mElements) {
            if (ElementType.QUESTION.equals(elementModel.getType()) && elementModel.isShowing()) {
                count++;
            }
        }

        return count;
    }

    private int getCountOfShowingScreens() {
        int count = 0;

        for (final ElementModel elementModel : mElements) {
            if (elementModel.isShowing()) {
                count++;
            }
        }

        return count;
    }
}