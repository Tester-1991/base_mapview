package com.shiyan.flutter.basemapview;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by shiyan on 2019/3/11
 * dsec:
 */
public class Util {

    public static int getScreenWidth(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        return metrics.heightPixels;
    }
}
