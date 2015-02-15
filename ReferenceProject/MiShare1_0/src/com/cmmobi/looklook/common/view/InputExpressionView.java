package com.cmmobi.looklook.common.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
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
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ExpressionAdapter;

public class InputExpressionView implements OnPageChangeListener {
	private View expressionLayout;
	private ViewPager expressionPager;
	private LayoutInflater inflater;
	private ArrayList<View> expressionTabs = new ArrayList<View>();
	private Context context;
	private ImageView[] ivPageDots;
	private GridView gv1;
	private GridView gv2;
//	private static HashMap<String, Integer> EXPHM = new HashMap<String, Integer>();
	private InputMethodManager inputMethodManager;
	private EditText editText;


	/**
	 * 
	 * @param activity
	 * @param editText 需要输入表情的编辑框
	 */

	public InputExpressionView(Context context, View view, EditText editText) {
		this.context = context;
		this.editText = editText;
		
		expressionLayout = view;
		expressionPager = (ViewPager) view.findViewById(R.id.vp_expression);
		inflater = LayoutInflater.from(context);
		View tab1 = inflater.inflate(R.layout.del_include_expression_tab1, null);
		View tab2 = inflater.inflate(R.layout.del_include_expression_tab2, null);
		gv1 = (GridView) tab1.findViewById(R.id.gv_tab1);
		gv2 = (GridView) tab2.findViewById(R.id.gv_tab2);
		gv1.setAdapter(new GridViewAdapter(FriendsExpressionView.icons1, FriendsExpressionView.expTextTab1));
		gv2.setAdapter(new GridViewAdapter(FriendsExpressionView.icons2, FriendsExpressionView.expTextTab2));
		expressionTabs.add(tab1);
		expressionTabs.add(tab2);
		expressionPager.setAdapter(new ExpressionAdapter(context,
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
		inputMethodManager = ((InputMethodManager) context
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

	public void showExpressionView(Activity actv) {
		IBinder ib = null;
		if(actv.getCurrentFocus()!=null){
			ib = actv.getCurrentFocus().getWindowToken();
		}
		inputMethodManager.hideSoftInputFromWindow(ib, InputMethodManager.HIDE_NOT_ALWAYS);
		expressionLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				expressionLayout.setVisibility(View.VISIBLE);
			}
		}, 50);
		
	}

	public void hideExpressionView(Activity actv) {
//		inputMethodManager.showSoftInput(actv.getCurrentFocus(), 0);
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
	public void replacedExpressions(String expressionText, TextView tv) {

		
		tv.setText(null);
		
		if(expressionText==null){
			return;
		}
		
		
		ArrayList<String> list = getTextExpressions(expressionText);
		Log.d("replacedExpressions", "list="+list);
		if (list != null && list.size() > 0) {
			int len = list.size();
			int expStart = 0;
			int expEnd = 0;
			tv.setText(null);
			for (int i = 0; i < len; i++) {
				String exp = list.get(i);
				if(FriendsExpressionView.EXPHM.get(exp)!=null){
					int expSrc=FriendsExpressionView.EXPHM.get(exp);
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
	
	ImageGetter imageGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			Drawable drawable = context.getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
					drawable.getIntrinsicHeight() / 2);
			return drawable;
		}
	};

/*	private static final Integer[] icons1 = { R.drawable.face001,
			R.drawable.face002, R.drawable.face003, R.drawable.face004,
			R.drawable.face005, R.drawable.face006, R.drawable.face007,
			R.drawable.face008, R.drawable.face009, R.drawable.face010,
			R.drawable.face011, R.drawable.face012, R.drawable.face013,
			R.drawable.face014, R.drawable.face015, R.drawable.face016,
			R.drawable.face017, R.drawable.face018, R.drawable.face019,
			R.drawable.face020, R.drawable.face021, R.drawable.face022,
			R.drawable.face023, R.drawable.face024, R.drawable.face025,
			R.drawable.face026, R.drawable.face027, R.drawable.face028, };
	private static final Integer[] icons2 = { R.drawable.face029,
			R.drawable.face030, R.drawable.face031, R.drawable.face032,
			R.drawable.face033, R.drawable.face034, R.drawable.face035,
			R.drawable.face036, R.drawable.face037, R.drawable.face038,
			R.drawable.face039, R.drawable.face040, R.drawable.face041,
			R.drawable.face042, R.drawable.face043, R.drawable.face044,
			R.drawable.face045, R.drawable.face046, R.drawable.face047,
			R.drawable.face048, R.drawable.face049, R.drawable.face050,
			R.drawable.face051, R.drawable.face052, R.drawable.face053,
			R.drawable.face054, R.drawable.face055, R.drawable.face056, };*/
	
	/*private static final Integer[] icons1 = {R.drawable.bq_ai,
		R.drawable.bq_baibai, R.drawable.bq_beishang, R.drawable.bq_bishi,
		R.drawable.bq_bizui, R.drawable.bq_buyao, R.drawable.bq_changzui,
		R.drawable.bq_chijing, R.drawable.bq_daihaqian,
		R.drawable.bq_dangao, R.drawable.bq_good, R.drawable.bq_guzhang,
		R.drawable.bq_haha, R.drawable.bq_haixiao, R.drawable.bq_han,
		R.drawable.bq_hehe, R.drawable.bq_heixiang, R.drawable.bq_huaxin,
		R.drawable.bq_jiyang, R.drawable.bq_keai,  R.drawable.bq_keling,
		R.drawable.bq_ku, R.drawable.bq_kun, R.drawable.bq_lai,
		R.drawable.bq_landelini, R.drawable.bq_lazhu, R.drawable.bq_lei,
		R.drawable.bq_liwu
		
	};

	private static final Integer[] icons2 = {R.drawable.bq_lu, R.drawable.bq_nvma,
		R.drawable.bq_ok, R.drawable.bq_paopao, R.drawable.bq_qinqin,
		R.drawable.bq_ruo, R.drawable.bq_shangxin, R.drawable.bq_shiwang,
		R.drawable.bq_shuijiao, R.drawable.bq_taikaixing,
		R.drawable.bq_touxiao, R.drawable.bq_tu,R.drawable.bq_wapishi,
		R.drawable.bq_weiqu, R.drawable.bq_xin, R.drawable.bq_xu,
		R.drawable.bq_ye, R.drawable.bq_yinxian, R.drawable.bq_yiwen,
		R.drawable.bq_youhenhen, R.drawable.bq_yun,
		R.drawable.bq_zhuaikuang, R.drawable.bq_zhutou,
		R.drawable.bq_zuohenhen
		
	};
	
	private static final String[] expTextTab1 = { "[高]", "[兴]", "[快]", "[乐]",
			"[悲]", "[上]", "[与]", "高飞", "兴的", "快的", "乐的", "悲的", "上的", "与的",
			"高饿", "兴饿", "快饿", "乐饿", "悲饿", "上饿", "与饿", "高个", "兴个", "快个", "乐个",
			"悲个", "上个", "与个", "高就", "兴就", "快就", "乐就", "悲就", "上就", "与就", };

	private static final String[] expTextTab2 = { "高", "兴", "快", "乐", "悲", "上",
			"与", "高飞", "兴的", "快的", "乐的", "悲的", "上的", "与的", "高饿", "兴饿", "快饿",
			"乐饿", "悲饿", "上饿", "与饿", "高个", "兴个", "快个", "乐个", "悲个", "上个", "与个",
			"高就", "兴就", "快就", "乐就", "悲就", "上就", "与就", };
	
	private static final String[] expTextTab1 = { "[衰]", "[拜拜]", "[可怜]",
		"[鄙视]", "[闭嘴]", "[不要]", "[馋嘴]", "[吃惊]", "[打哈气]", "[蛋糕]", "[good]",
		"[鼓掌]", "[哈哈]", "[害羞]", "[汗]", "[呵呵]", "[黑线]", "[花心]", "[鬼脸]",
		"[可爱]","[悲伤]", "[酷]", "[懒得理你]", "[来]", "[思考]", "[蜡烛]", "[泪]", "[礼物]"
		
	};
	
	private static final String[] expTextTab2 = {  "[怒]", "[怒骂]", "[ok]",
		"[太开心]", "[亲亲]", "[弱]", "[伤心]", "[失望]", "[睡觉]", "[爱你]", "[偷笑]",
		"[生病]",  "[挖鼻屎]", "[委屈]", "[心]", "[嘘]", "[耶]", "[嘻嘻]", "[疑问]", 
		"[右哼哼]", "[晕]", "[抓狂]", "[猪头]", "[左哼哼]",
		
	};

	static {
		for (int i = 0; i < expTextTab1.length; i++) {
			EXPHM.put(expTextTab1[i], icons1[i]);
		}
		for (int i = 0; i < expTextTab2.length; i++) {
			EXPHM.put(expTextTab2[i], icons2[i]);
		}
	}*/

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
			iv = new ImageView(context);
			iv.setBackgroundResource(id);
			iv.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			iv.setTag(expText[position]);
			return iv;
		}

	}
}
