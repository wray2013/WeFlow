package com.etoc.weflow.activity.login;

import java.io.InputStream;
import java.util.ArrayList;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.TitleRootActivity;
import com.etoc.weflow.activity.WelcomePageActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;



/**
 * 第一次登录的引导界面
 * 
 */

public class GuideActivity extends TitleRootActivity {
	private static final String TAG = "GuideActivity";
	private ViewPager vp_content;
	private int index = -1;
	private Context context;
	private ArrayList<View> viewList = new ArrayList<View>();
	private LayoutInflater inflater;
	
	public static String SP_NAME="show_guide";
	public static String SP_KEY="page";
	
	private int[] resIDs={
//			R.drawable.guide01,
//			R.drawable.guide02,
//			R.drawable.guide03,
//			R.drawable.guide04,
//			R.drawable.guide05,
//			R.drawable.guide06
			R.drawable.guide_1,
			R.drawable.guide_2,
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideTitlebar();
		
		context = this;
		
		vp_content = (ViewPager) findViewById(R.id.vp_content);
		inflater = getLayoutInflater();
		loadItem();
		WeFlowApplication.getAppInstance().addActivity(this);
	}

	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop(){
		super.onStop();
//		MainApplication.getAppInstance().cleanAllActivity();
	}



	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	private void loadItem() {
		viewList.clear();
		for(int i=0;i<resIDs.length;i++){
			LinearLayout view = (LinearLayout)inflater.inflate(R.layout.app_recommend_app_bigimage_view, null);
			ImageView v = (ImageView) view.findViewById(R.id.iv_image);
			v.setImageBitmap(readBitMap(context, resIDs[i]));
			if(i==resIDs.length-1){
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SharedPreferences sp=getSharedPreferences(GuideActivity.SP_NAME, MODE_PRIVATE);
						sp.edit().putInt(SP_KEY, 1).commit();
						GuideActivity.this.finish();
						Intent intent = new Intent(GuideActivity.this, WelcomePageActivity.class);
						startActivity(intent);
					}
				});
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
			}
			if(4==arg0){
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


	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_guide;
	} 

	
}
