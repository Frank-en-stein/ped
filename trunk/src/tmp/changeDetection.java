package tmp;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

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

	public static IplImage difference(IplImage image1, IplImage image2){

		IplImage res= cvCreateImage(image1.cvSize(), image1.depth(), image1.nChannels());
		image1.getIntBuffer();
		image2.getIntBuffer();
		for (int i = 0; i < image1.width() * image1.height(); i++) {
			CvScalar p1 = cvGet1D(image1, i);
			CvScalar p2 = cvGet1D(image2, i);
			double b1 = 255-p1.getVal(0);
			double b2 = 255-p2.getVal(0);
			double val = 255-(b1-b2);
			cvSet1D(res, i, CV_RGB(val, val, val));
		}
		return res;
	}


	public static void main(String[] args) {
		IplImage im1 = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-26.png",CV_LOAD_IMAGE_GRAYSCALE);
		IplImage im2 = cvLoadImage("/net/cremi/tmanson/ped-workspace/PED/Ressources/debian girl/capImage1-32.png",CV_LOAD_IMAGE_GRAYSCALE);
		cvCreateImage(im1.cvSize(), im1.depth(), im1.nChannels());
		CvSize tmpSize = new CvSize();
		tmpSize.width(im1.width()/10);
		tmpSize.height(im1.height()/10);
		IplImage tmp1= cvCreateImage(tmpSize, im1.depth(), im1.nChannels());
		IplImage tmp2= cvCreateImage(tmpSize, im1.depth(), im1.nChannels());
		cvResize(im1, tmp1, CV_INTER_LINEAR);
		cvResize(im2, tmp2, CV_INTER_LINEAR);	
		System.out.println(tmp1.width());
		// create image window named "My Image"
		final CanvasFrame canvas = new CanvasFrame("My Image");
		canvas.setSize(640, 480);
		// request closing of the application when the image window is closed
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		//IplImage diff = difference(im2, im1);
		IplImage corr = correlation(tmp1,tmp2);
		// show image on window
		System.out.println("ok");
		canvas.showImage(tmp1);
		cvWaitKey(2000);
		canvas.showImage(tmp2);
		cvWaitKey(2000);
		canvas.showImage(corr);
	}

	private static IplImage correlation(IplImage im1, IplImage im2) {
		/// Global Variables

		int AREA_SIZE = im1.width()/10;

		CvSize size = new CvSize();
		size.width(im1.width()-AREA_SIZE+1);
		size.height(im1.height()-AREA_SIZE+1);

		cvCreateImage(size, IPL_DEPTH_32F, 1);

		IplImage tpl1,tpl2;
		CvSize tplSize = new CvSize();
		tplSize.width(AREA_SIZE);
		tplSize.height(AREA_SIZE);
		tpl1 = cvCreateImage(tplSize, IPL_DEPTH_32F, 3);
		tpl2 = cvCreateImage(tplSize, IPL_DEPTH_32F, 3);

		System.out.println("compute");
		for(int i = 0; i<im1.height(); i++)
			for(int j = 0; j<im1.width(); j++){
				for(int k = 0; k<AREA_SIZE; k++)
					for(int l = 0; l< AREA_SIZE; l++){
						
						int index = (i+k)*im1.width()+j+l;
						if(index<im1.width()*im1.height()){
						//System.out.println(index);
						CvScalar p1 = cvGet2D(im1, i,j);
						CvScalar p2 = cvGet2D(im2, i,j);
						cvSet2D(tpl1, k,l, p1);
						cvSet2D(tpl2, k,l, p2);
						}
					}

				/* Choix possibles : 
				 * CV_TM_SQDIFF, CV_TM_CCORR, CV_TM_CCOEFF,
				 * CV_TM_SQDIFF_NORMED, CV_TM_CCORR_NORMED, CV_TM_CCOEFF_NORMED */
				/*int matchMethod = CV_TM_CCORR_NORMED;
				cvMatchTemplate(im1, tpl, result, matchMethod);
				cvNormalize(result, result, 0, 1, CV_MINMAX, null);*/
			}
		return tpl1;
	}
}