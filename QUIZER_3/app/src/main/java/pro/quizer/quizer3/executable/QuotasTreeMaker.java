package pro.quizer.quizer3.executable;

import android.util.Log;

import java.util.ArrayList;
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

public class QuotasTreeMaker extends BaseModelExecutable<ElementItemR[][]> {

    private List<ElementItemR> quotaList;
    private MainActivity activity;


    public QuotasTreeMaker(List<ElementItemR> quotaList, MainActivity activity) {
        super();

        this.quotaList = quotaList;
        this.activity = activity;
    }

    @Override
    public ElementItemR[][] execute() {
        return fillQuotas(getTree(quotaList), activity);
    }

    public static ElementItemR[][] getTree(List<ElementItemR> quotasBlock) {
        Log.d(TAG, "initQuestion: 3");
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

    private static ElementItemR[][] fillQuotas(ElementItemR[][] tree, MainActivity activity) {
        Log.d(TAG, "initQuestion: 4");
        Log.d(TAG, "============== fillQuotas ======================= ");
        int qn = 8;
        List<QuotaModel> quotas = activity.getCurrentUser().getQuotasR();
        if (quotas == null || quotas.isEmpty()) return tree;
        Log.d(TAG, "fillQuotas: tree: " + tree.length + "/" + tree[0].length);
        Log.d(TAG, "Quotas size: " + quotas.size());
        for (int q = 0; q < quotas.size(); q++) {
            Integer[] sequence = quotas.get(q).getArray();

//            Log.d(TAG, "sequence: " + q + " / " + sequence.length);
//            for (int t = 0; t < sequence.length; t++) {
//                Log.d(TAG, "sequence[" + t + "] = " + sequence[t]);
//            }
//            Log.d(TAG, "  ");
//            Log.d(TAG, "============== Quota: " + q);

            for (int i = 0; i < tree.length; i++) {
                for (int k = 0; k < tree[i].length; k++) {
                    if (sequence[0] == tree[i][k].getRelative_id()) {
                        int temp = i + 1;
                        if (sequence.length > 1) {
                            for (int s = 1; s < sequence.length; s++) {
                                if (sequence[s] == tree[temp][k].getRelative_id()) {

                                    if (s == sequence.length - 1) {
                                        if (tree[temp][k].getLimit() > quotas.get(q).getLimit()) {
                                            tree[temp][k].setLimit(quotas.get(q).getLimit());
                                            tree[temp][k].setDone(quotas.get(q).getDone());
                                            if ((tree[temp][k].getDone() + getLocalQuotas(activity, sequence)) >= tree[temp][k].getLimit()) {
                                                tree[temp][k].setEnabled(false);
                                                for (int x = temp - 1; x >= 0; x--) {
                                                    tree[x][k].setEnabled(false);
                                                }
                                            }
                                        }
                                    }
                                    temp++;
                                } else {
                                    break;
                                }
                            }
                        } else {
                            if (tree[i][k].getLimit() > quotas.get(q).getLimit()) {
                                tree[i][k].setLimit(quotas.get(q).getLimit());
                                tree[i][k].setDone(quotas.get(q).getDone());
                                if ((tree[i][k].getDone() + getLocalQuotas(activity, sequence)) >= tree[i][k].getLimit()) {
                                    tree[i][k].setEnabled(false);
                                }
                            }
                        }
                    }
                }
            }
        }

        return tree;
    }

    public static int getLocalQuotas(MainActivity activity, Integer[] sequence) {
        int counter = 0;
        Log.d(TAG, "getLocalQuotas: sequence " + sequence.length);
        try {
            //TODO: Добавить проверку на завершенность анкеты!
            List<QuestionnaireDatabaseModelR> questionnaires = MainActivity.getStaticDao().getQuestionnaireByUserIdWithStatus(activity.getCurrentUserId(), QuestionnaireStatus.NOT_SENT);
            for (final QuestionnaireDatabaseModelR questionnaireDatabaseModel : questionnaires) {
                final List<ElementDatabaseModelR> elements = MainActivity.getStaticDao().getElementByToken(questionnaireDatabaseModel.getToken());
                int found = 0;
                for (int s = 0; s < sequence.length; s++) {
                    for (final ElementDatabaseModelR element : elements) {
                        if (sequence[s] == element.getRelative_id()) {
                            found++;
                            break;
                        }
                    }
                }
                if (found == sequence.length) {
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