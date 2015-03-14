package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.fragment.MobileFlowFragment.FlowViewHolder;
import com.etoc.weflow.net.GsonResponseObject.GameGiftResp;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowResp;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GameGiftFragment extends Fragment {
	
	private View mView;
	private ListView lvGift;
	private GameGiftAdatper adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.fragment_game_gift, null);
		mView = view;
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		lvGift = (ListView) view.findViewById(R.id.lv_game_gift);
		
		adapter = new GameGiftAdatper(getActivity(), makeFlowData());
		lvGift.setAdapter(adapter);
	}
	
	private List<GameGiftResp> makeFlowData() {
		List<GameGiftResp> list = new ArrayList<GameGiftResp>();
		
		String[] imgUrls = {"http://up.ekoooo.com/uploads2/tubiao/6/20088712119375778013.png",
        		"http://up.ekoooo.com/uploads2/allimg/080730/03562737.png",
        		"http://pica.nipic.com/2007-09-18/2007918135853894_2.jpg",
        		"http://up.ekoooo.com/uploads2/allimg/080730/08455224.png",
        		"http://up.ekoooo.com/uploads2/tubiao/7/200887197170778030.png"
        		};
		
		String[] leaves = {
				"189",
				"189",
				"189",
				"189",
				"189",
		};
		for (int i = 0;i < 5;i++) {
			GameGiftResp resp = new GameGiftResp();
			resp.gamepkgid = i + "";
			resp.icon = imgUrls[i];
			resp.title = "DNF服务器喇叭";
			resp.leave = leaves[i];
			resp.cost = ((i + 1) * 100) + "";
			list.add(resp);
		}
		
		return list;
	}
	
	
	class GameGiftViewHolder {
		ImageView ivImg;
		TextView tvName;
		TextView tvLeave;
		TextView tvExchange;
		TextView tvFlowCoins;
	}
	
	private class GameGiftAdatper extends BaseAdapter {

		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		private List<GameGiftResp> appList = null;
		Context context;
		private LayoutInflater inflater;
		
		public GameGiftAdatper(Context context,List<GameGiftResp> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			inflater = LayoutInflater.from(context);
			appList = list;
			imageLoader = MyImageLoader.getInstance();

			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.showImageForEmptyUri(R.drawable.small_pic_default)
					.showImageOnFail(R.drawable.small_pic_default)
					.showImageOnLoading(R.drawable.small_pic_default)
					.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appList.size();
		}

		@Override
		public GameGiftResp getItem(int arg0) {
			// TODO Auto-generated method stub
			return appList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			GameGiftViewHolder holder = null;
			if (convertView == null) {
				holder = new GameGiftViewHolder();
				convertView = inflater.inflate(R.layout.item_game_gift, null);
				holder.ivImg = (ImageView) convertView.findViewById(R.id.iv_game_icon);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_gift_name);
				holder.tvLeave = (TextView) convertView.findViewById(R.id.tv_gift_left);
				holder.tvExchange = (TextView) convertView.findViewById(R.id.tv_gift_exchange);
				holder.tvFlowCoins = (TextView) convertView.findViewById(R.id.tv_flow_coins);
				
				ViewUtils.setHeight(convertView.findViewById(R.id.view_space), 152);
				ViewUtils.setSize(holder.ivImg, 92, 92);
				ViewUtils.setSize(holder.tvExchange, 112, 50);
				ViewUtils.setMarginLeft(holder.ivImg, 28);
				ViewUtils.setMarginLeft(holder.tvName, 44);
				ViewUtils.setMarginTop(holder.tvName, 28);
				ViewUtils.setMarginBottom(holder.tvLeave, 28);
				ViewUtils.setMarginTop(holder.tvExchange, 20);
				ViewUtils.setMarginRight(holder.tvExchange, 24);
				ViewUtils.setMarginTop(holder.tvFlowCoins, 32);
				
				ViewUtils.setTextSize(holder.tvName, 28);
				ViewUtils.setTextSize(holder.tvLeave, 23);
				ViewUtils.setTextSize(holder.tvExchange, 26);
				ViewUtils.setTextSize(holder.tvFlowCoins, 21);
				
				convertView.setTag(holder);
				
			} else {
				holder = (GameGiftViewHolder) convertView.getTag();
			}
			
			GameGiftResp item = appList.get(position);
			imageLoader.displayImage(item.icon, holder.ivImg,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvLeave.setText("剩余数量：" + item.leave);
			holder.tvFlowCoins.setText(item.cost + "流量币");
			return convertView;
		}
		
	}
}
