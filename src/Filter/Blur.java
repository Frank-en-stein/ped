package Filter;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Blur extends Filter{
	
	public int smoothType;
	public int size;
	
	public Blur(){
		smoothType = CV_GAUSSIAN;
		size = 3;
	}

	public Boolean filter(IplImage src, IplImage dst){
		cvSmooth(src, dst, smoothType, size);
		return true;
	}

}
