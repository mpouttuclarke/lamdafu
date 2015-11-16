/**
 * 
 */
package lamdafu.boot;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import lamdafu.metamodel.MetaLamda;

/**
 * 
 * 
 * @author mpouttuclarke
 *
 */
public class Boot {

	public static final String METAMODEL_KEY = "metamodel"; 
	
	private ClassLoader loader;
	
	protected Boot(URL... urls) {
		loader = new URLClassLoader(urls, String.class.getClassLoader());
	}
	
	public static Boot from(URL... urls) {
		if(urls == null || urls.length < 1) {
			try {
				urls = new URL[] { new File(".").toURI().toURL() };
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return new Boot(urls);
		
	}
	
	/**
	 * Boot a λ by name if available, otherwise return null.
	 * 
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map byName(String uri) {
		ServiceLoader<Map> load = ServiceLoader.load(Map.class, loader);
		Iterator<Map> i = load.iterator();
		while(i.hasNext()) {
			Map next = i.next();
			MetaLamda meta = Cast.get(next, METAMODEL_KEY, MetaLamda.class);
			if(meta != null && meta.name != null && meta.name.equals(uri)) {
				return next;
			}
		}
		return null;
	}
}
