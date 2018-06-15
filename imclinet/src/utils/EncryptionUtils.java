package utils;

import java.util.UUID;

//import org.junit.Test;

import common.Message;
import common.MessageType;


/**
 * 加密工具
 * @author c
 *
 */
public class EncryptionUtils {

	/**
	 * RSA公钥加密
	 * @param filePublicKeyName 要使用的公钥的文件路径
	 * @param data	要加密的数据
	 * @return 返回加密后的数据
	 */
	public static  byte[] encryptByPublicKey(String filePublicKeyName,String data){
		//1.读取文件名为:filePublicKeyName的密钥文件
		String key = IOUtils.ReadKeyFile(filePublicKeyName);
        try {
        	//2.用读取出来的密钥对数据进行加密
			byte[] encodedData = RSAUtils.encryptByPublicKey(data.getBytes(), key);
			return encodedData;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return  null;
	}
	
	
	/**
	 * RSA私钥加密
	 * @param filePrivateKeyName 要使用的私钥的文件路径
	 * @param data 要加密的数据
	 * @return 返回加密后的数据
	 */
	public static byte[] encryptByPrivateKey(String filePrivateKeyName,String data){
		//1.读取文件名为:filePrivateKeyName的密钥文件
		String key = IOUtils.ReadKeyFile(filePrivateKeyName);
		 try {
        	//2.用读取出来的密钥对数据进行加密
			byte[] encodedData = RSAUtils.encryptByPrivateKey(data.getBytes(), key);
			return encodedData;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	       return  null;
	}
	
	/**
	 * RSA公钥加密字符串
	 * @param filePublicKeyName 要使用的公钥的文件路径
	 * @param data	要加密的数据
	 * @return 返回加密后的数据
	 */
	public static  String encryptStringByPublicKey(String filePublicKeyName,String data){
		//1.读取文件名为:filePublicKeyName的密钥文件
		String key = IOUtils.ReadKeyFile(filePublicKeyName);
        try {
        	//2.用读取出来的密钥对数据进行加密
			String Data = RSAUtils.encryptStringByPublicKey(data, key);
			return Data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return  null;
	}
	
	
	
	/**
	 * 客户端加密消息数据
	 * @param filePublicKeyName 要使用的公钥的文件路径
	 * @param message 发送的消息
	 */
	public static Message encryptMessage(String filePublicKeyName,Message message){
		/*
		 * 客户端加密消息数据的步骤:
		 * 1.AES加密发送的消息内容.
		 * 		1.1 生成KEY,生成唯一的UUID作为KEY
		 * 		1.2用这个KEY来进行AES加密发送的内容
		 * 		1.3设置加密内容
		 * 2.加密发送能够解密AES的key.
		 * 		2.1对KEY用服务器派发的公钥进行RSA加密(只有服务器的私钥可以解密)
		 *		2.2设置key
		 */		
				
		//1.生成唯一的UUID
		String key = UUID.randomUUID().toString();
		System.out.println("UUID" + key + "\n" + key.length());
		//2.用这个UUID作为key来加密发送消息
	
	/********************************对AES的key和消息内容取摘要*********************/
		//对AES的key取摘要
		try {
			message.setSentKeyMac(ShaUtils.encryptSHA(key));
			System.out.println("AES key: "+message.getSentKeyMac());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//对消息原内容取摘要
		try {
			message.setSentContentMac(ShaUtils.encryptSHA(message.getContent()));
			System.out.println("消息 ： " +message.getSentContentMac());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
		}
		
	/********************************对AES的key和消息内容取摘要*********************/
		
		
		//判断发送的类型
		if(message.getMessageType().equals(MessageType.Common_Message_ToPerson)||message.getMessageType().equals(MessageType.Common_Message_ToAll)){
			try {
				
				//设置AES加密后的消息
				message.setContent(AESUtils.parseByte2HexStr(AESUtils.encrypt(message.getContent(), key)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				//2.对Key,用服务器公钥加密
				byte[] encryptKey = encryptByPublicKey("publicKey.key", key);
				//3.设置消息中的key
				message.setKey(encryptKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;
	}
	
	
	
	/**
	 * 客户端加密发送文件消息的KEY
	 * @param message 对message设置加密后的KEY
	 * @return 返回KEY
	 */
	public static String encryptFileKey(Message message){
		//1.生成唯一的UUID
		String key = UUID.randomUUID().toString();
	
		//2.用这个UUID作为key来加密发送消息
		//对Key,用公钥加密
		byte[] encryptByPublicKey = encryptByPublicKey("publicKey.key", key);
		//设置消息中的key
		message.setKey(encryptByPublicKey);
		return key;
	}
	
	
	
	
	
	
	
	/**
	 * 对发送的公钥进行加盐md5加密再形成数字签名
	 * @param myPublicKey 客户端生成的公钥
	 * @param myPrivateKey 客户端生成的私钥 
	 * @return
	 */
//	public static byte[] encryptKeySign(String myPublicKey,String myPrivateKey){
//		/*
//		 * 对发送的公钥进行加盐md5加密再形成数字签名的步骤
//		 * 	1. 对公钥进行加盐,然后在进行md5加密
//		 * 	2. 对md5加密后的数据用自己的私钥进行RSA加密形成数字签名
//		 */
//		//1. 对公钥进行加盐,然后在进行md5加密
//		//加盐
//		String publicKey = "CaiRou@and#Wren!" + myPublicKey;
//		//md5加密
//		String md5Key = Md5Utils.md5(publicKey);
//		//2. 对md5加密后的数据用自己的私钥进行RSA加密形成数字签名
//		byte[] key = EncryptionUtils.encryptByPrivateKey(myPrivateKey, md5Key);
//		return key;
//	}
//	
//	

	
	
}
