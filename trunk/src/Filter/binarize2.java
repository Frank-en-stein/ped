package Filter;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_64F;
import static com.googlecode.javacv.cpp.opencv_core.cvAdd;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_highgui.cvConvertImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_ADAPTIVE_THRESH_GAUSSIAN_C;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_DILATE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_ERODE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_SHAPE_ELLIPSE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvAdaptiveThreshold;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCanny;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCreateStructuringElementEx;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvIntegral;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMorphologyEx;
import static com.googlecode.javacv.cpp.opencv_core.*;


import com.googlecode.javacv.Parallel;
import com.googlecode.javacv.Parallel.Looper;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

public class binarize2 extends Filter {

        @Override
        public Boolean filter(IplImage src, IplImage dst) {
                
        	IplImage  sumimage, sqsumimage, binarized;

        	//IplImage image=cvLoadImage("capImage1-32.png",1);
        	
        	
        	IplImage image = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);
        	cvCvtColor(src, image, CV_BGR2GRAY);
        	
        	int width = 0, height = 0, depth = 0, channels = 0;
        	width = image.width();
        	height = image.height();
        	depth = image.depth();
        	channels = image.nChannels();
        	
             sumimage = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
             sqsumimage = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
             binarized = IplImage.create(width, height, IPL_DEPTH_8U, 1);

//             if (image.depth() != IPL_DEPTH_8U && image.nChannels() > 1) {
//                 cvConvertScale(image, tempsrc2, 255/image.getMaxIntensity(), 0);
//                 cvCvtColor(tempsrc2, tempsrc, CV_BGR2GRAY);
//                 image = tempsrc;
//             } else if (image.depth() != IPL_DEPTH_8U) {
//                 cvConvertScale(image, tempsrc, 255/image.getMaxIntensity(), 0);
//                 image = tempsrc;
//             } else 
            	 
             
        //long time1 = System.currentTimeMillis();
//             JavaCV.hysteresisThreshold(image,  binarized,
//                    150, 120, 255);
             binarization(image, sumimage, sqsumimage, dst,false,  51);

                return true;
        }
        
        
        
        
        
        public static void binarization(final IplImage src, final IplImage sumimage,
                final IplImage sqsumimage, final IplImage dst,final boolean invert, final int k) {
            final int w = src.width();
            final int h = src.height();
            final int srcdepth = src.depth();
//            final IplImage graysrc;
//            if (src.nChannels() > 1) {
//                cvCvtColor(src, dst, CV_BGR2GRAY);
//                graysrc = dst;
//            } else {
//                graysrc = src;
//            }
     
            // compute integral images
            cvIntegral(src, sumimage, sqsumimage, null);
            final DoubleBuffer sumbuf = sumimage.getByteBuffer().asDoubleBuffer();
            final DoubleBuffer sqsumbuf = sqsumimage.getByteBuffer().asDoubleBuffer();
            final int sumstep = sumimage.widthStep();
            final int sqsumstep = sqsumimage.widthStep();
            final ByteBuffer srcbuf = src.getByteBuffer();
            final ByteBuffer dstbuf = dst.getByteBuffer();
            final int srcstep = src.widthStep();
            final int dststep = dst.widthStep();
     
           
            
     
            //for (int y = 0; y < h; y++) {
            Parallel.loop(0, h, new Looper() {
            public void loop(int from, int to, int looperID) {
                for (int y = from; y < to; y++) {
                    for (int x = 0; x < w; x++) {
                        double var = 0, mean = 0, sqmean = 0;
                        
                        int window =  k;

                        int x1 = Math.max(x-window/2, 0);
                        int x2 = Math.min(x+window/2+1, w);

                        int y1 = Math.max(y-window/2, 0);
                        int y2 = Math.min(y+window/2+1, h);

                        mean = sumbuf.get(y2*sumstep/8 + x2) -
                        		sumbuf.get(y2*sumstep/8 + x1) -
                        		sumbuf.get(y1*sumstep/8 + x2) +
                        		sumbuf.get(y1*sumstep/8 + x1);
                        mean /= window*window;
                        sqmean = sqsumbuf.get(y2*sqsumstep/8 + x2) -
                        		sqsumbuf.get(y2*sqsumstep/8 + x1) -
                        		sqsumbuf.get(y1*sqsumstep/8 + x2) +
                        		sqsumbuf.get(y1*sqsumstep/8 + x1);
                        sqmean /= window*window;
                        var = sqmean - mean*mean; 

                    double value = 0;
                    if (srcdepth == IPL_DEPTH_8U) {
                    	value = srcbuf.get(y*srcstep       + x) & 0xFF;
                    } else if (srcdepth == IPL_DEPTH_32F) {
                    	value = srcbuf.getFloat(y*srcstep  + 4*x);
                    } else if (srcdepth == IPL_DEPTH_64F) {
                    	value = srcbuf.getDouble(y*srcstep + 8*x);
                    } else {
                    	//cvIntegral() does not support other image types, so we
                    	//should not be able to get here...
                    	assert(false);
                    }
                    if (invert) {
                    	double threshold = 255 - (255 - mean) * (1 + 0.1*(Math.sqrt(var)/128 - 1));
                    	//double threshold = 255 - (255 - mean) * k;
                    	dstbuf.put(y*dststep + x, (value < threshold ? (byte)0xFF : (byte)0x00));
                    } else {
                    	//double threshold = mean + ((var*var) * (1 + k*(Math.sqrt(var)/128 - 1)));
                    	double threshold = mean * (1 + k*(Math.sqrt(var)/128 - 1));
                    	//double threshold = mean * k;
                    	dstbuf.put(y*dststep + x, (value > threshold ? (byte)0xFF : (byte)0x00));
                    }
                }
            }
            }});
    }

}
