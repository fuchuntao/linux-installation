package cn.meiot.utils;


import java.util.Random;

/**
 * @Title: CacheKey.java
 * @Package cn.spacecg.web.utils
 * @Description: 生成緩存中的key
 * @author: 冯绍宇
 * @date: 2019年2月18日 下午12:00:31
 * @version V1.0
 * @Copyright: 2019 www.spacecg.cn
 */
public class CacheKey {


	/**
	 * 
	 * @Title: getCacheKey   
	 * @Description: 生成缓存key   
	 * @param: @param length
	 * @param: @return      
	 * @return: String      
	 * @throws
	 */
	public static String getCacheKey(int length) {
		// 产生随机数
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		// 循环length次
		for (int i = 0; i < length; i++) {
			// 产生0-2个随机数，既与a-z，A-Z，0-9三种可能
			int number = random.nextInt(3);
			long result = 0;
			switch (number) {
			// 如果number产生的是数字0；
			case 0:
				// 产生A-Z的ASCII码
				result = Math.round(Math.random() * 25 + 65);
				// 将ASCII码转换成字符
				sb.append(String.valueOf((char) result));
				break;
			case 1:
				// 产生a-z的ASCII码
				result = Math.round(Math.random() * 25 + 97);
				sb.append(String.valueOf((char) result));
				break;
			case 2:
				// 产生0-9的数字
				sb.append(String.valueOf(new Random().nextInt(10)));
				break;
			}
		}
		return sb.toString();
	}


	/**
	 * 生成redis key
	 * @return
	 */
	public String getRedisKey(){
		return  getCacheKey(20);
	}
}
