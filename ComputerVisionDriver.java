package application;

import org.opencv.imgproc.Imgproc;
import org.opencv.ximgproc.EdgeDrawing;
import org.opencv.ximgproc.Ximgproc;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Rect;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;


public class ComputerVisionDriver {
	
	Mat imageOriginal;
	Mat imageProcessedTmp;
	Mat imageProcessed;
	Mat imageFinal;
	Mat ellipses;
	Mat labColorspace;
	
	MarkerTable markerTable;
	
	boolean debugMode;
	int markerSeqNumber = 0;
	
	// Default size of color sampling rectangle;
	// should be even
	final int DEFAULT_SAMPLING_SIZE = 50;
	
	Scanner input;
	
	public ComputerVisionDriver(boolean isDebugMode) {
		
		debugMode = isDebugMode;
		
		imageProcessedTmp = new Mat();
		imageProcessed = new Mat();
		imageFinal = new Mat();
		ellipses = new Mat();
		labColorspace = new Mat();
		markerTable = new MarkerTable(debugMode);
		
		input = new Scanner(System.in);

	}
	
	public void release() {
		input.close();
	}
	
	public void startNewFile() {
		
		String fileName = "C:\\Users\\nakor\\FUSE\\markers.txt";
		var path = Paths.get(fileName);
        try {
            // Truncates existing marker file
            Files.writeString(path, "", StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

		markerSeqNumber = 0;
	}
	
	
	
	public void chooseColor() {
		
		FileWriter file;
		String colorLine = "";
		
		//"/home/nataliya/AC-courses/FUSE/app/markers.txt"
		//"C:\\Users\\nakor\\FUSE\\markers.txt"
		String fileName = "C:\\Users\\nakor\\FUSE\\markers.txt";
		

		var path = Paths.get(fileName);
        try {

        	
        	int ellipseSeqNumber = 0;

    		do {
    			System.out.printf("Please select color for %d-th marker or -1 to save the choice: ", markerSeqNumber);
    			
    			while(!input.hasNextInt())
    			{;}
    			ellipseSeqNumber = input.nextInt();
    			
    			if (ellipseSeqNumber != -1) {
    				
    				Scalar[] color = markerTable.getColor(ellipseSeqNumber);
    				colorLine = String.format("%d %d %d   %d %d %d%n",
    						(int)color[0].val[0], (int)color[0].val[1], (int)color[0].val[2],
    						(int)color[1].val[0], (int)color[1].val[1], (int)color[1].val[2]);
    				
    				String colorDescr = String.format("RGB: (%d, %d, %d) will be saved as (%d, %d, %d)%nLab: %d %d %d%n",
    						(int)color[0].val[2], (int)color[0].val[1], (int)color[0].val[0],
    						(int)color[0].val[0], (int)color[0].val[1], (int)color[0].val[2],
    						(int)color[1].val[0], (int)color[1].val[1], (int)color[1].val[2]);
    				
    				System.out.printf(colorDescr);
    				
    				
    				
    			}
    			
    		} while( ellipseSeqNumber != -1 );
    		
            Files.writeString(path, colorLine, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            
            //file.write( colorLine );
    		markerSeqNumber++;
    		
    		System.out.printf("The marker color saved%n");
    		//file.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
       
/*
		
		
		
		try {
			file = new FileWriter(fileName, false);
		
		int ellipseSeqNumber = 0;

		do {
			System.out.printf("Please select color for %d-th marker or -1 to save the choice: ", markerSeqNumber);
			
			while(!input.hasNextInt())
			{;}
			ellipseSeqNumber = input.nextInt();
			
			if (ellipseSeqNumber != -1) {
				
				Scalar[] color = markerTable.getColor(ellipseSeqNumber);
				colorLine = String.format("%d %d %d   %d %d %d%n",
						(int)color[0].val[0], (int)color[0].val[1], (int)color[0].val[2],
						(int)color[1].val[0], (int)color[1].val[1], (int)color[1].val[2]);
				
				String colorDescr = String.format("RGB: (%d, %d, %d) will be saved as (%d, %d, %d)%nLab: %d %d %d%n",
						(int)color[0].val[2], (int)color[0].val[1], (int)color[0].val[0],
						(int)color[0].val[0], (int)color[0].val[1], (int)color[0].val[2],
						(int)color[1].val[0], (int)color[1].val[1], (int)color[1].val[2]);
				
				System.out.printf(colorDescr);
				
				
				
			}
			
		} while( ellipseSeqNumber != -1 );
		
		file.write( colorLine );
		markerSeqNumber++;
		
		System.out.printf("The marker color saved%n");
		file.close();
		
		}
		catch(IOException e) {
			System.out.printf("File Exception:" + e);
		}
		*/
	}
	
	
	
	public Mat prepareLabImage(Mat inImage) {

		double normKoef = 1;//1./255;
		Scalar koefScalar = new Scalar(normKoef, normKoef, normKoef);

		Mat tmpImage = inImage.clone();

		Core.multiply(tmpImage, koefScalar, tmpImage);
		Imgproc.cvtColor(tmpImage, labColorspace, Imgproc.COLOR_BGR2Lab);

		return labColorspace;

	}
	
	
	/* Detects markers on an original snapshot:
	 *  snapshotIn is an original image,
	 *  snapshotOut is used to output diagnostic information over an original image;
	 *  the both objects are created and provided by a caller */
	public void findSelectedMarker(Mat snapshotIn, Mat snapshotOut) {
		
		imageOriginal = snapshotIn;
		imageFinal = snapshotOut;
		
		labColorspace = prepareLabImage(imageOriginal);
		
		/* Detect Ellipses in the original image */
		Imgproc.cvtColor(imageOriginal, imageProcessedTmp, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(imageProcessedTmp, imageProcessed, 3);
		
		
		EdgeDrawing ed = Ximgproc.createEdgeDrawing();
		ed.detectEdges(imageProcessed);
		ed.detectEllipses(ellipses);

		// The first [0] marker (a.k.a "pointer") is a center of the image
		markerTable.initMarkers(imageOriginal.width() / 2, imageOriginal.height() / 2);
		
		// Number of found ellipses
		int ellipsesNumber = ellipses.rows();
		// Number of markers that markerTable can keep
		int markerPlaces = markerTable.getMarkerPlaceNumber();
		
		int numberOfMarkers = (ellipsesNumber > markerPlaces) ? markerPlaces : ellipsesNumber;
		
		System.out.printf("Image size: %dx%d%n%n", imageOriginal.width(), imageOriginal.height());
		
		System.out.printf("%nEllipses found: %d%n", ellipsesNumber);
		System.out.printf("Ellipses to be processed %d%n", numberOfMarkers);
		
		// Index of legit markers ([0]-th is reference marker, start from 1)
		int legitMarkersNumber = 1;
		
		// Loop through detected ellipses
		for (int i = 0; i < numberOfMarkers; i++) {
		
			if(debugMode)
				System.out.printf("%nEllipse #%d:%n", i);
			
			// Extract a center and sizes of an ellipse
			
			// Ellipse center
			int centerX = (int)ellipses.get(i, 0)[0];
			int centerY = (int)ellipses.get(i, 0)[1];
			int ellipseSize1 = (int)(ellipses.get(i, 0)[2] + ellipses.get(i, 0)[3]);
			int ellipseSize2 = (int)(ellipses.get(i, 0)[2] + ellipses.get(i, 0)[4]);
			
			Point center = new Point(centerX, centerY);
			Size size = new Size(ellipseSize1, ellipseSize2);
			
			
			// 0. Prepare color sampling rectangle

			int sampleDefaultRectSize = DEFAULT_SAMPLING_SIZE;
			
			int sampleRectSize = (ellipseSize1 < ellipseSize2) ? ellipseSize1 : ellipseSize2;
			sampleRectSize = (sampleRectSize < sampleDefaultRectSize) ? sampleRectSize : sampleDefaultRectSize;
			
			if (sampleRectSize % 2 > 0) {
				sampleRectSize--; // make it even
			}

			// Drawing ellipse center
			Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
		    		2, new Scalar(255, 255, 255), 1);

			// Skip ellipses which sampling rectangle doesn't fit image
			if((centerX - sampleRectSize / 2) < 0 ||
					(centerX + sampleRectSize / 2) >= imageOriginal.width() ||
					(centerY - sampleRectSize / 2) < 0 ||
					(centerY + sampleRectSize / 2) >= imageOriginal.height()) {
				
				if(debugMode) {
					// drawing center of skipped ellipse 
					Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
				    		5, new Scalar(255, 90, 255), 2);
					Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
				    		15, new Scalar(255, 90, 255), 2);
					Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
				    		25, new Scalar(255, 90, 255), 2);
					
					// print a seq. number of found ellipse at the bottom-right side off an ellipse center
					Imgproc.putText(imageFinal, String.format("%d", i), new Point(center.x - 15, center.y - 10),
							Imgproc.FONT_HERSHEY_DUPLEX, 0.7, new Scalar (255, 170, 255));
					
					Imgproc.putText(imageFinal, String.format("%d", i), new Point(center.x + 5, center.y + 15),
							Imgproc.FONT_HERSHEY_DUPLEX, 0.7, new Scalar (255, 170, 255));
					
					System.out.printf("%nThe ellipse skipped%n");
				}
				
				continue;
			}

			int sampleRectX = centerX - sampleRectSize / 2;
			int sampleRectY = centerY - sampleRectSize / 2;
			
			// 1. Create sampling rectangle
			Rect sampleRect = new Rect(sampleRectX, sampleRectY, sampleRectSize, sampleRectSize);
	
			// 2. Create a submat of original RGB image and Lab image
			Mat areaToSampleRBG = imageOriginal.submat(sampleRect);
			Mat areaToSampleLab = labColorspace.submat(sampleRect);
	
			// 3. Calculate the mean color values for original RGB image and Lab image
			Scalar meanColorRBG = Core.mean(areaToSampleRBG);
			Scalar meanColorLab = Core.mean(areaToSampleLab);


			// highlight the contour of an ellipse with calculated color
			Size sizeHalo = size;
			sizeHalo.height += 10;
			sizeHalo.width += 10;
			
			// add a new marker to a marker table
			markerTable.addMarker((int)center.x, (int)center.y, meanColorRBG, meanColorLab);
			
			
			/* Output block */
			
			/*** *** ***
			 *  Draw everything needed to the final image 
			 *** *** ***  */
			
			// Draw a Halo around selected ellipse
			Imgproc.ellipse(imageFinal, center, sizeHalo, ellipses.get(i, 0)[5], 0, 360,
					meanColorRBG, 8, 1);

			// print a seq. number of legit marker at the top-left side off an ellipse center
			Imgproc.putText(imageFinal, String.format("%d", legitMarkersNumber),
					new Point(center.x - 15, center.y - 10), Imgproc.FONT_HERSHEY_DUPLEX,
					0.7, new Scalar (255, 255, 255));
			
			if(debugMode) {
				
				// print a seq. number of found ellipse at the bottom-right side off an ellipse center
				Imgproc.putText(imageFinal, String.format("%d", i),
						new Point(center.x + 5, center.y + 15), Imgproc.FONT_HERSHEY_DUPLEX, 0.7, new Scalar (255, 170, 255));
								
				// draw center of the image
				Imgproc.circle(imageFinal, new Point(imageOriginal.width() / 2, imageOriginal.height() / 2),
			    		5, new Scalar(0, 0, 255), 3);
				
				Imgproc.rectangle(imageFinal, sampleRect, new Scalar(255, 255, 255), 1);
			}
			
			
			/*** *** *** 
			 * Print out log to a terminal
			 *** *** *** */
			
			if(debugMode)
				System.out.printf("\t## Legit marker #%d%n", legitMarkersNumber);
			else
				System.out.printf("Legit marker #%d%n", legitMarkersNumber);
			System.out.printf("\tCenter: (%d, %d)%n", centerX, centerY);
			
			if(debugMode) {
				System.out.printf("\tRectangle: corner (%d, %d), size (%d, %d)%n", sampleRectX, sampleRectY, sampleRectSize, sampleRectSize);
			}
			
			// print out sampled color of an ellipse
			System.out.printf("\tColor RGB: (%d, %d, %d)%n",
					(int)meanColorRBG.val[2], (int)meanColorRBG.val[1], (int)meanColorRBG.val[0]);
			
			
			
			legitMarkersNumber++;
			
		}

		ellipses.release();
		labColorspace.release();
		imageProcessedTmp.release();
		imageProcessed.release();
		

	}

}
