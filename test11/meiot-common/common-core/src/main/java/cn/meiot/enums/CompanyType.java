package cn.meiot.enums;

public enum CompanyType {
    MQTT(0),
    HUAWEI(1);

    private Integer company;

    private CompanyType(Integer company){
        this.company = company;
    }

    public Integer value() {
        return this.company;
    }
}
