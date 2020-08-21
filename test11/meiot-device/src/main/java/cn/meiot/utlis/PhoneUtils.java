package cn.meiot.utlis;

public class PhoneUtils {
	private PhoneUtils() {}
	
	public static String getPhone(String phone) {
		String phoneNumber = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
		return phoneNumber;
	}

	public static String getPhone(Object object) {
		String phone = object+"";
		String phoneNumber = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
		return phoneNumber;
	}
}
