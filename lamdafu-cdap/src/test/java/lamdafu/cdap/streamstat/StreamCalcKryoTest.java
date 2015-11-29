package lamdafu.cdap.streamstat;

import static org.junit.Assert.*;

import org.junit.Test;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lamdafu.boot.Cast;
import lamdafu.primitives.StreamCalc;

public class StreamCalcKryoTest {

	@Test
	public void test() {
		StreamCalc calc = new StreamCalc(10, 10);
		StreamCalcKryo target = new StreamCalcKryo();
		calc.add(1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D);
		byte[] buff = target.write(calc);
		StreamCalc read = target.read(buff);
		assertEquals(9.0D, Cast.as(read.snapshot().get("q009"), Double.class), 0.0D);
	}

}
