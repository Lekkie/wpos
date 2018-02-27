package com.avantir.wpos.dao;

import android.content.Context;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;

import java.util.List;

/**
 * Created by lekanomotayo on 25/02/2018.
 */
public class ReversalInfoDao extends BaseDao<ReversalInfo, String> {
    public ReversalInfoDao(Context context) {
        super(context, ReversalInfo.class);
    }

    public ReversalInfo findByRetRefNo(String retRefNo) {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from tbl_reversals_info where ret_ref_no = '" + retRefNo + "'");
        return reversalInfoList == null ? null : reversalInfoList.get(0);
    }

    public List<ReversalInfo>  findByStatus(String status) {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from tbl_reversals_info where status = '" + status + "'");
        return reversalInfoList;
    }

    public List<ReversalInfo>  findAllOpenTransaction() {
        List<ReversalInfo> reversalInfoList = this.findBySQL("select * from tbl_reversals_info where status in NULL");
        return reversalInfoList;
    }

    public void updateRetryByRetRefNo(String retRefNo, int retry) {
        this.execute("update tbl_reversal_info set retry = " + retry
                + " where ret_ref_no = '" + retRefNo + "'");
    }

    public void  updateStatusByRetRefNo(String retRefNo, String status) {
        this.execute("update tbl_reversal_info set status = '" + status
                + "' where ret_ref_no = '" + retRefNo + "'");
    }
}
