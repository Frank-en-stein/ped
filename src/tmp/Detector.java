package tmp;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import java.awt.Dimension;
import java.awt.Toolkit;

import com.googlecode.javacv.CanvasFrame;

public class Detector{

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/// Global Variables
		int NB_TEMPLATES = 5;

		IplImage img, result;
		IplImage templates[] = new IplImage[NB_TEMPLATES];

		String imgFolder = "/net/cremi/nmestrea/espaces/travail/ped/Ressources/";
		String imgName = "scan_rotate.jpg";

		String templateNames[] = new String[NB_TEMPLATES];
		templateNames[0] = "man.jpg";
		templateNames[1] = "man2.jpg";
		templateNames[2] = "rect.jpg";
		templateNames[3] = "rect2.jpg";
		templateNames[4] = "tieFighter_gross2.jpg";

		System.out.println("Chargement image principale");
		img=cvLoadImage(imgFolder+imgName,1);
		if(img==null) System.out.println("Could not load image file: "+imgFolder+imgName);

		System.out.println("Chargement templates");
		for(int i=0 ; i<NB_TEMPLATES ; i++){
			System.out.println("--- "+imgFolder+templateNames[i]);
			templates[i] = cvLoadImage(imgFolder+templateNames[i],1);
			if(templates[i]==null) System.out.println("Could not load image file: "+imgFolder+templateNames[i]);
		}

		System.out.println("Matching...");
		for(int i=0 ; i<NB_TEMPLATES ; i++){
			System.out.println("--- "+imgFolder+templateNames[i]);

			CvSize size = new CvSize();
			size.width(Math.abs(img.width() - templates[i].width()) + 1);
			size.height(Math.abs(img.height() - templates[i].height()) + 1);

			result = cvCreateImage(size, IPL_DEPTH_32F, 1);

			/* Choix possibles : 
			 * CV_TM_SQDIFF, CV_TM_CCORR, CV_TM_CCOEFF,
			 * CV_TM_SQDIFF_NORMED, CV_TM_CCORR_NORMED, CV_TM_CCOEFF_NORMED */

			int matchMethod = CV_TM_SQDIFF_NORMED;
			cvMatchTemplate(img, templates[i], result, matchMethod);
			cvNormalize(result, result, 0, 1, CV_MINMAX, null);

			CvPoint minLoc = new CvPoint();
			CvPoint maxLoc = new CvPoint();

			double[] minVal = new double[1];
			double[] maxVal = new double[1];

			cvMinMaxLoc (result, minVal, maxVal, minLoc, maxLoc, null );

			CvPoint tempRect0 = new CvPoint();
			CvPoint tempRect1 = new CvPoint();

			if(matchMethod == CV_TM_SQDIFF || matchMethod == CV_TM_SQDIFF_NORMED){
				tempRect0.x(minLoc.x());
				tempRect0.y(minLoc.y());
				tempRect1.x(minLoc.x() + templates[i].width());
				tempRect1.y(minLoc.y() + templates[i].height());
			}

			else{
				tempRect0.x(maxLoc.x());
				tempRect0.y(maxLoc.y());
				tempRect1.x(maxLoc.x() + templates[i].width());
				tempRect1.y(maxLoc.y() + templates[i].height());
			}
			
			// draw rectangle
			double SEUIL_MATCHING = 0.90;
			if(matchMethod == CV_TM_SQDIFF || matchMethod == CV_TM_SQDIFF_NORMED){
				if(cvGet2D(result, tempRect0.y(), tempRect0.x()).getVal(0)<=1.0-SEUIL_MATCHING){
					cvRectangle( img, tempRect0, tempRect1, cvScalar( 1, 0, 0, 0 ), 10, 0, 0 ); 
				}
			}
			else{
				if(cvGet2D(result, tempRect0.y(), tempRect0.x()).getVal(0)>=SEUIL_MATCHING){
					cvRectangle( img, tempRect0, tempRect1, cvScalar( 1, 0, 0, 0 ), 10, 0, 0 ); 
				}
			}
		}
		
		System.out.println("Prêt à afficher");
		//cvSaveImage("/net/cremi/nmestrea/espaces/travail/ped/Ressources/scan_copy.jpg",img);	  

		// create image window
		final CanvasFrame canvas = new CanvasFrame("Template Matching");

		Toolkit t = canvas.getToolkit();
		Dimension d = t.getScreenSize();
		int w = d.width;
		int h = d.height; 
		canvas.setCanvasSize(800, 600);
		canvas.setLocation((w-canvas.getWidth())/2, (h-canvas.getHeight())/2);

		// request closing of the application when the image window is closed
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		// show image on window
		canvas.showImage(img);
	}
}


