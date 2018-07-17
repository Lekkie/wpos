package com.avantir.wpos.utils;

import android.os.Build;
import android.os.RemoteException;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import com.avantir.wpos.WPOSApplication;
import com.solab.iso8583.IsoMessage;
import wangpos.sdk4.libbasebinder.Core;
import wangpos.sdk4.libkeymanagerbinder.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;


/**
 * Created by wangdh on 2016/12/2.
 * name：
 * 描述：
 */

public class KeyUtils {
    private static final String TAG = KeyUtils.class.getName();


    public static int saveCTMK(Key mKey, String ctmk, String kcv, String encryptedCtmk) throws Exception{

        if(ctmk == null || ctmk.isEmpty())
            return -1;

        byte[] CertData = new byte[8];//Reserved.
        for(int i = 0 ; i < 8 ;i ++){
            CertData[i] = 0x00;
        }
        int ret = -1;
        //TLK
        //String TLK = "11111111111111111111111111111111";
        byte[] key = ByteUtil.hexString2Bytes(ctmk);
        byte[] checkval = ByteUtil.hexString2Bytes(kcv);//TLK no check

        //String base64Ctmk = encryptWithRSAReturnBase64String(ctmk);
        GlobalData.getInstance().setCtmk(encryptedCtmk);
        IsoMessageUtil.getInstance().setSessionKeyInit(false);

        return mKey.updateKeyEx(Key.KEY_REQUEST_TLK ,
                Key.KEY_PROTECT_ZERO ,
                CertData ,
                key ,
                true,
                checkval.length, checkval, ConstantUtils.APP_NAME, ConstantUtils.KEY_ID);
    }


    public static int saveBDK(Key mKey, String bdk, String kcv, String ksn) throws Exception{


        return 1;
    }

    public static int saveTMK(Key mKey, String tmk, String tmkKcv) throws Exception{


        byte[] key = ByteUtil.hexString2Bytes(tmk);
        byte[]checkval = ByteUtil.hexString2Bytes(tmkKcv);
        byte[] CertData = new byte[8];//Reserved.
        for(int i = 0 ; i < 8 ;i ++){
            CertData[i] = 0x00;
        }

        //String base64Tmk = encryptWithRSAReturnBase64String(clearTmk);
        GlobalData.getInstance().setTmk(tmk);
        IsoMessageUtil.getInstance().setSessionKeyInit(false);

        return mKey.updateKeyEx(Key.KEY_REQUEST_TMK ,
                Key.KEY_PROTECT_TLK ,
                CertData ,
                key ,
                true,
                checkval.length, checkval, ConstantUtils.APP_NAME, ConstantUtils.KEY_ID);

        /*if(ret == 0){
            tvkeyexshow.setText("TMK import success");
        }else {
            tvkeyexshow.setText("TMK import fail");
            return;
        }*/
    }

    public static int saveTSK(Key mKey, String macKey, String macKeyKcv) throws Exception{

        byte[] key = ByteUtil.hexString2Bytes(macKey);
        byte[] checkval = ByteUtil.hexString2Bytes(macKeyKcv);
        byte[] CertData = new byte[8];//Reserved.
        for(int i = 0 ; i < 8 ;i ++){
            CertData[i] = 0x00;
        }

        try{
            //String base64Tsk = encryptWithRSAReturnBase64String(clearTsk);
            GlobalData.getInstance().setTsk(macKey);
            IsoMessageUtil.getInstance().setSessionKeyInit(false);

            return mKey.updateKeyEx(Key.KEY_REQUEST_MAK ,
                    Key.KEY_PROTECT_TMK ,
                    CertData,
                    key ,
                    true,
                    checkval.length, checkval, ConstantUtils.APP_NAME, ConstantUtils.KEY_ID);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }



    public static int saveTPK(Key mKey, String pinKey, String pinKeyKcv) throws Exception{

        byte[] key = ByteUtil.hexString2Bytes(pinKey);
        byte[] checkval = ByteUtil.hexString2Bytes(pinKeyKcv);
        byte[] CertData = new byte[8];//Reserved.
        for(int i = 0 ; i < 8 ;i ++){
            CertData[i] = 0x00;
        }

        return mKey.updateKeyEx(Key.KEY_REQUEST_PEK ,
                Key.KEY_PROTECT_TMK ,
                CertData ,
                key ,
                true,
                checkval.length, checkval, ConstantUtils.APP_NAME, ConstantUtils.KEY_ID);
    }

    public static int saveIPEKTrack2(Key mKey){
        return 0;
    }

    public static int saveIPEKEMV(Key mKey){
        return 0;
    }



    public static void generateRSAKey(){
        try{
            /*
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.genKeyPair();
            */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                generator.initialize(new KeyGenParameterSpec.Builder(
                        ConstantUtils.RSA_KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                        .setDigests(KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA224, KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512)
                        .build()
                );
                generator.generateKeyPair();
            } else {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 20);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec
                        .Builder(WPOSApplication.app.getApplicationContext())
                        .setAlias(ConstantUtils.RSA_KEY_ALIAS)
                        .setSubject(new X500Principal("CN=Arca Networks ," +
                                " O=IT Dept" +
                                " C=Nigeria"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                generator.initialize(spec);
                generator.generateKeyPair();
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static PrivateKey getPrivateKey(){
        try{
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(ConstantUtils.RSA_KEY_ALIAS, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                return null;
            }
            return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            //byte[] encodedPrivkey = privateKey.getEncoded();
            //String base64 = Base64.encodeToString(encodedPrivkey, Base64.DEFAULT);
            //return base64;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static PublicKey getPublicKey(){
        try{
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(ConstantUtils.RSA_KEY_ALIAS, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                return null;
            }
            // Get certificate of public key
            Certificate cert = ks.getCertificate(ConstantUtils.RSA_KEY_ALIAS);
            // Get public key
            PublicKey publicKey = cert.getPublicKey();
            return publicKey;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static String getBase64PublicKey(){
        try{
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(ConstantUtils.RSA_KEY_ALIAS, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                return null;
            }
            // Get certificate of public key
            Certificate cert = ks.getCertificate(ConstantUtils.RSA_KEY_ALIAS);
            // Get public key
            PublicKey publicKey = cert.getPublicKey();
            byte[] encodedPublickey = publicKey.getEncoded();
            String base64 = Base64.encodeToString(encodedPublickey, Base64.NO_WRAP);
            return base64;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }


    public static String encryptWithEMVTLKKey(Core mCore, String cipherHex) throws Exception {
        final int len = 512;
        byte[] encryptedBytes = new byte[len];
        int[] encryptedBytesLen = new int[1];
        byte[] cipherBytes = hex2bin(cipherHex);
        int cipherBytesLen = cipherBytes.length;
        int pddmode = 0;
        int encryptmode = 1;
        int operationmode = 0;
        byte[] vectordata = new byte[8];
        int vectorLen = 8;
        for (int i = 0; i < vectordata.length; i++)
            vectordata[i] = 0;

        int ret = mCore.dataEnDecryptEx(Core.ALGORITHM_3DES, operationmode, ConstantUtils.APP_NAME, encryptmode, vectorLen,
                vectordata, cipherBytesLen, cipherBytes, pddmode, encryptedBytes, encryptedBytesLen);

        if (ret != 0)
            throw new Exception("");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < encryptedBytes.length; i++) {
            builder.append(encryptedBytes[i] + ",");
        }
        return builder.toString();
    }

    public static String decryptWithEMVTLKKey(Core mCore, String encryptedHex) throws Exception {
        final int len = 512;
        byte[] cipherBytes = new byte[len];
        int[] cipherBytesLen = new int[1];
        byte[] encryptedBytes = KeyUtils.hex2bin(encryptedHex);
        int encryptedBytesLen = encryptedBytes.length;
        int pddmode = 0;
        int encryptmode = 1;
        int operationmode = 1;
        byte[] vectordata = new byte[8];
        int vectorLen = 8;
        for (int i = 0; i < vectordata.length; i++)
            vectordata[i] = 0;

        int ret = mCore.dataEnDecryptEx(Core.ALGORITHM_3DES, operationmode, ConstantUtils.APP_NAME,
                encryptmode, vectorLen, vectordata, encryptedBytesLen, encryptedBytes, pddmode, cipherBytes, cipherBytesLen);

        if (ret != 0)
            throw new Exception("");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cipherBytes.length; i++) {
            builder.append(cipherBytes[i] + ",");
        }
        return builder.toString();
    }



    public static String encryptWithDES(String key, String cipherHex) throws Exception {
        // create a binary key from the argument key (seed)
        byte[] tmp = hex2bin(key);
        byte[] keyBytes = new byte[24];
        System.arraycopy(tmp, 0, keyBytes, 0, 16);
        System.arraycopy(tmp, 0, keyBytes, 16, 8);
        SecretKey sk = new SecretKeySpec(keyBytes, "DESede");
        // create an instance of cipher
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, sk);

        // enctypt!
        byte[] encrypted = cipher.doFinal(hex2bin(cipherHex));
        return bin2hex(encrypted);
    }

    public static String decryptWithDES(String key, String encryptedHex) throws Exception {
        // create a binary key from the argument key (seed)
        byte[] tmp = hex2bin(key);
        byte[] keyBytes = new byte[24];
        System.arraycopy(tmp, 0, keyBytes, 0, 16);
        System.arraycopy(tmp, 0, keyBytes, 16, 8);
        SecretKey sk = new SecretKeySpec(keyBytes, "DESede");

        // do the decryption with that key
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decrypted = cipher.doFinal(hex2bin(encryptedHex));
        return bin2hex(decrypted);
    }


    public static String encryptWithRSAReturnBase64String(String cipherText){

        byte[] encodedBytes = null;
        try {
            PublicKey publicKey = getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,  publicKey);
            encodedBytes = cipher.doFinal(cipherText.getBytes());
            String base64EncryptedText = new String(Base64.encode(encodedBytes, Base64.NO_WRAP));
            return base64EncryptedText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptBase64StringWithRSA(String base64CipherText){
        byte[] decodedBytes = null;
        try {
            PrivateKey privateKeyEntry = getPrivateKey();
            System.out.println("PrivateKey: " + Arrays.toString(privateKeyEntry.getEncoded()));
            Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            c.init(Cipher.DECRYPT_MODE,  privateKeyEntry);
            // byte[] cipherBytes = BytesUtil.hexString2Bytes(cipherText);
            System.out.println("Encrypted Key: " + Arrays.toString(Base64.decode(base64CipherText, Base64.NO_WRAP)));
            decodedBytes = c.doFinal(Base64.decode(base64CipherText, Base64.NO_WRAP));
            return BytesUtil.bytes2HexString(decodedBytes);
            //return new String(decodedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    public static String encryptWithRSA(String publicKeyBase64, String cipherText){

        byte[] encodedBytes = null;
        try {

            PublicKey publicKey = getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,  publicKey);
            byte[] encryptedBytes = cipher.doFinal(cipherText.getBytes());
            encodedBytes = Base64.encode(encryptedBytes, Base64.NO_WRAP);
            return new String(encodedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getMac(String seed, byte[] macDataBytes) throws Exception{
        byte [] keyBytes = hex2bin(seed);
        MessageDigest digest = MessageDigest.getInstance(ConstantUtils.SHA256);
        digest.update(keyBytes, 0, keyBytes.length);
        digest.update(macDataBytes, 0, macDataBytes.length);
        byte[] hashedBytes = digest.digest();
        String hashText = bin2hex(hashedBytes);
        hashText = hashText.replace(" ", "");

        if (hashText.length() < 64) {
            int numberOfZeroes = 64 - hashText.length();
            String zeroes = "";
            String temp = hashText.toString();
            for (int i = 0; i < numberOfZeroes; i++)
                zeroes = zeroes + "0";
            temp = zeroes + temp;
            return temp;
        }

        return hashText;
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }


    public static byte[] hex2bin(String hex)
    {
        if ((hex.length() & 0x01) == 0x01)
            throw new IllegalArgumentException();
        byte[] bytes = new byte[hex.length() / 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = Character.digit((int) hex.charAt(idx * 2), 16);
            int lo = Character.digit((int) hex.charAt(idx * 2 + 1), 16);
            if ((hi < 0) || (lo < 0))
                throw new IllegalArgumentException();
            bytes[idx] = (byte) ((hi << 4) | lo);
        }
        return bytes;
    }

    public static String bin2hex(byte[] bytes)
    {
        char[] hex = new char[bytes.length * 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = (bytes[idx] & 0xF0) >>> 4;
            int lo = (bytes[idx] & 0x0F);
            hex[idx * 2] = (char) (hi < 10 ? '0' + hi : 'A' - 10 + hi);
            hex[idx * 2 + 1] = (char) (lo < 10 ? '0' + lo : 'A' - 10 + lo);
        }
        return new String(hex);
    }


    public static boolean privateKeyExists(){
        return (getPrivateKey() != null);
    }



}
