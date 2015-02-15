/**
 * 
 */
package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.myblacklistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myblacklistUsers;
import com.cmmobi.looklook.common.gson.GsonResponse2.myfanslistResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;

/**
 * @author wuxiang
 *
 * @create 2013-4-9
 */
public class PersonFansActivity extends ZActivity implements OnItemClickListener, OnRefreshListener {

	private static final String TAG="EventMainActivity";
	private static final boolean ISDEBUG=true;
	
	public static final String INTENT_ACTION_USERID="userid";
	
	private LayoutInflater inflater;
	private PullToRefreshListView listView;
	private ListView lv;
	private ArrayList<GsonResponse2.myfanslistUsers> list;
	private boolean isShowAllFans = false;
	private AccountInfo ai;
	private String userID;
	private MyFansAdapter mfd;
	private String otherUserID;
	private boolean amIInBlackList = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_fans);
		isShowAllFans = false;
		inflater=LayoutInflater.from(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		listView=(PullToRefreshListView) findViewById(R.id.lv_activity_person_fans);
		listView.setOnRefreshListener(this);
		list=new ArrayList<GsonResponse2.myfanslistUsers>();
		//list=getMsg();
		userID = ActiveAccount.getInstance(this).getUID();
		ai = AccountInfo.getInstance(userID);
		otherUserID = getIntent().getStringExtra(INTENT_ACTION_USERID);
		
		Requester2.myBlacklist(handler,"", otherUserID, "50");
		
		/*if (!AccountInfo.getInstance(otherUserID).blackListContactManager.isMember(userID)) {
			try {
				ZDialog.show(R.layout.progressdialog, true, true, this);
				if (otherUserID != null) {
					Requester2.requestMyFansList(handler, null, otherUserID);
				} else {
					Requester2.requestMyFansList(handler, null, userID);
				}
				Log.e("ai.latestReqTime", ai.latestReqTime_fans + "");
				//保存档次请求的时间戳到缓存
				//ai.latestReqTime = (new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")).format(new Date());
			} catch (Exception e) {
				e.printStackTrace();
			}
			lv = listView.getRefreshableView();
			mfd = new MyFansAdapter(list);
			lv.setAdapter(mfd);
			lv.setOnItemClickListener(this);
		}else{
			new Xdialog.Builder(this).setTitle("黑名单用户").
			setMessage("黑名单用户不能查看粉丝列表！").setPositiveButton(android.R.string.ok, null);
		}*/
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
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
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
		case Requester2.RESPONSE_TYPE_MY_FANS_LIST:
			ZDialog.dismiss();
			GsonResponse2.myfanslistResponse response = (myfanslistResponse) msg.obj;
			if (null == response) {
				isShowAllFans = true;
				Log.e(TAG, "response is null");
				return false;
			}
			if ("0".equals(response.status)) {
				// list.clear();
				if (response.users.length > 0) {
					for (int i = 0; i < response.users.length; i++) {
							if (!list.contains(response.users[i])) {
								list.add(response.users[i]);
							}
					}
					if ("1".equals(response.hasnextpage )) {
						if (otherUserID != null) {
							Requester2.requestMyFansList(handler, response.user_time, otherUserID);
						} else {
							Requester2.requestMyFansList(handler, response.user_time, userID);
						}
					} 
				} else {
					isShowAllFans = true;
				}
				mfd.notifyDataSetChanged();
			}else if ("138119".equals(response.status)) {
				new Xdialog.Builder(this).setTitle("提醒").
				setMessage("无权访问，可能是当前用户设置了权限！").setPositiveButton(android.R.string.ok, null).create().show();
			}
			//保存档次请求的时间戳到缓存
			ai.latestReqTime_fans = response.user_time;
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
						Requester2.requestMyFansList(handler, null, otherUserID);
					} else {
						Requester2.requestMyFansList(handler, null, userID);
					}
					Log.e("ai.latestReqTime", ai.latestReqTime_fans + "");
					//保存档次请求的时间戳到缓存
					//ai.latestReqTime = (new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")).format(new Date());
				} catch (Exception e) {
					e.printStackTrace();
				}
				lv = listView.getRefreshableView();
				mfd = new MyFansAdapter(list);
				lv.setAdapter(mfd);
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
	
	class MyFansAdapter extends BaseAdapter{
		//使用开源的webimageloader
		private DisplayImageOptions options;
		protected ImageLoader imageLoader;
		private ImageLoadingListener animateFirstListener;

		ArrayList<GsonResponse2.myfanslistUsers> list;
		
		public MyFansAdapter(ArrayList<GsonResponse2.myfanslistUsers> list){
			this.list=list;
			animateFirstListener = new AnimateFirstDisplayListener();
			imageLoader = ImageLoader.getInstance();
			
			options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.moren_touxiang)
			.showImageForEmptyUri(R.drawable.moren_touxiang)
			.showImageOnFail(R.drawable.moren_touxiang)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			//.displayer(new SimpleBitmapDisplayer())
			.displayer(new CircularBitmapDisplayer()) //圆形图片
			//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
			.build();
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
			GsonResponse2.myfanslistUsers item = list.get(position);
			
			viewHolder holder;
			if(null==convertView){
				convertView=inflater.inflate(R.layout.activity_person_fans_list_item, null);
				holder=new viewHolder();
				holder.pic=(ImageView) convertView.findViewById(R.id.iv_pic);
				holder.name=(TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else{
				holder=(viewHolder) convertView.getTag();
			}
			
/*			holder.pic.setLoadingDrawable(R.drawable.temp_local_icon);
			holder.pic.setImageUrl(item.headimageurl, 1, true);*/
			if(item!=null && item.headimageurl!=null && item.headimageurl.length()>0){	
				imageLoader.displayImage(item.headimageurl, holder.pic, options, animateFirstListener, ActiveAccount.getInstance(PersonFansActivity.this).getUID(), 1);
			}else{
				holder.pic.setImageResource(R.drawable.moren_touxiang);
			}
			
			holder.name.setText(item.nickname);
			return convertView;
		}
	}
	
	static class viewHolder{
		ImageView pic;
		TextView name;
	}
	
	
	@Override
	protected void onDestroy() {
		persistMsg();
		super.onDestroy();
	}

	private void persistMsg(){
		if(ISDEBUG)Log.d(TAG, "persistMsg");
		if(KEY!=null)
			StorageManager.getInstance().putItem(KEY, list, ArrayList.class);
	}
	
	private static final String FANS_PERSIST_KEY="fans_persist_key";
	private static String KEY=null;
	private ArrayList<GsonResponse2.myfanslistUsers> getMsg(){
		if(ISDEBUG)Log.d(TAG, "getMsg");
		String uid = ActiveAccount.getInstance(this).getUID();
		if(null==uid){
			Log.e(TAG, "uid is null");
			return new ArrayList<GsonResponse2.myfanslistUsers>();
		}
		KEY = uid+"_"+FANS_PERSIST_KEY;
		ArrayList<GsonResponse2.myfanslistUsers> list=(ArrayList<GsonResponse2.myfanslistUsers>) StorageManager.getInstance().getItem(KEY, new TypeToken<ArrayList<GsonResponse2.myfanslistUsers>>(){}.getType());
		if (null==list) {
			list=new ArrayList<GsonResponse2.myfanslistUsers>();
		}
		return list;
	}

	@Override
	public void onRefresh() {
		if (!isShowAllFans) {
			if (otherUserID != null) {
				Requester2.requestMyFansList(handler, ai.latestReqTime_fans, otherUserID);
			}else{
				Requester2.requestMyFansList(handler, ai.latestReqTime_fans, userID);
			}
		}else{
			listView.onRefreshComplete();
			Toast.makeText(PersonFansActivity.this, "没有更多粉丝",Toast.LENGTH_SHORT).show();
		}
	}
}
