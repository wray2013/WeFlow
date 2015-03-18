package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.download.DownloadEvent;
import com.etoc.weflow.download.DownloadItem;
import com.etoc.weflow.download.DownloadManager;
import com.etoc.weflow.download.DownloadStatus;
import com.etoc.weflow.download.DownloadType;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.SpaceUtils;
import com.etoc.weflow.utils.SpaceUtils.SpaceInfo;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DownloadManageActivity extends TitleRootActivity {

	private String TAG = "DownloadManageActivity1";
	private SlidingUpPanelLayout mLayout;
	private ListView lvDownloading = null;
	private ListView lvDownloadDone = null;
	private RelativeLayout rlDownloading = null;
	
	private DownloadingAdapter downloadingAdapter;
	private DownloadDoneAdapter doneAdapter;
	
	private TextView tvDownloading = null;
	private TextView tvDownloadDone = null;
	private TextView tvSpaceInfo = null;
	
	private ImageView ivPrompt = null;
	SharedPreferences mySharedPreferences= null;
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_download_manage;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mySharedPreferences = getSharedPreferences("has_new_download", 
				Activity.MODE_PRIVATE); 
		initViews();
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	private void initViews() {
		setTitleText("下载管理");
		setRightButtonText("清理");
		
		rlDownloading = (RelativeLayout) findViewById(R.id.rl_top_downloading);
		rlDownloading.setOnClickListener(this);
		lvDownloading = (ListView) findViewById(R.id.lv_donwloading);
		lvDownloadDone = (ListView) findViewById(R.id.lv_donwload_done);
		
		tvDownloading = (TextView) findViewById(R.id.tv_top_downloading);
		tvDownloadDone = (TextView) findViewById(R.id.tv_top_download_done_type);
		ViewUtils.setHeight(rlDownloading, 84);
		ViewUtils.setHeight(findViewById(R.id.rl_download_done_top), 84);
		tvDownloading.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView) findViewById(R.id.tv_space_info)).setTextSize(DisplayUtil.textGetSizeSp(this, 28));
		tvDownloadDone.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		ViewUtils.setMarginLeft(findViewById(R.id.tv_top_downloading), 21);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_top_download_done_type), 21);
		ViewUtils.setMarginRight(findViewById(R.id.tv_space_info), 24);
		
		
		downloadingAdapter = new DownloadingAdapter(this, DownloadManager.getInstance().getRunningList());
		doneAdapter = new DownloadDoneAdapter(this, DownloadManager.getInstance().getDoneList());
		
		lvDownloading.setAdapter(downloadingAdapter);
		lvDownloadDone.setAdapter(doneAdapter);
		
		lvDownloadDone.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				DownloadItem item = (DownloadItem)lvDownloadDone.getAdapter().getItem(position);
				if (item.downloadType == DownloadType.APP) {
					String appPkg = item.data;
					if (appPkg != null) {
						try {
							PackageManager pm = WeFlowApplication.getAppInstance().getPackageManager();
							pm.getPackageInfo(appPkg.trim(), PackageManager.GET_ACTIVITIES);
							
						}catch(NameNotFoundException e){
							DownloadManager.installFromPath(DownloadManageActivity.this,item.path);
						}
					}
				} else {
					
				}
				
			}
		});
		
		tvSpaceInfo = (TextView) findViewById(R.id.tv_space_info);
		ViewUtils.setMarginRight(tvSpaceInfo, 24);
		tvSpaceInfo.setTextSize(DisplayUtil.textGetSizeSp(this, 32));
		
		SpaceInfo spaceInfo = SpaceUtils.getSpaceInfoOfSDCard();
		if (spaceInfo != null) {
			tvSpaceInfo.setText("空间:" + spaceInfo.getAvailSize() + " / " + spaceInfo.getTotalSize());
		}
		
		ivPrompt = (ImageView) findViewById(R.id.iv_tips);
		ViewUtils.setSize(ivPrompt, 16, 16);
		ViewUtils.setMarginLeft(ivPrompt, 16);
		
		if (mySharedPreferences.getBoolean("has_new", false)) {
			ivPrompt.setVisibility(View.VISIBLE);
        } else {
        	ivPrompt.setVisibility(View.GONE);
        }
		
		mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelState(PanelState.COLLAPSED);
        mLayout.setPanelHeight(DisplayUtil.getSize(this, 84));
        mLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                tvDownloading.setTextColor(getResources().getColor(R.color.text_gray));
                tvDownloadDone.setTextColor(getResources().getColor(R.color.title_bar_text_color));
                
                if (mySharedPreferences.getBoolean("has_new", false)) {
					SharedPreferences.Editor editor = mySharedPreferences.edit();
					editor.putBoolean("has_new", false);
					editor.commit();
					ivPrompt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                tvDownloading.setTextColor(getResources().getColor(R.color.title_bar_text_color));
                tvDownloadDone.setTextColor(getResources().getColor(R.color.text_gray));
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

	}
	
	private long lastTime = 0;
	public void onEventMainThread(DownloadEvent event) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","onEventMainThread eventType = " + event + " type = " + event.getType());
		switch (event) {
		case PROGRESS_CHANGED:
			if (System.currentTimeMillis() - lastTime >= 1000) {
				downloadingAdapter.notifyDataSetChanged();
				lastTime = System.currentTimeMillis();
			}
			break;
		case STATUS_CHANGED:
			if ((event.getType() & (DownloadEvent.RUNNING_LIST_ADD | DownloadEvent.RUNNING_LIST_DEL)) != 0) {
				downloadingAdapter.notifyDataSetChanged();
			}
			
			if ((event.getType() & (DownloadEvent.DONE_LIST_ADD | DownloadEvent.DONE_LIST_DEL)) != 0) {
				doneAdapter.notifyDataSetChanged();
			}
			if ((event.getType() & DownloadEvent.DONE_LIST_ADD) != 0) {
				
				SharedPreferences mySharedPreferences= getSharedPreferences("has_new_download", 
						Activity.MODE_PRIVATE); 
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				if (mLayout.getPanelState() == PanelState.EXPANDED) {
					editor.putBoolean("has_new", false);
					editor.commit();
					ivPrompt.setVisibility(View.GONE);
				} else {
					ivPrompt.setVisibility(View.VISIBLE);
				}
			} else if (event.getType() == 1 || event.getType() == 0) {
				downloadingAdapter.notifyDataSetChanged();
			} else if (event.getType() == 2) {
				doneAdapter.notifyDataSetChanged();
			}
			break;
		case DONE_CLEAN_ALL:
			doneAdapter.notifyDataSetChanged();
			break;

		}
		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_top_downloading:
			if (mLayout.getPanelState() == PanelState.EXPANDED) {
				mLayout.setPanelState(PanelState.COLLAPSED);
			}
			break;
		case R.id.btn_title_right:
			PromptDialog.Dialog(this, true, "温馨提示", "是否清理全部已下载内容", "是", "否", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					DownloadManager.getInstance().cleanupDownloadedTask();
				}
			}, null);
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	class DownloadingViewHolder {
		ImageView ivHead;
		TextView tvTitle;
		TextView tvProgress;
		TextView tvStatus;
		TextView tvSpeed;
		ProgressBar pbDownloading;
		ImageView ivDelete;
		ImageView ivPause;
	}
	
	class DownloadingAdapter extends BaseAdapter {
		List<DownloadItem> itemList = new ArrayList<DownloadItem>();
		private Context context;
		private LayoutInflater inflater;
		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		public DownloadingAdapter(Context context,List<DownloadItem> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.itemList = list;
			inflater = LayoutInflater.from(context);
			
			imageLoader = MyImageLoader.getInstance();

			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.showImageForEmptyUri(R.drawable.small_pic_default)
					.showImageOnFail(R.drawable.small_pic_default)
					.showImageOnLoading(R.drawable.small_pic_default)
					.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public DownloadItem getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DownloadingViewHolder holder = null;
			if (convertView == null) {
				holder = new DownloadingViewHolder();
				convertView = inflater.inflate(R.layout.item_downloading, null);
				holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tvProgress = (TextView) convertView.findViewById(R.id.tv_download_progress);
				holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_download_status);
				holder.tvSpeed = (TextView) convertView.findViewById(R.id.tv_download_speed);
				holder.ivPause = (ImageView) convertView.findViewById(R.id.iv_pause);
				holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
				holder.pbDownloading = (ProgressBar) convertView.findViewById(R.id.pb_download);
				
				ViewUtils.setSize(holder.ivHead, 96, 96);
				ViewUtils.setMarginLeft(holder.ivHead, 32);
				
				ViewUtils.setMarginLeft(holder.tvTitle, 32);
				holder.tvTitle.setTextSize(DisplayUtil.textGetSizeSp(context, 34));
				
				ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_line_bottom), 32);
				ViewUtils.setMarginRight(convertView.findViewById(R.id.view_line_bottom), 32);
				
				ViewUtils.setMarginBottom(holder.tvProgress, 36);
				holder.tvProgress.setTextSize(DisplayUtil.textGetSizeSp(context, 20));
				
				ViewUtils.setMarginRight(holder.tvStatus, 196);
				ViewUtils.setMarginBottom(holder.tvStatus, 36);
				holder.tvStatus.setTextSize(DisplayUtil.textGetSizeSp(context, 24));
				
				ViewUtils.setMarginRight(holder.tvSpeed, 196);
				holder.tvSpeed.setTextSize(DisplayUtil.textGetSizeSp(context, 20));
				
				ViewUtils.setSize(holder.ivPause, 112, 48);
				ViewUtils.setMarginRight(holder.ivPause, 32);
				ViewUtils.setMarginTop(holder.ivPause, 32);
				
				ViewUtils.setSize(holder.ivDelete, 48, 48);
				ViewUtils.setMarginRight(holder.ivDelete, 32);
				
				ViewUtils.setHeight(holder.pbDownloading, 12);
				ViewUtils.setMarginBottom(holder.pbDownloading, 66);
				ViewUtils.setMarginRight(holder.pbDownloading, 168);
				
				ViewUtils.setHeight(convertView.findViewById(R.id.rl_downloading), 170);
				convertView.setTag(holder);
				
			} else {
				holder = (DownloadingViewHolder) convertView.getTag();
			}
			
			final DownloadItem item = itemList.get(position);
			switch (item.downloadType) {
			case APP:
				break;
			case MOVIE:
				break;

			}
			
			holder.tvTitle.setText(item.title);
			
			imageLoader.displayImage(item.picUrl, holder.ivHead, imageLoaderOptions);
			
			Log.d("=AAA=","downloadStatus = " + item.downloadStatus);
			// TODO
			switch (item.downloadStatus) {
			case RUN:
				if (holder.tvSpeed.getVisibility() == View.GONE) {
					holder.tvSpeed.setVisibility(View.VISIBLE);
				}
				holder.tvSpeed.setText(SpaceUtils.getSize(item.speedBps) + "/s");
				holder.tvStatus.setVisibility(View.GONE);
				if (holder.tvProgress.getVisibility() == View.GONE) {
					holder.tvProgress.setVisibility(View.VISIBLE);
				}
				
				break;
			case PAUSE:
				if (holder.tvStatus.getVisibility() == View.GONE) {
					holder.tvStatus.setVisibility(View.VISIBLE);
				}
				if (item.downloadStatus.getReason().equals(DownloadStatus.REASON_STORAGE_NO_SPACE)) {
					
					holder.tvStatus.setText("空间不足");
					holder.pbDownloading.setProgressDrawable(getResources().getDrawable(R.drawable.drawable_download_progress_low_space));
					PromptDialog.Dialog(context, true, "问题反馈", "设备空间不足请尝试清理后重试", "清理", "关闭", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							DownloadManageActivity.this.onClick(getRightButton());
						}
					}, null);
				} else if (item.downloadStatus.getReason().equals(DownloadStatus.REASON_NETWORK_NO_WIFI)){
					holder.tvStatus.setText("非wifi");
				} else {
					holder.tvStatus.setText("等待下载");
				}
				holder.tvStatus.setTextColor(0xffd80000);
				holder.tvSpeed.setVisibility(View.GONE);
				break;
			case PERPARE:
			case WAIT:
				if (holder.tvStatus.getVisibility() == View.GONE) {
					holder.tvStatus.setVisibility(View.VISIBLE);
				}
				holder.tvStatus.setText("等待下载");
				holder.tvStatus.setTextColor(0xfff65c00);
				holder.tvStatus.setTextSize(DisplayUtil.textGetSizeSp(context, 24));
				holder.tvSpeed.setVisibility(View.GONE);
				holder.tvProgress.setVisibility(View.GONE);
				break;
			case FAIL:
				break;

			}
			
			holder.ivPause.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (item.isPaused()) {
						DownloadManager.getInstance().resumeItem(item);
					} else {
						DownloadManager.getInstance().pauseItem(item);
					}
					DownloadManager.getInstance().notifyLockItems();
				}
			});
			
//			Log.d("=AAA=","******before******** downloadSize " + item.downloadSize + " wholeSize = " + item.wholeSize);
			
			holder.tvProgress.setText(SpaceUtils.getSize(item.downloadSize) + "/" + SpaceUtils.getSize(item.wholeSize));
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DownloadManager.getInstance().delDownloadingTask(item);
					notifyDataSetChanged();
				}
			});
			long progress = 0;
			if (item.wholeSize > 0) {
				progress = (long)(item.downloadSize) * 100 / item.wholeSize;
				holder.pbDownloading.setProgress((int) progress);
			}
//			Log.d("=AAA=","******after******** downloadSize " + item.downloadSize + " wholeSize = " + item.wholeSize + " progress = " + progress);
			
			return convertView;
		}
	}
	
	class DownloadDoneViewHolder {
		ImageView ivHead;
		TextView tvTitle;
		TextView tvSize;
		ImageView ivDelete;
	}
	
	class DownloadDoneAdapter extends BaseAdapter {
		
		List<DownloadItem> itemList = new ArrayList<DownloadItem>();
		private Context context;
		private LayoutInflater inflater;
		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		public DownloadDoneAdapter(Context context,List<DownloadItem> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.itemList = list;
			inflater = LayoutInflater.from(context);
			
			imageLoader = MyImageLoader.getInstance();

			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.showImageForEmptyUri(R.drawable.small_pic_default)
					.showImageOnFail(R.drawable.small_pic_default)
					.showImageOnLoading(R.drawable.small_pic_default)
					.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public DownloadItem getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DownloadDoneViewHolder holder = null;
			if (convertView == null) {
				holder = new DownloadDoneViewHolder();
				convertView = inflater.inflate(R.layout.item_download_done, null);
				holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tvSize = (TextView) convertView.findViewById(R.id.tv_size);
				holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
				
				ViewUtils.setSize(holder.ivHead, 96, 96);
				ViewUtils.setMarginLeft(holder.ivHead, 32);
				
				ViewUtils.setMarginLeft(holder.tvTitle, 32);
				holder.tvTitle.setTextSize(DisplayUtil.textGetSizeSp(context, 34));
				
				ViewUtils.setMarginLeft(holder.tvSize, 8);
				holder.tvSize.setTextSize(DisplayUtil.textGetSizeSp(context, 24));
				
				ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_line_bottom), 32);
				ViewUtils.setMarginRight(convertView.findViewById(R.id.view_line_bottom), 32);
				
				ViewUtils.setSize(holder.ivDelete, 48, 48);
				ViewUtils.setMarginRight(holder.ivDelete, 32);
				
				ViewUtils.setHeight(convertView.findViewById(R.id.rl_download_done), 170);
				convertView.setTag(holder);
				
			} else {
				holder = (DownloadDoneViewHolder) convertView.getTag();
			}
			
			final DownloadItem item = itemList.get(position);
			switch (item.downloadType) {
			case APP:
				break;
			case MOVIE:
				break;

			}
			
			imageLoader.displayImage(item.picUrl, holder.ivHead, imageLoaderOptions);
			holder.tvTitle.setText(item.title);
			holder.tvSize.setText(SpaceUtils.getSize(item.wholeSize));
			
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DownloadManager.getInstance().delDownloadedTask(item);
					notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
		
	}
	

}
