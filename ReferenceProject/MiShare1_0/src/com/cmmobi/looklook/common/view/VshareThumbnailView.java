package com.cmmobi.looklook.common.view;

import android.R.integer;
import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.shareInfo;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tencent.mm.sdk.platformtools.BackwardSupportUtil.SmoothScrollFactory.IScroll;

/**
 * 用于显示微享内容的缩略图
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-03-12
 */
public class VshareThumbnailView extends RelativeLayout {
	
	private int size = 0;
	
	public VshareThumbnailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VshareThumbnailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VshareThumbnailView(Context context) {
		super(context);
		init();
	}

	private LayoutInflater inflater;
	
	//一个日记 
	private VshareThumbnailViewOneItem ctvoi_one_photo;
	
	//四分格布局
	private LinearLayout ll_four_photo;
	private VshareThumbnailViewOneItem ctvoi_four_photo_first;
	private VshareThumbnailViewOneItem ctvoi_four_photo_second;
	private VshareThumbnailViewOneItem ctvoi_four_photo_third;
	private VshareThumbnailViewOneItem ctvoi_four_photo_fourth;
	
	
	//下面的信息条布局
	private TextView tv_media_right_info;
	
	//此view是否选中
	private ImageView iv_selected;
	
	//日记当前的评论数
	private RelativeLayout rl_jiaobiao;
	private TextView tv_countThumb;
	private ImageView iv_jiaobiaoThumb;
	
	//日记在保险箱中
	private ImageView iv_safebox;
	
	private void init() {
		inflater = LayoutInflater.from(getContext());
		View v;
		v = inflater.inflate(R.layout.include_vshare_thumbnail_view, null);
		ctvoi_one_photo = (VshareThumbnailViewOneItem) v.findViewById(R.id.ctvoi_one_photo);
		
		ll_four_photo = (LinearLayout) v.findViewById(R.id.ll_four_photo);
		ctvoi_four_photo_first = (VshareThumbnailViewOneItem) v.findViewById(R.id.ctvoi_four_photo_first);
		ctvoi_four_photo_second = (VshareThumbnailViewOneItem) v.findViewById(R.id.ctvoi_four_photo_second);
		ctvoi_four_photo_third = (VshareThumbnailViewOneItem) v.findViewById(R.id.ctvoi_four_photo_third);
		ctvoi_four_photo_fourth = (VshareThumbnailViewOneItem) v.findViewById(R.id.ctvoi_four_photo_fourth);

		tv_media_right_info = (TextView) v.findViewById(R.id.tv_right_info);
	
		iv_selected = (ImageView) v.findViewById(R.id.iv_selected);	
		
		rl_jiaobiao = (RelativeLayout) v.findViewById(R.id.rl_jiaobiao);
		tv_countThumb = (TextView) v.findViewById(R.id.tv_count_thumb);
		iv_jiaobiaoThumb = (ImageView) v.findViewById(R.id.iv_jiaobiao_thumb);
		
		iv_safebox = (ImageView) v.findViewById(R.id.iv_safebox);
		addView(v);
			
	}
	
	
	/*
	 * 设置此view是否选中
	 */
	public void setViewSelected(boolean isSelected){
		if(isSelected){
			iv_selected.setVisibility(View.VISIBLE);
		}else{
			iv_selected.setVisibility(View.GONE);
		}
	}
	
	/*
	 * 获取此view是否选中
	 */
	public Boolean getViewSelected(){
		if(iv_selected.getVisibility() == View.VISIBLE){
			return true;
		}
		return false;
	}
	
	/*
	 * 设置此view是否显示评论数
	 */
	public void setViewIsShowComment(boolean isShow, String c){
		if(isShow && c!=null && !c.equals("") && !c.equals("0")){
			rl_jiaobiao.setVisibility(View.VISIBLE);
			if(c.length()>1){
				iv_jiaobiaoThumb.setImageResource(R.drawable.jiaobiao_2);
			}else{
				iv_jiaobiaoThumb.setImageResource(R.drawable.jiaobiao_1);
			}
			tv_countThumb.setText(c);
		}else{
			rl_jiaobiao.setVisibility(View.GONE);
		}
	}
	
	/*
	 * 获取此view是否显示评论数
	 */
	public boolean getViewIsShowComment(){
		if(rl_jiaobiao.getVisibility() == View.VISIBLE){
			return true;
		}
		return false;
	}
		
	private void setViewSize(int size){
		if(size <= 0) return;

		RelativeLayout.LayoutParams params;
		params =  (RelativeLayout.LayoutParams)tv_media_right_info.getLayoutParams();
		params.height= size/4;
		tv_media_right_info.setLayoutParams(params);
		tv_media_right_info.setTextSize(DensityUtil.px2dip(getContext(), size/8));

		LinearLayout.LayoutParams lparams;
		
		params =  (RelativeLayout.LayoutParams)ctvoi_one_photo.getLayoutParams();
		params.width = size;
		params.height = size;
		ctvoi_one_photo.setLayoutParams(params);
		ctvoi_one_photo.setSize(size);
		
		ctvoi_four_photo_first.setSize(size/2);
		ctvoi_four_photo_second.setSize(size/2);
		ctvoi_four_photo_third.setSize(size/2);
		ctvoi_four_photo_fourth.setSize(size/2);
		
	}
	
	@Override
	public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
		// TODO Auto-generated method stub
		super.setLayoutParams(params);
		if(params.width > 0){
			size = params.width;
			setViewSize(size);
		}else if(params.height > 0){
			size = params.height;
			setViewSize(size);
		}
	}
		
	/*
	 * 设置缩略图显示的日记
	 */
	public 	void setContentDiaries(String isCapsule, String isThrowaway, String isRead, final VshareDiary...diaries){
		if(diaries==null || diaries.length <1){
			iv_safebox.setVisibility(View.VISIBLE);
			iv_safebox.setImageResource(R.drawable.moren);
			ctvoi_one_photo.setVisibility(View.GONE);
			ll_four_photo.setVisibility(View.GONE);
			tv_media_right_info.setVisibility(View.GONE);
			return;
		}

		if("1".equals(isCapsule)&&"0".equals(isRead)){
			iv_safebox.setVisibility(View.VISIBLE);
			iv_safebox.setImageResource(R.drawable.sjjn);
			ctvoi_one_photo.setVisibility(View.GONE);
			ll_four_photo.setVisibility(View.GONE);
			tv_media_right_info.setVisibility(View.GONE);
		}else if("1".equals(isThrowaway)){
			iv_safebox.setVisibility(View.VISIBLE);
			iv_safebox.setImageResource(R.drawable.yuehoujifen);
			ctvoi_one_photo.setVisibility(View.GONE);
			ll_four_photo.setVisibility(View.GONE);
			tv_media_right_info.setVisibility(View.GONE);
		}else{
			if(diaries.length == 1){ //只有一个日记
				ctvoi_one_photo.setVisibility(View.VISIBLE);
				ll_four_photo.setVisibility(View.GONE);
				tv_media_right_info.setVisibility(View.GONE);
				ctvoi_one_photo.setContentDiary(diaries[0]);
			}else{
				ll_four_photo.setVisibility(View.VISIBLE);
				ctvoi_four_photo_first.setContentDiary(diaries[0]);
				ctvoi_four_photo_second.setContentDiary(diaries[1]);
				ctvoi_four_photo_first.setVisibility(View.VISIBLE);
				ctvoi_four_photo_second.setVisibility(View.VISIBLE);
				if(diaries.length == 2){
					ctvoi_four_photo_third.setVisibility(View.INVISIBLE);
					ctvoi_four_photo_fourth.setVisibility(View.INVISIBLE);
				}else if(diaries.length == 3){ //三个日记 
					ctvoi_four_photo_third.setContentDiary(diaries[2]);
					ctvoi_four_photo_third.setVisibility(View.VISIBLE);
					ctvoi_four_photo_fourth.setVisibility(View.INVISIBLE);
				}else if(diaries.length > 3){ //四个日记
					ctvoi_four_photo_third.setContentDiary(diaries[2]);
					ctvoi_four_photo_fourth.setContentDiary(diaries[3]);
					ctvoi_four_photo_third.setVisibility(View.VISIBLE);
					ctvoi_four_photo_fourth.setVisibility(View.VISIBLE);
				}
				tv_media_right_info.setVisibility(View.VISIBLE);
				String info = "共" + diaries.length + "项"; 
				tv_media_right_info.setText(info);
			}	
		}
	
	}
}
