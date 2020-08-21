package cn.meiot.service.impl;

import cn.meiot.entity.Maintenance;
import cn.meiot.entity.TroubleTicket;
import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.entity.vo.*;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.UserFeign;
import cn.meiot.mapper.TroubleTicketMapper;
import cn.meiot.service.ITroubleTicketService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.QueueConstantUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2020-02-17
 */
@Service
@Slf4j
public class TroubleTicketServiceImpl extends ServiceImpl<TroubleTicketMapper, TroubleTicket> implements ITroubleTicketService {
    @Autowired
    private  TroubleTicketMapper troubleTicketMapper;

    @Autowired
    private FileConfigVo fileConfigVo;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Result editStatus(List<StatusVo> statusVoList) {

        for (StatusVo statusVo:statusVoList) {
            if (null == statusVoList || statusVoList.size()==0 || null==statusVo || statusVo.isEmnty()) {
                throw new MyServiceException("数据为空","数据为空");
            }
            if (statusVo.getStatus() > 2) {
                throw  new MyServiceException("状态值错误 最大为2 数据错误","状态值错误 最大为3 数据错误  1受理 2完成");
            }
            TroubleTicket troubleTicket = troubleTicketMapper.selectById(statusVo.getId());
            if (null == troubleTicket) {
                throw new MyServiceException("ID没有找到对应数据","ID没有找到对应数据");
            }
            if (statusVo.getStatus() <= troubleTicket.getType()) {
                throw  new MyServiceException("状态不能降级","状态不能降级");
            }
            if (statusVo.getStatus()-troubleTicket.getType()!=1){
                throw  new MyServiceException("状态不能跳级","状态不能跳级");
            }
        }
        troubleTicketMapper.updateStatusByList(statusVoList);
        //修改完成后同步故障消息库
        for (StatusVo statusVo : statusVoList) {
            TroubleTicket troubleTicket = troubleTicketMapper.selectById(statusVo.getId());
            Long alarmId = troubleTicket.getAlarmId();
            Integer type = troubleTicket.getType();
            SendStatusVo snedVo=new SendStatusVo(alarmId,type+1);
            //1企业 0个人
            Integer isApp = troubleTicket.getIsApp();
            if (isApp.equals(1)) {
                rabbitTemplate.convertAndSend(QueueConstantUtil.QY_SYNCHRONIZE_STATIC, JSONObject.toJSONString(snedVo));
                log.info("\r\n同步故障消息队列以发送 \r\n队列名称：{},发送内容：{}",QueueConstantUtil.QY_SYNCHRONIZE_STATIC,snedVo);
            } else if (isApp.equals(0)) {
                rabbitTemplate.convertAndSend(QueueConstantUtil.APP_SYNCHRONIZE_STATIC, JSONObject.toJSONString(snedVo));
                log.info("\r\n同步故障消息队列以发送 \r\n队列名称：{},发送内容：{}",QueueConstantUtil.APP_SYNCHRONIZE_STATIC,snedVo);
            }
        }
        return Result.getDefaultTrue().builder().msg("操作成功").code("0").build();
    }

    @Override
    public PageVo getAfatersaleList(Integer currentPage, Integer pageSize, Long userId) {
      List<AftersaleVo> list=  troubleTicketMapper.selectByUserId(currentPage,pageSize,userId);
        setIcon(list);
      Integer total=troubleTicketMapper.selectByUserIdTotal(userId);
        return new PageVo<>(list,total);
    }


    @Override
    public PageVo getList(Integer currentPage, Integer pageSize, Long userId, Integer projectId) {
        List<AftersaleVo> list=  troubleTicketMapper.selectByUserIdAndProjectId(currentPage,pageSize,userId,projectId);
        setIcon(list);
        Integer total=troubleTicketMapper.selectByUserIdAndProjectIdTotal(userId,projectId);
        return new PageVo<>(list,total);
    }

    private void setIcon(List<AftersaleVo> aftersaleVos) {
        for (AftersaleVo aftersaleVo : aftersaleVos) {
            String iconKey= ConstantsUtil.ConfigItem.ALARM_DELAUT_IMG+aftersaleVo.getAlarmType();
            log.info("iconKey===>:{}",iconKey);
            String icon = fileConfigVo.getMPath(userFeign.getConfigValueByKey(iconKey));
            aftersaleVo.setIcon(icon);
        }
    }
}
