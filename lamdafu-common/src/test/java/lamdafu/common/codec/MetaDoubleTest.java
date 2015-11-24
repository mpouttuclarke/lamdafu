package lamdafu.common.codec;

import static org.junit.Assert.*;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.junit.Test;

public class MetaDoubleTest {

	@Test
	public void test() {
		PhoneticDouble target = new PhoneticDouble();
		double justADouble = target.encode("-1");
		assertEquals(-1.0D, justADouble, 0.0D);
		assertTrue(PhoneticDouble.isRawNumber(justADouble) && !PhoneticDouble.isMD64(justADouble)
				&& !PhoneticDouble.isMetaphone(justADouble));
		assertEquals(1.0D, target.encode("\t\t\t\t\t\t\t\t1"), 0.0D);
		assertEquals(0.1D, target.encode("1.0E-1"), 0.0D);
		String md64expected = "___F____";
		double md64 = target.encode(md64expected);
		assertTrue(
				!PhoneticDouble.isRawNumber(md64) && PhoneticDouble.isMD64(md64) && !PhoneticDouble.isMetaphone(md64));
		assertEquals(-5.62949533990911E14, md64, 0.0D);
		assertEquals(md64expected, target.decode(md64));
		String phoneticExpected = "bilbo baggins of bag end and barrelrider";
		double phonetic = target.encode(phoneticExpected);
		assertEquals(-7.28535047247244E14, phonetic, 0.0D);
		assertTrue(!PhoneticDouble.isRawNumber(phonetic) && !PhoneticDouble.isMD64(phonetic)
				&& PhoneticDouble.isMetaphone(phonetic));
		DoubleMetaphone dm = new DoubleMetaphone();
		dm.setMaxCodeLen(12);
		assertEquals(dm.doubleMetaphone(phoneticExpected), target.decode(phonetic));
	}

}
