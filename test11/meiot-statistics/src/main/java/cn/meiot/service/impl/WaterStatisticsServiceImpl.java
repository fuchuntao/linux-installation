package cn.meiot.service.impl;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.WaterAddressVo;
import cn.meiot.entity.vo.WaterStatisticsDto;
import cn.meiot.entity.vo.WaterStatisticsVo;
import cn.meiot.entity.water.Record;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.WaterStatisticsMapper;
import cn.meiot.service.IWaterStatisticsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 符纯涛
 * @since 2020-02-24
 */
@Slf4j
@Service
public class WaterStatisticsServiceImpl extends ServiceImpl<WaterStatisticsMapper, WaterStatistics> implements IWaterStatisticsService {

    @Autowired
    private WaterStatisticsMapper waterStatisticsMapper;

    @Autowired
    private DeviceFeign deviceFeign;


    /**
     *
     * @Title: queryWaterMeter
     * @Description: 查询抄表列表
     * @param waterStatisticsDto
     * @return: java.util.List<cn.meiot.entity.WaterStatistics>
     */
    @Override
    public Result queryWaterMeter(WaterStatisticsDto waterStatisticsDto) {
        List<WaterStatisticsVo> waterStatisticsVoList = new ArrayList<>();
        IPage<WaterStatisticsVo> waterStatisticsVos = null;
        //导出表格
        if(waterStatisticsDto.getSign() == null) {
            Page<WaterStatisticsVo> page = new Page(waterStatisticsDto.getPage(),waterStatisticsDto.getPageSize());
            //查询抄表列表
            waterStatisticsVos = waterStatisticsMapper.queryWaterMeter(page, waterStatisticsDto);
            waterStatisticsVoList = waterStatisticsVos.getRecords();
        }else {
            waterStatisticsVoList = waterStatisticsMapper.queryWaterMeter(waterStatisticsDto);
        }
        //获取所有水表编号id
        List<String> stringList = waterStatisticsVoList.stream().map(WaterStatisticsVo::getMeterid).collect(Collectors.toList());

        Set<String> customerList = new HashSet<>();
        customerList.addAll(stringList);

        //设备号获取项目id ,名称，地址，单位
//        List<Map> maps = deviceFeign.queryWaterUser(customerList);

//        if(CollectionUtils.isEmpty(customerList)) {
//            log.info("查询抄表列表从表中查询的数据为空！！！");
//            return Result.faild("水表编号为空！！！");
//        }
        log.info("拉取数据customerList数据：{}", customerList);
        Map<String, WaterAddressVo> maps = deviceFeign.queryWaterUser(customerList);

//        if(!CollectionUtils.isEmpty(maps) && !CollectionUtils.isEmpty(waterStatisticsVoList) ) {
//            for (Map map1 : maps) {
//                for(WaterStatisticsVo waterStatisticsVo: waterStatisticsVoList) {
//                    String meteridMap = (String) map1.get("meterid");
//
//                    if(meteridMap.equals(meterid)) {
//                        //从设备号获取项目id ,名称，地址，单位获取不到水表编号meterid
//                        if(waterStatisticsVo == null){
//                            continue;
//                        }else {
//                            String address = (String) map1.get("address");
//                            waterStatisticsVo.setAddress(address);
//                        }
//                    }
//                }
//            }
//        }
        if(maps != null && !CollectionUtils.isEmpty(customerList)) {
            for (WaterStatisticsVo waterStatisticsVo : waterStatisticsVoList) {
                String meterid = waterStatisticsVo.getMeterid();
                WaterAddressVo waterAddressVo = maps.get(meterid);
                if(waterAddressVo != null) {
                    BeanUtils.copyProperties(waterAddressVo, waterStatisticsVo);
                }

            }

        }
        Result defaultTrue = Result.getDefaultTrue();
        if(waterStatisticsDto.getSign() == null) {
            defaultTrue.setData(waterStatisticsVos);
        }else {
            defaultTrue.setData(waterStatisticsVoList);
        }
        return defaultTrue;
    }


    /**
     *
     * @Title: queryWaterMeterId
     * @Description: 查询当前最大的抄表id
     * @param
     * @return: java.lang.Long
     */
    @Override
    public Long queryWaterMeterId(WaterStatistics record) {
        Long aLong = waterStatisticsMapper.queryWaterMeterId(record);
        log.info("查询当前最大的抄表id,id:{}", aLong);
        return aLong;
    }


    /**
     *
     * @Title: saveWaterMeter
     * @Description: 更新抄表统计数据
     * @param waterStatisticsList
     * @return: java.lang.Integer
     */
    @Override
    public Integer saveWaterMeter(List<WaterStatistics> waterStatisticsList) {
        return waterStatisticsMapper.saveWaterMeter(waterStatisticsList);
    }

}
