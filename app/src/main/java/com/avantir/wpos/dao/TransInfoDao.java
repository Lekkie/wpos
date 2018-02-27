package com.avantir.wpos.dao;

import android.content.Context;
import com.avantir.wpos.model.TransInfo;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lekanomotayo on 25/02/2018.
 */
public class TransInfoDao extends BaseDao<TransInfo, String> {
    public TransInfoDao(Context context) {
        super(context, TransInfo.class);
    }

    public TransInfo findByRetRefNo(String retRefNo) {
        List<TransInfo> transInfoList = this.findBySQL("select * from tbl_trans_info where ret_ref_no = '" + retRefNo + "'");
        return transInfoList == null ? null : transInfoList.get(0);
    }

    public List<TransInfo>  findByStatus(String status) {
        List<TransInfo> transInfoList = this.findBySQL("select * from tbl_trans_info where status = '" + status + "'");
        return transInfoList;
    }


    public List<TransInfo>  findOpenTransaction() {
        List<TransInfo> transInfoList = this.findBySQL("select * from tbl_trans_info where status in NULL");
        return transInfoList;
    }

    public void updateReversalStatusByRetRefNo(String retRefNo, boolean reversed) {
        this.execute("update tbl_trans_info set reversed = " + reversed
                + " where ret_ref_no = '" + retRefNo + "'");
    }


    public void  updateStatusByRetRefNo(String retRefNo, String status) {
        this.execute("update tbl_trans_info set status = '" + status
                + "' where ret_ref_no = '" + retRefNo + "'");
    }

    public void  updateStatusAuthNumByRetRefNo(String retRefNo, String status, String authNum) {
        this.execute("update tbl_trans_info set status = '" + status
                + "', auth_num = '" + authNum + "' where ret_ref_no = '" + retRefNo + "'");
    }


}
