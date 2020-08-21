package cn.meiot.entity.db;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Objects;

@Data
public class PowerAppUser {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * switch_sn
     */
    private String switchSn;

    /**
     * power_id
     */
    private Integer powerId;

    /**
     * 状态 0失效 1正常
     */
    private Integer status;
    /**
     * index
     */
    @Transient
    private Integer index;

    @Transient
    private String name = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PowerAppUser that = (PowerAppUser) o;
        return Objects.equals(switchSn, that.switchSn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(switchSn);
    }
}
