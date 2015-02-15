package com.cmmobi.looklook.activity;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;


/**
 * 第一次登录的引导界面
 * 
 * @author youtian
 * 
 */

public class GuideActivity extends ZActivity {
	private static final String TAG = "GuideActivity";
	private ViewPager vp_content;
	private int index = -1;
	private Context context;
	private ArrayList<View> viewList = new ArrayList<View>();
	private LayoutInflater inflater;
	
	public static String SP_NAME="show_guide";
	public static String SP_KEY="page";
	public static String FROM_WELCOME= "from_welcome";
	public static String FROM_SETTING = "from_setting";
	
	
	private int[] resIDs={
			R.drawable.guide01,
			R.drawable.guide02,
			R.drawable.guide03,
//			R.drawable.guide04,
//			R.drawable.guide05,
//			R.drawable.guide06
	};

	private boolean isFromWelcome = false;
	private boolean isFromSetting = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		context = this;
		
		
		vp_content = (ViewPager) findViewById(R.id.vp_content);
		inflater = getLayoutInflater();
		isFromSetting = getIntent().getBooleanExtra(FROM_SETTING, false);
		if (isFromSetting) {
			resIDs= new int[] {
					R.drawable.help03,
					R.drawable.help02,
			};
		}
		loadItem();
		
		// 判断是否logo页传递过来的
		isFromWelcome = getIntent().getBooleanExtra(FROM_WELCOME, false);
		
		/*String userid = ActiveAccount.getInstance(this).getLookLookID();
		if(isFromWelcome && !ActiveAccount.verifyUseridSuccess(userid)){
			Requester3.submitUA(getHandler());
		}*/
		
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(isFromWelcome){
				GuideActivity.this.finish();
				Intent intent = new Intent(GuideActivity.this, LookLookActivity.class);
				startActivity(intent);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}



	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case Requester3.RESPONSE_TYPE_UA:
			GsonResponse3.uaResponse uaResponse = (GsonResponse3.uaResponse) msg.obj;
			
			// 之前登陆过 需完成自动登陆
			String userid = ActiveAccount.getInstance(this).getLookLookID();
			if(!ActiveAccount.verifyUseridSuccess(userid)){
				if(uaResponse!=null){
					if(uaResponse.equipmentid!=null){
						CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
					}
					// 完成用户数据更新
					String micshare = uaResponse.mishare_no;
					userid = uaResponse.userid;
					ActiveAccount.getInstance(this).updateMicShareNo(userid, micshare);
				}
			}
			break;
		}
		return false;
	}

	private void loadItem() {
		viewList.clear();
		for(int i=0;i<resIDs.length;i++){
			LinearLayout view = (LinearLayout)inflater.inflate(R.layout.app_recommend_app_bigimage_view, null);
			ImageView v = (ImageView) view.findViewById(R.id.iv_image);
			v.setImageBitmap(readBitMap(context, resIDs[i]));
			if(i==resIDs.length-1){
//				v.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						GuideActivity.this.finish();
//						Intent intent = new Intent(GuideActivity.this, LookLookActivity.class);
//						startActivity(intent);
//					}
//				});
			}
			
			viewList.add(view);
		}
		vp_content.setAdapter(new Pager(viewList));
		vp_content.setCurrentItem(0);
		vp_content.setOnPageChangeListener(new MyOnPageChangeListener());
	 }
	
	private View mPreSelectedBt;
	private int lastValue=-1;
	public class MyOnPageChangeListener implements
			ViewPager.OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if(lastValue!=-1&&lastValue==arg0&&0==arg1&&0==arg2){
				//到最后一页了
				GuideActivity.this.finish();
				if (!isFromSetting) {
					Intent intent = new Intent(GuideActivity.this, LookLookActivity.class);
					startActivity(intent);
				}
			}
			if(arg0==(resIDs.length-1)){
				lastValue=arg0;
			}else{
				lastValue=-1;
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	
	class Pager extends PagerAdapter {

		ArrayList<View> views;

		public Pager(ArrayList<View> viewList) {
			this.views = (ArrayList<View>) viewList;
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return view == arg1;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position), 0);
			return viewList.get(position);
		}

	}


	/** 
	 * 以最省内存的方式读取本地资源的图片 
	 * @param context 
	 * @param resId 
	 * @return 
	 */  
	 public static Bitmap readBitMap(Context context, int resId){  
	     BitmapFactory.Options opt = new BitmapFactory.Options();  
	     opt.inPreferredConfig = Bitmap.Config.RGB_565;   
	     opt.inPurgeable = true;  
	     opt.inInputShareable = true;  
	        //获取资源图片  
	     InputStream is = context.getResources().openRawResource(resId);  
	         return BitmapFactory.decodeStream(is,null,opt);  
	 }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	} 

	
}
