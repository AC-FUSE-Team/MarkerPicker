package application;

import org.opencv.core.Core;
import org.opencv.core.Scalar;
import org.opencv.core.Point;

public class Marker {

	/* color[0] - BGR color
	 * color[1] - Lab color
	 */
	Scalar[] color;

	Point center;
	int distance;
	// Status of marker in the scene:
	// 0 - not found; 1 - found
	int status;

	public Marker()
	{
		//;
	}
	
	public void setCenter(Point inCenter) {
		center =  inCenter;
	}
	
	public Point getCenter() {
		return center;
	}
	
	public void setDistance(int inDistance) {
		distance = inDistance;
	}
	
	public int getDistance() {
		return distance;
	}
	
	/* TODO: check for valid value */
	public void setStatus(int inStatus) {
		status = inStatus;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setColor(Scalar[] inColor) {
		color = inColor;
	}
	
	public Scalar[] getColor() {
		return color;
	}
	
}
