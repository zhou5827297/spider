package com.spider.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class Multidownload {

    static int ThreadCount = 30;   //线程的个数
    static String path = "http://36.110.226.58/115/13/61/letv-uts/14/ver_00_22-303265745-avc-478337-aac-32001-5676042-368330639-cbd2a4c8377b35ec9bd72658e2cd8cae-1421951800636.mp4?crypt=15aa7f2e583&b=519&nlh=4096&nlt=60&bf=90&p2p=1&video_type=mp4&termid=2&tss=no&platid=3&splatid=301&its=0&qos=4&fcheck=0&amltag=4701&mltag=4701&proxy=3702879645,2096101933,3702879652&uid=611189306.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1493194800&nkey=a72ba05d34ea86bedcce09178512e8f0&nkey2=0cd1ca08a3f407d295dc3922884b9311&geo=CN-1-0-1&mmsid=20016284&tm=1493176355&key=7aca6dd8d80d20bff56d0959bd5ebce1&playid=0&vtype=13&cvid=473820701120&payff=0&p1=0&p2=04&ostype=un&hwtype=iphone&uuid=1176355892167513&vid=20011977&errc=0&gn=1218&vrtmcd=108&buss=4701&cips=36.110.2.58";  //确定下载地址
    static String downloadFile = "熊出没之夺宝熊兵.mp4";

    public static void main(String[] args) {

        //发送get请求，请求这个地址的资源
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == 200) {
                //获取到请求资源文件的长度
                int length = conn.getContentLength();
                File file = new File(downloadFile);
                //创建随机存储文件
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                //设置临时文件的大小
                raf.setLength(length);
                //关闭raf
                raf.close();
                //计算出每一个线程下载多少字节

                int size = length / Multidownload.ThreadCount;

                for (int i = 0; i < Multidownload.ThreadCount; i++) {
                    //startIndex,endIndex分别代表线程的开始和结束位置
                    int startIndex = i * size;
                    int endIndex = (i + 1) * size - 1;
                    if (i == ThreadCount - 1) {
                        //如果是最后一个线程，那么结束位置写死
                        endIndex = length - 1;
                    }
                    System.out.println("线程" + i + "的下载区间是" + startIndex + "到" + endIndex);
                    new DownLoadThread(startIndex, endIndex, i).start(); //创建线程下载数据
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

class DownLoadThread extends Thread {
    int startIndex;
    int endIndex;
    int threadId;

    public DownLoadThread(int startIndex, int endIndex, int threadId) {
        super();
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        //使用http请求下载安装包文件
        URL url;
        try {
            url = new URL(Multidownload.path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //设置请求数据的区间
            conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
            //请求部分数据的响应码是206
            if (conn.getResponseCode() == 206) {
                //获取一部分数据来读取
                InputStream is = conn.getInputStream();
                byte[] b = new byte[1024];
                int len = 0;
                int total = 0;
                //拿到临时文件的引用
                File file = new File(Multidownload.downloadFile);
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                //更新文件的写入位置，startIndex
                raf.seek(startIndex);
                while ((len = is.read(b)) != -1) {
                    //每次读取流里面的数据，同步吧数据写入临时文件
                    raf.write(b, 0, len);
                    total += len;
                    System.out.println("线程" + threadId + "下载了" + total);
                }
                System.out.println("线程" + threadId + "下载过程结束===========================");
                raf.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    ;
}
