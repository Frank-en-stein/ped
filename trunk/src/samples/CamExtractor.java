package samples;

import camViewer.CamHiRes;
import camViewer.CamHiResThread;
import camViewer.Extractor2;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ListBox;
import processing.core.*;;

public class CamExtractor extends PApplet{

	private static final long serialVersionUID = 1L;
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
	private Extractor2 ext;

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

		ext = new Extractor2();


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
		ext.start();


		// GUI
		controlP5 = new ControlP5(this);
		//controlP5.addButton("shot").setId(1);
		listimg = controlP5.addListBox("imgList", cameraHiX, 0, 100, cameraHiX);
		listimg.setItemHeight(23);
		

		String[][] s = new String[3][];
		s[0] = new String[] {
				"a","b","c","d"
		};
		s[1] = new String[] {
				"a","b","c","d","e","f","g","h","i","j","k","l","m","n"
		};
		s[2] = new String[] {
				"l","m","n"
		};

		for(int i=0;i<s[1].length;i++) {
			listimg.addItem(s[1][i],i);
			
		}
	}

	int currentImageSave = 0;
	private PImage computedRes = null;

	public void draw() {

		background(0);

		hint(ENABLE_DEPTH_TEST);
		if(test)
			camHiResThread.dropImages();
		else
			camHiResThread.filmImages();
		if(!ext.outQueue.isEmpty())
			computedRes = ext.outQueue.getFirst();
		if(ext.outQueue.size()>1)
			ext.outQueue.removeFirst();

		PImage camView = camHiResThread.getImage(0);    

		if(camView != null) 
			image(camView, 0, 0);
		if(computedRes != null){
			image(computedRes , cameraHiX+100, 0);
		}

		if(saveImages){
			PImage im0 = camHiResThread.getImage(0);
			if(im0 != null) {
				PImage out1 = createImage(cameraHiX, cameraHiY, RGB);
				out1.set(0, 0, im0.get());
				//out1.save("capImage1-"+ currentImageSave++ +".png");
				//println("Image sauvée, id : "+ (currentImageSave-1));
				//	    camView[0].save("/dev/shm/capRight.png");
				ext.inQueue.addLast(out1);
				System.out.println("Image Pushed");
				saveImages = false;
			}
		}

		// makes the gui stay on top of elements
		// drawn before.
		hint(DISABLE_DEPTH_TEST);

		controlP5.draw();
	}



	public void controlEvent(ControlEvent theEvent) {
		// ListBox is if type ControlGroup.
		// 1 controlEvent will be executed, where the event
		// originates from a ControlGroup. therefore
		// you need to check the Event with
		// if (theEvent.isGroup())
		// to avoid an error message from controlP5.

		if (theEvent.isGroup()) {
			// an event from a group e.g. scrollList
			println(theEvent.group().value()+" from "+theEvent.group());
		}
	}

	void shot(float theValue) {
		println(theValue);
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
