package com.xinxin.eshop.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.xinxin.eshop.storm.zk.ZookeeperSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.trident.util.LRUMap;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductCountBolt extends BaseRichBolt {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCountBolt.class);

    private LRUMap<Long, Long> productCountMap = new LRUMap<Long, Long>(1000);
    private ZookeeperSession zookeeperSession;
    private int taskId;

    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.zookeeperSession = ZookeeperSession.getInstance();
        this.taskId = topologyContext.getThisTaskId();
        new Thread(new ProductCountThread()).start();
        initTaskId(topologyContext.getThisTaskId());
    }

    public void execute(Tuple tuple) {
        Long productId = tuple.getLongByField("productId");
        Long count = productCountMap.get(productId);
        if (null == count) {
            count = 0L;
        }
        count++;
        productCountMap.put(productId, count);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    public class ProductCountThread implements Runnable {

        public void run() {
            List<Map.Entry<Long, Long>> topnProductList = new ArrayList<Map.Entry<Long, Long>>();
                List<Long> productIdList = new ArrayList<Long>();

            while (true) {
                try {
                    topnProductList.clear();
                    productIdList.clear();

                    int topn = 3;

                    if (productCountMap.size() == 0) {
                        Utils.sleep(100);
                        continue;
                    }
                    LOGGER.info("productCountMap中的数据：{}",productCountMap);
                    for (Map.Entry<Long, Long> productCountEntry : productCountMap.entrySet()) {
                        if (topnProductList.size() == 0) {
                            topnProductList.add(productCountEntry);
                        }else {
                            boolean bigger = false;
                            for (int i = 0; i < topnProductList.size(); i++) {
                                if (productCountEntry.getValue() > topnProductList.get(i).getValue()) {
                                    int lastIndex = topnProductList.size() < topn ? topnProductList.size() - 1 : topn - 2;
                                    for (int j = lastIndex; j >= i; j--) {
                                        if (j + 1 == topnProductList.size()) {
                                            // 如果集合中还没有该下标 在进行set的时候会报错
                                            topnProductList.add(null);
                                        }
                                        topnProductList.set(j + 1, topnProductList.get(j));
                                    }
                                    topnProductList.set(i, productCountEntry);
                                    bigger = true;
                                    break;
                                }
                            }
                            if (!bigger) {
                                if (topnProductList.size() < topn) {
                                    topnProductList.add(productCountEntry);
                                }
                            }
                        }

                    }

                    topnProductList.forEach((o) -> {
                        productIdList.add(o.getKey());
                    });
                    LOGGER.info("productidList中的数据：{}",productIdList);
                    String topnProductListJSON = JSONArray.toJSONString(productIdList);
                    zookeeperSession.createNode("/task-hot-product-list-" + taskId);
                    zookeeperSession.setNodeData("/task-hot-product-list-" + taskId, topnProductListJSON);
                    Utils.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initTaskId(int taskId) {
        zookeeperSession.acquireDistributeLock();
        String path = "/taskId-list";
        zookeeperSession.createNode(path);
        String taskIdList = zookeeperSession.getNodeData(path);
        if (StringUtils.isBlank(taskIdList)) {
            taskIdList += taskId;
        } else {
            taskIdList += "," + taskId;
        }
        zookeeperSession.setNodeData(path, taskIdList);
        zookeeperSession.releaseDistributeLock();
    }
}
