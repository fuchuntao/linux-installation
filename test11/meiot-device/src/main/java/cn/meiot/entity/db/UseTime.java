package cn.meiot.entity.db;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@NoArgsConstructor
public class UseTime {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * user_id
     */
    private Long userId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     *
     * @param 项目id
     */
    private Integer projectId;

    public UseTime(Long userId,Integer projectId){
        this.userId = userId;
        if(projectId == null){
            this.projectId = 0;
        }
        this.projectId = projectId;
    }
}
