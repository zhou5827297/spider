//package com.spider.spider.service.impl;
//
//import CommentAnalyser;
//import CommentHtmlAnalyser;
//import CommentJsonAnalyser;
//import CommentBrowser;
//import Comment;
//import UserComment;
//import CommentService;
//import MatcherUtils;
//import ThreadUtils;
//import com.spider.subscribe.dao.CrawlerArticleDao;
//import com.spider.subscribe.dao.CrawlerCommentDao;
//import com.spider.subscribe.dao.CrawlerUserDao;
//import com.spider.subscribe.entity.CrawlerArticle;
//import com.spider.subscribe.entity.CrawlerComment;
//import com.spider.subscribe.entity.CrawlerUser;
//import common.base.enums.CompareOperator;
//import common.base.sql.Limit;
//import common.base.sql.LimitImpl;
//import common.base.sql.MultipleCondition;
//import common.base.sql.SingleCondition;
//import common.base.util.FileUtil;
//import common.base.util.JsonUtil;
//import common.base.util.StringUtil;
//import common.base.util.TraceUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * 用户评论
// */
//
//public class CommentServiceImpl implements CommentService {
//    private final Logger LOG = LoggerFactory.getLogger(getClass());
//    private final ThreadPoolExecutor EXECUTOR = ThreadUtils.EXECUTORSUB;
//    private CommentBrowser browser = new CommentBrowser();
//    private CrawlerArticleDao articleDao;
//    private CrawlerCommentDao commentDao;
//    private CrawlerUserDao userDao;
//    private File file;
//
//
//    public CommentServiceImpl(CrawlerArticleDao articleDao, CrawlerCommentDao commentDao, CrawlerUserDao userDao) {
//        this.articleDao = articleDao;
//        this.commentDao = commentDao;
//        this.userDao = userDao;
//    }
//
//    public CommentServiceImpl() {
//    }
//
//    @Override
//    public void execute() {
//        byte[] bytes = FileUtil.read(file);
//        Comment comment = JsonUtil.tryObject(bytes, Comment.class);
//        Limit limit = new LimitImpl();
//        limit.setOrderBy(CrawlerArticleDao.CREATE_TIME + " DESC");
//        MultipleCondition multipleCondition = new MultipleCondition();
//        multipleCondition.add(new SingleCondition().setField(CrawlerArticleDao.SOURCE_ID).setStrings(comment.getSourceId()));
//        Date date = new Date();
//        date.setMonth(date.getMonth() - 1);//一个月以内的文章
//        multipleCondition.add(new SingleCondition().setField(CrawlerArticleDao.CREATE_TIME).setOperator(CompareOperator.GET).setDates(date));
//        limit.setCondition(multipleCondition);
//        List<CrawlerArticle> crawlerArticles = articleDao.select(limit);
//        LOG.info("fetch sourceId [{}] ,articles total[{}]...", comment.getSourceId(), crawlerArticles.size());
//        for (int i = 0, j = crawlerArticles.size(); i < j; i++) {
//            if (EXECUTOR.getActiveCount() < EXECUTOR.getMaximumPoolSize()) {
//                CrawlerArticle article = crawlerArticles.get(i);
//                executePackageRunnable packageRunnale = new executePackageRunnable(article, comment);
//                EXECUTOR.execute(packageRunnale);
//            } else {
//                //无空闲线程执行
//                ThreadUtils.sleepThreadByTimeUnit(TimeUnit.MILLISECONDS, 500);
//                i -= 1;
//            }
//        }
//    }
//
//    /**
//     * 单文章执行子线程
//     */
//    private class executePackageRunnable implements Runnable {
//
//        private CrawlerArticle article;
//
//        private Comment comment;
//
//
//        public executePackageRunnable(CrawlerArticle article, Comment comment) {
//            this.article = article;
//            this.comment = comment;
//        }
//
//        @Override
//        public void run() {
//            String url = article.getUrl();
//            url = getRealCommentUrl(comment, url);
//            String content = browser.browse(url, comment);
//            if (content == null) {
//                return;
//            }
//            String type = comment.getResponse().get("type").toString();
//            CommentAnalyser analyser = null;
//            if ("html".equalsIgnoreCase(type)) {
//                analyser = new CommentHtmlAnalyser();
//            } else if ("json".equalsIgnoreCase(type)) {
//                analyser = new CommentJsonAnalyser();
//            } else if ("jsonp".equalsIgnoreCase(type)) {
//                content = content.substring(content.indexOf("(") + 1);
//                content = content.substring(0, content.lastIndexOf(")"));
//                analyser = new CommentJsonAnalyser();
//            }
//            List<UserComment> userComments = analyser.analyser(content, comment);
//            for (UserComment userComment : userComments) {
//                try {
//                    if (userComment != null) {
//                        userComment.setArticleId(article.getId());
//                        userComment.setUserId(article.getSourceId() + userComment.getUserId());
//                    }
//                    CrawlerUser crawlerUser = userDao.selectRowByCondition(new SingleCondition().setField(CrawlerUserDao.USER_ID).setStrings(userComment.getUserId()));
//                    if (crawlerUser == null) {
//                        crawlerUser = new CrawlerUser();
//                        crawlerUser.setSourceImage(userComment.getUserImg());
//                        crawlerUser.setUserId(userComment.getUserId());
//                        crawlerUser.setNickName(userComment.getNickName());
//                        crawlerUser.setCreateTime(new Date());
//                        userDao.insert(crawlerUser);
//                    }
//                    MultipleCondition mc = new MultipleCondition();
//                    mc.add(new SingleCondition().setField(CrawlerCommentDao.USER_ID).setLongs(crawlerUser.getId()));
//                    mc.add(new SingleCondition().setField(CrawlerCommentDao.ARTICLE_ID).setLongs(userComment.getArticleId()));
//                    CrawlerComment crawlerComment = commentDao.selectRowByCondition(mc);
//                    if (crawlerComment == null) {
//                        crawlerComment = new CrawlerComment();
//                        crawlerComment.setUserId(crawlerUser.getId());
//                        crawlerComment.setArticleId(userComment.getArticleId());
//                        crawlerComment.setCommentText(userComment.getContent());
////                        crawlerComment.setClickLikes(userComment.getClickLikes());
//                        crawlerComment.setCreateTime(new Date());
//                        commentDao.insert(crawlerComment);
//                    }
//                } catch (Exception e) {
//                    LOG.error(TraceUtil.trace(e));
//                }
//            }
//
//        }
//    }
//
//    /**
//     * 获取评论的请求地址
//     */
//    private String getRealCommentUrl(Comment comment, String articleUrl) {
//        String articleReg = comment.getRequest().getArticleId();
//        String articleId = MatcherUtils.matcher(articleReg, articleUrl);
//        String url = comment.getRequest().getUrl();
//        if (StringUtil.isEmpty(url)) {
//            return articleUrl;
//        }
//        url = url.replace("${articleId}", articleId);
//        return url;
//    }
//
//
//    public File getFile() {
//        return file;
//    }
//
//    public void setFile(File file) {
//        this.file = file;
//    }
//}
//
