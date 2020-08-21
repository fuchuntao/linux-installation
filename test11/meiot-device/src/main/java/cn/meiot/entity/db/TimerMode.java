package cn.meiot.entity.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author lingzhiying
 * @title: TimerMode.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@Data
public class TimerMode {
	private String serialNumber;
	@Id
	@GeneratedValue(generator = "JDBC")
		private  Long id;
     
	 //名称
      private  String name;
     
	
	//类型:1=时间段起止模式,2=星期重复模式,3=时间点开 4时间点关
      private  Integer type;
     
	 //定时信息json集合:date=日期1,time=时间1,status=状态1,date2=日期2,time2=时间2,week=周循环",status2=第二个开关状态
      private  String times;
     
	 //描述
      private  String content;
     
	 //创建时间
      @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
      private  Date createTime;
     
	 //更新时间
      @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
      private  Date updateTime;
      //开关状态
      private Boolean isSwitch;

      @Transient
      private Integer switchCount;

    public Integer getSwitchCount() {
        if(CollectionUtils.isEmpty(powerAppUserList)){
            return 0;
        }
        return powerAppUserList.size();
    }

    /**
     * 开关信息
     */
    private Set<PowerAppUser> powerAppUserList;

    /**
     * 失效开关信息
     */
    private Set<PowerAppUser> invalidPowerAppUserList = new HashSet<>();
}
