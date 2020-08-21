package cn.meiot.service;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

public interface UserService {

	public Long getMainUserId(Long userId) ;

    /**
     * 获取设备规则
     * @return
     */
    Map<String, Map<String, String>> getRule();
}
