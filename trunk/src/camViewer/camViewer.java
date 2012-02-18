package camViewer;

import processing.core.*;

public class camViewer extends PApplet {
	// TODO: Local folder 
	String sketchBookFolder = "/net/cremi/tmanson/espaces/travail/ped-workspace/PED/Ressources/camViewer";
	String calibrationBoardL = sketchBookFolder + "/data/my_markerboarda3v1.cfg";
	public String[] boards = new String[1];

	////////////// Camera parameters //////////////////////////////

	int cameraHiRes = 1;
	int cameraHiX = 1712;
	int cameraHiY = 960;

	////////////// Paper sheet parameters ////////////////////////

	int paperSheetWidth = 297;
	int paperSheetHeight = 210;


	CamHiRes camHiRes;
	CamHiResThread camHiResThread;


	public void setup(){ 

		size(cameraHiX, cameraHiY, P3D);

		// A4  21. * 29.7 cm.
		//  paperSheet = new PaperSheet(paperSheetWidth, paperSheetHeight, 4);
		//  DrawUtils.applet = this;

		// TODO: cleaner ?
		boards = new String[1];
		boards[0] = calibrationBoardL;

		// TODO: fichier de config ?
		camHiRes = new CamHiRes(this, cameraHiRes,    // 
				cameraHiX, cameraHiY,  // logitech ... 
				cameraHiX, cameraHiY,  // logitech ... 
				// TODO: avec autre résolutions !!
				//			  870, 630,  // ~ 20x 21 *29,7
				new PVector(paperSheetWidth, paperSheetHeight),
				sketchBookFolder + "/data/calibration-p2.yaml",
				sketchBookFolder + "/data/calib1-art-p2.txt");

		camHiResThread = new CamHiResThread(camHiRes);  
		camHiResThread.start();

		//  frameRate(200);
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
				out1.save("capImage1-"+ currentImageSave++ +".png");
				println("Image sauvée, id : "+ (currentImageSave-1));
				//	    camView[0].save("/dev/shm/capRight.png");
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

