package com.shiyan.flutter.basemapview;

import android.content.Context;
import android.util.DisplayMetrics;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;

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

    /**
     * 通过GPS的字符串 获得高德地图的latlng
     *
     * @param lat
     * @param lng
     * @return
     */
    public static LatLng getGdLatlngFormat(String lat, String lng, Context context) {
        LatLng latLng = new LatLng(Double.parseDouble(lat)
                , Double.parseDouble(lng));
        CoordinateConverter converter = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(latLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    public static String changeLatlngFormat(double num) {
        int degree = (int) num;
        int min = (int) ((num - degree) * 60);
        int sec = (int) ((num - degree) * 3600 - min * 60);
        return degree + "°" + min + "′" + sec + "″";
    }

    public static String getFormatLatLng(double latitude, double longitude) {
        String lat = changeLatlngFormat(latitude);

        String lng = changeLatlngFormat(longitude);

        return "N " + lat + "\nE " + lng;
    }
}
