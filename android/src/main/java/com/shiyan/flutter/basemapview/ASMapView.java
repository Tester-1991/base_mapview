package com.shiyan.flutter.basemapview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TileOverlay;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

/**
 * Created by shiyan on 2019/3/6
 * dsec:
 */
public class ASMapView extends MapView {

    private UiSettings mUiSettings;

    private LatLng latLng;

    //飞机marker
    private ArrayList<Marker> markers = new ArrayList<>();

    //圆形
    private Circle circle;

    //线段
    private Polyline polyline;

    //多边形
    private Polygon polygon;

    //组件key
    private String key;

    //逆地理编码功能
    private GeocodeSearch geocodeSearch;

    //屏幕中间图标
    private Marker marker;

    //禁飞区图层
    private TileOverlay jfqOverlay;

    //危险区图层
    private TileOverlay wxqOverlay;

    //限制区图层
    private TileOverlay xzqOverlay;

    //机场图层
    private TileOverlay airportOverlay;

    //固定飞场图层
    private TileOverlay gdfcOverlay;

    //临时任务区图层
    private TileOverlay lsrwqOverlay;


    public ASMapView(Context context) {
        super(context);
    }

    public ASMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ASMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /**
     * 初始化方法
     */
    public void init(Map<String, Object> mapViewOptions, final MethodChannel methodChannel) {
        MyLocationStyle myLocationStyle = new MyLocationStyle();

        //连续定位、视角不移动到地图中心点
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);

        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(1000);

        //自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));

        //圆圈的颜色,设为透明的时候就可以去掉园区区域了
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));

        //设置定位蓝点的Style
        getMap().setMyLocationStyle(myLocationStyle);

        //启用定位蓝点
        getMap().setMyLocationEnabled(true);

        //实例化UiSettings类对象
        mUiSettings = getMap().getUiSettings();

        //去掉地图右下角放大缩小地图按钮
        mUiSettings.setZoomControlsEnabled(false);

        //将logo放到底部居中
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);

        //隐藏logo
        mUiSettings.setLogoBottomMargin(-100);

        //旋转手势不可用
        mUiSettings.setRotateGesturesEnabled(false);

        //控制比例尺控件是否显示
        mUiSettings.setScaleControlsEnabled(false);

        //显示默认的定位按钮
        mUiSettings.setMyLocationButtonEnabled(false);

        //定位监听
        getMap().setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //获取当前经度纬度
                latLng = new LatLng(location.getLatitude(), location.getLongitude());

                //回调通知
                Map<String, Object> map = new HashMap<>();
                //经度
                map.put("latitude", location.getLatitude());
                //纬度
                map.put("longitude", location.getLongitude());
                //id
                map.put("id", key);

                methodChannel.invokeMethod("locationUpdate", map);
            }
        });

        //监听地图拖动和缩放事件
        getMap().setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                //得到屏幕中心的经纬度
                LatLng target = cameraPosition.target;

                //回调通知
                Map<String, Object> map = new HashMap<>();
                //经度
                map.put("latitude", target.latitude);
                //纬度
                map.put("longitude", target.longitude);
                //id
                map.put("id", key);

                methodChannel.invokeMethod("cameraChange", map);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

                LatLonPoint latLonPoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);

                //第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);

                geocodeSearch.getFromLocationAsyn(query);
            }
        });

        if (mapViewOptions == null) return;

        //设置地图模式
        int mapType = (int) mapViewOptions.get("mapType");

        setMapType(mapType);

        //中心点设置
        Map<String, Object> coordinateMap = (Map<String, Object>) mapViewOptions.get("centerCoordinate");

        if (coordinateMap != null) {

            animateCamera(new LatLng((double) coordinateMap.get("latitude"), (double) coordinateMap.get("longitude")));

        }

        //设置地图缩放级别
        double zoomLevel = (double) mapViewOptions.get("zoomLevel");

        getMap().moveCamera(CameraUpdateFactory.zoomTo((float) zoomLevel));

        //初始化逆向地理编码功能  地图拖动通过坐标点得到地址信息得到这个坐标点
        geocodeSearch = new GeocodeSearch(getContext());

        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            /**
             * 逆地理编码(坐标转地址)
             */
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
                //逆地理编码结果回调
                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();

                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (TextUtils.isEmpty(regeocodeAddress.getProvince())) {

                        return;
                    }

                    //省
                    String province = regeocodeAddress.getProvince();

                    //市
                    String city = regeocodeAddress.getCity();

                    //县级
                    String district = regeocodeAddress.getDistrict();

                    //乡镇
                    String township = regeocodeAddress.getTownship();

                    HashMap<String, Object> map = new HashMap<>();

                    map.put("province", province);

                    map.put("city", city);

                    map.put("district", district);

                    map.put("township", township);

                    map.put("id", key);

                    methodChannel.invokeMethod("regeocodeSearched", map);

                } else {

                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        initScreenMarker();

        getMap().setOnMarkerClickListener(marker -> {

            LatLng position = marker.getPosition();

            //返回marker当前数据给flutter
            //回调通知
            Map<String, Object> map = new HashMap<>();
            //经度
            map.put("latitude", position.latitude);
            //纬度
            map.put("longitude", position.longitude);
            //id
            map.put("id", key);

            methodChannel.invokeMethod("markerClick", map);

            return true;
        });
    }

    /**
     * 获取key
     *
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置key
     *
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取当前经度和纬度
     *
     * @return
     */
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * 安卓o没办法适配刘海屏 过一秒获取地图的高度来配饰
     * 初始化 并添加屏幕中间的marker
     */
    @SuppressLint("CheckResult")
    public void initScreenMarker() {
        if (marker == null) {
            post(() -> {

                MarkerOptions markerOption = new MarkerOptions();

                //设置Marker不可拖动
                markerOption.draggable(false);

                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.dw)));

                marker = getMap().addMarker(markerOption);

                marker.setPositionByPixels(Util.getScreenWidth(getContext()) / 2
                        , getTop() + getHeight() / 2);

            });

        }
    }

    /**
     * 地图定位到中心点
     *
     * @param latlng
     */
    public void animateCamera(LatLng latlng) {

        if (getMap() == null) return;

        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17.6f));
    }

    /**
     * 添加marker
     *
     * @param latlngList
     */
    public void addMarker(List<LatLng> latlngList) {

        removeMarker();

        for (int i = 0; i < latlngList.size(); i++) {

            LatLng latLng = latlngList.get(i);

            MarkerOptions markerOptions = new MarkerOptions();

//            markerOptions.position(GdLatlngUtil.getGdLatlngFormat(getContext(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)));

            markerOptions.position(latLng);

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getContext().getResources(), R.mipmap.plane_marker)));

            Marker marker = getMap().addMarker(markerOptions);

            markers.add(marker);
        }
    }

    /**
     * 移除marker
     */
    public void removeMarker() {

        if (markers == null) {

            markers = new ArrayList<>();

        }

        for (int i = 0; i < markers.size(); i++) {

            markers.get(i).remove();

        }

        markers.clear();
    }

    /**
     * 初始化点的坐标
     */
    public void addPointMarker(List<LatLng> latlngList) {

        for (int i = 0; i < latlngList.size(); i++) {

            LatLng latLng = latlngList.get(i);

            MarkerOptions markerOptions = new MarkerOptions();

//            markerOptions.position(GdLatlngUtil.getGdLatlngFormat(getContext(), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)));

            markerOptions.position(latLng);

            markerOptions.anchor(0.5f, 0.5f);

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getContext().getResources(), R.mipmap.area_dot)));

            Marker marker = getMap().addMarker(markerOptions);

        }
    }

    /**
     * 绘制圆形
     *
     * @param latLng
     */
    public void drawCircle(LatLng latLng, double radius) {
        if (circle != null) {
            circle.remove();
            circle = null;
        }
        circle = getMap().addCircle(new CircleOptions().
                center(latLng).
                radius(radius).
                fillColor(Color.parseColor("#99F33019")).
                strokeColor(Color.parseColor("#99F33019")).
                strokeWidth(1).zIndex(2));
    }

    /**
     * 绘制线
     *
     * @param latLngList
     */
    public void drawPolylin(List<LatLng> latLngList) {
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        polyline = getMap().addPolyline(new PolylineOptions().
                addAll(latLngList).width(12).color(Color.parseColor("#99F33019")));
    }

    /**
     * 绘制多边形
     *
     * @param latLngList
     */
    public void drawPolygon(List<LatLng> latLngList) {

        PolygonOptions polygonOptions = new PolygonOptions();

        for (int i = 0; i < latLngList.size(); i++) {

            polygonOptions.add(latLngList.get(i));

        }

        if (polygon != null) {
            polygon.remove();
            polygon = null;
        }
        polygonOptions.fillColor(Color.parseColor("#99FEB41E")).
                strokeColor(Color.parseColor("#99FEB41E")).
                strokeWidth(1).zIndex(9999);
        polygon = getMap().addPolygon(polygonOptions);
    }

    /**
     * 设置地图图层
     *
     * @param mapType
     */
    public void setMapType(int mapType) {
        getMap().setMapType(mapType);

    }

    /**
     * 放大地图级别
     */
    public void zoomOut() {
        getMap().moveCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * 缩小地图级别
     */
    public void zoomIn() {
        getMap().moveCamera(CameraUpdateFactory.zoomIn());
    }


    /**
     * 显示禁飞区图层
     */
    public void initWms(boolean airport, boolean jfq, boolean xzq, boolean wxq, boolean gdfc, boolean lsrwq) {
        //机场净空区
        if (airport) {

            if (airportOverlay == null) {

                HeritageScopeTileProvider airportProvider = new HeritageScopeTileProvider(HeritageScopeTileProvider.AIRPORT);

                TileOverlayOptions airportOptions = new TileOverlayOptions().tileProvider(airportProvider);

                airportOptions
                        .diskCacheDir("/storage/amap/cache")
                        .diskCacheEnabled(true)
                        .diskCacheSize(100000)
                        .zIndex(-1);

                airportOverlay = getMap().addTileOverlay(airportOptions);
            }

        } else {

            if (airportOverlay != null) {

                airportOverlay.remove();

                airportOverlay = null;
            }

        }

        if (jfq) {

            if (jfqOverlay == null) {

                HeritageScopeTileProvider jfqProvider = new HeritageScopeTileProvider(HeritageScopeTileProvider.JFQ);

                TileOverlayOptions jfqOptions = new TileOverlayOptions().tileProvider(jfqProvider);

                jfqOptions
                        .diskCacheDir("/storage/amap/cache")
                        .diskCacheEnabled(true)
                        .diskCacheSize(100000);

                jfqOverlay = getMap().addTileOverlay(jfqOptions);

            }

        } else {

            if (jfqOverlay != null) {

                jfqOverlay.remove();

                jfqOverlay = null;

            }

        }

        if (wxq) {

            if (wxqOverlay == null) {

                HeritageScopeTileProvider wxqProvider = new HeritageScopeTileProvider(HeritageScopeTileProvider.WXQ);

                TileOverlayOptions wxqOptions = new TileOverlayOptions().tileProvider(wxqProvider);

                wxqOptions
                        .diskCacheDir("/storage/amap/cache")
                        .diskCacheEnabled(true)
                        .diskCacheSize(100000);

                wxqOverlay = getMap().addTileOverlay(wxqOptions);

            }

        } else {

            if (wxqOverlay != null) {

                wxqOverlay.remove();

                wxqOverlay = null;

            }

        }

        if (xzq) {

            if (xzqOverlay == null) {

                HeritageScopeTileProvider xzqProvider = new HeritageScopeTileProvider(HeritageScopeTileProvider.XZQ);

                TileOverlayOptions xzqOptions = new TileOverlayOptions().tileProvider(xzqProvider);

                xzqOptions
                        .diskCacheDir("/storage/amap/cache")
                        .diskCacheEnabled(true)
                        .diskCacheSize(100000);

                xzqOverlay = getMap().addTileOverlay(xzqOptions);

            }

        } else {

            if (xzqOverlay != null) {

                xzqOverlay.remove();

                xzqOverlay = null;

            }

        }

        //固定飞场
        if (gdfc) {

            if (gdfcOverlay == null) {

                HeritageScopeTileProvider gdfcProvider = new HeritageScopeTileProvider(HeritageScopeTileProvider.GDFC);

                TileOverlayOptions gdfcOptions = new TileOverlayOptions().tileProvider(gdfcProvider);

                gdfcOptions
                        .diskCacheDir("/storage/amap/cache")
                        .diskCacheEnabled(true)
                        .diskCacheSize(100000);

                gdfcOverlay = getMap().addTileOverlay(gdfcOptions);

            }

        } else {

            if (gdfcOverlay != null) {

                gdfcOverlay.remove();

                gdfcOverlay = null;

            }

        }

        //临时任务区
        if (lsrwq) {

            if (lsrwqOverlay == null) {

                HeritageScopeTileProvider lsrwqProvider = new HeritageScopeTileProvider(HeritageScopeTileProvider.LSRWQ);

                TileOverlayOptions lsrwqOptions = new TileOverlayOptions().tileProvider(lsrwqProvider);

                lsrwqOptions
                        .diskCacheDir("/storage/amap/cache")
                        .diskCacheEnabled(true)
                        .diskCacheSize(100000);

                lsrwqOverlay = getMap().addTileOverlay(lsrwqOptions);

            }

        } else {

            if (lsrwqOverlay != null) {

                lsrwqOverlay.remove();

                lsrwqOverlay = null;

            }

        }
    }

}
