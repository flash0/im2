package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.File; 

import common.Message;

import sun.applet.Main;

public class ShaUtils {
	/**
	 * sha-1加密
	 * @param message 输入的数据
	 * @return 加密后的数据
	 */

	    private final static String KEY_SHA1 = "SHA-1";  
	    /** 
	     * 全局数组 
	     */  
	    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",  
	            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };  
	  
	    /** 
	     * 构造函数 
	     */  
	    
	  
	    /** 
	     * SHA 加密 
	     * @param data 需要加密的字节数组 
	     * @return 加密之后的字节数组 
	     * @throws Exception 
	     */  
	    public static byte[] encryptSHA(byte[] data) throws Exception {  
	        // 创建具有指定算法名称的信息摘要  

	    	
	        MessageDigest sha = MessageDigest.getInstance(KEY_SHA1);  
	        // 使用指定的字节数组对摘要进行最后更新  
	        sha.update(data);  
	        // 完成摘要计算并返回  
	        return sha.digest();  
	    }  
	  
	    /** 
	     * SHA 加密 
	     * @param data 需要加密的字符串 
	     * @return 加密之后的字符串 
	     * @throws Exception 
	     */  
	    public static String encryptSHA(String data) throws Exception {  
	        // 验证传入的字符串  
	        if (data.equals("")|| data ==null) {  
	            return "";  
	        }  
	        // 创建具有指定算法名称的信息摘要  
	        MessageDigest sha = MessageDigest.getInstance(KEY_SHA1);  
	        // 使用指定的字节数组对摘要进行最后更新  
	        sha.update(data.getBytes());  
	        // 完成摘要计算  
	        byte[] bytes = sha.digest();  
	        // 将得到的字节数组变成字符串返回  
	        return byteArrayToHexString(bytes);  
	    }  
	  
	    /** 
	     * 将一个字节转化成十六进制形式的字符串 
	     * @param b 字节数组 
	     * @return 字符串 
	     */  
	    private static String byteToHexString(byte b) {  
	        int ret = b;  

	        if (ret < 0) {  
	            ret += 256;  
	        }  
	        int m = ret / 16;  
	        int n = ret % 16;  
	        return hexDigits[m] + hexDigits[n];  
	    }  
	  
	    /** 
	     * 转换字节数组为十六进制字符串 
	     * @param bytes 字节数组 
	     * @return 十六进制字符串 
	     */  
	    private static String byteArrayToHexString(byte[] bytes) {  
	        StringBuffer sb = new StringBuffer();  
	        for (int i = 0; i < bytes.length; i++) {  
	            sb.append(byteToHexString(bytes[i]));  
	        }  
	        return sb.toString();  
	    }  
	  
	    public static String getFileSha512(String path) throws NoSuchAlgorithmException, IOException {

			File file = new File(path);

			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			FileInputStream inputStream = new FileInputStream(file);
			FileChannel channel = inputStream.getChannel();
			MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			digest.update(byteBuffer);

			return byte2Hex(digest.digest());

		}

		public static String byte2Hex(byte[] bytes) {

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String shaHex = Integer.toHexString(bytes[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		}
	    /** 
	     * 测试方法 
	     * @param args 
	     */  
//	    public static void main(String[] args) throws Exception {  
//	        String key = "123";  
//	        System.out.println(encryptSHA(key));
//	        System.out.println(encryptSHA(key).length());
//	    }  
}
