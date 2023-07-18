package com.facepass.method;

import android.content.pm.PackageManager;
import com.facepass.configuration.Config;
import android.os.Build;
import android.app.Activity;

public class HasPermission {
  public static boolean call(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return 
        activity.checkSelfPermission(Config.PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED && 
        activity.checkSelfPermission(Config.PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
        activity.checkSelfPermission(Config.PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        // getActivity().checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        // getActivity().checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
    } 
    
    return true;
  }
}
