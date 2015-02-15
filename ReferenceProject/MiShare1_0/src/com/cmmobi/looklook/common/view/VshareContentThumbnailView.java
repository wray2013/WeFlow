package com.cmmobi.looklook.common.view;

import java.util.Calendar;
import java.util.Date;

import android.R.bool;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.text.style.ParagraphStyle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 用于显示带创建者头像的内容缩略图
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-10-25
 */
public class VshareContentThumbnailView extends RelativeLayout {

	
	private int size = 0;
	
	public VshareContentThumbnailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VshareContentThumbnailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VshareContentThumbnailView(Context context) {
		super(context);
		init();
	}

	private LayoutInflater inflater;
	
	private VshareThumbnailView ctv_vshare;
	private ImageView iv_portrait;		
	private TextView tv_sharetime;
	private ImageView iv_undisturb;
	private ImageView iv_new;
		
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private void init() {
		inflater = LayoutInflater.from(getContext());

		View v;
		v = inflater.inflate(R.layout.include_vshare_content_thumbnail_view, null);
			 
		ctv_vshare = (VshareThumbnailView) v.findViewById(R.id.ctv_vshare);
		iv_portrait = (ImageView) v.findViewById(R.id.iv_portrait);
		tv_sharetime = (TextView) v.findViewById(R.id.tv_sharetime);
		iv_undisturb = (ImageView) v.findViewById(R.id.iv_undisturb);
		iv_new = (ImageView) v.findViewById(R.id.iv_new);
		addView(v);
		
		imageLoader = ImageLoader.getInstance();
		//imageLoader.init(ImageLoaderConfiguration.createDefault(VshareContentThumbnailView.this.getContext()));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}
	
	/*
	 * 设置是否显示评论数,设置评论数c
	 */
	public void setViewIsShowComment(boolean isShow, String c){
		ctv_vshare.setViewIsShowComment(isShow, c);	
	}
	
	public void setViewIsShowComment(boolean isShow){
		if(isShow){
			iv_new.setVisibility(View.VISIBLE);
		}else{
			iv_new.setVisibility(View.GONE);
		}
	}
	
	/*
	 * 获取是否显示评论数
	 */
	public boolean getViewIsShowComment(){
		return ctv_vshare.getViewIsShowComment();	
	}
	
	/*
	 * 设置是否显示免打扰
	 */
	public void setViewIsShowUndisturb(boolean isShow){
		if(isShow){
			iv_undisturb.setVisibility(View.VISIBLE);	
		}else{
			iv_undisturb.setVisibility(View.GONE);	
		}
		
	}
	
	/*
	 * 获取是否显示免打扰
	 */
	public boolean getViewIsShowUndisturb(){
		if(iv_undisturb.getVisibility() == View.VISIBLE){
			return true;
		}
		return false;	
	}
		
	/*
	 * 设置view的大小
	 */
	private void setViewSize(int size){
	 if(size == 0) return;
	 RelativeLayout.LayoutParams params = null;
	 params = (RelativeLayout.LayoutParams) ctv_vshare.getLayoutParams();
	 params.height = size-DensityUtil.dip2px(VshareContentThumbnailView.this.getContext(), 5);
	 params.width = size-DensityUtil.dip2px(VshareContentThumbnailView.this.getContext(), 5);
	 ctv_vshare.setLayoutParams(params);

	 params = (RelativeLayout.LayoutParams) iv_portrait.getLayoutParams();
	 params.height = size/4;
	 params.width = size/4;
	 iv_portrait.setLayoutParams(params);
	}
	
	@Override
	public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
		// TODO Auto-generated method stub
		super.setLayoutParams(params);
		
		if(params.width > 0){
			size = params.width;
			setViewSize(size);
		}	
	}
	
	/*
	 * 设置此view是否选中
	 */
	public void setViewSelected(boolean isSelected){
		ctv_vshare.setViewSelected(isSelected);
	}
	
	/*
	 * 获取此view是否选中
	 */
	public Boolean getViewSelected(){
		return ctv_vshare.getViewSelected();
	}
	

	/*
	 * 设置缩略图显示的日记、头像等数据
	 */
	public 	void setContentDiaries(String portrait , String time, String isCapsule, String isThrowaway, String isRead, VshareDiary...diaries){
		ctv_vshare.setContentDiaries(isCapsule, isThrowaway, isRead, diaries);
		if(time != null){
			Date date = new Date(Long.parseLong(time));
			tv_sharetime.setText("发布于 " + DateUtils.getMyCommonShowDate(date));
			tv_sharetime.setVisibility(View.VISIBLE);
		}else{
			tv_sharetime.setVisibility(View.GONE);
		}
		imageLoader.displayImageEx(portrait, iv_portrait, options, animateFirstListener, ActiveAccount.getInstance(MainApplication.getInstance()).getUID(), 1);
	}
}
