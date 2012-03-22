package samples.featureExtractor;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import Filter.Sauvola;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.processing.Utils;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ListBox;
import processing.core.*;

public class FeatureExtractor extends PApplet {

	private static final long serialVersionUID = 1L;

	//GUI
	private int imgShowWidth;
	private int imgShowHeight;

	private ControlP5 controlP5;
	private ListBox listimgG, listimgD;
	private Button compButton;

	private PImage imgG=null;
	private PImage imgD=null;
	private int imgGID;
	private int imgDID;
	private PImage imgResult=null;
	private PImage[][] resultCaching;

	// liste des images
	private String imagePath;
	private int imgsHeight;
	private int imgsWidth;
	private int NB_IMAGES = 11;
	private PImage[] img= new PImage[NB_IMAGES];






	public void setup() {

		// GUI
		controlP5 = new ControlP5(this);
		compButton = controlP5.addButton("compute");
		compButton.setLabel("Run");
		listimgG = controlP5.addListBox("imgListG", screenWidth/2-100, 0, 100, screenHeight);
		listimgG.captionLabel().multiline(true);
		listimgG.setItemHeight(23);
		listimgD = controlP5.addListBox("imgListD", screenWidth/2, 0, 100, screenHeight);
		listimgD.captionLabel().multiline(true);
		listimgD.setItemHeight(23);

		// chargement des images
		imagePath = sketchPath + "/../Ressources/debian girl/";
		for(int i=0; i<img.length; i++){
			img[i]=loadImage(imagePath+"capImage1-"+i+".png");
			listimgG.addItem("capImage1-"+i, i);
			listimgD.addItem("capImage1-"+i, i);
		}

		imgsWidth = img[0].width;
		imgsHeight = img[0].height;
		double ratio = 1.0*imgsWidth/imgsHeight;
		imgShowWidth = 500;//((screenWidth/2-100<imgsWidth)?(((screenWidth)/2)-100):(imgsWidth/2)-100);
		imgShowHeight = (int) (imgShowWidth/ratio);

		// lancement position du boutton de lancement de calcul
		compButton.setPosition(10, imgShowHeight+10);

		resultCaching = new PImage[img.length][img.length];
		size(screenWidth,imgShowHeight*2);
	}

	public void draw() {
		background(0);

		hint(ENABLE_DEPTH_TEST);
		if(imgG!=null)
			image(imgG, 0, 0 ,imgShowWidth,imgShowHeight);
		if(imgD != null)
			image(imgD, imgShowWidth+200,0,imgShowWidth,imgShowHeight);
		if(imgResult != null)
			image(imgResult, (imgShowWidth+200)/2,imgShowHeight,imgShowWidth,imgShowHeight);

		// makes the gui stay on top of elements drawn before.
		hint(DISABLE_DEPTH_TEST);

		controlP5.draw();
	}


	public void controlEvent(ControlEvent theEvent) {

		if (theEvent.isGroup()) {
			int ID = (int)theEvent.group().value();
			String name = theEvent.group().name();
			if(name=="imgListG"){
				imgG=img[ID];
				imgGID = ID;
			}
			if(name=="imgListD"){
				imgD=img[ID];
				imgDID = ID;
			}
		}
	}

	public void compute(int theValue) {
		if(resultCaching[imgDID][imgGID]==null){
			CvSize size = new CvSize();
			size.width(imgG.width);
			size.height(imgG.height);

			IplImage ipl11 = cvCreateImage(size, IPL_DEPTH_8U, 3);
			IplImage ipl1 = cvCreateImage(size, IPL_DEPTH_8U, 3);
			Utils.PImageToIplImage(imgG, ipl11, true);
			cvScale(ipl11, ipl1, 0.25, 0);
			IplImage ipl2 = cvCreateImage(size, IPL_DEPTH_8U, 3);
			IplImage ipl22 = cvCreateImage(size, IPL_DEPTH_8U, 3);
			Utils.PImageToIplImage(imgD, ipl22, true);
			cvScale(ipl22, ipl2, 0.25, 0);
			IplImage ipl3 = cvCreateImage(size, IPL_DEPTH_8U, 1);
			IplImage ipl4 = cvCreateImage(size, IPL_DEPTH_8U, 1);
			IplImage ipl5 = cvCreateImage(size, IPL_DEPTH_8U, 1);
			IplImage ipl6 = cvCreateImage(size, IPL_DEPTH_8U, 1);
			IplImage iplres = cvCreateImage(size, IPL_DEPTH_8U, 1);

			// WB imG
			IplImage r1 = cvCreateImage(ipl1.cvSize(), ipl1.depth(), 1);
			IplImage g1 = cvCreateImage(ipl1.cvSize(), ipl1.depth(), 1);
			IplImage b1 = cvCreateImage(ipl1.cvSize(), ipl1.depth(), 1);
			cvSplit(ipl1,r1,g1,b1,null);
			r1=etirerHistogramme(r1);
			g1=etirerHistogramme(g1);
			b1=etirerHistogramme(b1);
			cvMerge(r1, g1, b1, null, ipl1);
			cvCvtColor(ipl1, ipl3, CV_RGB2GRAY);

			// WB imD
			IplImage r2 = cvCreateImage(ipl1.cvSize(), ipl1.depth(), 1);
			IplImage g2 = cvCreateImage(ipl1.cvSize(), ipl1.depth(), 1);
			IplImage b2 = cvCreateImage(ipl1.cvSize(), ipl1.depth(), 1);
			cvSplit(ipl2,r2,g2,b2,null);
			r2=etirerHistogramme(r2);
			g2=etirerHistogramme(g2);
			b2=etirerHistogramme(b2);
			cvMerge(r2, g2, b2, null, ipl2);
			cvCvtColor(ipl2, ipl4, CV_RGB2GRAY);


			Sauvola sauvola = new Sauvola();
			sauvola.filter(ipl3, ipl5);
			sauvola.filter(ipl4, ipl6);

			IplConvKernel B = cvCreateStructuringElementEx(11,11, 5, 5, CV_SHAPE_ELLIPSE,null );
			IplImage temp = cvCreateImage(ipl5.cvSize(), ipl5.depth(), 2);
			cvMorphologyEx( ipl5, ipl5, temp, B, CV_MOP_ERODE,3);
			cvMorphologyEx( ipl5, ipl5, temp, B, CV_MOP_DILATE,2);
			cvAbsDiff(ipl6, ipl5, iplres);
			cvSub(ipl5, ipl6, iplres, null);
			PImage res = Utils.toPImage(iplres);

			imgResult = res;
			resultCaching[imgDID][imgGID] = res; 
		}else{
			imgResult = resultCaching[imgDID][imgGID];
		}

	}

	IplImage etirerHistogramme(IplImage img){
		int x, y, i;
		int[] plusSombre = new int[3];
		int[] plusClair = new int[3];
		CvScalar temp;
		IplImage res=cvCreateImage(cvSize(img.width(), img.height()), IPL_DEPTH_8U, 1);

		/* Initialisation */
		for(i=0; i<3; i++){
			plusSombre[i]=256;
			plusClair[i]=256;
		}

		/* Recherche des extremums */
		for(x=0; x<img.width(); x++){
			for(y=0; y<img.height(); y++){
				temp=cvGet2D(img, y, x);
				if(temp.val(0)<plusSombre[0] || plusSombre[0]>255)
					plusSombre[0]=(int) temp.val(0);

				if(temp.val(0)>plusClair[0] || plusClair[0]>255)
					plusClair[0]=(int) temp.val(0);

			}
		}

		/* Traitement */
		for(x=0; x<img.width(); x++){
			for(y=0; y<img.height(); y++){
				temp=cvGet2D(img, y, x);

				temp.val(0,(temp.val(0)-plusSombre[0])*255/(plusClair[0]-plusSombre[0]));


				cvSet2D(res, y, x, temp);
			}
		}

		return res;
	}

	boolean play = true;
	public void keyPressed() {
		if(key == ' '){
		}
	}

	public void stop() {
		super.stop();
	}

}
