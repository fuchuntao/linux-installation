package cn.meiot.dao.sql;

import cn.meiot.entity.vo.PcDataVo;
import cn.meiot.entity.vo.SerialNumberMasterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Slf4j
public class PcMeterYearsProvider {


    public String queryMeterByMasterIndex(Map map, Integer year,Integer projectId ){
        List<SerialNumberMasterVo> list = (List<SerialNumberMasterVo>) map.get("list");
        StringBuffer sql = new StringBuffer();
        sql.append("  SELECT s_month AS name,SUM(meter) AS value from ");
        sql.append(" ( ");
        sql.append("  SELECT s_month ,meter from pc_meter_years where id = 0 ");

        MessageFormat mf = new MessageFormat("UNION  SELECT s_month,meter FROM pc_meter_years" +
                " WHERE serial_number = #'{'list[{0}].serialNumber} AND switch_sn = #'{'list[{0}].masterSn} AND s_year = #'{'year} and project_id = #'{'projectId}  ");
        if(null != list && list.size() > 0 ){
            for(int i = 0 ; i < list.size() ;++i ){
                sql.append(mf.format(new Object[]{i}));
            }
        }
        sql.append(" ) ");
        sql.append(" a GROUP BY s_month ");
        log.info("查询当年sql=============>{}",sql.toString());
        return sql.toString();
    }


    public String queryNowMonthData(PcDataVo pcDataVo, List<SerialNumberMasterVo> list){
        //SELECT  s_month  as name,SUM(meter)  AS value FROM `pc_meter_months` WHERE project_id = #{projectId} AND s_year = #{year} AND s_month = #{month} LIMIT 1
        StringBuffer sql = new StringBuffer();
        sql.append("  SELECT s_month,SUM(meter) FROM ");
        sql.append(" ( ");
        sql.append("  SELECT s_month ,meter  from pc_meter_months where id = 0  ");

        MessageFormat mf = new MessageFormat("UNION  SELECT s_month,meter FROM pc_meter_months" +
                " WHERE serial_number = #'{'list[0].serialNumber} AND switch_sn = #'{'list[0].masterSn} AND s_year = #'{'pcDataVo.year} and project_id = #'{'pcDataVo.projectId} AND s_month = #'{'pcDataVo.month}  ");
        if(null != list && list.size() > 0 ){
            for(int i = 0 ; i < list.size() ;i++){
                sql.append(mf.format(new Object[]{i+""}));
            }
        }
        sql.append(" ) ");
        sql.append(" a  ");
        log.info("查询当月sql=============>{}",sql.toString());
        return sql.toString();
    }
}
