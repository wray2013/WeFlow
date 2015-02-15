package com.cmmobi.looklook.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2.moodRequest;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.wheeltime.WheelMaintime;
import com.cmmobi.sns.utils.wheel.widget.OnWheelChangedListener;
import com.cmmobi.sns.utils.wheel.widget.OnWheelScrollListener;
import com.cmmobi.sns.utils.wheel.widget.WheelView;
import com.cmmobi.sns.utils.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.cmmobi.sns.utils.wheel.widget.adapters.ArrayWheelAdapter;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 个人信息
 * 
 * @author youtian
 * 
 */
public class SettingPersonalInfoActivity extends ZActivity {

	private ImageView iv_commit;
	private ImageView iv_back;
	
	private RelativeLayout rl_photo; //头像
	private ImageView iv_photo;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private RelativeLayout rl_nickname; //昵称
	private TextView tv_nickname; 
	
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
	private String mood;
	
	private PopupWindow pw_photo; //头像设置
	private final int REQUEST_CAMERA = 0x123401;
	private final int REQUEST_PHOTO = 0x123402;
	private final int REQUEST_CROP = 0x123403;
	
	private final int REQUEST_NICKNAME = 0x123404;
	private final int REQUEST_SIGNATURE = 0x123405;
	
	private PopupWindow pw_sex; //设置性别
	private WheelView wv_sex;
	private String sex_items[];
	
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
	
	private String path ;
	private File webimage_file ;
	
	private int crop = 480;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting_personalinfo);
		context = this;
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		
		iv_commit = (ImageView) findViewById(R.id.iv_commit);
		iv_commit.setOnClickListener(this);
		
		rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(this);
		rl_photo.setOnClickListener(this);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(SettingPersonalInfoActivity.this));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.displayer(new CircularBitmapDisplayer())
		.build();
		
		rl_nickname = (RelativeLayout) findViewById(R.id.rl_nickname);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		rl_nickname.setOnClickListener(this);
		
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
		
		accountInfo = AccountInfo.getInstance(uid);
		if (accountInfo != null) {
			sex = accountInfo.sex;// 性别
			birthday = accountInfo.birthdate;// 生日
			area = accountInfo.address;// 地区
			photourl = accountInfo.headimageurl;// 头像地址
			tv_birthday.setText(birthday);
			if (sex != null) {
				if (sex.equals("0")) {
					tv_sex.setText("男");
					iv_sex.setVisibility(View.VISIBLE);
					iv_sex.setBackgroundResource(R.drawable.mapnan);
				} else if (sex.equals("1")) {
					tv_sex.setText("女");
					iv_sex.setVisibility(View.VISIBLE);
					iv_sex.setBackgroundResource(R.drawable.mapnv);
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
			imageLoader.displayImage(photourl, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 0);

			nickname = accountInfo.nickname;// 昵称	
			signature = accountInfo.signature;// 个性签名		
			mood = accountInfo.mood; //心情

			tv_nickname.setText(nickname);
			tv_signature.setText(signature);
		}
				
		 initPhotoChoice(); //初始化头像选择菜单
		 initSexChoice(); //初始化性别选择菜单
		 initBirthdayChoice(); //初始化生日选择菜单
		 initAreaChoice(); //初始化地区选择菜单
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
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
	}


	// 显示头像设置选择界面
	private void initPhotoChoice() {
		View view = inflater.inflate(R.layout.activity_setting_photo_menu,
				null);
		pw_photo = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pw_photo.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		view.findViewById(R.id.btn_from_camera).setOnClickListener(this);
		view.findViewById(R.id.btn_from_pictures).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
	}
	
	//显示性别选择界面
	private void initSexChoice(){
		View view = inflater.inflate(R.layout.activity_setting_sex_menu,
				null);
		pw_sex = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pw_sex.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		wv_sex = (WheelView)view.findViewById(R.id.wv_sex);	
		view.findViewById(R.id.iv_commit_sex).setOnClickListener(this);
		
		sex_items = new String[] { "男","女", "保密"};
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				sex_items);
		adapter.setTextSize(18);
		wv_sex.setViewAdapter(adapter);
		wv_sex.setCurrentItem(sex_items.length / 2);

	}
	
	//显示日期选择界面
	private void initBirthdayChoice(){
		View view = inflater.inflate(R.layout.activity_setting_birthday_menu,
				null);
		pw_birthday = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pw_birthday.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		time = new WheelMaintime(view);
		time.initDateTimePicker();
		view.findViewById(R.id.iv_commit_birthday).setOnClickListener(this);
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
	
	private void updateDistricts(String p){
		try {
			districtAdapter = null;
			districtAdapter = new AreaAdapter(context, getDistricts(p));
			wv_district.setViewAdapter(districtAdapter);
	        wv_district.setCurrentItem(districtAdapter.getItemsCount() / 2);
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
				LayoutParams.WRAP_CONTENT, true);
		pw_area.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		view.findViewById(R.id.iv_commit_area).setOnClickListener(this);
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
		wv_province.setCurrentItem(3);

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
			if(path != null){
				showphoto.putExtra("path", path);
			}
			startActivity(showphoto);
			overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			break;
		case R.id.rl_photo:
			pw_photo.showAtLocation(findViewById(R.id.rl_personalinfo),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.btn_from_camera:
			if(pw_photo.isShowing()){
				pw_photo.dismiss();
			}
			path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + "portrait.jpg";
			webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(webimage_file));
			startActivityForResult(cameraIntent, REQUEST_CAMERA);
			break;
		case R.id.btn_from_pictures:
			if(pw_photo.isShowing()){
				pw_photo.dismiss();
			}
			Intent intent = new Intent();
			path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + "portrait.jpg";
			webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			intent.setType("image/*");
			intent.putExtra("output", Uri.fromFile(webimage_file));
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// 裁剪框比例
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", crop);// 输出图片大小
			intent.putExtra("outputY", crop);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_PHOTO);
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
			if (sex != null) {
				if (sex.equals("0")) {
					wv_sex.setCurrentItem(0);
				} else if (sex.equals("1")) {
					wv_sex.setCurrentItem(1);
				} else{
					wv_sex.setCurrentItem(2);
				}
			}
			break;
		case R.id.iv_commit_sex:
			if(pw_sex.isShowing()){
				pw_sex.dismiss();
			}
			if(wv_sex.getCurrentItem() == 0){
				tv_sex.setText("男");
				iv_sex.setVisibility(View.VISIBLE);
				iv_sex.setBackgroundResource(R.drawable.mapnan);
				sex = "0";
			}else if(wv_sex.getCurrentItem() == 1){
				tv_sex.setText("女");
				iv_sex.setVisibility(View.VISIBLE);
				iv_sex.setBackgroundResource(R.drawable.mapnv);
				sex = "1";
			}else{
				tv_sex.setText("保密");
				iv_sex.setVisibility(View.INVISIBLE);
				sex = "2";
			}
			break;
		case R.id.rl_birthday:
			pw_birthday.showAtLocation(findViewById(R.id.rl_personalinfo),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.iv_commit_birthday:
			if(pw_birthday.isShowing()){
				pw_birthday.dismiss();
			}
			String bir = time.getTime();
			tv_birthday.setText(bir);
			birthday = bir;
			break;
		case R.id.rl_area:
			pw_area.showAtLocation(findViewById(R.id.rl_personalinfo),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.iv_commit_area:
			if(pw_area.isShowing()){
				pw_area.dismiss();
			}
			province = provinceAdapter.getItemText(wv_province.getCurrentItem());
			district = districtAdapter.getItemText(wv_district.getCurrentItem());
			try {
				area = getAreaCode(province, district);
				tv_area.setText(province + "-" + district);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.rl_signature:
			Intent it = new Intent(this, SettingSignatureActivity.class);
			it.putExtra("fromSetting", true);
			it.putExtra("signature", signature);
			it.putExtra("mood", mood);
			startActivityForResult(it, REQUEST_SIGNATURE);
			break;
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.iv_commit:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			if(path != null){
				Requester2.uploadPicture(handler, path,"1","");
			}else{
				Requester2.changeUserInfo(handler, nickname, signature, sex, area, birthday, mood);		
			}
			break;
		}
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
				//ZDialog.show(R.layout.progressdialog, false, true, this);
				startPhotoZoom(Uri.fromFile(webimage_file));
				break;
			case REQUEST_PHOTO:
				FileInputStream fis;
				try {
					fis = new FileInputStream(path);
					Bitmap bitmap  = BitmapFactory.decodeStream(fis);		
					iv_photo.setImageBitmap(BitmapUtils.getPortraitBitmap(bitmap));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester2.uploadPicture(handler,  path,"1","");*/			
				break;
			case REQUEST_CROP:
				FileInputStream fiscamera;
				try {
					fiscamera = new FileInputStream(path);
					Bitmap bmp  = BitmapFactory.decodeStream(fiscamera);		
					iv_photo.setImageBitmap(BitmapUtils.getPortraitBitmap(bmp));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Requester2.uploadPicture(handler, path,"1","");
				break;
			case REQUEST_NICKNAME:
				if(data.getExtras() != null ){
				 String result = data.getExtras().getString("newnickname");
				 tv_nickname.setText(result);
				 nickname = result;
				}
				break;
			case REQUEST_SIGNATURE:
				if(data.getExtras() != null){
				 String res = data.getExtras().getString("newsignature");
				 tv_signature.setText(res);
				 signature = res;
				 res = data.getExtras().getString("newmood");
				 mood = res;
				}
				break;
			}
		}else if(resultCode == Activity.RESULT_CANCELED){
			switch (requestCode) {
			case REQUEST_CAMERA:
			case REQUEST_PHOTO:
			case REQUEST_CROP:
				path = null;
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

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
		case Requester2.RESPONSE_TYPE_UPLOAD_PICTURE: //上传头像
			try {
				GsonResponse2.uploadPictrue res = (GsonResponse2.uploadPictrue) msg.obj;
				if(res != null ){
					if ("0".equals(res.status)) {
						accountInfo.headimageurl = res.imageurl;
						//imageLoader.displayImage(accountInfo.headimageurl, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 0);
						Intent intent = new Intent(SettingActivity.BROADCAST_PORTRAIT_CHANGED);
						lbc.sendBroadcast(intent);
						CmmobiClickAgentWrapper.onEvent(this, "my_ avatar", "2");
					} else{
						Prompt.Alert(this, "上传头像操作失败，请稍后再试");
					}
				}else{
					Prompt.Alert(this, "上传头像操作失败，网络不给力");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			Requester2.changeUserInfo(handler, nickname, signature, sex, area, birthday, mood);		
			break;
		case Requester2.RESPONSE_TYPE_CHANGE_USER_INFO: //修改用户信息
			ZDialog.dismiss();
			try {
				GsonResponse2.changeUserInfoResponse res = (GsonResponse2.changeUserInfoResponse) msg.obj;
				if(res != null ){
					if ("0".equals(res.status)) {
						accountInfo.nickname = nickname;
						accountInfo.birthdate = birthday;
						accountInfo.address = area;
						accountInfo.sex = sex;
						accountInfo.mood = mood;
						accountInfo.signature = signature;
						Intent intent = new Intent(SettingActivity.BROADCAST_PERSONALINFO_CHANGED);
						lbc.sendBroadcast(intent);
						//埋点
						if(birthday !=null && !birthday.equals("")){
							CmmobiClickAgentWrapper.onEvent(context, "sup_year", birthday.substring(0, 4));
						}
						if(sex != null){
							if(sex.equals("0")){
								CmmobiClickAgentWrapper.onEvent(context, "sup_gender", "1");
							}else if(sex.equals("1")){
								CmmobiClickAgentWrapper.onEvent(context, "sup_gender", "2");
							}else{
								CmmobiClickAgentWrapper.onEvent(context, "sup_gender", "3");
							}
						}
						if(area !=null && province!=null && district != null){
							CmmobiClickAgentWrapper.onEvent(context, "sup_et", getAreaCode(province, district));
						}
						
						CmmobiClickAgentWrapper.onEvent(this, "my_ mood", "2");
						Prompt.Dialog(this, false, "提示", "个人信息修改成功", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingPersonalInfoActivity.this.finish();
							}
						});
						
					}else if(res.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], null);
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
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
        				address = provinceElement.getAttribute("name") + "-" + district.getAttribute("name");
        				System.out.println("====" + c + "========" + address + "======");
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
            				System.out.println("====" + p + "==" + d + "======" + addresscode + "======");
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
