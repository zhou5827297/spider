package com.spider.model;

public class DateConfig {
	/**日期正则*/
	String regex;
	/**日期解析*/
	String format;
	/**日期替换*/
	String replace;

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getReplace() {
		return replace;
	}

	public void setReplace(String replace) {
		this.replace = replace;
	}
}
