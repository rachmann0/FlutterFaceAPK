// package pluginid;

// import org.apache.cordova.CordovaPlugin;
// import org.apache.cordova.CallbackContext;
// import org.apache.cordova.PluginResult;

// import com.ys.rkapi.GPIOManager;
// import com.ys.rkapi.MyManager;

// import org.json.JSONArray;
// import org.json.JSONException;
// import org.json.JSONObject;

// import android.content.ContextWrapper;
// import android.app.Activity;
// import android.Manifest;
// import android.graphics.Bitmap;
// import android.graphics.BitmapFactory;
// import android.content.res.Configuration;
// import android.content.SharedPreferences;
// import android.graphics.ImageFormat;
// import android.graphics.SurfaceTexture;
// import android.hardware.Camera;
// import android.opengl.GLES32;
// import android.os.Bundle;
// import android.os.Build;
// import android.content.pm.PackageManager;
// import android.content.Context;
// import android.telecom.Call;
// import android.text.TextUtils;
// import android.util.Base64;
// import android.util.Log;
// import android.util.Pair;
// import android.view.WindowManager;

// import androidx.activity.result.contract.ActivityResultContracts;

// import java.io.IOException;
// import java.util.concurrent.ArrayBlockingQueue;

// import mcv.facepass.FacePassException;
// import mcv.facepass.FacePassHandler;
// import mcv.facepass.auth.AuthApi.AuthApi;
// import mcv.facepass.auth.AuthApi.AuthApplyResponse;
// import mcv.facepass.auth.AuthApi.ErrorCodeConfig;
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
// import pluginid.Camera.CameraManager;
// import pluginid.Camera.CameraPreviewData;
// import pluginid.Camera.SettingVar;

// /**
//  * This class echoes a string called from JavaScript.
//  */
// //public class CordovaFacePlugin extends CordovaPlugin implements CameraManager.CameraListener{
// public class CordovaFacePlugin extends CordovaPlugin {
//     private CameraManager manager;
// /*
//     */
// /* Camera callback function *//*

//     @Override
//     public void onPictureTaken(CameraPreviewData cameraPreviewData) {
//         Log.d(DEBUG_TAG, "onPictureTaken");
//         if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
//             //ComplexFrameHelper.addRgbFrame(cameraPreviewData);
//         } else {
//             mFeedFrameQueue.offer(cameraPreviewData);
//         }
//     }
// */


//     @Override
//     public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//         if (action.equals("initGPIOManager")) {
//             this.initGPIOManager(callbackContext);
//             return true;
//         }
//         if (action.equals("getRelayStatus")) {
//             this.getRelayStatus(callbackContext);
//             return true;
//         }
//         if (action.equals("pullUpRelay")) {
//             this.pullUpRelay(callbackContext);
//             return true;
//         }
//         if (action.equals("pullDownRelay")) {
//             this.pullDownRelay(callbackContext);
//             return true;
//         }
//         if (action.equals("checkPluginAvailable")) {
//             PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "message");
//             pluginResult.setKeepCallback(true);
//             callbackContext.sendPluginResult(pluginResult);
//             new java.util.Timer().schedule(new java.util.TimerTask(){
//                 @Override
//                 public void run(){
//                     callbackContext.sendPluginResult(pluginResult);
//                 }
//             }, 7000);
//             return true;

//         }
//         if (action.equals("initializeSDK")) {
//             this.initializeSDK(callbackContext);
//             return true;
//         }
//         if (action.equals("createGroup")) {
//             String groupName = args.getString(0);
//             createGroup(groupName, callbackContext);
//             return true;
//         }
//         if (action.equals("addFace")) {
//             String bitmapBase64 = args.getString(0);
//             addFace(bitmapBase64, callbackContext);
//             return true;
//         }
//         if (action.equals("bindGroupFaceToken")) {
//             String groupName = args.getString(0);
//             String faceTokenStr = args.getString(1);
//             bindGroupFaceToken(groupName, faceTokenStr, callbackContext);
//             return true;
//         }
//         return false;
//     }

//     private GPIOManager gpioManager;
//     private MyManager myManager;
//     private void initGPIOManager(CallbackContext callbackContext) {
//         myManager = MyManager.getInstance(cordova.getContext());
//         //gpioManager = GPIOManager.getInstance(cordova.getContext());
//         gpioManager = myManager.getGpioManager();
//         callbackContext.success("success, API Version: " + myManager.getApiVersion());
//     }
//     private void getRelayStatus(CallbackContext callbackContext) {
//         callbackContext.success(gpioManager.getRelayStatus());
//     }
//     private void pullUpRelay(CallbackContext callbackContext) {
//         gpioManager.pullUpRelay();
//         callbackContext.success(gpioManager.getRelayStatus());
//     }
//     private void pullDownRelay(CallbackContext callbackContext) {
//         gpioManager.pullDownRelay();
//         callbackContext.success(gpioManager.getRelayStatus());
//     }

//     ArrayBlockingQueue<RecognizeData> mRecognizeDataQueue;
//     private static boolean cameraFacingFront = true;
//     int screenState = 0;// 0 横 1 竖
//     private void initView() {
//         int windowRotation = ((WindowManager) (cordova.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
//         if (windowRotation == 0) {
//             cameraRotation = FacePassImageRotation.DEG90;
//         } else if (windowRotation == 90) {
//             cameraRotation = FacePassImageRotation.DEG0;
//         } else if (windowRotation == 270) {
//             cameraRotation = FacePassImageRotation.DEG180;
//         } else {
//             cameraRotation = FacePassImageRotation.DEG270;
//         }
//         Log.i(DEBUG_TAG, "Rotation: cameraRation: " + cameraRotation);
//         cameraFacingFront = true;
//         SharedPreferences preferences = cordova.getContext().getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
//         SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
//         SettingVar.isCross = preferences.getBoolean("isCross", SettingVar.isCross);
//         SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
//         SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
//         SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
//         if (SettingVar.isSettingAvailable) {
//             cameraRotation = SettingVar.faceRotation;
//             cameraFacingFront = SettingVar.cameraFacingFront;
//         }

//         Log.i(DEBUG_TAG, "Rotation: screenRotation: " + String.valueOf(windowRotation));
//         Log.i(DEBUG_TAG, "Rotation: faceRotation: " + SettingVar.faceRotation);
//         Log.i(DEBUG_TAG, "Rotation: new cameraRation: " + cameraRotation);
//         final int mCurrentOrientation = cordova.getContext().getResources().getConfiguration().orientation;

//         if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//             screenState = 1;
//         } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//             screenState = 0;
//         }
//         //cordova.getActivity().setContentView(R.layout.activity_main);

//         SettingVar.cameraSettingOk = false;
//         //manager = new CameraManager();
//         //cameraView = (CameraPreview) findViewById(R.id.preview);
//         //manager.setPreviewDisplay(cameraView);
//         /* 注册相机回调函数 */
//         //manager.setListener(this);
//     }

//     private void initializeSDK(CallbackContext callbackContext) {
//         Log.d(DEBUG_TAG, "initializeSDK: ");
// /*
//         mImageCache = new FaceImageCache();
// */
//         mRecognizeDataQueue = new ArrayBlockingQueue<RecognizeData>(5);
//         mFeedFrameQueue = new ArrayBlockingQueue<CameraPreviewData>(1);

//         //initView();
//         if (!hasPermission()) {
//           requestPermission();
//         } else {
//           try {
//             initFacePassSDK(callbackContext);
//           } catch (IOException e) {
//             e.printStackTrace();
//           }
//         }

//         // initFaceHandler(callbackContext);
//         initFaceHandler(callbackContext);
//         //callbackContext.error("Expected one non-empty string argument.");

//         recognizeThreadCallbackContext = callbackContext;

//         RecognizeThread mRecognizeThread;
//         FeedFrameThread mFeedFrameThread;

//         mFeedFrameThread = new FeedFrameThread();
//         mFeedFrameThread.start();

//         mRecognizeThread = new RecognizeThread();
//         mRecognizeThread.start();

//     }

//     ArrayBlockingQueue<CameraPreviewData> mFeedFrameQueue;
//     private enum FacePassSDKMode {
//         MODE_ONLINE,
//         MODE_OFFLINE
//     }
//     private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;

//     private static final String TAG = "MyActivity";
//     private static final String DEBUG_TAG = "FacePassDemo";
//     private boolean ageGenderEnabledGlobal;
//     private enum FacePassCameraType{
//         FACEPASS_SINGLECAM,
//         FACEPASS_DUALCAM
//     };
//     private static FacePassCameraType CamType = FacePassCameraType.FACEPASS_SINGLECAM;
//     private static final int PERMISSIONS_REQUEST = 1;
//     private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
//     private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
// /*
//     private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
// */
//     private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_MEDIA_IMAGES;
//     private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
//     private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
// /*
//     private String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};
// */
//     private String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};

//     /* SDK 实例对象 */
//     FacePassHandler mFacePassHandler;

//     private boolean hasPermission() {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             return cordova.getActivity().checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
// /*
//                     checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                     checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
// */
//                     cordova.getActivity().checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
//                     cordova.getActivity().checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
//         } else {
//             return true;
//         }
//     }
//     private void requestPermission() {
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             cordova.getActivity().requestPermissions(Permission, PERMISSIONS_REQUEST);
//         }
//     }

//     public static final String CERT_PATH = "Cert/CBG_Android_Face_Reco---30-Trial-one-stage.cert";
//     private void singleCertification(Context mContext) throws IOException {
// /*
//         String cert = FileUtil.readExternal(CERT_PATH).trim();
// */
//         String cert = "\"{\"\"serial\"\":\"\"z0005a8759f61d5f4b2862852034c139ddada\"\",\"\"key\"\":\"\"2a6a6e824b1bfb87553faecb38faf4122936055c915fb3ac814c8879994d1542f304999e02ec2ff25d278b110695b76980b3002d57d2f4f20d779f2ffc95e1bac4ff713f244ad0d7da10a0491ee0fbfce6c9ee0f4a8fd42f0fb17ef56070773c73272014a60096f06154620fa427ea3b0dbace0ec3d7a9b59e4cb9775da41275d6fe6b904539f59910ad012bc89dc86d3fd43af436040a036375767226261a30e9d05e87c89f821b9875da230409f7d66748bcfc9f8281cf802305a8664739f3354a3d13565b16ce\"\"}\"\n".trim();
//         if(TextUtils.isEmpty(cert)){
//             Log.d("mcvsafe", "cert is null");
//             return;
//         }
//         final AuthApplyResponse[] resp = {new AuthApplyResponse()};
//         FacePassHandler.authDevice(mContext.getApplicationContext(), cert, "", new AuthApi.AuthDeviceCallBack() {
//             @Override
//             public void GetAuthDeviceResult(AuthApplyResponse result) {
//                 resp[0] = result;
//                 if (resp[0].errorCode == ErrorCodeConfig.AUTH_SUCCESS) {
//                     try {
//                         cordova.getActivity().runOnUiThread(new Runnable() {
//                             @Override
//                             public void run() {
//                                 Log.d("mcvsafe", "Apply update: OK");
//                             }

//                         });
//                     } catch (Throwable throwable) {
//                         throwable.printStackTrace();
//                     }
//                 } else {
//                     try {
//                         cordova.getActivity().runOnUiThread(new Runnable() {
//                             @Override
//                             public void run() {
//                                 Log.d("mcvsafe", "Apply update: error. error code is: " + resp[0].errorCode + " error message: " + resp[0].errorMessage);
//                             }
//                         });
//                     } catch (Throwable throwable) {
//                         throwable.printStackTrace();
//                     }
//                 }
//             }
//         });
//     }
//     private static final String authIP = "https://api-cn.faceplusplus.com";
//     public static final String apiKey = "";
//     public static final String apiSecret = "";
//     private enum FacePassAuthType{
//         FASSPASS_AUTH_MCVFACE,
//         FACEPASS_AUTH_MCVSAFE
//     };
//     private static FacePassAuthType authType = FacePassAuthType.FACEPASS_AUTH_MCVSAFE;
//     private void initFacePassSDK(CallbackContext callbackContext) throws IOException {
//         Context mContext = cordova.getContext().getApplicationContext();
//         FacePassHandler.initSDK(mContext);
//         if (authType == FacePassAuthType.FASSPASS_AUTH_MCVFACE) {
//             // face++授权
//             FacePassHandler.authPrepare(cordova.getContext().getApplicationContext());
//             FacePassHandler.getAuth(authIP, apiKey, apiSecret, true);
//         } else if (authType == FacePassAuthType.FACEPASS_AUTH_MCVSAFE) {
//             Log.d(DEBUG_TAG, "authType = FACEPASS_AUTH_MCVSAFE");
//             // 金雅拓授权接口
//             boolean auth_status = FacePassHandler.authCheck();
//             Log.d(DEBUG_TAG, "FacePassHandler.authCheck(): " + FacePassHandler.authCheck());
//             Log.d(DEBUG_TAG, "FacePassHandler.isAuthorized(): " + FacePassHandler.isAuthorized());
//             if ( !auth_status ) {
//                 singleCertification(mContext);
//                 auth_status = FacePassHandler.authCheck();
//             }

//             if ( !auth_status ) {
//                 Log.d(DEBUG_TAG, "Authentication result : failed.");
//                 Log.d("mcvsafe", "Authentication result : failed.");
//                 PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Authentication result : failed.");
//                 pluginResult.setKeepCallback(true);
//                 callbackContext.sendPluginResult(pluginResult);
//                 // 授权不成功，根据业务需求处理
//                 // ...
//                 return;
//             } else {
//                 Log.d("mcvsafe", "Authentication result : success.");
//                 PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "Authentication result : success.");
//                 pluginResult.setKeepCallback(true);
//                 callbackContext.sendPluginResult(pluginResult);
//                 return;
//             }
//         } else {
//             Log.d(DEBUG_TAG, "have no auth.");
//             Log.d("FacePassDemo", "have no auth.");
//             return ;
//         }

//         Log.d(DEBUG_TAG, "FacePassHandler.getVersion() = " + FacePassHandler.getVersion());
//     }

//     private void initFaceHandler(CallbackContext callbackContext) {
//         Log.d(DEBUG_TAG, "initFaceHandler");
//         new Thread() {
//             @Override
//             public void run() {
//                 while (true && !cordova.getActivity().isFinishing()) {
// //                while (!isFinishing()) {
//                     //Log.d(DEBUG_TAG, "FacePassHandler.isAvailable() = " + String.valueOf(FacePassHandler.isAvailable()));
//                     while (FacePassHandler.isAvailable()) {
//                         Log.d(DEBUG_TAG, "start to build FacePassHandler");
//                         FacePassConfig config;
//                         try {

//                             config = new FacePassConfig();
//                             config.poseBlurModel = FacePassModel.initModel(cordova.getActivity().getApplicationContext().getAssets(), "attr.pose_blur.arm.190630.bin");

//                             config.livenessModel = FacePassModel.initModel(cordova.getActivity().getApplicationContext().getAssets(), "liveness.CPU.rgb.G.bin");
//                             if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
//                                 config.rgbIrLivenessModel = FacePassModel.initModel(cordova.getActivity().getApplicationContext().getAssets(), "liveness.CPU.rgbir.I.bin");
//                                 // 真假人同屏模型
//                                 config.rgbIrGaLivenessModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "liveness.CPU.rgbir.ga_case.A.bin");
//                                 // 若需要使用GPU模型则加载以下模型文件
//                                 config.livenessGPUCache = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "liveness.GPU.rgbir.I.cache");
//                                 config.rgbIrLivenessGpuModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "liveness.GPU.rgbir.I.bin");
//                                 config.rgbIrGaLivenessGpuModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "liveness.GPU.rgbir.ga_case.A.bin");
//                             }

//                             config.searchModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "feat2.arm.K.v1.0_1core.bin");

//                             config.detectModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "detector.arm.G.bin");
//                             config.detectRectModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "detector_rect.arm.G.bin");
//                             config.landmarkModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "pf.lmk.arm.E.bin");

//                             config.rcAttributeModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "attr.RC.arm.G.bin");
//                             config.occlusionFilterModel = FacePassModel.initModel(cordova.getContext().getApplicationContext().getAssets(), "attr.occlusion.arm.20201209.bin");
//                             //config.smileModel = FacePassModel.initModel(getApplicationContext().getAssets(), "attr.RC.arm.200815.bin");


//                             config.rcAttributeAndOcclusionMode = 1;
//                             config.searchThreshold = 65f;
//                             config.livenessThreshold = 80f;
//                             config.livenessGaThreshold = 85f;
//                             if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
//                                 config.livenessEnabled = false;
//                                 config.rgbIrLivenessEnabled = true;      // 启用双目活体功能(默认CPU)
//                                 config.rgbIrLivenessGpuEnabled = true;   // 启用双目活体GPU功能
//                                 config.rgbIrGaLivenessEnabled = true;    // 启用真假人同屏功能(默认CPU)
//                                 config.rgbIrGaLivenessGpuEnabled = true; // 启用真假人同屏GPU功能
//                             } else {
//                                 config.livenessEnabled = true;
//                                 config.rgbIrLivenessEnabled = false;
//                             }

//                             ageGenderEnabledGlobal = (config.ageGenderModel != null);

//                             config.poseThreshold = new FacePassPose(35f, 35f, 35f);
//                             config.blurThreshold = 0.8f;
//                             config.lowBrightnessThreshold = 30f;
//                             config.highBrightnessThreshold = 210f;
//                             config.brightnessSTDThreshold = 80f;
//                             config.faceMinThreshold = 60;
//                             config.retryCount = 10;
//                             config.smileEnabled = false;
//                             config.maxFaceEnabled = true;
//                             config.fileRootPath = cordova.getContext().getExternalFilesDir("Download").getAbsolutePath();
// /*
//                             config.fileRootPath = "/storage/emulated/0/Android/dataHelloCordova/files/Download";
// */


//                             mFacePassHandler = new FacePassHandler(config);


//                             FacePassConfig addFaceConfig = mFacePassHandler.getAddFaceConfig();
//                             addFaceConfig.poseThreshold.pitch = 35f;
//                             addFaceConfig.poseThreshold.roll = 35f;
//                             addFaceConfig.poseThreshold.yaw = 35f;
//                             addFaceConfig.blurThreshold = 0.7f;
//                             addFaceConfig.lowBrightnessThreshold = 70f;
//                             addFaceConfig.highBrightnessThreshold = 220f;
//                             addFaceConfig.brightnessSTDThresholdLow = 14.14f;
//                             addFaceConfig.brightnessSTDThreshold = 63.25f;
//                             addFaceConfig.faceMinThreshold = 100;
//                             addFaceConfig.rcAttributeAndOcclusionMode = 2;
//                             mFacePassHandler.setAddFaceConfig(addFaceConfig);

//                             checkGroup();



//                         } catch (FacePassException e) {
//                             e.printStackTrace();
// //                            Log.d(DEBUG_TAG, "FacePassHandler is null");
//                             // Log.d(DEBUG_TAG, "FacePassHandler is null: " + e.getMessage());
//                             callbackContext.error("FacePassHandler is null: " + e.getMessage());
//                             return;
//                         }
//                         // Log.d(DEBUG_TAG, "SDK successfully initialized");
//                         callbackContext.success("SDK successfully initialized");
//                         return;
//                     }
//                     try {

//                         sleep(500);
//                     } catch (InterruptedException e) {
//                         e.printStackTrace();
//                     }
//                 }
//             }
//         }.start();
//     }

//     private boolean isLocalGroupExist = false;
//     private static final String group_name = "facepass";
//     private void checkGroup() {
//         Log.d(DEBUG_TAG, "checkGroup");
//         if (mFacePassHandler == null) {
//             return;
//         }
//         try {
//             String[] localGroups = mFacePassHandler.getLocalGroups();
//             isLocalGroupExist = false;
//             if (localGroups == null || localGroups.length == 0) {
//                 Log.d(DEBUG_TAG, group_name);
//                 return;
//             }
//             for (String group : localGroups) {
//                 if (group_name.equals(group)) {
//                     isLocalGroupExist = true;
//                 }
//             }
//             if (!isLocalGroupExist) {
//                 Log.d(DEBUG_TAG, group_name);
//             }
//         } catch (FacePassException e) {
//             e.printStackTrace();
//         }
//     }

//     public void createGroup(String groupName, CallbackContext callbackContext) {
//         if (mFacePassHandler == null) {
//             Log.d(DEBUG_TAG, "FacePassHandle is null ! ");
//             return;
//         }
//         if (TextUtils.isEmpty(groupName)) {
//             Log.d(DEBUG_TAG,"please input group name ！");
//             return;
//         }
//         boolean isSuccess = false;
//         try {
//             isSuccess = mFacePassHandler.createLocalGroup(groupName);
//         } catch (FacePassException e) {
//             e.printStackTrace();
//         }
//         Log.d(DEBUG_TAG,"create group " + isSuccess);
//         callbackContext.success("create group " + groupName + " " + isSuccess);
//         if (isSuccess && group_name.equals(groupName)) {
//             isLocalGroupExist = true;
//         }

//     }

//     private static byte[] faceToken;
//     private static byte[] faceData;
//     private static Bitmap faceBitmap;
//     public void addFace(String bitmapBase64, CallbackContext callbackContext) {
//         byte[] decodedString = Base64.decode(bitmapBase64, Base64.DEFAULT);
//         Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//         faceData = decodedString;
//         faceBitmap = bitmap;

//         if (mFacePassHandler == null) {
//             Log.d(DEBUG_TAG,"FacePassHandle is null !");
//             callbackContext.error("FacePassHandle is null !");
//             return;
//         }

//         try {
//             FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);
//             if (result != null) {
//                 if (result.result == 0) {
//                     android.util.Log.d("qujiaqi", "result:" + result
//                             + ",bl:" + result.blur
//                             + ",pp:" + result.pose.pitch
//                             + ",pr:" + result.pose.roll
//                             + ",py" + result.pose.yaw);
//                     callbackContext.success(new String(result.faceToken));
//                     faceToken = result.faceToken;
//                     Log.d(DEBUG_TAG, "add face successfully! ");
//                     Log.d(DEBUG_TAG, new String(result.faceToken));

//                 } else if (result.result == 1) {
//                     callbackContext.success("no face ！");
//                 } else {
//                     callbackContext.success("quality problem！");
//                 }
//             }
//         } catch (FacePassException e) {
//             e.printStackTrace();
//             callbackContext.error(e.getMessage());
//         }
//     }

//     public void bindGroupFaceToken(String groupName, String faceTokenStr, CallbackContext callbackContext) {
//         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, groupName + " " + faceTokenStr);
//         pluginResult.setKeepCallback(true);
//         callbackContext.sendPluginResult(pluginResult);

//         byte[] faceToken = faceTokenStr.trim().getBytes();

//         if (mFacePassHandler == null) {
//             callbackContext.error("FacePassHandle is null ! ");
//             return;
//         }

//         if (faceToken == null || faceToken.length == 0 || TextUtils.isEmpty(groupName)) {
//             callbackContext.error("params error！");
//             return;
//         }
//         try {
//             boolean b = mFacePassHandler.bindGroup(groupName, faceToken);
//             String result = b ? "success " : "failed";
//             callbackContext.success("bind  " + result);
//         } catch (Exception e) {
//             e.printStackTrace();
//             callbackContext.error(e.getMessage());
//         }


//     }

//     public class RecognizeData {
//         public byte[] message;
//         public FacePassTrackOptions[] trackOpt;

//         public RecognizeData(byte[] message) {
//             this.message = message;
//             this.trackOpt = null;
//         }

//         public RecognizeData(byte[] message, FacePassTrackOptions[] opt) {
//             this.message = message;
//             this.trackOpt = opt;
//         }
//     }
//     public CallbackContext recognizeThreadCallbackContext;
//     private class RecognizeThread extends Thread {
//         boolean isInterrupt;

//         @Override
//         public void run() {
//             while (!isInterrupt) {
//                 try {
//                     PluginResult pluginResultRun = new PluginResult(PluginResult.Status.OK, "run recognize thread: " + mRecognizeDataQueue.isEmpty());
//                     pluginResultRun.setKeepCallback(true);
//                     recognizeThreadCallbackContext.sendPluginResult(pluginResultRun);

//                     RecognizeData recognizeData = mRecognizeDataQueue.take();
//                     FacePassAgeGenderResult[] ageGenderResult = null;
//                     //if (ageGenderEnabledGlobal) {
//                     //    ageGenderResult = mFacePassHandler.getAgeGender(detectionResult);
//                     //    for (FacePassAgeGenderResult t : ageGenderResult) {
//                     //        Log.e("FacePassAgeGenderResult", "id " + t.trackId + " age " + t.age + " gender " + t.gender);
//                     //    }
//                     //}

//                     if (isLocalGroupExist) {
//                         Log.d(DEBUG_TAG, "RecognizeData >>>>");
//                         PluginResult pluginResultLocalGroup = new PluginResult(PluginResult.Status.OK, "isLocalGroupExist");
//                         pluginResultLocalGroup.setKeepCallback(true);
//                         recognizeThreadCallbackContext.sendPluginResult(pluginResultLocalGroup);

//                         FacePassRecognitionResult[][] recognizeResultArray = mFacePassHandler.recognize(group_name, recognizeData.message, 1, recognizeData.trackOpt);
//                         if (recognizeResultArray != null && recognizeResultArray.length > 0) {
//                             for (FacePassRecognitionResult[] recognizeResult : recognizeResultArray) {
//                                 if (recognizeResult != null && recognizeResult.length > 0) {
//                                     for (FacePassRecognitionResult result : recognizeResult) {
//                                         String faceToken = new String(result.faceToken);
//                                         if (FacePassRecognitionState.RECOGNITION_PASS == result.recognitionState) {
//                                             getFaceImageByFaceToken(result.trackId, faceToken);
//                                             Log.i(DEBUG_TAG, "SUCCESSFULLY RECOGNIZED");
//                                             PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "SUCCESSFULLY RECOGNIZED");
//                                             pluginResult.setKeepCallback(true);
//                                             recognizeThreadCallbackContext.sendPluginResult(pluginResult);
//                                         } else {
//                                             Log.i(DEBUG_TAG, "FAILED TO RECOGNIZE");
//                                             PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "FAILED TO RECOGNIZE");
//                                             pluginResult.setKeepCallback(true);
//                                             recognizeThreadCallbackContext.sendPluginResult(pluginResult);
//                                         }
//                                         int idx = findidx(ageGenderResult, result.trackId);
//                                         if (idx == -1) {
// /*
//                                             showRecognizeResult(result.trackId, result.detail.searchScore, result.detail.livenessScore, !TextUtils.isEmpty(faceToken));
// */
//                                         } else {
// /*
//                                             showRecognizeResult(result.trackId, result.detail.searchScore, result.detail.livenessScore, !TextUtils.isEmpty(faceToken), ageGenderResult[idx].age, ageGenderResult[idx].gender);
// */
//                                         }

//                                         Log.d(DEBUG_TAG, String.format("recognize trackid: %d, searchScore: %f  searchThreshold: %f, hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
//                                                 result.trackId,
//                                                 result.detail.searchScore,
//                                                 result.detail.searchThreshold,
//                                                 result.detail.rcAttr.hairType.ordinal(),
//                                                 result.detail.rcAttr.beardType.ordinal(),
//                                                 result.detail.rcAttr.hatType.ordinal(),
//                                                 result.detail.rcAttr.respiratorType.ordinal(),
//                                                 result.detail.rcAttr.glassesType.ordinal(),
//                                                 result.detail.rcAttr.skinColorType.ordinal()));
//                                     }
//                                 }
//                             }
//                         }
//                     }
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 } catch (FacePassException e) {
//                     e.printStackTrace();
//                 }
//             }
//         }

//         @Override
//         public void interrupt() {
//             isInterrupt = true;
//             super.interrupt();
//         }
//     }
//     int findidx(FacePassAgeGenderResult[] results, long trackId) {
//         int result = -1;
//         if (results == null) {
//             return result;
//         }
//         for (int i = 0; i < results.length; ++i) {
//             if (results[i].trackId == trackId) {
//                 return i;
//             }
//         }
//         return result;
//     }

//     private void getFaceImageByFaceToken(final long trackId, String faceToken) {
//         if (TextUtils.isEmpty(faceToken)) {
//             return;
//         }

//         try {
//             final Bitmap bitmap = mFacePassHandler.getFaceImage(faceToken.getBytes());
//             Log.i(DEBUG_TAG, mFacePassHandler.getFaceImagePath(faceToken.getBytes()));
// /*
//             mAndroidHandler.post(new Runnable() {
//                 @Override
//                 public void run() {
//                     Log.i(DEBUG_TAG, "getFaceImageByFaceToken cache is null");
//                     showToast("ID = " + String.valueOf(trackId), Toast.LENGTH_SHORT, true, bitmap);
//                 }
//             });
// */
//             if (bitmap != null) {
//                 return;
//             }
//         } catch (FacePassException e) {
//             e.printStackTrace();
//         }
//     }

//     private int cameraRotation;
//     private static ArrayBlockingQueue<Pair<CameraPreviewData, CameraPreviewData>> complexFrameQueue
//             = new ArrayBlockingQueue<>(2);
//     public static Pair<CameraPreviewData, CameraPreviewData> takeComplexFrame() throws InterruptedException {
//         return complexFrameQueue.take();
//     }
//     protected boolean front = false;
//     private int previewDegreen = 0;
//     private class FeedFrameThread extends Thread {
//         boolean isInterrupt;

//         @Override
//         public void run() {
//             while (!isInterrupt) {
//                 if (mFacePassHandler == null) {
//                     continue;
//                 }

//                 // PluginResult pluginResultRun = new PluginResult(PluginResult.Status.OK, "picture-taken");
//                 // pluginResultRun.setKeepCallback(true);
//                 // recognizeThreadCallbackContext.sendPluginResult(pluginResultRun);
//                 Camera mCamera = Camera.open();
//                 try {
//                     mCamera.setPreviewTexture(new SurfaceTexture(10));
//                 } catch (IOException e1) {
//                     Log.e(DEBUG_TAG, e1.getMessage());
//                 }

//                 Camera.Parameters params = mCamera.getParameters();
//                 params.setPreviewSize(640, 480);
//                 params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                 //params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//                 //params.setPictureFormat(ImageFormat.JPEG);
//                 params.setPreviewFormat(ImageFormat.NV21);
//                 mCamera.setParameters(params);
//                 mCamera.startPreview();
//                 Camera.PictureCallback pictureCallback =  new Camera.PictureCallback() {
//                     @Override
//                     public void onPictureTaken(byte[] data, Camera camera) {
//                         Log.i(DEBUG_TAG, "picture-taken");
//                         PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "picture-taken");
//                         pluginResult.setKeepCallback(true);
//                         recognizeThreadCallbackContext.sendPluginResult(pluginResult);
//  /*
//                          cameraPreviewData = new CameraPreviewData(data, 640, 480,
//                                  previewDegreen, front););
//  */
//                         CameraPreviewData cameraPreviewData = new CameraPreviewData(data, 640, 480, previewDegreen, front);
//                         mFeedFrameQueue.offer(cameraPreviewData);
//                     }
//                  };
//                 //mCamera.takePicture(null, pictureCallback, null);
//                 mCamera.takePicture(null, null, pictureCallback);


//                 // if (faceData != null) {
//                 // CameraPreviewData cameraPreviewData1 = new CameraPreviewData(faceData, 1700,2267, previewDegreen, front);
//                 // mFeedFrameQueue.offer(cameraPreviewData1);
//                 // }

//                 // PluginResult pluginResultRun = new PluginResult(PluginResult.Status.OK, "FeedFrameThread run");
//                 // pluginResultRun.setKeepCallback(true);
//                 // recognizeThreadCallbackContext.sendPluginResult(pluginResultRun);

//                 /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
//                 long startTime = System.currentTimeMillis(); //起始时间

//                 /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
//                 FacePassDetectionResult detectionResult = null;
//                 try {
//                     if (CamType == FacePassCameraType.FACEPASS_DUALCAM) {
//                         Pair<CameraPreviewData, CameraPreviewData> framePair;
//                         try {
//                             framePair = takeComplexFrame();
//                         } catch (InterruptedException e) {
//                             e.printStackTrace();
//                             continue;
//                         }
//                         FacePassImage imageRGB = new FacePassImage(framePair.first.nv21Data, framePair.first.width, framePair.first.height, cameraRotation, FacePassImageType.NV21);
//                         FacePassImage imageIR = new FacePassImage(framePair.second.nv21Data, framePair.second.width, framePair.second.height, cameraRotation, FacePassImageType.NV21);
//                         detectionResult = mFacePassHandler.feedFrameRGBIR(imageRGB, imageIR);
//                     } else {
//                         CameraPreviewData cameraPreviewData = null;
//                         try {
//                             cameraPreviewData = mFeedFrameQueue.take();
//                         } catch (InterruptedException e) {
//                             e.printStackTrace();
//                             continue;
//                         }
//                         FacePassImage imageRGB = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, cameraRotation, FacePassImageType.NV21);
//                         detectionResult = mFacePassHandler.feedFrame(imageRGB);
//                     }
//                 } catch (FacePassException e) {
//                     e.printStackTrace();
//                 }

//                 if (detectionResult == null || detectionResult.faceList.length == 0) {
//                     /* 当前帧没有检出人脸 */
//                     cordova.getActivity().runOnUiThread(new Runnable() {
//                         @Override
//                         public void run() {
// /*
//                             faceView.clear();
//                             faceView.invalidate();
// */
//                         }
//                     });
//                 } else {
//                     /* 将识别到的人脸在预览界面中圈出，并在上方显示人脸位置及角度信息 */
//                     final FacePassFace[] bufferFaceList = detectionResult.faceList;
//                     cordova.getActivity().runOnUiThread(new Runnable() {
//                         @Override
//                         public void run() {
// /*
//                             showFacePassFace(bufferFaceList);
// */
//                         }
//                     });
//                 }

//                 if (SDK_MODE == FacePassSDKMode.MODE_OFFLINE) {
//                     /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
//                     if (detectionResult != null && detectionResult.message.length != 0) {
//                         Log.d(DEBUG_TAG, "mRecognizeDataQueue.offer");
//                         /*所有检测到的人脸框的属性信息*/
//                         for (int i = 0; i < detectionResult.faceList.length; ++i) {
//                             Log.d(DEBUG_TAG, String.format("rc attribute faceList hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
//                                     detectionResult.faceList[i].rcAttr.hairType.ordinal(),
//                                     detectionResult.faceList[i].rcAttr.beardType.ordinal(),
//                                     detectionResult.faceList[i].rcAttr.hatType.ordinal(),
//                                     detectionResult.faceList[i].rcAttr.respiratorType.ordinal(),
//                                     detectionResult.faceList[i].rcAttr.glassesType.ordinal(),
//                                     detectionResult.faceList[i].rcAttr.skinColorType.ordinal()));
//                         }
//                         Log.d(DEBUG_TAG, "--------------------------------------------------------------------------------------------------------------------------------------------------");
//                         /*送识别的人脸框的属性信息*/
//                         FacePassTrackOptions[] trackOpts = new FacePassTrackOptions[detectionResult.images.length];
//                         for (int i = 0; i < detectionResult.images.length; ++i) {
//                             if (detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.INVALID
//                                     && detectionResult.images[i].rcAttr.respiratorType != FacePassRCAttribute.FacePassRespiratorType.NO_RESPIRATOR) {
//                                 float searchThreshold = 60f;
//                                 float livenessThreshold = 80f; // -1.0f will not change the liveness threshold
//                                 float livenessGaThreshold = 85f;
//                                 float smallsearchThreshold = -1.0f; // -1.0f will not change the smallsearch threshold
//                                 trackOpts[i] = new FacePassTrackOptions(detectionResult.images[i].trackId, searchThreshold, livenessThreshold, livenessGaThreshold, smallsearchThreshold);
//                             }
//                             Log.d(DEBUG_TAG, String.format("rc attribute in FacePassImage, hairType: 0x%x beardType: 0x%x hatType: 0x%x respiratorType: 0x%x glassesType: 0x%x skinColorType: 0x%x",
//                                     detectionResult.images[i].rcAttr.hairType.ordinal(),
//                                     detectionResult.images[i].rcAttr.beardType.ordinal(),
//                                     detectionResult.images[i].rcAttr.hatType.ordinal(),
//                                     detectionResult.images[i].rcAttr.respiratorType.ordinal(),
//                                     detectionResult.images[i].rcAttr.glassesType.ordinal(),
//                                     detectionResult.images[i].rcAttr.skinColorType.ordinal()));
//                         }
//                         RecognizeData mRecData = new RecognizeData(detectionResult.message, trackOpts);
//                         mRecognizeDataQueue.offer(mRecData);
//                     }
//                 }
//                 long endTime = System.currentTimeMillis(); //结束时间
//                 long runTime = endTime - startTime;
//                 for (int i = 0; i < detectionResult.faceList.length; ++i) {
//                     Log.i("DEBUG_TAG", "rect[" + i + "] = (" + detectionResult.faceList[i].rect.left + ", " + detectionResult.faceList[i].rect.top + ", " + detectionResult.faceList[i].rect.right + ", " + detectionResult.faceList[i].rect.bottom);
//                 }
//                 Log.i("]time", String.format("feedframe %d ms", runTime));
//                 PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, String.format("feedframe %d ms", runTime));
//                 pluginResult.setKeepCallback(true);
//                 recognizeThreadCallbackContext.sendPluginResult(pluginResult);

//             }
//         }

//         @Override
//         public void interrupt() {
//             isInterrupt = true;
//             super.interrupt();
//         }
//     }

// }