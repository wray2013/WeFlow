package com.cmmobi.sns.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.cmmobi.sns.R;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.utils.ConfigUtil;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

/**
 * 登录选择界面
 *		1.根据不同的按钮点击初始化数据 		
 * @author xudongsheng
 */
public class MainAct extends Activity implements OnClickListener{
	
	private final String LOGTAG = "MainAct";
	private CmmobiSnsLib mSnsLib;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSnsLib = CmmobiSnsLib.getInstance(this);
        
        Button sinaBtn = (Button) findViewById(R.id.sinaBtn);
        sinaBtn.setOnClickListener(this);
        sinaBtn.setTag(ConfigUtil.SINAW);
        
        Button qqBtn = (Button) findViewById(R.id.qqBtn);
        qqBtn.setOnClickListener(this);
        qqBtn.setTag(ConfigUtil.QQW);
        
        Button shareSinaBtn = (Button) findViewById(R.id.shareToSina);
        shareSinaBtn.setOnClickListener(this);
        shareSinaBtn.setTag(ConfigUtil.ShareToSina);
        
        Button shareTencentBtn = (Button) findViewById(R.id.shareToTencent);
        shareTencentBtn.setOnClickListener(this);
        shareTencentBtn.setTag(ConfigUtil.ShareToTencent);
    }
    
    private WeiboAuthListener listener = new WeiboAuthListener(){

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
		if(curWeibo.equals(ConfigUtil.SINAW)){
			mSnsLib.sinaAuthorize(listener);
		}else if(curWeibo.equals(ConfigUtil.QQW)){
			mSnsLib.tencentWeiboAuthorize(listener);
		}else if(curWeibo.equals(ConfigUtil.ShareToSina)){
			try {
				if(!mSnsLib.isSinaWeiboAuthorized()){
					mSnsLib.sinaAuthorize(listener);
				}else{
					String  picPath = "http://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s160-c/sample_image_01.jpg";
					//String picPath="/sdcard/qweibosdk2/logo_QWeibo.jpg";
					//boolean isSuccess = mSnsLib.sinaUploadUrlText("looklook 分享", "http://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s160-c/sample_image_01.jpg",
					//		"", "");
					boolean isSuccess = mSnsLib.sinaUploadUrlText("looklook 分享", picPath,
							"", "");
					if(isSuccess){
						Toast.makeText(this, "新浪微博分享成功", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(this, "新浪微博分享失败", Toast.LENGTH_LONG).show();
					}	
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(curWeibo.equals(ConfigUtil.ShareToTencent)){
			try {
				if(!mSnsLib.isTencentWeiboAuthorized()){
					mSnsLib.tencentWeiboAuthorize(listener);
				}else{
					String picPath="/sdcard/qweibosdk2/logo_QWeibo.jpg";
					//boolean isSuccess = mSnsLib.twUploadUrlText("looklook 分享", "",
						//	"http://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s160-c/sample_image_01.jpg");
					boolean isSuccess = mSnsLib.twUploadUrlText("looklook 分享", "", picPath);
					Log.i(LOGTAG, "------tencent weibo ,addPic str is:---------" + isSuccess);
					if(isSuccess){
						Toast.makeText(this, "腾讯微博分享成功", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(this, "腾讯微博分享失败", Toast.LENGTH_LONG).show();
					}
					String str = mSnsLib.getMyTWFriendsList("30", "0");
					Toast.makeText(this, str, Toast.LENGTH_LONG).show();
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
