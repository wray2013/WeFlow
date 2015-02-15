package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.StorageListAdapter;
import com.cmmobi.looklook.common.adapter.StorageListAdapter.ViewHolder;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 已同步界面
 * 
 * @author youtian
 * 
 */
public class SettingStorageManagerSynchronizedActivity extends ZActivity implements OnRefreshListener2<ListView>{

	private PullToRefreshListView xlv_synchronizeddata;
	private ListView lv_synchronizeddata;
	private ImageView iv_back;
	private TextView tv_title;

	private DiaryManager diaryManager;
	private ArrayList<Map<String, String>> synchronizeddata;
	private StorageListAdapter synchronizedAdapter;
	private int index=0;
	//float x , y , upx, upy;  
	/*private LayoutInflater inflater;
	private PopupWindow pw_delete;
	private int position;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_storagemanager_datalist);
		//inflater = LayoutInflater.from(this);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(R.string.synchronized_data);

		diaryManager = DiaryManager.getInstance();

		xlv_synchronizeddata = (PullToRefreshListView) findViewById(R.id.xlv_data);
		xlv_synchronizeddata.setShowIndicator(false);
		xlv_synchronizeddata.setOnRefreshListener(this);

		lv_synchronizeddata = xlv_synchronizeddata.getRefreshableView();
		/*initChoice();*/
		synchronizeddata = new ArrayList<Map<String, String>>();
		synchronizedAdapter = new StorageListAdapter(this, synchronizeddata);
		lv_synchronizeddata.setAdapter(synchronizedAdapter);
		lv_synchronizeddata.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				/*position = arg2 - 1;
				pw_delete.showAtLocation(findViewById(R.id.rl_root), Gravity.CENTER, 0, 0);
				*/// TODO Auto-generated method stub
				Prompt.Dialog(SettingStorageManagerSynchronizedActivity.this, true, "提示", "确认删除？",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								final int position = arg2 - 1;
								List<Map<String, String>> list = synchronizedAdapter.getDatas();
								diaryManager.findLocalDiaryByUuid(list.get(position).get("uuid")).clear();
								list.remove(position);  
								synchronizedAdapter.notifyDataSetChanged(); 
								xlv_synchronizeddata.post(new Runnable() {

							        @Override
							        public void run() {
							            // TODO Auto-generated method stub
							        	xlv_synchronizeddata.getRefreshableView().setSelection(position-1);
							        }
							    });
							}

						});
				return true;
			}
		});
/*		lv_synchronizeddata.setOnTouchListener(new View.OnTouchListener() {
			
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
						   int firstVisiblePostion = lv_synchronizeddata.getFirstVisiblePosition();
						   if(position1 == position2){
							 View view = lv_synchronizeddata.getChildAt(position1 - firstVisiblePostion);
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
		
		getMoreDiary();
		if (synchronizedAdapter.isEmpty()) {
			Prompt.Dialog(this, false, "提示", "没有相关日记", null);
		}
	}
	
	private void getMoreDiary(){
		//TODO 初始化startNum的值，请求日记数量的起始值
		//int startNum=0;
		Boolean issafeshow = false;
		try {
			if(AccountInfo.getInstance(ActiveAccount.getInstance(SettingStorageManagerSynchronizedActivity.this).getUID()).setmanager.getGesturepassword() == null){
				issafeshow = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		ArrayList<MyDiary> diaries = (ArrayList<MyDiary>)diaryManager.getSynchronizedDiary(synchronizedAdapter.getCount(),
				issafeshow);
		Map<String, String> map;
		int count = 0;
		synchronizeddata.clear();
		if (diaries != null && diaries.size() > 0) {
				for (int i = 0; i < diaries.size(); i++) {
					map = new HashMap<String, String>();
					String size = diaries.get(i).getDiarySize();
					int tmptype = DiaryListView.getDiaryType(diaries.get(i).attachs);// 获取日记类型
					int diarytype = SettingStorageManagerActivity.getDiaryType(diaries.get(i).attachs, tmptype);
					String coverurl = getCoverImage(diarytype, diaries.get(i));
					String time = diaries.get(i).diarytimemilli;
					map.put("size", SettingStorageManagerActivity.getSize(Long.parseLong(size)));
					map.put("imgurl", coverurl);
					map.put("time", SettingStorageManagerActivity.getTime(Long.parseLong(time)));
					map.put("diarytype", diarytype+"");
					map.put("type", "synchronized");
					//map.put("diaryID", diaries.get(i).diaryid);
					map.put("uuid", diaries.get(i).diaryuuid);
				    synchronizeddata.add(map);
				    count++;
				}			
				if(count != 0){
						index = synchronizedAdapter.getCount();
						synchronizedAdapter.addAfterData(synchronizeddata);
					    //lv_synchronizeddata.setSelection(index);
						return;
				}
		}
		index = synchronizedAdapter.getCount();
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

/*	// 显示删除
	private void initChoice() {
		View view = inflater.inflate(R.layout.dialog_delete,
				null);
		pw_delete = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		view.findViewById(R.id.ibtn_delete).setOnClickListener(this);
	}*/
	
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
			List<Map<String, String>> list = synchronizedAdapter.getDatas();
			diaryManager.findLocalDiaryByUuid(list.get(position).get("uuid")).clear();
			list.remove(position);  
			synchronizedAdapter.notifyDataSetChanged(); 
			xlv_synchronizeddata.post(new Runnable() {

		        @Override
		        public void run() {
		            // TODO Auto-generated method stub
		        	xlv_synchronizeddata.getRefreshableView().setSelection(position-1);
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
        		xlv_synchronizeddata.onRefreshComplete();
        		xlv_synchronizeddata.post(new Runnable() {

			        @Override
			        public void run() {
			            // TODO Auto-generated method stub
			        	xlv_synchronizeddata.getRefreshableView().setSelection(index);
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
    
    //文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
    public static String getCoverImage(int type, MyDiary myDiary) {
        diaryAttach[] attachs = myDiary.attachs;
    	String uid = ActiveAccount.getInstance(MainApplication.getInstance()).getUID();
    	List<diaryAttach> attachList=Arrays.asList(attachs);
    	String coverUrl = null;
    	for(int i = 0; i < attachList.size(); i++) {
    		diaryAttach attach=attachList.get(i);
    		if(attach == null) continue; 
        	switch(type) {
    		case 5: //视频
    			coverUrl = attach.videocover;
    			break;
    		case 2: //图片
    			if(attach.attachimage != null && attach.attachimage.length > 0){
    				MyAttachImage[] attachImage=attach.attachimage;
    				if(attachImage!=null&&attachImage.length>0){
						if(attachImage.length>1){
							for(int j=0;j<attachImage.length;j++){
								if(uid != null && uid.equals(myDiary.userid)){//自己的日记显示无标图片
									if("0".equals(attachImage[j].imagetype)){
										if(attachImage[j].imageurl!=null&&attachImage[j].imageurl.length()>0){
											return attachImage[j].imageurl;
										}else{
											return attach.attachuuid;
										}
									}
								}else{//他人日记显示有标图片
									if("1".equals(attachImage[j].imagetype)){
										if(attachImage[j].imageurl!=null&&attachImage[j].imageurl.length()>0){
											return attachImage[j].imageurl;
										}else{
											return attach.attachuuid;
										}
									}
								}
							}
						}
						
						if(attachImage[0].imageurl!=null&&attachImage[0].imageurl.length()>0){
							return attachImage[0].imageurl;
						}else{
							return attach.attachuuid;
						}
					}
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
}
