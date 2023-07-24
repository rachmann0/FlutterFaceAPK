import 'package:flutter/services.dart';

class Sample {
  static void onChannelConnect() async {
    try {
      var channel = const MethodChannel("com.facepass/channel");

      // final response = Map<String, dynamic>.from(
      //     await channel.invokeMethod('methodName', {"deviceId": 123}));
      final response = await channel.invokeMethod("methodName");
      print("init SDK");
      print(response);
    } catch (error) {
      print("error SDK");
      print(error);
    }
  }
}
