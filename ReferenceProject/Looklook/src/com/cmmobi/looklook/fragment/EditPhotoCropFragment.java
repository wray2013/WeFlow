package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditPhotoDetailActivity;
import com.cmmobi.looklook.common.imagecrop.CropImageView2;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobivideo.utils.XUtils;

public class EditPhotoCropFragment extends Fragment implements OnClickListener{

	private CropImageView2 cropImageView = null;
	private EditPhotoDetailActivity mActivity;
	private static final String TAG = "EditPhotoCropFragment";
	private Bitmap mBitmap = null;
	private ImageView btn_yes = null;
	private ImageView btn_no = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_photo_crop, container, false);
		cropImageView = (CropImageView2) view.findViewById(R.id.iv_edit_photo_crop);
		initImageView();
		
		btn_yes = (ImageView) view.findViewById(R.id.iv_edit_photo_crop_yes);
		btn_no = (ImageView) view.findViewById(R.id.iv_edit_photo_crop_no);
		btn_yes.setOnClickListener(this);
		btn_no.setOnClickListener(this);
		
		return view;
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.iv_edit_photo_crop_yes:
			mActivity.isMainAttachChanged = true;
			mActivity.isPhotoCroped = true;
			setPreBitmap();
			mActivity.mRect = cropImageView.getCropRect();
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		case R.id.iv_edit_photo_crop_no:
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.d(TAG, "EditPhotCropFragment - onAttach");

		try {
			mActivity = (EditPhotoDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
	public void initImageView() {
		Log.d(TAG,"EditPhotoCropFragment initImageView in");
		ViewTreeObserver vto2 = cropImageView.getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() { 
				if (mActivity.pictureAttachPath != null) {
			    	int height = cropImageView.getMeasuredHeight(); 
			    	int width  = cropImageView.getMeasuredWidth(); 
			    	mBitmap = BitmapUtils.readBitmapAutoSize(mActivity.pictureAttachPath, width, height);
			    	
			    	int orientaiton = XUtils.getExifOrientation(mActivity.pictureAttachPath);
			    	if(orientaiton > 0 && mBitmap != null){
			    		Log.d(TAG,"getConfig："+mBitmap.getConfig().name() + " width = " + width + " height = " + height + " bmpWidth = " + mBitmap.getWidth() + " bmpHeight = " + mBitmap.getHeight());
			    		Bitmap originBitmap2 = EditPhotoDetailActivity.rotate(mBitmap, orientaiton, true);
			    		if(originBitmap2 != null){
			    			mBitmap.recycle();
			    			mBitmap = originBitmap2;
			    		}
			    	}
			    	cropImageView.setImageBitmap(mBitmap);
			    	Log.d(TAG,"CropImageView setImageBitmap in");
			    	if (mActivity.effectId != 0) {
			    		Bitmap bmp = mActivity.switchEffect(mActivity.effectId, mBitmap);
			    		if (bmp == null) {
			    			bmp = mBitmap;
			    		}
			    		cropImageView.setImageBitmap(bmp);
			    	}
			    	
			    	cropImageView.setCropRect(mActivity.mRect);
				} 
				
				cropImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);         
			}   
		});
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public int getCropImageViewWidth() {
		return cropImageView.getMeasuredWidth();
	}
	
	public int getCropImageViewHeight() {
		return cropImageView.getMeasuredHeight();
	}
	
	public Rect getCropRect() {
		return cropImageView.getCropRect();
	}
	
	public Bitmap cropBitmap() {
		Bitmap bmp = getBitmap();
		Bitmap scaledBmp = createScaledBitmap(bmp, getCropImageViewWidth(), getCropImageViewHeight(), Config.ARGB_8888);
		Log.d(TAG,"cropBitmap 11111 bmpgetConfig = " + bmp.getConfig().name() + " scaledBmpconfig = " + scaledBmp.getConfig().name());
		Rect rc = getCropRect();
		Bitmap bitmap = Bitmap.createBitmap(scaledBmp, rc.left, rc.top, (rc.right - rc.left), (rc.bottom - rc.top));
		Log.d(TAG,"rc = " + rc + " bitmapWidth = " + bitmap.getWidth() + " bitmapHeight = " + bitmap.getHeight() + " config = " + bitmap.getConfig().name());
		return bitmap;
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
        
        return bitmap;
        
    }
	
	public void setPreBitmap() {
		Bitmap bmp = cropBitmap();
		mActivity.originBitmap = bmp;
		if (mActivity.effectId != 0) {
    		bmp = mActivity.switchEffect(mActivity.effectId, bmp);
    		if (bmp == null) {
    			bmp = mActivity.originBitmap;
    		}
    	}
		Log.d(TAG,"setPreBitmap getConfig："+bmp.getConfig().name());
		mActivity.getPreView().setImageBitmap(bmp);
		mActivity.getThumbView().setImageBitmap(bmp);
	}
}
