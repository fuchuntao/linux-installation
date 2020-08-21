package cn.meiot.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * 公共常量
 */
public class ConstantsUtil {


    public static class ConfigItem{
        /**
         * 配置项key
         */
        public  static  final  String CONFIG_KEYS ="config_keys";

        /**
         * 用户默认头像
         */
        public static final String USER_DEFAULT_HEAD_PORTRAIT = "user_default_head_portrait ";

        /**
         * 用户默认昵称
         */
        public static final String USER_DEFAULT_NIKNAME = "user_default_nikname ";

        /**
         * 系统通知默认图标
         */
        public  static  final  String SYS_INFORM_DEFAULT_ICON_KEY ="sys_inform_default_icon_key";

        /**
         * 系统公告默认图标
         */
        public  static  final  String SYS_PROCLAMATION_DEFAULT_ICON_KEY ="sys_proclamation_default_icon_key";

        /**
         * 系统参数富文本链接
         */
        public  static  final  String SYS_PARAM_RICH_TEXT_URL ="sys_param_rich_text_url";

        /**
         * 设备名
         */
        public final static String SERIAL_NAME = "default_serialNumer_name";
        /**
         * 主开关名称
         */
        public final static String MAIN_SWITCH = "default_switch_name";
        /**
         * 子开关
         */
        public final static String SUB_SWITCH = "default_sub_switch_name";

        /**
         * 故障报警预警列表图标前缀
         */
        public static final String ALARM_DELAUT_IMG="alarm_delaut_img_";

        /**
         * 设备规则校验key
         */
        public static final String DEVICE_FAMILY_RULES = "device_family_rules";
    }


    /**
     * 水表管理字典
     */
    public static class  WaterManager{

        /**
         * 统计更新的key
         */
        public static final String WATER_UPDATE_LAST_ID = "water_update_last_id";

        /**
         * 超过多大的数据开始使用多线程
         */
        public static final Integer WATER_MAX_NUM = 100000;
    }



    /**
     * socket类型
     */
    public static class  SocketType{

        /**
         * bind wx
         */
        public static final Integer BIND_WX = 100002;

        /**
         * 消息
         */
        public static final Integer MSG=100000;

        /**
         * 固件升级
         */
        public static final Integer FIRMWARE_UPGRADE=100001;


    }



    /**
     * 线程配置
     */
    public static class  ThreadParam{

        /**
         * 核心线程数
         */
        public static final Integer CORE_POOL_SIZE = 10;

        /**
         * 最大线程数
         */
        public static final Integer MAX_POOL_SIZE = 100;



        /**
         * 队列数
         */
        public static final Integer QUEUE_CAPACITY = 10;
    }



    /**
     * 水表管理字典
     */
    public static class  userManager{

        /**
         * 统计更新的key
         */
        public static final String USER_DEFAULT_HEAD_PORTRAIT  = "user_default_head_portrait ";
    }



    @Deprecated
    public static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private static ThreadLocal<SimpleDateFormat> t = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * 获取SimpleDateFormat实例
     * @return
     */
    public static SimpleDateFormat getSimpleDateFormat(){
        return t.get();
    }



    private static ThreadLocal<SimpleDateFormat> ymdFormat = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd"));

    /**
     * 获取SimpleDateFormat实例(年月日)
     * @return
     */
    public static SimpleDateFormat getYmdFormat(){
        return ymdFormat.get();
    }

    private static ThreadLocal<SimpleDateFormat> lzyFormat = ThreadLocal.withInitial(()->new SimpleDateFormat("d HH:00"));


    /**
     * 获取SimpleDateFormat实例(年月日)
     * @return ddhh00
     */
    public static SimpleDateFormat getddHH00Format(){
        return lzyFormat.get();
    }

    private static ThreadLocal<SimpleDateFormat> lzyFormat2 = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy"));


    /**
     * 获取SimpleDateFormat实例(年月日)
     * @return yyyy
     */
    public static SimpleDateFormat getyyyyFormat(){
        return lzyFormat2.get();
    }


    private static ThreadLocal<SimpleDateFormat> lzyFormat3 = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd"));


    /**
     * 获取SimpleDateFormat实例(年月日)
     * @return yyyy
     */
    public static SimpleDateFormat getyyyymmddFormat(){
        return lzyFormat3.get();
    }

    private static ThreadLocal<SimpleDateFormat> lzyFormat4 = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd HH.mm"));


    /**
     * 获取SimpleDateFormat实例(年月日)
     * @return yyyy
     */
    public static SimpleDateFormat getyyyyMMddhh_mmFormat1(){
        return lzyFormat4.get();
    }


    public static final String USER_ID = "userId";

    /**
     * 国际化
     */
    public static final String INTERNATIONALIZATION= "i18n/messages";


    public static final String TOKEN="token_";

    /**
     * 用户中心模块
     */
    public static final String MEIOT_USER="User_";

    /**
     * token的有效期 7*24*60    10080l
     */
    public static final long APP_TOKEN_EXPIRE_TIME=10080l;

    /**
     * token的有效期 3*60
     */
    public static final long OTHER_TOKEN_EXPIRE_TIME=180l;
    /**
     * app的有效时间类型
     */
    public static final TimeUnit EXPIRE_TYPE = TimeUnit.MINUTES;




    /**
     * token的验证字段
     */
    public static final String Authorization="Authorization";


    /**
     * 密码规则
     */
    public static final String PWD_RULE = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";

    /**
     * 权限模块key
     */
    public static final String SYS_PERMISSION = "user_permission_";


    /**
     * 用户所有项目key
     */
    public static final String USER_PROJECTS = "User_project_";
    
    /**
     * 项目key
     */
    public static final String PROJECT = "Project-Id";


    /**
     * 平台账户的类型
     */
    public static final Integer  ACCOUNT_TYPE= 1;


    /**
     * 企业账户的类型
     */
    public static final Integer   ENTERPRISE_ACCOUNT= 2;

    /**
     * 正常角色类型
     */
    public static final Integer  NORMAL_ROLE_STATUS= 1;


    /**
     * 菜单列表
     */
    public static final Integer  MENU_LIST= 1;



    /**
     * 短信签名密钥
     */
    public static final String  SIGN_SECRET= "meiot-space";

    /**
     * 短信签名密钥
     */
    public static final String  REPL_EMOJI_STR= "";


    /**
     *开启多线程的临界值
     */
    public static final Integer  START_THREAD_TOTAL= 10000;

    /**
     *拉取数据多线程的核心数
     */
    public static final int  WATER_THREAD_TOTAL= 5;

    /**
     * 设备默认图片小key
     */
    public static final String DEVICE_DEFAULT_RULES_SMAL_KEY="smalKey";

    /**
     * 设备默认图片小key
     */
    public static final String DEVICE_DEFAULT_RULES_BIG_KEY="bigKey";



    /**
     * 拉取水表记录的redis的key
     */
    public static final String  REDIS_WATER_PROJECT = "water_project_" ;



    /**
     * 拉取水表的redis过期时间
     */
    public static final Long  REDIS_WATER_EXPIRE_TIME = 30L;


    /**
     * 额定功率默认值  暂定。
     */
    public static final Long MAX_LOAD= 100000L;

    /**
     *断路器参数默认设置
     */
    public static final String CIRCUIT_PARAMETER = "circuit_parameter";

    /**
     * 报警key
     */
    public static final String ALARM_SERAIL = "alarm_device_";

    /**
     * 预警key
     */
    public static final String WARNING_SERAIL= "waring_device_";

    public static class SysPermission{
        /**
         *电工端
         */
        public static final String ELECTRICIAN = "electrician";
    }


    public static class ApplicationConstants {
        /**
         * 通过应用Key获取缓存中的应用详情
         */
        public static final String APPLICATION_KYE="application_key_";
        /**
         * 通过应用ID和类型获取回调地址
         */
        public static final String APPLICATION_CALLBACK="callback_id_type_";
    }
}

