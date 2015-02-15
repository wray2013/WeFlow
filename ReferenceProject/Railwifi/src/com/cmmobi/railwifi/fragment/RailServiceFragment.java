package com.cmmobi.railwifi.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.CallForHelpActivity;
import com.cmmobi.railwifi.activity.ComplaintSuggestActivity;
import com.cmmobi.railwifi.activity.IntroductionActivity;
import com.cmmobi.railwifi.activity.MainActivity;
import com.cmmobi.railwifi.activity.MainActivity.BannerEvent;
import com.cmmobi.railwifi.activity.OrderShoppingActivity;
import com.cmmobi.railwifi.activity.RailNewsActivity;
import com.cmmobi.railwifi.activity.SatisfactionSurveyActivity;
import com.cmmobi.railwifi.activity.TipActivity;
import com.cmmobi.railwifi.adapter.RailServiceBannerAdapter;
import com.cmmobi.railwifi.event.FragmentStatusEvent;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.serviceBannerphotoElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.autoscrollviewpager.AutoScrollViewPager;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.PageIndicator;

import de.greenrobot.event.EventBus;

public class RailServiceFragment extends TitleRootFragment implements OnClickListener{

	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private final String TAG = "RailServiceFragment";
//	private TextView welcomeTextView = null;
	private List<serviceBannerphotoElem> photoRespList = new ArrayList<GsonResponseObject.serviceBannerphotoElem>();
	ImageView ivNoNet;
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	RailServiceBannerAdapter adapter = null;
	
	private View mView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		
		Log.d(TAG,"onCreate in");
		
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.pic_index_default)
				.showImageOnFail(R.drawable.pic_index_default)
				.showImageOnLoading(R.drawable.pic_index_default)
//				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				  
				.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
				.build();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onDestroy in");
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onAttach in");
		super.onAttach(activity);
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onDetach in");
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
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onDestroyView in");
		super.onDestroyView();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onCreateView in");
		/*if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}*/
		View view = super.onCreateView(inflater, container, savedInstanceState);
		initViews(view);
//		mView = view;
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Requester.requestServiceBannerPhoto(handler);
			}
		}, 250);
		
		return view;
	}
	
	
	public void requestBanner() {
		if (photoRespList.size() == 0) {
			Requester.requestServiceBannerPhoto(handler);
		}
	}
	
	
	private void initViews(View view) {
		setTitleText(getString(R.string.app_name) + "欢迎您");
		if (!StringUtils.isEmpty(MainActivity.railway_name)) {
			setTitleText(MainActivity.railway_name + "欢迎您");
		} else {
			setTitleText(getString(R.string.app_name) + "欢迎您");
		}
		
		setLeftButtonBackground(R.drawable.btn_navigation);
		setRightButtonBackground(R.drawable.btn_rescure);
		
		 
//		welcomeTextView = (TextView) view.findViewById(R.id.tv_warm_welcome);
//		welcomeTextView.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
//		welcomeTextView.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 33));
//		ViewUtils.setMarginLeft(welcomeTextView, 17);
		ImageView ivOrderShopping = (ImageView) view.findViewById(R.id.iv_railway_ording);
		ivOrderShopping.setOnClickListener(this);
		ImageView ivTourism = (ImageView) view.findViewById(R.id.iv_satisfaction_survey);
		ivTourism.setOnClickListener(this);
		ViewUtils.setMarginLeft(ivTourism,  12);
		ImageView ivZixun = (ImageView) view.findViewById(R.id.iv_railway_zixun);
		ivZixun.setOnClickListener(this);
		ImageView ivTips = (ImageView) view.findViewById(R.id.iv_warm_tips);
		ivTips.setOnClickListener(this);
		ViewUtils.setMarginTop(ivTips, 12);
		ImageView ivIntroduce = (ImageView) view.findViewById(R.id.iv_service_introduce);
		ivIntroduce.setOnClickListener(this);
		
		ImageView ivSeggest = (ImageView) view.findViewById(R.id.iv_suggest);
		ivSeggest.setOnClickListener(this);
		
		ViewUtils.setMarginTop(ivIntroduce, 12);
		ViewUtils.setMarginTop(view.findViewById(R.id.ll_introduce), 12);
		ViewUtils.setMarginLeft(view.findViewById(R.id.ll_introduce), 12);
		ViewUtils.setMarginRight(view.findViewById(R.id.ll_introduce), 12);
		ViewUtils.setMarginTop(view.findViewById(R.id.view_bottom), 12);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_view_pager), 345);
		ViewUtils.setHeight(view.findViewById(R.id.ll_row1), 220);
		ViewUtils.setHeight(ivZixun, 165);
		ViewUtils.setHeight(ivTips, 165);
		ViewUtils.setHeight(ivIntroduce, 165);
		ViewUtils.setHeight(ivSeggest, 165);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.rl_view_pager), 10);
		ViewUtils.setMarginRight(view.findViewById(R.id.rl_view_pager), 10);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_view_pager), 12);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.ll_row1), 12);
		ViewUtils.setMarginRight(view.findViewById(R.id.ll_row1), 12);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_function), 12);
		
		ViewUtils.setMarginRight(view.findViewById(R.id.indicator_service), 16);
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) view.findViewById(R.id.indicator_service);
        adapter = new RailServiceBannerAdapter(getChildFragmentManager(),R.drawable.pic_index_default,photoRespList);
        getSlidingMenu().addIgnoredView(viewPager);
		
		ivNoNet = (ImageView) view.findViewById(R.id.iv_net_error);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_left:
			getSlidingMenu().toggle();
			break;
		case R.id.btn_title_right:
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_service","6");
			Intent callHelp = new Intent(getActivity(), CallForHelpActivity.class);
			startActivity(callHelp);
			break;

		case R.id.iv_railway_ording:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_service","1");
			Intent shopIntent = new Intent(getActivity(), OrderShoppingActivity.class);
			startActivity(shopIntent);
			break;
		case R.id.iv_satisfaction_survey:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
//			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_service","2");
			/*Intent intent = new Intent(getActivity(), RailTravelFragment.class);
			startActivity(intent);*/
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_survey");
			Intent intentSatisfaction = new Intent(getActivity(), SatisfactionSurveyActivity.class);
			startActivity(intentSatisfaction);
			break;
		case R.id.iv_railway_zixun:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_service","3");
			Intent intent_rail_news = new Intent(getActivity(), RailNewsActivity.class);
			startActivity(intent_rail_news);
			break;
		case R.id.iv_warm_tips:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			
			Intent intent_rail_tip = new Intent(getActivity(), TipActivity.class);
			startActivity(intent_rail_tip);
			break;
		case R.id.iv_service_introduce:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_service","5");
			Intent intent_rail_introduce = new Intent(getActivity(), IntroductionActivity.class);
			startActivity(intent_rail_introduce);
			break;
		case R.id.iv_suggest:
			if (ViewUtils.isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgentWrapper.onEvent(getActivity(), "t_service","4");
			Intent intentSuggest = new Intent(getActivity(), ComplaintSuggestActivity.class);
			startActivity(intentSuggest);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","RailServiceFragment hidden = " + hidden);
		if (hidden) {
			EventBus.getDefault().post(FragmentStatusEvent.RAILSERVICEHIDE);
		} else {
			EventBus.getDefault().post(FragmentStatusEvent.RAILSERVICESHOW);
		}
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_SERVICE_BANPHOTO:
			if (msg.obj != null) {
				GsonResponseObject.serviceBannerphotoResp photoResp = (GsonResponseObject.serviceBannerphotoResp) msg.obj;
				Log.d(TAG,"RESPONSE_TYPE_SERVICE_BANPHOTO status = " + photoResp.status);
				if ("0".equals(photoResp.status)) {
					
					if (photoResp.list != null && photoResp.list.length > 0) {
						photoRespList.clear();
						ivNoNet.setVisibility(View.GONE);
						Collections.addAll(photoRespList, photoResp.list);
						
						viewPager.setAdapter(adapter);
						
	//					viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % ListUtils.getSize(photoRespList));
						viewPager.setCurrentItem(0);
						mIndicator.setViewPager(viewPager);
						mIndicator.notifyDataSetChanged();
						Log.d(TAG,"photoRespList Size = " + photoRespList.size() + " adpapter = " + adapter);
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
			break;
		}
		return true;
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
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_rail_service;
	}
	
}
