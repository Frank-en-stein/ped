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

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Extractor2 extends Thread {
	private boolean stop = false; 
	public LinkedList<PImage> inQueue;
	public LinkedList<PImage> outQueue;
	private IplImage background;

	public Extractor2() {
		this.inQueue = new LinkedList<PImage>();
		this.outQueue = new LinkedList<PImage>();
		this.background = null;
	}

	public void run(){
		
		while(!stop || !inQueue.isEmpty()){
			System.out.flush();
			if(!inQueue.isEmpty()){
				System.out.println("traitement");
				// recupération de l'image
				PImage pimg = inQueue.removeFirst();
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
				IplImage template = cvLoadImage("../Ressources/templates/man.jpg",CV_LOAD_IMAGE_GRAYSCALE); 
				//IplImage template2 = cvLoadImage("/net/cremi/nmestrea/espaces/travail/ped/Ressources/tieFighter.jpg",CV_LOAD_IMAGE_GRAYSCALE);
				Detector detector = new SurfDetector();
				detector.addTemplate(template);
				//detector.addTemplate(template2);
				IplImage result = detector.Detect(iplImgGray);		
				
				PImage Presult = Utils.toPImage(result);
				outQueue.addLast(Presult);
				
				System.out.println("Image Poped "+outQueue.size());
			}
		}

	}

	public void stopThread(){
		stop = true;
	}
}
