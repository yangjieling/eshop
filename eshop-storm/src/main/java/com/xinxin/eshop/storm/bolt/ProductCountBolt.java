package com.xinxin.eshop.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.xinxin.eshop.storm.http.HttpClientUtils;
import com.xinxin.eshop.storm.zk.ZookeeperSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
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
        new Thread(new HotProductFindThread()).start();
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

    /**
     * 统计商品访问流量
     * 实现缓存预热
     */
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
                    LOGGER.info("productCountMap中的数据：{}", productCountMap);
                    for (Map.Entry<Long, Long> productCountEntry : productCountMap.entrySet()) {
                        if (topnProductList.size() == 0) {
                            topnProductList.add(productCountEntry);
                        } else {
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
                    LOGGER.info("productidList中的数据：{}", productIdList);
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

    /**
     * 统计热点数据线程
     */
    private class HotProductFindThread implements Runnable {

        @Override
        public void run() {
            List<Map.Entry<Long, Long>> productCountList = new ArrayList<Map.Entry<Long, Long>>();
            List<Long> hotProductIdList = new ArrayList<Long>();
            List<Long> lastTimeHotProductIdList = new ArrayList<Long>();

            while (true) {
                try {
                    productCountList.clear();
                    hotProductIdList.clear();

                    if (productCountMap.size() == 0) {
                        Utils.sleep(100);
                        continue;
                    }
                    LOGGER.info("对productCountMap中的数据开始排序：{}", productCountMap);
                    for (Map.Entry<Long, Long> productCountEntry : productCountMap.entrySet()) {
                        if (productCountList.size() == 0) {
                            productCountList.add(productCountEntry);
                        } else {
                            boolean bigger = false;
                            for (int i = 0; i < productCountList.size(); i++) {
                                if (productCountEntry.getValue() > productCountList.get(i).getValue()) {
                                    int lastIndex = productCountList.size() < productCountMap.size() ? productCountList.size() - 1 : productCountMap.size() - 2;
                                    for (int j = lastIndex; j >= i; j--) {
                                        if (j + 1 == productCountList.size()) {
                                            // 如果集合中还没有该下标 在进行set的时候会报错
                                            productCountList.add(null);
                                        }
                                        productCountList.set(j + 1, productCountList.get(j));
                                    }
                                    productCountList.set(i, productCountEntry);
                                    bigger = true;
                                    break;
                                }
                            }
                            if (!bigger) {
                                if (productCountList.size() < productCountMap.size()) {
                                    productCountList.add(productCountEntry);
                                }
                            }
                        }

                    }
                    LOGGER.info("对productCountMap中的数据开始排序完成，排序结果保存到：{}", productCountList);

                    // 计算排序后容器中95%的商品的平均访问量
                    int calculateCount = (int) Math.floor(productCountList.size() * 0.95);

                    Long totalCount = 0L;
                    for (int i = productCountList.size() - 1; i >= productCountList.size() - calculateCount; i--) {
                        totalCount += productCountList.get(i).getValue();
                    }
                    long avgCount = totalCount / calculateCount;
                    LOGGER.info("平均访问量avgCount：{}", avgCount);
                    productCountList.forEach((o) -> {
                        if (o.getValue() > 10 * avgCount) {
                            hotProductIdList.add(o.getKey());
                            if (!lastTimeHotProductIdList.contains(o.getKey())) {
                                // 将热点商品ID发送到分发层nginx进行缓存
                                String distributeNginxURL = "http://192.168.56.102/hot?productId=" + o.getKey();
                                HttpClientUtils.sendGetRequest(distributeNginxURL);
                                // 调用缓存服务 获取商品详情 将热点商品详细信息发送到所有应用层nginx进行缓存
                                String cacheServiceURL = "http://192.168.0.151:8081/getProductInfo?productId=" + o.getKey();
                                String response = HttpClientUtils.sendGetRequest(cacheServiceURL);
                                LOGGER.info("发送到nginx的缓存信息productId:{},productInfo:{}", o.getKey(), response);
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("productInfo", response));
                                String productInfo = URLEncodedUtils.format(params, HTTP.UTF_8);
                                LOGGER.info("===发送到nginx的缓存信息productId:{},productInfo:{}", o.getKey(), productInfo);
                                String[] appNginxURLs = new String[]{
                                        "http://192.168.56.103/hot?productId=" + o.getKey() + "&" + productInfo,
                                        "http://192.168.56.104/hot?productId=" + o.getKey() + "&" + productInfo,
                                };
                                for (String appNginxURL : appNginxURLs) {
                                    HttpClientUtils.sendGetRequest(appNginxURL);
                                }
                            }
                        }
                    });
                    LOGGER.info("hotProductIdList:{}", hotProductIdList);
                    // 如果上次热点商品集合为空 将本次的商品热点集合复制给它 以便下次比对是否还是热点商品数据
                    if (lastTimeHotProductIdList.size() == 0) {
                        if (hotProductIdList.size() > 0) {
                            hotProductIdList.forEach((o) -> {
                                lastTimeHotProductIdList.add(o);
                            });
                        }
                    } else {
                        lastTimeHotProductIdList.forEach((o) -> {
                            // 上次的热点商品经过计算 已经不算是热点商品了 需要通知 nginx删除
                            if (!hotProductIdList.contains(o)) {
                                String cancelNginxURL = "http://192.168.56.102/cancel_hot?productId=" + o;
                                HttpClientUtils.sendGetRequest(cancelNginxURL);
                            }
                        });
                        if (hotProductIdList.size() > 0) {
                            lastTimeHotProductIdList.clear();
                            hotProductIdList.forEach((o) -> {
                                lastTimeHotProductIdList.add(o);
                            });
                        } else {
                            lastTimeHotProductIdList.clear();
                        }
                    }
                    LOGGER.info("lastTimeHotProductIdList:{}", lastTimeHotProductIdList);
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
