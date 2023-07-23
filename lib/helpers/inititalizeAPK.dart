import 'package:flutter/services.dart';

class InitializeAPK {
  static void call(String channelName) async {
    try {
      var channel = MethodChannel(channelName);
      // final response = Map<String, dynamic>.from(await channel.invokeMethod('initializeSDK', {"deviceId": 123}));
      final response = await channel.invokeMethod("initializeAPK");
      
      print("Initialize APK Success!");
      print(response);
    } on Exception catch (error) {
      print("Initialize APK Failed!");
      print(error);
    }
  }
}
