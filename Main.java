package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
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
import org.opencv.imgcodecs.Imgcodecs;


public class Main extends Application {
	
	VideoCapture camvideo;
	Image image;
	Image snapShot;
	
	Canvas canvas;
	GraphicsContext g2d;

	Canvas canvas_tmp;
	GraphicsContext g2d_tmp;

	boolean debugMode;
	
	AnimationTimer timer;
	ComputerVisionDriver cvDriver;
	
	// Original image
	Mat matSnap;
	 // Image to put diagnostics on
	Mat matSnapDiag;

	//UserSelection userSelection;
	
	@Override
	public void start(Stage primaryStage) {
		
		debugMode = false;//true;
		//userSelection = new UserSelection();
		
		/* Setup GUI elements */
		
		/* Canvas */
		this.canvas = new Canvas(700, 550);
		this.g2d = canvas.getGraphicsContext2D();
		
		/* Tmp Canvas */
		this.canvas_tmp = new Canvas(700, 550);
		this.g2d_tmp = canvas_tmp.getGraphicsContext2D();

		/* Buttons and HBox*/
		Button setSceneButton = new Button();
		setSceneButton.setText("Set");
		setSceneButton.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	                System.out.println("Setting a Scene");
	                timer.start();
	            }
	        });

		Button takeSceneButton = new Button();
		takeSceneButton.setText("Snapshot");
		takeSceneButton.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	                System.out.println("Snapshot");
	                takeSnap();
	                timer.stop();
	            }
	        });


		Button newFileButton = new Button();
		newFileButton.setText("New marker file");
		newFileButton.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	                System.out.println("New marker file");
	                cvDriver.startNewFile();
	                timer.stop();
	            }
	        });

		Button chooseColorButton = new Button();
		chooseColorButton.setText("Choose Color");
		chooseColorButton.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	                System.out.println("Choosing a color");
	                cvDriver.chooseColor();
	            }
	        });

		Button copyFileButton = new Button();
		copyFileButton.setText("Copy the marker file");
		copyFileButton.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	                System.out.println("Copying the marker file");
	                //cvDriver.selectColors();
	                timer.stop();
	            }
	        });

		HBox hbox = new HBox(8);
	     hbox.getChildren().addAll(setSceneButton, takeSceneButton, newFileButton, chooseColorButton, copyFileButton);
	     
	     	/* a Pane and a Scene */
	        BorderPane root = new BorderPane();
	        root.setTop(hbox);
	        root.setMargin(hbox, new Insets(2, 2, 2, 2));
	        
	        root.setCenter(this.canvas);
	        root.setAlignment(this.canvas, Pos.TOP_LEFT);

	        root.setRight(this.canvas_tmp);

	        Scene scene = new Scene(root, 1400, 550);
	         
	        primaryStage.setTitle("Marker Picker");
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        
	        /* Setup OpenCV */
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
			
			openCVInit();
			initImg();
			
			cvDriver = new ComputerVisionDriver(debugMode);
			
	        timer = new AnimationTimer() {

	            Mat mat = new Mat();

	            @Override
	            public void handle(long now) {

	            	camvideo.read(mat);
	                image = mat2Image(mat);
	                g2d.drawImage(image, 0, 0);
	            }
	        };

	}
	
	private void takeSnap() {

		camvideo.read(matSnap);

		matSnapDiag = matSnap.clone();

		/* debug Mat tmp = cvDriver.prepareLabImage(matSnap); */
		
		cvDriver.findSelectedMarker(matSnap, matSnapDiag);
		
		/* debug snapShot = mat2Image(tmp); */
		snapShot = mat2Image(matSnapDiag);
		
		g2d_tmp.drawImage(snapShot, 0, 0);
		
		matSnapDiag.release();		
	}

	
	private Image mat2Image(Mat mat) {
		
	    
	    	MatOfByte byteMat = new MatOfByte();
	    	Imgcodecs.imencode(".png", mat, byteMat);
	    	return new Image(new ByteArrayInputStream(byteMat.toArray()));
	    
	}

	
	private void initImg() {
		
		matSnap = new Mat();

	}

	
	private void openCVInit() {
		
	    this.camvideo = new VideoCapture(0);
	    
	    if( !this.camvideo.isOpened() ) {
	    	this.camvideo.open(0);
	    }
	    if( !this.camvideo.isOpened() ) {
	    	System.out.printf("Failed to open Video%n");
	    }	
	}

	
	public static void main(String[] args) {
		launch(args);
	}

}
