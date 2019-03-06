package com.shiyan.flutter.basemapview;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

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


    private FlutterActivity root;

    private ASMapView mapView;

    public BaseMapviewPlugin(FlutterActivity activity, MethodChannel channel) {
        this.root = activity;
    }

    /**
     * 插件注册
     *
     * @param registrar
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_amap");
        channel.setMethodCallHandler(new BaseMapviewPlugin((FlutterActivity) registrar.activity(), channel));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        //显示地图
        if (call.method.equals("showMapView")) {

            mapView = new ASMapView(root);

            mapView.onCreate(new Bundle());

            mapView.onResume();

            mapView.init();

            root.addContentView(mapView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1200));

        }
        //定位
        else if (call.method.equals("location")) {
            mapView.animateCamera(mapView.getLatLng());
        }
        //添加marker
        else if (call.method.equals("addmarker")) {

            List<LatLng> latlngList = new ArrayList<>();

            LatLng latLng = new LatLng(39.833876, 116.301886);

            latlngList.add(latLng);

            mapView.addMarker(latlngList);

        }
        //移除marker
        else if (call.method.equals("removemarker")) {

            mapView.removeMarker();

        }
        //绘制圆形
        else if (call.method.equals("drawcircle")) {
            //116.297241,39.825262
            mapView.drawCircle(new LatLng(39.825262, 116.297241), 100);
        }
        //绘制线
        else if (call.method.equals("drawpolylin")) {
            //116.30102,39.82588 116.302554,39.825847 116.300612,39.823853
            List<LatLng> latlngList = new ArrayList<>();

            LatLng latLng = new LatLng(39.82588, 116.30102);

            latlngList.add(latLng);

            LatLng latLng2 = new LatLng(39.825847, 116.302554);

            latlngList.add(latLng2);

            LatLng latLng3 = new LatLng(39.823853, 116.300612);

            latlngList.add(latLng3);

            mapView.drawPolylin(latlngList);
        }

        //绘制多边形
        else if (call.method.equals("drawPolygon")) {
            //116.295385,39.823862  116.296299,39.823734 116.294776,39.823083
            List<LatLng> latlngList = new ArrayList<>();

            LatLng latLng = new LatLng(39.823862, 116.295385);

            latlngList.add(latLng);

            LatLng latLng2 = new LatLng(39.823734, 116.296299);

            latlngList.add(latLng2);

            LatLng latLng3 = new LatLng(39.823083, 116.294776);

            latlngList.add(latLng3);

            mapView.addPointMarker(latlngList);

            mapView.drawPolygon(latlngList);
        } else {
            result.notImplemented();
        }
    }


}
