package camViewer;

import java.util.LinkedList;

import Detector.*;
import Filter.FilterFactory;
import Filter.Grayscale;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.processing.Utils;

import processing.core.PImage;

public class Extractor extends Thread {
	private boolean stop = false; 
	public LinkedList<PImage> file;
	
	private IplImage background;

	public Extractor() {
		this.file = new LinkedList<PImage>();
		this.background = null;
	}

	public void run(){
		
		while(!stop || !file.isEmpty()){
			System.out.flush();
			if(!file.isEmpty()){
				System.out.println("traitement");
				// recupération de l'image
				PImage pimg = file.removeFirst();
				CvSize size = new CvSize();
				size.width(pimg.width);
				size.height(pimg.height);
				IplImage iplImg = cvCreateImage(size, IPL_DEPTH_8U, 3);
				Utils.PImageToIplImage(pimg, iplImg, true);
				
				Grayscale filter = (Grayscale) FilterFactory.getFilter(FilterFactory.GRAYSCALED);
				IplImage iplImgGray = cvCreateImage(size, IPL_DEPTH_8U, 1);
				filter.filter(iplImg, iplImgGray);
				
				if(background==null && iplImg!=null)
					background = iplImg;

				// traitement
				//iplImg = Filter.process(iplImg);
				//IplImage scene = cvLoadImage("/net/cremi/nmestrea/espaces/travail/ped/Ressources/scan_rotate.jpg",CV_LOAD_IMAGE_GRAYSCALE);
				IplImage template = cvLoadImage("/net/cremi/nmestrea/espaces/travail/ped/Ressources/man2.jpg",CV_LOAD_IMAGE_GRAYSCALE); 
				//IplImage template2 = cvLoadImage("/net/cremi/nmestrea/espaces/travail/ped/Ressources/tieFighter.jpg",CV_LOAD_IMAGE_GRAYSCALE);
				Detector detector = new MatchingDetector();
				detector.addTemplate(template);
				//detector.addTemplate(template2);
				IplImage result = detector.Detect(iplImgGray);
				
				final CanvasFrame canvas = new CanvasFrame("My Image",1);
				canvas.setCanvasSize(640, 480);
				canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

				// show image on window
				canvas.showImage(result);
				
				System.out.println("Image Poped");
			}
		}

	}

	public void stopThread(){
		stop = true;
	}
}
