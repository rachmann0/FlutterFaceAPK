import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../scan_controller.dart';
import 'dart:math' as math;

class CameraViewer extends StatelessWidget {
  const CameraViewer({super.key});

  @override
  Widget build(BuildContext context) {
    return GetX<ScanController>(
      builder: (controller) {
        if (!controller.isInitialized) {
          return Container();
        }

        var camera = controller.cameraController.value;

        var tmp = MediaQuery.of(context).size;

        final screenH = math.max(tmp.height, tmp.width);
        final screenW = math.min(tmp.height, tmp.width);

        tmp = camera.previewSize!;

        final previewH = math.max(tmp.height, tmp.width);
        final previewW = math.min(tmp.height, tmp.width);
        final screenRatio = screenH / screenW;
        final previewRatio = previewH / previewW;

        return ClipRRect(
          child: OverflowBox(
            maxHeight: screenRatio > previewRatio
                ? screenH
                : screenW / previewW * previewH,
            maxWidth: screenRatio > previewRatio
                ? screenH / previewH * previewW
                : screenW,
            child: CameraPreview(
              controller.cameraController,
            ),
          ),
        );
      },
    );
  }
}
