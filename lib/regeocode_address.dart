class RegeocodeAddress {
  final String province;

  final String city;

  final String district;

  final String township;

  RegeocodeAddress(this.province, this.city, this.district, this.township);

  static RegeocodeAddress fromMap(Map map) {
    return RegeocodeAddress(
        map["province"], map["city"], map["district"], map["township"]);
  }

  Map toMap() {
    return {
      "province": this.province,
      "city": this.city,
      "district": this.district,
      "township": this.township
    };
  }

  @override
  String toString() {
    return 'RegeocodeAddress{province: $province, city: $city, district: $district, township: $township}';
  }
}
