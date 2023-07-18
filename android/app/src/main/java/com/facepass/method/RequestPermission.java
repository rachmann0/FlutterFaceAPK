package com.facepass.method;

import android.os.Build;
import com.facepass.configuration.Config;
import android.app.Activity;

public class RequestPermission {
  public static void call(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activity.requestPermissions(Config.Permission, Config.PERMISSIONS_REQUEST);
    }
}
}
