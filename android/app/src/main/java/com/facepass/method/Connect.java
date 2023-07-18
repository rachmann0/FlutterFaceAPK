package com.facepass.method;

import android.util.Log;
import java.util.Map;
import java.util.HashMap;

public class Connect {
  private static final String DEBUG_TAG = "java";
  
  public static Map<String, Object> call(HashMap _arguments) {
    int deviceId = (int)_arguments.get("deviceId");

    Map<String, Object> response = new HashMap<>();
    response.put("isSuccess", true);
    response.put("message", "OK");
    response.put("deviceId", deviceId);

    Log.d(DEBUG_TAG, "Device connect with id " + deviceId);
    return response;
  }
}