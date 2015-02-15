package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZByteToSize;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class NetworkTaskActivity extends ZActivity {

	private List<Object> allTaskList = new ArrayList<Object>();
	private ListAdapter adapter;
	private ListView listView;
	
	// UI components
	private ImageView ivBack;
	private ImageView ivPauseStart;
	private TaskControlType taskcontrolType;
	public enum TaskControlType {
		TASK_PAUSE_ALL, TASK_START_ALL
	}
	
	private String uid;
	private AlertDialog taskItemMenu;
//	private boolean isLoaded    = false;
//	private boolean needRefresh = false;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_networktask);
		uid = ActiveAccount.getInstance(this).getLookLookID();
		if(uid != null && !uid.equals("") && !NetworkTaskManager.getInstance(uid).hasTask()) {
			makeTestData();
		}
		
		ivBack = viewFinder.findImageView(R.id.iv_back);
		ivBack.setOnClickListener(this);
		
		ivPauseStart = viewFinder.findImageView(R.id.iv_start_pause);
		ivPauseStart.setOnClickListener(this);
		
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

		adapter = new ListAdapter();
		adapter.setList(allTaskList);

		listView = viewFinder.findListView(R.id.list_view);
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
					} else if(task.info.taskType.equals(INetworkTask.TASK_TYPE_DOWNLOAD)){
						ZLog.e("Download Item!");
						showDownChoice(task);
					}
				}
			}

		});

		getZReceiverManager().registerLocalZReceiver(new ProgressReceiver());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
//		Toast.makeText(this, "部分上传任务暂时无任务封面，需底层提供相关缩略图接口", Toast.LENGTH_LONG).show();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onEventBegin(this, "ma_task_m");
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
		CmmobiClickAgentWrapper.onEventEnd(this, "ma_task_m");
	}
	
	// 显示下载任务选择界面
	private void showDownChoice(INetworkTask t) {
		taskItemMenu = new AlertDialog.Builder(this).create();
		Window w = taskItemMenu.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();

		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		View view = ZLayoutInflater.inflate(R.layout.activity_networktask_download_menu,
				null);

		Button btDownCancel = (Button) view.findViewById(R.id.btn_download_cancel);
		Button btPriority = (Button) view.findViewById(R.id.btn_down_priority);
		Button btCancel = (Button) view.findViewById(R.id.btn_down_cancel);
		
		btDownCancel.setTag(t);
		btPriority.setTag(t);
		btCancel.setTag(t);

		if (t.getState() == INetworkTask.STATE_PAUSED
				|| t.getState() == INetworkTask.STATE_ERROR) {
			btPriority.setText("继续任务");
		} else {
			btPriority.setText("优先处理");
		}
		
		btDownCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object object = v.getTag();
				if (object instanceof INetworkTask) {
					INetworkTask task = (INetworkTask) object;
					NetworkTaskManager.getInstance(uid).removeTask(task);
					//task.remove();
					refreshAllTask();
					//allTaskList.remove(task);
				}
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
		});
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
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
		});
		taskItemMenu.setView(view, 0, 0, 0, 0);
		taskItemMenu.show();
		taskItemMenu.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}
	
	// 显示上传任务选择界面
	private void showUpChoice(INetworkTask t) {
		boolean isPublish = NetworkTaskManager.getInstance(t.info.userid).getCurPublish(t);
		taskItemMenu = new AlertDialog.Builder(this).create();
		Window w = taskItemMenu.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();

		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		View view = ZLayoutInflater.inflate(R.layout.activity_networktask_upload_menu,
				null);
		
		Button btPublish = (Button) view.findViewById(R.id.btn_publish_cancel);
		Button btUpCancel = (Button) view.findViewById(R.id.btn_publish_upload_cancel);
		Button btPriority = (Button) view.findViewById(R.id.btn_up_priority);
		Button btCancel = (Button) view.findViewById(R.id.btn_up_cancel);
		
		btPublish.setTag(t);
		btUpCancel.setTag(t);
		btPriority.setTag(t);
		btCancel.setTag(t);
		
		if (t.getState() == INetworkTask.STATE_PAUSED
				|| t.getState() == INetworkTask.STATE_ERROR) {
			btPriority.setText("继续任务");
		} else {
			btPriority.setText("优先处理");
		}
		
		if (isPublish) { //如果已经设置为发布状态
			btPublish.setVisibility(View.VISIBLE);
			btUpCancel.setText("取消发布和上传");
			btPublish.setOnClickListener(new OnClickListener() {
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
			btPublish.setVisibility(View.GONE);
			btUpCancel.setText("取消上传");
		}
		btUpCancel.setOnClickListener(new OnClickListener() {
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
					//allTaskList.remove(task);
				}
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
			
		});
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
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (taskItemMenu != null) {
					taskItemMenu.dismiss();
				}
			}
			
		});
		taskItemMenu.setView(view, 0, 0, 0, 0);
		taskItemMenu.show();
		taskItemMenu.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}
	
	private void makeTestData() {

//		File file = ZFileSystem.ZExternalFile.getExternalStorageDirectory();
//		ZLog.e(file.getAbsolutePath());
//		NetworkTaskInfo testinfo = new NetworkTaskInfo(
//				uid,  //用户id
//				"424",//日记id
//				"73185",//附件id：私信id,评论id,陌生人消息id
//				"/mnt/sdcard/Download/psb.jpeg",//本地上传文件
//				"2013_06_28_15212156fff1b8fe314fdbb8525200ab34cb19.jpg",//网络地址
//				"4", //文件类型 ： 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
//				"3"); //业务类型 ： 1 日记  2 评论  3 私信  4 陌生人消息
//		UploadNetworkTask uploadtask = new UploadNetworkTask(testinfo);

	}

	@Override
	public void finish() {
		super.finish();
		ZLog.e("NetworkTask info persist!");
		NetworkTaskManager.getInstance(uid).persistTask();
		//NetworkTaskManager.getInstance(uid).removeAllTask();
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	private void refreshAllTask() {
		int size = allTaskList.size();
		allTaskList.clear();
		List<INetworkTask> networkTaskList = NetworkTaskManager.getInstance(uid).getTaskList();
//		if (networkTaskList != null && networkTaskList.size() > 0) {
//			// 任务数发生变化时要刷新
//			if(networkTaskList.size() != size) {
////				isLoaded = false;
//				needRefresh = true;
//			} else {
//				needRefresh = false;
//			}
//			allTaskList.addAll(networkTaskList);
//		}
		allTaskList.addAll(networkTaskList);
		refreshTaskController();
	}

	private void refreshTaskController() {
		if (ivPauseStart != null) {
			if (NetworkTaskManager.getInstance(uid).isTaskRunning()) {
				taskcontrolType = TaskControlType.TASK_PAUSE_ALL;
				ivPauseStart.setBackgroundResource(R.drawable.btn_activity_networktask_pauseall);
			} else {
				taskcontrolType = TaskControlType.TASK_START_ALL;
				ivPauseStart.setBackgroundResource(R.drawable.btn_activity_networktask_startall);
			}
		}
	}
	
	private class ProgressReceiver extends ZBroadcastReceiver {

		private ProgressReceiver() {
//			addAction(INetworkTask.ACTION_NOTIFY);
			addAction(INetworkTask.ACTION_TASK_PERCENT_NOTIFY);
			addAction(INetworkTask.ACTION_TASK_REMOVE_NOTIFY);
			addAction(INetworkTask.ACTION_TASK_STATE_CHANGE);
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			/*if (intent.getAction().equals(INetworkTask.ACTION_NOTIFY)) {
				isLoaded = false;
//				adapter.notifyDataSetChanged();
			} else */if(intent.getAction().equals(INetworkTask.ACTION_TASK_PERCENT_NOTIFY)) {
//				isLoaded = true;
//				needRefresh = false;
			} else if(intent.getAction().equals(INetworkTask.ACTION_TASK_REMOVE_NOTIFY)) {
				int index = intent.getExtras().getInt("taskid");
				ZLog.e("remove index = " + index);
				if (index > -1) {
					refreshAllTask();
				}
			} else if(intent.getAction().equals(INetworkTask.ACTION_TASK_STATE_CHANGE)) {
				int st = intent.getIntExtra("taskState", 0);
				int ec = intent.getIntExtra("taskErrorCode", -1);
				String at = intent.getStringExtra("attachid");
				Log.d("XXX", "task state " + st + ",at = " + at + ",errorcode = " + ec);
				refreshAllTask();
			}
			adapter.notifyDataSetChanged();
		}
	}

	public class ListAdapter extends ZBaseAdapter<Object> implements
			OnClickListener {

		public class ViewHolder {
			TextView stateTitleTv;
			WebImageView imageView;
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
					holder.stateTitleTv = viewFinder
							.findTextView(R.id.task_title);
					holder.imageView = (WebImageView) view.findViewById(R.id.image_view);
					holder.progressBar = viewFinder
							.findSeekBar(R.id.progress_bar);
					holder.shareImg = viewFinder
							.findImageView(R.id.task_share_img);
					holder.priorityImg = viewFinder
							.findImageView(R.id.task_priority_img);
					holder.stateImg = viewFinder
							.findImageView(R.id.task_state_img);
					holder.progressBar.setEnabled(false);
					holder.progressBar.setClickable(false);
					holder.progressBar.setSelected(false);
					holder.progressBar.setFocusable(false);
					holder.progressBar.setTag(task);
					holder.progressDrawable = (LayerDrawable) holder.progressBar.getProgressDrawable();
					holder.progressBar.setProgressDrawable(updateProgressBar(holder.progressDrawable, false));
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
			NetworkTaskManager.getInstance(task.info.userid).getCurPublish(task);
			holder.shareImg.setTag(task);
			holder.priorityImg.setTag(task);
			holder.stateImg.setTag(task);
			holder.imageView.setTag(task);
//			if (!isLoaded || needRefresh) {
//				isLoaded = true;
				// 没有取得封面url
				if (task.info.taskUrl == null) {
					Log.d("==WR==", "Audio or Text task");
					loadTaskImage(holder.imageView, task.info.diaryType);
				} else {
					imageLoader.displayImageEx(task.info.taskUrl, holder.imageView, options, animateFirstListener, ActiveAccount.getInstance(ZApplication.getInstance()).getUID(), 1);
//					holder.imageView.setImageUrl(R.drawable.maptankuang_moren, 1, task.info.taskUrl, false);
				}
//			}
//				holder.imageView.setImageUrl(R.drawable.maptankuang_moren, 0, task.info.taskUrl);
			holder.progressBar.setProgress((int) task.info.percentValue);
			holder.progressBar.setSecondaryProgress((int) task.info.percentValue);
			
			holder.progressDrawable = (LayerDrawable) holder.progressBar.getProgressDrawable();
			if(taskcontrolType == TaskControlType.TASK_START_ALL) {
				holder.progressBar.setProgressDrawable(updateProgressBar(holder.progressDrawable, true));
				holder.progressBar.setThumb(getResources().getDrawable(R.drawable.jindu_zanting3));
			} else if (taskcontrolType == TaskControlType.TASK_PAUSE_ALL) {
				holder.progressBar.setProgressDrawable(updateProgressBar(holder.progressDrawable, false));
				holder.progressBar.setThumb(getResources().getDrawable(R.drawable.jindu_th));
			}
			
			if(!task.info.isPublish) {
				holder.shareImg.setVisibility(View.INVISIBLE);
			} else {
				holder.shareImg.setVisibility(View.VISIBLE);
			}
			if (task.getState() == INetworkTask.STATE_WAITING) {
				if (task.info.taskType == INetworkTask.TASK_TYPE_DOWNLOAD) {
					holder.stateTitleTv.setText("等待下载...");
					holder.stateImg.setImageResource(R.drawable.download_1);
				} else {
					holder.stateTitleTv.setText("等待上传...");
					holder.stateImg.setImageResource(R.drawable.upload_1);
				}
				holder.priorityImg.setImageResource(R.drawable.dengdai);
				holder.priorityImg.setVisibility(View.VISIBLE);
				//holder.stateImg.setImageResource(R.drawable.dengdai);
			} else if (task.getState() == INetworkTask.STATE_RUNNING
					|| task.getState() == INetworkTask.STATE_PREPARING
					|| task.getState() == INetworkTask.ACTION_RETRY) {
				//STATE_RUNNING 和 STATE_PREPARING做相同处理
				if (task.info.taskType == INetworkTask.TASK_TYPE_DOWNLOAD) {
					holder.stateTitleTv.setText("正在下载("
							+ ZByteToSize.smartSize(task.info.currentDownloaingTotalLength) + "/"
							+ ZByteToSize.smartSize(task.info.totalTaskSize)/*totalDownloadedLength*/ + ")");
					holder.stateImg
							.setImageResource(R.drawable.download_animation);
				} else {
					holder.stateTitleTv.setText("正在上传("
							+ ZByteToSize.smartSize(task.info.uploadedLength) + "/"
							+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
					holder.stateImg
							.setImageResource(R.drawable.upload_animation);
				}
				if (task.info.isPriority) {
					holder.priorityImg.setVisibility(View.VISIBLE);
					holder.priorityImg.setImageResource(R.drawable.youxian);
				} else {
					holder.priorityImg.setVisibility(View.INVISIBLE);
				}
				AnimationDrawable animationDrawable = (AnimationDrawable) holder.stateImg
						.getDrawable();
				animationDrawable.stop();
				animationDrawable.start();
			} else if (task.getState() == INetworkTask.STATE_PAUSED) {
				if (task.info.taskType == INetworkTask.TASK_TYPE_DOWNLOAD) {
					holder.stateTitleTv.setText("等待下载...");//暂停下载
					holder.stateImg
							.setImageResource(R.drawable.zanting);
				} else {
					holder.stateTitleTv.setText("等待上传...");//暂停上传
					holder.stateImg
							.setImageResource(R.drawable.zanting);
				}
//				holder.priorityImg.setVisibility(View.INVISIBLE);
				if (task.info.isPriority) {
					holder.priorityImg.setVisibility(View.VISIBLE);
					holder.priorityImg.setImageResource(R.drawable.youxian);
				} else {
					holder.priorityImg.setVisibility(View.INVISIBLE);
				}
			} else if (task.getState() == INetworkTask.STATE_ERROR) {
				if (task.info.taskType == INetworkTask.TASK_TYPE_DOWNLOAD) {
					holder.stateTitleTv.setText("下载失败("
							+ ZByteToSize.smartSize(task.info.currentDownloaingTotalLength) + "/"
							+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
				} else {
					holder.stateTitleTv.setText("上传失败("
							+ ZByteToSize.smartSize(task.info.uploadedLength) + "/"
							+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
				}
				holder.stateImg.setImageResource(R.drawable.task_error);
			} else if (task.getState() == INetworkTask.STATE_COMPELETED) {
				if (task.info.taskType == INetworkTask.TASK_TYPE_DOWNLOAD) {
					holder.stateTitleTv.setText("下载完成("
							+ ZByteToSize.smartSize(task.info.currentDownloaingTotalLength) + "/"
							+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
				} else {
					holder.stateTitleTv.setText("上传完成("
							+ ZByteToSize.smartSize(task.info.uploadedLength) + "/"
							+ ZByteToSize.smartSize(task.info.totalTaskSize) + ")");
				}
				//holder.stateImg.setImageResource(R.drawable.task_done);
			} else if (task.getState() == INetworkTask.STATE_REMOVED) {
				holder.stateTitleTv.setText("正在删除任务");
			} else {
				if (task.info.taskType == INetworkTask.TASK_TYPE_DOWNLOAD) {
					holder.stateTitleTv.setText("下载 状态=" + task.getState());
				} else {
					holder.stateTitleTv.setText("上传 状态=" + task.getState());
				}
			}

			//holder.shareImg.setOnClickListener(this);
			holder.priorityImg.setOnClickListener(this);
			//暂时不允许手动点击暂停
			//holder.stateImg.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Object object = v.getTag();

			switch (v.getId()) {
			case R.id.task_share_img:
				// 分享
				ZLog.e("分享 !");
				break;
				
//			case R.id.task_priority_img:
//				// 优先
//				ZLog.e("优先 !");
//				if (object instanceof INetworkTask) {
//					INetworkTask task = (INetworkTask) object;
//					if(!NetworkTaskManager.getInstance(uid).getPriority(task)) {
//						NetworkTaskManager.getInstance(uid).setPriority(task,true);
//					}
//				}
//				break;

			case R.id.task_state_img:
				// 开关
				ZLog.e("开关 !");
				if (object instanceof INetworkTask) {
					INetworkTask task = (INetworkTask) object;
					ZLog.e(" click task state=" + task.state);
					if (task.state == INetworkTask.STATE_WAITING
							|| task.state == INetworkTask.STATE_PREPARING
							|| task.state == INetworkTask.STATE_RUNNING
							|| task.state == INetworkTask.STATE_EDITORING
							|| task.state == INetworkTask.ACTION_RETRY) {
						//这里有一个问题, 如果是从STATE_PREPARING进入pause状态
						//那么在pause状态再重新开始的时候,应该进入running状态还是STATE_PREPARING?
						// NetworkTaskManager.getInstance(uid).pauseTask(task);
						ZLog.e(" pause task");
						NetworkTaskManager.getInstance(uid).pauseTask(task);
						NetworkTaskManager.getInstance(uid).setPriority(task,false);
					} else if (task.state == INetworkTask.STATE_ERROR) {
						task.waiting();
						ZLog.e(" startNext task");
						NetworkTaskManager.getInstance(uid).setPriority(task,false);
						NetworkTaskManager.getInstance(uid).startNextTask();
					} else if (task.state == INetworkTask.STATE_PAUSED) {
						ZLog.e(" startTask task");
						NetworkTaskManager.getInstance(uid).startTask(task);
					}
				}
				break;
				
			default:
				break;
			}
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.iv_back:
			onBackPressed();
			break;
		case R.id.iv_start_pause:
			switch(taskcontrolType) {
			case TASK_PAUSE_ALL:
				//pause all
				if (NetworkTaskManager.getInstance(uid).isTaskRunning()) {
					NetworkTaskManager.getInstance(uid).pauseAllTask();
					ivPauseStart.setBackgroundResource(R.drawable.btn_activity_networktask_startall);
					taskcontrolType = TaskControlType.TASK_START_ALL;
				}
				break;
			case TASK_START_ALL:
				//start all
				NetworkTaskManager.getInstance(uid).startAllTask(true);
				ivPauseStart.setBackgroundResource(R.drawable.btn_activity_networktask_pauseall);
				taskcontrolType = TaskControlType.TASK_PAUSE_ALL;
				break;
			}
			break;
		}
	}
	
	//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	private void loadTaskImage(ImageView wiv, int type) {
    	switch(type) {
		case 1:
			wiv.setImageResource(R.drawable.tankuang_nothing);
			break;
		case 3:
		case 4:
			wiv.setImageResource(R.drawable.tankuang_luyin);
			break;
		default:
			wiv.setImageResource(R.drawable.maptankuang_moren);
			break;
		}
	}
	
	private LayerDrawable updateProgressBar(LayerDrawable ld, boolean isgray) {
		Drawable pc_Drawable = ld.findDrawableByLayerId(android.R.id.progress);
		Drawable sp_Drawable = ld.findDrawableByLayerId(android.R.id.secondaryProgress);
		if(pc_Drawable != null && sp_Drawable != null) {
			pc_Drawable.setAlpha(isgray ? 0 : 255);
			sp_Drawable.setAlpha(isgray ? 255 : 0);
			ld.setDrawableByLayerId(android.R.id.progress, pc_Drawable);
			ld.setDrawableByLayerId(android.R.id.secondaryProgress, sp_Drawable);
		}
		return ld;
	}
	
}
