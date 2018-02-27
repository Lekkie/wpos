package com.avantir.wpos.utils;

import wangpos.sdk4.libbasebinder.HEX;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class TLV {


    // IC卡第5部分-P84
    private final static String NAMES = //
        "9F01:收单行标识;"//
            + "9F40:附加终端性能;"//
            + "81:授权金额(二进制);"//
            + "9F02:授权金额(数值型);"//
            + "9F04:其它金额(二进制);"//
            + "9F03:其它金额(数值型);"//
            + "9F3A:参考货币金额;"//
            + "9F06:应用标识(AID);"//
            + "9F09:应用版本号;"//
            + "8A:授权响应代码;"//
            + "9F34:持卡人验证方法(CVM)结果;"//
            + "9F22:认证中心公钥索引;"//
            + "83:命令模版;"//
            + "9F1E:接口设备(IFD)序列号;"//
            + "9F15:商户分类码;"//
            + "9F16:商户标识;"//
            + "9F39:销售点(POS)输入方式;"//
            + "9F33:终端性能(TERM_CAPA);"//
            + "9F1A:终端国家代码;"//
            + "9F1B:终端最低限额;"//
            + "9F1C:终端标识;"//
            + "9F35:终端类型;"//
            + "95:终端验证结果(TVR);"//
            + "98:交易证书(TC)哈希值;"//
            + "5F2A:交易货币代码;"//
            + "5F36:交易货币指数;"//
            + "9A:交易日期;"//
            + "99:交易PIN数据;"//
            + "9F3C:交易参考货币代码;"//
            + "9F3D:交易参考货币指数;"//
            + "9F41:终端维护的交易序列计数器;"//
            + "9B:交易状态信息(TSI);"//
            + "9F21:交易时间;"//
            + "9C:交易类型;"//
            + "9F37:不可预知数(UNPR_NO);"//
            + "5F57:账户类型;"//
            + "6F:文件控制信息(FCI)模板;"//
            + "9F26:应用密文(AC)(ARQC);"// 生成应用密文命令返回的密文
            + "9F42:应用货币代码;"// 按 GB/T 12406 编码
            + "9F51:应用货币代码;"// JR/T 0025 专有数据。按GB/T 12406 编码
            + "9F44:应用货币指数;"// 指出金额数据中小数点从最右边开始第几个位置
            + "9F52:应用缺省行为(ADA);"// 定义在一些特定条件下卡片执行的发卡行指定的行为。如果卡片中没有此数据，缺省认为全零
            + "9F05:应用自定义数据;"// 和卡片应用有关的发卡行指定数据
            + "5F25:应用生效日期;"// YYMMDD卡片中应用启用日期
            + "5F24:应用失效日期;"// YYMMDD卡片中应用启用日期
            + "94:应用文件定位器(AFL);"// 指出和应用相关的数据存放位置（短文件标识符和记录号）,对于每一个要读的文件，AFL 包括
            // 4 个字节：
            // 字节 1：位 8–4=SFI 短文件标识符; 位 3–1=000;
            // 字节 2：文件中要读的第 1 个记录的记录号（不能为 0）;
            // 字节 3： 文件中要读的最后一个记录的记录号 （等于或大于字节 2）;
            // 字节 4：从字节 2 中的记录号开始，存放认证用静态数据记录的个数（值从 0 到字节 3-字节 2+1的值;
            + "4F:应用标识符(AID);"// 按 GB/T 16649.5
            // 规定标识应用。由注册的应用提供商标识（RID）和扩展的专用应用标识符（PIX）组成
            + "82:应用交互特征(AIP);"// 一个列表，说明此应用中卡片支持指定功能的能力;
            // 字节 1： 位 8：1=RFU; 位 7：1=支持 SDA; 位 6：1=支持 DDA; 位 5：1=支持持卡人认证; 位
            // 4：1=执行终端风险管理; 位 3：1=支持发卡行认证; 位 2：RFU（0）; 位 1：1=支持 CDA 字节
            // 2：RFU（“00”）
            + "50:应用标签;"// 和 AID 相关的便于记忆的数据。用于应用选择。 存在于 ADF的 FCI 中（可选）和
            // ADF目录入口中（必备）
            + "9F12:应用首选名称;"//
            + "5A:应用主账号(PAN);"//
            + "5F34:应用主账号序列号(CSN);"//
            + "87:应用优先指示器;"//
            + "61:应用模板;"//
            + "9F36:应用交易计数器(ATC);"//
            + "9F07:应用用途控制;"//
            + "9F08:应用版本号;"//
            + "8A:授权响应码;"//
            + "8C:卡片风险管理数据对象列表1(CDOL1);"//
            + "8D:卡片风险管理数据对象列表2(CDOL2);"//
            + "5F20:持卡人姓名;"//
            + "9F0B:持卡人姓名扩展;"//
            + "9F61:持卡人证件号;"//
            + "9F62:持卡人证件类型;"//
            + "8E:持卡人验证方法(CVM)列表;"//
            + "8F:CA公钥索引(PKI);"//
            + "9F53:连续脱机交易限制数(国际-货币);"//
            + "9F72:连续脱机交易限制数(国际-国家);"//
            + "9F27:应用信息数据;"//
            + "9F54:累计脱机交易金额限制数;"//
            + "9F75:累计脱机交易金额限制数(双货币);"//
            + "9F5C:累计脱机交易金额上限;"//
            + "9F73:货币转换因子;"//
            + "9F45:数据认证码;"//
            + "84:专用文件(DF)名称;"//
            + "73:目录自定义模板;" + "9F49:动态数据认证数据对象列表(DDOL);"//
            + "BF0C:文件控制信息(FCI)发卡行自定义数据;"//
            + "A5:文件控制信息(FCI)专用模板;"//
            + "6F:文件控制信息(FCI)模板;"//
            + "9F4C:IC动态数;"//
            + "9F47:IC卡RSA公钥指数;"//
            + "9F46:IC卡公钥证书;"//
            + "9F48:IC卡RSA公钥余数;"//
            + "9F0D:发卡行行为代码(IAC)-缺省;"//
            + "9F0E:发卡行行为代码(IAC)-拒绝;"//
            + "9F0F:发卡行行为代码(IAC)-联机;"//
            + "9F10:发卡行应用数据(IAD);"// qPBOC P-64
            + "91:发卡行认证数据;"//
            + "9F56:发卡行认证指示位;"//
            + "9F11:发卡行代码表索引;"//
            + "5F28:发卡行国家代码;"//
            + "9F57:发卡行国家代码;"//
            + "90:发卡行公钥证书;"//
            + "9F32:发卡行RSA公钥指数;"//
            + "92:发卡行RSA公钥余数;"//
            + "86:发卡行脚本命令;"//
            + "72:发卡行脚本模板2;"//
            + "5F50:发卡行URL;"//
            + "9F5A:发卡行URL2;"//
            + "5F2D:首选语言;"//
            + "9F13:上次联机应用交易计数器(ATC)寄存器;"//
            + "9F4D:交易日志入口;"//
            + "9F4F:交易日志格式;"//
            + "9F14:连续脱机交易下限;"//
            + "9F58:连续脱机交易下限;"//
            + "9F66:终端交易属性;"// 字节1:[7]支持非接触式借记/贷记应用; [6]支持 qPBOC;
            // [5]支持接触式借记/贷记应用;[4]终端仅支持脱机;[3]支持联机 PIN;[2]支持签名;
            // 字节2:[8]要求联机密文;[7]要求 CVM;
            // 字节4:[8]终端支持“01”版本的 fDDA
            + "9F17:PIN尝试计数器;"//
            + "9F38:处理选项数据对象列表(PDOL);"//
            + "80:响应报文模板格式1;"//
            + "77:响应报文模板格式2;"//
            + "9F76:第2应用货币代码;"//
            + "5F30:服务码;"//
            + "88:短文件标识符(SFI);"//
            + "9F4B:签名的动态应用数据;"//
            + "93:签名的静态应用数据(SAD);"//
            + "9F4A:静态数据认证标签列表;"//
            + "9F1F:磁条1自定义数据;"//
            + "57:磁条2等效数据;"// PAN和失效时间可以从这取得
            + "97:交易证书数据对象列表(TDOL);"//
            + "9F23:连续脱机交易上限;"//
            + "9F59:连续脱机交易上限;"//
            + "9F63:产品标识信息;"// 当终端能够获取卡标识信息时，本域出现；否则，本域不出现
            + "DF69:SM2算法支持指示器;"//
            + "70:响应报文的数据域;"//
            + "DF32:芯片序列号;"//
            + "DF33:过程密钥数据;"//
            + "DF34:终端读取时间;"//
            + "EFA0:自定义输入方式;"//
            + "";//

    private final static String DIR = "6F,A5,BF0C,70,61,77";
    private final static String LEN1 = "6F,4F";

    private final static HashSet<Integer> dirSet = new HashSet<Integer>();
    private final static HashSet<Integer> len1Set = new HashSet<Integer>();

    static {
        for (String s : DIR.split(",")) {
            int n = Integer.parseInt(s, 16);
            dirSet.add(n);
        }
        for (String s : LEN1.split(",")) {
            int n = Integer.parseInt(s, 16);
            len1Set.add(n);
        }
    }

    public String name;
    public TLV[] subs;

    public static byte[] findByTag(byte[] bs, int tag) {
        return findByTag(bs, 0, bs.length, tag);
    }

    public static byte[] findByTag(byte[] bs, int pos, int len, int tag) {
        int end = pos + len;
        while (pos < end) {
            int t = bs[pos++] & 0xFF;
            if ((t & 0xF) == 0xF && !len1Set.contains(t)) {
                // tag占用2字节
                t <<= 8;
                if (pos + 1 < end) {
                    t |= bs[pos++] & 0xFF;
                } else {
                    return null;
                }
            }
            // 再取得长度
            int tlen = 0;
            if (pos < end - 1) {
                tlen = bs[pos++] & 0xFF;
            } else {
                return null;
            }
            if ((tlen & 0x80) != 0) {
                int lenCount = tlen & 0x7F;
                tlen = 0;
                for (int i = 0; i < lenCount; i++) {
                    tlen <<= 8;
                    if (pos < end - 1) {
                        tlen |= bs[pos++] & 0xFF;
                    } else {
                        return null;
                    }
                }
            }
            if (pos + tlen > end) {
                // TAG体超出范围
                return null;
            }
            if (t == tag) {
                return Arrays.copyOfRange(bs, pos, pos + tlen);
            }
            if (dirSet.contains(t)) {
                byte[] r = findByTag(bs, pos, end - pos, tag);
                if (r != null) {
                    return r;
                }
            } else {
                // 跳过长度
                pos += tlen;
            }
        }
        return null;
    }

    private final static char[] CS = "0123456789ABCDEF".toCharArray();

    private static void appendByte(StringBuilder sb, int n) {
        sb.append(CS[(n >> 4) & 0xF]);
        sb.append(CS[(n >> 0) & 0xF]);
    }

    private static void appendBytes(StringBuilder sb, byte[] bs, int p, int len) {
        for (int i = p; i < p + len; i++) {
            int n = bs[i];
            sb.append(CS[(n >> 4) & 0xF]);
            sb.append(CS[(n >> 0) & 0xF]);
        }
    }

    public static String toString(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        toString(bs, 0, bs.length, sb, "");
        return sb.toString();
    }

    public static void anaTag(byte[] bs, HashMap<String, String> map) {
        TLV.toString(bs);
        anaTag(bs, 0, bs.length, map);
    }

    public static void decodeBF0C(byte[] bs, HashMap<String, String> map) {
        String p4F = null;
        String p50 = null;
        int p87 = 0xFFFF;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == 0x61) {
                int len = bs[i + 1] & 0xFF;
                byte[] ts = Arrays.copyOfRange(bs, i + 2, i + 2 + len);
                byte[] fs = findByTag(ts, 0x87);
                if (fs == null || fs.length <= 0 || (fs[0] & 0xFF) < p87) {
                    if (fs == null || fs.length <= 0) {
                        p87 = 0;
                    } else {
                        p87 = fs[0] & 0xFF;
                    }
                    byte[] tbs = findByTag(bs, 0x4F);
                    if (tbs != null) {
                        p4F = HEX.bytesToHex(tbs);
                    }
                    tbs = findByTag(bs, 0x50);
                    if (tbs != null) {
                        p50 = HEX.bytesToHex(tbs);
                    }
                }

            } else {
                break;
            }
        }
        if (p4F != null) {
            map.put("4F", p4F);
        }
        if (p50 != null) {
            map.put("50", p50);
        }
    }

    public static void anaTag(byte[] bs, int pos, int len, Map<String, String> map) {
        int end = pos + len;
        while (pos < end) {
            int t = bs[pos++] & 0xFF;
            if ((t & 0xF) == 0xF && !len1Set.contains(t)) {
                // tag占用2字节
                t <<= 8;
                if (pos + 1 < end) {
                    t |= bs[pos++] & 0xFF;
                } else {
                    return;
                }
            }
            String tag = Integer.toHexString(t).toUpperCase(Locale.getDefault());
            // 再取得长度
            int tlen = 0;
            // if (pos < end - 1) { //TODO houj 最后一个tag长度为0就漏掉了
            if (pos < end) {
                tlen = bs[pos++] & 0xFF;
            } else {
                return;
            }
            if ((tlen & 0x80) != 0) {
                int lenCount = tlen & 0x7F;
                tlen = 0;
                for (int i = 0; i < lenCount; i++) {
                    tlen <<= 8;
                    if (pos < end - 1) {
                        tlen |= bs[pos++] & 0xFF;
                    } else {
                        return;
                    }
                }
            }
            if (pos + tlen > end) {
                // TAG体超出范围
                return;
            }
            if (dirSet.contains(t)) {
                // anaTag(bs, pos, end - pos, map);
                map.put(tag, HEX.bytesToHex(bs, pos, tlen));
            } else {
                // 跳过长度
                map.put(tag, HEX.bytesToHex(bs, pos, tlen));
                pos += tlen;
            }

        }
    }

    private final static HashMap<String, String> nameMap = new HashMap<String, String>();
    static {
        for (String s : NAMES.split(";")) {
            int i = s.indexOf(':');
            if (i > 0) {
                nameMap.put(s.substring(0, i), s.substring(i + 1));
            }
        }
    }

    public static void toString(byte[] bs, int pos, int len, StringBuilder sb, String pre) {
        int end = pos + len;
        while (pos < end) {
            sb.append(pre);
            int t = bs[pos++] & 0xFF;
            int p = sb.length();
            appendByte(sb, t);
            if ((t & 0xF) == 0xF && !len1Set.contains(t)) {
                // tag占用2字节
                t <<= 8;
                if (pos + 1 < end) {
                    t |= bs[pos++] & 0xFF;
                    appendByte(sb, t);
                } else {
                    return;
                }
            }
            String name = sb.substring(p);
            System.out.println(name);
            sb.append('[').append(nameMap.get(name)).append(']');
            sb.append(":");
            // 再取得长度
            int tlen = 0;
            if (pos < end - 1) {
                tlen = bs[pos++] & 0xFF;
            } else {
                return;
            }
            if ((tlen & 0x80) != 0) {
                int lenCount = tlen & 0x7F;
                tlen = 0;
                for (int i = 0; i < lenCount; i++) {
                    tlen <<= 8;
                    if (pos < end - 1) {
                        tlen |= bs[pos++] & 0xFF;
                    } else {
                        return;
                    }
                }
            }
            if (pos + tlen > end) {
                // TAG体超出范围
                return;
            }
            if (dirSet.contains(t)) {
                sb.append("\n");
                toString(bs, pos, end - pos, sb, pre + "  ");
            } else {
                // 跳过长度
                appendBytes(sb, bs, pos, tlen);
                sb.append("\n");
            }
            pos += tlen;
        }
    }

    public static class DolItem {
        public String tag;
        public int len;

        public String toString() {
            return tag + "[" + len + "]";
        }
    }

    public static DolItem[] decodeDOL(byte[] bs) {
        return decodeDOL(bs, 0, bs.length);
    }

    /**
     * 解码DOL
     *
     * @param bs
     * @param pos
     * @param len
     * @return
     */
    public static DolItem[] decodeDOL(byte[] bs, int pos, int len) {
        ArrayList<DolItem> r = new ArrayList<DolItem>();
        int end = pos + len;
        while (pos < end) {
            int t = bs[pos++] & 0xFF;
            if ((t & 0xF) == 0xF && !len1Set.contains(t)) {
                // tag占用2字节
                t <<= 8;
                if (pos < end) {
                    t |= bs[pos++] & 0xFF;
                } else {
                    break;
                }
            }

            // 再取得长度
            int tlen = 0;
            if (pos < end) {
                tlen = bs[pos++] & 0xFF;
            } else {
                break;
            }
            DolItem it = new DolItem();
            it.tag = Integer.toHexString(t).toUpperCase(Locale.getDefault());
            it.len = tlen;
            r.add(it);
        }
        return r.toArray(new DolItem[0]);
    }

    public static boolean haveAllTag(HashMap<String, String> map, String tags) {
        String[] ss = tags.split(",");
        boolean r = true;
        for (String s : ss) {
            if (s.length() <= 0) {
                continue;
            }
            if (map.get(s) == null) {
                System.out.println("all: not found:" + s + " " + nameMap.get(s));
                r = false;
            }
        }
        if (r) {
            System.out.println("===============all have");
        }
        return r;
    }

    private static void appendV(StringBuilder sb, int v) {
        sb.append(CS[(v >> 4) & 0xF]);
        sb.append(CS[(v >> 0) & 0xF]);
    }

    public static String pack(HashMap<String, String> map, String tags) {
        String[] ss = tags.split(",");
        StringBuilder sb = new StringBuilder();
        for (String s : ss) {
            String v = map.get(s);
            if (v == null) {
                System.out.println("not fount tag:" + s + " " + nameMap.get(s));
            } else {
                sb.append(s);
                int len = v.length() / 2;
                if (len >= 0x80) {
                    //FIX  houj  修复长度大于一字节的情况
                    int count = 0;
                    if (len > 0xFF) {
                        count = 2;
                    } else {
                        count = 1;
                    }
                    appendV(sb, 0x80 | count);
                    while (--count >= 0) {
                        appendV(sb, len >> (count * 8));
                    }
                } else {
                    sb.append(CS[(len >> 4) & 0xF]);
                    sb.append(CS[(len >> 0) & 0xF]);
                }
                sb.append(v);
                System.out.println(s + ":" + v + " " + nameMap.get(s));
            }
        }
        return sb.toString();
    }

    private final static String Z0 = "00000000000000000000000000000000000000000000000000000000000";

    public static String makePol(DolItem[] pols, HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (DolItem d : pols) {
            String r = map.get(d.tag);
            System.out.println("POL:" + d.tag + " " + r);
            if (r == null) {
                System.out.println("makePol=====unknow tag=:" + d.tag);
                r = (Z0.substring(0, d.len * 2));
                map.put(d.tag, r);
            }
            if (r.length() != d.len * 2) {
                throw new RuntimeException("tag len err:" + d.len + " " + r);
            }
            System.out.println(d);
            sb.append(r);
            map.put(d.tag, r);
        }
        return sb.toString();
    }

    // private final static byte[] BS = { -4, -91, 6, -109, -92, -30, 79, -79,
    // -84, -49, -58, -21, 7, 100, 121, 37, 0, 10, 0, 9, 0 };
    //
    // static byte[] getMac(byte[] bs, byte[] key) throws Exception {
    // byte[] rs = new byte[8];
    // for (int i = 0; i < bs.length; i += 8) {
    // for (int p = 0; p < 8 && p + i < bs.length; p++) {
    // rs[p] ^= bs[i + p];
    // }
    // rs = DES.des3encrypt(key, rs);
    // }
    // return Arrays.copyOf(rs, 4);
    // }

    private static void printBitInfo(String s, String[] ss) {
        byte[] bs = HEX.hexToBytes(s);
        int len = Math.min(bs.length * 8, ss.length);
        for (int i = 0; i < len; i++) {
            if ((bs[i >> 3] & (1 << (i & 7))) != 0) {
                System.out.println(i + ":" + ss[i]);
            }
        }
    }

    // 6-P83 5 A.5 终端验证结果 (TVR)
    private final static String[] TVR = { "", "", "复合动态数据认证/应用密文生成失败", "脱机动态数据认证失败", "卡片出现在终端异常文件中", "IC 卡数据缺失", "脱机静态数据认证失败", "未进行脱机数据认证",//
        "", "", "", "新卡", "卡片不允许所请求的服务", "应用尚未生效", "应用已过期", "IC 卡和终端应用版本不一致",//
        "", "", "输入联机 PIN", "要求输入 PIN，密码键盘存在，但未输入 PIN", "要求输入 PIN，但密码键盘不存在或工作不正常", "PIN 重试次数超限", "未知的 CVM", "持卡人验证失败",//
        "", "", "", "商户要求联机交易", "交易被随机选择联机处理", "超过连续脱机交易上限", "超过连续脱机交易下限", "交易超过最低限额",//
        "", "", "", "", "最后一次 GENERATE AC 命令之后脚本处理失败", "最后一次 GENERATE AC 命令之前脚本处理失败", "发卡行认证失败", "使用缺省 TDOL",//
    };

    public static void printTVR(String s) {
        printBitInfo(s, TVR);
    }

    private final static String[] AIP_INFO = { "支持CDA—不支持", "", "支持发卡行认证—支持", "执行终端风险管理—支持", "支持持卡人认证—支持", "支持DDA—支持", "支持SDA—支持", "" };

    public static void printAIP(String ns) {
        printBitInfo(ns, AIP_INFO);
    }

    private final static String[] XINGNENG_INFO = {//
        "", "", "", "", "", "接触式IC卡", "磁条", "手工键盘输入",//
        "持卡人证件验证", "", "", "无需CVM", "", "签名（纸）", "加密 PIN联机验证", "IC卡明文PIN验证",//
        "", "", "", "复合动态数据认证/应用密文生成（CDA）", "", "吞卡", "动态数据认证（DDA）", "静态数据认证（SDA",//
    };

    /**
     * 终端性能 9F33
     *
     * @param ns
     */
    public static void printXinneng(String ns) {
        printBitInfo(ns, XINGNENG_INFO);
    }

    public static void printCvmList(String s) {
        StringBuilder sb = new StringBuilder();
        String x = s.substring(0, 8);
        String y = s.substring(8, 16);
        sb.append(" X:" + x);
        sb.append(" Y:" + y + "\n");
        int io = 0;
        for (int i = 16; i < s.length() - 3; i += 4) {
            int need = Math.min(4, s.length() - i);
            String sub = s.substring(i, i + need);
            sb.append(io + ":" + sub + " ");
            byte[] bs = HEX.hexToBytes(sub);
            if ((bs[0] & (1 << 7)) != 0) {
                sb.append("[如果此 CVM 失败，应用后续的]");
            } else {
                sb.append("[如果此 CVM 失败，则持卡人验证失败]");
            }
            String type = Integer.toBinaryString((bs[0] & 0xFF) | (0xC0));
            type = type.substring(2);
            if ("000000".equals(type)) {
                sb.append("[CVM 失败处理]");
            } else if ("000001".equals(type)) {
                sb.append("[卡片执行明文 PIN 核对]");
            } else if ("000010".equals(type)) {
                sb.append("[联机加密 PIN 验证]");
            } else if ("000011".equals(type)) {
                sb.append("[卡片执行明文 PIN 核对+签名纸上]");
            } else if ("000100".equals(type)) {
                sb.append("[保留]");
            } else if ("000101".equals(type)) {
                sb.append("[保留]");
            } else if ("011110".equals(type)) {
                sb.append("[签名（纸上）]");
            } else if ("011111".equals(type)) {
                sb.append("[无需 CVM]");
            } else if (type.compareTo("000110") >= 0 && type.compareTo("011101") <= 0) {
                sb.append("[保留给加入的支付系统]");
            } else if (type.compareTo("100000") >= 0 && type.compareTo("101111") <= 0) {
                sb.append("[保留给各自独立的支付系统]");
            } else if (type.compareTo("110000") >= 0 && type.compareTo("111110") <= 0) {
                sb.append("[保留给发卡行]");
            } else if ("111111".equals(type)) {
                sb.append("[RFU]");
            } else if ("100000".equals(type)) {
                sb.append("[持卡人证件出示]");
            }

            String condition = sub.substring(2);
            if ("00".equals(condition)) {
                sb.append("[总是]");
            } else if ("01".equals(condition)) {
                sb.append("[如果是 ATM 现金交易]");
            } else if ("02".equals(condition)) {
                sb.append("[如果不是 ATM 现金或有人值守现金或返现交易]");
            } else if ("03".equals(condition)) {
                sb.append("[如果终端支持这个 CVM]");
            } else if ("04".equals(condition)) {
                sb.append("[如果是人工值守现金交易]");
            } else if ("05".equals(condition)) {
                sb.append("[如果是返现交易]");
            } else if ("06".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且小于 X值]");
            } else if ("07".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且大于 X值]");
            } else if ("08".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且小于 Y值]");
            } else if ("09".equals(condition)) {
                sb.append("[如果交易货币等于应用货币代码而且大于 Y值]");
            } else if (condition.compareTo("0A") >= 0 && condition.compareTo("7F") <= 0) {
                sb.append("[RFU]");
            } else if (condition.compareTo("80") >= 0 && condition.compareTo("FF") <= 0) {
                sb.append("[保留给各个支付系统]");
            } else {
                sb.append("[未知]");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    /**
     * 取得联机pin正确的 CVM结果
     *
     * @param s
     * @return
     */
    public static String makeLinkPinOK(String s) {
        s = s.substring(16);
        for (int i = 0; i < s.length() - 3; i += 4) {
            String sub = s.substring(i, i + 4);
            byte[] bs = HEX.hexToBytes(sub);
            if ((bs[0] & 0x3F) == 2) {
                // 联机加密PIN验证
                return sub + "00"; // 未知6-P82
            }
        }
        return "3F0000";
    }

    /**
     *
     * @param f55
     *            源55
     * @param add
     *            添加项 "84,9F09,9F41,9F63"
     * @return 添加后的
     */
    private static String fixF55(String f55, String add) {
        int i = f55.indexOf(',');
        if (i < 0) {
            return f55;
        }
        String pre = f55.substring(0, i);
        String end = f55.substring(i + 1);

        HashMap<String, String> mapPre = new HashMap<String, String>();
        HashMap<String, String> mapEnd = new HashMap<String, String>();
        TLV.anaTag(HEX.hexToBytes(pre), mapPre);
        TLV.anaTag(HEX.hexToBytes(end), mapEnd);

        StringBuilder sb = new StringBuilder();
        for (String s : add.split(",")) {
            String v = mapEnd.get(s);
            if (v != null && !mapPre.containsKey(s)) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }
        }
        String radd = TLV.pack(mapEnd, sb.toString());
        return pre + radd + "," + end;
    }

    public static void main(String[] args) {

        // printAIP("7C00");

        // // TLV.printAIP("0040");
        //
        // // 无效交易
        // // String
        // //
        // s="9F2608359F8C2A7B342AAD9F2701809F10160706A203206E00010DA10308000000001512070900809F3704145786D49F36020009950500000000009A031512079C01009F02060000000000005F2A020156820200409F1A0201569F03060000000000009F33034060009F3501219F1E0833303031353635399F631030333038303030300020040000000000";
        // // 无效交易2 招行
        // // String
        // //
        // s="9F260896D72F3DD15BF3C29F2701809F10160706A203206E00010DA10308000000011512071600809F370498C656E39F36020011950500C00408009A031512089C01009F02060000000000005F2A020156820200409F1A0201569F03060000000000009F33034060009F3501219F1E0833303031353635399F631030333038303030300020040000000000";
        // // 借记卡 OK 民生银行
        // String s =
        // "9F2608C502F35571810BDC9F2701809F10160706A203206E00010DA10305000000031512031700809F37046B9FEEF69F36020026950500000000009A031512089C01009F02060000000000005F2A020156820200409F1A0201569F03060000000000009F33034060009F3501219F1E0833303031353635399F631030333035303030300020040000000000";
        // // 信用卡OK
        // // String
        // //
        // s="9F2608DE05157B25B47B4A9F2701809F10160708A203A00000010DA10000000000021512080900809F370493A9AA4A9F36020007950500000000009A031512089C01009F02060000000000005F2A02015682027C009F1A0201569F03060000000000009F33034060009F3501219F1E083330303135363539";
        // byte[] bs = HEX.hexToBytes(s);
        //
        // String r = toString(bs);
        // System.out.println(r);

        // PcSmartCard.printF55("9F2608EF600AC4CB46BF039F2701809F10160706A203206E00010DA10308000000081512091600809F370490788BDD9F3602005A950500000000009A031512099C01009F02060000000000505F2A020156820200409F1A0201569F03060000000000009F3303E0E1C89F3501219F1E0833303031353635399F6310303330383030303000200400000000008408A0000003330101019F09020020");

        // HashMap<String, String> map = new HashMap();
        // byte[] bs =
        // HEX.hexToBytes("9F26084EE0A7F9E31922B19F2701809F101307020103A0A000010A0100000000008F7630A39F37047890230A9F36020029950500000408009A031512189C01009F02060000000000015F2A02015682027C009F1A0201569F03060000000000009F33034060009F34034203009F3501219F1E0C313331303030303030363031");
        // TLV.anaTag(bs, map);
        // System.out.println(map);
        //

        // String f55 =
        // "9F2608012FBABC361078669F2701809F101307021703A00000010A010000000000770518049F37042CCD18059F36020017950500000000009A031601139C01009F02060000000002005F2A02015682027C009F1A0201569F03060000000000009F33036068009F3501229F1E0833303032313539338408A0000003330101029F09020020";
        // System.out.println(f55);
        // System.out.println(fixF55(f55, "84,9F09,9F41,9F63"));

    }


    private byte[] data;
    private String tag;
    private int length = -1;
    private byte[] value;

    public static TLV fromRawData(byte[] tlData, int tlOffset, byte[] vData, int vOffset)
    {
        int tLen = getTLength(tlData, tlOffset);
        int lLen = getLLength(tlData, tlOffset + tLen);
        int vLen = calcValueLength(tlData, tlOffset + tLen, lLen);
        TLV d = new TLV();
        d.data = ByteUtil.merage(new byte[][] { ByteUtil.subBytes(tlData, tlOffset, tLen + lLen), ByteUtil.subBytes(vData, vOffset, vLen) });
        d.getTag();
        d.getLength();
        d.getBytesValue();

        return d;
    }

    public static TLV fromData(String tagName, byte[] value) {
        byte[] tag = ByteUtil.hexString2Bytes(tagName);
        TLV d = new TLV();
        d.data = ByteUtil.merage(new byte[][] { tag, makeLengthData(value.length), value });
        d.tag = tagName;
        d.length = value.length;
        d.value = value;
        return d;
    }

    public static TLV fromRawData(byte[] data, int offset) {
        int len = getDataLength(data, offset);
        TLV d = new TLV();
        d.data = ByteUtil.subBytes(data, offset, len);
        d.getTag();
        d.getLength();
        d.getBytesValue();
        return d;
    }

    public String getTag() {
        if (this.tag != null) {
            return this.tag;
        }
        int tLen = getTLength(this.data, 0);
        return this.tag = ByteUtil.bytes2HexString(ByteUtil.subBytes(this.data, 0, tLen));
    }

    public int getLength() {
        if (this.length > -1) {
            return this.length;
        }
        int offset = getTLength(this.data, 0);
        int l = getLLength(this.data, offset);
        if (l == 1) {
            return this.data[offset] & 0xFF;
        }

        int afterLen = 0;
        for (int i = 1; i < l; i++) {
            afterLen <<= 8;
            afterLen |= this.data[(offset + i)] & 0xFF;
        }
        return this.length = afterLen;
    }

    public int getTLLength() {
        if (this.data == null) {
            return -1;
        }
        return this.data.length - getBytesValue().length;
    }

    public String getValue() {
        byte[] temp = getBytesValue();
        return ByteUtil.bytes2HexString(temp == null ? new byte[0] : temp);
    }

    public byte getByteValue() {
        return getBytesValue()[0];
    }

    public String getGBKValue() {
        try {
            return new String(getBytesValue(), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNumberValue() {
        String num = getValue();
        return String.valueOf(Integer.parseInt(num));
    }

    public byte[] getGBKNumberValue() {
        try {
            return getNumberValue().getBytes("GBK");
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
        }
        return null;
    }

    public byte[] getBCDValue() {
        return ByteUtil.hexString2Bytes(getGBKValue());
    }

    public byte[] getRawData() {
        return this.data;
    }

    public byte[] getBytesValue() {
        if (this.value != null) {
            return this.value;
        }
        int l = getLength();
        return this.value = ByteUtil.subBytes(this.data, this.data.length - l, l);
    }

    public boolean isValid() {
        return this.data != null;
    }

    private static int getTLength(byte[] data, int offset) {
        if ((data[offset] & 0x1F) == 31) {
            return 2;
        }
        return 1;
    }

    private static int getLLength(byte[] data, int offset) {
        if ((data[offset] & 0x80) == 0) {
            return 1;
        }
        return (data[offset] & 0x7F) + 1;
    }

    private static int getDataLength(byte[] data, int offset) {
        int tLen = getTLength(data, offset);
        int lLen = getLLength(data, offset + tLen);
        int vLen = calcValueLength(data, offset + tLen, lLen);
        return tLen + lLen + vLen;
    }

    private static int calcValueLength(byte[] l, int offset, int lLen) {
        if (lLen == 1) {
            return l[offset] & 0xFF;
        }

        int vLen = 0;
        for (int i = 1; i < lLen; i++) {
            vLen <<= 8;
            vLen |= l[(offset + i)] & 0xFF;
        }
        return vLen;
    }

    private static byte[] makeLengthData(int len) {
        if (len > 127) {
            byte[] lenData = new byte[4];
            int validIndex = -1;
            for (int i = 0; i < lenData.length; i++) {
                lenData[i] = ((byte)(len >> 8 * (3 - i) & 0xF));
                if ((lenData[(3 - i)] != 0) && (validIndex < 0)) {
                    validIndex = i;
                }
            }
            lenData = ByteUtil.subBytes(lenData, validIndex, -1);
            lenData = ByteUtil.merage(new byte[][] { { (byte)(0x80 & lenData.length) }, lenData });
            return lenData;
        }

        return new byte[] { (byte)len };
    }

    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof TLV)) {
            return false;
        }

        if ((this.data == null) || (((TLV)obj).data == null)) {
            return false;
        }

        return Arrays.equals(this.data, ((TLV)obj).data);
    }

    public String toString()
    {
        if (this.data == null) {
            return super.toString();
        }
        return ByteUtil.bytes2HexString(this.data);
    }
}

