package cn.meiot.enums;

public enum  FileTypeEnum {
    /**
     * 固件升级
     */
    FIRMWARE_UPGRADE(1),

    /**
     * 图片
     */
    IMG(2),


    /**
     * apk
     */
    APK(3),

    /**
     * 缩略图
     */
    THUM(4);





    private  Integer index;



    private FileTypeEnum(Integer value) {    //    必须是private的，否则编译错误
        this.index = value;
    }

    public Integer value() {
        return this.index;
    }


}
