package com.shiyan.flutter.basemapview;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shiyan on 2019/4/2
 *
 * desc:日期转换
 */

public class DateUtils {

    /**
     * 得到指定日期yyyy-mm-dd HH:mm:ss格式的日期
     *
     * @return
     */
    public static String getyyyyMMDD() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

    public static String getHHmmssDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

}
