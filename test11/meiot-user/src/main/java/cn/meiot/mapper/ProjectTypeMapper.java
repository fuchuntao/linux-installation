package cn.meiot.mapper;

import cn.meiot.entity.ProjectType;
import cn.meiot.entity.bo.ProjectTypeBo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-19
 */
@Mapper
public interface ProjectTypeMapper extends BaseMapper<ProjectType> {

    /**
     * 获取项目类型列表
     * @return
     */
    @Select("select id,name from project_type")
    @Results(value = {
            @Result(id = true, property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR)

    })
    List<ProjectTypeBo> getList();
}
