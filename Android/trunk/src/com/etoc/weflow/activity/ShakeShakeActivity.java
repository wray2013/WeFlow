package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.listener.ShakeListener;
import com.etoc.weflow.listener.ShakeListener.OnShakeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * 安卓晃动手机监听--“摇一摇”
 * 
 * @author 单红宇
 * 
 */

public class ShakeShakeActivity extends TitleRootActivity {

	ShakeListener mShakeListener = null;
	Vibrator mVibrator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// drawerSet ();//设置 drawer监听 切换 按钮的方向
		
		//获得振动器服务
		mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
		//实例化加速度传感器检测类
		mShakeListener = new ShakeListener(ShakeShakeActivity.this);
		
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			
			public void onShake() {
				mShakeListener.stop();
				startVibrato(); // 开始 震动
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast mtoast;
						mtoast = Toast.makeText(ShakeShakeActivity.this,
								"呵呵，成功了！。\n再试一次吧！", 1000);
						mtoast.show();
						mVibrator.cancel();
						mShakeListener.start();
					}
				}, 2000);
			}
		});
	}	
	 // 定义震动
	public void startVibrato() {
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
		// 第一个｛｝里面是节奏数组，
		// 第二个参数是重复次数，-1为不重复，非-1则从pattern的指定下标开始重复
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_shakeshake;
	}
}