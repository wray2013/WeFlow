package com.cmmobi.looklook.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zhugefubin.maptool.ConverterTool;
import cn.zhugefubin.maptool.Point;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.file.ZFileSystem;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPositionEditActivity;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.MediaScanActivity;
import com.cmmobi.looklook.activity.PhotoClipActivity;
import com.cmmobi.looklook.activity.ShareDiaryActivity;
import com.cmmobi.looklook.activity.ShareLookLookFriendsActivity;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.DiaryController.FileOperate;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.info.location.MyAddressInfo;
import com.cmmobi.looklook.info.location.OnPoiSearchListener;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.XUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.fragment
 * @filename FragmentPictureList.java
 * @summary 图片扫描界面
 * @author Lanhai
 * @date 2013-11-18
 * @version 1.0
 */
public class FragmentPictureList extends Fragment implements OnItemClickListener, Callback, OnPoiSearchListener
{
	private final String TAG = "FragmentPictureList";
	
	private static final int HANDLER_MESSAGE_LOAD_COMPLETE = 0;
	
	private final String MIME_TYPE_JPEG = "image/jpeg";
	
	private Handler handler = null;
	
	private static final int REQUEST_CODE_PHOTE_CLIP = 0;
	
	private LinearLayout root;
	
	private ArrayList<PictureProperty> mListMediaProperty = null;
	private ArrayList<String> diaryUUIDList = null;
	
	private GridView mGridView = null;
	private MediaAdapter mAdapter = null;
	private String mUserID = null;
	
	private int current = 0;
	
	// 记录选中个数 新选择的在尾部
	private LinkedList<Integer> mSelectArray = new LinkedList<Integer>();
	
	private ArrayList<DiaryWrapper> mDiaryWrapper = new ArrayList<DiaryWrapper>();
	
	private int diaryNum = 0;
	
	private AsyncTask<ContentResolver, Void, Void> mLoadingTask = null;
	private boolean taskCancel = false;
	
	private MyAddressInfo addInfo = null;
	
	public int mode = MediaScanActivity.MODE_PIC_NORMAL;
	private boolean isFromVshare = false;
	private int shortLen = 0;
	// 元素
	public class PictureProperty
	{
//		String TITLE;
		String DATA;
//		String DATE_ADDED;
		String DATE_MODIFIED;
//		String DISPLAY_NAME;
		String MIME_TYPE;
		String SIZE;
//		String WIDTH;
//		String HEIGHT;
//		String ORIENTATION;
		
		String LATITUDE;
		String LONGITUDE;
		String DATE_TAKEN;
		
		boolean selected = false;
		String ClipData = null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = (LinearLayout) inflater.inflate(R.layout.fragment_scanpicture, container, false);
		
		mGridView = (GridView) root.findViewById(R.id.lv_media_picture);
		
		mGridView.setOnItemClickListener(this);
		
		mUserID = ActiveAccount.getInstance(getActivity()).getUID();
		
		//getActivity()在asynch中可能为空
		ContentResolver contentResolver = getActivity().getContentResolver();
		ZDialog.show(R.layout.progressdialog, false, true, getActivity(),true);
		
		addInfo = MyAddressInfo.getInstance(getActivity());
		DisplayMetrics dm = getResources().getDisplayMetrics();
		shortLen = dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;
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
		Log.d(TAG,"onCreateView in");
		mLoadingTask.execute(contentResolver);
		
		mode = ((MediaScanActivity) getActivity()).mode;
		isFromVshare = ((MediaScanActivity) getActivity()).isFromVshare;
		
		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG,"onCreate in");
		handler = new Handler(this);
		mListMediaProperty = new ArrayList<PictureProperty>();
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
		addInfo.removeListener(FragmentPictureList.this);
		handler = null;
		mGridView = null;
		Log.d(TAG,"FragmentPictureList onDestroy  in wrapperSize = " + DiaryController.getInstanse().getWrapperSize());
		super.onDestroy();
	}
	
	private static long lastClickTime;

	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800)
		{
			return false;
		}
		lastClickTime = time;
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (!isFastDoubleClick()) {
			return;
		}
		if(!mListMediaProperty.get(position).selected && mSelectArray.size() >= 9
				- (mode == MediaScanActivity.MODE_PIC_NORMAL ? 0 : ShareDiaryActivity.diaryGroup.size()))
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
		
		
		if(mListMediaProperty.get(position).selected)
		{
			mListMediaProperty.get(position).selected = false;
//			mListMediaProperty.get(position).ClipData = null;
			try
			{
				mSelectArray.remove(Integer.valueOf(position));
			}
			catch (Exception e)
			{
				Log.v(TAG, "select item not exist");
			}
			mAdapter.notifyDataSetChanged();
			
			MediaScanActivity act = (MediaScanActivity) getActivity();
			if(mSelectArray.size() > 0)
			{
				act.changeTitle("已选择" + mSelectArray.size() + "项");
			}
			else
			{
				act.changeTitle(null);
			}
		}
		else
		{
			Intent intent = new Intent(getActivity(), PhotoClipActivity.class);
			intent.putExtra(PhotoClipActivity.PHOTO_PATH, mListMediaProperty.get(position).DATA);
			intent.putExtra(PhotoClipActivity.IS_FROM_IMPORT, true);
			startActivityForResult(intent, REQUEST_CODE_PHOTE_CLIP);
			current = position;
		}
	}
	
	private String tmpPath = null;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQUEST_CODE_PHOTE_CLIP)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				if (mListMediaProperty != null && mListMediaProperty.size() > current) {
					mListMediaProperty.get(current).selected = true;
					mListMediaProperty.get(current).ClipData = data.getStringExtra(PhotoClipActivity.CLIP_PATH);
					mSelectArray.addLast(current);
					
					mAdapter.notifyDataSetChanged();
					
					MediaScanActivity act = (MediaScanActivity) getActivity();
					if(mSelectArray.size() > 0)
					{
						act.changeTitle("已选择" + mSelectArray.size() + "项");
					}
					else
					{
						act.changeTitle(null);
					}
					current = 0;
				} else {
					tmpPath = data.getStringExtra(PhotoClipActivity.CLIP_PATH);
				}
			}
		} else if (requestCode == ShareDiaryActivity.REQUEST_CODE_USER) {
			if(resultCode == getActivity().RESULT_OK) {
				getActivity().finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void selectPicture(String path) {
		mListMediaProperty.get(current).selected = true;
		mListMediaProperty.get(current).ClipData = path;
		mSelectArray.addLast(current);
		
		mAdapter.notifyDataSetChanged();
		
		MediaScanActivity act = (MediaScanActivity) getActivity();
		if(mSelectArray.size() > 0)
		{
			act.changeTitle("已选择" + mSelectArray.size() + "项");
		}
		else
		{
			act.changeTitle(null);
		}
		current = 0;
		tmpPath = null;
	}

	// 加载数据
	private void loadData(ContentResolver contentResolver)
	{
//		ContentResolver contentResolver = getActivity().getContentResolver();
		Cursor cursor = null;

		String[] projectionPicture = new String[] {
//				MediaStore.Images.Media.TITLE,
				MediaStore.Images.Media.DATA,
//				MediaStore.Images.Media.DATE_ADDED,
				MediaStore.Images.Media.DATE_MODIFIED,
//				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.MIME_TYPE,
				MediaStore.Images.Media.SIZE,
				// MediaStore.Images.Media.WIDTH,
				// MediaStore.Images.Media.HEIGHT,
//				MediaStore.Images.Media.ORIENTATION
				
				MediaStore.Images.Media.LATITUDE,
				MediaStore.Images.Media.LONGITUDE,
				MediaStore.Images.Media.DATE_TAKEN
				};
		
		if(contentResolver == null)
		{
			return;
		}
		
		try
		{
			cursor = contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, projectionPicture, null, null,
					MediaStore.Images.Media.DATE_MODIFIED + " desc");
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
				PictureProperty picture = new PictureProperty();
//				picture.TITLE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
				picture.DATA = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//				picture.DATE_ADDED = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
				picture.DATE_MODIFIED = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
//				picture.DISPLAY_NAME = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				picture.MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
				picture.SIZE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
				// picture.WIDTH =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
				// picture.HEIGHT =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
//				picture.ORIENTATION = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
				picture.LATITUDE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
				picture.LONGITUDE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
				picture.DATE_TAKEN = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
				
/*//				Log.w(TAG, "----file TITLE is: " + picture.TITLE);
				Log.w(TAG, "----file DATA is: " + picture.DATA);
//				Log.w(TAG, "----file DATE_ADDED is: " + picture.DATE_ADDED);
//				Log.w(TAG, "----file DATE_MODIFIED is: " + picture.DATE_MODIFIED);
//				Log.w(TAG, "----file DISPLAY_NAME is: " + picture.DISPLAY_NAME);
				Log.w(TAG, "----file MIME_TYPE is: " + picture.MIME_TYPE);
				Log.w(TAG, "----file SIZE is: " + picture.SIZE);
				// Log.w(TAG, "----file WIDTH is: " + picture.WIDTH);
				// Log.w(TAG, "----file HEIGHT is: " + picture.HEIGHT);
//				Log.w(TAG, "----file ORIENTATION is: " + picture.ORIENTATION);
*/
//				Log.w(TAG, "----file LATITUDE is: " + picture.LATITUDE);
//				Log.w(TAG, "----file LONGITUDE is: " + picture.LONGITUDE);
				
				try
				{
					if (Long.valueOf(picture.SIZE) > 4096l/* && Integer.valueOf(picture.WIDTH) > 16
							&& Integer.valueOf(picture.HEIGHT) > 16*/
							&& (MIME_TYPE_JPEG.equals(picture.MIME_TYPE)))
						mListMediaProperty.add(picture);
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

		try
		{
			cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionPicture, null, null,
					MediaStore.Images.Media.DATE_MODIFIED + " desc");
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
				PictureProperty picture = new PictureProperty();
//				picture.TITLE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
				picture.DATA = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//				picture.DATE_ADDED = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
				picture.DATE_MODIFIED = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
//				picture.DISPLAY_NAME = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
				picture.MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
				picture.SIZE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
				// picture.WIDTH =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
				// picture.HEIGHT =
				// cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
//				picture.ORIENTATION = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
				picture.LATITUDE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
				picture.LONGITUDE = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
				picture.DATE_TAKEN = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
				
/*//				Log.w(TAG, "----file TITLE is: " + picture.TITLE);
				Log.w(TAG, "----file DATA is: " + picture.DATA);
//				Log.w(TAG, "----file DATE_ADDED is: " + picture.DATE_ADDED);
//				Log.w(TAG, "----file DATE_MODIFIED is: " + picture.DATE_MODIFIED);
//				Log.w(TAG, "----file DISPLAY_NAME is: " + picture.DISPLAY_NAME);
				Log.w(TAG, "----file MIME_TYPE is: " + picture.MIME_TYPE);
				Log.w(TAG, "----file SIZE is: " + picture.SIZE);
				// Log.w(TAG, "----file WIDTH is: " + picture.WIDTH);
				// Log.w(TAG, "----file HEIGHT is: " + picture.HEIGHT);
//				Log.w(TAG, "----file ORIENTATION is: " + picture.ORIENTATION);
*/
//				Log.w(TAG, "----file LATITUDE is: " + picture.LATITUDE);
//				Log.w(TAG, "----file LONGITUDE is: " + picture.LONGITUDE);
				
				try
				{
					if (Long.valueOf(picture.SIZE) > 4096l/* && Integer.valueOf(picture.WIDTH) > 16
							&& Integer.valueOf(picture.HEIGHT) > 16*/
							&& (MIME_TYPE_JPEG.equals(picture.MIME_TYPE)))
						mListMediaProperty.add(picture);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				cursor.moveToNext();
			}
			cursor.close();
			cursor = null;
			
			// 排序
		}
		Collections.sort(mListMediaProperty, new TimeComparator());
	}
	
	// holder
	private class ViewHolder
	{
		ImageView image;
		FrameLayout cover;
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
//			Log.v(TAG, "picture size = " + mListMediaProperty.size());
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
				convertView = inflater.inflate(R.layout.list_item_picture, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.iv_list_picture);
				holder.image.setMaxWidth(shortLen/4);
				holder.image.setMaxHeight(shortLen/4);
				holder.cover = (FrameLayout) convertView.findViewById(R.id.fl_list_picture_select);
				holder.selected = (TextView) convertView.findViewById(R.id.iv_list_picture_selected);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			PictureProperty picture = mListMediaProperty.get(position);
//			Bitmap bitmap = BitmapFactory.decodeFile(picture.DATA);
//			holder.image.setImageBitmap(bitmap);
			
			ImageLoader loader = ImageLoader.getInstance();
			
//			loader.init(ImageLoaderConfiguration.createDefault(getActivity()));
			
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.icon_default_album)
					.showImageForEmptyUri(R.drawable.icon_default_album)
					.showImageOnFail(R.drawable.icon_default_album)
					.cacheInMemory(false)
					.cacheOnDisc(false)
					.displayer(new SimpleBitmapDisplayer())
					.cacheInMemory(false)
					.imageScaleType(ImageScaleType.EXACTLY)
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
			
			loader.displayImage("file://" + (picture.ClipData == null ? picture.DATA : picture.ClipData),
					holder.image, options, mUserID, 0);
			
			if(picture.selected)
			{
				holder.cover.setVisibility(View.VISIBLE);
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
				holder.cover.setVisibility(View.INVISIBLE);
			}
				
			return convertView;
		}
		
	}
	
	// 创建日记
	public void createDiary()
	{
		addInfo.addListener(FragmentPictureList.this);
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
					PictureProperty pictureProperty = mListMediaProperty.get(i);
					String diaryUUID = DiaryController.getNextUUID();
					String attachUUID = DiaryController.getNextUUID();
					diaryUUIDList.add(diaryUUID);
					
					float[] latlng = new float[2];
					XUtils.getExifLatLong(pictureProperty.DATA, latlng);
					
					if ((int)latlng[0] > 0 && (int)latlng[1] > 0) {
						ConverterTool ct = new ConverterTool();
						Point p = ct.GPS2GG(latlng[1], latlng[0]);
						latlng[1] = (float) p.getX();
						latlng[0] = (float) p.getY();
					} 
					
					
					synchronized (mListMediaProperty)
					{
						String beforeStr = CommonInfo.getInstance().positionStr;
						
						double beforeLongtitude = 0;
						double beforeLatitude = 0;
						
						if(CommonInfo.getInstance().myLoc != null)
						{
							beforeLongtitude = CommonInfo.getInstance().myLoc.longitude;
							beforeLatitude = CommonInfo.getInstance().myLoc.latitude;
						}
						strAddr = "";
						
						addInfo.reverseGeocode((new GeoPoint((int)(latlng[0] * 1e6), (int)(latlng[1] * 1e6))));
						
						try
						{
							mListMediaProperty.wait(4000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						Log.v(TAG, "strAddr = " + strAddr + " positionStr = " + CommonInfo.getInstance().positionStr + " beforeStr = " + beforeStr);
						if (!strAddr.equals(beforeStr)) {
							CommonInfo.getInstance().positionStr = beforeStr;
							if (CommonInfo.getInstance().myLoc != null) {
								CommonInfo.getInstance().myLoc.longitude = beforeLongtitude;
								CommonInfo.getInstance().myLoc.latitude = beforeLatitude;
							}
						}
					}
				    
					Log.v(TAG, "create picture diary");

					DiaryController.getInstanse().includeDiary(handler,
							diaryUUID,
							attachUUID,
							(pictureProperty.ClipData == null ? pictureProperty.DATA : pictureProperty.ClipData),
							GsonProtocol.ATTACH_TYPE_PICTURE,
							GsonProtocol.SUFFIX_JPG,
							"",
							"",
							GsonProtocol.EMPTY_VALUE,
//							CommonInfo.getInstance().getLongitude(),
//							CommonInfo.getInstance().getLatitude(),
//							DiaryController.getPositionString1(),"",
							"" + (latlng[1]),
							"" + (latlng[0]),
							strAddr,
							"",
							mode == MediaScanActivity.MODE_PIC_NORMAL ? false : true,
							FileOperate.RENAME,
							pictureProperty.DATE_TAKEN);
					
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
	
	private void endCreate()
	{
		if (mode == MediaScanActivity.MODE_PIC_NORMAL)
		{
			// 按照生成日记的顺序排序
			ArrayList<MyDiaryList> diaryList = new ArrayList<MyDiaryList>();
			
			String UUID = diaryUUIDList.get(diaryUUIDList.size() -1);
			for (int i = diaryUUIDList.size() - 1; i >= 0; i--) {
				String diaryUUID = diaryUUIDList.get(i);
				MyDiaryList myDiaryList = DiaryManager.getInstance().findDiaryGroupByUUID(diaryUUID);
				if(myDiaryList != null)
				{
					diaryList.add(myDiaryList);
				}
			}
			
//			//  日记已经全部生成完成，后面做界面跳转相关操作。
//			DiaryManager.getInstance().setDetailDiaryList(diaryList, 0);
//			Intent intent = new Intent(getActivity(), DiaryPreviewActivity.class);
//			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, UUID);
//			startActivity(intent);
//			getActivity().finish();
//			ZDialog.dismiss();
			
			//  日记已经全部生成完成，后面做界面跳转相关操作。
			DiaryManager.getInstance().setDetailDiaryList(diaryList, 0);
			Intent intent = new Intent(getActivity(), DiaryPositionEditActivity.class);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, UUID);
			startActivity(intent);
			getActivity().finish();
			ZDialog.dismiss();
		}
		else
		{
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
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
		case HANDLER_MESSAGE_LOAD_COMPLETE:
			mAdapter = new MediaAdapter(getActivity());
			mGridView.setAdapter(mAdapter);
//			mAdapter.notifyDataSetChanged();
			if (tmpPath != null) {
				selectPicture(tmpPath);
			}
			ZDialog.dismiss();
			break;
		case DiaryController.DIARY_REQUEST_DONE:
			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in diaryNum = " + diaryNum);
			
			if (msg.obj == null)
			{// false
				//Prompt.Alert(getResources().getString(R.string.load_error));
			}
			else
			{
				DiaryWrapper wrapper = (DiaryWrapper) msg.obj;
				MyDiary diary = null;
				if (wrapper != null) {
				    diary = wrapper.diary;
				}
				mDiaryWrapper.add(wrapper);
				//System.out.println("==0==ClipData == " + mListMediaProperty.get(mSelectArray.size() - diaryNum).ClipData + "/n" + " === diary.getMainPath() == " + diary.getMainPath());
				//mListMediaProperty.get(mSelectArray.size() - diaryNum).ClipData = diary.getMainPath();
				//System.out.println("==1==ClipData == " + mListMediaProperty.get(mSelectArray.size() - diaryNum).ClipData + "/n" + " === diary.getMainPath() == " + diary.getMainPath());
			}
			diaryNum --;
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
	public class TimeComparator implements Comparator<PictureProperty> {
        public int compare(PictureProperty arg0, PictureProperty arg1) {
        	
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
	public void onPoiSearch(List<POIAddressInfo> poiList)
	{
		
	}

	String strAddr = null;
	@Override
	public void onAddrSearch(MKAddrInfo res)
	{
		synchronized (mListMediaProperty)
		{
			if (res != null) {
				strAddr = res.strAddr;
				mListMediaProperty.notify();
			}
		}
	}
	
	public void back()
	{
		for (PictureProperty property : mListMediaProperty)
		{
			if(!TextUtils.isEmpty(property.ClipData))
			{
				File f = new File(property.ClipData);
				if (f.exists() && f.isFile())
				{ 
					Log.e(TAG, "del file: " + f.getAbsolutePath());
					f.delete();
				}
			}
		}
	}

}
