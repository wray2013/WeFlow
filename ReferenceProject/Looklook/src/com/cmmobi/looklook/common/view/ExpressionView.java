/**
 * 
 */
package com.cmmobi.looklook.common.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ExpressionAdapter;

/**
 * @author wuxiang
 * 
 * @create 2013-4-10
 */
public class ExpressionView implements OnPageChangeListener {

	private View expressionLayout;
	private ViewPager expressionPager;
	private LayoutInflater inflater;
	private ArrayList<View> expressionTabs = new ArrayList<View>();
	private Activity activity;
	private ImageView[] ivPageDots;
	private GridView gv1;
	private GridView gv2;
	private GridView gv3;
	private static HashMap<String, Integer> EXPHM = new HashMap<String, Integer>();
	private InputMethodManager inputMethodManager;
	private EditText editText;

	View popupWindow_view;

	PopupWindow fdj;

	/**
	 * 
	 * @param activity
	 * @param editText
	 *            需要输入表情的编辑框
	 */

	public ExpressionView(Activity activity, EditText editText) {
		this.activity = activity;
		this.editText = editText;
		expressionLayout = activity.findViewById(R.id.rl_expression);
		expressionPager = (ViewPager) activity.findViewById(R.id.vp_expression);
		inflater = LayoutInflater.from(activity);
		View tab1 = inflater.inflate(R.layout.include_expression_tab1, null);
		View tab2 = inflater.inflate(R.layout.include_expression_tab2, null);
		View tab3 = inflater.inflate(R.layout.include_expression_tab3, null);
		gv1 = (GridView) tab1.findViewById(R.id.gv_tab1);
		gv2 = (GridView) tab2.findViewById(R.id.gv_tab2);
		gv3 = (GridView) tab3.findViewById(R.id.gv_tab3);
		gv1.setAdapter(new GridViewAdapter(icons1, expTextTab1));
		gv2.setAdapter(new GridViewAdapter(icons2, expTextTab2));
		gv3.setAdapter(new GridViewAdapter(icons3, expTextTab3));
		expressionTabs.add(tab1);
		expressionTabs.add(tab2);
		expressionTabs.add(tab3);
		expressionPager.setAdapter(new ExpressionAdapter(activity,
				expressionTabs));
		expressionPager.setOnPageChangeListener(this);
		ivPageDots = new ImageView[3];
		ivPageDots[0] = (ImageView) expressionLayout.findViewById(R.id.iv_dot1);
		ivPageDots[1] = (ImageView) expressionLayout.findViewById(R.id.iv_dot2);
		ivPageDots[2] = (ImageView) expressionLayout.findViewById(R.id.iv_dot3);
		editText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (View.GONE != expressionLayout.getVisibility())
					expressionLayout.setVisibility(View.GONE);
			}
		});
		inputMethodManager = ((InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE));

		popupWindow_view = activity.getLayoutInflater().inflate(
				R.layout.activity_homepage_biaoqing_fagndajing_popupwindow,
				null, false);
		fdj = new PopupWindow(popupWindow_view,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
	}

	public void setOnclickListener(OnItemClickListener onItemClickListener) {
		gv1.setOnItemClickListener(onItemClickListener);
		gv2.setOnItemClickListener(onItemClickListener);
		gv3.setOnItemClickListener(onItemClickListener);
	}

	// 取出edittext字符串中的表情字段
	private static ArrayList<String> getTextExpressions(String expressionText) {
		if (expressionText != null && expressionText.length() > 0) {
			ArrayList<String> list = new ArrayList<String>();
			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
			Matcher matcher = pattern.matcher(expressionText);
			while (matcher.find()) {
				list.add(matcher.group());
			}
			return list;
		}
		return null;
	}

	// textview中显示文字+表情
	public void replacedExpressions(String expressionText, TextView tv) {
		ArrayList<String> list = getTextExpressions(expressionText);
		if (list != null && list.size() > 0) {
			int len = list.size();
			int expStart = 0;
			int expEnd = 0;
			tv.setText(null);
			for (int i = 0; i < len; i++) {
				String exp = list.get(i);
				expEnd = expressionText.indexOf(exp, expStart);
				expressionText = expressionText.replaceFirst("\\[" + exp
						+ "\\]", "");
				tv.append(expressionText, expStart, expEnd);
				tv.append(Html.fromHtml("<img src='" + EXPHM.get(exp) + "'/>",
						imageGetter, null));
				expStart = expEnd;
			}
			tv.append(expressionText, expStart, expressionText.length());
		} else {
			tv.append(expressionText);
		}
	}
	
	// 取出edittext字符串中的表情字段
		private static ArrayList<String> getTextExpressions2(String expressionText) {
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
		
		// textview中显示文字+表情
		public void replacedExpressions2(String expressionText, TextView tv) {

			
			tv.setText(null);
			
			if(expressionText==null){
				return;
			}
			
			
			ArrayList<String> list = getTextExpressions2(expressionText);
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
			ivPageDots[0].setImageResource(R.drawable.biaoqing_fanye_2);
			ivPageDots[1].setImageResource(R.drawable.biaoqing_fanye_1);
			ivPageDots[2].setImageResource(R.drawable.biaoqing_fanye_1);
			break;
		case 1:
			ivPageDots[0].setImageResource(R.drawable.biaoqing_fanye_1);
			ivPageDots[1].setImageResource(R.drawable.biaoqing_fanye_2);
			ivPageDots[2].setImageResource(R.drawable.biaoqing_fanye_1);
			break;
		case 2:
			ivPageDots[0].setImageResource(R.drawable.biaoqing_fanye_1);
			ivPageDots[1].setImageResource(R.drawable.biaoqing_fanye_1);
			ivPageDots[2].setImageResource(R.drawable.biaoqing_fanye_2);
			break;

		default:
			break;
		}
	}

	public void load() {
		editText.requestFocus();
		if (View.GONE == expressionLayout.getVisibility()) {
			inputMethodManager.hideSoftInputFromWindow(activity
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			expressionLayout.setVisibility(View.VISIBLE);
		} else {
			inputMethodManager.showSoftInput(editText, 0);
			expressionLayout.setVisibility(View.GONE);
		}
	}

	public int getVisibility() {
		return expressionLayout.getVisibility();
	}

	public void show(boolean show) {
		editText.requestFocus();
		if (show) {
			inputMethodManager.hideSoftInputFromWindow(activity
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			expressionLayout.setVisibility(View.VISIBLE);
		} else {
			inputMethodManager.showSoftInput(editText, 0);
			expressionLayout.setVisibility(View.GONE);
		}
	}

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

	ImageGetter imageGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			Drawable drawable = activity.getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
					drawable.getIntrinsicHeight() / 2);
			return drawable;
		}
	};

	class GridViewAdapter extends BaseAdapter {

		Integer[] icons;
		String[] expText;

		public GridViewAdapter(Integer[] icons, String[] expText) {
			this.icons = icons;
			this.expText = expText;
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
			final int id = icons[position];
			ImageView iv;
			iv = new ImageView(activity);
			iv.setBackgroundResource(id);
			iv.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			iv.setTag(expText[position]);
			/*iv.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					if (action == MotionEvent.ACTION_DOWN) {
						ImageView bq = (ImageView) popupWindow_view
								.findViewById(R.id.iv_biaoqing);
						bq.setImageResource(id);
						fdj.setOutsideTouchable(true);
						fdj.setBackgroundDrawable(activity.getResources()
								.getDrawable(R.drawable.dot_big));

						Bitmap bm = BitmapFactory.decodeResource(
								activity.getResources(), R.drawable.fangdajing);
						int bm_width = bm.getWidth();
						int bm_height = bm.getHeight();
						fdj.showAsDropDown(v, (v.getWidth() - bm_width) / 2,
								0 - (v.getHeight() / 2 + bm_height));
						Log.e("MotionEvent", "ACTION_DOWN");
						return true;
					} else if (action == MotionEvent.ACTION_UP) {
						fdj.dismiss();
						Log.e("MotionEvent", "ACTION_UP");
						return true;
					}
					return false;
				}
			});*/
			return iv;
		}
	}
}
