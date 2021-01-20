package com.xinxin.eshop.storm.spout;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 获取数据源kafka中的消息Spout
 */
public class AccessLogKafkaSpout extends BaseRichSpout {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogKafkaSpout.class);

    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(1000);
    private SpoutOutputCollector outputCollector;

    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        LOGGER.info("AccessLogKafkaSpout stating ......");
        this.outputCollector = spoutOutputCollector;
        startKafkaConsumer();
    }

    public void nextTuple() {
        if (queue.size() > 0){
            try {
                String message = queue.take();
                LOGGER.info("从kafka中获取到的消息{}",message);
                outputCollector.emit(new Values(message));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            Utils.sleep(100);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }

    private void startKafkaConsumer(){
        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.56.102:2181,192.168.56.103:2181,192.168.56.104:2181");
        props.put("group.id", "eshop-cache-group");
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        ConsumerConfig consumerConfig = new ConsumerConfig(props);

        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);

        String topic = "access-log";
        Map<String,Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[],byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> kafkaStreams = consumerMap.get(topic);
        for (KafkaStream stream : kafkaStreams){
            new Thread(new kafkaMessageProcessor(stream)).start();
        }
    }

    private class kafkaMessageProcessor implements Runnable{
        private KafkaStream kafkaStream;

        public kafkaMessageProcessor(KafkaStream kafkaStream){
            this.kafkaStream = kafkaStream;
        }

        public void run() {
            ConsumerIterator<byte[], byte[]> consumerIterator = kafkaStream.iterator();
            while (consumerIterator.hasNext()){
                try {
                    String message = new String(consumerIterator.next().message(), "UTF-8");
                    queue.put(message);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
