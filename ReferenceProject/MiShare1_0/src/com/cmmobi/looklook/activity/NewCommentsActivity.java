package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.AllCommentsAdapter;
import com.cmmobi.looklook.common.adapter.CommentsContentAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyCommentListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.diarycommentlistItem;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.RemoteManager;
import com.cmmobi.looklook.common.view.InputRecoderView;
import com.cmmobi.looklook.common.view.InputRecoderView.AudioRecoderBean;
import com.cmmobi.looklook.common.view.InputRecoderView.OnSendListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.fragment.FragmentHelper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.prompt.Prompt;
public class NewCommentsActivity extends TitleRootFragmentActivity implements OnRefreshListener2<ListView>,
										OnItemClickListener,OnScrollListener,OnSendListener{

	private ArrayList<MyCommentListItem> commentList  = new ArrayList<MyCommentListItem>();;
	private CommentsContentAdapter acadapter;
	private ListView listView;
	
	private PullToRefreshListView pullRefreshLView;
	private InputRecoderView inpRecoderView;
	
	private DialogFragment dialogFragment;
	
	private MyCommentListItem currCmmsitem;
	private MyCommentListItem currDelCmmsitem;
	
	public static final String REQ_PARAM_SHARE_STATUS = "share_status";
	public static final String REQ_PARAM_PUBLIC       = "publishid";
	public static final String REQ_PARAM_WEIBOID      = "weiboid";
	
	/**保险箱评论参数*/
	public static final String REQ_PARAM_ENCRYPT      = "encrypt";
	
	private AccountInfo aInfo;
	
	private static final String commentType = "1";   // 1 收到的评论 
	
	@Override
	public int subContentViewId() {
		return R.layout.all_comments;
	}
	private boolean isEncrypt = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("新评论");
		hideRightButton();
		
		isEncrypt = getIntent().getBooleanExtra(REQ_PARAM_ENCRYPT, false);
		
		pullRefreshLView = (PullToRefreshListView)this.findViewById(R.id.ll_all_comment_List);
		pullRefreshLView.setShowIndicator(false);
		pullRefreshLView.setMode(Mode.PULL_FROM_START);
		pullRefreshLView.setOnRefreshListener(this);
		
		listView = pullRefreshLView.getRefreshableView();
		
		inpRecoderView = (InputRecoderView) this.findViewById(R.id.inp_recoder_view);
		FrameLayout flyTranslucent = (FrameLayout) this.findViewById(R.id.fl_translucent_layout);
		inpRecoderView.setOnSendListener(this);
		
		acadapter = new CommentsContentAdapter(this, commentList,commentType);;
		listView.setAdapter(acadapter);
		listView.setOnItemClickListener(this);
		listView.setOnScrollListener(this);
		
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		aInfo = AccountInfo.getInstance(userID);		
		
//		pullRefreshLView.setRefreshing();
		ZDialog.show(R.layout.progressdialog, true, true, NewCommentsActivity.this);
		if(isEncrypt){
			Requester3.MyCommentList(handler, "3", aInfo.accept_first_safebox_comment_time, "1","50","1");
		}else{
			Requester3.MyCommentList(handler, "3", aInfo.accept_first_comment_time, "1","50");
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		inpRecoderView.mRegisterReceiver();
	}
	@Override
	protected void onStop() {
		super.onStop();
		inpRecoderView.mUnRegisterReceiver();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_MY_COMMENTLIST:
			ZDialog.dismiss();
			pullRefreshLView.onRefreshComplete();
			
			GsonResponse3.MyCommentListResponse resp = (GsonResponse3.MyCommentListResponse)msg.obj;
			
			if(resp != null){
				
				if ("0".equals(resp.status) ) {
					
					// 更新角标
					if(isEncrypt){
						// 保险箱新评论
						aInfo.privateMsgManger.cleanSafeboxCommentNum();
					}else{
						aInfo.privateMsgManger.cleanCommentNum();
					}
					
					Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
					LocalBroadcastManager.getInstance(NewCommentsActivity.this).sendBroadcast(msgIntent);
					
					// 缓存数据到accountinfo
					if( resp.comments.length > 0 ){
						if(isEncrypt){
							// 保险箱新评论
							aInfo.accept_first_safebox_comment_time = resp.first_comment_time;
							aInfo.myAcceptSafeboxComments.clear();
							aInfo.myAcceptSafeboxComments.addAll(Arrays.asList(resp.comments));
							aInfo.privateMsgManger.commentid_safebox = resp.comments[0].commentid;
						}else{
							aInfo.accept_first_comment_time = resp.first_comment_time;
							aInfo.myAcceptComments.clear();
							aInfo.myAcceptComments.addAll(Arrays.asList(resp.comments));
							aInfo.privateMsgManger.commentid = resp.comments[0].commentid;
						}
						// 新评论更新评论时间给msg进程
						RemoteManager.getInstance(this).CallService(aInfo);
						saveAccount();
					}
					
					for (int i = resp.comments.length -1; i >= 0; i--) {
						commentList.add(0,resp.comments[i]);
					}
					
				}else{
					Prompt.Alert("服务器返回错误，错误码：" + resp.status );
				}
				acadapter.notifyDataSetChanged();
			}
			
			break;
		case Requester3.RESPONSE_TYPE_DELETE_COMMENT:
			ZDialog.dismiss();
			
			GsonResponse3.deleteCommentResponse delCmmResp = (GsonResponse3.deleteCommentResponse)msg.obj;
			if(delCmmResp!=null){
				if("0".equals(delCmmResp.status)){
					Prompt.Alert("删除评论成功");
					try {
						commentList.remove(currDelCmmsitem);
						currDelCmmsitem = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Prompt.Alert("服务器返回错误，错误码：" + delCmmResp.status);
				}
			}
			acadapter.notifyDataSetChanged();
			break;
		case Requester3.RESPONSE_TYPE_COMMENT:

			ZDialog.dismiss();
			
			GsonResponse3.commentResponse cmmresp = (GsonResponse3.commentResponse)msg.obj;
			if(cmmresp!=null){
				if("0".equals(cmmresp.status)){
					
					Prompt.Alert("评论成功");
					
					if(mCurrSendBean != null ){
						
						String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
						String diaryID = "";
						String publishid = "";
						String commenttype = "2";
						String replynickname = "";
						
						// 回复
						if(currCmmsitem != null ){
							diaryID = currCmmsitem.diaryid;
							replynickname = currCmmsitem.nickname;
							publishid = currCmmsitem.publishid;
						}
						
						// 语音加文字
						if("2".equals(mCurrSendBean.commenttype) ||  
						   "3".equals(mCurrSendBean.commenttype) ){
							// 上传语音
							try {
								NetworkTaskInfo tt = new NetworkTaskInfo(userID, diaryID, cmmresp.commentid, 
										Environment.getExternalStorageDirectory() + mCurrSendBean.localFilePath,
										cmmresp.audiopath, "3", "2");
								
								UploadNetworkTask uploadtask = new UploadNetworkTask(tt);// 创建上传/下载任务
								uploadtask.start();
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						mCurrSendBean = null;
						inpRecoderView.clearView();
						
					}
					
				}else{
					Prompt.Alert("服务器返回错误，错误码：" + cmmresp.status);
				}
			}
			
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			this.finish();
			break;
		case R.id.btn_comment_reply:
			if(dialogFragment!=null ){
				dialogFragment.dismiss();
			}
			
			currCmmsitem =  (MyCommentListItem) v.getTag();
			inpRecoderView.setVisibility(View.VISIBLE);
			
			break;
		case R.id.btn_comment_delete:
			if(dialogFragment!=null ){
				dialogFragment.dismiss();
			}
			
			MyCommentListItem deltag =  (MyCommentListItem) v.getTag();
			currDelCmmsitem = deltag;
			
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester3.deleteComment(handler,deltag.publishid, deltag.commentid,deltag.commentuuid);
			
			
			break;
			
		default:
			break;
		}
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		try {
			MyCommentListItem data = (MyCommentListItem) parent.getItemAtPosition(position);
			
			ArrayList<MyDiaryList> diaryList = new ArrayList<MyDiaryList>();
			
			MyDiaryList myDiaryList = DiaryManager.getInstance().findDiaryGroupByUUID(data.diaryids[0].diaryuuid);
			if(myDiaryList!=null){
				diaryList.add(myDiaryList);
				DiaryManager.getInstance().setDetailDiaryList(diaryList, 1);
			}else{
				
				ArrayList<MyDiary> diaries = new ArrayList<MyDiary>();
				diaries.addAll(Arrays.asList(data.diaries));
				
				diaryList.addAll(Arrays.asList(data.diaryids));
				
				DiaryManager.getInstance().setDetailDiaryList(diaryList, 2);
				DiaryManager.getInstance().setDetailDiary(diaries);
			}
			
			//  日记已经全部生成完成，后面做界面跳转相关操作。
			Intent intent = new Intent(this, DiaryPreviewActivity.class);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, data.diaryid);
			intent.putExtra("commentid", data.commentid);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private AudioRecoderBean mCurrSendBean;
	
	@Override
	public void onSend(AudioRecoderBean bean) {
		
		mCurrSendBean = bean;
		// 直接回复
		if(currCmmsitem != null){
			ZDialog.show(R.layout.progressdialog, true, true, this);
			String publishid = currCmmsitem.publishid;
			String diaryid = currCmmsitem.diaryid;
			Requester3.comment(handler, bean.content, currCmmsitem.commentid , "2" ,publishid,bean.commenttype,bean.commentuuid, diaryid, "1");
		}else{
			ZLog.e("send error。。。。");
		}
		
	}
	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:  // 停止滚动
			break;
		case OnScrollListener.SCROLL_STATE_FLING:  //  用力滑动--开始滚动
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //滚动ing
			if(inpRecoderView.isShown()){
				currCmmsitem = null;
				inpRecoderView.clearView();
			}
			break;
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	
	public static class CommentDialogFragment extends DialogFragment {

		private static CommentDialogFragment dialog;
		private OnClickListener listener;
		private MyCommentListItem data;
		
		public static CommentDialogFragment newInstance(OnClickListener listener,MyCommentListItem data){
			if(dialog == null){
				dialog = new CommentDialogFragment();
			}
			dialog.listener = listener;
			dialog.data = data;
			return dialog;
		}
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	
        	final Dialog d = new Dialog(getActivity(), R.style.dialog_theme); 
        	
        	LayoutInflater inflater = LayoutInflater.from(getActivity());
        	
        	View v = null;
        	Button delete;
        	Button cancel;
        	Button reply;
        	
    		v = inflater.inflate(R.layout.dialogfragment_comment_operate_three, null);
    		reply = (Button) v.findViewById(R.id.btn_comment_reply);
    		reply.setOnClickListener(listener);
    		reply.setTag(data);
    		
    		delete = (Button) v.findViewById(R.id.btn_comment_delete);
    		delete.setOnClickListener(listener);
    		delete.setTag(data);
    		
//			delete.setVisibility(View.GONE);
//			reply.setBackgroundResource(R.drawable.btn_menu_one);
    		
    		cancel = (Button) v.findViewById(R.id.btn_comment_cancel);
    		cancel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					d.dismiss();
				}
			});
        	
        	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        	d.addContentView(v, params);
        	
    		Window window = d.getWindow();
    		window.setGravity(Gravity.BOTTOM);
    		android.view.WindowManager.LayoutParams p = window.getAttributes();
			p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
//			p.alpha = 0.9f; // 增加一点按钮透明
			window.setAttributes(p);      //设置生效
    		return d;
        }
    }


	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pullRefreshLView.setRefreshing();
		if(isEncrypt){
			Requester3.MyCommentList(handler, "3", aInfo.accept_first_safebox_comment_time, "1","50","1");
		}else{
			Requester3.MyCommentList(handler, "3", aInfo.accept_first_comment_time, "1","50");
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}


}
