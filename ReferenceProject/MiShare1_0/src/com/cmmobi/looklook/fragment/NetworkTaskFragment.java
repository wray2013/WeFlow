package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.zipper.framwork.adapter.ZBaseAdapter;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZByteToSize;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.SlidingActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class NetworkTaskFragment extends XFragment implements OnClickListener {
	
	private View contentView;
	private XFragment currFragment;
	
	private List<Object> allTaskList = new ArrayList<Object>();
	private NetworkTaskListAdapter adapter;
	private ListView listView;
	
	// UI components
	private ImageView ivBack;
	private ImageView ivMenu;
//	private TextView ivPauseStart;
//	private TaskControlType taskcontrolType;
	public enum TaskControlType {
		TASK_PAUSE_ALL, TASK_START_ALL
	}
	
	private String uid;
	private Dialog taskItemMenu;
//	private boolean isLoaded    = false;
//	private boolean needRefresh = false;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	private ProgressReceiver mProgressReceiver = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.activity_networktask, null);
		uid = userID;
		if(!NetworkTaskManager.getInstance(uid).hasTask()) {
//			makeTestData();
		}
		
//		ivBack = (ImageView) contentView.findViewById(R.id.iv_back);
//		ivBack.setOnClickListener(this);
		
		ivMenu = (ImageView) contentView.findViewById(R.id.iv_menu);
		ivMenu.setOnClickListener(this);
		
//		ivPauseStart = (TextView) contentView.findViewById(R.id.iv_start_pause);
//		ivPauseStart.setOnClickListener(this);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.maptankuang_moren)
		.showImageForEmptyUri(R.drawable.maptankuang_moren)
		.showImageOnFail(R.drawable.maptankuang_moren)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
//		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
		
		refreshAllTask();

		adapter = new NetworkTaskListAdapter(this);
		adapter.setList(allTaskList);

		listView = (ListView) contentView.findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Object object = view.getTag();
				if (object instanceof INetworkTask) {
					INetworkTask task = (INetworkTask) object;
					if(task.info.taskType.equals(INetworkTask.TASK_TYPE_UPLOAD) ||
							task.info.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
						showUpChoice(task);
					}
				}
			}

		});
		
		mProgressReceiver = new ProgressReceiver();
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(INetworkTask.ACTION_TASK_PERCENT_NOTIFY);
		intentfilter.addAction(INetworkTask.ACTION_TASK_REMOVE_NOTIFY);
		intentfilter.addAction(INetworkTask.ACTION_TASK_STATE_CHANGE);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProgressReceiver, intentfilter);
//		getZReceiverManager().registerLocalZReceiver(new ProgressReceiver());
		return contentView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
//		Toast.makeText(this, "部分上传任务暂时无任务封面，需底层提供相关缩略图接口", Toast.LENGTH_LONG).show();
//		UmengclickAgentWrapper.onResume(this);
//		CmmobiClickAgentWrapper.onResume(this);
//		CmmobiClickAgentWrapper.onEventBegin(this, "ma_task_m");
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		UmengclickAgentWrapper.onPause(this);
//		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
//		CmmobiClickAgentWrapper.onStop(this);
//		CmmobiClickAgentWrapper.onEventEnd(this, "ma_task_m");
	}
	
	// 显示上传任务选择界面
	private void showUpChoice(INetworkTask t) {
		boolean isPublish = t.info.isPublish;
		taskItemMenu = new Dialog(getActivity(), R.style.networktask_dialog_style_none_background);
		Window w = taskItemMenu.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();

		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		View view = ZLayoutInflater.inflate(R.layout.activity_networktask_upload_menu,
				null);
		
//		Button btRemove = (Button) view.findViewById(R.id.btn_up_remove);
		Button btStartPause = (Button) view.findViewById(R.id.btn_up_startpause);
		Button btUpShare = (Button) view.findViewById(R.id.btn_up_share);
		Button btPriority = (Button) view.findViewById(R.id.btn_up_priority);
		Button btCancel = (Button) view.findViewById(R.id.btn_up_cancel);
		
//		btRemove.setTag(t);
		btStartPause.setTag(t);
		btUpShare.setTag(t);
		btPriority.setTag(t);
		btCancel.setTag(t);
		
		if (t.getState() == INetworkTask.STATE_PAUSED
				|| t.getState() == INetworkTask.STATE_ERROR) {
			btPriority.setText("优先处理");
//			btPriority.setVisibility(View.GONE);
			btStartPause.setText("开始上传");
		} else {
			btPriority.setText("优先处理");
			btStartPause.setText("暂停上传");
		}
		
		// 取消任务菜单
		/*
		btRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object object = v.getTag();
				if (object instanceof INetworkTask) {
					INetworkTask task = (INetworkTask) object;
					task.info.isPublish = false;
					OfflineTaskManager.getInstance().removeShareToLooklookTask(task.info.diaryuuid);
					NetworkTaskManager.getInstance(uid).removeTask(task);
					//task.remove();
					refreshAllTask();
				}
				adapter.notifyDataSetChanged();
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
		});*/
		
		// 分享菜单，已废弃
		if (false && isPublish) { //如果已经设置为发布状态
			btUpShare.setVisibility(View.VISIBLE);
			btUpShare.setText("取消分享");
			btUpShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Object object = v.getTag();
					if (object instanceof INetworkTask) {
						INetworkTask task = (INetworkTask) object;
						task.info.isPublish = false;
						//修改本地日记信息，通知详情更新
//						DiaryManager.getInstance().modifyDiaryPublishStatus(task.info.diaryuuid, "1"); //1新建 2 发布
						// 通知队列删除已有的分享任务
						OfflineTaskManager.getInstance().removeShareToLooklookTask(task.info.diaryuuid);
					}
					adapter.notifyDataSetChanged();
					if (taskItemMenu != null) {
						taskItemMenu.dismiss();
					}
				}
				
			});

		} else {
			btUpShare.setVisibility(View.GONE);
		}
		
		// 任务状态菜单
		btStartPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object object = v.getTag();
				if (object instanceof INetworkTask) {
					INetworkTask task = (INetworkTask) object;
					if(task.getState() == INetworkTask.STATE_PAUSED
							|| task.getState() == INetworkTask.STATE_ERROR) {
						NetworkTaskManager.getInstance(uid).setActiveUp(task, true);
						NetworkTaskManager.getInstance(uid).startTask(task);
					} else {
						NetworkTaskManager.getInstance(uid).pauseTask(task);
					}
				}
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
			
		});
		
		// 任务优先菜单
		btPriority.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object object = v.getTag();
				if (object instanceof INetworkTask) {
					INetworkTask task = (INetworkTask) object;
					NetworkTaskManager.getInstance(uid).setPriority(task, true);
					refreshAllTask();
//					isLoaded = false;
					adapter.notifyDataSetChanged();
				}
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
			
		});
		
		//取消操作
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
			
		});
		taskItemMenu.setContentView(view);
		taskItemMenu.show();
		taskItemMenu.getWindow().setBackgroundDrawable(getResources().getDrawable(
				R.drawable.del_dot_big));
		taskItemMenu.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}
	
	/**
	 * 测试假数据
	 */
	private void makeTestData() {

//		File file = ZFileSystem.ZExternalFile.getExternalStorageDirectory();
//		ZLog.e(file.getAbsolutePath());
		NetworkTaskInfo testinfo = new NetworkTaskInfo(
				uid,  //用户id
				"424",//日记id
				"73185",//附件id：私信id,评论id,陌生人消息id
				"/mnt/sdcard/Download/psb.jpeg",//本地上传文件
				"2013_06_28_15212156fff1b8fe314fdbb8525200ab34cb19.jpg",//网络地址
				"4", //文件类型 ： 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
				"3"); //业务类型 ： 1 日记  2 评论  3 私信  4 陌生人消息
		NetworkTaskInfo testinfo2 = new NetworkTaskInfo(
				uid,  //用户id
				"425",//日记id
				"73186",//附件id：私信id,评论id,陌生人消息id
				"/mnt/sdcard/Download/psb.jpeg",//本地上传文件
				"2013_06_28_15212156fff1b8fe314fdbb8525200ab34cb19.jpg",//网络地址
				"4", //文件类型 ： 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
				"3"); //业务类型 ： 1 日记  2 评论  3 私信  4 陌生人消息
		UploadNetworkTask uploadtask = new UploadNetworkTask(testinfo);
		UploadNetworkTask uploadtask2 = new UploadNetworkTask(testinfo2);
		uploadtask2.setPublish(true);
		NetworkTaskManager.getInstance(uid).addTask(uploadtask);
		NetworkTaskManager.getInstance(uid).addTask(uploadtask2);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ZLog.e("NetworkTask info persist!");
		if (mProgressReceiver != null) {
			LocalBroadcastManager.getInstance(ZApplication.getInstance())
					.unregisterReceiver(mProgressReceiver);
		}
		String currentuid = ActiveAccount.getInstance(getActivity()).getLookLookID();
		if(ZStringUtils.emptyToNull(currentuid) != null && currentuid.equals(uid)) {
			NetworkTaskManager.getInstance(uid).persistTask();
		}
		//NetworkTaskManager.getInstance(uid).removeAllTask();
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	private void refreshAllTask() {
		allTaskList.clear();
		List<INetworkTask> networkTaskList = NetworkTaskManager.getInstance(uid).getTaskList();
		allTaskList.addAll(networkTaskList);
	}

//	private void refreshTaskController() {
//		if (ivPauseStart != null) {
//			if (NetworkTaskManager.getInstance(uid).isTaskRunning()) {
//				taskcontrolType = TaskControlType.TASK_PAUSE_ALL;
//				ivPauseStart.setText(R.string.network_task_pauseall);
//			} else {
//				taskcontrolType = TaskControlType.TASK_START_ALL;
//				ivPauseStart.setText(R.string.network_task_startall);
//			}
//		}
//	}
	
	private class ProgressReceiver extends ZBroadcastReceiver {

		private ProgressReceiver() {
			addAction(INetworkTask.ACTION_TASK_PERCENT_NOTIFY);
			addAction(INetworkTask.ACTION_TASK_REMOVE_NOTIFY);
			addAction(INetworkTask.ACTION_TASK_STATE_CHANGE);
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(INetworkTask.ACTION_TASK_REMOVE_NOTIFY)) {
				int index = intent.getExtras().getInt("taskid");
				ZLog.e("remove index = " + index);
				if (index > -1) {
					refreshAllTask();
				}
			} else if (intent.getAction().equals(INetworkTask.ACTION_TASK_STATE_CHANGE)) {
				int st = intent.getIntExtra("taskState", 0);
				int ec = intent.getIntExtra("taskErrorCode", -1);
				String at = intent.getStringExtra("attachid");
				Log.d("XXX", "task state " + st + ",at = " + at + ",errorcode = " + ec);
				String currentuid = ActiveAccount.getInstance(getActivity()).getLookLookID();
				if(ZStringUtils.emptyToNull(currentuid) != null && currentuid.equals(uid)) {
					refreshAllTask();
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	public class NetworkTaskListAdapter extends ZBaseAdapter<Object>/* implements
			OnClickListener */{

		protected DisplayMetrics dm = new DisplayMetrics();
		private int layoutWidth;
		
		public NetworkTaskListAdapter(NetworkTaskFragment fragment) {
			(fragment.getActivity()).getWindowManager().getDefaultDisplay()
			.getMetrics(dm);
			layoutWidth = dm.widthPixels / 6;
		}
		
		public class ViewHolder {
			TextView stateTitleTv;
//			WebImageView imageView;
			ContentThumbnailView imageView;
			ImageView shareImg;
			ImageView priorityImg;
			ImageView stateImg;
			SeekBar progressBar;
			LayerDrawable progressDrawable;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			Object object = getItem(position);

			if (object instanceof INetworkTask) {
				final INetworkTask task = (INetworkTask) object;

				if (convertView == null || convertView.getTag() == null) {
					view = ZLayoutInflater
							.inflate(R.layout.list_item_networktask);
					viewFinder.set(view);

					holder = new ViewHolder();
					holder.stateTitleTv = viewFinder.findTextView(R.id.task_title);
					holder.imageView = (ContentThumbnailView) view.findViewById(R.id.image_view);
					holder.progressBar = viewFinder.findSeekBar(R.id.progress_bar);
					holder.shareImg = viewFinder.findImageView(R.id.task_share_img);
					holder.priorityImg = viewFinder.findImageView(R.id.task_priority_img);
					holder.stateImg = viewFinder.findImageView(R.id.task_state_img);
					holder.progressBar.setEnabled(false);
					holder.progressBar.setClickable(false);
					holder.progressBar.setSelected(false);
					holder.progressBar.setFocusable(false);
					holder.progressBar.setTag(task);
					view.setTag(task);
					view.setTag(R.string.view_tag_key, holder);
				} else {
					view = convertView;
					view.setTag(task);
					holder = (ViewHolder) view.getTag(R.string.view_tag_key);
				}
				bindTask(holder, task);
			}

			return view;
		}

		public void bindTask(ViewHolder holder, INetworkTask task) {
			//TODO:
//			NetworkTaskManager.getInstance(task.info.userid).getCurPublish(task);
			holder.shareImg.setTag(task);
			holder.priorityImg.setTag(task);
			holder.stateImg.setTag(task);
			holder.imageView.setTag(task);
			/*
			if (task.info.taskCover == null) {
				Log.d("==WR==", "Audio or Text task");
				loadTaskImage(holder.imageView, task.info.diaryType);
			} else {
				Log.d("==WR==", "taskCover[" + task.info.taskCover + "]");
				imageLoader.displayImageEx(task.info.taskCover, holder.imageView,
						options, animateFirstListener, ActiveAccount
								.getInstance(ZApplication.getInstance())
								.getUID(), 1);
			}*/
			MyDiary mydiary = diaryManager.findMyDiaryByUUID(task.info.diaryuuid);
			holder.imageView.setVisibility(View.VISIBLE);
			LayoutParams params=holder.imageView.getLayoutParams();
			Log.d("==WR==", "layoutWidth=" + layoutWidth + "; params.height=" + params.height);
			params.height=layoutWidth;
			params.width=layoutWidth;
			holder.imageView.setLayoutParams(params);
			((ContentThumbnailView) holder.imageView).setContentDiaries("0", mydiary);
			
			holder.progressBar.setProgress((int) task.info.percentValueUI);
//			holder.progressBar.setSecondaryProgress((int) task.info.percentValue);
			
			/*if(!task.info.isPublish) {
				holder.shareImg.setVisibility(View.INVISIBLE);
			} else {
				holder.shareImg.setVisibility(View.VISIBLE);
			}*/
			
			holder.shareImg.setVisibility(View.INVISIBLE);
			int state = task.getState();
			switch(state) {
			case INetworkTask.STATE_WAITING:
				holder.stateTitleTv.setText("等待上传...");
				holder.stateImg.setImageResource(R.drawable.shangchuan);
				if (task.info.isPriority) {
					holder.priorityImg.setImageResource(R.drawable.youxian);
				} else {
					holder.priorityImg.setImageResource(R.drawable.dengdai);
				}
				holder.priorityImg.setVisibility(View.VISIBLE);
				//holder.stateImg.setImageResource(R.drawable.dengdai);
				break;
			case INetworkTask.STATE_RUNNING:
			case INetworkTask.STATE_PREPARING:
			case INetworkTask.ACTION_RETRY:
				//STATE_RUNNING 和 STATE_PREPARING做相同处理
				holder.stateTitleTv.setText("正在上传("
						+ ZByteToSize.smartSize(task.info.uploadedLengthUI) + "/"
						+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
				holder.stateImg.setImageResource(R.drawable.upload_animation);
				if (task.info.isPriority) {
					holder.priorityImg.setVisibility(View.VISIBLE);
					holder.priorityImg.setImageResource(R.drawable.youxian);
				} else {
					holder.priorityImg.setVisibility(View.INVISIBLE);
				}
				//上传动画
				AnimationDrawable animationDrawable = (AnimationDrawable) holder.stateImg.getDrawable();
				animationDrawable.stop();
				animationDrawable.start();
				break;
			case INetworkTask.STATE_PAUSED:
				holder.stateTitleTv.setText("暂停上传...");// 暂停上传
				holder.stateImg.setImageResource(R.drawable.shangchuan);
//				holder.priorityImg.setVisibility(View.INVISIBLE);
				if (task.info.isPriority) {
					holder.priorityImg.setVisibility(View.VISIBLE);
					holder.priorityImg.setImageResource(R.drawable.youxian);
				} else {
					holder.priorityImg.setVisibility(View.INVISIBLE);
				}
				break;
			case INetworkTask.STATE_ERROR:
				holder.stateTitleTv.setText("上传失败("
						+ ZByteToSize.smartSize(task.info.uploadedLength) + "/"
						+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
				holder.stateImg.setImageResource(R.drawable.shangchuan);
				break;
			case INetworkTask.STATE_COMPELETED:
				holder.stateTitleTv.setText("上传完成("
						+ ZByteToSize.smartSize(task.info.uploadedLength) + "/"
						+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
				// holder.stateImg.setImageResource(R.drawable.task_done);
				break;
			case INetworkTask.STATE_REMOVED:
				holder.stateTitleTv.setText("正在删除任务");
				break;
			default:
				holder.stateTitleTv.setText("上传 状态=" + task.getState());
				break;
			}
			//暂时不允许手动点击暂停
			//holder.stateImg.setOnClickListener(this);
		}

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
//		case R.id.iv_back:
////			onBackPressed();
//			break;
		case R.id.iv_menu:
			showMenu();
			break;
//		case R.id.iv_start_pause:
//			switch(taskcontrolType) {
//			case TASK_PAUSE_ALL:
//				//pause all
//				if (NetworkTaskManager.getInstance(uid).isTaskRunning()) {
//					NetworkTaskManager.getInstance(uid).pauseAllTask();
////					ivPauseStart.setBackgroundResource(R.drawable.del_btn_activity_networktask_startall);
//					ivPauseStart.setText(R.string.network_task_startall);
//					taskcontrolType = TaskControlType.TASK_START_ALL;
//				}
//				break;
//			case TASK_START_ALL:
//				//start all
//				NetworkTaskManager.getInstance(uid).startAllTask(true);
////				ivPauseStart.setBackgroundResource(R.drawable.del_btn_activity_networktask_pauseall);
//				ivPauseStart.setText(R.string.network_task_pauseall);
//				taskcontrolType = TaskControlType.TASK_PAUSE_ALL;
//				break;
//			}
//			break;
		}
	}
	
	/**
	 * 切换内容区fragment
	 *//*
	@Override
	public void switchContent(final XFragment fragment) {
		if (null == fragment)
			return;
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		if (currFragment != null)
			ft.hide(currFragment);
		currFragment = fragment;
		if (null == getActivity().getSupportFragmentManager()
				.findFragmentByTag(fragment.getClass().getName())) {
			ft.add(R.id.zone_content, fragment, fragment.getClass().getName());
		} else {
			ft.show(fragment);
		}
		ft.commitAllowingStateLoss();
	}*/
	
	/**
	 * 显示菜单栏
	 */
	protected void showMenu(){
		if (getActivity() == null)
			return;
		SlidingActivity slidingActivity = (SlidingActivity) getActivity();
		slidingActivity.showMenu();
	}
	
	/**
	 * 显示内容区
	 */
	protected void showContent(){
		if (getActivity() == null)
			return;
		SlidingActivity slidingActivity = (SlidingActivity) getActivity();
		slidingActivity.showContent();
	}
	
}
