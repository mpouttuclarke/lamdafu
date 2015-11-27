package lamdafu.base;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.trie.PatriciaTrie;

import lamdafu.boot.Loader;

public abstract class LamdaMap extends PatriciaTrie<Object> {
	private static final long serialVersionUID = 3773739456023747954L;

	protected LamdaMap() {
		super();
	}
	
	protected LamdaMap(Map<? extends String, ? extends Object> m) {
		super(m);
		super.putIfAbsent(Loader.LAMDA_KEY, Collections.EMPTY_MAP);
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		return null;
	}

	@Override
	public boolean remove(Object key, Object value) {
		return false;
	}

	@Override
	public abstract Object put(String key, Object value);

	@Override
	public Object remove(Object k) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
	}

}
