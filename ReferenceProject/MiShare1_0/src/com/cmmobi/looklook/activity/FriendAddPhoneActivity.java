package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.PhoneSortAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest3.Invate_users;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.utils.PinYinUtil;

public class FriendAddPhoneActivity extends ZActivity implements OnItemClickListener{
	private static final String TAG = "FriendAddPhoneActivity";
	private ListView lv_phone;
	private PhoneSortAdapter phoneAdapter;
	public ArrayList<WrapUser> inviteUsers = new ArrayList<WrapUser>();
	private QuickBarView quickBarView;
	
	private TextView tv_invite;
	AccountInfo accountInfo;
	
	private ListView lv_search;
	private List<WrapUser> searchUsers = new ArrayList<WrapUser>();
	private EditText et_search;
	private TextView tv_search;
	private PhoneSortAdapter searchAdapter;
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_PHONE_BOOK:
			ZDialog.dismiss();
			if(msg.obj != null){
				GsonResponse3.phoneBookResponse response = (GsonResponse3.phoneBookResponse) msg.obj;
				if(response!=null && response.status!=null && response.status.equals("0")){
					accountInfo.phoneUsers.clearList();
					for(int i=0; i< response.users.length; i++){
						accountInfo.phoneUsers.addMember(response.users[i]);
					}
					PhoneSortTask task = new PhoneSortTask();
					task.execute();
				}else{
					if (response != null
							&& response.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(response.crm_status)],
								null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_INVATEPHONEADDRESS:
			if(msg.obj != null){
				GsonResponse3.invatePhoneAddressResponse response = (GsonResponse3.invatePhoneAddressResponse) msg.obj;
				if(response!=null && response.status!=null && response.status.equals("0")){
					System.out.println("==发送成功");
				}else{
					if (response != null
							&& response.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(response.crm_status)],
								null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_activity_friends_back:
			finish();
			break;
		case R.id.tv_phone_invite:
			String phonenums = "";
			Invate_users[] users = new Invate_users[inviteUsers.size()];
			for(int i=0; i< inviteUsers.size(); i++){
				phonenums = phonenums + inviteUsers.get(i).phonenum + ",";
				users[i] = new Invate_users();
				users[i].user_name = inviteUsers.get(i).phonename;
				users[i].user_phone = inviteUsers.get(i).phonenum;
			}
			Requester3.invatePhoneAddress(handler, users);
			Uri uri = Uri.parse("smsto:" + phonenums);            
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);   
			it.putExtra("sms_body", accountInfo.nickname + "邀请你加入他的小圈子，看看他都干了些什么，下载地址： " + Html.fromHtml("<a href=\"www.mishare.cn\">mishare.cn</a>"));            
			startActivity(it);  
			break;
		case R.id.tv_share_search:
			String searchString = et_search.getText().toString().trim();
			if(searchString.isEmpty()){
				lv_search.setVisibility(View.GONE);
				lv_phone.setVisibility(View.VISIBLE);
				quickBarView.setVisibility(View.VISIBLE);
			}else{
				searchUsers.clear();
				for(int i=0; i<phoneAdapter.getCount(); i++){
					if((phoneAdapter.getItem(i).phonename!=null && phoneAdapter.getItem(i).phonename.toLowerCase().contains(searchString.toLowerCase())) || (phoneAdapter.getItem(i).phonenum!=null && phoneAdapter.getItem(i).phonenum.toLowerCase().contains(searchString.toLowerCase()))){
						searchUsers.add(phoneAdapter.getItem(i));
					} 
				}
				ContactsComparator cmp = new ContactsComparator();
				Collections.sort(this.searchUsers, cmp);
				searchAdapter.setData(searchUsers, inviteUsers);
				searchAdapter.notifyDataSetChanged();
				lv_search.setVisibility(View.VISIBLE);
				lv_phone.setVisibility(View.GONE);
				quickBarView.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_add_phone);
		ZViewFinder finder = getZViewFinder();
		finder.setOnClickListener(R.id.lv_activity_friends_back, this);
		findViewById(R.id.lv_activity_friends_back).setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FriendAddPhoneActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				FriendAddPhoneActivity.this.finish();
				return false;
			}
		});
		accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());
		
		lv_phone = (ListView) findViewById(R.id.lv_phone);
		phoneAdapter = new PhoneSortAdapter(this, true);
		lv_phone.setAdapter(phoneAdapter);
		lv_phone.setOnItemClickListener(this);
		
		tv_invite = (TextView) findViewById(R.id.tv_phone_invite);
		tv_invite.setOnClickListener(this);
		
		quickBarView = (QuickBarView) findViewById(R.id.quick_bar);

		quickBarView.setListView(lv_phone);
		if(ZNetworkStateDetector.isAvailable()){
			Requester3.phoneBook(handler);
			ZDialog.show(R.layout.progressdialog, false, true, this);
		}else{
			phoneAdapter.setData(accountInfo.phoneUsers.getCache(), inviteUsers);	
			phoneAdapter.notifyDataSetChanged();
		}
		
		lv_search = (ListView)findViewById(R.id.lv_search);
		searchAdapter = new PhoneSortAdapter(this, false);
		lv_search.setAdapter(searchAdapter);
		lv_search.setOnItemClickListener(this);
		
		et_search = (EditText) findViewById(R.id.et_share_friend_search);
		tv_search = (TextView) findViewById(R.id.tv_share_search);
		tv_search.setOnClickListener(this);
		
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.lv_phone:
			phoneAdapter.setItemSelected(view, position);
			inviteUsers.clear();
			inviteUsers.addAll(phoneAdapter.to_invite);
			break;
		case R.id.lv_search:
			searchAdapter.setItemSelected(view, position);
			if(inviteUsers.size() >0 && inviteUsers.contains(searchAdapter.getItem(position))){
				inviteUsers.remove(searchAdapter.getItem(position));
			}else{
				inviteUsers.add(searchAdapter.getItem(position));
			}
			phoneAdapter.setData(accountInfo.phoneUsers.getCache(), inviteUsers);
			phoneAdapter.notifyDataSetChanged();
			lv_search.setVisibility(View.GONE);
			lv_phone.setVisibility(View.VISIBLE);
			quickBarView.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		if(inviteUsers.size()>0){
			tv_invite.setClickable(true);
			tv_invite.setTextColor(getResources().getColor(R.color.blue));
		}else{
			tv_invite.setClickable(false);
			tv_invite.setTextColor(getResources().getColor(R.color.gray));
		}
	}
	
	// 手机通讯录列表排序
    class PhoneSortTask extends AsyncTask<Void, Integer, Void> {  
        // 可变长的输入参数，与AsyncTask.exucute()对应  
        @Override  
        protected Void doInBackground(Void... param) {  
            try {  
            	AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID());
            	List<WrapUser> listUsers = accountInfo.phoneUsers.getCache();	
				for (int i = 0; i < listUsers.size(); i++) {
					WrapUser wrapUser = listUsers.get(i);
					if(wrapUser.phonename != null && !wrapUser.phonename.equals("")){
						wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.phonename);
					}else{
						wrapUser.sortKey = "";
					}	
				}
				ContactsComparator cmp = new ContactsComparator();
				Collections.sort(listUsers, cmp);
				
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            
            return null;
        }  
        
        
        @Override  
        protected void onPostExecute(Void result) {  
            // 返回HTML页面的内容   
			phoneAdapter.setData(accountInfo.phoneUsers.getCache(), inviteUsers);	
			phoneAdapter.notifyDataSetChanged();
        }  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
            // 更新进度  
        }  
    }
}