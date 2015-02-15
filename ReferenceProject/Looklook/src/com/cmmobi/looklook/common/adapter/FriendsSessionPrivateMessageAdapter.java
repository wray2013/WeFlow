package com.cmmobi.looklook.common.adapter;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity.DiaryType;
import com.cmmobi.looklook.activity.HomepageOtherDiaryActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachVideo;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyMessage;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.downloader.BackgroundDownloader;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.PrivateSendMessage;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class FriendsSessionPrivateMessageAdapter extends BaseAdapter implements OnLongClickListener {

	private static final String TAG = "FriendsSessionPrivateMessageAdapter";

	private final int SESSION_PERIOD_THRESHOLD = 2*60*1000;
	
	public static final int HANDLER_FLAG_MSG_DELETE = 0x1638376a;
	public static final int HANDLER_FLAG_MSG_RETRY = 0x1638376b;

	
	//public static final String RECEIVER_DIARLY_PROGRESS_UPDATE = "DIARLY_PROGRESS_UPDATE";
	public static final int HANDLER_DIARLY_PROGRESS_UPDATE = 0x81214001;
	
	private Context context;
	private Handler handler;
	//private BackgroundDownloadReceiver receiver;

	private LinkedList<PrivateCommonMessage> chatMsgList;
	private FriendsExpressionView expressionView;

	private LayoutInflater inflater;
	
	//使用开源的webimageloader
	public static DisplayImageOptions options;
	//public static DisplayImageOptions diary_pic_options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	
	private long last_item_time;

	private String other_nick;

	protected PopupWindow popupWindow;

	private RotateAnimation animation;

	private String uid;

	private ListView listView;



	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	public FriendsSessionPrivateMessageAdapter(ListView listView, Context context, Handler handler, LinkedList<PrivateCommonMessage> list, FriendsExpressionView expressionView, String other_nick) {
		this.listView = listView;
		this.context = context;
		this.chatMsgList = list;
		this.handler = handler;
		this.expressionView = expressionView;
		this.inflater = LayoutInflater.from(context);
		this.other_nick = other_nick;
		
		this.uid = ActiveAccount.getInstance(context).getLookLookID();
		
		/*animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);*/
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		//.displayer(new SimpleBitmapDisplayer())
		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
		//this.receiver = new BackgroundDownloadReceiver();
		//LocalBroadcastManager.getInstance(this.context).registerReceiver(receiver, new IntentFilter(DIARLY_PROGRESS_UPDATE));
	
/*		diary_pic_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.temp_shipin)
		.showImageForEmptyUri(R.drawable.temp_shipin)
		.showImageOnFail(R.drawable.temp_shipin)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		//.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();*/
	}

	
	@Override
	public int getCount() {
		return chatMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		return chatMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		Log.e(TAG, "position: " + position);
		final ViewHolder viewHolder;
		final PrivateCommonMessage item = chatMsgList.get(position);
		if(position>0){
			PrivateCommonMessage last = chatMsgList.get(position-1);
			last_item_time = last.create_time;
		}else{
			last_item_time = 0;
		}
		
		if (convertView == null) {
			viewHolder = new ViewHolder();
			
			if (item.send) {
				convertView = inflater.inflate(R.layout.list_item_friends_private_message_right, null);
/*				viewHolder.iv_audio_attach_loading = (ImageView)convertView.findViewById(R.id.iv_audio_attach_loading);
				viewHolder.iv_audio_attach_fail = (ImageView)convertView.findViewById(R.id.iv_audio_attach_fail);*/
				viewHolder.iv_send_status = (ImageView)convertView.findViewById(R.id.iv_send_status);
			} else {
				convertView = inflater.inflate(R.layout.list_item_friends_private_message_left, null);

			}
			
			//UI pick			
			viewHolder.send = item.send;
			
			viewHolder.ll_msg = (RelativeLayout) convertView.findViewById(R.id.ll_msg);
			
			viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			
			viewHolder.tv_txt_media = (TextView) convertView.findViewById(R.id.tv_txt_media);
			
			viewHolder.rl_all = (RelativeLayout)convertView.findViewById(R.id.rl_all);
			
			viewHolder.rl_video = (RelativeLayout)convertView.findViewById(R.id.rl_video);
			viewHolder.tv_video_txt_media = (TextView)convertView.findViewById(R.id.tv_video_txt_media);
			viewHolder.iv_video_play = (WebImageView) convertView.findViewById(R.id.iv_video_play);
			viewHolder.tv_video_len = (TextView) convertView.findViewById(R.id.tv_video_len);
			viewHolder.iv_video_screen = (ImageView) convertView.findViewById(R.id.iv_video_screen);
		    viewHolder.pb_video_progress = (ProgressBar) convertView.findViewById(R.id.pb_video_progress);
			viewHolder.rl_video_attach_audio = (TackView)convertView.findViewById(R.id.rl_video_attach_audio);
			//viewHolder.tv_record_len = (TextView) convertView.findViewById(R.id.tv_record_len);
			viewHolder.tv_video_attach_txt = (TextView) convertView.findViewById(R.id.tv_video_attach_txt);
			
			viewHolder.rl_record = (RelativeLayout)convertView.findViewById(R.id.rl_record);
			//viewHolder.tv_record_audio_len = (TextView) convertView.findViewById(R.id.tv_record_audio_len);
			viewHolder.rl_record_media = (TackView) convertView.findViewById(R.id.rl_record_media);
			viewHolder.tv_audio_attach_txt =  (TextView) convertView.findViewById(R.id.tv_audio_attach_txt);
			
			viewHolder.tv_msg_date =  (TextView) convertView.findViewById(R.id.tv_msg_date);
			

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		
		//UI show
		ActiveAccount aa = ActiveAccount.getInstance(context);			
		AccountInfo ai = AccountInfo.getInstance(aa.getLookLookID());
		String my_headimagurl = ai.headimageurl;
		
		PrivateMessageManager pmm = ai.privateMsgManger;
		
		boolean hasDiary = false;
		boolean hasAudio = false;
		boolean hasText = false;
		
		
		if(item.create_time-last_item_time>SESSION_PERIOD_THRESHOLD){
			viewHolder.tv_msg_date.setVisibility(View.VISIBLE);
			
		}else{
			viewHolder.tv_msg_date.setVisibility(View.GONE);
		}
		
		viewHolder.tv_msg_date.setText(DateUtils.getMyCommonShowDate(new Date(item.create_time)));
		//viewHolder.tv_msg_date.setText(DateUtils.getNormlDate(item.create_time));
		
		Log.e(TAG, "send: " + item.send + " " + DateUtils.getNormlDate(item.create_time) + " item:" + item.getContent());
		
		
		if(item.send){ //+++++++++++++++++++ 加载自己的消息
			
    		if(my_headimagurl!=null && my_headimagurl.length()>0){
    			imageLoader.displayImage(my_headimagurl, viewHolder.iv_pic, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 4);
				//viewHolder.iv_pic.setImageUrl(R.drawable.temp_local_icon, 4, my_headimagurl, true);
			}else{
				viewHolder.iv_pic.setImageResource(R.drawable.moren_touxiang);
			}
			
			viewHolder.rl_record_media.setBackground(R.drawable.paopao_haoyou);
			viewHolder.rl_video_attach_audio.setBackground(R.drawable.paopao_haoyou_ms);
			
			Gson gson = new Gson();

			Log.e(TAG, "item:" + item + "item.s_msg:" + gson.toJson(item.s_msg, PrivateSendMessage.class));

			if(item.s_msg!=null){
				//私信类型 --- 1文字 2语音，3日记，4语音加文字
				if("1".equals(item.s_msg.privatemsgtype)){
					hasDiary = false;
					hasAudio = false;
					hasText = true;
				}else if("2".equals(item.s_msg.privatemsgtype)){
					hasDiary = false;
					hasAudio = true;
					hasText = false;
				}else if("3".equals(item.s_msg.privatemsgtype)){
					hasDiary = true;
					if(item.s_msg!=null){
						if( item.s_msg.diaries!=null){
							if(getAttachUrl(item.s_msg.diaries, "2", "0")!=null){
								hasAudio = true;

							}else{
								hasAudio = false;
							}
							
							if(getTxtContent(item.s_msg.diaries)!=null){
								hasText = true;
							}else{
								hasText = false;
							}

						}else{
							hasAudio = false;
							hasText = false;
						}
						

						
						
					}else{
						hasAudio = false;
						hasText = false;
					}
				}else if("4".equals(item.s_msg.privatemsgtype)){
					hasDiary = false;
					hasAudio = true;
					hasText = true;
				}else{
					Log.e(TAG, "item.s_msg.privatemsgtype is null");
				}
				
				if(hasDiary && item.s_msg!=null && item.s_msg.diaries!=null){//){
					setMessageItem(imageLoader, viewHolder, hasDiary, getDiaryCoverUrl(item.s_msg.diaries), getDiaryType(item.s_msg.diaries), getVideoPlaytime(item.s_msg.diaries), hasAudio, getAudioPlaytime(item.s_msg.diaries), hasText, getTxtContent(item.s_msg.diaries));
				}else {
					setMessageItem(imageLoader, viewHolder, false, null, null, null, hasAudio, item.s_msg.playtime, hasText, item.s_msg.content);
				}
				int id=0;
				if(View.VISIBLE==viewHolder.rl_video.getVisibility()){
					Log.d(TAG, "viewHolder.rl_video="+viewHolder.rl_video.getId());
					id=viewHolder.rl_video.getId();
				}else if(View.VISIBLE==viewHolder.tv_txt_media.getVisibility()){
					Log.d(TAG, "viewHolder.tv_txt_media="+viewHolder.tv_txt_media.getId());
					id=viewHolder.tv_txt_media.getId();
				}else if(View.VISIBLE==viewHolder.rl_record.getVisibility()){
					Log.d(TAG, "viewHolder.rl_record="+viewHolder.rl_record.getId());
					id=viewHolder.rl_record.getId();
				}
				android.widget.RelativeLayout.LayoutParams params=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.rightMargin=10;
				params.topMargin=10;
				params.addRule(RelativeLayout.LEFT_OF, id);
				convertView.findViewById(R.id.iv_send_status).setLayoutParams(params);
				if(item.s_msg.status==SEND_MSG_STATUS.ALL_DONE){
/*					viewHolder.iv_audio_attach_loading.setVisibility(View.GONE);
					viewHolder.iv_audio_attach_fail.setVisibility(View.GONE);*/
					viewHolder.iv_send_status.setVisibility(View.GONE);
					viewHolder.iv_send_status.clearAnimation();
				}else if(item.s_msg.status==SEND_MSG_STATUS.RIA_FAIL || item.s_msg.status==SEND_MSG_STATUS.UPLOAD_FAIL){
/*					viewHolder.iv_audio_attach_loading.setVisibility(View.GONE);
					viewHolder.iv_audio_attach_fail.setVisibility(View.VISIBLE);*/
					viewHolder.iv_send_status.setVisibility(View.VISIBLE);
					viewHolder.iv_send_status.setImageResource(R.drawable.shibai);
					viewHolder.iv_send_status.clearAnimation();
					
					/*viewHolder.iv_audio_attach_fail*/
					viewHolder.iv_send_status.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							handler.obtainMessage(HANDLER_FLAG_MSG_RETRY, position).sendToTarget();
						}
					});
					
				}else{
/*					viewHolder.iv_audio_attach_loading.setVisibility(View.VISIBLE);
					viewHolder.iv_audio_attach_fail.setVisibility(View.GONE);*/
					viewHolder.iv_send_status.setVisibility(View.VISIBLE);
					viewHolder.iv_send_status.setImageResource(R.drawable.loading_s);
					//Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate);
					animation = new RotateAnimation(0, 360,
							RotateAnimation.RELATIVE_TO_SELF, 0.5f,
							RotateAnimation.RELATIVE_TO_SELF, 0.5f);
					animation.setInterpolator(new LinearInterpolator());
					animation.setDuration(500);
					animation.setRepeatCount(Animation.INFINITE);
					animation.setFillAfter(true);
					viewHolder.iv_send_status.clearAnimation();
					viewHolder.iv_send_status.startAnimation(animation); 
/*					AnimationDrawable anim = (AnimationDrawable)viewHolder.iv_send_status.getBackground();
					anim.start();*/
				}
			}
			
		}else{ //+++++++++++++++  加载对方的消息
			
			MessageWrapper mw = pmm.messages.get(item.userid);
			if(mw!=null && mw.headimageurl!=null && mw.headimageurl.length()>0){
				imageLoader.displayImage(mw.headimageurl, viewHolder.iv_pic, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 4);
				//viewHolder.iv_pic.setImageUrl(R.drawable.temp_local_icon, 4, my_headimagurl, true);
			}else{
				viewHolder.iv_pic.setImageResource(R.drawable.moren_touxiang);
			}
			
			viewHolder.rl_record_media.setBackground(R.drawable.paopao_ziji);
			viewHolder.rl_video_attach_audio.setBackground(R.drawable.paopao_ziji_ms);
			
			Gson gson = new Gson();
			Log.e(TAG, "item.r_msg:" + gson.toJson(item.r_msg, MyMessage.class));
			
			//私信类型 --- 1代表纯文字  2代表日记  3代表语音 4语音加文字
			//私信类型 --- 1文字 2语音，3日记，4语音加文字
			if(item.r_msg.privmsg!=null){
				if("1".equals(item.r_msg.privmsg.privmsg_type)){
					hasDiary = false;
					hasAudio = false;
					hasText = true;
				}else if("2".equals(item.r_msg.privmsg.privmsg_type)){
					hasDiary = false;
					hasAudio = true;
					hasText = false;
				}else if("3".equals(item.r_msg.privmsg.privmsg_type)){
					hasDiary = true;
					if(item.r_msg.privmsg!=null && item.r_msg.privmsg.diaries!=null){
						if( item.r_msg.privmsg.diaries!=null){
							if(getAttachUrl(item.r_msg.privmsg.diaries, "2", "0")!=null){
								hasAudio = true;
							}else{
								hasAudio = false;
							}
							
							
							if(getTxtContent(item.r_msg.privmsg.diaries)!=null){
								hasText = true;
							}else{
								hasText = false;
							}

						}else{
							hasAudio = false;
							hasText = false;
						}
						
						
					}else{
						hasAudio = false;
						hasText = false;
					}
				}else if("4".equals(item.r_msg.privmsg.privmsg_type)){
					hasDiary = false;
					hasAudio = true;
					hasText = true;
				}else{
					Log.e(TAG, "item.r_msg.privmsg.privmsg_type is null");
					hasDiary = false;
					hasAudio = false;
					hasText = true;
				}	
			}else{
				Log.e(TAG, "item.r_msg.privmsg is null");
				hasDiary = false;
				hasAudio = false;
				hasText = true;
			}
			

			
			if(hasDiary && item.r_msg!=null && item.r_msg.privmsg!=null && item.r_msg.privmsg.diaries!=null){//){
				setMessageItem(imageLoader, viewHolder, hasDiary, getDiaryCoverUrl(item.r_msg.privmsg.diaries), getDiaryType(item.r_msg.privmsg.diaries), getVideoPlaytime(item.r_msg.privmsg.diaries), hasAudio, getAudioPlaytime(item.r_msg.privmsg.diaries), hasText, getTxtContent(item.r_msg.privmsg.diaries));
			}else{
				setMessageItem(imageLoader, viewHolder, false, null, null, null, hasAudio, item.r_msg.privmsg.playtime, hasText, item.r_msg.privmsg.content);
			}

		}
		
		if(item.onDownloading){
			viewHolder.pb_video_progress.setVisibility(View.VISIBLE);
			Log.e(TAG, "=friendsSession percent: " + item.percent);
			viewHolder.pb_video_progress.setProgress(item.percent);
		}else{
			viewHolder.pb_video_progress.setVisibility(View.GONE);
		}
		
		viewHolder.rl_record_media.setOnLongClickListener(this);
		viewHolder.rl_record_media.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(item.send){
					if(item.s_msg.localaudiopath!=null && !item.s_msg.localaudiopath.equals("")){
						Log.e(TAG, "paly :" + Environment.getExternalStorageDirectory() + item.s_msg.localaudiopath);
						viewHolder.rl_record_media.setAudio(item.s_msg.localaudiopath, 4);
						//viewHolder.rl_record_media.playAudio(Environment.getExternalStorageDirectory() + item.s_msg.localaudiopath);
					}
					
				}else{
					if(item!=null && item.r_msg!=null && item.r_msg.privmsg!=null && item.r_msg.privmsg.audiourl!=null && !item.r_msg.privmsg.audiourl.equals("")){
						Log.e(TAG, "paly :" + item.r_msg.privmsg.audiourl);
						viewHolder.rl_record_media.setAudio(item.r_msg.privmsg.audiourl, 4);
						//viewHolder.rl_record_media.playAudio(item.r_msg.privmsg.audiourl);
					}
					
				}
				
			}
		});
		
		
		viewHolder.rl_video_attach_audio.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(item.send){
					if(item.s_msg!=null && item.s_msg.diaries!=null){
						viewHolder.rl_video_attach_audio.setAudio(getAttachUrl(item.s_msg.diaries, "2", "0"), 4 );
					}
					
				}else{
					if(item!=null && item.r_msg!=null && item.r_msg.privmsg!=null && item.r_msg.privmsg.diaries!=null){
						viewHolder.rl_video_attach_audio.setAudio(getAttachUrl(item.r_msg.privmsg.diaries, "2", "0"), 4 );
					}
					
				}
				
			}
		});
		
		viewHolder.rl_video.setOnLongClickListener(this);
		
		//viewHolder.rl_video
		//viewHolder.iv_video_play
		viewHolder.rl_video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e(TAG, "viewHolder.rl_video.setOnClickListener onClick");
				Gson gson = new Gson();
				//Log.e(TAG, "item.r_msg:" + gson.toJson(item.r_msg, MyMessage.class));
				
				// TODO Auto-generated method stub
				MyDiary diaries = null;
				if(!item.send && item.r_msg!=null && item.r_msg.privmsg!=null && item.r_msg.privmsg.diaries!=null && item.r_msg.privmsg.diaries.diaryid!=null){
					diaries = item.r_msg.privmsg.diaries;
				}
				
				if(item.send && item.s_msg!=null && item.s_msg.diaries!=null && item.s_msg.diaries.diaryid!=null){
					diaries = item.s_msg.diaries;
				}
				
				if(diaries!=null){

					String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
					MediaValue mv = null;
					DiaryType dt = diaries.getDiaryType();
					//DiaryType dt = DiaryType.TEXT;
					if(DiaryType.VEDIO.equals(dt)){
						String videoUrl = diaries.getVideoUrl();
						//String videoUrl = "/sdcard/DCIM/xunzhaoshijian.rmvb";
						
				        mv  = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, videoUrl);
				        if (MediaValue.checkMediaAvailable(mv)) {
				        	//hit
				        	Uri name = Uri.parse("file://" + Environment.getExternalStorageDirectory() + mv.path);
					         //String type = "video/mp4";
					        String type = "video/*";
					        Log.e(TAG, "start intent - uri:" + name.toString());
					        Intent intent = new Intent(Intent.ACTION_VIEW);
				        	intent.setDataAndType(name, type);				   
					        context.startActivity(intent);
				        }else{
				        	//not hit
							PrivateCommonMessage pcm = chatMsgList.get(position);
							String Key = MD5.encode((uid+videoUrl).getBytes());
							String path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/video/" + Key;
							File file = new File(path);
							if(!file.getParentFile().exists()){
								file.getParentFile().mkdirs();
							}
							BackgroundDownloader downloader = new BackgroundDownloader(
									pcm,
									videoUrl,
									file,
									5);
							downloader.start();
				        	
				        }
				        
					}else if(DiaryType.AUDIO.equals(dt)){
						String audioUrl = diaries.getLongRecUrl();
						//String audioUrl = "http://192.168.100.111:8080/looklook/audio_pub/audio_source/2013/07/15/115350802b67ce848a46ffa0a2f19aaeb7ddc6.mp4";
						
						mv  = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, audioUrl);
						if (MediaValue.checkMediaAvailable(mv)) {
							//AudioPlayerHelper.getInstance().play(audioUrl);
				        	Uri name = Uri.parse("file://" + Environment.getExternalStorageDirectory() + mv.path);
					         //String type = "video/mp4";
					        String type = "audio/*";
					        Log.e(TAG, "start intent - uri:" + name.toString());
					        Intent intent = new Intent(Intent.ACTION_VIEW);
				        	intent.setDataAndType(name, type);				   
					        context.startActivity(intent);
						} else {
							PrivateCommonMessage pcm = chatMsgList.get(position);
							String Key = MD5.encode((uid+audioUrl).getBytes());
							String path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/audio/" + Key;
							File file = new File(path);
							if(!file.getParentFile().exists()){
								file.getParentFile().mkdirs();
							}
							BackgroundDownloader downloader = new BackgroundDownloader(
				        			chatMsgList.get(position), 
				        			audioUrl, 
				        			file, //★★★
				        			4);
				        	downloader.start();

						}
						
					}else if(DiaryType.PICTURE.equals(dt)){
						String imageUrl = getDiaryCoverUrl(diaries);
						mv  = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, imageUrl);

						if (MediaValue.checkMediaAvailable(mv)) {
							//AudioPlayerHelper.getInstance().play(audioUrl);
				        	Uri name = Uri.parse("file://" + Environment.getExternalStorageDirectory() + mv.path);
					         //String type = "video/mp4";
					        String type = "image/*";
					        Log.e(TAG, "start intent - uri:" + name.toString());
					        Intent intent = new Intent(Intent.ACTION_VIEW);
				        	intent.setDataAndType(name, type);				   
					        context.startActivity(intent);
						}
						
					}else if(DiaryType.TEXT.equals(dt)){/*

						String txtContent = getTxtContent(diaries);
						TextView tv = new TextView(context);
						tv.setText(txtContent);
						tv.setTextColor(Color.rgb(255, 255, 255));
						tv.setTextSize(20);
						final PopupWindow popupWindow = new PopupWindow(tv, LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
						
						tv.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								popupWindow.dismiss();
							}
							
						});
					*/}
				}
				
			}
		});
		
		
		viewHolder.iv_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "click head position：" + position, Toast.LENGTH_SHORT).show();
				launchOtherHomePage(item.userid, other_nick);

			}
		});
		
		convertView.setTag(viewHolder);
		
		return convertView;
	}

	class ViewHolder {
		boolean send;
		
		RelativeLayout ll_msg;

		ImageView iv_pic;
		
		RelativeLayout rl_all;
		
		//视频
		RelativeLayout rl_video;       //视频私信条目layout
		TextView tv_video_txt_media; //纯文本日记使用到
		WebImageView iv_video_play; //视频截屏
		TextView tv_video_len;
		ImageView iv_video_screen; //播放按钮
		ProgressBar pb_video_progress; //日记下载进度
		
		//视频-音频评论
		TackView rl_video_attach_audio; //视频-音频评论layout
		//TextView tv_record_len;  //音频长度
		
		//视频-文字评论
		TextView tv_video_attach_txt;
		
		//音频
		RelativeLayout rl_record;
		
		TackView rl_record_media;
		//TextView tv_record_audio_len;
		
		//音频-文字评论
		TextView tv_audio_attach_txt;
		
		//纯文本
		TextView tv_txt_media;
		
		//会话时间
		TextView tv_msg_date;
		
/*		ImageView iv_audio_attach_loading;
		ImageView iv_audio_attach_fail;*/
		ImageView iv_send_status;
	}

	@Override
	public int getItemViewType(int position) {
		PrivateCommonMessage entity = chatMsgList.get(position);
		if (entity.send) {
			return IMsgViewType.IMVT_TO_MSG;
		} else {
			return IMsgViewType.IMVT_COM_MSG;
		}
		
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	private void launchOtherHomePage(String userid, String other_nick) {
		if(userid!=null){
			Intent intent = new Intent(context, HomepageOtherDiaryActivity.class);
			intent.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_USERID, userid);
			if(other_nick!=null){
				intent.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_NICKNAME, other_nick);
			}
			context.startActivity(intent);
		}
		
	}
	
	
	private void setMessageItem(ImageLoader imageLoader, ViewHolder viewHolder, boolean hasDiary, String DiaryCoverUrl, String DiaryType, String DiaryLen, 
			boolean hasAudio, String AudioLen,
			boolean hasText, String TxtContent){
		Log.e(TAG, "setMessageItem - hasDiary:" + hasDiary + ", DiaryCoverUrl:" + DiaryCoverUrl + ", DiaryType:" + DiaryType + ", VideoLen:" + DiaryLen +
				", hasAudio:" + hasAudio + ", AudioLen:" + AudioLen + ", hasText:" + hasText + ", TxtContent:" + TxtContent);
		
		
		if(hasDiary){ //有日记，可能是视频、音频、图片和文本
			viewHolder.rl_video.setVisibility(View.VISIBLE);
			viewHolder.rl_record.setVisibility(View.GONE);
			viewHolder.tv_txt_media.setVisibility(View.GONE);
			viewHolder.iv_video_screen.setVisibility(View.GONE);
			viewHolder.tv_video_txt_media.setVisibility(View.GONE);
			viewHolder.tv_audio_attach_txt.setVisibility(View.GONE);
			
			if("1".equals(DiaryType) || "3".equals(DiaryType)){ //日记主内容有图片，视频或图片为主内容
				viewHolder.iv_video_play.setVisibility(View.VISIBLE);
				viewHolder.iv_video_play.setImageUrl(R.drawable.temp_shipin, 4, DiaryCoverUrl, false);
				//imageLoader.displayImage(DiaryCoverUrl, viewHolder.iv_video_play, diary_pic_options, uid, 4);
				viewHolder.tv_video_len.setVisibility(View.VISIBLE);
				if("1".equals(DiaryType)){
					if(DiaryLen!=null){
						viewHolder.tv_video_len.setText(DiaryLen);
					}else{
						viewHolder.tv_video_len.setText("");
					}
					viewHolder.iv_video_screen.setVisibility(View.VISIBLE);
				}else{
					viewHolder.tv_video_len.setText("");
				}
			}else if("2".equals(DiaryType)){
				viewHolder.tv_video_len.setVisibility(View.GONE);
				viewHolder.iv_video_play.setVisibility(View.VISIBLE);
				//viewHolder.iv_video_play.setImageUrl(R.drawable.cidai_2, 4, DiaryCoverUrl, false);
				viewHolder.iv_video_play.setImageResource(R.drawable.cidai_2);
			}else{ //其它，如文本等
				viewHolder.tv_video_len.setVisibility(View.GONE);
				viewHolder.tv_video_txt_media.setVisibility(View.VISIBLE);
				viewHolder.iv_video_play.setVisibility(View.GONE);
				//viewHolder.tv_video_txt_media.setText(DiaryCoverUrl);
				if(DiaryCoverUrl!=null && !DiaryCoverUrl.equals("")){
					expressionView.replacedExpressions(DiaryCoverUrl, viewHolder.tv_video_txt_media);
				}else{
					viewHolder.tv_video_txt_media.setText("(空白)");
				}
				
			}


			
			/*viewHolder.tv_video_attach_txt.setVisibility(View.GONE);
			viewHolder.rl_video_attach_audio.setVisibility(View.GONE);*/
			
			if(hasAudio){
				viewHolder.rl_video_attach_audio.setVisibility(View.VISIBLE);
				//viewHolder.tv_record_len.setText(AudioLen);
				if(AudioLen!=null){
					viewHolder.rl_video_attach_audio.setPlaytime(AudioLen); //私信为4
				}else{
					viewHolder.rl_video_attach_audio.setPlaytime("");
				}

				
			}else{
				viewHolder.rl_video_attach_audio.setVisibility(View.GONE);
			}
			
			if(hasText && DiaryType!=null){ //日记有主内容，
				viewHolder.tv_video_attach_txt.setVisibility(View.VISIBLE);
				expressionView.replacedExpressions("  " + TxtContent, viewHolder.tv_video_attach_txt);
			}else{//否则不显示语音描述
				viewHolder.tv_video_attach_txt.setVisibility(View.GONE);
			}
			
		}else{ //无日记，有语音，可能有文本
			viewHolder.rl_video.setVisibility(View.GONE);
			viewHolder.tv_video_attach_txt.setVisibility(View.GONE);

			if(hasAudio){
				//有语音，可能有文本
				viewHolder.rl_record.setVisibility(View.VISIBLE);
				viewHolder.tv_txt_media.setVisibility(View.GONE);
				
				//viewHolder.tv_record_audio_len.setText(AudioLen);
				viewHolder.rl_record_media.setVisibility(View.VISIBLE);

				if(AudioLen!=null){
					viewHolder.rl_record_media.setPlaytime(AudioLen);	
				}

				
				if(hasText){
					viewHolder.tv_audio_attach_txt.setVisibility(View.VISIBLE);
					expressionView.replacedExpressions("  " + TxtContent, viewHolder.tv_audio_attach_txt);
					//viewHolder.tv_audio_attach_txt.setText(TxtContent);
				}else{
					viewHolder.tv_audio_attach_txt.setVisibility(View.GONE);
				}
				
			}else{ //仅文本
				viewHolder.rl_record.setVisibility(View.GONE);
				viewHolder.tv_txt_media.setVisibility(View.VISIBLE);
				viewHolder.tv_audio_attach_txt.setVisibility(View.GONE);
				//viewHolder.tv_txt_media.setText(TxtContent);

				expressionView.replacedExpressions("  " + TxtContent, viewHolder.tv_txt_media);
			}
		}

		

	}
	
	/**
	 *   @param myDiary 日记结构
	 *   @param attachtype 附件类型，1视频、2音频、3图片、4文字（暂不支持）
	 *   @param attachlevel 附件级别, 1主内容、0辅内容 null不检查
	 *   @return String 根据参数返回命中的附件的第一个url地址
	 * */
	private String getDiaryCoverUrl(MyDiary myDiary){
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachtype) && "1".equals(attach.attachlevel)){

					if(attach.videocover!=null && !attach.videocover.equals("")){
						return attach.videocover;
					}
					
				}else if("3".equals(attach.attachtype) && "1".equals(attach.attachlevel)){
					 if(attach.attachimage!=null && attach.attachimage.length>0){

						 for(MyAttachImage item:attach.attachimage){
							 if(item!=null && "0".equals(item.imagetype)){
								 return item.imageurl;
							 }
						 }
						 
						 return attach.attachimage[0].imageurl;
					 }
				}

				
			}
		}
		
/*		//若没有视频封面，返回图片附件
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("3".equals(attach.attachtype) && "1".equals(attach.attachlevel)){
					if(attach.attachimage!=null && attach.attachimage.length>0){
						if(attach.attachimage[0].imageurl!=null && !attach.attachimage[0].imageurl.equals("")){
							return attach.attachimage[0].imageurl;
						}
						
					}

				}

				
			}
		}*/
		
		//若没有图片附件，返回文本附件
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("4".equals(attach.attachtype)){

					if(attach.content!=null && !attach.content.equals("")){
						return attach.content;
					}
					
				}

				
			}
		}
		return null;
	}
	
	/**
	 *   @param myDiary 日记结构
	 *   @param attachtype 附件类型，1视频、2音频、3图片、4文字（暂不支持）
	 *   @param attachlevel 附件级别, 1主内容、0辅内容 null不检查
	 *   @return diaryAttach 根据参数返回命中的附件的diaryAttach
	 * */
	private String getTxtContent(MyDiary myDiary){
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("4".equals(attach.attachtype)){
					if(attach.content!=null && !attach.content.equals("")){
						return attach.content;
					}
					
				}

				
			}
		}
		return null;
	}
	
	private String getAudioPlaytime(MyDiary myDiary){
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("2".equals(attach.attachtype)){
					
					return attach.playtime + "''";
				}

				
			}
		}
		return null;
	}
	
	private String formatPlayTime(String time_seconds){
		StringBuilder ret = new StringBuilder();
		try{
			double d = Double.parseDouble(time_seconds);
			int i = (int) (d+0.5);
			int h = i/3600;
			int m = i%3600/60;
			int s = i%60;
			if(h<10){
				ret.append("0" + h);
			}else{
				ret.append(h);
			}
			
			ret.append(":");
			
			if(m<10){
				ret.append("0" + m);
			}else{
				ret.append(m);
			}
			
			ret.append(":");
			
			if(s<10){
				ret.append("0" + s);
			}else{
				ret.append(s);
			}

			
		}catch(Exception e){
			e.printStackTrace();
			ret.append(time_seconds);
		}
		
		return ret.toString();
	}
	
	private String getVideoPlaytime(MyDiary myDiary){
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachtype)){
					
					return formatPlayTime(attach.playtime);
				}

				
			}
		}
		return null;
	}
	
	/**
	 *   @param myDiary 日记结构
	 *   @param attachtype 附件类型，1视频、2音频、3图片、4文字（暂不支持）
	 *   @param attachlevel 附件级别, 1主内容、0辅内容 null不检查
	 *   @return String 根据参数返回命中的附件的第一个url地址
	 * */
	private String getAttachUrl(MyDiary myDiary, String attachtype, String attachlevel){
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if(attachtype.equals(attach.attachtype)){
					
					if(attachlevel==null || attachlevel.equals(attach.attachlevel)){
						if(attachtype.equals("1")){ //1视频
							MyAttachVideo[] attachvideo=attach.attachvideo;
							if(attachvideo!=null&&attachvideo.length>0){
								//TODO 是否需要根据隐私设置给不同的url
								if(attachvideo[0].playvideourl!=null && !attachvideo[0].playvideourl.equals("")){
									return attachvideo[0].playvideourl;
								}
								
							}
						}else if(attachtype.equals("2")){ //2音频
							MyAttachAudio[] attachaudio=attach.attachaudio;
							if(attachaudio!=null&&attachaudio.length>0){
								//TODO 是否需要根据隐私设置给不同的url
								if(attachaudio[0].audiourl!=null && !attachaudio[0].audiourl.equals("")){
									return attachaudio[0].audiourl;
								}
							}
						}else if(attachtype.equals("3")){ //3图片
							MyAttachImage[] attachImage=attach.attachimage;
							if(attachImage!=null&&attachImage.length>0){
								//TODO 是否需要根据隐私设置给不同的url
								if(attachImage[0].imageurl!=null && !attachImage[0].imageurl.equals("")){
									return attachImage[0].imageurl;
								}

							}
						}

						
					}
				}

				
			}
		}
		return null;
	}
	
	
	/**
	 *   @param myDiary 日记结构
	 *   @param attachtype 附件类型，1视频、2音频、3图片、4文字（暂不支持）
	 *   @param attachlevel 附件级别, 1主内容、0辅内容 null不检查
	 *   @return String "1", 视频;  "2" 音频; "3"图片; "4"文本
	 * */
	private String getDiaryType(MyDiary myDiary){
		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];

				
				if("1".equals(attach.attachlevel)){
					if("1".equals(attach.attachtype)){ //1视频
						return "1";
					}else if("2".equals(attach.attachtype)){ //2音频
						return "2";
					}else if("3".equals(attach.attachtype)){ //3图片
						return "3";
					}else{
						return "4";
					}

					
				}
			

				
			}
		}
		return null;
	}


	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		listView.performLongClick();
		return false;
	}
	
	

}
