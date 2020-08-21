package cn.meiot.service.impl;

import java.util.List;

import cn.meiot.dao.SwitchNameMapper;
import cn.meiot.entity.db.SwitchName;
import cn.meiot.entity.dto.pc.PcEquipmentUserCond;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.meiot.dao.SwitchTypeMapper;
import cn.meiot.entity.db.SwitchType;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SwitchTypeVo;
import cn.meiot.service.SwitchTypeService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SwitchTypeServiceImpl implements SwitchTypeService {

	@Autowired
	private SwitchTypeMapper switchTypeMapper;
	@Autowired
	private SwitchNameMapper  switchNameMapper;
	
	@Override
	@Transactional
	public Result insert(SwitchType switchType) {
		updateDefault(switchType);
		switchTypeMapper.insert(switchType);
		return Result.getDefaultTrue();
	}

	@Transactional
	public void updateDefault(SwitchType switchType){
		if(switchType.getIsDefault() == null )
			return;
		if(!switchType.getIsDefault().equals(1))
			return ;
		Long id = queryDefaultId(switchType.getProjectId());
		if(id == null)
			return;
		SwitchType switchType2 = new SwitchType();
		switchType2.setId(id);
		//把默认的设置为非默认
		switchType2.setIsDefault(0);
		switchTypeMapper.updateByPrimaryKeySelective(switchType2);
	}

	@Override
	@Transactional
	public Result delete(List<Long> ids) {
		ids.forEach(id->{
			SwitchName sn = new SwitchName();
			sn.setSwitchType(id.longValue());
			int i = switchNameMapper.selectCount(sn);
			if(i>0){
				throw new MyServiceException(ResultCodeEnum.ALREADY_SWITCH_BIND.getCode(),ResultCodeEnum.ALREADY_SWITCH_BIND.getMsg());
			}
			switchTypeMapper.deleteByPrimaryKey(id);
		});
		return Result.getDefaultTrue();
	}

	@Override
	@Transactional
	public Result update(SwitchType switchType) {
		updateDefault(switchType);
		switchTypeMapper.updateByPrimaryKeySelective(switchType);
		return Result.getDefaultTrue();
	}

	@Override
	public Result query(PcEquipmentUserCond cond) {
		PageHelper.startPage(cond.getPage(), cond.getPageSize());
		List<SwitchType> select = switchTypeMapper.selectPage(cond);
		PageInfo pageinfo = new PageInfo<>(select);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(pageinfo);
		return defaultTrue;
	}

	@Override
	public List<SwitchTypeVo> querySwitch(Integer projectId) {
		List<SwitchTypeVo> listData = switchTypeMapper.querySwitchByProjectId(projectId);
		listData.forEach(sw->{
			if(sw.getId() != null && sw.getId().equals(0L)) {
				sw.setName("其他");
			}
		} );
		return listData;
	}

	@Override
	public boolean queryNameCount(String name,Integer projectId,Long id) {
		Integer count = switchTypeMapper.queryNameCount(name,projectId,id);
		if(count == null || count == 0){
			return false;
		}
		return true;
	}

	@Override
	public Long queryDefaultId(Integer projectId) {
		return switchTypeMapper.queryDefaultId(projectId);
	}
}
