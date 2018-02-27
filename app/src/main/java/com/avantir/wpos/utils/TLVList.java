package com.avantir.wpos.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TLVList {
    private List<TLV> data = new ArrayList();

    public static TLVList fromBinary(byte[] data) {
        TLVList l = new TLVList();
        int offset = 0;
        while (offset < data.length) {
            TLV d = TLV.fromRawData(data, offset);
            l.addTLV(d);
            offset += d.getRawData().length;
        }
        return l;
    }

    public static TLVList fromBinary(String data) {
        return fromBinary(ByteUtil.hexString2Bytes(data));
    }

    public int size() {
        return this.data.size();
    }

    public byte[] toBinary() {
        byte[][] allData = new byte[this.data.size()][];
        for (int i = 0; i < this.data.size(); i++) {
            allData[i] = this.data.get(i).getRawData();
        }
        return ByteUtil.merage(allData);
    }

    public boolean contains(String tag) {
        return getTLV(tag) != null;
    }

    public TLV getTLV(String tag) {
        for (TLV d : this.data) {
            if (d.getTag().equals(tag)) {
                return d;
            }
        }
        return null;
    }
    public String getTLVVL(String tag)
    {
        for (TLV d : this.data) {
            if (d.getTag().equals(tag)) {
                return d.getValue();
            }
        }
        return "";
    }
    public TLVList getTLVs(String[] tags) {
        TLVList list = new TLVList();
        for (String tag : tags) {
            TLV data = getTLV(tag);
            if (data != null) {
                list.addTLV(data);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    public TLV getTLV(int index) {
        return this.data.get(index);
    }

    public void addTLV(TLV tlv) {
        if (tlv.isValid()) {
            this.data.add(tlv);
        } else
            throw new IllegalArgumentException("tlv is not valid!");
    }

    public void retainAll(String[] tags) {
        List tagList = Arrays.asList(tags);
        for (int i = 0; i < this.data.size(); )
            if (!tagList.contains(this.data.get(i).getTag())) {
                this.data.remove(i);
            } else
                i++;
    }

    public String toString() {
        if (this.data.isEmpty()) {
            return super.toString();
        }
        return ByteUtil.bytes2HexString(toBinary());
    }
}
