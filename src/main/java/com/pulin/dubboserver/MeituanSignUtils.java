package com.pulin.dubboserver;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by pulin on 2016/5/26.
 */
public class MeituanSignUtils {
	
	public static String  getRequestUrl(Map<String,String> param,String url,String secret){
		String paramStr = sortParam(param);
		//拼接url请求字符串
		StringBuilder sb = new StringBuilder();
		sb.append(url).append("?").append(paramStr);
		String urlParam = sb.toString();
		
		sb.append(secret);//加入secret，计算签名
		String sign = getMD5Str(sb.toString());
		
		//加入签名
		StringBuilder signSb = new StringBuilder();
		signSb.append(urlParam).append("&sig=").append(sign);
		//返回加了签名的url请求地址
		return signSb.toString();
	}
	
	
	/**
	 * 按照字母ASCII升序排序后，拼接的参数
	 * @param param
	 * @return
	 */
	public static String sortParam(Map<String,String> param){
		if(param == null){
			throw new NullPointerException("param is  null");
		}
		TreeMap<String,Object> treeMap = new TreeMap<String,Object>();
		Iterator<String> iter = param.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			treeMap.put(key, param.get(key));
		}
		
		StringBuilder sb = new StringBuilder();
		Iterator<String> treeMapIter = treeMap.keySet().iterator();
		while(treeMapIter.hasNext()){
			String key = treeMapIter.next();
			sb.append(key).append("=").append(treeMap.get(key)).append("&");
		}
		
		String paramStr = sb.toString();
		if(paramStr.endsWith("&")){
			paramStr = paramStr.substring(0, paramStr.lastIndexOf("&"));
		}
		
		return paramStr;
		
	}


    /**
     * 获取加密后的字符串
     * @param
     * @return
     */
    public static String getMD5Str(String pw) {
        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = pw.getBytes();
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray =new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

	
	//通过给定的url, params, consumer_secret计算出sig
		public static String genSig(String pathUrl, Map<String, String> temp, String consumerSecret)
				throws NoSuchAlgorithmException, UnsupportedEncodingException {
			String str = sortParam(temp);
			
			str =new StringBuffer(pathUrl).append("?").append(str).append(consumerSecret).toString();
			return getMD5Str(str);
		}

}
