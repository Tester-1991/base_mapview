///搜索提示类
class Tip {
  final String poiId;

  final String longitude;

  final String latitude;

  final String name;

  final String district;

  final String adcode;

  final String address;

  final String typeCode;

  const Tip(this.poiId, this.longitude, this.latitude, this.name, this.district,
      this.adcode, this.address, this.typeCode);

  static Tip fromMap(Map map) {
    return Tip(map["poiId"], map["longitude"], map["latitude"], map["name"],
        map["district"], map["adcode"], map["address"], map["typeCode"]);
  }

  Map toMap() {
    return {
      "poiId": this.poiId,
      "longitude": this.longitude,
      "latitude": this.latitude,
      "name": this.name,
      "district": this.district,
      "adcode": this.adcode,
      "address": this.address,
      "typeCode": this.typeCode
    };
  }

  @override
  String toString() {
    return 'Tip{poiId: $poiId, longitude: $longitude, latitude: $latitude, name: $name, district: $district, adcode: $adcode, address: $address, typeCode: $typeCode}';
  }
}
