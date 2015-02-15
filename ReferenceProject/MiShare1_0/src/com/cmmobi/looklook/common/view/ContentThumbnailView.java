package com.cmmobi.looklook.common.view;

import android.R.integer;
import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.shareInfo;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tencent.mm.sdk.platformtools.BackwardSupportUtil.SmoothScrollFactory.IScroll;

/**
 * 用于显示内容缩略图
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-10-25
 */
public class ContentThumbnailView extends RelativeLayout {
	
	private int size = 0;
	private DiaryManager diaryManager;
	
	public ContentThumbnailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ContentThumbnailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ContentThumbnailView(Context context) {
		super(context);
		init();
	}

	private LayoutInflater inflater;
		
	//此view是否选中
	private ImageView iv_selected;
	//些日记是否被分享
	private ImageView iv_share_first;
	private ImageView iv_share_second;
	private ImageView iv_share_third;
		
	//日记在保险箱中
	private ImageView iv_safebox;
	
	//整个布局内容
		private RelativeLayout rl_content;
		
	//一张图片时的布局
	private ImageView iv_one_photo;		
	
	//只有文字时的布局
	
	private TextView tv_content_text;
	
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
		v = inflater.inflate(R.layout.include_content_thumbnail_view, null);
		iv_selected = (ImageView) v.findViewById(R.id.iv_selected);
		
		iv_share_first = (ImageView) v.findViewById(R.id.iv_share_first);
		iv_share_second = (ImageView) v.findViewById(R.id.iv_share_second);
		iv_share_third = (ImageView) v.findViewById(R.id.iv_share_third);		
	
		iv_safebox = (ImageView) v.findViewById(R.id.iv_safebox);
		
		rl_content = (RelativeLayout) v.findViewById(R.id.rl_content);
		iv_one_photo = (ImageView) v.findViewById(R.id.iv_one_photo);	
		tv_content_text = (TextView) v.findViewById(R.id.tv_content_text);
		tv_chunyuyin_time = (TextView) v.findViewById(R.id.tv_chunyuyin_time);
		
		rl_media_info = (RelativeLayout) v.findViewById(R.id.rl_media_info);
		tv_media_right_info = (TextView) v.findViewById(R.id.tv_media_right_info);
		tv_media_middle_info = (TextView) v.findViewById(R.id.tv_media_middle_info);
		addView(v);
		if(!isInEditMode())
			diaryManager = DiaryManager.getInstance();
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren)
		.showImageForEmptyUri(R.drawable.moren)
		.showImageOnFail(R.drawable.moren)
		.imageScaleType(ImageScaleType.EXACTLY)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
			
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
	 * 设置此view是被分享
	 * //分享类型 (1-12为第三方分享)1新浪 2人人 5 qzone空间 6腾讯 9微信朋友圈 10短信 11邮箱 12微信好友 100站内公开 101朋友圈 103微享
	 */
	public void setViewShare(shareInfo[] info){
		int i = 0;
		if(info!=null && info.length > 0){
			if(i<info.length){
				iv_share_first.setVisibility(View.VISIBLE);
				if("103".equals(info[i].share_status)){
					iv_share_first.setImageResource(R.drawable.biaoqian_weixiang);
				}else if("101".equals(info[i].share_status)){
					iv_share_first.setImageResource(R.drawable.biaoqian_pengyou);
				}else if(!"100".equals(info[i].share_status)){
					iv_share_first.setImageResource(R.drawable.biaoqian_fenxiang);
				}
				
				i++;
				if(i<info.length){
					iv_share_second.setVisibility(View.VISIBLE);
					if("103".equals(info[i].share_status)){
						iv_share_second.setImageResource(R.drawable.biaoqian_weixiang);
					}else if("101".equals(info[i].share_status)){
						iv_share_second.setImageResource(R.drawable.biaoqian_pengyou);
					}else if(!"100".equals(info[i].share_status)){
						iv_share_second.setImageResource(R.drawable.biaoqian_fenxiang);
					}
					
					i++;
					if(i<info.length){
						iv_share_third.setVisibility(View.VISIBLE);
						if("103".equals(info[i].share_status)){
							iv_share_third.setImageResource(R.drawable.biaoqian_weixiang);
						}else if("101".equals(info[i].share_status)){
							iv_share_third.setImageResource(R.drawable.biaoqian_pengyou);
						}else if(!"100".equals(info[i].share_status)){
							iv_share_third.setImageResource(R.drawable.biaoqian_fenxiang);
						}
					}else{
						iv_share_third.setVisibility(View.GONE);
					}
				}else {
					iv_share_second.setVisibility(View.GONE);
					iv_share_third.setVisibility(View.GONE);
				}
			}else{
				iv_share_first.setVisibility(View.GONE);
				iv_share_second.setVisibility(View.GONE);
				iv_share_third.setVisibility(View.GONE);
			}			
		}else{
			iv_share_first.setVisibility(View.GONE);
			iv_share_second.setVisibility(View.GONE);
			iv_share_third.setVisibility(View.GONE);
		}
	}
	
	
	private void setViewSize(int size){
		if(size <= 0) return;
		RelativeLayout.LayoutParams params;
		
		params =  (RelativeLayout.LayoutParams)rl_media_info.getLayoutParams();
		params.height= size/4;
		rl_media_info.setLayoutParams(params);
		tv_media_middle_info.setTextSize(DensityUtil.px2dip(getContext(), size/8));
		tv_media_right_info.setTextSize(DensityUtil.px2dip(getContext(), size/8));

		tv_content_text.setTextSize(DensityUtil.px2dip(getContext(), size/10));
		tv_chunyuyin_time.setTextSize(DensityUtil.px2dip(getContext(), size/6));
		params = null;
		params =  (RelativeLayout.LayoutParams)tv_chunyuyin_time.getLayoutParams();
		params.leftMargin = size/2;
		tv_chunyuyin_time.setLayoutParams(params);
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
	public 	void setContentDiaries(shareInfo[] info, String isInSafeBox, final MyDiary...diaries){
		setContentDiaries(isInSafeBox, diaries);
		setViewShare(info);
	}
	
	/*
	 * 设置缩略图显示的日记
	 */
	public 	void setContentDiaries(String isInSafeBox, final MyDiary...diaries){
//		setViewShare(null);
		if(diaries==null || diaries.length <1){
			iv_safebox.setVisibility(View.VISIBLE);
			iv_safebox.setImageResource(R.drawable.moren);
			rl_content.setVisibility(View.GONE);
			return;
		}
		boolean inSafeBox = false;
		if(isInSafeBox !=null && isInSafeBox.equals("1")){
			inSafeBox = true;
		}
		if(inSafeBox){
			iv_safebox.setVisibility(View.VISIBLE);
			iv_safebox.setImageResource(R.drawable.moren_bxx_100x100);
			rl_content.setVisibility(View.GONE);
			return;
		}
		rl_content.setVisibility(View.VISIBLE);
		setContentDiary(diaries[0]);
	}
	
	/*
	 * 设置缩略图内容，即MyDiary
	 * @param: myDiary 该缩略图对应的日记
	 * 1视频、2音频、3图片、4文字、5 短录音 、6（短录音+文字）
	 */
	public void setContentDiary(MyDiary myDiary){
		if(myDiary == null){
			return;
		}
		String type = myDiary.getDiaryMainType();
		if(type!=null&& !type.equals("")){
		if(type.equals("3")){ //只有图片
			tv_chunyuyin_time.setVisibility(View.GONE);
			tv_content_text.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.GONE);
			imageLoader.displayImageEx(getCoverUrl(myDiary), iv_one_photo, options, animateFirstListener, ActiveAccount.getInstance(MainApplication.getInstance()).getUID(), 1);
		}else if(type.equals("1")){ //视频
			tv_chunyuyin_time.setVisibility(View.GONE);
			tv_content_text.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.VISIBLE);
			if(getCoverUrl(myDiary) != null){
				imageLoader.displayImageEx(getCoverUrl(myDiary), iv_one_photo, options, animateFirstListener, ActiveAccount.getInstance(MainApplication.getInstance()).getUID(), 1);	
			}else{
				iv_one_photo.setImageResource(R.drawable.moren);
			}
			rl_media_info.setBackgroundResource(R.drawable.suolvetu_shipin);
			tv_media_middle_info.setVisibility(View.GONE);
			tv_media_right_info.setVisibility(View.VISIBLE);
			tv_media_right_info.setText(DateUtils.getFormatTime0000(myDiary.getMainPlaytime()));
		}else if(type.equals("5")){ // 纯语音

			tv_chunyuyin_time.setVisibility(View.VISIBLE);
			tv_content_text.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.GONE);
			iv_one_photo.setImageResource(R.drawable.suolvetu_chunyuyin);
			tv_chunyuyin_time.setText(myDiary.getMainPlaytime() + "''");
		}else if(type.equals("2")){ //录音
			tv_chunyuyin_time.setVisibility(View.GONE);
			tv_content_text.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.VISIBLE);
			iv_one_photo.setImageResource(R.drawable.suolvetu_luyin);
			tv_media_middle_info.setVisibility(View.VISIBLE);
			rl_media_info.setBackgroundColor(getResources().getColor(R.color.transparent));
			tv_media_right_info.setVisibility(View.GONE);
			tv_media_middle_info.setText(DateUtils.getFormatTime0000(myDiary.getMainPlaytime()));
		}else if(type.equals("4")){ //纯文字
			tv_chunyuyin_time.setVisibility(View.GONE);
			tv_content_text.setVisibility(View.VISIBLE);
			rl_media_info.setVisibility(View.GONE);
			iv_one_photo.setImageResource(R.drawable.suolvetu_wenzi);
			tv_content_text.setText(myDiary.getMainTextContent());
		}else if(type.equals("6")){ //文字+语音
			tv_chunyuyin_time.setVisibility(View.GONE);
			tv_content_text.setVisibility(View.VISIBLE);
			rl_media_info.setVisibility(View.GONE);
			iv_one_photo.setImageResource(R.drawable.suolvetu_wenziyuyin);
			tv_content_text.setText(myDiary.getMainTextContent());
		}
		}else{
			tv_chunyuyin_time.setVisibility(View.GONE);
			tv_content_text.setVisibility(View.GONE);
			rl_media_info.setVisibility(View.GONE);
			iv_one_photo.setImageResource(R.drawable.moren);
		
		}
	}
	
    private String getCoverUrl(MyDiary diary){
		String type = diary.getDiaryMainType();
		String imageUrl = null;
		if(type.equals("3")){ //只有图片
			imageUrl = diary.getMainUrl_s();
		}else if(type.equals("1")){ //视频
			imageUrl = diary.getVideoCoverUrl_s();
		}
//		System.out.println("====imageUrl ====" + imageUrl);
		return imageUrl;		
    }
}
