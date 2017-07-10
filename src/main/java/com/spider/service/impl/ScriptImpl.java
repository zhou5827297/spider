package com.spider.service.impl;

import com.spider.service.Script;
import com.spider.service.SpiderService;
import com.spider.util.HtmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptImpl implements Script {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private SpiderService service;

    public ScriptImpl(SpiderService service) {
        this.service = service;
    }

    @Override
    public void links(String s) {
        s = s.replace("&nbsp;", " ").trim();
        service.links(s);
    }

    @Override
    public void title(String s) {
        s = trim(s);
        service.save(s);
    }

    @Override
    public void head(String s) {
        s = trim(s);
        service.save(s);
    }

    @Override
    public void body(String s) {
        s = trim(s);
        s = HtmlUtil.trimInner(s, "script");
        s = HtmlUtil.trimComment(s);
        s = HtmlUtil.trimWithoutTag(s, "p|img|table|tr|td|br");
        s = HtmlUtil.trimEmptyTag(s);
        service.save(s);
    }

    @Override
    public void text(String s) {
        s = trim(s);
        s = HtmlUtil.trimInner(s, "script");
        s = HtmlUtil.trimComment(s);
        s = HtmlUtil.trimWithoutTag(s, null);
        service.save(s);
    }

    @Override
    public void finish(Boolean successFlag) {
        service.finish(Boolean.TRUE.equals(successFlag));
    }

    private final String trim(String s) {
        return s.replace("&nbsp;", "").trim();
    }
}
