import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by zhoukai on 2017/4/11.
 */
public class DateTest {
    public static void main(String[] args) throws Exception {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy/'quanguo'_MMdd");
//        System.out.println(format.parse("2017/quanguo_0411"));
//        LockSupport.parkNanos(1000);
//        System.out.println("zhoukai");
//        File file =new File("D:\\1.jpg");
//        System.out.println(file.getPath());
//        boolean find = file.delete();
//        System.out.println(find);
//        System.out.println(file.getPath());
        FileWriter fout = null;
        PrintWriter out = null;
        try {
            //在tempPath路径下创建临时文件"mytempfileXXXX.tmp"
            //XXXX 是系统自动产生的随机数, tempPath对应的路径应事先存在
            File tempFile = File.createTempFile("mytempfile", ".json", new File("."));
            System.out.println(tempFile.getAbsolutePath());
            fout = new FileWriter(tempFile);
            out = new PrintWriter(fout);
            out.println("some info!");
            out.close(); //注意：如无此关闭语句，文件将不能删除
            tempFile.delete();
//            tempFile.deleteOnExit();
        } catch (IOException e1) {
            System.out.println(e1);
        } finally {
            if (out != null) {
                out.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
}
