// package com.example.sdk_test;

// import io.flutter.embedding.android.FlutterActivity;

// import android.content.Context;
// import android.util.Log;
// import android.widget.Toast;

// import androidx.annotation.NonNull;
// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.embedding.engine.FlutterEngine;
// import io.flutter.plugin.common.MethodChannel;

// import mcv.facepass.FacePassException;
// import mcv.facepass.FacePassHandler;
// import mcv.facepass.types.FacePassAddFaceResult;
// import mcv.facepass.types.FacePassConfig;
// import mcv.facepass.types.FacePassDetectionResult;
// import mcv.facepass.types.FacePassFace;
// import mcv.facepass.types.FacePassGroupSyncDetail;
// import mcv.facepass.types.FacePassImage;
// import mcv.facepass.types.FacePassImageRotation;
// import mcv.facepass.types.FacePassImageType;
// import mcv.facepass.types.FacePassModel;
// import mcv.facepass.types.FacePassPose;
// import mcv.facepass.types.FacePassRCAttribute;
// import mcv.facepass.types.FacePassRecognitionResult;
// import mcv.facepass.types.FacePassAgeGenderResult;
// import mcv.facepass.types.FacePassRecognitionState;
// import mcv.facepass.types.FacePassTrackOptions;

// public class MainActivity extends FlutterActivity {
//   FacePassHandler mFacePassHandler;

//   private static final String CHANNEL = "facepass";
//   private static final String DEBUG_TAG = "FACEPASS";

//   @Override
//   public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
//     super.configureFlutterEngine(flutterEngine);
//     new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
//       (call, result) -> {

//         switch(call.method) {
//           case "logtest":
//             result.success("[Example] this is log test from Java");
//             break;
//           default:
//             Log.e(DEBUG_TAG, "unidentified channel");
//             result.success(false);
//         }
//       }
//     );
//   }
// }
