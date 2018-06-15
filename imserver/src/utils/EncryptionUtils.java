package utils;

import java.util.UUID;

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
	 * 服务器转发加密消息数据
	 * @param filePublicKeyName 服务器上的私钥文件路径
	 * @param message 转发的消息
	 */
	public static Message encryptMessage(String filePrivateKeyName,Message message){
		/*
		 * 服务器转发加密消息的步骤：
		 * 	1.得到需要转发的消息(message)
		 * 	2.RSA解密消息的KEY
		 *	3.对解密后的Key用需要转发到的用户的公钥(注册时，用户发送给服务器的公钥)进行RSA加密,只有客户端的私钥能解密
		 *	4.重新设置KEY
		 */
		//1.用私钥解密message中的key(RSA解密消息的AES的KEY)
		if(message.getMessageType().equals(MessageType.Common_Message_ToPerson)||message.getMessageType().equals(MessageType.Common_Message_ToAll)){
			//AES
			byte[] key = DecryptionUtils.decryptByPrivateKey(filePrivateKeyName, message.getKey());	
			try {
				//2.对解密后的Key用需要转发到的用户的公钥(登陆时，用户发送给服务器的公钥)进行RSA加密,只有客户端的私钥能解密
				byte[] encryptKey = encryptByPublicKey(message.getGetter()+"_publicKey.key", new String(key));
				//3.设置消息中的key
				message.setKey(encryptKey);
				
				//清除服务器计算的AES密钥和消息的摘要值
				message.setGetKeyMac(null);
				message.setGetContentMac(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;
	}
	
	public static byte [] GetAES(String filePrivateKeyName,Message message){
		
		byte[] key = DecryptionUtils.decryptByPrivateKey(filePrivateKeyName, message.getKey());	
	    return key;
	}
	
	public static Message encryptMessage2(Message message){
		
		//1.用私钥解密message中的key(RSA解密消息的AES的KEY)
		if(message.getMessageType().equals(MessageType.Common_Message_ToPerson)||message.getMessageType().equals(MessageType.Common_Message_ToAll)){
				
			try {
				//2.对解密后的Key用需要转发到的用户的公钥(登陆时，用户发送给服务器的公钥)进行RSA加密,只有客户端的私钥能解密
				byte[] encryptKey = encryptByPublicKey(message.getGetter()+"_publicKey.key", new String(message.getKey()));
				//3.设置消息中的key
				message.setKey(encryptKey);
				
				//清除服务器计算的AES密钥和消息的摘要值
				message.setGetKeyMac(null);
				message.setGetContentMac(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;
	}

	
	
	
	
	
}
