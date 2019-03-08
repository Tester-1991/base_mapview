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
  base_mapview: ^0.0.2
```


在要用的地方导入:

```
import 'package:base_mapview/base_mapview.dart';
```

然后就可以使用了

```
 FlutterAmap amap = new FlutterAmap();

 void show(){
     amap.show(
         mapview: new AMapView(
             centerCoordinate: new LatLng(39.9242, 116.3979),
             zoomLevel: 13.0,
             mapType: MapType.night,
             showsUserLocation: true),
         title: new TitleOptions(title: "我的地图"));
     amap.onLocationUpdated.listen((Location location){

       print("Location changed $location") ;

     });
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








