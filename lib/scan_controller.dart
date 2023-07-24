import 'dart:typed_data';
import 'package:camera/camera.dart';
import 'package:facepass/helpers/pass_face_data.dart';
import 'package:get/state_manager.dart';

import 'helpers/inititalizeAPK.dart';
// import 'package:image/image.dart' as imglib;

class ScanController extends GetxController {
  final String _channelName = "com.facepass/channel";

  final RxBool _isInitialized = RxBool(false);
  late CameraController _cameraController;
  late List<CameraDescription> _cameras;
  // late CameraImage _cameraImage;
  String _groupName = "";
  String _faceToken = "";

  final RxList<Uint8List> _imageList = RxList([]);

  String get faceToken => _faceToken;
  String get groupName => _groupName;
  String get channelName => _channelName;
  List<Uint8List> get imageList => _imageList;
  bool get isInitialized => _isInitialized.value;
  CameraController get cameraController => _cameraController;

  Future<void> _initCamera() async {
    _cameras = await availableCameras();
    /* Notes: Camera Sensor
      _cameras[0] -> Main camera sensor
      _cameras[1] -> Front camera sensor
      _cameras[2/3] -> Tele/Wide Angel camera sensor */
    _cameraController = CameraController(_cameras[1], ResolutionPreset.medium,
        imageFormatGroup: ImageFormatGroup.nv21);

    _cameraController.initialize().then((_) {
      _isInitialized.value = true;
      _cameraController.startImageStream((image) => capture(image));
    }).catchError((Object e) {
      if (e is CameraException) {
        switch (e.code) {
          case 'CameraAccessDenied':
            print("User denied camera access");
            break;
          default:
            print("Others camera error");
            break;
        }
      }
    });
  }

  @override
  void onInit() {
    InitializeAPK.call(channelName);
    _initCamera();
    super.onInit();
  }

  @override
  void dispose() {
    _isInitialized.value = false;
    _cameraController.dispose();
    super.dispose();
  }

  void capture(CameraImage cameraImage) {
    PassFaceData.call(channelName, cameraImage.planes[0].bytes,
        cameraImage.width, cameraImage.height);
  }

  void setGroupName(String groupName) {
    _groupName = groupName;
  }

  void setFaceToken(String faceToken) {
    _faceToken = faceToken;
  }
}
