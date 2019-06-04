package pro.quizer.quizerexit.executable;

import com.activeandroid.query.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.database.ElementDatabaseModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;

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

        final List<QuestionnaireDatabaseModel> sentQuestionnaires = new Select()
                .from(QuestionnaireDatabaseModel.class)
                .where(QuestionnaireDatabaseModel.STATUS + " =? AND " +
                                QuestionnaireDatabaseModel.USER_ID + " =? AND " +
                                QuestionnaireDatabaseModel.USER_PROJECT_ID + " =?",
                        QuestionnaireStatus.NOT_SENT, userId, userProjectId)
                .execute();

        for (final QuestionnaireDatabaseModel questionnaireDatabaseModel : sentQuestionnaires) {
            final List<ElementDatabaseModel> elements = new Select()
                    .from(ElementDatabaseModel.class)
                    .where(ElementDatabaseModel.TOKEN + " =?", questionnaireDatabaseModel.token)
                    .execute();

            final Set<Integer> set = new HashSet<>();

            for (final ElementDatabaseModel elementDatabaseModel : elements) {
                set.add(elementDatabaseModel.relative_id);
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