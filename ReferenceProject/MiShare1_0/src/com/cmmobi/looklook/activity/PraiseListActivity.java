package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.getDiaryEnjoyUsers;
import com.cmmobi.looklook.common.gson.GsonResponse3.getDiaryEnjoyUsersResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class PraiseListActivity extends ZActivity implements OnLongClickListener, OnItemClickListener, OnRefreshListener2<ListView>
{
	private final String TAG = PraiseListActivity.class.toString();
	private String mUserID;
	
//	private String mViewID;
	private String mDiaryID;
	private String mPublishID;
	private String mType;
	private LinkedList<getDiaryEnjoyUsers> mListUser = new LinkedList<getDiaryEnjoyUsers>();
	
	private PullToRefreshListView mPullList = null; 
	private ListView mListView = null;
	
	private PraiseAdapter mAdapter = null;
	
	private Handler mHandler;
	
	private String mFirstTime = "";
	private String mLastTime = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_praiselist);
		findViewById(R.id.ib_title_back).setOnClickListener(this);
		findViewById(R.id.ib_title_back).setOnLongClickListener(this);
		mPullList = (PullToRefreshListView) findViewById(R.id.lv_praise_list);
		mPullList.setShowIndicator(false);
		mPullList.setOnRefreshListener(this);
		mListView = mPullList.getRefreshableView();
		mListView.setOnItemClickListener(this);
		
		mAdapter = new PraiseAdapter(this);
		
		mListView.setAdapter(mAdapter);
		
		mUserID = ActiveAccount.getInstance(this).getUID();
		
		mDiaryID = getIntent().getStringExtra(DiaryDetailDefines.INTENT_DIARY_DIARYID);
		mPublishID = getIntent().getStringExtra(DiaryDetailDefines.INTENT_DIARY_PUBLISHID);
		mType = getIntent().getStringExtra(DiaryDetailDefines.INTENT_DIARY_TYPE);
		mHandler = new Handler(this);
		Requester3.getDiaryEnjoyUsers(mHandler, mDiaryID, mPublishID, mType, "30", "", "");
		ZDialog.show(R.layout.progressdialog, false, true, this);
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case Requester3.RESPONSE_TYPE_GET_DIARY_ENJOY:
			ZDialog.dismiss();
			mPullList.onRefreshComplete();
			if(msg.obj != null)
			{
				getDiaryEnjoyUsersResponse res = (getDiaryEnjoyUsersResponse) msg.obj;
				
				long first = 0;
				long last = 0;
				
				try
				{
					first = Long.parseLong(res.first_diary_time);
					last = Long.parseLong(res.last_diary_time);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				if (mListUser.size() == 0)
				{
					Collections.addAll(mListUser, res.enjoies);
					mFirstTime = res.first_diary_time;
					mLastTime = res.last_diary_time;
				}
				else
				{
					if (first != 0 && first > Long.parseLong(mFirstTime))
					{
						mFirstTime = res.first_diary_time;
						mListUser.addAll(0, mListUser);
						break;
					}
					if (last != 0 && last < Long.parseLong(mLastTime))
					{
						mLastTime = res.last_diary_time;
						Collections.addAll(mListUser, res.enjoies);
						break;
					}
				}
				
//				if(res.hasnextpage != null && res.hasnextpage.equals("1"))
//				{
//					Requester3.getDiaryEnjoyUsers(mHandler, mDiaryID, mPublishID, mType, "10", res.first_diary_time, "2");
//				}
//				else
				{
					mAdapter.notifyDataSetChanged();
				}
			}
			else
			{
				Log.v(TAG, "enjoy request result null");
			}
			break;
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null)
			{
				addfriendResponse aresponse = (addfriendResponse) msg.obj;
				if ("0".equals(aresponse.status))
				{
					if (aresponse.target_userid == null)
					{
						break;
					}
					Prompt.Alert(this, "好友申请已发送");
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ib_title_back:
			finish();
			break;

		default:
			break;
		}

	}
	
	@Override
	public boolean onLongClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ib_title_back:
			Intent intent = new Intent(this,LookLookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
	{
		final getDiaryEnjoyUsers enjoyUser = (getDiaryEnjoyUsers) parent.getAdapter().getItem(position);
		if (enjoyUser != null) {
			if (!enjoyUser.userid.equals(ActiveAccount.getInstance(this).getUID()))
			{
				AccountInfo accountInfo = AccountInfo.getInstance(mUserID);
				
				if(accountInfo.friendsListName.findUserByUserid(enjoyUser.userid) == null)
				{
					new XEditDialog.Builder(PraiseListActivity.this)
					.setTitle(R.string.xeditdialog_title)
					.setPositiveButton(android.R.string.ok, new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//加好友
							Requester3.addFriend(handler, enjoyUser.userid, v.getTag().toString());
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.create().show();
				}
				else
				{
					Intent intent = new Intent(PraiseListActivity.this, OtherZoneActivity.class);
					intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, enjoyUser.userid);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					PraiseListActivity.this.startActivity(intent);
				}
			}
		}

	}
	
	private class ViewHolder
	{
		ImageView icon;
		TextView name;
	}
	
	private class PraiseAdapter extends BaseAdapter
	{
		private LayoutInflater inflater;
		private DisplayImageOptions options;
		private ImageLoader loader;
		public PraiseAdapter(Context context)
		{
			inflater = LayoutInflater.from(context);
			options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.moren_touxiang)
			.showImageForEmptyUri(R.drawable.moren_touxiang)
			.showImageOnFail(R.drawable.moren_touxiang)
			.cacheInMemory(true).cacheOnDisc(true)
			.displayer(new SimpleBitmapDisplayer())
			.build();
			loader = ImageLoader.getInstance();
	
		}
		
		@Override
		public int getCount()
		{
			return mListUser.size();
		}

		@Override
		public Object getItem(int position)
		{
			if(position<mListUser.size()){
				return mListUser.get(position);
			}

			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			if(convertView == null)
			{
				convertView = inflater.inflate(R.layout.list_item_praise, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.friend_icon_imageview);
				holder.name = (TextView) convertView.findViewById(R.id.friend_name_textview);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}			
			loader.displayImage(mListUser.get(position).headimageurl, holder.icon, options, mUserID, 0);
			holder.name.setText(mListUser.get(position).nickname);
			
			return convertView;
		}
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
	{
//		if(TextUtils.isEmpty(mFirstTime))
//		{
//			Requester3.getDiaryEnjoyUsers(mHandler, mDiaryID, mPublishID, mType, "30", "", "");
//		}
//		else
//		{
//			Requester3.getDiaryEnjoyUsers(mHandler, mDiaryID, mPublishID, mType, "10", "" + mFirstTime, "1");
//		}
		mListUser.clear();
		Requester3.getDiaryEnjoyUsers(mHandler, mDiaryID, mPublishID, mType, "30", "", "");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
	{
		if(TextUtils.isEmpty(mLastTime))
		{
			Requester3.getDiaryEnjoyUsers(mHandler, mDiaryID, mPublishID, mType, "30", "", "");
		}
		else
		{
			Requester3.getDiaryEnjoyUsers(mHandler, mDiaryID, mPublishID, mType, "10", "" + mLastTime, "2");
		}
	}

}
