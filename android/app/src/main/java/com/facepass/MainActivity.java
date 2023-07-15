package com.facepass;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import mcv.facepass.FacePassHandler;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
  FacePassHandler mFacePassHandler;
  
  private static final String CHANNEL = "facepass";

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    super.configureFlutterEngine(flutterEngine);
    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
    .setMethodCallHandler(
      (call, result) -> {
        //if ("logtest" == call.method) { // compare reference not value
        if ("logtest".equals(call.method)) {
          Toast.makeText(this, "logtest", Toast.LENGTH_LONG).show();
          Log.d(DEBUG_TAG, "onCreate");
          result.success("success");
        }
        // This method is invoked on the main thread.
        // TODO
      }
    );
  }
  private static final String DEBUG_TAG = "DEBUGTAG";
}
