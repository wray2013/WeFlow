/**
 * 
 */
package com.cmmobi.looklook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;

/**
 * @author wuxiang
 * 
 * @create 2013-4-9
 */
public class HomeEntriesActivity extends Activity implements
		OnItemClickListener {

	private static final String TAG = "HomeEntriesActivity";
	private static final boolean ISDEBUG = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_entry_main);
		((ListView) findViewById(R.id.lv_entry)).setOnItemClickListener(this);
		// Requester.submitUA(myHandler);
		
		AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());

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

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
/*			case Requester.RESPONSE_TYPE_UA:
				if (msg.obj != null) {
					ActiveAccount acct = ActiveAccount
							.getInstance(HomeEntriesActivity.this);
					acct.snstype = "0";
					acct.username = "222222@163.com";
					acct.password = "123456";
					Requester.login(HomeEntriesActivity.this, myHandler, acct);
				} else {
					Log.e(TAG, "RESPONSE_TYPE_UA error");
				}
				break;*/
/*			case Requester.RESPONSE_TYPE_LOGIN:
				GsonResponse.loginResponse response = (GsonResponse.loginResponse) msg.obj;
				AppState.setLoginResponse(msg.obj);
				Log.d(TAG, "response=" + response.status);
				break;*/

			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {
		System.out.println("which--->" + which);
		switch (which) {

		case 0:// 主界面
			startActivity(new Intent(this, HomeActivity.class));
			break;
		case 1:// 评论
//			startActivity(new Intent(this, CommentActivity.class));
			break;
		case 2:// 私信
//			startActivity(new Intent(this, MessagePersonalActivity.class));
			break;
		case 3:// 关注
			startActivity(new Intent(this, MyAttentionMembersActivity.class));
			break;
		case 4:// 添加好友
			launchFriendAddActivity();
			break;
		case 5:// 发现-地图模式
			startActivity(new Intent(this, DiscoverMainActivity.class));
			break;
		case 6:// @好友
			startActivity(new Intent(this, AltFriendActivity.class));
			break;
		case 7:// 选择活动
				// startActivity(new Intent(this, EventMainActivity.class));
			break;
		case 8:// 定位信息

			break;
		case 9:// 标签
			startActivity(new Intent(this, TagActivity.class));
			break;
		case 10:// 活动详情页
			// startActivity(new Intent(this, EventDetailActivity.class));
			break;
		case 11:// 更换空间封面
			startActivity(new Intent(this, SpaceCoverActivity.class));
			break;
		case 12:// 粉丝
			startActivity(new Intent(this, PersonFansActivity.class));
			break;
		case 13:// 设置
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case 14:// 注销用户
			ActiveAccount acct = ActiveAccount.getInstance(this);
			acct.logout();
			launchLoginActivity();
			break;
		case 15:// 设置
			startActivity(new Intent(this, HomepageCommentActivity.class));
			break;

		case 16: // 语音识别
			//startActivity(new Intent(this, TestMscActivity.class));
			break;
		case 17:// 图片编辑
//			startActivity(new Intent(this, EditPhotoActivity.class));
			break;
		case 18:// 视频编辑
//			startActivity(new Intent(this, EditVideoActivity.class));
			break;
		case 19:// 他人主页
			Intent intent = new Intent();
			intent.setClass(this, HomepageOtherDiaryActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("userid", ActiveAccount.getInstance(ZApplication.getInstance()).getUID());// 压入数据
			intent.putExtras(mBundle);
			startActivity(intent);
			break;
		case 20:// 网络任务
			startActivity(new Intent(this, NetworkTaskActivity.class));
			break;
		case 21:// 音频编辑
//			startActivity(new Intent(this,EditVoiceActivity.class));
			break;
		case 22:// 音频编辑
			startActivity(new Intent(this, DiaryDetailActivity.class));
			break;
		case 23:// 附近
			startActivity(new Intent(this, FriendsNearByActivity.class));
			break;
		case 24:// 附近
			startActivity(new Intent(this, MapWatchModeActivity.class));
			break;
		case 25:
//			startActivity(new Intent(this, AudioRecorderActivity.class));
			break;
		case 26://相册
			startActivity(new Intent(this, LookLookGalleryActivity.class));
			break;
		case 27://视频播放
			//startActivity(new Intent(this, MediaPlayerActivity.class));
			break;
		default:
			break;
		}

	}

	public void launchLoginActivity() {
		Intent intent = new Intent(this, LoginMainActivity.class);
		startActivity(intent);
		finish();
	}

	public void launchFriendAddActivity() {
		Intent intent = new Intent(this, FriendAddActivity.class);
		startActivity(intent);
	}



}
