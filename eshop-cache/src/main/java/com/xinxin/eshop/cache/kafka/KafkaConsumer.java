package com.xinxin.eshop.cache.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * kafka消费者线程
 * 项目启动的时候通过自定义监听器初始化
 */
public class KafkaConsumer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private ConsumerConnector consumerConnector;
    private String topic;

    public KafkaConsumer(String topic){
        this.consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig());
        this.topic = topic;
        log.info("kafkaConsumer 初始化完成，监听的topic：{}",topic);
    }

    @Override
    public void run() {
        log.info("开始执行kafkaConsumer的run方法");
        Map<String,Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[],byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> kafkaStreams = consumerMap.get(topic);
        for (KafkaStream stream : kafkaStreams){
            new Thread(new KafkaMessageProcessor(stream)).start();
        }

    }

    private ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.56.102:2181,192.168.56.103:2181,192.168.56.104:2181");
        props.put("group.id", "eshop-cache-group");
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        return new ConsumerConfig(props);
    }
}
