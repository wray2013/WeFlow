package com.cmmobi.looklook.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.wheeltime.WheelMaintime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.activity
 * @filename DiaryPositionEditActivity.java
 * @summary 日记导入后的位置编辑界面
 * @author Administrator
 * @date 2014-3-25
 * @version 1.0
 */
public class DiaryPositionEditActivity extends ZActivity
{

	private final String TAG = this.getClass().getSimpleName();
	
	public static final int DIARY_POSITION_EDIT = 0;
	
	private ListView mListView = null;
	private PositionAdapter adapter = null;
	
	private List<MyDiaryList> mListDiaryList = null;
	private List<DiaryInfo> mListDiary = new ArrayList<DiaryPositionEditActivity.DiaryInfo>();
	
	private LayoutInflater inflater;
	
	private String mUserID = null;
	
	private PopupWindow pw_birthday; //设置时间
	
	private TextView btn_commit;
	private WheelMaintime time;
	private final int REQUEST_POSITION = 0x12345678;
	
	private int mCurrent;
	private int changeDiaryCount = 0;
	
	private class DiaryInfo
	{
		String uuid;
		String time;
		String latitude;
		String longitude;
		String pos;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_position_edit);
		mUserID = ActiveAccount.getInstance(this).getUID();
		
		inflater = LayoutInflater.from(this);
		
		mListDiaryList = (List<MyDiaryList>) DiaryManager.getInstance().getDetailDiaryList().clone();
		mListDiary = new ArrayList<DiaryInfo>();
		
		if(mListDiaryList == null)
		{
			Log.v(TAG, "diarylist is null");
			finish();
		}
		for(int i = 0; i < mListDiaryList.size(); i++)
		{
			MyDiaryList myDiaryList = mListDiaryList.get(i);
			MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(myDiaryList.diaryuuid);
			if(diary != null)
			{
				DiaryInfo info = new DiaryInfo();
				info.uuid = diary.diaryuuid;
				info.time = diary.shoottime;
				info.pos = diary.position_view;
				info.latitude = diary.latitude_view;
				info.longitude = diary.longitude_view;
				mListDiary.add(info);
			}
		}
		
		mListView = (ListView) findViewById(R.id.lv_diary);
		adapter = new PositionAdapter(this);
		mListView.setAdapter(adapter);
//		mListView.setFocusable(false);
//		mListView.setEnabled(false);
		findViewById(R.id.btn_done).setOnClickListener(this);
		
		TextView tv = (TextView) findViewById(R.id.tv_title_text);
		tv.setText("成功导入" + mListDiary.size() + "张照片");
		
		initBirthdayChoice();
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what) {
		case DiaryController.DIARY_REQUEST_DONE:
			changeDiaryCount--;
			if (changeDiaryCount == 0) {
				ZDialog.dismiss();
				Intent intent = new Intent(this, DiaryPreviewActivity.class);
				String UUID = mListDiary.get(0).uuid;
				intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, UUID);
				startActivity(intent);
				finish();
			}
			break;
		}
		return false;
	}
	

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_done:
		{
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "import_finish");
			ArrayList<DiaryInfo> changeDiaryList = new ArrayList<DiaryInfo>();
			
			for (DiaryInfo info:mListDiary) {
				MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(info.uuid);
				if (diary != null) {
					if ((info.time != null && !info.time.equals(diary.shoottime)) || (info.pos != null && !info.pos.equals(diary.position_view))) {
						changeDiaryCount++;
						changeDiaryList.add(info);
					}
				}
			}
			
			if (changeDiaryCount != 0) {
				ZDialog.show(R.layout.progressdialog, false, true, this,true);
				for (DiaryInfo info:changeDiaryList) {
					MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(info.uuid);
					DiaryController.getInstanse().updateDiary(handler, diary, null, "", info.longitude, info.latitude, info.pos, "1", info.time);
					DiaryController.getInstanse().diaryContentIsReady(diary.diaryuuid);
				}
			} else {
				if(mListDiary.size() > 0)
				{
					Intent intent = new Intent(this, DiaryPreviewActivity.class);
					String UUID = mListDiary.get(0).uuid;
					intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, UUID);
					startActivity(intent);
				}
				finish();
			}
			break;
		}
		case R.id.iv_list_pic_time:
		{
			int i = (Integer) v.getTag();
			pw_birthday.showAtLocation(findViewById(R.id.rl_edit),
					Gravity.BOTTOM, 0, 0);
			
			if(mListDiary.size() > 0)
			{
				DiaryInfo info = mListDiary.get(i);
				
				long tmp = 0;
				int y = 0;
				int m = 0;
				int d = 0;
				Calendar c = Calendar.getInstance();
				
				y = c.get(Calendar.YEAR);
				m = c.get(Calendar.MONTH);
				d = c.get(Calendar.DAY_OF_MONTH);
				try
				{
					tmp = Long.parseLong(info.time);
					
					if(tmp != 0)
					{
						c.setTimeInMillis(tmp);
					}
					
					y = c.get(Calendar.YEAR);
					m = c.get(Calendar.MONTH);
					d = c.get(Calendar.DAY_OF_MONTH);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				time.initDateTimePicker(y, m, d);
			}
			btn_commit.setTag(i);
			break;
		}
		case R.id.iv_list_pic_position:
		{
			int i = (Integer) v.getTag();
			mCurrent = i;
			DiaryInfo info = mListDiary.get(i);
			
			Intent intent = new Intent(this, PositionSelectActivity.class);
			try {
				if (info.latitude != null && info.longitude != null && (int)Double.parseDouble(info.latitude) > 0 && (int)Double.parseDouble(info.longitude) > 0) { 
					intent.putExtra(PositionSelectActivity.INTENT_LATITUDE, info.latitude);
					intent.putExtra(PositionSelectActivity.INTENT_LONGITUDE, info.longitude);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			startActivityForResult(intent, DIARY_POSITION_EDIT);
			break;
		}
		case R.id.tv_commit_birthday:
		{
			int i = (Integer) v.getTag();
			if(pw_birthday.isShowing()){
				pw_birthday.dismiss();
			}
			String bir = time.getTime();
			
			if(mListDiary.size() > 0)
			{
				DiaryInfo info = mListDiary.get(i);
				
				long tmp = 0;
				int h = 0;
				int m = 0;
				int s = 0;
				try
				{
					tmp = Long.parseLong(info.time);
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(tmp);
					h = c.get(Calendar.HOUR_OF_DAY);
					m = c.get(Calendar.MINUTE);
					s = c.get(Calendar.SECOND);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
	//			info.time = Long.toString(DateUtils.stringToDate(bir, DateUtils.DATE_FORMAT_NORMAL_1).getTime() + tmp);
				
				Calendar c = Calendar.getInstance();
				c.setTime(DateUtils.stringToDate(bir, DateUtils.DATE_FORMAT_NORMAL_1));
				c.set(Calendar.HOUR_OF_DAY, h);
				c.set(Calendar.MINUTE, m);
				c.set(Calendar.SECOND, s);
				
				info.time = Long.toString(c.getTimeInMillis());
	
				adapter.notifyDataSetChanged();
			}
			break;
		}
		case R.id.tv_back_birthday:
			if(pw_birthday.isShowing()){
				pw_birthday.dismiss();
			}
			break;
		default:
			break;
		}
	}
	
	

	private class ViewHolder
	{
		ImageView image;
		TextView time;
		TextView position;
	}
	
	private class PositionAdapter extends BaseAdapter
	{
		
		private LayoutInflater inflater;
		
		public PositionAdapter(Context context)
		{
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			return mListDiary.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
//			if(convertView == null)
//			{
				convertView = inflater.inflate(R.layout.list_item_position, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.iv_list_pic_icon);
				holder.time = (TextView) convertView.findViewById(R.id.iv_list_pic_time);
				holder.time.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
//				holder.time.setTextColor(Color.rgb(0x00, 0x7a, 0xff));
				holder.time.setTag(position);
				holder.time.setOnClickListener(DiaryPositionEditActivity.this);
				
				holder.position = (TextView) convertView.findViewById(R.id.iv_list_pic_position);
				holder.position.setTag(position);
				holder.position.setOnClickListener(DiaryPositionEditActivity.this);
				convertView.setTag(holder);
//			}
//			else
//			{
//				holder = (ViewHolder) convertView.getTag();
//			}
			
			DiaryInfo info = mListDiary.get(position);
			
			ImageLoader loader = ImageLoader.getInstance();
			
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.bg_default)
					.showImageForEmptyUri(R.drawable.bg_default)
					.showImageOnFail(R.drawable.bg_default)
					.cacheInMemory(true).cacheOnDisc(true)
					.displayer(new SimpleBitmapDisplayer())
					// .displayer(new CircularBitmapDisplayer()) 圆形图片
					// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
					.build();
			
			MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(info.uuid);
			
			loader.displayImageEx(diary.getMainUrl(), holder.image, options, null, mUserID, 0);
			
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
				String str = sdf.format(new Date(Long.parseLong(info.time)));
				holder.time.setText(str);
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
			holder.position.setText(info.pos);
			
			return convertView;
		}
		
	}
	
	//显示日期选择界面
	private void initBirthdayChoice(){
		View view = inflater.inflate(R.layout.activity_setting_birthday_menu,
				null);
		pw_birthday = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_birthday.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		time = new WheelMaintime(view);
//		time.initDateTimePicker();
		btn_commit = (TextView) view.findViewById(R.id.tv_commit_birthday);
		btn_commit.setOnClickListener(this);
		view.findViewById(R.id.tv_back_birthday).setOnClickListener(this);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case DIARY_POSITION_EDIT:
			if(resultCode == RESULT_OK)
			{
				if(mListDiary.size() > 0)
				{
					DiaryInfo info = mListDiary.get(mCurrent);
					info.latitude = data.getStringExtra("latitude");
					info.longitude = data.getStringExtra("longitude");
					info.pos = data.getStringExtra("position");
					adapter.notifyDataSetChanged();
					
					List<POIAddressInfo> freqPosList = CommonInfo.getInstance().frequentpos;
					POIAddressInfo addInfo = new POIAddressInfo();
					addInfo.position = info.pos;
					addInfo.longitude = info.longitude;
					addInfo.latitude = info.latitude;
					Log.d(TAG,"position = " + addInfo.position + " longitude = " + addInfo.longitude + " latitude = " + addInfo.latitude);
					if (freqPosList.contains(addInfo)) {
						Log.d(TAG,"onActivityResult if");
						freqPosList.remove(addInfo);
						freqPosList.add(0, addInfo);
					} else {
						freqPosList.add(0,addInfo);
					}
				}
			}
			break;

		default:
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
