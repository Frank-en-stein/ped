package tmp;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Dimension;
import java.awt.Toolkit;


import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.ObjectFinder;
import com.googlecode.javacv.cpp.opencv_features2d.CvSURFPoint;

public class SurfDetector {
	public static void main(String[] args) throws Exception {
//      Logger.getLogger("com.googlecode.javacv").setLevel(Level.OFF);

      String objectFilename = args.length == 2 ? args[0] : "/net/cremi/nmestrea/espaces/travail/ped/Ressources/tieFighter_gross2.jpg";
      String sceneFilename  = args.length == 2 ? args[1] : "/net/cremi/nmestrea/espaces/travail/ped/Ressources/scan_rotate.jpg";

      IplImage object = cvLoadImage(objectFilename, CV_LOAD_IMAGE_GRAYSCALE);
      IplImage image  = cvLoadImage(sceneFilename,  CV_LOAD_IMAGE_GRAYSCALE);
      if (object == null || image == null) {
          System.err.println("Can not load " + objectFilename + " and/or " + sceneFilename);
          System.exit(-1);
      }

      IplImage objectColor = IplImage.create(object.width(), object.height(), 8, 3);
      cvCvtColor(object, objectColor, CV_GRAY2BGR);

      IplImage correspond = IplImage.create(image.width(), object.height()+ image.height(), 8, 1);
      cvSetImageROI(correspond, cvRect(0, 0, object.width(), object.height()));
      cvCopy(object, correspond);
      cvSetImageROI(correspond, cvRect(0, object.height(), correspond.width(), correspond.height()));
      cvCopy(image, correspond);
      cvResetImageROI(correspond);

      ObjectFinder.Settings settings = new ObjectFinder.Settings();
      settings.setObjectImage(object);
      //settings.setUseFLANN(true);
      ObjectFinder finder = new ObjectFinder(settings);

      long start = System.currentTimeMillis();
      double[] dst_corners = finder.find(image);
      System.out.println("Finding time = " + (System.currentTimeMillis() - start) + " ms");

      if (dst_corners !=  null) {
          for (int i = 0; i < 4; i++) {
              int j = (i+1)%4;
              int x1 = (int)Math.round(dst_corners[2*i    ]);
              int y1 = (int)Math.round(dst_corners[2*i + 1]);
              int x2 = (int)Math.round(dst_corners[2*j    ]);
              int y2 = (int)Math.round(dst_corners[2*j + 1]);
              cvLine(correspond, cvPoint(x1, y1 + object.height()),
                      cvPoint(x2, y2 + object.height()),
                      CvScalar.RED, 5, 8, 0);
          }
      }

      /*for (int i = 0; i < finder.ptpairs.size(); i += 2) {
          CvPoint2D32f pt1 = finder.objectKeypoints[finder.ptpairs.get(i)].pt();
          CvPoint2D32f pt2 = finder.imageKeypoints[finder.ptpairs.get(i+1)].pt();
          cvLine(correspond, cvPointFrom32f(pt1),
                  cvPoint(Math.round(pt2.x()), Math.round(pt2.y()+object.height())),
                  CvScalar.WHITE, 1, 8, 0);
      }*/

   // create image window
   		final CanvasFrame canvas = new CanvasFrame("Template Matching");

   		Toolkit t = canvas.getToolkit();
   		Dimension d = t.getScreenSize();
   		int w = d.width;
   		int h = d.height; 
   		canvas.setCanvasSize(800, 600);
   		canvas.setLocation((w-canvas.getWidth())/2, (h-canvas.getHeight())/2);

   		// request closing of the application when the image window is closed
   		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

   		// show image on window
   		canvas.showImage(correspond);
   		
      /*CanvasFrame objectFrame = new CanvasFrame("Object");
      CanvasFrame correspondFrame = new CanvasFrame("Object Correspond");

      correspondFrame.showImage(correspond);
      /*for (int i = 0; i < finder.objectKeypoints.length; i++ ) {
          CvSURFPoint r = finder.objectKeypoints[i];
          CvPoint center = cvPointFrom32f(r.pt());
          int radius = Math.round(r.size()*1.2f/9*2);
          cvCircle(objectColor, center, radius, CvScalar.RED, 1, 8, 0);
      }
      objectFrame.showImage(objectColor);

      objectFrame.waitKey();

      objectFrame.dispose();
      correspondFrame.dispose();*/
  }

}
