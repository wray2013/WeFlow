/**
 * 
 */
package com.cmmobi.looklook.common.listview.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.videorecommendItem;
import com.cmmobi.looklook.common.web.WebImageView;


/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-4-23
 */
public class RecommendVedioListView extends
		AbsRefreshView<videorecommendItem[]> implements OnClickListener {

	public RecommendVedioListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecommendVedioListView(Context context) {
		super(context);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.iv_pubu_vedio:
//			String url = v.getTag().toString();
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			Uri uri = Uri.parse(url);
//			String type = "video/*";
//			intent.setDataAndType(uri, type);
//			context.startActivity(intent);
//			break;
		case R.id.iv_portrait:
			Toast.makeText(context, "portrait", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

	}

	protected RelativeLayout relativeLayout;

	@Override
	protected void initContent(videorecommendItem[] items) {
		if (null == relativeLayout){
			relativeLayout = new RelativeLayout(context);
		}
		relativeLayout.removeAllViews();
		clear();
		viewId = 999;
		lId = 0;
		rId = 0;
		isLeft = true;
		lHeight = 0;
		addView(items);
		addChild(relativeLayout, 1);
	}

	int lId = 0;
	int rId = 0;
	boolean isLeft = true;
	int lHeight = 0;
	int viewId = 999;
	protected TextView[] tags = new TextView[3];

	@Override
	protected void addView(videorecommendItem[] items) {
		for (int i = 0; i < items.length; i++) {
			View v = inflater.inflate(
					R.layout.activity_homepage_main_list_item, null);
			v.setId(++viewId);
//			WebImageView imVedio = (WebImageView) v
//					.findViewById(R.id.iv_pubu_vedio);
//			TextView tvPlayCount = (TextView) v
//					.findViewById(R.id.tv_play_count);
//			TextView tvCommentCount = (TextView) v
//					.findViewById(R.id.tv_comment_count);
//			TextView tvIntroduce = (TextView) v.findViewById(R.id.tv_introduce);
			TextView tvDate = (TextView) v.findViewById(R.id.tv_date);
//			tags[0] = (TextView) v.findViewById(R.id.tv_tag1);
//			tags[1] = (TextView) v.findViewById(R.id.tv_tag2);
//			tags[2] = (TextView) v.findViewById(R.id.tv_tag3);
			WebImageView imPortrait = (WebImageView) v
					.findViewById(R.id.iv_portrait);
			TextView tvName = (TextView) v.findViewById(R.id.tv_name);
//			imVedio.setTag(items[i].videosharepath);
//			imVedio.setImageUrl(R.drawable.pubu_tu, 1,  items[i].videoimage);
//			imVedio.setOnClickListener(this);
//			tvPlayCount.setText(items[i].playtimes);
//			tvCommentCount.setText(items[i].commentcount);
//			tvIntroduce.setText(items[i].videocontent);
			tvDate.setText(items[i].createtime);
			imPortrait.setImageUrl(R.drawable.touxiang, 1, items[i].portraiturl, false);
			imPortrait.setOnClickListener(this);
			tvName.setText(items[i].nickname);
			int len = items[i].tags.length > 3 ? 3 : items[i].tags.length;
			for (int j = 0; j < len; j++) {
				tags[j].setText(items[i].tags[j].name);
				tags[j].setVisibility(View.VISIBLE);
			}
			relativeLayout.addView(v, getParams(v));
		}
	}

	private android.widget.RelativeLayout.LayoutParams getParams(View v) {
		measureView(v);
		int width = v.getMeasuredWidth();
		int height = v.getMeasuredHeight();
		int size_5 = context.getResources().getDimensionPixelSize(
				R.dimen.size_5dip);
		int marginLeft = size_5;
		if (width * 2 > dm.widthPixels) {
			width = dm.widthPixels / 2 - 6;
			marginLeft = 2;
		} else {
			width = (dm.widthPixels - marginLeft * 3) / 2;
			marginLeft = (dm.widthPixels - width * 2) / 3;
		}
		android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
				width, LayoutParams.WRAP_CONTENT);
		params.setMargins(marginLeft, size_5, 0, 0);

		if (1000 == viewId || 1001 == viewId) {
			if (1000 == viewId) {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP,
						RelativeLayout.TRUE);
				lHeight = height;
				lId = v.getId();
			} else {
				params.addRule(RelativeLayout.RIGHT_OF, lId);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP,
						RelativeLayout.TRUE);
				if (height > lHeight) {
					isLeft = true;
					lHeight = height - lHeight;
				} else {
					lHeight = lHeight - height;
					isLeft = false;
				}
				rId = v.getId();
			}
		} else {
			if (isLeft) {
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				params.addRule(RelativeLayout.BELOW, lId);
				if (height > lHeight) {
					lHeight = height - lHeight;
					isLeft = false;
				} else {
					lHeight = lHeight - height;
					isLeft = true;
				}
				lId = v.getId();
			} else {
				params.addRule(RelativeLayout.RIGHT_OF, lId);
				params.addRule(RelativeLayout.BELOW, rId);
				if (height > lHeight) {
					lHeight = height - lHeight;
					isLeft = true;
				} else {
					lHeight = lHeight - height;
					isLeft = false;
				}
				rId = v.getId();
			}
		}
		return params;
	}
}
