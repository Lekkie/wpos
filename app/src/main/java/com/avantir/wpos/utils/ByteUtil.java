package com.avantir.wpos.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class ByteUtil {

    private static final String TAG = "[ByteUtil]";

    private ByteUtil() {
    }

    public static byte hex2byte(char hex) {
        if (hex <= 'f' && hex >= 'a') {
            return (byte) (hex - 'a' + 10);
        }

        if (hex <= 'F' && hex >= 'A') {
            return (byte) (hex - 'A' + 10);
        }

        if (hex <= '9' && hex >= '0') {
            return (byte) (hex - '0');
        }

        return 0;
    }

    public static byte[] ascii2Bcd(String ascii) {
        if (ascii == null)
            return null;
        if ((ascii.length() & 0x01) == 1)
            ascii = "0" + ascii;
        byte[] asc = ascii.getBytes();
        byte[] bcd = new byte[ascii.length() >> 1];
        for (int i = 0; i < bcd.length; i++) {
            bcd[i] = (byte) (hex2byte((char) asc[2 * i]) << 4 | hex2byte((char) asc[2 * i + 1]));
        }
        return bcd;
    }

    public static int bytes2Int(byte[] data) {
        if (data == null || data.length == 0) {
            return 0;
        }

        int total = 0;
        for (int i = 0; i < data.length; i++) {
            total += (data[i] & 0xff) << (data.length - i - 1) * 8;
        }
        return total;
    }

    /**
     * @param n:[0,65536]
     */
    public static String intToHexString(int n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n / 256);
        b[1] = (byte) (n % 256);
        return String.format("%02X%02X", b[0], b[1]);
    }

    public static String int3ToHexString(int n) {
        byte[] b = new byte[3];
        b[0] = (byte) (n / 256);
        b[1] = (byte) (n % 256);

        return String.format("%02X%02X", b[0], b[1]);
    }

    public static String bytes2HexString(byte[] data) {
        if (data == null)
            return "";
        StringBuilder buffer = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                buffer.append('0');
            }
            buffer.append(hex);
        }
        return buffer.toString().toUpperCase();
    }

    public static byte[] hexString2Bytes(String data) {
        if (data == null)
            return null;
        byte[] result = new byte[(data.length() + 1) / 2];
        if ((data.length() & 1) == 1) {
            data += "0";
        }
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (hex2byte(data.charAt(i * 2 + 1)) | (hex2byte(data.charAt(i * 2)) << 4));
        }
        return result;
    }

    public static String bcd2Ascii(final byte[] bcd) {
        if (bcd == null)
            return "";
        StringBuilder sb = new StringBuilder(bcd.length << 1);
        for (byte ch : bcd) {
            byte half = (byte) (ch >> 4);
            sb.append((char) (half + ((half > 9) ? ('A' - 10) : '0')));
            half = (byte) (ch & 0x0f);
            sb.append((char) (half + ((half > 9) ? ('A' - 10) : '0')));
        }
        return sb.toString();
    }

    public static byte[] toBytes(String data, String charsetName) {
        try {
            return data.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] toBytes(String data) {
        return toBytes(data, "ISO-8859-1");
    }

    public static byte[] toGBK(String data) {
        return toBytes(data, "GBK");
    }

    public static byte[] toGB2312(String data) {
        return toBytes(data, "GB2312");
    }

    public static byte[] toUtf8(String data) {
        return toBytes(data, "UTF-8");
    }

    public static String fromBytes(byte[] data, String charsetName) {
        try {
            return new String(data, charsetName);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String fromBytes(byte[] data) {
        return fromBytes(data, "ISO-8859-1");
    }

    public static String fromGBK(byte[] data) {
        return fromBytes(data, "GBK");
    }

    public static String fromGB2312(byte[] data) {
        return fromBytes(data, "GB2312");
    }

    public static String fromGB2312New(String data) {
        return fromGB2312(toBytes(data.trim()));
    }

    public static String fromUtf8(byte[] data) {
        return fromBytes(data, "UTF-8");
    }

    public static void dumpHex(String msg, byte[] bytes) {
        int length = bytes.length;
        msg = (msg == null) ? "" : msg;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n---------------------- " + msg + "(len:%d) ----------------------\n", length));
        for (int i = 0; i < bytes.length; i++) {
            if (i % 16 == 0) {
                if (i != 0) {
                    sb.append('\n');
                }
                sb.append(String.format("0x%08X    ", i));
            }
            sb.append(String.format("%02X ", bytes[i]));
        }
        sb.append("\n----------------------------------------------------------------------\n");
        Log.d(TAG, sb.toString());
    }

    public static String str2HexStr(String str) {
        final char[] mChars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();

        for (int i = 0; i < bs.length; i++) {
            sb.append(mChars[(bs[i] & 0xFF) >> 4]);
            sb.append(mChars[bs[i] & 0x0F]);
        }
        return sb.toString().trim();
    }

    /**
     * 将整数按大端模式转为4字节数组
     *
     * @param intValue
     * @return 转换结果
     */
    public static byte[] intToBytes(int intValue) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((intValue >> ((3 - i) << 3)) & 0xFF);
        }
        return bytes;
    }

    /**
     * 将整数按小端模式转为4字节数组
     *
     * @param value
     * @return 转换结果
     */
    public static byte[] int2Bytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }
    // ASCII的字符串转byte数组

    public static byte[] ascii2Bytes(String asciiString) {
        if (asciiString == null) {
            return null;
        }
        byte[] byt = asciiString.getBytes(US_ASCII);
        return byt;
    }

    // ASCII的字节数组串转byte数组
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    private static byte asc_to_bcd(byte asc) {
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

    public static byte[] subBytes(byte[] data, int offset, int len) {
        if ((offset < 0) || (data.length <= offset)) {
            return null;
        }

        if ((len < 0) || (data.length < offset + len)) {
            len = data.length - offset;
        }

        byte[] ret = new byte[len];

        System.arraycopy(data, offset, ret, 0, len);
        return ret;
    }

    public static byte[] merage(byte[][] data) {
        int len = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                throw new IllegalArgumentException("");
            }
            len += data[i].length;
        }

        byte[] newData = new byte[len];
        len = 0;
        byte[][] arrayOfByte = data;
        int j = data.length;
        for (int i = 0; i < j; i++) {
            byte[] d = arrayOfByte[i];
            System.arraycopy(d, 0, newData, len, d.length);
            len += d.length;
        }
        return newData;
    }

}