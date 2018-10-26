package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

public class OptionsField {

    @SerializedName("polyanswer")
    private int polyanswer;

    public OptionsField() {
    }

    public int getPolyanswer() {
        return polyanswer;
    }
}
