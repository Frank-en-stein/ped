package Detector;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import java.util.LinkedList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;



public class MatchingDetector extends Detector{
	
	private int matchMethod;
	
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
	public IplImage Detect(IplImage scene) {
		IplImage result;
		result = scene.clone();
		
		for(int i=0 ; i<templates.size() ; i++){
			CvSize size = new CvSize();
			size.width(Math.abs(scene.width() - getTemplate(i).width()) + 1);
			size.height(Math.abs(scene.height() - getTemplate(i).height()) + 1);
			
			IplImage tmp;
			tmp = cvCreateImage(size, IPL_DEPTH_32F, 1);
			
			long start = System.currentTimeMillis();
			System.out.println("Scene : "+scene.width()+","+scene.height());
			System.out.println("Templ : "+getTemplate(i).width()+","+getTemplate(i).height());
			System.out.println("Tmp   : "+tmp.width()+","+tmp.height());
			
			cvMatchTemplate(scene, getTemplate(i), tmp, matchMethod);
			cvNormalize(tmp, tmp, 0, 1, CV_MINMAX, null);

			// Récupération du meilleur résultat
			CvPoint minLoc = new CvPoint();
			CvPoint maxLoc = new CvPoint();

			double[] minVal = new double[1];
			double[] maxVal = new double[1];

			cvMinMaxLoc (tmp, minVal, maxVal, minLoc, maxLoc, null );

			CvPoint tempRect0 = new CvPoint();
			CvPoint tempRect1 = new CvPoint();

			if(matchMethod == CV_TM_SQDIFF || matchMethod == CV_TM_SQDIFF_NORMED){
				tempRect0.x(minLoc.x());
				tempRect0.y(minLoc.y());
				tempRect1.x(minLoc.x() + getTemplate(i).width());
				tempRect1.y(minLoc.y() + getTemplate(i).height());
			}

			else{
				tempRect0.x(maxLoc.x());
				tempRect0.y(maxLoc.y());
				tempRect1.x(maxLoc.x() + getTemplate(i).width());
				tempRect1.y(maxLoc.y() + getTemplate(i).height());
			}
			System.out.println("Finding time = " + (System.currentTimeMillis() - start) + " ms");
			
			// Creation Image résultat
			double SEUIL_MATCHING = 0.90;
			if(matchMethod == CV_TM_SQDIFF || matchMethod == CV_TM_SQDIFF_NORMED){
				if(cvGet2D(tmp, tempRect0.y(), tempRect0.x()).getVal(0)<=1.0-SEUIL_MATCHING){
					cvRectangle( result, tempRect0, tempRect1, cvScalar( 1, 0, 0, 0 ), 2, 0, 0 ); 
				}
			}
			else{
				if(cvGet2D(tmp, tempRect0.y(), tempRect0.x()).getVal(0)>=SEUIL_MATCHING){
					cvRectangle( result, tempRect0, tempRect1, cvScalar( 1, 0, 0, 0 ), 2, 0, 0 ); 
				}
			}
		}
		System.out.println("Prêt à afficher");
		return result;
	}
}


