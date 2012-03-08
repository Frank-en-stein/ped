package tmp;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import Filter.Blur;
import Filter.FilterFactory;
import Filter.Grayscale;

public class testfiltre {

	public static void main(String[] args) {
		IplImage img = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-12.png",CV_LOAD_IMAGE_UNCHANGED);
		IplImage dst = cvCreateImage(img.cvSize(), img.depth(), 3);
		IplImage canny = cvCreateImage(img.cvSize(), img.depth(), 1);
		IplImage diff = cvCreateImage(img.cvSize(), img.depth(), 1);
		IplImage thr = cvCreateImage(img.cvSize(), img.depth(), 1);
		IplImage closed = cvCreateImage(img.cvSize(), img.depth(), 1);
		IplImage temp = cvCreateImage(img.cvSize(), img.depth(), 1);
		cvCvtColor(img, dst,CV_RGB2YUV);
		IplImage h = cvCreateImage(img.cvSize(), img.depth(), 1);
		IplImage s = cvCreateImage(img.cvSize(), img.depth(), 1);
		IplImage v = cvCreateImage(img.cvSize(), img.depth(), 1);
		cvSplit(dst,h,s,v,null);
		cvCanny(h, canny, 10, 100, 3);
		cvSub(h, canny, diff,null);
		cvAdaptiveThreshold(diff, thr, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 15, 10);
		IplConvKernel B = cvCreateStructuringElementEx(5, 5, 1, 1, CV_SHAPE_ELLIPSE,null );
		cvMorphologyEx( thr, closed, temp, B, CV_MOP_GRADIENT,1);
		//cvMorphologyEx( closed, closed, temp, B, CV_MOP_CLOSE,2);
		
		Blur blur = (Blur) FilterFactory.getFilter(FilterFactory.BLUR);
		blur.size = 7;
		IplImage bluredImg = blur.apply(img, true);
		if(bluredImg==null)
			System.out.println("blurring error");

		Grayscale grayscale = (Grayscale) FilterFactory.getFilter(FilterFactory.GRAYSCALED);
		IplImage grayscaledImg = grayscale.apply(img, true);
		if(grayscaledImg==null)
			System.out.println("grayscaling error");

		// create image window named "My Image"
		final CanvasFrame canvas = new CanvasFrame("My Image");
		canvas.setSize(640, 480);
		// request closing of the application when the image window is closed
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		// show image on window
		System.out.println("ok");
		canvas.showImage(h);
		cvWaitKey(2000);
		canvas.showImage(canny);
		cvWaitKey(2000);
		canvas.showImage(diff);
		cvWaitKey(2000);
		canvas.showImage(thr);
		cvWaitKey(2000);
		canvas.showImage(closed);
	}

}
