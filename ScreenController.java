package application;

import java.io.ByteArrayInputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;


public class ScreenController {

	private VideoCapture camVideo;
	private AnimationTimer aTimer;
	private boolean isON = false;
	private GraphicsContext g2d;

	private Image image;
	private Image snapShot;
	private ComputerVisionDriver cvDriver;
	private int selection;
	
	// Original image
	private Mat matSnap;
	// Image to put diagnostics on
	private Mat matSnapDiag;

    @FXML
    private Canvas canvas;

    @FXML
    private Button chooseColorButton;

    @FXML
    private Button liveSceneButton;

    @FXML
    private Button newFileButton;

    @FXML
    private Button snapshotButton;

    @FXML
    void handleChooseColorButtonClick(ActionEvent event) {

    	System.out.println("Choosing a color");
        cvDriver.chooseColor();
    }

    @FXML
    void handleLiveSceneClick(ActionEvent event) {

    	System.out.println("Live Scene");
        aTimer.start();
    }

    @FXML
    void handleNewFileButtonClick(ActionEvent event) {

    	System.out.println("New marker file");
        cvDriver.startNewFile();
        aTimer.stop();
    }

    @FXML
    void handleSnapshotButtonClick(ActionEvent event) {
    	
    	System.out.println("Snapshot");
        takeSnap();
        aTimer.stop();
    }
    
    public void set(VideoCapture vCapture, ComputerVisionDriver inCvDriver) {
    	
      	camVideo = vCapture;
    	cvDriver = inCvDriver;
    	
    	matSnap = new Mat();
     	
		g2d = canvas.getGraphicsContext2D();
		
		aTimer = new AnimationTimer() {

            Mat mat = new Mat();

            @Override
            public void handle(long now) {

            	camVideo.read(mat);
                image = mat2Image(mat);
                g2d.drawImage(image, 0, 0);
            }
        };
    	
    	
    }
    
	
	private Image mat2Image(Mat mat) {
		
    	MatOfByte byteMat = new MatOfByte();
    	Imgcodecs.imencode(".png", mat, byteMat);
    	return new Image(new ByteArrayInputStream(byteMat.toArray()));
    
	}
	
	private void takeSnap() {

		camVideo.read(matSnap);

		matSnapDiag = matSnap.clone();

		cvDriver.findSelectedMarker(matSnap, matSnapDiag);
		
		snapShot = mat2Image(matSnapDiag);
		
		g2d.drawImage(snapShot, 0, 0);
		
		matSnapDiag.release();		
	}

}
