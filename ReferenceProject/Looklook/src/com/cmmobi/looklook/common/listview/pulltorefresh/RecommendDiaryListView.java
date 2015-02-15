/**
 * 
 */
package com.cmmobi.looklook.common.listview.pulltorefresh;

/**
 * 
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;


/**
 * @author wuxiang
 * @author zhangwei
 */
public class RecommendDiaryListView extends AbsRefreshView<MyDiary[]> {
	
	public RecommendDiaryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RecommendDiaryListView(Context context) {
		super(context);
	}

	protected RelativeLayout relativeLayout;
	@Override
	protected void initContent(MyDiary[] items) {
		if(null==relativeLayout)
			relativeLayout=new RelativeLayout(context);
		relativeLayout.removeAllViews();
		clear();
		lastChildIdOdd=0;
		lastChildIdEven=0;
		viewId=1001;
		addView(items);
		addChild(relativeLayout,1);
	}

	int lastChildIdOdd=0;
	int lastChildIdEven=0;
	int viewId=1001;
	protected TextView[] tags=new TextView[3] ;
	@Override
	protected void addView(MyDiary[] items) {
		/*for(int i=0;i<items.length;i++){
			View v=inflater.inflate(R.layout.activity_homepage_main_list_item, null);
			v.setId(++viewId);
			WebImageView imVedio=(WebImageView) v.findViewById(R.id.iv_pubu_vedio);
			TextView tvPlayCount= (TextView) v.findViewById(R.id.tv_play_count);
			TextView tvCommentCount= (TextView) v.findViewById(R.id.tv_comment_count);
			TextView tvIntroduce= (TextView) v.findViewById(R.id.tv_introduce);
			TextView tvDate= (TextView) v.findViewById(R.id.tv_date);
			tags[0]= (TextView) v.findViewById(R.id.tv_tag1);
			tags[1]= (TextView) v.findViewById(R.id.tv_tag2);
			tags[2]= (TextView) v.findViewById(R.id.tv_tag3);
			WebImageView imPortrait=(WebImageView) v.findViewById(R.id.iv_portrait);
			TextView tvName= (TextView) v.findViewById(R.id.tv_name);
			imVedio.setImageUrl(R.drawable.pubu_tu, 1, items[i].videoimage);
			tvPlayCount.setText(items[i].playtimes);
			tvCommentCount.setText(items[i].commentcount);
			tvIntroduce.setText(items[i].videocontent);
			tvDate.setText(items[i].createtime);
			imPortrait.setImageUrl(R.drawable.touxiang, 1, items[i].portraiturl);
			tvName.setText(items[i].nickname);
			int len=items[i].tags.length>3?3:items[i].tags.length;
			for(int j=0;j<len;j++){
				tags[j].setText(items[i].tags[j].name);
				tags[j].setVisibility(View.VISIBLE);
			}
			measureView(v);
			int width=v.getMeasuredWidth();
			int marginLeft=0;
			if(width*2>dm.widthPixels){
				width=dm.widthPixels/2-6;
				marginLeft=2;
			}else{
				marginLeft=(dm.widthPixels-width*2)/3;
			}
			android.widget.RelativeLayout.LayoutParams params=new android.widget.RelativeLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
			int size_5=context.getResources().getDimensionPixelSize(R.dimen.size_5dip);
			params.setMargins(marginLeft, size_5, 0, 0);
			if(viewId%2==0){
				if(0==lastChildIdOdd){
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				}else{
					params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
					params.addRule(RelativeLayout.BELOW, lastChildIdOdd);
				}
			}else{
				if(0==lastChildIdEven){
					params.addRule(RelativeLayout.RIGHT_OF,lastChildIdOdd);
					params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				}else{
					params.addRule(RelativeLayout.RIGHT_OF, lastChildIdOdd);
					params.addRule(RelativeLayout.BELOW, lastChildIdEven);
				}
			}
			
			if(viewId%2==0){
				lastChildIdOdd=v.getId();
			}else{
				lastChildIdEven=v.getId();
			}
			relativeLayout.addView(v, params);
		}
		*/
	}

}

