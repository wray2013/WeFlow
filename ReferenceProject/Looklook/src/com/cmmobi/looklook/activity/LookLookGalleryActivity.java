package com.cmmobi.looklook.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDataPool;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.VideoShootActivity.TempDiaryWrapper;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;

/**
 * 连拍相册页面
 * @author jiayunan
 *
 */
public class LookLookGalleryActivity extends ZActivity implements
		OnItemClickListener {

	private GridView gallery_view;
	private GalleryAdapter gAdapter;
	private ImageButton back_btn;
	private ImageButton pichuli_btn;
	private ArrayList<TempDiaryWrapper> delList;
	private List<TempDiaryWrapper> list;
	private ArrayList<Boolean> chooseList;
	private boolean muti_mood = false;
	private boolean ispicchose = false;
	private boolean isBack = false;
	private int delState = -1;// -1 未删除 0 删除中 1 删除完成
	
//	private static final String GALLERY_PATH = "/storage/sdcard0/DCIM/100MEDIA";
	private static final String TAG = "LookLookGalleryActivity";

	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
		case Requester2.RESPONSE_TYPE_DELETE_DIARY:
			delState = 1;
			ZDialog.dismiss();
			if (isBack) {
				LookLookGalleryActivity.this.finish();
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gallery_back_btn:
			isBack = true;
			if (delState != 0) {
				LookLookGalleryActivity.this.finish();
			}
			break;
		case R.id.gallery_pichuli_btn:
			if (muti_mood == false) {
				pichuli_btn
						.setImageResource(R.drawable.btn_activity_homepage_remove);
				muti_mood = true;
			} else {
				delState = -1;
				pichuli_btn
						.setImageResource(R.drawable.btn_activity_paishe_pichuli);
				muti_mood = false;
				int delListSize = delList.size();
				String deleteDiaryIds = "";
				if(delList != null && delListSize > 0){
					for (int i = 0; i < delListSize; i++) {
						TempDiaryWrapper wrapper = delList.get(i);
						DiaryManager.getInstance().removeLocalDiaryByUUID(wrapper.request.diaryuuid, false);
						list.remove(wrapper);
						if (wrapper.response.diaryid != null) {
							deleteDiaryIds += wrapper.response.diaryid;
						}
						if (i != delListSize - 1) {
							deleteDiaryIds += ",";
						}
						AccountInfo.getInstance(wrapper.mediaValue.UID).mediamapping.delMedia(wrapper.mediaValue.UID, wrapper.mediaValue.url);
						File file = new File(wrapper.mediaPath);
						if (file.exists()) {
							file.delete();
						}
					}
					
					Requester2.deleteDiary(handler,deleteDiaryIds);
					ZDialog.show(R.layout.progressdialog, false, true, this);
					delState = 0;
					
					if (list != null) {
						int listSize = list.size();
						chooseList.clear();
						for(int i = 0;i < listSize; i++) {
							chooseList.add(false);
						}
					}
				}
				delList.clear();
//				getImageListFromPath(GALLERY_PATH);
				gAdapter.notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}
	
	private void initThumbImg() {
		list = (List<TempDiaryWrapper>) ZDataPool.getOut(VideoShootActivity.DIARYS);
		if (list != null) {
			int listSize = list.size();
			chooseList = new ArrayList<Boolean>();
			for(int i = 0;i < listSize; i++) {
				chooseList.add(false);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		delList = new ArrayList<TempDiaryWrapper>();
//		getImageListFromPath(GALLERY_PATH);
		gallery_view = (GridView) findViewById(R.id.gv_gallery);
		back_btn = (ImageButton) findViewById(R.id.gallery_back_btn);
		pichuli_btn = (ImageButton) findViewById(R.id.gallery_pichuli_btn);
		back_btn.setOnClickListener(this);
		pichuli_btn.setOnClickListener(this);
		initThumbImg();
		if (list != null && list.size() > 0) {
			gAdapter = new GalleryAdapter(this, list);
			gallery_view.setAdapter(gAdapter);
			gallery_view.setOnItemClickListener(this);
		}
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		
	}



	class GalleryAdapter extends BaseAdapter {

		private List<TempDiaryWrapper> filepathlist;
		private LayoutInflater inflater;

		public GalleryAdapter(Context context, List<TempDiaryWrapper> filepathlist) {
			this.filepathlist = filepathlist;
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return filepathlist.size();
		}

		@Override
		public Object getItem(int position) {
			return filepathlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			final int pos = position;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.activity_gallery_item,
						null);
				holder.pic = (ImageView) convertView
						.findViewById(R.id.iv_gallery_pic);
				holder.iv_gallery_chose = (ImageView) convertView.findViewById(R.id.iv_gallery_chose);

				holder.pic.setImageBitmap(filepathlist.get(position).thumbnail);
				Log.e("filepathlist ========", filepathlist.get(position) + "");
				
				holder.pic.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(muti_mood){
							if(!chooseList.get(pos)) {
								holder.iv_gallery_chose.setVisibility(View.VISIBLE);	
								chooseList.set(pos, true);
								delList.add(filepathlist.get(pos));
							} else {
								holder.iv_gallery_chose.setVisibility(View.GONE);
								chooseList.set(pos, false);
								delList.remove(filepathlist.get(pos));
							}
						} else {
							
						}
					}
				});
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (chooseList.get(pos)) {
				holder.iv_gallery_chose.setVisibility(View.VISIBLE);
			} else {
				holder.iv_gallery_chose.setVisibility(View.GONE);
			}
			
			return convertView;
		}

		class ViewHolder {
			ImageView pic;
			ImageView iv_gallery_chose;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

}
