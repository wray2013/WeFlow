package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.VshareDetailCommentsAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryDetailComment;
import com.cmmobi.looklook.common.gson.GsonResponse3.MiShareinfo;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.GsonResponse3.myMicInfoResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.InputRecoderView;
import com.cmmobi.looklook.common.view.XGridDialog;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.view.InputRecoderView.AudioRecoderBean;
import com.cmmobi.looklook.common.view.InputRecoderView.InputStrType;
import com.cmmobi.looklook.common.view.InputRecoderView.OnSendListener;
import com.cmmobi.looklook.common.view.VshareMemberThumbnailView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.fragment.SafeboxVShareFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.receiver.CmmobiPushReceiver;
import com.google.gson.Gson;

/**
 * 微享详情页
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-10-25
 * 
 */
public class VshareDetailActivity extends FragmentActivity implements OnRefreshListener2<ListView>,OnLongClickListener, OnSendListener, OnClickListener, Callback{

	private ImageView iv_back;
	private VshareMemberThumbnailView vmtv_members;
	private Context context;
	
	private PullToRefreshListView xlv_commentData;
	private ListView lv_commentData;
	private VshareDetailCommentsAdapter commentsAdapter;
	private ArrayList<VshareDetailItem> commentsList = new ArrayList<VshareDetailItem>();
	
	//日记内容
	private String is_encrypt = null; //微享是否在保险箱
	
	private myMicInfoResponse mMicinfo;
	
	private DiaryDetailComment mCurrCCmment;
	
	private String publishid;
	private InputRecoderView inpRecoderView;
					
	private AccountInfo accountInfo;
	private String userid;
	private String micuserid;
	
	private boolean isBurn = false; //阅后即焚标识
	
	private Handler handler;
			
	private boolean isBackVshareList = false;
	public static final String FLAG_BOOL_BACK_VSHARE_LIST = "goto_vsharelist";
	
	private boolean pullDown = true;
	
	private String first_time = "";
	private String last_time = "";
	private Boolean isHasNextPage = true;
	private static final int STOP_REFRESH = 0x12301f;
	
	private long start_time;
	
	
	private RelativeLayout mRootLayout = null;
	private int mOffset = 0;
	
	Rect commentViewRect = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vshare_detail);
		setContent();		
		setListener();		
		start_time = SystemClock.elapsedRealtime();
		// 是否
		isBackVshareList = getIntent().getBooleanExtra(FLAG_BOOL_BACK_VSHARE_LIST, false);
		
		handler = new Handler(this);
		context = this;			
		userid = ActiveAccount.getInstance(this).getUID();
		accountInfo = AccountInfo.getInstance(userid);
		
		if(getIntent()!=null){
			publishid = getIntent().getExtras().getString("publishid");
			is_encrypt = getIntent().getExtras().getString("is_encrypt");
			micuserid =  getIntent().getExtras().getString("micuserid");
			String burn = getIntent().getExtras().getString("is_burn");
			if(burn!=null && burn.equalsIgnoreCase("1")) {
				isBurn = true;
			}
			
			Log.e("XXX", "publishid = " + publishid);
			if(publishid!=null && TextUtils.isEmpty(micuserid)){
				MicListItem item = accountInfo.vshareDataEntities.findMember(publishid);
				if(item != null){
					micuserid = item.micuserid;
				}
			}
			Requester3.myMicInfo(handler, publishid, micuserid);
			ZDialog.show(R.layout.progressdialog, false, true, this);
		}
		
		mRootLayout = (RelativeLayout)findViewById(R.id.rl_root);
		mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                mRootLayout.getWindowVisibleDisplayFrame(r);

                int screenHeight = mRootLayout.getHeight();
                int heightDifference = screenHeight - r.height();
                if (commentViewRect != null && heightDifference > 0) {// 键盘弹起时
                	if (screenHeight - commentViewRect.top + inpRecoderView.getHeight() > r.height()) {
                		mOffset = r.height() - (screenHeight - commentViewRect.top + inpRecoderView.getHeight());
                		if (xlv_commentData != null) {
                			xlv_commentData.getRefreshableView().scrollBy(0, mOffset);
                		}
                	}else if(commentViewRect.bottom + inpRecoderView.getHeight() > r.height()) {
                		mOffset = inpRecoderView.getHeight();
                		if (xlv_commentData != null) {
                			xlv_commentData.getRefreshableView().scrollBy(0, mOffset);
                		}
                	}
                	commentViewRect = null;
                } else if (commentViewRect == null && heightDifference == 0) {
                	if (xlv_commentData != null && mOffset != 0) {
                		xlv_commentData.getRefreshableView().scrollBy(0, mOffset * -1);
            		}
                	mOffset = 0;
                }
            }
        });
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
		CmmobiPushReceiver.cancelNotification(this, CmmobiPushReceiver.NOTIFY_INDEX_VSHARE_DETAIL);
		if(getIntent()!=null){
			if(getIntent().getExtras().getString("frompush") != null){
				accountInfo.vSharePublishId = publishid;
			}
		}
		inpRecoderView.mRegisterReceiver();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
		inpRecoderView.mUnRegisterReceiver();
		// 20140408
		CmmobiClickAgentWrapper.onEventDuration(this, "sh_space", SystemClock.elapsedRealtime()- start_time);
	}

	private void setContent()
	{
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(VshareDetailActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				VshareDetailActivity.this.finish();
				return false;
			}
		});
		
		vmtv_members = (VshareMemberThumbnailView) findViewById(R.id.vmtv_members);
		vmtv_members.setImageUrls(null);

		xlv_commentData = (PullToRefreshListView) findViewById(R.id.xlv_comments_data);
		xlv_commentData.setShowIndicator(false);
		lv_commentData = xlv_commentData.getRefreshableView();
		xlv_commentData.setOnRefreshListener(this);
		commentsAdapter = new VshareDetailCommentsAdapter(this);
		lv_commentData.setAdapter(commentsAdapter);
		inpRecoderView = (InputRecoderView) findViewById(R.id.inp_recoder_view);
		lv_commentData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if(arg2>1){
					if( !userid.equals(((CommentItem)(commentsList.get(arg2-1))).comment.userid)){
						mCurrCCmment = ((CommentItem)(commentsList.get(arg2-1))).comment;
						inpRecoderView.clearView();
						String name = "";
						if(TextUtils.isEmpty(((CommentItem)(commentsList.get(arg2-1))).comment.nickmarkname)){
							name = ((CommentItem)(commentsList.get(arg2-1))).comment.nickname;
						}else {
							name = ((CommentItem)(commentsList.get(arg2-1))).comment.nickmarkname;
						}
						inpRecoderView.setReplyName(name);
						inpRecoderView.setInputStrKey(InputStrType.COMMENT, mCurrCCmment.commentid);
						inpRecoderView.setVisibility(View.VISIBLE);
						inpRecoderView.showSoftKeyBoard();
						commentViewRect = new Rect();
						arg1.getGlobalVisibleRect(commentViewRect);
					}else {
						inpRecoderView.clearView();
					}
					
				}
			}
		});
	}
	
	private void setListener()
	{
		iv_back.setOnClickListener(this);
		vmtv_members.setOnClickListener(this);	
		inpRecoderView.setOnSendListener(this);
	}
	
	@Override
	public boolean handleMessage(Message msg)
	{
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_MYMICINFO:
			try {
				ZDialog.dismiss();
				GsonResponse3.myMicInfoResponse res = (GsonResponse3.myMicInfoResponse) msg.obj;
				if (res != null) {
					if (res.status.equals("0")) {	
						MicListItem item = accountInfo.vshareDataEntities.findMember(publishid);
						if(item != null){
							item.is_clear = "1";
						}
						//组成员头像
						mMicinfo = res;
						String[] headurl = new String[res.userobj.length];
						for(int i=0; i< res.userobj.length; i++){
							headurl[i] = res.userobj[i].headimageurl;
							vmtv_members.setUserObjs(res.userobj);
						}
						vmtv_members.setImageUrls(headurl);
						VshareDetail vshareDetail = new VshareDetail();
						vshareDetail.miShareinfo = res.mishareinfo;
						vshareDetail.isBurn = isBurn;
						commentsList.clear();
						commentsList.add(vshareDetail);
						CommentItem commentItem;
						for(int i=0; i< res.comments.length; i++){
							commentItem = new CommentItem();
							commentItem.comment = res.comments[i];
							if(!isBurn)
								commentsList.add(commentItem);
						}
						
						if(res.comments.length>1){
							first_time = res.comments[0].createtime;
							last_time = res.comments[res.comments.length-1].createtime;
						}
						
						
						commentsAdapter.setList(commentsList);
						commentsAdapter.notifyDataSetChanged();
					} else if (res.status.equals("200600")) {
						Prompt.Alert(context, Constant.CRM_STATUS[Integer
								.parseInt(res.crm_status)]);
					} else {
						Prompt.Alert("失败");					
					}
				} else {
					Prompt.Alert(context, "网络不给力");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester3.RESPONSE_TYPE_COMMENT:
			
			ZDialog.dismiss();
			GsonResponse3.commentResponse cmmresp = (GsonResponse3.commentResponse)msg.obj;
			if(cmmresp!=null){
				if("0".equals(cmmresp.status)){
					Prompt.Alert("私语成功");	
					LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SafeboxVShareFragment.ACTION_VSHARE_CHANGED));  
					if(mCurrSendBean != null ){
						String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
						String commenttype = "";
						
						// 回复
						if(mCurrCCmment!=null){
							commenttype = "2";
						}else{ // 直接评论 
							commenttype = "1";
						}
						
						// 语音加文字
						if("2".equals(mCurrSendBean.commenttype) ||  
						    "3".equals(mCurrSendBean.commenttype) ){
							// 上传语音
							try {
								//参数有问题
								NetworkTaskInfo tt = new NetworkTaskInfo(userID, "", cmmresp.commentid, 
										Environment.getExternalStorageDirectory() + mCurrSendBean.localFilePath,
										cmmresp.audiopath, "3", "2");
								
								UploadNetworkTask uploadtask = new UploadNetworkTask(tt);// 创建上传/下载任务
								uploadtask.start();
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						// 保存评论
						AccountInfo accinfo = AccountInfo.getInstance(userid);
						
						DiaryDetailComment addCmm = new DiaryDetailComment();
						
						addCmm.userid       = userid;   //评论用户ID
						addCmm.headimageurl = accinfo.headimageurl;  //头像URL
						addCmm.nickname     = accinfo.nickname;   //昵称
						addCmm.sex          = accinfo.sex;        // 0男，1女， 2未知 
						
						addCmm.commentid   = cmmresp.commentid;   //评论ID，当前评论id
						addCmm.commentuuid = cmmresp.commentuuid;  //评论UUID，当前评论uuid
						addCmm.createtime  = TimeHelper.getInstance().now() + "";  // 时间戳
						
						addCmm.publishid = publishid;   //日记ID
						addCmm.commentcontent = mCurrSendBean.content;  //评论
						addCmm.audiourl = mCurrSendBean.localFilePath;     // 语音地址 
						addCmm.playtime = mCurrSendBean.playtime;    //语音播放时长
						addCmm.commentway = mCurrSendBean.commenttype;  // 评论方式1、文字 2、声音3、声音加文字
						addCmm.commenttype = commenttype;  //评论类型：1、评论 2回复
						if(mCurrCCmment!=null){
							addCmm.replynickname = mCurrCCmment.nickname;  //被回复人昵称
							addCmm.replymarkname = mCurrCCmment.nickmarkname;
						}
						
						//HashMap<String,String> map = new HashMap<String, String>();						
						if("1".equals(addCmm.commenttype)){
							if("1".equals(addCmm.commentway)){
								//map.put("label", "2");
								// 20140408
								CmmobiClickAgentWrapper.onEvent(this,"sh_comment_send", "2");
							}else{
								//map.put("label", "1");
								// 20140408
								CmmobiClickAgentWrapper.onEvent(this,"sh_comment_send", "1");
							}
						}else{
							if("1".equals(addCmm.commentway)){
								//map.put("label", "2");
								// 20140408
								CmmobiClickAgentWrapper.onEvent(this,"sh_reply", "2");	
							}else{
								//map.put("label", "1");
								// 20140408
								CmmobiClickAgentWrapper.onEvent(this,"sh_reply", "1");
							}
						}
						
						CommentItem commentItem = new CommentItem();
						commentItem.comment = addCmm;
						if(!isBurn)
							commentsList.add(1, commentItem);				
						commentsAdapter.setList(commentsList);
						commentsAdapter.notifyDataSetChanged();
						inpRecoderView.clearView();
						cleanCmmData();
						
						
					}
					
				}else{
					Prompt.Alert("服务器返回错误，错误码：" + cmmresp.status);
				}
			}else{
				Prompt.Alert("网络不给力");
			}		
			break;
		case Requester3.RESPONSE_TYPE_MICCOMMENTLIST:
			try {
				ZDialog.dismiss();
				GsonResponse3.MicCommentsResponse res = (GsonResponse3.MicCommentsResponse) msg.obj;
				if (res != null) {
					if (res.status.equals("0")) {	
						if("1".equals(res.is_refresh)){
							VshareDetail vshareDetail = new VshareDetail();
							vshareDetail.miShareinfo = mMicinfo.mishareinfo;
							vshareDetail.isBurn = isBurn;
							commentsList.clear();
							commentsList.add(vshareDetail);
						}
						if(commentsList.size() < 2){
							first_time = res.first_comment_time;
							last_time = res.last_comment_time;
						}
						
						CommentItem commentItem;
						if(pullDown){
							if(accountInfo.vshareDataEntities.findMember(publishid) !=null)
								accountInfo.vshareDataEntities.findMember(publishid).commentnum = "0";
							first_time = res.first_comment_time;
							for(int i=0; i< res.comments.length; i++){
								commentItem = new CommentItem();
								commentItem.comment = res.comments[i];
								if(!isBurn)
									commentsList.add(i+1,commentItem);
							}
						}else{
							last_time = res.last_comment_time;
							for(int i=0; i< res.comments.length; i++){
								commentItem = new CommentItem();
								commentItem.comment = res.comments[i];
								if(!isBurn)
									commentsList.add(commentItem);
							}
						}	
						uniqCommentlist();
						commentsAdapter.setList(commentsList);
						commentsAdapter.notifyDataSetChanged();
						
						if(!pullDown &&! "1".equals(res.hasnextpage)){
							isHasNextPage = false;
						}
					} else if (res.status.equals("200600")) {
						Prompt.Alert(context, Constant.CRM_STATUS[Integer
								.parseInt(res.crm_status)]);
					} else {
						Prompt.Alert("失败");
					}
				} else {
					Prompt.Alert(context, "网络不给力");
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			xlv_commentData.onRefreshComplete();
			break;
		case STOP_REFRESH:
			xlv_commentData.onRefreshComplete();
			break;
		default:
			break;
		}
		return false;
	}

	
	private void uniqCommentlist(){
		for (int i = 1; i < commentsList.size() - 1; i++) {
			for (int j = commentsList.size() - 1; j > i; j--) {		
				if (((CommentItem)(commentsList.get(j))).comment.commentid.equals(((CommentItem)(commentsList.get(i))).comment.commentid)) {
					commentsList.remove(j);	
					}
				}		
			}
	}
	
	private DialogFragment dialogFragment;
	
	private void DoBackPressed() {
		if(isBackVshareList){
			Intent i = new Intent(context, LookLookActivity.class);
			i.setAction(LookLookActivity.ACTION_ENTER_VSHARE);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			this.finish();
		}else{
			this.finish();
		}
	}
	
	private void removeVShareItem() {
		String uid = ActiveAccount.getInstance(context).getUID();
		AccountInfo ai = AccountInfo.getInstance(uid);
		ai.vshareDataEntities.removeMember(publishid);
	}
	
	private void PopDialog() {
		new Xdialog.Builder(this)
		.setMessage("该内容已被设置阅后即焚\n退出后将无法再次查看")
		.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog,
					int which) {
				// TODO Auto-generated method stub
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						removeVShareItem();
						Intent msgIntent = new Intent(LookLookActivity.MIC_LIST_CHANGE);
						LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
								msgIntent);
						DoBackPressed();
					}
				});
			}
		}).setNegativeButton("再看看", null)
		.create().show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			inpRecoderView.hideSoftInputFromWindow();
			if(isBurn) {
				PopDialog();
			} else {
				DoBackPressed();
			}
			break;
		case R.id.vmtv_members:
			if(!isBurn) {
				Intent intent = new Intent(this, VshareGroupActivity.class);
				intent.putExtra("detailinfo", new Gson().toJson(mMicinfo));
				intent.putExtra("is_encrypt", is_encrypt);
				startActivity(intent);
			} else if(vmtv_members != null) {
				UserObj[] objs = vmtv_members.getUserObjs();
				XGridDialog xgDialog = new XGridDialog(context);
				xgDialog.setUserObjs(objs);
				xgDialog.showDialog();
			}
			
			break;	
		default:
			break;

		}
		
	}

	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	if(isBurn) {
        		PopDialog();
        	} else {
        		DoBackPressed();
        	}
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.ib_comment_record:
			// TODO 录音
			break;
		}
		return false;
	}
	
		
		private AudioRecoderBean mCurrSendBean;
		
		/**
		 * 语音控件 : 发送按钮回调
		 */
		@Override
		public void onSend(AudioRecoderBean bean) {
			
			mCurrSendBean = bean;
			// 直接回复
			if(mCurrCCmment == null && mMicinfo.mishareinfo != null){
				ZDialog.show(R.layout.progressdialog, true, true, this);
				Requester3.comment(handler, bean.content, "" , "1" , publishid, bean.commenttype,bean.commentuuid, "", "2");
				/*if(!mMicinfo.mishareinfo.micuserid.equals(userid)){
					accountInfo.recentContactManager.addRecentContact(accountInfo.friendsListName.findUserByUserid(mMicinfo.mishareinfo.micuserid));
				}*/
			}else if(mCurrCCmment != null){   // 评论回复
				ZDialog.show(R.layout.progressdialog, true, true, this);
				Requester3.comment(handler, bean.content,mCurrCCmment.commentid,"2",publishid,bean.commenttype,bean.commentuuid, "", "2");
				/*if(!mCurrCCmment.userid.equals(userid)){
					accountInfo.recentContactManager.addRecentContact(accountInfo.friendsListName.findUserByUserid(mCurrCCmment.userid));
				}*/
			}else{
				ZLog.e("send error。。。。");
			}
		}	
		
		

		/**
		 * 隐藏输入框时清楚缓存
		 * 
		 */
		public void cleanCmmData(){
			mCurrCCmment = null;
		}
		
		 //点击EditText以外的任何区域隐藏键盘
	    @Override
	    public boolean dispatchTouchEvent(MotionEvent ev) {
	        if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
	            View v = inpRecoderView;
	            if (isShouldHideInput(v, ev)) {
	                if(hideInputView()) {
	                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
	                }
	            }
	        }
	        return super.dispatchTouchEvent(ev);   
	    }     
	    
	    public static boolean isShouldHideInput(View v, MotionEvent event) {
	        if (v != null) {
	            int top = v.getTop();
	            if (event.getY() > top) {
	                // 保留点击EditText的事件
	                return false;
	            } else {
	                return true;
	            }
	        }
	        return false;
	    }    
	    
	    private Boolean hideInputView(){
	    	if(inpRecoderView.getVisibility() == View.VISIBLE){
		        inpRecoderView.clearView();
				cleanCmmData();
				inpRecoderView.setVisibility(View.GONE);
		    	return true;
	    	}
	    	return false;
	    }

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			hideCommentInputView();
			pullDown = true;
			Requester3.MicComments(handler, publishid, first_time, "1");
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			hideCommentInputView();
			pullDown = false;
			if(isHasNextPage){
				xlv_commentData.setNoMoreData(this, true);
				Requester3.MicComments(handler, publishid, last_time, "2");
			}else{
				xlv_commentData.setNoMoreData(this, false);
				handler.sendEmptyMessage(STOP_REFRESH);
			}
		}
	    
		public static class VshareDetailItem{}
		
		public static class VshareDetail extends VshareDetailItem{
			public MiShareinfo miShareinfo;
			public boolean isBurn;
		}
		
		public static class CommentItem extends VshareDetailItem{
			public DiaryDetailComment comment;
		}
		
		public void showCommentInputView(){
			if(inpRecoderView.getVisibility() == View.VISIBLE){
				hideCommentInputView();
			}else{
			// 添加评论操作点击事件请求处理
			inpRecoderView.clearView();
			cleanCmmData();
			inpRecoderView.setNoReply();
			inpRecoderView.setInputStrKey(InputStrType.COMMENT,publishid);
			inpRecoderView.setVisibility(View.VISIBLE);
			inpRecoderView.showSoftKeyBoard();
			}
		}
		
		public void hideCommentInputView(){
			inpRecoderView.clearView();
			cleanCmmData();
		}
		
		
}
