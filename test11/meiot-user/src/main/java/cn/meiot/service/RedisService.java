package cn.meiot.service;


public interface RedisService {
    /**
     * 保存value
     * @param key
     * @param value
     */
    void saveValue(String key, Object value);

    /**
     * 通过key查询value
     * @param randomData
     * @return
     */
    String getValueByKey(String randomData);

}
