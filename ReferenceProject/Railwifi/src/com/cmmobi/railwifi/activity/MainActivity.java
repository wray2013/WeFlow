package com.cmmobi.railwifi.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.R.menu;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.cmmobi.common.push.Config;
import com.cmmobi.common.tools.Info;
import com.cmmobi.common.tools.NetworkTypeUtility;
import com.cmmobi.push.CmmobiPush;
import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.DownloadHistoryDao.Properties;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.MileageRecord;
import com.cmmobi.railwifi.dao.MileageRecordDao;
import com.cmmobi.railwifi.dao.Passenger;
import com.cmmobi.railwifi.dao.PassengerDao;
import com.cmmobi.railwifi.dialog.DialogUtils;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.download.DownloadManager;
import com.cmmobi.railwifi.event.DialogEvent;
import com.cmmobi.railwifi.event.FragmentEvent;
import com.cmmobi.railwifi.event.NetworkEvent;
import com.cmmobi.railwifi.fragment.CityIntroductionFragment;
import com.cmmobi.railwifi.fragment.CityLifeFragment;
import com.cmmobi.railwifi.fragment.GameWorldFragment;
import com.cmmobi.railwifi.fragment.HomePageFragment;
import com.cmmobi.railwifi.fragment.MediaFragment;
import com.cmmobi.railwifi.fragment.MenuFragment;
import com.cmmobi.railwifi.fragment.PopularAppFragment;
import com.cmmobi.railwifi.fragment.RailServiceFragment;
import com.cmmobi.railwifi.fragment.RailTravelFragment;
import com.cmmobi.railwifi.fragment.XFragment;
import com.cmmobi.railwifi.network.CRMRequester;
import com.cmmobi.railwifi.network.CRM_Object;
import com.cmmobi.railwifi.network.CRM_Object.versionCheckResponse;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.BaseInfoResp;
import com.cmmobi.railwifi.network.GsonResponseObject.MileageResp;
import com.cmmobi.railwifi.network.GsonResponseObject.TrainInfo;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.receiver.PushReceiver;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DateUtils;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import de.greenrobot.event.EventBus;

public class MainActivity extends SlidingFragmentActivity implements Callback {
	public static final String SAVE_KEY = "save_key";
	private final String TAG = "MainActivity";
	public final int HANDER_FLAG_SHOW_ALERT = 0xd138173;
	
	private XFragment mMediaFragment;
	private MenuFragment menuFragment;
	private XFragment railServiceFragment;
	private XFragment currContentFragment;
	private XFragment mCityIntrodutionFragment;
//	private XFragment cityLiftFragment;
//	private XFragment popurlarAppFragment;
	private XFragment gameWorldFragment;
	private XFragment homePageFragment;
	private XFragment railTravelFragment;
	private Handler handler;
	
	private static long back_pressed;
	

	private MileageRecordDao mileageRecordDao;
	private DisplayMetrics dm = new DisplayMetrics();
	private wifiConnectReceiver myreceiver;
	public static String train_num = "";
	public static String dev_id = "";
	public static String railway_name = "";
	public static String dev_mac = "";
	public static String last_dev_mac = "";
	
	private static String userid;
	
	/*
	 * 获取userid，值可能为空
	 */
	public static String getUserId(){
		return userid;
	}
	
	/*
	 * 设置userid
	 */
	public static void setUserId(String id){
		userid = id;
	}
	
	class wifiConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            	ConnectivityManager connectivityManager = (ConnectivityManager) context.
            			getSystemService(Context.CONNECTIVITY_SERVICE);  
            	
            	NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo(); 
            	
            	if ( activeNetInfo != null && activeNetInfo.isAvailable()) {   
                    
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    
                    final String curType = NetworkTypeUtility.getNetworkClass(tm, context);
                   
                    handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Requester.requestBaseInfo(handler);
							 if ("wifi".equals(curType)) {
		                    	 DownloadManager.getInstance().notifyLockItems();
		                    }
						}
					}, 1000);
                    	
//                    }
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            	Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            	if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;
//					if (isConnected) {
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Requester.requestBaseInfo(handler);
						}
					}, 1000);
//					}
            	}

            }
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        
		handler = new Handler(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		EventBus.getDefault().register(this);
		
		init(savedInstanceState);
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CRMRequester.checkVersion(handler);
			}
		}, 50);
		
		if (com.cmmobi.railwifi.Config.IS_USE_STRICTMODE){
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectNetwork() // or .detectAll() for all detectable
										// problems
					.detectAll().build());

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.penaltyDeath().build());
		}
		
	
	}
	
	private void init(Bundle savedInstanceState){
		DaoMaster daoMaster;
		DaoSession daoSession;
		SQLiteDatabase db;
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        mileageRecordDao = daoSession.getMileageRecordDao();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        myreceiver = new wifiConnectReceiver();
        registerReceiver(myreceiver, filter);

//        String ipStr = NetWorkUtils.getLocalIpAddress(this);
//        Log.d(TAG,"ipStr = " + ipStr);
        Requester.requestBaseInfo(handler);
		
		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null) {
			homePageFragment = (XFragment)getSupportFragmentManager().getFragment(
					savedInstanceState, HomePageFragment.class.getName());
			
			mMediaFragment = (XFragment)getSupportFragmentManager().getFragment(
					savedInstanceState, MediaFragment.class.getName());
//			menuFragment = (MenuFragment) getSupportFragmentManager().getFragment(
//					savedInstanceState, MenuFragment.class.getName());
//			menuFragment = (MenuFragment) this.getSupportFragmentManager()
//					.findFragmentById(R.id.menu_frame);
			railServiceFragment = (XFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, RailServiceFragment.class.getName());
			
			/*cityLiftFragment = (XFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, CityLifeFragment.class.getName());
			
			popurlarAppFragment = (XFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, PopularAppFragment.class.getName());*/
			
			gameWorldFragment = (XFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, GameWorldFragment.class.getName());
			
			railTravelFragment = (XFragment) getSupportFragmentManager().getFragment(savedInstanceState, RailTravelFragment.class.getName());
			
			mCityIntrodutionFragment = (XFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, CityIntroductionFragment.class.getName());
			
			
			String contentClass = savedInstanceState.getString("content");
			if (contentClass != null) {
				currContentFragment = (XFragment) getSupportFragmentManager().getFragment(
						savedInstanceState, contentClass);
			}
			
			Log.d(TAG,"savedInstanceState = " + savedInstanceState + " currContentFragment = " + currContentFragment);
			
		}
		
		if (homePageFragment == null) {
			homePageFragment = new HomePageFragment();
		}

		if (mMediaFragment == null) {
			mMediaFragment = new MediaFragment();
		}
		
		if (railTravelFragment == null) {
			railTravelFragment = new RailTravelFragment();
		}
		
		if (railServiceFragment == null) {
			railServiceFragment = new RailServiceFragment();
		}
		
		if (mCityIntrodutionFragment ==null){
			mCityIntrodutionFragment = new CityIntroductionFragment();
		}
		
		/*if (cityLiftFragment == null) {
			cityLiftFragment = new CityLifeFragment();
		}
		
		if (popurlarAppFragment == null) {
			popurlarAppFragment = new PopularAppFragment();
		}*/
		
		if (gameWorldFragment == null) {
			gameWorldFragment = new GameWorldFragment();
		}
		
		if (currContentFragment == null) {
			currContentFragment = homePageFragment;
		}
		if (null != getSupportFragmentManager().findFragmentByTag(
				currContentFragment.getClass().getName())) {
			getSupportFragmentManager().beginTransaction().remove(currContentFragment);
		}
		
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.content_frame, currContentFragment,currContentFragment.getClass().getName()).commit();

		// set the Behind View Fragment
		if (menuFragment != null && null != getSupportFragmentManager().findFragmentByTag(
				menuFragment.getClass().getName())) {
			getSupportFragmentManager().beginTransaction().remove(menuFragment);
		}
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		if (menuFragment == null) {
			menuFragment = new MenuFragment();
		}
		t.replace(R.id.menu_frame, menuFragment);
		t.commit();
		
		/**
		 * 切换菜单页的状态
		 */
		if (currContentFragment == homePageFragment) {
			menuFragment.change2HomePage();
		} else if (currContentFragment == railServiceFragment) {
			menuFragment.change2RailService();
		} else if (currContentFragment == mMediaFragment) {
			menuFragment.change2Amusement();
		} else if (currContentFragment == railTravelFragment) {
			menuFragment.change2Alliance();
		} else if (currContentFragment == gameWorldFragment) {
			menuFragment.change2GameWorld();
		} else if (currContentFragment == mCityIntrodutionFragment) {
			menuFragment.change2CityIntro();
		}
		
		dm = getResources().getDisplayMetrics();
		// customize the SlidingMenu
		final int leftOffset = 192 * dm.widthPixels / 720;
		final SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setBehindOffset(leftOffset);
		sm.setFadeEnabled(false);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		sm.setShadowDrawable(R.drawable.shadow_left);
		sm.setTopShadowDrawable(R.drawable.shadow_top);
		sm.setBottomShadowDrawable(R.drawable.shadow_down);
		sm.setShadowWidth(DisplayUtil.getSize(this, 30));
		
		sm.setBackgroundImage(R.drawable.bg_menu_root);
		
		final float defaultPercent = 910.0f/(1280 - 50);
		final float defaultMidPos = (577.0f) * dm.heightPixels / 1280;
		sm.setTopEdge((int) (defaultMidPos * (1 - defaultPercent)));
		Log.d(TAG,"defaultMidPos = " + defaultMidPos);
		sm.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * (1 - defaultPercent) + defaultPercent);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						defaultMidPos);
			}
		});

		sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * (1 - defaultPercent));
				canvas.scale(scale, scale, 0, defaultMidPos);
			}
		});
		
		sm.setOnOpenListener(new OnOpenListener() {
			
			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
				EventBus.getDefault().post(BannerEvent.BANNERSTOP);
			}
		});
		
		sm.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				EventBus.getDefault().post(BannerEvent.BANNERSTOP);
			}
		});
		
		sm.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				// TODO Auto-generated method stub
				EventBus.getDefault().post(BannerEvent.BANNERSTART);
			}
		});
		
		
//		Timer timer = new Timer(true);
//		timer.schedule(task,10000, 3000); 
		
		Intent intent = getIntent();
		String intent_title = intent.getStringExtra(Config.TITLE);
		String intent_content = intent.getStringExtra(Config.CONTENT);
		String intent_msgid = intent.getStringExtra(Config.MSGID);
		String intent_dict_str = intent.getStringExtra("");

		if(intent_title!=null && intent_content!=null && !intent_title.equals("") && !intent_content.equals("")){
			Map<String, String> dict = null;
			String content_type = null;
			String object_id = null;
			if(intent_dict_str!=null){
				Gson gson = new Gson();
				Type type = new TypeToken<Map<String, String>>() {}.getType();  
				dict = gson.fromJson(intent_dict_str, type);
			}
			
			if(dict!=null){
				content_type = dict.get(PushReceiver.CONTENT_TYPE);
				object_id = dict.get(PushReceiver.OBJECT_ID);
			}
			
			if(PushReceiver.HELP.equals(content_type)){
				DialogUtils.SendCallHelpDialog(this, intent_title, intent_content, intent_msgid, -1);
			}else if(content_type!=null && object_id!=null){
				DialogUtils.SendJumpDialog(this, intent_title, intent_content, intent_msgid, -1, content_type, object_id);
			}

		}
		
		
		
	}
	
	
//	TimerTask task = new TimerTask(){  
//	      public void run() {  
//	      Message message = new Message();      
//	      message.what = HANDER_FLAG_SHOW_ALERT;      
//	      handler.sendMessage(message);    
//	      
//	   }  
//	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		PromptDialog.dismissDialog();
		PromptDialog.dimissProgressDialog();
		unregisterReceiver(myreceiver);
		super.onDestroy();
	}
	
	
	public void switchContent(XFragment fragment) {
		back_pressed = 0;
		Log.d(TAG, "fragment=" + fragment);
		if (fragment == null) {
			return;
		}
		
		if (currContentFragment != null
				&& currContentFragment.getClass().getSimpleName()
						.equalsIgnoreCase(fragment.getClass().getSimpleName())) {
			showContent();
			return;
		}
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		currContentFragment = fragment;
		ft.replace(R.id.content_frame, fragment);
		ft.commitAllowingStateLoss();
		/*if (currContentFragment != null && currContentFragment != fragment) {
			Log.d(TAG,"switchContent hide");
			ft.hide(currContentFragment);
			ft.remove(currContentFragment);
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
		currContentFragment = fragment;
		ft.commitAllowingStateLoss();*/
		showContent();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (outState != null && currContentFragment != null
				&& getSupportFragmentManager().findFragmentByTag(currContentFragment.getClass()
					.getName()) != null) {
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
//		init(savedInstanceState);
	}
	
	public enum BannerEvent {
		BANNERSTART,BANNERSTOP
	}
	
	public void onEventMainThread(DialogEvent event) {
		TitleRootActivity.ProcessDialogEvent(this, event, this.getClass().getName());
	}
	
	public void onEvent(FragmentEvent event) {
		switch(event) {
		case HOMEPAGE:
			switchContent(homePageFragment);
			if (homePageFragment != null) {
				((HomePageFragment)homePageFragment).requestDiscover();
			}
			break;
		case RAILSERVICE:
			switchContent(railServiceFragment);
			/*if (railServiceFragment != null) {
				((RailServiceFragment)railServiceFragment).requestBanner();
			}*/
			break;
		case MEDIAAMUSEMENT:
			switchContent(mMediaFragment);
			/*if (mMediaFragment != null) {
				((MediaFragment) mMediaFragment).requestBanner();
			}*/
			break;
		case CITYLIFT:
//			switchContent(cityLiftFragment);
			break;
		case GAMEWORLD:
			switchContent(gameWorldFragment);
			break;
		case RAILTRAVEL:
			switchContent(railTravelFragment);
			if (menuFragment != null) {
				menuFragment.change2Alliance();
			}
			break;
		case POPULARAPP:
//			switchContent(popurlarAppFragment);
			break;
		case CITYINTRODUCTION:
			switchContent(mCityIntrodutionFragment);
			/*if (mCityIntrodutionFragment != null) {
				((CityIntroductionFragment) mCityIntrodutionFragment).requestCity();
			}*/
			
			if (menuFragment != null) {
				menuFragment.change2CityIntro();
			}
			break;
		}
	}
	
	@Override
	public void showContent() {
		// TODO Auto-generated method stub
		Log.e(TAG,"showContent in - devID:" + Info.getDevId(this));
		back_pressed = 0;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}
	
	@Override
	public void onBackPressed(){
		Log.d("=AAA=","onBackPressed in ");
		if (currContentFragment != homePageFragment) {
			switchContent(homePageFragment);
			if (menuFragment != null) {
				menuFragment.change2HomePage();
			}
			return;
		}
		if(back_pressed + 2000 > System.currentTimeMillis()){
			super.onBackPressed();
//	           android.os.Process.killProcess(android.os.Process.myPid());  
//	           System.exit(0);  
		}else{
			Toast.makeText(getBaseContext(), R.string.double_click_to_exit, Toast.LENGTH_SHORT).show();
		}
		
		back_pressed = System.currentTimeMillis();
	}
	
	@Override
	public void showMenu() {
		// TODO Auto-generated method stub
		Log.d(TAG,"showMenu in");
		super.showMenu();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		/*case Requester.RESPONSE_TYPE_REGISTERINFO:
			if (msg.obj != null) {
				GsonResponseObject.registerInfoResp r1 = (GsonResponseObject.registerInfoResp)(msg.obj);
				if(r1!=null && r1.status.equals("0")){
		        	Passenger passenger = new Passenger(null, Info.getDevId(this), Info.getDevId(this), r1.nick_name, "", "", "1", "");
		        	passengerDao.deleteAll();
		        	passengerDao.insert(passenger);
		        	if (menuFragment != null ) {
		        		menuFragment.setNickName(r1.nick_name);
		        	}
		        	CmmobiClickAgentWrapper.setUserid(this, Info.getDevId(this));
		        	Log.d("DaoExample", "Inserted new Passenger, ID: " + passenger.getId() + ", nick:" + r1.nick_name);	
				} else {
					
				}
			} else {
				
			}
			break;*/
		case CRMRequester.RESPONSE_TYPE_VERSION_CHECK:
			CRM_Object.versionCheckResponse versionResp = (versionCheckResponse) msg.obj;
			SettingActivity.doingCheckVersion(this, versionResp, false);
			break;
		case Requester.RESPONSE_TYPE_BASE_INFO:
			train_num = null;
			dev_id = null;
			railway_name = null;
			if (msg.obj != null) {
				BaseInfoResp resp = (BaseInfoResp) msg.obj;
				if ("0".equals(resp.status)) {
					train_num = resp.train_num;
					dev_id = resp.dev_id;
					railway_name = resp.railway_name;
					dev_mac = resp.dev_mac;
					
					Requester.requestMileage(handler, train_num, "", dev_id); //2.1.54 添加里程信息 （云端） 
					
					if (dev_mac != null && !dev_mac.equals(last_dev_mac)) {
						CmmobiClickAgentWrapper.onEvent(this, "s_mac",dev_mac);
					}
					Log.d("=AAA=","dev_mac = " + dev_mac);
					last_dev_mac = dev_mac;
					//T86-20141226
					List<String> tags = new ArrayList<String>();

					String tag = train_num + "-" + DateUtils.getDayString();
					Log.e(TAG, "===========show======= tag:" + tag);
					tags.add(tag);
					CmmobiPush.getConfigBuilder().coverTags(tags);
					CmmobiPush.getConfigBuilder().commit();
					SharedPreferences mySharedPreferences= getSharedPreferences("base_info", 
							Activity.MODE_PRIVATE); 
					SharedPreferences.Editor editor = mySharedPreferences.edit(); 
					//用putString的方法保存数据 
					editor.putString("train_num", resp.train_num); 
					editor.putString("dev_id", resp.dev_id);
					editor.putString("railway_name", resp.railway_name);
					//提交当前数据 
					editor.commit(); 
					if (railServiceFragment != null) {
						if (resp.railway_name != null) {
							((RailServiceFragment) railServiceFragment).setTitleText(resp.railway_name + "欢迎您"); 
						} else {
							((RailServiceFragment) railServiceFragment).setTitleText(getString(R.string.app_name) + "欢迎您"); 
						}
					}
					
					EventBus.getDefault().post(NetworkEvent.NET_RAILWIFI);
					
				} else {
					if (dev_mac == null && last_dev_mac != null) {
						CmmobiClickAgentWrapper.onEvent(this, "s_mac","000000000000");
					}
					last_dev_mac = null;
					EventBus.getDefault().post(NetworkEvent.NET_OTHERS);
				}
			} else {
				if (dev_mac == null && last_dev_mac != null) {
					CmmobiClickAgentWrapper.onEvent(this, "s_mac","000000000000");
				}
				last_dev_mac = null;
				EventBus.getDefault().post(NetworkEvent.NET_OTHERS);
			}
			break;
		case Requester.RESPONSE_TYPE_MILEAGE:
			if (msg.obj != null) {
				GsonResponseObject.MileageResp mileageRes = (MileageResp) msg.obj;
				if ("0".equals(mileageRes.status)) {
//					mileageRecordDao
					if(mileageRes.list != null) {
						mileageRecordDao.deleteAll();
						List<MileageRecord> entities = new ArrayList<MileageRecord>();
						for(TrainInfo info : mileageRes.list) {
							if(info != null) {
								MileageRecord m = new MileageRecord();
								m.setStarting(info.starting);
								m.setEnding(info.ending);
								m.setMileage(info.mileage);
								m.setHours(info.hours);
								m.setDate(info.date);
								m.setTrain_num(info.train_num);
								m.setPoints(info.points);
								entities.add(m);
							}
						}
						mileageRecordDao.insertOrReplaceInTx(entities);
					}
				}
			}
			break;
	
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		DaoMaster daoMaster;
		DaoSession daoSession;
		PassengerDao passengerDao;
		SQLiteDatabase db;
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        passengerDao = daoSession.getPassengerDao();
        List<Passenger> passList = passengerDao.queryBuilder().where(PassengerDao.Properties.Islogin.eq(true)).list();
        
        if(!passList.isEmpty()&& menuFragment != null){
        	Passenger passenger = passList.get(0);
        	menuFragment.setNickName(passenger.getNick_name());
        	menuFragment.setSex(passenger.getSex());
        	if(passenger.getIssign() && DateUtils.isToday(passenger.getSigntime())){
        		menuFragment.setLoginStatus(true, true);
        	}else{
        		menuFragment.setLoginStatus(true, false);
        	}
        }else{
        	//Requester.requestRegisterInfo(handler, Info.getDevId(this), "", "", "", "", "");
        	Passenger passenger;
        	if(passengerDao.count() != 1){
				passenger = new Passenger(null, null, Info.getDevId(this).substring(12, 23), null, null, null, null, null, null, null, null, null, false, false, null);
	        	passengerDao.deleteAll();
	        	passengerDao.insert(passenger);
			}else{
				passenger = passengerDao.loadAll().get(0);
			}
        	if (menuFragment != null ) {
        		menuFragment.setNickName(passenger.getNick_name());
        		menuFragment.setLoginStatus(false, false);
        	}
        }
        
        db.close();
	}
}
