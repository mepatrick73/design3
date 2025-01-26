package sean.app;
// Inludes common necessary includes for development using depthai library
import org.bytedeco.javacpp.*;
import org.bytedeco.depthai.*;
import org.bytedeco.depthai.Device;
import org.bytedeco.opencv.global.opencv_objdetect;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_highgui.*;
import static org.bytedeco.depthai.global.depthai.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;

import org.bytedeco.opencv.opencv_objdetect.ArucoDetector;
import org.bytedeco.opencv.opencv_objdetect.DetectorParameters;
import org.bytedeco.opencv.opencv_objdetect.Dictionary;
import org.bytedeco.opencv.opencv_objdetect.RefineParameters;
import org.opencv.aruco.Aruco;

import java.util.ArrayList;
import java.util.List;

public class CameraPreviewExample {
    static Pipeline createCameraPipeline() {
        Pipeline p = new Pipeline();

        ColorCamera colorCam = p.createColorCamera();
        XLinkOut xlinkOut = p.createXLinkOut();
        xlinkOut.setStreamName("preview");

        colorCam.setPreviewSize(300, 300);
        colorCam.setResolution(ColorCameraProperties.SensorResolution.THE_1080_P);
        colorCam.setInterleaved(true);

        // Link plugins CAM -> XLINK
        colorCam.preview().link(xlinkOut.input());

        return p;
    }

    public static void main(String[] args) {
        Pipeline p = createCameraPipeline();

        // Start the pipeline
        Device d = new Device();

        System.out.print("Connected cameras: ");
        IntPointer cameras = d.getConnectedCameras();
        for (int i = 0; i < cameras.limit(); i++) {
            System.out.print(cameras.get(i) + " ");
        }
        System.out.println();

        // Start the pipeline
        d.startPipeline(p);

        Mat frame;
        DataOutputQueue preview = d.getOutputQueue("preview");
        Dictionary dictionary= getPredefinedDictionary(DICT_4X4_50);
        System.out.println(dictionary.toString());

        while (true) {
            ImgFrame imgFrame = preview.getImgFrame();
            if (imgFrame != null) {
                ArucoDetector detector= new ArucoDetector(dictionary);
                MatVector corners = new MatVector();
                Mat ids = new Mat();
                System.out.println("Hello World!");
                System.out.printf("Frame - w: %d, h: %d\n", imgFrame.getWidth(), imgFrame.getHeight());
                frame = new Mat(imgFrame.getHeight(), imgFrame.getWidth(), CV_8UC3, imgFrame.getData());
                detector.detectMarkers(frame, corners, ids);
                System.out.println(corners.size());
                imshow("preview", frame);

                int key = waitKey(1);
                if (key == 'q') {
                    System.exit(0);
                }
            } else {
                System.out.println("Not ImgFrame");
            }
        }
    }
}