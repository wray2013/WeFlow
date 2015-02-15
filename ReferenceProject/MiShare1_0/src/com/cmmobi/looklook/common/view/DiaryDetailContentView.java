package com.cmmobi.looklook.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.VshareDetailActivity.VshareDetail;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MiShareinfo;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.fragment.FriendsContactsFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 日记详情的内容部分，包括转发和原创
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-12-16
 */
public class DiaryDetailContentView extends RelativeLayout implements Callback{
	
	private static String TAG = "DiaryDetailContentView";
	private Context context;
	
	public DiaryDetailContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public DiaryDetailContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public DiaryDetailContentView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private LayoutInflater inflater;
	
	//forward rl
	private ImageView iv_forward_icon;
	private TextView tv_forward_name;
	private TextView tv_forward_content;
	private Button btn_forward_more;
	private DiaryDetailCoverGroup vb_forword_covergroup;
		
	private MiShareinfo diaryDetailContent = null;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private ImageView iv_icon;  //转发图标
	
	private OnClickListener listener =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getTag() != null && v.getTag() instanceof MyDiary && diaryDetailContent !=null){
				Intent intent = new Intent(getContext(), DiaryPreviewActivity.class);
				intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, ((MyDiary)v.getTag()).diaryuuid);
				ArrayList<MyDiary> diarys = new ArrayList<GsonResponse3.MyDiary>();
				for(int i=0; i< diaryDetailContent.diaryinfo.length; i++){
					//if("0".equals(diaryDetailContent.diaries[i].join_safebox)){
						diarys.add(diaryDetailContent.diaryinfo[i]);
					//}
				}
				DiaryManager.getInstance().setmMyDiaryBuf(diarys);
				intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_SHOW_MODE, DiaryPreviewActivity.SHOW_MODE_SIMPLE);
				getContext().startActivity(intent);
			}
		}
	};
	
	private Handler handler;
	
	private void init() {
		inflater = LayoutInflater.from(getContext());
		View v;
		v = inflater.inflate(R.layout.include_diary_detail_content_view, null);

		iv_forward_icon = (ImageView) v.findViewById(R.id.iv_forward_icon);
		tv_forward_name = (TextView) v.findViewById(R.id.tv_forward_name);
		tv_forward_content = (TextView) v.findViewById(R.id.tv_forward_content);
		btn_forward_more = (Button) v.findViewById(R.id.btn_forward_more);
		vb_forword_covergroup = (DiaryDetailCoverGroup) v.findViewById(R.id.vb_forward_covergroup);
		
		DisplayMetrics dm = getContext().getApplicationContext().getResources().getDisplayMetrics();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vb_forword_covergroup.getLayoutParams();
		params.setMargins(0, (int)DensityUtil.dip2px(getContext(), 10), (int)((float)dm.widthPixels*48/1080), 0);
		//params.rightMargin = dm.widthPixels * (100/1080);
		vb_forword_covergroup.setLayoutParams(params);
		
		params = (RelativeLayout.LayoutParams) iv_forward_icon.getLayoutParams();
		params.width = dm.widthPixels * 169/1080;
		iv_forward_icon.setLayoutParams(params);
		
		iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
		addView(v);
			
		imageLoader = ImageLoader.getInstance();
		//if(!imageLoader.isInited())
		//imageLoader.init(ImageLoaderConfiguration.createDefault(DiaryDetailContentView.this.getContext()));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		handler = new Handler(this);
	}

	private boolean isBurn = false;
	
	public void setContent(VshareDetail vshareDetail) {
		if(vshareDetail != null) {
			isBurn = vshareDetail.isBurn;
			setContent(vshareDetail.miShareinfo);
		}
	}
	
	/*
	 * 设置日记内容
	 */
	public void setContent(MiShareinfo diaryDetailContent){
		if(diaryDetailContent == null || diaryDetailContent.diaryinfo == null){
			return;
		}
		this.diaryDetailContent = diaryDetailContent;
			vb_forword_covergroup.setVisibility(View.VISIBLE);
			vb_forword_covergroup.removeAllViews();
			iv_icon.setVisibility(View.GONE);
			imageLoader.displayImageEx(diaryDetailContent.mic_headimageurl, iv_forward_icon, options, animateFirstListener, ActiveAccount.getInstance(MainApplication.getInstance()).getUID(), 1);
			iv_forward_icon.setTag(diaryDetailContent.diaryinfo[0].userid);
			iv_forward_icon.setTag(iv_forward_icon.getId(),diaryDetailContent.diaryinfo[0].userid);
			iv_forward_icon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					String myuid = ActiveAccount.getInstance(getContext()).getUID();
					final String other_userid = (String) v.getTag(v.getId());
					if(!other_userid.equals(myuid)){
						if(isBurn) {
							PopDialog();
						} else {
							// 验证是否是自己的好友
							AccountInfo accinfo = AccountInfo.getInstance(myuid);
							ContactManager friendsListContactManager=accinfo.friendsListName;
							WrapUser currUserInfo=friendsListContactManager.findUserByUserid(other_userid);
							
							if(currUserInfo == null){
								if(other_userid.equals(accinfo.serviceUser.userid)){
									// 是客服，并跳转
	//								Intent intent = new Intent(getContext(), OtherZoneActivity.class);
	//								intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, other_userid);
	//								getContext().startActivity(intent);
								}else{
								
								// 不在好友列表中
								new XEditDialog.Builder(getContext())
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
								// 是好友，并跳转
								Intent intent = new Intent(getContext(), OtherZoneActivity.class);
								intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, other_userid);
								getContext().startActivity(intent);
							}
						}
					}
				}
			});
			if(TextUtils.isEmpty(diaryDetailContent.nickmarkname)){
				//tv_forward_name.setText(diaryDetailContent.mic_nickname);
				FriendsExpressionView.replacedExpressions(diaryDetailContent.mic_nickname, tv_forward_name);
			}else{
				//tv_forward_name.setText(diaryDetailContent.nickmarkname);
				FriendsExpressionView.replacedExpressions(diaryDetailContent.nickmarkname, tv_forward_name);
			}
			FriendsExpressionView.replacedExpressions(diaryDetailContent.content, tv_forward_content);
			if(TextUtils.isEmpty(diaryDetailContent.content)){
				tv_forward_content.setText("");
				tv_forward_content.setVisibility(View.GONE);
			}
			//tv_forward_content.setText(diaryDetailContent.sharecontent);
			btn_forward_more.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(tv_forward_content.getMaxLines() == 6)
					{
						tv_forward_content.setMaxLines(100);
						btn_forward_more.setText(R.string.diary_hide_content);
					}
					else
					{
						tv_forward_content.setMaxLines(6);
						btn_forward_more.setText(R.string.diary_all_content);
					}
				}
			});
			tv_forward_content.post(new Runnable() {

			    @Override
			    public void run() {
			        int lineCount    = tv_forward_content.getLineCount();
			        if(lineCount>6){
			        	btn_forward_more.setVisibility(View.VISIBLE);
			        } else{
			        	btn_forward_more.setVisibility(View.GONE);
			        }
			    }
			});
			for(int i=0; i<diaryDetailContent.diaryinfo.length; i++){
				ContentThumbnailView view = new ContentThumbnailView(getContext());
				view.setTag(diaryDetailContent.diaryinfo[i]);
				view.setOnClickListener(listener);
				view.setContentDiaries("0", diaryDetailContent.diaryinfo[i]);
				vb_forword_covergroup.addView(view);
			}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null) {
				addfriendResponse response = (addfriendResponse) msg.obj;
				if ("0".equals(response.status)) {
					if(response.target_userid == null){
						break;
					}
					Prompt.Alert(getContext(), "好友申请已发送");
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

	private void PopDialog() {
		new Xdialog.Builder(context)
		.setMessage("阅后即焚的内容\n不支持查看对方空间")
		.setPositiveButton("知道了", null)
//		.setNegativeButton("知道了", null)
		.create().show();
	}
	
}
