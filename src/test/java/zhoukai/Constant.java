package zhoukai;

/**
 * Created by zhoukai on 2017/4/18.
 */
public class Constant {
    private static String NAME = Service.getName();

    private static class Service {
        private static String NAME = "zhoukai";

        public static String getName() {
            return NAME;
        }

        public static void setName(String name) {
            NAME = name;
        }
    }

    public static void print() {
        System.out.println(NAME);
    }

    public static void setName(String name) {
        NAME = name;
    }


    public static void main(String[] args) {
        Constant.print();
        Service.setName("kaizhou");
        Constant.print();
        Constant.setName("zhouzhoukaikai");
        Constant.print();
    }
}
