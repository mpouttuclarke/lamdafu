/**
 * 
 */
package lamdafu.metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author mpouttuclarke
 *
 */
public class MetaLamda extends MetaBase {
	private static final long serialVersionUID = 7294936598957234283L;

	public final List<MetaField> groupBy = new ArrayList<>();
	public final List<MetaField> input = new ArrayList<>();
	public final List<MetaField> output = new ArrayList<>();
	public double version;
	public double cardinality = 1;

	public MetaLamda() {
		super();
	}

	public MetaLamda(String name, double version) {
		super(name);
		this.version = version;
	}

	@Override
	public String toString() {
		return super.toString() + String.format(" input=%s output=%s version=%s cardinality=%s]", input, output,
				version, cardinality);
	}

}
