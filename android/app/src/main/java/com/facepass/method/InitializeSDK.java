package com.facepass.method;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import mcv.facepass.FacePassHandler;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.facepass.camera.CameraPreviewData;

import com.facepass.method.HasPermission;
import com.facepass.configuration.Config;
import com.facepass.method.RequestPermission;
import com.facepass.method.RecognizeData;


public class InitializeSDK {
  FacePassHandler mFacePassHandler;

  public static boolean call(Activity activity) {
    // Log.d(Config.DEBUG_TAG, "initializeSDK: " + HasPermission.call());
    // mImageCache = new FaceImageCache();
    ArrayBlockingQueue<RecognizeData> mRecognizeDataQueue;
    ArrayBlockingQueue<CameraPreviewData> mFeedFrameQueue;
    mRecognizeDataQueue = new ArrayBlockingQueue<RecognizeData>(5);
    mFeedFrameQueue = new ArrayBlockingQueue<CameraPreviewData>(1);

    // initView();
    if (!HasPermission.call(activity)) {
      RequestPermission.call(activity);
      Log.d(Config.DEBUG_TAG, "PackageManager.PERMISSION_DENIED: " + PackageManager.PERMISSION_DENIED);
      Log.d(Config.DEBUG_TAG, "hasPermission(): " + HasPermission.call(activity));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_CAMERA): " + activity.checkSelfPermission(Config.PERMISSION_CAMERA));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_READ_STORAGE): " + activity.checkSelfPermission(Config.PERMISSION_READ_STORAGE));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_WRITE_STORAGE): " + activity.checkSelfPermission(Config.PERMISSION_WRITE_STORAGE));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_INTERNET): " + activity.checkSelfPermission(Config.PERMISSION_INTERNET));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE): " + activity.checkSelfPermission(Config.PERMISSION_ACCESS_NETWORK_STATE));
      return true;
    } else {
      Log.d(Config.DEBUG_TAG, "PackageManager.PERMISSION_DENIED: " + PackageManager.PERMISSION_DENIED);
      Log.d(Config.DEBUG_TAG, "hasPermission(): " + HasPermission.call(activity));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_CAMERA): " + activity.checkSelfPermission(Config.PERMISSION_CAMERA));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_READ_STORAGE): " + activity.checkSelfPermission(Config.PERMISSION_READ_STORAGE));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_WRITE_STORAGE): " + activity.checkSelfPermission(Config.PERMISSION_WRITE_STORAGE));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_INTERNET): " + activity.checkSelfPermission(Config.PERMISSION_INTERNET));
      Log.d(Config.DEBUG_TAG, "checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE): " + activity.checkSelfPermission(Config.PERMISSION_ACCESS_NETWORK_STATE));
      return HasPermission.call(activity);
      // try {
      //   // initFacePassSDK(callbackContext);
      // } catch (e) {
      //   e.printStackTrace();
      //   return false;
      // }
    }

    // // initFaceHandler(callbackContext);
    // initFaceHandler(callbackContext);
    // //callbackContext.error("Expected one non-empty string argument.");

    // recognizeThreadCallbackContext = callbackContext;

    // mFeedFrameThread = new FeedFrameThread();
    // mFeedFrameThread.start();

    // mRecognizeThread = new RecognizeThread();
    // mRecognizeThread.start();
  }
}
