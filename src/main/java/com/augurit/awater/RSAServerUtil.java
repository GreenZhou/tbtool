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
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAServerUtil {
	public static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCErkIL/QxCp0b3G0yEY/QLLyqRHD9QruS1L1w3jpXjCwNfkq+VNfIhgNeOJjAq8ZVRqTY4pkHrzxCXPdFHLUThQ2ix/xhEQbB01cTnC8bRt/VB3lEKv8Kv30zfH8SXKnVn6MVmUVetsABBmr+xGH72X7GfeXz4KGG5N1coGGmX7xzPh52YYc9aQ64a+g3qU4TyU3Ntm2bPl19sAS1uw6Kf7+7r3KeggbPiJgvsqfMOtk44e61nj76q2Jf49uGx76Ha08DCBRI45MqygSf/ONp05LpcknVV55acFVKfjSM9thG3fKYCki1BzKbqZZKfKD7Z+Uuel0B/sMhCfZCWq35XAgMBAAECggEAGUi/61rwoC8SdfqszTnjAtKdMQQKCM0bZm+9ChVQ+PBbVzYz4aJjHwFXko4ydayOAP7XB1qfi1ltVvT/0amNy8O1yt3K+Q7vmoQ2PrVe0Q5WXZQJK5w5dJyAILEEHK6JqyCPpZzwsXetxNLBnvUFFNxNlA9pwjynys0B/fERBlPTkaBOv91IcHDxEt8l/2YBFxHulMLNzIBrwdMfz/B2QoaRfko0c3uWWt+viiZCOlJvUQcli+zHyqC5O1yii7NGEFqyp93JAq9tGRB/kKdUwwJMx94oZtuTkW+zxefvFOred3RfAb0NTkcFqYjFs2MG2oMsxnmLwH3pWLeQfdbGwQKBgQC6L619DFkDQ1sQ1OBuADY6o0+cbODfRIl0qKSwd2PbdBPUlvUL2EY9uup7vazGKfAt8CM6+kUmjc+57ectoZSHPqDasmMgodgyPljiOiprEJBQDCpU9+fxx+w/t5HKGthW0Ezl3UMg2IvWOjlDivcl8I5Az/NojQ2aNyecBjxKdwKBgQC2bn7M+RVQt6q9n6GHgrjmv4c9S1qmDvmo4J1KNjxqMUZYA16lXHPqnRh2TEOpDxF7+zjuAhR29bA3WfYFbym6oB5rwhmX4Ye2EDeXmEUyhrlkl3mGFABKg548gTXujKhTJ+bz3Cuq/2pDcf5eNAKk0gJAFrmbmc5KYVOs/02DIQKBgQCi/XIG8nRKIwDprzH5mj3e/3CGI9qIGdurQKdLxHiqeOeTR8OlZuO2zpbPVLCXmccksABndQhsQ+EBh37Ft5nq+6ydR2T4ADbfZS0yfnD74Tg0mzHDyHJIexgaf30lTHLvLNLkt9o4OtnP5JCUzGan0/r0ShdwA2tRAc+vCtZk6QKBgFVPcJ57sHsRmJeaKZhMChll4WFJdreG8zsE5qkImdHy/vkzgjQD+vTwx7qySUWqlTuMLIAomtdSZzhLKmA6LqJmNDOiDgPXZHZAOS779wf8tn+S0jJf8g7mY73ZkpXeUuyoETlicU1CqbemfBQjcEURLBo6Rku8bhqcTtvse5vhAoGAfuHzU9bz++EtQgfo2PPTD76erfjsYHGy4SR+S2WwgCBt04WZEWkbympjvpZQAMdD9WCOv3R1fNGLUIcRzrjlUt+HIAxJtW6tWYhL4i+KM4NqUhF+2TesGzYTPP8x8PSaWGT4ruynKrgxR08XaHyH3L/Htd3OMXTasgTPuZNguIw=";
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
	 * 从字符串中加载私钥
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr) throws Exception {
		try {
			byte[] buffer = Base64.decodeBase64(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 私钥加密过程
	 *
	 * @param privateKey    私钥
	 * @param plainTextData 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public static String encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception {
		if (privateKey == null) {
			throw new Exception("加密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(plainTextData);
			return Base64.encodeBase64String(output);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/**
	 * 私钥解密过程
	 *
	 * @param privateKey 私钥
	 * @param cipherData 密文数据
	 * @return 明文
	 * @throws Exception 解密过程中的异常信息
	 */
	public static String decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
			return new String(output);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}

	/**
	 * 私钥加密
	 * @param content 明文
	 * @return
	 */
	public static String priEncrypt(String content) {
		String res = "";
		try {
			//加载私钥
			RSAPrivateKey rsaprikey = loadPrivateKeyByStr(PRIVATE_KEY);
			//私钥加密
			res = encrypt(rsaprikey,content.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}


	/**
	 * 私钥解密
	 * @param content 加密后的密文
	 */
	public static String priDecrypt(String content) throws Exception{
		String res = "";
		try {
			//加载私钥
			RSAPrivateKey rsaprikey = loadPrivateKeyByStr(PRIVATE_KEY);
			//解密
			res = decrypt(rsaprikey,Base64.decodeBase64(content));
		} catch (Exception e) {
			throw new Exception(e);
		}
		return res;
	}


	public static void main(String[] args) {
		try {
			//私钥加密
			String mes = "测试";
			String enc = priEncrypt(mes);
			System.out.println("加密后的密文：" + enc);


			System.out.println("---------------------------------");

			//私钥解密 公钥加密后的内容
			String enc2 = "DVMXdCuKAkWtvcV4WEnMuxRlHIqHEqcxjr9FGYNWONINqUAhuQcebBCpkuKsjFXtadhQuZjsfl932ZDZwz6KTD4JBwVmZJ6AGCc0NIzwKkSO4qRb1aZz8kWnZE7MuTHCAmbbwdj1SbUpOrjQ3Rf9wNzGp7hfXP9kcP1EhCoeGoVt/G7qt6/F0W17ve3LqPKO3URM/N+e3ILMCjvogCt1C/dwPcNMSv5XXCrwaFIW0G7a69FqtuPWaqrmHDJ9cAV6oLZr8jw/2j7sdMOlV1fHKCRvUOl2R7ofKC06S4HmFBTZXAXlk9LVIa9cgphbnFyy/+APkELRyCq4IzarxKWwNQ==";
			System.out.println("解密后的明文：" + priDecrypt(enc2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
