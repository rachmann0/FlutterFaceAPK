import 'package:flutter/services.dart';

class BindGroupFaceToken {
  static Future<void> call(
      String channelName, String groupName, String faceToken) async {
    try {
      var channel = MethodChannel(channelName);

      await channel.invokeMethod("bindGroupFaceToken",
          {"groupName": groupName, "faceToken": faceToken});
      print("Bind Group Face Token Success!");
    } on Exception catch (error) {
      print("Bind Group Face Token Failed!");
      print(error);
    }
  }
}
