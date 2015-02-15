/**
 * 
 */
package com.cmmobi.looklook.common.listview.pulltorefresh;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.DateUtils;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-4-23
 */
public abstract class AbsRefreshView<T> extends ScrollView {
	private static final String TAG = "PullToRefreshView";
	private static final boolean ISDEBUG = false;
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private int headContentHeight;
	private int footContentHeight;

	private LinearLayout innerLayout;
	private View headView;
	private View footView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView fArrowImageView;
	private ProgressBar fProgressBar;
	private TextView fTipsTextview;
	private TextView fLastUpdatedTextView;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;
	private boolean headorfooter = false;
	private int state;
	private boolean isBack;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private boolean canReturn;
	private boolean isRecored;
	private int startY;
	private Boolean isLastIndex = false;

	protected LayoutInflater inflater;
	protected Context context;

	public AbsRefreshView(Context context) {
		super(context);
		init(context);
	}

	public AbsRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	protected DisplayMetrics dm = new DisplayMetrics();
	
	public int getScreenWidth(){
		return dm.widthPixels;
	}
	
	public int getScreenHight(){
		return dm.heightPixels;
	}

	private void init(Context context) {
		this.context = context;
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		inflater = LayoutInflater.from(context);
		innerLayout = new LinearLayout(context);
		innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		innerLayout.setOrientation(LinearLayout.VERTICAL);

		headView = inflater.inflate(R.layout.mylistview_head,
				null);
		footView = inflater.inflate(R.layout.mylistview_footer,
				null);
		addHeader(headView);
		addFooter(footView);
		addView(innerLayout);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		canReturn = false;
	}

	protected void addHeader(View headView) {
		showHeader=true;
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);
		measureView(headView);

		headContentHeight = headView.getMeasuredHeight();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();
		innerLayout.addView(headView);
	}

	protected void addFooter(View footView) {
		showFooter=true;
		fTipsTextview = (TextView) footView
				.findViewById(R.id.footer_tipsTextView);
		measureView(footView);
		footContentHeight = footView.getMeasuredHeight();
		footView.invalidate();
		innerLayout.addView(footView);
	}

	private boolean showFooter;
	public void removeFooter() {
		showFooter=false;
		innerLayout.removeView(footView);
		innerLayout.invalidate();
	}
	
	private boolean showHeader;
	public void removeHeader(){
		showHeader=false;
		innerLayout.removeView(headView);
		innerLayout.invalidate();
	}
	
	public void setRefreshable(boolean refreshable){
		isRefreshable=refreshable;
	}
	
	private boolean isProcessRefresh;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if(ISDEBUG)Log.d(TAG, "onTouchEvent->ACTION_UP");
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						if (headorfooter) {
//							changeFooterViewByState();
						} else
							changeHeaderViewByState();
						if (ISDEBUG)
							Log.i(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						// 向下拉
						if (headorfooter) {
//							changeFooterViewByState();
							onMore();
						} else {
							changeHeaderViewByState();
							onRefresh();
						}
						if (ISDEBUG)
							Log.i(TAG, "由松开刷新状态，到done状态");
					}
				}
				isRecored = false;
				isBack = false;
				isProcessRefresh=false;
				break;
			case MotionEvent.ACTION_MOVE:
				if(ISDEBUG)Log.d(TAG, "onTouchEvent->ACTION_MOVE");
				int tempY = (int) event.getY();
				if (tempY > startY) {// pull down
					headorfooter = false;
					if(refreshListener!=null)refreshListener.onAutoScroll(0, 1, 0, 15);
					if(getScrollY() == 0)
						isProcessRefresh=true;
					if (isProcessRefresh&&showHeader&&state != REFRESHING && isRecored && state != LOADING) {
						// 可以松手去刷新了
						if (state == RELEASE_To_REFRESH) {
							canReturn = true;

							if (((tempY - startY) / RATIO < headContentHeight/2)
									&& (tempY - startY) > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
							}
							// 一下子推到顶了
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由松开刷新状态转变到done状态");
							} else {
								// 不用进行特别的操作，只用更新paddingTop的值就行了
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (state == PULL_To_REFRESH) {
							canReturn = true;

							// 下拉到可以进入RELEASE_TO_REFRESH的状态
							if ((tempY - startY) / RATIO >= headContentHeight/2) {
								state = RELEASE_To_REFRESH;
								isBack = true;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
							}
							// 上推到顶了
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
							}
						}

						// done状态下
						if (state == DONE) {
							if (tempY - startY > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
							}
						}

						// 更新headView的size
						if (state == PULL_To_REFRESH) {
							headView.setPadding(0, -1 * headContentHeight
									+ (tempY - startY) / RATIO, 0, 0);

						}

						// 更新headView的paddingTop
						if (state == RELEASE_To_REFRESH) {
							headView.setPadding(0, (tempY - startY) / RATIO
									- headContentHeight, 0, 0);
						}
						if (canReturn) {
							canReturn = false;
							return true;
						}
					}
				} else {
					headorfooter = true;
					/*isLastIndex = innerLayout.getMeasuredHeight() - 20 <= getScrollY()
							+ getHeight();
					if (showFooter&&state != REFRESHING && isRecored && state != LOADING) {
//						 可以松手去刷新了
						if (state == RELEASE_To_REFRESH) {
							// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
							if (((startY - tempY) / RATIO < footContentHeight)
									&& (startY - tempY) > 0) {
								state = PULL_To_REFRESH;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
							}
							// 一下子推到顶了
							else if (startY - tempY <= 0) {
								state = DONE;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由松开刷新状态转变到done状态");
							}
							// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
							else {
								// 不用进行特别的操作，只用更新paddingTop的值就行了
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (state == PULL_To_REFRESH && isLastIndex) {
							// setSelection(getCount() - 1);

							// 下拉到可以进入RELEASE_TO_REFRESH的状态
							if ((startY - tempY) / RATIO >= footContentHeight) {
								state = RELEASE_To_REFRESH;
								isBack = true;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
							}
							// 上推到顶了
							else if (startY - tempY <= 0) {
								state = DONE;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
							}
						}

						// done状态下
						if (state == DONE) {
							if (startY - tempY > 0) {
								state = PULL_To_REFRESH;
								changeFooterViewByState();
							}
						}

						if (isLastIndex) {
							// 更新footerView的size
							if (state == PULL_To_REFRESH) {
								footView.setPadding(0, 0, 0, -1
										* footContentHeight + (startY - tempY)
										/ RATIO);

							}

							// 更新footerView的paddingTop
							if (state == RELEASE_To_REFRESH) {
								footView.setPadding(0, 0, 0, (startY - tempY)
										/ RATIO - footContentHeight);
							}
						}
					}*/
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	float distance=0;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(ISDEBUG)Log.d(TAG, "onInterceptTouchEvent->ACTION_DOWN");
			if (!isRecored) {
				isRecored = true;
				startY = (int) ev.getY();
			}
			distance=ev.getY()+ev.getX();
			break;
//		case MotionEvent.ACTION_MOVE:
//			if(ISDEBUG)Log.d(TAG, "onInterceptTouchEvent->ACTION_MOVE");
//			if(Math.abs(distance-(ev.getY()+ev.getX()))<5){//防止子类中点击不灵敏
//				return true;
//			}
//			break;
		case MotionEvent.ACTION_UP:
			if(ISDEBUG)Log.d(TAG, "onInterceptTouchEvent->ACTION_UP");
//			Log.d(TAG, "startY="+startY);
//			Log.d(TAG, "ev.getY()="+ev.getY());
			isRecored = false;
			isBack = false;
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/*private void changeFooterViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			fArrowImageView.setVisibility(View.VISIBLE);
			fProgressBar.setVisibility(View.GONE);
			fTipsTextview.setVisibility(View.VISIBLE);
			fLastUpdatedTextView.setVisibility(View.VISIBLE);

			fArrowImageView.clearAnimation();
			fArrowImageView.startAnimation(reverseAnimation);

			fTipsTextview.setText("松开加载");

			if (ISDEBUG)
				Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			fProgressBar.setVisibility(View.GONE);
			fTipsTextview.setVisibility(View.VISIBLE);
			fLastUpdatedTextView.setVisibility(View.VISIBLE);
			fArrowImageView.clearAnimation();
			fArrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				fArrowImageView.clearAnimation();
				fArrowImageView.startAnimation(animation);

				fTipsTextview.setText("上拉加载");
			} else {
				fTipsTextview.setText("上拉加载");
			}
			if (ISDEBUG)
				Log.v(TAG, "当前状态，上拉加载");
			break;

		case REFRESHING:
			Log.d(TAG, "REFRESHING");
			innerLayout.invalidate();
			fProgressBar.setVisibility(View.VISIBLE);
			fArrowImageView.clearAnimation();
			fArrowImageView.setVisibility(View.GONE);
			fTipsTextview.setText("正在加载...");
			fLastUpdatedTextView.setVisibility(View.VISIBLE);
			if (ISDEBUG)
				Log.v(TAG, "当前状态,正在加载...");
			break;
		case DONE:
			footView.setPadding(0, 0, 0, -1 * footContentHeight);

			fProgressBar.setVisibility(View.GONE);
			fArrowImageView.clearAnimation();
			fArrowImageView.setImageResource(R.drawable.goicon);
			fTipsTextview.setText("上拉加载");
			fLastUpdatedTextView.setVisibility(View.VISIBLE);

			if (ISDEBUG)
				Log.v(TAG, "当前状态，done");
			break;
		}
	}*/

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开即可更新...");

			if (ISDEBUG)
				Log.i(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
			}
			tipsTextview.setText("下拉即可更新...");
			if (ISDEBUG)
				Log.i(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:
//			headView.setPadding(0, -1*headContentHeight/3, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("加载中...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			if (ISDEBUG)
				Log.i(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.shuaxin_jiantou);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setText("最后更新:"+DateUtils.dateToString(new Date(),DateUtils.DATE_FORMAT_NORMAL_2));
			if (ISDEBUG)
				Log.i(TAG, "当前状态，done");
			break;
		}
	}

	protected void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
//		removeFooter();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(refreshListener!=null)
			refreshListener.onAutoScroll(l, t, oldl, oldt);
		int offset=innerLayout.getMeasuredHeight()-getScrollY()-getHeight();
		if(offset<10&&!noMoreData){
			Log.d(TAG, "on bottom");
			if(state!=REFRESHING){
				state=REFRESHING;
				if(refreshListener!=null)refreshListener.onMore();
			}
		}
	}
	
	 @Override
     public void fling(int velocityY) {
         super.fling(velocityY / 2);
     }

	public interface OnRefreshListener {
		public void onRefresh();

		public void onMore();

		public void onAutoScroll(int l, int t, int oldl, int oldt);
	}

	public void loadDateError() {
		state = DONE;
		changeHeaderViewByState();
		measureView(headView);
	}

	protected void onRefreshComplete() {
		state = DONE;
		changeHeaderViewByState();
		scrollTo(0, 0);
		measureView(headView);
	}

	protected void onMoreComplete() {
		state = DONE;
		changeHeaderViewByState();
		measureView(headView);
	}
	
	private boolean noMoreData;
	/**
	 * 设置是否有更多数据可加载
	 * noMoreData true 无数据 false 有数据
	 */
	public void noMoreData(boolean noMoreData){
		this.noMoreData=noMoreData;
		Log.d(TAG, "this.noMoreData="+this.noMoreData);
		if(this.noMoreData){
			fTipsTextview.setText(R.string.no_more_date);
		}else{
			fTipsTextview.setText(R.string.loading_more_date);
		}
		footView.invalidate();
	}

	private void onRefresh() {
		if (refreshListener != null) {
//			fTipsTextview.setText(R.string.loading_more_date);
			refreshListener.onRefresh();
		}
	}

	private void onMore() {
		if (refreshListener != null) {
//			fTipsTextview.setText(R.string.loading_more_date);
			refreshListener.onMore();
		}
	}

	protected void addChild(View child) {
		innerLayout.addView(child);
	}

	protected void addChild(View child, int position) {
		innerLayout.addView(child, position);
	}

	protected void clear() {
		innerLayout.removeAllViews();
		innerLayout.addView(headView);
		footView.invalidate();
		innerLayout.addView(footView);
		innerLayout.invalidate();
	}

	private boolean isReset;

	// 设置是刷新还是加载
	public void reset() {
		this.isReset = true;
	}

	public void loadMore(T items) {
		if (isReset) {
			isReset = false;
			initContent(items);
			onRefreshComplete();
		} else {
			addView(items);
			onMoreComplete();
		}
	}

	protected abstract void initContent(T items);

	protected abstract void addView(T items);
	
	// textview中显示文字+表情
		protected void replacedExpressions(String expressionText, TextView tv) {

			if(tv == null)
			{
				return;
			}
			
			if(expressionText == null)
			{
				tv.setText("");
				return;
			}
			
			tv.setText(null);
			
			ArrayList<String> list = getTextExpressions(expressionText);
			Log.d("replacedExpressions", "list="+list);
			if (list != null && list.size() > 0) {
				int len = list.size();
				int expStart = 0;
				int expEnd = 0;
				tv.setText(null);
				for (int i = 0; i < len; i++) {
					String exp = list.get(i);
					if(EXPHM.get(exp)!=null){
						int expSrc=EXPHM.get(exp);
						expEnd = expressionText.indexOf(exp, expStart);
						exp=exp.replace("[", "");
						exp=exp.replace("]", "");
						expressionText = expressionText.replaceFirst("\\[" + exp
								+ "\\]", "");
						tv.append(expressionText, expStart, expEnd);
						tv.append(Html.fromHtml("<img src='" +expSrc + "'/>",
								imageGetter, null));
						expStart = expEnd;
					}else{
						expStart = 0;
						expEnd = 0;
					}
				}
				tv.append(expressionText, expStart, expressionText.length());
			} else {
				Log.d("replacedExpressions", "expressionText="+expressionText);
				Log.d("replacedExpressions", "tv.getText()="+tv.getText());
				tv.append(expressionText);
			}
		}
		
		// TextView的getter
		private ImageGetter imageGetter = new ImageGetter() {

			@Override
			public Drawable getDrawable(String source) {
				int id = Integer.parseInt(source);
				Drawable drawable = getResources().getDrawable(id);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
						drawable.getIntrinsicHeight() / 2);
				return drawable;
			}
		};
		
		// 取出edittext字符串中的表情字段
		private ArrayList<String> getTextExpressions(String expressionText) {
			if (expressionText != null && expressionText.length() > 0) {
				ArrayList<String> list = new ArrayList<String>();
//				Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
				Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5a-zA-Z]*?\\]");
				Matcher matcher = pattern.matcher(expressionText);
				while (matcher.find()) {
					list.add(matcher.group());
				}
				return list;
			}
			return null;
		}

		// 文字框效果输入
		public static HashMap<String, Integer> EXPHM = new HashMap<String, Integer>();
		private static final Integer[] icons1 = { R.drawable.bq_ai,
				R.drawable.bq_baibai, R.drawable.bq_beishang, R.drawable.bq_bishi,
				R.drawable.bq_bizui, R.drawable.bq_buyao, R.drawable.bq_changzui,
				R.drawable.bq_chijing, R.drawable.bq_daihaqian,
				R.drawable.bq_dangao, R.drawable.bq_good, R.drawable.bq_guzhang,
				R.drawable.bq_haha, R.drawable.bq_haixiao, R.drawable.bq_han,
				R.drawable.bq_hehe, R.drawable.bq_heixiang, R.drawable.bq_huaxin,
				R.drawable.bq_jiyang, R.drawable.bq_keai, };
		private static final Integer[] icons2 = { R.drawable.bq_keling,
				R.drawable.bq_ku, R.drawable.bq_kun, R.drawable.bq_lai,
				R.drawable.bq_landelini, R.drawable.bq_lazhu, R.drawable.bq_lei,
				R.drawable.bq_liwu, R.drawable.bq_lu, R.drawable.bq_nvma,
				R.drawable.bq_ok, R.drawable.bq_paopao, R.drawable.bq_qinqin,
				R.drawable.bq_ruo, R.drawable.bq_shangxin, R.drawable.bq_shiwang,
				R.drawable.bq_shuijiao, R.drawable.bq_taikaixing,
				R.drawable.bq_touxiao, R.drawable.bq_tu, };
		private static final Integer[] icons3 = { R.drawable.bq_wapishi,
				R.drawable.bq_weiqu, R.drawable.bq_xin, R.drawable.bq_xu,
				R.drawable.bq_ye, R.drawable.bq_yinxian, R.drawable.bq_yiwen,
				R.drawable.bq_youhenhen, R.drawable.bq_yun,
				R.drawable.bq_zhuaikuang, R.drawable.bq_zhutou,
				R.drawable.bq_zuohenhen, };
		
		private static final String[] expTextTab1 = { "[衰]", "[拜拜]", "[可怜]",
				"[鄙视]", "[闭嘴]", "[不要]", "[馋嘴]", "[吃惊]", "[打哈气]", "[蛋糕]", "[good]",
				"[鼓掌]", "[哈哈]", "[害羞]", "[汗]", "[呵呵]", "[黑线]", "[花心]", "[鬼脸]",
				"[可爱]", };
		
		private static final String[] expTextTab2 = { "[悲伤]", "[酷]", "[懒得理你]",
				"[来]", "[思考]", "[蜡烛]", "[泪]", "[礼物]", "[怒]", "[怒骂]", "[ok]",
				"[太开心]", "[亲亲]", "[弱]", "[伤心]", "[失望]", "[睡觉]", "[爱你]", "[偷笑]",
				"[生病]", };
		private static final String[] expTextTab3 = { "[挖鼻屎]", "[委屈]", "[心]",
				"[嘘]", "[耶]", "[嘻嘻]", "[疑问]", "[右哼哼]", "[晕]", "[抓狂]", "[猪头]",
				"[左哼哼]", };
		
		static {
			for (int i = 0; i < 20; i++) {
				EXPHM.put(expTextTab1[i], icons1[i]);
			}
			for (int i = 0; i < 20; i++) {
				EXPHM.put(expTextTab2[i], icons2[i]);
			}
			for (int i = 0; i < 12; i++) {
				EXPHM.put(expTextTab3[i], icons3[i]);
			}
		}
}
