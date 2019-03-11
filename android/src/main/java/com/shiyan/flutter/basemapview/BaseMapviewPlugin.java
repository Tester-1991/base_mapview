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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
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
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * Created by shiyan on 2019/3/6
 * <p>
 * desc:BaseMapView插件
 */
public class BaseMapviewPlugin implements MethodCallHandler {

    //当前Activity环境
    private FlutterActivity root;

    //当前地图控件
    private ASMapView mapView;

    //地图参数配置
    Map<String, Object> mapViewOptions;

    private String[] maniFests = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static MethodChannel channel;

    private Map<String, ASMapView> map = new ConcurrentHashMap<>();

    private String id;

    /**
     * 创建view
     *
     * @param id
     * @return
     */
    private ASMapView createView(String id) {

        ASMapView view = new ASMapView(root);

        view.setKey(id);

        map.put(id, view);

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
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity != root) return;
                if (mapView != null) {
                    mapView.onResume();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
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

    /**
     * 插件注册
     *
     * @param registrar
     */
    public static void registerWith(Registrar registrar) {

        channel = new MethodChannel(registrar.messenger(), "flutter_amap");

        channel.setMethodCallHandler(new BaseMapviewPlugin((FlutterActivity) registrar.activity()));

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

        //显示地图
        if (call.method.equals("showMapView")) {

            showMapViewAction();

        }

        //移除
        if (call.method.equals("remove")) {

            removeKeyAction();

        }

        //定位
        else if (call.method.equals("location")) {

            locationAction();

        }

        //添加marker
        else if (call.method.equals("addmarker")) {

            addMarkerAction();

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

        //无消息
        else {

            result.notImplemented();

        }
    }

    /**
     * 定位
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void locationAction() {
        if (root.checkSelfPermission((Manifest.permission.ACCESS_COARSE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {

            mapView.animateCamera(mapView.getLatLng());

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
                                    mapView.animateCamera(mapView.getLatLng());
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
        root.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView = createView(id);

                mapView.onCreate(new Bundle());

                mapView.onResume();

                mapView.init(mapViewOptions, channel);

                root.addContentView(mapView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1000));
            }
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

        mapView.drawCircle(new LatLng((double) roundcenterMap.get("latitude"), (double) roundcenterMap.get("longitude")), (Double) mapViewOptions.get("radius"));

    }

    /**
     * 绘制线
     */
    private void drawPolylinAction() {

        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("polylinlist");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            latlngList.add(lantLng);
        }

        mapView.drawPolylin(latlngList);

    }

    /**
     * 绘制多边形
     */
    private void drawPolygonAction() {

        List<LatLng> latlngList = new ArrayList<>();

        List<Map<String, Object>> list = (List) mapViewOptions.get("polygonlist");

        for (Map<String, Object> map : list) {

            LatLng lantLng = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));

            latlngList.add(lantLng);
        }

        mapView.drawPolygon(latlngList);
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

                    channel.invokeMethod("onGetInputtips", dataMap);
                }
            }
        });

        inputTips.requestInputtipsAsyn();
    }
}
