package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.login.BindingMobileNoActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.VideoCoverUploader;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.EmojiPaser;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.fragment.MyZoneFragment;
import com.cmmobi.looklook.fragment.SettingFragment;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.wheeltime.WheelMaintime;
import com.cmmobi.sns.utils.wheel.widget.OnWheelChangedListener;
import com.cmmobi.sns.utils.wheel.widget.OnWheelScrollListener;
import com.cmmobi.sns.utils.wheel.widget.WheelView;
import com.cmmobi.sns.utils.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.XUtils;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 个人信息
 * 
 * @author youtian
 * 
 */
public class SettingPersonalInfoActivity extends ZActivity {
	private final String TAG = "SettingPersonalInfoActivity";
	private ImageView iv_back;
	
	private RelativeLayout rl_photo; //头像
	private ImageView iv_photo;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	// 手机账号绑定
	private RelativeLayout rl_phonebind;
	private TextView tv_phonebindstate;
	private TextView tv_phonebindstate_bind;
	private MyBind phonebindstate = null;
	private LoginSettingManager lsm;
	private ActiveAccount activeAccount;
	
	private RelativeLayout rl_nickname; //昵称
	private TextView tv_nickname; 
	private TextView tv_nickname_warn;
	
	private RelativeLayout rl_sex; //性别
	private TextView tv_sex;
	private ImageView iv_sex;
	
	private RelativeLayout rl_birthday; //生日
	private TextView tv_birthday;
	
	private RelativeLayout rl_area; //地区
	private TextView tv_area;
	
	private RelativeLayout rl_signature; //签名
	private TextView tv_signature;

	private AccountInfo accountInfo; //当前账户的信息
	
	private String nickname;
	private String sex;
	private String photourl;
	private String area;
	private String birthday;
	private String signature;
	
	private PopupWindow pw_photo; //头像设置
	private final int REQUEST_CAMERA = 0x123401;
	private final int REQUEST_PHOTO = 0x123402;
	private final int REQUEST_CROP = 0x123403;
	
	private final int REQUEST_NICKNAME = 0x123404;
	private final int REQUEST_SIGNATURE = 0x123405;
	private final int RESULT_CODE_PHOTE_CLIP = 0x123406;
	
	private PopupWindow pw_sex; //设置性别
	private WheelView wv_sex;
	private SexAdapter sexAdapter;
	
	private PopupWindow pw_birthday; //设置生日
	private WheelMaintime time;
	
	private PopupWindow pw_area; //设置地区
	private WheelView wv_province;
	private WheelView wv_district;
	
	public boolean scrolling = false;
	private String province;
	private String district;
	private AreaAdapter provinceAdapter;
	private AreaAdapter districtAdapter;
	
	
	private LayoutInflater inflater;
	
	private String uid;
	
	private LocalBroadcastManager lbc;
	
	private static String ADDRESS_CODE_FILE = "addresscodeforsetting.xml"; //

	private Context context;
	
	private String coverImagePath ;
//	private File webimage_file ;
	
	private int crop = 480;
	public Uri imageFilePath;
	
	private myReceiver receiver;
	
	public static String PORTRAIT_UPLOAD_SUCCESS = "PORTRAIT_UPLOAD_SUCCESS";
	private final String MIME_TYPE_JPEG = "image/jpeg";
	private String isFromPrompt = null;
	private boolean isHeadShow = false;
	
	private class myReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action=intent.getAction();
			String picurl = intent.getExtras().getString("picurl");
			//System.out.println("====picurl==" + picurl);
			ZDialog.dismiss();
			if(PORTRAIT_UPLOAD_SUCCESS.equals(action)){//个人信息
				
				if (coverImagePath != null && picurl != null) {
					/**
					 * 2、注册MediaMapping
					 */
					// 上传图片成功,将头像文件在mediamapping中注册
					File currentFile = new File(coverImagePath);
					MediaValue mv = new MediaValue();
					long fileSize = currentFile.length();
					mv.Belong = 1;
					mv.MediaType = 2;
					mv.localpath = coverImagePath.replace(Environment.getExternalStorageDirectory().getPath(), "");
					mv.url = picurl;
					mv.UID = ActiveAccount.getInstance(SettingPersonalInfoActivity.this).getUID();
					mv.realSize = fileSize;
					mv.totalSize = fileSize;
					mv.Sync = 1;
					mv.SyncSize = fileSize;
					accountInfo.mediamapping.setMedia(ActiveAccount.getInstance(SettingPersonalInfoActivity.this).getUID(), picurl, mv);
					//imageLoader.displayImage(picurl, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(SettingPersonalInfoActivity.this).getUID(), 1);
					Intent intentUpdate = new Intent(ZoneBaseFragment.ACTION_ZONEBASE_USERINFO);
					intent.putExtra("picurl", picurl);
					accountInfo.headimageurl = picurl;
					lbc.sendBroadcast(intentUpdate);
					  File file=new File(Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/");//里面输入特定目录
					  File temp=null;	
					  File[] filelist= file.listFiles();
					  for(int i=0;i<filelist.length;i++){
						  temp=filelist[i];
						  if(temp.getName().endsWith("portrait.jpg") && !temp.getName().equals(currentFile.getName())){
							  temp.delete();//删除文件
						 }
					  }
					runOnUiThread(new Runnable() {
						public void run() {
							/*Prompt.Dialog(SettingPersonalInfoActivity.this, false, "提示", "个人信息修改成功", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									SettingPersonalInfoActivity.this.finish();
									if(getIntent().getExtras() != null && !TextUtils.isEmpty(getIntent().getExtras().getString("createvshare"))){
										//新微享
										Intent shareIntent = new Intent(SettingPersonalInfoActivity.this, ShareDiaryActivity.class);
										shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
										startActivity(shareIntent);
									}
								}
							});*/
							Prompt.Alert("个人信息修改成功");
							finish();
							if(getIntent().getExtras() != null && !TextUtils.isEmpty(getIntent().getExtras().getString("createvshare"))){
								//新微享
								Intent shareIntent = new Intent(SettingPersonalInfoActivity.this, ShareDiaryActivity.class);
								shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
								startActivity(shareIntent);
							}
						}
					});		
					
				}else{
					runOnUiThread(new Runnable() {
						public void run() {
							/*Prompt.Dialog(SettingPersonalInfoActivity.this, false, "提示", "个人信息修改失败", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									SettingPersonalInfoActivity.this.finish();
								}
							});*/
							Prompt.Alert("个人信息修改失败");
							finish();
						}
					});		
				}						
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting_personalinfo);
		context = this;
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingPersonalInfoActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SettingPersonalInfoActivity.this.finish();
				return false;
			}
		});
		
		isFromPrompt = getIntent().getStringExtra(MyZoneFragment.EXTRA_FROM_PROMPT);
		rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(this);
		rl_photo.setOnClickListener(this);
		int roundpx =  getResources().getDisplayMetrics().widthPixels * 3 / 640;
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		//imageLoader.init(ImageLoaderConfiguration.createDefault(SettingPersonalInfoActivity.this));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.kongjian_morentouxiang)
		.showImageForEmptyUri(R.drawable.kongjian_morentouxiang)
		.showImageOnFail(R.drawable.kongjian_morentouxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(roundpx <= 0 ? 3 : roundpx))
		.build();
		
		rl_nickname = (RelativeLayout) findViewById(R.id.rl_nickname);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_nickname_warn = (TextView) findViewById(R.id.tv_nickname_warn);
		rl_nickname.setOnClickListener(this);
		
		// 手机账号绑定
		rl_phonebind = (RelativeLayout) findViewById(R.id.rl_phonebind);
		rl_phonebind.setOnClickListener(this);
		tv_phonebindstate = (TextView) findViewById(R.id.tv_phonebindstate);
		tv_phonebindstate_bind = (TextView) findViewById(R.id.tv_phonebindstate_bind);
		
		rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		iv_sex = (ImageView) findViewById(R.id.iv_sex);
		iv_sex.setVisibility(View.INVISIBLE);
		rl_sex.setOnClickListener(this);
		
		rl_birthday = (RelativeLayout) findViewById(R.id.rl_birthday);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		rl_birthday.setOnClickListener(this);
		
		rl_area = (RelativeLayout) findViewById(R.id.rl_area);
		tv_area =(TextView) findViewById(R.id.tv_area);
		rl_area.setOnClickListener(this);
		
		rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
		tv_signature = (TextView) findViewById(R.id.tv_signature);
		rl_signature.setOnClickListener(this);
		
		inflater = LayoutInflater.from(this);
	
		uid = ActiveAccount.getInstance(this).getUID();
		
		lbc = LocalBroadcastManager.getInstance(this);
		
		activeAccount = ActiveAccount.getInstance(this);
		
		accountInfo = AccountInfo.getInstance(uid);
		if (accountInfo != null) {
			
			TextView mishare = (TextView) findViewById(R.id.personal_account_notbind);
			mishare.setText(accountInfo.mishare_no);
			
			sex = accountInfo.sex;// 性别
			birthday = accountInfo.birthdate;// 生日
			area = accountInfo.address;// 地区
			photourl = accountInfo.headimageurl;// 头像地址
			tv_birthday.setText(DateUtils.getStringFromMilli(birthday, DateUtils.DATE_FORMAT_NORMAL_1));
			
			lsm = accountInfo.setmanager;
			// 手机账号绑定
			phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (phonebindstate != null && phonebindstate.binding_info != null) {
				tv_phonebindstate_bind.setText(phonebindstate.binding_info);
				tv_phonebindstate.setVisibility(View.GONE);
				tv_phonebindstate_bind.setVisibility(View.VISIBLE);
			}
			if (sex != null) {
				if (sex.equals("0")) {
					tv_sex.setText("男");
					iv_sex.setVisibility(View.VISIBLE);
					iv_sex.setBackgroundResource(R.drawable.del_mapnan);
				} else if (sex.equals("1")) {
					tv_sex.setText("女");
					iv_sex.setVisibility(View.VISIBLE);
					iv_sex.setBackgroundResource(R.drawable.del_mapnv);
				} else{
					iv_sex.setVisibility(View.INVISIBLE);
					tv_sex.setText("保密");
				}
			}
			try {
				tv_area.setText(getAddress(area));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageLoader.displayImageEx(photourl, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			ViewTreeObserver vto2 = iv_photo.getViewTreeObserver();   
			vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
				@Override   
				public void onGlobalLayout() {
					if (!isHeadShow) {
						imageLoader.displayImageEx(photourl, iv_photo, options, animateFirstListener, uid, 1);
						isHeadShow = true;
					}
				}   
			});
			nickname = accountInfo.nickname;// 昵称	
			signature = accountInfo.signature;// 个性签名	

			FriendsExpressionView.replacedExpressions(nickname, tv_nickname);
			if (nickname != null && !"".equals(nickname)) {
				tv_nickname_warn.setVisibility(View.GONE);
			}
			tv_signature.setText(signature);
			FriendsExpressionView.replacedExpressions(signature, tv_signature);
		}
				
		 initPhotoChoice(); //初始化头像选择菜单
		 initSexChoice(); //初始化性别选择菜单
		 initBirthdayChoice(); //初始化生日选择菜单
		 initAreaChoice(); //初始化地区选择菜单
		 
		 IntentFilter filter=new IntentFilter();
			filter.addAction(PORTRAIT_UPLOAD_SUCCESS);
			receiver=new myReceiver();
			LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
			
	}
	
	@Override
	public void onResume() {
		if (lsm != null) {
			phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (phonebindstate != null && phonebindstate.binding_info != null) {
				tv_phonebindstate_bind.setText(phonebindstate.binding_info);
				tv_phonebindstate.setVisibility(View.GONE);
				tv_phonebindstate_bind.setVisibility(View.VISIBLE);
			}
		}
		tv_signature.setText(signature);
		FriendsExpressionView.replacedExpressions(signature, tv_signature);
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(receiver!=null)
			LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}

 

	// 显示头像设置选择界面
	private void initPhotoChoice() {
		View view = inflater.inflate(R.layout.activity_setting_photo_menu,
				null);
		pw_photo = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pw_photo.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.del_dot_big));
		view.findViewById(R.id.btn_from_camera).setOnClickListener(this);
		view.findViewById(R.id.btn_from_pictures).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
	}
	
	//显示性别选择界面
	private void initSexChoice(){
		View view = inflater.inflate(R.layout.menu_setting_sex,
				null);
		pw_sex = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_sex.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		view.findViewById(R.id.tv_back_sex).setOnClickListener(this);
		view.findViewById(R.id.tv_complete_sex).setOnClickListener(this);
		
		wv_sex = (WheelView) view.findViewById(R.id.wv_sex);
		try {
			ArrayList<String> sexList = new ArrayList<String>();
			sexList.add("男");
			sexList.add("女");
			sexList.add("保密");
			sexAdapter = new SexAdapter(this, sexList);
		
			wv_sex.setViewAdapter(sexAdapter);
	
			wv_sex.addChangingListener(new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					if (!scrolling) {
//						updateDistricts(sexAdapter.getItemText(newValue));
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*view.findViewById(R.id.btn_sexman).setOnClickListener(this);
		view.findViewById(R.id.btn_sexwoman).setOnClickListener(this);
		view.findViewById(R.id.btn_sexsecret).setOnClickListener(this);
		view.findViewById(R.id.btn_sexcancel).setOnClickListener(this);*/
	}
	
	//显示日期选择界面
	private void initBirthdayChoice(){
		View view = inflater.inflate(R.layout.activity_setting_birthday_menu,
				null);
		pw_birthday = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_birthday.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		time = new WheelMaintime(view);
		if (TextUtils.isEmpty(birthday)) {
			time.initDateTimePicker();
		} else {
			try {
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(Long.parseLong(birthday));
				
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);
				time.initDateTimePicker(year, month, day);
			} catch (Exception e) {
				time.initDateTimePicker();
			}
			
		}
		
		view.findViewById(R.id.tv_commit_birthday).setOnClickListener(this);
		view.findViewById(R.id.tv_back_birthday).setOnClickListener(this);
	}


	private class AreaAdapter extends AbstractWheelTextAdapter {
		private NodeList area;

		protected AreaAdapter(Context context, NodeList a) {
			super(context, R.layout.setting_area_wv_layout, NO_RESOURCE);
			this.area = a;
			setItemTextResource(R.id.tv_areaname);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return area.getLength();
		}

		@Override
		protected String getItemText(int index) {
			return ((Element)(area.item(index))).getAttribute("name");
		}
	}
	
	private class SexAdapter extends AbstractWheelTextAdapter {
		private List<String> sexList;

		protected SexAdapter(Context context, List<String> a) {
			super(context, R.layout.setting_sex_wv_layout, NO_RESOURCE);
			this.sexList = a;
			setItemTextResource(R.id.tv_sexname);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return sexList.size();
		}

		@Override
		protected String getItemText(int index) {
			return sexList.get(index);
		}
	}
	
	private void updateDistricts(String p){
		try {
			districtAdapter = null;
			districtAdapter = new AreaAdapter(context, getDistricts(p));
			wv_district.setViewAdapter(districtAdapter);
	        wv_district.setCurrentItem(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//显示地区选择界面
	private void initAreaChoice(){
		View view = inflater.inflate(R.layout.activity_setting_area_menu,
				null);
		pw_area = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_area.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		view.findViewById(R.id.tv_commit_area).setOnClickListener(this);
		view.findViewById(R.id.tv_back_area).setOnClickListener(this);
		wv_province = (WheelView) view.findViewById(R.id.wv_province);
		wv_district = (WheelView) view.findViewById(R.id.wv_district);
		
		
		try {
			
			provinceAdapter = new AreaAdapter(this, getProvinces());
		
		wv_province.setViewAdapter(provinceAdapter);

		wv_district.setVisibleItems(5);

		wv_province.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateDistricts(provinceAdapter.getItemText(newValue));
				}
			}
		});

		// 城市
		wv_province.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateDistricts(provinceAdapter.getItemText(wv_province.getCurrentItem()));

			}
		});
		wv_province.setCurrentItem(0);
		updateDistricts(provinceAdapter.getItemText(wv_province.getCurrentItem()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_photo:
			Intent showphoto = new Intent(SettingPersonalInfoActivity.this, SettingPortraitShowActivity.class);
			if (!TextUtils.isEmpty(coverImagePath)) {
				showphoto.putExtra("imageUrl", "file:///" + coverImagePath);
			}
			if (options.getKey() != null) {
				showphoto.putExtra("cachememorykey", options.getKey());
			}
			startActivity(showphoto);
			overridePendingTransition(R.anim.zoomin, R.anim.del_zoomout);
			break;
		case R.id.rl_photo:
			CmmobiClickAgent.onEvent(this, "choose_ava");
			pw_photo.showAtLocation(findViewById(R.id.rl_personalinfo),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.rl_phonebind:
			if (phonebindstate != null
				&& phonebindstate.binding_info != null) {
//				Prompt.Dialog(context, false, "提示", "抱歉，当前登录的账号不可解绑定", null);
				
			} else {
				SettingFragment.showContactUpload(context, BindingMobileNoActivity.class, handler,isFromPrompt,true);
			}
			break;
		case R.id.btn_from_camera:
			if(pw_photo.isShowing()){
				pw_photo.dismiss();
			}
			{
				imageFilePath = setUri();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
				startActivityForResult(intent, REQUEST_CAMERA);
			}
			break;
		case R.id.btn_from_pictures:
			if(pw_photo.isShowing()){
				pw_photo.dismiss();
			}
			{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.setType("image/jpeg");
				startActivityForResult(intent, REQUEST_PHOTO);
			}
			break;
		case R.id.btn_cancel:
			if(pw_photo.isShowing()){
				pw_photo.dismiss();
			}
			break;
		case R.id.rl_nickname:
			Intent intentnickname = new Intent(this, SettingNicknameModifiActivity.class);
			intentnickname.putExtra("nickname", nickname);
			startActivityForResult(intentnickname, REQUEST_NICKNAME);
			break;
		case R.id.rl_sex:
			pw_sex.showAtLocation(findViewById(R.id.rl_personalinfo),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.tv_back_sex:
			if(pw_sex.isShowing()){
				pw_sex.dismiss();
			}
			break;
		case R.id.tv_complete_sex:
			if(pw_sex.isShowing()){
				pw_sex.dismiss();
			}
			String sexStr = sexAdapter.getItemText(wv_sex.getCurrentItem());
			if ("男".equals(sexStr)) {
				tv_sex.setText("男");
				iv_sex.setVisibility(View.VISIBLE);
				iv_sex.setBackgroundResource(R.drawable.del_mapnan);
				sex = "0";
			} else if ("女".equals(sexStr)) {
				tv_sex.setText("女");
				iv_sex.setVisibility(View.VISIBLE);
				iv_sex.setBackgroundResource(R.drawable.del_mapnv);
				sex = "1";
			} else if ("保密".equals(sexStr)) {
				tv_sex.setText("保密");
				iv_sex.setVisibility(View.INVISIBLE);
				sex = "2";
			}
			
			// 2014-4-8
			if(sex != null){
				if(sex.equals("0")){
					CmmobiClickAgentWrapper.onEvent(context, "choose_gen", "1");
				}else if(sex.equals("1")){
					CmmobiClickAgentWrapper.onEvent(context, "choose_gen", "2");
				}else{
					CmmobiClickAgentWrapper.onEvent(context, "choose_gen", "3");
				}
			}
			break;
		case R.id.btn_sexman:
			if(pw_sex.isShowing()){
				pw_sex.dismiss();
			}
			tv_sex.setText("男");
			iv_sex.setVisibility(View.VISIBLE);
			iv_sex.setBackgroundResource(R.drawable.del_mapnan);
			sex = "0";
			break;
		case R.id.btn_sexwoman:
			if(pw_sex.isShowing()){
				pw_sex.dismiss();
			}
			tv_sex.setText("女");
			iv_sex.setVisibility(View.VISIBLE);
			iv_sex.setBackgroundResource(R.drawable.del_mapnv);
			sex = "1";
			break;
		case R.id.btn_sexsecret:
			if(pw_sex.isShowing()){
				pw_sex.dismiss();
			}
			tv_sex.setText("保密");
			iv_sex.setVisibility(View.INVISIBLE);
			sex = "2";
			break;
		case R.id.btn_sexcancel:
			if(pw_sex.isShowing()){
				pw_sex.dismiss();
			}
			break;
		case R.id.rl_birthday:
			pw_birthday.showAtLocation(findViewById(R.id.rl_personalinfo),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.tv_commit_birthday:
			if(pw_birthday.isShowing()){
				pw_birthday.dismiss();
			}
			String bir = time.getTime();
			tv_birthday.setText(bir);
			birthday = Long.toString(DateUtils.stringToDate(bir, DateUtils.DATE_FORMAT_NORMAL_1).getTime());
			// 2014-4-8
			if(birthday !=null && !birthday.equals("")){
				CmmobiClickAgentWrapper.onEvent(context, "choose_age", bir.substring(0, 4));
			}
			
			break;
		case R.id.tv_back_birthday:
			if(pw_birthday.isShowing()){
				pw_birthday.dismiss();
			}
			break;
		case R.id.rl_area:
			pw_area.showAtLocation(findViewById(R.id.rl_personalinfo),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.tv_commit_area:
			if(pw_area.isShowing()){
				pw_area.dismiss();
			}
			province = provinceAdapter.getItemText(wv_province.getCurrentItem());
			district = districtAdapter.getItemText(wv_district.getCurrentItem());
			try {
				area = getAreaCode(province, district);
				tv_area.setText(province + " " + district);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 2014-4-8
			if(area !=null && province!=null && district != null){
				CmmobiClickAgentWrapper.onEvent(context, "choose_loc", area);
			}
			break;
		case R.id.tv_back_area:
			if(pw_area.isShowing()){
				pw_area.dismiss();
			}
			break;
		case R.id.rl_signature:
			Intent it = new Intent(this, SettingSignatureActivity.class);
			it.putExtra("signature", signature);
			startActivityForResult(it, REQUEST_SIGNATURE);
			break;
		case R.id.iv_back:
			processDone();
			break;
		}
	}
	
	private void processDone() {
		/*if (nickname == null || "".equals(nickname)) {
			Prompt.Alert("需要填写昵称");
		} else {*/
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "edit_done");
			
			if (isPersonalInfoChanged()) {
				if (!ZNetworkStateDetector.isAvailable()) {
					Prompt.Alert("网络不给力，保存不成功");
					finish();
					return;
				}
				ZDialog.show(R.layout.progressdialog, false, true, this);
				if (signature != null) {
					signature = EmojiPaser.getInstance().format(signature);
				}
				
				Requester3.changeUserInfo(handler, nickname, signature, sex, area, birthday);
			} else if (coverImagePath != null) {
				if (!ZNetworkStateDetector.isAvailable()) {
					Prompt.Alert("网络不给力，保存不成功");
					finish();
					return;
				}
				ZDialog.show(R.layout.progressdialog, false, true, this);
				VideoCoverUploader.uploadParam upload = new VideoCoverUploader.uploadParam();
				upload.filetype = "4";
				upload.mfile = new File(coverImagePath);
				upload.rotation = 0;
				upload.businesstype = "4";
				VideoCoverUploader videoCoverUploader = new VideoCoverUploader(upload);
				videoCoverUploader.excute();
			} else {
				finish();
			}
		}
//	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		processDone();
	}
	
	private boolean isPersonalInfoChanged() {
		if (accountInfo == null) {
			return false;
		}
		if (!isEquals(nickname, accountInfo.nickname)/*nickname.equals(accountInfo.nickname)*/
				|| !isEquals(sex,accountInfo.sex)
				|| !isEquals(birthday,accountInfo.birthdate)
				|| !isEquals(area,accountInfo.address)
				|| !isEquals(signature,accountInfo.signature)) {
			return true;
		}
		return false;
	}
	
	private boolean isEquals(String pre,String next) {
		if (pre == null && next == null) {
			return true;
		} else if (pre == null || next == null) {
			return false;
		} else {
			return pre.equals(next);
		}
	}
	
	private Uri setUri() {
		String imageID = DiaryController.getNextUUID();
		String dir = DiaryController.getAbsolutePathUnderUserDir() + "/pic/";
		File destDir = new File(dir);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String outputFilePath = dir + imageID + ".jpg" ;
		File out = new File(outputFilePath);
		Uri uri = Uri.fromFile(out);// 将路径转化为uri

		return uri;
	}	
		
	/**
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", crop);
		intent.putExtra("outputY", crop);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, REQUEST_CROP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CAMERA:
				ZDialog.show(R.layout.progressdialog, false, true, this);
				/*try {
					startPhotoZoom(Uri.fromFile(webimage_file));
				} catch (Exception e) {
					// TODO: handle exception
				}*/
				if (imageFilePath != null) {
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String imagaPath = imageFilePath.getPath();
							startImageClipActivity(imagaPath);
							ZDialog.dismiss();
						}
					}, 1);
					
				}
				
				break;
			case REQUEST_PHOTO:
				/*FileInputStream fis;
				try {
					fis = new FileInputStream(path);
					Bitmap bitmap  = BitmapFactory.decodeStream(fis);		
					iv_photo.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	*/
				Uri uri = data.getData();
				
				if (uri != null) {
					String[] proj = {MediaStore.Images.Media.DATA,MediaStore.Images.Media.MIME_TYPE,
							MediaStore.Images.Media.SIZE,};
	
					Cursor cursor = managedQuery(uri, proj, null, null, null);
					if (cursor != null) {
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						//将光标移至开头 ，这个很重要，不小心很容易引起越界
						cursor.moveToFirst();
						//最后根据索引值获取图片路径
						String path = cursor.getString(column_index);
						String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
						String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
						if (Long.valueOf(size) > 4096l
								&& (MIME_TYPE_JPEG.equals(mime_type))) {
							startImageClipActivity(path);
						} else if (!MIME_TYPE_JPEG.equals(mime_type)) {
							Prompt.Dialog(this, false, "提示", "只支持jpeg格式图片！", null);
						} else {
							Prompt.Dialog(this, false, "提示", "图片太小,可能是图标。", null);
						}
						
						Log.d(TAG,"path = " + path + " mime_type = " + mime_type + " size = " + size);
					}
				}
				break;
			case RESULT_CODE_PHOTE_CLIP:
				if (data != null) {
					coverImagePath = data.getStringExtra(PhotoClipActivity.CLIP_PATH);
					/*if (coverImagePath != null) {
						uploadCover(coverImagePath);
					}*/
					FileInputStream fis;
//					try {
//						fis = new FileInputStream(coverImagePath);
//						Bitmap bitmap  = BitmapFactory.decodeStream(fis);	
//						bitmap = ZGraphics.rotate(bitmap, XUtils.getExifOrientation(coverImagePath), true);
//						iv_photo.setImageBitmap(bitmap);
						imageLoader.displayImageEx("file:///" + coverImagePath, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
						
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
				break;
			/*case REQUEST_CROP:
				FileInputStream fiscamera;
				try {
					fiscamera = new FileInputStream(path);
					Bitmap bmp  = BitmapFactory.decodeStream(fiscamera);		
					iv_photo.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;*/
			case REQUEST_NICKNAME:
				if(data.getExtras() != null ){
				 String result = data.getExtras().getString("newnickname");
				 FriendsExpressionView.replacedExpressions(result, tv_nickname);
				 nickname = result;
				 tv_nickname_warn.setVisibility(View.GONE);
				}
				break;
			case REQUEST_SIGNATURE:
				if(data.getExtras() != null){
				 String res = data.getExtras().getString("newsignature");
				 tv_signature.setText(res);
				 signature = res;
				}
				break;
			}
		}else if(resultCode == Activity.RESULT_CANCELED){
			switch (requestCode) {
			case REQUEST_CAMERA:
			case REQUEST_PHOTO:
//			case REQUEST_CROP:
				coverImagePath = null;
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}
	
	private void startImageClipActivity(String path) {
		Intent intent = new Intent(this, PhotoClipActivity.class);
		intent.putExtra(PhotoClipActivity.PHOTO_PATH, path);
		startActivityForResult(intent, RESULT_CODE_PHOTE_CLIP);
	}

/*    private String getPortraitToSDcard(byte[] data){
    	try {
    		String path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + "portrait.jpg";
			File webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			FileOutputStream mOutput = new FileOutputStream(webimage_file, false);
			mOutput.write(data);
			mOutput.close();
			return path;
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return null;
    }*/
	
	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_CHANGE_USER_INFO: //修改用户信息
			if(coverImagePath == null){
				ZDialog.dismiss();
			}
			try {
				GsonResponse3.changeUserInfoResponse res = (GsonResponse3.changeUserInfoResponse) msg.obj;
				if(res != null ){
					if ("0".equals(res.status)) {
						accountInfo.nickname = nickname;
						accountInfo.birthdate = birthday;
						accountInfo.address = area;
						accountInfo.sex = sex;
						accountInfo.signature = signature;
						
						if(coverImagePath != null){
							VideoCoverUploader.uploadParam upload = new VideoCoverUploader.uploadParam();
							upload.filetype = "4";
							upload.mfile = new File(coverImagePath);
							upload.rotation = 0;
							upload.businesstype = "4";
							VideoCoverUploader videoCoverUploader = new VideoCoverUploader(upload);
							videoCoverUploader.excute();
						}else{
							Prompt.Alert("个人信息修改成功");
							Intent intentUpdate = new Intent(ZoneBaseFragment.ACTION_ZONEBASE_USERINFO);
							lbc.sendBroadcast(intentUpdate);
							setResult(RESULT_OK);
							finish();
							if(getIntent().getExtras() != null && !TextUtils.isEmpty(getIntent().getExtras().getString("createvshare"))){
								//新微享
								Intent shareIntent = new Intent(SettingPersonalInfoActivity.this, ShareDiaryActivity.class);
								shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
								startActivity(shareIntent);
							}
							/*Prompt.Dialog(SettingPersonalInfoActivity.this, false, "提示", "个人信息修改成功", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									Intent intentUpdate = new Intent(ZoneBaseFragment.ACTION_ZONEBASE_USERINFO);
									lbc.sendBroadcast(intentUpdate);
									SettingPersonalInfoActivity.this.finish();
									if(getIntent().getExtras() != null && !TextUtils.isEmpty(getIntent().getExtras().getString("createvshare"))){
										//新微享
										Intent shareIntent = new Intent(SettingPersonalInfoActivity.this, ShareDiaryActivity.class);
										shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
										startActivity(shareIntent);
									}
								}
							});*/
						}
					
					}else if(res.status.equals("200600")){
						Prompt.Alert(Constant.CRM_STATUS[Integer.parseInt(res.crm_status)]);
						finish();
//						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], null);
					}else{
						Prompt.Alert("操作失败，请稍后再试");
						finish();
//						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Alert("网络不给力，保存不成功");
					finish();
//					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
					ZDialog.dismiss();
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}

		return false;
	}
	


	/*
	 * 通过国际码查找地区的名称，无则返回null
	 * @c : addresscode
	 * 
	 */
	public String getAddress(String c)
			throws Exception {
		String address = null;
        DocumentBuilderFactory factory=null;
        DocumentBuilder builder=null;
        Document document=null;
        InputStream inputStream=null;
        if(c == null) {
        	return null;
        }
        //首先找到xml文件
        factory=DocumentBuilderFactory.newInstance();
		try {
            //找到xml，并加载文档
            builder=factory.newDocumentBuilder();
            inputStream=context.getResources().getAssets().open(ADDRESS_CODE_FILE);
            document=builder.parse(inputStream);

            //找到根Element
            Element root = (Element) document.getDocumentElement();
            NodeList nodes=root.getElementsByTagName("province");
            for(int i=0;i<nodes.getLength();i++){
            	Element provinceElement=(Element)(nodes.item(i));
            	NodeList disnodes = provinceElement.getElementsByTagName("district");
        		for(int j=0;j<disnodes.getLength();j++) {
        			Element district=(Element)(disnodes.item(j));
        			if(c.equals(district.getAttribute("id"))) {
        				address = provinceElement.getAttribute("name") + " " + district.getAttribute("name");
        				//System.out.println("====" + c + "========" + address + "======");
        				return address;
        			}
        		}
            	
            }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return address;
	}
	
	

	/*
	 * 遍历查找是否有该城市信息，有则返回国际码，无则返回null
	 * @p : province
	 * @d : district
	 * 
	 */
	public String getAreaCode(String p, String d)
			throws Exception {
		String addresscode = null;
        DocumentBuilderFactory factory=null;
        DocumentBuilder builder=null;
        Document document=null;
        InputStream inputStream=null;
        if(p == null || d == null) {
        	return null;
        }
        //首先找到xml文件
        factory=DocumentBuilderFactory.newInstance();
		try {
            //找到xml，并加载文档
            builder=factory.newDocumentBuilder();
            inputStream=context.getResources().getAssets().open(ADDRESS_CODE_FILE);
            document=builder.parse(inputStream);

            //找到根Element
            Element root = (Element) document.getDocumentElement();
            NodeList nodes=root.getElementsByTagName("province");
            for(int i=0;i<nodes.getLength();i++){
            	Element provinceElement=(Element)(nodes.item(i));
            	if(p.equals(provinceElement.getAttribute("name"))) {//城市名匹配
            		NodeList disnodes = provinceElement.getElementsByTagName("district");
            		for(int j=0;j<disnodes.getLength();j++) {
            			Element district=(Element)(disnodes.item(j));
            			if(d.equals(district.getAttribute("name"))) {
            				addresscode = district.getAttribute("id");
            				//System.out.println("====" + p + "==" + d + "======" + addresscode + "======");
            				return addresscode;
            			}
            		}
            	}
            }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return addresscode;
	}
	

	/*
	 * 返回所有的province NodeList
	 * 
	 */
	public NodeList getProvinces()
			throws Exception {
        DocumentBuilderFactory factory=null;
        DocumentBuilder builder=null;
        Document document=null;
        InputStream inputStream=null;
        //首先找到xml文件
        factory=DocumentBuilderFactory.newInstance();
		try {
            //找到xml，并加载文档
            builder=factory.newDocumentBuilder();
            inputStream=context.getResources().getAssets().open(ADDRESS_CODE_FILE);
            document=builder.parse(inputStream);

            //找到根Element
            Element root = (Element) document.getDocumentElement();
            NodeList nodes=root.getElementsByTagName("province");
            return nodes;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 * 返回所有属于p的district
	 * @p : province
	 * 
	 */
	public NodeList getDistricts(String p)
			throws Exception {
        DocumentBuilderFactory factory=null;
        DocumentBuilder builder=null;
        Document document=null;
        InputStream inputStream=null;
        if(p == null) {
        	return null;
        }
        //首先找到xml文件
        factory=DocumentBuilderFactory.newInstance();
		try {
            //找到xml，并加载文档
            builder=factory.newDocumentBuilder();
            inputStream=context.getResources().getAssets().open(ADDRESS_CODE_FILE);
            document=builder.parse(inputStream);

            //找到根Element
            Element root = (Element) document.getDocumentElement();
            NodeList nodes=root.getElementsByTagName("province");
            for(int i=0;i<nodes.getLength();i++){
            	Element provinceElement=(Element)(nodes.item(i));
            	if(p.equals(provinceElement.getAttribute("name"))) {//城市名匹配
            		NodeList disnodes = provinceElement.getElementsByTagName("district");
            		return disnodes;
            	}
            }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
