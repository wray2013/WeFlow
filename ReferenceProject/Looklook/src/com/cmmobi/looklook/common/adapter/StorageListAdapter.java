package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.SettingActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.GsonResponse2.getCloudSizeResponse;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.prompt.Prompt;

public class StorageListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	//private boolean existsPrompt = false;
	//private Button btn_curdel;
	//private float x,ux,y,uy; 
	private DiaryManager diaryManager;
/*	private Handler handler;
	private String diaryId;
	private int currentposition;*/
	
	public StorageListAdapter(final Context context, ArrayList<Map<String, String>> list) {
		this.context = context;
		this.list.addAll(list);
		inflater = LayoutInflater.from(context);
		diaryManager = DiaryManager.getInstance();
		/*handler = new Handler() {
            public void handleMessage(Message msg) {
            	switch (msg.what) {
            	case Requester2.RESPONSE_TYPE_DELETE_DIARY:
            		ZDialog.dismiss();
            		GsonResponse2.deleteDiaryResponse res = (GsonResponse2.deleteDiaryResponse) msg.obj;
        			if(res != null){
        				if (res.status.equals("0")) {
        					diaryManager.createDiaryStructureById(diaryId, handler);
        					Prompt.Alert("已成功删除");
        					StorageListAdapter.this.list.remove(currentposition);  
    						notifyDataSetChanged(); 
        				}else if(res.status.equals("200600")){
        					Prompt.Alert(context, Constant.CRM_STATUS[Integer.parseInt(res.crm_status)]);
        				}else{
        					Prompt.Alert("操作失败");
        				}
        			}else{
        				Prompt.Alert("网络不给力");
        			}
            		break;
//            	case Requester2.RESPONSE_TYPE_CREATE_STRUCTURE:
//            		GsonResponse2.createStructureResponse structureResponse = (createStructureResponse) msg.obj;
//            		diaryManager.modifyLocalDiaryByStructure(structureResponse);
//            		Prompt.Alert("已成功删除");
//            		ZDialog.dismiss();
//            		break;
            	}
            }
		};*/
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_setting_storagemanager_list_item, null);
			holder = new ViewHolder();
			holder.wiv_img = (WebImageView) convertView.findViewById(R.id.wiv_img);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			//holder.btn_del = (Button) convertView.findViewById(R.id.btn_del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.btn_del.setVisibility(View.GONE);
		if(list.get(position).get("imgurl") == null){
			String type = list.get(position).get("diarytype");
			if(type != null){
				switch (Integer.parseInt(type)) {
				case 1:
					holder.wiv_img.setImageResource(R.drawable.guanli_nothing);
					break;
				case 3:
				case 4:
					holder.wiv_img.setImageResource(R.drawable.guanli_luyin);
					break;
				default:
					holder.wiv_img.setImageResource(R.drawable.guanli_moren);
					break;
				}
			}else{
				holder.wiv_img.setImageResource(R.drawable.guanli_nothing);
			}
		}else{
			holder.wiv_img.setImageUrl(R.drawable.maptankuang_moren, 0,
				list.get(position).get("imgurl"), false);
		}
		holder.tv_time.setText(list.get(position).get("time"));
		holder.tv_size.setText(list.get(position).get("size"));
		
		
/*		//为每一个view项设置触控监听  
		convertView.setOnTouchListener(new OnTouchListener() {  
			public boolean onTouch(View v, MotionEvent event) {   
			//当按下时处理  
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				x = event.getX(); 
				y = event.getY();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {// 松开处理  
				ux = event.getX();  
				uy = event.getY(); 
				//按下和松开绝对值差当大于20时显示删除按钮，否则不显示  
				if (Math.abs(x - ux) > 15  && Math.abs(y - uy) < 15) {  
					ViewHolder holder = (ViewHolder) v.getTag(); 
					if(holder.btn_del.getVisibility() == View.VISIBLE){
						holder.btn_del.setVisibility(View.GONE);
					}else{
						holder.btn_del.setVisibility(View.VISIBLE);
					}
				}
			} 
			return true;  
		}  
		});*/

		//为删除按钮添加监听事件，实现点击删除按钮时删除该项  
		/*holder.btn_del.setOnClickListener(new OnClickListener() {  
			public void onClick(View v) {  
			if(btn_curdel != null)  
				btn_curdel.setVisibility(View.GONE); 
				String type = list.get(position).get("type");
				if(type != null){
					if(type.equals("synchronized")){
						diaryManager.findLocalDiaryByUuid(list.get(position).get("uuid")).clear();
						list.remove(position);  
						notifyDataSetChanged(); 
					}else if(type.equals("notupload")){
						diaryManager.removeLocalDiaryByUUID(list.get(position).get("uuid"), true);
						list.remove(position);  
						notifyDataSetChanged(); 
					}else if(type.equals("cloud")){
						diaryId = list.get(position).get("diaryID");
						currentposition = position;
						ZDialog.show(R.layout.progressdialog, false, true, context);
						Requester2.deleteDiary(handler, list.get(position).get("diaryID"));
					}

				}
				 
			}  
		});  
*/
		
		return convertView;
	}

/*	public void setNoDiaryPrompt(String time)
	{
		Map<String, String> map;
		map = new HashMap<String, String>();	
				map.put("size", "暂无内容");
				map.put("imgurl", null);
				map.put("time", time);
				map.put("diarytype", "0");
				list.clear();
			    list.add(map);
		existsPrompt = true;
		notifyDataSetChanged();
	}*/
	
	public void addPreData(List<Map<String, String>> predata)
	{

		if (predata==null ||predata.size() <= 0)
		{
			return;
		}
/*		if (existsPrompt)
		{
			list.clear();
			existsPrompt = false;
		}*/
		int i = 0;
		while (i < predata.size())
		{
			list.add(i, predata.get(i));
			i++;
		}
		notifyDataSetChanged();
	}
	
	public void addAfterData(List<Map<String, String>> afterdata)
	{
		if (afterdata == null || afterdata.size() <= 0)
		{
			return;
		}
/*		if (existsPrompt)
		{
			list.clear();
			existsPrompt = false;
		}*/
		list.addAll(afterdata);
		notifyDataSetChanged();
	}
	

	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	public List<Map<String, String>> getDatas(){
		return list;
	}
	
	public void clearAdapter(){
		list.clear();
		notifyDataSetChanged();
	}
	
	public class ViewHolder {
		WebImageView wiv_img;
		TextView tv_time;
		TextView tv_size;
		//public Button btn_del;
	}

	public int getImgwidth(){
		return (int)(context.getResources().getDisplayMetrics().density *154 +0.5f);
	}
}