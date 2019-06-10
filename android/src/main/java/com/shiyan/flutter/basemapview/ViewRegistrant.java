package com.shiyan.flutter.basemapview;


import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;

import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.StandardMessageCodec;

public final class ViewRegistrant {
    public static void registerWith(PluginRegistry registry, TextureMapView mapView) {
        final String key = ViewRegistrant.class.getCanonicalName();

        if (registry.hasPlugin(key)) return;

        PluginRegistry.Registrar registrar = registry.registrarFor(key);

        BaseMapviewPlugin.registrar = registrar;

        registrar.platformViewRegistry().registerViewFactory("MapView", new BMapViewFactory(new StandardMessageCodec(), mapView));
    }
}
