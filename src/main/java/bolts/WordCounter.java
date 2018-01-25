package bolts;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class WordCounter extends BaseBasicBolt {

    Integer id;
    String name;
    Map<String, Integer> counters;

    @Override
    // This should be called when the cluster is shutdown in Local mode only
    public void cleanup() {
        System.out.println("-- Word Counter ["+name+"-"+id+"] --");
        for(Map.Entry<String, Integer> entry : counters.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue());
        }
    }

    @Override
    // Called before the bolt is run
    public void prepare(Map conf, TopologyContext context) {
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String word = input.getString(0);

        if(!counters.containsKey(word)){
            counters.put(word, 1);
        }else{
            Integer c = counters.get(word) + 1;
            counters.put(word, c);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
