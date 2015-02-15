package com.cmmobi.looklook.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeListItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.cancleActiveResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.joinActiveResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshScrollView;
import com.cmmobi.looklook.fragment.ActivitiesDetailAwardFragment;
import com.cmmobi.looklook.fragment.ActivitiesDetailIntroduceFragment;
import com.cmmobi.looklook.fragment.ActivitiesDetailPartFragment;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ActivitiesDetailActivity extends FragmentActivity implements
		OnClickListener, Callback {

	public static final String ACTION_ACTIVIES = "ACTION_ACTIVIES";
	public static final String FLAG_ACTIVITY_DATA_UPDATE = "FLAG_ACTIVITY_DATA_UPDATE";
	public static final String FLAG_ACTIVITY_SUCESS = "FLAG_ACTIVITY_SUCESS";
	public static final String FLAG_ACTIVITY_FAIL = "FLAG_ACTIVITY_FAIL";

	private ImageView activitiesIntroduce;
	private ImageView activitiesPart;
	private ImageView activitiesWin;
	private ImageButton backButton;
	private ActivitiesDetailIntroduceFragment activitiesIntroduceFragment;
	private ActivitiesDetailPartFragment activitiesDetailPartFragment;
	private ActivitiesDetailAwardFragment activitiesDetailAwardFragment;

	public activeListItem activeItem;
	public activeListItem activeItemJoined;
	public activeListItem friendsActiveItem;
	private ImageButton joinButton;

	private Handler handler;

	private ImageView activitesDeatilImage;

	// 使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	public PullToRefreshScrollView scrollView;

	public String comeDiaryId;
	public String comeDiaryuuid;
	
	private TextView tv_activitytitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_activities_detail);

		// ActivitiesDetailIntroduceFragment activitiesIntroduceFragment = new
		// ActivitiesDetailIntroduceFragment();

		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.activites_contacts, activitiesIntroduceFragment)
		// .commit();

		handler = new Handler(this);
		activitiesIntroduce = (ImageView) findViewById(R.id.activities_introduce);
		activitiesPart = (ImageView) findViewById(R.id.activities_part);
		activitiesWin = (ImageView) findViewById(R.id.activities_win);
		backButton = (ImageButton) findViewById(R.id.activies_back_btn);
		joinButton = (ImageButton) findViewById(R.id.join_btn);
		activitesDeatilImage = (ImageView) findViewById(R.id.activites_deatil_image);

		scrollView = (PullToRefreshScrollView) findViewById(R.id.scrollview);

		activitiesIntroduce.setOnClickListener(this);
		activitiesPart.setOnClickListener(this);
		activitiesWin.setOnClickListener(this);
		backButton.setOnClickListener(this);
		joinButton.setOnClickListener(this);
		
		tv_activitytitle = (TextView) findViewById(R.id.tv_activitytitle);
		

		LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
				new IntentFilter(FLAG_ACTIVITY_DATA_UPDATE));

		activeListItem comeListItem = (activeListItem) getIntent()
				.getSerializableExtra("activeItem");

		if (null != comeListItem) {
			activeItem = comeListItem;
		}
		comeDiaryId = getIntent().getStringExtra("comeDiaryId");
		comeDiaryuuid = getIntent().getStringExtra("comeDiaryuuid");
		String json = getIntent().getStringExtra(
				ActivitiesDetailActivity.ACTION_ACTIVIES);

		String friendsCircleJson = getIntent().getStringExtra(
				"comefromfriendscircle");
		if (json != null && json.length() > 0) {
			this.activeItemJoined = new Gson().fromJson(json,
					activeListItem.class);
			activeItem = this.activeItemJoined;
		}
		if (null != friendsCircleJson && friendsCircleJson.length() > 0) {
			this.friendsActiveItem = new Gson().fromJson(friendsCircleJson,
					activeListItem.class);
			activeItem = this.friendsActiveItem;
		}
		if (null != this.friendsActiveItem
				&& null != this.friendsActiveItem.activeid
				&& this.friendsActiveItem.activeid.length() > 0) {
			if ("1".equals(friendsActiveItem.iseffective)) {
				joinButton.setBackgroundResource(R.drawable.yijieshu);
				joinButton.setTag("1");
			} else if ("2".equals(friendsActiveItem.iseffective)) {
				joinButton.setBackgroundResource(R.drawable.weikaishi);
				joinButton.setTag("2");
			} else if ("0".equals(friendsActiveItem.iseffective)) {
				joinButton
						.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
				joinButton.setTag("4");
			}
		} else if (null == this.activeItemJoined
				&& (null == this.comeDiaryuuid || !(this.comeDiaryuuid.length() > 0))
				&& null != comeListItem) {

			if ("1".equals(comeListItem.iseffective)) {
				joinButton.setBackgroundResource(R.drawable.yijieshu);
				joinButton.setTag("1");
			} else if ("2".equals(comeListItem.iseffective)) {
				joinButton.setBackgroundResource(R.drawable.weikaishi);
				joinButton.setTag("2");
			} else if ("0".equals(comeListItem.iseffective)) {
				joinButton
						.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
				joinButton.setTag("4");
			}
		} else if (null != this.activeItemJoined && null != this.comeDiaryuuid
				&& this.comeDiaryuuid.length() > 0) {
			String diaryuid = this.getIntent().getStringExtra("diaryuid");
			if(diaryuid != null && ! ActiveAccount.getInstance(this).getUID().equals(diaryuid)){
				joinButton.setVisibility(View.GONE);
			}else{
				joinButton
				.setBackgroundResource(R.drawable.btn_activity_activities_detail_cancel);
				joinButton.setTag("3");
			}
			
		} else if (null == this.comeDiaryuuid && null != comeListItem) {
			if ("1".equals(comeListItem.iseffective)) {
				joinButton.setBackgroundResource(R.drawable.yijieshu);
				joinButton.setTag("1");
			} else if ("2".equals(comeListItem.iseffective)) {
				joinButton.setBackgroundResource(R.drawable.weikaishi);
				joinButton.setTag("2");
			} else if ("0".equals(comeListItem.iseffective)) {
				joinButton
						.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
				joinButton.setTag("4");
			}
		} else if (null != this.comeDiaryuuid && this.comeDiaryuuid.length() > 0
				&& null != comeListItem) {

			// if (null != activeItemJoined
			// && null != activeItemJoined.activeid
			// && activeItemJoined.activeid.length() > 0) {
			DiaryManager diarymanager = DiaryManager.getInstance();
			MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(comeDiaryuuid);
			if (null != myLocalDiary.active
					&& null != myLocalDiary.active.activeid
					&& myLocalDiary.active.activeid.length() > 0) {

				if (myLocalDiary.active.activeid.equals(comeListItem.activeid)) {
					joinButton
							.setBackgroundResource(R.drawable.btn_activity_activities_detail_cancel);
					joinButton.setTag("3");
				} else {
					// joinButton.setBackgroundResource(R.color.transparent);
					if ("1".equals(myLocalDiary.active.iseffective)) {
						joinButton.setBackgroundResource(R.drawable.yijieshu);
						joinButton.setTag("1");
					} else if ("2".equals(myLocalDiary.active.iseffective)) {
						joinButton.setBackgroundResource(R.drawable.weikaishi);
						joinButton.setTag("2");
					} else if ("0".equals(myLocalDiary.active.iseffective)) {
						joinButton.setBackgroundResource(R.drawable.yicanjia);
						joinButton.setTag("9");
					}
				}
			} else {
				if ("1".equals(comeListItem.iseffective)) {
					joinButton.setBackgroundResource(R.drawable.yijieshu);
					joinButton.setTag("1");
				} else if ("2".equals(comeListItem.iseffective)) {
					joinButton.setBackgroundResource(R.drawable.weikaishi);
					joinButton.setTag("2");
				} else if ("0".equals(comeListItem.iseffective)) {
					joinButton
							.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
					joinButton.setTag("0");
				}
			}
		}
		// {
		// activeItemJoined = this.activeItem;
		// joinButton.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
		// joinButton.setTag("0");
		// }

		//
		// if (null != comeListItem) {
		// this.activeItem = comeListItem;
		// if ("1".equals(this.activeItem.iseffective)) {
		// joinButton.setBackgroundResource(R.drawable.yijieshu);
		// joinButton.setTag("1");
		// } else if ("2".equals(this.activeItem.iseffective)) {
		// joinButton.setBackgroundResource(R.drawable.weikaishi);
		// joinButton.setTag("2");
		// } else if ("0".equals(this.activeItem.iseffective)){
		// //
		// // if (this.activeItemJoined != null) {
		// //
		// joinButton.setBackgroundResource(R.drawable.btn_activity_activities_detail_cancel);
		// // joinButton.setTag("3");
		// // } else {
		// joinButton.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
		// joinButton.setTag("0");
		// // }
		// }
		// } else {
		// this.activeItem = activeItemJoined;
		// }

		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(ActivitiesDetailActivity.this));

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.active_detail_icon)
				.showImageForEmptyUri(R.drawable.active_detail_icon)
				.showImageOnFail(R.drawable.active_detail_icon)
				.cacheInMemory(true).cacheOnDisc(true)
				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();

		/* activitesDeatilImage.setImageUrl(activeItem.picture, 1, false); */
		if (activeItem != null && activeItem.picture != null) {
			imageLoader.displayImage(activeItem.picture, activitesDeatilImage,
					options, animateFirstListener,
					ActiveAccount.getInstance(this).getUID(), 1);
		} else {
			activitesDeatilImage
					.setImageResource(R.drawable.active_detail_icon);
		}

		if (activeItem != null) {
			tv_activitytitle.setText(activeItem.activename);
		}
		
		onCheckChanged(R.id.activities_introduce);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.activies_back_btn) {
			this.finish();
		} else if (v.getId() == R.id.join_btn) {

			if ("4".equals(v.getTag())) {
				Prompt.Dialog(this, false, "提醒", "请到内容详情页面参加活动", null);
			}
			if ("0".equals(v.getTag()) && null != comeDiaryuuid
					&& comeDiaryuuid.length() > 0) {

				// Requester2
				// .joinActive(handler, comeDiaryId, activeItem.activeid);

				OfflineTaskManager.getInstance().addActiveAttendTask(
						comeDiaryId,comeDiaryuuid, activeItem.activeid);

			} else if ("3".equals(v.getTag()) && null != comeDiaryuuid
					&& comeDiaryuuid.length() > 0) {
				DiaryManager diarymanager = DiaryManager.getInstance();
				MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(comeDiaryuuid);

				if (null != myLocalDiary && null != myLocalDiary.active
						&& null != myLocalDiary.active.activeid) {

					// Requester2.cancleActive(handler, comeDiaryId,
					// myLocalDiary.active.activeid);

					OfflineTaskManager.getInstance().addActiveCancelTask(
							comeDiaryId,comeDiaryuuid, myLocalDiary.active.activeid);

				}
			} else if ("9".equals(v.getTag())) {
				Prompt.Dialog(this, false, "提醒", "当前视频已参加了其他活动！", null);
			}

		} else {
			onCheckChanged(v.getId());
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

	private void onCheckChanged(int id) {
		switch (id) {
		case R.id.activities_introduce:
			activitiesIntroduceFragment = new ActivitiesDetailIntroduceFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.activites_contacts,
							activitiesIntroduceFragment).commit();
			activitiesIntroduce.setSelected(true);
			activitiesPart.setSelected(false);
			activitiesWin.setSelected(false);
			activitiesIntroduce.setImageResource(R.drawable.hdjs_2);
			activitiesPart.setImageResource(R.drawable.cysp);
			activitiesWin.setImageResource(R.drawable.hjmd);
			break;
		case R.id.activities_part:

			activitiesDetailPartFragment = new ActivitiesDetailPartFragment();

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.activites_contacts,
							activitiesDetailPartFragment).commit();

			activitiesIntroduce.setSelected(false);
			activitiesPart.setSelected(true);
			activitiesWin.setSelected(false);
			activitiesIntroduce.setImageResource(R.drawable.hdjs);
			activitiesPart.setImageResource(R.drawable.cysp_2);
			activitiesWin.setImageResource(R.drawable.hjmd);
			break;
		case R.id.activities_win:

			activitiesDetailAwardFragment = new ActivitiesDetailAwardFragment();

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.activites_contacts,
							activitiesDetailAwardFragment).commit();

			activitiesIntroduce.setSelected(false);
			activitiesPart.setSelected(false);
			activitiesWin.setSelected(true);
			activitiesIntroduce.setImageResource(R.drawable.hdjs);
			activitiesPart.setImageResource(R.drawable.cysp);
			activitiesWin.setImageResource(R.drawable.hjmd_2);
			break;

		default:
			break;
		}
	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (FLAG_ACTIVITY_DATA_UPDATE.equals(intent.getAction())) {
				if (FLAG_ACTIVITY_SUCESS.equals(intent
						.getStringExtra("activity_update_type"))) {

					if ("add".equals(intent
							.getStringExtra("activity_add_cancel"))) {
						joinButton
								.setBackgroundResource(R.drawable.btn_activity_activities_detail_cancel);
						joinButton.setTag("3");
						// if (null != activeItemJoined) {
						// activeItemJoined.activeid = activeItem.activeid;
						// } else {
						// activeItemJoined = new GsonResponse2().new
						// activeListItem();
						// activeItemJoined.activeid = activeItem.activeid;
						// }

						DiaryManager diarymanager = DiaryManager.getInstance();
						MyDiary myLocalDiary = diarymanager
								.findLocalDiaryByUuid(comeDiaryuuid);
						GsonResponse2.MyActive active = new GsonResponse2().new MyActive();
						active.activeid = activeItem.activeid;
						active.activename = activeItem.activename;
						active.starttime = activeItem.starttime;
						active.endtime = activeItem.endtime;
						active.introduction = activeItem.introduction;
						active.add_way = activeItem.add_way;
						active.rule = activeItem.rule;
						active.prize = activeItem.prize;
						active.picture = activeItem.picture;
						active.isjoin = "1";
						active.iseffective = activeItem.iseffective;

						if (null != myLocalDiary) {
							myLocalDiary.active = active;
						}

						if (null != comeDiaryuuid) {
							DiaryManager.getInstance().diaryDataChanged(comeDiaryuuid);
						}
					} else if ("cancel".equals(intent
							.getStringExtra("activity_add_cancel"))) {
						joinButton
								.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
						joinButton.setTag("0");

						DiaryManager diarymanager = DiaryManager.getInstance();
						MyDiary myLocalDiary = diarymanager
								.findLocalDiaryByUuid(comeDiaryuuid);
						if (null != myLocalDiary) {
						myLocalDiary.active = null;
						}
						if (null != comeDiaryuuid) {
						DiaryManager.getInstance()
								.diaryDataChanged(comeDiaryuuid);
						}
					}
				}
			}
		}
	};

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_JOIN_ACTIVE:
			if (msg.obj != null) {
				joinActiveResponse awardDiaryList = (joinActiveResponse) msg.obj;

				if ("0".equals(awardDiaryList.status)) {

					Prompt.Alert(this, "参加活动成功！");
					joinButton
							.setBackgroundResource(R.drawable.btn_activity_activities_detail_cancel);
					joinButton.setTag("3");
					// if (null != activeItemJoined) {
					// activeItemJoined.activeid = activeItem.activeid;
					// } else {
					// activeItemJoined = new GsonResponse2().new
					// activeListItem();
					// activeItemJoined.activeid = activeItem.activeid;
					// }

					DiaryManager diarymanager = DiaryManager.getInstance();
					MyDiary myLocalDiary = diarymanager
							.findLocalDiaryByUuid(comeDiaryuuid);
					GsonResponse2.MyActive active = new GsonResponse2().new MyActive();
					active.activeid = activeItem.activeid;
					active.activename = activeItem.activename;
					active.starttime = activeItem.starttime;
					active.endtime = activeItem.endtime;
					active.introduction = activeItem.introduction;
					active.add_way = activeItem.add_way;
					active.rule = activeItem.rule;
					active.prize = activeItem.prize;
					active.picture = activeItem.picture;
					active.isjoin = "1";
					active.iseffective = activeItem.iseffective;
					if(myLocalDiary != null){
						myLocalDiary.active = active;
					}
					DiaryManager.getInstance().diaryDataChanged(comeDiaryuuid);

				} else {
					Toast.makeText(this, "参加活动失败！", Toast.LENGTH_SHORT).show();
				}
			}

			break;
		case Requester2.RESPONSE_TYPE_CANCEL_ACTIVE:
			if (msg.obj != null) {
				cancleActiveResponse response = (cancleActiveResponse) msg.obj;

				if ("0".equals(response.status)) {

					Prompt.Alert(this, "退出活动成功！");
					joinButton
							.setBackgroundResource(R.drawable.btn_activity_activities_detail_join);
					joinButton.setTag("0");

					DiaryManager diarymanager = DiaryManager.getInstance();
					MyDiary myLocalDiary = diarymanager
							.findLocalDiaryByUuid(comeDiaryuuid);
					if(myLocalDiary != null){
						myLocalDiary.active = null;
					}
					DiaryManager.getInstance().diaryDataChanged(comeDiaryuuid);
				} else {
					Toast.makeText(this, "退出参加活动失败！", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(this, "退出参加活动失败！", Toast.LENGTH_SHORT).show();
			}

			break;
		default:
			break;
		}
		return false;
	}

}
