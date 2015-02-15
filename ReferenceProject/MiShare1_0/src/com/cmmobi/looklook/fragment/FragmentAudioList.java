package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.file.ZFileSystem;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.MediaScanActivity;
import com.cmmobi.looklook.activity.ShareDiaryActivity;
import com.cmmobi.looklook.activity.ShareLookLookFriendsActivity;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.DiaryController.FileOperate;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import effect.XMp3ToMp4;
import effect.XMp3ToMp4.OnScheduleListener;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.fragment
 * @filename FragmentPictureList.java
 * @summary 音频扫描界面
 * @author Lanhai
 * @date 2013-11-18
 * @version 1.0
 */
public class FragmentAudioList extends Fragment implements OnItemClickListener, Callback
{
	private final String TAG = "FragmentAudioList";
	
	private static final int HANDLER_MESSAGE_LOAD_COMPLETE = 0;
	
	private final String MIME_TYPE_MP3 = "audio/mpeg";
	private final String MIME_TYPE_MP4 = "audio/mp4";
	
	private Handler handler = null;
	
	private LinearLayout root;
	
	private ArrayList<AudioProperty> mListMediaProperty = null;
	private ArrayList<String> diaryUUIDList = null;
	
	private ListView mListView = null;
	private MediaAdapter mAdapter = null;
	private String mUserID = null;
	
	// 记录选中个数 新选择的在尾部
	private LinkedList<Integer> mSelectArray = new LinkedList<Integer>();
	
	private ArrayList<DiaryWrapper> mDiaryWrapper = new ArrayList<DiaryWrapper>();
	
	private int diaryNum = 0;
	
	private AsyncTask<ContentResolver, Void, Void> mLoadingTask = null;
	private CountDownLatch processSignal;
	private boolean taskCancel = false;
	private boolean isFromVshare = false;
	
	// 元素
	public class AudioProperty
	{
		String TITLE;
		String DATA;
		String DATE_ADDED;
		String DATE_MODIFIED;
//		String DISPLAY_NAME;
		String MIME_TYPE;
		String SIZE;
//			String WIDTH;
//			String HEIGHT;
		String DURATION;
//		String IS_ALARM;
//		String IS_MUSIC;
//		String IS_NOTIFICATION;
//		String IS_PODCAST;
//		String IS_RINGTONE;
		
		String ALBUM;
		String ALBUM_KEY;
		String ARTIST;
		String ALBUM_ART;
		
		boolean selected = false;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = (LinearLayout) inflater.inflate(R.layout.fragment_scanaudio, container, false);
		
		mListView = (ListView) root.findViewById(R.id.lv_media_audio);
		
		mListView.setOnItemClickListener(this);
		
		mUserID = ActiveAccount.getInstance(getActivity()).getUID();
		
		//getActivity()在asynch中可能为空
		ContentResolver contentResolver = getActivity().getContentResolver();
		ZDialog.show(R.layout.progressdialog, false, true, getActivity(), true);
		
		mLoadingTask = new AsyncTask<ContentResolver, Void, Void>()
		{
			@Override
			protected void onPreExecute()
			{
				taskCancel = false;
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(ContentResolver... params)
			{
				loadData(params[0]);
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				handler.sendEmptyMessage(HANDLER_MESSAGE_LOAD_COMPLETE);
				super.onPostExecute(result);
			}
		};
		mLoadingTask.execute(contentResolver);
		isFromVshare = ((MediaScanActivity) getActivity()).isFromVshare;
		
		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		handler = new Handler(this);
		mListMediaProperty = new ArrayList<AudioProperty>();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy()
	{
		if (mLoadingTask != null && mLoadingTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			taskCancel = true;
			mLoadingTask.cancel(true);
			ZDialog.dismiss();
		}
		super.onDestroy();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if(!mListMediaProperty.get(position).selected && mSelectArray.size() >= 9
				- ShareDiaryActivity.diaryGroup.size())
		{
			new Xdialog.Builder(getActivity())
			.setMessage(getResources().getString(R.string.load_max))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.create().show();
			return;
		}
		
		mListMediaProperty.get(position).selected = !mListMediaProperty.get(position).selected;
		if(mListMediaProperty.get(position).selected)
		{
			if(!mSelectArray.contains(mListMediaProperty.get(position))){
				mSelectArray.addLast(position);
			}
		}
		else
		{
			try
			{
				mSelectArray.remove(Integer.valueOf(position));
			}
			catch (Exception e)
			{
				Log.v(TAG, "select item not exist");
			}
		}
		
		mAdapter.notifyDataSetChanged();
		
		MediaScanActivity act = (MediaScanActivity) getActivity();
		if(mSelectArray.size() > 0)
		{
			act.changeTitle("已选择" + mSelectArray.size() + "首");
		}
		else
		{
			act.changeTitle(null);
		}
		
	}
	// 加载数据
	private void loadData(ContentResolver contentResolver)
	{
//		ContentResolver contentResolver = getActivity().getContentResolver();
		Cursor cursor = null;
		
		String[] projectionAudio = new String[] {
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DATE_ADDED,
				MediaStore.Audio.Media.DATE_MODIFIED,
//				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.MIME_TYPE,
				MediaStore.Audio.Media.SIZE,
				// MediaStore.Audio.Media.WIDTH,
				// MediaStore.Audio.Media.HEIGHT,
				MediaStore.Audio.Media.DURATION,
//				MediaStore.Audio.Media.IS_ALARM,
//				MediaStore.Audio.Media.IS_MUSIC,
//				MediaStore.Audio.Media.IS_NOTIFICATION,
//				MediaStore.Audio.Media.IS_PODCAST,
//				MediaStore.Audio.Media.IS_RINGTONE,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.ALBUM_KEY,
				MediaStore.Audio.Media.ARTIST };
		
		if(contentResolver == null)
		{
			return;
		}
		
		try
		{
			cursor = contentResolver.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, projectionAudio, null, null,
					MediaStore.Audio.Media.DATE_MODIFIED + " desc");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		if (cursor != null)
		{
			cursor.moveToFirst();

			for (int counter = 0; counter < cursor.getCount() && !taskCancel; counter++)
			{
				AudioProperty audio = new AudioProperty();
				audio.TITLE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				audio.DATA = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				audio.DATE_ADDED = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
				audio.DATE_MODIFIED = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
//				audio.DISPLAY_NAME = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				audio.MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
				audio.SIZE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
				audio.DURATION = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
				// audio.WIDTH =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.WIDTH));
				// audio.HEIGHT =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.HEIGHT));
//				audio.IS_ALARM = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_ALARM));
//				audio.IS_MUSIC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
//				audio.IS_NOTIFICATION = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION));
//				audio.IS_PODCAST = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_PODCAST));
//				audio.IS_RINGTONE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE));
				audio.ALBUM = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				audio.ALBUM_KEY = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));
				audio.ARTIST = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

/*				Log.w(TAG, "----file TITLE is: " + audio.TITLE);
				Log.w(TAG, "----file DATA is: " + audio.DATA);
//				Log.w(TAG, "----file DATE_ADDED is: " + audio.DATE_ADDED);
//				Log.w(TAG, "----file DATE_MODIFIED is: " + audio.DATE_MODIFIED);
//				Log.w(TAG, "----file DISPLAY_NAME is: " + audio.DISPLAY_NAME);
				Log.w(TAG, "----file MIME_TYPE is: " + audio.MIME_TYPE);
				Log.w(TAG, "----file SIZE is: " + audio.SIZE);
				Log.w(TAG, "----file DURATION is: " + audio.DURATION);
				// Log.w(TAG, "----file WIDTH is: " + audio.WIDTH);
				// Log.w(TAG, "----file HEIGHT is: " + audio.HEIGHT);
//				Log.w(TAG, "----file IS_ALARM is: " + audio.IS_ALARM);
//				Log.w(TAG, "----file IS_MUSIC is: " + audio.IS_MUSIC);
//				Log.w(TAG, "----file IS_NOTIFICATION is: " + audio.IS_NOTIFICATION);
//				Log.w(TAG, "----file IS_PODCAST is: " + audio.IS_PODCAST);
//				Log.w(TAG, "----file IS_RINGTONE is: " + audio.IS_RINGTONE);
				Log.w(TAG, "----file ALBUM is: " + audio.ALBUM);
				Log.w(TAG, "----file ALBUM_KEY is: " + audio.ALBUM_KEY);
				Log.w(TAG, "----file ARTIST is: " + audio.ARTIST);*/

				try
				{
					if (Long.valueOf(audio.SIZE) > 4096l
//							&& Long.valueOf(audio.DURATION) > 1000l
//							&& Integer.valueOf(audio.IS_ALARM) == 0 && Integer.valueOf(audio.IS_NOTIFICATION) == 0
//							&& Integer.valueOf(audio.IS_RINGTONE) == 0
							&& (MIME_TYPE_MP3.equals(audio.MIME_TYPE) || MIME_TYPE_MP4.equals(audio.MIME_TYPE)))
						mListMediaProperty.add(audio);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			cursor.close();
			cursor = null;
		}

		try
		{
			cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionAudio, null, null,
					MediaStore.Audio.Media.DATE_MODIFIED + " desc");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		if (cursor != null)
		{
			cursor.moveToFirst();

			for (int counter = 0; counter < cursor.getCount() && !taskCancel; counter++)
			{
				AudioProperty audio = new AudioProperty();
				audio.TITLE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				audio.DATA = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				audio.DATE_ADDED = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
				audio.DATE_MODIFIED = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
//				audio.DISPLAY_NAME = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				audio.MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
				audio.SIZE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
				audio.DURATION = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
				// audio.WIDTH =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.WIDTH));
				// audio.HEIGHT =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.HEIGHT));
//				audio.IS_ALARM = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_ALARM));
//				audio.IS_MUSIC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
//				audio.IS_NOTIFICATION = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION));
//				audio.IS_PODCAST = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_PODCAST));
//				audio.IS_RINGTONE = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE));
				audio.ALBUM = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				audio.ALBUM_KEY = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));
				audio.ARTIST = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

/*				Log.w(TAG, "----file TITLE is: " + audio.TITLE);
				Log.w(TAG, "----file DATA is: " + audio.DATA);
//				Log.w(TAG, "----file DATE_ADDED is: " + audio.DATE_ADDED);
//				Log.w(TAG, "----file DATE_MODIFIED is: " + audio.DATE_MODIFIED);
//				Log.w(TAG, "----file DISPLAY_NAME is: " + audio.DISPLAY_NAME);
				Log.w(TAG, "----file MIME_TYPE is: " + audio.MIME_TYPE);
				Log.w(TAG, "----file SIZE is: " + audio.SIZE);
				Log.w(TAG, "----file DURATION is: " + audio.DURATION);
				// Log.w(TAG, "----file WIDTH is: " + audio.WIDTH);
				// Log.w(TAG, "----file HEIGHT is: " + audio.HEIGHT);
//				Log.w(TAG, "----file IS_ALARM is: " + audio.IS_ALARM);
//				Log.w(TAG, "----file IS_MUSIC is: " + audio.IS_MUSIC);
//				Log.w(TAG, "----file IS_NOTIFICATION is: " + audio.IS_NOTIFICATION);
//				Log.w(TAG, "----file IS_PODCAST is: " + audio.IS_PODCAST);
//				Log.w(TAG, "----file IS_RINGTONE is: " + audio.IS_RINGTONE);
				Log.w(TAG, "----file ALBUM is: " + audio.ALBUM);
				Log.w(TAG, "----file ALBUM_KEY is: " + audio.ALBUM_KEY);
				Log.w(TAG, "----file ARTIST is: " + audio.ARTIST);*/

				try
				{
					if (Long.valueOf(audio.SIZE) > 4096l
							&& Long.valueOf(audio.DURATION) > 1000l
//							&& Integer.valueOf(audio.IS_ALARM) == 0 && Integer.valueOf(audio.IS_NOTIFICATION) == 0
//							&& Integer.valueOf(audio.IS_RINGTONE) == 0
							&& (MIME_TYPE_MP3.equals(audio.MIME_TYPE) || MIME_TYPE_MP4.equals(audio.MIME_TYPE)))
						mListMediaProperty.add(audio);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				cursor.moveToNext();
			}
			cursor.close();
			cursor = null;
		}
		
		// 排序
		Collections.sort(mListMediaProperty, new TimeComparator());

		Iterator<AudioProperty> ite = mListMediaProperty.iterator();
		while (ite.hasNext() && !taskCancel)
		{
			AudioProperty audioProperty = (AudioProperty) ite.next();
			String[] albumAudio = new String[] { MediaStore.Audio.Albums.ALBUM_ART };
			try
			{
				cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumAudio,
						MediaStore.Audio.Albums.ALBUM_KEY + "= ?", new String[] { audioProperty.ALBUM_KEY },
						MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			if (cursor != null)
			{
				cursor.moveToFirst();

				for (int counter = 0; counter < cursor.getCount() && !taskCancel; counter++)
				{
					audioProperty.ALBUM_ART = cursor
							.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
					Log.w(TAG, "----file ALBUM_ART is: " + audioProperty.ALBUM_ART);

					cursor.moveToNext();
				}
				cursor.close();
				cursor = null;
			}
			try
			{
				cursor = contentResolver.query(MediaStore.Audio.Albums.INTERNAL_CONTENT_URI, albumAudio,
						MediaStore.Audio.Albums.ALBUM_KEY + "= ?", new String[] { audioProperty.ALBUM_KEY },
						MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			if (cursor != null)
			{
				cursor.moveToFirst();

				for (int counter = 0; counter < cursor.getCount() && !taskCancel; counter++)
				{
					audioProperty.ALBUM_ART = cursor
							.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
					Log.w(TAG, "----file ALBUM_ART is: " + audioProperty.ALBUM_ART);

					cursor.moveToNext();
				}
				cursor.close();
				cursor = null;
			}
		}
	}
	
	private class ViewHolder
	{
		ImageView image;
		TextView name;
		TextView artist;
		TextView album;
		TextView selected;
	}
	
	private class MediaAdapter extends BaseAdapter
	{
		private LayoutInflater inflater;
		
		public MediaAdapter(Context context)
		{
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount()
		{
//			Log.v(TAG, "audio size = " + mListMediaProperty.size());
			return mListMediaProperty.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			if(convertView == null)
			{
				convertView = inflater.inflate(R.layout.list_item_audio, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.iv_list_audio_icon);
				holder.name = (TextView) convertView.findViewById(R.id.iv_list_audio_name);
				holder.artist = (TextView) convertView.findViewById(R.id.iv_list_audio_artist);
				holder.album = (TextView) convertView.findViewById(R.id.iv_list_audio_album);
				holder.selected = (TextView) convertView.findViewById(R.id.iv_list_audio_selected);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			AudioProperty audio = mListMediaProperty.get(position);
//			Bitmap bitmap = BitmapFactory.decodeFile(audio.ALBUM_ART);
//			holder.image.setImageBitmap(bitmap);
			
			if(ZFileSystem.isFileExists(audio.ALBUM_ART)) //文件不存在 不加载封面
			{
			ImageLoader loader = ImageLoader.getInstance();
			
//			loader.init(ImageLoaderConfiguration.createDefault(getActivity()));
			
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.icon_default_album)
					.showImageForEmptyUri(R.drawable.icon_default_album)
					.showImageOnFail(R.drawable.icon_default_album)
					.cacheInMemory(true).cacheOnDisc(true)
					.displayer(new SimpleBitmapDisplayer())
					// .displayer(new CircularBitmapDisplayer()) 圆形图片
					// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
					.build();
			
//			ImageLoadingListener listener = new AnimateFirstDisplayListener()
//			{
//				@Override
//				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
//				{
//				}
//			};
			
//			LayoutParams p = holder.image.getLayoutParams();
//			p.width = 128;
//			p.height = 128;
//			holder.image.setLayoutParams(p);
			
			loader.displayImage("file://" + audio.ALBUM_ART, holder.image, options, mUserID, 0);
			}
			String name = audio.TITLE; 
			String artist = audio.ARTIST;
			String album = audio.ALBUM;
			holder.name.setText(name);
			holder.artist.setText(artist);
			holder.album.setText(album);
			if(audio.selected)
			{
				holder.selected.setVisibility(View.VISIBLE);
				for (int i = 0; i < mSelectArray.size(); i ++)
				{
					if(mSelectArray.get(i) == position)
					{
						holder.selected.setText(String.valueOf(i+1));
						break;
					}
				}
			}
			else
			{
				holder.selected.setVisibility(View.INVISIBLE);
			}
			
			return convertView;
		}
		
	}
	
	// 创建日记
	public void createDiary()
	{
		
		mDiaryWrapper.clear();
		diaryNum = mSelectArray.size();
		diaryUUIDList = new ArrayList<String>();
		if(diaryNum == 0)
		{
			Prompt.Alert(getResources().getString(R.string.load_min));
		}
		else
		{
			int i = mSelectArray.get(mSelectArray.size() - diaryNum);
			
			doCreate(i);
		}
		
	}
	
	private void doCreate(final int i)
	{
		if(ZFileSystem.isFileExists(mListMediaProperty.get(i).DATA))
		{
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					AudioProperty audioProperty = mListMediaProperty.get(i);
					String diaryUUID = DiaryController.getNextUUID();
					String attachUUID = DiaryController.getNextUUID();
					diaryUUIDList.add(diaryUUID);
					/*String type = "";
					if(MIME_TYPE_MP3.equals(audioProperty.MIME_TYPE))
					{
						type = GsonProtocol.SUFFIX_MP3;
					}
					else if(MIME_TYPE_MP4.equals(audioProperty.MIME_TYPE))
					{
						type = GsonProtocol.SUFFIX_MP4;
					}
					
					Integer dur;
					try
					{
						dur = Integer.valueOf(audioProperty.DURATION);
						dur = (dur + 500) / 1000;
					}
					catch (Exception e)
					{
						dur = 0;
					}*/
					String inputPath = audioProperty.DATA;
					String audioID = DiaryController.getNextUUID();
					String outputPath = DiaryController.getAbsolutePathUnderUserDir()  +  "/audio/" + audioID + ".mp4" ;
					XMp3ToMp4 mp3Processer = new XMp3ToMp4();
					mp3Processer.setOnInfoListener(new MyOnInfoListener());
					mp3Processer.open(inputPath, outputPath);
					mp3Processer.start();
					
					processSignal = new CountDownLatch(1);
					try {
						processSignal.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mp3Processer.release();
					
					Log.d(TAG,"outputPath = " + outputPath);
					Log.v(TAG, "create audio diary");
					DiaryController.getInstanse().includeDiary(handler,
							diaryUUID,
							attachUUID,
							outputPath,
							GsonProtocol.ATTACH_TYPE_AUDIO,
							GsonProtocol.SUFFIX_MP4,
							"",
							"",
							GsonProtocol.EMPTY_VALUE,
							CommonInfo.getInstance().getLongitude(),
							CommonInfo.getInstance().getLatitude(),
							DiaryController.getPositionString1(),
							"",
							true,
							FileOperate.COPY,
							"");
					
				}
			}).start();
		}
		else
		{
			diaryNum --;
			Prompt.Alert(getResources().getString(R.string.load_error_notexit));
			if (diaryNum == 0)
			{
				// 一个都没导入 退出界面 不进入diarypreview
				if(diaryUUIDList.size() == 0)
				{
					getActivity().finish();
					return;
				}

				endCreate();
			}
			else if(diaryNum > 0) 
			{
				doCreate(mSelectArray.get(mSelectArray.size() - diaryNum));
			}
		}
	}
	
	class MyOnInfoListener implements OnScheduleListener{
		@Override
		public void OnSchedule(double percent) {
			Log.d(TAG,"OnSchedule percent = " + percent);
		}
		@Override
		public void OnFinish() {
			Log.d(TAG,"OnFinish ");
			processSignal.countDown();
		}
    	
    }
	
	private void endCreate()
	{
		// 按照生成日记的顺序排序
//		ArrayList<MyDiaryList> diaryList = new ArrayList<MyDiaryList>();
//		
//		String UUID = diaryUUIDList.get(diaryUUIDList.size() -1);
//		for (int i = diaryUUIDList.size() - 1; i >= 0; i--) {
//			String diaryUUID = diaryUUIDList.get(i);
//			MyDiaryList myDiaryList = DiaryManager.getInstance().findDiaryGroupByUUID(diaryUUID);
//			if(myDiaryList != null)
//			{
//				diaryList.add(myDiaryList);
//			}
//		}
//		
//		//  日记已经全部生成完成，后面做界面跳转相关操作。
//		DiaryManager.getInstance().setDetailDiaryList(diaryList, 0);
//		Intent intent = new Intent(getActivity(), DiaryPreviewActivity.class);
//		intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, UUID);
//		startActivity(intent);
//		getActivity().finish();
//		ZDialog.dismiss();
		
		if (isFromVshare) {
			ShareDiaryActivity.diaryGroup.clear();
		}
		for(int i = 0; i < mDiaryWrapper.size(); i ++)
		{
			ShareDiaryActivity.diaryGroup.add(mDiaryWrapper.get(i).diary);
			ShareDiaryActivity.diaryuuid.add(mDiaryWrapper.get(i).diary);
		}
		getActivity().setResult(Activity.RESULT_OK);
		if (isFromVshare) {
			Intent intent = new Intent(getActivity(), ShareLookLookFriendsActivity.class);
			intent.putExtra(VShareFragment.IS_FROM_VSHARE, true);
			startActivityForResult(intent, ShareDiaryActivity.REQUEST_CODE_USER);
		} else {
			getActivity().finish();
		}
		ZDialog.dismiss();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
		case HANDLER_MESSAGE_LOAD_COMPLETE:
			mAdapter = new MediaAdapter(getActivity());
			mListView.setAdapter(mAdapter);
//			mAdapter.notifyDataSetChanged();
			ZDialog.dismiss();
			break;
		case DiaryController.DIARY_REQUEST_DONE:
			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE");
			diaryNum --;
			if (msg.obj == null)
			{// false
				Prompt.Alert(getResources().getString(R.string.load_error));
			}
			else
			{
				mDiaryWrapper.add((DiaryWrapper)msg.obj);
			}
			if (diaryNum == 0) {
				endCreate();
			}
			else if(diaryNum > 0) // 避免因日记生成模块错误 导致diaryNum < 0
			{
				int i = mSelectArray.get(mSelectArray.size() - diaryNum);
				
				doCreate(i);
			}
			break;
		}
		return false;
	}
	
	// 按日记时间排序
	public class TimeComparator implements Comparator<AudioProperty> {
        public int compare(AudioProperty arg0, AudioProperty arg1) {
        	
        	long l0 = 0;
        	long l1 = 0;
        	try
			{
				l0 = Long.parseLong(arg0.DATE_MODIFIED);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
        	try
			{
        		l1 = Long.parseLong(arg1.DATE_MODIFIED);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
        	
			if (l0 < l1)
			{
				return 1;
			}
			if (l0 == l1)
			{
				return 0;
			}
			return -1;
        }
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode)
		{
		case ShareDiaryActivity.REQUEST_CODE_USER:
			if(resultCode == getActivity().RESULT_OK) {
				getActivity().finish();
			} else {
				
			}
			break;
		}
	}

}
