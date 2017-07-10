package com.spider.service;

/**
 * 爬虫
 */
public interface Spider {
	/**
	 * 脚本
	 */
	void scripts();

	/**
	 * 下一页
	 */
	void nextUri();

	/**
	 * 下一站
	 */
	boolean nextSite();
}
