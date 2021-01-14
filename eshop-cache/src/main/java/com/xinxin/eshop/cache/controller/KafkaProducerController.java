//package com.xinxin.eshop.cache.controller;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * kafka消息生产者控制器
// */
//@RestController
//@RequestMapping("/kafka")
//public class KafkaProducerController {
//    private static final Logger log = LoggerFactory.getLogger(KafkaProducerController.class);
//
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//    @GetMapping("/send/{message}")
//    public String kafkaSendMsg(@PathVariable String message) {
//        kafkaTemplate.send("test", message);
//        String res = "消息：" + message + "发送成功 SUCCESS!";
//        log.info(res);
//        return res;
//    }
//}
