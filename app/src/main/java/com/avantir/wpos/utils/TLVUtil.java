package com.avantir.wpos.utils;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import sdk4.wangpos.libemvbinder.CAPK;
import sdk4.wangpos.libemvbinder.EmvAppList;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lekanomotayo on 07/02/2018.
 */
public class TLVUtil {


    public static CAPK getCAPK(Context context, String capkStr){

        CAPK capk = new CAPK(context);
        TLVList tlvList = TLVList.fromBinary(capkStr);
        try {

            capk.setRID(tlvList.getTLV("9F06").getValue());// rid
            capk.setKeyID(tlvList.getTLV("9F22").getValue());//认证中心公钥索引(CA Public Key Index)

            if (tlvList.getTLV("DF05") != null) {
                capk.setExpDate(tlvList.getTLV("DF05").getValue());//认证中心公钥有效期(CA Public Key period of validity)
                Log.e("acpkData", "ExpDate--->" + capk.getExpDate() + "\ntag-->" + tlvList.getTLV("DF05").getValue());
            }
            if (tlvList.getTLV("DF06") != null) {
                capk.setHashInd(tlvList.getTLV("DF06").getValue());//认证中心公钥哈什算法标识(CA Public Key Hash algorithm identification)
                Log.e("acpkData", "HashInd--->" + capk.getHashInd() + "\ntag-->" + tlvList.getTLV("DF06").getValue());
            }
            if (tlvList.getTLV("DF07") != null) {
                capk.setArithInd(tlvList.getTLV("DF07").getValue());//认证中心公钥算法标识(CA Public Key Algorithm identification)
                Log.e("acpkData", "ArithInd--->" + capk.getArithInd() + "\ntag-->" + tlvList.getTLV("DF07").getValue());
            }
            if (tlvList.getTLV("DF02") != null) {
                capk.setModul(tlvList.getTLV("DF02").getValue());//认证中心公钥模(CA Public Key module)
                Log.e("acpkData", "tModul--->" + capk.getModul() + "\ntag-->" + tlvList.getTLV("DF02").getValue());
            }
            if (tlvList.getTLV("DF04") != null) {
                capk.setExponent(tlvList.getTLV("DF04").getValue());//认证中心公钥指数(CA Public Key exponent)
                Log.e("acpkData", "Exponent--->" + capk.getExponent() + "\ntag-->" + tlvList.getTLV("DF04").getValue());
            }
            if (tlvList.getTLV("DF03") != null) {
                capk.setCheckSum(tlvList.getTLV("DF03").getValue().substring(0, 40));//认证中心公钥校验值(CA Public Key Check value)
                Log.e("acpkData", tlvList.getTLV("DF03").getLength() + "----" + tlvList.getTLV("DF03").getTLLength() + "CheckSum--->" + capk.getCheckSum() + "\ntag-->" + tlvList.getTLV("DF03").getValue() + "\ndataSize" + capk.toByteArray().length);
            }
            Log.e("addCapk", tlvList.toString() + "\n" + "capkSize-->" + capk.toByteArray().length + "\n" + capk.print());

        } catch (RemoteException ex){
            ex.printStackTrace();
        }

        return capk;
    }


    public static EmvAppList getAID(Context context, String aidStr){

        EmvAppList emvAppList = new EmvAppList(context);
        TLVList tlvList =TLVList.fromBinary(aidStr);
        try {
            emvAppList.setAID(tlvList.getTLV("9F06").getValue());//aid
            if (tlvList.getTLV("DF01") != null) {
                emvAppList.setSelFlag(tlvList.getTLV("DF01").getValue());//选择应用标识
            }
            if (tlvList.getTLV("9F09") != null) {
                emvAppList.setVersion(tlvList.getTLV("9F09").getValue());//应用版本
            }
            if (tlvList.getTLV("DF11") != null) {
                emvAppList.setTACDefault(tlvList.getTLV("DF11").getValue());//TAC－缺省
            }
            if (tlvList.getTLV("DF12") != null) {
                emvAppList.setTACOnline(tlvList.getTLV("DF12").getValue());//TAC－联机
            }
            if (tlvList.getTLV("DF13") != null) {
                emvAppList.setTACDenial(tlvList.getTLV("DF13").getValue());//TAC－拒绝
            }
            if (tlvList.getTLV("9F1B") != null) {
                emvAppList.setFloorLimit(Long.parseLong(tlvList.getTLV("9F1B").getValue()));//最低限额
            }
            if (tlvList.getTLV("DF15") != null) {
                emvAppList.setThreshold(Long.parseLong(tlvList.getTLV("DF15").getValue()));//偏置随机选择的阈值
            }
            if (tlvList.getTLV("DF16") != null) {
                emvAppList.setMaxTargetPer(Integer.parseInt(tlvList.getTLV("DF16").getValue()));//偏置随机选择的最大目标百分数
            }
            if (tlvList.getTLV("DF17") != null) {
                emvAppList.setTargetPer(Integer.parseInt(tlvList.getTLV("DF17").getValue()));//随机选择的目标百分数
            }
            if (tlvList.getTLV("DF14") != null) {
                emvAppList.setDDOL(tlvList.getTLV("DF14").getValue());//缺省DDOL
            }
            if (tlvList.getTLV("DF18") != null) {
                emvAppList.setBOnlinePin(Integer.parseInt(tlvList.getTLV("DF18").getValue()));
            }
            if (tlvList.getTLV("9F7B") != null) {
                emvAppList.setEC_TermLimit(Long.parseLong(tlvList.getTLV("9F7B").getValue()));//终端电子现金交易限额
            }
            if (tlvList.getTLV("DF19") != null) {
                emvAppList.setCL_FloorLimit(Long.parseLong(tlvList.getTLV("DF19").getValue()));//非接触读写器脱机最低限额
            }
            if (tlvList.getTLV("DF20") != null) {
                emvAppList.setCL_TransLimit(Long.parseLong(tlvList.getTLV("DF20").getValue()));//非接触读写器交易限额
            }
            if (tlvList.getTLV("DF21") != null) {
                emvAppList.setCL_CVMLimit(Long.parseLong(tlvList.getTLV("DF21").getValue())); //非接触终端CVM限额
            }
        } catch (RemoteException ex){
            ex.printStackTrace();
        }

        return emvAppList;
    }



    public static void main(String[] args){
        try{
            //TLVUtil.getCAPK("9F0605A0000000049F220100DF05083230303931323331DF060101DF070101DF02609E15214212F6308ACA78B80BD986AC287516846C8D548A9ED0A42E7D997C902C3E122D1B9DC30995F4E25C75DD7EE0A0CE293B8CC02B977278EF256D761194924764942FE714FA02E4D57F282BA3B2B62C9E38EF6517823F2CA831BDDF6D363DDF040103DF03148BB99ADDF7B560110955014505FB6B5F8308CE27");
            //TLVUtil.getCAPK("9F0605A0000000049F220106DF05083230323131323331DF060101DF070101DF0281F8CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747FDF040103DF0314F910A1504D5FFB793D94F3B500765E1ABCAD72D9");
            //TLVUtil.getAID("9F0607A0000000041010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
