package com.facepass.configuration;

import android.Manifest;

public class Config {
  // CHANNEL
  public static final String CHANNEL = "com.facepass/channel";

  // DEBUG TAG
  public static final String DEBUG_TAG = "facepass-java";

  // PERMISSION
  public static final int PERMISSIONS_REQUEST = 1;
  public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  public static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  public static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_MEDIA_IMAGES;
  public static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
  public static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
  public static String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};
  // public static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
}
