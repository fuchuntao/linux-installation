package cn.meiot.entity;

import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2020-02-17
 */
@Data
public class TroubleTicketVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 设备拥有者
     */
    private Long userId;

    /**
     * 报修人的电话
     */
    private String tel;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 故障类型ID
     */
    private Integer alarmType;

    /**
     * 故障类型名称
     */
    private String alarmTypeName;

    /**
     * 故障时间
     */
    private String alarmTime;

    /**
     * 备注
     */
    private String note;

    /**
     * 0 报修 1受理 2完成
     */
    private Integer type;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 0 显示 1不显示
     */
    private Integer isShow;
    /**
     * 开关ID
     */
    private String sn;

    /**
     * 开关名称
     */
    private String snName;

    /**
     * 电箱位置
     */
    private String address;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     *  是否为个人 0是 1不是
     */
    private Integer isApp;

    /**
     * 故障值
     */
    private String alarmValue;

    /**
     * 故障类型别名
     */
    private String fAlias;

    /**
     *故障类型符号
     */
    private String fAymbol;

    /**
     * 这个故障对应的人
     */
    public List<TroubleTicketVoUserAlarm> userAlarms;

    /**
     * 故障ID
     */
    private Long alarmId;

    //故障对用的用户信息
   public class TroubleTicketVoUserAlarm{
        //用户ID
        private Long userId;
        //故障ID
        private Long alarmId;
        //设备别名
        private String deviceAlias;

        //开关别名
        private String switchAlias;

        public TroubleTicketVoUserAlarm(Long userId, Long alarmId, String deviceAlias, String switchAlias) {
            this.userId = userId;
            this.alarmId = alarmId;
            this.deviceAlias = deviceAlias;
            this.switchAlias = switchAlias;
        }

        public TroubleTicketVoUserAlarm() {
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getAlarmId() {
            return alarmId;
        }

        public void setAlarmId(Long alarmId) {
            this.alarmId = alarmId;
        }

        public String getDeviceAlias() {
            return deviceAlias;
        }

        public void setDeviceAlias(String deviceAlias) {
            this.deviceAlias = deviceAlias;
        }

        public String getSwitchAlias() {
            return switchAlias;
        }

        public void setSwitchAlias(String switchAlias) {
            this.switchAlias = switchAlias;
        }
    }

}
