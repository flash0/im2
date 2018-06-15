package utils;

import java.io.IOException;

import common.Message;
import common.MessageType;

public class DecryptionUtils {

	/**
	 * RSA解密
	 * @param filePrivateKeyName 要使用的私钥的文件路径
	 * @param data 要解密的数据
	 * @return 返回解密后的数据
	 */
	public static byte[] decryptByPrivateKey(String filePrivateKeyName,byte[] data){
		//1.读取文件名为:filePrivateKeyName的密钥文件
		String key = IOUtils.ReadKeyFile(filePrivateKeyName);
		  try {
			//2.用读取出来的密钥对数据进行解密
			byte[] decodedData = RSAUtils.decryptByPrivateKey(data, key);
			return decodedData;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return null;
	}
	
	/**
	 * RSA解密
	 * @param filePrivateKeyName 要使用的私钥的文件路径
	 * @param data 要解密的数据
	 * @return 返回解密后的数据
	 */
	public static String decryptStringByPrivateKey(String filePrivateKeyName,String data){
		//1.读取文件名为:filePrivateKeyName的密钥文件
		String key = IOUtils.ReadKeyFile(filePrivateKeyName);
		  try {
			//2.用读取出来的密钥对数据进行解密
			String Data = RSAUtils.decryptStringByPrivateKey(data, key);
			return Data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return null;
	}
	
	
	/**
	 * 客户端解密服务器发送文件消息的KEY
	 * @param message 对message设置加密后的KEY
	 * @return 返回KEY
	 */
	public static void decryptFileKey(Message message){
		//对Key,用私钥解密
		byte[] encryptByPublicKey = decryptByPrivateKey(message.getGetter()+"_privateKey.key", message.getKey());
		//设置消息中的key
		message.setKey(encryptByPublicKey);
	}
	
	
	
	/**
	 * 客户端解密接收到的消息
	 * @param message	接受到的消息
	 * @return
	 */
	public static Message decryptMessage(Message message) throws IOException{
		/*
		 * 客户端解密接受到的消息的步骤：
		 * 	1.RSA解密消息中的KEY
		 * 		1.1.取得消息中的KEY
		 * 		1.2.用客户端上的私钥解密KEY,得到AES加密的KEY
		 * 2.AES机密消息中的内容
		 * 		2.1.得到消息中的内容
		 * 		2.2.用解密出来的KEY,对内容进行AES解密操作得到消息
		 *		2.3.设置消息内容
		 */
		
		
		//1.用客户端上的私钥解密KEY,得到DES加密的KEY
		byte[] AESKey = decryptByPrivateKey(message.getGetter()+"_privateKey.key",message.getKey());
		
		try {
			//2.用解密出来的KEY,对内容进行DES解密操作得到消息
			String decrypt = new String(AESUtils.decrypt(AESUtils.parseHexStr2Byte(message.getContent()), new String(AESKey)));
			//3.设置消息内容
			message.setContent(decrypt);
			
			//计算AES密钥和消息的摘要值
			message.setGetContentMac(ShaUtils.encryptSHA(message.getContent()));
			message.setGetKeyMac(ShaUtils.encryptSHA(new String(AESKey)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
		
	
	
	
}
