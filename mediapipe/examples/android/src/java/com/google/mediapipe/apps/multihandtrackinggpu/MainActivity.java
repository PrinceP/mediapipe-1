// Copyright 2019 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.mediapipe.apps.multihandtrackinggpu;

import android.os.Bundle;
import android.util.Log;
import com.google.mediapipe.formats.proto.ClassificationProto;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.formats.proto.RectProto.NormalizedRect;
import com.google.mediapipe.framework.PacketGetter;
import java.util.List;


/** Main activity of MediaPipe multi-hand tracking app. */
public class MainActivity extends com.google.mediapipe.apps.basic.MainActivity {
  private static final String TAG = "MainActivity";


  private static final String BINARY_GRAPH_NAME = "multihandtrackinggpu.binarypb";
  private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
  private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
  private static final String OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks";
  private static final String OUTPUT_CLASSIFICATIONS_STREAM_NAME = "multi_hand_gesture";
  private static final String OUTPUT_LUMINANCE_STREAM_NAME = "luminance_value";
  private static final String OUTPUT_DETECTIONS_STREAM_NAME = "multi_hand_rects_from_landmarks";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // To show verbose logging, run:
    // adb shell setprop log.tag.MainActivity VERBOSE
//    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      processor.addPacketCallback(
              OUTPUT_LANDMARKS_STREAM_NAME,
              (packet) -> {
                Log.v(TAG, "Received multi-hand landmarks packet.");
                List<NormalizedLandmarkList> multiHandLandmarks =
                        PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser());
                Log.v(
                        TAG,
                        "[TS:"
                                + packet.getTimestamp()
                                + "] "
                                + getMultiHandLandmarksDebugString(multiHandLandmarks));
              });

      processor.addPacketCallback(
            OUTPUT_CLASSIFICATIONS_STREAM_NAME,
            (packet) -> {
              Log.d(TAG, "Received multi-hand classifications packet.");
              List<ClassificationProto.ClassificationList> multiHandClassifications =
                      PacketGetter.getProtoVector(packet, ClassificationProto.ClassificationList.parser());
              Log.d(
                      TAG,
                      "[TS:"
                              + packet.getTimestamp()
                              + "] "
                              + getMultiHandClassificationsDebugString(multiHandClassifications));
            });
//
//

    processor.addPacketCallback(
            OUTPUT_LUMINANCE_STREAM_NAME,
            (packet) -> {
              Log.d(TAG, "Received luminance packet.");
              Float luminance_value =
                      PacketGetter.getFloat32(packet);
              Log.d(
                      TAG,
                      "[TS:"
                              + packet.getTimestamp()
                              + "] "
                              + luminance_value.toString());
            });

    processor.addPacketCallback(
            OUTPUT_DETECTIONS_STREAM_NAME,
            (packet) -> {
              Log.d(TAG, "Received rectangles packet.");
              List<NormalizedRect> multiHandRectangles =
              PacketGetter.getProtoVector(packet, NormalizedRect.parser());
              Log.d(
                      TAG,
                      "[TS:"
                              + packet.getTimestamp()
                              + "] "
                              + getMultiHandDetectionsDebugString(multiHandRectangles));


            });

//    }
  }



///** Main activity of MediaPipe multi-hand tracking app. */
//public class MainActivity extends com.google.mediapipe.apps.basic.MainActivity {
//  private static final String TAG = "MainActivity";
//
//  private static final String BINARY_GRAPH_NAME = "multihandtrackinggpu.binarypb";
//  private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
//  private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
//  private static final String OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks";
//  private static final String OUTPUT_CLASSIFICATIONS_STREAM_NAME = "multi_hand_gesture";
//  private static final String OUTPUT_LUMINANCE_STREAM_NAME = "luminance_value";
//  private static final String OUTPUT_DETECTIONS_STREAM_NAME = "multi_hand_rects_from_landmarks";
//
//
//  private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
//
//  // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
//  // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
//  // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
//  // corner, whereas MediaPipe in general assumes the image origin is at top-left.
//  private static final boolean FLIP_FRAMES_VERTICALLY = true;
//
//  static {
//    // Load all native libraries needed by the app.
//    System.loadLibrary("mediapipe_jni");
//    System.loadLibrary("opencv_java3");
//  }
//
//  // {@link SurfaceTexture} where the camera-preview frames can be accessed.
//  private SurfaceTexture previewFrameTexture;
//  // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
//  private SurfaceView previewDisplayView;
//
//  // Creates and manages an {@link EGLContext}.
//  private EglManager eglManager;
//  // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
//  // frames onto a {@link Surface}.
//  private FrameProcessor processor;
//  // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
//  // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
//  private ExternalTextureConverter converter;
//
//  // Handles camera access via the {@link CameraX} Jetpack support library.
//  private CameraXPreviewHelper cameraHelper;
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_main);
//
//    previewDisplayView = new SurfaceView(this);
//    setupPreviewDisplayView();
//
//    // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
//    // binary graphs.
//    AndroidAssetUtil.initializeNativeAssetManager(this);
//
//    eglManager = new EglManager(null);
//    processor =
//        new FrameProcessor(
//            this,
//            eglManager.getNativeContext(),
//            BINARY_GRAPH_NAME,
//            INPUT_VIDEO_STREAM_NAME,
//            OUTPUT_VIDEO_STREAM_NAME);
//    processor.getVideoSurfaceOutput().setFlipY(FLIP_FRAMES_VERTICALLY);
//
////    processor.addPacketCallback(
////        OUTPUT_LANDMARKS_STREAM_NAME,
////        (packet) -> {
////          Log.d(TAG, "Received multi-hand landmarks packet.");
////          List<NormalizedLandmarkList> multiHandLandmarks =
////              PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser());
////          Log.d(
////              TAG,
////              "[TS:"
////                  + packet.getTimestamp()
////                  + "] "
////                  + getMultiHandLandmarksDebugString(multiHandLandmarks));
////        });
//
//    processor.addPacketCallback(
//            OUTPUT_CLASSIFICATIONS_STREAM_NAME,
//            (packet) -> {
//              Log.d(TAG, "Received multi-hand classifications packet.");
//              List<ClassificationProto.ClassificationList> multiHandClassifications =
//                      PacketGetter.getProtoVector(packet, ClassificationProto.ClassificationList.parser());
//              Log.d(
//                      TAG,
//                      "[TS:"
//                              + packet.getTimestamp()
//                              + "] "
//                              + getMultiHandClassificationsDebugString(multiHandClassifications));
//            });
////
////
//
//    processor.addPacketCallback(
//            OUTPUT_LUMINANCE_STREAM_NAME,
//            (packet) -> {
//              Log.d(TAG, "Received luminance packet.");
//              Float luminance_value =
//                      PacketGetter.getFloat32(packet);
//              Log.d(
//                      TAG,
//                      "[TS:"
//                              + packet.getTimestamp()
//                              + "] "
//                              + luminance_value.toString());
//            });
//
//    processor.addPacketCallback(
//            OUTPUT_DETECTIONS_STREAM_NAME,
//            (packet) -> {
//              Log.d(TAG, "Received rectangles packet.");
//              List<NormalizedRect> multiHandRectangles =
//              PacketGetter.getProtoVector(packet, NormalizedRect.parser());
//              Log.d(
//                      TAG,
//                      "[TS:"
//                              + packet.getTimestamp()
//                              + "] "
//                              + getMultiHandDetectionsDebugString(multiHandRectangles));
//
//
//            });
//
//    PermissionHelper.checkAndRequestCameraPermissions(this);
//  }
//
//  @Override
//  protected void onResume() {
//    super.onResume();
//    converter = new ExternalTextureConverter(eglManager.getContext());
//    converter.setFlipY(FLIP_FRAMES_VERTICALLY);
//    converter.setConsumer(processor);
//    if (PermissionHelper.cameraPermissionsGranted(this)) {
//      startCamera();
//    }
//  }
//
//  @Override
//  protected void onPause() {
//
//
//    super.onPause();
//
////    previewFrameTexture.release();
////    previewDisplayView.setVisibility(View.GONE);
////
////    if(converter!=null)
////      converter.close();
////    previewDisplayView = null;
//////    if(cameraHelper!=null) {
//////      cameraHelper.unbindInstances();
//////    }
//
//
//    converter.close();
//  }
//
//  @Override
//  public void onRequestPermissionsResult(
//      int requestCode, String[] permissions, int[] grantResults) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
//  }
//
//  private void setupPreviewDisplayView() {
//    previewDisplayView.setVisibility(View.GONE);
//    ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
//    viewGroup.addView(previewDisplayView);
//
//    previewDisplayView
//        .getHolder()
//        .addCallback(
//            new SurfaceHolder.Callback() {
//              @Override
//              public void surfaceCreated(SurfaceHolder holder) {
//                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
//              }
//
//              @Override
//              public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                // (Re-)Compute the ideal size of the camera-preview display (the area that the
//                // camera-preview frames get rendered onto, potentially with scaling and rotation)
//                // based on the size of the SurfaceView that contains the display.
//                Size viewSize = new Size(width, height);
//                Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
//                boolean isCameraRotated = cameraHelper.isCameraRotated();
//
//                // Connect the converter to the camera-preview frames as its input (via
//                // previewFrameTexture), and configure the output width and height as the computed
//                // display size.
//                converter.setSurfaceTextureAndAttachToGLContext(
//                    previewFrameTexture,
//                    isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
//                    isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
//              }
//
//              @Override
//              public void surfaceDestroyed(SurfaceHolder holder) {
//                processor.getVideoSurfaceOutput().setSurface(null);
//              }
//            });
//  }

//  private void startCamera() {
//    cameraHelper = new CameraXPreviewHelper();
//    cameraHelper.setOnCameraStartedListener(
//        surfaceTexture -> {
//          previewFrameTexture = surfaceTexture;
//          // Make the display view visible to start showing the preview. This triggers the
//          // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
//          previewDisplayView.setVisibility(View.VISIBLE);
//        });
//    cameraHelper.startCamera(this, CAMERA_FACING, /*surfaceTexture=*/ null);
//  }

  private String getMultiHandLandmarksDebugString(List<NormalizedLandmarkList> multiHandLandmarks) {
    if (multiHandLandmarks.isEmpty()) {
      return "No hand landmarks";
    }
    String multiHandLandmarksStr = "Number of hands detected: " + multiHandLandmarks.size() + "\n";
    int handIndex = 0;
    for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
      multiHandLandmarksStr +=
          "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.getLandmarkCount() + "\n";
      int landmarkIndex = 0;
      for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {
        multiHandLandmarksStr +=
            "\t\tLandmark ["
                + landmarkIndex
                + "]: ("
                + landmark.getX()
                + ", "
                + landmark.getY()
                + ", "
                + landmark.getZ()
                + ")\n";
        ++landmarkIndex;
      }
      ++handIndex;
    }
    return multiHandLandmarksStr;
  }

  private String getMultiHandClassificationsDebugString(List<ClassificationProto.ClassificationList> multiHandClassifications) {
    if (multiHandClassifications.isEmpty()) {
      return "No hand classifications";
    }
    String multiHandClassificationsStr = "Number of hands detected: " + multiHandClassifications.size() + "\n";
    int handIndex = 0;
    for (ClassificationProto.ClassificationList classification : multiHandClassifications) {
      multiHandClassificationsStr +=
              "\t#Hand Classifications for hand[" + handIndex + "]: " + classification.getClassification(handIndex) + "\n";
//      ++handIndex;
    }
    return multiHandClassificationsStr;
  }

  private String getMultiHandDetectionsDebugString(List<NormalizedRect> multi_hand_rects){
    if (multi_hand_rects.isEmpty()) {
      return "No hand detections";
    }
    String multiHandDetectionsStr = "Number of hands detected: " + multi_hand_rects.size() + "\n";
    int handIndex = 0;
    for(NormalizedRect normalizedRect: multi_hand_rects) {

      multiHandDetectionsStr +=
              "\t#Hand Detection size for hand[" + handIndex + "]: " + normalizedRect.getHeight() * normalizedRect.getWidth()  + "\n";

    }
    return multiHandDetectionsStr;
  }
}
