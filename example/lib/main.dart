import 'package:base_mapview/latlng.dart';
import 'package:flutter/material.dart';
import 'package:base_mapview/amap_view.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
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
                      AMapView.channel.invokeMethod("addmarker");
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
                      AMapView.channel.invokeMethod("drawcircle");
                    },
                    child: Text(
                      "绘制圆形",
                      style: TextStyle(color: Colors.red),
                    ),
                  ),
                  RaisedButton(
                    onPressed: () {
                      AMapView.channel.invokeMethod("drawpolylin");
                    },
                    child: Text(
                      "绘制线",
                      style: TextStyle(color: Colors.red),
                    ),
                  ),
                  RaisedButton(
                    onPressed: () {
                      AMapView.channel.invokeMethod("drawPolygon");
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
