import 'package:base_mapview/local_weather_live.dart';
import 'package:base_mapview/regeocode_address.dart';
import 'package:base_mapview/tip.dart';
import 'package:base_mapview/lat_lng.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

///定位回调接口
typedef void LocationChange(LatLng latlng);

///搜索提示回调接口
typedef void OnGetInputtips(List<Tip> list);

///地图中心点经纬度回调接口
typedef void OnCameraChange(LatLng latlng);

///逆地理编码
typedef void OnRegeocodeSearched(RegeocodeAddress address);

///实时天气
typedef void OnWeatherLiveSearched(LocalWeatherLive weather);

///marker点击事件
typedef void OnMarkerClick(LatLng latlng);

///地图类型
enum MapType { standard, satellite, night, nav, bus }

///地图显示控件
class AMapView extends StatefulWidget {
  ///flutter和android通信句柄
  static MethodChannel channel = const MethodChannel("flutter_amap");

  ///地图模式
  final MapType mapType;

  ///中心点
  final LatLng centerCoordinate;

  ///当前缩放级别，取值范围 [3,19] 默认是10
  final double zoomLevel;

  ///定位回调
  final LocationChange onLocationChange;

  ///搜索提示回调
  final OnGetInputtips onGetInputtips;

  ///地图发生变化
  final OnCameraChange onCameraChange;

  ///逆地理编码
  final OnRegeocodeSearched onRegeocodeSearched;

  ///实时天气查询
  final OnWeatherLiveSearched onWeatherLiveSearched;

  ///marker点击事件
  final OnMarkerClick onMarkerClick;

  static Map<String, GlobalKey> map = {};

  static int counter = 0;

  static GlobalKey createKey(GlobalKey orgKey) {
    if (counter == 0) {
      channel.setMethodCallHandler(_handleMethod);
    }
    if (orgKey != null) {
      remove(orgKey);
    }
    GlobalKey key = new GlobalKey(debugLabel: "${++counter}");
    map[key.toString()] = key;
    return key;
  }

  static void remove(GlobalKey key) {
    AMapView.channel.invokeMethod('remove', {"id": key.toString()});
  }

  static Future<dynamic> _handleMethod(MethodCall call) async {
    String method = call.method;

    AMapView view;

    Map args = call.arguments;
    String id = args["id"];
    print("$id $args");
    GlobalKey key = map[id];
    if (key != null) {
      view = key.currentWidget;
    }

    switch (method) {
      case "locationUpdate":
        view?.onLocationChange(LatLng.fromMap(args));
        return new Future.value("");
      case "getInputtips":
        List tipsList = args["datalist"];

        List<Tip> dataList = List();

        for (int i = 0; i < tipsList.length; i++) {
          Map map = tipsList[i];

          Tip tip = Tip.fromMap(map);

          dataList.add(tip);
        }

        view?.onGetInputtips(dataList);

        return new Future.value("");
      case "cameraChange":
        view?.onCameraChange(LatLng.fromMap(args));
        return new Future.value("");
      case "regeocodeSearched":
        view?.onRegeocodeSearched(RegeocodeAddress.fromMap(args));
        return new Future.value("");
      case "weatherLiveSearched":
        view?.onWeatherLiveSearched(LocalWeatherLive.fromMap(args));
        return new Future.value("");
      case "markerClick":
        view?.onMarkerClick(LatLng.fromMap(args));
        return new Future.value("");
    }
    return new Future.value("");
  }

  AMapView({
    this.mapType: MapType.standard,
    this.centerCoordinate,
    this.zoomLevel: 10,
    this.onLocationChange,
    this.onGetInputtips,
    this.onCameraChange,
    this.onRegeocodeSearched,
    this.onWeatherLiveSearched,
    this.onMarkerClick,
    Key key,
  }) : super(key: key);

  Map toMap() {
    return {
      "mapType": this.mapType.index,
      "centerCoordinate":
          this.centerCoordinate != null ? this.centerCoordinate.toMap() : null,
      "zoomLevel": this.zoomLevel,
    };
  }

  @override
  State<StatefulWidget> createState() {
    return _AMapViewState();
  }
}

///地图显示控件状态管理
class _AMapViewState extends State<AMapView> {
  @override
  void initState() {
    ///加载地图
    AMapView.channel.invokeMethod("showMapView",
        {"mapView": widget.toMap(), "id": widget.key.toString()});

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      child: Container(),
      height: 250.0,
    );
  }
}
