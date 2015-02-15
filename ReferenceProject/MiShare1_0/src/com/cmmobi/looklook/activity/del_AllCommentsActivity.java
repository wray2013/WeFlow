package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.AllCommentsAdapter;
import com.cmmobi.looklook.common.adapter.AllWeiboCommentsAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.diarycommentlistItem;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWComment;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWCommentsInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SinaComments;
import com.cmmobi.looklook.common.gson.WeiboResponse.TencentCommentInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TencentComments;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinComment;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.RenrenCmmLoader;
import com.cmmobi.looklook.common.utils.RenrenCmmLoader.TaskInfo;
import com.cmmobi.looklook.common.view.InputRecoderView;
import com.cmmobi.looklook.common.view.InputRecoderView.AudioRecoderBean;
import com.cmmobi.looklook.common.view.InputRecoderView.OnSendListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.prompt.Prompt;
public class del_AllCommentsActivity extends TitleRootFragmentActivity implements OnRefreshListener2<ListView>,
										OnItemClickListener,OnScrollListener,OnSendListener{
	private final String TAG = "AllCommentsActivity";
	
	private ListView listView;
	private BaseAdapter acadapter;
	private ArrayList<diarycommentlistItem> commentList  = new ArrayList<diarycommentlistItem>();
	
	private PullToRefreshListView pullRefreshLView;
	private InputRecoderView inpRecoderView;
	
	private DialogFragment dialogFragment;
	private Object currCmmsitem;
	private diarycommentlistItem currDelCmmsitem;
	
	public static final String REQ_PARAM_SHARE_STATUS = "share_status";
	public static final String REQ_PARAM_PUBLIC       = "publishid";
	public static final String REQ_PARAM_WEIBOID      = "weiboid";
	public static final String REQ_PARAM_MICUSER = "mic_user";
	public static final String REQ_PARAM_DIARY_USERID = "diary_userid";
	
	private boolean addFromStackTop = true;
	
	private String publishid;
	
	private String share_status;
	
	private String weiboid;
	
	public String mic_userid;
	public String diary_userid; //微享所有者ID
	private SinaComments mSinaComments = null;
	private RWCommentsInfo mRenrenComments = null;
	private TencentComments mTencentComments = null;
	
	private String first_comment_time = "";
	private String last_comment_time = "";

	
	@Override
	public int subContentViewId() {
		return R.layout.all_comments;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("全部评论");
		setLeftLong2Home();
		hideRightButton();
		
		publishid    = getIntent().getStringExtra(REQ_PARAM_PUBLIC);
		share_status = getIntent().getStringExtra(REQ_PARAM_SHARE_STATUS);
		weiboid      = getIntent().getStringExtra(REQ_PARAM_WEIBOID);
		mic_userid = getIntent().getStringExtra(REQ_PARAM_MICUSER);
		diary_userid = getIntent().getStringExtra(REQ_PARAM_DIARY_USERID);
		
		pullRefreshLView = (PullToRefreshListView)this.findViewById(R.id.ll_all_comment_List);
		pullRefreshLView.setShowIndicator(false);
		pullRefreshLView.setOnRefreshListener(this);
		listView = pullRefreshLView.getRefreshableView();
		
		inpRecoderView = (InputRecoderView) this.findViewById(R.id.inp_recoder_view);
		FrameLayout flyTranslucent = (FrameLayout) this.findViewById(R.id.fl_translucent_layout);
		inpRecoderView.setOnSendListener(this);
		
		// 这样加入数据重栈底加入
		addFromStackTop = false;
				
		pullRefreshLView.setRefreshing();
		ZDialog.show(R.layout.progressdialog, true, true, del_AllCommentsActivity.this);
		if("1".equals(share_status))
		{
			// 新浪微博
//			pullRefreshLView.setMode(Mode.DISABLED);
			acadapter = new AllWeiboCommentsAdapter(this, mSinaComments);
			WeiboRequester.GetSinaComment(this, handler, weiboid, "100", "1");
		}
		else if("2".equals(share_status))
		{
			// 人人微博
//			pullRefreshLView.setMode(Mode.DISABLED);
			WeiboRequester.GetRenrenComment(this, handler, weiboid, "100", "1");
		}
		else if("6".equals(share_status))
		{
			// 腾讯微博
//			pullRefreshLView.setMode(Mode.DISABLED);
			acadapter = new AllWeiboCommentsAdapter(this, mTencentComments);
			WeiboRequester.GetTencentComment(this, handler, weiboid, "100", "1");
		}
		else
		{
			acadapter = new AllCommentsAdapter(this, commentList, mic_userid);
			listView.setAdapter(acadapter);
			listView.setOnItemClickListener(this);
			listView.setOnScrollListener(this);
			Requester3.diaryCommentList(handler, "", "1", publishid);
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
		case Requester3.RESPONSE_TYPE_DIARY_COMMENTLIST:
			ZDialog.dismiss();
			pullRefreshLView.onRefreshComplete();
			
			GsonResponse3.diaryCommentListResponse resp = (GsonResponse3.diaryCommentListResponse)msg.obj;
			
			if(resp != null){
				
				if (resp.status.equals("0") ) {
					
					updataFirstLastTime(resp.first_comment_time, resp.last_comment_time, addFromStackTop);
					
					if(addFromStackTop){
						for (int i = resp.commentsr.length -1; i >= 0; i--) {
							commentList.add(0,resp.commentsr[i]);
						}
					}else{
						for (int i = 0; i < resp.commentsr.length; i++) {
							commentList.add(resp.commentsr[i]);
						}
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
//						String publishid = "";
//						String commenttype = "2";
//						String replynickname = "";
						
						// 回复
						if(currCmmsitem != null ){
							diarycommentlistItem comment = (diarycommentlistItem) currCmmsitem;
							diaryID = comment.diaryid;
//							replynickname = comment.nickname;
							publishid = comment.publishid;
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
		case WeiboRequester.SINA_INTERFACE_GET_COMMENT:
			if(msg.obj != null)
			{
				mSinaComments  = (SinaComments) msg.obj;
			}
			else
			{
				Log.v(TAG, "obj == null");
			}
			acadapter = new AllWeiboCommentsAdapter(this, mSinaComments);
			listView.setAdapter(acadapter);
			listView.setOnItemClickListener(this);
			listView.setOnScrollListener(this);
			acadapter.notifyDataSetChanged();
			ZDialog.dismiss();
			break;
		case WeiboRequester.RENREN_INTERFACE_GET_COMMENT:
			if(msg.obj != null)
			{
				mRenrenComments = (RWCommentsInfo) msg.obj;
				LinkedList<RWCommentsInfo> lr = new LinkedList<RWCommentsInfo>();
				lr.add(mRenrenComments);
				RenrenCmmLoader.getInstance().loadCmmInfo(this, handler, lr);
			}
			else
			{
				Log.v(TAG, "obj == null");
			}

			break;
		case RenrenCmmLoader.HANDLER_RENREN_USER:
			Map<Long, TaskInfo> map = new HashMap<Long, TaskInfo>();
			if(msg.obj != null)
			{
				map = (Map<Long, TaskInfo>) msg.obj;
			}
			acadapter = new AllWeiboCommentsAdapter(this, mRenrenComments, map);
			listView.setAdapter(acadapter);
//			listView.setOnItemClickListener(this);
			listView.setOnScrollListener(this);
			acadapter.notifyDataSetChanged();
			ZDialog.dismiss();
			break;
		case WeiboRequester.TENCENT_INTERFACE_GET_COMMENT:
			if(msg.obj != null)
			{
				mTencentComments = (TencentComments) msg.obj;
			}
			else
			{
				Log.v(TAG, "obj == null");
			}
			acadapter = new AllWeiboCommentsAdapter(this, mTencentComments);
			listView.setAdapter(acadapter);
			listView.setOnItemClickListener(this);
			listView.setOnScrollListener(this);
			acadapter.notifyDataSetChanged();
			ZDialog.dismiss();
			break;
		case WeiboRequester.SINA_INTERFACE_REPLY_COMMENT:
		case WeiboRequester.RENREN_INTERFACE_REPLY_COMMENT:
		case WeiboRequester.TENCENT_INTERFACE_REPLY_COMMENT:
			ZDialog.dismiss();
			boolean statusOK = (Boolean) (msg.obj);
			if(statusOK){
				Prompt.Alert("评论成功");
			}else{
				Prompt.Alert("评论失败！");
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
			
			inpRecoderView.clearView();
			
			// 清除直接回复
			currCmmsitem = v.getTag();
			
			//设置recoder按钮状态
			inpRecoderView.setRecordBtnEnabled(currCmmsitem instanceof diarycommentlistItem);
			inpRecoderView.setVisibility(View.VISIBLE);
			
			break;
		case R.id.btn_comment_delete:
			if(dialogFragment!=null ){
				dialogFragment.dismiss();
			}
			
			diarycommentlistItem deltag =  (diarycommentlistItem) v.getTag();
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
		dialogFragment = CommentDialogFragment.newInstance(this,parent.getItemAtPosition(position), diary_userid);
		dialogFragment.show(this.getSupportFragmentManager(), "dialog");
	}
	
	private AudioRecoderBean mCurrSendBean;
	
	@Override
	public void onSend(AudioRecoderBean bean) {
		
		mCurrSendBean = bean;
		// 直接回复
		if(currCmmsitem != null){
			ZDialog.show(R.layout.progressdialog, true, true, this);
			if(currCmmsitem instanceof sinComment)
			{
				sinComment comment = (sinComment) currCmmsitem;
				WeiboRequester.replySinaComment(this, handler, weiboid, String.valueOf(comment.id), bean.content);
			}
			else if(currCmmsitem instanceof RWComment)
			{
				RWComment comment = (RWComment) currCmmsitem;
				WeiboRequester.replyRenrenComment(this, handler, weiboid, String.valueOf(comment.id), bean.content);
			}
			else if(currCmmsitem instanceof TencentCommentInfo)
			{
				TencentCommentInfo comment = (TencentCommentInfo) currCmmsitem;
				WeiboRequester.replyTencentComment(this, handler, weiboid, comment.id, bean.content);
			}
			else
			{
				diarycommentlistItem comment = (diarycommentlistItem) currCmmsitem;
				String publishid = comment.publishid;
				String diaryid = comment.diaryid;
				
				// 统计
				int i = 0;
				if("1".equals(bean.commenttype))
				{
					i = 2;
				}
				else if("2".equals(bean.commenttype))
				{
					i = 1;
				}
				if("3".equals(bean.commenttype))
				{
					i = 3;
				}
				
				HashMap<String, String> ids= new HashMap<String, String>();
				ids.put("label1", diaryid);
				ids.put("label2", publishid);
				ids.put("label3", i+"");
				
				if(!TextUtils.isEmpty(mic_userid)){
					//CmmobiClickAgentWrapper.onEvent(this, "sh_reply", ids);	
					Requester3.comment(handler, bean.content, comment.commentid , "2" ,publishid,bean.commenttype,bean.commentuuid, diaryid, "2");
				}else{
					//CmmobiClickAgentWrapper.onEvent(this, "content_reply", ids);	
					Requester3.comment(handler, bean.content, comment.commentid , "2" ,publishid,bean.commenttype,bean.commentuuid, diaryid, "1");
				}
				
				
			}
		}else{
			ZLog.e("send error。。。。");
		}
		
	}
	
	
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				addFromStackTop = true;
				pullRefreshLView.setRefreshing();
				Requester3.diaryCommentList(handler, first_comment_time, "1", publishid);
			}
		});
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				addFromStackTop = false;
				pullRefreshLView.setRefreshing();
				Requester3.diaryCommentList(handler, last_comment_time, "2", publishid);
			}
		});
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
		private Object data;
		private String diary_userid;
		public static CommentDialogFragment newInstance(OnClickListener listener,Object data, String diary_userid){
			if(dialog == null){
				dialog = new CommentDialogFragment();
			}
			dialog.listener = listener;
			dialog.data = data;
			dialog.diary_userid = diary_userid;
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
    		
    		boolean toReply = true;
    		boolean replyShowDel = true;
    		if(data instanceof diarycommentlistItem)
			{
    			diarycommentlistItem item = (diarycommentlistItem) data;
				
				String myUserid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID();
				
				// 自己评论的
				if(!TextUtils.isEmpty(myUserid)&&myUserid.equals(item.userid)){
					toReply = false;
				}
				// 可以回复，在判断是否能删除
				
				if(toReply){
					try {
						if(!TextUtils.isEmpty(myUserid) && (myUserid.equals(item.userid) || myUserid.equals(diary_userid))){
							replyShowDel = true;
						}else{
							replyShowDel = false;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
    		else
    		{
    			replyShowDel = false;
    		}
    		
    		reply = (Button) v.findViewById(R.id.btn_comment_reply);
    		if(toReply)
    		{
    			reply.setOnClickListener(listener);
    			reply.setTag(data);
    		}
    		else
    		{
    			reply.setVisibility(View.GONE);
    		}
    		
    		delete = (Button) v.findViewById(R.id.btn_comment_delete);
    		if(replyShowDel)
    		{
	    		delete.setOnClickListener(listener);
	    		delete.setTag(data);
    		}
    		else
    		{
    			delete.setVisibility(View.GONE);
    		}
    		
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

	
	
	/**
	 * 更新首记录尾记录时间
	 * @param first_time
	 * @param last_time
	 * @param addFromStackTop
	 */
	private void updataFirstLastTime(String first_time,String last_time,boolean addFromStackTop){
		// init
		if(TextUtils.isEmpty(this.first_comment_time)){
			this.first_comment_time = first_time;
		}
		// init
		if(TextUtils.isEmpty(this.last_comment_time)){
			this.last_comment_time = last_time;
		}
		// updata
		if(addFromStackTop){
			if(!TextUtils.isEmpty(first_time)){
				this.first_comment_time = first_time;
			}
		}else{
			if(!TextUtils.isEmpty(last_time)){
				this.last_comment_time = last_time;
			}
		}
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
        if (v != null/* && (v instanceof EditText)*/) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
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
			inpRecoderView.setVisibility(View.GONE);
	    	return true;
    	}
    	return false;
    }
}
