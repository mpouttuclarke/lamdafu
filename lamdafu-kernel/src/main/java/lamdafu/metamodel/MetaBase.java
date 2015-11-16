/**
 * 
 */
package lamdafu.metamodel;

import java.io.Serializable;

/**
 * 
 * 
 * @author mpouttuclarke
 *
 */
public abstract class MetaBase implements Serializable {
	private static final long serialVersionUID = 5318133070786432016L;

	public String uri;
	public String etag;
	
	public MetaBase() {
		super();
	}
	
	public MetaBase(String uri, String etag) {
		super();
		this.uri = uri;
		this.etag = etag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((etag == null) ? 0 : etag.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaBase other = (MetaBase) obj;
		if (etag == null) {
			if (other.etag != null)
				return false;
		} else if (!etag.equals(other.etag))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("[uri=%s etag=%s", uri, etag);
	}
	
}
