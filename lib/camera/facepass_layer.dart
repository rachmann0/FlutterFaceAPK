import 'package:facepass/scan_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class FacepassLayer extends GetView<ScanController> {
  const FacepassLayer({super.key});

  @override
  Widget build(BuildContext context) {
    return Positioned(
        bottom: 30,
        child: GestureDetector(
          onTap: () {
            controller.capture();
          },
          child: Container(
            height: 80,
            width: 80,
            padding: const EdgeInsets.all(5),
            decoration: BoxDecoration(
              border: Border.all(
                color: Colors.white60,
                width: 5,
              ),
              shape: BoxShape.circle,
            ),
            child: Container(
              decoration: const BoxDecoration(
                  color: Colors.white, shape: BoxShape.circle),
              child: const Center(child: Icon(Icons.camera, size: 60)),
            ),
          ),
        ));
  }
}
