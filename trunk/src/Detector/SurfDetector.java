package Detector;

import static com.googlecode.javacv.cpp.opencv_core.*;
import java.util.LinkedList;

import com.googlecode.javacv.ObjectFinder;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class SurfDetector extends Detector{
	
	protected LinkedList<ObjectFinder> finders;

	public SurfDetector() {
		templates = new LinkedList<IplImage>();
		finders = new LinkedList<ObjectFinder>();
	}
	
	@Override
	public IplImage Detect(IplImage scene) {
		IplImage result;
		result = scene.clone();

		for(int t=0; t<finders.size() ; t++){

			ObjectFinder finder = finders.get(t);
			
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
	
	public void addTemplate(IplImage template){
		templates.add(template);
		
		ObjectFinder.Settings settings = new ObjectFinder.Settings();
		settings.setObjectImage(template);	
		settings.setHessianThreshold(600);
		//settings.setDistanceThreshold(0.1);
		settings.setExtended(true);
		
		ObjectFinder finder = null;
		try {
			finder = new ObjectFinder(settings);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finders.add(finder);
	}
}


