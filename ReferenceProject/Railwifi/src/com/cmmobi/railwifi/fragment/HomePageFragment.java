package com.cmmobi.railwifi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.CmmobiVideoPlayer;
import com.cmmobi.railwifi.activity.JokeActivity;
import com.cmmobi.railwifi.activity.JokeDetailActivity;
import com.cmmobi.railwifi.activity.MainActivity;
import com.cmmobi.railwifi.activity.ModuleDragActivity;
import com.cmmobi.railwifi.activity.MovieDetailActivity;
import com.cmmobi.railwifi.activity.MoviesActivity;
import com.cmmobi.railwifi.activity.MusicDetailActivity;
import com.cmmobi.railwifi.activity.MusicMainPageActivity;
import com.cmmobi.railwifi.activity.NewsDetailActivity;
import com.cmmobi.railwifi.activity.OrderShoppingActivity;
import com.cmmobi.railwifi.activity.RailNewsActivity;
import com.cmmobi.railwifi.activity.RailTravelDetailAcitivity;
import com.cmmobi.railwifi.activity.VideoPlayerActivity;
import com.cmmobi.railwifi.event.FragmentEvent;
import com.cmmobi.railwifi.event.NetworkEvent;
import com.cmmobi.railwifi.music.MusicService;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.DiscoverElem;
import com.cmmobi.railwifi.network.GsonResponseObject.MusicElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ModuleUtils;
import com.cmmobi.railwifi.utils.ModuleUtils.TagWrapper;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.etsy.android.grid.StaggeredGridView;
import com.etsy.android.grid.StaggeredGridView.OnMeasureFinishListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnTouchUpFinishListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.api.CustomRoundedBitmapDisplayer;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import de.greenrobot.event.EventBus;

public class HomePageFragment extends TitleRootFragment {

	public static final String HTTP_HOST_PREFIX = "http://test1.mishare.cn:8080/1.1/images";
	private StaggeredGridView mGridView;
	private GridView tagGridView;
	private RelativeLayout rlDiscoverTitle;
	private RelativeLayout rlDiscoverBottom;
	private HomePageAdapter adapter;
	private TagAdapter tagAdapter;
	private ArrayList<TagWrapper> tagLists = new ArrayList<TagWrapper>();
	private ArrayList<DiscoverElem> discoverList = new ArrayList<DiscoverElem>();
	private final int REQUEST_ADD_MODULE = 0x6f8e;
	private PullToRefreshScrollView ptrScrollView = null;
	private boolean isCollapse = false;
	private final int STATE_SCROLL_INIT = 0;
	private final int STATE_SCROLL_UP = 1;
	private final int STATE_SCROLL_DOWN = 2;
	private int state = STATE_SCROLL_INIT;
	private DisplayMetrics dm = new DisplayMetrics();
	private int itemWidth = 0;
	private int statusHeight = 0;
	private View mView = null;
	
	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_homepage;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester.RESPONSE_TYPE_MUSIC_DETAILS:
//			Intent musicDetailIntent = new Intent(getActivity(),MusicDetailActivity.class);
//			musicDetailIntent.putExtra(ConStant.INTENT_MEDIA_ID, elem.object_id);
//			startActivity(musicDetailIntent);
			GsonResponseObject.musicDetailResp r45 = (GsonResponseObject.musicDetailResp) msg.obj;
			if(r45!=null && "0".equals(r45.status)){
				
				List<MusicElem> pathArray = new ArrayList<GsonResponseObject.MusicElem>();
				MusicElem me = new MusicElem();
				me.content = r45.content;
				me.img_path = r45.img_path;
				me.media_id = r45.media_id;
				me.name = r45.name;
				me.src_path = r45.src_path;
				me.tag = r45.tag;
				me.source_id = r45.source_id;
				me.source_logo = r45.source_logo;
				pathArray.add(me);
				
				Activity activity = getActivity();
				if (activity != null) {
					MusicService.getInstance().setPlayArray(pathArray);
					Intent intent = new Intent(activity, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, me.media_id);
					intent.putExtra(MusicDetailActivity.KEY_FROM_WHERE, MusicDetailActivity.VALUE_FROM_HOMEPAGE);
					activity.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			}
			break;
		case Requester.RESPONSE_TYPE_DISCOVER:
			Log.d("=AAA=","RESPONSE_TYPE_DISCOVER in");
			ptrScrollView.onRefreshComplete(); 
			if (msg.obj != null) {
				GsonResponseObject.DiscoverResp resp = (GsonResponseObject.DiscoverResp) msg.obj;
				if ("0".equals(resp.status)) {
					DiscoverElem[] list = resp.list;
					if (list != null && list.length > 0) {
						boolean isAddNew = false;
						int addHeight = getHeighestHeight(list);
						for (int i = 0;i < list.length;i++) {
							DiscoverElem elem = list[i];
							if (!discoverList.contains(elem)) {
								discoverList.add(elem);
								isAddNew = true;
							}
						}
						if (isAddNew) {
							Log.d("=AAA=","RESPONSE_TYPE_DISCOVER height = " + (mGridView.getMeasuredHeight() + addHeight) + " addHeight = " + addHeight);
							ViewUtils.setHeightPixel(mGridView, mGridView.getMeasuredHeight() + addHeight);
							adapter.notifyDataSetChanged();
						}
					}
				}
			} else {
				
			}
			break;
		}
		return false;
	}
	
	private int getHeighestHeight(DiscoverElem[] list) {
//		Log.d("=AAA=","getHeighestHeight in this = " + this + " activity = " + getActivity());
		
		int maxHeight = 0;
		int count = 0;
		for (int i = 0;i < list.length;i++) {
			DiscoverElem elem = list[i];
			if (!discoverList.contains(elem)) {
				count++;
				int height = 0;
				if (elem.width != null && elem.height != null) {
					int width = Integer.parseInt(elem.width);
					try {
						height = Integer.parseInt(elem.height);
						height = (itemWidth * height / width);
					} catch(Exception e) {
						e.printStackTrace();
						height = itemWidth;
					}
					if (height > maxHeight) {
						maxHeight = height;
					}
				} else {
					height = itemWidth;
					if (height > maxHeight) {
						maxHeight = height;
					}
				}
			}
		}
		maxHeight += DisplayUtil.getSize(getActivity(), 145);
		
		int maxLine = (int) Math.ceil(count / 2.0);
		return (int) (maxHeight * maxLine) + (int)(dm.density * maxLine);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		EventBus.getDefault().register(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("=AAA=","HomePageFragment onDestroy");
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(NetworkEvent event) {
		SharedPreferences mySharedPreferences= getActivity().getSharedPreferences("selected_modules", 
				Activity.MODE_PRIVATE); 
		String selectedModulesStr = mySharedPreferences.getString("modules", null);
		
		ArrayList<Integer> idLists = null;
		int gridHeight = 0;
		switch(event) {
		case NET_RAILWIFI:
			Log.d("=AAA=","NET_RAILWIFI ");
			if (selectedModulesStr != null) {
				idLists = new Gson().fromJson(selectedModulesStr, new TypeToken<ArrayList<Integer>>(){}.getType());
			}
			
			tagLists.clear();
			if (idLists != null) {
				for (Integer id:idLists) {
					tagLists.add(ModuleUtils.findModules(id));
				}
			} else {
				for (TagWrapper wrapper:ModuleUtils.tagLists) {
					if (wrapper.isDefault) {
						tagLists.add(wrapper);
					}
				}
			}
			
			gridHeight = getGridViewHeight(tagGridView);
			ViewUtils.setHeightPixel(tagGridView, gridHeight);
			tagAdapter.notifyDataSetChanged();
			
			if (gridHeight + DisplayUtil.getSize(getActivity(), 12) + DisplayUtil.getSize(getActivity(), 96) + statusHeight > dm.heightPixels) {
				rlDiscoverBottom.setVisibility(View.VISIBLE);
			} else {
				rlDiscoverBottom.setVisibility(View.GONE);
			}
			break;
		case NET_OTHERS:
			Log.d("=AAA=","NET_OTHERS ");
			if (selectedModulesStr != null) {
				idLists = new Gson().fromJson(selectedModulesStr, new TypeToken<ArrayList<Integer>>(){}.getType());
			}
			
			tagLists.clear();
			if (idLists != null) {
				for (Integer id:idLists) {
					TagWrapper tag = ModuleUtils.findModules(id);
					if (!tag.isRailService) {
						tagLists.add(ModuleUtils.findModules(id));
					}
				}
			} else {
				for (TagWrapper wrapper:ModuleUtils.tagLists) {
					if (!wrapper.isRailService && wrapper.isDefault) {
						tagLists.add(wrapper);
					}
				}
			}
			
			gridHeight = getGridViewHeight(tagGridView);
			ViewUtils.setHeightPixel(tagGridView, gridHeight);
			tagAdapter.notifyDataSetChanged();
			
			if (gridHeight + DisplayUtil.getSize(getActivity(), 12) + DisplayUtil.getSize(getActivity(), 96) + statusHeight > dm.heightPixels) {
				rlDiscoverBottom.setVisibility(View.VISIBLE);
			} else {
				rlDiscoverBottom.setVisibility(View.GONE);
			}
			break;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		View view = super.onCreateView(inflater, container, savedInstanceState);
		mView = view;
		initViews(view);
		return view;
	}
	
	private int getGridViewHeight(GridView gridView) {
		int count = gridView.getAdapter().getCount();
		int rowNum = (int)Math.ceil(count / (double)3);
		int height = DisplayUtil.getSize(getActivity(), 162) * rowNum  + cn.trinea.android.common.util.ViewUtils.getGridViewVerticalSpacing(gridView) * (rowNum + 1);
		return height;
	}
	
	private void initViews(View view) {
		dm = getResources().getDisplayMetrics();
		
		itemWidth = (int) ((dm.widthPixels - 6 * dm.density * 3) / 2);
		setLeftButtonBackground(R.drawable.btn_navigation);
		setRightButtonBackground(R.drawable.btn_collapse);
		
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
		
		ptrScrollView.getRefreshableView().setOnTouchListener(new OnTouchListener() {
			
			private int lastY = 0;  
	        private int touchEventId = -9983761;  
	        Handler stopHandler = new Handler() {  
	            @Override  
	            public void handleMessage(Message msg) {  
	            	super.handleMessage(msg);  
	            	View scroller = ptrScrollView.getRefreshableView();  
	            	if(msg.what==touchEventId) {  
	            		if(lastY ==scroller.getScrollY()) {  
	            			handleStop(scroller);  
	            		}else {  
	            			stopHandler.sendEmptyMessageDelayed(touchEventId,10);  
	            			lastY = scroller.getScrollY();
	            			handleStop(scroller);
	            		} 
	            		
	            	}  
	            }  
	        };
	        
	        private void handleStop(View v) {
	        	if (!isCollapse) {
	            	int scrollY=v.getScrollY();
	            	int bottom = rlDiscoverTitle.getHeight() + tagGridView.getHeight() + DisplayUtil.getSize(getActivity(), 12);
//		            	Log.d("=AAA=","ptrScrollView onTouch ACTION_MOVE scrollY = " + scrollY + " bottom = " + bottom + " discoverHeight = " + rlDiscoverTitle.getHeight() + " gridHeight = " + tagGridView.getHeight());
	            	if (scrollY > bottom) {
	            		if (state != STATE_SCROLL_UP) {
		            		setTitleText("箩筐发现");
		            		setRightButtonBackground(R.drawable.btn_expansion);
		            		state = STATE_SCROLL_UP;
	            		}
	            	} else {
	            		if (state != STATE_SCROLL_DOWN) {
	            			setTitleText("");
		            		setRightButtonBackground(R.drawable.btn_collapse);
		            		state = STATE_SCROLL_DOWN;
	            		}
	            	}
	            	
	            	if (dm.heightPixels - DisplayUtil.getSize(getActivity(), 96) - statusHeight + scrollY >= bottom) {
	            		rlDiscoverBottom.setVisibility(View.GONE);
	            	} else {
	            		rlDiscoverBottom.setVisibility(View.VISIBLE);
	            	}
            	}
	        }
	           
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
		        switch (action) {
		            case MotionEvent.ACTION_CANCEL:
		            case MotionEvent.ACTION_UP:
		            	if (mGridView.getPressedView() != null) {
		            		mGridView.getPressedView().setPressed(false);
		            		mGridView.getPressedView().invalidate();
		            	}
		            	stopHandler.sendEmptyMessageDelayed(touchEventId,10);
		            	break;
		            case MotionEvent.ACTION_MOVE:
		            	if (!isCollapse) {
			            	int scrollY=v.getScrollY();
			            	int bottom = rlDiscoverTitle.getHeight() + tagGridView.getHeight() + DisplayUtil.getSize(getActivity(), 12);
	//		            	Log.d("=AAA=","ptrScrollView onTouch ACTION_MOVE scrollY = " + scrollY + " bottom = " + bottom + " discoverHeight = " + rlDiscoverTitle.getHeight() + " gridHeight = " + tagGridView.getHeight());
			            	if (scrollY > bottom) {
			            		if (state != STATE_SCROLL_UP) {
				            		setTitleText("箩筐发现");
				            		setRightButtonBackground(R.drawable.btn_expansion);
				            		state = STATE_SCROLL_UP;
			            		}
			            	} else {
			            		if (state != STATE_SCROLL_DOWN) {
			            			setTitleText("");
				            		setRightButtonBackground(R.drawable.btn_collapse);
				            		state = STATE_SCROLL_DOWN;
			            		}
			            	}
			            	
			            	if (dm.heightPixels - DisplayUtil.getSize(getActivity(), 96) - statusHeight + scrollY >= bottom) {
			            		rlDiscoverBottom.setVisibility(View.GONE);
			            	} else {
			            		rlDiscoverBottom.setVisibility(View.VISIBLE);
			            	}
			            	
		            	}
		            	break;
		        }
				return false;
			}
		});
		
		
		ptrScrollView.setOnTouchUpFinishListener(new OnTouchUpFinishListener() {

			@Override
			public void onTouchUpFinish() {
				// TODO Auto-generated method stub
				if (mGridView.getPressedView() != null) {
            		mGridView.getPressedView().setPressed(false);
            		mGridView.getPressedView().invalidate();
            	}
			}
		});
		
		ptrScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {  
            
            @Override  
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {  
                //执行刷新函数  
            	Requester.requestDiscover(handler);
            }  
        });  
		
		mGridView = (StaggeredGridView) view.findViewById(R.id.grid_view);
		tagGridView = (GridView) view.findViewById(R.id.gv_tags);
		
		
		SharedPreferences mySharedPreferences= getActivity().getSharedPreferences("selected_modules", 
				Activity.MODE_PRIVATE); 
		String selectedModulesStr = mySharedPreferences.getString("modules", null);
		
		ArrayList<Integer> idLists = null;
		if (selectedModulesStr != null) {
			idLists = new Gson().fromJson(selectedModulesStr, new TypeToken<ArrayList<Integer>>(){}.getType());
		}
		
		tagLists.clear();
		if (StringUtils.isEmpty(MainActivity.train_num)) {
			if (idLists != null) {
				for (Integer id:idLists) {
					TagWrapper tag = ModuleUtils.findModules(id);
					if (!tag.isRailService) {
						tagLists.add(ModuleUtils.findModules(id));
					}
				}
			} else {
				for (TagWrapper wrapper:ModuleUtils.tagLists) {
					if (!wrapper.isRailService && wrapper.isDefault) {
						tagLists.add(wrapper);
					}
				}
			}
		} else {
			if (idLists != null) {
				for (Integer id:idLists) {
					tagLists.add(ModuleUtils.findModules(id));
				}
			} else {
				for (TagWrapper wrapper:ModuleUtils.tagLists) {
					if (wrapper.isDefault) {
						tagLists.add(wrapper);
					}
				}
			}
		}
		tagAdapter = new TagAdapter(getActivity(), tagLists);
		tagGridView.setAdapter(tagAdapter);
		tagGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		rlDiscoverBottom = (RelativeLayout) view.findViewById(R.id.rl_discovery_title_bottom);
		int gridHeight = getGridViewHeight(tagGridView);
		ViewUtils.setHeightPixel(tagGridView, gridHeight);
		statusHeight = DisplayUtil.getStatusHeight(getActivity());
		if (gridHeight + DisplayUtil.getSize(getActivity(), 12) + DisplayUtil.getSize(getActivity(), 96) + statusHeight > dm.heightPixels) {
			rlDiscoverBottom.setVisibility(View.VISIBLE);
		}
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("=AAA=","onItemClick position = " + arg2);
				DiscoverElem elem = discoverList.get(arg2);
				if ("1".equals(elem.type)) {// 列车资讯
					Intent newsDetailIntent = new Intent(getActivity(),NewsDetailActivity.class);
					newsDetailIntent.putExtra("news_id", elem.object_id);
					startActivity(newsDetailIntent);
				} else if ("2".equals(elem.type)) {// 看电影
					Intent movieDetailIntent = new Intent(getActivity(),MovieDetailActivity.class);
					movieDetailIntent.putExtra(ConStant.INTENT_MEDIA_ID, elem.object_id);
					startActivity(movieDetailIntent);
				} else if ("3".equals(elem.type)) {// 听音乐
					Requester.requestMusicDetail(handler, elem.object_id);

				} else if ("5".equals(elem.type)) {// 逗你玩
					Intent jokDetailIntent = new Intent(getActivity(),JokeDetailActivity.class);
					jokDetailIntent.putExtra(ConStant.INTENT_MEDIA_ID, elem.object_id);
					startActivity(jokDetailIntent);
				} else if ("6".equals(elem.type)) {// 旅游
					Intent railTravelDetailIntent = new Intent(getActivity(),RailTravelDetailAcitivity.class);
					railTravelDetailIntent.putExtra(ConStant.INTENT_LINE_ID, elem.object_id);
					startActivity(railTravelDetailIntent);
				} else if ("8".equals(elem.type)) {// 城市风采
					if(Config.IS_USE_COMMOBI_VIDEOVIEW){
						Intent movieIntent = new Intent(getActivity(), CmmobiVideoPlayer.class);
						movieIntent.putExtra(VideoPlayerActivity.KEY_MEDIA_ID, elem.object_id);
						movieIntent.putExtra(VideoPlayerActivity.KEY_NAME, elem.content);
						movieIntent.putExtra(VideoPlayerActivity.KEY_PATH, elem.src_path);
						movieIntent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "2");
						startActivity(movieIntent);
					}else{
						Intent movieIntent = new Intent(getActivity(), VideoPlayerActivity.class);
						movieIntent.putExtra(VideoPlayerActivity.KEY_MEDIA_ID, elem.object_id);
						movieIntent.putExtra(VideoPlayerActivity.KEY_NAME, elem.content);
						movieIntent.putExtra(VideoPlayerActivity.KEY_PATH, elem.src_path);
						movieIntent.putExtra(VideoPlayerActivity.KEY_MOVIE_TYPE, "2");
						startActivity(movieIntent);
					}
				}
			}
		});
		
		discoverList.clear();
		adapter = new HomePageAdapter(getActivity(), discoverList);
		mGridView.setAdapter(adapter);
		
		mGridView.setOnMeasureFinishListener(new OnMeasureFinishListener() {
			
			@Override
			public void onMeasureFinish() {
				// TODO Auto-generated method stub
				
				int height = mGridView.getLowestChildBottom();
				Log.d("=AAA=", "onMeasurefinish in height = " + height);
				if (height > 0) {
					ViewUtils.setHeightPixel(mGridView, height);
					mGridView.requestLayout();
				}
			}
		});
		
		rlDiscoverTitle = (RelativeLayout) view.findViewById(R.id.rl_discovery_title);
		ViewUtils.setHeight(rlDiscoverTitle, 60);
		ViewUtils.setMarginTop(rlDiscoverTitle, 12);
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Requester.requestDiscover(handler);
			}
		}, 250);
	}
	
	public void requestDiscover() {
		if (discoverList.size() == 0) {
			Requester.requestDiscover(handler);
		}
	}
	
	
	class HomePageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private Context context;
		private List<DiscoverElem> contentList;
		
		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		public HomePageAdapter(Context context,List<DiscoverElem> list) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			
			contentList = list;
			
			imageLoader = MyImageLoader.getInstance();

			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				    .bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new CustomRoundedBitmapDisplayer(10,10,0,0))
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
					.showImageForEmptyUri(R.drawable.homepage_default)
					.showImageOnFail(R.drawable.homepage_default)
					.showImageOnLoading(R.drawable.homepage_default)
					.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contentList.size();
		}

		@Override
		public DiscoverElem getItem(int position) {
			// TODO Auto-generated method stub
			return contentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ContentHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_homepage, null);
				
				holder = new ContentHolder();
				holder.ivContentImg = (ImageView)convertView.findViewById(R.id.iv_content_img);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content_desc);
				holder.tvContentType = (TextView) convertView.findViewById(R.id.tv_content_class);
				
				holder.tvContent.setTextSize(DisplayUtil.textGetSizeSp(context, 30));
				holder.tvContentType.setTextSize(DisplayUtil.textGetSizeSp(context, 27));
				ViewUtils.setHeight(holder.tvContentType, 42);
				ViewUtils.setMarginLeft(holder.tvContent, 6);
				ViewUtils.setMarginTop(holder.tvContent, 12);
				ViewUtils.setMarginTop(holder.tvContentType, 12);
				holder.tvContentType.setPadding(DisplayUtil.getSize(context, 21), 0, DisplayUtil.getSize(context, 21), 0);
				convertView.findViewById(R.id.rl_item_homepage_root).setPadding(0, 0, 0, 12);
				convertView.setTag(holder);
//				convertView.findViewById(R.id.rl_item_homepage_root).setClickable(true);
			} else {
				holder = (ContentHolder)convertView.getTag();
			}
			final DiscoverElem elem = contentList.get(position);
			
			int height = 0;
			if (elem.width != null && elem.height != null) {
				try {
					int width = Integer.parseInt(elem.width);
					height = Integer.parseInt(elem.height);
					height = (itemWidth * height / width);
				} catch (Exception e) {
					e.printStackTrace();
					height = itemWidth;
				}
				
			} else {
				height = itemWidth;
			}
			
			ViewUtils.setHeightPixel(holder.ivContentImg, height);
			final ImageView ivImg = holder.ivContentImg;
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ivImg.requestLayout();
				}
			}, 50);
			
			imageLoader.displayImage(elem.img_path, holder.ivContentImg, imageLoaderOptions);
			
			holder.tvContent.setText(elem.content);
			
			Log.d("=AAA=","position = " + position + " height = " + height);
			if ("1".equals(elem.type)) {
				holder.tvContentType.setText("铁路资讯");
				holder.tvContentType.setBackgroundResource(R.drawable.bg_homepage_item_news);
			} else if ("2".equals(elem.type)) {
				holder.tvContentType.setText("看电影");
				holder.tvContentType.setBackgroundResource(R.drawable.bg_homepage_item_movie);
			} else if ("3".equals(elem.type)) {
				holder.tvContentType.setText("听音乐");
				holder.tvContentType.setBackgroundResource(R.drawable.bg_homepage_item_music);
			} else if ("5".equals(elem.type)) {
				holder.tvContentType.setText("逗你玩");
				holder.tvContentType.setBackgroundResource(R.drawable.bg_homepage_item_jok);
			} else if ("6".equals(elem.type)) {
				holder.tvContentType.setText("铁旅联盟");
				holder.tvContentType.setBackgroundResource(R.drawable.bg_homepage_item_alliance);
			} else if ("8".equals(elem.type)) {
				holder.tvContentType.setText("城市风采");
				holder.tvContentType.setBackgroundResource(R.drawable.bg_homepage_item_city_intro);
			}
			
			holder.tvContentType.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if ("1".equals(elem.type)) {
						Intent newsIntent = new Intent(getActivity(),RailNewsActivity.class);
						startActivity(newsIntent);
					} else if ("2".equals(elem.type)) {
						Intent movieIntent = new Intent(getActivity(),MoviesActivity.class);
						startActivity(movieIntent);
					} else if ("3".equals(elem.type)) {
						Intent musicIntent = new Intent(getActivity(),MusicMainPageActivity.class);
						startActivity(musicIntent);
					} else if ("5".equals(elem.type)) {
						Intent jokIntent = new Intent(getActivity(),JokeActivity.class);
						startActivity(jokIntent);
					} else if ("6".equals(elem.type)) {
						EventBus.getDefault().post(FragmentEvent.RAILTRAVEL);
					} else if ("8".equals(elem.type)) {
						EventBus.getDefault().post(FragmentEvent.CITYINTRODUCTION);
					}
				}
			});
			
			return convertView;
		}
	}
	
	class ContentHolder {
		public ImageView ivContentImg;
		public TextView tvContent;
		public TextView tvContentType;
	}
	
	class TagHolder {
		public ImageView ivTagImg;
		public TextView tvTagText;
		public RelativeLayout rlRoot;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_left:
			getSlidingMenu().toggle();
			break;
		case R.id.btn_title_right:
			if (!isCollapse && state == STATE_SCROLL_UP) {
				ptrScrollView.getRefreshableView().scrollTo(0, 0);
				state = STATE_SCROLL_INIT;
				setTitleText("");
				setRightButtonBackground(R.drawable.btn_collapse);
				break;
			}
			isCollapse = !isCollapse;
			if (isCollapse) {
				rlDiscoverTitle.setVisibility(View.GONE);
				tagGridView.setVisibility(View.GONE);
				setTitleText("箩筐发现");
				setRightButtonBackground(R.drawable.btn_expansion);
			} else {
				rlDiscoverTitle.setVisibility(View.VISIBLE);
				tagGridView.setVisibility(View.VISIBLE);
				setTitleText("");
				setRightButtonBackground(R.drawable.btn_collapse);
			}
			break;
		case R.id.rl_tag_root:
			TagWrapper wrapper = (TagWrapper)v.getTag();
			switch(wrapper.tagId) {
			case ModuleUtils.MODULEADD://模块添加
				CmmobiClickAgentWrapper.onEvent(getActivity(), "nav", "7");
				Intent addModule = new Intent(getActivity(),ModuleDragActivity.class);
				addModule.putIntegerArrayListExtra("modules", getSelectedModule());
				startActivityForResult(addModule, REQUEST_ADD_MODULE);
				break;
			case ModuleUtils.MODULESHOPPING://订餐购物
				CmmobiClickAgentWrapper.onEvent(getActivity(), "nav", "1");
				Intent shoppingIntent = new Intent(getActivity(),OrderShoppingActivity.class);
				startActivity(shoppingIntent);
				break;
			case ModuleUtils.MODULETRAINSERVER://铁路资讯
				CmmobiClickAgentWrapper.onEvent(getActivity(), "nav", "2");
				Intent newsIntent = new Intent(getActivity(),RailNewsActivity.class);
				startActivity(newsIntent);
				break;
			case ModuleUtils.MODULEALLIIANCE://铁旅联盟
				CmmobiClickAgentWrapper.onEvent(getActivity(), "nav", "3");
				EventBus.getDefault().post(FragmentEvent.RAILTRAVEL);
				break;
			case ModuleUtils.MODULEMOVIE://看电影
				CmmobiClickAgentWrapper.onEvent(getActivity(), "nav", "4");
				Intent movieIntent = new Intent(getActivity(),MoviesActivity.class);
				startActivity(movieIntent);
				break;
			case ModuleUtils.MODULEFUNNY://逗你玩
				CmmobiClickAgentWrapper.onEvent(getActivity(), "nav", "5");
				Intent jokIntent = new Intent(getActivity(),JokeActivity.class);
				startActivity(jokIntent);
				break;
			case ModuleUtils.MODULEMUSIC:// 听音乐
				CmmobiClickAgentWrapper.onEvent(getActivity(), "nav", "6");
				Intent musicIntent = new Intent(getActivity(),MusicMainPageActivity.class);
				startActivity(musicIntent);
				break;
			case ModuleUtils.MODULECITYINTRO:
				break;
				
			}
			break;
		}
	}
	
	private ArrayList<Integer> getSelectedModule() {
		ArrayList<Integer> moduleList = new ArrayList<Integer>();
		for (TagWrapper addWrapper:tagLists) {
			moduleList.add(addWrapper.tagId);
		}
		return moduleList;
	}
	
	class TagAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private Context context;
		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		private ArrayList<TagWrapper> tagLists = new ArrayList<TagWrapper>();
		
		public TagAdapter(Context context,ArrayList<TagWrapper> list) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			
			imageLoader = MyImageLoader.getInstance();

			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
			    	.bitmapConfig(Bitmap.Config.RGB_565)
					.showImageForEmptyUri(R.drawable.pic_index_default)
					.showImageOnFail(R.drawable.pic_index_default)
					.showImageOnLoading(R.drawable.pic_index_default)
					.build();
			if (list != null) {
				tagLists = list;
			}
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tagLists.size() + 1;
		}

		@Override
		public TagWrapper getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TagHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_homepage_tag, null);
				holder = new TagHolder();
				holder.ivTagImg = (ImageView) convertView.findViewById(R.id.iv_tag_img);
				holder.tvTagText = (TextView) convertView.findViewById(R.id.tv_tag_text);
				holder.rlRoot = (RelativeLayout) convertView.findViewById(R.id.rl_tag_root);
				convertView.setTag(holder);
				
				ViewUtils.setHeight(holder.ivTagImg, 116);
				ViewUtils.setHeight(holder.rlRoot, 162);
				holder.tvTagText.setTextSize(DisplayUtil.textGetSizeSp(context, 30));
				ViewUtils.setMarginBottom(holder.tvTagText, 8);
			} else {
				holder = (TagHolder) convertView.getTag();
			}
			TagWrapper tagWrapper;
			if (position < tagLists.size()) {
				tagWrapper = tagLists.get(position);
				holder.rlRoot.setBackgroundResource(R.drawable.bg_item_tag);
				holder.tvTagText.setTextColor(0xff212324);
			} else {
				tagWrapper = new TagWrapper();
				tagWrapper.tagId = -1;
				tagWrapper.drawableRes = R.drawable.add_icon;
				tagWrapper.tagDesc = "添加模块";
				holder.rlRoot.setBackgroundResource(R.drawable.bg_item_tag_add);
				holder.tvTagText.setTextColor(0xffffffff);
			}
			
			holder.rlRoot.setTag(tagWrapper);
			holder.rlRoot.setOnClickListener(HomePageFragment.this);
			holder.tvTagText.setText(tagWrapper.tagDesc);
//			imageLoader.displayImage("", holder.ivTagImg, imageLoaderOptions);
			holder.ivTagImg.setImageResource(tagWrapper.drawableRes);
			
			return convertView;
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_ADD_MODULE) {
				if (data != null) {
					ArrayList<Integer> idLists = data.getIntegerArrayListExtra("selectedmodule");
					
					tagLists.clear();
					if (idLists != null && idLists.size() != 0) {
						for (Integer id:idLists) {
							tagLists.add(ModuleUtils.findModules(id));
						}
					}
					
					int gridHeight = getGridViewHeight(tagGridView);
					ViewUtils.setHeightPixel(tagGridView, gridHeight);
					tagAdapter.notifyDataSetChanged();
					
					if (gridHeight + DisplayUtil.getSize(getActivity(), 12) + DisplayUtil.getSize(getActivity(), 96) + statusHeight > dm.heightPixels) {
						rlDiscoverBottom.setVisibility(View.VISIBLE);
					} else {
						rlDiscoverBottom.setVisibility(View.GONE);
					}
				}
			}/*else if(requestCode == MusicMainPageActivity.BACK_FROM_DETAIL){
				MusicService.getInstance().setCurMusicId(musicElems.get(0).media_id);
			}*/
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
