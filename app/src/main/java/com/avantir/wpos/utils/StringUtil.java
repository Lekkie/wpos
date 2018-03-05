
package com.avantir.wpos.utils;

/**
 * Created by IntelliJ IDEA
 * User: Sirius
 * Date: 2014/11/13
 * Time: 15:25
 */
public class StringUtil {


    /**
     * String 非空判断
     * @param msg
     * @return
     */
    public static boolean isEmpty(String msg){
        boolean is;
        if (msg != null&&!"".equals(msg)) {
            is = false;
        }else {
            is = true;
        }
        return is;
    }


    public static boolean isNull(CharSequence str) {
        return str == null || str.length() == 0;
    }


    //为 EditText 获取相应的 selection index.即设置光标位置为最右方
    public static int getSelectionIndex(CharSequence str) {
        return isNull(str) ? 0 : str.length();
    }


    /**
     * 在str左边填充fill内容，填充后的总长度为totalLength。
     *
     * @param fill
     * @param totalLength
     * @param str
     * @return
     */
    public static String leftPadding(char fill, int totalLength, String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = str.length(); i < totalLength; i++) {
            buffer.append(fill);
        }
        buffer.append(str);
        return buffer.toString();
    }

    public static String leftPadding(String fill, int totalLength, String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = str.length(); i < totalLength; i++) {
            buffer.append(fill);
        }
        buffer.append(str);
        return buffer.toString();
    }

    //左边增加一定长度的字符串
    public static String leftAppend(String fill, int appendLength, String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < appendLength; i++) {
            buffer.append(fill);
        }
        buffer.append(str);
        return buffer.toString();
    }

    //右边增加一定长度的字符串
    public static String rightAppend(String fill, int appendLength, String str) {
        StringBuilder buffer = new StringBuilder(str);
        for (int i = 0; i < appendLength; i++) {
            buffer.append(fill);
        }
        return buffer.toString();
    }

    public static String rightPadding(String fill, int totalLength, String str) {
        StringBuilder buffer = new StringBuilder(str);
        while (str.length() < totalLength) {
            buffer.append(fill);
        }
        return buffer.toString();
    }


    //得到字符串的字节长度
    public static int getContentByteLength(String content) {
        if (content == null || content.length() == 0)
            return 0;
        int length = 0;
        for (int i = 0; i < content.length(); i++) {
            length += getByteLength(content.charAt(i));
        }
        return length;
    }

    //得到几位字节长度
    private static int getByteLength(char a) {
        String tmp = Integer.toHexString(a);
        return tmp.length() >> 1;
    }

    //文本右边补空格
    public static String fillRightSpacePrintData(String context, int fillDataLength){

        if(context != null){
            int printDataLength = fillDataLength - context.length();
            if(printDataLength>0){
                for (int i=0;i<printDataLength;i++){
                    context+=" ";
                }
            }

        }else {
            context = "";
            for (int i=0;i<fillDataLength;i++){
                context+=" ";
            }
        }

        return context;
    }

    //文本左边补空格
    public static String fillLeftSpacePrintData(String context, int fillDataLength){

        if(context != null){
            int printDataLength = fillDataLength - context.length();
            if(printDataLength>0){
                String tempSpace = "";
                for (int i=0;i<printDataLength;i++){
                    tempSpace+=" ";
                }

                context = tempSpace + context;
            }

        }else {
            context = "";
            for (int i=0;i<fillDataLength;i++){
                context+=" ";
            }
        }

        return context;
    }



    public static String leftPad(String str, int len, char pad) {
        if(str == null)
            return null;
        StringBuilder sb = new StringBuilder();
        while (sb.length() + str.length() < len) {
            sb.append(pad);
        }
        sb.append(str);
        String paddedString = sb.toString();
        return paddedString;
    }

    public static String rightPad(String str, int len, char pad) {

        if(str == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        while (sb.length() < len) {
            sb.append(pad);
        }
        String paddedString = sb.toString();
        return paddedString;
    }


    public static String maskPan(String pan){
        //take first 6 characters
        String firstPart = pan.substring(0, 6);
        //take last 4 characters
        int len = pan.length();
        String lastPart = pan.substring(len - 4, len);

        //take the middle part (******)
        //int middlePartLength = len - 10;
        //String middleLastPart = leftPad(lastPart, middlePartLength + 4, '*');

        int middlePartLength = len - 6;
        String middleLastPart = leftPad("", middlePartLength, '*');

        return firstPart + middleLastPart;
    }

    public static void main(String[] args) {
        String a = "阿斯顿法";
        a = leftAppend("-", 10, a);
        System.out.println(a);

        a = rightAppend("*", 6, a);
        System.out.println(a);
    }
}
