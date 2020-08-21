package cn.meiot.entity.vo;

import cn.meiot.config.TableConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMeterVo {

    private String serialNumber;

    private Integer switchIndex;

    private Long switchSn;

    private Integer year;

    private Integer month;

    private Integer day;

    private Long userId;

    private Integer oldMonth;

    private Integer oldYear;

    private Integer projectId;
    private String platform;

    private String tableName;


    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
        if(projectId != null) {
            if(!this.tableName.equals(TableConfig.METER)){
                this.platform = TableConfig.PC;
                return;
            }
            if(projectId > 0) {
                this.platform = TableConfig.PC;
                return;
            }
            this.platform = TableConfig.APP;
            return;
        }
    }
}
