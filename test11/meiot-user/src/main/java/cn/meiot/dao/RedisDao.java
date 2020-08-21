package cn.meiot.dao;

public interface RedisDao {
    /**
     * 保存值
     * @param key
     * @param value
     */
    void saveValue(String key, Object value);

    /**
     * 通过key查询value
     * @param key
     * @return
     */
    String getValueByKey(String key);

}
