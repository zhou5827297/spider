import com.spider.util.VMInfo;

/**
 * Created by zhoukai on 2017/5/23.
 */
public class VmTest {
    public static void main(String[] args){
        VMInfo info =VMInfo.getVmInfo();
        System.out.println(info.toString());
        System.out.println("======================================================");
        System.out.println(info.totalString());
    }
}
