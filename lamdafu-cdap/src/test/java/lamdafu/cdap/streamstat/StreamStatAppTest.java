package lamdafu.cdap.streamstat;

import static org.junit.Assert.*;

import co.cask.cdap.api.metrics.RuntimeMetrics;
import co.cask.cdap.test.ApplicationManager;
import co.cask.cdap.test.FlowManager;
import co.cask.cdap.test.ServiceManager;
import co.cask.cdap.test.StreamManager;
import co.cask.cdap.test.TestBase;
import co.cask.common.http.HttpRequest;
import co.cask.common.http.HttpRequests;
import co.cask.common.http.HttpResponse;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class StreamStatAppTest extends TestBase {
  private static final Gson GSON = new Gson();

  @Test
  public void test() throws Exception {
    // Deploy the application
    ApplicationManager appManager = deployApplication(StreamStatApp.class);

    // Start the flow
    FlowManager flowManager = appManager.getFlowManager(StreamStatFlow.NAME);
    flowManager.start();

    try {
      StreamManager streamManager = getStreamManager(StreamStatApp.STREAM_NAME);
      streamManager.send("disk1\t100");
      streamManager.send("disk1\t999");
      streamManager.send("disk1\t1000");
      streamManager.send("disk1\t1001");
      streamManager.send("disk1\t5000");
      streamManager.send("disk1\t10000");
      streamManager.send("disk2\t100");
      streamManager.send("disk2\t1000");
      streamManager.send("disk2\t5000");
      streamManager.send("disk2\t10000");

      RuntimeMetrics countMetrics = flowManager.getFlowletMetrics(StreamStatParseFlowlet.NAME);
      countMetrics.waitForProcessed(10, 3, TimeUnit.SECONDS);
      countMetrics = flowManager.getFlowletMetrics(StreamStatCalcFlowlet.NAME);
      countMetrics.waitForProcessed(5, 3, TimeUnit.SECONDS);


      // Start service and verify
      ServiceManager serviceManager = appManager.getServiceManager(StreamStatHttpHandler.NAME);
      serviceManager.start();
      serviceManager.waitForStatus(true);
      try {
        URL serviceUrl = serviceManager.getServiceURL();

        URL url = new URL(serviceUrl, "stats/all");
        HttpRequest request = HttpRequest.get(url).build();
        HttpResponse response = HttpRequests.execute(request);
        Assert.assertEquals(200, response.getResponseCode());
        Map<String, String> slowDisks = GSON.fromJson(response.getResponseBodyAsString(Charsets.UTF_8),
                                                      new TypeToken<Map<String, String>>() {}.getType());
        // disk1 should be classified as slow and disk2 should not.
        Assert.assertEquals(1, slowDisks.size());
        Assert.assertTrue(slowDisks.containsKey("disk1"));
      } finally {
        serviceManager.stop();
        serviceManager.waitForStatus(false);
      }
    } finally {
      flowManager.stop();
    }
  }
}
