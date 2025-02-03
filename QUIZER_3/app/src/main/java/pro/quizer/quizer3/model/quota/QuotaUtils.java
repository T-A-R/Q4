package pro.quizer.quizer3.model.quota;

import java.util.List;

import pro.quizer.quizer3.database.models.ElementItemR;

public class QuotaUtils {

    public boolean canShow(ElementItemR[][] tree, List<Integer> passedElementsId, int relativeId, int order) {

        if (tree == null) {
            return true;
        }

        if (order == 1) {
            for (int k = 0; k < tree[0].length; k++) {
                if (tree[0][k].getRelative_id().equals(relativeId)) {
                    if (tree[0][k].isEnabled())
                        return true;
                }
            }
            return false;
        } else {
            int endPassedElement = order - 1;

            for (int k = 0; k < tree[0].length; k++) {
                for (int i = 0; i < endPassedElement; ) {
                    if (tree[i][k].getRelative_id().equals(passedElementsId.get(i))) {
                        if (i == (endPassedElement - 1)) { // Если последний, то
                            if (tree[i + 1][k].getRelative_id().equals(relativeId)) { // Если следующий за последним равен Relative ID
                                if (tree[i + 1][k].isEnabled()) {
                                    return true;
                                }
                            }
                        }
                        i++;
                    } else break;
                }
            }
        }
        return false;
    }
}
