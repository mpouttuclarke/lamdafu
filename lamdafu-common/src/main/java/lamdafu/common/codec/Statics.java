package lamdafu.common.codec;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.math.NumberUtils;

public class Statics {

	static final long MD32_16_START = 562949953421312L;
	static final long MD64_START = 281474976710656L;

	static final PatriciaTrie<MD64> MD64_TRIE = new PatriciaTrie<>();
	static final PatriciaTrie<MD32> MD32_TRIE = new PatriciaTrie<>();
	static final PatriciaTrie<MD16> MD16_TRIE = new PatriciaTrie<>();
	static final DoubleMetaphone DM = new DoubleMetaphone();

	static {
		for (MD64 validChars : MD64.values()) {
			String stringVal = validChars.stringVal;
			for (int x = 0; x < stringVal.length(); x++) {
				String validChar = stringVal.substring(x, x + 1);
				MD64_TRIE.put(validChar, validChars);
			}
		}
		for (MD32 validChars : MD32.values()) {
			String stringVal = validChars.stringVal;
			for (int x = 0; x < stringVal.length(); x++) {
				String validChar = stringVal.substring(x, x + 1);
				MD32_TRIE.put(validChar, validChars);
			}
		}
		for (MD16 validChars : MD16.values()) {
			String stringVal = validChars.stringVal;
			for (int x = 0; x < stringVal.length(); x++) {
				String validChar = stringVal.substring(x, x + 1);
				MD16_TRIE.put(validChar, validChars);
			}
		}
		DM.setMaxCodeLen(12);
	}

	static double convertRawDouble(final String value) {
		double nbrVal = NumberUtils.toDouble(value, Double.NaN);
		if (nbrVal <= -MD64_START) {
			return Double.NEGATIVE_INFINITY;
		}
		return nbrVal;
	}

	static double encodeMD64(String[] subStrings) {
		long bits = 0L;
		for (int x = 0; x < subStrings.length; x++) {
			MD64 md64 = MD64_TRIE.get(subStrings[x]);
			if (md64 == null) {
				continue;
			}
			long xform = md64.bits;
			if (x > 0) {
				bits <<= 6;
			}
			bits |= xform;
		}
		bits |= MD64_START;
		return bits * -1.0D;
	}

	static double encodeMetaphone(final String value) {
		String dm = DM.doubleMetaphone(value);
		long bits = 0L;
		for (int x = 0; x < dm.length(); x++) {
			MD16 md16 = MD16_TRIE.get(dm.substring(x, x + 1));
			if (md16 == null) {
				continue;
			}
			if (x > 0) {
				bits <<= 4;
			}
			bits |= md16.bits;
		}
		bits |= MD32_16_START;
		return bits * -1.0D;
	}

}
