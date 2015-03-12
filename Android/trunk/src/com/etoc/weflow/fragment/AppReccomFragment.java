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
import com.etoc.weflow.R.id;
import com.etoc.weflow.activity.MainActivity;
import com.etoc.weflow.net.GsonResponseObject.SoftInfoResp;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.imbryk.viewPager.LoopViewPager;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.PageIndicator;

public class AppReccomFragment extends Fragment {
	private View mView;
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private AppBannerAdapter bannerAdapter;
	private PullToRefreshScrollView ptrScrollView = null;
	private ListView listView = null;
	private AppAdatper adapter = null;
	
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
		
		View v = inflater.inflate(R.layout.fragment_app_reccom, null);
		mView = v;
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) view.findViewById(R.id.indicator_service);
        
        bannerAdapter = new AppBannerAdapter(getChildFragmentManager(),  makeFakeData());
        viewPager.setAdapter(bannerAdapter);
        
        mIndicator.setViewPager(viewPager);
		mIndicator.notifyDataSetChanged();
		
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
		
		listView = (ListView) view.findViewById(R.id.lv_app_recomm);
		
		adapter = new AppAdatper(getActivity(), makeAppData());
		listView.setAdapter(adapter);
		
		ViewUtils.setHeightPixel(listView,ViewUtils.getListHeight(listView, DisplayUtil.getSize(getActivity(), 152)));
	}
	
	
	
	private List<SoftInfoResp> makeAppData() {
		List<SoftInfoResp> list = new ArrayList<SoftInfoResp>();
		
		String[] icons = {"http://ico.ooopic.com/ajax/iconpng/?id=137122.png",
				"http://pic27.nipic.com/20130313/3849388_101225310324_2.jpg",
				"http://ico.ooopic.com/ajax/iconpng/?id=319307.png",
				"http://img0.imgtn.bdimg.com/it/u=3348450869,464969419&fm=21&gp=0.jpg",
				"http://www.sucaijiayuan.com/uploads/file/contents/2014/01/52c6dcfc0c75d.png"
		};
		String[] titles = {
				"微话","美拍","苹果商店","麦当劳","爱买"
		};
		String[] descs = {
				"沟通无极限","做最美的自己","高富帅必备","叔叔约约约","没有你买不到的。"
		};
		for (int i = 0;i < 5;i++) {
			SoftInfoResp resp = new SoftInfoResp();
			resp.appid = i + "";
			resp.appicon = icons[i];
			resp.title = titles[i];
			resp.introduction = descs[i];
			resp.flowcoins = (i*10 + 10) + "";
			list.add(resp);
		}
		return list;
	}
	
	
	private List<SoftInfoResp> makeFakeData() {
		List<SoftInfoResp> list = new ArrayList<SoftInfoResp>();
		
		String[] imgUrls = {"http://www.adzop.com//uploadpic/xcp/1412/P190.rmvb_20141222_110554.306.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P186.rmvb_20141222_110108.278.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P184.rmvb_20141222_110040.713.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P176.rmvb_20141222_105653.404.jpg"
        		};
		for (int i = 0;i < 4;i++) {
			SoftInfoResp resp = new SoftInfoResp();
			resp.appid = i + "";
			resp.appbannerpic = imgUrls[i];
			list.add(resp);
		}
		return list;
	}
	
	private class AppBannerAdapter extends FragmentPagerAdapter {

		private List<SoftInfoResp> appList = null;
		
		public AppBannerAdapter(FragmentManager fm,List<SoftInfoResp> list) {
			// TODO Auto-generated constructor stub
			super(fm);
			
			appList = list;
		}
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			position = LoopViewPager.toRealPosition(position, getCount());
			Log.d("=AAA=","position = " + position);
			return new AppBannerFragment(appList.get(position));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appList.size();
		}
		
	}
	
	private class AppBannerFragment extends BaseBannerFragment {
		public SoftInfoResp appInfo = null;
		
		public AppBannerFragment(SoftInfoResp info) {
			super(info.appbannerpic, R.drawable.small_pic_default);
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
	
	class AppViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvDesc;
		TextView tvDownload;
		TextView tvFlowCoins;
	}
	
	private class AppAdatper extends BaseAdapter {

		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		private List<SoftInfoResp> appList = null;
		Context context;
		private LayoutInflater inflater;
		
		public AppAdatper(Context context,List<SoftInfoResp> list) {
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
		public SoftInfoResp getItem(int arg0) {
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
			AppViewHolder holder = null;
			if (convertView == null) {
				holder = new AppViewHolder();
				convertView = inflater.inflate(R.layout.item_app_recomm, null);
				holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_app_name);
				holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_app_desc);
				holder.tvDownload = (TextView) convertView.findViewById(R.id.tv_app_download);
				holder.tvFlowCoins = (TextView) convertView.findViewById(R.id.tv_flow_coins);
				
				ViewUtils.setHeight(convertView.findViewById(R.id.view_height), 152);
				ViewUtils.setSize(holder.ivIcon, 100, 100);
				ViewUtils.setSize(holder.tvDownload, 112, 50);
				ViewUtils.setMarginLeft(holder.ivIcon, 32);
				ViewUtils.setMarginLeft(holder.tvName, 40);
				ViewUtils.setMarginTop(holder.tvName, 40);
				ViewUtils.setMarginTop(holder.tvDesc, 14);
				ViewUtils.setMarginTop(holder.tvDownload, 28);
				ViewUtils.setMarginRight(holder.tvDownload, 32);
				ViewUtils.setMarginTop(holder.tvFlowCoins, 20);
				ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_bottom), 32);
				ViewUtils.setMarginRight(convertView.findViewById(R.id.view_bottom), 32);
				
				ViewUtils.setTextSize(holder.tvName, 32);
				ViewUtils.setTextSize(holder.tvDesc, 24);
				ViewUtils.setTextSize(holder.tvDownload, 26);
				ViewUtils.setTextSize(holder.tvFlowCoins, 20);
				
				convertView.setTag(holder);
				
			} else {
				holder = (AppViewHolder) convertView.getTag();
			}
			
			SoftInfoResp item = appList.get(position);
			imageLoader.displayImage(item.appicon, holder.ivIcon,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvDesc.setText(item.introduction);
			holder.tvFlowCoins.setText("可赚" + item.flowcoins + "流量币");
			return convertView;
		}
		
	}
	
	
}
