package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;

import butterknife.ButterKnife;

public class ViewsUtils {

    public static final ButterKnife.Action<View> VISIBLE = (view, index) -> view.setVisibility(View.VISIBLE);

    public static final ButterKnife.Action<View> GONE = (view, index) -> view.setVisibility(View.GONE);

    public static final ButterKnife.Action<View> INVISIBLE = (view, index) -> view.setVisibility(View.INVISIBLE);


    public enum COLORS {
        WHITE, YELLOW, ORANGE, RED, GREEN, BLACK, GREY_LIGHTER
    }

    public static int getColor(Context context, COLORS color) {
        switch (color) {
            case WHITE:
                return ContextCompat.getColor(context, R.color.txt_white);
            case YELLOW:
                return ContextCompat.getColor(context, R.color.txt_yellow);
            case ORANGE:
                return ContextCompat.getColor(context, R.color.txt_orange);
            case RED:
                return ContextCompat.getColor(context, R.color.txt_red);
            case GREEN:
                return ContextCompat.getColor(context, R.color.txt_green);
            case BLACK:
                return ContextCompat.getColor(context, R.color.txt_dark);
            case GREY_LIGHTER:
                return ContextCompat.getColor(context, R.color.txt_grey_lighter);
            default:
                return ContextCompat.getColor(context, R.color.txt_dark);
        }
    }

    public static int getColor(Context context, int timeDifference) {
        int color = ContextCompat.getColor(context, R.color.txt_dark);
        if (timeDifference == 0) {
            color = ContextCompat.getColor(context, R.color.ontime);
        } else if (timeDifference > 0) {
            color = ContextCompat.getColor(context, R.color.late1);
        } else if (timeDifference < 0) {
            color = ContextCompat.getColor(context, R.color.early1);
        }
        return color;
    }

}
