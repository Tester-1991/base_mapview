import 'package:base_mapview/latlng.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

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

  AMapView({
    this.mapType: MapType.standard,
    this.centerCoordinate,
    this.zoomLevel: 10,
  });

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
    AMapView.channel.invokeMethod("showMapView", {"mapView": widget.toMap()});

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
