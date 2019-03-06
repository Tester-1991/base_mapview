package com.shiyan.flutter.basemapview;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.amap.api.maps.MapView;

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
            MapView mapView = new MapView(root);
            mapView.onCreate(new Bundle());
            mapView.onResume();
            root.addContentView(mapView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        } else {
            result.notImplemented();
        }
    }
}
