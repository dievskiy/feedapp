package com.feedapp.app.ui.fragments.recipesbox;


public class CardItem {

    private String mTextResource;
    private String  mTitleResource;

    public CardItem(String title, String text) {
        mTitleResource = title;
        mTextResource = text;
    }

    public String getText() {
        return mTextResource;
    }

    public String getTitle() {
        return mTitleResource;
    }
}
