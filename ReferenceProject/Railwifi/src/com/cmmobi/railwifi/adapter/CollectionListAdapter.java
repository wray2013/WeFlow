package com.cmmobi.railwifi.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.CmmobiVideoPlayer;
import com.cmmobi.railwifi.activity.MovieDetailActivity;
import com.cmmobi.railwifi.activity.VideoPlayerActivity;
import com.cmmobi.railwifi.dao.Fav;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.network.GsonResponseObject;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.simope.yzvideo.base.MobilePlayActivity;

public class CollectionListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Fav> list = new ArrayList<Fav>();
	private int size = 0;
	
	//使用开源的webimageloader
	public static DisplayImageOptions options;
	protected MyImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	public CollectionListAdapter(final Context context) {
		this.context = context;
		DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
		this.size = dm.widthPixels/5;
		inflater = LayoutInflater.from(context);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = MyImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheOnDisk(true)
		.showImageOnFail(R.drawable.pic_list_default)
		.cacheInMemory(true)
		.displayer(new RoundedBorderBitmapDisplayer(20, 0xfff65c00, 3))// 圆角图片
		.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Fav getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.list_fav_item, null);
			holder = new ViewHolder();
			
			holder.rl_whole = (RelativeLayout) convertView.findViewById(R.id.rl_whole);
			holder.btn_play = (ImageButton) convertView.findViewById(R.id.btn_play);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_more = (TextView) convertView.findViewById(R.id.tv_more);
			holder.movie_pic = (ImageView) convertView.findViewById(R.id.movie_pic);
			holder.iv_fav = (Button) convertView.findViewById(R.id.iv_fav);
			holder.tag =  (TextView) convertView.findViewById(R.id.tag);
			holder.tv_source = (TextView) convertView.findViewById(R.id.tv_source_name);
			holder.tv_descrp = (TextView) convertView.findViewById(R.id.tv_descrp);
			holder.tv_director_name = (TextView) convertView.findViewById(R.id.tv_director_name);
			holder.tv_actor_name = (TextView) convertView.findViewById(R.id.tv_actor_name);
			holder.tv_score = (TextView) convertView.findViewById(R.id.tv_score);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(FavManager.getInstance().hasFavItem(list.get(position).getMedia_id())){
			holder.iv_fav.setBackgroundResource(R.drawable.btn_fav_selected);
		}else{
			holder.iv_fav.setBackgroundResource(R.drawable.btn_fav_normal);
		}
		
		holder.tv_director_name.setText(list.get(position).getDirector());
		holder.tv_actor_name.setText(list.get(position).getActors());
		holder.tv_title.setText(list.get(position).getName());
		holder.tv_score.setText(list.get(position).getScore());
		holder.tv_source.setText("来源：" + list.get(position).getSource());
		holder.tv_descrp.setText(list.get(position).getIntroduction());
		
		holder.tv_title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DownloadApkUtils downApk = new DownloadApkUtils(context, list.get(position).getSource_id());
				downApk.download();
			}
		});
		imageLoader.displayImage(list.get(position).getImg_path(), holder.movie_pic, options, animateFirstListener);
		if(list.get(position).getTag()!=null && !list.get(position).getTag().equals("")){
			String color = list.get(position).getColor();
			holder.tag.setVisibility(View.VISIBLE);
			holder.tag.setText(list.get(position).getTag());
			if(color.equals("1")){
				holder.tag.setBackgroundResource(R.drawable.tag_pic_a);
			}else if(color.equals("2")){
				holder.tag.setBackgroundResource(R.drawable.tag_pic_b);
			}else if(color.equals("3")){
				holder.tag.setBackgroundResource(R.drawable.tag_pic_c);
			}else if(color.equals("4")){
				holder.tag.setBackgroundResource(R.drawable.tag_pic_d);
			}else if(color.equals("5")){
				holder.tag.setBackgroundResource(R.drawable.tag_pic_e);
			}else{
				holder.tag.setBackgroundResource(R.drawable.tag_pic_b);
			}
		}else{
			holder.tag.setVisibility(View.GONE);
		}
		
		holder.tv_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(context, "进入详情：" + list.get(position).getName(), Toast.LENGTH_LONG).show();
				Intent intent = new Intent(context, MovieDetailActivity.class);
				intent.putExtra(ConStant.INTENT_MEDIA_ID, list.get(position).getMedia_id());
				context.startActivity(intent);
			}
		});
		
		holder.rl_whole.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(context, "进入详情：" + list.get(position).getName(), Toast.LENGTH_LONG).show();
				Intent intent = new Intent(context, MovieDetailActivity.class);
				intent.putExtra(ConStant.INTENT_MEDIA_ID, list.get(position).getMedia_id());
				context.startActivity(intent);
			}
		});
		
		
		holder.tv_source.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DownloadApkUtils downApk = new DownloadApkUtils(context, list.get(position).getSource_id());
				downApk.download();
			}
		});
		
		holder.btn_play.setOnClickListener(new OnClickListener() {
			
			private Gson gson = new Gson();

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(context, "进入播放：" + list.get(position).getName(), Toast.LENGTH_LONG).show();
				PlayHistory ph = HistoryManager.getInstance().getPlayHistoryItem(list.get(position).getMedia_id());
				if(ph==null){
					ph = SqlConvertor.mediaFav2PlayHistory(list.get(position), GsonResponseObject.MEDIA_TYPE_MOVIE, null, 0);
				}
				
				CmmobiClickAgentWrapper.onEvent(context, "p_inf_play");

				if(Config.IS_USE_COMMOBI_VIDEOVIEW){
					Intent intent = new Intent(context, CmmobiVideoPlayer.class);
					intent.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
					intent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
					context.startActivity(intent);
				}else{
					Intent intent = new Intent(context,  VideoPlayerActivity.class);
					intent.putExtra(VideoPlayerActivity.KEY_PLAYHISTORY, gson.toJson(ph));
					intent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "1");
					context.startActivity(intent);
				}
			}
		});
		
		holder.iv_fav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Fav mItem = list.get(position);
				if(FavManager.getInstance().hasFavItem(list.get(position).getMedia_id())){
					FavManager.getInstance().removeFavItem(mItem.getMedia_id());
					v.setBackgroundResource(R.drawable.btn_fav_normal);
					CmmobiClickAgentWrapper.onEvent(context, "p_inf_unfav");
				}else{
					FavManager.getInstance().putFavItem(mItem);
					v.setBackgroundResource(R.drawable.btn_fav_selected);
				}
			}
		});
		return convertView;
	}
	
	
	public void setData(List<Fav> data){
		if(data != null && data.size()>0){
			this.list.clear();
			this.list.addAll(data);
		}else{
			this.list.clear();
		}
	}
	
	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	
	
	public class ViewHolder {
		RelativeLayout rl_whole;
		TextView tv_title;
		TextView tv_more;
		ImageView movie_pic;
		Button iv_fav;
		ImageButton btn_play;
		TextView tag;
		TextView tv_actor_name;
		TextView tv_director_name;
		TextView tv_source;
		TextView tv_descrp;
		TextView tv_score;
	}

	
}