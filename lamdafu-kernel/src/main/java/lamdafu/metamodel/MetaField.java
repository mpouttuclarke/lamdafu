/**
 * 
 */
package lamdafu.metamodel;

/**
 * 
 * 
 * @author mpouttuclarke
 *
 */
public class MetaField extends MetaBase {
	private static final long serialVersionUID = -1322142208157401802L;

	public String type;

	public MetaField() {
		super();
	}

	public MetaField(String uri, String etag, String type) {
		super(uri, etag);
		this.type = type;
	}

	@Override
	public String toString() {
		return super.toString() + String.format(" type=%s]", type);
	}

}