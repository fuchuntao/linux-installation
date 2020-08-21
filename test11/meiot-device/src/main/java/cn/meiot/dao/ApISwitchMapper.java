package cn.meiot.dao;

import java.util.List;
import java.util.Map;

public interface ApISwitchMapper {

    List<Map> listSwitchSn(String serialNumber);
}
