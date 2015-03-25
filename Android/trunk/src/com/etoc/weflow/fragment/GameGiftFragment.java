package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.event.GameCoinEvent;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.ExchangeFlowPkgResp;
import com.etoc.weflow.net.GsonResponseObject.ExchangeGamePkgResp;
import com.etoc.weflow.net.GsonResponseObject.GameGiftProduct;
import com.etoc.weflow.net.GsonResponseObject.GameGiftResp;
import com.etoc.weflow.net.GsonResponseObject.GamePkgListResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import de.greenrobot.event.EventBus;

public class GameGiftFragment extends Fragment implements Callback {
	
	private View mView;
	private ListView lvGift;
	private GameGiftAdatper adapter;
	private Handler handler = null;
	private List<GameGiftProduct> itemList = new ArrayList<GsonResponseObject.GameGiftProduct>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		handler = new Handler(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(GameCoinEvent event) {
		if (event == GameCoinEvent.GAMEGIFT) {
			if (itemList.size() == 0) {
				Requester.getGamePkgList(true, handler);
			}
		}
	}
	
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
		
		adapter = new GameGiftAdatper(getActivity(), itemList);
		lvGift.setAdapter(adapter);
		ViewUtils.setMarginLeft(lvGift, 32);
		ViewUtils.setMarginRight(lvGift, 32);
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
		
		private List<GameGiftProduct> appList = null;
		Context context;
		private LayoutInflater inflater;
		
		public GameGiftAdatper(Context context,List<GameGiftProduct> list) {
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
		public GameGiftProduct getItem(int arg0) {
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
			
			final GameGiftProduct item = appList.get(position);
			imageLoader.displayImage(item.icon, holder.ivImg,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvLeave.setText(item.desc);
			holder.tvFlowCoins.setText(NumberUtils.convert2IntStr(item.cost) + "流量币");
			holder.tvExchange.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
					if (accountInfo != null) {
						Requester.exchangeGamePkg(true, handler, accountInfo.getUserid(), item.chargesid);
					} else {
						startActivity(new Intent(getActivity(), LoginActivity.class));
					}
				}
			});
			return convertView;
		}
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_GAME_PKG_LIST:
			if (msg.obj != null) {
				GamePkgListResp resp = (GamePkgListResp) msg.obj;
				if(resp.status.equals("0000") || resp.status.equals("0")) {
					if (resp.chargelist != null && resp.chargelist.length >0) {
						itemList.clear();
						for (GameGiftResp item:resp.chargelist) {
							if (item.products != null && item.products.length > 0) {
								Collections.addAll(itemList, item.products);
							}
						}
						
						adapter.notifyDataSetChanged();
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_EXCHANGE_GAME_PKG:
			if (msg.obj != null) {
				ExchangeGamePkgResp chargeResp = (ExchangeGamePkgResp) msg.obj;
				if (Requester.isSuccessed(chargeResp.status)) {
					PromptDialog.Alert("订购成功");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
				} else if (Requester.isProcessed(chargeResp.status)){
					PromptDialog.Alert("订购已处理");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
				} else if (Requester.isLowFlow(chargeResp.status)) {
					PromptDialog.Alert(ConStant.LOW_FLOW);
				}  else {
					PromptDialog.Alert(ConStant.ORDER_FAIL);
				}
			}
			break;
		}
		return false;
	}
}
