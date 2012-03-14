package samples.templateDetector;

import java.io.File;

import Detector.Detector;
import Detector.MatchingDetector;
import Detector.SurfDetector;
import Filter.Binarize;
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
		detector = new SurfDetector();

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
		Binarize filter = new Binarize();
		IplImage res = filter.apply(iplimg, true);
		
		Grayscale filterGrayscale = (Grayscale) FilterFactory.getFilter(FilterFactory.GRAYSCALED);
		IplImage resGray = cvCreateImage(size, IPL_DEPTH_8U, 1); 
		filterGrayscale.filter(res, resGray);
		
		//detection
		resGray = detector.Detect(resGray);
				
		// End process
		this.dst = Utils.toPImage(resGray);
	}

}
