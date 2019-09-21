package com.augurit.awater;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAClientUtil {
	public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhK5CC/0MQqdG9xtMhGP0Cy8qkRw/UK7ktS9cN46V4wsDX5KvlTXyIYDXjiYwKvGVUak2OKZB688Qlz3RRy1E4UNosf8YREGwdNXE5wvG0bf1Qd5RCr/Cr99M3x/Elyp1Z+jFZlFXrbAAQZq/sRh+9l+xn3l8+ChhuTdXKBhpl+8cz4edmGHPWkOuGvoN6lOE8lNzbZtmz5dfbAEtbsOin+/u69ynoIGz4iYL7KnzDrZOOHutZ4++qtiX+Pbhse+h2tPAwgUSOOTKsoEn/zjadOS6XJJ1VeeWnBVSn40jPbYRt3ymApItQcym6mWSnyg+2flLnpdAf7DIQn2Qlqt+VwIDAQAB";
	public static final String SIGN_NAME = "RSA"; // des dsa

	/**
	 * 生成公钥与私钥对
	 *
	 * @return 公钥Key=publicKey 私钥Key=privateKey
	 * @throws Exception
	 */
	public static Map<String, String> getKeys() throws Exception {
		int len = 2048;//rsa算法长度,生成秘钥对时用
		// 获得公钥与私钥对
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(SIGN_NAME);
		keyPairGen.initialize(len);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, String> keys = new HashMap<String, String>(2);
		String privateKey = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
		keys.put("privateKey", privateKey);
		RSAPublicKey rsapublicKey = (RSAPublicKey) keyPair.getPublic();
		String publicKey = Base64.encodeBase64String(rsapublicKey.getEncoded());
		keys.put("publicKey", publicKey);
		return keys;
	}

	/**
	 * 从字符串中加载公钥
	 *
	 * @param publicKeyStr 公钥数据字符串
	 * @throws Exception 加载公钥时产生的异常
	 */
	public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr) throws Exception {
		try {
			byte[] buffer = Base64.decodeBase64(publicKeyStr);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance(SIGN_NAME);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/**
	 * 公钥加密过程
	 *
	 * @param publicKey     公钥
	 * @param plainTextData 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public static String encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(SIGN_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return Base64.encodeBase64String(output);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/**
	 * 公钥解密过程
	 *
	 * @param publicKey  公钥
	 * @param cipherData 密文数据
	 * @return 明文
	 * @throws Exception 解密过程中的异常信息
	 */
	public static String decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
		if (publicKey == null) {
			throw new Exception("解密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(cipherData);
			return new String(output);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}

	/**
	 * 公钥加密
	 * @param content 明文
	 * @return
	 */
	public static String pubEncrypt(String content){
		String res = "";
		try {
			//加载公钥
			RSAPublicKey rsapubkey = loadPublicKeyByStr(PUBLIC_KEY);
			//公钥加密
			res = encrypt(rsapubkey,content.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 公钥解密
	 * @param content 加密后的密文
	 */
	public static String pubdecrypt(String content) throws Exception{
		String res = "";
		try {
			//加载公钥
			RSAPublicKey rsapubkey = loadPublicKeyByStr(PUBLIC_KEY);
			//解密
			res = decrypt(rsapubkey,Base64.decodeBase64(content));
		} catch (Exception e) {
			throw new Exception(e);
		}
		return res;
	}


	public static void main(String[] args) {
		try {

			// 公钥加密
			String mes = "测试";
			String enc = pubEncrypt(mes);
			System.out.println("加密后的密文：" + enc);


			System.out.println("---------------------------------");

			// 公钥解密 私钥加密后的内容
			String enc2 = "EI6UxZI/WkHe9/eK5axlqE4x2wwTnDVLczRavtehIAQlSzKW1qhX5+TRtmTTcMzOQS8X0Xy3uOHWOpzjI0KZ1R9fyq4stTN5nnCqEqMnzLI7gYS/ozZQTjGeBS6EZ/1UaPA+1cffTPpxZApmAq8R5GZtEluyxtx+UjDVUuyuXc3C07LIidO0Xie6KPF58VyYISiWbqjMxga77DW0ixE/Inmz38aodTRmSIRlKIfBhfEP4Vmfw8jO9ZXeqrO4RQmDnP7vKKiSK6dL+BvEUG19SF5jWU00XmmnPBmUk87Sx2Tn9hUAICNpl+yTut/JyF7yPcBi7wvuIdtpMg3tUy0HHQ==";
			System.out.println("解密后的明文：" + pubdecrypt(enc2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
