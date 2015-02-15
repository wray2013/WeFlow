package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinaUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.tencentInfo;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

/**
 * @好友界面
 * @author Administrator
 * 
 */
public class AltFriendActivity extends ZActivity {

	private ListView sinaListView, tencentListView, renrenListView;
	private ListView sinaRecentListView, tencentRecentListView,
			renrenRecentListView;
	/**
	 * 保存用户点击列表某一项的用户名
	 */
	public ArrayList<String> sina_check_list = new ArrayList<String>();
	public ArrayList<String> tencent_check_list = new ArrayList<String>();
	public ArrayList<String> renren_check_list = new ArrayList<String>();

	public static final int RECNET_CASH_SIZE = 5;
	public static final int RESULT_CODE = 10000;

	/**
	 * 保存用户选中的列表的位置
	 */
	private ArrayList<String> sina_pos = new ArrayList<String>();
	private ArrayList<String> tencent_pos = new ArrayList<String>();
	private ArrayList<String> renren_pos = new ArrayList<String>();

	int a, b, k, s, h, g, x, alt = 0;
	// private TextView mixtext2;// 新浪微博
	//
	// private TextView mixtext3;// 腾讯好友
	//
	// private TextView mixtext;// 人人

	private ImageView refreshFriends;

	private ImageView iv_finish;
	private ImageView iv_back;

	private QuickBarView quick_bar_alt;

	private SinaAdapter mad_sina;
	private TencentAdapter mad_tencent;
	private RenrenAdapter mad_renren;
	private SinaAdapter mad_sina_recent;
	private TencentAdapter mad_tencent_recent;
	private RenrenAdapter mad_renren_recent;
	private int mk1, mk2, mk3, mk4, mk5;

	private int flag;
	private int pageno = 1;
	private boolean isSinaBind = false;
	private boolean isTencentBind = false;
	private boolean isRenrenBind = false;

	ImageView webimg;// 图片

	// 使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	private List<Object> array;

	private final int HANDLER_SINA_AUTHOR_SUCCESS = 0;
	private final int HANDLER_TENCENT_AUTHOR_SUCCESS = 1;
	private final int HANDLER_RENREN_AUTHOR_SUCCESS = 2;

	private AccountInfo ai;
	private String userid;
	private String mWeiboType;
	private TextView textname;

	private WeiboAuthListener tencentlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			Prompt.Alert("tencent授权成功！");
			// CookieSyncManager.getInstance().sync();
			Message message = getHandler().obtainMessage(
					HANDLER_TENCENT_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			flag = 1;
			isTencentBind = WeiboRequester.getTencentFriendList(
					AltFriendActivity.this, handler, 50, 1);
			// if(!isTencentBind){
			// createTencentBindDialog();
			// }

		}

		@Override
		public void onCancel(int arg0) {
			Prompt.Alert("tencent授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			Prompt.Alert("tencent授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			Prompt.Alert("tencent授权异常！");
		}

	};

	private WeiboAuthListener sinalistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			Prompt.Alert("sina授权成功！");
			// CookieSyncManager.getInstance().sync();
			Message message = getHandler().obtainMessage(
					HANDLER_SINA_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			flag = 2;
			isSinaBind = WeiboRequester.getSinaFriendList(
					AltFriendActivity.this, handler, 50, pageno);
			// if(!isSinaBind){
			// createSinaBindDialog();
			// }

		}

		@Override
		public void onCancel(int arg0) {
			Prompt.Alert("sina授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			Prompt.Alert("sina授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			Prompt.Alert("sina授权异常！");
		}

	};

	private WeiboAuthListener renrenlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			Prompt.Alert("renren授权成功！");
			// CookieSyncManager.getInstance().sync();
			Message message = getHandler().obtainMessage(
					HANDLER_RENREN_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			flag = 3;
			isRenrenBind = WeiboRequester.getRenrenFriendList(
					AltFriendActivity.this, handler, 50, 1);
			// if (!isRenrenBind){
			// createRenrenBindDialog();
			// }

		}

		@Override
		public void onCancel(int arg0) {
			Prompt.Alert("renren授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			Prompt.Alert("renren授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			Prompt.Alert("renren授权异常！");
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_altfriend);
		// 获取分享类型
		mWeiboType = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_SHARE_TYPE);

		textname = (TextView) findViewById(R.id.textname);

		userid = ActiveAccount.getInstance(this).getUID();
		ai = AccountInfo.getInstance(userid);
		sinaListView = (ListView) findViewById(R.id.list_sina);
		tencentListView = (ListView) findViewById(R.id.list_tencent);
		renrenListView = (ListView) findViewById(R.id.list_renren);
		sinaRecentListView = (ListView) findViewById(R.id.list_sina_recent);
		tencentRecentListView = (ListView) findViewById(R.id.list_tencent_recent);
		renrenRecentListView = (ListView) findViewById(R.id.list_renren_recent);
		// mixtext = (TextView) findViewById(R.id.mixtext);
		// mixtext2 = (TextView) findViewById(R.id.mixtext2);
		// mixtext3 = (TextView) findViewById(R.id.mixtext3);
		refreshFriends = (ImageView) findViewById(R.id.iv_activity_alt_friends_refresh);
		quick_bar_alt = (QuickBarView) findViewById(R.id.quick_bar_alt);
		iv_finish = (ImageView) findViewById(R.id.iv_finish);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		// mixtext.setOnClickListener(this);
		// mixtext2.setOnClickListener(this);
		// mixtext3.setOnClickListener(this);
		refreshFriends.setOnClickListener(this);
		iv_finish.setOnClickListener(this);
		iv_back.setOnClickListener(this);

		array = new ArrayList<Object>();
		handler = getHandler();
		if (mWeiboType.equals("1")) {
			flag = 2;
			isSinaBind = WeiboRequester.getSinaFriendList(this, handler, 50,
					pageno);
			textname.setText("新浪微博");
			if (!isSinaBind) {
				createSinaBindDialog();
			}
		} else if (mWeiboType.equals("2")) {
			flag = 3;
			isRenrenBind = WeiboRequester.getRenrenFriendList(this, handler,
					50, 1);
			textname.setText("人人网");
			if (!isRenrenBind) {
				createRenrenBindDialog();
			}
		} else if (mWeiboType.equals("6")) {
			flag = 1;
			isTencentBind = WeiboRequester.getTencentFriendList(this, handler,
					50, 1);
			textname.setText("腾讯微博");
			if (!isTencentBind) {
				createTencentBindDialog();
			}
		}

		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder()

		.showStubImage(R.drawable.moren_touxiang)
				.showImageForEmptyUri(R.drawable.moren_touxiang)
				.showImageOnFail(R.drawable.moren_touxiang).cacheInMemory(true)
				.cacheOnDisc(true)
				// .displayer(new SimpleBitmapDisplayer())
				.displayer(new CircularBitmapDisplayer()) // 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();

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
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
			CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(
					requestCode, resultCode, intent);
		}
	}

	private ArrayList<tencentInfo> tencentList;
	private ArrayList<sinaUser> sinaList;
	private ArrayList<RWUser> renrenList;

	// private List<Object> m;

	// ===============================================================================
	/**
	 * 处理返回回来的结果
	 */
	@Override
	public boolean handleMessage(Message msg) {
		// tencentList = new ArrayList<Object>();
		// sinaList = new ArrayList<Object>();
		// renrenList = new ArrayList<Object>();
		if (array != null) {
			array.clear();
			// tencentList.clear();
			// sinaList.clear();
			// renrenList.clear();
		}
		switch (msg.what) {
		case WeiboRequester.SINA_INTERFACE_FRIENDS_LIST:
			ZDialog.dismiss();
			WeiboResponse.SinaFriends reponesd_sina = (WeiboResponse.SinaFriends) msg.obj;
			if (reponesd_sina != null) {

				tencentListView.setVisibility(View.GONE);
				sinaListView.setVisibility(View.VISIBLE);
				renrenListView.setVisibility(View.GONE);

				WeiboResponse.sinaUser[] muser = reponesd_sina.users;
				for (int n = 0; muser !=null && n < muser.length; n++) {
					array.add(muser[n]);
				}
				Collections.sort(array, new WeiBoComparator());
				sinaList = new ArrayList<sinaUser>();
				if (ai.sinaFriendsList == null) {
					ai.sinaFriendsList = new ArrayList<sinaUser>();
				} else {
					ai.sinaFriendsList.clear();
				}
				for (int i = 0; i < array.size(); i++) {
					sinaUser te = (sinaUser) array.get(i);
					sinaList.add(te);
					ai.sinaFriendsList.add(te);
				}
				quick_bar_alt.setListView(sinaListView);
				mad_sina = new SinaAdapter(this, sinaList);
				sinaListView.setAdapter(mad_sina);
				sinaListView.setOnItemClickListener(ccl);
			}
			break;
		case WeiboRequester.RENREN_INTERFACE_FRIENDS_LIST:
			ZDialog.dismiss();
			WeiboResponse.Renrenfriend reponesd_renren = (WeiboResponse.Renrenfriend) msg.obj;
			if (reponesd_renren != null /*
										 * && reponesd_renren.response != null
										 * && reponesd_renren.response.length >
										 * 0
										 */) {

				tencentListView.setVisibility(View.GONE);
				sinaListView.setVisibility(View.GONE);
				renrenListView.setVisibility(View.VISIBLE);

				for (int n = 0; reponesd_renren.response!= null && n < reponesd_renren.response.length; n++) {
					array.add(reponesd_renren.response[n]);
				}
				Collections.sort(array, new WeiBoComparator());
				if (ai.renrenFriendsList == null) {
					ai.renrenFriendsList = new ArrayList<RWUser>();
				} else {
					ai.renrenFriendsList.clear();
				}
				renrenList = new ArrayList<RWUser>();
				for (int i = 0; i < array.size(); i++) {
					RWUser te = (RWUser) array.get(i);
					renrenList.add(te);
					ai.renrenFriendsList.add(te);
				}
				quick_bar_alt.setListView(renrenListView);
				mad_renren = new RenrenAdapter(this, renrenList);
				renrenListView.setAdapter(mad_renren);
				renrenListView.setOnItemClickListener(ccl);
			}
			break;
		case WeiboRequester.TENCENT_INTERFACE_FRIENDS_LIST:
			ZDialog.dismiss();
			WeiboResponse.TencentFriends reponesd_tencent = (WeiboResponse.TencentFriends) msg.obj;
			if (reponesd_tencent != null) {

				tencentListView.setVisibility(View.VISIBLE);
				sinaListView.setVisibility(View.GONE);
				renrenListView.setVisibility(View.GONE);

				ZLog.printObject(reponesd_tencent);
				WeiboResponse.tencentData mdata = reponesd_tencent.data;
				if (mdata != null) {
					WeiboResponse.tencentInfo[] minfo = mdata.info;
					if (minfo != null) {
						for (int i = 0; i < minfo.length; i++) {
							array.add(minfo[i]);
						}

						Collections.sort(array, new WeiBoComparator());
						if (ai.tencentFriendsList == null) {
							ai.tencentFriendsList = new ArrayList<tencentInfo>();
						} else {
							ai.tencentFriendsList.clear();
						}
						tencentList = new ArrayList<tencentInfo>();
						for (int i = 0; i < array.size(); i++) {
							tencentInfo te = (tencentInfo) array.get(i);
							tencentList.add(te);
							ai.tencentFriendsList.add(te);
							quick_bar_alt.setListView(tencentListView);
							mad_tencent = new TencentAdapter(this, tencentList);
							tencentListView.setAdapter(mad_tencent);
							tencentListView.setOnItemClickListener(ccl);
						}
					}
				}
//				else {
//					Toast.makeText(AltFriendActivity.this, "获取好友列表失败",
//							Toast.LENGTH_SHORT).show();
//				}
			}
			break;
		}
		return false;
	}

	private OnItemClickListener ccl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			CheckBox cb = (CheckBox) view.findViewById(R.id.checkboxxx);
			TextView names = (TextView) view.findViewById(R.id.text_tencent);
			Log.e("被选中的条目是：", pos + "");
			String intobj = pos + "";
			if (flag == 2) {
				if (cb.isChecked()) {
					sina_pos.remove(intobj);
					cb.setChecked(false);
					sina_check_list.remove(names.getText().toString());
				} else {
					sina_pos.add(intobj);
					cb.setChecked(true);
					sina_check_list.add(names.getText().toString());
				}
			} else if (flag == 3) {
				if (cb.isChecked()) {
					renren_pos.remove(intobj);
					cb.setChecked(false);
					renren_check_list.remove(names.getText().toString() + "(" + renrenList.get(pos).id + ")");
				} else {
					renren_pos.add(intobj);
					cb.setChecked(true);
					renren_check_list.add(names.getText().toString() + "(" + renrenList.get(pos).id + ")");
				}
			} else if (flag == 1) {
				if (tencentList != null) {
					if (cb.isChecked()) {
						tencent_pos.remove(intobj);
						cb.setChecked(false);
						tencent_check_list.remove(tencentList.get(pos).name);
					} else {
						tencent_pos.add(intobj);
						cb.setChecked(true);
						tencent_check_list.add(tencentList.get(pos).name);
					}
				}
			}
		}
	};

	class SinaAdapter extends BaseAdapter implements SectionIndexer {
		private Context context;
		private ArrayList<sinaUser> list;
		private LayoutInflater inflater;

		public SinaAdapter(Context context, ArrayList<sinaUser> list) {
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder hold;

			final Object object = list.get(position);

			// if (convertView == null) {
			hold = new ViewHolder();
			View view = inflater.inflate(R.layout.activity_alt_friend_tecent,
					null);

			hold.text_tencent = (TextView) view.findViewById(R.id.text_tencent);
			hold.wev = (ImageView) view.findViewById(R.id.webimgxx);

			hold.check = (CheckBox) view.findViewById(R.id.checkboxxx);
			if (sina_pos.size() > 0) {
				for (int i = 0; i < sina_pos.size()
						&& position == Integer.parseInt(sina_pos.get(i)); i++) {
					hold.check.setChecked(true);
				}
			}

			hold.text_tencent.setText(((sinaUser) object).screen_name);

			/*
			 * hold.wev.setLoadingDrawable(R.drawable.temp_local_icon);
			 * hold.wev.setImageUrl(((sinaUser) object).profile_image_url, 1,
			 * true);
			 */

			if (object != null && ((sinaUser) object).profile_image_url != null) {
				if (!imageLoader.isInited()) {
					imageLoader.init(ImageLoaderConfiguration
							.createDefault(AltFriendActivity.this));
				}
				imageLoader.displayImage(((sinaUser) object).profile_image_url,
						hold.wev, options, animateFirstListener, ActiveAccount
								.getInstance(context).getUID(), 1);
			} else {
				hold.wev.setImageResource(R.drawable.moren_touxiang);
			}

			return view;
		}

		class ViewHolder {
			TextView alt_title;
			ImageView wev;
			TextView text_tencent;
			CheckBox check;
		}

		/**
		 * 提取英文的首字母，非英文字母用#代替。
		 * 
		 * @param str
		 * @return
		 */
		private String getAlpha(String str) {
			if (str == null) {
				return "#";
			}
			if (str.trim().length() == 0) {
				return "#";
			}
			char c = str.trim().substring(0, 1).charAt(0);
			// 正则表达式，判断首字母是否是英文字母
			Pattern pattern = Pattern.compile("^[A-Za-z]+$");
			if (pattern.matcher(c + "").matches()) {
				return (c + "").toUpperCase(); // 大写输出
			} else {
				return "#";
			}
		}

		@Override
		public int getPositionForSection(int section) {
			char key = 'a';
			// if (flag == 1) {
			// for (int i = 0; i < list.size(); i++) {
			// key = getAlpha(((tencentInfo) (list.get(i))).nick)
			// .charAt(0);
			// if (key == section) {
			// return i;
			// }
			// }
			// } else
			if (flag == 2) {
				for (int i = 0; i < list.size(); i++) {
					key = getAlpha(((sinaUser) (list.get(i))).name).charAt(0);
					if (key == section) {
						return i;
					}
				}
			}
			// else if (flag == 3) {
			// for (int i = 0; i < list.size(); i++) {
			// key = getAlpha(((RWUser) (list.get(i))).name).charAt(0);
			// if (key == section) {
			// return i;
			// }
			// }
			// }
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class WeiBoComparator implements Comparator<Object> {
		String str1;
		String str2;

		@Override
		public int compare(Object lhs, Object rhs) {
			if (flag == 1) {
				tencentInfo me = (tencentInfo) lhs;
				tencentInfo mt = (tencentInfo) rhs;

				str1 = "" + getAlpha(toPinYin(me.nick.trim()));
				str2 = "" + getAlpha(toPinYin(mt.nick.trim()));

			} else if (flag == 2) {

				sinaUser me = (sinaUser) lhs;
				sinaUser mt = (sinaUser) rhs;
				str1 = "" + getAlpha(toPinYin(me.name.trim()));
				str2 = "" + getAlpha(toPinYin(mt.name.trim()));

			} else if (flag == 3) {
				RWUser me = (RWUser) lhs;
				RWUser mt = (RWUser) rhs;
				str1 = "" + getAlpha(toPinYin(me.name.trim()));
				str2 = "" + getAlpha(toPinYin(mt.name.trim()));
			}
			return str1.compareTo(str2);
		}
	}

	class TencentAdapter extends BaseAdapter implements SectionIndexer {
		private Context context;
		private ArrayList<tencentInfo> list;
		private LayoutInflater inflater;

		public TencentAdapter(Context context, ArrayList<tencentInfo> list) {
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder hold;

			final Object object = list.get(position);

			// if (convertView == null) {
			hold = new ViewHolder();
			// if (object instanceof String) {
			// convertView = inflater.inflate(
			// R.layout.activity_altfriend_title, null);
			// hold.alt_title = (TextView) convertView
			// .findViewById(R.id.alt_title);
			// hold.alt_title.setText((String) object);
			// hold.alt_title.setVisibility(View.VISIBLE);
			// } else {
			View view = inflater.inflate(R.layout.activity_alt_friend_tecent,
					null);

			hold.text_tencent = (TextView) view.findViewById(R.id.text_tencent);
			hold.wev = (ImageView) view.findViewById(R.id.webimgxx);
			hold.check = (CheckBox) view.findViewById(R.id.checkboxxx);
			if (tencent_pos.size() > 0) {
				for (int i = 0; i < tencent_pos.size()
						&& position == Integer.parseInt(tencent_pos.get(i)); i++) {
					hold.check.setChecked(true);
				}
			}
			// hold.check
			// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//
			// @Override
			// public void onCheckedChanged(
			// CompoundButton buttonView, boolean isChecked) {
			// hold.check.setChecked(isChecked);
			// }
			// });
			// convertView.setTag(hold);
			// }
			// } else {
			// hold = (ViewHolder) convertView.getTag();
			// }
			hold.text_tencent.setText(((tencentInfo) object).nick);
			/*
			 * hold.wev.setLoadingDrawable(R.drawable.temp_local_icon);
			 * hold.wev.setImageUrl(((tencentInfo) object).headurl, 1, true);
			 */
			if (object != null && ((tencentInfo) object).headurl != null) {
				if (!imageLoader.isInited()) {
					imageLoader.init(ImageLoaderConfiguration
							.createDefault(AltFriendActivity.this));
				}
				imageLoader.displayImage(((tencentInfo) object).headurl + "50",
						hold.wev, options, animateFirstListener, ActiveAccount
								.getInstance(context).getUID(), 1);
			} else {
				hold.wev.setImageResource(R.drawable.moren_touxiang);
			}

			return view;
		}

		class ViewHolder {
			TextView alt_title;
			ImageView wev;
			TextView text_tencent;
			CheckBox check;
		}

		/**
		 * 提取英文的首字母，非英文字母用#代替。
		 * 
		 * @param str
		 * @return
		 */
		private String getAlpha(String str) {
			if (str == null) {
				return "#";
			}

			if (str.trim().length() == 0) {
				return "#";
			}

			char c = str.trim().substring(0, 1).charAt(0);
			// 正则表达式，判断首字母是否是英文字母
			Pattern pattern = Pattern.compile("^[A-Za-z]+$");
			if (pattern.matcher(c + "").matches()) {
				return (c + "").toUpperCase(); // 大写输出
			} else {
				return "#";
			}
		}

		@Override
		public int getPositionForSection(int section) {
			char key = 'a';
			if (flag == 1) {
				for (int i = 0; i < list.size(); i++) {
					key = getAlpha(((tencentInfo) (list.get(i))).nick)
							.charAt(0);
					if (key == section) {
						return i;
					}
				}
			}
			// else if (flag == 2) {
			// for (int i = 0; i < list.size(); i++) {
			// key = getAlpha(((sinaUser) (list.get(i))).name).charAt(0);
			// if (key == section) {
			// return i;
			// }
			// }
			// } else if (flag == 3) {
			// for (int i = 0; i < list.size(); i++) {
			// key = getAlpha(((RWUser) (list.get(i))).name).charAt(0);
			// if (key == section) {
			// return i;
			// }
			// }
			// }
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class RenrenAdapter extends BaseAdapter implements SectionIndexer {
		private Context context;
		private ArrayList<RWUser> list;
		private LayoutInflater inflater;

		public RenrenAdapter(Context context, ArrayList<RWUser> list) {
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder hold;

			final Object object = list.get(position);

			// if (convertView == null) {
			hold = new ViewHolder();
			// if (object instanceof String) {
			// convertView = inflater.inflate(
			// R.layout.activity_altfriend_title, null);
			// hold.alt_title = (TextView) convertView
			// .findViewById(R.id.alt_title);
			// hold.alt_title.setText((String) object);
			// hold.alt_title.setVisibility(View.VISIBLE);
			// } else {
			View view = inflater.inflate(R.layout.activity_alt_friend_tecent,
					null);

			hold.text_tencent = (TextView) view.findViewById(R.id.text_tencent);
			hold.wev = (ImageView) view.findViewById(R.id.webimgxx);
			hold.check = (CheckBox) view.findViewById(R.id.checkboxxx);
			if (renren_pos.size() > 0) {
				for (int i = 0; i < renren_pos.size()
						&& position == Integer.parseInt(renren_pos.get(i)); i++) {
					hold.check.setChecked(true);
				}
			}
			// hold.check
			// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//
			// @Override
			// public void onCheckedChanged(
			// CompoundButton buttonView, boolean isChecked) {
			// hold.check.setChecked(isChecked);
			// }
			// });
			// convertView.setTag(hold);

			// }
			// } else {
			// hold = (ViewHolder) convertView.getTag();
			// }
			hold.text_tencent.setText(((RWUser) object).name);
			/*
			 * if (((RWUser) object).avatar != null && ((RWUser)
			 * object).avatar.length > 0 && ((RWUser) object).avatar[0].url !=
			 * null) { hold.wev.setLoadingDrawable(R.drawable.temp_local_icon);
			 * hold.wev.setImageUrl(((RWUser) object).avatar[0].url, 1, true); }
			 */
			if (object != null && ((RWUser) object).avatar != null
					&& ((RWUser) object).avatar.length > 0
					&& ((RWUser) object).avatar[0].url != null) {
				if (!imageLoader.isInited()) {
					imageLoader.init(ImageLoaderConfiguration
							.createDefault(AltFriendActivity.this));
				}
				imageLoader.displayImage(((RWUser) object).avatar[0].url,
						hold.wev, options, animateFirstListener, ActiveAccount
								.getInstance(context).getUID(), 1);
			} else {
				hold.wev.setImageResource(R.drawable.moren_touxiang);
			}
			return view;
		}

		class ViewHolder {
			TextView alt_title;
			ImageView wev;
			TextView text_tencent;
			CheckBox check;
		}

		/**
		 * 提取英文的首字母，非英文字母用#代替。
		 * 
		 * @param str
		 * @return
		 */
		private String getAlpha(String str) {
			if (str == null) {
				return "#";
			}

			if (str.trim().length() == 0) {
				return "#";
			}

			char c = str.trim().substring(0, 1).charAt(0);
			// 正则表达式，判断首字母是否是英文字母
			Pattern pattern = Pattern.compile("^[A-Za-z]+$");
			if (pattern.matcher(c + "").matches()) {
				return (c + "").toUpperCase(); // 大写输出
			} else {
				return "#";
			}
		}

		@Override
		public int getPositionForSection(int section) {
			char key = 'a';
			// if (flag == 1) {
			// for (int i = 0; i < list.size(); i++) {
			// key = getAlpha(((tencentInfo) (list.get(i))).nick)
			// .charAt(0);
			// if (key == section) {
			// return i;
			// }
			// }
			// } else if (flag == 2) {
			// for (int i = 0; i < list.size(); i++) {
			// key = getAlpha(((sinaUser) (list.get(i))).name).charAt(0);
			// if (key == section) {
			// return i;
			// }
			// }
			// } else
			if (flag == 3) {
				for (int i = 0; i < list.size(); i++) {
					key = getAlpha(((RWUser) (list.get(i))).name).charAt(0);
					if (key == section) {
						return i;
					}
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		ZDialog.show(R.layout.progressdialog, true, true, this);
		switch (v.getId()) {
		/*
		 * case R.id.mixtext: isRenrenBind = csl.isRenrenWeiboAuthorized(); flag
		 * = 3; // mixtext.setBackgroundResource(R.drawable.qiehuan_xuanzhong);
		 * // mixtext2.setBackgroundResource(0); //
		 * mixtext3.setBackgroundResource(0); if (ai.renrenFriendsList != null
		 * && ai.renrenFriendsList.size() > 0) { ZDialog.dismiss();
		 * tencentListView.setVisibility(View.GONE);
		 * sinaListView.setVisibility(View.GONE);
		 * renrenListView.setVisibility(View.VISIBLE); if
		 * (ai.renrenRecentFriendsList != null &&
		 * ai.renrenRecentFriendsList.size() > 0) {
		 * renrenRecentListView.setVisibility(View.VISIBLE);
		 * sinaRecentListView.setVisibility(View.INVISIBLE);
		 * tencentRecentListView.setVisibility(View.INVISIBLE);
		 * mad_renren_recent = new RenrenAdapter( AltFriendActivity.this,
		 * ai.renrenRecentFriendsList);
		 * renrenRecentListView.setAdapter(mad_renren_recent); } mad_renren =
		 * new RenrenAdapter(AltFriendActivity.this, ai.renrenFriendsList);
		 * renrenListView.setAdapter(mad_renren);
		 * renrenListView.setOnItemClickListener(ccl); }
		 * 
		 * 
		 * if (!isRenrenBind) { createRenrenBindDialog(); ZDialog.dismiss();
		 * }else{ ai.renrenFriendsList = new ArrayList<RWUser>(); isRenrenBind =
		 * WeiboRequester.getRenrenFriendList(this, handler, 50, 1); } break;
		 * case R.id.mixtext2: isSinaBind = csl.isSinaWeiboAuthorized(); flag =
		 * 2; mixtext2.setBackgroundResource(R.drawable.qiehuan_xuanzhong); if
		 * (ai.sinaFriendsList != null && ai.sinaFriendsList.size() > 0) {
		 * ZDialog.dismiss(); tencentListView.setVisibility(View.GONE);
		 * sinaListView.setVisibility(View.VISIBLE);
		 * renrenListView.setVisibility(View.GONE); if (ai.sinaRecentFriendsList
		 * != null && ai.sinaRecentFriendsList.size() > 0) {
		 * renrenRecentListView.setVisibility(View.INVISIBLE);
		 * sinaRecentListView.setVisibility(View.VISIBLE);
		 * tencentRecentListView.setVisibility(View.INVISIBLE); mad_sina_recent
		 * = new SinaAdapter(AltFriendActivity.this, ai.sinaRecentFriendsList);
		 * sinaRecentListView.setAdapter(mad_sina_recent); } mad_sina = new
		 * SinaAdapter(AltFriendActivity.this, ai.sinaFriendsList);
		 * sinaListView.setAdapter(mad_sina);
		 * sinaListView.setOnItemClickListener(ccl); } if (!isSinaBind) {
		 * createSinaBindDialog(); ZDialog.dismiss(); }else { ai.sinaFriendsList
		 * = new ArrayList<sinaUser>(); isSinaBind =
		 * WeiboRequester.getSinaFriendList(this, handler, 50, pageno); } break;
		 * case R.id.mixtext3: isTencentBind = csl.isTencentWeiboAuthorized();
		 * flag = 1;
		 * mixtext3.setBackgroundResource(R.drawable.qiehuan_xuanzhong); if
		 * (ai.tencentFriendsList != null && ai.tencentFriendsList.size() > 0) {
		 * ZDialog.dismiss(); tencentListView.setVisibility(View.VISIBLE);
		 * sinaListView.setVisibility(View.GONE);
		 * renrenListView.setVisibility(View.GONE); if
		 * (ai.tencentRecentFriendsList != null &&
		 * ai.tencentRecentFriendsList.size() > 0) {
		 * renrenRecentListView.setVisibility(View.INVISIBLE);
		 * sinaRecentListView.setVisibility(View.INVISIBLE);
		 * tencentRecentListView.setVisibility(View.VISIBLE); mad_tencent_recent
		 * = new TencentAdapter( AltFriendActivity.this,
		 * ai.tencentRecentFriendsList);
		 * tencentRecentListView.setAdapter(mad_tencent_recent); } mad_tencent =
		 * new TencentAdapter(AltFriendActivity.this, ai.tencentFriendsList);
		 * tencentListView.setAdapter(mad_tencent);
		 * tencentListView.setOnItemClickListener(ccl); } if (!isTencentBind) {
		 * createTencentBindDialog(); ZDialog.dismiss(); } else {
		 * ai.tencentFriendsList = new ArrayList<tencentInfo>(); isTencentBind =
		 * WeiboRequester.getTencentFriendList(this, handler, 50, 1); } break;
		 */
		case R.id.iv_activity_alt_friends_refresh:
			if (flag == 1) {
				isTencentBind = WeiboRequester.getTencentFriendList(this,
						handler, 50, 1);
				if (!isTencentBind) {
					createTencentBindDialog();
					ZDialog.dismiss();
				}
			}
			if (flag == 2) {
				isSinaBind = WeiboRequester.getSinaFriendList(this, handler,
						50, pageno);
				if (!isSinaBind) {
					createSinaBindDialog();
					ZDialog.dismiss();
				}
			}
			if (flag == 3) {
				isRenrenBind = WeiboRequester.getRenrenFriendList(this,
						handler, 50, 1);
				if (!isRenrenBind) {
					createRenrenBindDialog();
					ZDialog.dismiss();
				}
			}
			break;
		case R.id.back:
			AltFriendActivity.this.finish();
			break;
//		case R.id.checkboxxx:
//			break;
		case R.id.iv_finish:
			saveToCash();

			Intent intent = new Intent();
			if (sina_check_list.size() > 0) {
				intent.putStringArrayListExtra("SINA_FRIEDS_NAME_LIST",
						sina_check_list);
			}
			if (tencent_check_list.size() > 0) {
				intent.putStringArrayListExtra("TENCENT_FRIEDS_NAME_LIST",
						tencent_check_list);
			}
			if (renren_check_list.size() > 0) {
				intent.putStringArrayListExtra("RENREN_FRIEDS_NAME_LIST",
						renren_check_list);
			}
			setResult(RESULT_CODE, intent);
			AltFriendActivity.this.finish();
			break;
		case R.id.iv_back:
			AltFriendActivity.this.finish();
			break;
		}
	}

	/**
	 * 最近联系缓存处理、好友列表缓存处理
	 */
	private void saveToCash() {
		if (ai.sinaFriendsList == null) {
			ai.sinaFriendsList = new ArrayList<sinaUser>();
			if (sinaList != null) {
				ai.sinaFriendsList.addAll(sinaList);
			}
		}
		if (ai.tencentFriendsList == null) {
			ai.tencentFriendsList = new ArrayList<tencentInfo>();
			if (tencentList != null) {
				ai.tencentFriendsList.addAll(tencentList);
			}
		}
		if (ai.renrenFriendsList == null) {
			ai.renrenFriendsList = new ArrayList<RWUser>();
			if (renrenList != null) {
				ai.renrenFriendsList.addAll(renrenList);
			}
		}

		// zhw ...
		// 保存sina最近联系人列表
		if (ai.sinaRecentFriendsList == null) {
			ai.sinaRecentFriendsList = new ArrayList<sinaUser>();
		}
		for (String i : sina_pos) {
			ai.sinaRecentFriendsList.add(sinaList.get(Integer.parseInt(i)));
		}

		Iterator<sinaUser> iterator_sina = ai.sinaRecentFriendsList.iterator();
		int len_sina = ai.sinaRecentFriendsList.size();
		if (len_sina > RECNET_CASH_SIZE) {

			for (int index = 0; iterator_sina.hasNext()
					&& index < len_sina - RECNET_CASH_SIZE; index++) {
				// ai.sinaRecentFriendsList.remove(len);
				iterator_sina.next();
				iterator_sina.remove();
			}
		}

		
		if (ai.tencentRecentFriendsList == null) {
			ai.tencentRecentFriendsList = new ArrayList<tencentInfo>();
		}
		for (String i : tencent_pos) {
			ai.tencentRecentFriendsList
					.add(tencentList.get(Integer.parseInt(i)));
		}

		Iterator<tencentInfo> iterator_tencent = ai.tencentRecentFriendsList
				.iterator();
		int len_tencent = ai.tencentRecentFriendsList.size();
		if (len_tencent > RECNET_CASH_SIZE) {

			for (int index = 0; iterator_tencent.hasNext()
					&& index < len_tencent - RECNET_CASH_SIZE; index++) {
				// ai.sinaRecentFriendsList.remove(len);
				iterator_tencent.next();
				iterator_tencent.remove();
			}
		}

		
		if (ai.renrenRecentFriendsList == null) {
			ai.renrenRecentFriendsList = new ArrayList<RWUser>();
		}
		for (String i : renren_pos) {
			ai.renrenRecentFriendsList.add(renrenList.get(Integer.parseInt(i)));
		}

		Iterator<RWUser> iterator_renren = ai.renrenRecentFriendsList
				.iterator();
		int len_renren = ai.renrenRecentFriendsList.size();
		if (len_renren > RECNET_CASH_SIZE) {

			for (int index = 0; iterator_renren.hasNext()
					&& index < len_renren - RECNET_CASH_SIZE; index++) {
				// ai.sinaRecentFriendsList.remove(len);
				iterator_renren.next();
				iterator_renren.remove();
			}
		}

		/*
		 * if (ai.sinaRecentFriendsList != null) { if (sina_pos.size() >
		 * RECNET_CASH_SIZE) { ai.sinaRecentFriendsList.clear(); for (int i =
		 * sina_pos.size(); i > (sina_pos.size() - RECNET_CASH_SIZE - 1); i--) {
		 * ai.sinaRecentFriendsList.add(sinaList.get(Integer.parseInt(sina_pos
		 * .get(i - 1)))); } } else if (sina_pos.size() > 0 && sina_pos.size()
		 * <= RECNET_CASH_SIZE) { ArrayList<sinaUser> sinaRecentFriendsListClone
		 * = new ArrayList<sinaUser>();
		 * sinaRecentFriendsListClone.addAll(ai.sinaRecentFriendsList);
		 * ai.sinaRecentFriendsList.clear(); for (int i = sina_pos.size(); i >
		 * 0; i--) {
		 * ai.sinaRecentFriendsList.add(sinaList.get(Integer.parseInt(sina_pos
		 * .get(i - 1)))); } if (sinaRecentFriendsListClone.size() +
		 * sina_pos.size() <= RECNET_CASH_SIZE) { for (int i = 0; i <
		 * sina_pos.size(); i++) {
		 * ai.sinaRecentFriendsList.add(sinaRecentFriendsListClone .get(i - 1));
		 * } } else { for (int i = 0; i < (RECNET_CASH_SIZE - sina_pos.size());
		 * i--) { ai.sinaRecentFriendsList.add(sinaRecentFriendsListClone
		 * .get(i)); } } } } else { ai.sinaRecentFriendsList = new
		 * ArrayList<sinaUser>(); if (sina_pos.size() > 0 && sina_pos.size() <=
		 * RECNET_CASH_SIZE) { for (int i = sina_pos.size(); i > 0; i--) {
		 * ai.sinaRecentFriendsList.add(sinaList.get(i - 1)); } } else if
		 * (sina_pos.size() > 5) { for (int i = sina_pos.size(); i >
		 * (sina_pos.size() - RECNET_CASH_SIZE - 1); i--) {
		 * ai.sinaRecentFriendsList.add(sinaList.get(i - 1)); } } } //
		 * 保存tencent最近联系人列表 if (ai.tencentRecentFriendsList != null) { if
		 * (tencent_pos.size() > RECNET_CASH_SIZE) {
		 * ai.tencentRecentFriendsList.clear(); for (int i = tencent_pos.size();
		 * i > (tencent_pos.size() - RECNET_CASH_SIZE - 1); i--) {
		 * ai.tencentRecentFriendsList
		 * .add(tencentList.get(Integer.parseInt(tencent_pos .get(i - 1)))); } }
		 * else if (tencent_pos.size() > 0 && tencent_pos.size() <=
		 * RECNET_CASH_SIZE) { ArrayList<tencentInfo>
		 * tencentRecentFriendsListClone = new ArrayList<tencentInfo>();
		 * tencentRecentFriendsListClone .addAll(ai.tencentRecentFriendsList);
		 * ai.tencentRecentFriendsList.clear(); for (int i = tencent_pos.size();
		 * i > 0; i--) {
		 * ai.tencentRecentFriendsList.add(tencentList.get(Integer.
		 * parseInt(tencent_pos .get(i - 1)))); } if
		 * (tencentRecentFriendsListClone.size() + tencent_pos.size() <=
		 * RECNET_CASH_SIZE) { for (int i = 0; i < tencent_pos.size(); i++) {
		 * ai.tencentRecentFriendsList .add(tencentRecentFriendsListClone.get(i
		 * - 1)); } } else { for (int i = 0; i < (RECNET_CASH_SIZE -
		 * tencent_pos.size()); i--) { ai.tencentRecentFriendsList
		 * .add(tencentRecentFriendsListClone.get(i)); } } } } else {
		 * ai.tencentRecentFriendsList = new ArrayList<tencentInfo>(); if
		 * (tencent_pos.size() > 0 && tencent_pos.size() <= RECNET_CASH_SIZE) {
		 * for (int i = tencent_pos.size(); i > 0; i--) {
		 * ai.tencentRecentFriendsList.add(tencentList.get(i - 1)); } } else if
		 * (tencent_pos.size() > 5) { for (int i = tencent_pos.size(); i >
		 * (tencent_pos.size() - RECNET_CASH_SIZE - 1); i--) {
		 * ai.tencentRecentFriendsList.add(tencentList.get(i - 1)); } } }
		 * 
		 * // 保存renren最近联系人列表 if (ai.renrenRecentFriendsList != null) { if
		 * (renren_pos.size() > RECNET_CASH_SIZE) {
		 * ai.renrenRecentFriendsList.clear(); for (int i = renren_pos.size(); i
		 * > (renren_pos.size() - RECNET_CASH_SIZE - 1); i--) {
		 * ai.renrenRecentFriendsList
		 * .add(renrenList.get(Integer.parseInt(renren_pos .get(i - 1)))); } }
		 * else if (renren_pos.size() > 0 && renren_pos.size() <=
		 * RECNET_CASH_SIZE) { ArrayList<RWUser> renrenRecentFriendsListClone =
		 * new ArrayList<RWUser>();
		 * renrenRecentFriendsListClone.addAll(ai.renrenRecentFriendsList);
		 * ai.renrenRecentFriendsList.clear(); for (int i = renren_pos.size(); i
		 * > 0; i--) {
		 * ai.renrenRecentFriendsList.add(renrenList.get(Integer.parseInt
		 * (renren_pos .get(i - 1)))); } if (renrenRecentFriendsListClone.size()
		 * + renren_pos.size() <= RECNET_CASH_SIZE) { for (int i = 0; i <
		 * renren_pos.size(); i++) { ai.renrenRecentFriendsList
		 * .add(renrenRecentFriendsListClone.get(i - 1)); } } else { for (int i
		 * = 0; i < (RECNET_CASH_SIZE - renren_pos.size()); i--) {
		 * ai.renrenRecentFriendsList .add(renrenRecentFriendsListClone.get(i));
		 * } } } } else { ai.renrenRecentFriendsList = new ArrayList<RWUser>();
		 * if (renren_pos.size() > 0 && renren_pos.size() <= RECNET_CASH_SIZE) {
		 * for (int i = renren_pos.size(); i > 0; i--) {
		 * ai.renrenRecentFriendsList.add(renrenList.get(i - 1)); } } else if
		 * (renren_pos.size() > 5) { for (int i = renren_pos.size(); i >
		 * (renren_pos.size() - RECNET_CASH_SIZE - 1); i--) {
		 * ai.renrenRecentFriendsList.add(renrenList.get(i - 1)); } } }
		 */
	}

	private void createRenrenBindDialog() {
		Toast.makeText(AltFriendActivity.this, "还未绑定人人微博账号", Toast.LENGTH_SHORT)
				.show();
		Dialog dialog = new AlertDialog.Builder(this).setTitle("绑定账号")
				.setMessage("绑定人人微博账号？")
				.setPositiveButton("绑定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CmmobiSnsLib.getInstance(AltFriendActivity.this)
								.renrenAuthorize(renrenlistener);
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.show();
	}

	private void createSinaBindDialog() {
		Toast.makeText(AltFriendActivity.this, "还未绑定新浪微博账号", Toast.LENGTH_SHORT)
				.show();
		Dialog dialog = new AlertDialog.Builder(this).setTitle("绑定账号")
				.setMessage("绑定新浪微博账号？")
				.setPositiveButton("绑定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CmmobiSnsLib.getInstance(AltFriendActivity.this)
								.sinaAuthorize(sinalistener);
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.show();
	}

	private void createTencentBindDialog() {
		Toast.makeText(AltFriendActivity.this, "还未绑定腾讯微博账号", Toast.LENGTH_SHORT)
				.show();
		Dialog dialog = new AlertDialog.Builder(this).setTitle("绑定账号")
				.setMessage("绑定腾讯微博账号？")
				.setPositiveButton("绑定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CmmobiSnsLib.getInstance(AltFriendActivity.this)
								.tencentWeiboAuthorize(tencentlistener);
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.show();
	}

	// 判断首字母是否为特殊字符
	public String getAlpha(char str) {

		if (str == 0) {
			return "#";
		}
		String a = str + "";
		return a.toUpperCase();
	}

	/*
	 * 返回一个字的拼音
	 */
	public char toPinYin(String name) {
		char hanzi = name.charAt(0);
		char result = 0;

		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(hanzi + "").matches()) {
			return hanzi;
		}

		HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
		hanyuPinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		String[] pinyinArray = null;
		try {
			// 是否在汉字范围内
			if (hanzi >= 0x4e00 && hanzi <= 0x9fa5) {
				pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi,
						hanyuPinyin);
				result = pinyinArray[0].charAt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// public void lookadapter(User[] user) {
	// if (list != null)
	// list.clear();
	// for (int i = 0; i < user.length; i++) {
	// if (user[i].nickname == null || TextUtils.isEmpty(user[i].nickname)) {
	// user[i].nickname = "XX";
	// }
	// list.add(user[i]);
	// }
	//
	// Collections.sort(list, new WeiBoComparator());
	// data = new ArrayList<Object>();
	//
	// for (int n = 0; n < list.size(); n++) {
	// User mark = (User) list.get(n);
	//
	// if (getAlpha(toPinYin(mark.nickname)).equals("A")) {
	// if (a == 0) {
	// data.add("A");
	// }
	// data.add(mark);
	//
	// a++;
	// } else if (getAlpha(toPinYin(mark.nickname)).equals("B")) {
	// if (b == 0) {
	// data.add("B");
	// }
	//
	// data.add(mark);
	// b++;
	// } else if (getAlpha(toPinYin(mark.nickname)).equals("K")) {
	// if (k == 0) {
	// data.add("K");
	// }
	// if (sflag == 1) {
	// data.add("K");
	// }
	// data.add(mark);
	// k++;
	// } else if (getAlpha(toPinYin(mark.nickname)).equals("S")) {
	// if (s == 0) {
	// data.add("S");
	// }
	// data.add(mark);
	// s++;
	// } else if (getAlpha(toPinYin(mark.nickname)).equals("H")) {
	// if (h == 0) {
	// data.add("H");
	// }
	// data.add(mark);
	// h++;
	// } else if (getAlpha(toPinYin(mark.nickname)).equals("G")) {
	// if (g == 0) {
	// data.add("G");
	// }
	// data.add(mark);
	// g++;
	// } else if (getAlpha(toPinYin(mark.nickname)).equals("X")) {
	// if (x == 0) {
	// data.add("X");
	// }
	// data.add(mark);
	// x++;
	// } else if (getAlpha(toPinYin(mark.nickname)).equals("#")) {
	// if (alt == 0) {
	// data.add("#");
	// }
	// data.add(mark);
	// alt++;
	// }
	// }
	// }
	//
	// //
	// =-==================================================================================
	// /**
	// * listview的自定义适配器
	// *
	// * @author Administrator
	// *
	// */
	// private class Listadapter extends BaseAdapter implements SectionIndexer{
	//
	// private Context context;
	// private List<Object> list;
	// private LayoutInflater inflater;
	// private Map<Integer, Boolean> isSelected;
	//
	// public Listadapter(Context context, List<Object> list) {
	// this.context = context;
	// this.list = list;
	// inflater = LayoutInflater.from(context);
	// isSelected = new HashMap<Integer, Boolean>();
	// for (int i = 0; i < list.size(); i++) {
	// isSelected.put(i, false);
	// }
	//
	// }
	//
	// @Override
	// public int getCount() {
	// return list.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return list.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(final int position, View convertView,
	// ViewGroup parent) {
	//
	// View view = null;
	// ViewHold hold = new ViewHold();
	//
	// Object object = list.get(position);
	// if (object instanceof String) {
	// view = inflater
	// .inflate(R.layout.activity_altfriend_title, null);
	//
	// hold.alt_title = (TextView) view.findViewById(R.id.alt_title);
	//
	// hold.alt_title.setText((String) object);
	//
	// hold.alt_title.setVisibility(View.VISIBLE);
	// } else {
	// view = inflater.inflate(R.layout.activity_alt_friend_list_item,
	// null);
	// User user = (User) object;
	// hold.text02 = (TextView) view.findViewById(R.id.text02);
	// webimg = (WebImageView) view.findViewById(R.id.webimg);
	// hold.text02.setText(user.nickname);
	// webimg.setImageUrl(R.drawable.effect_fugu, ActiveAccount
	// .getInstance(context).getUID(), 1, user.portraiturl);
	// hold.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
	//
	// hold.checkbox
	// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(
	// CompoundButton buttonView, boolean isChecked) {
	// // TODO Auto-generated method stub
	// if (isChecked) {
	// isSelected.put(position, true);
	//
	// } else {
	// isSelected.put(position, false);
	// }
	// }
	// });
	// hold.checkbox.setChecked(isSelected.get(position));
	//
	// }
	//
	// return view;
	// }
	//
	// class ViewHold {
	// // TextView text_tencent;// 最近联系人
	// TextView text02;// 昵称
	// TextView alt_title;// 字母缩写
	// CheckBox checkbox;// 单选框
	// }
	//
	// /**
	// * 提取英文的首字母，非英文字母用#代替。
	// *
	// * @param str
	// * @return
	// */
	// private String getAlpha(String str) {
	// if (str == null) {
	// return "#";
	// }
	//
	// if (str.trim().length() == 0) {
	// return "#";
	// }
	//
	// char c = str.trim().substring(0, 1).charAt(0);
	// // 正则表达式，判断首字母是否是英文字母
	// Pattern pattern = Pattern.compile("^[A-Za-z]+$");
	// if (pattern.matcher(c + "").matches()) {
	// return (c + "").toUpperCase(); // 大写输出
	// } else {
	// return "#";
	// }
	// }
	//
	// @Override
	// public int getPositionForSection(int section) {
	// for (int i = 0; i < list.size(); i++) {
	// char key = getAlpha(((User)(list.get(i))).nickname).charAt(0);
	// if (key == section) {
	// return i;
	// }
	// }
	// return -1;
	// }
	//
	// @Override
	// public int getSectionForPosition(int position) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public Object[] getSections() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// }
}
