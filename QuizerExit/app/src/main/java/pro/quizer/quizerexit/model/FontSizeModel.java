package pro.quizer.quizerexit.model;

public class FontSizeModel {

    private String mName;
    private float mScale;

    public FontSizeModel(String pName, float pScale) {
        this.mName = pName;
        this.mScale = pScale;
    }

    public String getName() {
        return mName;
    }

    public float getScale() {
        return mScale;
    }
}
