package pro.quizer.quizer3.executable;

import static pro.quizer.quizer3.view.fragment.SmartFragment.getDao;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.SmsAnswersR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ElementModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.QuestionsMatchesModel;
import pro.quizer.quizer3.model.config.StagesModel;
import pro.quizer.quizer3.model.sms.SmsAnswer;
import pro.quizer.quizer3.model.sms.SmsStage;

public class SmsStageModelExecutable extends BaseModelExecutable<Map<String, SmsAnswer>> {

    private final int mUserId;
    private Map<Integer, ElementModelNew> mMap;
    private final StagesModel mStagesModel;
    private SmsStage mSmsStage;
    private MainActivity activity;

    public SmsStageModelExecutable(final MainActivity pBaseActivity, final SmsStage pSmsStage, final UserModelR pUserModel, final StagesModel pStageModel) {
        super();

        final List<ElementModelNew> elements = pBaseActivity.getConfig().getProjectInfo().getElements();

        mStagesModel = pStageModel;
        mUserId = pUserModel.getUser_id();
        mSmsStage = pSmsStage;
        mMap = pBaseActivity.createNewMap(elements);
        activity = pBaseActivity;
    }

    @Override
    public Map<String, SmsAnswer> execute() {
        final Map<String, SmsAnswer> result = new LinkedHashMap<>();

        for (final QuestionsMatchesModel questionMatch : mStagesModel.getQuestionsMatches()) {
            final String smsNum = questionMatch.getSmsNum();
            final int questionId = questionMatch.getQuestionId();
//            Log.d("T-A-R.SmsStageModelEx", "=== questionId: " + questionId);
            final ElementModelNew element = mMap.get(questionId);
//            Log.d("T-A-R.SmsStageModelEx", "=== mMap.size(): " + mMap.size());

            final int countSubElements = element.getNotNullableSubElementsCount();
//            Log.d("T-A-R.SmsStageModelEx", "=== element: " + element.getRelativeID() + " : " + countSubElements);

            final SmsAnswer smsAnswer = new SmsAnswer(smsNum, countSubElements);

            result.put(smsNum, smsAnswer);
        }

        load(QuestionnaireStatus.NOT_SENT, result);


        return result;
    }

    private void load(@QuestionnaireStatus final String pStatus, final Map<String, SmsAnswer> result) {
        Log.d("T-A-R.SmsStageModelEx", "load: <<<<<<<<<<<<<<<<<<<<<<");
        final List<ElementDatabaseModelR> allElements = new ArrayList<>();

        final Integer userId = activity.getCurrentUserId();

        final List<QuestionnaireDatabaseModelR> questionnaires = activity.getMainDao().getQuestionnaireWithTime(
                mUserId,
                pStatus,
                false,
                mStagesModel.getTimeFrom(),
                mStagesModel.getTimeTo());

        Integer quizCounter = questionnaires.size();

        Log.d("T-A-R.SmsStageModelEx", "before load: " + userId + "/" + quizCounter);

//        if (saved != null) {
//            result.append(mUserId).append(" ").append(mSmsIndex).append(" ").append(mQuizCount + saved.getQuizQuantity());
//        } else

        for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : questionnaires) {
            final String token = questionnaireDatabaseModel.getToken();

            mSmsStage.addToken(token);

            final List<ElementDatabaseModelR> elements = activity.getMainDao().getElementByToken(token);
            allElements.addAll(elements);
        }

        final List<QuestionsMatchesModel> matches = mStagesModel.getQuestionsMatches();
        Log.d("T-A-R.SmsStageModelEx", "before load: " + userId + "/" + quizCounter);
        final List<SmsAnswersR> savedAnswers = activity.getMainDao().getSmsAnswersByUserId(userId);
        Map<String, List<Integer>> savedAnswersMap = mapSavedAnswers(savedAnswers);
        for (final QuestionsMatchesModel match : matches) {
            final String index = match.getSmsNum();
            List<ElementDatabaseModelR> elementsByQuestionId = getElementsByParentId(allElements, match.getQuestionId());
            List<Integer> saved = savedAnswersMap.get(index);

            SmsAnswer smsAnswer = result.get(index);
            final int answersCount = smsAnswer.getAnswersCount();
            if (!elementsByQuestionId.isEmpty()) {
                for (int i = 1; i <= answersCount; i++) {
                    int countAnswers = getCountAnswersByOrder(elementsByQuestionId, i);
                    if(saved != null && !saved.isEmpty()) {
                        countAnswers += saved.get(i - 1);
                    }
                    smsAnswer.put(i - 1, countAnswers);
                }
            } else {
                for (int i = 0; i < answersCount; i++) {
                    if(saved != null && !saved.isEmpty()) {
                        smsAnswer.put(i, saved.get(i));
                    }

                }
//                Log.d("T-A-R.SmsStageModelEx", "load: elementsByQuestionId.isEmpty()");
            }
            SmsAnswersR savedAnswer = getDao().getSmsAnswersBySmsId(userId, smsAnswer.getSmsIndex());
            if(savedAnswer != null && savedAnswer.getQuizQuantity() != null) {
                smsAnswer.setQuizCount(quizCounter + savedAnswer.getQuizQuantity());
            } else {
                smsAnswer.setQuizCount(quizCounter);
            }
            Log.d("T-A-R.SmsStageModelEx", "N:" + smsAnswer.getSmsIndex() + "load: " + quizCounter + "/" + smsAnswer.getQuizCount());
            smsAnswer.setUserId(userId);
            result.put(index, smsAnswer);

        }
    }

    private Map<String, List<Integer>> mapSavedAnswers(List<SmsAnswersR> list) {
        Map<String, List<Integer>> saved = new HashMap<>();
        for(SmsAnswersR item : list) {
            saved.put(item.getSmsIndex(), item.getAnswers());
        }
        return saved;
    }

    private List<ElementDatabaseModelR> getElementsByParentId(final List<ElementDatabaseModelR> pAllElements, final int pRelativeId) {
        final List<ElementDatabaseModelR> result = new ArrayList<>();

        for (final ElementDatabaseModelR elementDatabaseModel : pAllElements) {
            if (elementDatabaseModel.getRelative_parent_id() == pRelativeId) {
                result.add(elementDatabaseModel);
            }
        }
        return result;
    }

    private int getCountAnswersByOrder(final List<ElementDatabaseModelR> pAllElements, final int pOrder) {
        int count = 0;

        for (final ElementDatabaseModelR elementDatabaseModel : pAllElements) {
            if (elementDatabaseModel.getItem_order() == pOrder) {
                count++;
            }
        }
        return count;
    }
}
