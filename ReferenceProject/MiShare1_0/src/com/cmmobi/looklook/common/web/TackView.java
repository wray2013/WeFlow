package com.cmmobi.looklook.common.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.common.view.AudioRecogniseView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;

import effect.XEffectMediaPlayer;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-6-8
 */
public class TackView extends FrameLayout implements OnCompletionListener,OnErrorListener, OnInfoListener {

	private static final String TAG = "TackView";
	private String localSounds = "";

	private boolean isLeft = true;
	private boolean needRecogn = false;
	private boolean noTouchRecong = false;
	
	public TackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.TackView);
		int resId = a.getResourceId(R.styleable.TackView_backresource, -1);
		isLeft = a.getBoolean(R.styleable.TackView_isLeft, true);
		needRecogn = a.getBoolean(R.styleable.TackView_needRecogn, false);
		noTouchRecong = a.getBoolean(R.styleable.TackView_noTouchRecong, false);
		if (resId != -1) {
			init(context,resId);
		} else {
			init(context,R.layout.del_common_tackview);
		}
		a.recycle();
		
	}

	public TackView(Context context) {
		super(context);
		init(context,R.layout.del_common_tackview);
	}
	
	/*@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		llContainer.setOnClickListener(l);
	}*/
	
	//显示麦克风图标
	public void setMicMode(){
		removeAllViews();
		isMicMode=true;
		init(getContext(),R.layout.del_common_tackview);
	}
	
	private Handler handler;
	public void setHandler(Handler handler){
		this.handler=handler;
	}

	private View llContainer;
	private ImageView ivBiezhen;
	private TextView tvBiezhen;
	private ImageView ivMic;
	private ImageView imgDian;  // 点
	private FrameLayout flyRecogn;  // 转换
	private LayoutInflater inflater;
	
	private boolean isMicMode;

	private AudioRecogniseView audioRecogn;
	
	
	
	private void init(Context context,int layoutId) {
		inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layoutId, null);
		llContainer=v.findViewById(R.id.ll_biezhen);
		
		ivBiezhen = (ImageView) v.findViewById(R.id.iv_biezhen_sound);// 别针中的声音图片
		tvBiezhen = (TextView) v.findViewById(R.id.tv_biezhen_text);// 别针中的文字
		
		addView(v);
		
		imgDian = (ImageView) v.findViewById(R.id.img_dian);// 别针中的文字
		flyRecogn = (FrameLayout) v.findViewById(R.id.fly_recogn_view);// 别针中的文字
		
		if(needRecogn && flyRecogn!=null){
			// 加入转文字控件
			audioRecogn = new AudioRecogniseView(context,noTouchRecong); 
			flyRecogn.addView(audioRecogn);
		}
		
	}
	
	
	/**
	 * 显示未读红点（本地）
	 */
	private void showUnReadPoint(boolean notReadLocalAudio){
		// 已阅读
		if(!notReadLocalAudio){
			if(imgDian!= null ){
				imgDian.setVisibility(View.GONE);
			}
		}else{// 未阅读 显示红点
			if(needRecogn && flyRecogn != null && imgDian!= null){
				imgDian.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * 阅读状态
	 * onclick
	 */
	public void readAudioState(){
		// 表示阅读的是接收到的语音。并且是未读状态
		if(msgdata.r_msg!=null && msgdata.r_msg.notReadLocalAudio ){
			msgdata.r_msg.notReadLocalAudio = false;
		}
		if(imgDian!=null){
			imgDian.setVisibility(View.GONE);
		}
	}
	
	
	/**
	 * 设置背景图 如果resID=0 背景图为空
	 */
	public void setBackground(int resID){
		if(0==resID){
			llContainer.setBackgroundDrawable(null);
		}else{
			llContainer.setBackgroundResource(resID);
		}
	}

	/**
	 * 设置音频转文字控件需要的path
	 * @param path
	 */
	public void setRecogniPath(String path){
		if(audioRecogn!= null){
			audioRecogn.setLocalPath(path);
		}
	}
	
	
	private PrivateCommonMessage msgdata;
	/**
	 * 设置adapter中的item数据
	 * @param path
	 */
	public void setPrivateMsgData(PrivateCommonMessage item){
		msgdata = item;
		
		if(item.r_msg!=null &&item.r_msg.notReadLocalAudio){
			showUnReadPoint(true);
		}else{
			showUnReadPoint(false);
		}
		if(audioRecogn!=null){
			audioRecogn.setPrivateMsgData(item,imgDian);
		}
	}
	
	/**
	 * 设置录音播放总时长
	 */
	public void setPlaytime(String playtime) {
//		Log.d(TAG, "playtime=" + playtime);
		if(playtime == null || playtime.equals("")){
			return;
		}
		if(playtime.contains("''")){
			tvBiezhen.setText(playtime);
		}else{
			tvBiezhen.setText(playtime + "''");
		}
		
		/*int time = 0;
		try {
			time = Integer.parseInt(playtime.replace("''", ""));
		} catch (Exception e) {
		}		
		if(time != 0 && time <= 30){
			float level = ((time - 1) / 10 + 1) / 3f;
			this.setBackgroudLevel(level, 110);
		}*/
	}
	
	/**
	 * 设置录音播放总时长
	 */
	public void setPlaytime(String playtime, boolean resize) {
//		Log.d(TAG, "playtime=" + playtime);
		if(playtime == null || playtime.equals("")){
			return;
		}
		if(playtime.contains("''")){
			tvBiezhen.setText(playtime);
		}else{
			tvBiezhen.setText(playtime + "''");
		}
		
		int time = 0;
		try {
			time = Integer.parseInt(playtime.replace("''", ""));
		} catch (Exception e) {
		}		
	}
	
	public void setLocalAudio (String audioPath) {
		localSounds = audioPath;
		if(mp!=null){
			mp.release();
			mp=null;
			if(tackView!=null)tackView.stop();
		}
		if (isPlaying) {
			stop();
		} else {
			try {
				mp=new MediaPlayer();
				mp.setOnCompletionListener(this);
				mp.setOnErrorListener(this);
				mp.setDataSource(audioPath);
				mp.prepare();
				double total = new Mp4InfoUtils(audioPath).totaltime;
				Log.d(TAG,"totalDuration = " + total);
				if (total < 1.0) {
					total = 1.0;
				}
				String playtime = String.valueOf((int) (total));
				tvBiezhen.setText("" + playtime + "″");
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getLocalPath() {
		return localSounds;
	}

	/**
	 * url 录音的网络地址 playtime
	 */
	public void setAudio(String url,int belong) {
		Log.d(TAG, "TackView - setAudio url=" + url);
		if (isPlaying&&tackView==this) {
			stop();
		} else {
			if(mp!=null){
				mp.release();
				mp=null;
				if(tackView!=null)tackView.stop();
			}
			getAudio(url,belong);
		}
	}
	
	/**
	 * url 录音的网络地址 playtime
	 */
	public void setAudio(String url,int belong, String statisUrl) {
		Log.d(TAG, "TackView - setAudio url=" + url + statisUrl);
		if (isPlaying&&tackView==this) {
			stop();
		} else {
			if(mp!=null){
				mp.release();
				mp=null;
				if(tackView!=null)tackView.stop();
			}
			getAudio(url,belong, statisUrl);
		}
	}

	private void getAudio(String url,int belong) {
		if (null == url || 0 == url.length()) {
			Log.e(TAG, "url is null");
			return;
		}
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, 3)) {
			//  hit:
			File file = new File(Environment.getExternalStorageDirectory() + mv.localpath);
			if(file.exists() && file.isFile()){
				playAudio(file.getAbsolutePath());
			}else{
				if(url.startsWith("http:")){
					Log.e(TAG, "hit, try to load from http");
					loadAudio(url,belong);
				}else{
					File file1 = new File(Environment.getExternalStorageDirectory() + url);
					if(file1.exists() && file1.isFile()){
						playAudio(file1.getAbsolutePath());
					}else{
						Toast.makeText(getContext(), "提示：该音频不存在", Toast.LENGTH_SHORT).show();
					}
				}
			}
			


		} else {
			// not hit:
			if(url.startsWith("http:")){
				Log.e(TAG, "hit, try to load from http");
				loadAudio(url,belong);
			}else{
				File file = new File(Environment.getExternalStorageDirectory() + url);
				if(file.exists() && file.isFile()){
					playAudio(file.getAbsolutePath());
				}else{
					Toast.makeText(getContext(), "提示：该音频不存在", Toast.LENGTH_SHORT).show();
				}

			}
		}
	}

	private void getAudio(String url,int belong, String statisUrl) {
		if (null == url || 0 == url.length()) {
			Log.e(TAG, "url is null");
			return;
		}
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, 3)) {
			//  hit:
			File file = new File(Environment.getExternalStorageDirectory() + mv.localpath);
			if(file.exists() && file.isFile()){
				playAudio(file.getAbsolutePath());
			}else{
				if(url.startsWith("http:")){
					Log.e(TAG, "hit, try to load from http");
					loadAudio(url,belong, statisUrl);
				}else{
					File file1 = new File(Environment.getExternalStorageDirectory() + url);
					if(file1.exists() && file1.isFile()){
						playAudio(file1.getAbsolutePath());
					}else{
						Toast.makeText(getContext(), "提示：该音频不存在", Toast.LENGTH_SHORT).show();
					}
				}
			}
			


		} else {
			// not hit:
			if(url.startsWith("http:")){
				Log.e(TAG, "hit, try to load from http");
				loadAudio(url,belong);
			}else{
				File file = new File(Environment.getExternalStorageDirectory() + url);
				if(file.exists() && file.isFile()){
					playAudio(file.getAbsolutePath());
				}else{
					Toast.makeText(getContext(), "提示：该音频不存在", Toast.LENGTH_SHORT).show();
				}

			}
		}
	}
	
	// 下载录音
	private void loadAudio(String url,int belong) {
		if(ZNetworkStateDetector.isConnected()){
			new DownloadTask().execute(url,belong);
		}else{
			Prompt.Alert(this.getContext(), "您的网络不给力呀");
		}
	}
	
	// 下载录音
	private void loadAudio(String url,int belong, String statisUrl) {
		if(ZNetworkStateDetector.isConnected()){
			new DownloadTask().execute(url,belong, statisUrl);
		}else{
			Prompt.Alert(this.getContext(), "您的网络不给力呀");
		}
	}
	
	public static TackView getTackView(){
		return tackView;
	}
	
	private boolean isPaused;
	public void pause(){
		if(tackView!=null&&isPlaying&&mp!=null&&mp.isPlaying()){
			mp.pause();
			isPlaying=false;
			isPaused=true;
		}
	}
	
	public void resume(){
		if(tackView!=null&&!isPlaying&&mp!=null&&isPaused){
			mp.start();
			isPaused=false;
		}
	}

	public static boolean isPlaying;
	private static TackView tackView;
	private static MediaPlayer mp;
	// 播放录音
	public void playAudio(String audioPath) {
		Log.d(TAG, "audioPath=" + audioPath);
		if(isPlaying)return;
		isPlaying = true;
		tackView=this;
		if(!isMicMode)
			myHandler.post(timeTask);
		try {
//			audioPath="/storage/sdcard0/looklook/e135f0650bccd044ed0a1450f9d6d445b456/audio/taisha";
			mp=new MediaPlayer();
			mp.setOnCompletionListener(this);
			mp.setOnErrorListener(this);
			mp.setDataSource(audioPath);
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			stop();
			e.printStackTrace();
		}
	}
	
	public void playAudio(String audioPath,XMediaPlayer xMediaPlayer) {
		tackView=this;
		tackView.index = 0;
		xMediaPlayer.stop();
		xMediaPlayer.setListener(this);
		xMediaPlayer.open(audioPath);
	}
	
	public void stopAudio(XMediaPlayer xMediaPlayer) {
		if (xMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
			xMediaPlayer.stop();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
//		Log.d(TAG, "onError");
		stop();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
//		Log.d(TAG, "onCompletion");
		stop();
	}

	private static Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_UPDATE_AUDIO_ICON:
				int resId = (Integer) msg.obj;
				tackView.ivBiezhen.setImageResource(resId);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	public void stop() {
		isPlaying = false;
		if(mp!=null){
			mp.release();
			mp=null;
		}
		if(tackView!=null){
			tackView.ivBiezhen.setImageResource(SOUND_ICONS[2]);
			tackView.myHandler.removeCallbacks(timeTask,null);
		}
		ivBiezhen.setImageResource(SOUND_ICONS[2]);
		myHandler.removeCallbacks(timeTask,null);
		if(tackView != null && tackView.handler != null)
			tackView.handler.obtainMessage(DiaryPreviewActivity.HANDLER_UPDATE_TACK_PLAYER_PROCESS, 0).sendToTarget();
	}

	private static final int HANDLER_UPDATE_AUDIO_ICON = 0x110011;
	private static final int LOOP_PERIOD = 500;
	
	private int y=-1;//
	private static Runnable timeTask = new Runnable() {

		@Override
		public void run() {
			if (tackView.index >= tackView.SOUND_ICONS.length)
				tackView.index = 0;
			int[] location=new int[2];
			tackView.getLocationInWindow(location);
			if(-1==tackView.y){
				tackView.y=location[1];
			}
			if(tackView.y!=location[1]){
				tackView.y=location[1];
//				tackView.stop();
			}else{
				myHandler.obtainMessage(HANDLER_UPDATE_AUDIO_ICON,
						tackView.SOUND_ICONS[tackView.index]).sendToTarget();
			}
			myHandler.postDelayed(this, LOOP_PERIOD);
			if(mp!=null&&mp.isPlaying()){
				int current=mp.getCurrentPosition();
				int total=mp.getDuration();
				int process = 0;
				if(total!=0){
					process = current*100/total;
				}
				
				if(tackView.handler!=null)tackView.handler.obtainMessage(DiaryPreviewActivity.HANDLER_UPDATE_TACK_PLAYER_PROCESS, process).sendToTarget();
			}
			tackView.index++;
		}
	};

	int index = 0;
	private int[] SOUND_ICONS = { R.drawable.del_yuyin_xiao_sound_1,
			R.drawable.del_yuyin_xiao_sound_2, R.drawable.del_yuyin_xiao_sound_3, };
	
	public void setSoundIcons(int[] icons) {
		SOUND_ICONS = icons;
		ivBiezhen.setImageResource(SOUND_ICONS[2]);
	}
	
	public void setSoundIcons(int[] icons, boolean setDefault) {
		SOUND_ICONS = icons;
		if(setDefault)
		{
			ivBiezhen.setImageResource(SOUND_ICONS[2]);
		}
	}
	
	public void setSoundIconBackground(int resId) {
		ivBiezhen.setBackgroundResource(resId);
	}

	class DownloadTask extends AsyncTask<Object, Void, MediaValue> {
		String url;
		int belong;

		@Override
		protected MediaValue doInBackground(Object... param) {
			String audioPath = null;
			MediaValue result=null;
			try {
				url = (String) param[0];
				belong = (Integer) param[1];
				String statisUrl = "";
				if(param.length > 2)
				{
					statisUrl = (String) param[2];
				}
				ZHttp2 http2 = new ZHttp2();
//				url=url.substring(url.indexOf("http"), url.length());
				Log.e(TAG, "url="+url+statisUrl);
				ZHttpResponse httpResponse = http2.get(url+statisUrl);
				if(null==httpResponse)return null;
				InputStream inputStream = httpResponse.getInputStream();
				if (inputStream == null) {
					return null;
				}
				String uid = ActiveAccount.getInstance(
						MainApplication.getAppInstance()).getUID();
				String Key = MD5.encode((uid + url).getBytes());
				audioPath = Environment.getExternalStorageDirectory()
						+ Constant.SD_STORAGE_ROOT + "/" + uid + "/audio/" + Key;
				File audioFile = new File(Environment.getExternalStorageDirectory()+audioPath);
				if (!audioFile.getParentFile().exists()) {
					audioFile.getParentFile().mkdirs();
				}
				FileOutputStream os = new FileOutputStream(audioFile);
				byte buffer[] = new byte[1024];
				long size=0;
				int len;
				while ((len=inputStream.read(buffer)) != -1) {
					os.write(buffer,0,len);
					size+=len;
				}
				os.flush();
				os.close();
				result = new MediaValue();
				result.UID = uid;
				result.Belong = belong;
				result.Direction = 1;
				result.MediaType = 3;
				result.url = url;
				result.localpath = audioPath;
				result.realSize = size;
				result.Sync = 1;
				result.SyncSize = size;
				result.totalSize = size;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return result;
		}

		@Override
		protected void onPostExecute(MediaValue result) {
			if (!MediaValue.checkMediaAvailable(result, 3)) {  
				Log.e(TAG, "checkMediaAvailable is  false");
				return;
			}
			AccountInfo.getInstance(result.UID).mediamapping.setMedia(result.UID, url, result);
			playAudio(Environment.getExternalStorageDirectory()+result.localpath);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if(isPlaying||!isAppOnFront(this.getContext())){
			stop();
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void onAttachedToWindow() {
		if(isPlaying||!isAppOnFront(this.getContext())){
			stop();
		}
		super.onAttachedToWindow();
	}
	
	private boolean isAppOnFront(Context context){
		if(context!=null){
			String packageName = context.getPackageName();
			ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
			if(appTask!=null && appTask.size()>0){
				if(appTask.get(0).topActivity.toString().contains(packageName)){
					return true;
				}
			}
		}
	    return false;
	}
	
	private float mBackgroundLevel = 1f;
	private int mEnd = 0;
	
	/*public void setBackgroudLevel(float level, int end)
	{
		mBackgroundLevel = level;
		mEnd = end;
		
		int px = DensityUtil.dip2px(this.getContext(), 100);
		int sum = (px * 3) + (end/2);
		px = (int) (sum * level);
		
		if(this.getParent() instanceof LinearLayout){
			this.setLayoutParams(new LinearLayout.LayoutParams(px,LayoutParams.WRAP_CONTENT));
		}else if(this.getParent() instanceof RelativeLayout){
			RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(px,LayoutParams.WRAP_CONTENT);
			if(!isLeft){
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
			}
			this.setLayoutParams(params);
		}else if(this.getParent() instanceof FrameLayout){
			FrameLayout.LayoutParams fparams = new FrameLayout.LayoutParams(px,LayoutParams.WRAP_CONTENT);
			if(!isLeft){
				fparams.gravity = Gravity.RIGHT;
			}
			this.setLayoutParams(fparams);
		}else{
			this.setLayoutParams(new ViewGroup.LayoutParams(px,LayoutParams.MATCH_PARENT));
		}
	}*/
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
//		int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
//		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		
//		ZLog.e("TackView Onmeasure widthMeasure 1 = " + widthMeasure);
//		if(widthMeasure != 0)
//		{
//			widthMeasure = (int) (mEnd + (widthMeasure - mEnd) * mBackgroundLevel);
//		}
//		ZLog.e("TackView Onmeasure widthMeasure 2 = " + widthMeasure);
//		widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasure, widthMode);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void onStartPlayer(XMediaPlayer player) {
		// TODO Auto-generated method stub
		
		myHandler.post(timeTask);
	}

	@Override
	public void onStopPlayer(XMediaPlayer player) {
		// TODO Auto-generated method stub
		ivBiezhen.setImageResource(SOUND_ICONS[2]);
		myHandler.removeCallbacks(timeTask,null);
	}

	@Override
	public void OnFinishPlayer(XMediaPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreparedPlayer(XMediaPlayer player) {
		player.play();
	}

	@Override
	public void onUpdateTime(XMediaPlayer player, double time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceCreated(XMediaPlayer player) {
		// TODO Auto-generated method stub
		
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
