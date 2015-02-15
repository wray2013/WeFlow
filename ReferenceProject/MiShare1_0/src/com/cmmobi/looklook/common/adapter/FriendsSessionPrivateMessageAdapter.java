package com.cmmobi.looklook.common.adapter;

import java.util.Date;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.platform.comapi.map.n;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyMessage;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.AudioRecogniseView;
import com.cmmobi.looklook.common.view.ExpressionView;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.PrivateSendMessage;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class FriendsSessionPrivateMessageAdapter extends BaseAdapter  {

	private static final String TAG = "FriendsSessionPrivateMessageAdapter";

	private final int SESSION_PERIOD_THRESHOLD = 2*60*1000;
	
	public static final int HANDLER_FLAG_MSG_DELETE = 0x1638376a;
	public static final int HANDLER_FLAG_MSG_RETRY = 0x1638376b;

	
	public static final int HANDLER_DIARLY_PROGRESS_UPDATE = 0x81214001;
	
	private Context context;
	private Handler handler;

	private LinkedList<PrivateCommonMessage> chatMsgList;

	private LayoutInflater inflater;
	
	//使用开源的webimageloader
	public static DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	
	private long last_item_time;

	private String other_nick;

	protected PopupWindow popupWindow;

	private RotateAnimation animation;

	private String uid;

	private ListView listView;

	int[] icons = { R.drawable.wave1,
			R.drawable.wave2, R.drawable.wave3, };

	private boolean isFriends = true;
	private String other_userid = "";
	
	private AccountInfo ai;
	
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
		int IMVT_ADD_MSG = 2;
	}

	
	public FriendsSessionPrivateMessageAdapter(ListView listView, Context context, Handler handler, LinkedList<PrivateCommonMessage> list, String other_nick,String other_userid) {
		this.listView = listView;
		this.context = context;
		this.chatMsgList = list;
		this.handler = handler;
		this.inflater = LayoutInflater.from(context);
		this.other_nick = other_nick;
		this.other_userid = other_userid;
		
		this.uid = ActiveAccount.getInstance(context).getLookLookID();
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.icon_head_default)
		.showImageForEmptyUri(R.drawable.icon_head_default)
		.showImageOnFail(R.drawable.icon_head_default)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
//		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	
	}

	
	@Override
	public int getCount() {
		// 不是好友 item + 1 
		return chatMsgList.size() + (isFriends? 0 : 1);
	}

	@Override
	public Object getItem(int position) {
		if(position < chatMsgList.size()){
			return chatMsgList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// 不是朋友，显示加好友显示
		if(!isFriends && position == chatMsgList.size()){
			View addFriendsView = inflater.inflate(R.layout.private_message_add_friends, null);
			TextView tvAddFriends = (TextView) addFriendsView.findViewById(R.id.tv_add_friends);
			tvAddFriends.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new XEditDialog.Builder(v.getContext())
					.setTitle(R.string.xeditdialog_title)
					.setPositiveButton(R.string.send, new OnClickListener() {
						@Override
						public void onClick(View v) {
							//加好友
							Requester3.addFriend(handler, other_userid, v.getTag().toString());
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.create().show();
				}
			});
			return addFriendsView;
		}
		
		
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
			
			viewHolder.rl_record_media = (TackView) convertView.findViewById(R.id.rl_record_media);
			viewHolder.rl_record_media.setSoundIcons(icons);
			
			viewHolder.tv_msg_date =  (TextView) convertView.findViewById(R.id.tv_msg_date);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		
		//UI show
		ActiveAccount aa = ActiveAccount.getInstance(context);			
		ai = AccountInfo.getInstance(aa.getLookLookID());
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
		
		viewHolder.tv_msg_date.setText(DateUtils.getMyCommonListShowDate(new Date(item.create_time)));
		//viewHolder.tv_msg_date.setText(DateUtils.getNormlDate(item.create_time));
		
		Log.e(TAG, "send: " + item.send + " " + DateUtils.getNormlDate(item.create_time) + " item:" + item.getContent());
		
		
		if(item.send){ //+++++++++++++++++++ 加载自己的消息
			
    		if(my_headimagurl!=null && my_headimagurl.length()>0){
    			imageLoader.displayImage(my_headimagurl, viewHolder.iv_pic, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 4);
				//viewHolder.iv_pic.setImageUrl(R.drawable.temp_local_icon, 4, my_headimagurl, true);
			}else{
				viewHolder.iv_pic.setImageResource(R.drawable.icon_head_default);
			}
			
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
				}else if("4".equals(item.s_msg.privatemsgtype)){
					hasDiary = false;
					hasAudio = true;
					hasText = true;
				}else{
					Log.e(TAG, "item.s_msg.privatemsgtype is null");
				}
				
				setMessageItem(imageLoader, viewHolder, false, null, null, null, hasAudio, item.s_msg.playtime, hasText, item.s_msg.content,item.s_msg.localaudiopath);
				if(item.s_msg.status==SEND_MSG_STATUS.ALL_DONE){
					viewHolder.iv_send_status.setVisibility(View.GONE);
					viewHolder.iv_send_status.clearAnimation();
				}else if(item.s_msg.status==SEND_MSG_STATUS.RIA_FAIL || item.s_msg.status==SEND_MSG_STATUS.UPLOAD_FAIL){
					viewHolder.iv_send_status.setVisibility(View.VISIBLE);
					viewHolder.iv_send_status.setImageResource(R.drawable.del_shibai);
					viewHolder.iv_send_status.clearAnimation();
					viewHolder.iv_send_status.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							handler.obtainMessage(HANDLER_FLAG_MSG_RETRY, position).sendToTarget();
						}
					});
				}else{
					viewHolder.iv_send_status.setVisibility(View.VISIBLE);
					viewHolder.iv_send_status.setImageResource(R.drawable.vshare_loading);
					animation = new RotateAnimation(0, 360,
							RotateAnimation.RELATIVE_TO_SELF, 0.5f,
							RotateAnimation.RELATIVE_TO_SELF, 0.5f);
					animation.setInterpolator(new LinearInterpolator());
					animation.setDuration(500);
					animation.setRepeatCount(Animation.INFINITE);
					animation.setFillAfter(true);
					viewHolder.iv_send_status.clearAnimation();
					viewHolder.iv_send_status.startAnimation(animation); 
				}
			}
			
		}else{ //+++++++++++++++  加载对方的消息
			
			MessageWrapper mw = pmm.get(item.userid);
			if(mw!=null && mw.headimageurl!=null && mw.headimageurl.length()>0){
				imageLoader.displayImage(mw.headimageurl, viewHolder.iv_pic, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 4);
				//viewHolder.iv_pic.setImageUrl(R.drawable.temp_local_icon, 4, my_headimagurl, true);
			}else{
				viewHolder.iv_pic.setImageResource(R.drawable.icon_head_default);
			}
			
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
			
			setMessageItem(imageLoader, viewHolder, false, null, null, null, hasAudio, item.r_msg.privmsg.playtime, hasText, item.r_msg.privmsg.content,item.r_msg.privmsg.audiourl);

		}
		
		
		viewHolder.rl_record_media.setPrivateMsgData(item);
		viewHolder.rl_record_media.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		viewHolder.rl_record_media.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewHolder.rl_record_media.readAudioState();
				
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
		
		
		viewHolder.iv_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContactManager friendsListContactManager= ai.friendsListName;
				WrapUser currUserInfo = friendsListContactManager.findUserByUserid(other_userid);
				
				if(currUserInfo == null){
					// 不是好友
					if(other_userid.equals(ai.serviceUser.userid)){
//						launchOtherHomePage(item.userid);
					}else{
						new XEditDialog.Builder(v.getContext())
						.setTitle(R.string.xeditdialog_title)
						.setPositiveButton(R.string.send, new OnClickListener() {
							@Override
							public void onClick(View v) {
								//加好友
								Requester3.addFriend(handler, other_userid, v.getTag().toString());
							}
						})
						.setNegativeButton(android.R.string.cancel, null)
						.create().show();
					}
				}else{
					// 是好友
					launchOtherHomePage(item.userid);
				}
			}
		});
		
		convertView.setTag(viewHolder);
		
		return convertView;
	}

	class ViewHolder {
		boolean send;
		
		//会话时间
		TextView tv_msg_date;
		RelativeLayout ll_msg;
		ImageView iv_pic;
		RelativeLayout rl_all;
		TackView rl_record_media;
		//纯文本
		TextView tv_txt_media;
		ImageView iv_send_status;
		
	}

	@Override
	public int getItemViewType(int position) {
		
		if(!isFriends && position == chatMsgList.size()){
			return IMsgViewType.IMVT_ADD_MSG;
		}
		
		PrivateCommonMessage entity = chatMsgList.get(position);
		if (entity.send) {
			return IMsgViewType.IMVT_TO_MSG;
		} else {
			return IMsgViewType.IMVT_COM_MSG;
		}
		
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	private void launchOtherHomePage(String userid) {
		if(userid!=null){
			Intent intent = new Intent(context, OtherZoneActivity.class);
			intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, userid);
			context.startActivity(intent);
		}
		
	}
	
	
	private void setMessageItem(ImageLoader imageLoader, ViewHolder viewHolder, boolean hasDiary, String DiaryCoverUrl, String DiaryType, String DiaryLen, 
			boolean hasAudio, String AudioLen,
			boolean hasText, String TxtContent,String localaudiopath){
		Log.e(TAG, "setMessageItem - hasDiary:" + hasDiary + ", DiaryCoverUrl:" + DiaryCoverUrl + ", DiaryType:" + DiaryType + ", VideoLen:" + DiaryLen +
				", hasAudio:" + hasAudio + ", AudioLen:" + AudioLen + ", hasText:" + hasText + ", TxtContent:" + TxtContent);
		
		if(hasAudio){
			//有语音，可能有文本
			viewHolder.tv_txt_media.setVisibility(View.GONE);
			//viewHolder.tv_record_audio_len.setText(AudioLen);
			viewHolder.rl_record_media.setVisibility(View.VISIBLE);
//			viewHolder.view_audio_recognise.setVisibility(View.VISIBLE);
			try{
				if(AudioLen!=null && !AudioLen.isEmpty()){
					viewHolder.rl_record_media.setPlaytime(AudioLen);	
					
					int time = Integer.parseInt(AudioLen.replace("''", ""));
					Log.e("TackView", "time = " + time);
					if(time != 0 && time <= 30){
	//					float level = ((time - 1) / 10 + 1) / 3f;
	//					viewHolder.rl_record_media.setBackgroudLevel(level, 110);
	//					viewHolder.rl_record_media.requestLayout();
					}
				}else{
					viewHolder.rl_record_media.setPlaytime("");
				}				
				viewHolder.rl_record_media.setRecogniPath(localaudiopath);
			
			}catch (Exception e){
				e.printStackTrace();
			}
			
			if(hasText){
				viewHolder.tv_txt_media.setVisibility(View.VISIBLE);
				FriendsExpressionView.replacedExpressions("  " + TxtContent, viewHolder.tv_txt_media);
//				viewHolder.tv_txt_media.setText(TxtContent);
			}else{
				viewHolder.tv_txt_media.setVisibility(View.GONE);
			}
			
		}else{ //仅文本
			viewHolder.rl_record_media.setVisibility(View.GONE);
			viewHolder.tv_txt_media.setVisibility(View.VISIBLE);
//			viewHolder.tv_txt_media.setText(TxtContent);

			FriendsExpressionView.replacedExpressions("  " + TxtContent, viewHolder.tv_txt_media);
		}

		

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


	
	/**
	 * 设置adapter的显示朋友状态
	 * @param true
	 */
	public void setFriendsState(boolean b) {
		isFriends = b;
	}
	



}
