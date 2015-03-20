package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.listener.ShakeListener;
import com.etoc.weflow.listener.ShakeListener.OnShakeListener;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
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
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// drawerSet ();//设置 drawer监听 切换 按钮的方向
		setTitleText("摇一摇");
		hideRightButton();
		
		//获得振动器服务
		mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
		//实例化加速度传感器检测类
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		
		mShakeListener = new ShakeListener(ShakeShakeActivity.this);
		
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			
			public void onShake() {
				startAnim();  //开始 摇一摇手掌动画
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
	
	private void loadSound(final Context ctx) {
		final SoundPool sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		new Thread() {
			public void run() {
				try {
					sp.load(ctx, R.raw.glass, 1);
					sp.play(1, 1, 1, 0, 0, 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void startAnim () {   //定义摇一摇动画动画
		loadSound(this);
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);
		
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);	
	}
	 // 定义震动
	public void startVibrato() {
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
		// 第一个｛｝里面是节奏数组，
		// 第二个参数是重复次数，-1为不重复，非-1则从pattern的指定下标开始重复
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
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