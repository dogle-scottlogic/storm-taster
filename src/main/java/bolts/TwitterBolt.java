package bolts;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class TwitterBolt extends BaseBasicBolt {
    private Integer id;
    private String name;

    @Override
    // Called before the bolt is run
    public void prepare(Map conf, TopologyContext context) {
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String tweet = input.getString(0);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
