package Filter;


import static com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;

public abstract class Filter {
	/**
	 * Permet d'appliquer le filtre à l'image
	 * @param src Image a filtrer
	 * @param copy true -> crée une nouvelle iage / false -> l'image src est modifiée
	 * @return
	 */
	public final IplImage apply(IplImage src,Boolean copy){
		IplImage dst;
		if(copy)
			dst = src.clone();
		else
			dst = src;
		if(filter(src,dst))
			return dst;
		else
			return null;
	}

	/**
	 * Cette méthode doit contenir le code du filtre. 
	 * L'image src ne doit pas etre modifiée.
	 * L'image resultante du filtre devra être dst.
	 * @param src Image à modifier
	 * @param dst Image destination
	 * @return retourne true si le filtre à pû etre appliqué false sinon
	 */
	public abstract Boolean filter(final IplImage  src, IplImage dst);
}
