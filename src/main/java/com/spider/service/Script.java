package com.spider.service;

/**
 * 脚本接口
 */
public interface Script {
	/**
	 * 处理脚本获取到的标题
	 */
	void title(String s);

	/**
	 * 处理头部，包括来源，发布时间，作者等
	 */
	void head(String s);

	/**
	 * 处理脚本获取到的正文
	 */
	void body(String s);

	/**
	 * 处理脚本获取到的链接
	 */
	void links(String s);

	/**
	 * 让脚步调用下一步，并告知是否抓完
	 */
	void finish(Boolean successFlag);

	/**
	 * 纯文本
	 */
	void text(String s);
}
