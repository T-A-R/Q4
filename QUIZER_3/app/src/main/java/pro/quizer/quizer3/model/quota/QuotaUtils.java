package pro.quizer.quizer3.model.quota;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;

import static pro.quizer.quizer3.MainActivity.TAG;

public class QuotaUtils {

    public static boolean canShow(ElementItemR[][] tree, List<Integer> passedElementsId, int relativeId, int order) {
//        boolean canShowElement = true;
//        int positiveCounter = 0;
//        int negativeCounter = 0;

//        Log.d(TAG, "canShow: Relative_ID = " + relativeId);

        if (tree == null) {
//            Log.d(TAG, "canShow: Tree is NULL!");
            return true;
        }

//        if (passedElementsId == null || passedElementsId.size() == 0) {
        if (order == 1) {
//            Log.d(TAG, "canShow: Passed Elements is NULL!");
            for (int k = 0; k < tree[0].length; k++) {
                if (tree[0][k].getRelative_id().equals(relativeId)) {
                    if (tree[0][k].isEnabled())
                        return true;
                }
            }
            return false;
        } else {

//            Log.d(TAG, "canShow: Passed size: " + passedElementsId.size());

//            for (Integer id : passedElementsId) {
//                Log.d(TAG, "id: " + id);
//            }


            int endPassedElement = order - 1;
//            for(int p = 0; p < endPassedElement; p++) {
//                if(passedElementsId.get(p).equals(relativeId)) {
//                    endPassedElement = p;
//                    break;
//                }
//            }

            for (int k = 0; k < tree[0].length; k++) {
                for (int i = 0; i < endPassedElement; ) {
//                    if(tree[i][k].getRelative_id().equals(relativeId)) {
//                        endPassedElement = i;
//                    }
//                    Log.d(TAG, "canShow: lines " + k + " | " + i + " : " + tree[i][k].getRelative_id() + "/" + passedElementsId.get(i));
                    if (tree[i][k].getRelative_id().equals(passedElementsId.get(i))) {
//                        Log.d(TAG, "Нашел: " + tree[i][k].getElementOptionsR().getTitle() + "/id=" + tree[i][k].getRelative_id() + "/relative=" + relativeId);
                        if (i == (endPassedElement - 1)) { // Если последний, то
//                            Log.d(TAG, "Он последний");
                            if (tree[i + 1][k].getRelative_id().equals(relativeId)) { // Если следующий за последним равен Relative ID
//                                Log.d(TAG, "Следующий элемент совпал с Relative ID");
                                if (tree[i + 1][k].isEnabled()) {
//                                    Log.d(TAG, "Он включен: ");
                                    return true;
                                } else {
//                                    Log.d(TAG, "Он выключен: ");
                                }
                            } else {
//                                Log.d(TAG, "Следующий элемент не совпал");
                            }
                        } else {
//                            Log.d(TAG, "Он не последний");
//                            if (tree[i][k].getRelative_id() == relativeId) { // Если следующий за последним равен Relative ID
//////                                Log.d(TAG, "Следующий элемент совпал с Relative ID");
////                                if (tree[i][k].isEnabled()) {
//////                                    Log.d(TAG, "Он включен: ");
////                                    return true;
////                                } else {
//////                                    Log.d(TAG, "Он выключен: ");
////                                }
////                            } else {
//////                                Log.d(TAG, "Следующий элемент не совпал");
////                            }
                        }
                        i++;
                    } else break;
                }
            }
        }
//        canShowElement = positiveCounter > 0;
//
//        return canShowElement;
        return false;
    }
}
