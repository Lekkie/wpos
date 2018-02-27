package com.avantir.wpos.dao;

import android.content.Context;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;

import java.util.List;

/**
 * Created by lekanomotayo on 25/02/2018.
 */
public class ReversalInfoDao extends BaseDao<ReversalInfo, String> {

    String TABLE_NAME = "tbl_reversals_info";
    public ReversalInfoDao(Context context) {
        super(context, ReversalInfo.class);
    }

    public ReversalInfo findByRetRefNo(String retRefNo) {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from " + TABLE_NAME + " where ret_ref_no = '" + retRefNo + "'");
        return (reversalInfoList == null || reversalInfoList.size() < 1) ? null : reversalInfoList.get(0);
    }

    public List<ReversalInfo>  findByStatus(String status) {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from " + TABLE_NAME + " where status = '" + status + "'");
        return reversalInfoList;
    }

    public List<ReversalInfo>  findAll() {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from " + TABLE_NAME);
        return reversalInfoList;
    }

    public List<ReversalInfo>  findAllOpenTransaction() {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from " + TABLE_NAME + " where completed = 0");
        return reversalInfoList;
    }

    public void updateRetryByRetRefNo(String retRefNo, int retry) {
        this.execute("update " + TABLE_NAME + " set retry_no = " + retry
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public void  updateStatusCompletionByRetRefNo(String retRefNo, String status, int completed) {
        this.execute("update " + TABLE_NAME + " set status = '" + status + "', completed = " + completed
                + " where ret_ref_no = '" + retRefNo + "'");
    }
}
