// import 'package:facepass/helpers/add_face.dart';
import 'package:facepass/helpers/add_face.dart';
import 'package:facepass/helpers/bind_group_face_token.dart';
import 'package:facepass/helpers/create_group.dart';
import 'package:facepass/scan_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:sprintf/sprintf.dart';

class FacepassLayer extends GetView<ScanController> {
  const FacepassLayer({super.key});

  @override
  Widget build(BuildContext context) {
    TextEditingController inputGroup = TextEditingController();

    return Positioned(
      bottom: 15,
      right: 15,
      child: Container(
        width: 40,
        height: 40,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(50),
          color: Colors.white,
        ),
        child: Center(
          child: GestureDetector(
            child: const Icon(
              Icons.settings,
              color: Color.fromARGB(255, 67, 66, 66),
              size: 20,
            ),
            onTap: () {
              openBottomSheet(context, inputGroup);
            },
          ),
        ),
      ),
    );
    // return Container();
  }

  void openBottomSheet(BuildContext context, TextEditingController inputGroup) {
    Get.bottomSheet(
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.only(
            topLeft: Radius.circular(20), topRight: Radius.circular(20)),
      ),
      Padding(
        padding: const EdgeInsets.all(20),
        child: ListView(
          children: [
            const Text(
              "Settings",
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(
              height: 20,
            ),
            const Text(
              "Face",
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(
              height: 10,
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                minimumSize: const Size(double.infinity, 40),
              ),
              onPressed: () async {
                controller
                    .setFaceToken(await AddFace.call(controller.channelName));
              },
              child: const Text(
                "Add Face",
                style: TextStyle(
                  fontSize: 18,
                ),
              ),
            ),
            const SizedBox(
              height: 20,
            ),
            Text(
              sprintf("%s %s", ["Group: ", controller.groupName]),
              style: const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            SizedBox(
              width: Get.width,
              height: 40,
              child: TextField(
                controller: inputGroup,
                decoration: InputDecoration(
                  hintText: controller.groupName != ""
                      ? controller.groupName
                      : "Group Name",
                ),
              ),
            ),
            const SizedBox(
              height: 10,
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                minimumSize: const Size(double.infinity, 40),
              ),
              onPressed: () {
                controller.setGroupName(inputGroup.text);
                CreateGroup.call(controller.channelName, controller.groupName);
                print("CREATE GROUP SUCCESS");
                print(controller.groupName);
              },
              child: const Text(
                "Create Group",
                style: TextStyle(
                  fontSize: 18,
                ),
              ),
            ),
            const SizedBox(
              height: 10,
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                minimumSize: const Size(double.infinity, 40),
              ),
              onPressed: () {
                BindGroupFaceToken.call(
                  controller.channelName,
                  controller.groupName, 
                  controller.faceToken
                );
              },
              child: const Text(
                "Bind Group",
                style: TextStyle(
                  fontSize: 18,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
