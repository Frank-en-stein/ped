package Filter;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class AdaptiveDocumentBinarization extends Filter {
	// info image
	int width;
	int height;

	//param algo
	int N = 3;
	// weighted average table
	private static int A_MIN = 0;
	private static int A_MED = 1;
	private static int A_MAX = 2;
	private double[][] WAT;
	private void computeWAT(IplImage img){
		WAT = new double[width*height][3];
		int index = 0;
		for(int i=0; i<width;i++)
			for(int j=0; j<height; j++){
				for(int k=0;k<N;k++)
					for(int l=0;l<N;l++){
						index =(i+k)*height+(j+l);
						
					}
				WAT[index][A_MIN] += -1;
				WAT[index][A_MED] += -1;
				WAT[index][A_MAX] += -1;
			}

	}
	@Override
	public Boolean filter(IplImage src, IplImage dst) {
		width = src.width();
		height = src.height();
		return null;

	}

}
