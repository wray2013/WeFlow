package com.etoc.weflow.adapter;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.net.GsonResponseObject.GameWrapper;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class H5GameListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
//	private Context ctx;
	private MyImageLoader imageLoader = null;
	private DisplayImageOptions imageLoaderOptions = null;
	
	private List<GameWrapper> gamelist = new ArrayList<GameWrapper>();
	
	public H5GameListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
//		ctx = context;
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.banner_activity)
				.showImageOnFail(R.drawable.banner_activity)
				.showImageOnLoading(R.drawable.banner_activity)
				.build();
	}
	
	public void setData(List<GameWrapper> data) {
		// TODO Auto-generated method stub
		gamelist.clear();
		gamelist.addAll(data);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gamelist.size();
	}

	@Override
	public GameWrapper getItem(int position) {
		// TODO Auto-generated method stub
		return gamelist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Object object = getItem(position);
		if(object instanceof GameWrapper) {
			GameWrapper gamewrapper = (GameWrapper) object;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.h5game_list_item, null);
				holder = new ViewHolder();
				holder.ivGameBanner = (ImageView) convertView.findViewById(R.id.iv_game_banner);
				holder.tvGameName = (TextView) convertView.findViewById(R.id.tv_game_name);
				initViews(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			bindGameItem(holder, gamewrapper);
		}
		return convertView;
	}

	private void bindGameItem(ViewHolder holder, GameWrapper gamewrapper) {
		// TODO Auto-generated method stub
		imageLoader.displayImage(gamewrapper.gamepic, holder.ivGameBanner, imageLoaderOptions);
		holder.tvGameName.setText(gamewrapper.gamename);
	}

	private void initViews(View convertView) {
		// TODO Auto-generated method stub
//		ViewUtils.setMarginLeft(convertView.findViewById(R.id.iv_game_banner), 15);
//		ViewUtils.setMarginRight(convertView.findViewById(R.id.iv_game_banner), 15);
		ViewUtils.setMarginTop(convertView.findViewById(R.id.iv_game_banner), 15);
		ViewUtils.setSize(convertView.findViewById(R.id.iv_game_banner), 680, 262);
		
		ViewUtils.setMarginTop(convertView.findViewById(R.id.tv_game_name), 15);
		ViewUtils.setMarginBottom(convertView.findViewById(R.id.tv_game_name), 15);
	}
	
	public class ViewHolder {
		ImageView ivGameBanner;
		TextView tvGameName;
	}

}
