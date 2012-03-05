package tmp;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;


public class testCV {
	
	/*    public static void main(String args[]) { 
	    	IplImage image = cvLoadImage("capImage1-36.png");
	    	IplImage imageGris = cvCreateImage(cvGetSize(image), image.depth(), 1);
	    	IplImage image2 = cvCreateImage(cvGetSize(image), image.depth(), 1);
	    	IplImage imageTmp = cvCreateImage(cvGetSize(image), image.depth(), 1);
	    	IplImage grad_x = cvCreateImage(cvGetSize(image), image.depth()*4, 1);
	    	IplImage grad_y = cvCreateImage(cvGetSize(image), image.depth()*4, 1);
	    	IplImage abs_grad_x = cvCreateImage(cvGetSize(image), image.depth(), 1);
	    	IplImage abs_grad_y = cvCreateImage(cvGetSize(image), image.depth(), 1);
	    	
	        if (image != null) {
	        	GaussianBlur( image, image, cvSize(3,3), 0, 0, BORDER_DEFAULT );
	        	cvCvtColor( image, imageGris, CV_RGB2GRAY );
	        	
	        	
	        	
	        	int scale = 1;
	        	int delta = 0;
	        	int ddepth = CV_16S;
	        	
	        	/// Gradient X
	        	cvSobel(imageGris, grad_x, 1, 0, 3);
	        	/// Gradient Y
	        	cvSobel( imageGris, grad_y, 0, 1, 3);
	        	
	        	cvConvertScaleAbs(grad_x, abs_grad_x, 1, 0);
	        
	        	cvConvertScaleAbs(grad_y, abs_grad_y, 1, 0);
	        	cvAddWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, imageGris );
	        //	CvScalar some= cvSum(image);
	        //	some.[0] = some[0] /(cvGetSize(image).height()*cvGetSize(image).width());
	            //cvThreshold(imageGris, imageGris, 4,255, CV_THRESH_BINARY);
	            cvMorphologyEx(imageGris, image2, imageTmp,cvCreateStructuringElementEx(3, 3, 1, 1,CV_SHAPE_RECT, null),MORPH_CLOSE,1);
	            cvMorphologyEx(image2, imageGris, imageTmp,cvCreateStructuringElementEx(3, 3, 1, 1,CV_SHAPE_RECT, null),MORPH_DILATE,2);
	            
	            /*
	             * cvThreshold(image2, image2, 10,255, CV_THRESH_BINARY);
	            cvSaveImage("capImage1-12bis2.png", image2);
	            */
	            
	     /*      cvThreshold(imageGris, imageGris, 5,255, CV_THRESH_BINARY);
	      
	          
	            
	            
	            cvSaveImage("capImage1-12bis2.png", imageGris);
	            
	            cvReleaseImage(image);
	            cvReleaseImage(image2);
	        }
	        System.out.println("je suis ok");
	    }*/
	
	


public static void main(String args[])
{

	IplImage img=cvLoadImage("capImage1-6.png",1);
	IplImage flou = cvCreateImage(cvGetSize(img), img.depth(), 3);
	IplImage rf = cvCreateImage(cvGetSize(img), img.depth(), 3);
	IplImage image_nvg = cvCreateImage(cvGetSize(img), img.depth(), 1);
	IplImage image_nvg1 = cvCreateImage(cvGetSize(img), img.depth(), 1);
	IplImage result = cvCreateImage(cvGetSize(img),img.depth(), 1);
	IplImage result1 = cvCreateImage(cvGetSize(img),img.depth(), 1);
	IplImage temp = cvCreateImage(cvGetSize(img), img.depth(), 3);
	IplImage grad_x = cvCreateImage(cvGetSize(img), img.depth()*4, 1);
	IplImage grad_y = cvCreateImage(cvGetSize(img), img.depth()*4, 1);
	IplImage abs_grad_x = cvCreateImage(cvGetSize(img), img.depth(), 1);
	IplImage abs_grad_y = cvCreateImage(cvGetSize(img), img.depth(), 1);
	//IplImage* sobel=cvCreateImage(cvGetSize(img),IPL_DEPTH_32U,1);
	IplImage r = cvCreateImage(cvGetSize(img),img.depth(), 1);
	IplImage g = cvCreateImage(cvGetSize(img),img.depth(), 1);
	IplImage b = cvCreateImage(cvGetSize(img),img.depth(), 1);
	boolean finish=true;
	String image;

	String extension =".png";
	String path ="capImage1-";
	/*cvSplit(img, r,g, b,null);
	IplImage[] blueArray = {b};
    IplImage[] greenArray = {g};
    IplImage[] redArray = {r};
    
	int[] dims = {256};
    float rangesArr[][] = {{0, 255}};
    CvHistogram histRed = cvCreateHist(1, dims, CV_HIST_ARRAY,
rangesArr, 1);
    cvCalcHist(redArray, histRed, 0, null);

 // Draw the histograms for R, G and B
    int hist_w = 400; int hist_h = 400;
    int bin_w = hist_w/255;
    CvScalar scal= new CvScalar();
    IplImage CvhistImage = cvCreateImage(cvSize(hist_w, hist_h), CV_8UC3, 3);
*/
    
	int nb = 6;
	cvNamedWindow("Hello World", CV_WINDOW_AUTOSIZE);
	// creat histogram; 
	

	CvHistogram r_hist= new CvHistogram();
	CvHistogram g_hist= new CvHistogram();
	CvHistogram b_hist= new CvHistogram();

	
	
	
	//cvCalcHist(arr, hist, accumulate, mask)
	while( finish)
	{
		// créer un flux de sortie
		String oss =new String();
		// écrire un nombre dans le flux
		oss += nb;

		String num = oss;
		image = path +num+ extension;
		String im= image;
		System.out.println(image);
		//img=cvLoadImage(im);




		int flip=0;
		if(img.origin()!=IPL_ORIGIN_TL)
		{
			flip=CV_CVTIMG_FLIP;
		}

		//cvGaussianBlur( img, flou, cvSize(3,3), 0 );
		cvConvertImage(img, image_nvg1, flip);
		cvSmooth(img, flou, CV_GAUSSIAN, 3);
		cvConvertImage(flou, image_nvg, flip);

		cvConvertImage(flou, image_nvg, flip);

		cvSobel(image_nvg, grad_x, 1, 0, 3);

		cvSobel( image_nvg, grad_y, 0, 1, 3);

		cvConvertScaleAbs(grad_x, abs_grad_x, 1, 0);

		cvConvertScaleAbs(grad_y, abs_grad_y, 1, 0);

		cvAddWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, result);
		//cvResize(result,result2);

		IplConvKernel B = cvCreateStructuringElementEx(5, 5, 1, 1, CV_SHAPE_CROSS,null );
		//cvConvertImage(img, image_nvg, flip);
		//cvThreshold(image_nvg,result , 220,255, CV_THRESH_BINARY);
		//cvSmooth(img, flou, CV_MEDIAN, 7);
		cvThreshold(result,result , 5,255, CV_THRESH_BINARY);
		//cvMorphologyEx( result, result1, temp, B, CV_MOP_OPEN,4);
		cvMorphologyEx( result, result1, temp, B, CV_MOP_CLOSE,4);

		CvScalar scalaire= new CvScalar();
		CvScalar scalaire1;
		CvScalar scalaire2;
		//On parcourt toute l'image
		for(int x=0; x<img.width(); x++)
		{
			for(int y=0; y<img.height(); y++)
			{
				//On récupère le pixel de coordonnées (x,y)
				scalaire1=cvGet2D(result1, y, x);
				scalaire2=cvGet2D(image_nvg1, y, x);

				//Si le niveau de gris est inférieur à p_min, il devient p_min



				if(scalaire1.getVal(0) > 100)
				{

					if (scalaire2.getVal(0) < 140)

						scalaire=cvGet2D(img, y, x);

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

				cvSet2D(rf, y, x, scalaire);
			}
		}

	cvShowImage("Hello World", rf);
	cvWaitKey();
	img=cvLoadImage(im);
	nb+=2;
	if (nb >36)
		finish =false;
	 //  cvSaveImage("new_pic"+nb+extension, rf);
       

}

cvDestroyWindow("Hello World");



cvReleaseImage(img);
cvReleaseImage(image_nvg);
cvReleaseImage(temp);
cvReleaseImage(result);
cvReleaseImage(flou);
//cvReleaseImage(&sobel);


}

	}

