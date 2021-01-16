package com.xinxin.eshop.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 单词计算拓扑Demo
 */
public class WordCountTopology {

    /**
     * 定义个spout继承一个基类，或者实现接口都可以，spout主要负责从数据源获取数据
     * 获取数据后 不断的发送给bolt进行处理
     * spout对象会在task中被调用
     */
    public static class RandomSentenceSpout extends BaseRichSpout {
        private static final Logger log = LoggerFactory.getLogger(RandomSentenceSpout.class);
        // 发送数据给bolt
        private SpoutOutputCollector collector;
        private Random random;

        /**
         * 对spout进行初始化
         * 首先调用open方法对spout进行初始化
         * 可以在此时进行一些资源的创建 比如创建一个线程池，数据库连接池，或者钩爪一个httpClient
         *
         * @param map
         * @param topologyContext
         * @param spoutOutputCollector
         */
        public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.collector = spoutOutputCollector;
            this.random = new Random();
        }

        /**
         * task中会不断的循环的调用nextTuple方法
         * 然后不断的发送数据出去
         * 形成一个数据流
         */
        public void nextTuple() {
            Utils.sleep(100);// 间隔100ms发送一次数据
            String[] sentences = new String[]{"the cow jumped over the moon", "an apple a day keeps the doctor away",
                    "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature"};
            String sentence = sentences[random.nextInt(sentences.length)];
            log.info("【发送的句子】=sentence={}", sentence);
            collector.emit(new Values(sentence));// 发送tuple tuple是最小的数据单位 无限个tuple组成一个数据流
        }

        /**
         * 定义发射出去的每个tuple中每个field的名称
         *
         * @param outputFieldsDeclarer
         */
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("sentence"));
        }
    }

    /**
     * 定义一个bolt,继承一个BaseRichBolt
     * 每个bolt代码都在task中被执行
     */
    public static class SplitSentenceBolt extends BaseRichBolt {
        private static final Logger log = LoggerFactory.getLogger(SplitSentenceBolt.class);

        private OutputCollector outputCollector;

        /**
         * bolt的初始方法
         *
         * @param map
         * @param topologyContext
         * @param outputCollector bolt的tuple发射器
         */
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
        }

        /**
         * bolt接收到的每条数据(tuple) 都在execute方法中执行
         *
         * @param tuple
         */
        public void execute(Tuple tuple) {
            String sentence = tuple.getStringByField("sentence");
            String[] words = sentence.split(" ");
            for (String word : words) {
                outputCollector.emit(new Values(word));
            }
        }

        /**
         * 定义发送出去的tuple中每个field的名称
         *
         * @param outputFieldsDeclarer
         */
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }

    public static class WordCountBolt extends BaseRichBolt {
        private static final Logger log = LoggerFactory.getLogger(WordCountBolt.class);
        private OutputCollector outputCollector;
        private Map<String, Long> wordCounts = new HashMap<String, Long>();

        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
        }

        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            Long count = wordCounts.get(word);
            if (count == null) {
                count = 0L;
            }
            count++;
            wordCounts.put(word, count);
            log.info("【单词计数={}出现次数是{}", word, count);
            outputCollector.emit(new Values(word, count));
        }

        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
        }
    }

    public static void main(String[] args) {
        // 将spout和bolt组合起来 形成一个拓扑
        TopologyBuilder builder = new TopologyBuilder();
        /**
         * 第一个参数：spout的自定义名称
         * 第二个参数：创建spout对象
         * 第三个参数：spout需要几个executor
         * 如果不设置task 则执行spout的task个数与executor数相同
         */
//        builder.setSpout("RandomSentence", new RandomSentenceSpout(), 2).setNumTasks(3);
        builder.setSpout("RandomSentenceSpout", new RandomSentenceSpout(), 2);
        builder.setBolt("SplitSentenceBolt", new SplitSentenceBolt(), 5)
                .setNumTasks(10)
                .shuffleGrouping("RandomSentenceSpout");
        builder.setBolt("WordCountBolt", new WordCountBolt(), 10)
                .setNumTasks(20)
                .fieldsGrouping("SplitSentenceBolt", new Fields("word"));

        Config config = new Config();
        if (args != null && args.length > 0) {
            // 设置topology的worker数量
            config.setNumWorkers(3);
            try {
                StormSubmitter.submitTopology(args[0], config, builder.createTopology());
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            } catch (AuthorizationException e) {
                e.printStackTrace();
            }
        } else {
            config.setMaxTaskParallelism(20);
            LocalCluster cluster = null;
            try {
                cluster = new LocalCluster();
                cluster.submitTopology("WordCountTopology", config, builder.createTopology());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.sleep(60000);
            cluster.shutdown();
        }
    }
}
