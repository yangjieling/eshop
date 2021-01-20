package com.xinxin.eshop.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LogParseBolt extends BaseRichBolt {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogParseBolt.class);
    private OutputCollector outputCollector;

    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    public void execute(Tuple tuple) {
        String message = tuple.getStringByField("message");
        LOGGER.info("从AccessLogKafkaSpout中接收到的消息：{}",message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        JSONObject uriArgsJSON = jsonObject.getJSONObject("uri_args");
        Long productId = uriArgsJSON.getLong("productId");
        if (productId != null){
            LOGGER.info("发送的productId:{}",productId);
            outputCollector.emit(new Values(productId));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("productId"));
    }
}
