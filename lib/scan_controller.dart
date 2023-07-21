import 'dart:typed_data';
import 'package:camera/camera.dart';
import 'package:get/state_manager.dart';
// import 'package:image/image.dart' as imglib;

class ScanController extends GetxController {
  final RxBool _isInitialized = RxBool(false);
  late CameraController _cameraController;
  late List<CameraDescription> _cameras;
  late CameraImage _cameraImage;

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
    _cameraController = CameraController(
      _cameras[0], 
      ResolutionPreset.max,
      imageFormatGroup: ImageFormatGroup.nv21
    );

    _cameraController.initialize().then((_) {

      _isInitialized.value = true;
      _cameraController.startImageStream((image) => _cameraImage = image);

    }).catchError((Object e) {
      if (e is CameraException) {
        switch (e.code) {
          case 'CameraAccessDenied':
            print("User denied camera access");
            break;
          default:
            print("Others camera errors");
            break;
        }
      }
    });
  }

  @override
  void onInit() {
    _initCamera();
    super.onInit();
  }

  void capture() {
    print(_cameraImage.format);
    print(_cameraImage.width);
    print(_cameraImage.height);
    print(_cameraImage.planes[0].bytes);
  }
}
