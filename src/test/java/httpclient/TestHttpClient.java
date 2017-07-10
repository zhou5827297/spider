package httpclient;

import java.io.File;

/**
 * Created by zhoukai on 2017/5/23.
 */
public class TestHttpClient {
    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Program Files (x86)\\Microsoft Office\\Office15");
        System.out.println(file.toURL());
        System.out.println(file.toURI());
        System.out.println(file.toURI().toURL());
//        try {
//            String content = RetryUtil.asyncExecuteWithRetry(new Callable<String>() {
//                @Override
//                public String call() throws Exception {
//                    return HttpPoolManage.sendGet("http://192.168.0.50:50070/explorer.html#/user/hive/warehouse/spider.db");
//                }
//            }, 5, 2 * 1000, true, 5 * 1000, (ThreadPoolExecutor) ThreadUtils.FETCHEREXECUTOR);
//            System.out.println(content);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("=======================end=======================");
    }
}
