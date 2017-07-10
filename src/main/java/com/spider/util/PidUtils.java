package com.spider.util;

import com.spider.config.ProxyConstant;
import common.base.util.FileUtil;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

/**
 * 获取pid工具类
 */
public class PidUtils {
    /**
     * 写入pid
     */
    public static void writePid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        File data = new File(ProxyConstant.DATA_ROOT);
        if (!data.exists()) {
            data.mkdirs();
        }
        File pidFile = new File(ProxyConstant.DATA_ROOT + "/spider.pid");
        FileUtil.write(pidFile, pid, Charset.defaultCharset(), false);
    }
}
