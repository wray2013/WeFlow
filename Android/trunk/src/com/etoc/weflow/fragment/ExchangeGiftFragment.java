package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.net.GsonResponseObject.ExchangeGiftResp;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.imbryk.viewPager.LoopViewPager;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.PageIndicator;

public class ExchangeGiftFragment extends Fragment {
	private View mView;
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private GiftBannerAdapter bannerAdapter;
	private PullToRefreshScrollView ptrScrollView = null;
	
	private ListView listView = null;
	private GiftAdatper adapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
        
        bannerAdapter = new GiftBannerAdapter(getChildFragmentManager(),  makeFakeData());
        viewPager.setAdapter(bannerAdapter);
        
        mIndicator.setViewPager(viewPager);
		mIndicator.notifyDataSetChanged();
		
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
		
		listView = (ListView) view.findViewById(R.id.lv_gift_exchange);
		adapter = new GiftAdatper(getActivity(), makeGiftData());
		listView.setAdapter(adapter);
		
		ViewUtils.setHeightPixel(listView,ViewUtils.getListHeight(listView, DisplayUtil.getSize(getActivity(), 152)));
	}
	
	private List<ExchangeGiftResp> makeGiftData() {
		List<ExchangeGiftResp> list = new ArrayList<ExchangeGiftResp>();
		
		String[] imgUrls = {"http://pic7.nipic.com/20100526/3726655_170231009273_2.jpg",
        		"http://pic5.nipic.com/20100102/3759236_100017502126_2.jpg",
        		"http://pic8.nipic.com/20100722/4235094_143649006971_2.jpg",
        		"http://pic1.nipic.com/2008-12-23/2008122312587944_2.jpg",
        		"http://img1.imgtn.bdimg.com/it/u=3517413395,2250230838&fm=21&gp=0.jpg"
        		};
		String[] titles = {"庐山月饼",
				"菲尼迪100元礼券",
				"乐行仕优惠券",
				"茅台礼券",
				"阳澄湖大闸蟹"
		};
		
		String[] descs = {
				"9月6日下午14:00-16:30，新湖庐山国际将浓情上演中秋月饼DIY家庭聚会",
				"菲妮迪女装 2014秋装新款 经典简约菱格时尚撞色薄款棉衣外套",
				"乐行仕作为目前国内休闲皮鞋网络第一品牌,自成立之初,便始终对男士高档皮鞋及配套耐用",
				"大曲酱香型白酒的鼻祖，有“国酒”之称，是中国最高端白酒之一",
				"蟹身不沾泥，俗称清水大闸蟹，体大膘肥，青壳白肚，金爪黄毛",
		};
		for (int i = 0;i < 5;i++) {
			ExchangeGiftResp resp = new ExchangeGiftResp();
			resp.giftid = i + "";
			resp.imgsrc = imgUrls[i];
			resp.title = titles[i];
			resp.giftdesc = descs[i];
			resp.flowcoins = ((i + 1) * 1000) + "";
			list.add(resp);
		}
		
		return list;
	}
	
	private List<ExchangeGiftResp> makeFakeData() {
		List<ExchangeGiftResp> list = new ArrayList<ExchangeGiftResp>();
		
		String[] imgUrls = {"http://www.adzop.com//uploadpic/xcp/1412/P190.rmvb_20141222_110554.306.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P186.rmvb_20141222_110108.278.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P184.rmvb_20141222_110040.713.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P176.rmvb_20141222_105653.404.jpg"
        		};
		for (int i = 0;i < 4;i++) {
			ExchangeGiftResp resp = new ExchangeGiftResp();
			resp.giftid = i + "";
			resp.imgsrc = imgUrls[i];
			list.add(resp);
		}
		return list;
	}
	
	private class GiftBannerAdapter extends FragmentPagerAdapter {

		private List<ExchangeGiftResp> appList = null;
		
		public GiftBannerAdapter(FragmentManager fm,List<ExchangeGiftResp> list) {
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
		public ExchangeGiftResp appInfo = null;
		
		public GiftBannerFragment(ExchangeGiftResp info) {
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
		
		private List<ExchangeGiftResp> appList = null;
		Context context;
		private LayoutInflater inflater;
		
		public GiftAdatper(Context context,List<ExchangeGiftResp> list) {
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
		public ExchangeGiftResp getItem(int arg0) {
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
			
			ExchangeGiftResp item = appList.get(position);
			imageLoader.displayImage(item.imgsrc, holder.ivImg,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvDesc.setText(item.giftdesc);
			holder.tvFlowCoins.setText(item.flowcoins + "流量币");
			return convertView;
		}
		
	}
}
