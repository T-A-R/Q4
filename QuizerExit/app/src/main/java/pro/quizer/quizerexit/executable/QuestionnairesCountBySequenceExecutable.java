package pro.quizer.quizerexit.executable;

import com.activeandroid.query.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.ElementDatabaseModelR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.database.ElementDatabaseModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;

public class QuestionnairesCountBySequenceExecutable extends BaseModelExecutable<Integer> {

    private final Set<Integer> mSet;
    private final int userId;
    private final int userProjectId;


    public QuestionnairesCountBySequenceExecutable(final int userId, final int userProjectId, final Set<Integer> pSet) {
        super();

        this.userId = userId;
        this.userProjectId = userProjectId;
        mSet = pSet;
    }

    @Override
    public Integer execute() {
        int count = 0;

        // GOOD select

//        final List<QuestionnaireDatabaseModel> sentQuestionnaires = new Select()
//                .from(QuestionnaireDatabaseModel.class)
//                .where(QuestionnaireDatabaseModel.STATUS + " =? AND " +
//                                QuestionnaireDatabaseModel.USER_ID + " =? AND " +
//                                QuestionnaireDatabaseModel.USER_PROJECT_ID + " =? AND " +
//                                QuestionnaireDatabaseModel.SURVEY_STATUS + " !=?",
//                        QuestionnaireStatus.NOT_SENT, userId, userProjectId, "aborted")
//                .execute();

        final List<QuestionnaireDatabaseModelR> sentQuestionnaires = BaseActivity.getDao().getQuestionnaireForQuotas(userId, userProjectId,QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.ABORTED);

        for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : sentQuestionnaires) {
//        for (final QuestionnaireDatabaseModel questionnaireDatabaseModel : sentQuestionnaires) {

            // GOOD select
//            final List<ElementDatabaseModel> elements = new Select()
//                    .from(ElementDatabaseModel.class)
//                    .where(ElementDatabaseModel.TOKEN + " =?", questionnaireDatabaseModel.token)
//                    .execute();
//
//            final Set<Integer> set = new HashSet<>();
//
//            for (final ElementDatabaseModel elementDatabaseModel : elements) {
//                set.add(elementDatabaseModel.relative_id);
//            }

//            final List<ElementDatabaseModelR> elements = BaseActivity.getDao().getElementByToken(questionnaireDatabaseModel.token);
            final List<ElementDatabaseModelR> elements = BaseActivity.getDao().getElementByToken(questionnaireDatabaseModel.getToken());

            final Set<Integer> set = new HashSet<>();

            for (final ElementDatabaseModelR elementDatabaseModel : elements) {
                set.add(elementDatabaseModel.getRelative_id());
            }

            int matchesCount = 0;

            for (final Integer relativeId : mSet) {
                if (set.contains(relativeId)) {
                    matchesCount++;
                }
            }

            if (matchesCount == mSet.size()) {
                count++;
            }
        }

        return count;
    }
}