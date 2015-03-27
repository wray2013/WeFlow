package com.etoc.weflow.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etoc.weflow.Config;
import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.fragment.DiscoveryFragment;
import com.etoc.weflow.fragment.FlowBankFragment;
import com.etoc.weflow.fragment.HomePageFragment;
import com.etoc.weflow.fragment.MyselfFragment;
import com.etoc.weflow.fragment.XFragment;
import com.etoc.weflow.utils.RandomUtils;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.version.CheckUpdate;

public class MainActivity extends TitleRootActivity implements Callback, OnClickListener {
	
	private final String TAG = "MainActivity";

	private Handler handler;
	private DisplayMetrics dm = new DisplayMetrics();
	
	private XFragment<?> currContentFragment;
	
	private XFragment<?> homePageFragment;
	private XFragment<?> flowBankFragment;
	private XFragment<?> discoveryFragment;
	private XFragment<?> myselfFragment;
	
	private RelativeLayout rlHomePage;
	private RelativeLayout rlBank;
	private RelativeLayout rlDiscover;
	private RelativeLayout rlMe;
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;
	private AccountInfoDao accountInfoDao;
	
	private static long back_pressed;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		handler = new Handler(this);
		dm = getResources().getDisplayMetrics();
		
		initDataBase();
		initController();
		initMain(savedInstanceState);
		setLeftButtonBackground(R.drawable.btn_message);
		//检查更新
		CheckUpdate.getInstance(this).update();
	}
	
	private void initDataBase() {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "weflowdb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        accountInfoDao = daoSession.getAccountInfoDao();
	}
	
	public AccountInfoDao getAccountInfoDao() {
		return accountInfoDao;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(db != null)
			db.close();
	}
	
	private void initController() {
		
		ViewUtils.setHeight(findViewById(R.id.ll_controller), 118);
		ViewUtils.setSize(findViewById(R.id.btn_tab_weflow), 48, 48);
		ViewUtils.setSize(findViewById(R.id.btn_tab_bank), 48, 48);
		ViewUtils.setSize(findViewById(R.id.btn_tab_discover), 48, 48);
		ViewUtils.setSize(findViewById(R.id.btn_tab_me), 48, 48);
		ViewUtils.setMarginTop(findViewById(R.id.btn_tab_weflow), 16);
		ViewUtils.setMarginTop(findViewById(R.id.btn_tab_bank), 16);
		ViewUtils.setMarginTop(findViewById(R.id.btn_tab_discover), 16);
		ViewUtils.setMarginTop(findViewById(R.id.btn_tab_me), 16);
		
		ViewUtils.setMarginBottom(findViewById(R.id.tv_tab_weflow), 16);
		ViewUtils.setMarginBottom(findViewById(R.id.tv_tab_bank), 16);
		ViewUtils.setMarginBottom(findViewById(R.id.tv_tab_discover), 16);
		ViewUtils.setMarginBottom(findViewById(R.id.tv_tab_me), 16);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_tab_weflow), 24);
		ViewUtils.setTextSize(findViewById(R.id.tv_tab_bank), 24);
		ViewUtils.setTextSize(findViewById(R.id.tv_tab_discover), 24);
		ViewUtils.setTextSize(findViewById(R.id.tv_tab_me), 24);
		
		rlHomePage = (RelativeLayout) findViewById(R.id.rl_btn_weflow);
		rlBank     = (RelativeLayout) findViewById(R.id.rl_btn_bank);
		rlDiscover = (RelativeLayout) findViewById(R.id.rl_btn_discover);
		rlMe       = (RelativeLayout) findViewById(R.id.rl_btn_me);
		
		
		rlHomePage.setOnClickListener(this);
		rlBank.setOnClickListener(this);
		rlDiscover.setOnClickListener(this);
		rlMe.setOnClickListener(this);
	}
	
	private void switchStatus(XFragment fragment) {
		if (fragment instanceof HomePageFragment) {
			rlHomePage.setSelected(true);
			rlBank.setSelected(false);
			rlDiscover.setSelected(false);
			rlMe.setSelected(false);
		} else if (fragment instanceof FlowBankFragment) {
			rlHomePage.setSelected(false);
			rlBank.setSelected(true);
			rlDiscover.setSelected(false);
			rlMe.setSelected(false);
		} else if (fragment instanceof DiscoveryFragment) {
			rlHomePage.setSelected(false);
			rlBank.setSelected(false);
			rlDiscover.setSelected(true);
			rlMe.setSelected(false);
		} else if (fragment instanceof MyselfFragment) {
			rlHomePage.setSelected(false);
			rlBank.setSelected(false);
			rlDiscover.setSelected(false);
			rlMe.setSelected(true);
		}
	}
	
	private void initMain(Bundle savedInstanceState) {
		
		if(savedInstanceState != null) {
			
			homePageFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, HomePageFragment.class.getName());
			
			flowBankFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, FlowBankFragment.class.getName());
			
			discoveryFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, DiscoveryFragment.class.getName());
			
			myselfFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, MyselfFragment.class.getName());
			
			String contentClass = savedInstanceState.getString("content");
			if (contentClass != null) {
				currContentFragment = (XFragment<?>) getSupportFragmentManager().getFragment(
						savedInstanceState, contentClass);
			}
		}
		
		if (homePageFragment == null) {
			homePageFragment = new HomePageFragment();
		}
		
		if (flowBankFragment == null) {
			flowBankFragment = new FlowBankFragment();
		}
		
		if (discoveryFragment == null) {
			discoveryFragment = new DiscoveryFragment();
		}
		
		if (myselfFragment == null) {
			myselfFragment = new MyselfFragment();
		}
		
		if (currContentFragment == null) {
			currContentFragment = homePageFragment;
		}
		
		showTitle(currContentFragment);
		
		if (null != getSupportFragmentManager().findFragmentByTag(
				currContentFragment.getClass().getName())) {
			getSupportFragmentManager().beginTransaction().remove(currContentFragment);
		}
		
		switchStatus(currContentFragment);
		
		
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.content_frame, currContentFragment,currContentFragment.getClass().getName()).commit();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (outState != null && currContentFragment != null) {
			Log.d(TAG, "onSaveInstanceState currContentFragment="
					+ currContentFragment);
			outState.putString("content", currContentFragment.getClass()
					.getName());
			getSupportFragmentManager().putFragment(outState, currContentFragment.getClass()
					.getName(), currContentFragment);

		}
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	public void switchContent(XFragment<?> fragment) {
		Log.d(TAG, "fragment=" + fragment);
		if (fragment == null) {
			return;
		}
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		if (currContentFragment != null){
//			ft.hide(currContentFragment);
			if (currContentFragment.getIndex() < fragment.getIndex()) {
				ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			} else {
				ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
			}
		}
		
		if (currContentFragment != null && currContentFragment != fragment) {
			Log.d(TAG,"switchContent hide");
			ft.hide(currContentFragment);
			fragment.onShow();
			switchStatus(fragment);
		}
		
		if (null == getSupportFragmentManager().findFragmentByTag(
				fragment.getClass().getName())) {
			Log.d(TAG, "add fragment=" + fragment + " name = " + fragment.getClass().getName());
			ft.add(R.id.content_frame, fragment, fragment.getClass().getName());
			// ft.addToBackStack(null);
		} else {
			Log.d(TAG, "show fragment=" + fragment);
			ft.show(fragment);
		}
		
		showTitle(fragment);
		currContentFragment = fragment;
		ft.commitAllowingStateLoss();
	}
	
	private void showTitle(XFragment<?> fragment) {
		String title = "";
		showLeftButton();
		showRightButton();
		if(fragment != null) {
			if(fragment instanceof HomePageFragment) {
				title = "流量钱包";
				setRightButtonText("宝典");
//				hideLeftButton();
				setLeftButtonBackground(R.drawable.btn_message);
			} else if(fragment instanceof FlowBankFragment) {
				title = "存钱罐";
				setRightButtonText("攻略");
				hideLeftButton();
			} else if(fragment instanceof DiscoveryFragment) {
				title = "发现";
//				hideLeftButton();
				hideRightButton();
				setLeftButtonBackground(R.drawable.btn_scan);
			} else if(fragment instanceof MyselfFragment) {
				title = "我";
				hideLeftButton();
				hideRightButton();
			}
		}
		setTitleText(title);
	}
	
	/*@Override
	public void showContent() {
		// TODO Auto-generated method stub
//		Log.e(TAG,"showContent in - devID:" + Info.getDevId(this));
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}*/
	
	@Override
	public void onBackPressed(){
		if(back_pressed + 2000 > System.currentTimeMillis()){
			super.onBackPressed();
		}else{
			Toast.makeText(getBaseContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
		}
		back_pressed = System.currentTimeMillis();
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private int num = 0;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_btn_weflow:
			switchContent(homePageFragment);
			break;
		case R.id.rl_btn_bank:
			switchContent(flowBankFragment);
			break;
		case R.id.rl_btn_discover:
			switchContent(discoveryFragment);
			break;
		case R.id.rl_btn_me:
			switchContent(myselfFragment);
			break;
		case R.id.btn_title_left:
			if(currContentFragment instanceof HomePageFragment) {
				/*PushMsgUtil pushmsg = new PushMsgUtil(handler, 0x88661256);
//				List<MessageList> fakedata = MyMessageActivity.makeFakeData();
//				MessageList msglist = fakedata.get(num % fakedata.size());
//				num ++;
				
				MessageList item = new MessageList();
				item.msgid = "msg" + String.format("%05d", RandomUtils.getRandom(10000));
				item.type = "02";//看视频
				item.picurl = "";
				item.title = "欢迎您使用流量钱包";
				item.content = "现在完成任务，即刻领取大礼\n点击开始赚币>>";
				item.flowcoins = "";
				item.time = System.currentTimeMillis() + "";
				item.pageurl = "";
				item.productid = "";
				item.extradata = "";
				
				PushMsgResp resp = new PushMsgResp();
				resp.msgtype = "2";
				resp.msgtitle = "有新消息";
				resp.msgcontent = "这是一条测试消息";
				resp.msghint = "点击查看";
				resp.msglist = item;
				String jsonStr = new Gson().toJson(resp);
				
	            String sendMSG = "{\"message\" : " + jsonStr + "}";
	            pushmsg.execute(sendMSG, "18627934685");*/
	            
				AccountInfo info = WeFlowApplication.getAppInstance().getAccountInfo();
				if(info != null && info.getUserid() != null && !info.getUserid().equals("")) {
					startActivity(new Intent(this, MyMessageActivity.class));
				} else {
					startActivity(new Intent(this, LoginActivity.class));
				}
			} else if(currContentFragment instanceof DiscoveryFragment) {
				Intent discIntent = new Intent(this, CaptureActivity.class);
				startActivity(discIntent);
			}
            break;
		case R.id.btn_title_right:
			if(currContentFragment instanceof HomePageFragment) {
				Intent homeIntent = new Intent(this, WebViewActivity.class);
				homeIntent.putExtra("pageurl", Config.HOMEPAGE_URL);
				homeIntent.putExtra("pagetitle", "宝典");
				startActivity(homeIntent);
			} else if(currContentFragment instanceof FlowBankFragment) {
				Intent bankIntent = new Intent(this, WebViewActivity.class);
				bankIntent.putExtra("pageurl", Config.BANKPAGE_URL);
				bankIntent.putExtra("pagetitle", "攻略");
				startActivity(bankIntent);
			}
			
			/*Intent intent = new Intent();
	        intent.setAction(Intent.ACTION_PICK);
	        intent.setData(ContactsContract.Contacts.CONTENT_URI);
	        startActivityForResult(intent, 1);*/
			break;
		}
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_main;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode != RESULT_OK || data == null) return;
		if (requestCode == 1) {
			Uri result = data.getData();
			String contactId = result.getLastPathSegment();
			String contactnumber = getPhoneContacts(contactId);
			Toast.makeText(this, "phone number = " + contactnumber, Toast.LENGTH_LONG).show();
		}

	}
	
	@SuppressWarnings("deprecation")
	private String getPhoneContacts(String contactId) {
		Cursor cursor = null;
		String number = "";
		try {
			Uri uri = Phone.CONTENT_URI;
			cursor = getContentResolver().query(uri, null, Phone.CONTACT_ID + "=" + contactId , null, null);
			if (cursor.moveToFirst()) {
				number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
				// Toast.makeText(this, name, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "No contact found.", Toast.LENGTH_LONG)
						.show();
			}
		} catch (Exception e) {
			number = "";
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return number;
	}
}
