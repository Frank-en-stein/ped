package Filter;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_core.cvSub;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;

import com.googlecode.javacpp.Loader;
import  com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.*;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_ADAPTIVE_THRESH_MEAN_C;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_GRADIENT;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2YUV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2RGB;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.util.LinkedList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

public class NoveltyMask extends Filter {
	LinkedList<IplImage> previous;

	@Override
	public Boolean filter(IplImage src, IplImage dst) {
		if(previous==null)
			previous = new LinkedList<IplImage>();


		/*Blur blur = (Blur) FilterFactory.getFilter(FilterFactory.BLUR);
		blur.size = 3;
		blur.smoothType = CV_MEDIAN;
		blur.filter(src, dst);*/

		/*IplImage temp = cvCreateImage(src.cvSize(), src.depth(), 1);
		IplConvKernel Kerosion = cvCreateStructuringElementEx(7, 7, 3, 3, CV_SHAPE_ELLIPSE,null );
		IplConvKernel Kdilatation = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_ELLIPSE,null );
		IplImage erosion= cvCreateImage(src.cvSize(), src.depth(), 3);
		IplImage dilatation= cvCreateImage(src.cvSize(), src.depth(), 3);
		cvMorphologyEx( src, dilatation, temp, Kdilatation, CV_MOP_DILATE,1);
		cvMorphologyEx( src, erosion, temp, Kerosion, CV_MOP_ERODE,3);
		cvSub(dilatation, erosion, dst, null);*/
		IplImage dstGray = cvCreateImage(src.cvSize(), src.depth(), 1);
	//	cvCvtColor(dst, dstGray, CV_RGB2GRAY);
		CvMemStorage storage = cvCreateMemStorage(0);
		CvSeq contour = null;
		cvFindContours(src, storage, contour,Loader.sizeof(CvContour.class) , CV_RETR_TREE, CV_LINK_RUNS);

		int i=0;
		//cvZero( dst );
		for( ; contour != null; contour = contour.h_next())
		{	
			CvScalar color = CV_RGB( Math.random(), Math.random(), Math.random() );
			/* replace CV_FILLED with 1 to see the outlines */
			cvDrawContours( dst, contour, color, color, -1, 1, 8 );
			i++;
		}
		//cvCanny(dstGray, dstGray, 10, 10, 7);
		//cvAdaptiveThreshold(dstGray, dstGray, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 5, 10);
		//cvCvtColor(dstGray, dst, CV_GRAY2RGB);

		/*IplImage canny = cvCreateImage(src.cvSize(), src.depth(), 1);
		IplImage diff = cvCreateImage(src.cvSize(), src.depth(), 1);
		IplImage thr = cvCreateImage(src.cvSize(), src.depth(), 1);
		IplImage closed = cvCreateImage(src.cvSize(), src.depth(), 1);
		IplImage temp = cvCreateImage(src.cvSize(), src.depth(), 1);*/


		/*cvCvtColor(src, dst,CV_RGB2YUV);
		IplImage h = cvCreateImage(src.cvSize(), src.depth(), 1);
		IplImage s = cvCreateImage(src.cvSize(), src.depth(), 1);
		IplImage v = cvCreateImage(src.cvSize(), src.depth(), 1);
		cvSplit(dst,h,s,v,null);*/


		//cvCanny(h, canny, 90, 110, 7);

		//cvSub(h, canny, diff,null);
		//cvAdaptiveThreshold(diff, thr, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 15, 10);
		//IplConvKernel B = cvCreateStructuringElementEx(3, 3, 1, 1, CV_SHAPE_CROSS,null );
		//cvMorphologyEx( thr, closed, temp, B, CV_MOP_GRADIENT,1);
		//cvMorphologyEx( closed, closed, temp, B, CV_MOP_CLOSE,2);

		//sauvegarde du masque corresondant au dessin
		//previous.add(closed);

		// calcul des difference avec l'image precedente
		if(previous.size()>1){

		}
		System.out.println("end");
		//cvConvertImage(closed,dst,CV_GRAY2RGB);
		return true;
	}

}
