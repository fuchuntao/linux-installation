package cn.meiot.service.impl.api;

import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.SysUserMapper;
import cn.meiot.mapper.SysUserRoleMapper;
import cn.meiot.service.api.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Result findAllUserId(Integer type) {
        Result result = Result.getDefaultTrue();
        List<String> list = sysUserMapper.findAllUserId(type);
        result.setData(list);
        return result;
    }

    @Override
    public List<Long> getSubUserIdByMainUserId(Long mainUserId) {
        return sysUserMapper.getSubUserIdByMainUserId(mainUserId);
    }

    @Override
    public List<Long> getUserIdsByRoleId(List<Integer> roleIds) {
        List<Long> userIds = sysUserRoleMapper.selectUserIdByRoles(roleIds);
        return userIds;
    }
}
