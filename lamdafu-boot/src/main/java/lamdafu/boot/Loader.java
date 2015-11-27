/**
 * 
 */
package lamdafu.boot;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.ServiceLoader;

/**
 * A Boot instance defines a class loader container for complete λ dependency
 * isolation. Only JDK standard class instances (loaded by the JDK bootstrap
 * class loader) can pass between λ instances loaded by different Boot
 * instances. All λ instances loaded by a Boot container instance share a common
 * class loader and so can share dependencies, allowing compatible λ instances
 * to interoperate at a deeper level. Currently, only one λ with a given name
 * can load within a Boot instance, to support different versions of the same λ
 * (i.e. A/B testing) use different Boot instances to load them.
 * 
 * @author mpouttuclarke
 *
 */
public class Loader {

	public static final String LAMDA_KEY = "λ福";

	private final URLClassLoader loader;

	protected Loader(URL... urls) {
		loader = new URLClassLoader(urls, String.class.getClassLoader());
	}

	/**
	 * Boot from URLs or from local working directory if no URL provided.
	 * 
	 * @param urls
	 * @return
	 */
	public static Loader from(URL... urls) {
		if (urls == null || urls.length < 1) {
			try {
				urls = new URL[] { new File(".").toURI().toURL() };
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return new Loader(urls);
	}

	/**
	 * Boot a λ by name if available, otherwise return null. If multiple
	 * versions exist with the same name, an IllegalArgumentException is thrown.
	 * 
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SortedMap<? extends String, ? extends Object> byName(String name) throws IllegalArgumentException {
		ServiceLoader<SortedMap> load = ServiceLoader.load(SortedMap.class, loader);
		Iterator<SortedMap> i = load.iterator();
		List<SortedMap> impls = new ArrayList<>();
		while (i.hasNext()) {
			SortedMap next = i.next();
			if (Cast.get(next, LAMDA_KEY, SortedMap.class) != null && next.containsKey(name)) {
				impls.add(next);
			}
		}
		if (impls.size() > 1) {
			throw new IllegalArgumentException("Classloader URLs " + Arrays.asList(loader.getURLs())
					+ " resolved multiple Lamda versions " + impls + " for name " + name);
		}
		return impls.isEmpty() ? null : impls.get(0);
	}

	/**
	 * Fetches all known λ metamodel instances as JSON strings.
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterable<SortedMap<? extends String, ? extends Object>> metamodel() {
		List<SortedMap<? extends String, ? extends Object>> mm = new ArrayList<>();
		ServiceLoader<SortedMap> load = ServiceLoader.load(SortedMap.class, loader);
		Iterator<SortedMap> i = load.iterator();
		while (i.hasNext()) {
			SortedMap next = i.next();
			SortedMap meta = Cast.get(next, LAMDA_KEY, SortedMap.class);
			if (meta != null) {
				mm.add(meta);
			}
		}
		return mm;
	}
}
