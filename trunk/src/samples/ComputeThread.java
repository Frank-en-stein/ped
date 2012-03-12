package samples;

import Detector.*;
import Filter.FilterFactory;
import Filter.Grayscale;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
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
		IplImage iplImg = cvCreateImage(size, IPL_DEPTH_8U, 3);
		Utils.PImageToIplImage(src, iplImg, true);

		// Process
		Grayscale filter = (Grayscale) FilterFactory.getFilter(FilterFactory.GRAYSCALED);
		IplImage iplImgGray = cvCreateImage(size, IPL_DEPTH_8U, 1);
		filter.filter(iplImg, iplImgGray);

		IplImage template = cvLoadImage("../Ressources/templates/man.jpg",CV_LOAD_IMAGE_GRAYSCALE); 
		
		Detector detector = new SurfDetector();
		detector.addTemplate(template);
		
		IplImage result = detector.Detect(iplImgGray);		

		// End process
		this.dst = Utils.toPImage(result);
	}

}
