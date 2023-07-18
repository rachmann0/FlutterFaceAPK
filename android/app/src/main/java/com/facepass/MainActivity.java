package com.facepass;

import io.flutter.embedding.android.FlutterActivity;

import android.content.Context;
import android.util.Log;
import java.util.Map;
import java.util.HashMap;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassConfig;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;
import mcv.facepass.types.FacePassGroupSyncDetail;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageRotation;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassModel;
import mcv.facepass.types.FacePassPose;
import mcv.facepass.types.FacePassRCAttribute;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassAgeGenderResult;
import mcv.facepass.types.FacePassRecognitionState;
import mcv.facepass.types.FacePassTrackOptions;

import com.facepass.method.InitializeSDK;
import com.facepass.method.Connect;
import com.facepass.configuration.Config;

import android.app.Activity;
import android.content.Context;

public class MainActivity extends FlutterActivity {
  FacePassHandler mFacePassHandler;

  Activity activity = getActivity();
  Context context = getContext();
  
  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    super.configureFlutterEngine(flutterEngine);
    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), Config.CHANNEL)
    .setMethodCallHandler((call, result) -> {

        /*  Notes: Channel Response Example
          - Get arguments
          Map<String, Object> args = call.arguments();
          System.out.println(args.get("message"));
          System.out.println((boolean) args.get("isActive") == false);
          System.out.println(args.get("isActive").getClass());

          - Sending response
          Map<String, Object> data = new HashMap<>();
          Map<String, Object> response = new HashMap<>();

          - Object structure
          response = {
            isSuccess: true / false
            data: {
              "a": any
              "b": any
            }
          }

          - Response data
          String firstName = "John";
          String lastName = "Doe";
          data.put("firstName", firstName);
          data.put("lastName", lastName);

          - Response status 
          boolean isSuccess = true;
          response.put("isSuccess", isSuccess);

          - All response
          response.put("data", data);

          System.out.println(response);

          result.success(response);
        */
        switch (call.method) {
          case "connect":
            result.success(Connect.call(call.arguments()));
            break;
          case "initializeSDK":
            result.success(InitializeSDK.call(activity));
            break;
          case "createGroup":
            result.success(true);
            break;
          case "addFace":
            result.success(true);
            break;
          case "bindGroupFaceToken":
            result.success(true);
            break;
          // --
          case "initGPIOManager":
            result.success(true);
            break;
          case "getRelayStatus":
            result.success(true);
            break;
          case "pullUpRelay":
            result.success(true);
            break;
          case "pullDownRelay":
            result.success(true);
            break;
          case "checkPluginAvailable":
            result.success(true);
            break;
          default:
            Log.e(Config.DEBUG_TAG, "unidentified channel");
            result.notImplemented();
        }
        
      }
    );
  }
}

