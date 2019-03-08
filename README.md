# base_mapview

A new Flutter MapView plugin.

## 高德地图插件

![image](https://github.com/android-shiyan/base_mapview/blob/master/image/device-2019-03-07-152243.png?raw=true)

## Getting Started

### 集成高德地图android版本

1、先申请一个apikey
http://lbs.amap.com/api/android-sdk/guide/create-project/get-key

2、在AndroidManifest.xml中增加
```
 <meta-data
            android:name="com.amap.api.v2.apikey"
            android:value="你的Key" />
```

3、增加对应的权限：

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_CONFIGURATION" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
```

## How to use

先导入dart包
修改pubspec.yaml，增加依赖：

```
dependencies:
  base_mapview: ^0.0.3
```


在要用的地方导入:

```
import 'package:base_mapview/base_mapview.dart';
```

然后就可以使用了

```
 import 'package:base_mapview/latlng.dart';
 import 'package:flutter/material.dart';
 import 'package:base_mapview/amap_view.dart';

 void main() => runApp(MyApp());

 class MyApp extends StatefulWidget {
   @override
   _MyAppState createState() => _MyAppState();
 }

 class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
   List _list = [
     LatLng(43.99791, 125.397968).toMap(),
     LatLng(39.833876, 116.301886).toMap(),
     LatLng(39.823862, 116.295385).toMap(),
     LatLng(39.823734, 116.296299).toMap(),
     LatLng(39.823083, 116.294776).toMap()
   ];

   List _polylinelist = [
     LatLng(39.82588, 116.30102).toMap(),
     LatLng(39.825847, 116.302554).toMap(),
     LatLng(39.823853, 116.300612).toMap(),
   ];

   List _polygonlist = [
     LatLng(39.823862, 116.295385).toMap(),
     LatLng(39.823734, 116.296299).toMap(),
     LatLng(39.823083, 116.294776).toMap(),
   ];

   @override
   Widget build(BuildContext context) {
     return MaterialApp(
       home: Scaffold(
           appBar: AppBar(
             title: const Text('MapView app'),
           ),
           body: Column(
             children: <Widget>[
               AMapView(
                 mapType: MapType.satellite,
                 centerCoordinate: LatLng(43.99791, 125.397968),
                 zoomLevel: 10,
               ),
               Wrap(
                 children: <Widget>[
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod("location");
                     },
                     child: Text(
                       "定位",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod(
                         "addmarker",
                         {
                           "mapView": {"markerlist": _list}
                         },
                       );
                     },
                     child: Text(
                       "添加marker",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod("removemarker");
                     },
                     child: Text(
                       "移除marker",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod(
                         "drawcircle",
                         {
                           "mapView": {
                             "roundcenter":
                                 LatLng(39.825262, 116.297241).toMap(),
                             "radius": 100.0,
                           }
                         },
                       );
                     },
                     child: Text(
                       "绘制圆形",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod(
                         "drawpolylin",
                         {
                           "mapView": {"polylinlist": _polylinelist}
                         },
                       );
                     },
                     child: Text(
                       "绘制线",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod(
                         "drawPolygon",
                         {
                           "mapView": {"polygonlist": _polygonlist}
                         },
                       );
                     },
                     child: Text(
                       "绘制多边形",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod(
                         "setMapType",
                         {
                           "mapView": {"mapType": 2}
                         },
                       );
                     },
                     child: Text(
                       "卫星图层",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod(
                         "setMapType",
                         {
                           "mapView": {"mapType": 1}
                         },
                       );
                     },
                     child: Text(
                       "正常图层",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod("zoomOut");
                     },
                     child: Text(
                       "放大地图",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                   RaisedButton(
                     onPressed: () {
                       AMapView.channel.invokeMethod("zoomIn");
                     },
                     child: Text(
                       "缩小地图",
                       style: TextStyle(color: Colors.red),
                     ),
                   ),
                 ],
               )
             ],
           )),
     );
   }
 }

```

## 特性

* [x] android支持
* [x] 不需要添加任何支持即可显示地图，无需Activity和Controller
* [x] 3D地图的显示
* [x] 地图的定位
* [x] 添加marker,移除marker
* [x] 绘制圆形，绘制线，绘制多边形
* [x] 设置地图缩放层级，放大地图，缩放地图
* [ ] 添加wms图层
* [ ] 获取POI数据
* [ ] 获取天气数据
* [ ] 更多api








