package com.cmmobi.railwifi.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.trinea.android.common.util.ViewUtils;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.MoviesListAdapter;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.mediaDetailInfoResp;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.sql.FavManager;
import com.cmmobi.railwifi.sql.HistoryManager;
import com.cmmobi.railwifi.sql.SqlConvertor;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DownloadApkUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.api.RoundedBorderBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MovieDetailActivity extends TitleRootActivity {
	private static final String TAG = "MovieDetailActivity";
	
	private RelativeLayout rlNoNet;
	private ImageView btn_more;
	private FrameLayout fl_desc;
	private ImageView movie_pic;
	private ImageButton btn_play;
	private ImageView div_line2;
	private ImageView div_line4;
	private TextView tv_movie_name;
	private TextView tv_director_name;
	private TextView tv_actor_name;
	private TextView tv_desc_short;
	private TextView tv_desc_long;
	private TextView tv_duration_name;
	private TextView tv_lang_name;
	private TextView tv_source_name;
	private TextView tag_1;
	private TextView tag_2;
	private TextView tag_3;
	private TextView tag_4;
	private TextView recommand_content;
	private TextView score;
	private TextView source;
	private ImageView iv_fav;
	private boolean isInit = false;
	private int pendingThirdSource;
	private boolean hasRecommand = false;
	private boolean isShowShortText = true;
	private TextView btn_recommand;
	private ScrollView scroll_content;
//	private ImageView iv_more_line;
	private int oldRecommandTop;
	private int oldRecommandCur;
	private boolean isChecked;
	
	private String media_id;
	private String source_id;
	private String source_name;
	
	private GsonResponseObject.mediaDetailInfoResp curMediaDetail;
	private GsonResponseObject.sohuMovieResp curSohuDetail;
	
	//使用开源的webimageloader
	public static DisplayImageOptions options;
	protected MyImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	private Gson gson = new Gson();
	
	private OnGlobalLayoutListener recommandPosListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			// TODO Auto-generated method stub
			oldRecommandTop = btn_recommand.getTop();
			Log.v(TAG, "bt_recommand.top:" + btn_recommand.getTop());
		}
	};
	
	OnPreDrawListener btnMoreVisibleListener = new OnPreDrawListener() {
		@Override
		public boolean onPreDraw() {
			if(isInit){
				return true;
			}
			
			if(hasRecommand){
				if (mesureDescription(tv_desc_short, tv_desc_long)) {
					btn_more.setVisibility(View.VISIBLE);
				} else {
					btn_more.setVisibility(View.GONE);
				}
			}else{
				btn_more.setVisibility(View.GONE);
				tv_desc_short.setVisibility(View.GONE);
				tv_desc_long.setVisibility(View.VISIBLE);
			}

			
			return true;

			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		curSohuDetail = null;
		curMediaDetail = null;
		pendingThirdSource = 0;
		
		setTitleText("看电影");
		hideRightButton();
		rightButton.setBackgroundResource(R.drawable.btn_tag);


		Intent intent = getIntent();
		if(intent!=null){
			media_id = intent.getStringExtra(ConStant.INTENT_MEDIA_ID);
			source_id = intent.getStringExtra(ConStant.INTENT_SOURCE_ID);
			if(media_id!=null && !media_id.equals("")){
				Requester.requestMovieInfo(handler, media_id);
			}
			
		}
		
		findView();
		initView();
		setListener();
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = MyImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheOnDisk(true)
		.showImageOnFail(R.drawable.pic_list_default)
		.cacheInMemory(true)
		.displayer(new RoundedBorderBitmapDisplayer(20, 0xfff65c00, 3))// 圆角图片
//		.displayer(new RoundedBitmapDisplayer(10))// 圆角图片
		.build();

	}
	
	@Override
	public void onResume(){
		super.onResume();
		
	}
	

	private void setListener() {
		// TODO Auto-generated method stub
		btn_more.setOnClickListener(this);
		btn_recommand.setOnClickListener(this);
		iv_fav.setOnClickListener(this);
		btn_play.setOnClickListener(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		isChecked = false;
		
		if(media_id!=null && FavManager.getInstance().hasFavItem(media_id)){
			iv_fav.setBackgroundResource(R.drawable.btn_fav_selected);
		}else{
			iv_fav.setBackgroundResource(R.drawable.btn_fav_normal);
		}
		
		ViewTreeObserver vto1 = btn_recommand.getViewTreeObserver();
		vto1.addOnGlobalLayoutListener(recommandPosListener);
		
		ViewTreeObserver vto2 = fl_desc.getViewTreeObserver();
		vto2.addOnPreDrawListener(btnMoreVisibleListener);
		
		fl_desc.setOnClickListener(this);

	}

	private void findView() {
		rlNoNet = (RelativeLayout) findViewById(R.id.rl_no_network);
		fl_desc = (FrameLayout) findViewById(R.id.fl_desc);
		tv_desc_short = (TextView) findViewById(R.id.tv_desc_short);
		tv_desc_long = (TextView) findViewById(R.id.tv_desc_long);
		
		score = (TextView) findViewById(R.id.score);
		source = (TextView) findViewById(R.id.score);
		
		tv_movie_name = (TextView) findViewById(R.id.tv_movie_name);
		btn_more = (ImageView) findViewById(R.id.btn_more);
		movie_pic = (ImageView) findViewById(R.id.movie_pic);
		btn_play = (ImageButton) findViewById(R.id.btn_play);
		div_line2 = (ImageView) findViewById(R.id.div_line2);
		div_line4 = (ImageView) findViewById(R.id.div_line4);
		iv_fav = (ImageView) findViewById(R.id.iv_fav);
		
		btn_recommand = (TextView) findViewById(R.id.recommand_title);
		scroll_content = (ScrollView) findViewById(R.id.scroll_pannel);
		
		tv_director_name = (TextView) findViewById(R.id.tv_director_name);
		tv_actor_name = (TextView) findViewById(R.id.tv_actor_name);
		
		tv_duration_name = (TextView) findViewById(R.id.tv_duration_name);
		tv_lang_name = (TextView) findViewById(R.id.tv_lang_name);
		tv_source_name = (TextView) findViewById(R.id.tv_source_name);
		
		recommand_content = (TextView) findViewById(R.id.recommand_content);

		tag_1 = (TextView) findViewById(R.id.tag_1);
		tag_2 = (TextView) findViewById(R.id.tag_2);
		tag_3 = (TextView) findViewById(R.id.tag_3);
		tag_4 = (TextView) findViewById(R.id.tag_4);
		
		com.cmmobi.railwifi.utils.ViewUtils.setSize(tag_1, 92, 50);
		com.cmmobi.railwifi.utils.ViewUtils.setSize(tag_2, 92, 50);
		com.cmmobi.railwifi.utils.ViewUtils.setSize(tag_3, 92, 50);
		com.cmmobi.railwifi.utils.ViewUtils.setSize(tag_4, 92, 50);
		com.cmmobi.railwifi.utils.ViewUtils.setSize(movie_pic, 266, 348);
		com.cmmobi.railwifi.utils.ViewUtils.setSize(btn_play, 260, 342);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester.RESPONSE_TYPE_SOHU_MOVIE:
			GsonResponseObject.sohuMovieResp r46 = (GsonResponseObject.sohuMovieResp) msg.obj;
			if(r46!=null && "0".equals(r46.status)){
				curSohuDetail = r46;
				if(pendingThirdSource==2){
					if(source_id!=null && !source_id.equals("")){
						
						if(ConStant.SOHU_SOURCE_NAME.equalsIgnoreCase(source_name)){
							try {
								PackageManager pm = getPackageManager();
								pm.getPackageInfo("com.sohu.sohuvideo", PackageManager.GET_ACTIVITIES);
								if(curSohuDetail!=null){
									startSohuClient(this, curSohuDetail);
								}
							} catch (NameNotFoundException e) {
								
							}
						}
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_MEDIA_MOVIEINFO:
			GsonResponseObject.mediaDetailInfoResp r12 = (GsonResponseObject.mediaDetailInfoResp) msg.obj;
			if(r12!=null && "0".equals(r12.status)){
				updateView(r12);
				curMediaDetail = r12;
				rlNoNet.setVisibility(View.GONE);
				if(r12.source_id!=null && !r12.source_id.equals("")){
					source_id = r12.source_id;
					source_name = r12.source;
					if(source_name!=null && source_name.equalsIgnoreCase(ConStant.SOHU_SOURCE_NAME) && r12.media_id!=null){
						Requester.requestSohuMovie(handler, r12.media_id);
					}
				}
			}else{
				rlNoNet.setVisibility(View.VISIBLE);
			}
			break;
		}
		return false;
	}


	private void updateView(final mediaDetailInfoResp r12) {
		// TODO Auto-generated method stub
		tv_desc_short.setText(r12.details);
		tv_desc_long.setText(r12.details);
		tv_desc_long.setVisibility(View.VISIBLE);
		
		if(r12.recommended==null || r12.recommended.equals("")){
			div_line4.setVisibility(View.GONE);
			btn_recommand.setVisibility(View.GONE);
			recommand_content.setVisibility(View.GONE);
			btn_more.setVisibility(View.GONE);
			tv_desc_short.setVisibility(View.GONE);
			hasRecommand = false;
		}else{
			div_line4.setVisibility(View.VISIBLE);
			btn_recommand.setVisibility(View.VISIBLE);
			recommand_content.setVisibility(View.VISIBLE);
			btn_more.setVisibility(View.VISIBLE);
			tv_desc_short.setVisibility(View.VISIBLE);
			hasRecommand = true;
		}

		isInit = false;
		
		tv_actor_name.setText(r12.actors);
		
		tv_director_name.setText(r12.director);
		tv_lang_name.setText(r12.language);
		tv_duration_name.setText(r12.length);
		tv_movie_name.setText(r12.name);
		recommand_content.setText(r12.recommended);
		score.setText(r12.score);
		tv_source_name.setText("来源:" + r12.source);
		tv_source_name.setOnClickListener(this);
		

		
		imageLoader.displayImage(r12.img_path, movie_pic, options, animateFirstListener);
		
		if(r12.label!=null && r12.label.length>0){
			if(r12.label.length==1){
				tag_1.setVisibility(View.VISIBLE);
				tag_2.setVisibility(View.GONE);
				tag_3.setVisibility(View.GONE);
				tag_4.setVisibility(View.GONE);
				tag_1.setText(r12.label[0]);
			}else if(r12.label.length==2){
				tag_1.setVisibility(View.VISIBLE);
				tag_2.setVisibility(View.VISIBLE);
				tag_3.setVisibility(View.GONE);
				tag_4.setVisibility(View.GONE);
				tag_1.setText(r12.label[0]);
				tag_2.setText(r12.label[1]);
			}else if(r12.label.length==3){
				tag_1.setVisibility(View.VISIBLE);
				tag_2.setVisibility(View.VISIBLE);
				tag_3.setVisibility(View.VISIBLE);
				tag_4.setVisibility(View.GONE);
				tag_1.setText(r12.label[0]);
				tag_2.setText(r12.label[1]);
				tag_3.setText(r12.label[2]);
			}else{
				tag_1.setVisibility(View.VISIBLE);
				tag_2.setVisibility(View.VISIBLE);
				tag_3.setVisibility(View.VISIBLE);
				tag_4.setVisibility(View.VISIBLE);
				tag_1.setText(r12.label[0]);
				tag_2.setText(r12.label[1]);
				tag_3.setText(r12.label[2]);
				tag_4.setText(r12.label[3]);
			}
		}
		
		
		fl_desc.invalidate();
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_movie_detail;
	}

	/**
	 * 计算描述信息是否过长
	 */
	private boolean mesureDescription(TextView shortView, TextView longView) {
		int shortHeight = shortView.getHeight();
		int longHeight = longView.getHeight();
		Log.v(TAG, "mesureDescription - shortHeight:" + shortHeight + ", longHeight:" + longHeight);
		if (longHeight > shortHeight) {
			shortView.setVisibility(View.VISIBLE);
			longView.setVisibility(View.GONE);
			return true;
		}
		shortView.setVisibility(View.GONE);
		longView.setVisibility(View.VISIBLE);
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_desc:
		case R.id.btn_more:
			if(!hasRecommand){
				break;
			}
			if (isShowShortText) {
				tv_desc_short.setVisibility(View.GONE);
				tv_desc_long.setVisibility(View.VISIBLE);
				btn_more.setBackgroundResource(R.drawable.btn_close);
			} else {
				tv_desc_short.setVisibility(View.VISIBLE);
				tv_desc_long.setVisibility(View.GONE);
				btn_more.setBackgroundResource(R.drawable.btn_open);
			}
			isShowShortText = !isShowShortText;
			isInit = true;
			break;
		case R.id.tv_source_name:
			CmmobiClickAgentWrapper.onEvent(this, "av_movie_source", "label", media_id, "label2", source_id);
			if(source_id!=null && !source_id.equals("")){
				
				if(ConStant.SOHU_SOURCE_NAME.equalsIgnoreCase(source_name)){
					try {
						PackageManager pm = getPackageManager();
						pm.getPackageInfo("com.sohu.sohuvideo", PackageManager.GET_ACTIVITIES);
						pendingThirdSource = 1;
						if(curMediaDetail!=null){
							Requester.requestSohuMovie(handler, media_id);
						}else{
							Requester.requestMovieInfo(handler, media_id);
						}
					} catch (NameNotFoundException e) {
						DownloadApkUtils downApk = new DownloadApkUtils(MovieDetailActivity.this, source_id);
						downApk.download();
					}
				}else{
					DownloadApkUtils downApk = new DownloadApkUtils(MovieDetailActivity.this, source_id);
					downApk.download();
				}
				

			}
			break;
		case R.id.recommand_title:
			if(isChecked){
				isChecked = false;
				scroll_content.scrollTo(0, oldRecommandCur);
				
			}else{
				oldRecommandCur = scroll_content.getScrollY();
				scroll_content.scrollTo(0, oldRecommandTop);
				isChecked = true;
			}
			
			//Log.v(TAG, "---scroll oldRecommandTop:" + oldRecommandTop + ", cur:" + oldRecommandCur);
			break;
			
		case R.id.iv_fav:
			if(FavManager.getInstance().hasFavItem(media_id)){
				FavManager.getInstance().removeFavItem(media_id);
				v.setBackgroundResource(R.drawable.btn_fav_normal);
			}else{
				CmmobiClickAgentWrapper.onEvent(this, "av_movie_details", "2");
				FavManager.getInstance().putFavItem(SqlConvertor.mediaDetail2Fav(curMediaDetail, GsonResponseObject.MEDIA_TYPE_MOVIE));
				v.setBackgroundResource(R.drawable.btn_fav_selected);
			}

			break;
			
		case R.id.btn_play:
//			Toast.makeText(this, "播放 media_id:" + media_id, Toast.LENGTH_LONG).show();
			CmmobiClickAgentWrapper.onEvent(this, "av_movie_details", "1");
//			CmmobiClickAgentWrapper.onEvent(this, "av_movie_play", "label", media_id, "label2", source_id);
			if(ConStant.SOHU_SOURCE_NAME.equals(source_name)){

				try {
					PackageManager pm = getPackageManager();
					pm.getPackageInfo("com.sohu.sohuvideo", PackageManager.GET_ACTIVITIES);
					if(curSohuDetail!=null){
						startSohuClient(this, curSohuDetail);

					}else{
						pendingThirdSource = 2;
						if(curMediaDetail!=null){
							Requester.requestSohuMovie(handler, media_id);
						}else{
							Requester.requestMovieInfo(handler, media_id);
						}
						
					}

				} catch (NameNotFoundException e) {
					DownloadApkUtils downApk = new DownloadApkUtils(MovieDetailActivity.this, source_id);
					downApk.download();
				}
				break;
			}
			PlayHistory ph = HistoryManager.getInstance().getPlayHistoryItem(media_id);
			if(ph==null){
				ph = SqlConvertor.mediaDetail2PlayHistory(curMediaDetail, GsonResponseObject.MEDIA_TYPE_MOVIE, null, 0);
			}

			if(Config.IS_USE_COMMOBI_VIDEOVIEW){
				Intent intent = new Intent(this, CmmobiVideoPlayer.class);
				intent.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
				intent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
				this.startActivity(intent);
			}else{
				Intent intent = new Intent(this, VideoPlayerActivity.class);
				intent.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
				intent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
				this.startActivity(intent);
			}
			break;

		default:
			super.onClick(v);
			break;
		}
	}
	
	public static void startSohuClient(Context context, GsonResponseObject.sohuMovieResp curSohuDetail){
		if(curSohuDetail==null || !"0".equals(curSohuDetail.status)){
			return;
		}
		
		Intent intent = new Intent("android.intent.action.START_SOHUTV");
		intent.putExtra("channelid", curSohuDetail.channelid);//必传，由搜狐分配
		intent.putExtra("playlever", curSohuDetail.playlever);//必传，表示标清的清晰度
		intent.putExtra("enterid", curSohuDetail.enterid);//必传，由搜狐分配
		intent.putExtra("appname", curSohuDetail.appname);//必传，表示直接返回第三方
		intent.putExtra("position", curSohuDetail.position);//非必传，播放起始点
		intent.putExtra("videopath", curSohuDetail.videopath);// 必传，播放的路径
		if(curSohuDetail.videopath_list!=null && curSohuDetail.videopath_list.length>0){
			ArrayList<String> al = new ArrayList<String>();
			for(String path : curSohuDetail.videopath_list){
				al.add(path);
			}
			intent.putStringArrayListExtra("videopath_list", al);
		}

		// paths：ArrayList<String>非必传，表示同一个sid中的下载好的视频地址列表
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
