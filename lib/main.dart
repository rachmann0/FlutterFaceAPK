import 'package:facepass/camera/camera_screen.dart';
import 'package:facepass/global_bindings.dart';
import 'package:flutter/material.dart';
import 'package:get/route_manager.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      home: const CameraScreen(),
      title: "Facepass",
      initialBinding: GlobalBindings(),
    );
  }
}

// class MyHomePage extends StatefulWidget {
//   const MyHomePage({super.key, required this.title});

//   final String title;

//   @override
//   State<MyHomePage> createState() => _MyHomePageState();
// }

// class _MyHomePageState extends State<MyHomePage> {
//   var channelName = "com.facepass/channel";

//   // onInit
//   @override
//   void initState() {
//     super.initState();
//   }

//   // onDestroy
//   @override
//   void dispose() {
//     super.dispose();
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         backgroundColor: Theme.of(context).colorScheme.inversePrimary,
//         centerTitle: true,
//         title: Text(widget.title),
//       ),
//       body: Center(
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: <Widget>[
//             Container(
//               margin: const EdgeInsets.only(top: 10),
//               child: ButtonTheme(
//                 child: Padding(
//                   padding: const EdgeInsets.only(left: 20, right: 20),
//                   child: ElevatedButton(
//                     style: ElevatedButton.styleFrom(
//                       minimumSize: const Size.fromHeight(50),
//                       shape: RoundedRectangleBorder(
//                         borderRadius: BorderRadius.circular(10), // <-- Radius
//                       ),
//                     ),
//                     onPressed: () {
//                       InitializeSDK.call(channelName);
//                     },
//                     child: const Text(
//                       'Initialize SDK',
//                     ),
//                   ),
//                 ),
//               ),
//             ),
//             Container(
//               margin: const EdgeInsets.only(top: 10),
//               child: ButtonTheme(
//                 child: Padding(
//                   padding: const EdgeInsets.only(left: 20, right: 20),
//                   child: ElevatedButton(
//                     style: ElevatedButton.styleFrom(
//                       minimumSize: const Size.fromHeight(50),
//                       shape: RoundedRectangleBorder(
//                         borderRadius: BorderRadius.circular(10), // <-- Radius
//                       ),
//                     ),
//                     onPressed: () {},
//                     child: const Text(
//                       'Add Face',
//                     ),
//                   ),
//                 ),
//               ),
//             ),
//           ],
//         ),
//       ),
//     );
//   }
// }
