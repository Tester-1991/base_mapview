///wms配置类
class Wms {
  final bool airport;

  final bool jfq;

  final bool xzq;

  final bool wxq;

  final bool gdfc;

  final bool lsrwq;

  final bool lsjfq;

  Wms(this.airport, this.jfq, this.xzq, this.wxq, this.gdfc, this.lsrwq,
      this.lsjfq);

  static Wms fromMap(Map map) {
    return Wms(map["airport"], map["jfq"], map["xzq"], map["wxq"], map["gdfc"],
        map["lsrwq"], map["lsjfq"]);
  }

  Map toMap() {
    return {
      "airport": this.airport,
      "jfq": this.jfq,
      "xzq": this.xzq,
      "wxq": this.wxq,
      "gdfc": this.gdfc,
      "lsrwq": this.lsrwq,
      "lsjfq": this.lsjfq,
    };
  }

  @override
  String toString() {
    return 'Wms{airport: $airport, jfq: $jfq, xzq: $xzq, wxq: $wxq, gdfc: $gdfc, lsrwq: $lsrwq,lsjfq: $lsjfq}';
  }
}
