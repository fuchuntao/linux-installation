package cn.meiot.entity;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFeignVo {
    /**
     * 应用主键
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用key
     */
    private String appKey;
}
