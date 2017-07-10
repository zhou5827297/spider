package com.spider.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SpiderUtils {

    private final static int[] HTTP_CODE_WHITE_LIST = {404, 200, 304};

    /**
     * 是否放行的状态码
     */
    public static boolean isPass(int code) {
        boolean pass = false;
        for (int codeTmp : HTTP_CODE_WHITE_LIST) {
            if (codeTmp == code) {
                pass = true;
                break;
            }
        }
        return pass;
    }


    private enum Version {
        //FIREFOX_17(BrowserVersion.FIREFOX_17), //
        FIREFOX_24(BrowserVersion.FIREFOX_45), //
        // INTERNET_EXPLORER_8(BrowserVersion.INTERNET_EXPLORER_8), //
        //INTERNET_EXPLORER_9(BrowserVersion.INTERNET_EXPLORER_9), //
//        CHROME(BrowserVersion.FIREFOX_17),//
        INTERNET_EXPLORER_11(BrowserVersion.INTERNET_EXPLORER); //
        private BrowserVersion value;

        Version(BrowserVersion value) {
            this.value = value;
        }

        public BrowserVersion getValue() {
            return value;
        }
    }

    /**
     * 随机获取一个BrowserVersion
     */
    public static BrowserVersion getBrowserVersion() {
        int length = Version.values().length;
        int random = new Random().nextInt(length);
        return Version.values()[random].getValue();
    }

    /**
     * 随机获取最大值范围内的数字
     *
     * @param max 最大值
     * @return
     */
    public static int getRandomNum(int max) {
        int random = new Random().nextInt(max + 1);
        if (random == 0) {
            return 1;
        } else {
            return random;
        }
    }

    /**
     * 休眠一条线程，随机时间内
     *
     * @param max 最大值
     * @return
     */
    public static void sleepThreadByRandomTime(int max) {
        int random = new Random().nextInt(max + 1);
        if (random == 0) {
            random = 1;
        }
        try {
            TimeUnit.SECONDS.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 线程池中没有，则退出主线程线程
     *
     * @param threadPoolTaskExecutor
     */
    public static void exit(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        int activeCount = threadPoolTaskExecutor.getActiveCount();
        while (activeCount > 0) {
            try {
                TimeUnit.MINUTES.sleep(3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            activeCount = threadPoolTaskExecutor.getActiveCount();
        }
        System.exit(0);
    }

}
