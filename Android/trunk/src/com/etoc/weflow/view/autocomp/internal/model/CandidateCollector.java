package com.etoc.weflow.view.autocomp.internal.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.FrequentPhone;
import com.etoc.weflow.dao.FrequentPhoneDao;
import com.etoc.weflow.dao.FrequentQQ;
import com.etoc.weflow.dao.FrequentQQDao;
import com.etoc.weflow.view.autocomp.internal.entity.AccountType;
import com.etoc.weflow.view.autocomp.internal.filter.AccountFilter;


/**
 * @author KeithYokoma
 * @since 2014/03/05
 */
public final class CandidateCollector {
	private static FrequentPhoneDao phoneDao;
	private static FrequentQQDao qqDao;
	private static List<FrequentPhone> phoneList;
	private static List<FrequentQQ> qqList;
	
	static {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(WeFlowApplication.getAppInstance(), "weflowdb", null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
        phoneDao = daoSession.getFrequentPhoneDao();
        qqDao = daoSession.getFrequentQQDao();
        phoneList = phoneDao.loadAll();
        qqList = qqDao.loadAll();
	}
    public static String[] getAccounts(Context context, AccountType type) {
    	Set<String> accountSet = new HashSet<String>();
        final AccountFilter filter = type.getFilter();
        switch (type) {
        case PHONE_NUMBER:
        	phoneList = phoneDao.loadAll();
        	for (FrequentPhone phone:phoneList) {
        		String accepted = filter.filter(phone.getPhone_num());
        		Log.d("=AAA=","accepted = " + accepted + " num = " + phone.getPhone_num());
        		if (!TextUtils.isEmpty(accepted)) {
                    accountSet.add(accepted);
                }
        	}
        	break;
        case QQ_NUMBER:
        	qqList = qqDao.loadAll();
        	for (FrequentQQ qq:qqList) {
        		String accepted = filter.filter(qq.getQq_num());
        		if (!TextUtils.isEmpty(accepted)) {
                    accountSet.add(accepted);
                }
        	}
        	break;
        }
        
        return accountSet.toArray(new String[accountSet.size()]);
    }
}