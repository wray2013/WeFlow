package com.cmmobi.looklook.common.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.XUtils;


public class PhotoClipUtil implements OnTouchListener, Callback {
	
	private final String TAG = "PhotoClipUtil";
	private Activity mActivity = null;
	private DisplayMetrics dm = new DisplayMetrics();
	private ImageView photoView = null;
	private ImageView clipView = null;
	private View doneBtn = null;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	
	int mode = NONE;
	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	// 两指间距
	float oldDist = 1f;
	float newDist = 1f;
	
	private ImageState mapState = new ImageState();
	private int leftEdge = 0;
	private int rightEdge = 0;
	private int topEdge = 0;
	private int bottomEdge = 0;
	
	private boolean isClipViewShow = false;
	private String picPath = "";
	private float minScale = 1.0f;
	private float maxScale = 50.0f;
	private float widthHeightScale = 1.0f;
	private int clipWidth = 0;
	private int strokeWidth = 2;
	private long lastClickedTime = 0;
	private final int PHOTO_CLIP_WIDTH = 1080;
	private final int PHOTO_CLIP_HEIGHT = 1080;
	private Handler handler;
	private DecodeBmpThread decodeBmpThread = null;
	private final int BITMAP_DECODE_DONE = 0x0012353;
	private final int BITMAP_DECODE_FAILE = 0x0012354;
	private final int BITMAP_DECODE_OOM = 0x0012355;
	
	public PhotoClipUtil(Activity activity,ImageView photoView,ImageView clipView,String picPath) {
		this(activity,photoView,clipView,picPath,1.0f);
	}
	
	public PhotoClipUtil(Activity activity,ImageView photoView,ImageView clipView,String picPath,float scale) {
		mActivity = activity;
		this.photoView = photoView;
		this.clipView = clipView;
		this.picPath = picPath;
		this.widthHeightScale = scale;
		dm = mActivity.getResources().getDisplayMetrics();
		clipWidth = dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;
		Bitmap bmp = createClipBmp(scale);
		clipView.setImageBitmap(bmp);
		handler = new Handler(this);
		decodeBmpThread = new DecodeBmpThread();
		ViewTreeObserver vto1 = clipView.getViewTreeObserver();   
		vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() {
				ImageState state = new ImageState();
				getMapState(PhotoClipUtil.this.clipView, PhotoClipUtil.this.clipView.getImageMatrix(), state);
				leftEdge = (int) 0;
				rightEdge = (int) clipWidth;
				topEdge = (int) state.getTop();
				bottomEdge = (int) state.getBottom();
				
				isClipViewShow = true;
				
				ZDialog.show(R.layout.progressdialog, false, true, mActivity,false);
				decodeBmpThread.start();
//				preTranslate();
				Log.d(TAG,"left = " + leftEdge + " right = " + rightEdge + " top = " + topEdge + " bottom = " + bottomEdge);
				PhotoClipUtil.this.clipView.getViewTreeObserver().removeGlobalOnLayoutListener(this); 
			}   
		});
		
		
	}
	
	public void setDoneBtn(View btn) {
		this.doneBtn = btn;
	}
	
	private Bitmap createClipBmp(float scale) {
		int clipHeight = (int) (clipWidth * scale);
		Bitmap bmp = Bitmap.createBitmap(clipWidth, clipHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(0xff1f7efb);
		canvas.drawRect(0, 0, clipWidth, strokeWidth, paint);
		canvas.drawRect(0, 0, strokeWidth, clipHeight, paint);
		canvas.drawRect(clipWidth - strokeWidth , 0, clipWidth, clipHeight, paint);
		canvas.drawRect(0, clipHeight - strokeWidth, clipWidth, clipHeight, paint);
		return bmp;
	}
	
	/**
	 * 将原始图片缩放成最小边为裁剪框宽度。
	 * @return
	 */
	private Bitmap initImage() {
		final Rect rect = clipView.getDrawable().getBounds(); 
		Bitmap originBitmap = BitmapUtils.readBitmapAutoSize(picPath, dm.widthPixels, dm.heightPixels);
		originBitmap = ZGraphics.rotate(originBitmap, XUtils.getExifOrientation(picPath), true);
		if (originBitmap == null) {
			Prompt.Alert(mActivity, "图片不存在");
			mActivity.finish();
		}
		int width = originBitmap.getWidth();
		int height = originBitmap.getHeight();
		float imageScale = (float)height / width;
		int scaleWidth = 0;
		int scaleHeight = 0;
		float scale = 1.0f;
		if (imageScale >= widthHeightScale) {// 若原图高宽比>=裁剪框高宽比 则将原图宽缩放到裁剪框的宽度
			scale = (float)width / clipWidth;
		} else {// 若原图高宽比<裁剪框高宽比 则将原图宽缩放到裁剪框的高度
			scale = height / (widthHeightScale * clipWidth);
		}
		
		scaleWidth = (int) (width / scale);
		scaleHeight = (int) (height / scale);
		
		
//				if (minLength > rect.width()) {
//					int minScaleLength = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
//					minScale = rect.width() / ((float) minScaleLength);
//				}
		Log.d(TAG,"initImage width = " + width + " height = " + height + " imageScale = " + imageScale
				+ " rectwidth = " + rect.width() + " rectHeight = " + rect.height() 
				+ " scale = " + scale + " scaleWidth = " + scaleWidth + " scaleHeight = " + scaleHeight + " minScale = " + minScale);
		
		return createScaledBitmap(originBitmap, scaleWidth, scaleHeight, Config.ARGB_8888);
	}
	
	private int retryTime = 0;
	
	private class DecodeBmpThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Rect rect = clipView.getDrawable().getBounds(); 
				System.gc();
				Bitmap originBitmap = BitmapUtils.readBitmapAutoSize(picPath, dm.widthPixels, dm.heightPixels);
				int angle = XUtils.getExifOrientation(picPath);
				originBitmap = ZGraphics.rotate(originBitmap, angle, true);
				if (originBitmap == null) {
					Prompt.Alert(mActivity, "图片不存在");
					mActivity.finish();
				}
				int width = originBitmap.getWidth();
				int height = originBitmap.getHeight();
				float imageScale = (float)height / width;
				int scaleWidth = 0;
				int scaleHeight = 0;
				float scale = 1.0f;
				if (imageScale >= widthHeightScale) {// 若原图高宽比>=裁剪框高宽比 则将原图宽缩放到裁剪框的宽度
					scale = (float)width / clipWidth;
				} else {// 若原图高宽比<裁剪框高宽比 则将原图宽缩放到裁剪框的高度
					scale = height / (widthHeightScale * clipWidth);
				}
				
				scaleWidth = (int) (width / scale);
				scaleHeight = (int) (height / scale);
				
//				if (minLength > rect.width()) {
//					int minScaleLength = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
//					minScale = rect.width() / ((float) minScaleLength);
//				}
				Log.d(TAG,"initImage width = " + width + " height = " + height + " imageScale = " + imageScale
						+ " rectwidth = " + rect.width() + " rectHeight = " + rect.height() + " angle = " + angle
						+ " scale = " + scale + " scaleWidth = " + scaleWidth + " scaleHeight = " + scaleHeight + " minScale = " + minScale);
				
				Bitmap bmp =  createScaledBitmap(originBitmap, scaleWidth, scaleHeight, Config.ARGB_8888);
				Message msg = new Message();
				msg.what = BITMAP_DECODE_DONE;
				msg.obj = bmp;
				handler.sendMessage(msg);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				System.gc();
				if (retryTime >= 3){
					handler.sendEmptyMessage(BITMAP_DECODE_FAILE);
					return;
				}
				retryTime ++;
				handler.sendEmptyMessage(BITMAP_DECODE_OOM);
//				this.start();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Bitmap createScaledBitmap(Bitmap putBitmap ,int dstW,int dstH, Config dstConfig) {
        Bitmap bitmap = Bitmap.createBitmap(dstW, dstH, dstConfig);
        Canvas c = new Canvas(bitmap);
        Paint p = new Paint();
        p.setDither(true);
        Rect src = new Rect(0,0,putBitmap.getWidth(),putBitmap.getHeight());
        RectF dst = new RectF(0, 0, dstW, dstH);
        
        //自己设置这两个rect 就可进行缩放
        c.drawBitmap(putBitmap , src, dst, p);
        putBitmap.recycle();
        putBitmap = null;
        
        return bitmap;
    }
	
	private void getMapState(Matrix mtx,ImageState imageState) {
		Rect rect = photoView.getDrawable().getBounds(); 
		float[] values = new float[9]; 
		mtx.getValues(values); 
		imageState.setLeft(values[2]); 
		imageState.setTop(values[5]); 
		imageState.setRight(imageState.getLeft() + rect.width() * values[0]);
		imageState.setBottom(imageState.getTop() + rect.height() * values[4]);
		imageState.setScale(values[0]);
		
		Log.d(TAG,"getMapState matrix = " + matrix + "\n" + " state : " + imageState + " width = " + rect.width() + " height = " + rect.height());
	}
	
	/**
	 * 获取图片位置信息
	 * @param imageView
	 * @param mtx
	 * @param imageState
	 */
	private void getMapState(ImageView imageView,Matrix mtx,ImageState imageState) {
		Rect rect = imageView.getDrawable().getBounds(); 
		float[] values = new float[9]; 
		mtx.getValues(values); 
		imageState.setLeft(values[2]); 
		imageState.setTop(values[5]); 
		imageState.setRight(imageState.getLeft() + rect.width() * values[0]); 
		imageState.setBottom(imageState.getTop() + rect.height() * values[4]);
		imageState.setScale(values[0]);
		
		Log.d(TAG,"**getMapState matrix = " + mtx + "\n" + " state : " + imageState + " width = " + rect.width() + " height = " + rect.height());
	}
	
	/**
	 * 将图片居中显示
	 */
	private void preTranslate(Bitmap bmp) {
		if (isClipViewShow) {
			
			photoView.setImageBitmap(bmp);
			Matrix mtx = photoView.getImageMatrix(); 
			getMapState(mtx, mapState);
			
			int x1 = (leftEdge + rightEdge) / 2;
			int y1 = (topEdge + bottomEdge) / 2;
			int x2 = (int) ((mapState.getLeft() + mapState.getRight()) / 2);
			int y2 = (int) ((mapState.getTop() + mapState.getBottom()) / 2);
			matrix.postTranslate(x1 - x2, y1 - y2);
			Log.d(TAG,"preTranslate matrix = " + matrix + "\n" + "   state : " + mapState);
			Log.d(TAG,"leftEdge = " + leftEdge + " rightEdge = " + rightEdge + " topEdge = " + topEdge + " bottomEdge = " + bottomEdge + " x1 - x2 = " + (x1 - x2) + " y1 - y2 = " + (y1 - y2));
			photoView.setImageMatrix(matrix);
			getMapState(matrix, mapState);
			
			isClipViewShow = false;
		}
	}
	
	public void resetPic(Bitmap bmp) {
		ImageState state = new ImageState();
		getMapState(PhotoClipUtil.this.clipView, PhotoClipUtil.this.clipView.getImageMatrix(), state);
		leftEdge = (int) state.getLeft();
		rightEdge = (int) state.getRight();
		topEdge = (int) state.getTop();
		bottomEdge = (int) state.getBottom();
		
		photoView.setImageBitmap(bmp);
		matrix = new Matrix();
		photoView.setImageMatrix(matrix);
		getMapState(matrix, mapState);
		
		int x1 = (leftEdge + rightEdge) / 2;
		int y1 = (topEdge + bottomEdge) / 2;
		int x2 = (int) ((mapState.getLeft() + mapState.getRight()) / 2);
		int y2 = (int) ((mapState.getTop() + mapState.getBottom()) / 2);
		matrix.postTranslate(x1 - x2, y1 - y2);
		Log.d(TAG,"preTranslate matrix = " + matrix + "\n" + "   state : " + mapState);
		photoView.setImageMatrix(matrix);
	}
	
	public String saveClipImage(boolean flag) {
		Log.d(TAG,"saveClipImage  imageState = " + mapState + " matrix = " + matrix + " topEdge = " + topEdge);
		Bitmap viewBitmap = loadBitmapFromView(photoView);
		Bitmap saveBmp = Bitmap.createBitmap(viewBitmap, leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge);
		String imageID = DiaryController.getNextUUID();
		String outputFilePath = DiaryController.getAbsolutePathUnderUserDir() + "/pic/" + imageID + ".jpg" ;
		Log.d(TAG,"outputFile = " + outputFilePath);
		if(BitmapUtils.saveBitmap2file(saveBmp, outputFilePath)) {
			Log.d(TAG,"saveBitmap2file sucessed imageID = " + imageID + " this = " + this);
		}
		return outputFilePath;
	}
	
	public String saveClipImage() {
		String imageID = DiaryController.getNextUUID();
		String outputFilePath = DiaryController.getAbsolutePathUnderUserDir() + "/pic/" + imageID + ".jpg" ;
		BitmapFactory.Options opt = BitmapUtils.getBitmapOption(picPath);
		if (opt == null) {
			return null;
		}
		int oriWidth = opt.outWidth;
		int oriHeight = opt.outHeight;
		int oriShortEdge = oriWidth < oriHeight ? oriWidth : oriHeight;
		double shortEdge = mapState.getWidth() < mapState.getHeight()?mapState.getWidth():mapState.getHeight();
		// outWidth 在原图中裁剪outWidth宽度的正方形区域。
		int outWidth =(int) ((clipWidth / (float)shortEdge) * oriShortEdge);
		int xoffset = (int) (((leftEdge - mapState.getLeft()) / (float)shortEdge) * oriShortEdge);
		int yoffset = (int) (((topEdge - mapState.getTop()) / (float)shortEdge) * oriShortEdge);
		/*PhotoCropNative.jpegCrop(picPath,outputFilePath,outWidth,outWidth,xoffset,yoffset);
		Bitmap originBitmap = BitmapUtils.readBitmapAutoSize(outputFilePath, PHOTO_CLIP_WIDTH, PHOTO_CLIP_HEIGHT);
		Bitmap outBitmap = createScaledBitmap(originBitmap, PHOTO_CLIP_WIDTH, PHOTO_CLIP_WIDTH, Config.ARGB_8888);
		if(BitmapUtils.saveBitmap2file(outBitmap,50, outputFilePath)) {
			Log.d(TAG,"outputFilePath = " + outputFilePath + " outWidth = " + outWidth 
					+ " xoffset = " + xoffset + " yoffset = " + yoffset + " clipWidth = " + clipWidth
					+ " shortEdge = " + shortEdge + " left = " + (leftEdge - mapState.getLeft()) + " top = " + (topEdge - mapState.getTop()));
		}*/
		int oriLongEdge = oriWidth + oriHeight - oriShortEdge;
		int angle = XUtils.getExifOrientation(picPath);
		Log.d(TAG,"outputFilePath angle = " + angle + " oriWidth = " + oriWidth + " oriHeight = " + oriHeight + " xoffset = " + xoffset + " yoffset = " + yoffset);
		if (angle == 90) {
			int oriXoffset = xoffset;
			xoffset = yoffset;
			yoffset = oriShortEdge - outWidth - oriXoffset;
		} else if (angle == 180) {
			int oriXoffset = xoffset;
			xoffset = oriWidth - outWidth - oriXoffset;
			yoffset = oriHeight - outWidth - yoffset;
		} else if (angle == 270) {
			int oriXoffset = xoffset;
			xoffset = oriLongEdge - outWidth - yoffset;
			yoffset = oriXoffset;
		}
		
		if (yoffset < 0) {
			yoffset = 0;
		}
		
		if (xoffset < 0) {
			xoffset = 0;
		}
		
		Bitmap originBitmap = BitmapUtils.cropBitmap(picPath, outWidth, xoffset, yoffset, PHOTO_CLIP_WIDTH);
		if (originBitmap == null) {
			return null;
		}
		Log.d(TAG,"width = " + originBitmap.getWidth() + " height = " + originBitmap.getHeight());
		
		Bitmap outBitmap = null;
		try {
			outBitmap = createScaledBitmap(originBitmap, PHOTO_CLIP_WIDTH, PHOTO_CLIP_WIDTH, Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			Log.d(TAG,"createScaledBitmap failed OOM");
			return null;
		}
		if(BitmapUtils.saveBitmap2file(outBitmap,50, outputFilePath)) {
			Log.d(TAG,"outputFilePath = " + outputFilePath + " outWidth =  " + outWidth 
					+ " xoffset = " + xoffset + " yoffset = " + yoffset + " clipWidth = " + clipWidth
					+ " shortEdge = " + shortEdge + " left = " + (leftEdge - mapState.getLeft()) + " top = " + (topEdge - mapState.getTop()));
		}
		XUtils.setExifOrientation(outputFilePath, angle);
		
		BitmapUtils.releaseBmp(originBitmap);
		BitmapUtils.releaseBmp(outBitmap);
//		releaseView();
		
		return outputFilePath;
	}
	
	private void releaseView(ImageView view) {
		if (view != null) {
			BitmapDrawable mDrawable =  (BitmapDrawable) view.getDrawable();
			if (mDrawable != null) {
				Bitmap image = mDrawable.getBitmap();
				BitmapUtils.releaseBmp(image);
			}
		}
	}
	
	public void releaseView() {
		releaseView(photoView);
		releaseView(clipView);
	}
	
	public static Bitmap loadBitmapFromView(View v) {
		if (v == null) {
			return null;
		}
		Bitmap screenshot = null;
		screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(),Config.ARGB_8888);
		Canvas c = new Canvas(screenshot);
		v.draw(c);
		return screenshot;
	}
	
	public String saveClipImage(String imageID) {
		photoView.setDrawingCacheEnabled(true);  
		Bitmap viewBitmap = photoView.getDrawingCache();
		Bitmap saveBmp = Bitmap.createBitmap(viewBitmap, leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge);
		String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT +  "/pic";
		String outputFilePath = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR +   "/" + imageID + ".jpg" ;
		Log.d(TAG,"outputFile = " + outputFilePath);
		if(BitmapUtils.saveBitmap2file(saveBmp, outputFilePath)) {
			Log.d(TAG,"saveClipImage(String imageID) saveBitmap2file sucessed");
		}
		photoView.setDrawingCacheEnabled(false);
		return outputFilePath;
	}
	
	public boolean isClipped() {
		ImageState state = new ImageState();
		getMapState(PhotoClipUtil.this.photoView, PhotoClipUtil.this.photoView.getImageMatrix(), state);
		return !(leftEdge == state.getLeft() 
				&& rightEdge == state.getRight()
				&& topEdge == state.getTop()
				&& bottomEdge == state.getBottom());
	}
	
	public Bitmap getClipBmp() {
		photoView.setDrawingCacheEnabled(true);  
		Bitmap viewBitmap = photoView.getDrawingCache();
		Bitmap saveBmp = Bitmap.createBitmap(viewBitmap, leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge);
		photoView.setDrawingCacheEnabled(false);
		return saveBmp;
	}
	
	public Bitmap getClipBmp(Bitmap oriBmp) {
		ImageState state = new ImageState();
		getMapState(matrix, state);
		float scale = state.getScale();
		float left = state.getLeft();
		float top = state.getTop();
		float bmpScale = ((float)(rightEdge - leftEdge)) / oriBmp.getWidth();
		scale *= bmpScale;
		
		Log.d(TAG,"width = " + oriBmp.getWidth() + " height = " + oriBmp.getHeight());
		
		int width = (int) ((rightEdge - leftEdge) / scale);
		int scaleLeft = (int) ((leftEdge - left) / scale);
		int scaleTop = (int) ((topEdge - top) /scale);
		Bitmap tempBmp = Bitmap.createBitmap(oriBmp, scaleLeft, scaleTop, width, width);
		return Bitmap.createScaledBitmap(tempBmp, (rightEdge - leftEdge), (rightEdge - leftEdge), true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		ImageView view = (ImageView) v;
		
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			// 设置初始点位置
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
			long nowClickTime = System.currentTimeMillis();
			/*Log.d(TAG,"MotionEvent.ACTION_UP nowClickTime = " + (nowClickTime - lastClickedTime));
			if (nowClickTime - lastClickedTime < 200) {
				Log.d(TAG,"*********MotionEvent.ACTION_UP************");
			}*/
			lastClickedTime = nowClickTime;
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
			} else if (mode == ZOOM) {
				newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist + " savedMatrix = " + savedMatrix);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					float oldScale = getXscale(matrix);
					if (oldScale * scale < 50.0f) {
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
					
					float newScale = getXscale(matrix);
					Log.d(TAG,"ZOOM scale = " + scale + " minScale = " + minScale + " maxScale = " + maxScale + " newScale = " + newScale);
					if (newScale < minScale) {// 缩小时将偏移调整至以前位置
//						matrix.setTranslate(getXtranslate(savedMatrix), getYtranslate(savedMatrix));
						matrix.postTranslate(getXtranslate(savedMatrix) - getXtranslate(matrix),
								getYtranslate(savedMatrix) - getYtranslate(matrix));
					}
					
					
					
					Log.d(TAG,"ZOOM Over matrix = " + matrix);
					
					oldDist = newDist;
					savedMatrix.set(matrix);
					midPoint(mid, event);
				}
			}
			break;
		}
		
		getMapState(matrix, mapState);
		if (mapState.getLeft() <= leftEdge && mapState.getRight() >= rightEdge 
				&& mapState.getTop() <= topEdge && mapState.getBottom() >= bottomEdge) {
			view.setImageMatrix(matrix);
		} else {
			savedMatrix.set(matrix);
			if (mode == DRAG) {
				postOverEdge();
				view.setImageMatrix(matrix);
			} else {
				postOverScale();
				view.setImageMatrix(matrix);
			}
		}
		Log.d(TAG,"----------------------------------------------------------");
		return true;
	}
	
	private float getXscale(Matrix mtx) {
		float[] values = new float[9]; 
		mtx.getValues(values); 
		return values[0];
	}
	
	private float getYscale(Matrix mtx) {
		float[] values = new float[9]; 
		mtx.getValues(values); 
		return values[4];
	}
	
	private float getXtranslate(Matrix mtx) {
		float[] values = new float[9]; 
		mtx.getValues(values); 
		return values[2];
	}
	
	private float getYtranslate(Matrix mtx) {
		float[] values = new float[9]; 
		mtx.getValues(values); 
		return values[5];
	}
	
	private void postOverScale() {
		Log.d(TAG,"postOverScale in.........");
		if (getXscale(matrix) < minScale) {
			Log.d(TAG,"postOverScale in++++++++");
			float xTrans = getXtranslate(matrix);
			float yTrans = getYtranslate(matrix);
			matrix.setScale(minScale, minScale);
			matrix.postTranslate(xTrans, yTrans);
		}
		
		getMapState(matrix, mapState);
		
		postOverEdge();
	}
	
	private void postOverEdge() {
		if (mapState.getLeft() > leftEdge) {
			matrix.postTranslate(leftEdge - mapState.getLeft(), 0);
		}
		
		if (mapState.getRight() < rightEdge) {
			matrix.postTranslate(rightEdge - mapState.getRight(), 0);
		}
		
		if (mapState.getTop() > topEdge) {
			matrix.postTranslate(0, topEdge - mapState.getTop());
		}
		
		if (mapState.getBottom() < bottomEdge) {
			matrix.postTranslate(0, bottomEdge - mapState.getBottom());
		}
		Log.d(TAG,"postOverEdge in ... matrix = " + matrix);
	}
	
	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case BITMAP_DECODE_DONE:
			ZDialog.dismiss();
			if (msg.obj != null) {
				Bitmap bmp = (Bitmap) msg.obj;
				preTranslate(bmp);
				photoView.setOnTouchListener(this);
				if (doneBtn!= null) {
					Log.d(TAG,"handleMessage doneBtn enable true");
					doneBtn.setEnabled(true);
				}
			} else {
				Prompt.Alert("图片解析失败");
				mActivity.finish();
			}
			break;
		case BITMAP_DECODE_FAILE:
			ZDialog.dismiss();
			Prompt.Alert("图片解析失败");
			mActivity.finish();
			break;
		case BITMAP_DECODE_OOM:
			Log.d(TAG,"BITMAP_DECODE_OOM in");
			decodeBmpThread = new DecodeBmpThread();
			decodeBmpThread.start();
			break;
		}
		return false;
	}
}
