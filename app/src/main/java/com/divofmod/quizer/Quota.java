package com.divofmod.quizer;

class Quota {

    private String mSequence;
    private String mQuantity1;
    private String mQuantity2;

    public Quota(String sequence, String quantity1, String quantity2) {
        mSequence = sequence;
        mQuantity1 = quantity1;
        mQuantity2 = quantity2;
    }

    public String getSequence() {
        return mSequence;
    }

    public String getQuantity1() {
        return mQuantity1;
    }

    public String getQuantity2() {
        return mQuantity2;
    }

}
