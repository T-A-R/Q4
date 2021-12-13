package pro.quizer.quizer3.executable;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.quota.QuotaModel;

import static pro.quizer.quizer3.MainActivity.TAG;

public class QuotasTreeMaker extends BaseModelExecutableWithCallback<ElementItemR[][]> {

    private List<ElementItemR> quotaList;
    private MainActivity activity;

    public QuotasTreeMaker(List<ElementItemR> quotaList, MainActivity activity, final ICallback pCallback) {
        super(pCallback);

        this.quotaList = quotaList;
        this.activity = activity;
    }

    @Override
    public ElementItemR[][] execute() {
        Log.d(TAG, "====== PREPARING QUOTAS TREE MAKER ======");
        return fillQuotas(getTree(quotaList), activity);
    }

    public static ElementItemR[][] getTree(List<ElementItemR> quotasBlock) {

        List<ElementItemR> questions = new ArrayList<>();
        int answersTotal = 1;
        int answersMultiple = 1;
        List<Integer> answersCounters = new ArrayList<>();

        for (ElementItemR element : quotasBlock) {
            if (element.getType().equals(ElementType.QUESTION)) {
                questions.add(element);
                answersCounters.add(element.getElements().size());
                answersTotal = answersTotal * element.getElements().size(); // element.getElements() - список ответов
            }
        }

        ElementItemR[][] tree = new ElementItemR[questions.size()][answersTotal];

        for (int i = 0; i < questions.size(); i++) {
            int n = 0;
            List<ElementItemR> answers = questions.get(i).getElements();
            answersMultiple = answersMultiple * answers.size();
            int counter = 0;

            for (int k = 0; k < answersTotal; k++) {
                tree[i][k] = ElementItemR.clone(answers.get(n));
                counter++;
                if (counter == (answersTotal / answersMultiple)) {
                    n++;
                    counter = 0;
                    if (n == answers.size()) {
                        n = 0;
                    }
                }
            }
        }

        return tree;
    }

    private ElementItemR[][] fillQuotas(ElementItemR[][] tree, MainActivity activity) {
        Log.d(TAG, "============== fillQuotas ======================= 3");
        int user_id = activity.getCurrentUserId();
        Integer user_project_id = null;
        user_project_id = activity.getCurrentUser().getConfigR().getUserProjectId();
        if (user_project_id == null)
            user_project_id = activity.getCurrentUser().getUser_project_id();
        List<QuotaModel> quotas = new ArrayList<>();

        final List<QuotaR> quotasR = activity.getMainDao().getQuotaR(user_project_id);
        for (QuotaR quotaR : quotasR) {
            quotas.add(new QuotaModel(quotaR.getSequence(), quotaR.getLimit(), quotaR.getDone(), user_id, user_project_id));
        }
        if (quotas == null || quotas.isEmpty()) return tree;

        for (int q = 0; q < quotas.size(); q++) {
            Integer[] sequence = quotas.get(q).getArray();

            for (int i = 0; i < tree.length; i++) {
                for (int k = 0; k < tree[i].length; k++) {
                    if (sequence[0].equals(tree[i][k].getRelative_id())) {
                        int temp = i + 1;
                        if (sequence.length > 1) {
                            for (int s = 1; s < sequence.length; ) {
                                if (sequence[s].equals(tree[temp][k].getRelative_id())) {
                                    if (s == sequence.length - 1) {
                                        if (tree[temp][k].getLimit() > quotas.get(q).getLimit()) {
                                            tree[temp][k].setLimit(quotas.get(q).getLimit());
                                            tree[temp][k].setDone(quotas.get(q).getDone(activity));
                                            int done = tree[temp][k].getDone();
                                            int local = 0;
                                            int total = done + local;
                                            int limit = tree[temp][k].getLimit();
                                            if (total >= limit) {
                                                tree[temp][k].setEnabled(false);
                                                for (int x = temp - 1; x >= 0; x--) {
                                                    tree[x][k].setEnabled(false);
                                                }
                                            }
                                        }
                                    }
                                    s++;
                                } else {
                                    temp++;
                                    if (temp == tree.length) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (tree[i][k].getLimit() > quotas.get(q).getLimit()) {
                                tree[i][k].setLimit(quotas.get(q).getLimit());
                                tree[i][k].setDone(quotas.get(q).getDone(activity));
                                int done = tree[i][k].getDone();
                                int limit = tree[i][k].getLimit();
                                int local = 0;
                                int total = done + local;
                                if (total >= limit) {
                                    tree[i][k].setEnabled(false);
                                }
                            }
                        }
                    }
                }
            }
        }

//        showTree(tree); // Для отладки
        onSuccess();
        return tree;
    }

    public static void showTree(ElementItemR[][] tree) {
        if (tree != null) {

            Log.d(TAG, "=============== Final Quotas ======================");
            try {
                for (int i = 0; i < tree[0].length; i++) {
                    Log.d(TAG, tree[0][i].getElementOptionsR().getRelative_id() + " " + tree[0][i].getDone() + "/" + tree[0][i].getLimit() + "/" + tree[0][i].isEnabled() + " | " +
                            tree[1][i].getElementOptionsR().getRelative_id() + " " + tree[1][i].getDone() + "/" + tree[1][i].getLimit() + "/" + tree[1][i].isEnabled() + " | " +
                            tree[2][i].getElementOptionsR().getRelative_id() + " " + tree[2][i].getDone() + "/" + tree[2][i].getLimit() + "/" + tree[2][i].isEnabled() + " | "+
                            tree[3][i].getElementOptionsR().getRelative_id() + " " + tree[3][i].getDone() + "/" + tree[3][i].getLimit() + "/" + tree[3][i].isEnabled() + " | "+
                            tree[4][i].getElementOptionsR().getRelative_id() + " " + tree[4][i].getDone() + "/" + tree[4][i].getLimit() + "/" + tree[4][i].isEnabled() + " | "+
                            tree[5][i].getElementOptionsR().getRelative_id() + " " + tree[5][i].getDone() + "/" + tree[5][i].getLimit() + "/" + tree[5][i].isEnabled() + " | "+
                            tree[6][i].getElementOptionsR().getRelative_id() + " " + tree[6][i].getDone() + "/" + tree[6][i].getLimit() + "/" + tree[6][i].isEnabled() + " | "

                    );
                }
            } catch (Exception e) {
                Log.d(TAG, "Не тестовый проект!");
            }
        }
    }

    public static int getLocalQuotas(MainActivity activity, Integer[] sequence) {
        int counter = 0;
        Set<Integer> mSet = new HashSet<>(Arrays.asList(sequence));
        Log.d(TAG, "getLocalQuotas: sequence " + sequence.length);
        try {
            Integer user_project_id = null;
            user_project_id = activity.getCurrentUser().getConfigR().getUserProjectId();
            if (user_project_id == null)
                user_project_id = activity.getCurrentUser().getUser_project_id();

            List<QuestionnaireDatabaseModelR> offlineQuestionnaires = activity.getMainDao().getQuestionnaireForQuotas(activity.getCurrentUserId(), user_project_id, QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED);

            for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : offlineQuestionnaires) {
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
                    counter++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getLocalQuotas: " + counter);
        return counter;
    }
}