package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import cn.jpush.android.c.r;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.StorageListAdapter;
import com.cmmobi.looklook.common.adapter.StorageListAdapter.ViewHolder;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.hp.hpl.sparta.xpath.PositionEqualsExpr;

/**
 * 未上传界面
 * 
 * @author youtian
 * 
 */
public class SettingStorageManagerNotSynchronizedActivity extends ZActivity implements OnRefreshListener2<ListView>{

	private PullToRefreshListView xlv_notsynchronized;
	private ListView lv_notsynchronized;
	private ImageView iv_back;

	private DiaryManager diaryManager;
	private ArrayList<Map<String, String>> notsynchronizeddata;
	private StorageListAdapter notsynchronizedAdapter;
	
	private Context context;
	
	private int index = 0;
	float x , y , upx, upy; 

/*	private LayoutInflater inflater;
	private PopupWindow pw_delete;
	private int position;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_storagemanager_datalist);
		context = this;
		//inflater = LayoutInflater.from(this);
		xlv_notsynchronized = (PullToRefreshListView) findViewById(R.id.xlv_data);
		xlv_notsynchronized.setShowIndicator(false);
		xlv_notsynchronized.setOnRefreshListener(this);

		lv_notsynchronized = xlv_notsynchronized.getRefreshableView();
		notsynchronizeddata = new ArrayList<Map<String, String>>();
		notsynchronizedAdapter = new StorageListAdapter(this, notsynchronizeddata);
		lv_notsynchronized.setAdapter(notsynchronizedAdapter);
		//initChoice();
		/*		
		lv_notsynchronized.setOnTouchListener(new View.OnTouchListener() {
			
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
						   int firstVisiblePostion = lv_notsynchronized.getFirstVisiblePosition();
						   if(position1 == position2){
							 View view = lv_notsynchronized.getChildAt(position1 - firstVisiblePostion);
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
		});*/
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);

		diaryManager = DiaryManager.getInstance();
		
		lv_notsynchronized.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				/*position = arg2 -1;
				pw_delete.showAtLocation(findViewById(R.id.rl_root), Gravity.CENTER, 0, 0);			
				*/
				Prompt.Dialog(SettingStorageManagerNotSynchronizedActivity.this, true, "提示", "确认删除？",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									final int position = arg2 -1;
									List<Map<String, String>> list = notsynchronizedAdapter.getDatas();
									NetworkTaskManager.getInstance(ActiveAccount.getInstance(context).getUID()).removeTask(list.get(position).get("uuid"));
									diaryManager.removeLocalDiaryByUUID(list.get(position).get("uuid"), true);
									list.remove(position); 
									notsynchronizedAdapter.notifyDataSetChanged();
									xlv_notsynchronized.post(new Runnable() {

								        @Override
								        public void run() {
								            // TODO Auto-generated method stub
								        	xlv_notsynchronized.getRefreshableView().setSelection(position-1);
								        }
								    });
							
								} catch (Exception e) {
									// TODO: handle exception
								}
									
							}
				});
				return true;
			}
		});
		getMoreDiary();
		if (notsynchronizedAdapter.isEmpty()) {
			Prompt.Dialog(context, false, "提示", "没有相关日记", null);
		}
	}
	
/*	// 显示删除
	private void initChoice() {
		View view = inflater.inflate(R.layout.dialog_delete,
				null);
		pw_delete = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		view.findViewById(R.id.ibtn_delete).setOnClickListener(this);
	}
	*/
	private void getMoreDiary(){	
		//TODO 初始化startNum的值，请求日记数量的起始值
		//int startNum=0;
		Boolean issafeshow = false;
		try {
			if(AccountInfo.getInstance(ActiveAccount.getInstance(context).getUID()).setmanager.getGesturepassword() == null){
				issafeshow = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		ArrayList<MyDiary> diaries = (ArrayList<MyDiary>)diaryManager.getUnSyncDiaryStorage(notsynchronizedAdapter.getCount(), issafeshow);
		Map<String, String> map;
		int count = 0;
		notsynchronizeddata.clear();
		if (diaries != null && diaries.size() > 0) {
				for (int i = 0; i < diaries.size(); i++) {
					map = new HashMap<String, String>();
					String size = diaries.get(i).getDiarySize();
					int tmptype = DiaryListView.getDiaryType(diaries.get(i).attachs);// 获取日记类型
					int diarytype = SettingStorageManagerActivity.getDiaryType(diaries.get(i).attachs, tmptype);
					String coverurl = SettingStorageManagerActivity.getCoverImage(diarytype, diaries.get(i).attachs);
					String time = diaries.get(i).diarytimemilli;
					map.put("size", SettingStorageManagerActivity.getSize(Long.parseLong(size)));
					map.put("imgurl", coverurl);
					map.put("time", SettingStorageManagerActivity.getTime(Long.parseLong(time)));
					map.put("diarytype", diarytype+"");
					map.put("type", "notupload");
					//map.put("diaryID", diaries.get(i).diaryid);
					map.put("uuid", diaries.get(i).diaryuuid);	
				//	System.out.println("SettingStorageManagerNotUploadActivity == " + new Gson().toJson(diaries.get(i)));
					//System.out.println("SettingStorageManagerNotUploadActivity == uuid " + diaries.get(i).diaryuuid);
					notsynchronizeddata.add(map);
				    count++;
					}			
				if(count != 0){
						index = notsynchronizedAdapter.getCount();
						notsynchronizedAdapter.addAfterData(notsynchronizeddata);
						return;
				}
		}
		index = notsynchronizedAdapter.getCount();
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



	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			this.onBackPressed();
			break;
		/*case R.id.ibtn_delete:
			if(pw_delete.isShowing()){
				pw_delete.dismiss();
			}
			List<Map<String, String>> list = notsynchronizedAdapter.getDatas();
			diaryManager.removeLocalDiaryByUUID(list.get(position).get("uuid"), true);
			list.remove(position);  
			notsynchronizedAdapter.notifyDataSetChanged();
			xlv_notsynchronized.post(new Runnable() {

		        @Override
		        public void run() {
		            // TODO Auto-generated method stub
		        	xlv_notsynchronized.getRefreshableView().setSelection(position-1);
		        }
		    });
			break;*/
		default:
			break;
		}
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		index = 1;
		GetMoreDiaryTask taskall = new GetMoreDiaryTask(); 
		taskall.execute(1);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		GetMoreDiaryTask taskall = new GetMoreDiaryTask();  
		taskall.execute(0);
	}
	
    class GetMoreDiaryTask extends AsyncTask<Integer, Integer, String> {  
    	  
        @Override  
        protected void onCancelled() {  
            super.onCancelled();  
        }  
        @Override  
        protected void onPostExecute(String result) {  
        		xlv_notsynchronized.onRefreshComplete();
        		xlv_notsynchronized.post(new Runnable() {

			        @Override
			        public void run() {
			            // TODO Auto-generated method stub
			        	xlv_notsynchronized.getRefreshableView().setSelection(index);
			        }
			    });
        }  
        @Override  
        protected void onPreExecute() {  

        }  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
             
        }
        
		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
            try {  
            	if(params[0] == 0){
            		getMoreDiary();	
            	}
            	
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            return null; 
		}  
    }
}
