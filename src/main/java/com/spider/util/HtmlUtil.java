package com.spider.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class HtmlUtil {
	public static final String trimInner(String s, String tag) {
		Pattern parten_begin = Pattern.compile("<" + tag + "[^>]*>", Pattern.CASE_INSENSITIVE);
		Pattern parten_end = Pattern.compile("</" + tag + ">", Pattern.CASE_INSENSITIVE);
		return trimInner(s, parten_begin, parten_end);
	}

	public static final String trimComment(String s) {
		return trimInner(s, Regs.COMMENT_BEGIN, Regs.COMMENT_END);
	}

	public static final String trimWithoutTag(String s, String tag) {
		Matcher matcher = Regs.TAG.matcher(s);
		int offset = 0;
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			sb.append(s.substring(offset, matcher.start()).trim());
			String g2 = matcher.group(2);
			if (tag != null && g2.matches(tag)) {
				String g1 = matcher.group(1);
				String g4 = matcher.group(4);
				if (g1.length() == 0) {
					Matcher m = Regs.SRC.matcher(matcher.group(3));
					if (m.find()) {
						sb.append("<" + g2 + m.group() + g4 + ">");
					} else {
						sb.append("<" + g2 + g4 + ">");
					}
				} else {
					sb.append("<" + g1 + g2 + g4 + ">");
				}
			}
			offset = matcher.end();
		}
		if (offset < s.length()) {
			sb.append(s.substring(offset).trim());
		}
		//log.debug(sb.toString());
		return sb.toString();
	}

	private static final String trimInner(String s, Pattern parten_begin, Pattern parten_end) {
		Matcher matcher_begin = parten_begin.matcher(s);
		Matcher matcher_end = parten_end.matcher(s);
		int offset = 0;
		StringBuilder sb = new StringBuilder();
		boolean find;
		while (find = matcher_begin.find()) {
			int beginStart = matcher_begin.start();
			if (offset > beginStart) {
				continue;
			} else {
				if (offset < beginStart) {
					sb.append(s.substring(offset, beginStart).trim());
				}
				if (matcher_end.find()) {
					offset = matcher_end.end();
				} else {
					offset = s.length();
					break;
				}
			}
		}
		if (!find) {
			sb.append(s.substring(offset).trim());
		}
		//log.debug(sb.toString());
		return sb.toString();
	}

	public static final String filterContent(String content) {
		String rets = null;
		if (StringUtils.isNotEmpty(content)) {
			rets = content.replaceAll("<script(?:[^<]++|<(?!/script>))*+</script>", "")//去除script标签
					.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "")//去除style
					.replaceAll("</?[^/?(br)|(p)|(img)][^><]*>", "")//去掉其他标签除（p、image、br）
					.replaceAll("&nbsp;", "").replaceAll("&nbsp", "").replaceAll("\\{ProofReader\\}", "").replaceAll("(class|style|width|height|alt|title)\\s*=\\s*('[^']*'|\"[^\"]*\")", "");//去掉样式及其他
		}
		return rets;
	}

	public static final String trimEmptyTag(String s) {
		return s.replace("<p></p>", "").replace("<td></td>", "").replace("<tr></tr>", "").replace("<table></table>", "");
	}
}
