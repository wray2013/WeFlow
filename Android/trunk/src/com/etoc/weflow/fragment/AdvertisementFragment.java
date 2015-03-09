package com.etoc.weflow.fragment;

import java.util.ArrayList;

import com.etoc.weflow.R;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.etoc.weflow.adapter.BannerAdapter;
import com.etoc.weflow.net.GsonResponseObject.AdvInfo;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.PageIndicator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AdvertisementFragment extends Fragment implements OnClickListener {

	private final String TAG = "AdvertisementFragment";
	private View mView;
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	
	private PullToRefreshScrollView ptrScrollView = null;
	private BannerAdapter bannerAdapter = null;
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	
	private ImageView ivNewest1 = null;
	private ImageView ivNewest2 = null;
	private ImageView ivNewest3 = null;
	
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
		View v = inflater.inflate(R.layout.fragment_advertisement, null);
		mView = v;
		initView(v);
		
		return v;
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) view.findViewById(R.id.indicator_service);
        
        bannerAdapter = new BannerAdapter(getActivity().getSupportFragmentManager(), R.drawable.small_pic_default, makeFakeData());
        viewPager.setAdapter(bannerAdapter);
        
        mIndicator.setViewPager(viewPager);
		mIndicator.notifyDataSetChanged();
		
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
		
		AdvInfo info1 = new AdvInfo();
		AdvInfo info2 = new AdvInfo();
		AdvInfo info3 = new AdvInfo();
		
		info1.coverurl = "http://www.adzop.com//uploadpic/xcp/1412/P176.rmvb_20141222_105653.404.jpg";
		info1.title = "情侣婚纱照";
		info1.content = "某婚纱品牌高清宣传片 情侣婚纱照 ";
		info1.videourl = "http://v.adzop.com/xcp/1412/P176.mp4";
		
		info2.coverurl = "http://www.adzop.com//uploadpic/xcp/1412/P188.rmvb_20141222_110458.801.jpg";
		info2.title = "五金卫浴";
		info2.content = "国内某五金卫浴品牌高清宣传片 骑马唯美 门锁花洒";
		info2.videourl = "http://v.adzop.com/xcp/1412/P188.mp4";
		
		info3.coverurl = "http://www.adzop.com//uploadpic/xcp/1412/P183.rmvb_20141222_110026.111.jpg";
		info3.title = "印度旅游";
		info3.content = "India 印度旅游高清宣传片 实拍素材 人文风景 ";
		info3.videourl = "http://v.adzop.com/xcp/1412/P183.mp4";
		RelativeLayout rlAd1 = (RelativeLayout) view.findViewById(R.id.rl_newest_1);
		RelativeLayout rlAd2 = (RelativeLayout) view.findViewById(R.id.rl_newest_2);
		RelativeLayout rlAd3 = (RelativeLayout) view.findViewById(R.id.rl_newest_3);
		rlAd1.setTag(info1);
		rlAd2.setTag(info2);
		rlAd3.setTag(info3);
		
		rlAd1.setOnClickListener(this);
		rlAd2.setOnClickListener(this);
		rlAd3.setOnClickListener(this);
		
		ivNewest1 = (ImageView) view.findViewById(R.id.iv_ad_img_1);
		ivNewest2 = (ImageView) view.findViewById(R.id.iv_ad_img_2);
		ivNewest3 = (ImageView) view.findViewById(R.id.iv_ad_img_3);
		
		imageLoader.displayImage(info1.coverurl, ivNewest1, imageLoaderOptions);
		imageLoader.displayImage(info2.coverurl, ivNewest2, imageLoaderOptions);
		imageLoader.displayImage(info3.coverurl, ivNewest3, imageLoaderOptions);
		
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
	}
	
	private ArrayList<AdvInfo> makeFakeData() {
		ArrayList<AdvInfo> adList = new ArrayList<AdvInfo>();
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
        	AdvInfo info = new AdvInfo();
        	info.content = contents[i];
        	info.title = titles[i];
        	info.coverurl = imgUrls[i];
        	info.videourl = videoUrls[i];
        	
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
			break;
		}
	}
	
}
