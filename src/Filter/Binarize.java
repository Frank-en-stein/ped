package Filter;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

import static com.googlecode.javacv.cpp.opencv_core.cvAdd;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_ADAPTIVE_THRESH_GAUSSIAN_C;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_DILATE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_ERODE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_SHAPE_ELLIPSE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvAdaptiveThreshold;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCanny;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCreateStructuringElementEx;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMorphologyEx;
import static com.googlecode.javacv.cpp.opencv_core.cvMerge;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

public class Binarize extends Filter {

	@Override
	public Boolean filter(IplImage src, IplImage dst) {
		
		CvSize size = new CvSize();
		size.width(src.width());
		size.height(src.height());
		
		IplImage R = cvCreateImage(size, IPL_DEPTH_8U, 1);
		IplImage G = cvCreateImage(size, IPL_DEPTH_8U, 1);
		IplImage B = cvCreateImage(size, IPL_DEPTH_8U, 1);
		cvSplit(src, R, G, B, null);
		cvEqualizeHist(B, B);

		
		IplImage temp = cvCreateImage(B.cvSize(), B.depth(), 1);
		IplConvKernel K = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_ELLIPSE,null );
		IplImage morph = cvCreateImage(B.cvSize(), B.depth(), 1);
		
		cvMorphologyEx( B, morph, temp, K, CV_MOP_ERODE,1);
		
		IplImage canny = cvCreateImage(B.cvSize(), B.depth(), 1);
		cvCanny(morph, canny, 10, 100, 3);
		
		cvMorphologyEx(canny, canny, temp, K, CV_MOP_DILATE, 1);
		
		cvAdaptiveThreshold(morph, morph, 255.0, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 31, 3);
		
		cvAdd(canny, morph, morph, null);

		cvMerge(morph, morph, morph, null, dst);
		return true;
	}

}
