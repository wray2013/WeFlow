package com.etoc.weflowdemo.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.activity.AdvertActivity.AdAdapter;
import com.etoc.weflowdemo.adapter.BannerAdapter;
import com.etoc.weflowdemo.util.DisplayUtil;
import com.etoc.weflowdemo.util.ViewUtils;
import com.etoc.weflowdemo.view.autoscrollviewpager.AutoScrollViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.viewpagerindicator.PageIndicator;

public class AdvertActivity extends TitleRootActivity {

	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private GridView gvRecommentAd = null;
	BannerAdapter bannerAdapter = null;
	private AdAdapter adapter = null;
	private PullToRefreshScrollView ptrScrollView = null;
	ArrayList<Integer> imgIds = new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		setTitleText("看视频");
		hideRightButton();
//		setRightButtonText("记录");
		
		viewPager = (AutoScrollViewPager) findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) findViewById(R.id.indicator_service);
        int [] adRes = {R.drawable.banner_ad_1,R.drawable.banner_ad_2,R.drawable.banner_ad_3};
        for (int i = 0;i < 3; i++) {
        	imgIds.add(adRes[i]);
        }
        bannerAdapter = new BannerAdapter(getSupportFragmentManager(), 0, imgIds);
        viewPager.setAdapter(bannerAdapter);
        
        mIndicator.setViewPager(viewPager);
		mIndicator.notifyDataSetChanged();
        
        gvRecommentAd = (GridView) findViewById(R.id.gv_recomment_ad);
        ArrayList<AdInfo> infoList = new ArrayList<AdvertActivity.AdInfo>();
        int [] resArr = {R.drawable.rec_ad_1,R.drawable.rec_ad_2,R.drawable.rec_ad_3,R.drawable.rec_ad_4};
        for(int i = 0;i < 4;i++) {
        	AdInfo info = new AdInfo();
        	info.imgId = resArr[i];
        	info.content = "广告文字...";
        	info.scroe = (20 + i * 10) + "";
        	infoList.add(info);
        }
        adapter = new AdAdapter(this, infoList);
        gvRecommentAd.setAdapter(adapter);
        int gridHeight = getGridViewHeight(gvRecommentAd);
		ViewUtils.setHeightPixel(gvRecommentAd, gridHeight);
		
		gvRecommentAd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AdvertActivity.this, AdDetailActivity.class);
				startActivity(i);
				
			}
		});
		
		findViewById(R.id.rl_newest_1).setOnClickListener(this);
		findViewById(R.id.rl_newest_2).setOnClickListener(this);
		findViewById(R.id.rl_newest_3).setOnClickListener(this);
		
		ptrScrollView = (PullToRefreshScrollView) findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_newest_1:
		case R.id.rl_newest_2:
		case R.id.rl_newest_3:
			Intent i = new Intent(AdvertActivity.this, AdDetailActivity.class);
			startActivity(i);
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	private int getGridViewHeight(GridView gridView) {
		int count = gridView.getAdapter().getCount();
		int rowNum = (int)Math.ceil(count / (double)2);
		int height = DisplayUtil.getSize(this, 202) * rowNum  + cn.trinea.android.common.util.ViewUtils.getGridViewVerticalSpacing(gridView) * (rowNum + 1);
		return height;
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_advert;
	}
	
	class AdInfo {
		int imgId;
		String content;
		String scroe;
	}
	
	class AdHolder {
		ImageView imgView;
		TextView tvContent;
		TextView tvScore;
	}
	
	class AdAdapter extends BaseAdapter {

		ArrayList<AdInfo> infoList = new ArrayList<AdInfo>();
		private LayoutInflater inflater;
		private Context context;
		
		public AdAdapter(Context context,ArrayList<AdInfo> list) {
			this.infoList = list;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infoList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			AdHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_recomment_ad, null);
				
				holder = new AdHolder();
				holder.imgView = (ImageView)convertView.findViewById(R.id.iv_ad_img);
				holder.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
				holder.tvScore = (TextView)convertView.findViewById(R.id.tv_score);
				
				ViewUtils.setHeight(convertView, 202);
				convertView.setTag(holder);
			} else {
				holder = (AdHolder)convertView.getTag();
			}
			
			AdInfo info = infoList.get(position);
			holder.imgView.setImageResource(info.imgId);
			holder.tvContent.setText(info.content);
			holder.tvScore.setText(info.scroe + "流量币");
			
			return convertView;
		}
		
	}

}
