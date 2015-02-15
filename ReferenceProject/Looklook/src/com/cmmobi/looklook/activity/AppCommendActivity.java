package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.CRMRequester;
import com.cmmobi.looklook.common.gson.CRM_Object;
import com.cmmobi.looklook.common.gson.CRM_Object.recItem;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * app推荐
 * 
 * @author Administrator
 * 
 */
public class AppCommendActivity extends ZActivity implements
		OnPageChangeListener {

	private ImageButton about_back_btn;
	private ViewPager tabcontent_vp;
	private ListView recommend_list;
	private RecommendAdapter ra;

	private View view1, view2;
	private ArrayList<View> viewList;
	private ImageView dot1, dot2;

	private ArrayList<CRM_Object.recItem> recList;

	// 使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private String recomUrlAlph;
	private String recomUrlBeta;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_commend);
		about_back_btn = (ImageButton) findViewById(R.id.about_back_btn);
		about_back_btn.setOnClickListener(this);
		tabcontent_vp = (ViewPager) findViewById(R.id.tabcontent_vp);
		recommend_list = (ListView) findViewById(R.id.recommend_list);
		dot1 = (ImageView) findViewById(R.id.iv_dot1);
		dot2 = (ImageView) findViewById(R.id.iv_dot2);
		CRMRequester.getRecommend(handler, "101", "3");
		ZDialog.show(R.layout.progressdialog, true, true, this);

		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder()
				.showStubImage(0)
				.showImageForEmptyUri(0)
				.showImageOnFail(0).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();
	}

	class Pager extends PagerAdapter {

		ArrayList<View> views;

		public Pager(ArrayList<View> viewList) {
			this.views = (ArrayList<View>) viewList;
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return view == arg1;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position), 0);
			return viewList.get(position);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case CRMRequester.RESPONSE_TYPE_GET_RECOMMEND:
			ZDialog.dismiss();
			CRM_Object.getRecommendResponse aur = (CRM_Object.getRecommendResponse) msg.obj;
			if (aur != null) {
				recList = new ArrayList<CRM_Object.recItem>();
				String total = aur.total;// 总数
				final CRM_Object.recItem item[] = aur.items;
				if (item != null && item.length > 0) {
					for (int i = 0; i < item.length; i++) {
						recList.add(item[i]);
					}
					ra = new RecommendAdapter(recList, AppCommendActivity.this);
					recommend_list.setAdapter(ra);

					LayoutInflater lf = getLayoutInflater().from(this);
					view1 = lf.inflate(R.layout.app_recommend_view1, null);
					view2 = lf.inflate(R.layout.app_recommend_view2, null);

					ImageView wiv1 = (ImageView) view1
							.findViewById(R.id.view_page1);
					ImageView wiv2 = (ImageView) view2
							.findViewById(R.id.view_page2);

					/*
					 * wiv1.setLoadingDrawable(R.drawable.pubu_tu);
					 * wiv1.setImageUrl(item[0].appbigimage, 1, false);
					 */
					if (item != null && item.length > 0) {
						for (int i = 0; i < item.length; i++) {
							if (item[i] != null && item[i].appbigimage != null) {
								if(recomUrlAlph == null){
									recomUrlAlph = item[i].appbigimage;
								}else{
									break;
								}
							}
						}
						if (recomUrlAlph !=null) {
							imageLoader
									.displayImage(recomUrlAlph, wiv1,
											options, animateFirstListener,
											ActiveAccount.getInstance(this)
													.getUID(), 1);
						}
					} 

					wiv1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String url = item[0].loadpath;
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(url));
							intent.setClassName("com.android.browser",
									"com.android.browser.BrowserActivity");
							startActivity(intent);
						}
					});

					/*
					 * wiv2.setLoadingDrawable(R.drawable.pubu_tu);
					 * wiv2.setImageUrl(item[1].appbigimage, 1, false);
					 */
					if (item != null && item.length > 1) {
						for (int i = 0; i < item.length; i++) {
							if (item[i] != null && item[i].appbigimage != null) {
								if(recomUrlBeta == null && recomUrlAlph != null && !recomUrlAlph.equals(item[i].appbigimage)){
									recomUrlBeta = item[i].appbigimage;
								}else{
									break;
								}
							}
						}
						if (recomUrlBeta != null) {
							imageLoader
									.displayImage(recomUrlBeta, wiv2, options,
											animateFirstListener,
											ActiveAccount.getInstance(this)
													.getUID(), 1);
						}
					} 

					wiv2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String url = item[1].loadpath;
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(url));
							/*
							 * intent.setClassName("com.android.browser",
							 * "com.android.browser.BrowserActivity");
							 */
							startActivity(intent);
						}
					});

					viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
					viewList.add(view1);
					viewList.add(view2);
					tabcontent_vp.setAdapter(new Pager(viewList));
					tabcontent_vp.setOnPageChangeListener(this);
				}
			}
			ZLog.e(aur);

			break;
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_back_btn:
			AppCommendActivity.this.finish();
			break;

		default:
			break;
		}
	}

	private class RecommendAdapter extends BaseAdapter {

		private ArrayList<CRM_Object.recItem> recList;
		private LayoutInflater inflater;
		private DisplayImageOptions RecommendAdapterOptions;

		public RecommendAdapter(ArrayList<CRM_Object.recItem> recList,
				Context context) {
			this.recList = recList;
			inflater = LayoutInflater.from(context);
			RecommendAdapterOptions = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.gallrery_bg)
					.showImageForEmptyUri(R.drawable.gallrery_bg)
					.showImageOnFail(R.drawable.gallrery_bg)
					.cacheInMemory(true).cacheOnDisc(true)
					.displayer(new SimpleBitmapDisplayer())
					// .displayer(new CircularBitmapDisplayer()) 圆形图片
					// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
					.build();
		}

		@Override
		public int getCount() {
			return recList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return recList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Object obj = recList.get(position);
			ViewHolder holder;
			if (convertView != null) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.app_recommend_item,
						null);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.app_recommend_icon);
				holder.appname = (TextView) convertView
						.findViewById(R.id.app_name);
				holder.appkind = (TextView) convertView
						.findViewById(R.id.app_kind);
				holder.appdes = (TextView) convertView
						.findViewById(R.id.app_describe);
				holder.downloadcount = (TextView) convertView
						.findViewById(R.id.download_times);

				convertView.findViewById(R.id.app_download).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
									String url = ((recItem) obj).loadpath;
									Intent intent = new Intent(
											Intent.ACTION_VIEW, Uri.parse(url));
									intent.setClassName("com.android.browser",
											"com.android.browser.BrowserActivity");
									startActivity(intent);
								}
						});
				convertView.setTag(holder);
			}

			/*
			 * holder.icon.setLoadingDrawable(R.drawable.gallrery_bg);
			 * holder.icon.setImageUrl(((recItem) obj).appimage, 1, false);
			 */
			if (obj != null && ((recItem) obj).appimage != null) {
				imageLoader.displayImage(((recItem) obj).appimage, holder.icon,
						RecommendAdapterOptions, animateFirstListener,
						ActiveAccount.getInstance(AppCommendActivity.this)
								.getUID(), 1);
			} else {
				holder.icon.setImageResource(R.drawable.gallrery_bg);
			}

			holder.appname.setText(((recItem) obj).appname);
			holder.downloadcount.setText(((recItem) obj).downloadcount + "人使用");
			holder.appkind.setText(((recItem) obj).appkind);
			holder.appdes.setText(((recItem) obj).appdes);

			return convertView;
		}

		class ViewHolder {
			ImageView icon;
			TextView appname;
			TextView appkind;
			TextView downloadcount;
			TextView appdes;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		if (position == 0) {
			dot1.setImageResource(R.drawable.biaoqing_fanye_2);
			dot2.setImageResource(R.drawable.biaoqing_fanye_1);
		}
		if (position == 1) {
			dot1.setImageResource(R.drawable.biaoqing_fanye_1);
			dot2.setImageResource(R.drawable.biaoqing_fanye_2);
		}
	}
}
