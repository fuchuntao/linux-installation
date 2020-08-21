package cn.meiot.service;

public interface RedisService {

    void insertSerial(String serialNumber);

    void removeInsertSerial(String serialNumber);
}
