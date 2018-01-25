package spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

public class WordReader extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;

    // Called when Storm detects a tuple emitted successfully
    @Override
    public void ack(Object msgId) {
        System.out.println("SUCCESS: " + msgId);
    }

    // Called when a tuple fails to be emitted
    @Override
    public void fail(Object msgId) {
        System.out.println("ERROR: " + msgId);
    }

    @Override
    public void close() {
    }

    // Called when a task for this component is initialized within a worker on the cluster.
    @Override
    public void open(Map conf, TopologyContext context,
                     SpoutOutputCollector collector) {
        try {
            // new reader with the words.txt file passed from the config
            this.fileReader = new FileReader(conf.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
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
        String line;
        //Open the reader
        BufferedReader reader = new BufferedReader(fileReader);
        try {
            //Read all lines
            while ((line = reader.readLine()) != null) {
                /**
                 * For each line emmit a new value
                 */
                this.collector.emit(new Values(line), line);
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
        declarer.declare(new Fields("line"));
    }
}
