package lamdafu.dsciprim;

import static org.junit.Assert.*;

import org.junit.Test;

import lamdafu.common.codec.PhoneticDouble;

public class StreamingCalcTest {

	@Test
	public void test() {
		StreamingCalc target = new StreamingCalc(10, 1024);
		for(int x = 0; x < 10000000; x++) {
			target.addOne(Math.random() * (Integer.MAX_VALUE - (x % 489392977)));
		}
		target.microBatch();
		System.out.println(target.snapshotPrimitives());
		target = new StreamingCalc(12, 1024);
		target.add("bob", "bill", "jim", "jimbo");
		for(Object val : target.snapshotPrimitives().subMap("q0", "q0_").values()) {
			System.out.println(PhoneticDouble.decode((double)val));
		}
	}

}
