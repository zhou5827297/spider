package com.spider.util;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterUtils {

    //从Nutch借鉴的网页编码检测代码
    private static final int CHUNK_SIZE = 2000;

    private static Pattern metaPattern = Pattern.compile(
            "<meta\\s+([^>]*http-equiv=(\"|')?content-type(\"|')?[^>]*)>",
            Pattern.CASE_INSENSITIVE);
    private static Pattern charsetPattern = Pattern.compile(
            "charset=\\s*([a-z][_\\-0-9a-z]*)", Pattern.CASE_INSENSITIVE);
    private static Pattern charsetPatternHTML5 = Pattern.compile(
            "<meta\\s+charset\\s*=\\s*[\"']?([a-z][_\\-0-9a-z]*)[^>]*>",
            Pattern.CASE_INSENSITIVE);

    //从Nutch借鉴的网页编码检测代码
    private static String guessEncodingByNutch(byte[] content) {
        int length = Math.min(content.length, CHUNK_SIZE);

        String str = "";
        try {
            str = new String(content, "ascii");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        Matcher metaMatcher = metaPattern.matcher(str);
        String encoding = null;
        if (metaMatcher.find()) {
            Matcher charsetMatcher = charsetPattern.matcher(metaMatcher.group(1));
            if (charsetMatcher.find()) {
                encoding = new String(charsetMatcher.group(1));
            }
        }
        if (encoding == null) {
            metaMatcher = charsetPatternHTML5.matcher(str);
            if (metaMatcher.find()) {
                encoding = new String(metaMatcher.group(1));
            }
        }
        if (encoding == null) {
            if (length >= 3 && content[0] == (byte) 0xEF
                    && content[1] == (byte) 0xBB && content[2] == (byte) 0xBF) {
                encoding = "UTF-8";
            } else if (length >= 2) {
                if (content[0] == (byte) 0xFF && content[1] == (byte) 0xFE) {
                    encoding = "UTF-16LE";
                } else if (content[0] == (byte) 0xFE
                        && content[1] == (byte) 0xFF) {
                    encoding = "UTF-16BE";
                }
            }
        }

        return encoding;
    }

    /**
     * 根据字节数组，猜测可能的字符集，如果检测失败，返回utf-8
     *
     * @param bytes 待检测的字节数组
     * @return 可能的字符集，如果检测失败，返回utf-8
     */
    public static String guessEncodingByMozilla(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }

    /**
     * 根据字节数组，猜测可能的字符集，如果检测失败，返回utf-8
     *
     * @param content 待检测的字节数组
     * @return 可能的字符集，如果检测失败，返回utf-8
     */
    public static String guessEncoding(byte[] content) {
        String encoding = null;
        try {
            encoding = guessEncodingByNutch(content);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (encoding == null) {
            try {
                encoding = guessEncodingByMozilla(content);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return encoding;
    }

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }

    }


    private final static String[] MESSYCODES = {"�"};

    /**
     * 判断是否包含，乱码符号中的字符
     */
    public static boolean isContainsMessyCode(String content) {
        boolean pass = false;
        for (String messycode : MESSYCODES) {
            if (content.contains(messycode)) {
                pass = true;
                break;
            }
        }
        return pass;
    }

    public static void main(String[] args) throws Exception {
		System.out.println(isMessyCode("周锴�"));
//		System.out.println(isMessyCode("你好"));
        String content = "12宀佷互涓嬬\uE6E6鐢ㄢ�滄\uE11B鍜宠嵂姘粹��";
//		String encoding = guessEncoding(content.getBytes("GBK"));
        System.out.println(content.contains("�"));
        System.out.println(isContainsMessyCode("宠嵂姘粹��"));

        String destination = "周锴�";
        if(destination.equals(new String(destination.getBytes("iso8859-1"), "iso8859-1")))
        {
            destination=new String(destination.getBytes("iso8859-1"),"utf-8");
        }
        System.out.println(destination);
    }


}
