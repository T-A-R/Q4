package pro.quizer.quizer3.executable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
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

    public SmsStageModelExecutable(final MainActivity pBaseActivity, final SmsStage pSmsStage, final UserModelR pUserModel, final StagesModel pStageModel) {
        super();

        final List<ElementModelNew> elements = pUserModel.getConfigR().getProjectInfo().getElements();

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
            final ElementModelNew element = mMap.get(questionId);
            final int countSubElements = element.getNotNullableSubElementsCount();

            final SmsAnswer smsAnswer = new SmsAnswer(smsNum, countSubElements);

            result.put(smsNum, smsAnswer);
        }

        load(QuestionnaireStatus.NOT_SENT, result);


        return result;
    }

    private void load(@QuestionnaireStatus final String pStatus, final Map<String, SmsAnswer> result) {

        final List<ElementDatabaseModelR> allElements = new ArrayList<>();

        final List<QuestionnaireDatabaseModelR> questionnaires = MainActivity.getStaticDao().getQuestionnaireWithTime(
                mUserId,
                pStatus,
                mStagesModel.getTimeFrom(),
                mStagesModel.getTimeTo());

        for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : questionnaires) {
            final String token = questionnaireDatabaseModel.getToken();

            mSmsStage.addToken(token);

            final List<ElementDatabaseModelR> elements = MainActivity.getStaticDao().getElementByToken(token);
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
//                mSmsStage.setStatus(pStatus);
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
