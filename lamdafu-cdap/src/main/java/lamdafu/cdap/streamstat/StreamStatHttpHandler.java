package lamdafu.cdap.streamstat;

import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.commons.collections4.trie.PatriciaTrie;

import co.cask.cdap.api.annotation.UseDataSet;
import co.cask.cdap.api.dataset.lib.KeyValue;
import co.cask.cdap.api.dataset.lib.KeyValueTable;
import co.cask.cdap.api.service.http.AbstractHttpServiceHandler;
import co.cask.cdap.api.service.http.HttpServiceContext;
import co.cask.cdap.api.service.http.HttpServiceRequest;
import co.cask.cdap.api.service.http.HttpServiceResponder;
import lamdafu.primitives.StreamCalc;

@Path("stats")
public class StreamStatHttpHandler extends AbstractHttpServiceHandler {
	static final String NAME = "StreamStatService";

	@UseDataSet("streamStat")
	private KeyValueTable streamStat;
	private StreamCalcKryo ser;

	@Override
	public void initialize(HttpServiceContext context) throws Exception {
		ser = new StreamCalcKryo();
	}

	@Path("all")
	@GET
	public void getAllStats(HttpServiceRequest request, HttpServiceResponder responder) {
		Iterator<KeyValue<byte[], byte[]>> streamStatScan = streamStat.scan(null, null);
		PatriciaTrie<Map<String, Object>> results = new PatriciaTrie<>();
		while (streamStatScan.hasNext()) {
			KeyValue<byte[], byte[]> result = streamStatScan.next();
			ser.in.setBuffer(result.getValue());
			results.put(new String(result.getKey()), ser.kryo.readObject(ser.in, StreamCalc.class).snapshot());
		}
		responder.sendJson(200, results);
	}
}