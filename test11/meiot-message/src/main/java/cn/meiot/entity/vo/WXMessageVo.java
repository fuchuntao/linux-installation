package cn.meiot.entity.vo;

import java.util.Map;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/1/14 14:22
 * @Copyright: www.spacecg.cn
 */
public class WXMessageVo {
    //openId
    private String touser;

    //模板Id
    private String template_id;

    // 模板跳转链接（海外帐号没有跳转能力）
    private String url;

    //
    private Miniprogram miniprogram;

    private WXData data;

    public WXMessageVo(String touser, String template_id, WXData data) {
        this.touser = touser;
        this.template_id = template_id;
        this.data = data;
    }

    public WXData getData() {
        return data;
    }

    public void setData(WXData data) {
        this.data = data;
    }

    public WXMessageVo() {
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Miniprogram getMiniprogram() {
        return miniprogram;
    }

    public void setMiniprogram(Miniprogram miniprogram) {
        this.miniprogram = miniprogram;
    }


    public class Miniprogram {
        private String appid;
        private String pagepath;

        public Miniprogram(String appid, String pagepath) {
            this.appid = appid;
            this.pagepath = pagepath;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPagepath() {
            return pagepath;
        }

        public void setPagepath(String pagepath) {
            this.pagepath = pagepath;
        }
    }

    public class WXData {
        private Entry first;
        private Entry keyword1;
        private Entry keyword2;
        private Entry keyword3;
        private Entry keyword4;
        private Entry remark;

        public WXData(Entry first, Entry keyword1, Entry keyword2, Entry keyword3, Entry keyword4, Entry remark) {
            this.first = first;
            this.keyword1 = keyword1;
            this.keyword2 = keyword2;
            this.keyword3 = keyword3;
            this.keyword4 = keyword4;
            this.remark = remark;
        }

        public Entry getFirst() {
            return first;
        }

        public void setFirst(Entry first) {
            this.first = first;
        }

        public Entry getKeyword1() {
            return keyword1;
        }

        public void setKeyword1(Entry keyword1) {
            this.keyword1 = keyword1;
        }

        public Entry getKeyword2() {
            return keyword2;
        }

        public void setKeyword2(Entry keyword2) {
            this.keyword2 = keyword2;
        }

        public Entry getKeyword3() {
            return keyword3;
        }

        public void setKeyword3(Entry keyword3) {
            this.keyword3 = keyword3;
        }

        public Entry getKeyword4() {
            return keyword4;
        }

        public void setKeyword4(Entry keyword4) {
            this.keyword4 = keyword4;
        }

        public Entry getRemark() {
            return remark;
        }

        public void setRemark(Entry remark) {
            this.remark = remark;
        }
    }

    public class Entry {
        private String value;
        private String color;

        public Entry(String value, String color) {
            this.value = value;
            this.color = color;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static String get(Integer integer) {
        switch (integer) {
            case 1:
                return "mA";
            case 2:
                return "mA";
            case 3:
                    return "℃";
            case 4:
                return "W";
            case 5:
                return "A";
            case 6:
                return "V";
            case 7:
                return "V";
            case 8:
                return "";
            case 9:
                return "";
            case 10:
                return "mA";
            case 11:
                return "mA";
            case 12:
                return "℃";
            case 13:
                return "W";
            case 14:
                return "A";
            case 15:
                return "V";
            case 16:
                return "V";
        }
        return null;
    }

}
