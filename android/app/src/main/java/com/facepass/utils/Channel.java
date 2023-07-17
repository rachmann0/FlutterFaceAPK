package com.facepass.utils;

import android.util.Log;
import java.util.Map;
import java.util.HashMap;

public class Channel {
  private static final String DEBUG_TAG = "java";
  
  public static Map<String, Object> connect(HashMap _arguments) {
    // Initialize SDK, etc..
    int deviceId = (int)_arguments.get("deviceId");

    Map<String, Object> response = new HashMap<>();
    response.put("isSuccess", true);
    response.put("message", "OK");
    response.put("deviceId", deviceId);

    Log.d(DEBUG_TAG, "Device connect with id " + deviceId);
    return response;
  }
}
