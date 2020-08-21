package cn.meiot.entity.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PcTimerMode implements Serializable{



    private static final long serialVersionUID = 1L;

    /**
     * 定时最大功率，单位W
     */
     private Integer loadmax;

 /**
     * '1开关，2功率',
     */
       private Integer flag = 1;
 /**
  * id
    */
    @Id
	@GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 名称
     */
     private String name;

     /**
     * 类型:1=时间段起止模式，2=星期重复模式，3=时间点开关
     */
     private Integer type;

     /**
     * 描述
     */
     private String content;

     /**
     * 创建时间
     */
     private Date createTime;

     /**
     * 更新时间
     */
     private Date updateTime = new Date();

     /**
     * 开关状态
     */
     private Integer isSwitch;

     /**
     * 开始时间
     */
     private String startTime;

     /**
     * 结束时间
     */
     private String endTime;

     /**
     * 关闭时间
     */
     private String off;

     /**
     * 打开时间
     */
     @Transient
     private String on;

     /**
     * cycle
     */
     private String cycle;

     /**
     * 项目id
     */
     private Integer projectId;

     /**
     * user_id
     */
     private Long userId;

     private String kai;

     /**
      * 设备数量
      */
     @Transient
     private int serialCount;

     /**
      * 开关数量
      * @param kai
      */
     @Transient
     private int switchCount;

     public void setKai(String kai) {
             this.kai = kai;
             this.on = kai;
         }
     public void setOn(String on) {
         this.on = on;
         this.kai = on;
     }
}
