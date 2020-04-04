//package notice.impl;
//
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.exceptions.ServerException;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
//import com.aliyuncs.sms.model.v20160927.SingleSendSmsRequest;
//import com.aliyuncs.sms.model.v20160927.SingleSendSmsResponse;
//import RemoteConstant;
//import HttpPoolManage;
//import notice.StatusNotice;
//import ThreadUtils;
//import common.base.util.DateUtils;
//import common.base.util.JsonUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
///**
// * 短信通知
// */
//public class SmsStatusNotice implements StatusNotice {
//    private final Logger LOG = LoggerFactory.getLogger(getClass());
//
//    private static boolean SPIDERNOTICE = true;
//
//    @Override
//    public void execute() {
//        Map<?, ?> resMap = null;
//        for (int i = 0; i < RemoteConstant.TASK_MAX_RETRY; i++) {
//            String json = HttpPoolManage.sendGet(RemoteConstant.SUBSCRIBE_URL + "subscribe/subscribe/crawlerArticle/get?orderBy=create_time%20desc&isOpen=false");
//            if (json != null) {
//                try {
//                    resMap = JsonUtil.readJsonMap(json);
//                    if (resMap != null) {
//                        break;
//                    }
//                } catch (Exception ex) {
//                    LOG.error("json analyser [{}]", json);
//                }
//            }
//        }
//
//        if (resMap != null) {
//            String flag = resMap.get("s").toString();
//            if ("true".equals(flag)) {
//                List<Map<String, Object>> lists = (List<Map<String, Object>>) resMap.get("d");
//                if (lists.isEmpty() == false) {
//                    Map<String, Object> map = lists.get(0);
//                    long mils = Long.valueOf(map.get("createTime").toString());
//                    long currentMils = System.currentTimeMillis();
//                    if ((currentMils - mils) / 1000 / 60 > 30) { // 大约30分钟
//
//                        int poolSize = 0;
//                        for (int i = 0; i < RemoteConstant.TASK_MAX_RETRY; i++) {
//                            String json = HttpPoolManage.sendGet("http://192.168.0.127:8080/proxy/proxyPool");
//                            if (json != null) {
//                                try {
//                                    List<?> poolLists = JsonUtil.readJsonList(json, List.class);
//                                    if (poolLists != null) {
//                                        poolSize = poolLists.size();
//                                        break;
//                                    }
//                                } catch (Exception ex) {
//                                    LOG.error("json analyser [{}]", json);
//                                }
//                            }
//                        }
//
//                        Map<String, String> tmpMap = new HashMap<String, String>();
//                        tmpMap.put("articleDate", DateUtils.getDateStr(new Date(mils), DateUtils.SIMPLE_FORMAT));
//                        tmpMap.put("articleTime", DateUtils.getDateStr(new Date(mils), "HH:mm:ss"));
//                        tmpMap.put("poolNum", String.valueOf(poolSize));
//                        tmpMap.put("searchDate", DateUtils.getDateStr(new Date(currentMils), DateUtils.SIMPLE_FORMAT));
//                        tmpMap.put("searchTime", DateUtils.getDateStr(new Date(currentMils), "HH:mm:ss"));
//                        String tplData = JsonUtil.toString(tmpMap);
//
//                        if (SPIDERNOTICE) {
//                            sendSms("18611071397", tplData);
//                        }
//                        SPIDERNOTICE = false;
//                    } else {
//                        SPIDERNOTICE = true;
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 短信通知
//     */
//    private void sendSms(String phone, String tplData) {
//        try {
//            IClientProfile profile = DefaultProfile.getProfile("xxxx-xxxx", "xxxx", "xxxx");
//            DefaultProfile.addEndpoint("cn-hangzhou", "xxxx-xxxx", "Sms", "xxx.xxx.xxx");
//            IAcsClient client = new DefaultAcsClient(profile);
//            SingleSendSmsRequest request = new SingleSendSmsRequest();
//            request.setSignName("温馨提示");
//            request.setTemplateCode("xxx");
//            request.setParamString(tplData);
//            request.setRecNum(phone);
////            SingleSendSmsResponse httpResponse = client.getAcsResponse(request);
////            System.out.println(httpResponse.getRequestId() + "|||" + httpResponse.getModel());
//        } catch (ServerException e) {
//            LOG.error(e.getMessage(), e);
//        } catch (ClientException e) {
//            LOG.error(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void start() {
//        ThreadUtils.executeQuertz(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    long start = System.currentTimeMillis();
//                    execute();
//                    long end = System.currentTimeMillis();
//                    LOG.info("push batch time [{}] ms", end - start);
//                } catch (Exception ex) {
//                    LOG.error("push batch error", ex);
//                }
//            }
//        }, 0, 5, TimeUnit.MINUTES);
//    }
//
//    @Override
//    public void shutdown() {
//        ThreadUtils.shutdown();
//    }
//
//
//    public static void main(String[] args) {
//        new SmsStatusNotice().execute();
//    }
//}
