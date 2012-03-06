package tmp;

import java.util.LinkedList;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public abstract class Detector {
protected LinkedList<IplImage> templates;

public void addTemplate(IplImage template){
	templates.add(template);
}

public void removeTemplate(int indice){
	templates.remove(indice);
}

public IplImage getTemplate(int indice){
	return templates.get(indice);
}

public abstract IplImage Detect(IplImage scene);

}
