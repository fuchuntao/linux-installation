package cn.meiot.service.impl.pc;

import cn.meiot.entity.ProjectType;
import cn.meiot.entity.ProjectTypePermission;
import cn.meiot.entity.SysPermission;
import cn.meiot.entity.bo.ProjectTypeBo;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.AccountType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.mapper.ProjectTypeMapper;
import cn.meiot.mapper.ProjectTypePermissionMapper;
import cn.meiot.mapper.SysPermissionMapper;
import cn.meiot.service.pc.IProjectTypePermissionService;
import cn.meiot.service.pc.IProjectTypeService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.MenuTreeUtil;
import cn.meiot.utils.QueueConstantUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-19
 */
@Service
@Slf4j
public class ProjectTypeServiceImpl extends ServiceImpl<ProjectTypeMapper, ProjectType> implements IProjectTypeService {

    @Autowired
    private ProjectTypeMapper projectTypeMapper;

    @Autowired
    private ProjectTypePermissionMapper projectTypePermissionMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private IProjectTypePermissionService projectTypePermissionService;

    @Autowired
    private MenuTreeUtil menuTreeUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @Override
    public List<ProjectTypeBo> getList() {
        return projectTypeMapper.getList();
    }

    @Override
    public Result typePermissionlist(Integer id) {
        Result result = Result.getDefaultTrue();
        List<SysPermission> list = projectTypePermissionMapper.typePermissionlist(id);
        list = menuTreeUtil.menuList(list);
        result.setData(list);
        return result;
    }

    @Transactional
    public  Result setper(PermissionVo permissionVo){
        //校验项目类型是否存在
        Integer count = projectTypeMapper.selectCount(new QueryWrapper<ProjectType>().eq("id", permissionVo.getId()));
        if( 0 == count){
            log.info("设置权限的项目类型id：{}", permissionVo.getId());
            return new Result().Faild(ErrorCodeUtil.PROJECT_TYPE_IS_NOT_EXIST);
        }
        //获取之前的旧权限
        List<Integer> oldPermission = projectTypePermissionMapper.selectIdsBypTypeId(permissionVo.getId());
        //删除之前的旧数据
        projectTypePermissionMapper.delete(new QueryWrapper<ProjectTypePermission>().eq("project_type_id", permissionVo.getId()));
        List<Integer> permission = new ArrayList<Integer>();
        if(null != permissionVo.getPerimissions() && permissionVo.getPerimissions().size() > 0){
            //校验权限id是否合法
            permission = sysPermissionMapper.selectIdsByIds(permissionVo.getPerimissions());
            if(null == permission || permission.size() == 0){
                throw  new MyServiceException(ErrorCodeUtil.NOT_LEGITIMATE_PERMISSION);
            }
            List<ProjectTypePermission> list = new ArrayList<ProjectTypePermission>();
            for(Integer id: permission){
                ProjectTypePermission projectTypePermission = new ProjectTypePermission();
                projectTypePermission.setProjectTypeId(permissionVo.getId());
                projectTypePermission.setPermissionId(id);
                list.add(projectTypePermission);
            }
            boolean flag = projectTypePermissionService.saveBatch(list);
            if(!flag){
                return new Result().Faild(ErrorCodeUtil.SAVE_ERROR);
            }

        }
        Map<String,Object> map =  new HashMap<String, Object>();
        map.put("newPermission",permission);
        map.put("oldPermission",oldPermission);
        map.put("projectType",permissionVo.getId());
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }

    @Override
    public Result setPermission(PermissionVo permissionVo) {
        Result result = setper(permissionVo);
        if(result.isResult()){
            Map<String,Object> map = (Map<String, Object>) result.getData();
            rabbitTemplate.convertAndSend(QueueConstantUtil.DELETE_SURPLUS_PROJECT_PERMISSION,map);
            return Result.getDefaultTrue();
        }
        return result;
    }

    @Override
    public Result permissionList(Integer id) {
        log.info("查询的项目类型：{}",id);
        Map<String,Object> map = new HashMap<String,Object>();
        //获取当前项目类型所选中的权限id
        if(null != id &&  !"".equals(id)){
            List<Long> ids = projectTypePermissionMapper.selectCheckButtonIds(id);
            map.put("checked",ids);
        }
        //获取企业的所有权限列表
        List<SysPermission> permissions = sysPermissionMapper.selectList(new QueryWrapper<SysPermission>().lambda()
                .eq(SysPermission::getType, AccountType.ENTERPRISE.value()));
        permissions = menuTreeUtil.menuList(permissions);
        map.put("list",permissions);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }
}
