package lamdafu.primitives;

import static org.junit.Assert.*;

import org.junit.Test;

import lamdafu.common.codec.Unibit;
import lamdafu.primitives.StreamingCalc;

public class StreamingCalcTest {

	@Test
	public void test() {
		StreamingCalc target = new StreamingCalc(10, 1024);
		for(int x = 0; x < 10000000; x++) {
			target.addOne(Math.random() * (Integer.MAX_VALUE - (x % 489392977)));
		}
		System.out.println(target.snapshotPrimitives());
		target = new StreamingCalc(12, 1024);
		target.add("jimbo", "jim", "bob", "zeke");
		System.out.println(target.snapshotPrimitives());
	}

}
