package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.PcMeterMonths;
import cn.meiot.entity.PcMeterYears;
import cn.meiot.entity.bo.MeterStatisticalBo;
import cn.meiot.entity.vo.*;
import cn.meiot.mapper.PcMeterHoursMapper;
import cn.meiot.mapper.PcMeterMonthsMapper;
import cn.meiot.mapper.PcMeterYearsMapper;
import cn.meiot.service.IPcMeterYearsService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.DataUtil;
import cn.meiot.utils.NumUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 企业平台年电量统计 服务实现类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Slf4j
@Service
public class PcMeterYearsServiceImpl extends ServiceImpl<PcMeterYearsMapper, PcMeterYears> implements IPcMeterYearsService {


    @Autowired
    private PcMeterYearsMapper pcMeterYearsMapper;

    @Autowired
    private PcMeterMonthsMapper pcMeterMonthsMapper;

    @Autowired
    private PcMeterHoursMapper pcMeterHoursMapper;

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private CommonUtil commonUtil;

    private Calendar cal = Calendar.getInstance();

    /**
     *
     * @Title: monthStatisticsPc
     * @Description: 将上个月的电流总数统计出来存放到年度表中
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    @Transactional
    public Result monthStatisticsPc(int year, int month) {
        //判断查询时是否时当前月份
        log.info("年：{}", year);
        log.info("当前月：{}", month);
        AppMeterVo appMeterVo = AppMeterVo.builder().year(year).month(month).build();




        //通过设备序列号查询列表
        List<AppMeterMonthsVo> appMeterMonthsVoList = pcMeterMonthsMapper.selectListBySerialNumberPc(appMeterVo);
        if(null == appMeterMonthsVoList){
            log.info("企业上个月没有设备用电");
            return  Result.getDefaultTrue();
        }

        ParametersDto parametersDto = ParametersDto.builder()
                .tableName(TableConfig.METER)
                .platform(TableConfig.PC)
                .type(0)
                .sYear(year)
                .sMonth(month)
                .build();
        List<PcMeterYears> list = commonUtil.appMeterMonthsVoToPcMeterYears(parametersDto, appMeterMonthsVoList);


        this.saveBatch(list);
        log.info("企业统计月度成功！");
        return Result.getDefaultTrue();
    }

    /**
     *
     * @Title: pullMonthStatisticsPc
     * @Description: 手机拉取pc端数据
     * @param year
     * @param month
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result pullMonthStatisticsPc(int year, int month) {
        //判断查询时是否时当前月份
//        cal.setTime(new Date());
//        int year = cal.get(Calendar.YEAR);//获取年份
        log.info("年：{}", year);
//        int month = cal.get(Calendar.MONTH);//获取月份
        log.info("月：{}", month);
        AppMeterVo appMeterVo = AppMeterVo.builder().year(year).month(month).build();
        //通过设备序列号查询列表
        List<AppMeterMonthsVo> appMeterMonthsVoList = pcMeterMonthsMapper.selectListBySerialNumberPc(appMeterVo);
        if(null == appMeterMonthsVoList){
            log.info("手动拉取，企业上个月没有设备用电");
            return  Result.getDefaultTrue();
        }


        ParametersDto parametersDto = ParametersDto.builder()
                .tableName(TableConfig.METER)
                .platform(TableConfig.PC)
                .type(0)
                .sYear(year)
                .sMonth(month)
                .build();
        List<PcMeterYears> list = commonUtil.appMeterMonthsVoToPcMeterYears(parametersDto, appMeterMonthsVoList);
        this.saveBatch(list);
        log.info("手动拉取，企业统计月度成功！");
        return Result.getDefaultTrue();
    }

    @Override
    public Result queryYearData(Integer projectId) {
        Result result = Result.getDefaultTrue();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);//获取年份
        Map<String,Object> map = new HashMap<String,Object>();
        //通过项目id查询主开关信息
        List<MeterStatisticalBo> yearData = new ArrayList<MeterStatisticalBo>();
        List<SerialNumberMasterVo> masterIndexByProjectId = dataUtil.getMasterIndexByProjectId(projectId, year, null);

        if(masterIndexByProjectId != null){
            //查询去年用电量
            yearData = pcMeterYearsMapper.queryMeterByMasterIndex(masterIndexByProjectId,year-1,projectId);
        }
        //将没有的月份自动补0
        yearData =  DataUtil.complementedMonth(yearData,12);
        map.put("lastYearData",yearData);
        //拆寻当年的用电量
        //获取当月用电量
        int month = cal.get(Calendar.MONTH) +1;
        masterIndexByProjectId = dataUtil.getMasterIndexByProjectId(projectId, year, month);
        BigDecimal percent = BigDecimal.ZERO;
        if(masterIndexByProjectId != null){
            yearData = pcMeterYearsMapper.queryMeterByMasterIndex(masterIndexByProjectId,year,projectId);
            log.info("当年数据：{}",yearData);
            //获取当年总电量
           // BigDecimal sumMeter = sumMeter(yearData);
            if(null != yearData){
                //计算两数的百分比
                percent = NumUtil.percent2(yearData,month);
            }

//            PcDataVo pcDataVo = PcDataVo.builder().year(year).month(month).projectId(projectId).build();
//            MeterStatisticalBo nowMonthData = pcMeterMonthsMapper.queryNowMonthData(pcDataVo,masterIndexByProjectId);
//            log.info("当月数据：{}",nowMonthData);
//            BigDecimal nowMonthMeter = BigDecimal.ZERO;
//            if(nowMonthData != null  ){
//                yearData.add(nowMonthData);
//                nowMonthMeter = nowMonthData.getValue();
//            }


        }
        yearData =  DataUtil.complementedMonth(yearData,month);
        map.put("nowYearData",yearData);
        map.put("percent",percent);
        result.setData(map);
        return result;
    }

    @Override
    public List<String> querySerialNumberByProject(Integer projectId, Integer year,Integer month) {
        return pcMeterYearsMapper.querySerialNumberByProject(projectId,year,month);
    }

    /**
     *
     * @Title: getIndexAllByProjectId
     * @Description: 根据项目获取设备的设备号
     * @param projectId
     * @return: java.util.List<java.lang.String>
     */
    @Override
    public List<String> getIndexAllByProjectId(Integer projectId, int year, int month, int day) {
        //根据项目获取设备编号
        List<String> indexAllByProjectId = pcMeterYearsMapper.getIndexAllByProjectId(projectId,year,month,day);
        return indexAllByProjectId;
    }

}
