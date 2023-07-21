import "package:facepass/camera/camera_viewer.dart";
import 'package:facepass/camera/facepass_layer.dart';
import "package:facepass/camera/top_image_viewer.dart";
import "package:flutter/material.dart";

class CameraScreen extends StatelessWidget {
  const CameraScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Stack(
      alignment: Alignment.center,
      children: [
        // Add camera layer here
        CameraViewer(),
        FacepassLayer(),
        TopImageViewer()
      ],
    );
  }
}
