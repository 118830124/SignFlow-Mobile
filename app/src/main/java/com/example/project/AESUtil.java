package com.example.project;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * AESUtil 类提供了AES加密算法的密钥生成、加密和解密功能。
 */
public class AESUtil {

    // 指定加密算法为AES
    private static final String ALGORITHM = "AES";

    // 指定生成密钥的长度为128位
    private static final int KEY_SIZE = 128;

    /**
     * 生成一个AES密钥。
     * @return 返回生成的密钥
     * @throws Exception 抛出异常，包括密钥生成失败的情况
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM); // 创建一个密钥生成器
        keyGenerator.init(KEY_SIZE); // 初始化密钥生成器，设置密钥长度
        return keyGenerator.generateKey(); // 生成密钥并返回
    }

    /**
     * 使用指定的密钥对数据进行AES加密。
     * @param dataToEncrypt 需要加密的数据
     * @param key 加密密钥
     * @return 返回加密后的数据
     * @throws Exception 抛出异常，包括加密过程失败的情况
     */
    public static byte[] encrypt(byte[] dataToEncrypt, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM); // 创建一个Cipher对象，用于加密
        cipher.init(Cipher.ENCRYPT_MODE, key); // 初始化为加密模式
        return cipher.doFinal(dataToEncrypt); // 加密数据并返回加密后的数据
    }

    /**
     * 使用指定的密钥对AES加密后的数据进行解密。
     * @param encryptedData 加密后的数据
     * @param key 解密密钥
     * @return 返回解密后的数据
     * @throws Exception 抛出异常，包括解密过程失败的情况
     */
    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM); // 创建一个Cipher对象，用于解密
        cipher.init(Cipher.DECRYPT_MODE, key); // 初始化为解密模式
        return cipher.doFinal(encryptedData); // 解密数据并返回解密后的数据
    }
}
