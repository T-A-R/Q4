package pro.quizer.quizerexit.executable;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.QuestionsMatchesModel;
import pro.quizer.quizerexit.model.config.StagesModel;
import pro.quizer.quizerexit.model.database.ElementDatabaseModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.sms.SmsAnswer;
import pro.quizer.quizerexit.model.sms.SmsStage;

public class SmsStageModelExecutable extends BaseModelExecutable<Map<String, SmsAnswer>> {

    private final int mUserId;
    private Map<Integer, ElementModel> mMap;
    private final StagesModel mStagesModel;
    private SmsStage mSmsStage;

    public SmsStageModelExecutable(final BaseActivity pBaseActivity, final SmsStage pSmsStage, final UserModel pUserModel, final StagesModel pStageModel) {
        super();

        final List<ElementModel> elements = pUserModel.getConfig().getProjectInfo().getElements();

        mStagesModel = pStageModel;
        mUserId = pUserModel.user_id;
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
        final List<ElementDatabaseModel> allElements = new ArrayList<>();

        final List<QuestionnaireDatabaseModel> questionnaires = new Select()
                .from(QuestionnaireDatabaseModel.class)
                .where(QuestionnaireDatabaseModel.USER_ID + " = ? AND " +
                                QuestionnaireDatabaseModel.STATUS + " =? AND " +
                                QuestionnaireDatabaseModel.DATE_INTERVIEW + " >=? AND " +
                                QuestionnaireDatabaseModel.DATE_INTERVIEW + " <=?",
                        mUserId,
                        pStatus,
                        mStagesModel.getTimeFrom(),
                        mStagesModel.getTimeTo())
                .execute();

        for (final QuestionnaireDatabaseModel questionnaireDatabaseModel : questionnaires) {
            final String token = questionnaireDatabaseModel.token;

            mSmsStage.addToken(token);

            final List<ElementDatabaseModel> elements = new Select()
                    .from(ElementDatabaseModel.class)
                    .where(ElementDatabaseModel.TOKEN + " =?", token)
                    .execute();

            allElements.addAll(elements);
        }

        final List<QuestionsMatchesModel> matches = mStagesModel.getQuestionsMatches();

        for (final QuestionsMatchesModel match : matches) {
            final String index = match.getSmsNum();
            List<ElementDatabaseModel> elementsByQuestionId = getElementsByParentId(allElements, match.getQuestionId());

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

    private List<ElementDatabaseModel> getElementsByParentId(final List<ElementDatabaseModel> pAllElements, final int pRelativeId) {
        final List<ElementDatabaseModel> result = new ArrayList<>();

        for (final ElementDatabaseModel elementDatabaseModel : pAllElements) {
            if (elementDatabaseModel.relative_parent_id == pRelativeId) {
                result.add(elementDatabaseModel);
            }
        }

        return result;
    }

    private int getCountAnswersByOrder(final List<ElementDatabaseModel> pAllElements, final int pOrder) {
        int count = 0;

        for (final ElementDatabaseModel elementDatabaseModel : pAllElements) {
            if (elementDatabaseModel.item_order == pOrder) {
                count++;
            }
        }

        return count;
    }
}
