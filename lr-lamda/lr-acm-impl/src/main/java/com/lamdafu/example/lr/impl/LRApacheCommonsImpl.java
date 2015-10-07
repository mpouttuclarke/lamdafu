/**
 * 
 */
package com.lamdafu.example.lr.impl;

import java.util.HashMap;

import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * @author mpouttuc
 */
public class LRApacheCommonsImpl extends HashMap<Object, Object> {
	private static final String KEY_ADD = "add";
	private static final String KEY_SLOPE = "slope";
	private static final String KEY_INTERCEPT = "intercept";
	private static final String KEY_CLEAR = "clear";

	private static final long serialVersionUID = 2229309181571393707L;

	private final SimpleRegression sr = new SimpleRegression(true);

	public LRApacheCommonsImpl() {
		super(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {
		final String valueOfKey = String.valueOf(key);
		if (KEY_SLOPE.equalsIgnoreCase(valueOfKey)) {
			return sr.getSlope();
		} else if (KEY_INTERCEPT.equalsIgnoreCase(valueOfKey)) {
			return sr.getIntercept();
		}
		return super.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(Object key, Object value) {
		final String valueOfKey = String.valueOf(key);
		if (KEY_ADD.equalsIgnoreCase(valueOfKey)) {
			if (value instanceof double[]) {
				double[] vals = (double[]) value;
				if (vals.length > 1) {
					sr.addData(vals[0], vals[1]);
				}
			}
		} else if (KEY_CLEAR.equalsIgnoreCase(valueOfKey)) {
			sr.clear();
		} else {
			return super.put(key, value);
		}
		return null;
	}

}
