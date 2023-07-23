import 'dart:io';

import 'package:flutter/services.dart';

class AddFace {
  static void call(String channelName) async {
    try {
      var channel = MethodChannel(channelName);
      // /Users/lukydwisaputra/Documents/Work/Werkdone/Projects/FacePassAPK/FlutterFaceAPK/assets/base64.txt

      // final response = Map<String, dynamic>.from(await channel.invokeMethod('initializeSDK', {"deviceId": 123}));
      String data = await getData();
      await channel.invokeMethod("addFace", {"data": data});

      // print("Add Face Success!");
      // print(response);
    } on Exception catch (error) {
      print("Add Face Failed!");
      print(error);
    }
  }

  static Future<String> getData() async {
    return await rootBundle.loadString('assets/base64.txt');
  }
}
