package samples.templateDetector;

import java.io.File;
import java.util.LinkedList;

import Detector.Detector;
import Detector.MatchingDetector;
import Filter.Binarize;
import Filter.Sauvola;
import Filter.FilterFactory;
import Filter.Grayscale;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.processing.Utils;

import processing.core.PImage;

public class ComputeThread extends Thread {

	private static Detector detector;
	public PImage src;
	public PImage dst;
	
	public ComputeThread(PImage src,PImage dst) {
		this.src = src; 
		this.dst = dst;
	}

	public static void initialize(){
		detector = new MatchingDetector();

		File templateDir = new File("../Ressources/templates");
		if(templateDir.isDirectory()){
			File[] list = templateDir.listFiles();
			if (list != null){
				for ( int i = 0; i < list.length; i++) {
					if(!list[i].isDirectory() && list[i].getName().endsWith(".jpg")){
						IplImage template = cvLoadImage(list[i].getAbsolutePath());
						
						Binarize filterBinarize = new Binarize();
						filterBinarize.apply(template, false);
						
						
						CvSize size = new CvSize();
						size.width(template.width());
						size.height(template.height());
						IplImage templateGray = cvCreateImage(size, IPL_DEPTH_8U, 1); 
						
						Grayscale filterGrayscale = (Grayscale) FilterFactory.getFilter(FilterFactory.GRAYSCALED);
						filterGrayscale.filter(template, templateGray);
						
						detector.addTemplate(templateGray);
						System.out.println("Template "+list[i].getName()+" Chargé");
					}
				} 
			} 
		} 
	}
	
	public void run(){

		System.out.println("Computing");
		
		CvSize size = new CvSize();
		size.width(src.width);
		size.height(src.height);

		IplImage iplimg = cvCreateImage(size, IPL_DEPTH_8U, 3);
		Utils.PImageToIplImage(src, iplimg, true);

		//preprocess
		
		IplImage res = iplimg.clone();
		Grayscale filterGrayscale = (Grayscale) FilterFactory.getFilter(FilterFactory.GRAYSCALED);
		IplImage resGray = cvCreateImage(size, IPL_DEPTH_8U, 1); 
		filterGrayscale.filter(res, resGray);
		
		Sauvola filter = new Sauvola();
		filter.apply(resGray, false);
		
		//detection
		LinkedList<CvScalar> resultats = detector.Detect(resGray);

		for(int i=0 ; i<resultats.size() ; i++){
			CvPoint p1 = new CvPoint();
			CvPoint p2 = new CvPoint();
			p1.x((int)(resultats.get(i).getVal(0)));
			p1.y((int)(resultats.get(i).getVal(1)));
			p2.x((int)(resultats.get(i).getVal(0) + resultats.get(i).getVal(2)));
			p2.y((int)(resultats.get(i).getVal(1) + resultats.get(i).getVal(3)));
			
			cvRectangle(iplimg, p1, p2, cvScalar(1,0,0,0), 2, 0, 0);
		}
				
		// End process
		this.dst = Utils.toPImage(iplimg);
	}

}
