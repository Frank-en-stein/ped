package camViewer;

import java.util.LinkedList;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.processing.Utils;

import processing.core.PImage;
import tmp.Filter;

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
			System.out.println("nsdv");
			if(!file.isEmpty()){
				System.out.println("traitement");
				// recupération de l'image
				PImage pimg = file.removeFirst();
				CvSize size = new CvSize();
				size.width(pimg.width);
				size.height(pimg.height);
				IplImage iplImg = cvCreateImage(size, IPL_DEPTH_8U, 3);
				Utils.PImageToIplImage(pimg, iplImg, true);
				
				if(background==null && iplImg!=null)
					background = iplImg;

				// traitement
				//iplImg = Filter.process(iplImg);
				
				final CanvasFrame canvas = new CanvasFrame("My Image",1);
				canvas.setSize(640, 480);
				canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

				// show image on window
				canvas.showImage(iplImg);
				
				System.out.println("Image Poped");
			}
		}

	}

	public void stopThread(){
		stop = true;
	}
}
