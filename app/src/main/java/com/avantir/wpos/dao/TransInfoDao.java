package com.avantir.wpos.dao;

import android.content.Context;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.avantir.wpos.utils.TimeUtil;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by lekanomotayo on 25/02/2018.
 */
public class TransInfoDao extends BaseDao<TransInfo, String> {

    String TABLE_NAME = "tbl_trans_info";


    public TransInfoDao(Context context) {
        super(context, TransInfo.class);
    }

    public TransInfo findByRetRefNo(String retRefNo) {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME + " where ret_ref_no = '" + retRefNo + "'");
        return (transInfoList == null || transInfoList.size() < 1) ? null : transInfoList.get(0);
    }

    public TransInfo findByStan(String stan) {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME + " where stan = '" + stan + "'");
        return (transInfoList == null || transInfoList.size() < 1) ? null : transInfoList.get(0);
    }

    public List<TransInfo>  findByResponseCode(String responseCode) {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME + " where response_code = '" + responseCode + "'");
        return transInfoList;
    }


    public List<TransInfo>  findAll() {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME);
        return transInfoList;
    }

    public List<TransInfo>  findAllOpenTransaction() {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME + " where completed = 0");
        return transInfoList;
    }

    public List<TransInfo>  findByToday() {
        long nowEpochTime = TimeUtil.getTimeInEpoch(new Date());
        long startDate = TimeUtil.getStartOfDay(nowEpochTime);
        long endDate = TimeUtil.getEndOfDay(nowEpochTime);
        return findByDate(startDate, endDate);
    }

    public List<TransInfo>  findByDate(long startDate, long endDate) {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME + " where created_on >= " + startDate +  " AND created_on <= " + endDate);
        return transInfoList;
    }

    public void updateReversalCompletionByRetRefNo(String retRefNo, int reversed, int completed) {
        this.execute("update " + TABLE_NAME + " set reversed = " + reversed + ", completed = " + completed
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public void updateCompletionStatusByRetRefNo(String retRefNo, int completed) {
        this.execute("update " + TABLE_NAME + " set completed = " + completed
                + " where ret_ref_no = '" + retRefNo + "'");
    }


    public void  updateResponseCodeByRetRefNo(String retRefNo, String responseCode) {
        this.execute("update " + TABLE_NAME + " set response_code = '" + responseCode
                + "' where ret_ref_no = '" + retRefNo + "'");
    }

    public void  updateResponseCodeAuthNumCompletedByRetRefNo(String retRefNo, String responseCode, String authNum, int completed) {
        this.execute("update " + TABLE_NAME + " set response_code = '" + responseCode
                + "', auth_num = '" + authNum + "', completed = " + completed
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public TransInfo findLastTransaction() {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME + " order by id desc LIMIT 1");
        return (transInfoList == null || transInfoList.size() < 1) ? null : transInfoList.get(0);
    }

}
