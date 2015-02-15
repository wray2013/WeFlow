package com.cmmobi.looklook.activity;

import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;


/**
 * 第一次登录的引导界面
 * 
 * @author youtian
 * 
 */

public class GuideActivity extends ZActivity {
	private static final String TAG = "GuideActivity";
	private ImageView iv_guide;
	public static String SP_NAME="show_guide";
	public static String SP_KEY="page";
	private int index = -1;
	private Context context;
	
	private int[] resIDs={
			R.drawable.guide_1,
			R.drawable.guide_2,
			R.drawable.guide_3,
			R.drawable.guide_4,
			R.drawable.guide_5
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		context = this;
		
		iv_guide = (ImageView)findViewById(R.id.iv_guide);
		SharedPreferences sp=getSharedPreferences(SP_NAME, MODE_PRIVATE);
		if(sp.getInt(SP_KEY, 0) == 0){
			sp.edit().putInt(SP_KEY, 1).commit(); //初始值0，代表从未显示过引导页；1表示日记页有显示过；2表示详情页也有显示过,不用再显示
			index = 0;
			iv_guide.setImageBitmap(readBitMap(context, resIDs[index]));
		}else if(sp.getInt(SP_KEY, -1) == 1){
			sp.edit().putInt(SP_KEY, 2).commit();
			index = 3;
			iv_guide.setImageBitmap(readBitMap(context, resIDs[index]));
		}else {
			
		}
		
		iv_guide.setOnClickListener(this);
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
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}



	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_guide:
			if(index == 2 || index == 4){
				this.finish();
			}else {
				index ++;
				iv_guide.setImageBitmap(readBitMap(context, resIDs[index]));
			}
			break;

		default:
			break;
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

	
}
