package tmp;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

public class Filter {
	
	static IplConvKernel B;

	public Filter() {
		
		B = cvCreateStructuringElementEx(5, 5, 1, 1, CV_SHAPE_ELLIPSE,null );
	}

	static IplImage blur(IplImage src){
		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());
		cvSmooth(src, dst, CV_GAUSSIAN, 5);
		return dst;
	}

	static IplImage grayScale(IplImage src){
		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(), 1);
		cvConvertImage(src, dst,CV_RGB2BGR);
		return dst;
	}

	static IplImage contour(IplImage src){

		IplImage grad_x = cvCreateImage(cvGetSize(src), src.depth()*4, 1);
		IplImage grad_y = cvCreateImage(cvGetSize(src), src.depth()*4, 1);
		IplImage abs_grad_x = cvCreateImage(cvGetSize(src), src.depth(), 1);
		IplImage abs_grad_y = cvCreateImage(cvGetSize(src), src.depth(), 1);
		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(), 1);


		IplImage image_gray = grayScale(src);

		cvSobel(image_gray, grad_x, 1, 0, 3);

		cvSobel( image_gray, grad_y, 0, 1, 3);

		cvConvertScaleAbs(grad_x, abs_grad_x, 1, 0);

		cvConvertScaleAbs(grad_y, abs_grad_y, 1, 0);

		cvAddWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, dst);

		return dst;
	}

	static IplImage Open(IplImage src){

		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(),  src.nChannels());
		IplImage temp = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());

		cvMorphologyEx( src, dst, temp, B, CV_MOP_OPEN,4);

		return dst;
	}

	static IplImage Close(IplImage src,int n){

		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(),  src.nChannels());
		IplImage temp = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());

		cvMorphologyEx( src, dst, temp, B, CV_MOP_CLOSE,n);

		return dst;
	}

	static IplImage Threshold(IplImage src, int n){

		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(),  src.nChannels());

		cvThreshold(src,dst , n,255, CV_THRESH_BINARY);

		return dst;
	}

	public static IplImage process(IplImage src){
		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(),  src.nChannels());
		// image 1
		IplImage image_gray = grayScale(src);

		//image 2

		IplImage blur = blur(src);
		IplImage gray = grayScale(blur);
		IplImage contour = contour(gray);
		IplImage threshold = Threshold(contour, 5);
		IplImage close = Close(threshold,4);

		CvScalar scalaire= new CvScalar();
		CvScalar scalaire1;
		CvScalar scalaire2;
		//On parcourt toute l'image
		for(int x=0; x<src.width(); x++)
		{
			for(int y=0; y<src.height(); y++)
			{
				//On récupère le pixel de coordonnées (x,y)
				scalaire1=cvGet2D(close, y, x);
				scalaire2=cvGet2D(image_gray, y, x);

				//Si le niveau de gris est inférieur à p_min, il devient p_min



				if(scalaire1.getVal(0) > 100)
				{

					if (scalaire2.getVal(0) < 140)

						scalaire=cvGet2D(src, y, x);

					else
					{
						scalaire.setVal(0, 255);
						scalaire.setVal(1, 255);
						scalaire.setVal(2, 255);
					}

				}
				else
				{
					scalaire.setVal(0, 255);
					scalaire.setVal(1, 255);
					scalaire.setVal(2, 255);
				}

				cvSet2D(dst, y, x, scalaire);
			}
		}
		return dst;


	}
	static IplImage process2(IplImage src){
		IplImage blur = blur(src);
		IplImage image_gray = grayScale(blur);
		cvEqualizeHist( image_gray, image_gray );	
		//image 2

		
		return image_gray;
	}
	


}

