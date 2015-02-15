package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.CommonInfo;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-8-16
 */
public class WelcomeActivity extends ZActivity {
	private static final String TAG = "WelcomeActivity";
	private ViewPager mPager;
	private LinearLayout mNumLayout;
	public static final String KEY_FRIST="frist";
	private ArrayList<View> listViews = new ArrayList<View>();
	
	private int[] resIDs={
			R.drawable.welcome_1,
			R.drawable.welcome_2,
			R.drawable.welcome_3,
			R.drawable.welcome_4,
			R.drawable.welcome_5
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		SharedPreferences sp=getSharedPreferences("MainActivity", MODE_PRIVATE);
		int frist=sp.getInt(WelcomeActivity.KEY_FRIST, 0);
		if(frist!=0){
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
			startActivity(new Intent(this,LoginWelcomeActivity.class));
		}else{
			Requester2.submitUA(getHandler());
		}
		mPager = (ViewPager) findViewById(R.id.vPager);
		mNumLayout = (LinearLayout) findViewById(R.id.ll_pager_num);
		loadItem();
		sp.edit().putInt(KEY_FRIST, 1).commit();
		

	}

	private void loadItem() {
		listViews.clear();
		for(int i=0;i<resIDs.length;i++){
			ImageView v=new ImageView(this);
			v.setImageResource(resIDs[i]);
			v.setScaleType(ScaleType.FIT_XY);
			v.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			listViews.add(v);
		}
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		initImageView(listViews);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	private View mPreSelectedBt;
	private int lastValue=-1;
	public class MyOnPageChangeListener implements
			ViewPager.OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			if (mPreSelectedBt != null) {
				mPreSelectedBt.setBackgroundResource(R.drawable.home_icon1);
			} else {
				if (arg0 == 1) {
					View preButton = mNumLayout.getChildAt(0);
					preButton.setBackgroundResource(R.drawable.home_icon1);
				}
			}
			View currentBt = mNumLayout.getChildAt(arg0);
			currentBt.setBackgroundResource(R.drawable.home_icon2);
			mPreSelectedBt = currentBt;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if(lastValue!=-1&&lastValue==arg0&&0==arg1&&0==arg2){
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				startActivity(new Intent(WelcomeActivity.this,LoginMainActivity.class));
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

	private void initImageView(ArrayList<View> list) {
		if (list.size() < 2)
			return;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.home_icon1);
		if (mNumLayout != null)
			mNumLayout.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			View view = new View(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					bitmap.getWidth(), bitmap.getHeight());
			params.setMargins(5, 10, 0, 10);
			view.setLayoutParams(params);
			if (i == 0) {
				view.setBackgroundResource(R.drawable.home_icon2);
			} else {
				view.setBackgroundResource(R.drawable.home_icon1);
			}
			mNumLayout.addView(view);
		}

	}

	class MyPagerAdapter extends PagerAdapter {
	    public List<View> mListViews;
	    public MyPagerAdapter(List<View> mListViews) {
	        this.mListViews = mListViews;
	    }

	    @Override
	    public void destroyItem(View arg0, int arg1, Object arg2) {
	        try {
				((ViewPager) arg0).removeView(mListViews.get(arg1));
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }

	    @Override
	    public void finishUpdate(View arg0) {

	    }

	    @Override
	    public int getCount() {
	        return mListViews.size();
	    }

	    @Override
	    public Object instantiateItem(View arg0, int arg1) {
	        ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
	        return mListViews.get(arg1);
	    }

	    @Override
	    public boolean isViewFromObject(View arg0, Object arg1) {
	        return arg0 == (arg1);
	    }

	    @Override
	    public void restoreState(Parcelable arg0, ClassLoader arg1) {
	    }

	    @Override
	    public Parcelable saveState() {
	        return null;
	    }

	    @Override
	    public void startUpdate(View arg0) {
	    }
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case  Requester2.RESPONSE_TYPE_UA:
			GsonResponse2.uaResponse uaResponse = (GsonResponse2.uaResponse) msg.obj;
			if(uaResponse!=null){
				if(uaResponse.equipmentid!=null){
					CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
				}
				
			}
			break;

		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
