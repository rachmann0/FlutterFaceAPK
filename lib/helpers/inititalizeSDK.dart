import 'package:flutter/services.dart';

class InitializeSDK {
  static void call(String channelName) async {
    try {
      var channel = MethodChannel(channelName);
      // final response = Map<String, dynamic>.from(await channel.invokeMethod('initializeSDK', {"deviceId": 123}));
      final response = await channel.invokeMethod("initializeSDK");
      
      print("Initialize SDK Success!");
      print(response);
    } on Exception catch (error) {
      print("Initialize SDK Failed!");
      print(error);
    }
  }
}
