import 'package:flutter/services.dart';

class CreateGroup {
  static Future<void> call(String channelName, String groupName) async {
    try {
      var channel = MethodChannel(channelName);

      await channel.invokeMethod("createGroup", {"groupName": groupName});
      print("Create Face Success!");
    } on Exception catch (error) {
      print("Add Face Failed!");
      print(error);
    }
  }
}
