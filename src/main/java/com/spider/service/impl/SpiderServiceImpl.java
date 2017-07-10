package com.spider.service.impl;

import com.spider.dubbo.vo.Article;
import com.spider.analyser.ArticleAnalyser;
import com.spider.analyser.impl.SpecialAnalyser;
import com.spider.browser.AriticleBrowser;
import com.spider.config.ProxyConstant;
import com.spider.dubbo.DubboService;
import com.spider.filter.impl.BloomFilterManage;
import com.spider.model.DateConfig;
import com.spider.model.ScriptEnum;
import com.spider.model.Site;
import com.spider.push.DataPushManage;
import com.spider.push.article.AbstractPush;
import com.spider.push.article.DataPushSimpleManage;
import com.spider.sequence.Sequence;
import com.spider.sequence.impl.SnowflakeSequence;
import com.spider.service.Script;
import com.spider.service.SpiderService;
import com.spider.util.*;
import com.spider.config.RemoteConstant;
import com.spider.filter.FilterManage;
import com.spider.model.Config;
import com.spider.push.article.PushFactory;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zhoukai.service.monitor.TaskService;
import com.zhoukai.entity.Process;
import com.zhoukai.entity.Task;
import com.zhoukai.status.TaskStatusEnum;
import common.base.util.MD5Util;
import common.base.util.StringUtil;
import common.base.util.TraceUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO >>> 抓取业务类代码，过多是spider1中的老代码，过于繁琐，已整理过部分，还有大量需后期整理
 */
public class SpiderServiceImpl implements SpiderService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private Site site;
    private Config config;
    private Set<String> uriHistory = new LinkedHashSet<>();
    private Queue<String> uris = new LinkedBlockingQueue<>();
    private AriticleBrowser browser = new AriticleBrowser();
    private ScriptEnum type;
    private String uri;
    private String url;
    private long yesterday;
    private Article article;
    private HtmlPage page;
    private String pageContent;
    private Script script;
    private Process process;
    private Task task;
    private FilterManage bloomFilter = BloomFilterManage.getInstance();
    private DataPushManage dataPushManage = DataPushSimpleManage.getInstance();
    private final static TaskService TASKSERVICE = DubboService.getTaskService();
    private final static Sequence SEQUENCE = SnowflakeSequence.getSequence();
    private final static int FETCH_BEFORE_MILLIS = ProxyConstant.FETCH_BEFORE_HOUR * 60 * 60 * 1000;

    public SpiderServiceImpl() {
        script = new ScriptImpl(this);
    }

    @Override
    public void links(String s) {
        int offset = 0;
        Pattern linkPatten;
        if (config.getLink() != null) {
            linkPatten = Pattern.compile(config.getLink(), Pattern.CASE_INSENSITIVE);
        } else {
            linkPatten = Regs.A;
        }
        Matcher m = linkPatten.matcher(s);
        DateConfig dc = config.getDate();
        Matcher mDate = null;
        SimpleDateFormat sdf = null;
        if (dc != null) {
            mDate = dateRegex(dc, s);
            sdf = dateFormat(dc);
        }
        while (m.find(offset)) {
            String g = m.group(1);
            Date date = null;
            if (dc != null) {
                if (mDate.find(offset)) {
                    try {
                        if (dc.getReplace() != null) {
                            date = sdf.parse(dc.getReplace().replace("$1", mDate.group()));
                        } else {
                            date = sdf.parse(mDate.group());
                        }

                        //==========================列表页面加入判断，抓取指定小时内的新闻==============================================//
                        //判断，如果是yyyy-mm-dd，只不大于24小时即可
                        if (date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0) {
                            long yesterdayMills = System.currentTimeMillis() - 86400000;//最大只取24小时内的新闻
                            if (date.getTime() < yesterdayMills) {  //早于24小时的
                                offset = m.end();
                                continue;
                            }
                        } else if (date.getTime() >= site.getMaxTime().longValue()) { // 大约N小时前的，属于新的新闻
                            //只抓更新的部分
                        } else {
                            offset = m.end();
                            continue;
                        }
                        //===========================================================================================================//
//                        long dt = date.getTime() - site.getMaxTime().longValue();
//                        //DEBUG
//                        if (dt >= 0) {
//                            //只抓更新的部分
//                        } else {
//                            // 个别页面是分栏目的，需要遍历每个栏目的新闻列表。
//                            offset = m.end();
//                            continue;
//                        }
                    } catch (Throwable e) {
                        printSite();
                        log.error(TraceUtil.trace(e));
                    }
                }
            }
            offset = m.end();
            if (config.getLink() != null) {
                relative(g);
            } else {
                if (!g.startsWith("javascript:")) {
                    saveUrl(g, false);
                }
            }
        }
    }

    @Override
    public void finish(boolean success) {
        if (config != null && config.isArticle()) {
            if (article != null && StringUtil.notEmpty(article.getTitle()) && StringUtil.notEmpty(article.getBody())) {
                pushArticle(article);
            } else {
                task.setMessage(task.getMessage() + "|文章过期，或者标题正文之一为空");
            }
        }
    }

    /**
     * 保存快照文件
     */
    private void savePageSnapshot(boolean ovrrided) {
        if (ProxyConstant.SNAPSHOT_WHETHER_SAVE == 1 && page != null) {
            String path = ProxyConstant.SNAPSHOT_DATA_ROOT + "/snapshot/html/" + site.getId() + "/";
            String fileName = MD5Util.MD5(url) + ".html";
            try {
                if (new File(path + fileName).exists() && ovrrided == false) {
                    return;
                }
                String fetchDate = DateUtil.parseDateTimeToSec(DateUtil.getCurrentDate());
                page.setTitleText("爬虫抓取快照【" + page.getTitleText() + "】" + fetchDate);
                String content = page.asXml();
                // 调整为：快照文件统一采用源网站的编码格式保存，便于页面查看
                byte[] bytes = null;
                try {
                    String originalEncoding = page.getCharset().displayName();
                    bytes = content.getBytes(originalEncoding);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                FileUtil.writeFile(bytes, path, fileName);
            } catch (Exception e) {
                log.error("[{}] save snapshot error [{}]", url, e.getMessage());
            } finally {
                task.setSnapshotPath(path + fileName);
                TASKSERVICE.updateByPrimaryKeySelective(task);
            }
        }
    }


    /**
     * 推送文章
     */
    private void pushArticle(Article article) {
        AbstractPush push = PushFactory.makeDataPush(RemoteConstant.PUSH_METHOD);
        push.setArticle(article);
        push.setTaskId(task.getId());
        push.setSiteId(task.getSiteId());
        boolean flag = push.push();
        if (flag) {
            dataPushManage.pushSuccess(push);
            savePageSnapshot(false); // 保存快照文件，推送成功不可覆盖
        } else {
            dataPushManage.pushFail(push);
            savePageSnapshot(true);  // 保存快照文件，推送失败可覆盖
        }
    }

    /**
     * 推送文章列表
     */
    private void pushArticle(List<Article> articlies) {
        for (Article article : articlies) {
            pushArticle(article);
        }
    }

    @Override
    public void nextUri() {
        boolean flag = false;
        while (!flag) {
            try {
                String uri = uris.poll();
                if (uri == null) {
                    flag = nextSite();
                } else {
                    flag = browse(uri);
                }
                task.setStatus(TaskStatusEnum.SUCCESS.getStatus());
            } catch (Throwable e) {
                printSite();
                task.setStatus(TaskStatusEnum.FAIL.getStatus());
                task.setMessage(task.getMessage() + "|" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                //释放浏览器
                if (page != null) {
                    page.cleanUp();
                    page = null;
                }
                browser.closeWebClient();

                // TODO: 2017/4/6 更新处理完成的状态
                Date endDate = new Date();
                task.setEndTime(endDate);
                long executeTime = endDate.getTime() - task.getBeginTime().getTime();
                task.setExecuteTime(executeTime);

                TASKSERVICE.updateByPrimaryKeySelective(task);
                //================

                // 释放掉上一轮抓取的资源
                article = null;
                pageContent = null;
                task = null;
            }
            if (uris.isEmpty()) {
                flag = true;
            } else {
                flag = false;
            }
        }
    }

    @Override
    public boolean nextSite() {
        boolean ret = false;
        if (site != null) {
            yesterday = System.currentTimeMillis() - FETCH_BEFORE_MILLIS;//只取N小时内的新闻
            if (site.getMaxTime() != null) {//确保取最大的时间
                yesterday = Math.max(yesterday, site.getMaxTime());
            }
            site.setMaxTime(yesterday);
            // TODO 特殊处理站点文件
            SiteUtils.analyserReplace(site);
        }
        // ===============================================================================
        if (site != null) {
            uris.clear();
            uriHistory.clear();
            String uri = site.getIndex();
            if (uri != null) {
                ret = browse(uri);
            }
        }
        return ret;
    }

    @Override
    public void saveUrl(final String url, boolean manual) {
        Matcher matcher = Regs.SITE.matcher(url);
        if (matcher.find()) {
            String uri = "";
            if (config.getReg() != null && (config.getReg().startsWith("http") || config.getReg().startsWith("https"))) {
                //配置全路径时，加入全路径访问地址。
                uri = url;
            } else {
                //TODO 要根据上下文过滤掉非当前站点的地址
                uri = url.substring(matcher.end());
            }
            if (manual) {
                //TODO 匹配site
                browse(uri);
            } else {
                offer(uri);
            }
        } else {
            relative(url);
        }
    }

    private void relative(String uri) {
        if (uri.indexOf('/') == 0) {
            offer(uri.replaceFirst("^/+", ""));
        } else if (config.getUri() != null) { //优先取config的uri
            String s = config.getUri();
            int index = s.lastIndexOf('/');
            if (index != -1) {
                s = s.substring(0, index + 1);
            } else if (s.length() > 0) {
                s = "";
            }
            offer(s + uri.replaceFirst("^\\./", ""));
        } else if (this.uri != null) {
            String s = this.uri;
            int index = s.lastIndexOf('/');
            if (index != -1) {
                s = s.substring(0, index + 1);
            } else if (s.length() > 0) {
                s = "";
            }
            offer(s + uri.replaceFirst("^\\./", ""));
        } else {
            offer(uri);
        }
    }

    private void offer(String uri) {
        if (!uriHistory.contains(uri)) {
            if (!uris.contains(uri)) {
                uris.offer(uri);
            }
        }
    }

    private void offer(List<String> uries) {
        for (String uri : uries) {
            offer(uri);
        }
    }

    public boolean browse(String uri) {
        boolean ret = false;
        if (site != null) {
            if (uriHistory.add(uri)) {
                StringBuilder sb = new StringBuilder();
                if (uri != null && uri.startsWith("http") || uri.startsWith("https")) {
                    //地址是全路径，不再拼接站点地址
                } else {
                    sb.append(site.getContext());
                }
                sb.append(uri);
                String url = sb.toString();
                this.url = url;
                // TODO: 2017/4/6  记录 task
                task = new Task();
                task.setId(SEQUENCE.getSequenceId());
                task.setProcessId(process.getId());
                task.setSiteId(process.getSiteId());
                task.setUrl(url);
                task.setBeginTime(new Date());
                task.setUrlMd5(MD5Util.MD5(url));
                task.setStatus(TaskStatusEnum.WAIT.getStatus());
                task.setMessage("");
                TASKSERVICE.insert(task);
                browser.setTask(task);
                //==========================

                config = match(uri, url);
                if (config != null) {
                    this.uri = uri;
                    if (config.isArticle()) {
                        boolean exists = bloomFilter.isExists(url);
                        if (exists) {
                            //不爬已经有的
                        } else {
                            article = new Article();
                            article.setUrl(url);
                            article.setSourceId(site.getId().toString());
                            ret = true;
                        }
                    } else {
                        //TODO 分页内容
                        ret = true;
                    }
                }
                if (ret) {
                    task.setStatus(TaskStatusEnum.DEALING.getStatus());
                    TASKSERVICE.updateByPrimaryKeySelective(task);

                    Page page = browser.browse(url, site.getHeader(), config);
                    if (page == null) {
                        // 页面重试多次也无法打开
                        log.error("[{}] retry to max , it cant't open ...", url);
                        task.setMessage(task.getMessage() + "|页面多次无法打开");
                        printSite();
                    } else if (page.isHtmlPage()) {
                        this.page = (HtmlPage) page;
                    } else {
                        this.pageContent = page.getWebResponse().getContentAsString();
                    }
                    if (page != null) {
                        this.scripts();
                    }
                }
            }
        }
        if (config == null) {
            log.warn("[{}] config not matcher", uri);
        } else if (!ret) {
            log.warn("[{}] already fetch", uri);
        }
        return ret;
    }

    @Override
    public void scripts() {
        boolean ret = false;
        if (config != null) {
            //TODO 最好先判断返回页面是否正常打开，再执行解析，否则当前页面需要稍后重试。
            Map<String, List<String>> map = config.getJs();
            if (map == null) {
                if (config.getLink() != null) {
                    //TODO 不浏览直接发请求获取链接
                    String body = browser.executeJs(page, "document.body.innerHTML");
                    script.links(body);
                }
            } else if (StringUtil.notEmpty(site.getReplaceArgs())) { // 处理特殊类型文章，如flash的单独逻辑
                ArticleAnalyser analyser = new SpecialAnalyser();
                if (config.isArticle()) {
                    List<Article> articlies = analyser.analyserArticle(pageContent, site, config);
                    pushArticle(articlies);
                } else {
                    List<String> urls = analyser.analyserList(pageContent, site, config);
                    offer(urls);
                }
            } else {
                Iterator<Entry<String, List<String>>> js = map.entrySet().iterator();
                while (js.hasNext()) {
                    Entry<String, List<String>> entry = js.next();
                    String key = entry.getKey();
                    type = ScriptEnum.parseCode(key);
                    List<String> scripts = entry.getValue();
                    for (String scriptJs : scripts) {
                        if (type == ScriptEnum.BODY) { //TODO 分页单独处理
                            String bodyJs = "";
                            String body = "";
                            if (config.getPage() != null) {
                                bodyJs = getBodyPageJs(scriptJs);
                            }
                            if (StringUtil.isEmpty(bodyJs)) {
                                body = browser.executeJs(page, scriptJs, false);
                            } else {
                                body = browser.executeJs(page, bodyJs, true);
                            }
                            // TODO 处理相对路径的图片
                            body = JsoupUtils.dealAbsImg(url, body);
                            script.body(body);

                        } else if (type == ScriptEnum.HREF) {
                            String body = browser.executeJs(page, scriptJs);
                            script.links(body);
                        } else {
                            String body = browser.executeJs(page, scriptJs);
                            script.text(body);
                        }
                    }
                }
            }
            //browser.removeInterface();
            if (map != null && map.containsKey("finish")) {
                return;
            }
        }
        this.finish(ret);//兼容没有finish的配置
    }

    /**
     * 获取分页组装的bodyjs
     */
    private String getBodyPageJs(String body) {
        String countReg = config.getPage().get("countReg");
        String currentPage = config.getPage().get("currentPage");
        String increment = config.getPage().get("increment");
        String format = config.getPage().get("format");
        String pageSize = browser.executeJs(page, countReg).toString();
        pageSize = (StringUtils.isNotBlank(pageSize) && pageSize.matches("-?[0-9]+.*[0-9]*")) ? pageSize : "1.0";
        if ("1.0".equals(pageSize)) {
            return body;
        }
        Pattern p = Pattern.compile(currentPage);
        Matcher m = p.matcher(article.getUrl());
        if (m.find()) {
            currentPage = m.group(1);
        }
        format = format.replace("${currentPage}", currentPage);
        // 渲染js模版
        Map<String, String> root = new HashMap<String, String>();
        root.put("pageSize", pageSize);
        root.put("increment", increment);
        root.put("format", format);
        root.put("body", body.replace("document", "tagTpl"));
        String bodyJs = FreeMarkerUtil.analysisTemplate("page.ftl", root);
        return bodyJs;
    }

    /**
     * 根据uri匹配配置
     */
    private Config match(String uri, String url) {
        if (site != null) {
            List<Config> configs = site.getConfigs();
            for (Config config : configs) {
                if (config.getUri() != null) {
                    if (uri.equals(config.getUri())) {
                        return config;
                    }
                }
            }
            for (Config config : configs) {
                if (config.getReg() != null) {
                    if (uri.matches(config.getReg())) {
                        return config;
                    }
                    if (url.matches(config.getReg())) {
                        return config;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void save(String s) {
        if (article != null && StringUtil.notEmpty(s)) {
            // 判断是否包含指定的乱码
            if (CharacterUtils.isContainsMessyCode(s)) {
                article = null;
                return;
            }
            if (type != null) {
                switch (type) {
                    case TITLE:
                        article.setTitle(s);
                        task.setTitle(s);
                        task.setTitleMd5(MD5Util.MD5(s));
                        TASKSERVICE.updateByPrimaryKeySelective(task);
                        break;
                    case HEAD:
                        article.setHead(s);
                        break;
                    case BODY:
                        article.setBody(s);
                        break;
                    case AUTHOR:
                        article.setAuthor(s);
                        break;
                    case PTIME:
                        Date publishDate = DateUtil.getDate(s);
                        if (publishDate != null) {
                            long publishTime = publishDate.getTime();
                            //判断，如果是yyyy-mm-dd，只不大于24小时即可
                            if (publishDate.getHours() == 0 && publishDate.getMinutes() == 0 && publishDate.getSeconds() == 0) {
                                long yesterdayMills = System.currentTimeMillis() - 86400000;//最大只取24小时内的新闻
                                if (publishTime < yesterdayMills) {  //早于24小时的
                                    article = null;
                                    break;
                                }
                            } else if (publishTime < yesterday) { //早于N小时的
                                article = null;
                                break;
                            }
                            String publishDateStr = DateUtil.parseDateTimeToSec(publishDate);
                            article.setPublishTime(publishDateStr);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private SimpleDateFormat dateFormat(DateConfig date) {
        SimpleDateFormat ret = null;
        if (date.getFormat() != null) {
            try {
                ret = new SimpleDateFormat(date.getFormat());
            } catch (Throwable e) {
                printSite();
                log.error(TraceUtil.trace(e));
            }
        }
        return ret;
    }

    private Matcher dateRegex(DateConfig date, String s) {
        Matcher ret = null;
        if (date.getRegex() != null) {
            try {
                ret = Pattern.compile(date.getRegex()).matcher(s);
            } catch (Throwable e) {
                printSite();
                log.error(TraceUtil.trace(e));
            }
        }
        return ret;
    }

    @Override
    public final void setBrowser(AriticleBrowser browser) {
        this.browser = browser;
    }

    @Override
    public final AriticleBrowser getBrowser() {
        return browser;
    }


    @Override
    public long timeout() {
        long ret = 0L;
        if (config != null && config.getTimeout() != null) {
            ret = config.getTimeout();
        }
        return ret;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    /**
     * 打印当前出错站点
     */
    private void printSite() {
        if (site != null) {
            log.error("[{}--{}] have error...", site.getId(), site.getContext());
        }
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

}
