import bolts.BackerCounter;
import bolts.CategoryCounter;
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
    private static final String csv2016Path = new File("src/main/resources/ks-projects-201612.csv").getAbsolutePath();
    private static final String csv2018Path = new File("src/main/resources/ks-projects-201801.csv").getAbsolutePath();
    private static final String host = "192.168.99.100";
    private static final Integer port = 32768;
    private static final Integer timeout = 5000;

    public static void main(String[] args) throws InterruptedException {
        JedisClient jedis = new JedisClient(host, port);
        jedis.deleteAll();
        runCSVAnalisisTopology();
        jedis.printAllCats("category");
        jedis.printAllCats("backers");
    }

    public static void runCSVAnalisisTopology() throws InterruptedException {
        // Topology definition
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("csv-parser-2016", new CSVParser(csv2016Path));
        builder.setSpout("csv-parser-2018", new CSVParser(csv2018Path));
        builder.setBolt("category-counter-1", new CategoryCounter()).shuffleGrouping("csv-parser-2016");
        builder.setBolt("category-counter-2", new CategoryCounter()).shuffleGrouping("csv-parser-2018");
        builder.setBolt("backer-counter-1", new BackerCounter()).shuffleGrouping("csv-parser-2016");
        builder.setBolt("backer-counter-2", new BackerCounter()).shuffleGrouping("csv-parser-2018");

        // Configuration
        Config config = new Config();
        config.put("host", host);
        config.put("port", port.toString());

        // Run topology
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("my-third-topology", config, builder.createTopology());
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
