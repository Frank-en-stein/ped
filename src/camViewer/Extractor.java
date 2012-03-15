package camViewer;

import java.io.File;
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
	Detector detector;

	private IplImage background;

	public Extractor() {
		this.file = new LinkedList<PImage>();
		this.background = null;
	}

	public void run(){

		initialize();

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
				//IplImage scene = cvLoadImage("../Ressources/scan.jpg",CV_LOAD_IMAGE_GRAYSCALE);
				//IplImage result = detector.Detect(iplImgGray);

				final CanvasFrame canvas = new CanvasFrame("My Image",1);
				canvas.setCanvasSize(640, 480);
				canvas.setLocation(640, 100);
				canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

				// show image on window
				//canvas.showImage(result);

				System.out.println("Image Poped");
			}
		}

	}

	private void initialize() {
		// TODO Auto-generated method stub
		detector = new SurfDetector();

		File templateDir = new File("../Ressources/templates");
		if(templateDir.isDirectory()){
			File[] list = templateDir.listFiles();
			if (list != null){
				for ( int i = 0; i < list.length; i++) {
					if(!list[i].isDirectory() && list[i].getName().endsWith(".jpg")){
						IplImage template = cvLoadImage(list[i].getAbsolutePath(),CV_LOAD_IMAGE_GRAYSCALE); 
						detector.addTemplate(template);
						System.out.println("Template "+list[i].getName()+" Chargé");
					}
				} 
			} 
		} 
	}

	public void stopThread(){
		stop = true;
	}
}
