package com.spider.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarker操作
 */
public class FreeMarkerUtil {
    private static Logger LOG = LoggerFactory.getLogger(FreeMarkerUtil.class);
    private static String ENCODING = "UTF-8";

    private static Configuration CONFIG = null;

    static {
        initConfiguration();
    }

    private static void initConfiguration() {
        try {
            CONFIG = new Configuration(Configuration.VERSION_2_3_21);
            CONFIG.setClassForTemplateLoading(FreeMarkerUtil.class,"/ftl");
            CONFIG.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_21));
        } catch (Exception ex) {
            LOG.error("FreeMarker init error", ex);
        }
    }


    /**
     * @param templateName 模板文件名称
     * @param root         数据模型根对象
     */
    public static String analysisTemplate(String templateName, Map<?, ?> root) {
        ByteArrayOutputStream os = null;
        Writer out = null;
        String tpl = "";
        try {
            Template template = CONFIG.getTemplate(templateName, ENCODING);
            os = new ByteArrayOutputStream();
            out = new OutputStreamWriter(os);
            template.process(root, out);
            out.flush();
            tpl = os.toString();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (TemplateException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tpl;
    }

    public static void main(String[] args) {
        Map<String, String> root = new HashMap<String, String>();
        root.put("pageSize", "10");
        root.put("increment", "1");
        root.put("format", "123");
        root.put("body", "body");
        String ftl = analysisTemplate("page.ftl", root);
        System.out.println(ftl);
    }
}
