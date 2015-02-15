package com.cmmobi.looklook.common.view;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 用于显示微享群组成员
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-10-25
 * 
 */
public class VshareMemberThumbnailView extends RelativeLayout {

	public VshareMemberThumbnailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VshareMemberThumbnailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VshareMemberThumbnailView(Context context) {
		super(context);
		init();
	}

	private LayoutInflater inflater;
	
	private GridView gv_fourMember;
	private GridView gv_nineMember;
	
	private String[] imageUrls;
	private UserObj[] userObjs;
		
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private void init() {
		inflater = LayoutInflater.from(getContext());
		View v = inflater.inflate(R.layout.include_vshare_member_thumbnail_view, null);
		gv_fourMember = (GridView) v.findViewById(R.id.gv_four_member);
		gv_nineMember = (GridView) v.findViewById(R.id.gv_nine_member);
		addView(v);
		gv_fourMember.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv_nineMember.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		gv_nineMember.setClickable(false);		 
		gv_nineMember.setPressed(false);
		gv_nineMember.setEnabled(false);

		gv_fourMember.setClickable(false);		 
		gv_fourMember.setPressed(false);
		gv_fourMember.setEnabled(false);
		
		imageLoader = ImageLoader.getInstance();
		//if(!imageLoader.isInited())
		//imageLoader.init(ImageLoaderConfiguration.createDefault(VshareMemberThumbnailView.this.getContext()));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}
	
	/*
	 * 设置ImageUrls
	 */
	public void setImageUrls(String...urls){
		if(urls == null || urls.length == 0){
			return;
		}
		imageUrls = urls;
		if(imageUrls.length<5){
			gv_fourMember.setVisibility(View.VISIBLE);
			gv_nineMember.setVisibility(View.GONE);
			gv_fourMember.setAdapter(new ImageAdapter(this.getContext(), urls));
		}else{
			gv_fourMember.setVisibility(View.GONE);
			gv_nineMember.setVisibility(View.VISIBLE);
			gv_nineMember.setAdapter(new ImageAdapter(this.getContext(), urls));
		}
	}
	
	public void setUserObjs(UserObj...objs) {
		if(objs == null || objs.length == 0){
			return;
		}
		userObjs = objs;
	}
	
	public UserObj[] getUserObjs() {
		return userObjs;
	}
	
	private class ImageAdapter extends BaseAdapter
	{
		// 定义Context
		private Context	mContext;
		// 定义String数组 即图片源
		private String[] mImageUrl;
		private LayoutInflater inflater;
		public ImageAdapter(Context c)
		{
			mContext = c;
			this.inflater = LayoutInflater.from(mContext);
		}
		
		public ImageAdapter(Context c, String[] imageurl)
		{
			mContext = c;
			mImageUrl = imageurl;
			this.inflater = LayoutInflater.from(mContext);
		}

		// 获取图片的个数
		public int getCount()
		{
			return mImageUrl.length; 
		}

		// 获取图片在库中的位置
		public Object getItem(int position)
		{
			return position;
		}

		// 获取图片ID
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null)
			{
				convertView = inflater
						.inflate(R.layout.include_vshare_member_thumbnail_view_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_portrait = (ImageView) convertView
						.findViewById(R.id.iv_portrait);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.iv_portrait.getLayoutParams();
				if(getCount()>4){
					params.height = DensityUtil.dip2px(mContext, 14);
					params.width = DensityUtil.dip2px(mContext, 14);
				}else{
					params.height = DensityUtil.dip2px(mContext, 21f);
					params.width = DensityUtil.dip2px(mContext, 21f);
				}
				viewHolder.iv_portrait.setLayoutParams(params);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(mImageUrl[position] !=null && !mImageUrl[position].isEmpty()){
				imageLoader.displayImageEx(mImageUrl[position], viewHolder.iv_portrait, options, animateFirstListener, ActiveAccount.getInstance(mContext).getUID(), 1);
			}else{
				viewHolder.iv_portrait.setImageResource(R.drawable.moren_touxiang);
			}
			return convertView;
		}

		class ViewHolder {
			ImageView iv_portrait;
		}
	}


}
