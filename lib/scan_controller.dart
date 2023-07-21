import 'dart:typed_data';
import 'package:camera/camera.dart';
import 'package:facepass/helpers/pass_face_data.dart';
import 'package:get/state_manager.dart';

import 'helpers/InititalizeSDK.dart';
// import 'package:image/image.dart' as imglib;

class ScanController extends GetxController {
  var channelName = "com.facepass/channel";

  final RxBool _isInitialized = RxBool(false);
  late CameraController _cameraController;
  late List<CameraDescription> _cameras;
  // late CameraImage _cameraImage;

  final RxList<Uint8List> _imageList = RxList([]);

  List<Uint8List> get imageList => _imageList;
  bool get isInitialized => _isInitialized.value;
  CameraController get cameraController => _cameraController;

  Future<void> _initCamera() async {
    _cameras = await availableCameras();
    /* Notes: Camera Sensor
      _cameras[0] -> Main camera sensor
      _cameras[1] -> Front camera sensor
      _cameras[2/3] -> Tele/Wide Angel camera sensor */
    _cameraController = CameraController(_cameras[0], ResolutionPreset.medium,
        imageFormatGroup: ImageFormatGroup.nv21);

    _cameraController.initialize().then((_) {
      _isInitialized.value = true;
      _cameraController.startImageStream(
          (image) => PassFaceData.call(channelName, "Ini Data"));
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
    InitializeSDK.call(channelName);
    _initCamera();
    super.onInit();
  }

  // Convert CameraImage (NV21 format) data to bytes array
  Uint8List convertCameraImageToBytes(CameraImage cameraImage) {
    int width = cameraImage.width;
    int height = cameraImage.height;

    // Plane 0 contains the Y (luminance) data
    Plane plane0 = cameraImage.planes[0];
    Uint8List bytesY = plane0.bytes;

    // Plane 1 contains the UV (chrominance) data
    Plane plane1 = cameraImage.planes[1];
    Uint8List bytesUV = plane1.bytes;

    // Calculate the size of the Y plane (luminance) and UV plane (chrominance)
    int ySize = width * height;
    int uvSize = plane1.bytesPerRow * height ~/ 2;

    // Create a new Uint8List for the output bytes array
    Uint8List bytes = Uint8List(ySize + uvSize);

    // Copy Y plane (luminance) data
    bytes.setRange(0, ySize, bytesY);

    // Rearrange UV plane (chrominance) data
    for (int i = 0; i < uvSize; i += 2) {
      bytes[ySize + i] = bytesUV[i]; // U
      bytes[ySize + i + 1] = bytesUV[i + 1]; // V
    }

    return bytes;
  }

  void capture() {}
}
