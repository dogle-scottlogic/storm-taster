package spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;

public class CSVParser extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;

    // Called when a task for this component is initialized within a worker on the cluster.
    @Override
    public void open(Map conf, TopologyContext context,
                     SpoutOutputCollector collector) {
        try {
            // new reader with the words.txt file passed from the config
            this.fileReader = new FileReader(conf.get("csvFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        /**
         * NextTuple either emits a new tuple into the topology or simply returns if there are no new tuples to emit
         */
        if (completed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
            return;
        }
        String[] line;
        try {
            CSVReader reader = new CSVReader(this.fileReader);
            while ((line = reader.readNext()) != null) {
                if(reader.getLinesRead() != 1) {
                    List<String> emitValue = new ArrayList<String>(Arrays.asList(line));
                    this.collector.emit(new Values(emitValue));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading tuple", e);
        } finally {
            completed = true;
        }
    }

    // The declareOutputFields function declares the output fields ("line") for the component.
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("emitValue"));
    }
}
