package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/14 14:49
 * @Copyright: www.spacecg.cn
 */
@Data
public class UploadVo  implements Serializable {

    /**
     * 当前读到的字节数
     */
    private Integer len;


    /**
     * crc校验码
     */
    private String crc;

    /**
     * 文件大小
     */
    private Long file;

    /**
     * 当前读取的长度
     */
    private Integer size;


    /**
     * 跳过字节数
     */
    private Long skip;

    public UploadVo(Integer len, String crc, Long file, Integer size, Long skip) {
        this.len = len;
        this.crc = crc;
        this.file = file;
        this.size = size;
        this.skip = skip;
    }
}
