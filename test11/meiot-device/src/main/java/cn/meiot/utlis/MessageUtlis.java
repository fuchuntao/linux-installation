package cn.meiot.utlis;
/**
 * @author lingzhiying
 * @title: MessageUtlis.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月20日
 */
public class MessageUtlis {
	/**
	 * 申请绑定
	 */
	public String sendSQBD(String serialNumber) {
		StringBuffer sb = new StringBuffer();
		sb.append("请求对设备(").append(serialNumber).append(")进行绑定");
		return sb.toString();
	}
}
