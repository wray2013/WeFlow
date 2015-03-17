package com.cmmobi.railwifi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.alipay.PayInfo;
import com.cmmobi.railwifi.alipay.Result;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.Passenger;
import com.cmmobi.railwifi.dao.PassengerDao;
import com.cmmobi.railwifi.dao.TravelOrderInfo;
import com.cmmobi.railwifi.dao.TravelOrderInfoDao;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.dialog.RechargeDialog;
import com.cmmobi.railwifi.network.GsonResponseObject.RechargeResp;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.ListDialog;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2015-03-10
 */
public class UserInfoAcitivity extends TitleRootActivity {

	private String TAG = "UserMainPageAcitivity";
	private TextView tvWelcome;
	
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private PassengerDao passengerDao;
	private SQLiteDatabase db;
	private LayoutInflater inflater;
	private PopupWindow pw_photo; //头像设置
	
	private final int RESULT_CODE_PHOTE_CLIP = 0x3406;
	private final String MIME_TYPE_JPEG = "image/jpeg";

	private TextView tvNickname;
	private TextView tvSex;
	private TextView tvAccount;
	private TextView tvRecord;
	private TextView tvHometown;
	private TextView tvIdcard;
	private TextView tvAddress;
	
	private Button btnRecharge;
	
	private String idcard;
	
	
	private final int REQUEST_CAMERA = 0x3401;
	private final int REQUEST_PHOTO = 0x3402;
	private final int REQUEST_CROP = 0x3403;
	public static final int REQUEST_CODE_NICKNAME = 0xabb1;
	public static final int REQUEST_CODE_IDCARD = 0xabb2;
	public static final int REQUEST_CODE_ADDRESS = 0xabb3;
	
	public Uri imageFilePath;
	private String coverImagePath ;
	private ImageView ivHead = null;
	
	private RechargeDialog rechargeDialog = null;
	
	//使用开源的webimageloader
	public static DisplayImageOptions options;
	protected MyImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_user_info;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("个人中心");
		setRightButtonText("保存");
		
		imageLoader = MyImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheOnDisk(true)
		.showImageOnLoading(R.drawable.user_head_default)
		.showImageForEmptyUri(R.drawable.user_head_default)
		.showImageOnFail(R.drawable.user_head_default)
		.cacheInMemory(true)
		.displayer(new RoundedBitmapDisplayer(DisplayUtil.getSize(this, 96)))
		.build();
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_message), 186);
		ViewUtils.setHeight(findViewById(R.id.rl_message), 60);
		
		tvWelcome = (TextView) findViewById(R.id.tv_welcome);
		
		tvNickname = (TextView) findViewById(R.id.tv_nickname);
		tvSex = (TextView) findViewById(R.id.tv_sex);
		tvAccount = (TextView) findViewById(R.id.tv_account);
		tvRecord = (TextView) findViewById(R.id.tv_record);
		tvHometown = (TextView) findViewById(R.id.tv_hometown);
		tvIdcard = (TextView) findViewById(R.id.tv_idcard);
		tvAddress = (TextView) findViewById(R.id.tv_address);
		
		btnRecharge = (Button) findViewById(R.id.btn_recharge);
		btnRecharge.setOnClickListener(this);
		
		TextView tvIdText = (TextView) findViewById(R.id.tv_userid);
		tvIdText.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		ViewUtils.setMarginTop(tvIdText, 16);
		
		inflater = LayoutInflater.from(this);
		
		tvWelcome.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		ViewUtils.setMarginTop(tvWelcome, 54);
		
		ivHead = (ImageView) findViewById(R.id.iv_head);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_user_info), 22);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_head), 24);
		ViewUtils.setMarginTop(findViewById(R.id.rl_head), 30);
		ViewUtils.setSize(findViewById(R.id.rl_head), 193, 193);
		ViewUtils.setSize(ivHead, 169, 169);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_luokuangbi), 208);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_score), 90);
		ViewUtils.setMarginRight(findViewById(R.id.rl_button), 12);
		ViewUtils.setHeight(findViewById(R.id.rl_button), 184);
		//ViewUtils.setMarginTop(findViewById(R.id.btn_sign), 34);
		//ViewUtils.setMarginRight(findViewById(R.id.btn_recharge), 12);
		ViewUtils.setMarginTop(findViewById(R.id.btn_recharge), 22);
		
		findViewById(R.id.iv_head).setOnClickListener(this);
		findViewById(R.id.rl_nickname).setOnClickListener(this);
		findViewById(R.id.rl_account).setOnClickListener(this);
		findViewById(R.id.rl_sex).setOnClickListener(this);
		findViewById(R.id.rl_record).setOnClickListener(this);
		findViewById(R.id.rl_hometown).setOnClickListener(this);
		findViewById(R.id.rl_idcard).setOnClickListener(this);
		findViewById(R.id.rl_address).setOnClickListener(this);
		findViewById(R.id.rl_password).setOnClickListener(this);
		
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_nickname), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_nickname_tag), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_sex), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_sex_tag), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_account), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_account_tag), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_record), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_record_tag), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_hometown), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_hometown_tag), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_idcard), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_idcard_tag), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_address), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_address_tag), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_password), 36);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_password_tag), 36);

		ViewUtils.setHeight(findViewById(R.id.rl_nickname), 120);
		ViewUtils.setHeight(findViewById(R.id.rl_sex), 120);
		ViewUtils.setHeight(findViewById(R.id.rl_account), 120);
		ViewUtils.setHeight(findViewById(R.id.rl_record), 120);
		ViewUtils.setHeight(findViewById(R.id.rl_hometown), 120);
		ViewUtils.setHeight(findViewById(R.id.rl_idcard), 120);
		ViewUtils.setHeight(findViewById(R.id.rl_address), 120);
		ViewUtils.setHeight(findViewById(R.id.rl_password), 120);
		
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        passengerDao = daoSession.getPassengerDao();
        
		if (passengerDao.count() != 0) {
        	List<Passenger> passList = passengerDao.loadAll();
        	tvWelcome.setText(passList.get(0).getNick_name());
        	//tvIdText.setText("ID:" + passList.get(0).getUser_id());
        }
		
		initPhotoChoice();
		initRechargeDialog();
		
	}
	
	
	private Uri setUri() {
		String imageID = ConStant.getNextUUID();
		String dir = ConStant.getPicAbsoluteDirPath();
		File destDir = new File(dir);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String outputFilePath = dir + imageID + ".jpg" ;
		File out = new File(outputFilePath);
		Uri uri = Uri.fromFile(out);// 将路径转化为uri

		return uri;
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
		case R.id.iv_head:
			//修改头像
//			pw_photo.showAtLocation(findViewById(R.id.rl_user_info_root),
//					Gravity.BOTTOM, 0, 0);
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
			//修改昵称
			Intent nicknameIntent = new Intent(this, NicknameActivity.class);
			nicknameIntent.putExtra("nickname", tvNickname.getText());
			startActivityForResult(nicknameIntent, REQUEST_CODE_NICKNAME);
			break;
		case R.id.rl_account:
			//箩筐账号
			Intent accountIntent = new Intent(this, RegisterActivity.class);
			startActivity(accountIntent);
			break;
		case R.id.rl_sex:
			//性别
			ListDialog dialog = new ListDialog(this,tvSex);
			dialog.setTitle("请选择性别");
			List<String> listStr = new ArrayList<String>();
			listStr.add("女");
			listStr.add("男");
			List<Integer> listIcon = new ArrayList<Integer>();
			listIcon.add(R.drawable.icon_sex_female);
			listIcon.add(R.drawable.icon_sex_male);
			dialog.setDate(listStr, listIcon);
			dialog.show();
			break;
		case R.id.rl_record:
			//里程纪录
			startActivity(new Intent(this, MileageRecordActivity.class));
			break;
		case R.id.rl_hometown:
			Intent addrintent = new Intent(this, AddressSelectorActivity.class);
			addrintent.putExtra("isCityOnly", true);
			startActivityForResult(addrintent, REQUEST_CODE_ADDRESS);
			//家乡
			break;
		case R.id.rl_idcard:
			//证件号
			Intent idcardIntent = new Intent(this, IDCardActivity.class);
			idcardIntent.putExtra("idcard", tvIdcard.getText());
			startActivityForResult(idcardIntent, REQUEST_CODE_IDCARD);
			break;
		case R.id.rl_address:
			startActivity(new Intent(this, ReceiptAddressActivity.class));
			//收货地址
			break;
		case R.id.btn_recharge:// 充值
			
			rechargeDialog.show();
			break;
		case R.id.rl_password:
			Intent changePwd = new Intent(this, ChangePasswordActivity.class);
			startActivity(changePwd);
			break;
		default:{
				
			}	
		}
	}
	
	private void initRechargeDialog() {
		rechargeDialog = new RechargeDialog(this, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Requester.requestRecharge(handler,"134234324",rechargeDialog.getMoney());
			}
		});
	}
	
	private void startImageClipActivity(String path) {
		Intent intent = new Intent(this, PhotoClipActivity.class);
		intent.putExtra(PhotoClipActivity.PHOTO_PATH, path);
		startActivityForResult(intent, RESULT_CODE_PHOTE_CLIP);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CAMERA:
				if (imageFilePath != null) {
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String imagaPath = imageFilePath.getPath();
							startImageClipActivity(imagaPath);
						}
					}, 1);
					
				}
				
				break;
			case REQUEST_PHOTO:
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
							PromptDialog.Dialog(this, "温馨提示提示", "只支持jpeg格式图片！", "确定");
						} else {
							PromptDialog.Dialog(this, "温馨提示提示", "只支持jpeg格式图片！", "确定");
						}
						
						Log.d(TAG,"path = " + path + " mime_type = " + mime_type + " size = " + size);
					}
				}
				break;
			case RESULT_CODE_PHOTE_CLIP:
				if (data != null) {
					coverImagePath = data.getStringExtra(PhotoClipActivity.CLIP_PATH);
					imageLoader.displayImage("file:///" + coverImagePath, ivHead, options);
				}
				break;
			case REQUEST_CODE_NICKNAME:
				if (data !=null  && data.getStringExtra("nickname")!=null) {
					tvNickname.setText(data.getStringExtra("nickname"));
				}
				break;
			case REQUEST_CODE_IDCARD:
				if (data !=null  && data.getStringExtra("idcard")!=null) {
					idcard = data.getStringExtra("idcard");
					tvIdcard.setText(idcard.substring(0, 4) + "**********" + idcard.substring(14));
				}
				break;
			case REQUEST_CODE_ADDRESS:
				if (data !=null && data.getStringArrayExtra("address")!=null) {
					String[] result = data.getExtras().getStringArray("address");
					if(result != null && result.length >= 2) {
						((TextView)findViewById(R.id.tv_hometown)).setText(result[1]);
					}
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
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_RECHARGE:
			if (msg.obj != null) {
				RechargeResp resp = (RechargeResp) msg.obj;
				if ("0".equals(resp.status)) {
					//调用支付宝接口返回结果
					Log.d("=AAA=","order_no = " + resp.order_no);
					if (Config.IS_USE_REAL_PRICE) {
						PayInfo.pay(this, handler, "箩筐币充值", "箩筐币充值 " + rechargeDialog.getMoney() + "元", rechargeDialog.getMoney(), resp.order_no);
					} else {
						PayInfo.pay(this, handler, "箩筐币充值", "箩筐币充值 " + rechargeDialog.getMoney() + "元", "0.01", resp.order_no);
					}
				}else{
					//弹窗
					PromptDialog.Dialog(this, "支付失败", "当前网络状态不佳", "稍后再试", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});	
					
				}
			}
			break;
		case PayInfo.RESPONSE_ALIPAY_RESULT:
			if (msg.obj != null) {
				Result result = (Result) msg.obj;
				if (result != null) {
					Log.d(TAG,"payInfo = " + result.getPayInfo());
					if (result.isTransactionSecc()) {
						Log.d(TAG,"isTransactionSecc succ");
						PromptDialog.Alert("支付成功");
					}else{
						//弹窗
						if (!result.isUserCancle()) {
							PromptDialog.Dialog(this, "支付失败", "当前网络状态不佳", "稍后再试", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							});	
						} else {
							rechargeDialog.dismiss();
						}
					}
					
				}
			}
			break;
		}
		return false;
	}
	
	// 显示头像设置选择界面
	private void initPhotoChoice() {
		View view = inflater.inflate(R.layout.activity_setting_photo_menu,
				null);
		Button btnCancle = (Button) view.findViewById(R.id.btn_cancel);
		Button btnPictures = (Button) view.findViewById(R.id.btn_from_pictures);
		Button btnCamera = (Button) view.findViewById(R.id.btn_from_camera);
		ViewUtils.setHeight(view.findViewById(R.id.rl_popupwindow), 380);
		ViewUtils.setHeight(btnCancle, 90);
		ViewUtils.setHeight(btnPictures, 90);
		ViewUtils.setHeight(btnCamera, 90);
		ViewUtils.setMarginBottom(btnCancle, 40);
		ViewUtils.setMarginBottom(btnPictures, 20);
		ViewUtils.setMarginBottom(btnCamera, 20);
		ViewUtils.setMarginLeft(btnCancle, 70);
		ViewUtils.setMarginRight(btnCancle, 70);
		btnCancle.setTextSize(DisplayUtil.textGetSizeSp(this, 34));
		btnPictures.setTextSize(DisplayUtil.textGetSizeSp(this, 34));
		btnCamera.setTextSize(DisplayUtil.textGetSizeSp(this, 34));
		
		pw_photo = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pw_photo.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		view.findViewById(R.id.btn_from_camera).setOnClickListener(this);
		view.findViewById(R.id.btn_from_pictures).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
	}

}
