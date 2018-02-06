import bolts.LineSplitter;
import bolts.WordCounter;
import bolts.WordNormalizer;
import database.JedisClient;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import spouts.CSVParser;
import spouts.WordReader;

import java.io.File;

public class TopologyMain {

    private static final String wordPath = new File("src/main/resources/words.txt").getAbsolutePath();
    private static final String csvPath = new File("src/main/resources/ks-projects-201612.csv").getAbsolutePath();
    private static final String host = "192.168.99.100";
    private static final Integer port = 32768;
    private static final Integer timeout = 5000;

    public static void main(String[] args) throws InterruptedException {
        JedisClient jedis = new JedisClient(host, port);
        jedis.deleteAll();
        runCSVAnalisisTopology();
        jedis.printAllCats();
    }

    public static void runCSVAnalisisTopology() throws InterruptedException {
        // Topology definition
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("csv-parser", new CSVParser(),1);
        //The spout and the bolts are connected using shuffleGroupings. This type of grouping
        //tells Storm to send messages from the source node to target nodes in randomly distributed
        //fashion.
        builder.setBolt("line-splitter", new LineSplitter()).shuffleGrouping("csv-parser");
        // Send the same word to the same instance of the word-counter using fieldsGrouping instead of shuffleGrouping
//        builder.setBolt("word-counter", new WordCounter()).fieldsGrouping("word-normalizer", new Fields("word"));

        // Configuration
        Config config = new Config();
        config.put("csvFile", csvPath);
        config.put("host", host);
        config.put("port", port.toString());

        // Run topology
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("my-second-topology", config, builder.createTopology());
        Thread.sleep(timeout);
        localCluster.shutdown();
    }

    public static void runWordCountTopology() throws InterruptedException {
        // Topology definition
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader());
        //The spout and the bolts are connected using shuffleGroupings. This type of grouping
        //tells Storm to send messages from the source node to target nodes in randomly distributed
        //fashion.
        builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("word-reader");
        // Send the same word to the same instance of the word-counter using fieldsGrouping instead of shuffleGrouping
        builder.setBolt("word-counter", new WordCounter()).fieldsGrouping("word-normalizer", new Fields("word"));

        // Configuration
        Config config = new Config();
        config.put("wordsFile", wordPath);
        config.put("host", host);
        config.put("port", port.toString());

        // Run topology
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("my-first-topology", config, builder.createTopology());
        Thread.sleep(timeout);
        localCluster.shutdown();
    }
}
