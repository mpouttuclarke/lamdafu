package lamdafu.cdap.streamstat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import cern.colt.buffer.DoubleBuffer;
import cern.colt.list.DoubleArrayList;
import hep.aida.bin.QuantileBin1D;
import lamdafu.primitives.StreamCalc;

public class StreamCalcKryo {
	Kryo kryo;
	Input in = new Input();
	Output out = new Output(new byte[1024 * 512]);

	protected StreamCalcKryo() {
		super();
		kryo = new Kryo();
		kryo.register(StreamCalc.class, 16);
		kryo.register(QuantileBin1D.class, 17);
		kryo.register(DoubleArrayList.class, 18);
		kryo.register(DoubleBuffer.class, 19);
	}

}
