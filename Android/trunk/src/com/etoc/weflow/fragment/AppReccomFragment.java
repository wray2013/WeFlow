package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.MakeFlowActivity;
import com.etoc.weflow.activity.SoftDetailActivity;
import com.etoc.weflow.download.DownloadManager;
import com.etoc.weflow.download.DownloadType;
import com.etoc.weflow.event.MakeFlowFragmentEvent;
import com.etoc.weflow.net.GsonRequestObject.appListRequest;
import com.etoc.weflow.net.GsonResponseObject.AdvListResp;
import com.etoc.weflow.net.GsonResponseObject.AppHomeResp;
import com.etoc.weflow.net.GsonResponseObject.AppListMoreResp;
import com.etoc.weflow.net.GsonResponseObject.SoftInfoResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.imbryk.viewPager.LoopViewPager;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.PageIndicator;

import de.greenrobot.event.EventBus;

public class AppReccomFragment extends Fragment implements Callback, OnRefreshListener<ScrollView> {
	private View mView;
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private AppBannerAdapter bannerAdapter;
	private PullToRefreshScrollView ptrScrollView = null;
	private ListView listView = null;
	private AppAdatper adapter = null;
	private List<SoftInfoResp> bannerList = new ArrayList<SoftInfoResp>();
	private List<SoftInfoResp> appList = new ArrayList<SoftInfoResp>();
	private Handler handler = null;
	private int currentPage = 0;
	private boolean hasNextPage = true;
	
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
		ViewUtils.setHeight(view.findViewById(R.id.rl_view_pager), 360);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) view.findViewById(R.id.indicator_service);
        
        bannerAdapter = new AppBannerAdapter(getChildFragmentManager(),  bannerList);
		
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("正在加载");
		ptrScrollView.setReleaseLabel("松开加载更多");
		ptrScrollView.setOnRefreshListener(this);
		
		listView = (ListView) view.findViewById(R.id.lv_app_recomm);
		
		adapter = new AppAdatper(getActivity(), appList);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SoftDetailActivity.class);
				SoftInfoResp item = (SoftInfoResp)listView.getAdapter().getItem(position);
				intent.putExtra(ConStant.INTENT_SOFT_DETAIL, new Gson().toJson(item));
				startActivity(intent);
			}
		});
		
		ViewUtils.setHeightPixel(listView,ViewUtils.getListHeight(listView, DisplayUtil.getSize(getActivity(), 152)));
	}
	
	public void onEvent(MakeFlowFragmentEvent event) {
		if (event.getIndex() == MakeFlowActivity.INDEX_APPRECOMM) {
			if (bannerList.size() == 0) {
				Requester.getAppHome(true, handler);
			}
		}
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
			super(info.appbannerpic, R.drawable.content_pic_default);
			appInfo = info;
		}
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch(view.getId()) {
			case R.id.rl_item_view_pager:
				Intent intent = new Intent(getActivity(),SoftDetailActivity.class);
				intent.putExtra(ConStant.INTENT_SOFT_DETAIL, new Gson().toJson(appInfo));
				startActivity(intent);
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
			Log.d("=AAA=","getCount = " + appList.size());
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
			
			final SoftInfoResp item = appList.get(position);
			imageLoader.displayImage(item.appicon, holder.ivIcon,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvDesc.setText(item.introduction);
			holder.tvFlowCoins.setText("可赚" + NumberUtils.convert2IntStr(item.flowcoins) + "流量币");
			
			holder.tvDownload.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					DownloadManager.getInstance().addDownloadTask(item.soft, item.appid, item.title, item.appicon, "",  DownloadType.APP, "","","", "");
					
					Intent intent = new Intent(getActivity(),SoftDetailActivity.class);
					intent.putExtra(ConStant.INTENT_SOFT_DETAIL, new Gson().toJson(item));
					startActivity(intent);
				}
			});
			return convertView;
		}
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_APP_HOME:
			ptrScrollView.onRefreshComplete();
			if(msg.obj != null) {
				AppHomeResp response = (AppHomeResp) msg.obj;
				if(response.status.equals("0000") || response.status.equals("0")) {
					currentPage = 0;
					bannerList.clear();
					appList.clear();
					if(response.bannerlist != null && response.bannerlist.length > 0) {
						Collections.addAll(bannerList, response.bannerlist);
						viewPager.setAdapter(bannerAdapter);
						viewPager.setCurrentItem(0);
						mIndicator.setViewPager(viewPager);
						mIndicator.notifyDataSetChanged();
					}
					if(response.applist != null && response.applist.length > 0) {
						Collections.addAll(appList, response.applist);
						adapter.notifyDataSetChanged();
						ViewUtils.setHeightPixel(listView,ViewUtils.getListHeight(listView, DisplayUtil.getSize(getActivity(), 152)));
						currentPage = 1;
					}
					
				}
			}
			break;
		case Requester.RESPONSE_TYPE_APP_LIST:
			ptrScrollView.onRefreshComplete();
			if (msg.obj != null) {
				AppListMoreResp resp = (AppListMoreResp) msg.obj;
				if(resp.status.equals("0000") || resp.status.equals("0")) {
					if (resp.list != null && resp.list.length > 0) {
						currentPage++;
						hasNextPage = "1".equals(resp.hasnextpage);
						Collections.addAll(appList, resp.list);
						adapter.notifyDataSetChanged();
						ViewUtils.setHeightPixel(listView,ViewUtils.getListHeight(listView, DisplayUtil.getSize(getActivity(), 152)));
					}
				}
			}
			break;
		}
		return false;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		if (bannerList.size() == 0) {
			Requester.getAppHome(true, handler);
		} else if (hasNextPage){
			Requester.getMoreAppList(true, handler, (currentPage) + "");
		} else {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ptrScrollView.onRefreshComplete();
				}
			});
		}
	}
	
	
}
