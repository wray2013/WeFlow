package com.etoc.weflow.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.OrderDialog;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.listener.ShakeListener;
import com.etoc.weflow.listener.ShakeListener.OnShakeListener;
import com.etoc.weflow.net.GsonResponseObject.shakeConfigResp;
import com.etoc.weflow.net.GsonResponseObject.shakeflowResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.RandomUtils;
import com.etoc.weflow.view.FlowerView;

/**
 * 安卓晃动手机监听--“摇一摇”
 * 
 * @author 单红宇
 * 
 */

public class ShakeShakeActivity extends TitleRootActivity implements OnLongClickListener {

	ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	
	private ImageView ivShakeBg;
	private FlowerView mFlowerView;
	
	private TextView tvShakeHint;

	private AccountInfo accountInfo;
	
	Timer myTimer = null;
	TimerTask mTask = null;
	private Handler mHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			mFlowerView.invalidate();
		};
	};
	private static final int SNOW_BLOCK = 1;
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// drawerSet ();//设置 drawer监听 切换 按钮的方向
		setTitleText("摇一摇");
		hideRightButton();
		
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
		
		tvShakeHint = (TextView) findViewById(R.id.tv_shake_label);
		
		//获得振动器服务
		mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
		//实例化加速度传感器检测类
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		
		ivShakeBg = (ImageView) findViewById(R.id.shakeBg);
		ivShakeBg.setOnLongClickListener(this);
		
		int screenWidth = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		int screenHeight = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();
		mFlowerView = (FlowerView) findViewById(R.id.flowerview);
		mFlowerView.setWH(screenWidth, screenHeight, dm.density);
		mFlowerView.loadFlower();
		mFlowerView.addRect();
		
		mShakeListener = new ShakeListener(ShakeShakeActivity.this);
		
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			
			public void onShake() {
				shakePerform();
			}
		});
		
		myTimer = new Timer();
		mTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = SNOW_BLOCK;
				mHandler.sendMessage(msg);
			}
		};
		
		Requester.getShakeConfig(true, handler);
	}
	
	private void shakePerform() {
		startAnim();  //开始 摇一摇手掌动画
		mShakeListener.stop();
		startVibrato(); // 开始 震动
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
				Requester.shakeFlow(false, handler, accountInfo.getUserid());
				/*Toast mtoast;
				mtoast = Toast.makeText(ShakeShakeActivity.this,
						"呵呵，成功了！。\n再试一次吧！", 1000);
				mtoast.show();*/
				mVibrator.cancel();
//				mShakeListener.start();
			}
		}, 2000);
	
	}
	
	private void loadSound(final Context ctx, final int looptime, int rawResID) {
		final SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		final int id = sp.load(ctx, rawResID, 1);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					sp.play(id, 1, 1, 0, looptime, 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 200);
	}

	public void startAnim () {   //定义摇一摇动画动画
		loadSound(this, 0, R.raw.glass);
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
		mFlowerView.recycle();
		if(mTask != null) {
			mTask.cancel();
		}
		if(myTimer != null) {
			myTimer.cancel();
		}
	}
	
	private String[] noAward = {
			"哎呀，离中奖就差一点",
			"搞什么灰机，又没摇中",
			"据说下雨天跟大奖更配哦",
			"很遗憾，请再接再厉",
			"又没中，什么仇什么怨？"
	};
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_SHAKE:
			if(msg.obj != null) {
				shakeflowResp resp = (shakeflowResp) msg.obj;
//				loadSound(this, 5, R.raw.diaoluo_xiao);
				if("0".equals(resp.status) || "0000".equals(resp.status)) {
					Toast mtoast;
					if(resp.award != null) {
						accountInfo.setFlowcoins(resp.flowcoins);
						WeFlowApplication.getAppInstance().PersistAccountInfo(accountInfo);
//						myTimer.schedule(mTask, 0, 10);
						String awardname = resp.award.prizename;
						if(awardname == null || awardname.equals("摇一摇0流量币")) {
							awardname = noAward[RandomUtils.getRandom(4) % 5];
							OrderDialog.Dialog(this, awardname, true);
							mtoast = Toast.makeText(ShakeShakeActivity.this, awardname, Toast.LENGTH_SHORT);
						} else {
							OrderDialog.Dialog(this, "恭喜您获得" + resp.award.prizename);
							mtoast = Toast.makeText(ShakeShakeActivity.this,
									"恭喜您获得" + resp.award.prizename, Toast.LENGTH_SHORT);
						}
						mtoast.show();
					} else {
						mtoast = Toast.makeText(ShakeShakeActivity.this,
								noAward[RandomUtils.getRandom(4) % 5], Toast.LENGTH_SHORT);
						mtoast.show();
					}
				} else if("2016".equals(resp.status)) {
					OrderDialog.Dialog(this, "摇奖次数已用完", true);
//					PromptDialog.Alert(MainActivity.class, "摇奖次数已用完");
				}
			} else {
				PromptDialog.Alert(ShakeShakeActivity.class, "您的网络不给力啊！");
			}
			mShakeListener.start();
			break;
			
		case Requester.RESPONSE_TYPE_SHAKE_CONFIG:
			if(msg.obj != null) {
				shakeConfigResp resp = (shakeConfigResp) msg.obj;
				if("0".equals(resp.status) || "0000".equals(resp.status)) {
					float cost = 0.0f;
					try {
						cost = Float.parseFloat(resp.cost);
					} catch(Exception e) {
						e.printStackTrace();
					}
					if(cost != 0) {
						tvShakeHint.setText("摇一摇，每次只需" + Math.abs((int)cost) + "流量币");
					}
				}
			}
			break;
		}
		return false;
	}
	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_shakeshake;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.shakeBg:
			shakePerform();
			break;
		}
		return false;
	}
	
}