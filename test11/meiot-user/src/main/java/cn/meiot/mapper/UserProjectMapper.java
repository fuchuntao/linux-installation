package cn.meiot.mapper;

import cn.meiot.entity.UserProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Mapper
public interface UserProjectMapper extends BaseMapper<UserProject> {

    /**
     * 通过用户id查询项目id
     * @param id
     * @return
     */
    @Select(" select project_id from user_project where user_id = #{id} ")
    List<Integer> getProjectIdByUserId(Long id);
}
