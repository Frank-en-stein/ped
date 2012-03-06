package Filter;

import static com.googlecode.javacv.cpp.opencv_highgui.cvConvertImage;

import java.awt.image.BufferedImage;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class Grayscale extends Filter {

	@Override
	public Boolean filter(IplImage src, IplImage dst) {
		if(src.nChannels() == 3){
			int mode;
			System.out.println(src.getBufferedImage().getType());
			switch(src.getBufferedImage().getType()){
			case BufferedImage.TYPE_4BYTE_ABGR :
				mode = CV_BGRA2GRAY;
				break;
			case BufferedImage.TYPE_INT_BGR :
				mode = CV_BGR2GRAY;
				break;
			case BufferedImage.TYPE_3BYTE_BGR :
				mode = CV_BGR2GRAY;
				break;
			case BufferedImage.TYPE_INT_RGB :
				mode = CV_RGB2GRAY;
				break;
			case BufferedImage.TYPE_INT_ARGB :
				mode = CV_RGBA2GRAY;
				break;
			default:
				return false;
			}
			cvConvertImage(src, dst, mode);
		}
		return true;
	}

}
