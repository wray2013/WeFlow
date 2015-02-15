package com.cmmobi.looklook.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.SNS;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareInfo;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareSNSTrace;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.getDiaryUrlResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.platformUrls;
import com.cmmobi.looklook.common.gson.GsonResponse2.shareDiaryResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;

public class ShareWithSnsActivity extends ZActivity{

	public static final String ACTION_DIARY_STRING="action_diary_string";
	private static final String TAG="ShareWithSnsActivity";
	
	private ImageView snsShareBtn = null;
	private ImageView cancleBtn = null;
	private ImageView publishBtn = null;
	
	private Set<String> mSinaSet = new HashSet<String>();
	private Set<String> mRenrenSet = new HashSet<String>();
	private Set<String> mTencentSet = new HashSet<String>();
	
	//分享时间
	private TextView mTVPublishTime = null;
	
//	private boolean isSinaSelected = false;
//	private boolean isTencentSelected = false;
//	private boolean isRenrenSelected = false;
	public static final int REQUEST_CODE = 1;
	
	private MyDiary myDiary;
	private WebImageView ivDiaryCorver;
	private ImageView ivDiaryBackground;
	private EditText etShareContent;
	private TextView tvRemainText;
	private TextView tvTitle;
	
//	private AccountInfo accountInfo;
	
//	private LoginSettingManager settingManager;
	
	// 标示当前是什么微博 "looklook", "active", "sina1", "tencent6", "renren2";
	private String mWeiboType = "";
	
	private DiaryManager mDiaryManager;
	
	private ArrayList<String> mStrWeiboId = new ArrayList<String>();
	private StringBuffer mStrSnsId = new StringBuffer("");
	
	MyBind myBind;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
//		accountInfo = AccountInfo.getInstance(userID);
//		settingManager = accountInfo.setmanager;
		setContentView(R.layout.activity_publish_with_sns);
		snsShareBtn = (ImageView) findViewById(R.id.iv_activity_publish_alt_sns);
		mTVPublishTime = (TextView) findViewById(R.id.tv_activity_publish_time_sns);
		cancleBtn = (ImageView) findViewById(R.id.iv_publish_cancle);
		publishBtn = (ImageView) findViewById(R.id.iv_publish_done);
		ivDiaryCorver=(WebImageView) findViewById(R.id.iv_publish_preview_src);
		ivDiaryBackground=(ImageView) findViewById(R.id.iv_publish_preview);
		etShareContent= (EditText) findViewById(R.id.et_share_content);
		tvRemainText = (TextView) findViewById(R.id.tv_remain_text);
		tvTitle = (TextView) findViewById(R.id.tv_publish_name);
		snsShareBtn.setOnClickListener(this);
		
		mWeiboType = getIntent().getStringExtra(DiaryDetailActivity.INTENT_ACTION_SHARE_TYPE);
		if(mWeiboType == null)
		{
			// 避免出现空
			mWeiboType = "1";
		}
		Drawable drawable = getResources().getDrawable(R.drawable.fabu_sina_normal);;
		if(mWeiboType.equals("2"))
		{
			drawable = getResources().getDrawable(R.drawable.fabu_renren_normal);
		}
		else if(mWeiboType.equals("6"))
		{
			drawable = getResources().getDrawable(R.drawable.fabu_tengxun_normal);
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		tvTitle.setCompoundDrawables(null, null, drawable, null);
		
		cancleBtn.setOnClickListener(this);
		publishBtn.setOnClickListener(this);
		String diaryString =getIntent().getStringExtra(ACTION_DIARY_STRING);
		if(diaryString!=null&&diaryString.length()>0){
			myDiary=new Gson().fromJson(diaryString,MyDiary.class);
			initUI();
			//initBind();
		}else{
			Log.e(TAG, "diaryString is null");
			finish();
		}
		
		StringBuffer time = new StringBuffer("");
		getShareStatus(time, mStrWeiboId, mStrSnsId);
		mTVPublishTime.setText(time);
		if(isMyself())
		{
			mTVPublishTime.setOnClickListener(this);
		}
		
		String content = getContent();
		etShareContent.setText(content);
		tvRemainText.setText(String.valueOf(90 - content.length()));
		etShareContent.addTextChangedListener(mWatcher);
		
		mDiaryManager = DiaryManager.getInstance();
		
	}
	
	private TextWatcher mWatcher = new TextWatcher()
	{
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
			
		}
		
		@Override
		public void afterTextChanged(Editable s)
		{
			tvRemainText.setText(String.valueOf(90 - s.length()));
		}
	};
	
	
//	// move to DiaryDetailActivity
//	private boolean isSinaBind(){
//		if(ActiveAccount.getInstance(this).isSNSBind("sina"))
//			return true;
//		return false;
//	}
//	
//	private boolean isRenrenBind(){
//		if(ActiveAccount.getInstance(this).isSNSBind("renren"))
//			return true;
//		return false;
//	}
//	
//	private boolean isTencentBind(){
//		if(ActiveAccount.getInstance(this).isSNSBind("tencent"))
//			return true;
//		return false;
//	}
	
	//初始化日记布局
	private void initUI(){
		int diaryType=DiaryListView.getDiaryType(myDiary.attachs);
		switch (diaryType) {
		case 0x10000000://主体 视频
		case 0x10000100://主体 视频+辅 音频
		case 0x10000101://主体 视频+辅 音频+文字
			String videoCover = getVideoCover();
			ivDiaryCorver.setImageUrl(0, 1, videoCover, false);
			break;
		case 0x1000000://主体 音频
		case 0x1000101://主体 音频+辅 音频+文字
		case 0x1000100://主体 音频+辅 音频
			ivDiaryBackground.setVisibility(View.INVISIBLE);
			ivDiaryCorver.setVisibility(View.INVISIBLE);
			break;
		case 0x100000://主体 图片
		case 0x100100://主体 图片+辅 音频
		case 0x100101://主体 图片+辅 音频+文字
			ivDiaryCorver.setImageUrl(0, 1, getImageUrl(), false);
			break;
		case 0x10000://主体 文字
		case 0x10100://主体 文字+辅 音频
		case 0x10101://主体 文字+辅 音频+文字
			ivDiaryBackground.setVisibility(View.INVISIBLE);
			ivDiaryCorver.setVisibility(View.INVISIBLE);
			break;
//		case 0x1000://辅 视频
//			break;
		case 0x100://辅 音频
		case 0x101://辅 音频+文字
		case 0x1://辅 文字
			ivDiaryBackground.setVisibility(View.INVISIBLE);
			ivDiaryCorver.setVisibility(View.INVISIBLE);
			break;
//		case 0x10://辅 图片
//			break;
		
		default:
			break;
		}
		
		
	}
	
	private String getImageUrl(){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if(("1".equals(attach.attachtype)||"3".equals(attach.attachtype))&&"1".equals(attach.attachlevel)){
					MyAttachImage[] attachImage=attach.attachimage;
					if(attachImage!=null&&attachImage.length>0){
						//TODO 是否需要根据隐私设置给不同的url
						return attachImage[0].imageurl;
					}
				}
			}
		}
		return null;
	}

//	private String getVideoUrl(){
//		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
//			for(int i=0;i<myDiary.attachs.length;i++){
//				diaryAttach attach=myDiary.attachs[i];
//				if("1".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){
//					MyAttachVideo[] attachVideo=attach.attachvideo;
//					if(attachVideo!=null&&attachVideo.length>0){
//						//TODO 是否需要根据隐私设置给不同的url
//						return attachVideo[0].playvideourl;
//					}
//				}
//			}
//		}
//		return null;
//	}
	
	private String getVideoCover(){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachtype)&&"1".equals(attach.attachlevel)) {
					if(attach.videocover != null) {
						return attach.videocover;
					}
				}
			}
		}
		return null;
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
	
	ArrayList<SNS> snsListNew = null;
	@Override
	public boolean handleMessage(Message msg) {
//		ActiveAccount acct = ActiveAccount.getInstance(this);
//		AccountInfo ai = AccountInfo.getInstance(acct.getLookLookID());
//		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(this);
		switch (msg.what) {
//		case Requester2.RESPONSE_TYPE_DIARY_PUBLISH:
//			if(msg.obj!=null){
//				diaryPublishResponse res = (diaryPublishResponse) msg.obj;
//				if ("0".equals(res.status)) {
//					Prompt.Alert("发布成功");
//					myDiary.diary_status = "2";
//				} else {
//					Prompt.Alert("发布失败");
//					Log.e(TAG, "RESPONSE_TYPE_DIARY_PUBLISH status is "
//							+ res.status);
//				}
//			}else{
//				ZDialog.dismiss();
//				Prompt.Alert("发布失败");
//				Log.e(TAG, "msg.obj is null");
//			}
//			ZDialog.dismiss();
//			break;
		case Requester2.RESPONSE_TYPE_GET_DIARY_URL:
			if(msg.obj!=null){
				getDiaryUrlResponse response=(getDiaryUrlResponse) msg.obj;
				if("0".equals(response.status)){
					
					String sinaUrl = "";
					String renrenUrl = "";
					String tencentUrl = "";
					
					StringBuffer userList = new StringBuffer();
					String shareContent = getInput(etShareContent.getText().toString(), userList);
					
					String picUrl=response.shareimageurl;// + "?width=300&heigh=400";
					for(int i=0;i<response.platformurls.length;i++){
						platformUrls urls=response.platformurls[i];
//						if("0".equals(urls.snstype)){//looklook站内
//							
//						}
						if("1".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//新浪
							sinaUrl = getUrl(urls.url)+userList;
						}
						if("2".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//人人
							renrenUrl = getUrl(urls.url)+userList;
						}
						if("6".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//腾讯
							tencentUrl = getUrl(urls.url)+userList;
						}
//						if("9".equals(urls.snstype)){//微信
//							weixinUrl=urls.url;
//						}
					}
					String lng="";
					String lat="";
					if(CommonInfo.getInstance().myLoc!=null){
						lng=((int)(CommonInfo.getInstance().myLoc.longitude*1E6))/1E6d+"";
						lat=((int)(CommonInfo.getInstance().myLoc.latitude*1E6))/1E6d+"";
					}
					Log.d(TAG, "sinaUrl="+sinaUrl);
					Log.d(TAG, "renrenUrl="+renrenUrl);
					Log.d(TAG, "tencentUrl="+tencentUrl);
					Log.d(TAG, "picUrl="+picUrl);
					if(mWeiboType.equals("1"))
					{
//						WeiboRequester.publishSinaWeibo(this, handler, sinaUrl, "http://t3.qpic.cn/mblogpic/a0582683f18a940923c0/2000");
//						WeiboRequester.publishSinaWeibo(this, handler, shareContent+sinaUrl, picUrl);
						
						// 改为离线模式
						OfflineTaskManager.getInstance().addShareToSina(shareContent, sinaUrl, picUrl, myDiary.diaryid, myDiary.diaryuuid,
								myDiary.position == null ? "" : myDiary.position,lng, lat,myDiary.userid);
						ZDialog.dismiss();
						finish();
					}
					else if(mWeiboType.equals("6"))
					{
//						WeiboRequester.publishTencentWeibo(this, handler, shareContent+tencentUrl, picUrl);
						
						// 改为离线模式
						OfflineTaskManager.getInstance().addShareToTencent(shareContent, tencentUrl, picUrl, myDiary.diaryid, myDiary.diaryuuid,
								myDiary.position == null ? "" : myDiary.position, lng, lat,myDiary.userid);
						ZDialog.dismiss();
						finish();
					}
					else if(mWeiboType.equals("2"))
					{
//						WeiboRequester.publishRenrenWeibo(this, handler, shareContent+renrenUrl, picUrl);
						
						// 改为离线模式
						OfflineTaskManager.getInstance().addShareToRenren(shareContent, renrenUrl, picUrl, myDiary.diaryid,myDiary.diaryuuid,
								myDiary.position == null ? "" : myDiary.position,lng, lat,myDiary.userid);
						ZDialog.dismiss();
						finish();
					}
					else
					{
						Prompt.Alert("请选择发布平台或@好友");
						ZDialog.dismiss();
					}
				}else{
					ZDialog.dismiss();
					Prompt.Alert("分享失败");
					Log.e(TAG, "response.status="+response.status);
				}
			}else{
				ZDialog.dismiss();
				Prompt.Alert("分享失败");
				Log.e(TAG, "msg.obj is null");
			}
			break;
		case WeiboRequester.SINA_INTERFACE_PUBLISH_WEIBO:
			snsListNew = new ArrayList<SNS>();
			if(msg.obj!=null){
				String sinaID=msg.obj.toString();
				SNS sns =new GsonRequest2.SNS();
				sns.snsid=msg.getData().getString("snsid");
				sns.snstype="1";
				sns.weiboid=sinaID;
				snsListNew.add(sns);
				Prompt.Alert("成功分享到新浪微博");
				sendShareDiaryToServer(snsListNew);
			}else{
				Prompt.Alert("分享到新浪微博失败");
				ZDialog.dismiss();
			}
			break;
		case WeiboRequester.TENCENT_INTERFACE_PUBLISH_WEIBO:
			snsListNew = new ArrayList<SNS>();
			if(msg.obj!=null){
				String tencentID=msg.obj.toString();
				SNS sns =new GsonRequest2.SNS();
				sns.snsid=msg.getData().getString("snsid");
				sns.snstype="6";
				sns.weiboid=tencentID;
				snsListNew.add(sns);
				Prompt.Alert("成功分享到腾讯微博");
				sendShareDiaryToServer(snsListNew);
			}else{
				Prompt.Alert("分享到腾讯微博失败");
				ZDialog.dismiss();
			}
			break;
		case WeiboRequester.RENREN_INTERFACE_PUBLISH_WEIBO:
			snsListNew = new ArrayList<SNS>();
			if(msg.obj!=null){
				String renrenID=msg.obj.toString();
				SNS sns =new GsonRequest2.SNS();
				sns.snsid=msg.getData().getString("snsid");
				sns.snstype="2";
				sns.weiboid=renrenID;
				snsListNew.add(sns);
				Prompt.Alert("成功分享到人人网");
				sendShareDiaryToServer(snsListNew);
			}else{
				Prompt.Alert("分享到人人网失败");
				ZDialog.dismiss();
			}
			break;
		case Requester2.RESPONSE_TYPE_SHARE_DIARY:
			if(msg.obj!=null){
				shareDiaryResponse response=(shareDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					Log.d(TAG, "日记分享ID上传服务器成功");
					String diaryString=new Gson().toJson(myDiary);
					setResult(RESULT_OK, new Intent().putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_STRING, diaryString));
					finish();
				}
			}else{
				Log.e(TAG, "RESPONSE_TYPE_SHARE_DIARY msg.obj is null");
			}
			ZDialog.dismiss();
			break;
		case WeiboRequester.SINA_INTERFACE_DEL_WEIBO:
		case WeiboRequester.RENREN_INTERFACE_DEL_WEIBO:
		case WeiboRequester.TENCENT_INTERFACE_DEL_WEIBO:
			if(msg.obj!=null){
				boolean result = (Boolean) msg.obj;
				if(result){
					Prompt.Alert("已成功删除分享");
					mTVPublishTime.setText("");
					mDiaryManager.deleteSnsTrace(myDiary.diaryuuid, mStrSnsId.toString());
					
					mDiaryManager.diaryDataChanged(myDiary.diaryuuid);
					finish();
				}
				else
				{
					Prompt.Alert("删除失败");
				}
			}else{
				Log.e(TAG, "SINA_INTERFACE_DEL_WEIBO msg.obj is null");
			}
			ZDialog.dismiss();
			break;
		default:
			break;
		}
		return false;
	}
	
	//将第三方分享发送到本地
	private void sendShareDiaryToMydiary(String snscontent){

		// 增加到DiaryManager
		ShareSNSTrace snsTrace = new ShareSNSTrace();
		snsTrace.snscontent = snscontent;
		
		snsTrace.shareinfo = new ShareInfo[1];
		snsTrace.shareinfo[0] = new ShareInfo();
		snsTrace.shareinfo[0].snsid = mStrSnsId.toString();
		snsTrace.shareinfo[0].snstype = mWeiboType;
		snsTrace.shareinfo[0].weiboid = "";
		
//		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);       
//		String date = sDateFormat.format(new java.util.Date()); 
		long time = TimeHelper.getInstance().now();
		snsTrace.sharetime = String.valueOf(time);
		
		String diaryString=new Gson().toJson(snsTrace);
		Intent intent = new Intent(DiaryDetailActivity.INTENT_ACTION_SHARE_SUCCESS);
		intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, myDiary.diaryuuid);
		intent.putExtra(ShareWithSnsActivity.ACTION_DIARY_STRING, diaryString);
		LocalBroadcastManager.getInstance(ShareWithSnsActivity.this).sendBroadcastSync(intent);
			
	}
	
	//将第三方分享发送到服务器
	private void sendShareDiaryToServer(ArrayList<SNS> snsList){
		if(snsList.size()>0){
			Log.d(TAG, "上传分享ID到服务器...");
			String snscontent=etShareContent.getText().toString();
			String lng="";
			String lat="";
			if(CommonInfo.getInstance().myLoc!=null){
				lng=((int)(CommonInfo.getInstance().myLoc.longitude*1E6))/1E6d+"";
				lat=((int)(CommonInfo.getInstance().myLoc.latitude*1E6))/1E6d+"";
			}
			Requester2.shareDiary(handler, myDiary.diaryid, "", snscontent, lng,lat, snsList.toArray(new SNS[snsList.size()]));
			// 增加到DiaryManager
			ShareSNSTrace snsTrace = new ShareSNSTrace();
			snsTrace.snscontent = snscontent;
			
			snsTrace.shareinfo = new ShareInfo[1];
			snsTrace.shareinfo[0] = new ShareInfo();
			snsTrace.shareinfo[0].snsid = snsList.get(0).snsid;
			snsTrace.shareinfo[0].snstype = snsList.get(0).snstype;
			snsTrace.shareinfo[0].weiboid = snsList.get(0).weiboid;
			
//			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);       
//			String date = sDateFormat.format(new java.util.Date()); 
			long time = TimeHelper.getInstance().now();
			snsTrace.sharetime = String.valueOf(time);
			
			mDiaryManager.addSnsTrace(myDiary.diaryuuid, snsTrace);
			
			mDiaryManager.diaryDataChanged(myDiary.diaryuuid);
			
			// 为了让推荐列表中的分享能及时响应
//			Intent intent = new Intent(DiaryDetailActivity.INTENT_ACTION_SHARE_SUCCESS);
//			intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_ID, myDiary.diaryid);
//			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
			
		}else{
			Log.d(TAG, "分享到第三方微博ID列表未空");
			ZDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.iv_activity_publish_alt_sns:
			startActivityForResult(new Intent(this,AltFriendActivity.class)
			.putExtra(DiaryDetailActivity.INTENT_ACTION_SHARE_TYPE, mWeiboType), REQUEST_CODE);
			break;
		case R.id.iv_publish_cancle:
			finish();
			break;
		case R.id.iv_publish_done:
			String shareContent=etShareContent.getText().toString();
			if(shareContent == null)
			{
				etShareContent.setText("");
				shareContent = "";
			}
			else if (shareContent.length() > 90)
			{
				new Xdialog.Builder(ShareWithSnsActivity.this)
				.setTitle("")
				.setMessage("文字超过字数限制！")
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
			}
			else
			{
				ZDialog.show(R.layout.progressdialog, false, true, this);
//				Requester2.getDiaryUrl(handler, myDiary.diaryid);
				
				// 改为直接离线
				StringBuffer userList = new StringBuffer();
				shareContent = getInput(etShareContent.getText().toString(), userList);
				String positionInfo = getUrl();
				
				String url = "";
				if(mWeiboType.equals("1"))
				{
					url += " （分享自@looklook-懂你的生活记录 ）";
				}
				else if(mWeiboType.equals("2"))
				{
					url += " （分享自looklook-懂你的生活记录）";
				}
				else if(mWeiboType.equals("6"))
				{
					url += " （分享自@looklook6197－懂你的生活记录）";
				}
				url +=  userList;
				
				String lng="";
				String lat="";
				if(CommonInfo.getInstance().myLoc!=null){
					lng=((int)(CommonInfo.getInstance().myLoc.longitude*1E6))/1E6d+"";
					lat=((int)(CommonInfo.getInstance().myLoc.latitude*1E6))/1E6d+"";
				}
				
				Log.d(TAG, "shareContent="+shareContent);
				Log.d(TAG, "positionInfo="+positionInfo);
				Log.d(TAG, "url="+url);
				
				OfflineTaskManager.getInstance().addGetDiaryShareUrlTask(shareContent, positionInfo, url,myDiary.diaryid, myDiary.diaryuuid,
						myDiary.position == null ? "" : myDiary.position, lng, lat, myDiary.userid, Integer.valueOf(mWeiboType));
				
				if(!isMyself())
				{
					sendShareDiaryToMydiary(shareContent);
				}
				
				ZDialog.dismiss();
				finish();
			}
			break;
		case R.id.tv_activity_publish_time_sns:
			if(!mTVPublishTime.getText().equals("") && !mWeiboType.equals("2"))
			{
				new Xdialog.Builder(this)
				.setTitle("提醒")
				.setMessage("是否删除第三方分享内容")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						ZDialog.show(R.layout.progressdialog, false, true, ShareWithSnsActivity.this);
//						deleteSns();

//						setResult(RESULT_OK, new Intent()
//							.putStringArrayListExtra(DiaryDetailActivity.INTENT_EXTRA_DELETE_SNS, mStrWeiboId)
//							.putExtra(DiaryDetailActivity.INTENT_ACTION_SHARE_TYPE, mWeiboType));
//						ShareWithSnsActivity.this.finish();
						
						if(mWeiboType.equals("1"))
						{
							mDiaryManager.getRemoveSinaIdList().addAll(mStrWeiboId);
						}
						else if(mWeiboType.equals("6"))
						{
							mDiaryManager.getRemoveTencentIdList().addAll(mStrWeiboId);
						}
						else if(mWeiboType.equals("2"))
						{
							mDiaryManager.getRemoveRenrenIdList().addAll(mStrWeiboId);
						}
						Intent intent = new Intent(DiaryDetailActivity.INTENT_ACTION_DELETE_SNS);
						intent.putExtra(DiaryDetailActivity.INTENT_ACTION_SHARE_TYPE, mWeiboType);
						LocalBroadcastManager.getInstance(ShareWithSnsActivity.this).sendBroadcastSync(intent);
						
						Prompt.Alert("已成功删除分享");
						mTVPublishTime.setText("");
						mDiaryManager.deleteSnsTrace(myDiary.diaryuuid, mStrSnsId.toString());
						DiaryManager.getInstance().diaryDataChanged(myDiary.diaryuuid);
						
						finish();
						
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
			}
			break;
		default:
			break;
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_CODE&&resultCode==AltFriendActivity.RESULT_CODE){
			String sinaNames="";
			String renrenNames="";
			String tencentNames="";
			ArrayList<String> sinaList=data.getStringArrayListExtra("SINA_FRIEDS_NAME_LIST");
			ArrayList<String> renrenList=data.getStringArrayListExtra("RENREN_FRIEDS_NAME_LIST");
			ArrayList<String> tencentList=data.getStringArrayListExtra("TENCENT_FRIEDS_NAME_LIST");
			if(sinaList!=null&&sinaList.size()>0){
				for(int i=0;i<sinaList.size();i++){
					String str = "@"+sinaList.get(i)+" ";
					mSinaSet.add(str);
					sinaNames += str;
				}
			}
			if(renrenList!=null&&renrenList.size()>0){
				for(int i=0;i<renrenList.size();i++){
					String str = "@"+renrenList.get(i)+" ";
					mRenrenSet.add(str);
					renrenNames += str;
				}
			}
			if(tencentList!=null&&tencentList.size()>0){
				for(int i=0;i<tencentList.size();i++){
					String str = "@"+tencentList.get(i)+" ";
					mTencentSet.add(str);
					tencentNames += str;
				}
			}
			if(mWeiboType.equals("1"))
			{
				etShareContent.append(sinaNames);
			}
			else if(mWeiboType.equals("2"))
			{
				etShareContent.append(renrenNames);
			}
			else if(mWeiboType.equals("6"))
			{
				etShareContent.append(tencentNames);
			}
//			etShareContent.append(sinaNames+renrenNames+tencentNames);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// Url拼接
	private String getUrl(String strLink)
	{
		String url = "";
		if(myDiary.position != null && !myDiary.position.equals("") &&
				myDiary.position_status != null && myDiary.position_status.equals("1"))
		{
			
			if(isMyself())
			{
				url += " #我在这里：";
			}
			else
			{
				url += " #内容所在位置:";
			}
			url += myDiary.position;
			url += "#";
		}
		url += " 播放地址：";
		url += strLink;
		
		if(mWeiboType.equals("1"))
		{
			url += " （分享自@looklook-懂你的生活记录）";
		}
		else if(mWeiboType.equals("2"))
		{
			url += " （分享自looklook－懂你的生活记录）";
		}
		else if(mWeiboType.equals("6"))
		{
			url += " （分享自@looklook-懂你的生活记录）";
		}
		
		return url;
	}
	
	// Url拼接
	private	String getUrl()
	{
		String url = "";
		if(myDiary.position != null && !myDiary.position.equals("") &&
				myDiary.position_status != null && myDiary.position_status.equals("1"))
		{
			
			if(isMyself())
			{
				url += " #我在这里：";
			}
			else
			{
				url += " #内容所在位置:";
			}
			url += myDiary.position;
			url += "#";
		}
		url += " 播放地址：";
		
		return url;
	}
	
	// 判断当前日记是否是自己的日记
	private boolean isMyself() {
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		if (userID != null && userID.equals(myDiary.userid))
				return true;
		return false;
	}
	
	// 获取已分享文字
	private String getContent()
	{
		// 查找最后一次的分享内容
		String content = null;
		if(myDiary.sns != null)
		{
//			for(int i = 0; i < myDiary.sns.length; i++)
//			{
//				boolean flag = false;
//				for(int j = 0; j < myDiary.sns[i].shareinfo.length; j++)
//				{
//					if(myDiary.sns[i].shareinfo[j].snstype.equals(mWeiboType))
//					{
//						flag = true;
//						break;
//					}
//				}
//				if(flag)
//				{
//					content = myDiary.sns[i].snscontent;
//				}
//			}
			if(myDiary.sns.length > 0)
			{
				content = myDiary.sns[myDiary.sns.length -1].snscontent;
			}
		}
		
		if(content == null)
		{
			for(int i = 0; i < myDiary.attachs.length; i++)
			{
				if(myDiary.attachs[i].attachtype.equals("4"))
				{
					content = myDiary.attachs[i].content;
					break;
				}
			}
		}
		
		if(content == null)
		{
			content = "";
		}
		
		return content;
	}
	
	// 获取分享信息的状态
	private boolean getShareStatus(StringBuffer time, List<String> weiboId, StringBuffer snsId)
	{
		String mtime = "";
		String mweibo = "";
		String msns = "";
		if(myDiary.sns != null)
		{
			int shareCount = 0;
			for(int i = 0; i < myDiary.sns.length; i++)
			{
				if(myDiary.sns[i].shareinfo != null)
				{
					boolean flag = false;
					for(int j = 0; j < myDiary.sns[i].shareinfo.length; j++)
					{
						if(myDiary.sns[i].shareinfo[j].snstype != null
								&&myDiary.sns[i].shareinfo[j].snstype.equals(mWeiboType))
						{
							mweibo = myDiary.sns[i].shareinfo[j].weiboid;
							weiboId.add(mweibo);
							msns = myDiary.sns[i].shareinfo[j].snsid;
							flag = true;
						}
					}
					if(flag)
					{
						shareCount ++;
						
						long t = 0l;
						try
						{
							t = Long.parseLong(myDiary.sns[i].sharetime);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);       
						mtime = sDateFormat.format(new java.util.Date(t)); 
					}
				}
			}
			if(shareCount > 1)
			{
				mtime = "已分享";
				if(mWeiboType.equals("1"))
				{
					mtime = "已在新浪分享";
				}
				else if(mWeiboType.equals("2"))
				{
					mtime = "已在人人分享";
				}
				else if(mWeiboType.equals("6"))
				{
					mtime = "已在腾讯分享";
				}
				mtime += String.valueOf(shareCount);
				mtime += "次";
			}
			else if(shareCount == 1)
			{
				mtime += " 分享";
			}
			else if(shareCount == 0)
			{
				mtime = "";
			}
			time.append(mtime);
			snsId.append(msns);
			
		}
		return false;
	}
	
	// 删除微博
//	private void deleteSns()
//	{
//		if(mWeiboType.equals("1"))
//		{
//			WeiboRequester.delSinaWeibo(this, handler, mStrWeiboId.toString());
//		}
//		else if(mWeiboType.equals("2"))
//		{
//			WeiboRequester.delRenrenWeibo(this, handler, mStrWeiboId.toString());
//		}
//		else if(mWeiboType.equals("6"))
//		{
//			WeiboRequester.delTencentWeibo(this, handler, mStrWeiboId.toString());
//		}
//	}
	
	// 过滤处理输入文字
	private String getInput(String content, StringBuffer userList)
	{
		if(mSinaSet.size() > 0)
		{
			Iterator<String> ite = mSinaSet.iterator();
			while(ite.hasNext())
			{
				String s = ite.next();
				if(content.indexOf(s) != -1)
				{
					content = content.replace(s, "");
					userList.append(s);
				}
			}
		}
		if(mRenrenSet.size() > 0)
		{
			Iterator<String> ite = mRenrenSet.iterator();
			while(ite.hasNext())
			{
				String s = ite.next();
				if(content.indexOf(s) != -1)
				{
					content = content.replace(s, "");
					userList.append(s);
				}
			}
		}
		if(mTencentSet.size() > 0)
		{
			Iterator<String> ite = mTencentSet.iterator();
			while(ite.hasNext())
			{
				String s = ite.next();
				if(content.indexOf(s) != -1)
				{
					content = content.replace(s, "");
					userList.append(s);
				}
			}
		}
		
		return content;
		
	}
	
}
