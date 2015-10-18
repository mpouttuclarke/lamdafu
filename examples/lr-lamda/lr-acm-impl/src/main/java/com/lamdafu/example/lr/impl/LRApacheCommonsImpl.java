/**
 * 
 */
package com.lamdafu.example.lr.impl;

import java.util.TreeMap;

import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * @author mpouttuc
 */
public class LRApacheCommonsImpl extends TreeMap<String, Object> {

	private static final String KEY_LAMBDA = "\u03BBlr";
	private static final String KEY_ADD = KEY_LAMBDA + "\u0394add";
	private static final String KEY_SLOPE = KEY_LAMBDA + "\u0398slope";
	private static final String KEY_INTERCEPT = KEY_LAMBDA + "\u0398intercept";
	private static final String MESG_PARM_ADD = KEY_ADD +" requires double[2]";

	private static final long serialVersionUID = 2229309181571393707L;

	private final SimpleRegression lr = new SimpleRegression(true);

	public LRApacheCommonsImpl() {
		super();
		initSuper();
	}

	protected void initSuper() {
		// Add prototypes to allow data type discovery
		super.put(KEY_LAMBDA, new String[0]);
		super.put(KEY_ADD, new double[2]);
		super.put(KEY_SLOPE, new Double(0.0d));
		super.put(KEY_INTERCEPT, new Double(0.0d));
	}

	@Override
	public void clear() {
		super.clear();
		lr.clear();
		initSuper();
	}

	@Override
	public Object get(Object key) {
		final String valueOfKey = String.valueOf(key);
		if (KEY_SLOPE.equalsIgnoreCase(valueOfKey)) {
			return lr.getSlope();
		} else if (KEY_INTERCEPT.equalsIgnoreCase(valueOfKey)) {
			return lr.getIntercept();
		}
		return super.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		final String valueOfKey = String.valueOf(key);
		if (KEY_ADD.equalsIgnoreCase(valueOfKey)) {
			if (value instanceof double[]) {
				double[] vals = (double[]) value;
				if (vals.length == 2) {
					lr.addData(vals[0], vals[1]);
					super.put(key, value);
				} else {
					throw new IllegalArgumentException(MESG_PARM_ADD);
				}
			} else {
				throw new IllegalArgumentException(MESG_PARM_ADD);
			}
		} else if (!valueOfKey.startsWith("\u03BB")) {
			return super.put(key, value);
		}
		return null;
	}

}
