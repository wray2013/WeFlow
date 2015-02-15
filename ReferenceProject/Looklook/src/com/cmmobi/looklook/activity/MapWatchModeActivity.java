package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity.AudioPlayer;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.FootView;
import com.cmmobi.looklook.common.web.MapTackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import effect.XEffectMediaPlayer;
import effect.XEffects;

/**
 * @author Daniel Gan
 * @date 2013-7-4
 * @Description: TODO
 */
public class MapWatchModeActivity extends Activity implements Callback,
		OnClickListener {

	private MapView mMapView = null;
	private MapController mMapController = null;
	MKMapViewListener mMapListener = null;
	private Handler handler;
	// private ArrayList<GeoPoint> list;
	private ImageButton backButton;
	private double xDiv;
	private boolean isCurDiaryScreenXBigger;
	private RelativeLayout relativeLayout;

	public static final String INTENT_ACTION_DIARY_ID = "intent_action_diary_id";
	public static final String INTENT_ACTION_DIARY_ARRAY = "intent_action_diary_array";
	private static final int INIT_UI = 10;
	private static final int PROCESS_PLAY = 0x0011;
	private static final int START_MOVE_MAP = 12;
	private static final int MOVE_MAP = 13;
	private static final int MOVE_COMPLETE = 14;
	private static final int RESET_PLAY = 20;
	private static final int FULL_UI_VIDEO = 21;
	private static final int FULL_UI_PIC = 22;
	public static final int MAP_UPDATE_TIME_REPLACE = 23;

	private int CURRENT_WHAT;

	public ArrayList<OverlayItem> mItems = new ArrayList<OverlayItem>();
	private ImageView cidaiDown;

	// private int footPosion = 0;
	// private int currentPlayPosion;

	private FootView footview;
	private String currentDiaryId;
	private String diaryString;
	private ArrayList<MyDiary> diaryList;
	private MyDiary currentDiary;
	private GeoPoint currentGeoPoint;
	private GeoPoint nextGeoPoint;
	private MyDiary nextDiary;
	private ImageButton playButton;
	private ImageButton preButton;
	private ImageButton nextButton;
	private ArrayList<GeoPoint> geoPointList;
	private ImageView cidaiLeft;
	private TextView singleText;
	private MapTackView soundWithText;
	// private RelativeLayout viedoRelativeLayout;

	private XMediaPlayer mMediaPlayer;
	XEffects mEffects = new XEffects();
	private RelativeLayout viedoRelativeLayout;
	private String shortUrl;
	private ImageView fullImageView;
	private RelativeLayout fullRelativeLayout;
	private RelativeLayout noFullRelativeLayout;
	private ImageView touxiangImageView;
	private ImageView videoTouXiangImageView;
	private ImageView videoTouXiangImageViewBg;
	private ArrayList<diaryAttach> attachList = new ArrayList<GsonResponse2.diaryAttach>();
	private TextView fullLeftSingleTextView;
	private ImageView fullLeftTouxiangImageView;
	private MapTackView fullVideoTack;

	private TextView fullLeftDownText;

	private MapTackView fullLeftDownTack;
	private ArrayList<GeoPoint> footList;
	private MoveFootThread moveFootThread;
	private int footStep = 0;

	private String firstDiaryId;
	private String lastDiaryId;
	private boolean currentChange = false;
	private RelativeLayout videoThumbnailRelativeLayout;

	private TextView rightTopLocalTextView;
	private ImageView ivHour;
	private ImageView ivSecond;
	private TextView tvHourAndMinutes;
	private TextView tvDay;
	
	private Bitmap bmHour;
	private Bitmap bmSecond;
	
	private int hWidth;
	private int hHeight;
	private int sWidth;
	private int sHeight;
	private ImageView ivHoursReplace;
	private ImageView ivMinutesReplace;
	private TextView tvHoursReplace;
	private TextView tvMinutesReplace;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	private DisplayImageOptions circular_options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	// private ArrayList<GeoPoint> geoPointList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainApplication app = (MainApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(MainApplication.strKey,
					new MainApplication.MyGeneralListener());
		}

		setContentView(R.layout.activity_map_watch_mode);

		mMapView = (MapView) findViewById(R.id.bmapView);

		backButton = (ImageButton) findViewById(R.id.back_button);
		playButton = (ImageButton) findViewById(R.id.play_image_button);
		preButton = (ImageButton) findViewById(R.id.pre_button);
		nextButton = (ImageButton) findViewById(R.id.next_button);

		footview = (FootView) findViewById(R.id.footview);

		relativeLayout = (RelativeLayout) findViewById(R.id.rl);

		noFullRelativeLayout = (RelativeLayout) findViewById(R.id.no_full_rl);
		cidaiDown = (ImageView) findViewById(R.id.cidai_down);
		cidaiLeft = (ImageView) findViewById(R.id.cidai_left);
		singleText = (TextView) findViewById(R.id.single_text);
		soundWithText = (MapTackView) findViewById(R.id.map_sound_with_text);
		touxiangImageView = (ImageView) findViewById(R.id.touxiang);
		videoThumbnailRelativeLayout = (RelativeLayout) findViewById(R.id.viedo_thumbnail);
		videoTouXiangImageView = (ImageView) findViewById(R.id.video_touxiang);
		videoTouXiangImageViewBg = (ImageView) findViewById(R.id.video_touxiang_bg);

		fullLeftSingleTextView = (TextView) findViewById(R.id.full_left_single_text);
		fullLeftTouxiangImageView = (ImageView) findViewById(R.id.full_left_touxiang);

		fullRelativeLayout = (RelativeLayout) findViewById(R.id.full_rl);
		fullImageView = (ImageView) findViewById(R.id.full_image);
		viedoRelativeLayout = (RelativeLayout) findViewById(R.id.full_video_rl);

		fullVideoTack = (MapTackView) findViewById(R.id.full_viedo_tack);

		fullLeftDownText = (TextView) findViewById(R.id.full_left_text);
		fullLeftDownTack = (MapTackView) findViewById(R.id.full_left_down_tack);

		rightTopLocalTextView = (TextView) findViewById(R.id.right_top_local_textview);

		ivHour = (ImageView) findViewById(R.id.iv_clock_h);
		ivSecond = (ImageView) findViewById(R.id.iv_clock_s);
		tvHourAndMinutes = (TextView) findViewById(R.id.tv_hour_minutes);
		tvDay = (TextView) findViewById(R.id.tv_day);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		//.displayer(new CircularBitmapDisplayer()) 圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
		
		circular_options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		//.displayer(new SimpleBitmapDisplayer())
		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();

		
		ivHoursReplace= (ImageView) findViewById(R.id.iv_clock_h);
    	ivMinutesReplace=(ImageView) findViewById(R.id.iv_clock_s);
    	tvHoursReplace= (TextView) findViewById(R.id.tv_day);
    	tvMinutesReplace= (TextView) findViewById(R.id.tv_hour_minutes);

		
		bmHour = BitmapFactory.decodeResource(getResources(),
				R.drawable.shijian_clock_h);
		bmSecond = BitmapFactory.decodeResource(getResources(),
				R.drawable.shijian_clock_s);
		hWidth = bmHour.getWidth();
		hHeight = bmHour.getHeight();
		sWidth = bmSecond.getWidth();
		sHeight = bmSecond.getHeight();
		
		if (null == mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, false);
			mMediaPlayer.setListener(new MyOnInfoListener());
			viedoRelativeLayout.addView((View) mMediaPlayer.getXSurfaceView());
		}

		mMapController = mMapView.getController();
		mMapController.enableClick(false);
		mMapController.setOverlookingGesturesEnabled(false);
		mMapController.setZoomGesturesEnabled(false);

		noFullRelativeLayout.setVisibility(View.VISIBLE);

		// mMapController.setZoom(17);

		// 116.404844,39.916263
		// 114.298353,30.564765
		// 121.476748,31.233585

		/**
		 * I only want to say,it's change. Bundle bundle =
		 * getIntent().getExtras(); currentDiaryId = (String)
		 * bundle.get(INTENT_ACTION_DIARY_ID); diaryString = (String)
		 * bundle.get(INTENT_ACTION_DIARY_ARRAY);
		 * 
		 * diaryList = new Gson().fromJson(diaryString, new
		 * TypeToken<ArrayList<MyDiary>>() { }.getType());
		 **/
		currentDiaryId = (String) getIntent().getStringExtra(
				INTENT_ACTION_DIARY_ID);
		diaryList = DiaryManager.getInstance().getDetailDiaryList();

		firstDiaryId = diaryList.get(0).diaryid;
		lastDiaryId = diaryList.get(diaryList.size() - 1).diaryid;

		// disablePreOrNextButton(currentDiaryId);
		Log.d("MapWatchModeActivity", "diaryList=" + diaryList);
		// double cLat1 = 39.916263;
		// double cLon1 = 116.404844;
		//
		// double cLat2 = 30.564765;
		// double cLon2 = 114.298353;
		//
		// double cLat3 = 31.233585;
		// double cLon3 = 121.476748;

		for (int i = 0; i < diaryList.size(); i++) {
			if (i == 0) {
				diaryList.get(i).latitude = "39.916263";
				diaryList.get(i).longitude = "116.404844";
			} else if (i == 1) {
				diaryList.get(i).latitude = "30.564765";
				diaryList.get(i).longitude = "114.298353";
			} else if (i == 2) {
				diaryList.get(i).latitude = "31.233585";
				diaryList.get(i).longitude = "121.476748";
			}
		}

		mMapController.setZoom(17);

		currentDiary = getDiaryById(currentDiaryId);

		touxiangImageView.setVisibility(View.VISIBLE);

		/*touxiangImageView.setImageUrl(currentDiary.headimageurl, 1, true);*/
		if(currentDiary!=null && currentDiary.headimageurl!=null){	
			imageLoader.displayImage(currentDiary.headimageurl, touxiangImageView, circular_options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
		}
		

		String time = currentDiary.updatetimemilli;
		
		setTime(time);
		currentGeoPoint = new GeoPoint(
				(int) (Double.parseDouble("".equals(currentDiary.latitude) ? "0"
						: currentDiary.latitude) * 1e6),
				(int) (Double.parseDouble("".equals(currentDiary.longitude) ? "0"
						: currentDiary.longitude) * 1e6));
		// nextGeoPoint = new GeoPoint((int) (Double.parseDouble(""
		// .equals(nextDiary.latitude) ? "0" : nextDiary.latitude) * 1e6),
		// (int) (Double.parseDouble("".equals(nextDiary.longitude) ? "0"
		// : nextDiary.longitude) * 1e6));

		mMapController.setCenter(currentGeoPoint);

		rightTopLocalTextView
				.setText((null == currentDiary.position || !(currentDiary.position
						.length() > 0)) ? "暂无位置信息" : currentDiary.position);

		// geoPointList = new ArrayList<GeoPoint>();
		// geoPointList.add(currentGeoPoint);
		// geoPointList.add(nextGeoPoint);

		handler = new Handler(MapWatchModeActivity.this);
		playButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		preButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		
		handler.sendEmptyMessageDelayed(INIT_UI, 5000);
		CURRENT_WHAT = INIT_UI;

	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {

		case INIT_UI:

			// todo 获取当前diary， 初始化布局，初始化附件list， process_play

			currentDiary = getDiaryById(currentDiaryId);

			attachList.clear();
			for (int i = 0; i < currentDiary.attachs.length; i++) {
				attachList.add(currentDiary.attachs[i]);
			}

			initUi(currentDiary);

			break;

		case RESET_PLAY:

			if (AudioPlayer.status != 3) {
				AudioPlayer.stop();
			}

			currentDiary = getDiaryById(currentDiaryId);

			currentGeoPoint = new GeoPoint(
					(int) (Double.parseDouble("".equals(currentDiary.latitude) ? "0"
							: currentDiary.latitude) * 1e6),
					(int) (Double.parseDouble("".equals(currentDiary.longitude) ? "0"
							: currentDiary.longitude) * 1e6));

			mMapController.animateTo(currentGeoPoint);

			attachList.clear();
			for (int i = 0; i < currentDiary.attachs.length; i++) {
				attachList.add(currentDiary.attachs[i]);
			}

			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.VISIBLE);

			initUi(currentDiary);

			footStep = 0;

//			handler.sendEmptyMessage(PROCESS_PLAY);
//			CURRENT_WHAT = INIT_UI;

			break;

		// case FULL_UI_VIDEO:
		// fullRelativeLayout.setVisibility(View.VISIBLE);
		// noFullRelativeLayout.setVisibility(View.GONE);
		// viedoRelativeLayout.setVisibility(View.VISIBLE);
		// mMapView.setVisibility(View.GONE);
		//
		// handler.sendEmptyMessage(PROCESS_PLAY);
		// CURRENT_WHAT = INIT_UI;
		// break;
		// case FULL_UI_PIC:
		// mMapView.setVisibility(View.INVISIBLE);
		// fullRelativeLayout.setVisibility(View.VISIBLE);
		// noFullRelativeLayout.setVisibility(View.GONE);
		// viedoRelativeLayout.setVisibility(View.GONE);
		//
		// videoTouXiangImageView.setVisibility(View.VISIBLE);
		// videoTouXiangImageViewBg.setVisibility(View.VISIBLE);
		//
		// fullImageView.setVisibility(View.VISIBLE);
		//
		// handler.sendEmptyMessage(PROCESS_PLAY);
		// CURRENT_WHAT = INIT_UI;
		// break;

		case 0x10000000:// 主体 视频

			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			break;
		case 0x10000100: {// 主体 视频+辅 音频
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			// todo 不要文字处理
			fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			fullVideoTack.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			break;
		}
		case 0x10000101:// 主体 视频+辅 音频+文字
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			fullVideoTack.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			break;
		case 0x10000001:// 主体 视频+文字
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			fullLeftSingleTextView.setVisibility(View.VISIBLE);
			fullLeftTouxiangImageView.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			break;

		case 0x100000:// 主体 图片

			mMapView.setVisibility(View.GONE);
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			fullImageView.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			// String url = getImageUrl();
			// if (url != null && url.length() > 0) {
			// fullImageView.setImageUrl(url, 1, false);
			// }

			break;
		case 0x100001:// 主体 图片 +文字
			mMapView.setVisibility(View.INVISIBLE);
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			fullImageView.setVisibility(View.VISIBLE);
			fullLeftDownText.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			break;
		case 0x100100: {// 主体 图片+辅 音频
			mMapView.setVisibility(View.INVISIBLE);
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			fullImageView.setVisibility(View.VISIBLE);
			fullLeftDownTack.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			break;
		}
		case 0x100101:// 主体 图片+辅 音频+文字
			mMapView.setVisibility(View.INVISIBLE);
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			fullImageView.setVisibility(View.VISIBLE);
			// 无文字
			fullLeftDownTack.setVisibility(View.VISIBLE);
			fullLeftSingleTextView.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
			break;

		case PROCESS_PLAY:

			// todo 附件list 循环。

			if (attachList.size() > 0) {

				for (int i = 0; i < attachList.size(); i++) {

					diaryAttach attach = attachList.get(i);
					if ("4".equals(attach.attachtype)) {

						if (attach.content != null
								&& attach.content.length() > 0) {
							// 显示
							// textView.setText = "";
						} else {
							// 隐藏
						}

						attachList.remove(i);

						handler.sendEmptyMessage(PROCESS_PLAY);
						CURRENT_WHAT = PROCESS_PLAY;
						break;
					} else if ("1".equals(attach.attachtype)) {

						if (attach.attachvideo != null
								&& attach.attachvideo.length > 0) {

							play(attach.attachvideo[0].playvideourl,
									attach.attachtype);
						} else {

						}

						attachList.remove(i);
						break;
					} else if ("2".equals(attach.attachtype)) {

						if (attach.attachaudio != null
								&& attach.attachaudio.length > 0) {

							play(attach.attachaudio[0].audiourl,
									attach.attachtype);
						} else {

							handler.sendEmptyMessageDelayed(PROCESS_PLAY, 2);
							CURRENT_WHAT = PROCESS_PLAY;
						}

						attachList.remove(i);
						break;
					} else if ("3".equals(attach.attachtype)) {

						if (attach.attachimage != null
								&& attach.attachimage.length > 0) {

							play(attach.attachimage[0].imageurl,
									attach.attachtype);
						} else {
							handler.sendEmptyMessage(PROCESS_PLAY);
							CURRENT_WHAT = PROCESS_PLAY;
						}

						attachList.remove(i);
						break;
					}
				}
			} else {
				handler.sendEmptyMessage(START_MOVE_MAP);
				CURRENT_WHAT = START_MOVE_MAP;
			}

			break;

		case START_MOVE_MAP:
			// 计算步子

			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.VISIBLE);
			cidaiDown.setVisibility(View.GONE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.GONE);
			fullVideoTack.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);

			nextDiary = getNextDiaryById(currentDiaryId);

			currentGeoPoint = new GeoPoint(
					(int) (Double.parseDouble("".equals(currentDiary.latitude) ? "0"
							: currentDiary.latitude) * 1e6),
					(int) (Double.parseDouble("".equals(currentDiary.longitude) ? "0"
							: currentDiary.longitude) * 1e6));

			if (nextDiary != null) {

				nextGeoPoint = new GeoPoint(
						(int) (Double.parseDouble("".equals(nextDiary.latitude) ? "0"
								: nextDiary.latitude) * 1e6),
						(int) (Double.parseDouble(""
								.equals(nextDiary.longitude) ? "0"
								: nextDiary.longitude) * 1e6));
			} else {
				Toast.makeText(this, "播放完成！", Toast.LENGTH_SHORT).show();
				break;
			}

			if (nextGeoPoint != null) {

				ArrayList<GeoPoint> geoPointList = new ArrayList<GeoPoint>();
				geoPointList.add(currentGeoPoint);
				geoPointList.add(nextGeoPoint);

				fitZoomFromPoints(mMapController, geoPointList);

				if (footList != null) {
					footList.clear();
				}
				footList = footMap(currentGeoPoint, nextGeoPoint);

				moveFoot(footList);
			}
			break;

		case MOVE_MAP:
			//

			if (footStep < footList.size()) {
				mMapController.animateTo(footList.get(footStep));

				footStep++;
				handler.sendEmptyMessageDelayed(MOVE_MAP, 500);
				CURRENT_WHAT = MOVE_MAP;
			} else {
				footStep = 0;
				handler.sendEmptyMessageDelayed(
						MapWatchModeActivity.MOVE_COMPLETE, 1000);
				CURRENT_WHAT = MOVE_COMPLETE;  
			}
			// mMapController.animateTo((GeoPoint) msg.obj);
			break;

		case MOVE_COMPLETE:

			currentDiaryId = nextDiary.diaryid;
			currentChange = true;

			handler.sendEmptyMessage(INIT_UI);
			CURRENT_WHAT = INIT_UI;

			break;
		default:
			break;
		}
		return false;
	}

	private void play(String url, String type) {

		String longUrl;
		switch (Integer.parseInt(type)) {
		case 1:

			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW
					|| mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_STOP) {
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/test.mp4";
				mMediaPlayer.open(path);
			}

			break;
		case 2:

			longUrl = getLongRecUrl();
			shortUrl = getShortRecUrl();
			if (longUrl != null && longUrl.length() > 0) {
				AudioPlayer.playAudio(getAudioPath(longUrl), handler);
			} else if (shortUrl != null && shortUrl.length() > 0) {
				AudioPlayer.playAudio(getAudioPath(shortUrl), handler);

			} else {
				handler.sendEmptyMessage(PROCESS_PLAY);
				CURRENT_WHAT = PROCESS_PLAY;
			}

			break;
		case 3:

			/*fullImageView.setImageUrl(url, 0, false);*/
			if(url!=null){	
				imageLoader.displayImage(url, fullImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}

			handler.sendEmptyMessageDelayed(PROCESS_PLAY, 2000);
			CURRENT_WHAT = PROCESS_PLAY;
			break;

		default:
			break;
		}
	}

	class MoveFootThread extends Thread {

		public void run() {
			for (int i = 0; i < footList.size(); i++) {
				GeoPoint point = footList.get(i);
				Message meg = new Message();
				meg.what = MapWatchModeActivity.MOVE_MAP;
				meg.obj = point;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.sendMessage(meg);
			}

			handler.sendEmptyMessageDelayed(MapWatchModeActivity.MOVE_COMPLETE,
					1000);
			CURRENT_WHAT = MOVE_COMPLETE;
		};
	};

	private void moveFoot(final ArrayList<GeoPoint> footList) {

		currentChange = false;
		handler.sendEmptyMessage(MOVE_MAP);
		CURRENT_WHAT = MOVE_MAP;
		// mMapController.animateTo(point);
		//
		//
		// footPosion++;
		//
		// if (footPosion < footList.size()) {
		// } else {
		// footPosion = 0;
		//
		// if (currentPlayPosion < list.size()) {
		// playSth(currentPlayPosion);
		// }
		// }

		// ArrayList<Point> xyPointList = new ArrayList<Point>();
		// for (int i = 0; i < posion; i++) {
		//
		// Point xyPoint = mMapView.getProjection().toPixels(footList.get(i),
		// null);
		// xyPointList.add(xyPoint);
		// }
		// footview.addPointList(xyPointList);
		// footview.invalidate();

		// ImageView imageView = new ImageView(this);
		// LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// imageView.setLayoutParams(params);
		// imageView.setBackgroundResource(R.drawable.temp_jiaoyin);
		//
		// relativeLayout.addView(imageView);
		// imageView.layout(100, 100, 150, 150);
	}

	private ArrayList<GeoPoint> footMap(GeoPoint curGeoPoint,
			GeoPoint nexGeoPoint) {
		int FOOT_COUTT = 10;

		GeoPoint point1 = curGeoPoint;
		GeoPoint point2 = nexGeoPoint;

		double rake = (double) (point2.getLatitudeE6() - point1.getLatitudeE6())
				/ (point2.getLongitudeE6() - point1.getLongitudeE6());


		xDiv = Math.abs(point1.getLongitudeE6() - point2.getLongitudeE6())
				/ FOOT_COUTT;

		if (point2.getLongitudeE6() > point1.getLongitudeE6()) {
			isCurDiaryScreenXBigger = true;
		} else {
			isCurDiaryScreenXBigger = false;
		}

		ArrayList<GeoPoint> footList = new ArrayList<GeoPoint>();
		for (int i = 0; i < FOOT_COUTT + 1; i++) {

			double tempX = 0;
			if (!isCurDiaryScreenXBigger) {
				tempX = point1.getLongitudeE6() - xDiv * i;
			} else {
				tempX = point1.getLongitudeE6() + xDiv * i;
			}

			double tempY = getPointYByX(rake, tempX, point1.getLongitudeE6(),
					point1.getLatitudeE6());

			GeoPoint point = new GeoPoint((int) tempY, (int) tempX);

			footList.add(point);

		}

		return footList;
	}

	private void initUi(MyDiary currentDiary) {

		diaryAttach[] attachs = currentDiary.attachs;

		int type = DiaryListView.getDiaryType(attachs);// 获取日记类型

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/test.mp4";

		rightTopLocalTextView
				.setText((null == currentDiary.position || !(currentDiary.position
						.length() > 0)) ? "暂无位置信息" : currentDiary.position);

		
		String time = currentDiary.updatetimemilli;
		
		setTime(time);
//		handler.post(new updateTimeThread(time));

		String longUrl = "";
		String imageUrl = "";
		String coverImageUrl = "";
		// type = 0x10000000;
		switch (type) {
		case 0x10000000:// 主体 视频

			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.GONE);
			videoTouXiangImageView.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			coverImageUrl = getCoverImageUrl();
			/*videoTouXiangImageView.setImageUrl(coverImageUrl, 1, false);*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(coverImageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}

			handler.sendEmptyMessageDelayed(0x10000000, 5000);
			CURRENT_WHAT = 0x10000000;
			break;
		case 0x10000100: {// 主体 视频+辅 音频
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			coverImageUrl = getCoverImageUrl();
			/*videoTouXiangImageView.setImageUrl(coverImageUrl, 1, false);*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(coverImageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}

			// // todo 不要文字处理
			// fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			// fullVideoTack.setVisibility(View.VISIBLE);

			handler.sendEmptyMessageDelayed(0x10000100, 5000);
			CURRENT_WHAT = 0x10000100;
			break;
		}
		case 0x10000101:// 主体 视频+辅 音频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			coverImageUrl = getCoverImageUrl();
			/*videoTouXiangImageView.setImageUrl(coverImageUrl, 1, false);*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(coverImageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}

			// fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			// fullVideoTack.setVisibility(View.VISIBLE);

			handler.sendEmptyMessageDelayed(0x10000101, 5000);
			CURRENT_WHAT = 0x10000101;
			break;
		case 0x10000001:// 主体 视频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			coverImageUrl = getCoverImageUrl();
			/*videoTouXiangImageView.setImageUrl(coverImageUrl, 1, false);*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(coverImageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}
			// fullLeftSingleTextView.setVisibility(View.VISIBLE);
			// fullLeftTouxiangImageView.setVisibility(View.VISIBLE);

			handler.sendEmptyMessageDelayed(0x10000001, 5000);
			CURRENT_WHAT = 0x10000001;
			break;

		case 0x1000000:// 主体 音频
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.VISIBLE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;

			// viedoRelativeLayout.setVisibility(View.GONE);
			// fullImageView.setVisibility(View.VISIBLE);

			break;
		case 0x1000100: {// 主体 音频+辅 音频
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.VISIBLE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.GONE);

			fullVideoTack.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;
		}
			break;
		case 0x1000101:// 主体 音频+辅 音频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.GONE);
			cidaiLeft.setVisibility(View.VISIBLE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;
			break;
		case 0x1000001:// 主体音频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.VISIBLE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.VISIBLE);
			soundWithText.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;
			break;

		case 0x100000:// 主体 图片

			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			imageUrl = getImageUrl();
/*			if (imageUrl != null && imageUrl.length() > 0) {
				videoTouXiangImageView.setImageUrl(imageUrl, 1, false);
			}*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(imageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}
			// fullImageView.setVisibility(View.GONE);

			handler.sendEmptyMessageDelayed(0x100000, 5000);
			CURRENT_WHAT = 0x100000;
			// String url = getImageUrl();
			// if (url != null && url.length() > 0) {
			// fullImageView.setImageUrl(url, 1, false);
			// }

			break;
		case 0x100001:// 主体 图片 +文字
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			imageUrl = getImageUrl();
/*			if (imageUrl != null && imageUrl.length() > 0) {
				videoTouXiangImageView.setImageUrl(imageUrl, 1, false);
			}*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(imageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}
			// fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			// fullImageView.setVisibility(View.VISIBLE);
			// fullLeftDownText.setVisibility(View.VISIBLE);

			handler.sendEmptyMessageDelayed(0x100001, 5000);
			CURRENT_WHAT = 0x100001;
			break;
		case 0x100100: {// 主体 图片+辅 音频
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			imageUrl = getImageUrl();
/*			if (imageUrl != null && imageUrl.length() > 0) {
				videoTouXiangImageView.setImageUrl(imageUrl, 1, false);
			}*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(imageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}
			// fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			// fullImageView.setVisibility(View.VISIBLE);
			// fullLeftDownTack.setVisibility(View.VISIBLE);

			handler.sendEmptyMessageDelayed(0x100100, 5000);
			CURRENT_WHAT = 0x100100;
			break;
		}
		case 0x100101:// 主体 图片+辅 音频+文字
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.GONE);
			videoThumbnailRelativeLayout.setVisibility(View.VISIBLE);
			videoTouXiangImageViewBg.setVisibility(View.VISIBLE);

			imageUrl = getImageUrl();
/*			if (imageUrl != null && imageUrl.length() > 0) {
				videoTouXiangImageView.setImageUrl(imageUrl, 1, false);
			}*/
			if(coverImageUrl!=null){	
				imageLoader.displayImage(imageUrl, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}
			// fullLeftTouxiangImageView.setVisibility(View.VISIBLE);
			// fullImageView.setVisibility(View.VISIBLE);
			// // 无文字
			// fullLeftDownTack.setVisibility(View.VISIBLE);
			// fullLeftSingleTextView.setVisibility(View.VISIBLE);

			handler.sendEmptyMessageDelayed(0x100101, 5000);
			CURRENT_WHAT = 0x100101;
			break;

		case 0x10000:// 主体 文字
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;

			break;
		case 0x10001:// 主体 文字+文字
			mMapView.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;

			break;
		case 0x10100: {// 主体 文字+辅 音频
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;

			break;
		}
		case 0x10101:// 主体 文字+辅 音频+文字
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;

			break;
		case 0x100: {// 辅 音频
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.GONE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;

			break;
		}
		case 0x101: {// 辅 音频+文字
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.GONE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.VISIBLE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;
			break;
		}
		case 0x1:// 辅 文字
			mMapView.setVisibility(View.VISIBLE);
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			touxiangImageView.setVisibility(View.VISIBLE);
			videoThumbnailRelativeLayout.setVisibility(View.GONE);
			videoTouXiangImageViewBg.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.GONE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.VISIBLE);
			soundWithText.setVisibility(View.GONE);

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = INIT_UI;
			break;
		default:
			break;
		}

	}

	private void playSth(MyDiary currentDiary) {

		diaryAttach[] attachs = currentDiary.attachs;

		// XMediaPlayer mediaPlayer = new XMediaPlayer(this);
		// mediaPlayer.setMediaUri(Environment.getExternalStorageDirectory()
		// .getPath() + "/test.mp4");
		//
		// MediaPlayerUI mediaPlayerUI = new MediaPlayerUI(this, mediaPlayer);
		// mediaPlayer.setOnXMediaPlayerStateChangedListener(mediaPlayerUI);
		//
		// RelativeLayout medialLayout = mediaPlayerUI.getLayout();
		//
		// relativeLayout.addView(medialLayout);

		int type = DiaryListView.getDiaryType(attachs);// 获取日记类型

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/test.mp4";

		String longUrl = "";
		// type = 0x10000000;
		switch (type) {
		case 0x10000000:// 主体 视频
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);

			mMapView.setVisibility(View.INVISIBLE);
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW
					|| mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_STOP) {

				mMediaPlayer.open(path);
			}
			break;
		case 0x10000100: {// 主体 视频+辅 音频
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.INVISIBLE);
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW
					|| mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_STOP) {

				mMediaPlayer.open(path);
			}
			break;
		}
		case 0x10000101:// 主体 视频+辅 音频+文字
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.INVISIBLE);
			
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW
					|| mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_STOP) {

				mMediaPlayer.open(path);
			}

			break;
		case 0x10000001:// 主体 视频+文字
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			mMapView.setVisibility(View.INVISIBLE);
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW
					|| mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_STOP) {

				mMediaPlayer.open(path);
			}
			break;

		case 0x1000000:// 主体 音频
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.VISIBLE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.GONE);

			// viedoRelativeLayout.setVisibility(View.GONE);
			// fullImageView.setVisibility(View.VISIBLE);

			longUrl = getLongRecUrl();
			if (longUrl != null && longUrl.length() > 0) {
				AudioPlayer.playAudio(getAudioPath(longUrl), handler);
			}
			break;
		case 0x1000100: {// 主体 音频+辅 音频
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);
			cidaiDown.setVisibility(View.VISIBLE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.GONE);
			longUrl = getLongRecUrl();
			shortUrl = getShortRecUrl();

			if (longUrl != null && longUrl.length() > 0) {
				AudioPlayer.playAudio(getAudioPath(longUrl), handler);
			}

		}
			break;
		case 0x1000101:// 主体 音频+辅 音频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.GONE);
			cidaiLeft.setVisibility(View.VISIBLE);
			singleText.setVisibility(View.GONE);
			soundWithText.setVisibility(View.VISIBLE);
			break;
		case 0x1000001:// 主体音频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			cidaiDown.setVisibility(View.VISIBLE);
			cidaiLeft.setVisibility(View.GONE);
			singleText.setVisibility(View.VISIBLE);
			soundWithText.setVisibility(View.GONE);
			break;

		case 0x100000:// 主体 图片

			// mMapView.setVisibility(View.INVISIBLE);
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);
			fullImageView.setVisibility(View.VISIBLE);

			String url = getImageUrl();
/*			if (url != null && url.length() > 0) {
				fullImageView.setImageUrl(url, 1, false);
			}*/
			if(url!=null && url.length() > 0){	
				imageLoader.displayImage(url, videoTouXiangImageView, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 1);
			}

			break;
		case 0x100001:// 主体 图片 +文字
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		case 0x100100: {// 主体 图片+辅 音频
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		}
		case 0x100101:// 主体 图片+辅 音频+文字
			fullRelativeLayout.setVisibility(View.VISIBLE);
			noFullRelativeLayout.setVisibility(View.GONE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;

		case 0x10000:// 主体 文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		case 0x10001:// 主体 文字+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		case 0x10100: {// 主体 文字+辅 音频
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		}
		case 0x10101:// 主体 文字+辅 音频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		case 0x100: {// 辅 音频
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		}
		case 0x101: {// 辅 音频+文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		}
		case 0x1:// 辅 文字
			fullRelativeLayout.setVisibility(View.GONE);
			noFullRelativeLayout.setVisibility(View.VISIBLE);
			viedoRelativeLayout.setVisibility(View.GONE);

			break;
		default:
			break;
		}

	}

	private String getCoverImageUrl() {

		if (null != currentDiary.attachs && currentDiary.attachs.length > 0) {

			for (int i = 0; i < currentDiary.attachs.length; i++) {
				diaryAttach attach = currentDiary.attachs[i];
				if ("1".equals(attach.attachtype)
						&& "1".equals(attach.attachlevel)) {

					return attach.videocover;
				}
			}
		}

		return "";
	}

	private String getImageUrl() {
		if (currentDiary.attachs != null && currentDiary.attachs.length > 0) {
			for (int i = 0; i < currentDiary.attachs.length; i++) {
				diaryAttach attach = currentDiary.attachs[i];
				if ("3".equals(attach.attachtype)
						&& "1".equals(attach.attachlevel)) {// 音频+辅内容
					MyAttachImage[] attachImage = attach.attachimage;
					if (attachImage != null && attachImage.length > 0) {
						// TODO 是否需要根据隐私设置给不同的url
						return attachImage[0].imageurl;
					}
				}
			}
		}
		return null;
	}

	// 获取短录音url
	private String getShortRecUrl() {
		if (currentDiary.attachs != null && currentDiary.attachs.length > 0) {
			for (int i = 0; i < currentDiary.attachs.length; i++) {
				diaryAttach attach = currentDiary.attachs[i];
				if ("2".equals(attach.attachtype)
						&& "0".equals(attach.attachlevel)) {// 音频+辅内容
					MyAttachAudio[] attachAudio = attach.attachaudio;
					if (attachAudio != null && attachAudio.length > 0) {
						// TODO 是否需要根据隐私设置给不同的url
						if (attachAudio[0].audiourl != null
								&& attachAudio[0].audiourl.length() > 0) {
							return attachAudio[0].audiourl;
						} else {
							return attach.attachuuid;
						}
					}
				}
			}
		}
		return null;
	}

	private String getLongRecUrl() {
		if (currentDiary.attachs != null && currentDiary.attachs.length > 0) {
			for (int i = 0; i < currentDiary.attachs.length; i++) {
				diaryAttach attach = currentDiary.attachs[i];
				if ("2".equals(attach.attachtype)
						&& "1".equals(attach.attachlevel)) {// 音频+主内容
					MyAttachAudio[] attachAudio = attach.attachaudio;
					if (attachAudio != null && attachAudio.length > 0) {
						// TODO 是否需要根据隐私设置给不同的url
						if (attachAudio[0].audiourl != null
								&& attachAudio[0].audiourl.length() > 0) {
							return attachAudio[0].audiourl;
						} else {
							return attach.attachuuid;
						}
					}
				}
			}
		}
		return null;
	}

	public String getAudioPath(String url) {
		if (null == url || 0 == url.length()) {
			// url=
			for (int i = 0; i < currentDiary.attachs.length; i++) {
				diaryAttach attach = currentDiary.attachs[i];
				if ("1".equals(attach.attachlevel)
						&& "2".equals(attach.attachtype)) {
					url = attach.attachuuid;
				}
			}
		}
		if ((null == url || 0 == url.length())) {
			return null;
		}
		String uid = ActiveAccount
				.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
				url);
		if (MediaValue.checkMediaAvailable(mv, 4)) {
			return Environment.getExternalStorageDirectory() + mv.path;
		}
		return url;
	}

	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {

			handler.sendEmptyMessage(PROCESS_PLAY);
			CURRENT_WHAT = PROCESS_PLAY;
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
		}

		@Override
		public void onPreparedPlayer(XMediaPlayer player) {
			player.play();
		}

		@Override
		public void onSurfaceCreated(XMediaPlayer player) {
			
		}

		@Override
		public void onVideoSizeChanged(XMediaPlayer player, int w, int h) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(XMediaPlayer player, int what, int extra) {
			// TODO Auto-generated method stub
			
		}
	}

	private double getPointYByX(double rake, double pointX,
			double knownPointX1, double knownPointY1) {

		return rake * (pointX - knownPointX1) + knownPointY1;
	}

	private void fitZoomFromPoints(MapController mapController,
			ArrayList<GeoPoint> points) {
		int nwLat = -90 * 1000000;
		int nwLng = 180 * 1000000;
		int seLat = 90 * 1000000;
		int seLng = -180 * 1000000;
		for (GeoPoint point : points) {
			nwLat = Math.max(nwLat, point.getLatitudeE6());
			nwLng = Math.min(nwLng, point.getLongitudeE6());
			seLat = Math.min(seLat, point.getLatitudeE6());
			seLng = Math.max(seLng, point.getLongitudeE6());
		}
		GeoPoint center = new GeoPoint((nwLat + seLat) / 2, (nwLng + seLng) / 2);

		int spanLatDelta = (int) (Math.abs(nwLat - seLat) * 1.1);
		int spanLngDelta = (int) (Math.abs(seLng - nwLng) * 1.1);

		mapController.zoomToSpan(spanLatDelta, spanLngDelta);
		// mapController.setCenter(points.get(0));
	}

	
	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	protected void onDestroy() {
		
		handler.removeCallbacksAndMessages(null);
		mMapView.destroy();

		try {
			if (mMediaPlayer != null) {
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	private MyDiary getDiaryById(String diaryId) {

		for (int i = 0; i < diaryList.size(); i++) {

			if (diaryId.equalsIgnoreCase(diaryList.get(i).diaryid)) {
				return diaryList.get(i);
			}
		}
		return null;
	}

	private MyDiary getNextDiaryById(String diaryId) {
		for (int i = 0; i < diaryList.size() - 1; i++) {
			if (diaryId.equalsIgnoreCase(diaryList.get(i).diaryid)) {
				return diaryList.get(i + 1);
			}
		}
		return null;
	}

	// private MyDiary getNextDiaryById(String diaryId) {
	// for (int i = 0; i < diaryList.size(); i++) {
	// if (diaryId.equalsIgnoreCase(diaryList.get(i).diaryid)) {
	// if (i < (diaryList.size() - 1)) {
	// return diaryList.get(i + 1);
	// } else if (i == (diaryList.size()- 1)) {
	// return diaryList.get(i);
	// }
	// }
	// }
	// return null;
	// }

	private MyDiary getPreDiaryById(String diaryId) {
		for (int i = 0; i < diaryList.size(); i++) {
			if (i > 0 && diaryId.equalsIgnoreCase(diaryList.get(i).diaryid)) {
				return diaryList.get(i - 1);
			}
		}
		return null;
	}

	// private MyDiary getPreDiaryById(String diaryId) {
	// for (int i = 0; i < diaryList.size(); i++) {
	// if (diaryId.equalsIgnoreCase(diaryList.get(i).diaryid)) {
	// if (i > 0) {
	// return diaryList.get(i - 1);
	// } else if (i == 0) {
	// return diaryList.get(i);
	// }
	// }
	// }
	// return null;
	// }

	private void disablePreOrNextButton(String currentDiaryId) {
		if (currentDiaryId.equals(lastDiaryId)) {

			nextButton.setClickable(false);
		} else {

			nextButton.setClickable(true);
		}
		if (currentDiaryId.equals(firstDiaryId)) {

			preButton.setClickable(false);
		} else {
			preButton.setClickable(true);

		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.play_image_button:

//			handler.sendEmptyMessage(INIT_UI);
//			CURRENT_WHAT = INIT_UI;
			// playSth(currentDiary);

			break;
		case R.id.pre_button:
//			handler.removeMessages(CURRENT_WHAT);

//			if (handler.hasMessages(INIT_UI)) {
//				handler.removeMessages(INIT_UI);
//			}
//			if (handler.hasMessages(PROCESS_PLAY)) {
//				handler.removeMessages(PROCESS_PLAY);
//			}
//			if (handler.hasMessages(START_MOVE_MAP)) {
//				handler.removeMessages(START_MOVE_MAP);
//			}
//			if (handler.hasMessages(MOVE_MAP)) {
//				handler.removeMessages(MOVE_MAP);
//			}
//			if (handler.hasMessages(MOVE_COMPLETE)) {
//				handler.removeMessages(MOVE_COMPLETE);
//			}

			handler.removeCallbacksAndMessages(null);
			if (currentChange) {
				currentDiaryId = (getPreDiaryById(currentDiaryId) == null ? currentDiaryId
						: getPreDiaryById(currentDiaryId).diaryid);
			} else {
				currentChange = true;
			}

			// disablePreOrNextButton(currentDiaryId);
			handler.sendEmptyMessage(RESET_PLAY);
			CURRENT_WHAT = RESET_PLAY;

			break;

		case R.id.next_button:
			// handler.removeMessages(CURRENT_WHAT);
			//
			// if (handler.hasMessages(INIT_UI)) {
			// handler.removeMessages(INIT_UI);
			// }
			// if (handler.hasMessages(PROCESS_PLAY)) {
			// handler.removeMessages(PROCESS_PLAY);
			// }
			// if (handler.hasMessages(START_MOVE_MAP)) {
			// handler.removeMessages(START_MOVE_MAP);
			// }
			// if (handler.hasMessages(MOVE_MAP)) {
			// handler.removeMessages(MOVE_MAP);
			// }
			// if (handler.hasMessages(MOVE_COMPLETE)) {
			// handler.removeMessages(MOVE_COMPLETE);
			// }
			handler.removeCallbacksAndMessages(null);
			currentDiaryId = (getNextDiaryById(currentDiaryId) == null ? currentDiaryId
					: getNextDiaryById(currentDiaryId).diaryid);

			// disablePreOrNextButton(currentDiaryId);

			handler.sendEmptyMessage(RESET_PLAY);
			CURRENT_WHAT = RESET_PLAY;

			break;
		case R.id.back_button:
			this.finish();
			break;
		default:
			break;
		}

	}

	private void setTime(String timemilli) {
		if (timemilli != null) {
			long time = Long.parseLong(timemilli);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			String timeHHmm = DateUtils.dateToString(c.getTime(),
					DateUtils.DATE_FORMAT_TODAY);
			String timeDay = DateUtils.dateToString(c.getTime(),
					DateUtils.DATE_FORMAT_NORMAL_1);
			if (timeDay.startsWith("0"))
				timeDay = timeDay.substring(1, timeDay.length());
			float hour = c.get(Calendar.HOUR_OF_DAY);
			float seconds = c.get(Calendar.MINUTE);// 分
			float hAngle = (hour / 12f) * 360f;
			float hSecond = (seconds / 60f) * 360f;
			Matrix m = new Matrix();
			m.setRotate(hAngle);
			Bitmap bHour = Bitmap.createBitmap(bmHour, 0, 0, hWidth,
					hHeight, m, true);
			m.setRotate(hSecond);
			Bitmap bSecond = Bitmap.createBitmap(bmSecond, 0, 0, sWidth,
					sHeight, m, true);
			
			tvMinutesReplace.setText(timeHHmm);
			tvHoursReplace.setText(timeDay);
			ivHoursReplace.setImageBitmap(bHour);
			ivMinutesReplace.setImageBitmap(bSecond);
//			handler.obtainMessage(MAP_UPDATE_TIME_REPLACE,
//					new Object[] { timeHHmm, timeDay, bHour, bSecond })
//					.sendToTarget();
		}
	}
	class updateTimeThread implements Runnable {
		private String timemilli;

		public updateTimeThread(String timemilli) {
			this.timemilli = timemilli;
		}

		@Override
		public void run() {
			if (timemilli != null) {
				long time = Long.parseLong(timemilli);
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(time);
				String timeHHmm = DateUtils.dateToString(c.getTime(),
						DateUtils.DATE_FORMAT_TODAY);
				String timeDay = DateUtils.dateToString(c.getTime(),
						DateUtils.DATE_FORMAT_NORMAL_1);
				if (timeDay.startsWith("0"))
					timeDay = timeDay.substring(1, timeDay.length());
				float hour = c.get(Calendar.HOUR_OF_DAY);
				float seconds = c.get(Calendar.MINUTE);// 分
				float hAngle = (hour / 12f) * 360f;
				float hSecond = (seconds / 60f) * 360f;
				Matrix m = new Matrix();
				m.setRotate(hAngle);
				Bitmap bHour = Bitmap.createBitmap(bmHour, 0, 0, hWidth,
						hHeight, m, true);
				m.setRotate(hSecond);
				Bitmap bSecond = Bitmap.createBitmap(bmSecond, 0, 0, sWidth,
						sHeight, m, true);
				handler.obtainMessage(MAP_UPDATE_TIME_REPLACE,
						new Object[] { timeHHmm, timeDay, bHour, bSecond })
						.sendToTarget();
			}

		}

	}
}
