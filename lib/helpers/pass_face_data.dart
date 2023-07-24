import 'package:flutter/services.dart';

class PassFaceData {
  static Future<void> call(String channelName, dynamic data, int width, int height) async {
    try {
      var channel = MethodChannel(channelName);
      // final response = Map<String, dynamic>.from(await channel.invokeMethod('initializeSDK', {"deviceId": 123}));
      await channel.invokeMethod("passFaceData", {
        "byteData": data, 
        "width": width,
        "height": height,
      });

      // print("Passing data success!");
      // print(response["byteData"]);
      // print(response["width"]);
      // print(response["height"]);
    } on Exception catch (error) {
      print("passing data failed!");
      print(error);
    }
  }
}
