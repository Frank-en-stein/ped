package camViewer;

import processing.core.*;

public class CamHiResThread extends Thread{

    private CamHiRes cam;

    PImage[] images;
    boolean[] updated;
    private boolean stop = false;
    static final int VIDEO_DROP = 0;
    static final int VIDEO_TAKE = 1;
    static final int VIDEO_TAKE_ONLY = 3;
    private int state = VIDEO_DROP;

    public CamHiResThread(PApplet parent, int camNo, 
			  int vw, int vh,
			  int imgW, int imgH,
			  PVector paperSize,
			  String calibrationYAML, String calibrationData){
	cam = new CamHiRes(parent, camNo, vw, vh, imgW, imgH, paperSize, calibrationYAML, calibrationData);

    }


    public CamHiResThread(CamHiRes cam){
	this.cam = cam;
	// this.updated = new boolean[cam.sheets.length];
	// for(int i =0; i < update.length; i++)
	//     updated = false;
    }
    
    public void filmImages(){
	this.state = VIDEO_TAKE;
    }

    public void dropImages(){
	this.state = VIDEO_DROP;
    }

    public PImage getImage(int id){
	return cam.getLastPaperView(id);
    }

    public void run(){

	while(!stop){
	    switch(state){
	    case VIDEO_DROP:
		cam.art.grab();	    
		break;
	    case VIDEO_TAKE:
		cam.getPaperView();
		break;
	    case VIDEO_TAKE_ONLY:
		cam.getPaperView();
		
		boolean hasTakenAll = true;
		for(int i =0; i < cam.sheets.length; i++){
		    if(cam.getLastPaperView(i) == null){
			hasTakenAll = false;
			break;
		    }
		}
		if(hasTakenAll)
		    stopThread();
		
	    }
	}

	cam.close();
    }

    public void stopThread(){
	stop = true;
    }

}
