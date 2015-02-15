package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.network.GsonResponseObject.recmmandInfoResp;
import com.cmmobi.railwifi.network.GsonResponseObject.recommandDetail;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.sql.HistoryManager;
import com.cmmobi.railwifi.sql.SqlConvertor;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 专辑详情页面
 * @author wangjm@cmmobi.com
 *
 */
public class AlbumDetailActivity extends TitleRootActivity {

	private TextView tvTitle;
	private ListView lvAlbum;
	ArrayList<recommandDetail> albumList = new ArrayList<recommandDetail>();
	private Gson gson = new Gson();
	private AlbumAdapter adapter;
	private String object_id;
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_MEDIA_RECOMMANDINFO:
			if (msg.obj != null) {
				final recmmandInfoResp resp = (recmmandInfoResp) msg.obj;
				if ("0".equals(resp.status)) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							tvTitle.setText(resp.title);
						}
					});
					object_id = resp.object_id;
					if (resp.list != null && resp.list.length >0) {
						Collections.addAll(albumList, resp.list);
						if (adapter != null) {
							adapter.notifyDataSetChanged();
						}
					}
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_album_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		hideRightButton();
		
		initViews();
		String mediaId = getIntent().getStringExtra("mediaid");
		String title = getIntent().getStringExtra("title");
		if (mediaId != null) {
			Requester.requestRecommendInfo(handler, mediaId);
		}
		if (title != null) {
			setTitleText(title);
		}
		
		AlbumAdapter adapter = new AlbumAdapter(this, albumList);
		lvAlbum.setAdapter(adapter);
	}
	
	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.tv_album_title);
		lvAlbum = (ListView) findViewById(R.id.lv_album_list);
		lvAlbum.setSelector(new ColorDrawable(Color.TRANSPARENT));;
		ViewUtils.setMarginTop(tvTitle, 24);
		ViewUtils.setMarginTop(lvAlbum, 24);
		ViewUtils.setMarginLeft(lvAlbum, 24);
		ViewUtils.setMarginRight(lvAlbum, 16);
		
	}
	
	public class AlbumAdapter extends BaseAdapter{

		private LayoutInflater inflater;
		private Context context;
		private List<recommandDetail> albumList;
		
		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		public AlbumAdapter(Context context,List<recommandDetail> list) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			
			albumList = list;
			
			imageLoader = MyImageLoader.getInstance();

			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				    .bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new SimpleBitmapDisplayer())
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.build();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return albumList.size();
		}

		@Override
		public recommandDetail getItem(int position) {
			// TODO Auto-generated method stub
			return albumList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.d("=AAA=","AlbumDetail getView poistion = " + position);
			AlbumHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_album_detail_list, null);
				
				convertView.setPadding(0, 0, 8, 0);
				holder = new AlbumHolder();
				holder.ibtnPlay = (ImageButton) convertView.findViewById(R.id.ib_content_play_btn);
				holder.ivContentImg = (ImageView) convertView.findViewById(R.id.iv_content_img);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content_intro);
				holder.rlContentImg = (RelativeLayout) convertView.findViewById(R.id.rl_content_img);
				
				ViewUtils.setMarginRight(holder.ibtnPlay, 24);
				ViewUtils.setMarginTop(holder.ibtnPlay, 24);
				holder.tvContent.setTextSize(DisplayUtil.textGetSizeSp(context, 28));
				
				convertView.setTag(holder);
			} else {
				holder = (AlbumHolder)convertView.getTag();
			}
			
			recommandDetail item = albumList.get(position);
			
			imageLoader.displayImage(item.img_path, holder.ivContentImg, imageLoaderOptions);
			holder.tvContent.setText(item.content);
			if ("2".equals(item.type)) {
				holder.ibtnPlay.setVisibility(View.VISIBLE);
			} else {
				holder.ibtnPlay.setVisibility(View.GONE);
			}
			holder.rlContentImg.setTag(item);
			holder.ibtnPlay.setTag(item);
			holder.ibtnPlay.setOnClickListener(AlbumDetailActivity.this);
			holder.rlContentImg.setOnClickListener(AlbumDetailActivity.this);
			return convertView;
		}
	}
	
	class AlbumHolder {
		public ImageView ivContentImg;
		public TextView tvContent;
		public ImageButton ibtnPlay;
		public RelativeLayout rlContentImg;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_content_img:
		case R.id.ib_content_play_btn:
			recommandDetail item = (recommandDetail)v.getTag();
			if ("2".equals(item.type)) {
				/*Intent movieIntent = new Intent(this,VideoPlayerActivity.class);
				startActivity(movieIntent);*/
				if(TextUtils.isEmpty(item.media_id)){
					Toast.makeText(this, "播放路径错误, 请稍后刷新列表再试", Toast.LENGTH_LONG).show();
					return;
				}
				PlayHistory ph = HistoryManager.getInstance().getPlayHistoryItem(item.media_id);
				if(ph==null){
					ph = SqlConvertor.recommendDetail2PlayHistory(item, GsonResponseObject.MEDIA_TYPE_MOVIE);
				}
				CmmobiClickAgentWrapper.onEvent(this, "av_sub_details", "label", object_id, "label2", item.src_path);
				if(Config.IS_USE_COMMOBI_VIDEOVIEW){
					Intent intent = new Intent(this, CmmobiVideoPlayer.class);
					intent.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
					intent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
					startActivity(intent);
				}else{
					Intent intent = new Intent(this, VideoPlayerActivity.class);
					intent.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
					intent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
					startActivity(intent);
				}
			} else if ("5".equals(item.type)) {//逗你玩
				Intent jokIntent = new Intent(this,JokeDetailActivity.class);
				jokIntent.putExtra(ConStant.INTENT_MEDIA_SRC_PATH, item.src_path);
				startActivity(jokIntent);
			}
			break; 
		}
		super.onClick(v);
	}

}
