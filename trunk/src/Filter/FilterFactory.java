package Filter;

public class FilterFactory{

	// FILTRES
	public final static int BLUR = 0;
	public final static int GRAYSCALED = 1;

	// singleton pour les filtres
	private static volatile Blur blurFilter = null;
	private static volatile Grayscale grayscaledFilter = null;

	private static Object get(Object f, String className){
		if (f == null)
			try {
				f = Class.forName(className).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		return f;
	}

	public static Filter getFilter(int filter){
		switch(filter){
		case BLUR:
			return (Filter) get(blurFilter,Blur.class.getName());
		case GRAYSCALED:
			return (Filter) get(grayscaledFilter,Grayscale.class.getName());
		}
		return null;
	}

}
