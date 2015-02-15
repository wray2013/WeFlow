package com.cmmobi.looklook.activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.StorageListAdapter;
import com.cmmobi.looklook.common.adapter.StorageListAdapter.ViewHolder;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareInfo;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareSNSTrace;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MediaMapping;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 存储管理
 * 
 * @author youtian
 * 
 */
public class SettingStorageManagerActivity extends ZActivity implements OnRefreshListener2<ListView>{

	private ImageView iv_back; //返回键
	
	
	private ImageButton ibtn_localdata; //本地
	private RelativeLayout rl_clouddata; //云端
	private ImageView iv_clouddata;
	private TextView tv_cloudsize;
	
	private LinearLayout ll_localdata; //本地视图
	private RelativeLayout rl_notuploaddata; //未上传
	private TextView tv_notuploaddatasize; //未上传数据大小 
	private RelativeLayout rl_synchronizeddata; //已同步
	private TextView tv_synchronizeddatasize; //已同步数据大小
	private RelativeLayout rl_browsecachedata; //浏览缓存
	private TextView tv_browsecachedatasize; //浏览缓存数据大小
	private RelativeLayout rl_primsg; //私信
	private TextView tv_primsgsize; //私信数据大小 
	private RelativeLayout rl_favoritesdata; //收藏
	private TextView tv_favoritesdatasize; //收藏数据大小
	private Button btn_intelligentcleanup; //智能清理
	
	private LinearLayout ll_clouddata; //云端视图
	private PullToRefreshListView xlv_clouddata;
	private ListView lv_clouddata;
	private ArrayList<Map<String, String>> clouddata;
	private StorageListAdapter adapter;
	float x , y , upx, upy; 


	private PopupWindow pop_synchronized;	
	private PopupWindow pop_browsecache;
	private PopupWindow pop_primsg;
	private PopupWindow pop_favorites;

	private LayoutInflater inflater;

	private Display display ;

	private int state = 0; //0,第一次;1,上拉刷新;2,下拉更多
	private String first_diary_time;
	private String last_diary_time;

	private Context context;
	private AccountInfo accountInfo;
	public MediaMapping mmp;
	
	private LoginSettingManager lsm;
	private int index=0;
	private DiaryManager diaryManager;
	private String diaryId;
	private int currentposition;
	private MyDiary myDiary;
	
	private static String show_height;
	private static String show_width;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_storagemanager);
		
		context = this;
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		
		rl_clouddata = (RelativeLayout) findViewById(R.id.rl_clouddata);
		rl_clouddata.setOnClickListener(this);
		iv_clouddata = (ImageView) findViewById(R.id.iv_clouddata);
		tv_cloudsize = (TextView) findViewById(R.id.tv_cloudsize);
		tv_cloudsize.setText("");
		Requester2.getCloudSize(handler);
		ibtn_localdata = (ImageButton) findViewById(R.id.ibtn_localdata);
		ibtn_localdata.setOnClickListener(this);
		
		ll_localdata = (LinearLayout) findViewById(R.id.ll_localdata);
		ll_clouddata = (LinearLayout) findViewById(R.id.ll_clouddata);
		
		rl_notuploaddata = (RelativeLayout) findViewById(R.id.rl_notuploaddata);
		rl_notuploaddata.setOnClickListener(this);
		tv_notuploaddatasize = (TextView) findViewById(R.id.tv_notuploaddatasize);
		
		rl_synchronizeddata = (RelativeLayout) findViewById(R.id.rl_synchronizeddata);
		rl_synchronizeddata.setOnClickListener(this);
		tv_synchronizeddatasize = (TextView) findViewById(R.id.tv_synchronizeddatasize);
		
		rl_browsecachedata = (RelativeLayout) findViewById(R.id.rl_browsecachedata);
		rl_browsecachedata.setOnClickListener(this);
		tv_browsecachedatasize = (TextView) findViewById(R.id.tv_browsecachedatasize);
		
		rl_primsg = (RelativeLayout) findViewById(R.id.rl_primsg);
		rl_primsg.setOnClickListener(this);
		tv_primsgsize = (TextView) findViewById(R.id.tv_privmsgsize);
		
		rl_favoritesdata = (RelativeLayout) findViewById(R.id.rl_favoritesdata);
		rl_favoritesdata.setOnClickListener(this);
		tv_favoritesdatasize = (TextView) findViewById(R.id.tv_favoritesdatasize);
		
		btn_intelligentcleanup = (Button) findViewById(R.id.btn_intelligentcleanup);
		btn_intelligentcleanup.setOnClickListener(this);
		
		diaryManager = DiaryManager.getInstance();
		xlv_clouddata = (PullToRefreshListView) findViewById(R.id.xlv_clouddata);
		xlv_clouddata.setShowIndicator(false);
		xlv_clouddata.setOnRefreshListener(this);

		lv_clouddata = xlv_clouddata.getRefreshableView();
		clouddata = new ArrayList<Map<String, String>>();
		adapter = new StorageListAdapter(this, clouddata);
		lv_clouddata.setAdapter(adapter);	
		
		lv_clouddata.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				final List<Map<String, String>> list = adapter.getDatas();
				currentposition = arg2 -1 ;
				diaryId = list.get(currentposition).get("diaryID");
				String msg;
				if(diaryManager.findLocalDiary(diaryId).sync_status == 4){
					msg = "本地无此内容，确认删除？";
				}else{
					msg = "确认删除？";
				}
				myDiary = null;
				if(diaryManager.getShareStatusByDiaryId(diaryId)){
					myDiary = diaryManager.findLocalDiary(diaryId);
				}
				Prompt.Dialog(SettingStorageManagerActivity.this, true, "提示", msg,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub			
								ZDialog.show(R.layout.progressdialog, false, true, context);
								Requester2.deleteDiary(handler, diaryId);
							}

						});
				return true;
			}
		});
		/*lv_clouddata.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				  switch (event.getAction()) {
				   case MotionEvent.ACTION_DOWN:
				    // 获得第一个点的x坐标
				    x = event.getX(0);
				    y = event.getY(0);
				    break;
				   case MotionEvent.ACTION_UP:
				    //删除item
					   upx = event.getX();
					   upy = event.getY();
					   if(Math.abs(upx - x) > 20 && Math.abs(upy - y) < 20){
						   int position1 = ((ListView) v).pointToPosition((int) x, (int) y);   
						   int position2 = ((ListView) v).pointToPosition((int) upx,(int) upy); 
						   int firstVisiblePostion = lv_clouddata.getFirstVisiblePosition();
						   if(position1 == position2){
							 View view = lv_clouddata.getChildAt(position1 - firstVisiblePostion);
							 if(view != null ){
								StorageListAdapter.ViewHolder holder = (ViewHolder) view.getTag();
								if(holder.btn_del.getVisibility() == View.VISIBLE){
									holder.btn_del.setVisibility(View.GONE);
								}else{
									holder.btn_del.setVisibility(View.VISIBLE);
								}
							 }
						   }
					   }
				    break;
				    default:{
				    	
				    }
				}
				return false;
			}
		});
		*/
		accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this)
				.getUID());
		if(accountInfo != null){
			mmp = accountInfo.mediamapping;
			lsm = accountInfo.setmanager;
		}
		inflater = LayoutInflater.from(this);
		
		initPopSynchronized(); //初始化已同步popwindow
		initPopBrowsecache(); //初始化浏览缓存popwindow
		initPopPrimsg(); //初始化私信popwindow
		initPopFavorites(); //初始化收藏popwindow
		

		display = getWindowManager().getDefaultDisplay(); 
        refreshDataSize();

	}
	
	//已同步popwindow
	private void initPopSynchronized(){	
		View view = inflater.inflate(R.layout.activity_setting_storage_pop_synchronized,
				null);
		pop_synchronized = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pop_synchronized.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		view.findViewById(R.id.btn_synchronized_cleanup).setOnClickListener(this);
		view.findViewById(R.id.btn_synchronized_cleanupall).setOnClickListener(this);
		view.findViewById(R.id.btn_synchronized_cancel).setOnClickListener(this);	
	}
	
	//浏览缓存popwindow
	private void initPopBrowsecache(){
		View view = inflater.inflate(
				R.layout.activity_setting_storage_pop_browsecache, null);
		pop_browsecache = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pop_browsecache.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		view.findViewById(R.id.btn_browsecache_cleanupall).setOnClickListener(this);	
		view.findViewById(R.id.btn_browsecache_cancel).setOnClickListener(this);
	}
	
	//私信popwindow
	private void initPopPrimsg(){
		View view = inflater.inflate(
				R.layout.activity_setting_storage_pop_primsg, null);
		pop_primsg = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pop_primsg.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		view.findViewById(R.id.btn_primsg_cleanvideo).setOnClickListener(this);
		view.findViewById(R.id.btn_primsg_cleanpic).setOnClickListener(this);
		view.findViewById(R.id.btn_primsg_cleanaudio).setOnClickListener(this);
		view.findViewById(R.id.btn_primsg_cleanupall).setOnClickListener(this);
		view.findViewById(R.id.btn_primsg_cancel).setOnClickListener(this);
	}
	
	//收藏popwindow
	private void initPopFavorites(){
		View view = inflater.inflate(
				R.layout.activity_setting_storage_pop_favorites, null);
		pop_favorites = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		pop_favorites.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		view.findViewById(R.id.btn_favorites_cleanupall).setOnClickListener(this);
		view.findViewById(R.id.btn_favorites_cancel).setOnClickListener(this);
	}
	
	private void refreshDataSize() {
		tv_notuploaddatasize.setText(getSize(DiaryManager.getInstance().getUnSyncDiaryTotalSize(true))); //未同步
		tv_synchronizeddatasize.setText(getSize(DiaryManager.getInstance().getSyncDiaryTotalSize(true))); //已同步
		tv_browsecachedatasize.setText(getSize(mmp.getTotalSizeBySrc(1))); //浏览缓存
		tv_primsgsize.setText(getSize(mmp.getTotalSizeBySrc(4))); //私信数据
		tv_favoritesdatasize.setText(getSize(mmp.getTotalSizeBySrc(2))); //收藏数据
	}

	public static String getSize(long size){
		String finalsize = null;
		if(size/1024/1024/1024 >= 1){
			finalsize = String.format("%.2f", ((double)size)/1024/1024/1024) + "G";
		}else if(size/1024/1024 >= 1){	
			finalsize = String.format("%.2f", ((double)size)/1024/1024) + "M";
		}else{
			finalsize = size/1024 + "K";
		}
		return finalsize;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		refreshDataSize();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.onBackPressed();
			break;
		case R.id.ibtn_localdata:
			//press = false;
			ll_localdata.setVisibility(View.VISIBLE);
			ll_clouddata.setVisibility(View.GONE);
			ibtn_localdata.setBackgroundResource(R.drawable.cunchuguanli_qiehuan_xuanzhong);
			rl_clouddata.setBackgroundDrawable(null);
			iv_clouddata.setImageResource(R.drawable.cunchuguanli_qiehuan_yunduan);
			ibtn_localdata.setImageResource(R.drawable.cunchuguanli_qiehuan_bendi_2);
			refreshDataSize();
			break;
		case R.id.rl_clouddata:
			adapter.clearAdapter();
			ll_localdata.setVisibility(View.GONE);
			ll_clouddata.setVisibility(View.VISIBLE);
			rl_clouddata.setBackgroundResource(R.drawable.cunchuguanli_qiehuan_xuanzhong);
			ibtn_localdata.setBackgroundDrawable(null);
			iv_clouddata.setImageResource(R.drawable.cunchuguanli_qiehuan_yunduan_2);
			ibtn_localdata.setImageResource(R.drawable.cunchuguanli_qiehuan_bendi);
			ZDialog.show(R.layout.progressdialog, false, true, this);
			state = 0;
			Requester2.homePage(getHandler(), ActiveAccount.getInstance(this).getUID(), "", "", adapter.getImgwidth() + "","", display.getWidth()+"", display.getHeight()+"");
			break;
		case R.id.rl_notuploaddata:// 未上传
			Intent intent = new Intent(this, SettingStorageManagerNotSynchronizedActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_synchronizeddata: //已同步
			pop_synchronized.showAtLocation(findViewById(R.id.fl_content),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.rl_browsecachedata:// 浏览缓存
			pop_browsecache.showAtLocation(findViewById(R.id.fl_content),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.rl_primsg:
			pop_primsg.showAtLocation(findViewById(R.id.fl_content),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.rl_favoritesdata:
			pop_favorites.showAtLocation(findViewById(R.id.fl_content),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.btn_synchronized_cleanup:// 手动清理
			if(pop_synchronized.isShowing()){
				pop_synchronized.dismiss();
			}
			Intent i = new Intent(this, SettingStorageManagerSynchronizedActivity.class);
			startActivity(i);
			break;
		case R.id.btn_synchronized_cleanupall:// 清空同步记录
			if(pop_synchronized.isShowing()){
				pop_synchronized.dismiss();
			}
			CleanTask task = new CleanTask();  
			task.execute(R.id.btn_synchronized_cleanupall);
			break;
		case R.id.btn_synchronized_cancel:// 取消
			if(pop_synchronized.isShowing()){
				pop_synchronized.dismiss();
			}
			break;
		case R.id.btn_intelligentcleanup:// 智能清理
			CleanTask taskauto = new CleanTask();  
			taskauto.execute(R.id.btn_intelligentcleanup);
			break;
		case R.id.btn_browsecache_cleanupall: //清空浏览缓存
			if(pop_browsecache.isShowing()){
				pop_browsecache.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mmp.cleanReadMedia(handler, -1);// HANDLER_FLAG_READ_CLEANUP
			break;
		case R.id.btn_browsecache_cancel:
			if(pop_browsecache.isShowing()){
				pop_browsecache.dismiss();
			}
			break;
		case R.id.btn_primsg_cleanvideo:// 清理视频记录
			if(pop_primsg.isShowing()){
				pop_primsg.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mmp.cleanPrivateMsgMedia(handler, 5);
			CleanTask taskvideo = new CleanTask();  
			taskvideo.execute(R.id.btn_primsg_cleanvideo);
			break;
		case R.id.btn_primsg_cleanpic:
			if(pop_primsg.isShowing()){
				pop_primsg.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mmp.cleanPrivateMsgMedia(handler, 2);
			CleanTask taskpic = new CleanTask();  
			taskpic.execute(R.id.btn_primsg_cleanpic);
			break;
		case R.id.btn_primsg_cleanaudio:
			if(pop_primsg.isShowing()){
				pop_primsg.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mmp.cleanPrivateMsgMedia(handler, 3);
			mmp.cleanPrivateMsgMedia(handler, 4);
			CleanTask taskaudio = new CleanTask();  
			taskaudio.execute(R.id.btn_primsg_cleanaudio);
			break;
		case R.id.btn_primsg_cleanupall:
			if(pop_primsg.isShowing()){
				pop_primsg.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mmp.cleanPrivateMsgMedia(handler, -1);
			CleanTask taskall = new CleanTask();  
			taskall.execute(R.id.btn_primsg_cleanupall);
			break;
		case R.id.btn_primsg_cancel:
			if(pop_primsg.isShowing()){
				pop_primsg.dismiss();
			}
			break;
		case R.id.btn_favorites_cleanupall:
			if(pop_favorites.isShowing()){
				pop_favorites.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mmp.cleanFavoriteMedia(handler, -1);// HANDLER_FLAG_FAVOR_CLEANUP
			break;
		case R.id.btn_favorites_cancel:
			if(pop_favorites.isShowing()){
				pop_favorites.dismiss();
			}
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MediaMapping.HANDLER_FLAG_READ_CLEANUP:// 清楚浏览缓存
		case MediaMapping.HANDLER_FLAG_FAVOR_CLEANUP:// 清理收藏
			ZDialog.dismiss();
			refreshDataSize();
			break;
		case MediaMapping.HANDLER_FLAG_PRIVATEMSG_CLEANUP:// 清理私信
			refreshDataSize();
			break;
		case Requester2.RESPONSE_TYPE_HOME:
			try {
				if(state==0){
					ZDialog.dismiss();
				}
				GsonResponse2.homeResponse gld = (GsonResponse2.homeResponse) msg.obj;
				if(gld != null){
				if (gld.status.equals("0")) {
					MyDiary[] myDiaries = gld.diaries;
					Map<String, String> map;
					int count = 0;
					clouddata.clear();
					if (myDiaries.length != 0) {
						for (int i = 0; i < myDiaries.length; i++) {
							if((lsm.getGesturepassword() != null && !lsm.getSafeIsOn()) && (myDiaries[i].join_safebox != null && myDiaries[i].join_safebox.equals("1"))){
								continue;
							}
							map = new HashMap<String, String>();
							String size = myDiaries[i].size;
							if(size==null || size.equals("")){
								size = "0";
							}
							int tmptype = DiaryListView.getDiaryType(myDiaries[i].attachs);// 获取日记类型
							int diarytype = getDiaryType(myDiaries[i].attachs, tmptype);
							String coverurl = getCoverImage(diarytype, myDiaries[i].attachs);
							String time = myDiaries[i].diarytimemilli;
							
							if(DateUtils.isNum(show_width) && DateUtils.isNum(show_height) && coverurl!= null){
								Log.d("SettingStorageManager", "show_width="+show_width);
								Log.d("SettingStorageManager", "show_heigh="+show_height);
								coverurl += "&width="+show_width+"&heigh="+show_height;
							}
							map.put("size", getSize(Long.parseLong(size)));
							map.put("imgurl", coverurl);
							map.put("time", getTime(Long.parseLong(time)));
							map.put("diarytype", diarytype+"");
							map.put("diaryID", myDiaries[i].diaryid);
							map.put("uuid", myDiaries[i].diaryuuid);
							map.put("type", "cloud");
						    clouddata.add(map);
						    count++;
						}
						}
						if(count != 0){
							if(state == 0){
								first_diary_time = gld.first_diary_time;// //第一条记录的时间
								last_diary_time = gld.last_diary_time;// 最后一条记录的时间
							}else if(state == 1 ){
								first_diary_time = gld.first_diary_time;// //第一条记录的时间
							}else if(state == 2){
								last_diary_time = gld.last_diary_time;// 最后一条记录的时间
							}
							if(state == 1 || state == 0){
								adapter.addPreData(clouddata);
								index = 1;
								//lv_clouddata.setSelection(1);
							}else if(state == 2){
								index = adapter.getCount();
								System.out.println("===index===" + index);
								adapter.addAfterData(clouddata);
							    //lv_clouddata.setSelection(index);
							    System.out.println("===count===" + adapter.getCount());
							}
						}else{
							if(state == 1){
								index = 1;
							}else if(state == 2){
								index = adapter.getCount();
							}
						}
				}else{
					if(state == 1){
						index = 1;
					}else if(state == 2){
						index = adapter.getCount();
					}
					if(gld.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(gld.crm_status)], null);
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}
				if (adapter.isEmpty()) {
					Prompt.Dialog(this, false, "提示", "没有相关日记", null);		
				}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
				
				if(state == 1 || state == 2){
					xlv_clouddata.onRefreshComplete();
				}
				xlv_clouddata.post(new Runnable() {

			        @Override
			        public void run() {
			            // TODO Auto-generated method stub
			        	xlv_clouddata.getRefreshableView().setSelection(index);
			        }
			    });
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester2.RESPONSE_TYPE_GETCLOUDSIZE:
			try{
			GsonResponse2.getCloudSizeResponse csres = (GsonResponse2.getCloudSizeResponse) msg.obj;
			if(csres != null){
			if (csres.status.equals("0")) {
				if(csres.cloud_size != null){
					long size = Long.parseLong(csres.cloud_size);
					tv_cloudsize.setText("(已用" + getSize(size) + ")");
				}else{
					tv_cloudsize.setText("(已用0K)");
				}
			}
			}
			}catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester2.RESPONSE_TYPE_DELETE_DIARY:
    		ZDialog.dismiss();
    		GsonResponse2.deleteDiaryResponse res = (GsonResponse2.deleteDiaryResponse) msg.obj;
			if(res != null){
				if (res.status.equals("0")) {
					diaryManager.createDiaryStructureById(diaryId, handler);
					Prompt.Alert("已成功删除");
					Requester2.getCloudSize(handler);
					adapter.getDatas().remove(currentposition);  
					adapter.notifyDataSetChanged(); 
					xlv_clouddata.post(new Runnable() {

				        @Override
				        public void run() {
				            // TODO Auto-generated method stub
				        	xlv_clouddata.getRefreshableView().setSelection(currentposition-1);
				        }
				    });
					if(myDiary != null){
					Prompt.Dialog(SettingStorageManagerActivity.this, true, "提示", "是否删除该日记的第三方分享轨迹？",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub		
									for(int i=0; i< myDiary.sns.length; i++){
										if(myDiary.sns[i].shareinfo == null){
											continue;
										}
										for(int j= 0; j < myDiary.sns[i].shareinfo.length; j++){
											String weiboType = myDiary.sns[i].shareinfo[j].snstype;
											String weiboId = myDiary.sns[i].shareinfo[j].weiboid;
											if(weiboType.equals("1"))
											{
												WeiboRequester.delSinaWeibo(SettingStorageManagerActivity.this, handler, weiboId);
											}
											else if(weiboType.equals("2"))
											{
												WeiboRequester.delRenrenWeibo(SettingStorageManagerActivity.this, handler, weiboId);
											}
											else if(weiboType.equals("6"))
											{
												WeiboRequester.delTencentWeibo(SettingStorageManagerActivity.this, handler, weiboId);
											}
										}
								}
								}

							});
					}
				}else if(res.status.equals("200600")){
					Prompt.Alert(context, Constant.CRM_STATUS[Integer.parseInt(res.crm_status)]);
				}else{
					Prompt.Alert("操作失败");
				}
			}else{
				Prompt.Alert("网络不给力");
			}
    		break;
		case WeiboRequester.SINA_INTERFACE_DEL_WEIBO:
		case WeiboRequester.RENREN_INTERFACE_DEL_WEIBO:
		case WeiboRequester.TENCENT_INTERFACE_DEL_WEIBO:
			if(msg.obj!=null){
				boolean result = (Boolean) msg.obj;
				if(result){
					Prompt.Alert("已成功删除分享");
				}else{
					Prompt.Alert("删除失败");
				}
			}else{
				Prompt.Alert("删除失败，网络不给力");
			}
			break;
		}

		return false;
	}



	
	public static String getTime(long millis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date(millis);
		return sdf.format(date);
	}

	//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
    public static int getDiaryType(diaryAttach[] attachs, int type) {
		if(null==attachs||0==attachs.length){
			return 0;
		}
		int diaryType = 0;
    	switch(type) {
		case 0x10000000://主体 视频
		case 0x10000100://主体 视频+辅 音频
		case 0x10000101://主体 视频+辅 音频+文字
		case 0x10000001://主体 视频+文字
			diaryType = 5;
			//url = getCoverImage(1, attachs);
			break;
		case 0x1000000://主体 音频
		case 0x1000100://主体 音频+辅 音频
		case 0x1000101://主体 音频+辅 音频+文字
		case 0x1000001://主体音频+文字
		case 0x100://辅 音频
		case 0x101://辅 音频+文字
			diaryType = 4;
			break;
		case 0x100000://主体 图片
		case 0x100100://主体 图片+辅 音频
		case 0x100101://主体 图片+辅 音频+文字
		case 0x100001://主体 图片 +文字
			diaryType = 2;
			//url = getCoverImage(3, attachs);
			break;
		case 0x10000://主体 文字
		case 0x10001://主体 文字+文字
		case 0x10100://主体 文字+辅 音频
		case 0x10101://主体 文字+辅 音频+文字
		case 0x1://辅 文字
			diaryType = 1;
			break;
		default:
			break;
		}
    	return diaryType;
    }

    //文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
    public static String getCoverImage(int type, diaryAttach[] attachs) {
    	List<diaryAttach> attachList=Arrays.asList(attachs);
    	String coverUrl = null;
    	for(int i = 0; i < attachList.size(); i++) {
    		diaryAttach attach=attachList.get(i);
    		if(attach == null) continue; 
        	switch(type) {
    		case 5: //视频
    			show_height = attach.show_height;
    			show_width = attach.show_width;
    			coverUrl = attach.videocover;
    			break;
    		case 2: //图片
    			if(attach.attachimage != null && attach.attachimage.length > 0){
    			show_height = attach.show_height;
        		show_width = attach.show_width;
    			coverUrl = attach.attachimage[0].imageurl;
    			}
    			break;
    		default:
    			break;
    		}
        	if(coverUrl != null) {
        		break;
        	}
    	}
    	return coverUrl;
    }

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		state = 1;
		Requester2.homePage(getHandler(), ActiveAccount.getInstance(this).getUID(), first_diary_time, "1", adapter.getImgwidth() + "","", display.getWidth()+"", display.getHeight()+"");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		state = 2;
		Requester2.homePage(getHandler(), ActiveAccount.getInstance(this).getUID(), last_diary_time, "2", adapter.getImgwidth() + "","", display.getWidth()+"", display.getHeight()+"");
	}

	
	// 设置三种类型参数分别为String,Integer,String  
    class CleanTask extends AsyncTask<Integer, Integer, String> {  
  
        @Override  
        protected void onCancelled() {  
            super.onCancelled();  
            ZDialog.dismiss();
        }  
        @Override  
        protected void onPostExecute(String result) {  
            // 返回HTML页面的内容  
            ZDialog.dismiss();
            refreshDataSize();
        }  
        @Override  
        protected void onPreExecute() {  
            // 任务启动，可以在这里显示一个对话框，这里简单处理  
        	ZDialog.show(R.layout.progressdialog, false, true, context);
        }  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
            // 更新进度  
             
        }
        
    	/**
    	 *  清理私信
    	 *  type：
    	 *  -1 全部
    	 *  1 文本私信 -》 日记或文本私信
    	 *  2 图片私信 -》 日记
    	 *  3 音频私信  -》 日记或音频私信
    	 *  4 视频私信 -》 日记
    	 * */
        
		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
            try {  
                if(params[0] == R.id.btn_synchronized_cleanupall){
                	DiaryManager.getInstance().removeSyncDiaryFiles(false);
                	System.out.println("~~~~clean btn_synchronized_cleanupall~~~");
                }else if(params[0] == R.id.btn_intelligentcleanup){
                	//智能清理
                	DiaryManager.getInstance().removeSyncDiaryFiles(true);
                	mmp.cleanReadMedia(handler, -1);
                	System.out.println("~~~~clean btn_intelligentcleanup~~~");
                }else if(params[0] == R.id.btn_primsg_cleanvideo){
                	accountInfo.privateMsgManger.cleanMessageByType(4);
                	System.out.println("~~~~clean btn_primsg_cleanvideo~~~");
                }else if(params[0] == R.id.btn_primsg_cleanpic){
                	accountInfo.privateMsgManger.cleanMessageByType(2);
                	System.out.println("~~~~clean btn_primsg_cleanpic~~~");
                }else if(params[0] == R.id.btn_primsg_cleanaudio){
                	accountInfo.privateMsgManger.cleanMessageByType(3);
                	System.out.println("~~~~clean btn_primsg_cleanaudio~~~");
                }else if(params[0] == R.id.btn_primsg_cleanupall){
                	accountInfo.privateMsgManger.cleanMessageByType(-1);
                	System.out.println("~~~~clean btn_primsg_cleanupall~~~");
                }
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            return null; 
		}  
    }
	
}
