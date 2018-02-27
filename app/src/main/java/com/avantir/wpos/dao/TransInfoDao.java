package com.avantir.wpos.dao;

import android.content.Context;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
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

    public List<TransInfo>  findByStatus(String status) {
        List<TransInfo> transInfoList = this.findBySQL("select * from " + TABLE_NAME + " where status = '" + status + "'");
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

    public void updateReversalCompletionStatusByRetRefNo(String retRefNo, int reversed, int completed) {
        this.execute("update " + TABLE_NAME + " set reversed = " + reversed + ", completed = " + completed
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public void updateCompletionStatusByRetRefNo(String retRefNo, int completed) {
        this.execute("update " + TABLE_NAME + " set completed = " + completed
                + " where ret_ref_no = '" + retRefNo + "'");
    }


    public void  updateStatusByRetRefNo(String retRefNo, String status) {
        this.execute("update " + TABLE_NAME + " set status = '" + status
                + "' where ret_ref_no = '" + retRefNo + "'");
    }

    public void  updateStatusAuthNumCompletedByRetRefNo(String retRefNo, String status, String authNum, int completed) {
        this.execute("update " + TABLE_NAME + " set status = '" + status
                + "', auth_num = '" + authNum + "', completed = " + completed
                + " where ret_ref_no = '" + retRefNo + "'");
    }


}
