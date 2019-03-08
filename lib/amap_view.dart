import 'package:base_mapview/latlng.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef void LocationChange(LatLng latlng);

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
    switch (method) {
      case "locationUpdate":
        {
          Map args = call.arguments;
          String id = args["id"];
          print("$id $args");
          GlobalKey key = map[id];
          if (key != null) {
            AMapView view = key.currentWidget;
            view?.onLocationChange(LatLng.fromMap(args));
          }
          return new Future.value("");
        }
    }
    return new Future.value("");
  }

  AMapView({
    this.mapType: MapType.standard,
    this.centerCoordinate,
    this.zoomLevel: 10,
    this.onLocationChange,
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
