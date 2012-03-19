package samples;

import Filter.Sauvola;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.processing.Utils;

import processing.core.PImage;

public class ComputeThread extends Thread {
	public PImage src;
	public PImage dst;

	public ComputeThread(PImage src,PImage dst) {
		this.src = src; 
		this.dst = dst;
	}

	public void run(){

		System.out.println("Computing");

		CvSize size = new CvSize();
		size.width(src.width);
		size.height(src.height);
		IplImage iplimg = cvCreateImage(size, IPL_DEPTH_8U, 1);
		Utils.PImageToIplImage(src, iplimg, false);
		cvEqualizeHist(iplimg, iplimg);
		Sauvola sauvola = new Sauvola();
		sauvola.apply(iplimg, false);

		//cvThreshold(iplimg,iplimg,120.0,255.0,CV_THRESH_TOZERO);
		// End process
		this.dst = Utils.toPImage(iplimg);
	}

}

