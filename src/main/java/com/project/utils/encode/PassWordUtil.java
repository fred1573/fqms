package com.project.utils.encode;

import com.project.common.Constants;
import com.project.utils.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PassWordUtil {

    /**
     * 获取直连订单接口签名
     *
     * @return
     */
    public static String getDirectSignature(long timestamp) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.OMS_PROXY_PID);
        stringBuilder.append(timestamp);
        stringBuilder.append(Constants.OMS_DX_NAME);
        stringBuilder.append(Constants.OMS_DX_PWD);
        return getMd5Pwd(stringBuilder.toString());
    }


    public static String getDirectSignature(long timestamp,String proxyId,String name,String pwd) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(proxyId);
        stringBuilder.append(timestamp);
        stringBuilder.append(name);
        stringBuilder.append(pwd);
        return getMd5Pwd(stringBuilder.toString());
    }

    /**
     * 获取直连订单接口签名
     *
     * @return
     *//*
    public static String getDirectSignature(long timestamp, Integer otaId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(otaId));
        stringBuilder.append(timestamp);
        stringBuilder.append(Constants.OMS_DX_NAME);
        stringBuilder.append(Constants.OMS_DX_PWD);
        return getMd5Pwd(stringBuilder.toString());
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(getDirectSignature(date.getTime(),102));
        System.out.println(String.valueOf(date.getTime())+102);
        String temp ="102"+ String.valueOf(date.getTime())+"DX"+"dx43646";
        System.out.println(getMd5Pwd(temp));

    }*/

    //哈希加密算法，传入要加密的字符串得到加密后的字符串
    public static String getShaPwd(String pwd) {
        return getShaPwd(pwd, null);
    }

    //哈希加密算法，传入要加密的字符串以及需要盐值，默认是用户名
    public static String getShaPwd(String pwd, String userName) {
        ShaPasswordEncoder sha = new ShaPasswordEncoder();
        sha.setEncodeHashAsBase64(false);
        pwd = sha.encodePassword(pwd, userName);
        return pwd;
    }

    //md5加密算法，传入要加密的字符串得到加密后的字符串
    public static String getMd5Pwd(String pwd) {
        return getMd5Pwd(pwd, null);
    }

    //md5加密算法，传入要加密的字符串以及需要的盐值，默认是用户名
    public static String getMd5Pwd(String pwd, String userName) {
        Md5PasswordEncoder md5 = new Md5PasswordEncoder();
        md5.setEncodeHashAsBase64(false);
        pwd = md5.encodePassword(pwd, userName);
        return pwd;
    }

    /**
     * 根据输入字符串生成公钥与私钥
     */
    public static Map<String, byte[]> generater(String s) {
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        try {
            java.security.KeyPairGenerator keygen = java.security.KeyPairGenerator.getInstance("RSA");
            SecureRandom secrand = new SecureRandom();
            secrand.setSeed(s.getBytes()); // 初始化随机产生器
            keygen.initialize(1024, secrand);
            KeyPair keys = keygen.genKeyPair();
            PublicKey pubkey = keys.getPublic();
            PrivateKey prikey = keys.getPrivate();
            map.put("pubKey", Base64.encodeBase64(pubkey.getEncoded()));
            map.put("priKey", Base64.encodeBase64(prikey.getEncoded()));
        } catch (java.lang.Exception e) {
            System.out.println("生成密钥对失败");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Description:数字签名
     *
     * @param priKeyText
     * @param plainText
     * @return
     */
    public static byte[] sign(byte[] priKeyText, String plainText) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKeyText));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey prikey = keyf.generatePrivate(priPKCS8);
            // 用私钥对信息生成数字签名
            java.security.Signature signet = java.security.Signature.getInstance("MD5withRSA");
            signet.initSign(prikey);
            signet.update(plainText.getBytes());
            byte[] signed = Base64.encodeBase64(signet.sign());
            return signed;
        } catch (java.lang.Exception e) {
            System.out.println("签名失败");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Description:校验数字签名,此方法不会抛出任务异常,成功返回true,失败返回false,要求全部参数不能为空
     *
     * @param pubKeyText 公钥,base64编码
     * @param plainText  明文
     * @param signText   数字签名的密文,base64编码
     * @return 校验成功返回true 失败返回false
     */
    public static boolean verify(byte[] pubKeyText, String plainText, byte[] signText) {
        try {
            // 解密由base64编码的公钥,并构造X509EncodedKeySpec对象
            java.security.spec.X509EncodedKeySpec bobPubKeySpec = new java.security.spec.X509EncodedKeySpec(Base64.decodeBase64(pubKeyText));
            // RSA对称加密算法
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            java.security.PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
            // 解密由base64编码的数字签名
            byte[] signed = Base64.decodeBase64(signText);
            java.security.Signature signatureChecker = java.security.Signature.getInstance("MD5withRSA");
            signatureChecker.initVerify(pubKey);
            signatureChecker.update(plainText.getBytes());
            // 验证签名是否正常
            if (signatureChecker.verify(signed))
                return true;
            else
                return false;
        } catch (Throwable e) {
            System.out.println("校验签名失败");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取基础参数
     *
     * @return
     */
    public static Map<String, Object> getBaseParamMap() {
        Map<String, Object> paramsMap = new HashMap<>();
        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        paramsMap.put("timestamp", timestamp);
        // 获取签名
        paramsMap.put("signature", PassWordUtil.getDirectSignature(timestamp));
        // 设置父渠道ID
        paramsMap.put("otaId", Constants.OMS_PROXY_PID);
        return paramsMap;
    }


}
