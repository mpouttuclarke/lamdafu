package lamdafu.common.codec;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Packs an ASCII compatible String into a double while preserving as much
 * information as possible. If the String represents a Number, then it is
 * encoded as such. If not, the PM64 enum defines a 6-bit encoding scheme used
 * for values up to literalCutoff characters. After that length, the input
 * String is phonetically encoded and then the PM16 enum is used for a 4-bit
 * encoding of the phonetic characters.
 * 
 * Also supports utility methods to identify the encoded data type and also to
 * decode back to the original String (if literalCutoff or less chars) or the
 * phonetic string (if literalCutoff+ chars).
 * 
 * Numeric input less than or equal to -281474976710656L will be treated as
 * negative infinity, in order to reserve flags to allow accurate decoding of
 * String input.
 * 
 * If encoding errors occur or any reason, a NaN result occurs.
 * 
 * @author mpouttuclarke
 *
 */
public class PhoneticDouble {

	private static final MD16[] MD16_VALUES = MD16.values();

	private static final MD64[] MD64_VALUES = MD64.values();

	public static final int MAX_LITERAL_CUTOFF = 8;

	private static final String NUMERICS = "0123456789";

	private int literalCutoff = MAX_LITERAL_CUTOFF;
	private int maxCacheSize = 64;
	private Cache<String, Double> cache;

	public PhoneticDouble() {
		super();
		initCache();
	}

	/**
	 * Initializes or resets the operation cache.
	 */
	public void initCache() {
		cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MILLISECONDS).maximumSize(maxCacheSize).build();
	}

	/**
	 * String length cutoff after which phonetic encoding replaces MD64
	 * encoding. For example, if the data is acronyms or other already encoded
	 * values then phonetic re-encoding doesn't make sense and can be turned off
	 * by setting literalCutoff to 8. If the value is longer than 8 characters
	 * then phonetic encoding will be used regardless.
	 * 
	 * @param literalCutoff
	 * @return
	 */
	public PhoneticDouble withLiteralCutoff(int literalCutoff) {
		if (literalCutoff < 0 || literalCutoff > MAX_LITERAL_CUTOFF) {
			this.literalCutoff = MAX_LITERAL_CUTOFF;
		} else {
			this.literalCutoff = literalCutoff;
		}
		return this;
	}

	/**
	 * Operation cache size which saves operation results for common inputs.
	 * 
	 * @param maxCacheSize
	 * @return
	 */
	public PhoneticDouble withMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
		initCache();
		return this;
	}

	public double encode(final String value) {
		if (StringUtils.isEmpty(value)) {
			return Double.NaN;
		}
		try {
			return cache.get(value, new Callable<Double>() {
				@Override
				public Double call() throws Exception {
					return encodeInternal(value);
				}
			});
		} catch (ExecutionException e) {
			return Double.NaN;
		}
	}

	protected double encodeInternal(final String value) {
		if (StringUtils.isEmpty(value)) {
			return Double.NaN;
		}
		if (NumberUtils.isNumber(value)) {
			return Statics.convertRawDouble(value);
		}
		String[] subStrings = subStrings(value);
		if (StringUtils.containsAny(value, NUMERICS) && containsNoMD32(subStrings)) {
			// If no characters, strip out any numbers and convert
			StringBuilder stripNonNumeric = new StringBuilder();
			for (int x = 0; x < subStrings.length; x++) {
				char charAt = subStrings[x].charAt(0);
				if (charAt >= '0' && charAt <= '9') {
					stripNonNumeric.append(charAt);
				}
			}
			return Statics.convertRawDouble(stripNonNumeric.toString());
		}
		if (value.length() <= literalCutoff) {
			return Statics.encodeMD64(subStrings);
		} else {
			return Statics.encodeMetaphone(value);
		}
	}

	/*
	 * TODO: we should really commit a change to commons collections4 to avoid
	 * this
	 */
	private static String[] subStrings(final String value) {
		String[] subStrings = new String[value.length()];
		for (int x = 0; x < value.length(); x++) {
			subStrings[x] = value.substring(x, x + 1);
		}
		return subStrings;
	}

	private static boolean containsNoMD32(String... values) {
		for (int x = 0; x < values.length; x++) {
			if (Statics.MD32_TRIE.containsKey(values[x])) {
				return false;
			}
		}
		return true;
	}

	public String decode(double d) {
		if (isRawNumber(d)) {
			return String.valueOf(d);
		}
		StringBuilder result = new StringBuilder(15);
		long bits = (long) (d * -1.0D);
		long bitmask = 0x3F; // 6-bit
		if (isMD64(d)) {
			for (int x = 6 * 7; x > -1; x -= 6) {
				long shift = bits >> x;
				int i = (int) (shift & bitmask);
				if (i > 0 && i < MD64_VALUES.length) {
					result.append(MD64_VALUES[i].charVal);
				}
			}
		} else if (isMetaphone(d)) {
			bitmask /= 4; // Switch to 4-bit
			for (int x = 4 * 11; x > -1; x -= 4) {
				long shift = bits >> x;
				int i = (int) (shift & bitmask);
				if (i > 0 && i < MD16_VALUES.length) {
					result.append(MD16_VALUES[i].charVal);
				}
			}
		}
		return result.toString();
	}

	/**
	 * Indicates if the parameter is a raw number without any embedded encoded
	 * string.
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isRawNumber(double d) {
		return d > -Statics.MD64_START;
	}

	/**
	 * Indicates the parameter is an MD64 encoded string up to 8 characters.
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isMD64(double d) {
		return d <= -Statics.MD64_START && d > -Statics.MD32_16_START;
	}

	/**
	 * Indicates the parameter is a metaphone encoded value using MD32 and MD16
	 * encoding up to 15 characters.
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isMetaphone(double d) {
		return d <= -Statics.MD32_16_START && d > -Statics.MD32_16_START + -Statics.MD64_START;
	}
}
