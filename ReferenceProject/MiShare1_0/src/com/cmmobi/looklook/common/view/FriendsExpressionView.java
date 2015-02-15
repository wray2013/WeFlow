package com.cmmobi.looklook.common.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
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
import android.widget.TextView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ExpressionAdapter;

public class FriendsExpressionView implements OnPageChangeListener {
	private View expressionLayout;
	private ViewPager expressionPager;
	private LayoutInflater inflater;
	private ArrayList<View> expressionTabs = new ArrayList<View>();
	private Activity activity;
	private ImageView[] ivPageDots;
	private GridView gv1;
	private GridView gv2;
	public static HashMap<String, Integer> EXPHM = new HashMap<String, Integer>();
	public static HashMap<String, Integer> EXPHM_INDEX = new HashMap<String, Integer>();
	private InputMethodManager inputMethodManager;
	private EditText editText;


	/**
	 * 
	 * @param activity
	 * @param editText 需要输入表情的编辑框
	 */

	public FriendsExpressionView(Activity activity, EditText editText) {
		this.activity = activity;
		this.editText = editText;
		
		expressionLayout = activity
				.findViewById(R.id.expression_relative_layout);
		expressionPager = (ViewPager) activity.findViewById(R.id.vp_expression);
		inflater = LayoutInflater.from(activity);
		View tab1 = inflater.inflate(R.layout.del_include_expression_tab1, null);
		View tab2 = inflater.inflate(R.layout.del_include_expression_tab2, null);
		gv1 = (GridView) tab1.findViewById(R.id.gv_tab1);
		gv2 = (GridView) tab2.findViewById(R.id.gv_tab2);
		gv1.setAdapter(new GridViewAdapter(icons1, expTextTab1));
		gv2.setAdapter(new GridViewAdapter(icons2, expTextTab2));
		expressionTabs.add(tab1);
		expressionTabs.add(tab2);
		expressionPager.setAdapter(new ExpressionAdapter(activity,
				expressionTabs));
		expressionPager.setOnPageChangeListener(this);
		ivPageDots = new ImageView[2];
		ivPageDots[0] = (ImageView) expressionLayout.findViewById(R.id.iv_dot1);
		ivPageDots[1] = (ImageView) expressionLayout.findViewById(R.id.iv_dot2);
//		editText.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (View.GONE != expressionLayout.getVisibility())
//					expressionLayout.setVisibility(View.GONE);
//			}
//		});
		inputMethodManager = ((InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
	}

	public View getExpressionView() {
		return expressionLayout;
	}

	public void setOnclickListener(OnItemClickListener onItemClickListener) {
		gv1.setOnItemClickListener(onItemClickListener);
		gv2.setOnItemClickListener(onItemClickListener);
	}

	// public void load() {
	// editText.requestFocus();
	// if (View.GONE == expressionLayout.getVisibility()) {
	// inputMethodManager.hideSoftInputFromWindow(activity
	// .getCurrentFocus().getWindowToken(),
	// InputMethodManager.HIDE_NOT_ALWAYS);
	// expressionLayout.setVisibility(View.VISIBLE);
	// } else {
	// inputMethodManager.showSoftInput(editText, 0);
	// // expressionLayout.setVisibility(View.GONE);
	// }
	// }

	public void showExpressionView() {
		IBinder ib = null;
		if(activity.getCurrentFocus()!=null){
			ib = activity.getCurrentFocus().getWindowToken();
		}
		inputMethodManager.hideSoftInputFromWindow(ib, InputMethodManager.HIDE_NOT_ALWAYS);
		expressionLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				expressionLayout.setVisibility(View.VISIBLE);
			}
		}, 50);
		
	}

	public void hideExpressionView() {
//		inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
		expressionLayout.setVisibility(View.GONE);
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
			ivPageDots[0].setImageResource(R.drawable.del_btn_expression_dot_focus);
			ivPageDots[1].setImageResource(R.drawable.del_btn_expression_dot);
			break;
		case 1:
			ivPageDots[1].setImageResource(R.drawable.del_btn_expression_dot_focus);
			ivPageDots[0].setImageResource(R.drawable.del_btn_expression_dot);
			break;

		default:
			break;
		}
	}
	
	// 取出edittext字符串中的表情字段
	private static ArrayList<String> getTextExpressions(String expressionText) {
		if (expressionText != null && expressionText.length() > 0) {
			ArrayList<String> list = new ArrayList<String>();
//			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
//			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5a-zA-Z]*?\\]");
			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5a-zA-Z0-9_.]*?\\]");
			Matcher matcher = pattern.matcher(expressionText);
			while (matcher.find()) {
				list.add(matcher.group());
			}
			return list;
		}
		return null;
	}
	
	//ios_emoji_array
	/** 
	 * @param expressionText <e>emoji</e>  or [haha]
	 * */
	public static void replacedExpressions(String expressionText, TextView tv) {
		if(TextUtils.isEmpty(expressionText)){
			tv.setText("");
			return;
		}
//		String regex = "\\<e\\>(.*?)\\</e\\>";
		String regex = "\\[[\\u4e00-\\u9fa5a-zA-Z0-9_.]*?\\]|\\<e\\>(.*?)\\</e\\>";
		Pattern pattern = Pattern.compile(regex);
		String emo = "";
		Resources resources = MainApplication.getAppInstance().getResources();
		DisplayMetrics dm = MainApplication.getAppInstance().getResources().getDisplayMetrics();
		int width = (int) (18 * dm.density);
		Matcher matcher = pattern.matcher(expressionText);
		SpannableStringBuilder sBuilder = new SpannableStringBuilder(expressionText);
		while (matcher.find()) {
			Drawable drawable = null;
			ImageSpan span = null;
			emo = matcher.group();
			try {
				int id=0;
				if(EXPHM.get(emo)!=null){
					id=EXPHM.get(emo);
					if (id != 0) {
						drawable = resources.getDrawable(id);
					}else{
						drawable = resources.getDrawable(R.drawable.emoji_default);
					}
				}else if(emo.contains(">")&&emo.contains("<")){
					id = resources.getIdentifier(
							"emoji_"+ emo.substring(emo.indexOf(">") + 1,emo.lastIndexOf("<")), "drawable",
							MainApplication.getAppInstance().getPackageName());
					if (id != 0) {
						drawable = resources.getDrawable(id);
					}else{
						drawable = resources.getDrawable(R.drawable.emoji_default);
					}
				}
				
				if(drawable!=null){
					
					drawable.setBounds(0, 0, width, width);
					span = new ImageSpan(drawable);
					if(span!=null)
						sBuilder.setSpan(span, matcher.start(), matcher.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		
		tv.setText(sBuilder);
	}
	
	// textview中显示文字+表情
	public static void replacedExpressionsReply(String expressionText, TextView tv, int start, int end) {
		if(TextUtils.isEmpty(expressionText)){
			tv.setText("");
			return;
		}
//		String regex = "\\<e\\>(.*?)\\</e\\>";
		String regex = "\\[[\\u4e00-\\u9fa5a-zA-Z0-9_.]*?\\]|\\<e\\>(.*?)\\</e\\>";
		Pattern pattern = Pattern.compile(regex);
		String emo = "";
		Resources resources = MainApplication.getAppInstance().getResources();
		DisplayMetrics dm = MainApplication.getAppInstance().getResources().getDisplayMetrics();
		int width = (int) (18 * dm.density);
		Matcher matcher = pattern.matcher(expressionText);
		SpannableStringBuilder sBuilder = new SpannableStringBuilder(expressionText);
		CharacterStyle spanColor=new ForegroundColorSpan(Color.BLACK); 
		sBuilder.setSpan(spanColor, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
		while (matcher.find()) {
			Drawable drawable = null;
			ImageSpan span = null;
			emo = matcher.group();
			try {
				int id=0;
				if(EXPHM.get(emo)!=null){
					id=EXPHM.get(emo);
					if (id != 0) {
						drawable = resources.getDrawable(id);
					}else{
						drawable = resources.getDrawable(R.drawable.emoji_default);
					}
				}else if(emo.contains(">")&&emo.contains("<")){
					id = resources.getIdentifier(
							"emoji_"+ emo.substring(emo.indexOf(">") + 1,emo.lastIndexOf("<")), "drawable",
							MainApplication.getAppInstance().getPackageName());
					if (id != 0) {
						drawable = resources.getDrawable(id);
					}else{
						drawable = resources.getDrawable(R.drawable.emoji_default);
					}
				}
				
				if(drawable!=null){
					
					drawable.setBounds(0, 0, width, width);
					span = new ImageSpan(drawable);
					if(span!=null)
						sBuilder.setSpan(span, matcher.start(), matcher.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		
		tv.setText(sBuilder);
	}
	
	// textview中显示文字+表情
		public static void replacedExpressionsHint(String expressionText, TextView tv) {
			if(TextUtils.isEmpty(expressionText)){
				tv.setText("");
				return;
			}
//			String regex = "\\<e\\>(.*?)\\</e\\>";
			String regex = "\\[[\\u4e00-\\u9fa5a-zA-Z0-9_.]*?\\]|\\<e\\>(.*?)\\</e\\>";
			Pattern pattern = Pattern.compile(regex);
			String emo = "";
			Resources resources = MainApplication.getAppInstance().getResources();
			DisplayMetrics dm = MainApplication.getAppInstance().getResources().getDisplayMetrics();
			int width = (int) (18 * dm.density);
			Matcher matcher = pattern.matcher(expressionText);
			SpannableStringBuilder sBuilder = new SpannableStringBuilder(expressionText);
			while (matcher.find()) {
				Drawable drawable = null;
				ImageSpan span = null;
				emo = matcher.group();
				try {
					int id=0;
					if(EXPHM.get(emo)!=null){
						id=EXPHM.get(emo);
						if (id != 0) {
							drawable = resources.getDrawable(id);
						}else{
							drawable = resources.getDrawable(R.drawable.emoji_default);
						}
					}else if(emo.contains(">")&&emo.contains("<")){
						id = resources.getIdentifier(
								"emoji_"+ emo.substring(emo.indexOf(">") + 1,emo.lastIndexOf("<")), "drawable",
								MainApplication.getAppInstance().getPackageName());
						if (id != 0) {
							drawable = resources.getDrawable(id);
						}else{
							drawable = resources.getDrawable(R.drawable.emoji_default);
						}
					}
					
					if(drawable!=null){
						
						drawable.setBounds(0, 0, width, width);
						span = new ImageSpan(drawable);
						if(span!=null)
							sBuilder.setSpan(span, matcher.start(), matcher.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			
			tv.setHint(sBuilder);
		}
	
	static ImageGetter imageGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			Drawable drawable = MainApplication.getInstance().getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
					drawable.getIntrinsicHeight() / 2);
			return drawable;
		}
	};

	public static final Integer[] icons1 = {
		R.drawable.bq_hehe, R.drawable.bq_huaxin, R.drawable.bq_ku,
		R.drawable.bq_lei, R.drawable.bq_haixiao, R.drawable.bq_bizui,
		R.drawable.bq_shuijiao, R.drawable.bq_heixiang,
		R.drawable.bq_lu, R.drawable.bq_jiyang, R.drawable.bq_yinxian,
		R.drawable.bq_chijing, R.drawable.bq_beishang, R.drawable.bq_tu,
		R.drawable.bq_touxiao, R.drawable.bq_keai, R.drawable.bq_kun,
		R.drawable.bq_landelini, R.drawable.bq_changzui,  R.drawable.bq_daihaqian,
		R.drawable.bq_han, R.drawable.bq_haha, R.drawable.bq_nvma,
		R.drawable.bq_yiwen, R.drawable.bq_xu, R.drawable.bq_yun,
		R.drawable.bq_zhuaikuang,R.drawable.bq_ai
		
	};

	public static final Integer[] icons2 = {R.drawable.bq_baibai, R.drawable.bq_wapishi,
		R.drawable.bq_guzhang, R.drawable.bq_zuohenhen, R.drawable.bq_youhenhen,
		R.drawable.bq_bishi, R.drawable.bq_weiqu, R.drawable.bq_shiwang,
		R.drawable.bq_qinqin, R.drawable.bq_keling,
		R.drawable.bq_zhutou, R.drawable.bq_taikaixing,R.drawable.bq_xin,
		R.drawable.bq_shangxin, R.drawable.bq_lazhu, R.drawable.bq_dangao,
		R.drawable.bq_liwu, R.drawable.bq_paopao, R.drawable.bq_good,
		R.drawable.bq_ruo, R.drawable.bq_ye,
		R.drawable.bq_lai, R.drawable.bq_buyao,
		R.drawable.bq_ok
	};
	
	public static final String[] expTextTab1 = { "[呵呵]", "[花心]", "[酷]",
		"[泪]", "[害羞]", "[闭嘴]", "[睡觉]", "[黑线]", "[怒]", "[鬼脸]", "[嘻嘻]",
		"[吃惊]", "[可怜]", "[生病]", "[偷笑]", "[可爱]", "[懒得理你]", "[思考]", "[馋嘴]",
		"[打哈气]","[汗]", "[哈哈]", "[怒骂]", "[疑问]", "[嘘]", "[晕]", "[抓狂]", "[衰]"
	};
	
	public static final String[] expTextTab2 = {  "[拜拜]", "[挖鼻屎]", "[鼓掌]",
		"[左哼哼]", "[右哼哼]", "[鄙视]", "[委屈]", "[失望]", "[亲亲]", "[悲伤]",
		"[猪头]",  "[爱你]", "[心]", "[伤心]", "[蜡烛]", "[蛋糕]", "[礼物]", "[太开心]", 
		"[good]", "[弱]", "[耶]", "[来]", "[不要]","[ok]",
		
	};

	static {
		int index = 1;
		for (int i = 0; i < expTextTab1.length; i++) {
			EXPHM.put(expTextTab1[i], icons1[i]);
			EXPHM_INDEX.put(expTextTab1[i], index);
			index++;
		}
		for (int i = 0; i < expTextTab2.length; i++) {
			EXPHM.put(expTextTab2[i], icons2[i]);
			EXPHM_INDEX.put(expTextTab2[i], index);
			index++;
		}
	}

	
	public static int getExpIndex(String strExp){
		Integer index = EXPHM_INDEX.get(strExp);
		if(index == null){
			index = 0;
		}
		return index;
	}
	
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
			int id = icons[position];
			ImageView iv;
			iv = new ImageView(activity);
			iv.setBackgroundResource(id);
			iv.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			iv.setTag(expText[position]);
			return iv;
		}

	}
}
