import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

///地图类型
enum MapType { standard, satellite, night, nav, bus }

///地图显示控件
class AMapView extends StatefulWidget {
  ///flutter和android通信句柄
  static MethodChannel channel = const MethodChannel("flutter_amap");

  final MapType mapType;

  AMapView({this.mapType = MapType.standard});

  Map toMap() {
    return {
      "mapType": this.mapType.index,
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
      child: Container(
      ),
      height: 250.0,
    );
  }
}
