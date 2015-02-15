package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.UniversalImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 用于显示内容缩略图,一个微享
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-03-12
 */
public class VshareThumbnailViewOneItem extends RelativeLayout {
	
	private static String TAG = "ContentThumbnailViewOneItem";
	private int size = 0;
	
	// "type": "1" ，// 1视频、2音频 3图片(如果是单内容有效)
	public static String TYPE_VIDEO = "1";
	public static String TYPE_AUDIO = "2";
	public static String TYPE_PIC = "3";
	
	public VshareThumbnailViewOneItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VshareThumbnailViewOneItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VshareThumbnailViewOneItem(Context context) {
		super(context);
		init();
	}

	private LayoutInflater inflater;
	
	//整个布局内容
	private RelativeLayout rl_content;
	
	//一张图片时的布局
	private ImageView iv_one_photo;
	
	//只有纯语音时的布局
	private TextView tv_chunyuyin_time;
	
	//下面的信息条布局
	private RelativeLayout rl_media_info;
	private TextView tv_media_right_info;
	private TextView tv_media_middle_info;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private void init() {
		inflater = LayoutInflater.from(getContext());
		View v;
		v = inflater.inflate(R.layout.include_vshare_thumbnail_view_one_item, null);
		rl_content = (RelativeLayout) v.findViewById(R.id.rl_content);
		iv_one_photo = (ImageView) v.findViewById(R.id.iv_one_photo);	
		tv_chunyuyin_time = (TextView) v.findViewById(R.id.tv_chunyuyin_time);
		
		rl_media_info = (RelativeLayout) v.findViewById(R.id.rl_media_info);
		tv_media_right_info = (TextView) v.findViewById(R.id.tv_media_right_info);
		tv_media_middle_info = (TextView) v.findViewById(R.id.tv_media_middle_info);
		
		addView(v);
			
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren)
		.showImageForEmptyUri(R.drawable.moren)
		.showImageOnFail(R.drawable.moren)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}

	/*
	 * 设置缩略图内容，即MyDiary
	 * @param: myDiary 该缩略图对应的日记
	 * 1视频、2音频、3图片
	 */
	public void setContentDiary(VshareDiary myDiary){
		if(myDiary == null){
			return;
		}
		String type = myDiary.type;
		if(type!=null&& !type.equals("")){
		if(type.equals(TYPE_PIC)){ //只有图片
			tv_chunyuyin_time.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.GONE);
			imageLoader.displayImageEx(myDiary.imageurl, iv_one_photo, options, animateFirstListener, ActiveAccount.getInstance(MainApplication.getInstance()).getUID(), 1);
		}else if(type.equals(TYPE_VIDEO)){ //视频
			tv_chunyuyin_time.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.VISIBLE);
			if(myDiary.imageurl != null){
				imageLoader.displayImageEx(myDiary.imageurl, iv_one_photo, options, animateFirstListener, ActiveAccount.getInstance(MainApplication.getInstance()).getUID(), 1);
			}else{
				iv_one_photo.setImageResource(R.drawable.moren);
			}
			rl_media_info.setBackgroundResource(R.drawable.suolvetu_shipin);
			tv_media_middle_info.setVisibility(View.GONE);
			tv_media_right_info.setVisibility(View.VISIBLE);
			tv_media_right_info.setText(DateUtils.getFormatTime0000(myDiary.playtime));
		}else if(type.equals(TYPE_AUDIO)){ //录音
			tv_chunyuyin_time.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.VISIBLE);
			iv_one_photo.setImageResource(R.drawable.suolvetu_luyin);
			tv_media_middle_info.setVisibility(View.VISIBLE);
			rl_media_info.setBackgroundColor(getResources().getColor(R.color.transparent));
			tv_media_right_info.setVisibility(View.GONE);
			tv_media_middle_info.setText(DateUtils.getFormatTime0000(myDiary.playtime));
		}
		}else{
			tv_chunyuyin_time.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.GONE);
			iv_one_photo.setImageResource(R.drawable.moren);
		
		}
		
		
	}
	
	private void setViewSize(int size){
		if(size <= 0 ) return;
		
		RelativeLayout.LayoutParams params;
		
		//if(rl_media_info.getVisibility() == View.VISIBLE){
			params =  (RelativeLayout.LayoutParams)rl_media_info.getLayoutParams();
			params.height= size/4;
			rl_media_info.setLayoutParams(params);
			tv_media_middle_info.setTextSize(DensityUtil.px2dip(getContext(), size/8));
			tv_media_right_info.setTextSize(DensityUtil.px2dip(getContext(), size/8));
		//}
		
		//if(tv_chunyuyin_time.getVisibility() == View.VISIBLE){
			tv_chunyuyin_time.setTextSize(DensityUtil.px2dip(getContext(), size/6));
			params = null;
			params =  (RelativeLayout.LayoutParams)tv_chunyuyin_time.getLayoutParams();
			params.leftMargin = size/2;
			tv_chunyuyin_time.setLayoutParams(params);
		//}
	}
	
	public void setSize(int s){
		size = s;
		setViewSize(size);
	}
	
   
}
