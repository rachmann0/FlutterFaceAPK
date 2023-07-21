import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../scan_controller.dart';

class CameraViewer extends StatelessWidget {
  const CameraViewer({super.key});

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;

    return GetX<ScanController>(
      builder: (controller) {
        if (!controller.isInitialized) {
          return Container();
        }

        return ClipRect(
          child: Transform.scale(
            scale: controller.cameraController.value.aspectRatio *
                size.aspectRatio,
            child: Center(
              child: AspectRatio(
                aspectRatio: controller.cameraController.value.aspectRatio < 1 ? 1 / controller.cameraController.value.aspectRatio : controller.cameraController.value.aspectRatio,
                child: CameraPreview(controller.cameraController),
              ),
            ),
          ),
        );
      },
    );
  }
}
