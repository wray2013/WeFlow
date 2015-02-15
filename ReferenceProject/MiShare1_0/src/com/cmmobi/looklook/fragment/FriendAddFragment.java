package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendAddPhoneActivity;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.activity.login.BindingMobileNoActivity;
import com.cmmobi.looklook.common.adapter.FriendsSeacherAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest3.AddrBook;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.searchUserResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
//import com.cmmobi.looklook.common.service.CommonService;

public class FriendAddFragment extends Fragment implements Callback, OnClickListener, OnItemClickListener{
	private static final String TAG = "FriendAddActivity";
	
	private EditText searchEditText;
	private TextView searchTextView;

	private ListView listView;
	private FriendsSeacherAdapter adapter;
	private WrapUser[] users;
	
	private RelativeLayout phoneAdd;
	
	MyBind myBind;
	private LoginSettingManager lsm;
	private String userID;
	private AccountInfo accountInfo;
	protected String searchString;
	
	private Context context;
	private Handler handler;
	
	public LinearLayout ll_contacts;
	
	public TextView tvNotFound;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(
				R.layout.activity_friends_add, null);
		context = this.getActivity();
		
		ll_contacts= (LinearLayout) view.findViewById(R.id.ll_contacts);
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null) {
			lsm = accountInfo.setmanager;
		}

		phoneAdd = (RelativeLayout) view.findViewById(R.id.rl_phonebook);
		searchEditText = (EditText) view.findViewById(R.id.et_friend_search);
		searchTextView = (TextView) view.findViewById(R.id.tv_search);
		listView = (ListView) view.findViewById(R.id.activites_list);
		
		searchTextView.setOnClickListener(this);

		tvNotFound = (TextView) view.findViewById(R.id.tv_notfound);
		
		adapter = new FriendsSeacherAdapter(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		handler = new Handler(this);
		
		phoneAdd.setOnClickListener(this);

		
		return view;
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isVisible()){
		listView.setVisibility(View.GONE);
		tvNotFound.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(!hidden){
			listView.setVisibility(View.GONE);
			tvNotFound.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		//accountInfo.privateMsgManger.hSubScript.t_snsfriend = accountInfo.t_snsList;
		super.onStop();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_phonebook:
			MyBind phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (phonebindstate == null || phonebindstate.binding_info == null) {
				Intent phonebind = new Intent(context,BindingMobileNoActivity.class);
				phonebind.putExtra("isfinish", true);
				startActivity(phonebind);
			}else{
				if(accountInfo.isPhonePromt && !ZNetworkStateDetector.isAvailable()){
					Intent intentPhone = new Intent(context, FriendAddPhoneActivity.class);
					startActivity(intentPhone);
				}else if(accountInfo.isPhonePromt){
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();
							getContactInfo();
						}
					}).start();
				}else {
					String msg = "为了方便找到好友需要访问通讯录";
					Prompt.Dialog(context, true, "提示", msg,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
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

							}, R.string.phone_positive, R.string.phone_negative);
				}
			}	
			break;
		case R.id.tv_search:
			hideSystemKeyBoard(searchEditText);
			String info = searchEditText.getText().toString().trim();
			if(info.isEmpty()){
				listView.setVisibility(View.GONE);
				tvNotFound.setVisibility(View.GONE);
			}else{
				Requester3.searchUser(handler, searchEditText.getText().toString().trim(), "2");
				ZDialog.show(R.layout.progressdialog, false, true, getActivity());
			}
			searchEditText.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_POST_ADDRESSBOOK:
			ZDialog.dismiss();
			if(msg.obj != null){
				GsonResponse3.postAddressBookResponse response = (GsonResponse3.postAddressBookResponse)msg.obj;
				if(response.status!= null && response.status.equals("0")){
					Intent intentPhone = new Intent(context, FriendAddPhoneActivity.class);
					startActivity(intentPhone);
				}else{
					if (response != null
							&& response.status.equals("200600")) {
						Prompt.Dialog(context, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(response.crm_status)], null);
					} else {
						Prompt.Dialog(context, false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}else{
				Prompt.Dialog(context, false, "提示", "网络不给力",null);
			}
			break;
		case Requester3.RESPONSE_TYPE_SEARCH_USER:
			ZDialog.dismiss();
			if (msg.obj != null) {
				searchUserResponse response = (searchUserResponse) msg.obj;
				listView.setVisibility(View.VISIBLE);
				if ("0".equals(response.status)) {
					users = response.users;
					adapter.setData(users);
					adapter.notifyDataSetChanged();
				}
				
				if(response.users.length == 0){
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
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null) {
				addfriendResponse response = (addfriendResponse) msg.obj;
				if ("0".equals(response.status)) {
					if(response.target_userid == null || null == users || users.length == 0) {
						break;
					}
					for (int i = 0; i < users.length; i++) {
						WrapUser user = users[i];
						if (response.target_userid.equals(user.userid)) {
							user.request_status = "2";
							user.update_time = System.currentTimeMillis() + "";
							accountInfo.friendsRequestList.insertMember(0, user);
							Prompt.Alert(context, "好友申请已发送");
							break;
						}
					}
				}
			}
			break;
		}
		return false;
	}
	
	private static final String[] PHONES_PROJECTION = new String[]{ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
	protected void getContactInfo() {
		ArrayList<AddrBook> addrBooks = new ArrayList<AddrBook>();
		try {
			// 从本机中取号
			// 得到ContentResolver对象
			ContentResolver cr = context.getContentResolver();
			Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null,
					null);
			// 让activity管理游标
			getActivity().startManagingCursor(cursor);
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
					Boolean isNameDiff = true;
					for(int i=0; i< addrBooks.size();i++){
						if(addrBooks.get(i).phone_name.equals(oneAddrBook.phone_name)){
							addrBooks.get(i).phone_num = addrBooks.get(i).phone_num + "," + oneAddrBook.phone_num;
							isNameDiff = false;
							break;
						}
					}	
					
					if(isNameDiff){
						addrBooks.add(oneAddrBook);	
					}
				}
			}			
			if(addrBooks.isEmpty()){
				/*getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Prompt.Dialog(context, false, "提示", "通讯录里没有有效的手机号码",null);
					}
				});*/
				Intent intentPhone = new Intent(context, FriendAddPhoneActivity.class);
				startActivity(intentPhone);
			}else{
				AddrBook[] addrBooksArray;
				addrBooksArray = addrBooks.toArray(new AddrBook[addrBooks.size()]);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ZDialog.show(R.layout.progressdialog, false, true, context);
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


	public void hideSystemKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (null != users && users.length > 0) {
			final WrapUser user = users[position];
			if (null != user && !(user.userid.equals(ActiveAccount.getInstance(getActivity()).getUID()))) {
				if(accountInfo.friendsListName.findUserByUserid(user.userid) !=null){
				Intent intent = new Intent(getActivity(),
						OtherZoneActivity.class);
				intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, user.userid);
				//intent.putExtra("nickname", user.nickname);
				this.startActivity(intent);
				}else{
					if(TextUtils.isEmpty(accountInfo.nickname)){
						//修改个信息
						Intent shareIntent = new Intent(context, SettingPersonalInfoActivity.class);
						context.startActivity(shareIntent);	
					}else{
						new XEditDialog.Builder(context)
						.setTitle(R.string.xeditdialog_title)
						.setPositiveButton(R.string.send, new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								//加好友
								Requester3.addFriend(handler, user.userid, v.getTag().toString());
							}
						})
						.setNegativeButton(android.R.string.cancel, null)
						.create().show();
					}
				}
			}
		}
	}

	/* //点击EditText以外的任何区域隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
            View v = rl_search;
            if (isShouldHideInput(v, ev)) {
                if(hideInputMethod(getActivity(), v)) {
                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);   
    }     
    
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }    
    
    public static Boolean hideInputMethod(Context context, View v) {
	InputMethodManager imm = (InputMethodManager) context
		.getSystemService(Context.INPUT_METHOD_SERVICE);
	if (imm != null) {
		return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	return false;
    }
    */

}