package com.cmmobi.looklook.common.view;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author Ray
 *
 */
public class XGridDialog extends Dialog {

	private Window window = null;
	private FrameLayout flGrid;
	
	private GridView gv_fourMember;
	private GridView gv_nineMember;
	
	private UserObj[] userObjs;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private static DisplayMetrics dm = new DisplayMetrics();
	
    public XGridDialog(Context context)
    {
		super(context);
		
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.x_grid_dialog);
		
		flGrid = (FrameLayout) findViewById(R.id.fl_pop_grid);
		LayoutParams lp = (LayoutParams) flGrid.getLayoutParams();
		lp.width = dm.widthPixels * 27 / 64;
		flGrid.setLayoutParams(lp);
		
		gv_fourMember = (GridView) findViewById(R.id.gv_four_member);
		gv_nineMember = (GridView) findViewById(R.id.gv_nine_member);
		
		gv_fourMember.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv_nineMember.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		gv_fourMember.setClickable(false);		 
		gv_fourMember.setPressed(false);
		gv_fourMember.setEnabled(false);
		
		gv_nineMember.setClickable(false);		 
		gv_nineMember.setPressed(false);
		gv_nineMember.setEnabled(false);
		
		int padding = dm.widthPixels / 128;
		gv_fourMember.setHorizontalSpacing(padding / 2);
		gv_fourMember.setVerticalSpacing(padding);
		
		gv_nineMember.setHorizontalSpacing(padding / 2);
		gv_nineMember.setVerticalSpacing(padding);
		
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
    
	public void showDialog() {
		if (gv_fourMember == null && gv_nineMember == null) {
			return;
		}
//		windowDeploy(x, y);
		windowDeploy(dm.widthPixels * 27 / 64, 0);
		// 设置触摸对话框意外的地方取消对话框
		setCanceledOnTouchOutside(true);
		show();
	}
    
	/*
	 * 设置ImageUrls
	 */
/*	public void setImageUrls(String...urls) {
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
	}*/
	
	public void setUserObjs(UserObj...objs) {
		if(objs == null || objs.length == 0){
			return;
		}
		userObjs = objs;
		if(userObjs.length<5){
			gv_fourMember.setVisibility(View.VISIBLE);
			gv_nineMember.setVisibility(View.GONE);
			gv_fourMember.setAdapter(new ImageAdapter(this.getContext(), objs));
		}else{
			gv_fourMember.setVisibility(View.GONE);
			gv_nineMember.setVisibility(View.VISIBLE);
			gv_nineMember.setAdapter(new ImageAdapter(this.getContext(), objs));
		}
	}
	
    //设置窗口显示
    public void windowDeploy(int x, int y){
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        window.setBackgroundDrawableResource(R.color.transparent); //设置对话框背景为透明
        WindowManager.LayoutParams wl = window.getAttributes();
        //根据x，y坐标设置窗口需要显示的位置
//        wl.width = x;
//        wl.x = x; //x小于0左移，大于0右移
//        wl.y = y; //y小于0上移，大于0下移  
//        wl.alpha = 0.6f; //设置透明度
        wl.gravity = Gravity.RIGHT | Gravity.TOP; //设置重力
        window.setAttributes(wl);
    }
    
	private class ImageAdapter extends BaseAdapter
	{
		// 定义Context
		private Context	mContext;
		// 定义String数组 即图片源
		private String[] mImageUrl;
		private String[] mUserName;
		private LayoutInflater inflater;
		public ImageAdapter(Context c)
		{
			mContext = c;
			this.inflater = LayoutInflater.from(mContext);
		}
		
		public ImageAdapter(Context c, UserObj[] objs)
		{
			mContext = c;
			if (objs != null) {
				String[] headurl = new String[objs.length];
				String[] objname = new String[objs.length];
				for (int i = 0; i < objs.length; i++) {
					headurl[i] = objs[i].headimageurl;
					objname[i] = objs[i].user_telname;
				}
				mImageUrl = headurl;
				mUserName = objname;
			}
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
						.inflate(R.layout.include_vshare_detail_gridview, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
				viewHolder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.iv_portrait.getLayoutParams();
				if(getCount()>4){
					params.width = dm.widthPixels * 7 / 64;
				}else{
					params.width = dm.widthPixels * 11 / 64;
				}
				params.height = params.width;
				
				viewHolder.iv_portrait.setLayoutParams(params);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//加载头像
			if(mImageUrl[position] !=null && !mImageUrl[position].isEmpty()){
				imageLoader.displayImageEx(mImageUrl[position], viewHolder.iv_portrait, options, animateFirstListener, ActiveAccount.getInstance(mContext).getUID(), 1);
			}else{
				viewHolder.iv_portrait.setImageResource(R.drawable.moren_touxiang);
			}
			//加载昵称
			if(mUserName[position] !=null && !mUserName[position].isEmpty()){
				viewHolder.tv_username.setText(mUserName[position]);
			}else{
				viewHolder.tv_username.setText("");
			}
			
			return convertView;
		}

		class ViewHolder {
			ImageView iv_portrait;
			TextView tv_username;
		}
	}
}