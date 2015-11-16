/**
 * 
 */
package lamdafu.boot;

import java.util.Map;

/**
 * Utilities for strong typing in Map.get()
 * 
 * @author mpouttuclarke
 *
 */
public class Cast {

	/**
	 * Attempt to get the key's value from the passed map, casting to the
	 * caller's desired type if possible.
	 * 
	 * If the key does not exist or the type is not as expected, returns a null.
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T get(Map map, Object key, Class<T> clazz) {
		if (map == null) {
			return null;
		}
		Object object = map.get(key);
		if (object == null || !clazz.isAssignableFrom(object.getClass())) {
			return null;
		} else {
			return (T) object;
		}
	}

	/**
	 * Attempt to get the key's value from the passed map, casting to the
	 * caller's desired type if possible. Throws a ClassCastException in the
	 * case of a failed cast, and a NullPointerException if the map is null.
	 * 
	 * @param map
	 * @param key
	 * @return
	 * @throws NullPointerException
	 * @throws ClassCastException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T get(Map map, Object key) throws NullPointerException, ClassCastException {
		return (T) map.get(key);
	}
}
