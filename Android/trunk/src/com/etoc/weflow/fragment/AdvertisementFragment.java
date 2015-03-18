package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Arrays;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.etoc.weflow.activity.AdDetailActivity;
import com.etoc.weflow.adapter.BannerAdapter;
import com.etoc.weflow.net.GsonResponseObject.AdverInfo;
import com.etoc.weflow.net.GsonResponseObject.AdvListMoreResp;
import com.etoc.weflow.net.GsonResponseObject.AdvListResp;
import com.etoc.weflow.net.Requester;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.PageIndicator;

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AdvertisementFragment extends Fragment implements OnClickListener, OnRefreshListener2<ScrollView>, Callback {

	private final String TAG = "AdvertisementFragment";
	private View mView;
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	
	private ListView lvRecommentAdv;
	private AdvListAdapter adapter;
	
	private PullToRefreshScrollView ptrScrollView = null;
	private BannerAdapter bannerAdapter = null;
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	
	private ImageView ivNewest1 = null;
	private ImageView ivNewest2 = null;
	private ImageView ivNewest3 = null;
	
	private RelativeLayout rlAd1 = null;
	private RelativeLayout rlAd2 = null;
	private RelativeLayout rlAd3 = null;
	
	private Handler handler;
	
	private ArrayList<AdverInfo> bannerlist = new ArrayList<AdverInfo>();
	private ArrayList<AdverInfo> newestlist = new ArrayList<AdverInfo>();
	private ArrayList<AdverInfo> wonderfullist = new ArrayList<AdverInfo>();
	
	private Boolean isClearData 	= true;
	private Boolean isHasNextPage 	= false;
	private int currentpage = 0;
	private int itemHeight = 0;
	
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
		handler = new Handler(this);
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_advertisement, null);
		mView = v;
		initView(v);
		
		return v;
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		lvRecommentAdv =  (ListView) view.findViewById(R.id.lv_recomment_ad);
		adapter = new AdvListAdapter(getActivity(), wonderfullist);
		lvRecommentAdv.setAdapter(adapter);
		setListViewHeightBasedOnChildren(lvRecommentAdv);
		
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) view.findViewById(R.id.indicator_service);
        
        bannerAdapter = new BannerAdapter(getChildFragmentManager(), R.drawable.small_pic_default, bannerlist/*makeFakeData()*/);
        if(bannerAdapter.getCount() > 0) {
        	viewPager.setAdapter(bannerAdapter);
        	mIndicator.setViewPager(viewPager);
        	mIndicator.notifyDataSetChanged();
        }
		
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.small_pic_default)
				.showImageOnFail(R.drawable.small_pic_default)
				.showImageOnLoading(R.drawable.small_pic_default)
				.displayer(new RoundedBitmapDisplayer(5))
//				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				.build();
		
		rlAd1 = (RelativeLayout) view.findViewById(R.id.rl_newest_1);
		rlAd2 = (RelativeLayout) view.findViewById(R.id.rl_newest_2);
		rlAd3 = (RelativeLayout) view.findViewById(R.id.rl_newest_3);
		
		rlAd1.setOnClickListener(this);
		rlAd2.setOnClickListener(this);
		rlAd3.setOnClickListener(this);
		
		ivNewest1 = (ImageView) view.findViewById(R.id.iv_ad_img_1);
		ivNewest2 = (ImageView) view.findViewById(R.id.iv_ad_img_2);
		ivNewest3 = (ImageView) view.findViewById(R.id.iv_ad_img_3);
		
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
		ptrScrollView.setOnRefreshListener(this);
		
		refreshView();
		
		Requester.getAdvList(true, handler);
	}
	
	private void refreshView() {
		
		rlAd1.setVisibility(View.INVISIBLE);
		rlAd2.setVisibility(View.INVISIBLE);
		rlAd3.setVisibility(View.INVISIBLE);
		
		if(newestlist != null) {
			int size = newestlist.size();
			if(size >= 3) {
				rlAd1.setVisibility(View.VISIBLE);
				rlAd2.setVisibility(View.VISIBLE);
				rlAd3.setVisibility(View.VISIBLE);
				rlAd1.setTag(newestlist.get(0));
				rlAd2.setTag(newestlist.get(1));
				rlAd3.setTag(newestlist.get(2));
				imageLoader.displayImage(newestlist.get(0).cover, ivNewest1, imageLoaderOptions);
				imageLoader.displayImage(newestlist.get(1).cover, ivNewest2, imageLoaderOptions);
				imageLoader.displayImage(newestlist.get(2).cover, ivNewest3, imageLoaderOptions);
			} else if(size == 2) {
				rlAd1.setVisibility(View.VISIBLE);
				rlAd2.setVisibility(View.VISIBLE);
				rlAd3.setVisibility(View.INVISIBLE);
				rlAd1.setTag(newestlist.get(0));
				rlAd2.setTag(newestlist.get(1));
				imageLoader.displayImage(newestlist.get(0).cover, ivNewest1, imageLoaderOptions);
				imageLoader.displayImage(newestlist.get(1).cover, ivNewest2, imageLoaderOptions);
			} else if(size == 1) {
				rlAd1.setVisibility(View.VISIBLE);
				rlAd2.setVisibility(View.INVISIBLE);
				rlAd3.setVisibility(View.INVISIBLE);
				rlAd1.setTag(newestlist.get(0));
				imageLoader.displayImage(newestlist.get(0).cover, ivNewest1, imageLoaderOptions);
			}
		}
		
		if(bannerlist != null) {
			if(bannerAdapter.getCount() > 0) {
	        	viewPager.setAdapter(bannerAdapter);
	        	mIndicator.setViewPager(viewPager);
	        	mIndicator.notifyDataSetChanged();
	        }
		}
		
	}
	
	
	
	private ArrayList<AdverInfo> makeFakeData() {
		ArrayList<AdverInfo> adList = new ArrayList<AdverInfo>();
        String[] imgUrls = {"http://www.adzop.com//uploadpic/xcp/1412/P190.rmvb_20141222_110554.306.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P186.rmvb_20141222_110108.278.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P184.rmvb_20141222_110040.713.jpg",
        		};
        String[] contents = {"2014地球一小时 Earth Hour 高清宣传片",
        		"Switzerland 瑞士旅游高清宣传片 冰天雪地雪山",
        		"国外知名制药企业高清宣传片 细胞素材 病毒扩散"
        		};
        String[] titles = {"珍惜地球",
        		"瑞士旅游",
        		"病毒扩散"
        };
        String[] videoUrls = {
        		"http://v.adzop.com/xcp/1412/P190.mp4",
        		"http://v.adzop.com/xcp/1412/P186.mp4",
        		"http://v.adzop.com/xcp/1412/P184.mp4",
        		
        };
        
        for (int i = 0;i < 3;i++) {
        	AdverInfo info = new AdverInfo();
        	info.content = contents[i];
        	info.title = titles[i];
        	info.cover = imgUrls[i];
        	info.video = videoUrls[i];
        	
        	adList.add(info);
        }
        return adList;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_newest_1:
		case R.id.rl_newest_2:
		case R.id.rl_newest_3:
			AdverInfo info = (AdverInfo) v.getTag();
			Intent i = new Intent(getActivity(), AdDetailActivity.class);
			i.putExtra("adinfo", new Gson().toJson(info));
			startActivity(i);
			break;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		Requester.getAdvList(false, handler);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		if(!isHasNextPage && !isClearData) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					ptrScrollView.onRefreshComplete();
				}
			}, 5);
		} else {
			Requester.getMoreAdvList(false, handler, currentpage + "");
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		ptrScrollView.onRefreshComplete();
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_ADV_LIST:
			if(msg.obj != null) {
				AdvListResp response = (AdvListResp) msg.obj;
				if(response.status.equals("0000") || response.status.equals("0")) {
					currentpage = 0;
					isClearData = true;
					bannerlist.clear();
					newestlist.clear();
					wonderfullist.clear();
					if(response.bannerlist != null) {
						bannerlist.addAll(Arrays.asList(response.bannerlist));
					}
					if(response.newestlist != null) {
						newestlist.addAll(Arrays.asList(response.newestlist));
					}
					if(response.wonderfullist != null) {
						wonderfullist.addAll(Arrays.asList(response.wonderfullist));
					}
					refreshView();
				}
			}
			break;
		case Requester.RESPONSE_TYPE_ADV_MORE:
			if(msg.obj != null) {
				AdvListMoreResp moreresp = (AdvListMoreResp) msg.obj;
				if(moreresp.status.equals("0000") || moreresp.status.equals("0")) {
					currentpage ++;
					isClearData = false;
					int pagesize = 0;
					if(moreresp.hasnextpage != null) {
						try {
							pagesize = Integer.parseInt(moreresp.hasnextpage);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					isHasNextPage = currentpage < pagesize;
					if(moreresp.list != null && moreresp.list.length > 0) {
						wonderfullist.addAll(Arrays.asList(moreresp.list));
						adapter.setData(wonderfullist);
					}
					
				}
			}
			break;
		}
		return false;
	}
	
	/***
     * 动态设置listview的高度
     * 
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
//    	if(!TAG_ON) return;
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() <= 0) {
            return;
        }
        
        if(itemHeight <= 0) {
        	/*View listItem = listAdapter.getView(0, null, listView);
        	listItem.measure(0, 0);
        	itemHeight = listItem.getMeasuredHeight();*/
        	itemHeight = DisplayUtil.getSize(getActivity(), 140);
        }
        
        int totalHeight = 0;
        /*for (int i = 0; i < listAdapter.getCount(); i++) {
        	View listItem = listAdapter.getView(i, null, listView);
        	listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }*/
        totalHeight = listAdapter.getCount() * itemHeight;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    
	class AdvListAdapter extends BaseAdapter {

		ArrayList<AdverInfo> infoList = new ArrayList<AdverInfo>();
		private LayoutInflater inflater;
		private Context context;
		
		public AdvListAdapter(Context context,ArrayList<AdverInfo> list) {
			this.infoList = list;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		public void setData(ArrayList<AdverInfo> list) {
			if(list != null) {
				infoList.clear();
				infoList.addAll(list);
			}
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infoList.size();
		}

		@Override
		public AdverInfo getItem(int position) {
			// TODO Auto-generated method stub
			return infoList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			AdvHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_recommend_adv, null);
				
				holder = new AdvHolder();
				holder.imgView = (ImageView)convertView.findViewById(R.id.iv_adv_img);
				holder.tvContent = (TextView)convertView.findViewById(R.id.tv_title);
				holder.tvDuration = (TextView)convertView.findViewById(R.id.tv_duration);
				holder.tvScore = (TextView)convertView.findViewById(R.id.tv_adv_flow);
				
				ViewUtils.setHeight(holder.imgView, 340);
				ViewUtils.setHeight(convertView.findViewById(R.id.view_space), 452);
				ViewUtils.setMarginTop(holder.imgView, 16);
				ViewUtils.setMarginLeft(holder.imgView, 32);
				ViewUtils.setMarginRight(holder.imgView, 32);
				ViewUtils.setSize(convertView.findViewById(R.id.rl_flowcoins), 72, 96);
				ViewUtils.setMarginRight(convertView.findViewById(R.id.rl_flowcoins), 16);
				ViewUtils.setMarginTop(holder.tvScore, 12);
				ViewUtils.setTextSize(holder.tvScore, 30);
				ViewUtils.setTextSize(convertView.findViewById(R.id.tv_app_flow_label), 10);
				
				ViewUtils.setMarginTop(convertView.findViewById(R.id.rl_desc), 32);
				ViewUtils.setTextSize(holder.tvContent, 34);
				ViewUtils.setTextSize(holder.tvDuration, 24);
				
				convertView.setTag(holder);
			} else {
				holder = (AdvHolder)convertView.getTag();
			}
			
			AdverInfo info = infoList.get(position);
			imageLoader.displayImage(info.cover, holder.imgView, imageLoaderOptions);
			holder.tvContent.setText(info.title);
			holder.tvDuration.setText(info.duration + "s");
			holder.tvScore.setText(info.flowcoins);
			
			return convertView;
		}
		
		class AdvHolder {
			ImageView imgView;
			TextView tvContent;
			TextView tvDuration;
			TextView tvScore;
		}
		
	}
	
}
