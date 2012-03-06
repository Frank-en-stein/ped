package tmp;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import Filter.Blur;
import Filter.FilterFactory;
import Filter.Grayscale;

public class testfiltre {

	public static void main(String[] args) {
		IplImage img = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-32.png",CV_LOAD_IMAGE_UNCHANGED);

		Blur blur = (Blur) FilterFactory.getFilter(FilterFactory.BLUR);
		blur.size = 7;
		IplImage bluredImg = blur.apply(img, true);
		if(bluredImg==null)
			System.out.println("blurring error");

		Grayscale grayscale = (Grayscale) FilterFactory.getFilter(FilterFactory.GRAYSCALED);
		IplImage grayscaledImg = grayscale.apply(img, true);
		if(grayscaledImg==null)
			System.out.println("grayscaling error");

		// create image window named "My Image"
		final CanvasFrame canvas = new CanvasFrame("My Image");
		canvas.setSize(640, 480);
		// request closing of the application when the image window is closed
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		// show image on window
		System.out.println("ok");
		canvas.showImage(img);
		cvWaitKey(2000);
		canvas.showImage(bluredImg);
		cvWaitKey(2000);
		canvas.showImage(grayscaledImg);
	}

}
