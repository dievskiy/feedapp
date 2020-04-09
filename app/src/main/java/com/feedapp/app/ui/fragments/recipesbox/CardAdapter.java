package com.feedapp.app.ui.fragments.recipesbox;


import androidx.cardview.widget.CardView;

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 1;

    CardView getCardViewAt(int position);

    int getCount();
}