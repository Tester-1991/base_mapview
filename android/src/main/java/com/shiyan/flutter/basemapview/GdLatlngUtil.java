package com.shiyan.flutter.basemapview;


import android.content.Context;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;

/**
 * 将gps坐标转换为高德的火星坐标系
 */
public class GdLatlngUtil {


    public static LatLng getGdLatlngFromGps(Context context,LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(latLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 通过GPS的字符串 获得高德地图的latlng
     *
     * @param lat
     * @param lng
     * @return
     */
    public static LatLng getGdLatlngFormat(Context context,String lat, String lng) {
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

}
