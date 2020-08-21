package cn.meiot.service;

public interface UseTimeService {

    /**
     * 添加设备
     * @param userId
     */
    void insert(Long userId,Integer projectId);

    /**
     * 子用户删除设备。。APP和企业使用
     * @param userId
     */
    void deleteSerial(Long userId,Integer projectId);

    /**
     * 设备主账户删除设备
     * @param userId
     */
    void deleteMasterSerial(String serialNumber);
}
