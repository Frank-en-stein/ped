package samples.featureExtractor;

import camViewer.CamHiRes;
import camViewer.CamHiResThread;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ListBox;
import processing.core.*;;

public class CamExtractor extends PApplet{

	private static final long serialVersionUID = 1L;
	private static final int NB_MAX_IMAGES = 100;
	// PLAYER 
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

	//GUI
	ControlP5 controlP5;
	ListBox listimg;

	int buttonValue = 1;

	public void setup() {

		// player
		// TODO: Local folder 

		sketchBookFolder = sketchPath + "/../src/camViewer";
		calibrationBoardL = sketchBookFolder + "/data/my_markerboarda3v1.cfg";
		boards = new String[1];

		////////////// Camera parameters //////////////////////////////

		cameraHiRes = 1;
		//cameraHiX = 1712;
		//cameraHiY = 960;

		cameraHiX = 640;
		cameraHiY = 480;

		////////////// Paper sheet parameters ////////////////////////

		paperSheetWidth = 297;
		paperSheetHeight = 210;

		videoFileName = "/net/cremi/tmanson/ped/videos/capture.avi";
		useVideo = true;

		size(cameraHiX*2+100, cameraHiY, P2D);

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
					sketchBookFolder + "/data/calib1-art-p2.txt",boards[0]);

		if(useVideo)
			camHiRes = new CamHiRes(this, videoFileName,    // 
					cameraHiX, cameraHiY,  // logitech ... 
					cameraHiX, cameraHiY,  // logitech ... 
					// TODO: avec autre résolutions !!
					//			  870, 630,  // ~ 20x 21 *29,7
					new PVector(paperSheetWidth, paperSheetHeight),
					sketchBookFolder + "/data/calibration-p1.yaml",
					sketchBookFolder + "/data/calib1-art.txt",boards);



		camHiResThread = new CamHiResThread(camHiRes);  
		camHiResThread.start();
		
		takenImages = new PImage[NB_MAX_IMAGES];
		computedImages = new PImage[NB_MAX_IMAGES];
		comp = new ComputeThread[NB_MAX_IMAGES];

		// GUI
		controlP5 = new ControlP5(this);
		controlP5.addButton("shot").setId(1);
		listimg = controlP5.addListBox("imgList", cameraHiX, 0, 100, cameraHiY);
		listimg.captionLabel().multiline(true);
		listimg.setItemHeight(23);
	}

	private PImage camView;
	private PImage resultImage = null;
	public void draw() {
		background(0);

		hint(ENABLE_DEPTH_TEST);
		if(play)
			camHiResThread.filmImages();

		// affiche la video
		camView = camHiResThread.getImage(0);    
		if(camView != null) 
			image(camView, 0, 0);

		// affichage resultat
		if(resultImage != null)
			image(resultImage , cameraHiX+100, 0);

		// makes the gui stay on top of elements drawn before.
		hint(DISABLE_DEPTH_TEST);

		controlP5.draw();
	}

	int nbFrames = 0;
	private PImage[] takenImages;
	private PImage[] computedImages;
	ComputeThread[] comp;
	void shot(float theValue) {
		listimg.addItem("Image n°"+Integer.toString(nbFrames),nbFrames);

		camHiResThread.pause();
		if(camView != null) {
			takenImages[nbFrames] = createImage(cameraHiX, cameraHiY, RGB);
			takenImages[nbFrames].set(0, 0, camView.get());
			//out1.save("capImage1-"+ currentImageSave++ +".png");
			//println("Image sauvée, id : "+ (currentImageSave-1));
			//	    camView[0].save("/dev/shm/capRight.png");

			comp[nbFrames] = new ComputeThread(takenImages[nbFrames],computedImages[nbFrames]);
			comp[nbFrames].setDaemon(true);
			comp[nbFrames].start();
		}
		nbFrames++;
	}

	public void controlEvent(ControlEvent theEvent) {

		if (theEvent.isGroup()) {
			int ID = (int)theEvent.group().value();
			resultImage = comp[ID].dst;
		}
	}
	
	boolean play = true;
	public void keyPressed() {
		if(key == ' '){
			play = !play;
			if(play)
				camHiResThread.filmImages();
			else
				camHiResThread.pause();
		}
	}

	public void stop() {
		super.stop();
	}

}
