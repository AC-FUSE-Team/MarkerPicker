package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.geometry.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;


public class Main extends Application {
	
	VideoCapture camVideo;
	ComputerVisionDriver cvDriver;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {

			primaryStage.setTitle("Marker Picker");
			
			/* Setup OpenCV */
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
			openCVInit();

			cvDriver = new ComputerVisionDriver(false); // TODO: fix debug mode

			ScreenController scrController = new ScreenController();
			FXMLLoader screenLoader = new FXMLLoader(getClass().getResource("AppScreen.fxml"));
			screenLoader.setController(scrController);
			
			Parent root = screenLoader.load();
			scrController.set(camVideo, cvDriver);
			
			Scene scene = primaryStage.getScene();
            scene = new Scene(root, 1024, 636);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openCVInit() {
		
	    this.camVideo = new VideoCapture(0);
	    
	    if( !this.camVideo.isOpened() ) {
	    	this.camVideo.open(0);
	    }
	    if( !this.camVideo.isOpened() ) {
	    	System.out.printf("Failed to open Video%n");
	    }
	    
	    camVideo.set(Videoio.CAP_PROP_FRAME_WIDTH, 1024);
	    camVideo.set(Videoio.CAP_PROP_FRAME_HEIGHT, 576);
	}

	
	public static void main(String[] args) {
		launch(args);
	}

}
