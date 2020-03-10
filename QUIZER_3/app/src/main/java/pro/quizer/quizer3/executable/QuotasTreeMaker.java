package pro.quizer.quizer3.executable;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
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
        int qn = 8;
        List<QuotaModel> quotas = activity.getCurrentUser().getQuotasR();
        if (quotas == null || quotas.isEmpty()) return tree;

//        for (int q = 0; q < quotas.size(); q++) {
//            Integer[] sequence = quotas.get(q).getArray();
//
//            for (int i = 0; i < tree.length; i++) {
//                for (int k = 0; k < tree[i].length; k++) {
//                    if (sequence[0].equals(tree[i][k].getRelative_id())) {
//                        int temp = i + 1;
//                        if (sequence.length > 1) {
//                            for (int s = 1; s < sequence.length; s++) {
//                                if (sequence[s].equals(tree[temp][k].getRelative_id())) {
//
//                                    if (s == sequence.length - 1) {
//                                        if (tree[temp][k].getLimit() > quotas.get(q).getLimit()) {
//                                            tree[temp][k].setLimit(quotas.get(q).getLimit());
//                                            tree[temp][k].setDone(quotas.get(q).getDone());
//                                            if ((tree[temp][k].getDone() + getLocalQuotas(activity, sequence)) >= tree[temp][k].getLimit()) {
//                                                tree[temp][k].setEnabled(false);
//                                                for (int x = temp - 1; x >= 0; x--) {
//                                                    tree[x][k].setEnabled(false);
//                                                }
//                                            }
//                                        }
//                                    }
//                                    temp++;
//                                } else {
//                                    break;
//                                }
//                            }
//                        } else {
//                            if (tree[i][k].getLimit() > quotas.get(q).getLimit()) {
//                                tree[i][k].setLimit(quotas.get(q).getLimit());
//                                tree[i][k].setDone(quotas.get(q).getDone());
//                                if ((tree[i][k].getDone() + getLocalQuotas(activity, sequence)) >= tree[i][k].getLimit()) {
//                                    tree[i][k].setEnabled(false);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

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
                                            tree[temp][k].setDone(quotas.get(q).getDone());
                                            int done = tree[temp][k].getDone();
//                                            int local = getLocalQuotas(activity, sequence);
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
                                tree[i][k].setDone(quotas.get(q).getDone());
                                int done = tree[i][k].getDone();
                                int limit = tree[i][k].getLimit();
//                                int local = getLocalQuotas(activity, sequence);
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

    private static void showTree(ElementItemR[][] tree) {
        if (tree != null) {

            Log.d(TAG, "=============== Final Quotas ======================");
            try {
                for (int i = 0; i < tree[0].length; i++) {
                    Log.d(TAG, tree[0][i].getElementOptionsR().getTitle() + " " + tree[0][i].getDone() + "/" + tree[0][i].getLimit() + "/" + tree[0][i].isEnabled() + " | " +
                            tree[1][i].getElementOptionsR().getTitle() + " " + tree[1][i].getDone() + "/" + tree[1][i].getLimit() + "/" + tree[1][i].isEnabled() + " | " +
                            tree[2][i].getElementOptionsR().getTitle() + " " + tree[2][i].getDone() + "/" + tree[2][i].getLimit() + "/" + tree[2][i].isEnabled() + " | "

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
            //TODO: Добавить проверку на завершенность анкеты!
//            List<QuestionnaireDatabaseModelR> offlineQuestionnaires = MainActivity.getStaticDao().getQuestionnaireByUserIdWithStatus(activity.getCurrentUserId(), QuestionnaireStatus.NOT_SENT);
            List<QuestionnaireDatabaseModelR> offlineQuestionnaires = MainActivity.getStaticDao().getQuestionnaireForQuotas(activity.getCurrentUserId(), activity.getCurrentUser().getUser_project_id(), QuestionnaireStatus.NOT_SENT, Constants.QuestionnaireStatuses.COMPLETED);

            for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : offlineQuestionnaires) {
                final List<ElementDatabaseModelR> elements = MainActivity.getStaticDao().getElementByToken(questionnaireDatabaseModel.getToken());
//                int found = 0;
//                for (int s = 0; s < sequence.length; s++) {
//                    for (final ElementDatabaseModelR element : elements) {
//                        if (sequence[s].equals(element.getRelative_id())) {
//                            found++;
//                            break;
//                        }
//                    }
//                }
//                if (found == sequence.length) {
//                    counter++;
//                }

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
            MainActivity.addLog(activity.getCurrentUser().getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, activity.getString(R.string.get_quotas), Constants.LogResult.ERROR, activity.getString(R.string.log_error_102_desc), e.toString());
        }
        Log.d(TAG, "getLocalQuotas: " + counter);
        return counter;
    }
}