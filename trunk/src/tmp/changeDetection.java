package tmp;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import processing.core.PImage;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_legacy.*;

public class changeDetection {

	public static int[] getHistogram(IplImage image){
		int [] hist = new int[(int) image.highValue()+1];
		for(int i=0; i<image.height();i++)
			for(int j=0; j< image.width(); j++){
				CvScalar p = cvGet2D(image, i, j);
				int x = (int) p.getVal(0);
				hist[x]++;
			}
		return hist;
	}

	public static int calcMediane(IplImage image, int[] histo){
		int med = 0;
		float count = 0;
		while(count<=image.width()*image.height() && med<histo.length){
			count += histo[med++];
		}
		return med-1;
	}

	public static IplImage difference(IplImage image1, IplImage image2){

		IplImage res= cvCreateImage(image1.cvSize(), image1.depth(), image1.nChannels());
		IntBuffer buff1 = image1.getIntBuffer();
		IntBuffer buff2 = image2.getIntBuffer();
		System.out.println(image1.width()*image1.height());
		for (int i = 0; i < image1.width() * image1.height(); i++) {
			int index = i;
			CvScalar p1 = cvGet1D(image1, i);
			CvScalar p2 = cvGet1D(image2, i);
			double b1 = 255-p1.getVal(0);
			double b2 = 255-p2.getVal(0);
			double val = 255-(b1-b2);
			cvSet1D(res, i, CV_RGB(val, val, val));
		}
		return res;
	}


	public static void main(String[] args) {
		IplImage im1 = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-26.png",CV_LOAD_IMAGE_GRAYSCALE);
		IplImage im2 = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-32.png",CV_LOAD_IMAGE_GRAYSCALE);
		int size = im1.height()*im1.width();
		IplImage new_areas= cvCreateImage(im1.cvSize(), im1.depth(), im1.nChannels());

		// create image window named "My Image"
		final CanvasFrame canvas = new CanvasFrame("My Image");
		canvas.setSize(640, 480);
		// request closing of the application when the image window is closed
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		IplImage res = difference(im2, im1);
		// show image on window
		System.out.println("ok");
		canvas.showImage(im1);
		cvWaitKey(2000);
		canvas.showImage(im2);
		cvWaitKey(2000);
		canvas.showImage(res);
	}

}