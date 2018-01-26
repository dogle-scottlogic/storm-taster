import bolts.TwitterBolt;
import bolts.WordCounter;
import bolts.WordNormalizer;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import spouts.TwitterSpout;
import spouts.WordReader;

import java.io.File;

public class TopologyMain {

    private static final String path = new File("src/main/resources/words.txt").getAbsolutePath();
    private static final String host = "192.168.99.100";
    private static final Integer port = 32768;
    private static final Integer timeout = 5000;

    public static void main(String[] args) throws InterruptedException {
        runTopology();
    }

    public static void runTopology() throws InterruptedException {
        // Topology definition
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("twitter-stream", new TwitterSpout());
        //The spout and the bolts are connected using shuffleGroupings. This type of grouping
        //tells Storm to send messages from the source node to target nodes in randomly distributed
        //fashion.
        builder.setBolt("twitter-bolt", new TwitterBolt()).shuffleGrouping("twitter-stream");

        // Configuration
        Config config = new Config();
        config.put("host", host);
        config.put("port", port.toString());
        config.setDebug(true);

        // Run topology
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("my-twitter-topology", config, builder.createTopology());
        Thread.sleep(timeout);
        localCluster.shutdown();
    }
}
