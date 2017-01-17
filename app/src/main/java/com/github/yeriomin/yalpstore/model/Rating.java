package com.github.yeriomin.yalpstore.model;

public class Rating {

    private float average;
    private int oneStar;
    private int twoStars;
    private int threeStars;
    private int fourStars;
    private int fiveStars;

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public int getFiveStars() {
        return fiveStars;
    }

    public void setFiveStars(int fiveStars) {
        this.fiveStars = fiveStars;
    }

    public int getFourStars() {
        return fourStars;
    }

    public void setFourStars(int fourStars) {
        this.fourStars = fourStars;
    }

    public int getOneStar() {
        return oneStar;
    }

    public void setOneStar(int oneStar) {
        this.oneStar = oneStar;
    }

    public int getThreeStars() {
        return threeStars;
    }

    public void setThreeStars(int threeStars) {
        this.threeStars = threeStars;
    }

    public int getTwoStars() {
        return twoStars;
    }

    public void setTwoStars(int twoStars) {
        this.twoStars = twoStars;
    }
}
