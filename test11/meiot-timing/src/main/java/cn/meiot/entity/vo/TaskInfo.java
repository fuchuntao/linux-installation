package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfo implements Serializable{
    private static final long serialVersionUID = -8054692082716173379L;
    private int id = 0;

    /**任务名称*/
    private String jobName;

    /**任务分组*/
    private String jobGroup;

    /**任务描述*/
    private String jobDescription;

    /**任务状态*/
    private String jobStatus;

    /**任务表达式*/
    private String cronExpression;

    /**
     * 是否启动执行   1:执行   0：不执行
     */
    private Integer isSatrtExecute;

    private String createTime;


}
