package samples;

import Filter.Binarize;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
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
		
		IplImage iplimg = cvCreateImage(size, IPL_DEPTH_8U, 3);
		Utils.PImageToIplImage(src, iplimg, true);

		Binarize nico = new Binarize();
		IplImage res = nico.apply(iplimg, true);
				
		// End process
		this.dst = Utils.toPImage(res);
	}

}
