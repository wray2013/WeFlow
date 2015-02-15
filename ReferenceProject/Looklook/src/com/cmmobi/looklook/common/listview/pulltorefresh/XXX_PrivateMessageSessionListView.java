package com.cmmobi.looklook.common.listview.pulltorefresh;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsActivity;
import com.cmmobi.looklook.activity.FriendsStrangerActivity;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;

/**
 * @author zhangwei
 */
public class PrivateMessageSessionListView extends AbsRefreshView<List<PrivateCommonMessage>> {

	private static final String TAG="PrivateMessageSessionListView";
	private final int SESSION_PERIOD_THRESHOLD = 2*60*1000;
	
	private Context context;
	public Handler handler;
	
	private WebImageView iv_pic;
	
	//视频
	private RelativeLayout rl_video;       //视频私信条目layout
	private WebImageView iv_video_screen; //视频截屏
	private TextView tv_video_len;
	
	//视频-音频评论
	private RelativeLayout rl_video_attach_audio; //视频-音频评论layout
	//private TextView tv_record_len;  //音频长度
	
	//视频-文字评论
	private TextView tv_video_attach_txt;
	
	//音频
	private RelativeLayout rl_record;
	//private TextView tv_record_audio_len;
	
	//音频-文字评论
	private TextView tv_audio_attach_txt;
	
	//纯文本
	private TextView tv_txt_media;
	
	//会话时间
	private TextView tv_msg_date;
	
/*	private ImageView iv_audio_attach_loading;
	private ImageView iv_audio_attach_fail;*/
	private ImageView  iv_audio_send;
	
	private long last_item_time;
	
	public PrivateMessageSessionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PrivateMessageSessionListView(Context context) {
		super(context);
		init(context);
	}
	
	LinearLayout root;
	
	public void setParams(Handler handler){
		this.handler=handler;
	}
	
	private void init(Context context){
		last_item_time = 0;
		this.context = context;
		root=(LinearLayout) inflater.inflate(R.layout.activity_singlelistview, null);
		addChild(root,1);
	}

	@Override
	protected void initContent(List<PrivateCommonMessage> items) {
		root.removeAllViews();
		addView(items);
		
	}
	

	@Override
	protected void addView(List<PrivateCommonMessage> items) {
		if(null==items||0==items.size()){
			Log.e(TAG, "items is null");
			return;
		}
		
		ActiveAccount aa = ActiveAccount.getInstance(context);
		if(!aa.isLogin()){
			return;
		}
		
		AccountInfo ai = AccountInfo.getInstance(aa.getLookLookID());
		String my_headimagurl = ai.headimageurl;
		
		PrivateMessageManager pmm = ai.privateMsgManger;
		
		View v;

		//other:
		Log.d(TAG, "items="+items.size());
		for(int i=0; i<items.size(); i++){
			final PrivateCommonMessage item = items.get(i);
			if(item.send){//left is other, right is mine
				v = inflater.inflate(R.layout.list_item_friends_private_message_right, null);
				iv_audio_attach_loading = (ImageView)v.findViewById(R.id.iv_audio_attach_loading);
				iv_audio_attach_fail = (ImageView)v.findViewById(R.id.iv_audio_attach_fail);
			}else{
				v = inflater.inflate(R.layout.list_item_friends_private_message_left, null);
			}
			
			//UI pick
			iv_pic = (WebImageView) v.findViewById(R.id.iv_pic);
			
			tv_txt_media = (TextView) v.findViewById(R.id.tv_txt_media);
			
			rl_video = (RelativeLayout)v.findViewById(R.id.rl_video);
			iv_video_screen = (WebImageView) v.findViewById(R.id.iv_video_screen);
			tv_video_len = (TextView) v.findViewById(R.id.tv_video_len);
			rl_video_attach_audio = (RelativeLayout)v.findViewById(R.id.rl_video_attach_audio);
			//tv_record_len = (TextView) v.findViewById(R.id.tv_record_len);
			tv_video_attach_txt = (TextView) v.findViewById(R.id.tv_video_attach_txt);
			
			rl_record = (RelativeLayout)v.findViewById(R.id.rl_record);
			//tv_record_audio_len = (TextView) v.findViewById(R.id.tv_record_audio_len);
			tv_audio_attach_txt =  (TextView) v.findViewById(R.id.tv_audio_attach_txt);
			
			tv_msg_date =  (TextView) v.findViewById(R.id.tv_msg_date);
			
			//UI show
			boolean hasVideo = false;
			boolean hasAudio = false;
			boolean hasText = false;
			
			if(item.create_time-last_item_time>SESSION_PERIOD_THRESHOLD){
				tv_msg_date.setVisibility(View.VISIBLE);
				
			}else{
				tv_msg_date.setVisibility(View.GONE);
			}
			
			tv_msg_date.setText(DateUtils.getMyCommonShowDate(new Date(item.create_time)));
			last_item_time = item.create_time;
			
			
			if(item.send){ //+++++++++++++++++++ 加载自己的消息
				
				//iv_pic.setPortraiUrl(R.drawable.temp_local_icon, 1, my_headimagurl);
				iv_pic.setImageUrl(R.drawable.temp_local_icon, 1, my_headimagurl, true);
				if(item.s_msg!=null){
					//私信类型 --- 1代表纯文字  2代表日记  3代表语音 4语音加文字
					if("1".equals(item.s_msg.privatemsgtype)){
						hasVideo = false;
						hasAudio = false;
						hasText = true;
					}else if("2".equals(item.s_msg.privatemsgtype)){
						hasVideo = true;
						hasAudio = false;
						hasText = false;
					}else if("3".equals(item.s_msg.privatemsgtype)){
						hasVideo = false;
						hasAudio = true;
						hasText = false;
					}else if("4".equals(item.s_msg.privatemsgtype)){
						hasVideo = false;
						hasAudio = true;
						hasText = true;
					}
					
					if(item.s_msg.content!=null){
						setMessageItem(hasVideo, null, " ", hasAudio, " ", hasText, item.s_msg.content);
					}else{
						setMessageItem(false, null, null, false, null, true, " ");
					}
					
					if(item.s_msg.status==SEND_MSG_STATUS.ALL_DONE){
						iv_audio_attach_loading.setVisibility(GONE);
						iv_audio_attach_fail.setVisibility(GONE);
					}else if(item.s_msg.status==SEND_MSG_STATUS.RIA_FAIL || item.s_msg.status==SEND_MSG_STATUS.UPLOAD_FAIL){
						iv_audio_attach_loading.setVisibility(GONE);
						iv_audio_attach_fail.setVisibility(VISIBLE);
					}else{
						iv_audio_attach_loading.setVisibility(VISIBLE);
						iv_audio_attach_fail.setVisibility(GONE);
					}
				}
				
			}else{ //+++++++++++++++  加载对方的消息
				
				MessageWrapper mw = pmm.messages.get(item.userid);
				//iv_pic.setPortraiUrl(R.drawable.temp_local_icon, 1, mw.headimageurl);
				iv_pic.setImageUrl(R.drawable.temp_local_icon, 1, my_headimagurl, true);
				//私信类型 --- 1代表纯文字  2代表日记  3代表语音 4语音加文字
				if("1".equals(item.r_msg.privmsg)){
					hasVideo = false;
					hasAudio = false;
					hasText = true;
				}else if("2".equals(item.r_msg.privmsg)){
					hasVideo = true;
					if(item.r_msg.privmsg!=null){
						if( item.r_msg.privmsg.audiourl!=null){
							hasAudio = true;
						}else{
							hasAudio = false;
						}
						
						if( item.r_msg.privmsg.content!=null){
							hasText = true;
						}else{
							hasText = false;
						}
						
					}else{
						hasAudio = false;
						hasText = false;
					}
				}else if("3".equals(item.r_msg.privmsg)){
					hasVideo = false;
					hasAudio = true;
					hasText = false;
				}else if("4".equals(item.r_msg.privmsg)){
					hasVideo = false;
					hasAudio = true;
					hasText = true;
				}else{
					hasVideo = false;
					hasAudio = false;
					hasText = true;
				}
				
				if(item.r_msg.privmsg!=null){
					setMessageItem(hasVideo, null, "4:23", hasAudio, "17''", hasText, item.r_msg.privmsg.content);
				}else{
					setMessageItem(false, null, null, false, null, true, " ");
				}
			}
			


			
			//UI event
			v.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					//Toast.makeText(context, "item click:" + item.userid, Toast.LENGTH_SHORT).show();
					//handler.obtainMessage(FriendsActivity.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
				}
			});

			root.addView(v);
			
/*			div = new ImageView(context);
			div.setBackgroundResource(R.drawable.friends_list_line);
			root.addView(div,params);*/
		
		}
	}
	
	
	private void setMessageItem(boolean hasVideo, String VideoPicUrl, String VideoLen, 
			boolean hasAudio, String AudioLen,
			boolean hasText, String TxtContent){
		
		//视频
		//rl_video;       //视频私信条目layout
		//视频-音频评论
		//rl_video_attach_audio; //视频-音频评论layout
		//视频-文字评论
		//tv_video_attach_txt;
		
		//音频
		//rl_record;
		//音频-文字评论
        //tv_audio_attach_txt; 
        
		//纯文本
		//tv_txt_media;
		
		
		if(hasVideo){ //有视频，可能有音频和文本
			rl_video.setVisibility(View.VISIBLE);
			rl_record.setVisibility(View.GONE);
			tv_txt_media.setVisibility(View.GONE);
			
			iv_video_screen.setImageUrl(R.drawable.temp_shipin, 4, VideoPicUrl, false);
			
			tv_video_len.setText(VideoLen);
			
			if(hasAudio){
				rl_video_attach_audio.setVisibility(View.VISIBLE);
				//tv_record_len.setText(AudioLen);
				
			}else{
				rl_video_attach_audio.setVisibility(View.GONE);
			}
			
			if(hasText){
				tv_video_attach_txt.setVisibility(View.VISIBLE);
				tv_video_attach_txt.setText(TxtContent);
			}else{
				tv_video_attach_txt.setVisibility(View.GONE);
			}
			
		}else{ //无视频，有语音，可能有文本
			rl_video.setVisibility(View.GONE);
			
			if(hasAudio){
				rl_record.setVisibility(View.VISIBLE);
				tv_txt_media.setVisibility(View.GONE);
				
				//tv_record_audio_len.setText(AudioLen);
				
				if(hasText){
					tv_video_attach_txt.setVisibility(View.VISIBLE);
					tv_audio_attach_txt.setText(TxtContent);
				}else{
					tv_video_attach_txt.setVisibility(View.GONE);
				}
				
			}else{ //仅文本
				rl_record.setVisibility(View.GONE);
				tv_txt_media.setVisibility(View.VISIBLE);
				tv_txt_media.setText(TxtContent);
			}
		}

		

	}
	
}

