package com.ft.mapp.home.models;

import android.graphics.drawable.Drawable;

public class FakeIconModel {
    private boolean chosen;
    private int imgRes;
    private Drawable imgDrawable;

    public FakeIconModel(boolean chosen, int imgRes) {
        this.chosen = chosen;
        this.imgRes = imgRes;
    }

    public FakeIconModel(int imgRes) {
        this.imgRes = imgRes;
    }

    public Drawable getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(Drawable imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }
}
