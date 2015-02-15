package com.cmmobi.looklook.common.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ExpressionAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareSNSTrace;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.view
 * @filename DiaryDetailMenuView.java
 * @summary 日记详情界面菜单
 * @author 兰海
 * @date 2013-8-12
 * @version 1.0
 */
public class DiaryDetailMenuView implements OnPageChangeListener {
	private View expressionLayout;
	private ViewPager expressionPager;
	private LayoutInflater inflater;
	private ArrayList<View> expressionTabs = new ArrayList<View>();
	private ImageView[] ivPageDots;
	private GridView mGVSelfPage1 = null;
	private GridView mGVSelfPage2 = null;
	private GridView mGVOther = null;
	private View mViewTab1 = null;
	private View mViewTab2 = null;
	private View mViewTab3 = null;
	private Button mBtnCancel = null;
	private Activity mActivity = null;
//	private static HashMap<String, Integer> EXPHM = new HashMap<String, Integer>();

	/**
	 * @description 构造函数
	 * @param activity
	 */
	public DiaryDetailMenuView(Activity activity) {
		mActivity = activity;
		expressionLayout = activity
				.findViewById(R.id.relative_layout_diarydetailmenu);
		expressionPager = (ViewPager) activity.findViewById(R.id.vp_expression);
		mBtnCancel = (Button) activity.findViewById(R.id.ib_diarydetail_menu_cancel);
		
		// 点
		ivPageDots = new ImageView[2];
		ivPageDots[0] = (ImageView) expressionLayout.findViewById(R.id.iv_dot1);
		ivPageDots[1] = (ImageView) expressionLayout.findViewById(R.id.iv_dot2);
		
		inflater = LayoutInflater.from(activity);
		mViewTab1 = inflater.inflate(R.layout.grid_diarydetail_menu, null);
		mViewTab2 = inflater.inflate(R.layout.grid_diarydetail_menu, null);
		mViewTab3 = inflater.inflate(R.layout.grid_diarydetail_menu, null);
		mGVSelfPage1 = (GridView) mViewTab1.findViewById(R.id.gv_diarydetail_menu);
		mGVSelfPage2 = (GridView) mViewTab2.findViewById(R.id.gv_diarydetail_menu);
		mGVOther = (GridView) mViewTab3.findViewById(R.id.gv_diarydetail_menu);
		
	}
	
	// 解决bitmapde density导致变化错误的问题
	private Bitmap decodeResource(Resources resources, int id) {
		TypedValue value = new TypedValue();
		resources.openRawResource(id, value);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inTargetDensity = value.density;
		return BitmapFactory.decodeResource(resources, id, opts).copy(Bitmap.Config.ARGB_8888, true);
	}
	
	// 获取分享路径状态
	public static Set<Integer> getSnsTrace(MyDiary myDiary)
	{
		ShareSNSTrace[] sns = myDiary.sns;
		
		Set<Integer> snsSet = new HashSet<Integer>();
//		snsSet.add(R.drawable.btn_diarydetail_looklook);
//		snsSet.add(R.drawable.btn_diarydetail_sina);
//		snsSet.add(R.drawable.btn_diarydetail_tencent);
//		snsSet.add(R.drawable.btn_diarydetail_renren);
		
		// 更新sns状态
		if(sns != null)
		{
			for(int i = 0; i < sns.length; i ++)
			{
				for(int j = 0; j < sns[i].shareinfo.length; j ++)
				{
					/*if(sns[i].shareinfo[j].snstype.equals("0"))
					{
						snsSet.add(R.drawable.btn_diarydetail_looklook);
					}
					else */if(sns[i].shareinfo[j].snstype.equals("1"))
					{
						snsSet.add(R.drawable.btn_diarydetail_sina);
					}
					else if(sns[i].shareinfo[j].snstype.equals("6"))
					{
						snsSet.add(R.drawable.btn_diarydetail_tencent);
					}
					else if(sns[i].shareinfo[j].snstype.equals("2"))
					{
						snsSet.add(R.drawable.btn_diarydetail_renren);
					}
				}
			}
		}
		
		// looklook 分享可能不在sns中，所以要用diary_status判断
		if(myDiary.diary_status != null && myDiary.diary_status.equals("2"))
		{
			snsSet.add(R.drawable.btn_diarydetail_looklook);
		}
		
		return snsSet;
	}
	
	/**
	 * @description 显示自己的分享菜单
	 * @param sns 分享路径
	 * @param isActive 是否激活活动按钮
	 */
	public void showMyMenu(MyDiary myDiary, boolean access, boolean join)
	{
		Set<Integer> snsSet = getSnsTrace(myDiary);
		
		// 根据id叠加图片
//		Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.xieyi_pressed);
		Bitmap bitmap = decodeResource(mActivity.getResources(), R.drawable.ic_diarydetail_menu_shared);
		Bitmap[] mapmap1 = new Bitmap[gIcons1.length];
		for(int i = 0; i < gIcons1.length; i++)
		{
//				Bitmap newbitmap = BitmapFactory.decodeResource(activity.getResources(), gIcons1[i]);
			Bitmap newbitmap = decodeResource(mActivity.getResources(), gIcons1[i]);
			Paint paint = new Paint();
			// 画活动的灰色
			if(!access && i == 2)
			{
				ColorMatrix cm = new ColorMatrix();
		        cm.setSaturation(0);
		        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		        paint.setColorFilter(f);
			}
			Canvas canvas = new Canvas(newbitmap);
			canvas.drawBitmap(newbitmap, 0, 0, paint);
			// 画活动和其他类型的对勾
			if(snsSet.contains(gIcons1[i]) || (i == 2 && join))
			{
				paint = new Paint();
				canvas.drawBitmap(bitmap, newbitmap.getWidth()-bitmap.getWidth(), newbitmap.getHeight()-bitmap.getHeight(), paint);
			}
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			// 如果不能参加活动
//			if(!isActive && i == 2)
//			{
//				paint.setColor(Color.GRAY);
//				paint.setAlpha(125);
//				canvas.drawRect(0, 0, newbitmap.getWidth(), newbitmap.getHeight(), paint);
//			}
			mapmap1[i] = newbitmap;
		}
		
		Bitmap[] mapmap2 = new Bitmap[gIcons2.length];
		for(int i = 0; i < gIcons2.length; i++)
		{
//				Bitmap newbitmap = BitmapFactory.decodeResource(activity.getResources(), gIcons2[i]);
			Bitmap newbitmap = decodeResource(mActivity.getResources(), gIcons2[i]);
			// 画对勾
			if(snsSet.contains(gIcons2[i]))
			{
				Canvas canvas = new Canvas(newbitmap);
				Paint paint = new Paint();
				canvas.drawBitmap(bitmap, newbitmap.getWidth()-bitmap.getWidth(), newbitmap.getHeight()-bitmap.getHeight(), paint);
				canvas.save(Canvas.ALL_SAVE_FLAG);
		        canvas.restore();
			}
	        mapmap2[i] = newbitmap;
		}
		
		mGVSelfPage1.setAdapter(new GridViewAdapter(gIcons1, gText1, mapmap1));
		mGVSelfPage2.setAdapter(new GridViewAdapter(gIcons2, gText2, mapmap2));
		
		expressionTabs.clear();
		expressionTabs.add(mViewTab1);
		expressionTabs.add(mViewTab2);
		expressionPager.setAdapter(new ExpressionAdapter(mActivity,
				expressionTabs));
		expressionPager.setOnPageChangeListener(this);
		ivPageDots[0].setVisibility(View.VISIBLE);
		ivPageDots[1].setVisibility(View.VISIBLE);
	}

	/**
	 * @description 显示其他人的分享菜单
	 * @param sns 分享路径
	 */
	public void showOtherMenu(MyDiary myDiary)
	{
		Set<Integer> snsSet = getSnsTrace(myDiary);
		
		// 根据id叠加图片
//		Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.xieyi_pressed);
		Bitmap bitmap = decodeResource(mActivity.getResources(), R.drawable.ic_diarydetail_menu_shared);
		Bitmap[] mapmap3 = new Bitmap[gIcons3.length];
		for(int i = 0; i < gIcons3.length; i++)
		{
			Bitmap newbitmap = decodeResource(mActivity.getResources(), gIcons3[i]);
			if(snsSet.contains(gIcons3[i]))
			{
				Canvas canvas = new Canvas(newbitmap);
				Paint paint = new Paint();
				canvas.drawBitmap(bitmap, newbitmap.getWidth()-bitmap.getWidth(), newbitmap.getHeight()-bitmap.getHeight(), paint);
				canvas.save(Canvas.ALL_SAVE_FLAG);
		        canvas.restore();
			}
	        mapmap3[i] = newbitmap;
		}
		
		mGVOther.setAdapter(new GridViewAdapter(gIcons3, gText3, mapmap3));
		expressionTabs.clear();
		expressionTabs.add(mViewTab3);
		expressionPager.setAdapter(new ExpressionAdapter(mActivity,
				expressionTabs));
		expressionPager.setOnPageChangeListener(this);
		ivPageDots[0].setVisibility(View.GONE);
		ivPageDots[1].setVisibility(View.GONE);
	}
	
	public View getExpressionView() {
		return expressionLayout;
	}

	public void setOnclickListener(OnItemClickListener onItemClickListener, OnClickListener onClickListener) {
		mGVSelfPage1.setOnItemClickListener(onItemClickListener);
		mGVSelfPage2.setOnItemClickListener(onItemClickListener);
		mGVOther.setOnItemClickListener(onItemClickListener);
		mBtnCancel.setOnClickListener(onClickListener);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		switch (arg0) {
		case 0:
			ivPageDots[0].setImageResource(R.drawable.btn_expression_dot_focus);
			ivPageDots[1].setImageResource(R.drawable.btn_expression_dot);
			break;
		case 1:
			ivPageDots[1].setImageResource(R.drawable.btn_expression_dot_focus);
			ivPageDots[0].setImageResource(R.drawable.btn_expression_dot);
			break;

		default:
			break;
		}
	}
	
	private static final Integer[] gIcons1 = {R.drawable.btn_diarydetail_looklook,
		R.drawable.btn_diarydetail_privatemsg, R.drawable.btn_diarydetail_active,
		R.drawable.btn_diarydetail_sina, R.drawable.btn_diarydetail_weixin,
		R.drawable.btn_diarydetail_weixinfriend, R.drawable.btn_diarydetail_tencent,
		R.drawable.btn_diarydetail_renren
	};
	
	private static final String[] gText1 = { "looklook", "站内私信", "站内活动", "新浪微博",
		"微信", "微信朋友圈", "腾讯微博", "人人网",
	};
	
	private static final Integer[] gIcons2 = {R.drawable.btn_diarydetail_email, R.drawable.btn_diarydetail_msg	
	};
	
	private static final String[] gText2 = {"邮件分享", "短信分享"	
	};
	
	private static final Integer[] gIcons3 = {R.drawable.btn_diarydetail_privatemsg,
		R.drawable.btn_diarydetail_sina, R.drawable.btn_diarydetail_weixin,
		R.drawable.btn_diarydetail_weixinfriend, R.drawable.btn_diarydetail_tencent,
		R.drawable.btn_diarydetail_renren, R.drawable.btn_diarydetail_email,
		R.drawable.btn_diarydetail_msg
	};
	
	private static final String[] gText3 = {"站内私信", "新浪微博",
		"微信", "微信朋友圈", "腾讯微博", "人人网", "邮件分享", "短信分享"	
	};

//	static {
//		for (int i = 0; i < gText1.length; i++) {
//			EXPHM.put(gText1[i], gIcons1[i]);
//		}
//		for (int i = 0; i < gText2.length; i++) {
//			EXPHM.put(gText2[i], gIcons2[i]);
//		}
//	}
	
	class ViewHolder
	{
		ImageView pic;
		TextView text;
	}

	class GridViewAdapter extends BaseAdapter {

		Integer[] icons;
		String[] expText;
		Bitmap[] bitmap;

		public GridViewAdapter(Integer[] icons, String[] expText, Bitmap[] bitmap) {
			this.icons = icons;
			this.expText = expText;
			this.bitmap = bitmap;
		}

		@Override
		public int getCount() {
			return icons.length;
		}

		@Override
		public Object getItem(int position) {
			return icons[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			//if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.grid_item_diarydetail_menu,
						null);
				holder.pic = (ImageView) convertView.findViewById(R.id.iv_diarydetail_item);
				holder.text = (TextView) convertView.findViewById(R.id.tv_diarydetail_item);
				holder.pic.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), bitmap[position]));
				holder.text.setText(expText[position]);
				//convertView.setTag(holder);
				convertView.setTag(icons[position]);
			//} else {
				//holder = (ViewHolder) convertView.getTag();
			//}
			return convertView;
//			TextView tv;
//			tv = new TextView(activity);
//			tv.setText(expText[position]);
//			tv.setGravity(Gravity.CENTER);
//			tv.setTextSize(12);
//			tv.setTextColor(Color.WHITE);
//			Drawable drawable= activity.getResources().getDrawable(icons[position]);  
//			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
//			 
//			tv.setCompoundDrawables(null,drawable,null,null);  
//			tv.setLayoutParams(new AbsListView.LayoutParams(
//					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//			tv.setTag(expText[position]);
//			return tv;
		}

	}
}
