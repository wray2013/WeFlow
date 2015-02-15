package com.cmmobi.looklook.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;
import cn.zipper.framwork.device.ZScreen;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsAddPhoneAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.prompt.Prompt;

public class FriendAddPhoneActivity extends ZActivity implements OnItemClickListener{
	private static final String TAG = "FriendAddPhoneActivity";
	public ListView lv_alreadyin;
	private ListView lv_tobeinvite;
	private FriendsAddPhoneAdapter alreadyinAdapter;
	private FriendsAddPhoneAdapter tobeinviteAdapter;
	private TextView tv_alreadyin;
	private TextView tv_tobeinvite;
	public static String BROADCAST_ALREADYIN_DATA_CHANGED = "ALREADYIN_DATA_CHANGED";
	private BroadcastReceiver alreadyinDataChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			runOnUiThread(new Runnable() {
				public void run() {
					if(alreadyinAdapter.getCount() == 0){
						tv_alreadyin.setVisibility(View.GONE);
					}
					ViewGroup.LayoutParams params = lv_alreadyin.getLayoutParams(); 
					params.height = ZScreen.dipToPixels(77)* alreadyinAdapter.getCount(); //需要设置的listview的高度，你可以设置成一个定值，也可以设置成其他容器的高度，如果是其他容器高度，那么不要在oncreate中执行，需要做延时处理，否则高度为0 
					lv_alreadyin.setLayoutParams(params); 
				}
			});
			
		}
	};
	
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_PHONE_BOOK:
			if(msg.obj != null){
				GsonResponse2.phoneBookResponse response = (GsonResponse2.phoneBookResponse) msg.obj;
				if(response!=null && response.status!=null && response.status.equals("0")){
					for(int i=0; i< response.users.length; i++){
						if(response.users[i].isjoin.equals("1") && response.users[i].isattention.equals("0")){
							alreadyinAdapter.addData(response.users[i]);
						}else if(response.users[i].isjoin.equals("0")){
							tobeinviteAdapter.addData(response.users[i]);
						}
					}
					ViewGroup.LayoutParams params;
					if(alreadyinAdapter.getCount() != 0){
						tv_alreadyin.setVisibility(View.VISIBLE);
						params = lv_alreadyin.getLayoutParams(); 
						params.height = ZScreen.dipToPixels(77)* alreadyinAdapter.getCount(); //需要设置的listview的高度，你可以设置成一个定值，也可以设置成其他容器的高度，如果是其他容器高度，那么不要在oncreate中执行，需要做延时处理，否则高度为0 
						lv_alreadyin.setLayoutParams(params); 
						alreadyinAdapter.notifyDataSetChanged();
					/*	System.out.println("===" + alreadyinAdapter.getCount() + "===");
						String persons = null;
						for(int i=0; i< alreadyinAdapter.getCount(); i++){
							persons = persons + alreadyinAdapter.getItem(i).nickname + "," + alreadyinAdapter.getItem(i).phonenum + "=";
						}
						System.out.println(persons);*/
					}
					
					if(tobeinviteAdapter.getCount() != 0){
						tv_tobeinvite.setVisibility(View.VISIBLE);
						params = lv_tobeinvite.getLayoutParams(); 
						params.height = ZScreen.dipToPixels(77)* tobeinviteAdapter.getCount(); //需要设置的listview的高度，你可以设置成一个定值，也可以设置成其他容器的高度，如果是其他容器高度，那么不要在oncreate中执行，需要做延时处理，否则高度为0 
						lv_tobeinvite.setLayoutParams(params); 
						tobeinviteAdapter.notifyDataSetChanged();
						/*System.out.println("===" + tobeinviteAdapter.getCount() + "===");
						String persons = null;
						for(int i=0; i< tobeinviteAdapter.getCount(); i++){
							persons = persons + tobeinviteAdapter.getItem(i).nickname + "," + tobeinviteAdapter.getItem(i).phonenum + "=";
						}
						System.out.println(persons);*/
					}			
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

		lv_alreadyin = (ListView) findViewById(R.id.lv_alreadyin);
		lv_tobeinvite = (ListView) findViewById(R.id.lv_tobeinvite);
		alreadyinAdapter = new FriendsAddPhoneAdapter(this);
		tobeinviteAdapter = new FriendsAddPhoneAdapter(this);
		lv_alreadyin.setAdapter(alreadyinAdapter);
		lv_tobeinvite.setAdapter(tobeinviteAdapter);
		lv_alreadyin.setOnItemClickListener(this);
		
		tv_alreadyin = (TextView)findViewById(R.id.tv_alreadyin);
		tv_tobeinvite = (TextView)findViewById(R.id.tv_tobeinvite);
		
		Requester2.phoneBook(handler);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				alreadyinDataChangedReceiver, new IntentFilter(BROADCAST_ALREADYIN_DATA_CHANGED));
		
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
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, HomepageOtherDiaryActivity.class);
		intent.putExtra("userid", alreadyinAdapter.getItem(position).userid);
		intent.putExtra("nickname", alreadyinAdapter.getItem(position).nickname);
		this.startActivity(intent);
	}
	
}