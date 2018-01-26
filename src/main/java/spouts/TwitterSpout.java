package spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import twitter.TwitterConf;
import twitter4j.*;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private TwitterConf conf;
    private LinkedBlockingQueue messages;
    private TwitterStream twitterStream;


    public TwitterSpout(TwitterConf conf) {
        this.conf = conf;
    }

    // Called when Storm detects a tuple emitted successfully
    @Override
    public void ack(Object msgId) {
    }

    // Called when a tuple fails to be emitted
    @Override
    public void fail(Object msgId) {
    }

    @Override
    public void close() {
        this.twitterStream.shutdown();
        super.close();
    }

    // Called when a task for this component is initialized within a worker on the cluster.
    @Override
    public void open(Map map, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.messages = new LinkedBlockingQueue();
        this.twitterStream = new TwitterStreamFactory(conf.getConfigurationBuilder().build()).getInstance();
        this.twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                messages.offer(status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int i) {

            }

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }

            @Override
            public void onException(Exception e) {

            }
        });

        this.twitterStream.filter(this.conf.getQueryString());
    }

    @Override
    public void nextTuple() {
        /**
         * NextTuple either emits a new tuple into the topology or simply returns if there are no new tuples to emit
         */
        Object tweets = this.messages.poll();
        if (tweets == null) {
            Utils.sleep(1000);
        } else {
            collector.emit(new Values(tweets));
        }
    }

    // The declareOutputFields function declares the output fields ("line") for the component.
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("message"));
    }
}
