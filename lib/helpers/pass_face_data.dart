import 'package:camera/camera.dart';
import 'package:flutter/services.dart';

class PassFaceData {
  static void call(String channelName, String data) async {
    try {
      var channel = MethodChannel(channelName);
      // final response = Map<String, dynamic>.from(await channel.invokeMethod('initializeSDK', {"deviceId": 123}));
      final response = await channel.invokeMethod("passFaceData", data);

      print("Passing data success!");
      print(response);
    } on Exception catch (error) {
      print("passing data failed!");
      print(error);
    }
  }
}
