package lamdafu.cdap.streamstat;

import org.apache.commons.lang.ArrayUtils;
import org.objenesis.strategy.SerializingInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import lamdafu.primitives.StreamCalc;

/**
 * Isolate the Kryo dependency and related logic.
 * 
 * @author mpouttuclarke
 *
 */
public class StreamCalcKryo {
	private Kryo kryo;
	private byte[] buff = new byte[1024 * 512];
	private Serializer<?> ser;

	protected StreamCalcKryo() {
		super();
		kryo = new Kryo();
		kryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
		// Since StreamCalc extends Map, explicitly set a FieldSerializer
		ser = kryo.register(StreamCalc.class, new FieldSerializer<>(kryo, StreamCalc.class), 16).getSerializer();
	}

	public StreamCalc read(byte[] inBuff) {
		return kryo.readObject(new Input(inBuff), StreamCalc.class, ser);
	}

	public byte[] write(StreamCalc obj) {
		Output out = new Output(buff);
		kryo.writeObject(out, obj, ser);
		return ArrayUtils.subarray(buff, 0, out.position());
	}

}
