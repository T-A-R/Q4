package pro.quizer.quizerexit.executable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.ElementDatabaseModelR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.QuestionsMatchesModel;
import pro.quizer.quizerexit.model.config.StagesModel;
import pro.quizer.quizerexit.model.sms.SmsAnswer;
import pro.quizer.quizerexit.model.sms.SmsStage;

public class SmsStageModelExecutable extends BaseModelExecutable<Map<String, SmsAnswer>> {

    private final int mUserId;
    private Map<Integer, ElementModel> mMap;
    private final StagesModel mStagesModel;
    private SmsStage mSmsStage;

    public SmsStageModelExecutable(final BaseActivity pBaseActivity, final SmsStage pSmsStage, final UserModelR pUserModel, final StagesModel pStageModel) {
        super();

        final List<ElementModel> elements = pUserModel.getConfigR().getProjectInfo().getElements();

        mStagesModel = pStageModel;
        mUserId = pUserModel.getUser_id();
        mSmsStage = pSmsStage;
        mMap = pBaseActivity.createNewMap(elements);
    }

    @Override
    public Map<String, SmsAnswer> execute() {
        final Map<String, SmsAnswer> result = new LinkedHashMap<>();

        for (final QuestionsMatchesModel questionMatch : mStagesModel.getQuestionsMatches()) {
            final String smsNum = questionMatch.getSmsNum();
            final int questionId = questionMatch.getQuestionId();
            final ElementModel element = mMap.get(questionId);
            final int countSubElements = element.getNotNullableSubElementsCount();

            final SmsAnswer smsAnswer = new SmsAnswer(smsNum, countSubElements);

            result.put(smsNum, smsAnswer);
        }

        load(QuestionnaireStatus.SENT, result);
        load(QuestionnaireStatus.NOT_SENT, result);


        return result;
    }

    private void load(@QuestionnaireStatus final String pStatus, final Map<String, SmsAnswer> result) {
        final List<ElementDatabaseModelR> allElements = new ArrayList<>();

        final List<QuestionnaireDatabaseModelR> questionnaires = BaseActivity.getDao().getQuestionnaireWithTime(
                mUserId,
                pStatus,
                mStagesModel.getTimeFrom(),
                mStagesModel.getTimeTo());

        for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : questionnaires) {
            final String token = questionnaireDatabaseModel.getToken();

            mSmsStage.addToken(token);

            final List<ElementDatabaseModelR> elements = BaseActivity.getDao().getElementByToken(token);
            allElements.addAll(elements);
        }

        final List<QuestionsMatchesModel> matches = mStagesModel.getQuestionsMatches();

        for (final QuestionsMatchesModel match : matches) {
            final String index = match.getSmsNum();
            List<ElementDatabaseModelR> elementsByQuestionId = getElementsByParentId(allElements, match.getQuestionId());

            if (elementsByQuestionId != null && !elementsByQuestionId.isEmpty()) {
                final SmsAnswer smsAnswer = result.get(index);
                final int answersCount = smsAnswer.getAnswersCount();

                for (int i = 1; i <= answersCount; i++) {
                    final int countAnswers = getCountAnswersByOrder(elementsByQuestionId, i);
                    smsAnswer.put(i - 1, countAnswers);
                }

                result.put(index, smsAnswer);
                mSmsStage.setStatus(pStatus);
            }
        }
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
