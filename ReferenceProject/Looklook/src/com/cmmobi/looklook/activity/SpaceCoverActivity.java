package com.cmmobi.looklook.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
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
import android.widget.SimpleAdapter;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.utils.BitmapHelper;
import com.cmmobi.sns.utils.MyListView;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.api.UniversalImageLoader;
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
public class SpaceCoverActivity extends ZActivity implements
		OnItemClickListener {

	private GridView grid;
	private MyListView list;
	private ImageView iv_back;
	private ArrayList<Map<String, String>> map;

	public static Uri imageFilePath;

	private final int RESULT_IMG = 0x03;
	private final int RESULT_PHOTOGRAPH = 0x01;
	private final int RESULT_CROP = 0x05;
	private final String TAG = "SpaceCoverActivity";

	private gridadapter gr;
	private List<Map<String, String>> netWorkBackList = new ArrayList<Map<String, String>>();

	private LayoutInflater inflater;
	private PopupWindow portraitMenu;
	private View ll_spaceCover;
	
	private String shortpath;

	public View v;
	public boolean click = true;

	public AccountInfo ai;
	public String userID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_space);
		handler = getHandler();
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		grid = (GridView) findViewById(R.id.grid);
		inflater = LayoutInflater.from(this);
		ll_spaceCover = findViewById(R.id.ll_spaceCover);

		userID = ActiveAccount.getInstance(this).getUID();
		ai = AccountInfo.getInstance(userID);
		Log.e("ai.backCoverPicCash.size()", ai.backCoverPicCash.size() + "");



		if (ai.backCoverPicCash.size() < 1) {
			// sendGetBackGroundReq();
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester2.getSpaceCoverList(handler, null);
		} else {
			gr = new gridadapter(this, ai.backCoverPicCash);
			grid.setAdapter(gr);
		}
		list = (MyListView) findViewById(R.id.list);
		map = new ArrayList<Map<String, String>>();
		list.setAdapter(setAdapter());
		list.setOnItemClickListener(this);
		grid.setOnItemClickListener(this);

		// Requester.submitUA(handler);
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

	/*
	 * private void sendGetBackGroundReq(){ //Gson gson = new Gson();
	 * GsonRequest.getbackgroundlistRequest req = new
	 * GsonRequest.getbackgroundlistRequest(); //.userid = ""; req.userid =
	 * ActiveAccount.getInstance(this).getUID(); req.pagesize = 12; req.index =
	 * " "; }
	 */

	/**
	 * 设置适配器
	 * 
	 * @return
	 */
	public SimpleAdapter setAdapter() {
		Map<String, String> data = new HashMap<String, String>();

		data.put("text", "从手机相册中选取");
		map.add(data);

		return new SimpleAdapter(this, map, R.layout.list_item_space,
				new String[] { "text" }, new int[] { R.id.tv_listimg });
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {

		if (arg0 == list) {
			showChoiceItem();
		} else if (arg0 == grid) {

			v = grid.getChildAt(position);

			String back;
			if (ai.backCoverPicCash.size() > 0
					&& ai.backCoverPicCash.get(position) != null) {
				back = ai.backCoverPicCash.get(position).get("backgroundimage");
				
//				view.findViewById(R.id.iv_downloaded_pic).setVisibility(
//						View.VISIBLE);
//				view.findViewById(R.id.tv_download_text).setVisibility(
//						View.GONE);
//				view.findViewById(R.id.iv_downloaded_pic)
//						.setBackgroundResource(R.drawable.fengmian_xuanzhong);
			} else {
				back = netWorkBackList.get(position).get("portraiturl");
//				view.findViewById(R.id.iv_downloaded_pic).setVisibility(
//						View.VISIBLE);
//				view.findViewById(R.id.iv_downloaded_pic)
//				.setBackgroundResource(R.drawable.fengmian_xiazai);
//				view.findViewById(R.id.tv_download_text).setVisibility(
//						View.VISIBLE);
//				ImageDwonloadTask idlt = new ImageDwonloadTask();
//				idlt.execute(userID, back, 1);
			}
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester2.setUserSpaceCover(handler, back);

			CmmobiClickAgent.onEvent(this, "ma_back_b", "0");
			if (v != null) {
				v.setOnClickListener(null);// 取消单个view的监听
			}
		}
	}

	private void showChoiceItem() {
		View view = inflater.inflate(R.layout.activity_spacecover_more_menu,
				null);
		portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAtLocation(ll_spaceCover, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_more_menu_photograph).setOnClickListener(
				this);
		view.findViewById(R.id.btn_more_menu_chose_from_gallery)
				.setOnClickListener(this);
		view.findViewById(R.id.btn_more_menu_cancel).setOnClickListener(this);
	}

	public void callImage() {
		imageFilePath = setUri();
		/*Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, RESULT_IMG);*/
		
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
		int screenWidth = metric.widthPixels;
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", screenWidth);
		intent.putExtra("outputY", screenWidth);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection
		startActivityForResult(intent, RESULT_CROP);

		
		/*Intent intent = new Intent(Intent.ACTION_PICK, null);
		
		intent.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, RESULT_IMG);*/

	}

	private Uri setUri() {
		File destDir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath(), "backgroundcover");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		StringBuilder filePath = new StringBuilder();
		filePath.append("img");
		filePath.append(TimeHelper.getInstance().now());
		filePath.append(".jpg");
		File out = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/backgroundcover/", filePath.toString());
		Uri uri = Uri.fromFile(out);// 将路径转化为uri

		return uri;
	}
	
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	
	/**
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
		int screenWidth = metric.widthPixels;
		Log.d(TAG,"screenWidth = " + screenWidth);
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
			String img = imageFilePath.getPath();
			CmmobiClickAgent.onEvent(this, "ma_back_b", "0");
			switch (requestCode) {
			case RESULT_IMG:
				portraitMenu.dismiss();
				Uri uri = data.getData();
				/*Bitmap bit = getBitmap(uri);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bit.compress(CompressFormat.JPEG, 100, baos);
				File file = new File(img);
				try {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baos.toByteArray());
					Requester2.uploadPicture(handler, file.getPath(), "6", "");
					// Requester2.setUserSpaceCover(handler, s);

					fos.close();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				startPhotoZoom(uri);
				break;
			case RESULT_PHOTOGRAPH:
				if (resultCode == Activity.RESULT_OK) {
					String sdStatus = Environment.getExternalStorageState();
					if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
						Log.i("TestFile",
								"SD card is not avaiable/writeable right now.");
						return;
					}
					startPhotoZoom(imageFilePath);
					/*Bundle bundle = data.getExtras();
					Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

					FileOutputStream b = null;
					try {
						b = new FileOutputStream(img);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
						
						// Requester2.setUserSpaceCover(handler, img);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							b.flush();
							b.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}*/
				}
				break;
			case RESULT_CROP:
//				Bundle extras = data.getExtras();
//				Bitmap bit = null;
//				if (extras != null) {
//					bit = extras.getParcelable("data");
//				}
				Bitmap bit = decodeUriAsBitmap(imageFilePath);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (bit == null) {
					
				}
				bit.compress(CompressFormat.JPEG, 100, baos);
				File file = new File(img);
				try {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baos.toByteArray());
					Requester2.uploadPicture(handler, file.getPath(), "6", "");

					fos.close();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}else{
			ZDialog.dismiss();
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

		public gridadapter(Context context, List<Map<String, String>> l) {
			this.l = l;
			inflater = LayoutInflater.from(context);
			
			UniversalImageLoader.initImageLoader(MainApplication.getAppInstance(), ActiveAccount.getInstance(SpaceCoverActivity.this).getLookLookID());
			animateFirstListener = new AnimateFirstDisplayListener();
			imageLoader = ImageLoader.getInstance();

			options = new DisplayImageOptions.Builder()
					.showStubImage(0)
					.showImageForEmptyUri(0)
					.showImageOnFail(0)
					.cacheInMemory(true).cacheOnDisc(true)
					// .displayer(new SimpleBitmapDisplayer())
					.displayer(new SimpleBitmapDisplayer())
					// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
					.build();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return l.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return l.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
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
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.web.setImageUrl(R.drawable.u23_normal, 1,
			// l.get(position).get("portraiturl"), false);

			if (ai != null && ai.headimageurl != null) {
				imageLoader.displayImage(l.get(position).get("portraiturl"),
						holder.web, options, animateFirstListener,
						ActiveAccount.getInstance(SpaceCoverActivity.this)
								.getUID(), 1);
			} else {
				holder.web.setImageResource(0);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView web;
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_GET_SPACECOVER_LIST:
			ZDialog.dismiss();
			GsonResponse2.getSpaceCoverListResponse respone = (GsonResponse2.getSpaceCoverListResponse) msg.obj;
			ZLog.printObject(respone);
			if (null != respone) {
				GsonResponse2.getbackgroundlistItem[] user = respone.backgrounds;
				for (int i = 0; i < user.length; i++) {
					Map<String, String> d = new HashMap<String, String>();
					String portraiturl = user[i].spacecoverurl;
					String backgroundimage = user[i].backgroundpath;
					d.put("portraiturl", portraiturl);
					d.put("backgroundimage", backgroundimage);
					netWorkBackList.add(d);
					/*
					 * //将从网络读取到的背景图片保存至缓存 
					 */ ai.backCoverPicCash.add(d);
					 
					// grid.setAdapter(new gridadapter(this, l));
				}
				if(respone.hasnextpage.equals("1")){
					Requester2.getSpaceCoverList(handler, respone.index);
				}
				gr = new gridadapter(this, netWorkBackList);
				grid.setAdapter(gr);
			}
			break;
		case Requester2.RESPONSE_TYPE_SET_SPACECOVER:
			ZDialog.dismiss();
			GsonResponse2.setUserSpaceCoverResponse res = (GsonResponse2.setUserSpaceCoverResponse) msg.obj;
			ZLog.printObject(res);
			if (res != null && res.status.equals("0")) {
				
				if (imageFilePath != null && res.imageurl != null) {
					/**
					 * 2、注册MediaMapping
					 */
					// 上传图片成功,将录音文件在mediamapping中注册
					MediaValue mv = new MediaValue();
					String pathString = imageFilePath.getPath();
					long fileSize = (new File(pathString))
							.length();
					/*if (pathString.contains("sdcard")) {
						pathString = pathString.substring(pathString
								.indexOf("sdcard"));
						pathString = pathString.substring(pathString.indexOf("/"));
					}*/
					mv.Belong = 1;
					mv.MediaType = 2;
					mv.path = pathString.replace(Environment.getExternalStorageDirectory().getPath(), "");
					mv.url = res.imageurl;
					mv.UID = userID;
					mv.realSize = fileSize;
					mv.totalSize = fileSize;
					mv.Sync = 1;
					mv.SyncSize = fileSize;
					ai.mediamapping.setMedia(userID, res.imageurl, mv);
					Log.e("LocalPath", mv.path + "");
					Log.e("HTTPPath", res.imageurl + "");
				}
				
				// 更换封面成功保存缓存
				String userID = ActiveAccount.getInstance(
						ZApplication.getInstance()).getUID();
				AccountInfo.getInstance(userID).zoneBackGround = res.imageurl;
				Prompt.Alert("更换封面成功");
				setResult(RESULT_OK);
				SpaceCoverActivity.this.finish();
			}
			break;
		case Requester2.RESPONSE_TYPE_UPLOAD_PICTURE:
			ZDialog.dismiss();
			GsonResponse2.uploadPictrue resp = (GsonResponse2.uploadPictrue) msg.obj;
			if (null != resp) {
				if ("0".equals(resp.status)) {

					if (resp.imageurl != null) {
						/**
						 * 2、注册MediaMapping
						 */
						// 上传图片成功,将录音文件在mediamapping中注册
						MediaValue mv = new MediaValue();
						/*String pathString = imageFilePath.getPath();
						long fileSize = (new File(pathString)).length();
						if (pathString.contains("sdcard")) {
							pathString = pathString.substring(pathString
									.indexOf("sdcard"));
							mv.path = pathString.substring(pathString.indexOf("/"));
						}
						mv.Belong = 1;
						mv.MediaType = 2;
						mv.url = resp.imageurl;
						mv.UID = userID;
						mv.realSize = fileSize;
						mv.totalSize = fileSize;
						mv.Sync = 1;
						mv.SyncSize = fileSize;
						ai.mediamapping.setMedia(userID, resp.imageurl, mv);*/
						shortpath = resp.imageurl;

						Requester2.setUserSpaceCover(handler, shortpath);
					}
				} else {
					Log.e(TAG, "RESPONSE_TYPE_UPLOAD_PICTURE status is "
							+ resp.status);
				}
			}else{
				Prompt.Alert("网络超时！");
				
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
		case R.id.iv_back:
			SpaceCoverActivity.this.finish();
			break;
		case R.id.btn_more_menu_cancel:
			break;
		case R.id.btn_more_menu_chose_from_gallery:
			callImage();
			ZDialog.show(R.layout.progressdialog, true, true, this);
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
	}

	private class ImageDwonloadTask extends Thread {
		MediaValue mv;
		private String uid;
		private String url;
		private int belong;
		private String Key;

		public void execute(String UID, String url, int Belong) {
			// TODO Auto-generated method stub
			this.mv = AccountInfo.getInstance(UID).mediamapping.getMedia(UID,
					url);
			this.uid = UID;
			this.url = url;
			this.belong = Belong;
			this.start();
		}

		public void run() {
			Bitmap saveBitmap;
			Bitmap tmp = null;

			if (MediaValue.checkMediaAvailable(mv, 2)) {// load local
				Log.e(TAG, ImageDwonloadTask.this + " load local: "
						+ ImageDwonloadTask.this);
				try {
					File file = new File(
							Environment.getExternalStorageDirectory() + mv.path);
					tmp = BitmapHelper
							.getBitmapFromInputStream(new FileInputStream(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			if (tmp == null) { // load net
				Log.e(TAG, ImageDwonloadTask.this + " load net: "
						+ ImageDwonloadTask.this);
				Key = url.substring(url.lastIndexOf("/"));
				String webimage_path = Environment
						.getExternalStorageDirectory()
						+ Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + Key;
				File webimage_file = new File(webimage_path);
				if (!webimage_file.getParentFile().exists()) {
					webimage_file.getParentFile().mkdirs();
				}

				MediaValue result = null;
				try {
					Log.e(TAG, "download task Key:" + Key + " url:" + url);

					ZHttp2 http2 = new ZHttp2();
					ZHttpResponse httpResponse = http2.get(url);

					ZHttpReader reader = new ZHttpReader(
							httpResponse.getInputStream(), null);
					byte[] imageData = reader.readAll(0);
					Log.d(TAG, "imageData.length=" + imageData.length);

					if (imageData.length > 0) {
						// save file
						FileOutputStream mOutput = new FileOutputStream(
								webimage_file, false);

						mOutput.write(imageData);
						mOutput.close();

						result = new MediaValue();
						result.UID = uid;
						result.Belong = belong;
						result.Direction = 1;
						result.MediaType = 2;
						result.url = url;
						result.path = Constant.SD_STORAGE_ROOT + "/" + uid
								+ "/pic/" + Key;
						result.realSize = imageData.length;
						result.Sync = 1;
						result.SyncSize = imageData.length;
						result.totalSize = imageData.length;
						AccountInfo.getInstance(result.UID).mediamapping
								.setMedia(result.UID, url, result);

						if (MediaValue.checkMediaAvailable(result, 2)) {
							try {
								File file = new File(
										Environment
												.getExternalStorageDirectory()
												+ result.path);
								tmp = BitmapHelper
										.getBitmapFromInputStream(new FileInputStream(
												file));
							} catch (FileNotFoundException e) {
								Log.e("Exception", e.getMessage(), e);
							}
						}
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}

			saveBitmap = tmp;
		}
	}
}
