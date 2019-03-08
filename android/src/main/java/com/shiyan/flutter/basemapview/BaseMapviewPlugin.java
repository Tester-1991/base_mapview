package com.shiyan.flutter.basemapview;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_amap");

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

        }

        //显示地图
        if (call.method.equals("showMapView")) {

            showMapViewAction();

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

        //无消息
        else {

            result.notImplemented();

        }
    }

    /**
     * 定位
     */
    private void locationAction() {
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

    /**
     * 显示地图
     */
    private void showMapViewAction() {
        root.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView = new ASMapView(root);

                mapView.onCreate(new Bundle());

                mapView.onResume();

                mapView.init(mapViewOptions);

                root.addContentView(mapView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1000));
            }
        });
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

}
