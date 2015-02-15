/**
 * 
 */
package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.GsonResponse2.myblacklistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myblacklistUsers;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.google.gson.reflect.TypeToken;

/**
 * @author jiayunan
 *
 * @create 2013-6-6
 */
public class MyAttentionMembersActivity extends ZActivity implements OnItemClickListener, OnRefreshListener {

	private static final String TAG="MyAttentionMembersActivity";
	private static final String REQUEST_ATTENTION_PAGE_NO = "2000";
	public static final String INTENT_ACTION_USERID="userid";
	
	private static final boolean ISDEBUG=true;
	private LayoutInflater inflater;
	private PullToRefreshListView listView;
	private ListView lv;
	private ArrayList<GsonResponse2.myattentionlistUsers> list;
	private int requestPageno = 1;
	private boolean isShowAllAttention = false;
	private String userID;
	private AccountInfo ai;//, otherAI;
	private String otherUserID;
	private String lastReqTime;
	private boolean amIInBlackList = false;
	//private String friends_type;
	
	//private LoginSettingManager lsm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_attention_member);
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		ai = AccountInfo.getInstance(userID);
		
		otherUserID = getIntent().getStringExtra(INTENT_ACTION_USERID);
		//otherAI = AccountInfo.getInstance(otherUserID);
		//lsm = otherAI.setmanager;
		//friends_type = lsm.getFriends_type();
		
		isShowAllAttention = false;
		inflater=LayoutInflater.from(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		listView=(PullToRefreshListView) findViewById(R.id.lv_activity_person_fans);
		listView.setOnRefreshListener(this);
		list=new ArrayList<GsonResponse2.myattentionlistUsers>();
		//list=getMsg();
		Requester2.myBlacklist(handler,"", otherUserID, "50");
	
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (!list.get(position).userid.equals(userID)) {
			Intent intent = new Intent(this, HomepageOtherDiaryActivity.class);
			intent.putExtra("userid", list.get(position).userid);
			intent.putExtra("nickname", list.get(position).nickname);
			this.startActivity(intent);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_MY_ATTENTIONLIST:
			ZDialog.dismiss();
			GsonResponse2.myattentionlistResponse response = (GsonResponse2.myattentionlistResponse) msg.obj;
			if (null == response) {
				isShowAllAttention = true;
				Log.e(TAG, "response is null");
				return false;
			}else{
				lastReqTime = response.user_time;
			}
			if ("0".equals(response.status)) {
				// list.clear();
				if (response.users.length > 0) {
					for (int i = 0; i < response.users.length; i++) {
//						for (int j = 0; j < 10; j++) {
							list.add(response.users[i]);
//						}
					}
				} else {
					isShowAllAttention = true;
				}
				((MyAttentionMemberAdapter) lv.getAdapter()).notifyDataSetChanged();
				requestPageno++;
			}else if ("138119".equals(response.status)) {
				new Xdialog.Builder(this).setTitle("提醒").
				setMessage("无权访问，可能是当前用户设置了权限！").setPositiveButton(android.R.string.ok, null).create().show();
			}
			listView.onRefreshComplete();
			break;
		case Requester2.RESPONSE_TYPE_MY_BLACK_LIST:
			if (msg.obj != null) {
				myblacklistResponse blackList = (myblacklistResponse) msg.obj;

				if ("0".equals(blackList.status)) {
					myblacklistUsers[] userList = blackList.users;
					for (int i = 0; i < userList.length; i++) {
						if(userID.equals(userList[i].userid)){
							amIInBlackList = true;
						}
						Log.e("黑名单", userList[i].nickname);
					}
					Log.e("当前用户", ai.nickname);

					if ("1".equals(blackList.hasnextpage )) {
						Requester2.myBlacklist(handler, blackList.user_time, otherUserID, "50");
					} 
				} 
			}
			if (!amIInBlackList) {
				try {
					ZDialog.show(R.layout.progressdialog, true, true, this);
					if (otherUserID != null) {
						Requester2.requestAttentionList(handler, null,
								otherUserID, REQUEST_ATTENTION_PAGE_NO);
					}else{
						Requester2.requestAttentionList(handler, null,
								ai.userid, REQUEST_ATTENTION_PAGE_NO);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				lv = listView.getRefreshableView();
				lv.setAdapter(new MyAttentionMemberAdapter(list));
				lv.setOnItemClickListener(this);
			}else{
				new Xdialog.Builder(this).setTitle("提醒").
				setMessage("无权访问，可能是当前用户设置了权限！").setPositiveButton(android.R.string.ok, null).create().show();
			}
			break;

		default:
			break;
		}
		return false;
	}
	
	class MyAttentionMemberAdapter extends BaseAdapter{

		ArrayList<GsonResponse2.myattentionlistUsers> list;
		
		public MyAttentionMemberAdapter(ArrayList<GsonResponse2.myattentionlistUsers> list2){
			this.list=list2;
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GsonResponse2.myattentionlistUsers item = list.get(position);
			
			viewHolder holder;
			if(null==convertView){
				convertView=inflater.inflate(R.layout.activity_my_attention_member_item, null);
				holder=new viewHolder();
				holder.pic=(WebImageView) convertView.findViewById(R.id.iv_pic);

				holder.name=(TextView) convertView.findViewById(R.id.tv_name);
				
			}else{
				holder=(viewHolder) convertView.getTag();
			}
			
			//holder.pic.setLoadingDrawable(R.drawable.temp_local_icon);
			//holder.pic.setImageUrl(item.headimageurl, 1, true);
			holder.pic.setImageUrl(R.drawable.moren_touxiang, 1, item.headimageurl, true);
			holder.name.setText(item.nickname);
			
			convertView.setTag(holder);
			return convertView;
		}
	}
	
	static class viewHolder{
		WebImageView pic;
		TextView name;
	}
	
	
	@Override
	protected void onDestroy() {
		//persistMsg();
		super.onDestroy();
	}

	private void persistMsg(){
		if(ISDEBUG)Log.d(TAG, "persistMsg");
		if(KEY!=null)
			StorageManager.getInstance().putItem(KEY, list, ArrayList.class);
	}
	
	private static final String FANS_PERSIST_KEY="fans_persist_key";
	private static String KEY=null;
	/*private ArrayList<GsonResponse2.myattentionlistUsers> getMsg(){
		if(ISDEBUG)Log.d(TAG, "getMsg");
		String uid=ActiveAccount.getInstance(this).getUID();
		if(null==uid){
			Log.e(TAG, "uid is null");
			return new ArrayList<GsonResponse2.myattentionlistUsers>();
		}
		KEY=uid+"_"+FANS_PERSIST_KEY;
		ArrayList<GsonResponse2.myattentionlistUsers> list = 
				(ArrayList<GsonResponse2.myattentionlistUsers>) StorageManager.getInstance()
				.getItem(KEY, new TypeToken<ArrayList<GsonResponse2.myattentionlistUsers>>(){}.getType());
		if (null==list) {
			list=new ArrayList<GsonResponse2.myattentionlistUsers>();
		}
		return list;
	}*/

	@Override
	public void onRefresh() {
		if (!isShowAllAttention) {
			if (otherUserID != null) {
				Requester2.requestAttentionList(handler, lastReqTime,
						otherUserID, REQUEST_ATTENTION_PAGE_NO);
			}else{
				Requester2.requestAttentionList(handler, lastReqTime,
						userID, REQUEST_ATTENTION_PAGE_NO);
			}
		}else{
			listView.onRefreshComplete();
			Toast.makeText(MyAttentionMembersActivity.this, "没有更多关注", Toast.LENGTH_SHORT).show();
		}
	}
}
