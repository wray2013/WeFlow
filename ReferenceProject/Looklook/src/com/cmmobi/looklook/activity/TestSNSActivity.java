package com.cmmobi.looklook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.WeiboResponse;
import com.cmmobi.looklook.common.gson.WeiboResponse.RenrenSourceLink;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.utils.ConfigUtil;
import com.google.gson.Gson;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

/**
 * 根据不同的按钮点击初始化数据
 * 
 * @author zhangwei
 */
public class TestSNSActivity extends Activity implements OnClickListener {

	private static final String TAG = "TestSNSActivity";
	private final String LOGTAG = "TestSNSActivity";
	private CmmobiSnsLib mSnsLib;
	private Gson gson;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testsns_main);
		String uid = ActiveAccount.getInstance(this).getUID();
		mSnsLib = CmmobiSnsLib.getInstance(this);

		gson = new Gson();
		Button sinaBtn = (Button) findViewById(R.id.sinaBtn);
		sinaBtn.setOnClickListener(this);
		sinaBtn.setTag(ConfigUtil.SINAW);

		Button rrBtn = (Button) findViewById(R.id.renrenBtn);
		rrBtn.setOnClickListener(this);
		rrBtn.setTag(ConfigUtil.RNRNW);
		
		Button qqBtn = (Button) findViewById(R.id.qqBtn);
		qqBtn.setOnClickListener(this);
		qqBtn.setTag(ConfigUtil.TENCENTW);

		Button shareSinaBtn = (Button) findViewById(R.id.shareToSina);
		shareSinaBtn.setOnClickListener(this);
		shareSinaBtn.setTag(ConfigUtil.ShareToSina);
		
		Button shareRenrenBtn = (Button) findViewById(R.id.shareToRenren);
		shareRenrenBtn.setOnClickListener(this);
		shareRenrenBtn.setTag(ConfigUtil.ShareToRenren);

		Button shareTencentBtn = (Button) findViewById(R.id.shareToTencent);
		shareTencentBtn.setOnClickListener(this);
		shareTencentBtn.setTag(ConfigUtil.ShareToTencent);
		
		Button getCommentFromSinaBtn = (Button) findViewById(R.id.getCommentFromSina);
		getCommentFromSinaBtn.setOnClickListener(this);
		getCommentFromSinaBtn.setTag(ConfigUtil.GetCommentFromSina);
		
		Button getCommentFromRenrenBtn = (Button) findViewById(R.id.getCommentFromRenren);
		getCommentFromRenrenBtn.setOnClickListener(this);
		getCommentFromRenrenBtn.setTag(ConfigUtil.GetCommentFromRenren);

		Button getCommentFromTencentBtn = (Button) findViewById(R.id.getCommentFromTencent);
		getCommentFromTencentBtn.setOnClickListener(this);
		getCommentFromTencentBtn.setTag(ConfigUtil.GetCommentFromTencent);
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

	private WeiboAuthListener listener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {

		}

		@Override
		public void onWeiboException(WeiboException e, int weiboIndex) {

		}

		@Override
		public void onError(WeiboDialogError e, int weiboIndex) {

		}

		@Override
		public void onCancel(int weiboIndex) {

		}
	};

	@Override
	public void onClick(View v) {
		String curWeibo = String.valueOf(v.getTag());
		String uid = ActiveAccount.getInstance(this).getUID();
		if (curWeibo.equals(ConfigUtil.SINAW)) {
			mSnsLib.sinaAuthorize(listener);
		}else if (curWeibo.equals(ConfigUtil.RNRNW)) {
			mSnsLib.renrenAuthorize(listener);
		} else if (curWeibo.equals(ConfigUtil.TENCENTW)) {
			mSnsLib.tencentWeiboAuthorize(listener);
		} else if (curWeibo.equals(ConfigUtil.ShareToSina)) {
			try {
				if (!mSnsLib.isSinaWeiboAuthorized()) {
					mSnsLib.sinaAuthorize(listener);
				} else {
					String picPath = "http://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s160-c/sample_image_01.jpg";

					String weiboID = mSnsLib.sinaUploadUrlText(
							"looklook 分享", picPath, "", "");
					if (weiboID!=null) {
						Toast.makeText(this, "新浪微博分享成功", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(this, "新浪微博分享失败", Toast.LENGTH_LONG)
								.show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (curWeibo.equals(ConfigUtil.ShareToRenren)) {
			try {
				if (!mSnsLib.isRenrenWeiboAuthorized()) {
					mSnsLib.renrenAuthorize(listener);
				} else {
					//String picPath = "http://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s160-c/sample_image_01.jpg";
					//boolean isSuccess = mSnsLib.renrenUploadUrlText(uid, "looklook 分享", picPath);
					
					//String videopath = "http://v.looklook.cn/ua/bYRzIj/2/1/v.htm";
					String videopath = "http://2s.looklook.cn:8081/pub/platform/201305/30/604e7f527a4f46b6bfe1d51e0cfc3cd6.mp4";
					//{ "text": "App A", "href": "http://appa.com/path"}
					RenrenSourceLink rsl = new WeiboResponse.RenrenSourceLink();
					rsl.text = "LookLook视频分享";
					rsl.href = "http://v.looklook.cn/ua/index.htm";
					boolean isSuccess = mSnsLib.renrenShareVideo(videopath, 10, "hahaha", gson.toJson(rsl));
					
					if (isSuccess) {
						Toast.makeText(this, "人人微博分享成功", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(this, "人人微博分享失败", Toast.LENGTH_LONG)
								.show();
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (curWeibo.equals(ConfigUtil.ShareToTencent)) {
			try {
				if (!mSnsLib.isTencentWeiboAuthorized()) {
					mSnsLib.tencentWeiboAuthorize(listener);
				} else {
					// String picPath="/sdcard/qweibosdk2/logo_QWeibo.jpg";
					String picPath = "http://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s160-c/sample_image_01.jpg";
					
					String weiboID = mSnsLib.twUploadUrlText("looklook 分享", "", picPath);
					if (weiboID!=null) {
						Toast.makeText(this, "腾讯微博分享成功", Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(this, "腾讯微博分享失败", Toast.LENGTH_LONG)
								.show();
					}
					String name = "zhangweihust";
					String str = mSnsLib.getMyTWFriendsList(name, "30", "0");
					Toast.makeText(this, str, Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (curWeibo.equals(ConfigUtil.GetCommentFromRenren)) {
			try {
				if (!mSnsLib.isRenrenWeiboAuthorized()) {
					mSnsLib.renrenAuthorize(listener);
				} else {
					String jsonStr =  mSnsLib.getRWCommentList("48633", "512787643" , 1, 20);
					Log.e(TAG, jsonStr);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
        if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
        	CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
        }
	
	}
}
