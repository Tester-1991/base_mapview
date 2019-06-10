package com.shiyan.flutter.basemapview;

import android.content.Context;
import android.view.View;


import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;

import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class BMapViewFactory extends PlatformViewFactory {
    private TextureMapView mapView;

    public BMapViewFactory(MessageCodec<Object> createArgsCodec, TextureMapView mapView) {
        super(createArgsCodec);
        this.mapView = mapView;
    }

    @Override
    public PlatformView create(final Context context, int i, Object o) {
        return new PlatformView() {
            @Override
            public View getView() {
                return mapView;
            }

            @Override
            public void dispose() {

            }
        };
    }
}
