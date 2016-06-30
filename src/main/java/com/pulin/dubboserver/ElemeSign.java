package com.pulin.dubboserver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ElemeSign {
	private static String concatParams(Map<String, String> params) throws UnsupportedEncodingException {
		Object[] key_arr = params.keySet().toArray();
		Arrays.sort(key_arr);
		String str = "";

		for (Object key : key_arr) {
			String val = params.get(key);
			key = URLEncoder.encode(key.toString(), "UTF-8");
			val = URLEncoder.encode(val, "UTF-8");
			str += "&" + key + "=" + val;
		}

		return str.replaceFirst("&", "");
	}
	
	//将byte类型的结果转化为HexString
		private static String byte2hex(byte[] b) {
			StringBuffer buf = new StringBuffer();
			int i;

			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			return buf.toString();
		}
	
	//通过给定的url, params, consumer_secret计算出sig
	public static String genSig(String pathUrl, Map<String, String> params, String consumerSecret)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String str = concatParams(params);
		str = pathUrl + "?" + str + consumerSecret;
		System.out.println(str);
		MessageDigest md = MessageDigest.getInstance("SHA1");
		return byte2hex(md.digest(byte2hex(str.getBytes("UTF-8")).getBytes()));
	}
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		// http://v2.openapi.ele.me/restaurant/215224/product_profile/?consumer_key=8665552594&sig=915a3cd4c95f9a3d0813e46844d600ad34572137&timestamp=1466560476
		
		
		String url="http://v2.openapi.ele.me/restaurant/215224/product_profile/";
		String secret="c1119eed1a89c0c52b5ab2662a4de577b20322bd";
		Map<String,String> map = new HashMap<String,String>();
		map.put("consumer_key", "8665552594");
		map.put("timestamp", "1466560476");
		//map.put("restaurant_id", "215224");
		System.out.println("915a3cd4c95f9a3d0813e46844d600ad34572137");
		System.out.println(genSig(url,map,secret));
	}
}
