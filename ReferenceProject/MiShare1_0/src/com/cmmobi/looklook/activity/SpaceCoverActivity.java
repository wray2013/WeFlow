package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.VideoCoverUploader;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 空间封面界面
 * 
 * @author Administrator
 * 
 */
public class SpaceCoverActivity extends TitleRootActivity implements
		OnItemClickListener {

	private GridView grid;
	private ArrayList<Map<String, String>> map;

	public static Uri imageFilePath;

	private String coverImagePath = null;

	private final int RESULT_IMG = 0x03;
	private final int RESULT_PHOTOGRAPH = 0x01;
	private final int RESULT_CROP = 0x05;
	private final int RESULT_CODE_PHOTE_CLIP = 0x07;
	private final String TAG = "SpaceCoverActivity";

	private gridadapter gr;
	private List<Map<String, String>> netWorkBackList = new ArrayList<Map<String, String>>();

	private LayoutInflater inflater;
	private PopupWindow portraitMenu;
	private View ll_spaceCover;

	private String shortpath;

	public boolean click = true;

	public AccountInfo ai;
	public String userID;
	private final String MIME_TYPE_JPEG = "image/jpeg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideRightButton();
		setTitle(R.string.str_spacecover);
		handler = getHandler();
		grid = (GridView) findViewById(R.id.grid);
		inflater = LayoutInflater.from(this);
		ll_spaceCover = findViewById(R.id.ll_spaceCover);
		userID = ActiveAccount.getInstance(this).getUID();
		ai = AccountInfo.getInstance(userID);
		Log.e("ai.backCoverPicCash.size()", ai.backCoverPicCash.size() + "");
		gr = new gridadapter(this, ai.backCoverPicCash);
		grid.setAdapter(gr);
		if (ai.backCoverPicCash.size() < 1) {
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester3.getSpaceCoverList(handler, null);
		} else {
			Requester3.getSpaceCoverList(handler, null);
		}
		map = new ArrayList<Map<String, String>>();
		grid.setOnItemClickListener(this);
		findViewById(R.id.rl_enter_album).setOnClickListener(this);

//		LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
//				new IntentFilter(ACTION_SPACECOVER_UPLOAD_COMPLETED));
	}

	@Override
	public int subContentViewId() {
		return R.layout.activity_space;
	}

	@Override
	protected void onDestroy() {
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
		super.onDestroy();
	}
	
	

	@Override
	protected void onResume() {
		//2014-4-8 wuxiang
		CmmobiClickAgentWrapper.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		//2014-4-8 wuxiang
		CmmobiClickAgentWrapper.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStop() {
		//2014-4-8 wuxiang
		CmmobiClickAgentWrapper.onStop(this);
		super.onStop();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		if (!(ZNetworkStateDetector.isAvailable() && ZNetworkStateDetector
				.isConnected())) {
			Prompt.Alert(getString(R.string.prompt_network_error));
			return;
		}
		String backgroundurl = view.getTag(
				R.layout.activity_activity_space_back).toString();
		ZDialog.show(R.layout.progressdialog, true, true, this);
		Requester3.setUserSpaceCover(handler, backgroundurl);
		//2014-4-8 wuxiang
		Log.d(TAG, "switch_background "+(position+1));
		CmmobiClickAgentWrapper.onEvent(this, "switch_background", (position+1)+"");
	}

	private void showChoiceItem() {
		View view = inflater.inflate(R.layout.activity_spacecover_more_menu,
				null);
		portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.del_dot_big));
		portraitMenu.showAtLocation(ll_spaceCover, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_more_menu_photograph).setOnClickListener(
				this);
		view.findViewById(R.id.btn_more_menu_chose_from_gallery)
				.setOnClickListener(this);
		view.findViewById(R.id.btn_more_menu_cancel).setOnClickListener(this);
	}

	public void callImage() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, RESULT_IMG);

	}

	private Uri setUri() {
		String imageID = DiaryController.getNextUUID();
		String dir = DiaryController.getAbsolutePathUnderUserDir() + "/pic/";
		File destDir = new File(dir);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String outputFilePath = dir + imageID + ".jpg";
		File out = new File(outputFilePath);
		Uri uri = Uri.fromFile(out);// 将路径转化为uri

		return uri;
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int screenWidth = metric.widthPixels;
		Log.d(TAG, "screenWidth = " + screenWidth);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", screenWidth);
		intent.putExtra("outputY", screenWidth);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection

		startActivityForResult(intent, RESULT_CROP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			System.out.println("ok");
			switch (requestCode) {
			case RESULT_IMG:
				portraitMenu.dismiss();
				Uri uri = data.getData();

				if (uri != null) {
					String[] proj = {MediaStore.Images.Media.DATA,MediaStore.Images.Media.MIME_TYPE,
							MediaStore.Images.Media.SIZE,};

					Cursor cursor = managedQuery(uri, proj, null, null, null);
					if (cursor != null) {
						int column_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						// 将光标移至开头 ，这个很重要，不小心很容易引起越界
						cursor.moveToFirst();
						// 最后根据索引值获取图片路径
						String path = cursor.getString(column_index);
						String mime_type = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
						String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
						if (Long.valueOf(size) > 4096l
								&& (MIME_TYPE_JPEG.equals(mime_type))) {
							startImageClipActivity(path);
						} else if (!MIME_TYPE_JPEG.equals(mime_type)) {
							ZDialog.dismiss();
							Prompt.Dialog(this, false, "提示", "只支持jpeg格式图片！", null);
						} else {
							ZDialog.dismiss();
							Prompt.Dialog(this, false, "提示", "图片太小,可能是图标。", null);
						}
						Log.d(TAG, "path = " + path);
					}
				}

				break;
			case RESULT_PHOTOGRAPH:
				if (imageFilePath != null) {
					String imagaPath = imageFilePath.getPath();
					startImageClipActivity(imagaPath);
				}
				break;
			case RESULT_CODE_PHOTE_CLIP:
				if (!(ZNetworkStateDetector.isAvailable() && ZNetworkStateDetector
						.isConnected())) {
					ZDialog.dismiss();
					Prompt.Alert(getString(R.string.prompt_network_error));
					return;
				}
				if (data != null) {
					coverImagePath = data
							.getStringExtra(PhotoClipActivity.CLIP_PATH);
					if (coverImagePath != null) {
						uploadCover(coverImagePath);
					}
				}
				break;
			}
		} else {
			ZDialog.dismiss();
		}
		
	}

	private void startImageClipActivity(String path) {
		Intent intent = new Intent(this, PhotoClipActivity.class);
		intent.putExtra(PhotoClipActivity.PHOTO_PATH, path);
		intent.putExtra(PhotoClipActivity.WIDTH_HEIGHT_SCALE, 0.75f);
		intent.putExtra(PhotoClipActivity.IS_FROM_SPACE_COVER, true);
		startActivityForResult(intent, RESULT_CODE_PHOTE_CLIP);
	}

	private void uploadCover(String path) {
		File file = new File(path);
		try {
			// Requester3.uploadPicture(handler, file.getPath(), "6", "");
			VideoCoverUploader.uploadParam upload = new VideoCoverUploader.uploadParam();
			upload.filetype = "4";
			upload.mfile = file;
			upload.rotation = 0;
			upload.businesstype = "5";
			VideoCoverUploader videoCoverUploader = new VideoCoverUploader(
					upload);
			videoCoverUploader.excute();
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Bitmap getBitmap(Uri uri) {
		Bitmap pic = null;
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Display display = getWindowManager().getDefaultDisplay();
		int dw = display.getWidth();
		int dh = display.getHeight();

		try {
			pic = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri), null, op);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int wRatio = (int) Math.ceil(op.outWidth / (float) dw);
		int hRatio = (int) Math.ceil(op.outHeight / (float) dh);
		if (wRatio > 1 && hRatio > 1) {
			op.inSampleSize = wRatio + hRatio;
		}
		op.inJustDecodeBounds = false;
		try {
			pic = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri), null, op);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return pic;
	}

	class gridadapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<Map<String, String>> l;

		// 使用开源的webimageloader
		private DisplayImageOptions options;
		protected ImageLoader imageLoader;
		private ImageLoadingListener animateFirstListener;

		private DisplayMetrics dm = new DisplayMetrics();

		public gridadapter(Context context, List<Map<String, String>> l) {
			this.l = l;
			inflater = LayoutInflater.from(context);
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			// UniversalImageLoader.initImageLoader(MainApplication.getAppInstance(),
			// ActiveAccount.getInstance(SpaceCoverActivity.this).getLookLookID());
			animateFirstListener = new AnimateFirstDisplayListener();
			imageLoader = ImageLoader.getInstance();

			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.moren_touxiang)
					.showImageForEmptyUri(R.drawable.moren_touxiang)
					.showImageOnFail(R.drawable.moren_touxiang)
					.cacheInMemory(true).cacheOnDisc(true)
					// .displayer(new SimpleBitmapDisplayer())
					.displayer(new SimpleBitmapDisplayer())
					// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
					.build();
		}

		@Override
		public int getCount() {
			return l.size();
		}

		@Override
		public Object getItem(int position) {
			return l.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.activity_activity_space_back, null);
				holder = new ViewHolder();
				holder.web = (ImageView) convertView.findViewById(R.id.web);
				holder.webChecked = (ImageView) convertView
						.findViewById(R.id.web_checked);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			android.view.ViewGroup.LayoutParams params = holder.webChecked
					.getLayoutParams();
			params.width = dm.widthPixels / 3 - 20;
			params.height = params.width;
			holder.webChecked.setLayoutParams(params);
			String url = l.get(position).get("portraiturl");
			String back = l.get(position).get("backgroundimage");
			String userID = ActiveAccount.getInstance(SpaceCoverActivity.this)
					.getUID();
			if (userID != null && userID.length() > 0) {
				String defaultbg = AccountInfo.getInstance(userID).zoneBackGround;
				if (defaultbg != null && defaultbg.equals(url)) {
					holder.webChecked.setVisibility(View.VISIBLE);
				} else {
					holder.webChecked.setVisibility(View.INVISIBLE);
				}
				imageLoader.displayImageEx(url, holder.web, options,
						animateFirstListener, userID, 1);
				convertView.setTag(R.layout.activity_activity_space_back, back);
			}
			return convertView;
		}

		class ViewHolder {
			ImageView web;
			ImageView webChecked;
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_GET_SPACECOVER_LIST:
			ZDialog.dismiss();
			GsonResponse3.getSpaceCoverListResponse respone = (GsonResponse3.getSpaceCoverListResponse) msg.obj;
			ZLog.printObject(respone);
			if (null != respone) {
				GsonResponse3.getbackgroundlistItem[] user = respone.backgrounds;
				ai.backCoverPicCash.clear();
				for (int i = 0; i < user.length; i++) {
					Map<String, String> d = new HashMap<String, String>();
					String portraiturl = user[i].spacecoverurl;
					String backgroundimage = user[i].backgroundpath;
					d.put("portraiturl", portraiturl);
					d.put("backgroundimage", backgroundimage);
					netWorkBackList.add(d);
					/*
					 * //将从网络读取到的背景图片保存至缓存
					 */
					ai.backCoverPicCash.add(d);
				}
				if (respone.hasnextpage.equals("1")) {
					Requester3.getSpaceCoverList(handler, respone.index);
				}
				gr.notifyDataSetChanged();
			}
			break;
		case Requester3.RESPONSE_TYPE_SET_SPACECOVER:
			ZDialog.dismiss();
			GsonResponse3.setUserSpaceCoverResponse res = (GsonResponse3.setUserSpaceCoverResponse) msg.obj;
			ZLog.printObject(res);
			if (res != null && res.status.equals("0")) {

				if (imageFilePath != null && res.imageurl != null) {
					/**
					 * 2、注册MediaMapping
					 */
					// 上传图片成功,在mediamapping中注册
					MediaValue mv = new MediaValue();
					String pathString = imageFilePath.getPath();
					long fileSize = (new File(pathString)).length();
					/*
					 * if (pathString.contains("sdcard")) { pathString =
					 * pathString.substring(pathString .indexOf("sdcard"));
					 * pathString =
					 * pathString.substring(pathString.indexOf("/")); }
					 */
					mv.Belong = 1;
					mv.MediaType = 2;
					mv.localpath = pathString.replace(Environment
							.getExternalStorageDirectory().getPath(), "");
					mv.url = res.imageurl;
					mv.UID = userID;
					mv.realSize = fileSize;
					mv.totalSize = fileSize;
					mv.Sync = 1;
					mv.SyncSize = fileSize;
					ai.mediamapping.setMedia(userID, res.imageurl, mv);
					imageFilePath = null;
					Log.d(TAG, "LocalPath=" + mv.localpath);
					Log.d(TAG, "HTTPPath=" + res.imageurl + "");
				}

				// 更换封面成功保存缓存
				String userID = ActiveAccount.getInstance(
						ZApplication.getInstance()).getUID();
				AccountInfo.getInstance(userID).zoneBackGround = res.imageurl;
				gr.notifyDataSetChanged();
				Prompt.Alert("更换封面成功");
				// setResult(RESULT_OK);
				LocalBroadcastManager.getInstance(this).sendBroadcast(
						new Intent(ZoneBaseFragment.ACTION_ZONEBASE_USERINFO));
				SpaceCoverActivity.this.finish();
			}
			break;
		case Requester3.RESPONSE_TYPE_UPLOAD_PICTURE:
			GsonResponse3.uploadPictrue resp = (GsonResponse3.uploadPictrue) msg.obj;
			if (null != resp) {
				if ("0".equals(resp.status)) {

					if (resp.imageurl != null) {
						/**
						 * 2、注册MediaMapping
						 */
						// 上传图片成功,将录音文件在mediamapping中注册
						MediaValue mv = new MediaValue();
						/*
						 * String pathString = imageFilePath.getPath(); long
						 * fileSize = (new File(pathString)).length(); if
						 * (pathString.contains("sdcard")) { pathString =
						 * pathString.substring(pathString .indexOf("sdcard"));
						 * mv.path =
						 * pathString.substring(pathString.indexOf("/")); }
						 * mv.Belong = 1; mv.MediaType = 2; mv.url =
						 * resp.imageurl; mv.UID = userID; mv.realSize =
						 * fileSize; mv.totalSize = fileSize; mv.Sync = 1;
						 * mv.SyncSize = fileSize;
						 * ai.mediamapping.setMedia(userID, resp.imageurl, mv);
						 */
						shortpath = resp.imageurl;
						Requester3.setUserSpaceCover(handler, shortpath);
					}
				} else {
					Log.e(TAG, "RESPONSE_TYPE_UPLOAD_PICTURE status is "
							+ resp.status);
				}
			}
			break;
		}
		return false;
	}

	public boolean indexof(String url) {
		boolean mofi = false;

		int index = url.indexOf("&imageurl=");
		int len = "&imageurl=".length();
		String content = url.substring(index + len);
		System.out.println("content------>" + content);
		if (content.startsWith("http")) {
			mofi = true;

		} else {
			mofi = false;
		}
		return mofi;
	}

	@Override
	public void onClick(View v) {
		if (portraitMenu != null && portraitMenu.isShowing()) {
			portraitMenu.dismiss();
		}
		switch (v.getId()) {
		case R.id.rl_enter_album:
			showChoiceItem();
			break;
		case R.id.btn_more_menu_cancel:
			break;
		case R.id.btn_more_menu_chose_from_gallery:
			ZDialog.show(R.layout.progressdialog, true, true, this);
			callImage();
			break;
		case R.id.btn_more_menu_photograph:
			ZDialog.show(R.layout.progressdialog, true, true, this);
			imageFilePath = setUri();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
			startActivityForResult(intent, RESULT_PHOTOGRAPH);
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	public static final String ACTION_SPACECOVER_UPLOAD_COMPLETED = "ACTION_SPACECOVER_UPLOAD_COMPLETED";
	/*private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String picurl = intent.getStringExtra("picurl");
			Log.d(TAG, "picurl=" + picurl);
			// Requester3.setUserSpaceCover(handler, shortpath);
			if (coverImagePath != null && picurl != null) {
				*//**
				 * 2、注册MediaMapping
				 *//*
				// 上传图片成功,将录音文件在mediamapping中注册
				MediaValue mv = new MediaValue();
				String pathString = intent.getStringExtra("filepath");
				long fileSize = (new File(pathString)).length();
				mv.Belong = 1;
				mv.MediaType = 2;
				mv.localpath = pathString.replace(Environment
						.getExternalStorageDirectory().getPath(), "");
				mv.url = picurl;
				mv.UID = userID;
				mv.realSize = fileSize;
				mv.totalSize = fileSize;
				mv.Sync = 1;
				mv.SyncSize = fileSize;
				ai.mediamapping.setMedia(userID, picurl, mv);
				imageFilePath = null;
				Log.d(TAG, "LocalPath=" + mv.localpath);
				Log.d(TAG, "HTTPPath=" + picurl + "");
				//2014-4-8 wuxiang
				Log.d(TAG, "switch_background 0");
				CmmobiClickAgentWrapper.onEvent(SpaceCoverActivity.this, "switch_background", "0");
			}

			// 更换封面成功保存缓存
			String userID = ActiveAccount.getInstance(
					ZApplication.getInstance()).getUID();
			AccountInfo.getInstance(userID).zoneBackGround = picurl;
			gr.notifyDataSetChanged();
			Prompt.Alert("更换封面成功");
			LocalBroadcastManager.getInstance(SpaceCoverActivity.this)
					.sendBroadcast(
							new Intent(
									ZoneBaseFragment.ACTION_ZONEBASE_USERINFO));
			SpaceCoverActivity.this.finish();
		}

	};*/
}
