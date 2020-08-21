package cn.meiot.config;

import cn.meiot.utils.ConstantsUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object val = getFieldValByName("updateTime", metaObject);
        if(!metaObject.hasGetter("et.updateTime")){
            return ;
        }
        if(val == null){
            log.info("需要填充");
            this.setFieldValByName("updateTime", ConstantsUtil.DF.format(new Date()),metaObject);
        }
    }
}
