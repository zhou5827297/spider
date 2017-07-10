package com.spider.model;

import java.util.List;
import java.util.Map;

public class Config {
	Long id;
	Long timeout;
	boolean article;
	/**相对地址*/
	String uri;
	/**正则匹配*/
	String reg;
	/**日期配置*/
	DateConfig date;
	/**脚本*/
	Map<String, List<String>> js;
	/**连接匹配*/
	String link;
	/**分页*/
	Map<String, String> page;
	/**启动js引擎*/
	private boolean javaScriptEnabled;

	public final Long getId() {
		return id;
	}

	public final void setId(Long id) {
		this.id = id;
	}

	public final Long getTimeout() {
		return timeout;
	}

	public final void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public final boolean isArticle() {
		return article;
	}

	public final void setArticle(boolean article) {
		this.article = article;
	}

	public final String getUri() {
		return uri;
	}

	public final void setUri(String uri) {
		this.uri = uri;
	}

	public final String getReg() {
		return reg;
	}

	public final void setReg(String reg) {
		this.reg = reg;
	}

	public final DateConfig getDate() {
		return date;
	}

	public final void setDate(DateConfig date) {
		this.date = date;
	}

	public final Map<String, List<String>> getJs() {
		return js;
	}

	public final void setJs(Map<String, List<String>> js) {
		this.js = js;
	}

	public final String getLink() {
		return link;
	}

	public final void setLink(String link) {
		this.link = link;
	}

	public Map<String, String> getPage() {
		return page;
	}

	public void setPage(Map<String, String> page) {
		this.page = page;
	}

	public boolean getJavaScriptEnabled() {
		return javaScriptEnabled;
	}

	public void setJavaScriptEnabled(boolean javaScriptEnabled) {
		this.javaScriptEnabled = javaScriptEnabled;
	}
}
