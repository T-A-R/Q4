package pro.quizer.quizer3.model.state;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotationBoxState implements Serializable {

    @SerializedName("rotations_states")
    private Map<Integer, List<Integer>> rotations_states;

    public RotationBoxState() {
        this.rotations_states =  new HashMap<Integer, List<Integer>>();
    }

    public Map<Integer, List<Integer>> getRotationsStates() {
        return rotations_states;
    }

    public void setRotationsStates(Map<Integer, List<Integer>> rotations_states) {
        this.rotations_states = rotations_states;
    }
}
