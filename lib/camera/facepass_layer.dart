import 'package:facepass/helpers/add_face.dart';
import 'package:facepass/scan_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class FacepassLayer extends GetView<ScanController> {
  const FacepassLayer({super.key});

  @override
  Widget build(BuildContext context) {
    return Positioned(
      bottom: 30,
      child: Row(children: [
        GestureDetector(
          onTap: () {
            // controller.capture();
            AddFace.call("com.facepass/channel");
          },
          child: Container(
            height: 80,
            width: 80,
            padding: const EdgeInsets.all(5),
            decoration: BoxDecoration(
              border: Border.all(
                color: Colors.red,
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
        ),
        GestureDetector(
          onTap: () {
            // controller.capture();
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
        ),
      ]),
    );

    // return Positioned(
    //   bottom: 30,
    //   child: Stack(
    //     children: <Widget>[
    //       Container(
    //         margin: const EdgeInsets.only(top: 10),
    //         child: ButtonTheme(
    //           child: Padding(
    //             padding: const EdgeInsets.only(left: 20, right: 20),
    //             child: ElevatedButton(
    //               style: ElevatedButton.styleFrom(
    //                 minimumSize: const Size.fromHeight(50),
    //                 shape: RoundedRectangleBorder(
    //                   borderRadius: BorderRadius.circular(10), // <-- Radius
    //                 ),
    //               ),
    //               onPressed: () {},
    //               child: const Text(
    //                 'Add Face',
    //               ),
    //             ),
    //           ),
    //         ),
    //       ),
    //       Container(
    //         margin: const EdgeInsets.only(top: 10),
    //         child: ButtonTheme(
    //           child: Padding(
    //             padding: const EdgeInsets.only(left: 20, right: 20),
    //             child: ElevatedButton(
    //               style: ElevatedButton.styleFrom(
    //                 minimumSize: const Size.fromHeight(50),
    //                 shape: RoundedRectangleBorder(
    //                   borderRadius: BorderRadius.circular(10), // <-- Radius
    //                 ),
    //               ),
    //               onPressed: () {},
    //               child: const Text(
    //                 'Add Face',
    //               ),
    //             ),
    //           ),
    //         ),
    //       ),
    //     ]
    //   ),
    // );
  }
}
