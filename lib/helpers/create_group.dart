import 'package:flutter/services.dart';

class CreateGroup {
  static Future<void> call(String channelName, String groupName) async {
    try {
      var channel = MethodChannel(channelName);
      var response = await channel.invokeMethod("createGroup", {"groupName": groupName});
      print(response);
      // print("Create Group Success!");
    } on Exception catch (error) {
      print("Add Face Failed!");
      print(error);
    }
  }
}
