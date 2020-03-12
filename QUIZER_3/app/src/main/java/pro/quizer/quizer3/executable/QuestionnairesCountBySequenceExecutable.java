package pro.quizer.quizer3.executable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.model.QuestionnaireStatus;

public class QuestionnairesCountBySequenceExecutable extends BaseModelExecutable<Integer> {

    private final Set<Integer> mSet;
    private final int userId;
    private final int userProjectId;
    private MainActivity activity;


    public QuestionnairesCountBySequenceExecutable(MainActivity activity, final int userId, final int userProjectId, final Set<Integer> pSet) {
        super();

        this.userId = userId;
        this.userProjectId = userProjectId;
        this.activity = activity;
        mSet = pSet;
    }

    @Override
    public Integer execute() {
        int count = 0;

        final List<QuestionnaireDatabaseModelR> sentQuestionnaires = activity.getMainDao().getQuestionnaireForQuotas(userId, userProjectId,QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED);

        for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : sentQuestionnaires) {

            final List<ElementDatabaseModelR> elements = activity.getMainDao().getElementByToken(questionnaireDatabaseModel.getToken());

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