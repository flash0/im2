package utils;

import java.io.ByteArrayOutputStream;  
import java.security.Key;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.PrivateKey;  
import java.security.PublicKey;  
import java.security.Signature;  
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;  
import java.security.interfaces.RSAPublicKey;  
import java.security.spec.PKCS8EncodedKeySpec;  
import java.security.spec.X509EncodedKeySpec;  
import java.util.HashMap;  
import java.util.Map;  
  
import javax.crypto.Cipher;  

public class RSAUtils {  

    /**  
     * 加密算法RSA  
     */  
    public static final String KEY_ALGORITHM = "RSA";  
  
    /**  
     * 签名算法  
     */  
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";  
  
    /**  
     * 获取公钥的key  
     */  
    private static final String PUBLIC_KEY = "RSAPublicKey";  
  
    /**  
     * 获取私钥的key  
     */  
    private static final String PRIVATE_KEY = "RSAPrivateKey";  
  
    /**  
     * RSA最大加密明文大小  
     */  
    private static final int MAX_ENCRYPT_BLOCK = 117;  
  
    /**  
     * RSA最大解密密文大小  
     */  
    private static final int MAX_DECRYPT_BLOCK = 128;  
  
    /**  
     * <p>  
     * 生成密钥对(公钥和私钥)  
     * </p>  
     *   
     * @return  
     * @throws Exception  
     */  
    public static Map<String, Object> genKeyPair() throws Exception {  
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);  //RSA
        keyPairGen.initialize(1024);  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        keyMap.put(PUBLIC_KEY, publicKey);  
        keyMap.put(PRIVATE_KEY, privateKey);  
        return keyMap;  
    }  
  
    /**  
     * <p>  
     * 用私钥对信息生成数字签名  
     * </p>  
     *   
     * @param data  
     *            已加密数据  
     * @param privateKey  
     *            私钥(BASE64编码)  
     *   
     * @return  
     * @throws Exception  
     */  
    public static String sign(byte[] data, String privateKey) throws Exception {  
        byte[] keyBytes = Base64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(privateK);  
        signature.update(data);  
        return Base64Utils.encode(signature.sign());  
    }  
  
    /**  
     * <p>  
     * 校验数字签名  
     * </p>  
     *   
     * @param data  
     *            已加密数据  
     * @param publicKey  
     *            公钥(BASE64编码)  
     * @param sign  
     *            数字签名  
     *   
     * @return  
     * @throws Exception  
     *   
     */  
    public static boolean verify(byte[] data, String publicKey, String sign)  
            throws Exception {  
        byte[] keyBytes = Base64Utils.decode(publicKey);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PublicKey publicK = keyFactory.generatePublic(keySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(publicK);  
        signature.update(data);  
        return signature.verify(Base64Utils.decode(sign));  
    }  
  
    /**  
     * <P>  
     * 私钥解密  
     * </p>  
     *   
     * @param encryptedData  
     *            已加密数据  
     * @param privateKey  
     *            私钥(BASE64编码)  
     * @return  
     * @throws Exception  
     */  
    public static byte[] decryptByPrivateKey(byte[] encryptedData,  
            String privateKey) throws Exception {  
        byte[] keyBytes = Base64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, privateK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher  
                        .doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher  
                        .doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
    
    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptStringByPrivateKey(String data, String privateKey)
            throws Exception {
    	 byte[] keyBytes = Base64Utils.decode(privateKey);  
         PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
         KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
         Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
         Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
         cipher.init(Cipher.DECRYPT_MODE, privateK);  
        //模长
        int key_len = ((RSAKey) privateK).getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
       // System.err.println(bcd.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for(byte[] arr : arrays){
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }
  
    /**  
     * <p>  
     * 公钥解密  
     * </p>  
     *   
     * @param encryptedData  
     *            已加密数据  
     * @param publicKey  
     *            公钥(BASE64编码)  
     * @return  
     * @throws Exception  
     */  
    public static byte[] decryptByPublicKey(byte[] encryptedData,  
            String publicKey) throws Exception {  
        byte[] keyBytes = Base64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, publicK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher  
                        .doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher  
                        .doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /**  
     * <p>  
     * 公钥加密  
     * </p>  
     *   
     * @param data  
     *            源数据  
     * @param publicKey  
     *            公钥(BASE64编码)  
     * @return  
     * @throws Exception  
     */  
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)  
            throws Exception {  
        byte[] keyBytes = Base64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, publicK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    }  
    
    /**
     * 公钥加密,返回字符串
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptStringByPublicKey(String data, String publicKey)
            throws Exception {
    	 byte[] keyBytes = Base64Utils.decode(publicKey);  
         X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
         KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
         Key publicK = keyFactory.generatePublic(x509KeySpec);  
         // 对数据加密  
         Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
         cipher.init(Cipher.ENCRYPT_MODE, publicK);  
        // 模长
        int key_len = ((RSAKey) publicK).getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += bcd2Str(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }
  
    /**  
     * <p>  
     * 私钥加密  
     * </p>  
     *   
     * @param data  
     *            源数据  
     * @param privateKey  
     *            私钥(BASE64编码)  
     * @return  
     * @throws Exception  
     */  
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)  
            throws Exception {  
        byte[] keyBytes = Base64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, privateK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    }  
  
    
    /**  
     * <p>  
     * 获取私钥  
     * </p>  
     *   
     * @param keyMap  
     *            密钥对  
     * @return  
     * @throws Exception  
     */  
    public static String getPrivateKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        return Base64Utils.encode(key.getEncoded());  
    }  
  
    /**  
     * <p>  
     * 获取公钥  
     * </p>  
     *   
     * @param keyMap  
     *            密钥对  
     * @return  
     * @throws Exception  
     */  
    public static String getPublicKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return Base64Utils.encode(key.getEncoded());  
    }  
    
    /**
     * ASCII码转BCD码
     *
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }
    public static byte asc_to_bcd(byte asc) {
        byte bcd;
 
        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }
    /**
     * BCD转字符串
     */
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;
 
        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
 
            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }
    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i=0; i<x+z; i++) {
            if (i==x+z-1 && y!=0) {
                str = string.substring(i*len, i*len+y);
            }else{
                str = string.substring(i*len, i*len+len);
            }
            strings[i] = str;
        }
        return strings;
    }
    /**
     *拆分数组
     */
    public static byte[][] splitArray(byte[] data,int len){
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if(y!=0){
            z = 1;
        }
        byte[][] arrays = new byte[x+z][];
        byte[] arr;
        for(int i=0; i<x+z; i++){
            arr = new byte[len];
            if(i==x+z-1 && y!=0){
                System.arraycopy(data, i*len, arr, 0, y);
            }else{
                System.arraycopy(data, i*len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }
    

}