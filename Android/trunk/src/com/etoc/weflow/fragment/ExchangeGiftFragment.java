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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
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
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.event.ExpenseFlowFragmentEvent;
import com.etoc.weflow.net.GsonResponseObject.GiftBannerResp;
import com.etoc.weflow.net.GsonResponseObject.GiftListResp;
import com.etoc.weflow.net.GsonResponseObject.GiftProduct;
import com.etoc.weflow.net.GsonResponseObject.GiftResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.imbryk.viewPager.LoopViewPager;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.PageIndicator;

import de.greenrobot.event.EventBus;

public class ExchangeGiftFragment extends Fragment implements Callback {
	private View mView;
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private GiftBannerAdapter bannerAdapter;
	private PullToRefreshScrollView ptrScrollView = null;
	
	private List<GiftBannerResp> bannerList = new ArrayList<GiftBannerResp>();
	private List<GiftProduct> giftList = new ArrayList<GiftProduct>();
	
	private ListView listView = null;
	private GiftAdatper adapter = null;
	private Handler handler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		handler = new Handler(this);
		EventBus.getDefault().register(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(ExpenseFlowFragmentEvent event) {
		if (event.getIndex() == ExpenseFlowActivity.INDEX_GIFT) {
			if (bannerList.size() == 0) {
				Requester.getGiftList(true, handler);
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
		
		View view = inflater.inflate(R.layout.fragment_exchange_gift, null);
		mView = view;
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) view.findViewById(R.id.indicator_service);
        
        bannerAdapter = new GiftBannerAdapter(getChildFragmentManager(),  bannerList);
//        viewPager.setAdapter(bannerAdapter);
        
//        mIndicator.setViewPager(viewPager);
//		mIndicator.notifyDataSetChanged();
		
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
		
		listView = (ListView) view.findViewById(R.id.lv_gift_exchange);
		adapter = new GiftAdatper(getActivity(), giftList);
		listView.setAdapter(adapter);
		
		ViewUtils.setHeightPixel(listView,ViewUtils.getListHeight(listView, DisplayUtil.getSize(getActivity(), 152)));
	}
	
	
	private class GiftBannerAdapter extends FragmentPagerAdapter {

		private List<GiftBannerResp> appList = null;
		
		public GiftBannerAdapter(FragmentManager fm,List<GiftBannerResp> list) {
			// TODO Auto-generated constructor stub
			super(fm);
			
			appList = list;
		}
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			position = LoopViewPager.toRealPosition(position, getCount());
			Log.d("=AAA=","position = " + position);
			return new GiftBannerFragment(appList.get(position));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appList.size();
		}
		
	}
	
	private class GiftBannerFragment extends BaseBannerFragment {
		public GiftBannerResp appInfo = null;
		
		public GiftBannerFragment(GiftBannerResp info) {
			super(info.imgsrc, R.drawable.small_pic_default);
			appInfo = info;
		}
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch(view.getId()) {
			case R.id.iv_playbill:
				break;
			}
		}
	}
	
	class GiftViewHolder {
		ImageView ivImg;
		TextView tvName;
		TextView tvDesc;
		TextView tvExchange;
		TextView tvFlowCoins;
	}
	
	private class GiftAdatper extends BaseAdapter {

		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		private List<GiftProduct> appList = null;
		Context context;
		private LayoutInflater inflater;
		
		public GiftAdatper(Context context,List<GiftProduct> list) {
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
		public GiftProduct getItem(int arg0) {
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
			GiftViewHolder holder = null;
			if (convertView == null) {
				holder = new GiftViewHolder();
				convertView = inflater.inflate(R.layout.item_gift_exchange, null);
				holder.ivImg = (ImageView) convertView.findViewById(R.id.iv_img);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_gift_name);
				holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_gift_desc);
				holder.tvExchange = (TextView) convertView.findViewById(R.id.tv_gift_exchange);
				holder.tvFlowCoins = (TextView) convertView.findViewById(R.id.tv_flow_coins);
				
				ViewUtils.setHeight(convertView.findViewById(R.id.view_height), 152);
				ViewUtils.setSize(holder.ivImg, 200, 120);
				ViewUtils.setSize(holder.tvExchange, 112, 50);
				ViewUtils.setMarginLeft(holder.ivImg, 32);
				ViewUtils.setMarginLeft(holder.tvName, 22);
				ViewUtils.setMarginTop(holder.tvName, 26);
				ViewUtils.setMarginTop(holder.tvDesc, 18);
				ViewUtils.setMarginTop(holder.tvExchange, 28);
				ViewUtils.setMarginRight(holder.tvExchange, 32);
				ViewUtils.setMarginTop(holder.tvFlowCoins, 20);
				ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_bottom), 32);
				ViewUtils.setMarginRight(convertView.findViewById(R.id.view_bottom), 32);
				
				ViewUtils.setTextSize(holder.tvName, 28);
				ViewUtils.setTextSize(holder.tvDesc, 23);
				ViewUtils.setTextSize(holder.tvExchange, 26);
				ViewUtils.setTextSize(holder.tvFlowCoins, 21);
				
				convertView.setTag(holder);
				
			} else {
				holder = (GiftViewHolder) convertView.getTag();
			}
			
			final GiftProduct item = appList.get(position);
			imageLoader.displayImage(item.imgsrc, holder.ivImg,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvDesc.setText(item.giftdesc);
			holder.tvFlowCoins.setText(item.flowcoins + "流量币");
			holder.tvExchange.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
					if (accountInfo != null) {
						Requester.exchangeGift(true, handler, accountInfo.getUserid(), item.giftid);
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
		case Requester.RESPONSE_TYPE_GIFT_LIST:
			if (msg.obj != null) {
				GiftListResp response = (GiftListResp) msg.obj;
				if(response.status.equals("0000") || response.status.equals("0")) {
					bannerList.clear();
					giftList.clear();
					if(response.bannerlist != null && response.bannerlist.length > 0) {
						Collections.addAll(bannerList, response.bannerlist);
						viewPager.setAdapter(bannerAdapter);
						viewPager.setCurrentItem(0);
						mIndicator.setViewPager(viewPager);
						mIndicator.notifyDataSetChanged();
					}
					if(response.giftlist != null && response.giftlist.length > 0) {
						for (GiftResp item:response.giftlist) {
							if (item.products != null && item.products.length > 0) {
								Collections.addAll(giftList, item.products);
							}
						}
						adapter.notifyDataSetChanged();
						ViewUtils.setHeightPixel(listView,ViewUtils.getListHeight(listView, DisplayUtil.getSize(getActivity(), 152)));
					}
					
				}
			}
			break;
		}
		return false;
	}
}
