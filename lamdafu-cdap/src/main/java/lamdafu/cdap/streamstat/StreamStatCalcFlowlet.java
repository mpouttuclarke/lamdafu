package lamdafu.cdap.streamstat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.ArrayUtils;

import co.cask.cdap.api.annotation.Batch;
import co.cask.cdap.api.annotation.HashPartition;
import co.cask.cdap.api.annotation.ProcessInput;
import co.cask.cdap.api.annotation.UseDataSet;
import co.cask.cdap.api.dataset.lib.KeyValueTable;
import co.cask.cdap.api.flow.flowlet.AbstractFlowlet;
import co.cask.cdap.api.flow.flowlet.FlowletContext;
import lamdafu.primitives.StreamCalc;

/**
 * Receives the ID of a disk that recorded a slow read, updating how many times
 * the disk has been slow and storing the disk ID in a separate dataset if it
 * has been slow very often, indicating that it may be time to replace the disk.
 */
public class StreamStatCalcFlowlet extends AbstractFlowlet {

	static final String NAME = "streamStatCalc";

	@UseDataSet("streamStat")
	private KeyValueTable streamStat;
	private StreamCalcKryo ser;

	@Override
	public void initialize(FlowletContext ctx) throws Exception {
		ser = new StreamCalcKryo();
	}

	@ProcessInput
	@Batch(1024 * 16)
	@HashPartition("streamStatID")
	public void process(Iterator<String[]> tuples) {
		PatriciaTrie<List<String>> keys = new PatriciaTrie<>();
		while (tuples.hasNext()) {
			String[] next = tuples.next();
			List<String> values = keys.get(next[0]);
			if (values == null) {
				values = new ArrayList<>();
				keys.put(next[0], values);
			}
			values.add(next[1]);
		}
		for (Entry<String, List<String>> entry : keys.entrySet()) {
			byte[] keyBytes = entry.getKey().getBytes();
			byte[] statKryo = streamStat.read(keyBytes);
			ser.in.setBuffer(statKryo);
			StreamCalc streamCalc = ser.kryo.readObject(ser.in, StreamCalc.class);
			List<String> values = entry.getValue();
			streamCalc.add(values.toArray());
			ser.kryo.writeObject(ser.out, streamCalc);
			streamStat.write(keyBytes, ArrayUtils.subarray(ser.out.getBuffer(), 0, ser.out.position()));
		}
	}
}