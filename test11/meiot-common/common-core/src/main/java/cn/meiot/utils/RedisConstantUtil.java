package cn.meiot.utils;

public class RedisConstantUtil {


    /**
     *存储设备号主账户id的hash值
     */
    public static final String SERIAL_NUMBER_USER_ID = "serialNumber_userId";


    /**
     *别名的hash（设备）
     */
    public static final String NIKNAME_SERIALNUMBER = "nikname_SerialNumber";


    /**
     *别名的hash（开关）
     */
    public static final String NIKNAME_SWITCH = "nikname_switch";


    /**
     *用户登录的基础信息
     */
    public static final String USER_TOKEN = "User_token_";


    /**
     *通过用户id与设备号存储的主账号id
     */
    public static final String DEVICE_RTUER_SERIALNUMER = "Device_Rtuer_serialNumer";

    /**
     *master_index
     */
    public static final String DEVICE_MASTER_INDEX = "Device_master_index";
    
    /**
     *master_index
     */
    public static final String DEVICE_MASTER_SN = "Device_master_sn";


    /**
     *故障事件信息
     */
    public static final String FAULT_EVENTS = "fault_events";

    /**
     *图片配置信息
     */
    public static final String IMG_CONFIG = "img_config";


    /**
     *存储设备号id的hash值
     */
    public static final String SERIAL_NUMBER_USER_TYPE = "serialNumber_userType";

    /**
     * 存储设备故障消息类型
     */
    public  static  final  String FAULT_SERIALNUMER="fault";


    /**
     * 存储设备故障消息类型
     */
    public  static  final  String USER_ROLES="user_roles_";
    
    /**
     * 设备项目+设备号
     */
    public  static  final  String PROJECT_SERIALNUMER="project_serial_";
    
    /**
     * 项目断路器参数+项目id
     */
    public  static  final  String PROJECT_PARAMETER="project_parameter_";


    /**
     * User_permission_
     */
    public  static  final  String USER_PERMISSIONS="user_permission_";


    /**
     * device
     */
    public  static  final  String DEVICE="device_";

    /**
     * 项目的创建时间
     */
    public  static  final  String PROJECT_CREATE_TIME="project_create_time";


    /**
     * 项目的名称
     */
    public  static  final  String PROJECT_NAMES="project_names";


    /**
     * 用户的默认项目
     */
    public  static  final  String DEFAULT_PROJECT="default_project";


    /**
     * 用户的默认项目
     */
    public  static  final  String DEL_USER_TOKEN="del_user_token";

    /**
     * 文件大小
     */
    public  static  final  String FILE_SIZE="file_size";

    /**
     * access_token
     */
    public  static  final  String ACESS_TOKEN="access_token";

    /**
     * Ticket
     */
    public  static  final  String QRCODE_TICKET="qrcode_Ticket";

    /**
     * 用户openid
     */
    public  static  final  String USER_OPENID="user_openid";

    /**
     * 华为accessToken
     */
    public static final String HUAWEI_ACCESS_TOKEN="huawei_access_token";

    /**
     * 用户openid
     */
    public  static  final  String USER_TYPE="user_types";
    /**
     * 水箱token
     */
    public  static  final  String WATER_TOKEN="water_token";


    /**
     * 用户头像
     */
    public  static  final  String USER_HEAD_PORTRAIT ="user_head_portrait";


    /**
     * 微信code标识
     */
    public  static  final  String WX_CODE_UNIONID ="wx_code_unionid";

    /**
     * 微信code标识
     */
    public  static  final  String WX_USER_INFO ="wx_user_info";

    /**
     * 华为注册账号密码key
     */
    public static final String HUAWEI_PASSWORD_SERIAL="huawei_password_serial";

    public static class ConfigItem{
        /**
         * 配置项key
         */
        public  static  final  String CONFIG_KEYS ="config_keys";

        /**
         * 用户默认头像
         */
        public static final String USER_DEFAULT_HEAD_PORTRAIT = "user_default_head_portrait";

        /**
         * 系统通知默认图标
         */
        public  static  final  String SYS_INFORM_DEFAULT_ICON_KEY ="sys_inform_default_icon_key";

        /**
         * 系统公告默认图标
         */
        public  static  final  String SYS_PROCLAMATION_DEFAULT_ICON_KEY ="sys_proclamation_default_icon_key";



    }

    /**
     * 用户昵称
     */
    public static final String USER_NIKNAMES= "user_niknames";

    /**
     * 设备联网状态
     */
    public static final String SERIAL_ONLINE = "serial_online_";

    /**
     * 设备刷新开关数量
     */
    public static final String SERIAL_REFRESH = "serial_refresh_";

    /**
     * 213上传温度，电流， 负载数据记录时间戳
     */
    public static final String UPLOAD_DATA = "upload_data_";


    /**
     * 213记录电量上传的
     */
    public static final String UPLOAD_METER = "upload_meter_";

    public static final String ADD_SERIAL = "add_serial_";
}
