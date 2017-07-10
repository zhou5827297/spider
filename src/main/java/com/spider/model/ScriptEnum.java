package com.spider.model;

import java.nio.charset.StandardCharsets;
import common.base.util.JsonUtil;
import net.sf.json.JSONObject;

/**
 * 脚本类型
 */
public enum ScriptEnum {
	TITLE("title", 1, "标题"), HEAD("head", 2, "头部"), BODY("body", 4, "正文"), HREF("links", 8, "链接"), AUTHOR("author", 16, "文章出处"), PTIME("ptime", 32, "原文发布时间"), COMMENTS("comments", 64, "评论");
	public static final byte[] JSON = JsonUtil.toString(ScriptEnum.values()).getBytes(StandardCharsets.UTF_8);

	ScriptEnum(String code, Integer id, String title) {
		this.code = code;
		this.id = id;
		this.title = title;
	}
	/**标题*/
	String code;
	/**编号*/
	Integer id;
	/**名称*/
	String title;

	/**标题*/
	public final String getCode() {
		return code;
	}

	/**标题*/
	public final void setCode(String code) {
		this.code = code;
	}

	/**编号*/
	public final Integer getId() {
		return id;
	}

	/**编号*/
	public final void setId(Integer id) {
		this.id = id;
	}

	/**名称*/
	public final String getTitle() {
		return title;
	}

	/**名称*/
	public final void setTitle(String title) {
		this.title = title;
	}

	public static final ScriptEnum parseId(Integer id) {
		ScriptEnum[] values = values();
		if (id != null && values != null) {
			for (ScriptEnum value : values) {
				if (value != null) {
					if (id.equals(value.getId())) {
						return value;
					}
				}
			}
		}
		return null;
	}

	public static final ScriptEnum parseCode(String code) {
		ScriptEnum[] values = values();
		if (code != null && values != null) {
			for (ScriptEnum value : values) {
				if (value != null) {
					if (code.equalsIgnoreCase(value.getCode())) {
						return value;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		JSONObject ret = new JSONObject();
		ret.put("code", code);
		ret.put("id", id);
		ret.put("title", title);
		return ret.toString();
	}
}