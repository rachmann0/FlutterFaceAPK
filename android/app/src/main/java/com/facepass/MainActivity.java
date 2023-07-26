package com.facepass;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import java.util.Map;
import java.util.HashMap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

// import com.android.volley.AuthFailureError;
// import com.android.volley.DefaultRetryPolicy;
// import com.android.volley.NetworkResponse;
// import com.android.volley.Request;
// import com.android.volley.RequestQueue;
// import com.android.volley.Response;
// import com.android.volley.VolleyError;
// import com.android.volley.VolleyLog;
// import com.android.volley.toolbox.HttpHeaderParser;
// import com.android.volley.toolbox.ImageLoader;
// import com.android.volley.toolbox.Volley;

// import org.apache.http.HttpEntity;
// import org.apache.http.entity.ContentType;
// import org.apache.http.entity.mime.MultipartEntityBuilder;
// import org.apache.http.entity.mime.content.ByteArrayBody;
// import org.apache.http.entity.mime.content.StringBody;
// import org.apache.http.util.CharsetUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

// import com.facepass.adapter.FaceTokenAdapter;
// import com.facepass.adapter.GroupNameAdapter;
import com.facepass.camera.CameraManager;
import com.facepass.camera.CameraPreview;
import com.facepass.camera.CameraPreviewData;
import com.facepass.camera.ComplexFrameHelper;
// import com.facepass.network.ByteRequest;
import com.facepass.utils.FileUtil;

public class MainActivity extends FlutterActivity {
  public static final String CHANNEL = "com.facepass/channel";

  private enum FacePassSDKMode {
    MODE_ONLINE,
    MODE_OFFLINE
  }

  private enum FacePassCameraType{
    FACEPASS_SINGLECAM,
    FACEPASS_DUALCAM
  };

  private enum FacePassAuthType{
    FASSPASS_AUTH_MCVFACE,
    FACEPASS_AUTH_MCVSAFE
  };

  private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;

  private static final String DEBUG_TAG = "FacePass";

  // 需要客户根据自己需求配置
  private static final String authIP = "https://api-cn.faceplusplus.com";
  public static final String apiKey = "";
  public static final String apiSecret = "";

public static final String CERT_PATH = "Download/CBG_Android_Face_Reco---30-Trial-one-stage.cert";

  /* 根据需求配置单目 / 双目场景，默认单目 */
  private static FacePassCameraType CamType = FacePassCameraType.FACEPASS_SINGLECAM;

  /* 根据需求配置授权 mcvface / mcvsafe ，默认mcvface */
  // private static FacePassAuthType authType = FacePassAuthType.FASSPASS_AUTH_MCVFACE;
  private static FacePassAuthType authType = FacePassAuthType.FACEPASS_AUTH_MCVSAFE;

  /* 人脸识别Group */
  private static final String group_name = "facepass";

  /* 程序所需权限 ：相机 文件存储 网络访问 */
  private static final int PERMISSIONS_REQUEST = 1;
  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
  private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
  private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
  private String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};

  /* SDK 实例对象 */
  FacePassHandler mFacePassHandler;

  /* 相机实例 */
  private CameraManager manager;
  private CameraManager mIRCameraManager;

  /* 显示人脸位置角度信息 */
  // private TextView faceBeginTextView;

  /* 显示faceId */
  private TextView faceEndTextView;

  /* 相机预览界面 */
  private CameraPreview cameraView;
  private CameraPreview mIRCameraView;

  private boolean isLocalGroupExist = false;

  /* 在预览界面圈出人脸 */
  // private FaceView faceView;

  private ScrollView scrollView;

  /* 相机是否使用前置摄像头 */
  private static boolean cameraFacingFront = true;
  /* 相机图片旋转角度，请根据实际情况来设置
  * 对于标准设备，可以如下计算旋转角度rotation
  * int windowRotation = ((WindowManager)(getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
  * Camera.CameraInfo info = new Camera.CameraInfo();
  * Camera.getCameraInfo(cameraFacingFront ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK, info);
  * int cameraOrientation = info.orientation;
  * int rotation;
  * if (cameraFacingFront) {
  *     rotation = (720 - cameraOrientation - windowRotation) % 360;
  * } else {
  *     rotation = (windowRotation - cameraOrientation + 360) % 360;
  * }
  */
  private int cameraRotation;

  private static final int cameraWidth = 1280;
  private static final int cameraHeight = 720;

  private int mSecretNumber = 0;
  private static final long CLICK_INTERVAL = 600;
  private long mLastClickTime;


  private int heightPixels;
  private int widthPixels;

  int screenState = 0;// 0 横 1 竖

  Button visible;
  LinearLayout ll;
  FrameLayout frameLayout;
  private int buttonFlag = 0;
  private Button settingButton;
  private boolean ageGenderEnabledGlobal;

  /*Toast*/
  private Toast mRecoToast;

  /*DetectResult queue*/
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

  ArrayBlockingQueue<RecognizeData> mRecognizeDataQueue;
  ArrayBlockingQueue<CameraPreviewData> mFeedFrameQueue;
  /*recognize thread*/
  RecognizeThread mRecognizeThread;
  FeedFrameThread mFeedFrameThread;


  /*底库同步*/
  private ImageView mSyncGroupBtn;
  private AlertDialog mSyncGroupDialog;

  private ImageView mFaceOperationBtn;
  /*图片缓存*/
  // private FaceImageCache mImageCache;

  private Handler mAndroidHandler;

  private CameraPreviewData mCurrentImage;


  private Button mSDKModeBtn;
  int mId = 0;

  protected boolean front = true;
  private int previewDegreen = 0;

  private static final int REQUEST_CODE_CHOOSE_PICK = 1;

  private AlertDialog mFaceOperationDialog;


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
          // case "initializeAPK":
          //   result.success(initializeAPK());
          //   break;
          // case "createGroup":
          //   group_name = (String) args.get("groupName");
          //   result.success(createGroup(group_name));
          //   break;
          // case "addFace":
          //   // addFace(faceBM64);
          //   Log.d(DEBUG_TAG, "invoke add face");
          //   String faceImage = (String) args.get("data");
          //   result.success(addFace(faceImage));
          //   break;
          // case "bindGroupFaceToken":
          //   group_name = (String) args.get("groupName");
          //   String faceTokenStr = (String) args.get("faceToken");
          //   bindGroupFaceToken(group_name, faceTokenStr);
          //   result.success(true);
          //   break;
          // case "passFaceData":
          //   byte[] byteData = (byte[]) args.get("byteData");
          //   int width = (int) args.get("width");
          //   int height = (int) args.get("height");

          //   mFeedFrameQueue.offer(new CameraPreviewData(byteData, width, height, previewDegreen, front));
          //   // try calling feed frame and recognize without using threads
          //   feedFrame();

          //   result.success(args);
          //   break;
          default:
            Log.e(DEBUG_TAG, "unidentified channel");
            result.notImplemented();
        }
      }
    );
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(DEBUG_TAG, "onCreate");

    super.onCreate(savedInstanceState);
    // mImageCache = new FaceImageCache();
    mRecognizeDataQueue = new ArrayBlockingQueue<RecognizeData>(5);
    mFeedFrameQueue = new ArrayBlockingQueue<CameraPreviewData>(1);
    initAndroidHandler();

    /* 初始化界面 */
    // initView();

    /* 申请程序所需权限 */
    if (!hasPermission()) {
      requestPermission();
      Log.d(DEBUG_TAG, "PackageManager.PERMISSION_DENIED: " + PackageManager.PERMISSION_DENIED);
      Log.d(DEBUG_TAG, "hasPermission(): " + hasPermission());
      Log.d(DEBUG_TAG, "checkSelfPermission(PERMISSION_CAMERA): " + checkSelfPermission(PERMISSION_CAMERA));
      Log.d(DEBUG_TAG, "checkSelfPermission(PERMISSION_READ_STORAGE): " + checkSelfPermission(PERMISSION_READ_STORAGE));
      Log.d(DEBUG_TAG, "checkSelfPermission(PERMISSION_WRITE_STORAGE): " + checkSelfPermission(PERMISSION_WRITE_STORAGE));
      Log.d(DEBUG_TAG, "checkSelfPermission(PERMISSION_INTERNET): " + checkSelfPermission(PERMISSION_INTERNET));
      Log.d(DEBUG_TAG, "checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE): " + checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE));
    } else {
      try {
        initFacePassSDK();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    initFaceHandler();

    mRecognizeThread = new RecognizeThread();
    mRecognizeThread.start();
    mFeedFrameThread = new FeedFrameThread();
    mFeedFrameThread.start();
  }

  private void initAndroidHandler() {
    mAndroidHandler = new Handler();
  }

  private void singleCertification(Context mContext) throws IOException {
    // String cert = FileUtil.readExternal(CERT_PATH).trim();
    
    // String cert = "\"{\"\"serial\"\":\"\"z0005a8759f61d5f4b2862852034c139ddada\"\",\"\"key\"\":\"\"2a6a6e824b1bfb87553faecb38faf4122936055c915fb3ac814c8879994d1542f304999e02ec2ff25d278b110695b76980b3002d57d2f4f20d779f2ffc95e1bac4ff713f244ad0d7da10a0491ee0fbfce6c9ee0f4a8fd42f0fb17ef56070773c73272014a60096f06154620fa427ea3b0dbace0ec3d7a9b59e4cb9775da41275d6fe6b904539f59910ad012bc89dc86d3fd43af436040a036375767226261a30e9d05e87c89f821b9875da230409f7d66748bcfc9f8281cf802305a8664739f3354a3d13565b16ce\"\"}\"\n".trim();
    // String cert = "\"{\"\"serial\"\":\"\"z0005a8759f61d5f4b2862852034c139ddada\"\",\"\"key\"\":\"\"2a6a6e824b1bfb87553faecb38faf4122936055c915fb3ac814c8879994d1542f304999e02ec2ff25d278b110695b76980b3002d57d2f4f20d779f2ffc95e1bac4ff713f244ad0d7da10a0491ee0fbfce6c9ee0f4a8fd42f0fb17ef56070773c73272014a60096f06154620fa427ea3b0dbace0ec3d7a9b59e4cb9775da41275d6fe6b904539f59910ad012bc89dc86d3fd43af436040a036375767226261a30e9d05e87c89f821b9875da230409f7d66748bcfc9f8281cf802305a8664739f3354a3d13565b16ce\"\"}\"\n";
    String cert = "\"{\"\"serial\"\":\"\"z0005a8759f61d5f4b2862852034c139ddada\"\",\"\"key\"\":\"\"2a6a6e824b1bfb87553faecb38faf4122936055c915fb3ac814c8879994d1542f304999e02ec2ff25d278b110695b76980b3002d57d2f4f20d779f2ffc95e1bac4ff713f244ad0d7da10a0491ee0fbfce6c9ee0f4a8fd42f0fb17ef56070773c73272014a60096f06154620fa427ea3b0dbace0ec3d7a9b59e4cb9775da41275d6fe6b904539f59910ad012bc89dc86d3fd43af436040a036375767226261a30e9d05e87c89f821b9875da230409f7d66748bcfc9f8281cf802305a8664739f3354a3d13565b16ce\"\"}\"\n".trim();
    if(TextUtils.isEmpty(cert)){
      Log.d("mcvsafe", "cert is null");
      return;
    } else {
      Log.d("mcvsafe", "cert is not null");
    }
    final AuthApplyResponse[] resp = {new AuthApplyResponse()};
    FacePassHandler.authDevice(mContext.getApplicationContext(), cert, "", new AuthApi.AuthDeviceCallBack() {
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

    Context mContext = getApplicationContext();
    FacePassHandler.initSDK(mContext);
    if (authType == FacePassAuthType.FASSPASS_AUTH_MCVFACE) {
      
      // face++授权
      FacePassHandler.authPrepare(getApplicationContext());
      FacePassHandler.getAuth(authIP, apiKey, apiSecret, true);
    } else if (authType == FacePassAuthType.FACEPASS_AUTH_MCVSAFE) {
      Log.d(DEBUG_TAG, "authType = FACEPASS_AUTH_MCVSAFE");

      // 金雅拓授权接口
      boolean auth_status = FacePassHandler.authCheck();
      if ( !auth_status ) {
        singleCertification(mContext);
        auth_status = FacePassHandler.authCheck();
      }

      if ( !auth_status ) {
        Log.d(DEBUG_TAG, "Authentication result : failed.");
        Log.d("mcvsafe", "Authentication result : failed.");

        // 授权不成功，根据业务需求处理
        return;
      }else {
        Log.d("mcvsafe", "Authentication result : success.");
      }
    } else {
      Log.d(DEBUG_TAG, "have no auth.");
      Log.d("FacePassDemo", "have no auth.");
      return ;
    }
    Log.d(DEBUG_TAG, "FacePassHandler.getVersion() = " + FacePassHandler.getVersion());
  }

  private void initFaceHandler() {
    Log.d(DEBUG_TAG, "initFaceHandler");
    new Thread() {
        @Override
      public void run() {
        while (true && !isFinishing()) {
          Log.d(DEBUG_TAG, "FacePassHandler.isAvailable() = " + String.valueOf(FacePassHandler.isAvailable()));
          while (FacePassHandler.isAvailable()) {
            Log.d(DEBUG_TAG, "start to build FacePassHandler");
            FacePassConfig config;
            try {
              /* 填入所需要的模型配置 */
              config = new FacePassConfig();
              config.poseBlurModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.pose_blur.arm.190630.bin");

              config.livenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.CPU.rgb.G.bin");
              if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
                config.rgbIrLivenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.CPU.rgbir.I.bin");
                // 真假人同屏模型
                config.rgbIrGaLivenessModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.CPU.rgbir.ga_case.A.bin");
                // 若需要使用GPU模型则加载以下模型文件
                config.livenessGPUCache = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.GPU.rgbir.I.cache");
                config.rgbIrLivenessGpuModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.GPU.rgbir.I.bin");
                config.rgbIrGaLivenessGpuModel = FacePassModel.initModel(getApplicationContext().getAssets(), "liveness.GPU.rgbir.ga_case.A.bin");
              }

              config.searchModel = FacePassModel.initModel(getApplicationContext().getAssets(), "feat2.arm.K.v1.0_1core.bin");

              config.detectModel = FacePassModel.initModel(getApplicationContext().getAssets(), "detector.arm.G.bin");
              config.detectRectModel = FacePassModel.initModel(getApplicationContext().getAssets(), "detector_rect.arm.G.bin");
              config.landmarkModel = FacePassModel.initModel(getApplicationContext().getAssets(), "pf.lmk.arm.E.bin");

              config.rcAttributeModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.G.bin");
              config.occlusionFilterModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.occlusion.arm.20201209.bin");
              //config.smileModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.200815.bin");

              /* 送识别阈值参数 */
              config.rcAttributeAndOcclusionMode = 1;
              config.searchThreshold = 65f;
              config.livenessThreshold = 80f;
              config.livenessGaThreshold = 85f;
              if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
                config.livenessEnabled = false;
                config.rgbIrLivenessEnabled = true;      // 启用双目活体功能(默认CPU)
                config.rgbIrLivenessGpuEnabled = true;   // 启用双目活体GPU功能
                config.rgbIrGaLivenessEnabled = true;    // 启用真假人同屏功能(默认CPU)
                config.rgbIrGaLivenessGpuEnabled = true; // 启用真假人同屏GPU功能
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
              config.fileRootPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

              /* 创建SDK实例 */
              mFacePassHandler = new FacePassHandler(config);

              /* 入库阈值参数 */
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
              // Log.d(DEBUG_TAG, "FacePassHandler is null");
              Log.d(DEBUG_TAG, "FacePassHandler is null: " + e.getMessage());
              return;
            }
            return;
          }
          try {
            /* 如果SDK初始化未完成则需等待 */
            sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
  }

  // @Override
  // protected void onResume() {
  //   checkGroup();
  //   initToast();
  //   /* 打开相机 */
  //   if (hasPermission()) {
  //     manager.open(getWindowManager(), false, cameraWidth, cameraHeight);
  //     if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
  //       mIRCameraManager.open(getWindowManager(), true, cameraWidth, cameraHeight);
  //     }
  //   }
  //   adaptFrameLayout();
  //   super.onResume();
  // }

  private void checkGroup() {
    if (mFacePassHandler == null) {
      return;
    }
    try {
      String[] localGroups = mFacePassHandler.getLocalGroups();
      isLocalGroupExist = false;
      if (localGroups == null || localGroups.length == 0) {
        // faceView.post(new Runnable() {
        //   @Override
        //   public void run() {
        //     toast("请创建" + group_name + "底库");
        //   }
        // });
        return;
      }
      for (String group : localGroups) {
        if (group_name.equals(group)) {
          isLocalGroupExist = true;
        }
      }
      if (!isLocalGroupExist) {
        // faceView.post(new Runnable() {
        //   @Override
        //   public void run() {
        //     toast("请创建" + group_name + "底库");
        //   }
        // });
      }
    } catch (FacePassException e) {
      e.printStackTrace();
    }
  }

  private class FeedFrameThread extends Thread {
    boolean isInterrupt;

    @Override
    public void run() {
      while (!isInterrupt) {
        if (mFacePassHandler == null) {
          continue;
        }

        // Camera mCamera = Camera.open();
        // try {
        //     mCamera.setPreviewTexture(new SurfaceTexture(10));
        // } catch (IOException e1) {
        //     Log.e(DEBUG_TAG, e1.getMessage());
        // }

        // Camera.Parameters params = mCamera.getParameters();
        // params.setPreviewSize(1280, 720);
        // params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        // //params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        // //params.setPictureFormat(ImageFormat.JPEG);
        // params.setPreviewFormat(ImageFormat.NV21);
        // mCamera.setParameters(params);
        // mCamera.startPreview();
        // mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
        //     @Override
        //     public void onPictureTaken(byte[] data, Camera camera) {
        //         Log.i(DEBUG_TAG, "picture-taken");
        //         //cameraPreviewData = new CameraPreviewData(data, 640, 480,
        //                 //previewDegreen, front););


        //         CameraPreviewData cameraPreviewData = new CameraPreviewData(data, 1280,720, previewDegreen, front);
        //         mFeedFrameQueue.offer(cameraPreviewData);
        //     }
        // });

        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        long startTime = System.currentTimeMillis(); //起始时间

        /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
        FacePassDetectionResult detectionResult = null;
        try {
          if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
            Pair<CameraPreviewData, CameraPreviewData> framePair;
            try {
              framePair = ComplexFrameHelper.takeComplexFrame();
            } catch (InterruptedException e) {
              e.printStackTrace();
              continue;
            }
            FacePassImage imageRGB = new FacePassImage(framePair.first.nv21Data, framePair.first.width, framePair.first.height, cameraRotation, FacePassImageType.NV21);
            FacePassImage imageIR = new FacePassImage(framePair.second.nv21Data, framePair.second.width, framePair.second.height, cameraRotation, FacePassImageType.NV21);
            detectionResult = mFacePassHandler.feedFrameRGBIR(imageRGB, imageIR);
            //detectionResult = mFacePassHandler.feedFrame(imageRGB, imageIR);
          } else {
            // CamType == FacePassCameraType.FACEPASS_SINGLECAM
            CameraPreviewData cameraPreviewData = null;
            try {
              cameraPreviewData = mFeedFrameQueue.take();
            } catch (InterruptedException e) {
              e.printStackTrace();
              continue;
            }

            // Log.d(DEBUG_TAG, "cameraPreviewData.nv21Data: "+ cameraPreviewData.nv21Data);
            // Log.d(DEBUG_TAG, "cameraPreviewData.width: "+ cameraPreviewData.width);
            // Log.d(DEBUG_TAG, "cameraPreviewData.height: "+ cameraPreviewData.height);
            // Log.d(DEBUG_TAG, "cameraRotation: "+ cameraRotation);
            // Log.d(DEBUG_TAG, "FacePassImageType.NV21: "+ FacePassImageType.NV21);

            FacePassImage imageRGB = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, cameraRotation, FacePassImageType.NV21);
            detectionResult = mFacePassHandler.feedFrame(imageRGB);
          }
        } catch (FacePassException e) {
          e.printStackTrace();
        }

        if (detectionResult == null || detectionResult.faceList.length == 0) {
          /* 当前帧没有检出人脸 */
          // runOnUiThread(new Runnable() {
          //   @Override
          //   public void run() {
          //     faceView.clear();
          //     faceView.invalidate();
          //   }
          // });
        } else {
          /* 将识别到的人脸在预览界面中圈出，并在上方显示人脸位置及角度信息 */
          final FacePassFace[] bufferFaceList = detectionResult.faceList;
          // runOnUiThread(new Runnable() {
          //   @Override
          //   public void run() {
          //     showFacePassFace(bufferFaceList);
          //   }
          // });
        }

        if (SDK_MODE == FacePassSDKMode.MODE_OFFLINE) {
          /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
          if (detectionResult != null && detectionResult.message.length != 0) {
            Log.d(DEBUG_TAG, "detection result not null");
            Log.d(DEBUG_TAG, "mRecognizeDataQueue.offer");
            /*所有检测到的人脸框的属性信息*/
            for (int i = 0; i < detectionResult.faceList.length; ++i) {
              Log.d(DEBUG_TAG, String.format("rc attribute faceList hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
                detectionResult.faceList[i].rcAttr.hairType.ordinal(),
                detectionResult.faceList[i].rcAttr.beardType.ordinal(),
                detectionResult.faceList[i].rcAttr.hatType.ordinal(),
                detectionResult.faceList[i].rcAttr.respiratorType.ordinal(),
                detectionResult.faceList[i].rcAttr.glassesType.ordinal(),
                detectionResult.faceList[i].rcAttr.skinColorType.ordinal())
              );
            }
            Log.d(DEBUG_TAG, "--------------------------------------------------------------------------------------------------------------------------------------------------");
            /*送识别的人脸框的属性信息*/
            FacePassTrackOptions[] trackOpts = new FacePassTrackOptions[detectionResult.images.length];
            for (int i = 0; i < detectionResult.images.length; ++i) {
              if (detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.INVALID
                      && detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.NO_RESPIRATOR) {
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
                detectionResult.images[i].rcAttr.skinColorType.ordinal())
              );
            }
            RecognizeData mRecData = new RecognizeData(detectionResult.message, trackOpts);
            mRecognizeDataQueue.offer(mRecData);
          }
        }
        long endTime = System.currentTimeMillis(); //结束时间
        long runTime = endTime - startTime;
        for (int i = 0; i < detectionResult.faceList.length; ++i) {
          //Log.i(DEBUG_TAG, "rect[" + i + "] = (" + detectionResult.faceList[i].rect.left + ", " + detectionResult.faceList[i].rect.top + ", " + detectionResult.faceList[i].rect.right + ", " + detectionResult.faceList[i].rect.bottom);
        }
        Log.i("]time", String.format("feedframe %d ms", runTime));
      }
    }

    @Override
    public void interrupt() {
      isInterrupt = true;
      super.interrupt();
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
        try {
          RecognizeData recognizeData = mRecognizeDataQueue.take();
          FacePassAgeGenderResult[] ageGenderResult = null;
          //if (ageGenderEnabledGlobal) {
          //    ageGenderResult = mFacePassHandler.getAgeGender(detectionResult);
          //    for (FacePassAgeGenderResult t : ageGenderResult) {
          //        Log.e("FacePassAgeGenderResult", "id " + t.trackId + " age " + t.age + " gender " + t.gender);
          //    }
          //}

          if (isLocalGroupExist) {
            Log.d(DEBUG_TAG, "RecognizeData >>>>");

            // FacePassLivenessResult livenessResult[] = mFacePassHandler.livenessClassify(recognizeData.message, recognizeData.trackOpt);
            // if (livenessResult != null && livenessResult.length > 0) {
            //     for (FacePassLivenessResult result : livenessResult) {
            //     Log.d(DEBUG_TAG, String.format("liveness trackid: %d, livenessScore: %f  livenessThreshold: %f, hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
            //       result.trackId,
            //       result.livenessScore,
            //       result.livenessThreshold,
            //       result.rcAttr.hairType.ordinal(),
            //       result.rcAttr.beardType.ordinal(),
            //       result.rcAttr.hatType.ordinal(),
            //       result.rcAttr.respiratorType.ordinal(),
            //       result.rcAttr.glassesType.ordinal(),
            //       result.rcAttr.skinColorType.ordinal())
            //     );
            //     }
            // }

            //FacePassRecognitionResult[] recognizeResult = mFacePassHandler.recognize(group_name, recognizeData.message, recognizeData.trackOpt);
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
                    int idx = findidx(ageGenderResult, result.trackId);
                    if (idx == -1) {
                      showRecognizeResult(result.trackId, result.detail.searchScore, result.detail.livenessScore, !TextUtils.isEmpty(faceToken));
                    } else {
                      showRecognizeResult(result.trackId, result.detail.searchScore, result.detail.livenessScore, !TextUtils.isEmpty(faceToken), ageGenderResult[idx].age, ageGenderResult[idx].gender);
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

  private void showRecognizeResult(final long trackId, final float searchScore, final float livenessScore, final boolean isRecognizeOK) {
    mAndroidHandler.post(new Runnable() {
      @Override
      public void run() {
        faceEndTextView.append("ID = " + trackId + (isRecognizeOK ? "识别成功" : "识别失败") + "\n");
        faceEndTextView.append("识别分 = " + searchScore + "\n");
        faceEndTextView.append("活体分 = " + livenessScore + "\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
      }
    });
  }

  private void showRecognizeResult(final long trackId, final float searchScore, final float livenessScore, final boolean isRecognizeOK, final float age, final int gender) {
    mAndroidHandler.post(new Runnable() {
      @Override
      public void run() {
        faceEndTextView.append("ID = " + trackId + (isRecognizeOK ? "识别成功" : "识别失败") + "\n");
        faceEndTextView.append("识别分 = " + searchScore + "\n");
        faceEndTextView.append("活体分 = " + livenessScore + "\n");
        faceEndTextView.append("年龄 = " + age + "\n");
        if (gender == 0) {
          faceEndTextView.append("性别 = " + "男" + "\n");
        } else if (gender == 1) {
          faceEndTextView.append("性别 = " + "女" + "\n");
        } else {
          faceEndTextView.append("性别 = " + "unknown" + "\n");
        }
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
      }
    });
  }

  /* 判断程序是否有所需权限 android22以上需要自申请权限 */
  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
        checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
        checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  /* 请求程序所需权限 */
  private void requestPermission() {
    Log.d(DEBUG_TAG, "requestPermission");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(Permission, PERMISSIONS_REQUEST);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      boolean granted = true;
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED)
          granted = false;
      }
      if (!granted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
          if (!shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
            || !shouldShowRequestPermissionRationale(PERMISSION_READ_STORAGE)
            || !shouldShowRequestPermissionRationale(PERMISSION_WRITE_STORAGE)
            || !shouldShowRequestPermissionRationale(PERMISSION_INTERNET)
            || !shouldShowRequestPermissionRationale(PERMISSION_ACCESS_NETWORK_STATE)) {
            Toast.makeText(getApplicationContext(), "需要开启摄像头网络文件存储权限", Toast.LENGTH_SHORT).show();
          }
      } else {
        try {
          initFacePassSDK();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void getFaceImageByFaceToken(final long trackId, String faceToken) {
    if (TextUtils.isEmpty(faceToken)) {
      return;
    }

    try {
      final Bitmap bitmap = mFacePassHandler.getFaceImage(faceToken.getBytes());
      Log.i(DEBUG_TAG, mFacePassHandler.getFaceImagePath(faceToken.getBytes()));
      mAndroidHandler.post(new Runnable() {
        @Override
        public void run() {
          Log.i(DEBUG_TAG, "getFaceImageByFaceToken cache is null");
          // showToast("ID = " + String.valueOf(trackId), Toast.LENGTH_SHORT, true, bitmap);
        }
      });
      if (bitmap != null) {
        return;
      }
    } catch (FacePassException e) {
      e.printStackTrace();
    }
  }

}

