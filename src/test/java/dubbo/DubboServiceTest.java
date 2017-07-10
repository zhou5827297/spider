//package dubbo;
//
//import com.spider.dubbo.service.SpiderSyncService;
//import DubboService;
//import Sequence;
//import SnowflakeSequence;
//import ApplicationContextUtils;
//import SpringContextHolder;
//import com.zhoukai.service.monitor.ProcessService;
//import com.zhoukai.service.monitor.SiteService;
//import com.zhoukai.entity.Process;
//import com.zhoukai.entity.Site;
//import com.zhoukai.status.ProcessStatusEnum;
//import org.junit.Test;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by zhoukai on 2017/4/5.
// */
//public class DubboServiceTest {
//    @Test
//    public void testDubbo() {
//        Sequence sequence = new SnowflakeSequence();
//        ApplicationContextUtils.getBean(SpringContextHolder.class);
//        SiteService siteService = DubboService.getSiteService();
//        Site site = new Site();
//        site.setId(sequence.getSequenceId());
//        site.setTitle("　杭州于2016年增补了有关儿童交通安全的规定，明确规定“驾驶员及副驾驶乘客必须使用安全带”，“4周岁以下或者身高低于1米的儿童乘坐小型家用轿车时应当配备并正确使用儿童安全座椅”。然而，在实际出行中，真正安装和使用儿童安全座椅的私家车比例较低，给车内婴幼儿的安全问题构成了隐患。\n" +
//                "\n" +
//                "　　建议：细化相关规定，制定具体的惩罚措施，如：4周岁以下或身高低于1米的儿童乘坐家用轿车未配备和正确使用安全座椅的，罚款100元，扣1分等；同时，加强交通监管力度，加大使用儿童安全座椅的宣传力度。\n" +
//                "\n" +
//                "　　——蒋吉清(界别：无党派人士)\n" +
//                "\n" +
//                "　　国际慢城，是意大利只有1.5万人的小城市布拉提出的一种新的城市模式。人口不超过5万、注重可持续发展、汽车行驶速度不超过20公里/小时、广告牌和霓虹灯光电设备尽量少用、到处张贴“蜗牛”标识倡导“慢生活”理念……至今，全球25个国家已有145个城市宣称为“慢城”。\n" +
//                "\n" +
//                "　　杭州西部区域(包括余杭、临安、富阳、桐庐、建德、淳安等区县市)地域广阔，生态资源丰富、文化资源深厚、产业资源多元，在人们生活水平越来越高，更为关注品质生活、休闲方式的发展阶段，推进高端的“国际慢城”建设有利于挖掘有效需求，助推城市产业的转型升级。建议构建“国际慢城”生活体验区要与该区域内“特色小镇”建设相融合、要着力用好山水优势和人文优势、要积极融入智慧智能和互联互通。\n" +
//                "\n" +
//                "　　——寿遐(界别：民建)\n" +
//                "\n" +
//                "　　针对老年人的诈骗，也就是所谓的银发骗局近年来有高发趋势。骗子大都打着保健品、收藏品、高额借贷、高息理财等幌子，以各种骗术榨取老年人钱财。此类欺诈比电信、网络欺诈更加恶劣，不但引发民众的家庭纠纷，更带来老年人轻生的社会悲剧。\n" +
//                "\n" +
//                "　　建议相关执法部门加大对销售假药的不法分子的打击力度，对看似合法经营实则打擦边球的保健品公司加强监管，堵住源头；同时充分发挥社区的作用，在超市，菜场、银行、医院等诈骗高发地段，加大监管和宣传力度；在有条件的农村社区设立集供餐、医疗、健身、教育、娱乐和心理疏导等功能于一体的社区社会福利馆，通过经济供养、生活照料和精神需求“一站式”服务，鼓励农村留守老人走出家庭，走进社区，进入科学养老的生活系统等等。\n" +
//                "\n" +
//                "　　——陈旭虎(界别：无党派人士)\n" +
//                "\n" +
//                "　　高校外迁为国家大形势，杭州市区部分高校受校区面积偏小等客观因素制约，难以满足学校进一步发展的需要，需以迁建的模式完成规范设置，而下沙高教园区已缺少高校继续扩大规模进行发展的区域，使得在杭高校向杭州市西部县市迁建正成为一种可能。\n" +
//                "\n" +
//                "　　杭州西部县市建德市、临安市、桐庐县、淳安县等，有的已进入“高铁时代”，有的形成综合交通体系，同时还具有山水生态优势以及拓展建设空间。建议由市政府制定《杭州西部县市关于加快引进优质高等教育资源的意见》，在资金、土地、人才落户及生活等方面出台一系列支持政策；可先建大学分校、独立学院、大一校区等，条件成熟后再整体外迁至西部县市。\n" +
//                "\n" +
//                "　　——王才洪(界别：教育)\n" +
//                "\n" +
//                "　　杭州特色的“斑马线让行”已经成为这座城市一道靓丽风景线。而夜里，尤其是天气恶劣导致视线受阻的时候，斑马线本身都不清晰不显眼，更难看清斑马线上的行人。设置“会发光的斑马线”，好比是杭州斑马线文化的2.0版。用耐重力好的LED灯管替代斑马线，将LED斑马线和路灯的电路相连接，只要天色一暗，路灯亮了，斑马线就跟着发光，对司机和行人都有非常明确的指示作用，将大大减少因此产生的交通问题，节能又环保的LED灯带设置成本并不是很高，而且调成合适亮度后，也不会影响司机视线，更不会造成光污染。\n" +
//                "\n" +
//                "　　——葛继宏(界别：文艺)\n" +
//                "\n" +
//                "　　快递包装循环再利用的呼声一直都很高，但迄今没有得到实质性落实，原因主要是：包装纸箱没有统一标准；重复利用纸箱易破损；缺乏消费者配合。\n" +
//                "\n" +
//                "　　针对这些难点，建议邮政、质检、环保等部门联合出台快递包装技术标准、规范标准，统一网购行业货物包装。比如，采取分类包装、分类计费，根据商品属性，采用不同的包装材料，在材料、结构上明确要求，只能统一使用设计为可循环重复使用、可降解的环保包装箱、包装袋，不能使用一次性包装箱、包装袋。\n" +
//                "\n" +
//                "　　同时，合理规划布局，设置网购包装二次回收点。如在智能邮柜附近增设快递包装回收点，允许电商开通“纸箱当面回收”的服务，通过送积分等优惠措施鼓励消费者配合回收。另一方面，可以在推行垃圾分类的过程中，在各小区、办公楼、公共场所等设有分类垃圾桶的点，选取一部分增设快递垃圾回收点，方便市民投放快递垃圾。\n" +
//                "\n" +
//                "　　——诸剑超(界别：民进)");
//        site.setContext("http://blog.csdn.net/augfun/article/details/54590983");
//        site.setFileContent("d:\\");
//        site.setDeleted(false);
//        try {
//            int flag = siteService.insert(site);
//            System.out.println(flag);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void testProcessService() {
//        Sequence sequence = new SnowflakeSequence();
//        ApplicationContextUtils.getBean(SpringContextHolder.class);
//        ProcessService processService = DubboService.getProcessService();
//        Process process = new Process();
//        String processId = sequence.getSequenceId();
//        process.setId(processId);
//        process.setBeginTime(new Date());
//        process.setStatus(ProcessStatusEnum.WAIT.getStatus());
//        process.setSiteId("1");
//        processService.insert(process);
//        System.out.println("zhoukai");
//
//    }
//
//    @Test
//    public void testPushUrls() {
//        ApplicationContextUtils.getContext();
//        SpiderSyncService spiderSyncService = DubboService.getSpiderSyncService();
//        try {
//            List<String> urls = spiderSyncService.listUrl("create_time desc", 1, 10000);
//            System.out.println(urls.size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//}
