package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.login.BindingMobileNoActivity;
import com.cmmobi.looklook.common.adapter.ShareLookLookFriendsAdapter;
import com.cmmobi.looklook.common.adapter.ShareLookLookFriendsRecentAndSearchAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest3.AddrBook;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.fragment.VShareFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.utils.PinYinUtil;
import com.google.gson.Gson;

public class ShareLookLookFriendsActivity extends ZActivity implements
		OnItemClickListener, Callback{

	public static final int MSG_WHAT = 0;

	private View contentView;

	ListView lv_friends;
	private QuickBarView quickBarView;
	
	private ImageView iv_back;
	private TextView tv_invite;
	
	private ListView lv_search;
	private List<WrapUser> searchUsers = new ArrayList<WrapUser>();
	private EditText et_search;
	private TextView tv_search;

	private String userID;
	private AccountInfo accountInfo;

	private List<WrapUser> wrapUserList = new ArrayList<WrapUser>();
	private ShareLookLookFriendsAdapter friendsAdapter;
	private List<WrapUser> recentUsers = new ArrayList<WrapUser>();
	private List<WrapUser> allUsers = new ArrayList<WrapUser>();
	
	private ShareLookLookFriendsRecentAndSearchAdapter searchFriendsAdapter;
	private ArrayList<UserObj> defaultName = null;
	private boolean isFromVshare = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_looklook_friends);
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("@给谁看");
		lv_friends = (ListView) findViewById(R.id.friends_circle_contacts_list);		
		quickBarView = (QuickBarView) findViewById(R.id.quick_bar);
		quickBarView.setListView(lv_friends);
		friendsAdapter = new ShareLookLookFriendsAdapter(
				this, handler);
		lv_friends.setAdapter(friendsAdapter);
		lv_friends.setOnItemClickListener(this);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_invite = (TextView) findViewById(R.id.tv_invite);
		iv_back.setOnClickListener(this);
		tv_invite.setOnClickListener(this);
		
		isFromVshare = getIntent().getBooleanExtra(VShareFragment.IS_FROM_VSHARE, false);
		defaultName = getIntent().getParcelableArrayListExtra(ShareDiaryActivity.INTENT_ACTION_SHARE_DIARY_LIST);
		if(defaultName == null)
		{
			defaultName = new ArrayList<UserObj>();
		}
		
		if(isFromVshare){
			tv_invite.setEnabled(false);
		}
		
		lv_search = (ListView)findViewById(R.id.lv_friends_search);
		searchFriendsAdapter = new ShareLookLookFriendsRecentAndSearchAdapter(this, handler);
		lv_search.setAdapter(searchFriendsAdapter);
		lv_search.setOnItemClickListener(this);
		
		et_search = (EditText) findViewById(R.id.et_share_friend_search);
		tv_search = (TextView) findViewById(R.id.tv_share_search);
		tv_search.setOnClickListener(this);
		
		if(accountInfo.setmanager.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, LoginSettingManager.BINDING_INFO_POINTLESS) == null){
			Intent phonebind = new Intent(this, BindingMobileNoActivity.class);
			phonebind.putExtra("isfinish", true);
			startActivity(phonebind);
		}else if(!accountInfo.isPhonePromt){
			Prompt.Dialog(this, true, "提示","请访问手机通讯录，可以@更多好友", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							accountInfo.isPhonePromt = true;
							Looper.prepare();
							getContactInfo();
						}
					}).start();
				}
			});
		}else if(ZNetworkStateDetector.isAvailable()){
			Requester3.phoneBook(handler);
		}else{
			loadData();
		}
	}


	private void loadData() {
		SortTask task = new SortTask();
		task.execute();
	}
	
	// 搜索结果表排序
    class SortTask extends AsyncTask<Void, Integer, Void> {  
        // 可变长的输入参数，与AsyncTask.exucute()对应  
        @Override  
        protected Void doInBackground(Void... param) {  
            try {  
            	wrapUserList.clear();
        		wrapUserList.addAll(accountInfo.friendsListName.getCache());
        		wrapUserList.addAll(accountInfo.phoneUsers.getCache());
        		for (int i = 0; i < wrapUserList.size(); i++) {

        			WrapUser wrapUser = wrapUserList.get(i);
        			if (!(null != wrapUser.sortKey && wrapUser.sortKey.length() > 0)) {
        				if(wrapUser.markname != null && !wrapUser.markname.equals("")){
        					wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.markname);
        				}else if(wrapUser.nickname != null && !wrapUser.nickname.equals("")){
        					wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.nickname);
        				}else if(wrapUser.phonename != null && !wrapUser.phonename.equals("")){
        					wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.phonename);;
        				}else if(wrapUser.telname !=null && !wrapUser.telname.isEmpty()){
							wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.telname);
						}else{
        					wrapUser.sortKey = "";
        				}
        			}
        		}
        		
        		ContactsComparator cmp = new ContactsComparator();
        		Collections.sort(wrapUserList, cmp);

        		
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            
            return null;
        }  
        
        
        @Override  
        protected void onPostExecute(Void result) {  
            // 返回HTML页面的内容   
        	recentUsers = accountInfo.recentContactManager.getCache();
    		allUsers.clear();
    		allUsers.addAll(recentUsers);
    		allUsers.addAll(wrapUserList);
    		friendsAdapter.setData(allUsers, defaultName);
    		friendsAdapter.notifyDataSetChanged();
    		if(isFromVshare){
	    		if(friendsAdapter.to_invite.size() == 0){
	    			tv_invite.setEnabled(false);
	    		}else{
	    			tv_invite.setEnabled(true);
	    		}
    		}
        }  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
            // 更新进度  
        }  
    }
	
	//type 0 loaddata,  1 search, 2 friendlist
	private void updateData(int type, WrapUser wrapUser){
		lv_search.setVisibility(View.GONE);
		lv_friends.setVisibility(View.VISIBLE);
		quickBarView.setVisibility(View.VISIBLE);
		
		switch (type) {
		case 0:
			
			break;
		case 1:	
			defaultName.clear();
			if(!findSearchInviteUser(wrapUser)){
				removeFriendInviteUser(wrapUser);				
			}else{
				defaultName.addAll(searchFriendsAdapter.to_invite);
			}
			defaultName.addAll(friendsAdapter.to_invite);
			break;
		case 2:
			defaultName.clear();
			defaultName.addAll(friendsAdapter.to_invite);
			break;
		default:
			break;
		}
		
		friendsAdapter.setData(allUsers, defaultName);
		friendsAdapter.notifyDataSetChanged();
		if(isFromVshare){
			if(friendsAdapter.to_invite.size() == 0){
				tv_invite.setEnabled(false);
			}else{
				tv_invite.setEnabled(true);
			}
		}
	}

	
	private void removeFriendInviteUser(WrapUser wrapUser){
		for(int i=0; i< friendsAdapter.to_invite.size(); i++){
			if(wrapUser.userid!=null && !wrapUser.userid.isEmpty()){
				if(wrapUser.userid.equals(friendsAdapter.to_invite.get(i).userid)){
					friendsAdapter.to_invite.remove(i);
					break;
				}
			}else{
				if(wrapUser.phonenum.equals(friendsAdapter.to_invite.get(i).user_tel)&& wrapUser.phonename.equals(friendsAdapter.to_invite.get(i).user_telname)){
					friendsAdapter.to_invite.remove(i);
					break;
				}
			}
		}
	}
	
	private Boolean findSearchInviteUser(WrapUser wrapUser){
		for(int i=0; i< searchFriendsAdapter.to_invite.size(); i++){
			if(wrapUser.userid!=null && !wrapUser.userid.isEmpty()){
				if(wrapUser.userid.equals(searchFriendsAdapter.to_invite.get(i).userid)){
					return true;
				}
			}else{
				if(wrapUser.phonenum.equals(searchFriendsAdapter.to_invite.get(i).user_tel)&& wrapUser.phonename.equals(searchFriendsAdapter.to_invite.get(i).user_telname)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_POST_ADDRESSBOOK:
			ZDialog.dismiss();
			if(msg.obj != null){
				GsonResponse3.postAddressBookResponse response = (GsonResponse3.postAddressBookResponse)msg.obj;
				if(response.status!= null && response.status.equals("0")){
					Requester3.phoneBook(handler);
				}else{
					if (response != null
							&& response.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(response.crm_status)], null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}else{
				Prompt.Dialog(this, false, "提示", "网络不给力",null);
			}
			break;
		case Requester3.RESPONSE_TYPE_PHONE_BOOK:
			ZDialog.dismiss();
			if(msg.obj != null){
				accountInfo.phoneUsers.clearList();
				ArrayList<WrapUser> wrapUsersPhone = new ArrayList<WrapUser>();
				GsonResponse3.phoneBookResponse response = (GsonResponse3.phoneBookResponse) msg.obj;
				if(response!=null && response.status!=null && response.status.equals("0")){
					for(int i=0; i< response.users.length; i++){
						wrapUsersPhone.add(response.users[i]);
					}
					accountInfo.phoneUsers.addMembers(wrapUsersPhone);
					loadData();
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
		default:
			break;
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
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
			case R.id.friends_circle_contacts_list:
				friendsAdapter.setItemSelected(view, position);
				updateData(2, friendsAdapter.getItem(position));
				break;
			case R.id.lv_friends_search:
				searchFriendsAdapter.setItemSelected(view, position);
				updateData(1, searchFriendsAdapter.getItem(position));
			default:
				break;
			}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			this.setResult(RESULT_CANCELED);
			this.finish();
			break;
		case R.id.tv_invite:
			ArrayList<UserObj> toInvite = new ArrayList<UserObj>();
			toInvite.addAll(friendsAdapter.to_invite);
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("invite_list", toInvite);
			this.setResult(RESULT_OK, intent);
			this.finish();
			if (isFromVshare) {
				Intent shareIntent = new Intent(this, ShareDiaryActivity.class);
				shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
				UserObj[] users = toInvite.toArray(new UserObj[toInvite.size()]);
				if(users != null){
					shareIntent.putExtra(ShareDiaryActivity.INTENT_ACTION_SHARE_DIARY_LIST, new Gson().toJson(users));
				}
				
				if (ShareDiaryActivity.diaryGroup!= null && ShareDiaryActivity.diaryGroup.size() > 0) {
					MyDiary[] diarys = ShareDiaryActivity.diaryGroup.toArray(new MyDiary[ShareDiaryActivity.diaryGroup.size()]);
					if (diarys != null) {
						shareIntent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, new Gson().toJson(diarys));
					}
				}
				
				startActivity(shareIntent);
			}
			break;
		case R.id.tv_share_search:
			String searchString = et_search.getText().toString().trim();
			if(searchString.isEmpty()){
				updateData(0, null);
			}else{
				searchUsers.clear();
				for(int i=0; i<wrapUserList.size(); i++){
					if((wrapUserList.get(i).nickname!=null && wrapUserList.get(i).nickname.toLowerCase().contains(searchString.toLowerCase())) || (wrapUserList.get(i).markname!=null && wrapUserList.get(i).markname.toLowerCase().contains(searchString.toLowerCase()))|| (wrapUserList.get(i).phonename!=null && wrapUserList.get(i).phonename.toLowerCase().contains(searchString.toLowerCase()))){
						searchUsers.add(wrapUserList.get(i));
					} 
				}
				ContactsComparator cmp = new ContactsComparator();
				Collections.sort(this.searchUsers, cmp);
				searchFriendsAdapter.setData(searchUsers, friendsAdapter.to_invite);
				searchFriendsAdapter.notifyDataSetChanged();
				if(isFromVshare){
					if(friendsAdapter.to_invite.size() == 0){
						tv_invite.setEnabled(false);
					}else{
						tv_invite.setEnabled(true);
					}
				}
				lv_search.setVisibility(View.VISIBLE);
				lv_friends.setVisibility(View.GONE);
				quickBarView.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}
	
	private static final String[] PHONES_PROJECTION = new String[]{ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
	protected void getContactInfo() {
		ArrayList<AddrBook> addrBooks = new ArrayList<AddrBook>();
		try {
			// 从本机中取号
			// 得到ContentResolver对象
			ContentResolver cr = this.getContentResolver();
			Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null,
					null);
			// 让activity管理游标
			this.startManagingCursor(cursor);
			int count=0;
			while (cursor.moveToNext()) {
				count++;
				// 获取联系人姓名在表的中列的位置
				int phoneName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				// 获取联系人号码在表的中列的位置
				int phoneNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				// 取得联系人名字
				String contactName = cursor.getString(phoneName).trim();
				String contactNumber = cursor.getString(phoneNumber).trim();

				// 如果缺省为null 跳出当前循环
				if (contactNumber == null) {
					continue ;
				}
				if (contactName == null ) {
					continue ;
				}

				String phoneRegex = getNumber(contactNumber);
				if (phoneRegex == null || phoneRegex.equals("")) continue ;
				
	  			AddrBook oneAddrBook = new AddrBook();
				oneAddrBook.phone_name = contactName;
				oneAddrBook.phone_num = phoneRegex;
				if(!addrBooks.contains(oneAddrBook)){
					addrBooks.add(oneAddrBook);
				}else{
					for(int i=0; i< addrBooks.size();i++){
						if(addrBooks.get(i).phone_name.equals(oneAddrBook.phone_name)){
							addrBooks.get(i).phone_num = addrBooks.get(i).phone_num + "," + oneAddrBook.phone_num;
							break;
						}
					}
				}
			}			
			if(addrBooks.isEmpty()){
				/*this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Prompt.Dialog(ShareLookLookFriendsActivity.this, false, "提示", "通讯录里没有有效的手机号码",null);
					}
				});*/
				Requester3.phoneBook(handler);
			}else{
				AddrBook[] addrBooksArray;
				addrBooksArray = addrBooks.toArray(new AddrBook[addrBooks.size()]);
				this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ZDialog.show(R.layout.progressdialog, false, true, ShareLookLookFriendsActivity.this);
					}
				});
				Requester3.postAddressBook(handler,"", addrBooksArray);
			}
		} catch (Exception e) {
			e.printStackTrace();// TODO just for debug
		}
	}

	
	//还原11位手机号 包括去除“-”
	public static String getNumber(String num2) {
		String num;
		if (num2 != null) {
		
			num = num2.replaceAll("\\D", "");
//			if (num.startsWith("+86")) {
//				num = num.substring(3);
//			} else 
			if (num.startsWith("86")) {
				num = num.substring(2);
			} else if (num.startsWith("17951")) {
				num = num.substring(5);
			} else if (!num.startsWith("1") && num.length()>11) {
					num = num.substring(num.length()-11, num.length());
			}
			if(!Prompt.checkPhoneNum(num)) {
				num = "";
			}
		} else {
			num = "";
		}
		return num;
	}
}