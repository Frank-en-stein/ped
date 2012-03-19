package Filter;


import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;






import com.googlecode.javacv.Parallel;
import com.googlecode.javacv.Parallel.*;

public class Sauvola extends Filter {

	public static int size;

	public Sauvola() {
		size = 51;
	}


	@Override
	public Boolean filter(IplImage src, IplImage dst) {

		IplImage  sumimage, sqsumimage;
		int width = src.width();
		int height = src.height();

		sumimage = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
		sqsumimage = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);

		binarization(src, sumimage, sqsumimage, dst,false);

		return true;
	}





	public static void binarization(IplImage src, final IplImage sumimage,
			final IplImage sqsumimage, final IplImage dst,final boolean invert) {




		final int w = src.width();
		final int h = src.height();
		final int srcdepth = src.depth();
		

		cvIntegral(src, sumimage, sqsumimage, null);

		final DoubleBuffer sumbuf = sumimage.getByteBuffer().asDoubleBuffer();
		final DoubleBuffer sqsumbuf = sqsumimage.getByteBuffer().asDoubleBuffer();
		final int sumstep = sumimage.widthStep();
		final int sqsumstep = sqsumimage.widthStep();
		final ByteBuffer srcbuf = src.getByteBuffer();
		final ByteBuffer dstbuf = dst.getByteBuffer();
		final int srcstep = src.widthStep();
		final int dststep = dst.widthStep();

		final int window =  size;
		final double k = 0.05;
		//for (int y = 0; y < h; y++) {
			Parallel.loop(0, h, new Looper() {
				public void loop(int from, int to, int looperID) {
					for (int y = from; y < to; y++) {
						for (int x = 0; x < w; x++) {
							double var = 0, mean = 0, sqmean = 0;

							

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
								double threshold = 255 - (255 - mean) * (1 + k*(Math.sqrt(var)/128 - 1));
								
								dstbuf.put(y*dststep + x, (value < threshold ? (byte)0xFF : (byte)0x00));
							} else {
								double threshold = mean * (1 + k*(Math.sqrt(var)/128 - 1));
								dstbuf.put(y*dststep + x, (value > threshold ? (byte)0xFF : (byte)0x00));
							}
						}
					}
				}});
	}

}
