//package com.xinxin.eshop.cache.kafka;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
////@Component
//public class ConsumerListener {
//    private static final Logger log = LoggerFactory.getLogger(ConsumerListener.class);
//
//    @KafkaListener(topics = "test", groupId = "consumer-group")
//    public void listener(List<String> list, Acknowledgment ack) {
//        log.info("本次批量拉取数量:" + list.size() + " 开始消费....");
//        List<String> msgList = new ArrayList<>();
//        for (String record : list) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record);
//            // 获取消息
//            kafkaMessage.ifPresent(o -> msgList.add(o.toString()));
//        }
//        if (msgList.size() > 0) {
//            for (String msg : msgList) {
//                log.info("开始消费消息【" + msg + "】");
//            }
//            // 更新索引
//            // updateES(messages);
//        }
//        //手动提交offset
//        ack.acknowledge();
//        msgList.clear();
//        log.info("消费结束");
//    }
//}
