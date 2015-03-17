package com.cmmobi.railwifi.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.trinea.android.common.util.ListUtils;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.FunctionalBuildingActivity;
import com.cmmobi.railwifi.activity.JokeActivity;
import com.cmmobi.railwifi.activity.MoviesActivity;
import com.cmmobi.railwifi.activity.MainActivity.BannerEvent;
import com.cmmobi.railwifi.activity.MusicMainPageActivity;
import com.cmmobi.railwifi.activity.RecommendationActivity;
import com.cmmobi.railwifi.adapter.PlayBillAdapter;
import com.cmmobi.railwifi.adapter.RailServiceBannerAdapter;
import com.cmmobi.railwifi.event.FragmentStatusEvent;
import com.cmmobi.railwifi.network.FakeData;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.network.GsonResponseObject.serviceBannerphotoElem;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.autoscrollviewpager.AutoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.PageIndicator;

import de.greenrobot.event.EventBus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

@SuppressLint("ValidFragment")
public class MediaFragment extends TitleRootFragment implements OnClickListener {
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private List<serviceBannerphotoElem> photoRespList = new ArrayList<GsonResponseObject.serviceBannerphotoElem>();
	
	ImageView ivNoNet;
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	RailServiceBannerAdapter adapter = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		initViews(view);
		
		
		return view;
	}
	
	private void initViews(View view) {
		setTitleText("影音娱乐");
		setLeftButtonBackground(R.drawable.btn_navigation);
		hideRightButton();
		view.findViewById(R.id.iv_watch_film).setOnClickListener(this);
		view.findViewById(R.id.iv_read_book).setOnClickListener(this);
		view.findViewById(R.id.iv_play_jok).setOnClickListener(this);
		view.findViewById(R.id.iv_listenmusic).setOnClickListener(this);
		
		
		ViewUtils.setHeight(view.findViewById(R.id.ll_view_pager), 462);
		ViewUtils.setMarginTop(view.findViewById(R.id.ll_view_pager), 12);
		ViewUtils.setMarginLeft(view.findViewById(R.id.ll_view_pager), 10);
		ViewUtils.setMarginRight(view.findViewById(R.id.ll_view_pager), 10);
		
		View ivRecommendation = (View) view.findViewById(R.id.iv_period);
		View ivBefore = (View) view.findViewById(R.id.iv_previous_period);
		ViewUtils.setSize(ivRecommendation, 567, 97);
		ViewUtils.setSize(ivBefore, 185, 97);
		ViewUtils.setMarginRight(ivRecommendation, -40);
		ViewUtils.setMarginLeft(view.findViewById(R.id.rl_recommend), 12);
		ViewUtils.setMarginRight(view.findViewById(R.id.rl_recommend), 12);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_recommend), 12);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.ll_two_rows), 12);
		ViewUtils.setMarginRight(view.findViewById(R.id.ll_two_rows), 12);
		ViewUtils.setMarginTop(view.findViewById(R.id.ll_two_rows), 12);
		ViewUtils.setMarginBottom(view.findViewById(R.id.ll_two_rows), 12);
		
/*		ViewUtils.setHeight(view.findViewById(R.id.iv_watch_film), 258);
		ViewUtils.setHeight(view.findViewById(R.id.iv_read_book), 258);
		ViewUtils.setHeight(view.findViewById(R.id.iv_play_jok), 258);
		ViewUtils.setHeight(view.findViewById(R.id.iv_listenmusic), 258);
*/		
		ivRecommendation.setOnClickListener(this);
		ivBefore.setOnClickListener(this);
		
		
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.vp_pager);
		/*PlayBillAdapter adpAdapter = new PlayBillAdapter(getActivity().getSupportFragmentManager());
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(FakeData.HTTP_HOST_PREFIX + "/media_main/ban1.jpg");
		list.add(FakeData.HTTP_HOST_PREFIX + "/media_main/ban2.jpg");
		list.add(FakeData.HTTP_HOST_PREFIX + "/media_main/ban3.jpg");
		list.add(FakeData.HTTP_HOST_PREFIX + "/media_main/ban4.jpg");
		
		adpAdapter.setList(list);*/
		
		adapter = new RailServiceBannerAdapter(getChildFragmentManager(),R.drawable.pic_entertainment_default,photoRespList);
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
		
		mIndicator = (PageIndicator) view.findViewById(R.id.indicator);
//		mIndicator.setViewPager(viewPager);
//		mIndicator.notifyDataSetChanged();
		ivNoNet = (ImageView) view.findViewById(R.id.iv_net_error);
		
		getSlidingMenu().addIgnoredView(viewPager);
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Requester.requestMediaBanPhoto(handler);
			}
		}, 250);
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
		
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.pic_entertainment_default)
				.showImageOnFail(R.drawable.pic_entertainment_default)
				.showImageOnLoading(R.drawable.pic_entertainment_default)
//				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				
				.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
				.build();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);
	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}

	public void requestBanner() {
		if (photoRespList.size() == 0) {
			Requester.requestMediaBanPhoto(handler);
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","RailServiceFragment hidden = " + hidden);
		if (hidden) {
			EventBus.getDefault().post(FragmentStatusEvent.MEDIAHIDE);
		} else {
			EventBus.getDefault().post(FragmentStatusEvent.MEDIASHOW);
		}
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_left:
			getSlidingMenu().toggle();
			break;
		case R.id.iv_watch_film:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "av_fun", "1");
			Intent intent = new Intent(getActivity(), MoviesActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_read_book:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "av_fun", "2");
			Intent intent_book = new Intent(getActivity(), FunctionalBuildingActivity.class);
			startActivity(intent_book);
			break;
		case R.id.iv_play_jok:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "av_fun", "3");
			Intent intent_joke = new Intent(getActivity(), JokeActivity.class);
			startActivity(intent_joke);
			break;
		case R.id.iv_listenmusic:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "av_fun", "4");
//			Intent intent_music = new Intent(getActivity(), FunctionalBuildingActivity.class);
//			startActivity(intent_music);
			Intent intent_music = new Intent(getActivity(), MusicMainPageActivity.class);
			startActivity(intent_music);
			break;
		case R.id.iv_period:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			Intent intent_recommend = new Intent(getActivity(), RecommendationActivity.class);
			intent_recommend.putExtra(RecommendationActivity.INTENT_RECOMMENDATION_TAB, "period");
			startActivity(intent_recommend);
			break;
		case R.id.iv_previous_period:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			Intent intent_before = new Intent(getActivity(), RecommendationActivity.class);
			intent_before.putExtra(RecommendationActivity.INTENT_RECOMMENDATION_TAB, "previous");
			startActivity(intent_before);
			break;
		}
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_media;
	}
	
	public void onEvent(BannerEvent event) {
		switch(event) {
		case BANNERSTART:
			if (viewPager != null) {
				viewPager.startAutoScroll();
			}
			break;
		case BANNERSTOP:
			if (viewPager != null) {
				viewPager.stopAutoScroll();
			}
			break;
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_MEDIA_BANPHOTO:
			long startTime = System.currentTimeMillis();
			if (msg.obj != null) {
				GsonResponseObject.serviceBannerphotoResp photoResp = (GsonResponseObject.serviceBannerphotoResp) msg.obj;
				if ("0".equals(photoResp.status)) {
					if (photoResp.list != null && photoResp.list.length > 0) {
						photoRespList.clear();
						Collections.addAll(photoRespList, photoResp.list);
						Log.d("=AAA=","middleTime = " + (System.currentTimeMillis() - startTime));
						viewPager.setAdapter(adapter);
						viewPager.setCurrentItem(0);
						mIndicator.setViewPager(viewPager);
						mIndicator.notifyDataSetChanged();
						ivNoNet.setVisibility(View.GONE);
					} else {
						ivNoNet.setVisibility(View.VISIBLE);
						imageLoader.displayImage("", ivNoNet,imageLoaderOptions);
					}
				} else {
					ivNoNet.setVisibility(View.VISIBLE);
					imageLoader.displayImage("", ivNoNet,imageLoaderOptions);
				}
			} else {
				ivNoNet.setVisibility(View.VISIBLE);
				imageLoader.displayImage("", ivNoNet,imageLoaderOptions);
			}
			long endTime = System.currentTimeMillis();
			Log.d("=AAA=","duration = " + (endTime - startTime));
			break;
		}
		return true;
	}

}
