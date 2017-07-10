package com.spider.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 主键序列号生成器
 */
public class SequenceUtils {
    /**
     * 获取现在时间
     *
     * @return 返回字符串格式yyyyMMddHHmmssSSS（17位）
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 返回64位业务流水号
     *
     * @return
     */
    public static String generateBusNo() {
        // 15位数字数组
        int[] number = new int[15];
        // 循环变量
        int i = 0;
        StringBuffer bussinessNo = new StringBuffer(64);
        bussinessNo.append(getStringDate());
        bussinessNo.append(get32UUIDString());
        // 生成15位随机数算法
        for (i = 0; i < number.length; i++) {
            if (number[i] == 0) {
                // 产生0-10之间的随机小数，强制转换成正数
                number[i] = (int) (Math.random() * 10);
            }
            bussinessNo.append(number[i]);
        }

        System.out.println(bussinessNo.toString());
        return bussinessNo.toString();
    }

    /**
     * 获取32位UUID字符串
     */
    public static String get32UUIDString() {
        String uuidStr = UUID.randomUUID().toString().replaceAll("-", "");
        return uuidStr;
    }


    /**
     * 通过左移位操作（<<）给每一段的数字加权
     * 第一段的权为2的24次方
     * 第二段的权为2的16次方
     * 第三段的权为2的8次方
     * 最后一段的权为1
     *
     * @param ip
     * @return int
     */
    public static int ipToInt(String ip) {
        String[] ips = ip.split("\\.");
        return (Integer.parseInt(ips[0]) << 24) + (Integer.parseInt(ips[1]) << 16)
                + (Integer.parseInt(ips[2]) << 8) + Integer.parseInt(ips[3]);
    }

    /**
     * 将整数值进行右移位操作（>>）
     * 右移24位，右移时高位补0，得到的数字即为第一段IP
     * 右移16位，右移时高位补0，得到的数字即为第二段IP
     * 右移8位，右移时高位补0，得到的数字即为第三段IP
     * 最后一段的为第四段IP
     *
     * @param i
     * @return String
     */
    public static String intToIp(int i) {
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
    }

    public static void main(String[] args) {
//        int num = ipToInt("192.168.100.203");
//        System.out.println(num);
//        System.out.println(intToIp(num));
        System.out.println(generateBusNo());
    }
}
