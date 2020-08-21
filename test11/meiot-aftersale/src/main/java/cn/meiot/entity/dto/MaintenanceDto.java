package cn.meiot.entity.dto;

import cn.meiot.entity.Maintenance;
import cn.meiot.utils.ImgUrl;
import com.alibaba.fastjson.JSONObject;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@Data
public class MaintenanceDto implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 申报的账号
     */
    private String account;

    /**
     * 申报描述
     */
    private String reason;

    /**
     * 状态  1：保修  2：受理  3：维修
     */
    private Integer mStatus;

    /**
     * 报修时间
     */
    private String reportTime;

    /**
     * 受理时间
     */
    private String acceptTime;

    /**
     * 维修时间
     */
    private String maintainTime;

    /**
     * 附件图片路径
     */
//    private String imgPath;
    private List<Map> imgPath;

    /**
     * 故障类型ID
     */
    private Integer mType;

    /**
     * 故障类型名字
     */
    private String typeName;

    /**
     * 用户id
     */
    private Long userId;

    public MaintenanceDto() {
    }


    public MaintenanceDto(Maintenance maintenance, ImgUrl imgUrl) {
        List<String> imgPath = JSONObject.parseObject(maintenance.getImgPath(), List.class);
        this.imgPath = new ArrayList<>();
        if (null != imgPath && !imgPath.isEmpty()) {
            for (String img : imgPath) {
                Map<String,Object> map=new HashMap();
                map.put("resource",imgUrl.getImgUrl() + img);
                map.put("thumbnail",imgUrl.getImgUrl("")+img);
                this.imgPath.add(map);
            }
        }
        this.userId = maintenance.getUserId();
        this.typeName = maintenance.getTypeName();
        this.mType = maintenance.getMType();
        this.maintainTime = maintenance.getMaintainTime();
        this.acceptTime = maintenance.getAcceptTime();
        this.reportTime = maintenance.getReportTime();
        this.mStatus = maintenance.getMStatus();
        this.reason = maintenance.getReason();
        this.account = maintenance.getAccount();
        this.serialNumber = maintenance.getSerialNumber();
        this.id = maintenance.getId();
    }

    public synchronized static MaintenanceDto getMaintenanceDto(Maintenance maintenance, ImgUrl imgUrl) {
        if (null == maintenance) {
            return null;
        }
        MaintenanceDto maintenanceDto = new MaintenanceDto();
        List<String> imgPath = JSONObject.parseObject(maintenance.getImgPath(), List.class);
        List<Map> imgPathList = new ArrayList<>();
        if (null != imgPath && !imgPath.isEmpty()) {
            for (String img : imgPath) {
                Map<String,Object> map=new HashMap();
                map.put("resource",imgUrl.getImgUrl() + img);
                map.put("thumbnail",imgUrl.getImgUrl("")+img);
                imgPathList.add(map);
            }
        }
        maintenanceDto.setImgPath(imgPathList);
        maintenanceDto.setUserId(maintenance.getUserId());
        maintenanceDto.setTypeName(maintenance.getTypeName());
        maintenanceDto.setMType(maintenance.getMType());
        maintenanceDto.setMaintainTime(maintenance.getMaintainTime());
        maintenanceDto.setAcceptTime(maintenance.getAcceptTime());
        maintenanceDto.setReportTime(maintenance.getReportTime());
        maintenanceDto.setMStatus(maintenance.getMStatus());
        maintenanceDto.setReason(maintenance.getReason());
        maintenanceDto.setAccount(maintenance.getAccount());
        maintenanceDto.setSerialNumber(maintenance.getSerialNumber());
        maintenanceDto.setId(maintenance.getId());
        return maintenanceDto;
    }


}
