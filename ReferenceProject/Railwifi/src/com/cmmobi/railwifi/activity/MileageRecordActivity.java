package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.List;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.MileageRecordAdapter;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.MileageRecord;
import com.cmmobi.railwifi.dao.MileageRecordDao;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.network.GsonResponseObject.TrainInfo;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.widget.ListView;

public class MileageRecordActivity extends TitleRootActivity {

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private MileageRecordDao mileageRecordDao;
	private SQLiteDatabase db;
	
	private MileageRecordAdapter adapter;
	private ListView lvMileage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("箩筐里程记录");
		hideRightButton();
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        mileageRecordDao = daoSession.getMileageRecordDao();
        
		initView();
	}
	
	
	private void initView() {
		// TODO Auto-generated method stub
		adapter = new MileageRecordAdapter(this);
		adapter.setData(makeFakeData());
		
		lvMileage = (ListView) findViewById(R.id.lv_mileage);
		lvMileage.setDividerHeight(DisplayUtil.getSize(this, 12));
		ViewUtils.setMarginTop(lvMileage, 12);
		lvMileage.setAdapter(adapter);
	}

	private List<TrainInfo> makeFakeData() {
		
		List<MileageRecord> listall = mileageRecordDao.loadAll();
		List<TrainInfo> list = new ArrayList<TrainInfo>();
		for(MileageRecord m : listall) {
			TrainInfo item = new TrainInfo();
			item.starting  = m.getStarting();
			item.ending = m.getEnding();
			item.mileage = m.getMileage();
			item.hours = m.getHours();
			item.date = m.getDate();
			item.train_num = m.getTrain_num();
			item.points = m.getPoints();
			list.add(item);
		}
		return list;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_mileage_record;
	}

}
