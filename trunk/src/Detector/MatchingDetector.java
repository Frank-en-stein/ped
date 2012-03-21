package Detector;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import java.util.LinkedList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;



public class MatchingDetector extends Detector{
	
	private int matchMethod = CV_TM_CCOEFF;
	
	public MatchingDetector() {
		templates = new LinkedList<IplImage>();
	}
	
	// Choix possibles : 
		// CV_TM_SQDIFF, CV_TM_CCORR, CV_TM_CCOEFF,
		// CV_TM_SQDIFF_NORMED, CV_TM_CCORR_NORMED, CV_TM_CCOEFF_NORMED 
	public void setMatchMethod(int method){
		matchMethod=method;
	}
	
	@Override
	public LinkedList<CvScalar> Detect(IplImage src) {
		LinkedList<CvScalar> rectangles = new LinkedList<CvScalar>();
		IplImage scene = src.clone();
		
		for(int i=0 ; i<templates.size() ; i++){
			CvSize size = new CvSize();
			size.width(Math.abs(scene.width() - getTemplate(i).width()) + 1);
			size.height(Math.abs(scene.height() - getTemplate(i).height()) + 1);
			
			IplImage tmp;
			tmp = cvCreateImage(size, IPL_DEPTH_32F, 1);
			
			long start = System.currentTimeMillis();
			
			cvMatchTemplate(scene, getTemplate(i), tmp, matchMethod);
			cvNormalize(tmp, tmp, 0, 1, CV_MINMAX, null);

			// Récupération du meilleur résultat
			CvPoint minLoc = new CvPoint();
			CvPoint maxLoc = new CvPoint();

			double[] minVal = new double[1];
			double[] maxVal = new double[1];

			cvMinMaxLoc (tmp, minVal, maxVal, minLoc, maxLoc, null );

			if(matchMethod == CV_TM_SQDIFF || matchMethod == CV_TM_SQDIFF_NORMED){
				CvScalar rect = cvScalar(minLoc.x(),minLoc.y(), getTemplate(i).width(), getTemplate(i).height());
				rectangles.add(rect);
			}

			else{
				CvScalar rect = cvScalar(maxLoc.x(),maxLoc.y(), getTemplate(i).width(), getTemplate(i).height());
				rectangles.add(rect);
			}
			System.out.println("Finding time = " + (System.currentTimeMillis() - start) + " ms");	
			
			
			/* This part can be used to avoid multiple detections on an object.
			 * It removes the zone where the template matches the most.
			 */
			CvPoint p1 = new CvPoint();
			CvPoint p2 = new CvPoint();
			p1.x((int)(rectangles.get(i).getVal(0)));
			p1.y((int)(rectangles.get(i).getVal(1)));
			p2.x((int)(rectangles.get(i).getVal(0) + rectangles.get(i).getVal(2)));
			p2.y((int)(rectangles.get(i).getVal(1) + rectangles.get(i).getVal(3)));
			
			cvRectangle(scene, p1, p2, cvScalar(0,0,0,0), CV_FILLED, 0, 0);
		}
		System.out.println("Prêt à afficher");
		return rectangles;
	}
}


