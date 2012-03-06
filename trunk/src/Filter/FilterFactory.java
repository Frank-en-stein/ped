package Filter;

public class FilterFactory{

	// FILTRES
	public final static int BLUR = 0;
	public final static int GRAYSCALED = 1;
	
	// singleton pour les filtres
	private static volatile Blur blurFilter = new Blur();
	private static volatile Grayscale grayscaledFilter = new Grayscale();
	
	private static Object get(Object f){
		//Le "Double-Checked Singleton"/"Singleton doublement vérifié" permet d'éviter un appel 
		//coûteux à synchronized, une fois que l'instanciation est faite.
		if (f == null) 
			// Le mot-clé synchronized sur ce bloc empêche toute instanciation multiple même 
			//par différents "threads"
			synchronized(f) {
				if (f == null)
					try {
						f = Class.forName(f.getClass().getName()).newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			}
		return f;
	}

	public static Filter getFilter(int filter){
		switch(filter){
		case BLUR:
			return (Filter) get(blurFilter);
		case GRAYSCALED:
			return (Filter) get(grayscaledFilter);
		}
		return null;
	}

}
