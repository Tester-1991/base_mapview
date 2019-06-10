package com.shiyan.flutter.basemapview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.view.FlutterView;

/**
 * Created by shiyan on 2019/3/6
 * <p>
 * desc:BaseMapView插件
 */
public class BaseMapviewPlugin implements MethodCallHandler {

    //当前Activity环境
    public static FlutterActivity root;

    //当前地图控件
    public static ASMapView mapView;

    //地图参数配置
    Map<String, Object> mapViewOptions;

    private String[] maniFests = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static MethodChannel channel;

    private static Map<String, ASMapView> map = new ConcurrentHashMap<>();

    private static String id;

    static PluginRegistry.Registrar registrar;

    /**
     * 创建view
     *
     * @param id
     * @return
     */
    private static ASMapView createView(String id) {

        ASMapView view = new ASMapView(root);

        return view;
    }

    /**
     * 获取view
     *
     * @param id
     * @return
     */
    private ASMapView getView(String id) {

        return map.get(id);

    }


    //构造函数
    public BaseMapviewPlugin(FlutterActivity activity) {
        this.root = activity;
        //处理生命周期
        this.root.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("plugin","onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.e("plugin","onActivityResumed");
                if (activity != root) return;
                if (mapView != null) {
                    mapView.onResume();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.e("plugin","onActivityDestroyed");
                if (activity != root) return;
                if (mapView != null) {
                    mapView.onPause();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("plugin","onActivityDestroyed");
                if (activity != root) return;
                if (mapView != null) {
                    mapView.onDestroy();
                }
            }
        });

        requestPermission();
    }

    /**
     * 获取权限(ACCESS_COARSE_LOCATION,WRITE_EXTERNAL_STORAGE)
     */
    private void requestPermission() {
        Acp.getInstance(root).request(new AcpOptions.Builder()
                        .setPermissions(maniFests)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                    }
                });
    }

    static boolean isFirst = true;

    /**
     * 插件注册
     *
     * @param registrar
     */
    public static void registerWith(Registrar registrar) {

        Log.e("plugin", "registerWith");

        channel = new MethodChannel(registrar.messenger(), "flutter_amap");

        channel.setMethodCallHandler(new BaseMapviewPlugin((FlutterActivity) registrar.activity()));

        mapView = createView(id);

        ViewRegistrant.registerWith(root, mapView);
    }

    /**
     * 方法回调
     *
     * @param call
     * @param result
     */
    @Override
    public void onMethodCall(MethodCall call, Result result) {
        //获取参数
        Map<String, Object> args = (Map<String, Object>) call.arguments;

        //获取地图参数配置信息
        if (args != null && args.containsKey("mapView")) {

            mapViewOptions = (Map<String, Object>) args.get("mapView");

            if (args.containsKey("id")) {

                id = (String) args.get("id");

            }
        }

        //处理多个mapview
        if (args != null && args.containsKey("id")) {
            id = (String) args.get("id");
            Log.e("plugin", "id");
            if (getView(id) != null) {
                mapView = getView(id);
            }
        }

        //显示地图
        if (call.method.equals("showMapView")) {

            showMapViewAction();

        }

        //移除
        if (call.method.equals("remove")) {

//            ViewGroup viewGroup = (ViewGroup) mapView.getParent();
//
//            viewGroup.removeView(mapView);

        }

        //定位
        else if (call.method.equals("location")) {
            Log.e("plugin", "location");
            locationAction();

        }

        //添加marker
        else if (call.method.equals("addmarker")) {

            addMarkerAction();

        }

        //添加查找设备marker
        else if (call.method.equals("add_find_device_marker")) {

            addFindDeviceMarkerAction(result);

        }

        //添加空域详情marker
        else if (call.method.equals("add_area_detail_marker")) {

            addAreaDetailMarkerAction();

        }

        //移除marker
        else if (call.method.equals("removemarker")) {

            removeMarkerAction();

        }

        //绘制圆形
        else if (call.method.equals("drawcircle")) {

            drawCircleAction();

        }

        //绘制线
        else if (call.method.equals("drawpolylin")) {

            drawPolylinAction();

        }

        //绘制飞行轨迹
        else if (call.method.equals("drawflypolylin")) {

            drawFlyPolylinAction();

        }

        //清除飞行轨迹
        else if (call.method.equals("clearflypolylin")) {
            clearFlyPolylinAction();
        }

        //缩放
        else if (call.method.equals("animateCamera")) {

            animateCameraAction();

        }

        //空域详情缩放
        else if (call.method.equals("areaAnimateCamera")) {

            areaAnimateCameraAction();
        }

        //绘制多边形
        else if (call.method.equals("drawPolygon")) {

            drawPolygonAction();

        }

        //切换地图图层
        else if (call.method.equals("setMapType")) {

            setMapTypeAction();

        }

        //放大地图级别
        else if (call.method.equals("zoomOut")) {

            zoomOutAction();

        }

        //缩小地图级别
        else if (call.method.equals("zoomIn")) {

            zoomInAction();

        }

        //输入内容自动提示
        else if (call.method.equals("queryInputeData")) {

            queryInputeDataAction();

        }

        //查询天气信息
        else if (call.method.equals("queryWeatherbyCity")) {

            queryWeatherbyCityAction();

        }

        //显示wms图层
        else if (call.method.equals("initWms")) {

            initWmsAction();

        }

        //定位到具体位置
        else if (call.method.equals("location_address")) {

            locationAddressAction();

        }

        //注册
        else if (call.method.equals("register")) {

            mapView = new ASMapView(root);

            if (args != null && args.containsKey("viewType")) {

                String viewType = (String) args.get("viewType");

                registrar.platformViewRegistry().registerViewFactory(viewType, new BMapViewFactory(new StandardMessageCodec(), mapView));
            }

        }

        //无消息
        else {

            result.notImplemented();

        }
    }


    /**
     * 定位
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void locationAction() {
        if (root.checkSelfPermission((Manifest.permission.ACCESS_COARSE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {

            mapView.animateCamera(mapView.getLatLng(), "17.6");

        } else {
            Acp.getInstance(root).request(new AcpOptions.Builder()
                            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                            .build(),
                    new AcpListener() {
                        @Override
                        public void onGranted() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mapView.animateCamera(mapView.getLatLng(), "17.6");
                                }
                            }, 2000);
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                        }
                    });
        }
    }

    /**
     * 显示地图
     */
    private void showMapViewAction() {
        if (registrar != null && !isFirst) {
            Log.e("plugin", "showMapViewAction:" + id);
        }
        root.runOnUiThread(() -> {

            mapView.setKey(id);

            map.put(id, mapView);

            mapView.onCreate(new Bundle());

            mapView.onResume();

            double widthPercent = (double) mapViewOptions.get("widthPercent");

            double heightPercent = (double) mapViewOptions.get("heightPercent");

            boolean showCenterIcon = (boolean) mapViewOptions.get("showCenterIcon");

            boolean openFirstLocation = (boolean) mapViewOptions.get("openFirstLocation");

            int mapWidth = (int) (Util.getScreenWidth(root) * widthPercent);

            int mapHeight = (int) (Util.getScreenHeight(root) * heightPercent);

            mapView.init(mapViewOptions, channel, mapWidth, mapHeight, this, showCenterIcon, openFirstLocation);

            isFirst = false;
            //root.addContentView(mapView, new FrameLayout.LayoutParams(mapWidth, mapHeight));
        });
    }

    /**
     * 移除key
     */
    private void removeKeyAction() {

        View view = map.get(id);

        if (view != null) {

            ViewGroup viewGroup = (ViewGroup) view.getParent();

            viewGroup.removeView(view);

        }
    }

    /**
     * 切换图层
     */
    private void setMapTypeAction() {

        int mapType = (int) mapViewOptions.get("mapType");

        mapView.setMapType(mapType);

    }

    /**
     * 扩大地图层级
     */
    private void zoomOutAction() {

        mapView.zoomOut();

    }

    /**
     * 缩小地图层级
     */
    private void zoomInAction() {

        mapView.zoomIn();

    }

    /**
     * 添加marker
     */
    private void addMarkerAction() {

        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("markerlist");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            latlngList.add(lantLng);
        }

        mapView.addMarker(latlngList);

    }

    /**
     * 添加查找设备marker
     *
     * @param result
     */
    private void addFindDeviceMarkerAction(Result result) {

        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("markerlist");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            latlngList.add(lantLng);
        }

        mapView.addFindDeviceMarker(latlngList, result);
    }

    /**
     * 添加空域详情marker
     */
    private void addAreaDetailMarkerAction() {
        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("markerlist");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            latlngList.add(lantLng);
        }

        mapView.addAreaDetailMarker(latlngList);
    }

    /**
     * 移除marker标记
     */
    private void removeMarkerAction() {

        mapView.removeMarker();

    }

    /**
     * 绘制圆形
     */
    private void drawCircleAction() {

        Map<String, Object> roundcenterMap = (Map<String, Object>) mapViewOptions.get("roundcenter");

        mapView.drawCircle(new LatLng((double) roundcenterMap.get("latitude"), (double) roundcenterMap.get("longitude")), (Double) mapViewOptions.get("radius"), (String) mapViewOptions.get("color"));

    }

    /**
     * 绘制线
     */
    private void drawPolylinAction() {

        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("polylinlist");

        String color = (String) mapViewOptions.get("color");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            latlngList.add(lantLng);
        }

        mapView.drawPolylin(latlngList, color);

    }

    /**
     * 绘制飞行轨迹
     */
    private void drawFlyPolylinAction() {

        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("polylinlist");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            lantLng = Util.getGdLatlngFormat(String.valueOf(lantLng.latitude), String.valueOf(lantLng.longitude), root);

            latlngList.add(lantLng);
        }

        mapView.drawFlyPolylin(latlngList);
    }

    /**
     * 清除飞行轨迹
     */
    private void clearFlyPolylinAction() {
        mapView.clearFlyPolylin();
    }

    /**
     * 缩放
     */
    private void animateCameraAction() {
        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("polylinlist");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            lantLng = Util.getGdLatlngFormat(String.valueOf(lantLng.latitude), String.valueOf(lantLng.longitude), root);

            latlngList.add(lantLng);
        }

        mapView.animateCamera(latlngList);
    }

    /**
     * 空域详情缩放
     */
    private void areaAnimateCameraAction() {

        boolean includeSelf = false;

        List<Map<String, Object>> list = null;

        String areaType = "";


        if (mapViewOptions.containsKey("list")) {
            list = (List) mapViewOptions.get("list");
        }

        if (mapViewOptions.containsKey("includeSelf")) {
            includeSelf = (boolean) mapViewOptions.get("includeSelf");
        }

        if (mapViewOptions.containsKey("areaType")) {
            areaType = (String) mapViewOptions.get("areaType");
        }

        double radius = (double) mapViewOptions.get("radius");

        boolean first = (boolean) mapViewOptions.get("first");

        Map<String, Object> roundcenterMap = (Map<String, Object>) mapViewOptions.get("roundcenter");

        double latitude = (double) roundcenterMap.get("latitude");

        double longitude = (double) roundcenterMap.get("longitude");

        List<LatLng> latlngList = new ArrayList<>();

        if (list != null) {
            for (Map<String, Object> map : list) {

                LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

                //lantLng = Util.getGdLatlngFormat(String.valueOf(lantLng.latitude), String.valueOf(lantLng.longitude), root);

                latlngList.add(lantLng);
            }
        }

        mapView.areaScaleCamera(latlngList, includeSelf, areaType, radius, latitude, longitude, first);
    }

    /**
     * 绘制多边形
     */
    private void drawPolygonAction() {

        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("polygonlist");

        String color = (String) mapViewOptions.get("color");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            latlngList.add(lantLng);
        }

        mapView.drawPolygon(latlngList, color);
    }

    /**
     * 输入内容自动提示
     */
    private void queryInputeDataAction() {
        //获取关键字
        String keyWord = (String) mapViewOptions.get("keyword");

        if (TextUtils.isEmpty(keyWord)) return;

        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        InputtipsQuery inputquery = new InputtipsQuery(keyWord, "");

        Inputtips inputTips = new Inputtips(root, inputquery);

        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int rCode) {
                if (rCode == 1000 && list != null && list.size() > 0) {
                    List<Map<String, Object>> dataList = new ArrayList<>();

                    for (Tip tip : list) {                 //挑出虚假数据
                        if (!TextUtils.isEmpty(tip.getPoiID()) && tip.getPoint() != null) {
                            Map<String, Object> map = new HashMap<>();
                            //获取Poi的ID
                            map.put("poiId", tip.getPoiID());

                            //获取经度
                            map.put("longitude", String.valueOf(tip.getPoint().getLongitude()));

                            //获取纬度
                            map.put("latitude", String.valueOf(tip.getPoint().getLatitude()));

                            //获取提示名称
                            map.put("name", tip.getName());

                            //获取提示区域
                            map.put("district", tip.getDistrict());

                            //获取提示区域编码
                            map.put("adcode", tip.getAdcode());

                            //获取详细地址
                            map.put("address", tip.getAddress());

                            //获取输入提示结果的类型编码
                            map.put("typeCode", tip.getTypeCode());

                            dataList.add(map);
                        }
                    }


                    if (dataList.size() <= 0) return;

                    Map<String, Object> dataMap = new HashMap<>();

                    dataMap.put("id", id);

                    dataMap.put("datalist", dataList);

                    channel.invokeMethod("getInputtips", dataMap);
                }
            }
        });

        inputTips.requestInputtipsAsyn();
    }

    /**
     * 获取城市列表的天气状况  这个方法应该在拖动地图后
     * 得到地图中间的坐标城市结果出来后调用
     */
    public void queryWeatherbyCityAction() {

        String city = (String) mapViewOptions.get("city");

        //检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
        WeatherSearchQuery mquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);

        WeatherSearch mweathersearch = new WeatherSearch(root);

        mweathersearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
                if (rCode == 1000) {

                    if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {

                        LocalWeatherLive weatherlive = weatherLiveResult.getLiveResult();

                        //行政区划代码
                        String adCode = weatherlive.getAdCode();

                        //城市名称
                        String city = weatherlive.getCity();

                        //空气湿度的百分比
                        String humidity = weatherlive.getHumidity();

                        //省份
                        String province = weatherlive.getProvince();

                        //实时数据发布时间
                        String reprotTime = weatherlive.getReportTime();

                        //温度
                        String temperature = weatherlive.getTemperature();

                        //天气
                        String weather = weatherlive.getWeather();

                        //风向
                        String windDirection = weatherlive.getWindDirection();

                        //风力
                        String windPower = weatherlive.getWindPower();

                        HashMap<String, Object> map = new HashMap<>();

                        map.put("adCode", adCode);

                        map.put("city", city);

                        map.put("humidity", humidity);

                        map.put("province", province);

                        map.put("reprotTime", reprotTime);

                        map.put("temperature", temperature);

                        map.put("weather", weather);

                        map.put("windDirection", windDirection);

                        map.put("windPower", windPower);

                        map.put("id", id);

                        channel.invokeMethod("weatherLiveSearched", map);

                    } else {

                        HashMap<String, Object> map = new HashMap<>();

                        map.put("id", id);

                        map.put("temperature", "");

                        map.put("weather", "");

                        channel.invokeMethod("weatherLiveSearched", map);
                    }

                } else {

                    HashMap<String, Object> map = new HashMap<>();

                    map.put("id", id);

                    map.put("temperature", "");

                    map.put("weather", "");

                    channel.invokeMethod("weatherLiveSearched", map);
                }
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

            }
        });

        mweathersearch.setQuery(mquery);

        //异步搜索
        mweathersearch.searchWeatherAsyn();
    }

    /**
     * 初始化wms图层
     */
    private void initWmsAction() {

        //机场净空区
        boolean airport = (boolean) mapViewOptions.get("airport");

        //禁飞区
        boolean jfq = (boolean) mapViewOptions.get("jfq");

        //限制区
        boolean xzq = (boolean) mapViewOptions.get("xzq");

        //危险区
        boolean wxq = (boolean) mapViewOptions.get("wxq");

        //固定飞场
        boolean gdfc = (boolean) mapViewOptions.get("gdfc");

        //临时任务区
        boolean lsrwq = (boolean) mapViewOptions.get("lsrwq");

        //临时禁飞区
        boolean lsjfq = (boolean) mapViewOptions.get("lsjfq");

        mapView.initWms(airport, jfq, xzq, wxq, gdfc, lsrwq, lsjfq);

    }

    /**
     * 定位到具体位置
     */
    public void locationAddressAction() {
        //获取经度
        String longitude = (String) mapViewOptions.get("longitude");
        Log.e("plugin","longitude:"+longitude);
        //获取纬度
        String latitude = (String) mapViewOptions.get("latitude");
        Log.e("plugin","latitude:"+latitude);

        String v = (String) mapViewOptions.get("v");

        int type = (int) mapViewOptions.get("type");

        Log.e("plugin","type:"+type);

        LatLng latLng = Util.getGdLatlngFormat(latitude, longitude, root);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (root.checkSelfPermission((Manifest.permission.ACCESS_COARSE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {

                switch (type) {
                    case 1:
                        mapView.animateCamera(latLng, v);
                        break;
                    case 2:
                        mapView.animateUpdateCamera(latLng, v);
                        break;
                }

            } else {
                Acp.getInstance(root).request(new AcpOptions.Builder()
                                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                                .build(),
                        new AcpListener() {
                            @Override
                            public void onGranted() {
                                switch (type) {
                                    case 1:
                                        mapView.animateCamera(latLng, v);
                                        break;
                                    case 2:
                                        mapView.animateUpdateCamera(latLng, v);
                                        break;
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                            }
                        });
            }
        }
    }
}
