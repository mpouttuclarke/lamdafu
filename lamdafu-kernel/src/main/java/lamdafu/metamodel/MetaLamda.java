/**
 * 
 */
package lamdafu.metamodel;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * 
 * @author mpouttuclarke
 *
 */
public class MetaLamda extends MetaBase {
	private static final long serialVersionUID = 7294936598957234283L;
	
	public final SortedMap<String, MetaField> factor = new TreeMap<>();
	public final SortedMap<String, MetaField> product = new TreeMap<>();
	
	public MetaLamda() {
		super();
	}

	public MetaLamda(String uri, String etag) {
		super(uri, etag);
	}

	@Override
	public String toString() {
		return super.toString() + String.format(" factor=%s, product=%s]", factor, product);
	}
	
}
