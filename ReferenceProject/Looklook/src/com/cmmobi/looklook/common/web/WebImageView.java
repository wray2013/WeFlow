package com.cmmobi.looklook.common.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import cn.zipper.framwork.io.network.ZHttp;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.sns.utils.BitmapHelper;
import com.cmmobivideo.utils.XUtils;

/**
 * @author zhangwei
 */
public class WebImageView extends ImageView implements Callback{

	private final static String TAG = "WebImageView";
	private static HashMap<String, SoftReference<Drawable>> map;
	
	private LoadTask loadtask;
	private Handler handler;
	private boolean beRound = false;
	//private boolean mLoaded = false;
	private boolean beRotate = false;
	volatile private boolean beRecycle = false;

	private String key;

	private final static int MAX_WIDTH = 2048;
	private final static int MAX_HEIGHT = 2048;

	public WebImageView(Context context) {
		this(context, null);
	}

	public WebImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WebImageView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		setCenter();
		handler = new Handler(this);
		
		if(map==null){
			map = new HashMap<String, SoftReference<Drawable>>();
		}
	}

	
	public void setImageUrl(Handler handler, int defaultRes, int Belong, String url, boolean beRound){
		setImageUrl(handler, defaultRes, ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID(), Belong, url, beRound);
	}
	
	public void setImageUrl(int defaultRes, int Belong, String url, boolean beRound){
		setImageUrl(defaultRes, ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID(), Belong, url, beRound);
	}
	
	/**
	 * @description 设置需要旋转的图片
	 * @param beRotate true 需要旋转
	 */
	public void setImageUrl(int defaultRes, int Belong, String url, boolean beRound, boolean beRotate){
		setImageUrl(defaultRes, ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID(), Belong, url, beRound);
		this.beRotate = beRotate;
	}

	/**
	 * 设置图片url，先加载本地默认图片的resID，再异步下载网络图片并替换
	 * @deprecated
	 * @param handler 无作用！！  为了兼容以前的接口
	 * @param defaultRes  默认的图片resID
	 * @param UID 用户的uid
	 * @param belong 文件属于  0：未定义， 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
	 * @param url 远程图片url地址 
	 * @param beRound 是否设置为圆形图片
	 */
	public void setImageUrl(Handler handler, int defaultRes, String UID, int belong, String url, boolean beRound) {
		setImageUrl(defaultRes, UID, belong, url, beRound);
	}

	/**
	 * 设置图片url，先加载本地默认图片的resID，再异步下载网络图片并替换
	 * 
	 * @param defaultRes  默认的图片resID
	 * @param UID 用户的uid
	 * @param belong 文件属于  0：未定义， 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
	 * @param url 远程图片url地址 
	 * @param beRound 是否设置为圆形图片
	 */
	public void setImageUrl(int defaultRes,String UID, int belong, String url, boolean beRound) {
		beRecycle = false;
		
		if (url == null || url.length() == 0 || url.equals("")) {
			Log.e(TAG, "url is null");
			if(defaultRes!=0){
				super.setImageResource(defaultRes);
				setCenter();
				isReloading=false;
			}
			return;
		}

		this.beRound = beRound;
		
		key = MD5.encode((UID+url).getBytes()) + ".jpg";
		
		Drawable saveDrawable = null;
		SoftReference<Drawable> result = map.get(key);
		if(result!=null){
			saveDrawable = result.get();
		}
		if(saveDrawable==null){
			if(defaultRes!=0){
				setImageDrawable(getResources().getDrawable(defaultRes));
				setCenter();
			}
			loadPicture(UID, url, belong);
		}else{
			mDrawable=(BitmapDrawable) saveDrawable;
			if(!mDrawable.getBitmap().isRecycled()){
				isReloading=false;
				setImageDrawable(saveDrawable);
				setCenter();
			}else{
				loadPicture(UID, url, belong);
			}
		}
	}

	
	
	/******************* 私有成员， 优先本地异步加载，  失败后再从网络获取，并加载本地 *******************/
	private void loadPicture(String UID, String url, int Belong) {

		loadtask = new LoadTask();
		loadtask.execute(UID,  url,  Belong);

	}
	
	private class LoadTask extends Thread{
		MediaValue mv;
		private String uid;
		private String url;
		private int belong;
		private String Key;
		
		public void execute(String UID, String url, int Belong) {
			// TODO Auto-generated method stub
			this.mv = AccountInfo.getInstance(UID).mediamapping.getMedia(UID, url);
			this.uid = UID;
			this.url = url;
			this.belong = Belong;
			this.start();
		}
		
	    public void run() {
	    	Bitmap saveBitmap;
			Bitmap tmp = null;

			if(MediaValue.checkMediaAvailable(mv, 2)){//load local
				Log.e(TAG, WebImageView.this + " load local: " + LoadTask.this);
				try {
					File file = new File(Environment.getExternalStorageDirectory() + mv.path);
					tmp = BitmapHelper.getBitmapFromInputStream(new FileInputStream(file));
//					if(beRotate)
					{
						tmp = ZGraphics.rotate(tmp, XUtils.getExifOrientation(Environment.getExternalStorageDirectory() + mv.path), true);
					}
					tmp = modifyImage(tmp);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}				
			} 
			
			if(tmp==null){ //load net	
				Log.e(TAG, WebImageView.this + " load net: " + LoadTask.this);
				Key = MD5.encode((uid+url).getBytes()) + ".jpg";
				String webimage_path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + Key;
				File webimage_file = new File(webimage_path);
				if(!webimage_file.getParentFile().exists()) {
					webimage_file.getParentFile().mkdirs();
				}
				
				MediaValue result = null;
				try {
					Log.e(TAG, "download task Key:" + Key + " url:" + url);

					ZHttp2 http2 = new ZHttp2();
					ZHttpResponse httpResponse = http2.get(url);

					ZHttpReader reader = new ZHttpReader(httpResponse.getInputStream(), null);
					if(httpResponse.getResponseCode()!=200){
						Log.e(TAG, "WebImageVIew - LoadTask - bad responseCode:" + httpResponse.getResponseCode());
						return;
					}
					byte[] imageData = reader.readAll(0);
					Log.d(TAG, "imageData.length="+imageData.length);
					
					if(imageData.length>0){
						// save file
						FileOutputStream mOutput = new FileOutputStream(webimage_file, false);
						
						mOutput.write(imageData);
						mOutput.close();

						result = new MediaValue();
						result.UID = uid;
						result.Belong = belong;
						result.Direction = 1;
						result.MediaType = 2;
						result.url = url;
						result.path = Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + Key;;
						result.realSize = imageData.length;
						result.Sync = 1;
						result.SyncSize = imageData.length;
						result.totalSize = imageData.length;
						AccountInfo.getInstance(result.UID).mediamapping.setMedia(result.UID, url, result);
						
						if (beRecycle)
						{
							Log.v(TAG, "interrupt loading pic");
							recycle();
							return;
						}
						
						if (MediaValue.checkMediaAvailable(result, 2)) {  
							// try to load local image file
							try {
								File file = new File(Environment.getExternalStorageDirectory() + result.path);
								tmp = BitmapHelper.getBitmapFromInputStream(new FileInputStream(file));
//								if(beRotate)
								{
									tmp = ZGraphics.rotate(tmp, XUtils.getExifOrientation(Environment.getExternalStorageDirectory() + mv.path), true);
								}
								tmp = modifyImage(tmp);
							} catch (FileNotFoundException e) {
								Log.e("Exception", e.getMessage(), e);
							}
						}
					}
					
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
			
			if (beRecycle)
			{
				if(tmp != null && !tmp.isRecycled()){
					tmp.recycle();
					tmp = null;
					recycle();
					Log.v(TAG, "interrupt loading pic");
				}
				return;
			}
			
			if(beRound){
				saveBitmap = getPortraitImage(tmp);
			}else{
				saveBitmap = tmp;
			}
			if(saveBitmap!=null){
				final BitmapDrawable saveDrawable = new BitmapDrawable(getResources(), saveBitmap);
				mDrawable=saveDrawable;
				isReloading=false;
				handler.post(new Runnable() {

					@Override
					public void run() {
						if(saveDrawable!=null&&!saveDrawable.getBitmap().isRecycled()){
							setImageDrawable(saveDrawable);
							setCenter();
							map.put(Key, new SoftReference<Drawable>(saveDrawable));
						}
						if (beRecycle)
						{
							recycle();
						}
					}
				});
			}

	    }
		
	}
	
	
	private Bitmap getPortraitImage(Bitmap bitmap){
		Bitmap ret = null;
		if(bitmap!=null){
			ret = BitmapUtils.getPortraitBitmap(bitmap);	
		}
	
		return ret;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private BitmapDrawable mDrawable;
	private boolean isReloading;
	/**
	 * 重新加载图片
	 */
	public void reload() {
		if (this.mDrawable == null&&getTag()!=null&&!isReloading) {
			String url=getTag().toString();
			Log.d(TAG, "reload url="+url);
			isReloading=true;
			setImageUrl(R.drawable.zhaopian_beijing_da, 1, url, false);
//			new LoadTask().execute(ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID(), url, 1);
		}
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		// 可能正在加载中
		beRecycle = true;
		if (mDrawable == null)
			return;
		Bitmap bitmap=mDrawable.getBitmap();
		mDrawable=null;
		setImageBitmap(null);
		if(!bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	public void setCenter() {
		if(ScaleType.MATRIX==getScaleType()){
			Bitmap bitmap =((BitmapDrawable)getDrawable()).getBitmap();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
//			int disWidth = getMeasuredWidth();
			Log.v(TAG, "bitmap width = " + width);
			Log.v(TAG, "dis Width = " + disWidth);
			int disHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
//			int disHeight = getMeasuredHeight();
			Log.v(TAG, "bitmap height = " + height);
			Log.v(TAG, "dis Height = " + disHeight);
			float scaleWidth = ((float)disWidth) / ((float)width);
			float scaleHeight = ((float)disHeight) / ((float)height);
			float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			float scaleX = (disWidth - width * scale) / 2;
			float scaleY = (disHeight - height * scale) / 2;
			matrix.postTranslate(scaleX, scaleY);
			setImageMatrix(matrix);
		}
	}
	
	// 对bigmap处理 保证不超过MAX_WIDTH和MAX_HEIGHT
	private Bitmap modifyImage(Bitmap bitmap)
	{
		if(bitmap == null)
		{
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
		int disHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
		
		int maxWidth = MAX_WIDTH > disWidth ? disWidth : MAX_WIDTH;
		int maxHeight = MAX_HEIGHT > disHeight ? disHeight : MAX_HEIGHT;
		
		Log.v(TAG, "bitmap width = " + width);
		Log.v(TAG, "bitmap height = " + height);
		float scaleWidth = ((float)maxWidth) / ((float)width);
		float scaleHeight = ((float)maxHeight) / ((float)height);
		float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
		
		if(scale < 1f)
		{
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 
		}
		return bitmap;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		try
		{
			super.onDraw(canvas);
		}
		catch (Exception e)
		{
			Log.v(TAG, "onDraw() Canvas: trying to use a recycled bitmap");
		}
	}
	
}
