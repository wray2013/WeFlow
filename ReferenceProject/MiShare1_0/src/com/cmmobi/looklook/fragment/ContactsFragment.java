package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.Collections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsRequestActivity;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.common.adapter.ContactsSortAdapter;
import com.cmmobi.looklook.common.adapter.ContactsSortAdapter.ViewHolder;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.deleteFriendResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.searchUserResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.receiver.UserDatasMessageReceiver;
import com.cmmobi.sns.utils.PinYinUtil;

public class ContactsFragment extends Fragment implements OnClickListener, Callback {

	private View contentView;
	private RelativeLayout rl_request;
	private RelativeLayout rl_service;
	private LinearLayout ll_contactsfragment;
	
	private AccountInfo accountInfo;
	
	private ImageView iv_jiaoBiao_request;
	private TextView tv_count_request;

	private Handler handler;
	
	private AutoCompleteTextView searchEditText;
	private TextView searchTextView;
	protected String searchString;
	private ListView listView;
	public TextView tvNotFound;
	
	
	private PopupWindow pw_clear; //清除
	private LayoutInflater inflater;
	private View currentView;
	private String currentUserid;
	
	private ArrayList<WrapUser> users = new ArrayList<WrapUser>();
	private ContactsSortAdapter searchAdapter;
	
	private ContactsSortAdapter friendsCircleContactsAdapter;
	private ListView friendsCircleContactsListView;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {    
			    // Get extra data included in the Intent
				if(LookLookActivity.CONTACTS_REFRESH_DATA.equals(intent.getAction())){			
					setCount();
					if(searchEditText == null) return;
					if(LookLookActivity.FRIEND_LIST_CHANGE.equals(intent.getExtras().get(LookLookActivity.CONTACTS_REFRESH_KEY))){			
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line, accountInfo.friendsListName.getStrings());
						searchEditText.setAdapter(adapter);
						friendsCircleContactsAdapter.setData(accountInfo.friendsListName.getCache());
			    		friendsCircleContactsAdapter.notifyDataSetChanged();
					}		
				}
			}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(getActivity()).getUID());
		handler = new Handler(this);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
			      new IntentFilter(LookLookActivity.CONTACTS_REFRESH_DATA));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(
				R.layout.fragment_friends_circle_contacts, null);
		this.inflater = inflater;	
		ll_contactsfragment = (LinearLayout) contentView.findViewById(R.id.ll_contactsfragment);
		rl_request = (RelativeLayout) contentView.findViewById(R.id.rl_request);
		rl_service = (RelativeLayout) contentView.findViewById(R.id.rl_service);
		rl_request.setOnClickListener(this);
		rl_service.setOnClickListener(this);
		
		iv_jiaoBiao_request = (ImageView) contentView.findViewById(R.id.iv_jiaobiao_request);
		tv_count_request = (TextView) contentView.findViewById(R.id.tv_count_request);
		
		friendsCircleContactsListView = (ListView) contentView
				.findViewById(R.id.friends_circle_contacts_list);

		friendsCircleContactsAdapter = new ContactsSortAdapter(getActivity());

		friendsCircleContactsListView.setAdapter(friendsCircleContactsAdapter);

		friendsCircleContactsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentUserid = friendsCircleContactsAdapter.getItem(arg2).userid;
				ViewHolder holder = (ViewHolder)arg1.getTag();
				holder.rl_contact.setBackgroundColor(getResources().getColor(R.color.light_gray));
				currentView = holder.rl_contact;
				pw_clear.showAtLocation(ll_contactsfragment, Gravity.BOTTOM, 0, 0);
				return false;
			}
		});
		
		friendsCircleContactsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						OtherZoneActivity.class);
				intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, friendsCircleContactsAdapter.getItem(arg2).userid);
				startActivity(intent);
			}
		
		});
		
		friendsCircleContactsAdapter.setData(accountInfo.friendsListName.getCache());
		friendsCircleContactsAdapter.notifyDataSetChanged();
		
		searchEditText = (AutoCompleteTextView) contentView.findViewById(R.id.et_friend_search);
		searchTextView = (TextView) contentView.findViewById(R.id.tv_search);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_dropdown_item_1line, accountInfo.friendsListName.getStrings());
		searchEditText.setAdapter(adapter);
		
		listView = (ListView) contentView.findViewById(R.id.activites_list);
		
		searchTextView.setOnClickListener(this);

		tvNotFound = (TextView) contentView.findViewById(R.id.tv_notfound);
		
		searchAdapter = new ContactsSortAdapter(getActivity());
		initClearChoice();
		listView.setAdapter(searchAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (null != users && users.size() > 0) {
					WrapUser user = users.get(arg2);
					Intent intent = new Intent(getActivity(),
									OtherZoneActivity.class);
					intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, user.userid);
					startActivity(intent);
				}
			}
		});	
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentUserid = searchAdapter.getItem(arg2).userid;
				ViewHolder holder = (ViewHolder)arg1.getTag();
				holder.rl_contact.setBackgroundColor(getResources().getColor(R.color.light_gray));
				currentView = holder.rl_contact;
				pw_clear.showAtLocation(ll_contactsfragment, Gravity.BOTTOM, 0, 0);
				return false;
			}
		});
		
		return contentView;
	}
	
	//显示清除选择界面
		private void initClearChoice(){
			View view = inflater.inflate(R.layout.activity_vshare_list_clear_menu ,
					null);
			pw_clear = new PopupWindow(view, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true);
			pw_clear.setBackgroundDrawable(getResources().getDrawable(
					R.color.transparent));

			pw_clear.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					currentView.setBackgroundResource(R.drawable.bg_listview_item);
				}
			});
			view.findViewById(R.id.btn_joinsafe).setVisibility(View.GONE);
			Button btnClear = (Button)view.findViewById(R.id.btn_clear);
			btnClear.setBackgroundResource(R.drawable.btn_menu_one);
			btnClear.setOnClickListener(this);
			btnClear.setText("删除");
			btnClear.setTextColor(Color.RED);
			view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		}
		
	
	private void setCount() {
		if(accountInfo == null || tv_count_request == null || iv_jiaoBiao_request == null) return;
		if(accountInfo.newFriendRequestCount == 0){
			tv_count_request.setVisibility(View.GONE);
			iv_jiaoBiao_request.setVisibility(View.GONE);
		}else{
			tv_count_request.setText(accountInfo.newFriendRequestCount + "");
			tv_count_request.setVisibility(View.VISIBLE);
			iv_jiaoBiao_request.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(!hidden){
			accountInfo.newFriend = 0;
			setCount();			
			friendsCircleContactsListView.setVisibility(View.VISIBLE);
			friendsCircleContactsAdapter.setData(accountInfo.friendsListName.getCache());
			friendsCircleContactsAdapter.notifyDataSetChanged();
			listView.setVisibility(View.GONE);
			tvNotFound.setVisibility(View.GONE);
			getActivity().sendBroadcast(new Intent(UserDatasMessageReceiver.REFRESH_FRIEND_LIST));
		}
	}
	
	@Override
	public void onResume() {
		if(isVisible()){
		accountInfo.newFriend = 0;
		setCount();
		friendsCircleContactsListView.setVisibility(View.VISIBLE);
		friendsCircleContactsAdapter.setData(accountInfo.friendsListName.getCache());
		friendsCircleContactsAdapter.notifyDataSetChanged();
		listView.setVisibility(View.GONE);
		tvNotFound.setVisibility(View.GONE);
		getActivity().sendBroadcast(new Intent(UserDatasMessageReceiver.REFRESH_FRIEND_LIST));
		}
		super.onResume();
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
	}

	@Override
	public void onClick(View v) {

		Intent intent;
		switch (v.getId()) {
		case R.id.rl_request:
			intent = new Intent(this.getActivity(), FriendsRequestActivity.class);
			startActivity(intent);
			accountInfo.newFriendRequestCount = 0;
			Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
			setCount();
			Intent msgIntent2 = new Intent(
					LookLookActivity.CONTACTS_REFRESH_DATA);
			msgIntent2.putExtra(LookLookActivity.CONTACTS_REFRESH_KEY,
					LookLookActivity.FRIEND_NEWS_CHANGE);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
					msgIntent2);
			break;
		case R.id.rl_service:
			if(TextUtils.isEmpty(accountInfo.nickname)){
				//修改个信息
				Intent shareIntent = new Intent(this.getActivity(), SettingPersonalInfoActivity.class);
				startActivity(shareIntent);	
			}else{
				//客服
				Requester3.customer(handler);
			}
			break;
		case R.id.tv_search:
			hideSystemKeyBoard(searchEditText);
			String key =  searchEditText.getText().toString().trim();
			if(key.isEmpty()){
				friendsCircleContactsListView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				tvNotFound.setVisibility(View.GONE);
			}else{
				friendsCircleContactsListView.setVisibility(View.GONE);
				listView.setVisibility(View.GONE);
				users = accountInfo.friendsListName.getSearchUsers(searchEditText.getText().toString().trim());
				searchAdapter.setData(users);
				searchAdapter.setSearchString(searchEditText.getText().toString().trim());
				searchAdapter.notifyDataSetChanged();
				listView.setVisibility(View.VISIBLE);
				if(users.size()==0){
					tvNotFound.setVisibility(View.VISIBLE);
				}else{
					tvNotFound.setVisibility(View.GONE);
				}
				/*Requester3.searchUser(handler, searchEditText.getText().toString().trim(), "1");
				ZDialog.show(R.layout.progressdialog, false, true, getActivity());*/
			}
			searchEditText.setText("");
			break;
		case R.id.btn_clear:
			if(pw_clear.isShowing()){
				pw_clear.dismiss();
			}
			Requester3.deleteFriendRequest(handler, currentUserid);
			ZDialog.show(R.layout.progressdialog, false, true, getActivity());
			break;
		case R.id.btn_cancel:
			if(pw_clear.isShowing()){
				pw_clear.dismiss();
			}
			break;
		default:
			break;
		}
	}

	public void hideSystemKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_CUSTOMER:
			GsonResponse3.customerResponse cres = (GsonResponse3.customerResponse) msg.obj;
			if(cres !=null && "0".equals(cres.status)){
				Intent intent = new Intent(getActivity(),
						FriendsSessionPrivateMessageActivity.class);	
				accountInfo.serviceUser.nickname = cres.nickname;
				accountInfo.serviceUser.userid = cres.userid;
				accountInfo.serviceUser.headimageurl = cres.headimageurl;
				intent.putExtra("wrapuser", accountInfo.serviceUser.toString());// 压入数据
				startActivity(intent);
			}else{
				Prompt.Alert("网络状况不佳，请稍后再试");
			}
			break;
		case Requester3.RESPONSE_TYPE_SEARCH_USER:
			ZDialog.dismiss();
			if (msg.obj != null) {
				searchUserResponse sresponse = (searchUserResponse) msg.obj;
				users.clear();
				if ("0".equals(sresponse.status)) {
					for(int i=0; i< sresponse.users.length; i++){
						users.add(sresponse.users[i]);
					}
					SearchSortTask task = new SearchSortTask();
					task.execute();
				}else{
					Prompt.Alert("操作失败，请稍后再试");
				}
				
				if(users.size() == 0){
					tvNotFound.setVisibility(View.VISIBLE);
				}else{
					tvNotFound.setVisibility(View.GONE);
				}
			}else{
				Prompt.Alert("网络状况不佳，请稍后再试");
				listView.setVisibility(View.GONE);
				tvNotFound.setVisibility(View.VISIBLE);
			}
			break;
		case Requester3.RESPONSE_TYPE_DELETE_FRIEND:
			ZDialog.dismiss();
			if (msg.obj != null) {
				deleteFriendResponse dResponse = (deleteFriendResponse) msg.obj;
				if("0".equals(dResponse.status)){
					searchAdapter.removeUser(currentUserid);
					friendsCircleContactsAdapter.removeUser(currentUserid);
					accountInfo.friendsListName.removeMemberByUserid(currentUserid);
					//accountInfo.friendsRequestList.removeMemberByUserid(currentUserid);
					getActivity().sendBroadcast(new Intent(UserDatasMessageReceiver.REFRESH_FRIEND_REQUEST_LIST));
					accountInfo.friendNewsDataEntities.removeUserNews(currentUserid);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_dropdown_item_1line, accountInfo.friendsListName.getStrings());
					searchEditText.setAdapter(adapter);
				}else{
					Prompt.Alert("操作失败，请稍后再试");
				}
			}else{
				Prompt.Alert("网络状况不佳，请稍后再试");
			}
			break;
		default:
			break;
		}
		return false;
	}

	
	// 搜索结果表排序
    class SearchSortTask extends AsyncTask<Void, Integer, Void> {  
        // 可变长的输入参数，与AsyncTask.exucute()对应  
        @Override  
        protected Void doInBackground(Void... param) {  
            try {  
            	for (int i = 0; i < users.size(); i++) {

					WrapUser wrapUser = users.get(i);
						if(wrapUser.markname != null && !wrapUser.markname.equals("")){
							wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.markname);
						}else if(wrapUser.nickname != null && !wrapUser.nickname.equals("")){
							wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.nickname);
						}else if(wrapUser.telname !=null && !wrapUser.telname.isEmpty()){
							wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.telname);
						}else{
							wrapUser.sortKey = "";
						}
					
				}
				ContactsComparator cmp = new ContactsComparator();
				Collections.sort(users, cmp);
			
				
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            
            return null;
        }  
        
        
        @Override  
        protected void onPostExecute(Void result) {  
            // 返回HTML页面的内容   
        	searchAdapter.setData(users);
			searchAdapter.notifyDataSetChanged();
			listView.setVisibility(View.VISIBLE);
        }  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
            // 更新进度  
        }  
    }
}
