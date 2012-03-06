package camViewer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.processing.*;

import java.util.concurrent.Semaphore;
import javax.media.opengl.GL;

import processing.core.*;
import processing.opengl.*;

public class CamViewer extends PApplet{

	private static final long serialVersionUID = 1L;
	private String sketchBookFolder;
	private String calibrationBoardL;
	String[] boards;
	private int cameraHiRes;
	private int cameraHiX;
	private int cameraHiY;
	private int paperSheetWidth;
	private int paperSheetHeight;
	private CamHiRes camHiRes;
	private CamHiResThread camHiResThread;
	private String videoFileName;
	private boolean useVideo;
	private Extractor ext;
	
	public void setup(){ 
		// TODO: Local folder 
		
		sketchBookFolder = sketchPath + "/../src/camViewer";
		calibrationBoardL = sketchBookFolder + "/data/my_markerboarda3v1.cfg";
		boards = new String[1];
		
		////////////// Camera parameters //////////////////////////////

		cameraHiRes = 1;
		// int cameraHiX = 1712;
		// int cameraHiY = 960;

		cameraHiX = 640;
		cameraHiY = 480;

		////////////// Paper sheet parameters ////////////////////////

		paperSheetWidth = 297;
		paperSheetHeight = 210;

		videoFileName = "/net/cremi/nmestrea/ped/Ressources/capture.avi";
		useVideo = true;

		ext = new Extractor();

		
		size(cameraHiX, cameraHiY, P3D);

		// A4  21. * 29.7 cm.
		//  paperSheet = new PaperSheet(paperSheetWidth, paperSheetHeight, 4);
		//  DrawUtils.applet = this;

		// TODO: cleaner ?
		boards = new String[1];
		boards[0] = calibrationBoardL;

		// TODO: fichier de config ?
		if(!useVideo)
			camHiRes = new CamHiRes(this, cameraHiRes,    // 
					cameraHiX, cameraHiY,  // logitech ... 
					cameraHiX, cameraHiY,  // logitech ... 
					// TODO: avec autre résolutions !!
					//			  870, 630,  // ~ 20x 21 *29,7
					new PVector(paperSheetWidth, paperSheetHeight),
					sketchBookFolder + "/data/calibration-p2.yaml",
					sketchBookFolder + "/data/calib1-art-p2.txt");

		if(useVideo)
			camHiRes = new CamHiRes(this, videoFileName,    // 
					cameraHiX, cameraHiY,  // logitech ... 
					cameraHiX, cameraHiY,  // logitech ... 
					// TODO: avec autre résolutions !!
					//			  870, 630,  // ~ 20x 21 *29,7
					new PVector(paperSheetWidth, paperSheetHeight),
					sketchBookFolder + "/data/calibration-p1.yaml",
					sketchBookFolder + "/data/calib1-art.txt");



		camHiResThread = new CamHiResThread(camHiRes);  
		camHiResThread.start();
ext.start();
		//frameRate(200);
	}


	int currentImageSave = 0;

	public void draw(){

		background(0);

		if(test)
			camHiResThread.dropImages();
		else
			camHiResThread.filmImages();

		PImage camView = camHiResThread.getImage(0);    

		if(camView != null) 
			image(camView, 0, 0);

		if(saveImages){
			PImage im0 = camHiResThread.getImage(0);
			if(im0 != null) {
				PImage out1 = createImage(cameraHiX, cameraHiY, RGB);
				out1.set(0, 0, im0.get());
				//out1.save("capImage1-"+ currentImageSave++ +".png");
				//println("Image sauvée, id : "+ (currentImageSave-1));
				//	    camView[0].save("/dev/shm/capRight.png");
				ext.file.addLast(out1);
				System.out.println("Image Pushed");
				saveImages = false;
			}
		}

	}


	// DEBUG VARIABLES
	boolean test = false;
	int skip = 0;

	boolean stopProj = false;
	boolean saveImages = false;

	public void keyPressed() {

		if(key == 't')
			test = !test;

		if(!test)
			println("Taking images...");
		else
			println("dropping images");

		if(key == 'a')
			saveImages = true;
	}


	public void stop() {
		super.stop();
	}
}



