/**
 * This is a sample program that uses JavaFX along with OpenCV to do face detection on a MAC OS.
 * Button click will start the camera acquisition and check the data with a pre-trained data sets from openCV. (Only can detect front face)
 *
 */

package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;


public class Main extends Application {
    Mat matrix = null;
    VideoCapture capture =null;
    BufferedImage img = null;
    @Override
    public void start(Stage primaryStage) throws Exception{

        // Loading the OpenCV core library
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        //create a root and scene for the GUI
        VBox root = new VBox();
        Scene scene = new Scene(root,1200,800);
        root.setAlignment(Pos.CENTER);


            // Instantiating the VideoCapture class (camera:: 0)
            capture = new VideoCapture(0);
            // Reading the next video frame from the camera
            matrix = new Mat();
            capture.read(matrix);

            //create imageview
        ImageView imageview = new ImageView();
        imageview.setId("originalFrame");
        root.getChildren().add(imageview);

        //create a button to trigger the start of video acquisition and face detection
        Button myButton = new Button();
        myButton.setText("Click to start video!");
        root.getChildren().add(myButton);
        //press the button then start another thread to capture a video
        myButton.addEventHandler(ActionEvent.ACTION, (ActionEvent)->{
            //imageview.setImage(capureSnapShot());
            Thread t = new Thread(()->{
                while (true)
                {
                    imageview.setImage(contCapture());
                }
            });
            t.setDaemon(true);
            t.start();
        });
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("My Face detection using openCV");


    }
    //methods
    //detect face in image
    public  void detectFace()
    {
        //https://www.tutorialspoint.com/opencv/opencv_face_detection_in_picture.htm
        CascadeClassifier CC = new CascadeClassifier("/Users/puanxuesheng/" +
                "eclipse-workspace/Java Learning/Code Clinic/Face_Detection/src/haarcascade_frontalface_default.xml");

        MatOfRect faceDetections = new MatOfRect();
        //Mat grayFrame = new Mat();
        //Imgproc.cvtColor(matrix, grayFrame, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.equalizeHist(grayFrame, grayFrame);

        //face detection
        CC.detectMultiScale(matrix, faceDetections,1.04,5);

        //Print the rectangular onto the display
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(
                    matrix,                                               // where to draw the box
                    new Point(rect.x, rect.y),                            // bottom left
                    new Point(rect.x + rect.width, rect.y + rect.height), // top right
                    new Scalar(0, 0, 255),
                    3                                                     // RGB colour
            );
        }

    }

    //this is copied from https://www.tutorialspoint.com/opencv/opencv_using_camera.htm
    public WritableImage capureSnapShot() {
        WritableImage WritableImage = null;


        // If camera is opened
        if( capture.isOpened()) {
            // If there is next video frame
            if (capture.read(matrix)) {

                // Creating BuffredImage from the matrix
                BufferedImage image = new BufferedImage(matrix.width(),
                        matrix.height(), BufferedImage.TYPE_3BYTE_BGR);

                WritableRaster raster = image.getRaster();
                DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
                byte[] data = dataBuffer.getData();
                matrix.get(0, 0, data);
                //this.matrix = matrix;

                // Creating the Writable Image
                WritableImage = SwingFXUtils.toFXImage(image, null);
            }
        }
        return WritableImage;
    }

    public WritableImage contCapture()
    {
        WritableImage WritableImage = null;
        if( capture.isOpened()) {
            // If there is next video frame
            if (capture.read(matrix)) {

                //add face detection algorithm
                detectFace();

                // Creating BuffredImage from the matrix
                BufferedImage image = new BufferedImage(matrix.width(),
                        matrix.height(), BufferedImage.TYPE_3BYTE_BGR);

                WritableRaster raster = image.getRaster();
                DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
                byte[] data = dataBuffer.getData();
                matrix.get(0, 0, data);
                //this.matrix = matrix;

                // Creating the Writable Image
                WritableImage = SwingFXUtils.toFXImage(image, null);
            }
        }
        return WritableImage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
