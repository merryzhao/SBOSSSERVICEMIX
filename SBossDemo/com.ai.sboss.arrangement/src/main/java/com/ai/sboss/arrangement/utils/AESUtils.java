package com.ai.sboss.arrangement.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * Security 提供了一个安全算法类,其中包括对称密码算法和散列算法。对称加密 128 位 AES算法。
 */
public final class AESUtils {
	
	private static final Log LOGGER = LogFactory.getLog(AESUtils.class);
	
	/**
	 * 禁止实例化
	 */
	private AESUtils() {
		
	}
	
	/**
	 * 根据参数生成secret.key
	 * @param strKey
	 */
	public static void createKey(String strKey) throws BizException {
		//TODO 这个地方需要进行修改
		try {
			KeyGenerator _generator = KeyGenerator.getInstance("AES");
			_generator.init(new SecureRandom(strKey.getBytes()));
			Key key = _generator.generateKey();
			File file = new File("c:\\chatmessagesecret.key");
			if (!file.exists()) {
				file.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(key);
			oos.flush();
			oos.close();
			oos = null;
			_generator = null;
		} catch (NoSuchAlgorithmException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._501);
		} catch (IOException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
			throw new BizException(e.getMessage(), ResponseCode._502);
		}
	}

	/**
	 * 加密String明文输入
	 * @param strMing 需要加密的明文信息
	 * @return 加密后的密文信息
	 */
	public static String getEncString(String strMing) {
		String strMi = "";
		try {
			return byte2hex(getEncCode(strMing.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		}
		
		return strMi;
	}

	/**
	 * 解密 以String密文输入,String明文输出
	 * @param strMi
	 * @return
	 */
	public static String getDesString(String strMi) {
		String strMing = "";
		
		try {
			return new String(getDesCode(hex2byte(strMi.getBytes())), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		}
		
		return strMing;
	}

	/**
	 * 二行制转字符串
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) { // 一个字节的数，
		// 转成16进制字符串
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			// 整数转成十六进制表示
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			}
			else {
				hs = hs + stmp;
			}
		}
		// 转成大写
		return hs.toUpperCase(); 
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("长度不是偶数");
		}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}

		return b2;
	}
	
	/**
	 * 获取秘钥的私有方法,秘钥文件存放在resource下，文件为
	 * @param keypath
	 * @return
	 */
	private static Key getSectKey() {
		Key key = null;
		URL fileUrl = Thread.currentThread().getContextClassLoader().getResource("./chatmessagesecret.key");
		File sectFile = null;
		ObjectInputStream ois;
		try {
			sectFile = new File(fileUrl.toURI());
			ois = new ObjectInputStream(new FileInputStream(sectFile));
			key = (Key) ois.readObject();
			ois.close();
			return key;
		} catch (URISyntaxException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		}
		
		return key;
	}

	/**
	 * 加密以byte[]明文输入
	 * @param byteS
	 * @return
	 */
	private static byte[] getEncCode(byte[] byteS) {
		Key key = getSectKey();
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (NoSuchAlgorithmException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (BadPaddingException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 解密以byte[]密文输入
	 * @param byteD
	 * @return
	 */
	private static byte[] getDesCode(byte[] byteD) {
		Key key = getSectKey();
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byteFina = cipher.doFinal(byteD);
		} catch (NoSuchAlgorithmException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} catch (BadPaddingException e) {
			AESUtils.LOGGER.error(e.getMessage(), e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}
}