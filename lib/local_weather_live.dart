///本地实时天气
class LocalWeatherLive {
  final String adCode;

  final String city;

  final String humidity;

  final String province;

  final String reprotTime;

  final String temperature;

  final String weather;

  final String windDirection;

  final String windPower;

  LocalWeatherLive(
      this.adCode,
      this.city,
      this.humidity,
      this.province,
      this.reprotTime,
      this.temperature,
      this.weather,
      this.windDirection,
      this.windPower);

  static LocalWeatherLive fromMap(Map map) {
    if (map["temperature"] == "") {
      return LocalWeatherLive(map[""], map[""], map[""], map[""], map[""],
          map[""], map[""], map[""], map[""]);
    }
    return LocalWeatherLive(
        map["adCode"],
        map["city"],
        map["humidity"],
        map["province"],
        map["reprotTime"],
        map["temperature"],
        map["weather"],
        map["windDirection"],
        map["windPower"]);
  }

  Map toMap() {
    return {
      "adCode": this.adCode,
      "city": this.city,
      "humidity": this.humidity,
      "province": this.province,
      "reprotTime": this.reprotTime,
      "temperature": this.temperature,
      "weather": this.weather,
      "windDirection": this.windDirection,
      "windPower": this.windPower
    };
  }

  @override
  String toString() {
    return 'LocalWeatherLive{adCode: $adCode, city: $city, humidity: $humidity, province: $province, reprotTime: $reprotTime, temperature: $temperature, weather: $weather, windDirection: $windDirection, windPower: $windPower}';
  }
}
