package com.shiyan.flutter.basemapview;

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

            getMap().addMarker(markerOptions);

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
}
