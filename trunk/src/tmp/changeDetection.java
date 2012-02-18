package tmp;
import java.nio.IntBuffer;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_legacy.*;

public class changeDetection {
	
	public static int[] getHistogram(IplImage image){
		int [] hist = new int[(int) image.highValue()+1];
		for(int i=0; i<image.height();i++)
        	for(int j=0; j< image.width(); j++){
        		CvScalar p = cvGet2D(image, i, j);
        		int x = (int) p.getVal(0);
        		hist[x]++;
        	}
		return hist;
	}
	public static int calcMediane(IplImage image, int[] histo){
		int med = 0;
		float count = 0;
		while(count<=image.width()*image.height() && med<histo.length){
			count += histo[med++];
		}
		return med-1;
	}
	
	public static void main(String[] args) {
		IplImage im1 = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-10.png",0);
		IplImage im2 = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-12.png",0);
		
		//CvHistogram hist = getHistogram(im1);
		
		// create image window named "My Image"
        final CanvasFrame canvas = new CanvasFrame("My Image");
        canvas.setSize(640, 480);
        // request closing of the application when the image window is closed
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        
        int [] hist = getHistogram(im1);
        System.out.println(calcMediane(im1, hist));
        // show image on window
        canvas.showImage(im1);
	}

}