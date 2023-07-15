package com.example.sdk_test;

import io.flutter.embedding.android.FlutterActivity;

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
    
    private static final String CHANNEL = "channelname";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(

/*
                        var args = call.arguments as Map<String, String>;
                        var message = args["message"];
*/

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

    //private void initFacePassSDK() throws IOException {
/*        Log.d(DEBUG_TAG, "initFacePassSDK");

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
                // ...
                return;
            }else {
                Log.d("mcvsafe", "Authentication result : success.");
            }
        } else {
            Log.d(DEBUG_TAG, "have no auth.");
            Log.d("FacePassDemo", "have no auth.");
            return ;
        }

        Log.d(DEBUG_TAG, "FacePassHandler.getVersion() = " + FacePassHandler.getVersion());*/
//    }

}
