package Detector;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.LinkedList;

import com.googlecode.javacv.ObjectFinder;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class SurfDetector extends Detector{

	public SurfDetector() {
		templates = new LinkedList<IplImage>();
	}
	
	@Override
	public IplImage Detect(IplImage scene) {
		IplImage result;
		result = scene.clone();

		for(int t=0; t<templates.size() ; t++){
			IplImage object = getTemplate(t);
			System.out.println("Nb templates : "+templates.size());
			System.out.println("Template n°"+t);

			IplImage objectColor = IplImage.create(object.width(), object.height(), 8, 3);
			cvCvtColor(object, objectColor, CV_GRAY2BGR);

			ObjectFinder.Settings settings = new ObjectFinder.Settings();
			settings.setObjectImage(object);
			settings.setHessianThreshold(600);
			//settings.setDistanceThreshold(0.1);
			settings.setExtended(true);

			//settings.setUseFLANN(true);
			ObjectFinder finder = null;
			try {
				finder = new ObjectFinder(settings);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (finder != null){
				long start = System.currentTimeMillis();
				double[] dst_corners = finder.find(scene);
				System.out.println("Finding time = " + (System.currentTimeMillis() - start) + " ms");
				
				if (dst_corners !=  null) {
					for (int i = 0; i < 4; i++) {
						int j = (i+1)%4;
						int x1 = (int)Math.round(dst_corners[2*i    ]);
						int y1 = (int)Math.round(dst_corners[2*i + 1]);
						int x2 = (int)Math.round(dst_corners[2*j    ]);
						int y2 = (int)Math.round(dst_corners[2*j + 1]);
						System.out.println("("+x1+","+y1+") ; ("+x2+","+y2+")");
						cvLine(result, cvPoint(x1, y1),
								cvPoint(x2, y2),
								CvScalar.RED, 10, 8, 0);
					}
				}
			}
		}

		return result;
	}

}

