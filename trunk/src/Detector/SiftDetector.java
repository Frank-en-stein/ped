package Detector;

import java.util.LinkedList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class SiftDetector extends Detector{

	public SiftDetector() {
		templates = new LinkedList<IplImage>();
	}
	
	@Override
	public IplImage Detect(IplImage scene) {
		// TODO Auto-generated method stub
		return null;
	}

}
