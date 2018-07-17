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

    public List<ReversalInfo>  findByResponseCode(String responseCode) {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from " + TABLE_NAME + " where response_code = '" + responseCode + "'");
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

    public List<ReversalInfo>  findAllCompletedUnNotifiedReversalTransaction() {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from " + TABLE_NAME + " where notified = 0" +
                " AND completed = 1");
        return reversalInfoList;
    }

    public void updateRetryByRetRefNo(String retRefNo, int retry) {
        this.execute("update " + TABLE_NAME + " set retry_no = " + retry
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public void  updateResponseCodeCompletionByRetRefNo(String retRefNo, String responseCode, int completed, long latency) {
        this.execute("update " + TABLE_NAME + " set response_code = '" + responseCode
                + "', completed = " + completed + "', latency = " + latency
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public void  updateNotificationByRetRefNo(String retRefNo, int notified) {
        this.execute("update " + TABLE_NAME + " set notified = " + notified
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public List<ReversalInfo>  findOlderThanDate(long date) {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from " + TABLE_NAME + " where created_on <= " + date);
        return reversalInfoList;
    }
}
