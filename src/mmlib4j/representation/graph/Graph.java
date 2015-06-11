package mmlib4j.representation.graph;

import java.util.List;


/**
 * MMorph4J - Mathematical Morphology Library for Java 
 * @author Wonder Alexandre Luz Alves
 *
 */
public interface Graph<T> {

	public int getNumVerteces();
	
	public List<Edge<T>> getEdges();
	
	public T[] getVerteces();
	
	public int getCustVertex(int p);
}
