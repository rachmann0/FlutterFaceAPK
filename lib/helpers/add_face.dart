import 'package:flutter/services.dart';

class AddFace {
  static Future<String> call(String channelName) async {
    try {
      var channel = MethodChannel(channelName);
      // /Users/lukydwisaputra/Documents/Work/Werkdone/Projects/FacePassAPK/FlutterFaceAPK/assets/base64.txt

      String data = await getData();
      return await channel.invokeMethod("addFace", {"data": data});
    } on Exception catch (error) {
      print("Add Face Failed!");
      print(error);
      return "";
    }
  }

  static Future<String> getData() async {
    return await rootBundle.loadString('assets/base64.txt');
  }
}
