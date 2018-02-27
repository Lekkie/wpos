package com.avantir.wpos.utils;

import com.avantir.wpos.model.NibssTLV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lekanomotayo on 21/02/2018.
 */
public class NibssTLVUtil {

    public static HashMap<String, String>nibssEMVCAPKTagMap = new HashMap<>();
    public static HashMap<String, String>nibssEMVAIDTagMap = new HashMap<>();

    static{
        /*
        32 Certificate Authority (CA) Key Index
        33 CA Key Internal Reference Number
        34 CA Key Name
        35 EMV RID
        36 Hash Algorithm
        37 EMV CA PK Modulus
        38 EMV CA PK Exponent
        39 EMV CA PK Hash
         */
        nibssEMVCAPKTagMap.put("32", "9F22"); // CA Public Key Index
        nibssEMVCAPKTagMap.put("35", "9F06"); //rid
        nibssEMVCAPKTagMap.put("36", "DF06"); //CA Public Key Hash algorithm identification
        nibssEMVCAPKTagMap.put("37", "DF02"); // CA Public Key modulus
        nibssEMVCAPKTagMap.put("38", "DF04"); //CA Public Key exponent
        nibssEMVCAPKTagMap.put("39", "DF03"); //CA Public Key Check value or Hash
        /*
        nibssEMVCAPKTagMap.put("33", "");
        nibssEMVCAPKTagMap.put("34", "");
        nibssEMVCAPKTagMap.put("XX", "DF05"); // CA Public Key period of validity
        nibssEMVCAPKTagMap.put("XX", "DF07"); // CA Public Key Algorithm identification
        */

        /*
            13 AID Index
            14 Application Internal Reference Number
            15 Application Identification Number (EMV AID)
            16 Match 001
            17 EMV Application Name
            18 EMV Application Version
            19 EMV Application Selection Priority
            20 EMV DDOL
            21 EMV TDOL
            22 EMV TFL Domestic
            23 EMV TFL International
            24 EMV Offline Threshold Domestic
            25 EMV Max Target Domestic
            26 EMV Max Target International
            27 EMV Target Percentage Domestic
            28 EMV Target Percentage International
            29 Default EMV TAC Value
            30 EMV TAC Denial
            31 EMV TAC Online
             */
        nibssEMVAIDTagMap.put("13", "9F06"); //aid
        nibssEMVAIDTagMap.put("15", "DF01"); // Application ID
        nibssEMVAIDTagMap.put("17", "9F12"); // Application Name
        nibssEMVAIDTagMap.put("18", "9F09"); // version
        nibssEMVAIDTagMap.put("20", "DF14"); // default DDOL
        nibssEMVAIDTagMap.put("22", "9F7B"); // terminal transaction floor limit
        nibssEMVAIDTagMap.put("24", "DF15"); // Offset randomly selected thresholds
        nibssEMVAIDTagMap.put("25", "DF16"); // Bias randomly selects the maximum target percentage (Max Target Domestic)
        nibssEMVAIDTagMap.put("27", "DF17"); // randomly selected target percentage (EMV Target Percentage Domestic)
        nibssEMVAIDTagMap.put("29", "DF11"); //TAC - default
        nibssEMVAIDTagMap.put("30", "DF13"); // TAC - denial
        nibssEMVAIDTagMap.put("31", "DF12"); // TAC - online
        /*
        nibssEMVAIDTagMap.put("14", ""); // internal ref number
        nibssEMVAIDTagMap.put("16", ""); // Match
        nibssEMVAIDTagMap.put("19", ""); // selection priority
        nibssEMVAIDTagMap.put("21", ""); // default TDOL
        nibssEMVAIDTagMap.put("23", ""); // floor limit intl
        nibssEMVAIDTagMap.put("26", ""); // max target intl
        nibssEMVAIDTagMap.put("28", ""); // target percentage intl
        nibssEMVAIDTagMap.put("", "9F1B");// minimum amount
        nibssEMVAIDTagMap.put("", "DF18");
        nibssEMVAIDTagMap.put("", "DF19"); // Non-contact reader offline minimum
        nibssEMVAIDTagMap.put("", "DF20"); // non-contact reader transaction limit
        nibssEMVAIDTagMap.put("", "DF21");// non-contact terminal CVM quota
        */

    }


    public static List<List<NibssTLV>> parseTLVData(String tlvData){

        List<List<NibssTLV>> tlvListList = new ArrayList<>();
        List<NibssTLV> tlvList = new ArrayList<>();
        try{
            String tmp = tlvData;
            while(tmp.length() > 0){
                String tag = tmp.substring(0, 2);
                tmp = tmp.substring(2);
                int len = Integer.parseInt(tmp.substring(0, 3));
                tmp = tmp.substring(3);
                String value = tmp.substring(0, len);
                tmp = tmp.substring(len);
                NibssTLV tlv = new NibssTLV(tag, len, value);
                tlvList.add(tlv);
                int tmpLen = tmp.length();
                String delim = tmpLen > 0 ? tmp.substring(0, 1) : "";
                if(delim.equalsIgnoreCase("~") || tmpLen == 0) {
                    tlvListList.add(tlvList);
                    tmp = tmpLen > 0 ? tmp.substring(1) : tmp;
                    tlvList = new ArrayList<>();
                }
            }
        }
        catch(Exception ex){
         ex.printStackTrace();
        }
        return tlvListList;
    }


    public static String[] getCAPKAsStringArray(List<List<NibssTLV>> capkTlvListList){
        int i = 0;
        String capkStrArr[] = new String[capkTlvListList.size()];
        for(List<NibssTLV> capkTlvList: capkTlvListList){
            String capkStr = "";
            for(NibssTLV capkTlv: capkTlvList){
                if(nibssEMVCAPKTagMap.containsKey(capkTlv.getTag())){
                    String emvTag = nibssEMVCAPKTagMap.get(capkTlv.getTag());
                    String emvTagVal = capkTlv.getValue();
                    if((emvTagVal.length() % 2) != 0){
                        if(emvTagVal.matches("^[0-9a-fA-F]+$"))
                            emvTagVal = "0" + emvTagVal;
                        else
                            emvTagVal = emvTagVal + " ";
                    }
                    String emvTagLen = getTagLen(emvTagVal);
                    capkStr = capkStr + emvTag + emvTagLen + emvTagVal;
                }
            }
            capkStrArr[i++] = capkStr;
        }
        return capkStrArr;
    }

    public static String[] getAIDAsStringArray(List<List<NibssTLV>> aidTlvListList){
        int i = 0;
        String aidStrArr[] = new String[aidTlvListList.size()];
        for(List<NibssTLV> aidTlvList: aidTlvListList){
            String aidStr = "";
            for(NibssTLV aidTlv: aidTlvList){
                if(nibssEMVAIDTagMap.containsKey(aidTlv.getTag())){
                    String emvTag = nibssEMVAIDTagMap.get(aidTlv.getTag());
                    String emvTagVal = aidTlv.getValue();
                    if((emvTagVal.length() % 2) != 0){
                        if(emvTagVal.matches("^[0-9a-fA-F]+$"))
                            emvTagVal = "0" + emvTagVal;
                        else
                            emvTagVal = emvTagVal + " ";
                    }
                    String emvTagLen = getTagLen(emvTagVal);
                    aidStr = aidStr + emvTag + emvTagLen + emvTagVal;
                }
            }
            aidStrArr[i++] = aidStr;
        }
        return aidStrArr;
    }




    public static String getTagLen(String val){
        int tagLenInt = val.length() / 2;
        if((val.length() % 2) != 0)
            tagLenInt = (val.length() / 2) + 1;

        String tagLenHexStr  = Integer.toHexString(tagLenInt).toUpperCase();
        if(tagLenInt <= 0x7F)
            return tagLenHexStr.length() == 2 ? tagLenHexStr : ("0" + tagLenHexStr);

        if(tagLenInt <= 0xFF)
            return tagLenHexStr.length() == 2 ? ("81" + tagLenHexStr) : ("810" + tagLenHexStr);

        if(tagLenInt <= 0xFFFF)
            return tagLenHexStr.length() == 4 ? ("82" + tagLenHexStr) : ("820" + tagLenHexStr);

        if(tagLenInt <= 0xFFFFFF)
            return tagLenHexStr.length() == 6 ? ("83" + tagLenHexStr) : ("830" + tagLenHexStr);

        if(tagLenInt <= 0xFFFFFFFF)
            return tagLenHexStr.length() == 8 ? ("84" + tagLenHexStr) : ("840" + tagLenHexStr);

        return null;
    }

    public static void main(String[] args){
        try{

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
