package bolts;

import database.JedisClient;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class WordCounter extends BaseBasicBolt {

    private JedisClient jedis;

    @Override
    // Called before the bolt is run
    public void prepare(Map conf, TopologyContext context) {
        try {
            this.jedis = new JedisClient(conf.get("host").toString(), Integer.parseInt(conf.get("port").toString()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String word = input.getString(0);

        if (jedis.getValue(word) == null) {
            jedis.setTuple(word, "1");
        } else {
            Integer c = Integer.parseInt(jedis.getValue(word)) + 1;
            jedis.setTuple(word, c.toString());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
