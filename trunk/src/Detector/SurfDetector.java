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
	public LinkedList<CvScalar> Detect(IplImage scene) {
		LinkedList<CvScalar> rectangles = new LinkedList<CvScalar>();

		for(int t=0; t<finders.size() ; t++){

			ObjectFinder finder = finders.get(t);
			
			if (finder != null){
				long start = System.currentTimeMillis();
				double[] dst_corners = finder.find(scene);
				System.out.println("Finding time = " + (System.currentTimeMillis() - start) + " ms");
				
				if (dst_corners !=  null) {
					int xmin = Integer.MAX_VALUE;
					int ymin = Integer.MAX_VALUE;
					int xmax = -1;
					int ymax = -1;
					
					for (int i = 0; i < 4; i++) {
						int x = (int)Math.round(dst_corners[2*i    ]);
						int y = (int)Math.round(dst_corners[2*i + 1]);
						if(x<xmin)
							xmin = x;
						if(x>xmax)
							xmax = x;
						if(y<ymin)
							ymin = y;
						if(y>ymax)
							ymax = y;	
					}
					
					CvScalar rect = cvScalar(xmin,ymin,xmax-xmin,ymax-ymin);
					rectangles.add(rect);
				}
			}
		}

		return rectangles;
	}
	
	public void addTemplate(IplImage template){
		templates.add(template);
		
		ObjectFinder.Settings settings = new ObjectFinder.Settings();
		settings.setObjectImage(template);	
		settings.setHessianThreshold(500);
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


