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

	public String name;
	public String etag;
	
	public MetaBase() {
		super();
	}
	
	public MetaBase(String name, String etag) {
		super();
		this.name = name;
		this.etag = etag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((etag == null) ? 0 : etag.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("[uri=%s etag=%s", name, etag);
	}
	
}
