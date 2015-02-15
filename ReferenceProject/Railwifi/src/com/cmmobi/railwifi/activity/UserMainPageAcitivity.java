package com.cmmobi.railwifi.activity;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.common.tools.Info;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.Passenger;
import com.cmmobi.railwifi.dao.PassengerDao;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.fragment.UserCollectionFragment;
import com.cmmobi.railwifi.fragment.UserHistoryFragment;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-12-01
 */
public class UserMainPageAcitivity extends TitleRootActivity {

	private String TAG = "UserMainPageAcitivity";
	private LinearLayout llCheckTab;
	private Button btnCheckOne;
	private Button btnCheckTwo;
	private TextView tvWelcome;
	
	public Fragment currFragment;
	private static String MY_TAB_FAVERATE = "MY_TAB_FAVERATE";
	private static String MY_TAB_COLLECTION = "MY_TAB_COLLECTION";
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private PassengerDao passengerDao;
	private SQLiteDatabase db;
	@Override
	public int subContentViewId() {
		return R.layout.activity_user_main_page;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("我");
		hideRightButton();
		
		llCheckTab = (LinearLayout) findViewById(R.id.ll_check_tab);
		llCheckTab.setPadding(DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 21));
		
		btnCheckOne = (Button) findViewById(R.id.btn_check_one);
		btnCheckOne.setOnClickListener(this);
		btnCheckTwo = (Button) findViewById(R.id.btn_check_two);
		btnCheckTwo.setOnClickListener(this);
		
		btnCheckOne.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		btnCheckTwo.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		
		tvWelcome = (TextView) findViewById(R.id.tv_welcome);
		tvWelcome.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		
		ViewUtils.setHeight(findViewById(R.id.rl_top), 265);
		
		/** test GreenDao	 begin*/
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        passengerDao = daoSession.getPassengerDao();
        
		if (passengerDao.count() != 0) {
        	List<Passenger> passList = passengerDao.loadAll();
        	tvWelcome.setText(passList.get(0).getNick_name());
        }
		
		onCheckChanged(R.id.btn_check_one);
	}
	
	
	

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch(v.getId()){
		case R.id.btn_check_one:
		case R.id.btn_check_two:
			onCheckChanged(v.getId());
			break;
			default:{
				
			}	
		}
	}	
	
	
	public void onCheckChanged(int id) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if(currFragment != null){
			ft.hide(currFragment);
		}
		Fragment fragment = null;
		Fragment fragment_other = null;
		switch (id) {
		case R.id.btn_check_one:
			fragment = fm.findFragmentByTag(UserCollectionFragment.class.getName());
			fragment_other = fm.findFragmentByTag(UserHistoryFragment.class.getName());
			if (fragment == null) {
				fragment = new UserCollectionFragment();
				ft.add(R.id.empty, fragment, UserCollectionFragment.class.getName());
			} else {
				ft.show(fragment);
			}
			
			if(fragment_other!=null){
				ft.hide(fragment_other);
			}
			currFragment = fragment;		
			btnCheckOne.setTextColor(Color.WHITE);
			btnCheckTwo.setTextColor(Color.parseColor("#212434"));
			btnCheckOne.setBackgroundResource(R.drawable.table_left_selected_3);
			btnCheckTwo.setBackgroundResource(R.drawable.table_right_default_3);
			break;
		case R.id.btn_check_two:
			fragment = fm.findFragmentByTag(UserHistoryFragment.class.getName());
			fragment_other = fm.findFragmentByTag(UserCollectionFragment.class.getName());
			if (fragment == null) {
				fragment = new UserHistoryFragment();
				ft.add(R.id.empty, fragment, UserHistoryFragment.class.getName());
			} else {
				ft.show(fragment);
			}
			
			if(fragment_other!=null){
				ft.hide(fragment_other);
			}
			currFragment = fragment;
			btnCheckOne.setTextColor(Color.parseColor("#212434"));
			btnCheckTwo.setTextColor(Color.WHITE);
			btnCheckOne.setBackgroundResource(R.drawable.table_left_default_3);
			btnCheckTwo.setBackgroundResource(R.drawable.table_right_selected_3);
			break;
		default:
			break;
		}
		
		ft.commitAllowingStateLoss();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		
		return false;
	}

}
