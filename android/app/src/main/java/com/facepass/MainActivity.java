package com.facepass;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import android.content.Context;
import android.content.ContextWrapper;
import android.app.Activity;
import android.Manifest;
import android.util.Log;
import android.content.pm.PackageManager;
import android.os.Build;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.text.TextUtils;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.graphics.ImageFormat;
import android.util.Base64;
import android.view.WindowManager;
import android.content.res.Configuration;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Map;
import java.util.HashMap;

import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.auth.AuthApi.AuthApi;
import mcv.facepass.auth.AuthApi.AuthApplyResponse;
import mcv.facepass.auth.AuthApi.ErrorCodeConfig;
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

import com.facepass.camera.CameraManager;
import com.facepass.camera.CameraPreviewData;
import com.facepass.SettingVar;

public class MainActivity extends FlutterActivity {
  FacePassHandler mFacePassHandler;

  // CHANNEL
  public static final String CHANNEL = "com.facepass/channel";

  // TAG
  public static String DEBUG_TAG = "facepass-java";
  private static final String TAG = "MyActivity";

  // GROUP
  String group_name = "";
  private boolean isLocalGroupExist = false;


  // CAMERA
  // private CameraManager manager;
  CameraPreviewData cameraPreviewData;
  private static boolean cameraFacingFront = true;
  int screenState = 0;

  private enum FacePassCameraType{
    FACEPASS_SINGLECAM,
    FACEPASS_DUALCAM
  };
  private static FacePassCameraType CamType = FacePassCameraType.FACEPASS_SINGLECAM;

  protected boolean front = false;
  private int previewDegreen = 0;

  private int cameraRotation;
  private static ArrayBlockingQueue<Pair<CameraPreviewData, CameraPreviewData>> complexFrameQueue = new ArrayBlockingQueue<>(2);

  // FACEPASS SDK MODE
  private enum FacePassSDKMode {
    MODE_ONLINE,
    MODE_OFFLINE
  }
  private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;

  private boolean ageGenderEnabledGlobal;

  ArrayBlockingQueue<RecognizeData> mRecognizeDataQueue;
  ArrayBlockingQueue<CameraPreviewData> mFeedFrameQueue;

  RecognizeThread mRecognizeThread;
  FeedFrameThread mFeedFrameThread;

  // PERMISSION
  private static final int PERMISSIONS_REQUEST = 1;
  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
  private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
  private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
  private String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};

  // AUTHENTICATION
  public static final String CERT_PATH = "Cert/CBG_Android_Face_Reco---30-Trial-one-stage.cert";

  private static final String authIP = "https://api-cn.faceplusplus.com";
  public static final String apiKey = "";
  public static final String apiSecret = "";
  private enum FacePassAuthType{
    FACEPASS_AUTH_MCVFACE,
    FACEPASS_AUTH_MCVSAFE
  };
  private static FacePassAuthType authType = FacePassAuthType.FACEPASS_AUTH_MCVSAFE;

  // FACE PROCESSING
  private static byte[] faceToken;
  private static byte[] faceData;
  private static Bitmap faceBitmap;


  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    super.configureFlutterEngine(flutterEngine);
    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
      .setMethodCallHandler((call, result) -> {
        Map<String, Object> args = call.arguments();

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

          result.success(response);*/

        switch (call.method) {
          case "initializeAPK":
            result.success(initializeAPK());
            break;
          case "createGroup":
            createGroup((String) args.get("groupName"));
            result.success(true);
            break;
          case "addFace":
            // addFace(faceBM64);
            Log.d(DEBUG_TAG, "invoke add face");
            String faceImage = (String) args.get("data");
            result.success(addFace(faceImage));
            break;
          case "bindGroupFaceToken":
            bindGroupFaceToken((String) args.get("groupName"), (String) args.get("faceToken"));
            result.success(true);
            break;
          case "passFaceData":
            byte[] byteData = (byte[]) args.get("byteData");
            int width = (int) args.get("width");
            int height = (int) args.get("height");

            mFeedFrameQueue.offer(new CameraPreviewData(byteData, width, height, previewDegreen, front));
            result.success(args);
            // try calling feed frame and recognize without using threads
            break;
          default:
            Log.e(DEBUG_TAG, "unidentified channel");
            result.notImplemented();
        }
      }
    );
  }

  // --- INITIALIZE APK
  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return
        getActivity().checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
        // getActivity().checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        // getActivity().checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        getActivity().checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
        getActivity().checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
      }
    return true;
  }

  private boolean requestPermission() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getActivity().requestPermissions(Permission, PERMISSIONS_REQUEST);
        Log.d(DEBUG_TAG, "Request permission success.");
      }
      return true;
    } catch (Exception e) {
      Log.e(DEBUG_TAG, "Request permission failed.");
      return false;
    }
  }

  public class RecognizeData {
    public byte[] message;
    public FacePassTrackOptions[] trackOpt;

    public RecognizeData(byte[] message) {
      this.message = message;
      this.trackOpt = null;
    }

    public RecognizeData(byte[] message, FacePassTrackOptions[] opt) {
      this.message = message;
      this.trackOpt = opt;
    }
  }

  private void singleCertification() throws IOException {
    String cert = "\"{\"\"serial\"\":\"\"z0005a8759f61d5f4b2862852034c139ddada\"\",\"\"key\"\":\"\"2a6a6e824b1bfb87553faecb38faf4122936055c915fb3ac814c8879994d1542f304999e02ec2ff25d278b110695b76980b3002d57d2f4f20d779f2ffc95e1bac4ff713f244ad0d7da10a0491ee0fbfce6c9ee0f4a8fd42f0fb17ef56070773c73272014a60096f06154620fa427ea3b0dbace0ec3d7a9b59e4cb9775da41275d6fe6b904539f59910ad012bc89dc86d3fd43af436040a036375767226261a30e9d05e87c89f821b9875da230409f7d66748bcfc9f8281cf802305a8664739f3354a3d13565b16ce\"\"}\"\n".trim();

    if(TextUtils.isEmpty(cert)){
      Log.d("mcvsafe", "cert is null");
      return;
    }

    final AuthApplyResponse[] resp = {new AuthApplyResponse()};
    FacePassHandler.authDevice(getApplicationContext(), cert, "", new AuthApi.AuthDeviceCallBack() {
      @Override
      public void GetAuthDeviceResult(AuthApplyResponse result) {
          resp[0] = result;
        if (resp[0].errorCode == ErrorCodeConfig.AUTH_SUCCESS) {
          try {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Log.d("mcvsafe", "Apply update: OK");
              }
            });
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        } else {
          try {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Log.d("mcvsafe", "Apply update: error. error code is: " + resp[0].errorCode + " error message: " + resp[0].errorMessage);
              }
            });
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        }
      }
    });
  }

  private void initFacePassSDK() throws IOException {
    Log.d(DEBUG_TAG, "initFacePassSDK");
    FacePassHandler.initSDK(getApplicationContext());

    if (authType == FacePassAuthType.FACEPASS_AUTH_MCVFACE) {
      // Face Authorization
      FacePassHandler.authPrepare(getApplicationContext());
      FacePassHandler.getAuth(authIP, apiKey, apiSecret, true);
    } else if (authType == FacePassAuthType.FACEPASS_AUTH_MCVSAFE) {
      Log.d(DEBUG_TAG, "authType = FACEPASS_AUTH_MCVSAFE");

      // Authorized Interface
      boolean auth_status = FacePassHandler.authCheck();
      Log.d(DEBUG_TAG, "FacePassHandler.authCheck(): " + FacePassHandler.authCheck());
      Log.d(DEBUG_TAG, "FacePassHandler.isAuthorized(): " + FacePassHandler.isAuthorized());
      if (!auth_status) {
        singleCertification();
        auth_status = FacePassHandler.authCheck();
      }

      if (!auth_status) {
        Log.d(DEBUG_TAG, "Authentication result : failed.");
        Log.d("mcvsafe", "Authentication result : failed.");
        return;
      } else {
        Log.d("mcvsafe", "Authentication result : success.");
        return;
      }
    } else {
      Log.d(DEBUG_TAG, "have no auth.");
      Log.d("FacePassDemo", "have no auth.");
      return ;
    }

    Log.d(DEBUG_TAG, "FacePassHandler Version = " + FacePassHandler.getVersion());
  }

  private void checkGroup() {
    Log.d(DEBUG_TAG, "checkGroup");
    if (mFacePassHandler == null) {
      return;
    }

    try {
      String[] localGroups = mFacePassHandler.getLocalGroups();
      isLocalGroupExist = false;

      if (localGroups == null || localGroups.length == 0) {
        Log.d(DEBUG_TAG, group_name);
        return;
      }

      for (String group : localGroups) {
        if (group_name.equals(group)) {
          isLocalGroupExist = true;
        }
      }

      if (!isLocalGroupExist) {
        Log.d(DEBUG_TAG, group_name);
      }
    } catch (FacePassException e) {
      e.printStackTrace();
    }
  }

  private void initFaceHandler() {
    Log.d(DEBUG_TAG, "initFaceHandler");

    new Thread() {
      @Override
      public void run() {
        while (!getActivity().isFinishing()) {
          Log.d(DEBUG_TAG, "FacePassHandler Version: " + String.valueOf(FacePassHandler.getVersion()));
          Log.d(DEBUG_TAG, "FacePassHandler isAvailable: " + String.valueOf(FacePassHandler.isAvailable()));
          while (FacePassHandler.isAvailable()) {
            Log.d(DEBUG_TAG, "Start to build FacePassHandler");
            FacePassConfig config;
            try {
              config = new FacePassConfig();
              config.poseBlurModel = FacePassModel.initModel(getActivity().getApplicationContext().getAssets(), "attr.pose_blur.arm.190630.bin");
              config.livenessModel = FacePassModel.initModel(getActivity().getApplicationContext().getAssets(), "liveness.CPU.rgb.G.bin");

              if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
                config.rgbIrLivenessModel = FacePassModel.initModel(getActivity().getApplicationContext().getAssets(), "liveness.CPU.rgbir.I.bin");
                // Real and fake models on the same screen
                config.rgbIrGaLivenessModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "liveness.CPU.rgbir.ga_case.A.bin");
                // If you need to use the GPU model, load the following model files
                config.livenessGPUCache = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "liveness.GPU.rgbir.I.cache");
                config.rgbIrLivenessGpuModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "liveness.GPU.rgbir.I.bin");
                config.rgbIrGaLivenessGpuModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "liveness.GPU.rgbir.ga_case.A.bin");
              }

              config.searchModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "feat2.arm.K.v1.0_1core.bin");
              config.detectModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "detector.arm.G.bin");
              config.detectRectModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "detector_rect.arm.G.bin");
              config.landmarkModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "pf.lmk.arm.E.bin");
              config.rcAttributeModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "attr.RC.arm.G.bin");
              config.occlusionFilterModel = FacePassModel.initModel(getContext().getApplicationContext().getAssets(), "attr.occlusion.arm.20201209.bin");
              //config.smileModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.200815.bin");

              config.rcAttributeAndOcclusionMode = 1;
              config.searchThreshold = 65f;
              config.livenessThreshold = 80f;
              config.livenessGaThreshold = 85f;

              if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
                config.livenessEnabled = false;
                config.rgbIrLivenessEnabled = true;      // Enable binocular living function (default CPU)
                config.rgbIrLivenessGpuEnabled = true;   // Enable binocular living GPU function
                config.rgbIrGaLivenessEnabled = true;    // Enable the function of real and fake people on the same screen (default CPU)
                config.rgbIrGaLivenessGpuEnabled = true; // Enable the GPU function of real and fake people on the same screen
              } else {
                config.livenessEnabled = true;
                config.rgbIrLivenessEnabled = false;
              }

              ageGenderEnabledGlobal = (config.ageGenderModel != null);

              config.poseThreshold = new FacePassPose(35f, 35f, 35f);
              config.blurThreshold = 0.8f;
              config.lowBrightnessThreshold = 30f;
              config.highBrightnessThreshold = 210f;
              config.brightnessSTDThreshold = 80f;
              config.faceMinThreshold = 60;
              config.retryCount = 10;
              config.smileEnabled = false;
              config.maxFaceEnabled = true;
              config.fileRootPath = getContext().getExternalFilesDir("Download").getAbsolutePath();
              
              // SDK
              mFacePassHandler = new FacePassHandler(config);

              FacePassConfig addFaceConfig = mFacePassHandler.getAddFaceConfig();
              addFaceConfig.poseThreshold.pitch = 35f;
              addFaceConfig.poseThreshold.roll = 35f;
              addFaceConfig.poseThreshold.yaw = 35f;
              addFaceConfig.blurThreshold = 0.7f;
              addFaceConfig.lowBrightnessThreshold = 70f;
              addFaceConfig.highBrightnessThreshold = 220f;
              addFaceConfig.brightnessSTDThresholdLow = 14.14f;
              addFaceConfig.brightnessSTDThreshold = 63.25f;
              addFaceConfig.faceMinThreshold = 100;
              addFaceConfig.rcAttributeAndOcclusionMode = 2;
              mFacePassHandler.setAddFaceConfig(addFaceConfig);

              checkGroup();
            } catch (FacePassException e) {
              e.printStackTrace();
              Log.d(DEBUG_TAG, "FacePassHandler is null: " + e.getMessage());
              return;
            }
            Log.d(DEBUG_TAG, "SDK successfully initialized");
            return;
          }

          try {
            sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
  }

  private void getFaceImageByFaceToken(final long trackId, String faceToken) {
    if (TextUtils.isEmpty(faceToken)) {
      return;
    }

    try {
      final Bitmap bitmap = mFacePassHandler.getFaceImage(faceToken.getBytes());
      Log.i(DEBUG_TAG, mFacePassHandler.getFaceImagePath(faceToken.getBytes()));

      // mAndroidHandler.post(new Runnable() {
      //     @Override
      //     public void run() {
      //         Log.i(DEBUG_TAG, "getFaceImageByFaceToken cache is null");
      //         showToast("ID = " + String.valueOf(trackId), Toast.LENGTH_SHORT, true, bitmap);
      //     }
      // });

      if (bitmap != null) {
        return;
      }
    } catch (FacePassException e) {
      e.printStackTrace();
    }
  }

  int findidx(FacePassAgeGenderResult[] results, long trackId) {
    int result = -1;
    if (results == null) {
      return result;
    }
    for (int i = 0; i < results.length; ++i) {
      if (results[i].trackId == trackId) {
        return i;
      }
    }
    return result;
  }

  private class RecognizeThread extends Thread {
    boolean isInterrupt;

    @Override
    public void run() {
      while (!isInterrupt) {
        Log.d(DEBUG_TAG, "!!!!RecognizeThread!!!");
        try {
          RecognizeData recognizeData = mRecognizeDataQueue.take();
          FacePassAgeGenderResult[] ageGenderResult = null;

          if (isLocalGroupExist) {
          //  if (true) {
            Log.d(DEBUG_TAG, "RecognizeData >>>>");

            FacePassRecognitionResult[][] recognizeResultArray = mFacePassHandler.recognize(group_name, recognizeData.message, 1, recognizeData.trackOpt);
            if (recognizeResultArray != null && recognizeResultArray.length > 0) {
              for (FacePassRecognitionResult[] recognizeResult : recognizeResultArray) {
                if (recognizeResult != null && recognizeResult.length > 0) {
                  for (FacePassRecognitionResult result : recognizeResult) {
                    String faceToken = new String(result.faceToken);
                    if (FacePassRecognitionState.RECOGNITION_PASS == result.recognitionState) {
                      getFaceImageByFaceToken(result.trackId, faceToken);
                      Log.i(DEBUG_TAG, "SUCCESSFULLY RECOGNIZED");
                    } else {
                      Log.i(DEBUG_TAG, "FAILED TO RECOGNIZE");
                    }

                    Log.d(DEBUG_TAG, String.format("recognize trackid: %d, searchScore: %f  searchThreshold: %f, hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
                      result.trackId,
                      result.detail.searchScore,
                      result.detail.searchThreshold,
                      result.detail.rcAttr.hairType.ordinal(),
                      result.detail.rcAttr.beardType.ordinal(),
                      result.detail.rcAttr.hatType.ordinal(),
                      result.detail.rcAttr.respiratorType.ordinal(),
                      result.detail.rcAttr.glassesType.ordinal(),
                      result.detail.rcAttr.skinColorType.ordinal())
                    );
                  }
                }
              }
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (FacePassException e) {
          e.printStackTrace();
        }
      }
    }

    @Override
    public void interrupt() {
      isInterrupt = true;
      super.interrupt();
    }
  }

  public static Pair<CameraPreviewData, CameraPreviewData> takeComplexFrame() throws InterruptedException {
    return complexFrameQueue.take();
  }

  private class FeedFrameThread extends Thread {
    boolean isInterrupt;

    @Override
    public void run() {
      while (!isInterrupt) {
        if (mFacePassHandler == null) {
          continue;
        }

        long startTime = System.currentTimeMillis(); //起始时间

        // Send each frame of FacePassImage into the SDK algorithm and get the returned result
        FacePassDetectionResult detectionResult = null;
        try {
          if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
            Pair<CameraPreviewData, CameraPreviewData> framePair;
            try {
              framePair = takeComplexFrame();
            } catch (InterruptedException e) {
              e.printStackTrace();
              continue;
            }
            FacePassImage imageRGB = new FacePassImage(framePair.first.nv21Data, framePair.first.width, framePair.first.height, cameraRotation, FacePassImageType.NV21);
            FacePassImage imageIR = new FacePassImage(framePair.second.nv21Data, framePair.second.width, framePair.second.height, cameraRotation, FacePassImageType.NV21);
            detectionResult = mFacePassHandler.feedFrameRGBIR(imageRGB, imageIR);
          } else {
            CameraPreviewData cameraPreviewData = null;
            try {
              cameraPreviewData = mFeedFrameQueue.take();
            } catch (InterruptedException e) {
              e.printStackTrace();
              continue;
            }

            FacePassImage imageRGB = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, cameraRotation, FacePassImageType.NV21);
            detectionResult = mFacePassHandler.feedFrame(imageRGB);
          }
        } catch (FacePassException e) {
          e.printStackTrace();
        }

        if (detectionResult == null || detectionResult.faceList.length == 0) {
          // There is no face detected in the current frame
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              // faceView.clear();
              // faceView.invalidate();
            }
          });
        } else {
          // Circle the recognized face in the preview interface, and display the face position and angle information on the top
          final FacePassFace[] bufferFaceList = detectionResult.faceList;
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              // showFacePassFace(bufferFaceList);
            }
          });
        }

        if (SDK_MODE == FacePassSDKMode.MODE_OFFLINE) {
          // Offline mode, add the result that recognizes the face and the message is not empty to the processing queue
          if (detectionResult != null && detectionResult.message.length != 0) {
            Log.d(DEBUG_TAG, "mRecognizeDataQueue.offer");
            // Attribute information of all detected face frames
            for (int i = 0; i < detectionResult.faceList.length; ++i) {
              Log.d(DEBUG_TAG, String.format("rc attribute faceList hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
              detectionResult.faceList[i].rcAttr.hairType.ordinal(),
              detectionResult.faceList[i].rcAttr.beardType.ordinal(),
              detectionResult.faceList[i].rcAttr.hatType.ordinal(),
              detectionResult.faceList[i].rcAttr.respiratorType.ordinal(),
              detectionResult.faceList[i].rcAttr.glassesType.ordinal(),
              detectionResult.faceList[i].rcAttr.skinColorType.ordinal()));
            }

            Log.d(DEBUG_TAG, "--------------------------------------------------------------------------------------------------------------------------------------------------");
            // Send the attribute information of the recognized face frame
            FacePassTrackOptions[] trackOpts = new FacePassTrackOptions[detectionResult.images.length];
            for (int i = 0; i < detectionResult.images.length; ++i) {
              if (detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.INVALID &&
                  detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.NO_RESPIRATOR) {

                float searchThreshold = 60f;
                float livenessThreshold = 80f; // -1.0f will not change the liveness threshold
                float livenessGaThreshold = 85f;
                float smallsearchThreshold = -1.0f; // -1.0f will not change the smallsearch threshold
                trackOpts[i] = new FacePassTrackOptions(detectionResult.images[i].trackId, searchThreshold, livenessThreshold, livenessGaThreshold, smallsearchThreshold);
              }
              Log.d(DEBUG_TAG, String.format("rc attribute in FacePassImage, hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
              detectionResult.images[i].rcAttr.hairType.ordinal(),
              detectionResult.images[i].rcAttr.beardType.ordinal(),
              detectionResult.images[i].rcAttr.hatType.ordinal(),
              detectionResult.images[i].rcAttr.respiratorType.ordinal(),
              detectionResult.images[i].rcAttr.glassesType.ordinal(),
              detectionResult.images[i].rcAttr.skinColorType.ordinal()));
            }
            RecognizeData mRecData = new RecognizeData(detectionResult.message, trackOpts);
            mRecognizeDataQueue.offer(mRecData);
          }
        }
        long endTime = System.currentTimeMillis(); // End Time
        long runTime = endTime - startTime;
        for (int i = 0; i < detectionResult.faceList.length; ++i) {
          Log.i("DEBUG_TAG", "rect[" + i + "] = (" + detectionResult.faceList[i].rect.left + ", " + detectionResult.faceList[i].rect.top + ", " + detectionResult.faceList[i].rect.right + ", " + detectionResult.faceList[i].rect.bottom);
        }
        //Log.i("]time", String.format("feedframe %d ms", runTime));
      }
    }

    @Override
    public void interrupt() {
      isInterrupt = true;
      super.interrupt();
    }
  }

  private boolean initializeAPK() {
    Log.d(DEBUG_TAG, "initializeAPK: ");

    mRecognizeDataQueue = new ArrayBlockingQueue<RecognizeData>(5);
    mFeedFrameQueue = new ArrayBlockingQueue<CameraPreviewData>(1);

    if (!hasPermission()) {
      Log.d(DEBUG_TAG, "Device Permission: " + hasPermission());
      requestPermission();

      boolean isPermissionDenied = PackageManager.PERMISSION_DENIED == -1 ? false : true;
      Log.d(DEBUG_TAG, "Permission Denied: " + isPermissionDenied);
    } else {
      try {
        Log.d(DEBUG_TAG, "Device Permission: " + hasPermission());

        initFacePassSDK();
      } catch (Exception e) {
        e.printStackTrace();
        Log.e(DEBUG_TAG, "Error: Initialize Facepass SDK");
        return false;
      }
    }

    initFaceHandler();

    mFeedFrameThread = new FeedFrameThread();
    mFeedFrameThread.start();

    mRecognizeThread = new RecognizeThread();
    mRecognizeThread.start();

    return true;
  }

  // --- CREATE GROUP
  public boolean createGroup(String groupName) {
    if (mFacePassHandler == null) {
      Log.d(DEBUG_TAG, "FacePassHandle is null ! ");
      return false;
    }

    if (TextUtils.isEmpty(groupName)) {
      Log.d(DEBUG_TAG,"please input group name ！");
      return false;
    }

    boolean isSuccess = false;
    try {
      isSuccess = mFacePassHandler.createLocalGroup(groupName);
    } catch (FacePassException e) {
      e.printStackTrace();
      return false;
    }

    Log.d(DEBUG_TAG,"create group " + isSuccess);
    // callbackContext.success("create group " + groupName + " " + isSuccess);
    if (isSuccess && group_name.equals(groupName)) {
      isLocalGroupExist = true;
      group_name = groupName;
      return true;
    }
    return false;
  }

  // --- ADD FACE
  public String addFace(String bitmapBase64) {
    DEBUG_TAG = "ADD_FACE";
    Log.d(DEBUG_TAG, "addFace ");
    byte[] decodedString = Base64.decode(bitmapBase64, Base64.DEFAULT);
    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    faceData = decodedString;
    faceBitmap = bitmap;

    if (mFacePassHandler == null) {
      System.out.println("FacePassHandle is null !");
      Log.d(DEBUG_TAG,"FacePassHandle is null !");
      // callbackContext.error("FacePassHandle is null !");
      return "";
    }

    try {
      FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);
      boolean isNull = result == null;
      Log.d(DEBUG_TAG, "result isNUll: " + isNull);
      if (result != null) {
        if (result.result == 0) {
          android.util.Log.d("qujiaqi", "result:" + result
            + ",bl:" + result.blur
            + ",pp:" + result.pose.pitch
            + ",pr:" + result.pose.roll
            + ",py" + result.pose.yaw);
          // callbackContext.success(new String(result.faceToken));
          faceToken = result.faceToken;
          Log.d(DEBUG_TAG, "add face successfully! ");
          Log.d(DEBUG_TAG, new String(result.faceToken));
          return new String(result.faceToken);
        } else if (result.result == 1) {
          // callbackContext.success("no face ！");
          Log.d(DEBUG_TAG, "no face ！");
          return "";
        } else {
          // callbackContext.success("quality problem！");
          Log.d(DEBUG_TAG, "quality problem！");
          return "";
        }
      }
    } catch (FacePassException e) {
      e.printStackTrace();
      Log.d(DEBUG_TAG, "add face error");
      // callbackContext.error(e.getMessage());
      return "";
    }

    Log.d(DEBUG_TAG, "add face throws error");
    return "";
  }

  // --- Bind Group Face Token
  public boolean bindGroupFaceToken(String groupName, String faceTokenStr) {

    byte[] faceToken = faceTokenStr.trim().getBytes();

    if (mFacePassHandler == null) {
      // callbackContext.error("FacePassHandle is null ! ");
      Log.d(DEBUG_TAG, "Facepass Handle is null!");
      return false;
    }

    if (faceToken == null || faceToken.length == 0 || TextUtils.isEmpty(groupName)) {
      // callbackContext.error("params error！");
      Log.d(DEBUG_TAG, "Params error!");
      return false;
    }

    try {
      boolean b = mFacePassHandler.bindGroup(groupName, faceToken);
      String result = b ? "success " : "failed";
      Log.d(DEBUG_TAG, "bind  " + result);
      // callbackContext.success("bind  " + result);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      // callbackContext.error(e.getMessage());
      Log.d(DEBUG_TAG, e.getMessage());
      return false;
    }
  }

  // --- Initialize View
  // private void initView() {
  //   int windowRotation = ((WindowManager) (getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
  //   if (windowRotation == 0) {
  //     cameraRotation = FacePassImageRotation.DEG90;
  //   } else if (windowRotation == 90) {
  //     cameraRotation = FacePassImageRotation.DEG0;
  //   } else if (windowRotation == 270) {
  //     cameraRotation = FacePassImageRotation.DEG180;
  //   } else {
  //     cameraRotation = FacePassImageRotation.DEG270;
  //   }
  //   Log.i(DEBUG_TAG, "Rotation: cameraRation: " + cameraRotation);

  //   cameraFacingFront = true;
  //   SharedPreferences preferences = getContext().getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
  //   SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
  //   SettingVar.isCross = preferences.getBoolean("isCross", SettingVar.isCross);
  //   SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
  //   SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
  //   SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);

  //   if (SettingVar.isSettingAvailable) {
  //     cameraRotation = SettingVar.faceRotation;
  //     cameraFacingFront = SettingVar.cameraFacingFront;
  //   }

  //   Log.i(DEBUG_TAG, "Rotation: screenRotation: " + String.valueOf(windowRotation));
  //   Log.i(DEBUG_TAG, "Rotation: faceRotation: " + SettingVar.faceRotation);
  //   Log.i(DEBUG_TAG, "Rotation: new cameraRation: " + cameraRotation);
  //   final int mCurrentOrientation = getContext().getResources().getConfiguration().orientation;

  //   if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
  //     screenState = 1;
  //   } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
  //     screenState = 0;
  //   }
  //   //cordova.getActivity().setContentView(R.layout.activity_main);

  //   SettingVar.cameraSettingOk = false;
  //   //manager = new CameraManager();
  //   //cameraView = (CameraPreview) findViewById(R.id.preview);
  //   //manager.setPreviewDisplay(cameraView);
  //   /* 注册相机回调函数 */
  //   //manager.setListener(this);
  // }
}

