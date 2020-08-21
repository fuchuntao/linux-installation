package cn.meiot.utils;


/**
 * 队列常量
 */
public class QueueConstantUtil {

    /**
     * 更新进度条
     *
     */

    public static final String PROGRESS_BAR="progressBar";
    /**
     * 发送短信消息
     */
    public static final String SEND_SMS_MSG = "sendSms";

    /**
     * 删除短信验证码
     *
     */
    public static final String DEL_SMS_CODE = "delCode";
    /**
     * 接收硬件上传的信息(交换机)
     */
    public static final String MQTT_DEVICE_EVNET = "MQTT_device_event";

    /**
     * 接收硬件上传的信息（路由key）
     */
    public static final String MQTT_DEVICE_KEY = "key_event";

    /**
     * 接收硬件上传的信息（队列名）
     */
    public static final String MQTT_DEVICE_QUEUE = "MQTT_event_queue";

    /**
     * statisticsMeter
     */
    public static final String STATISTICS_METER_APP = "statisticsMeterApp";

    /**
     * 接受108消息(交换机)
     */
    public static final String MQTT_DEVICE_REGISTER = "MQTT_device_108";

    /**
     * 接受108消息(交换机)
     */
    public static final String MQTT_DEVICE_101 = "MQTT_device_101";

    /**
     * 接收104消息(交换机)   MQTT_device_status
     */
    public static final String MQTT_DEVICE_STATUS = "MQTT_device_104";

    /**
     * 110交换机
     */
    public static final String MQTT_DEVICE_110="MQTT_device_110";

    /**
     * 二代
     * 210交换机
     */
    public static final String MQTT_DEVICE_210="MQTT_device_210";

    /**
     * 二代
     * 221交换机
     */
    public static final String MQTT_DEVICE_221="MQTT_device_221";


    /**
     * 二代
     * 210交换机
     */
    public static final String MQTT_DEVICE_208="MQTT_device_208";
    /**
     * 二代
     * 213交换机
     */
    public static final String MQTT_DEVICE_213="MQTT_device_213";

    /**
     * 二代
     * 213交换机
     */
    public static final String MQTT_DEVICE_216="MQTT_device_216";
    /**
     * 二代
     * 213交换机
     */
    public static final String MQTT_DEVICE_215="MQTT_device_215";


    /**
     * 设备更换统计修改
     */
    public static final String CHANGE_SWTICH_SN="change_switch_sn";

    /**
     * 接收104消息（路由key）   key_status
     */
    public static final String MQTT_DEVICE_STATUS_KEY = "key_status_104";

    /**
     * 接收104消息（路由key）   key_status
     */
    public static final String MQTT_DEVICE_STATUS_101 = "key_status_101";


    /**
     * 接收104消息（路由key）   key_status
     */
    public static final String MQTT_DEVICE_UPGRADE_208 = "upgrade_208";

    /**
     * 预警|报警 信息 交换机
     */
    public static final String ALARM_MQTT_110="key_status_110";

    /**
     * 二代开关数据上报
     */
    public static final String MQTT_DEVICE_STATUS_210 = "key_status_210";

    /**
     * 二代开关数据上报漏电自检
     */
    public static final String MQTT_DEVICE_STATUS_221 = "key_status_221";


    /**
     * 二代 版本号
     */
    public static final String MQTT_DEVICE_STATUS_208 = "key_status_208";


    /**
     * 二代 213
     */
    public static final String MQTT_DEVICE_STATUS_213 = "key_status_213";
    /**
     * 二代 216
     */
    public static final String MQTT_DEVICE_STATUS_216 = "key_status_216";


    /**
     * 二代 213
     */
    public static final String MQTT_DEVICE_STATUS_215 = "key_status_215";

    /**
     * 接收108消息（路由key）
     */
    public static final String MQTT_DEVICE_REGISTER_KEY = "key_status_108";


    /**
     * 统计天的用电量
     */
    public static final String STATISTICS_DAY_QUEUE = "statistics_day_meter_queue";

    /**
     * 发送系统消息队列
     */
    public static final String SYS_MSG_QUEUE = "sys_msg_queue";



    /**
     * 系统公告队列
     */
    public static final String PUBLISH_SYSTEM_BULLETIN = "publishSystemBulletin";

    /**
     *保存用户的操作日志
     */
    public static final String SAVE_OPERATION_LOG = "save_operation_log";


    /**
     *主账号解绑设备通知队列
     */
    public static final String UNBIND_DEVICE_NOTIFICATION = "unbind_device_notification";

    /**
     *发送邮箱
     */
    public static final String SEND_EMAIL_QUEUE = "send_email_queue";

    /**
     * 设置定时
     */
    public static final String SEND_TIMER ="send_timer";

    /**
     * 设置定时
     */
    public static final String SEND_TIMER2 ="send_timer2";

    /**
     * 重置开关信息
     */
    public static final String RESET_SWITCH = "reset_switch";  
    /**
     * 获取设备数据
     */
    public static final String QUERY_SERIA = "query_seria";
    
    /**
     * 关闭定时
     */
    public static final String OFF_TIMER ="off_timer";

    /**
     * 固件升级
     */
    public static final String UP_VERSION ="up_version";

    /**
     *平台添加账户通知队列
     */
    public static final String MODIFY_USER_NOTIFICATION = "modify_user_notification";


    /**
     * 开关事件
     */
    public static final String SWITCH_STATUS ="SWITCH_STATUS";

    /**
     * 报警预警
     */
    public static final String ALARM_QUEUE="alarm_queue";

    /**
     *
     */
public static final String ALARM_QUEUE_TWO="alarm_queue_two";

    /**
     * 统计pc端天的用电量
     */
    public static final String STATISTICS_DAY_QUEUE_PC = "statistics_day_meter_queue_pc";


    /**
     * 统计用户量的数据
     */
    public static final String STATISTICS_USER_QUEUE = "statistics_user_queue";


    /**
     * 将用户踢出下线
     */
    public static final String TAKE_THE_USER_OFFLINE = "take_the_user_offline";

    /**
     *保存用户的操作异常日志
     */
    public static final String SAVE_EXCEPTION_LOG = "save_exception_log";

    /**
     *保存用户登录日志
     */
    public static final String SAVE_LOGIN_LOG = "save_login_log";

    /**
     *比较项目类型权限和项目权限，超出部分删除
     */
    public static final String DELETE_SURPLUS_PROJECT_PERMISSION = "delete_surplus_project_permission";

    /**
     *删除企业用户多余的权限
     */
    public static final String DELETE_SURPLUS_ENTERPRISE_PERMISSION = "delete_surplus_enterprise_permission";


    /**
     * 推送大数据路由名称
     */
    public static final String WSS_CMD_21="WSS_CMD_21";

    /**
     *推送大数据路由KEY
     */

    public static final String  WSS_KEY="wss_key";

    /**
     *权限校验
     */

    public static final String  PERMISSION_CHECK="PERMISSION_CHECK";

    /**
     *替换开关改变电量
     */
    public static final String UPTATE_METER="update_meter";

    /**
     *替换开关改变电流
     */
    public static final String UPTATE_LEAKAGE="update_leakage";

    /**
     *替换开关改变温度
     */
    public static final String UPTATE_TEMP="update_temp";

    /**
     *替换开关改变负载
     */
    public static final String UPTATE_POWER="update_power";

    /**
     *电量上传失败
     */
    public static final String LOSE_METER="lose_meter";


    /**
     *查询电量上传失败
     */
    public static final String SELECT_LOSE_METER="select_lose_meter";


    /**
     * 断路器参数设置
     */
    public static final String CRCUIT="crcuit_cmd_03";

    /**
     * 断路器参数设置
     */
    public static final String CRCUIT2="crcuit_cmd_205";

    /**
     * 断路器参数设置
     */
    public static final String EXAMINATION2="examination2";

    /**
     * 保存用户openid
     */
    public static final String SAVE_USER_OPENID="save_user_openid";



    /**
     * 删除用户openid
     */
    public static final String DELETE_USER_OPENID="delete_user_openid";

    /**
     * 故障工单
     */
    public static final String TROUBLE_TICKET = "trouble_ticket";

    /**
     * 水表抄表记录
     */
    public static final String WATER_RECORD = "water_record";

    /**
     * 水表修改记录
     */
    public static final String UPDATE_WATER_RECORD = "update_water_record";

    /**
     * 华为注册设备
     */
    public static final String REGISTE_RSERIAL="register_serial";

    /**
     * 华为激活设备
     */
    public static final String ACTIVATION_RSERIAL="activation_serial";

    /**
     * synchronizeStatic 个人同步故障消息状态
     */
    public static final String APP_SYNCHRONIZE_STATIC="app_synchronize_static";

    /**
     * synchronizeStatic 企业同步故障消息状态
     */
    public static final String QY_SYNCHRONIZE_STATIC="qy_synchronize_static";



    /**
     * 手动拉取水表数据队列
     */
    public static final String HAND_WATER_RECORD = "hand_water_record";


    /**
     * 水表月表修改对列数据
     */
    public static final String YEAR_WATER_RECORD = "year_water_record";


//    public static final String WATER_RECORD_TEST = "WATER_RECORD_TEST";


    /**
     *
     */
    public static class ProjectMessage{
        /**
         * 开关控制消息
         */
        public static final String SWITCH_CONTROL = "switch_control";
        /**
         * 漏电自检消息
         */
        public static final String EXAMINATION = "message_examination";
        /**
         * 功率限定消息
         */
        public static final String LOADMAX_ONE="message_loadmax_one";
        /**
         * 功率限定统一设置消息
         */
        public static final String LOADMAX_ALL="message_loadmax_all";
    }

    /**
     * 开放平台的队列
     */
    public static class OpenApi{
        /**
         * 设备数据上报 交换机
         */
        public static final String CALL_BACK_ADD_EQUIPMENT = "call_back_add_equipment";
        /**
         *队列
         */
        public static final String QUEUE_OPEN_ADD_EQUIPMENGT = "open_add_equipment";
        /**
         * 路由key
         */
        public static final String ROUTING_ADD_EQUIPMENGT = "routing_add_equipment";


        /**
         * 开关状态变化
         */
        public static final String CALL_BACK_STATUS = "call_back_switch_status";
        /**
         * 开关状态变化队列
         */
        public static final String QUEUE_OPEN_STATUS = "open_switch_status";
        /**
         * 开关状态变化队列
         */
        public static final String ROUTING_STATUS = "routing_switch_status";


        /**
         * 故障预警
         */
        public static final String CALL_BACK_FAULT = "call_back_fault";
        /**
         * 故障预警队列名
         */
        public static final String QUEUE_OPEN_FAULT = "open_back_fault";
        /**
         * 故障预警队列名
         */
        public static final String ROUTING_FAULT = "routing_back_fault";


        /**
         * 故障预警
         */
        public static final String CALL_BACK_EXAMINATION = "call_back_examination";
        /**
         * 故障预警队列
         */
        public static final String QUEUE_OPEN_EXAMINATION = "open_examination";
        /**
         * 故障预警队列
         */
        public static final String ROUTING_EXAMINATION = "routing_examination";


    }

    /**
     * 二代电压，电流，温度
     */
    public static final String TEMP_STATISTICS_V2 = "tempStatisticsV2";



}
