package com.shiyan.flutter.basemapview;

import android.util.Log;

import com.amap.api.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by shiyan on 2019/3/12
 * <p>
 * desc: wms图层
 */
public class HeritageScopeTileProvider extends UrlTileProvider {

    private String mRootUrl;

    //默认瓦片大小
    private static int titleSize = 256;//a=6378137±2（m）

    //基本参数
    private final double initialResolution = 156543.03392804062;//2*Math.PI*6378137/titleSize;

    private final double originShift = 20037508.342789244;//2*Math.PI*6378137/2.0; 周长的一半

    private final double HALF_PI = Math.PI / 2.0;

    private final double RAD_PER_DEGREE = Math.PI / 180.0;

    private final double HALF_RAD_PER_DEGREE = Math.PI / 360.0;

    private final double METER_PER_DEGREE = originShift / 180.0;//一度多少米

    private final double DEGREE_PER_METER = 180.0 / originShift;//一米多少度

    public static final int AIRPORT = 0;

    public static final int JFQ = 3;

    public static final int GDFC = 1;

    public static final int LSRWQ = 2;

    public static final int XZQ = 5;

    public static final int WXQ = 6;

    public static final int LSJFQ = 4;

    //public static final String wmsBaseUrl = "http://map-api.airspace.cn/";

    public static final String wmsBaseUrl = "http://map-api-test.airspace.cn/";

    public HeritageScopeTileProvider(int i) {
        super(titleSize, titleSize);

        //临时任务区
        String lsrwqUrl = "geoserver/airspace/wms?layers=airspace:airspace&FORMAT=image/png&TRANSPARENT=TRUE&SERVICE=WMS&version=1.1.0&request=GetMap&STYLES=&SRS=EPSG:3857&tiled=true&cql_filter=type='TEMPORARY' AND used = true AND '%s' <= end_time AND '%s' >= start_time&BBOX=";

        String dateWithSecond = DateUtils.getyyyyMMDD();

        String date = DateUtils.getHHmmssDate();

        String otherUrl = "geoserver/airspace/wms?layers=airspace:airspace&FORMAT=image/png&TRANSPARENT=TRUE&SERVICE=WMS&version=1.1.0&request=GetMap&STYLES=&SRS=EPSG:3857&tiled=true&cql_filter=type='%s' AND used=true&BBOX=";

        String dangerUrl = "geoserver/airspace/wms?layers=airspace:airspace&FORMAT=image/png&TRANSPARENT=TRUE&SERVICE=WMS&version=1.1.0&request=GetMap&STYLES=&SRS=EPSG:3857&tiled=true&cql_filter=type='DANGER_AREA' AND used=true AND ((morrow = false AND '%s' >= start_time_of_day AND '%s' <= end_time_of_day ) OR ( morrow = true AND '%s' >= start_time_of_day ) OR ( morrow = true AND '%s' <= end_time_of_day))&BBOX=";

        String lsjfqUrl = "geoserver/airspace/wms?layers=airspace:airspace&FORMAT=image/png&TRANSPARENT=TRUE&SERVICE=WMS&version=1.1.0&request=GetMap&STYLES=&SRS=EPSG:3857&tiled=true&cql_filter=type='TEMPORARY_NO_FLY' AND used=true AND '%s' <= end_time AND '%s' >= start_time AND ((morrow = false AND '%s' >= start_time_of_day AND '%s' <= end_time_of_day ) OR ( morrow = true AND '%s' >= start_time_of_day ) OR ( morrow = true AND '%s' <= end_time_of_day))&BBOX=";

        //地址写你自己的wms地址
        if (i == 0)      //机场
            mRootUrl = wmsBaseUrl + String.format(otherUrl, "AIRPORT");
        else if (i == 3) //禁飞区
            mRootUrl = wmsBaseUrl + String.format(otherUrl, "NO_FLY");
        else if (i == 5)   //限制区
            mRootUrl = wmsBaseUrl + String.format(otherUrl, "RESTRICTED_AREA");
        else if (i == 6)  //危险区
            mRootUrl = wmsBaseUrl + String.format(dangerUrl, date, date, date, date);
        else if (i == 1) //固定飞场
            mRootUrl = wmsBaseUrl + String.format(otherUrl, "LONGTERM");
        else if (i == 2) //临时任务区
            mRootUrl = wmsBaseUrl + String.format(lsrwqUrl, dateWithSecond, dateWithSecond);
        else if (i == 4)  //临时禁飞区
            mRootUrl = wmsBaseUrl + String.format(lsjfqUrl, dateWithSecond, dateWithSecond, date, date,date,date);
        mRootUrl = mRootUrl.replaceAll(" ", "%20").replaceAll("'", "%27");

    }

    @Override
    public URL getTileUrl(int x, int y, int level) {

        try {
            String url = mRootUrl + TitleBounds(x, y, level);
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 根据像素、等级算出坐标
     *
     * @param p
     * @param zoom
     * @return
     */
    private double Pixels2Meters(int p, int zoom) {
        return p * Resolution(zoom) - originShift;
    }

    /**
     * 根据瓦片的x/y等级返回瓦片范围
     *
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    private String TitleBounds(int tx, int ty, int zoom) {
        double minX = Pixels2Meters(tx * titleSize, zoom);
        double maxY = -Pixels2Meters(ty * titleSize, zoom);
        double maxX = Pixels2Meters((tx + 1) * titleSize, zoom);
        double minY = -Pixels2Meters((ty + 1) * titleSize, zoom);

        //转换成经纬度
        minX = Meters2Lon(minX);
        minY = Meters2Lat(minY);
        maxX = Meters2Lon(maxX);
        maxY = Meters2Lat(maxY);

        minX = Lon2Meter(minX);
        minY = Lat2Meter(minY);
        maxX = Lon2Meter(maxX);
        maxY = Lat2Meter(maxY);

        return minX + "," + Double.toString(minY) + "," + Double.toString(maxX) + "," + Double.toString(maxY) + "&WIDTH=256&HEIGHT=256";
    }

    /**
     * 计算分辨率
     *
     * @param zoom
     * @return
     */
    private double Resolution(int zoom) {
        return initialResolution / (Math.pow(2, zoom));
    }

    /**
     * X米转经纬度
     */
    private double Meters2Lon(double mx) {
        double lon = mx * DEGREE_PER_METER;
        return lon;
    }

    /**
     * Y米转经纬度
     */
    private double Meters2Lat(double my) {
        double lat = my * DEGREE_PER_METER;
        lat = 180.0 / Math.PI * (2 * Math.atan(Math.exp(lat * RAD_PER_DEGREE)) - HALF_PI);
        return lat;
    }

    /**
     * X经纬度转米
     */
    private double Lon2Meter(double lon) {
        double mx = lon * METER_PER_DEGREE;
        return mx;
    }

    /**
     * Y经纬度转米
     */
    private double Lat2Meter(double lat) {
        double my = Math.log(Math.tan((90 + lat) * HALF_RAD_PER_DEGREE)) / (RAD_PER_DEGREE);
        my = my * METER_PER_DEGREE;
        return my;
    }

}