package cn.meiot.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**  
 * @Title:  SHAencrypt.java   
 * @Package com.space.utils   
 * @Description:   
 * @author: 冯绍宇
 * @date:   2018年8月12日 下午7:41:53   
 * @version V1.0 
 */
public class Md5 {
	private static final String EGGTOY = "meiot";

	/**
	 *
	 * @param password 密码
	 * @param key key
	 * @return
	 */
	public static String md5(String password,String key)
	  {
	    try
	    {
	      String pwd = password + EGGTOY + key;

	      MessageDigest digest = MessageDigest.getInstance("md5");
	      byte[] result = digest.digest(pwd.getBytes());
	      StringBuffer buffer = new StringBuffer();

	      for (byte b : result)
	      {
	        int number = b & 0xFF;
	        String str = Integer.toHexString(number);
	        if (str.length() == 1) {
	          buffer.append("0");
	        }
	        buffer.append(str);
	      }

	      return buffer.toString().trim();
	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }return "";
	  }

	  public static String cmd5(String s) {
	    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	    try
	    {
	      byte[] btInput = s.getBytes();

	      MessageDigest mdInst = MessageDigest.getInstance("MD5");

	      mdInst.update(btInput);

	      byte[] md = mdInst.digest();

	      int j = md.length;
	      char[] str = new char[j * 2];
	      int k = 0;
	      for (int i = 0; i < j; ++i) {
	        byte byte0 = md[i];
	        str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
	        str[(k++)] = hexDigits[(byte0 & 0xF)];
	      }
	      return new String(str);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }return null;
	  }


	  public static String md5En(String content){
		  try {
			  // 加密对象，指定加密方式
			  MessageDigest md5 = MessageDigest.getInstance("md5");
			  // 准备要加密的数据
			  byte[] b = content.getBytes();
			  // 加密
			  byte[] digest = md5.digest(b);
			  // 十六进制的字符
			  char[] chars = new char[] { '0', '1', '2', '3', '4', '5',
					  '6', '7' , '8', '9', 'A', 'B', 'C', 'D', 'E','F' };
			  StringBuffer sb = new StringBuffer();
			  // 处理成十六进制的字符串(通常)
			  for (byte bb : digest) {
				  sb.append(chars[(bb >> 4) & 15]);
				  sb.append(chars[bb & 15]);
			  }
			  // 打印加密后的字符串
			  return sb.toString();
		  } catch (NoSuchAlgorithmException e) {
			  e.printStackTrace();
			  return null;
		  }
	  }

	/**
	 * md5加密  （32位小写）
	 * @param value
	 * @return
	 */
	public static String md5To32x(String value){
		String result = null;
		MessageDigest md5 = null;
		try{
			md5 = MessageDigest.getInstance("MD5");
			md5.update((value).getBytes("UTF-8"));
		}catch (NoSuchAlgorithmException error){
			error.printStackTrace();
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		byte b[] = md5.digest();
		int i;
		StringBuffer buf = new StringBuffer("");

		for(int offset=0; offset<b.length; offset++){
			i = b[offset];
			if(i<0){
				i+=256;
			}
			if(i<16){
				buf.append("0");
			}
			buf.append(Integer.toHexString(i));
		}

		result = buf.toString();
		return result;
	}


	  public static void main(String[] args) {
		System.out.println(md5("18565630359","123456"));
	}
}
