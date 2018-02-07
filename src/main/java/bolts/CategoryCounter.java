package bolts;

import database.JedisClient;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import java.util.ArrayList;
import java.util.Map;

public class CategoryCounter extends BaseBasicBolt {

    private JedisClient jedis;
    private final String MAP_NAME = "category";

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
        ArrayList<String> line = (ArrayList<String>) input.getValue(0);
        if (line.get(9).equals("successful") && !line.get(12).isEmpty()) {
            try {
                this.jedis.addToMap(MAP_NAME, line.get(3), Double.parseDouble(line.get(12)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
