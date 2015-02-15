package com.cmmobi.looklook.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import cn.zhugefubin.maptool.ConverterTool;
import cn.zhugefubin.maptool.Point;
import cn.zipper.framwork.core.ZApplication;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;


/**
 * MapItemHelper 用于计算和获取聚合后的视频条目
 * 
 * @author zhangwei
 *  
 * */
public class MapItemHelper {
	
	private final static String TAG = "MapItemHelper";

	/**
	 *  在手机百度地图上，50m缩放级别，125m距离对应325px，
	 *  则480px对应185m，若将宽等分10份放置坐标，则每个坐标占据18.5m
	 *  
	 * */
	private final double ratio = 18.5/50;
	
	/**
	 *  1度对应111km
	 *  则latE6/lonE6每单位对应0.111m
	 *  
	 * */
	private final double degree_2_distance = 0.111/4 ;// 

	/**
	 *  缩放级别，取值范围是[3,19]
	 *  {3."50m",4."100m",5."200m",6."500m",7."1km",8."2km",9."5km",10."10km",11."20km",12."25km",13."50km",14."100km",15."200km",16."500km",17."1000km",18."2000km"} 
	 *  <li>3. 2000km, 4.1000km 5.500km 6.200km, 7.100km, 8.50km, 9.25km, 10.20km , 11.10km, 12.5km, 13.2km, 14.1km, 15.500m, 16.200m, 17.100m, 18.50m, 19.20m
	 * 
	 * */
	private final int[] zoom_contants = {2000000, 1000000, 500000, 200000, 100000, 50000, 25000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 20 };
	
	private static MapItemHelper ins;
	
	private HashMap<String, ArrayList<MyDiary>> nearVideo_map;

	private HashMap<String, ArrayList<MyDiary>> recommond_map;
	
	private final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
	
	
	private MapItemHelper(){
		nearVideo_map = new HashMap<String, ArrayList<MyDiary>>();
		recommond_map = new HashMap<String, ArrayList<MyDiary>>();
	}
	
	public static MapItemHelper getInstance(){
		if(ins==null){
			ins = new MapItemHelper();
		}
		return ins;
	}
	
	public Item[] getNearByItems(ArrayList<MyDiary> input, int zoomlevel){
		String key;
		int latitudeE6=0;
		int longitudeE6=0;
		if(zoomlevel>19 || zoomlevel<3){
			Log.e(TAG, "invaild zoomlevel:" + zoomlevel);
			zoomlevel = 19; //set default
		}
		
		nearVideo_map.clear();
		
		//calc the diver
		int diver = (int) (ratio * zoom_contants[zoomlevel-3]);
		for(MyDiary it : input){
			if(it.latitude_view==null || it.latitude_view.equals("") || it.longitude_view==null || it.longitude_view.equals("") || !locateAvailable(it)){
				Log.e(TAG, "latitude or longitude is null, continue");
				continue;
			}
			latitudeE6 = (int) (Double.valueOf(it.latitude_view)*1e6);
			longitudeE6 = (int) (Double.valueOf(it.longitude_view)*1e6);
			ArrayList<MyDiary> near_tmp_list;
			
    		key = String.valueOf((int)(latitudeE6*degree_2_distance/diver)) + "_" + 
					String.valueOf((int)(longitudeE6*degree_2_distance/diver));
/*			key = String.valueOf((int)(latitudeE6)) + "_" + 
					String.valueOf((int)(longitudeE6));*/
			
			Log.e(TAG, "key:" + key + " latitudeE6: " + latitudeE6 + " longitudeE6: " + longitudeE6);
			
			if(nearVideo_map.containsKey(key)){
				near_tmp_list = nearVideo_map.get(key);
				near_tmp_list.add(it);
			}else{
				near_tmp_list = new ArrayList<MyDiary>();
				near_tmp_list.add(it);
				nearVideo_map.put(key, near_tmp_list);
			}
		}
		
		int ItemSize = nearVideo_map.values().size();
		Item[] obj = new Item[ItemSize];
		for(int i=0; i<ItemSize; i++){
			obj[i] = new Item();
		}
		
		int index = 0;
		for(Iterator<ArrayList<MyDiary>> it=nearVideo_map.values().iterator(); it.hasNext(); ){
			ArrayList<MyDiary> near_item = it.next();
			obj[index].type = 1;
			obj[index].nearby_list.addAll(near_item);
			obj[index].latitudeE6 = (int) (Double.valueOf(near_item.get(0).latitude_view)*1e6);
			obj[index].longitudeE6 =(int) (Double.valueOf(near_item.get(0).longitude_view)*1e6);
			obj[index].num = near_item.size();
			index++;
			
		}
		
		return obj;
		
	}
	
	public Item[] getRecommondItems(ArrayList<MyDiary> input, int zoomlevel){
		String key;
		int latitudeE6=0;
		int longitudeE6=0;
		if(zoomlevel>19 || zoomlevel<3){
			Log.e(TAG, "invaild zoomlevel:" + zoomlevel);
			zoomlevel = 19; //set default
		}
		
		recommond_map.clear();
		
		//calc the diver
		int diver = (int) (ratio * zoom_contants[zoomlevel-3]);
		for(MyDiary it : input){
			if(it.latitude_view==null || it.latitude_view.equals("") || it.longitude_view==null || it.longitude_view.equals("") || !locateAvailable(it)){
				Log.e(TAG, "latitude or longitude is null, continue");
				continue;
			}
			latitudeE6 = (int) (Double.valueOf(it.latitude_view)*1e6);
			longitudeE6 = (int) (Double.valueOf(it.longitude_view)*1e6);
			ArrayList<MyDiary> recommend_tmp_list;
			
    		key = String.valueOf((int)(latitudeE6*degree_2_distance/diver)) + "_" + 
					String.valueOf((int)(longitudeE6*degree_2_distance/diver));
/*			key = String.valueOf((int)(latitudeE6)) + "_" + 
					String.valueOf((int)(longitudeE6));*/
			
			Log.e(TAG, "key:" + key + " latitudeE6: " + latitudeE6 + " longitudeE6: " + longitudeE6);
			
			if(recommond_map.containsKey(key)){
				recommend_tmp_list = recommond_map.get(key);
				recommend_tmp_list.add(it);
			}else{
				recommend_tmp_list = new ArrayList<MyDiary>();
				recommend_tmp_list.add(it);
				recommond_map.put(key, recommend_tmp_list);
			}
		}
		
		int ItemSize = recommond_map.values().size();
		Item[] obj = new Item[ItemSize];
		for(int i=0; i<ItemSize; i++){
			obj[i] = new Item();
		}
		
		int index = 0;
		for(Iterator<ArrayList<MyDiary>> it=recommond_map.values().iterator(); it.hasNext(); ){
			ArrayList<MyDiary> recommend_item = it.next();
			obj[index].type = 2;
			obj[index].recommond_list.addAll(recommend_item);
			obj[index].latitudeE6 = (int) (Double.valueOf(recommend_item.get(0).latitude_view)*1e6);
			obj[index].longitudeE6 =(int) (Double.valueOf(recommend_item.get(0).longitude_view)*1e6);
			obj[index].num = recommend_item.size();
			index++;
			
		}
		
		return obj;
	}
	
	/**
	 *  聚合条目，每一个条目代表多个视频的集合，根据缩放级别，把地理坐标相近聚合在一起
	 * */
	public class Item{
		public int type; // 1 nearby or 2 recommond
		public ArrayList<MyDiary> nearby_list;
		public ArrayList<MyDiary> recommond_list;
		public int latitudeE6;
		public int longitudeE6;
		public int num;
		
		public Item(){
			nearby_list = new ArrayList<MyDiary>();
			recommond_list = new ArrayList<MyDiary>();
			type = 0;
			latitudeE6 = 0;
			longitudeE6 = 0;
			num = 0;
		}
	} 
	
	public static Span calcNearVideoItemSpan(ArrayList<MyDiary> items){
		return calcNearVideoItemSpan(items, 0, items.size());
	}
	
	private static Span calcNearVideoItemSpan(ArrayList<MyDiary> items, int startIndex, int endIndex){
		double lat_min = 999;
		double lat_max = -999;
		double lon_min = 999;
		double lon_max = -999;
		
		double lat_tmp;
		double lon_tmp;
		Span ret = new Span(0, 0, 0, 0);
		

		if(items!=null && items.size()>1)
		{
			for(MyDiary it : items.subList(startIndex, endIndex)){
				Log.e(TAG, "it it.latitude:" + it.latitude_view + " it.longitude:" + it.longitude_view + " pos:" + it.position_view  );
				if(it.latitude_view==null || it.latitude_view.equals("") || it.longitude_view==null || it.longitude_view.equals("")
						|| !locateAvailable(it)){
					Log.e(TAG, "latitude or longitude is null, continue");
					continue;
				}
				lat_tmp =  Double.valueOf(it.latitude_view);
				lon_tmp = Double.valueOf(it.longitude_view);
				if(lat_min>lat_tmp){
					lat_min = lat_tmp;
				}
				if(lon_min>lon_tmp){
					lon_min=lon_tmp;
				}
				if(lat_max<lat_tmp){
					lat_max = lat_tmp;
				}
				if(lon_max<lon_tmp){
					lon_max = lon_tmp;
				}
				
			}
			
			ret = new Span((int)((lat_max- lat_min)*1e6), 
					(int)((lon_max- lon_min)*1e6),
					(int)((lat_max + lat_min)/2*1e6),
					(int)((lon_max + lon_min)/2*1e6));
		} else if (items.size() == 1 && items.get(0).latitude_view != null
				&& !items.get(0).latitude_view.equals("")
				&& items.get(0).longitude_view != null
				&& !items.get(0).longitude_view.equals("")) {
			lat_tmp = Double.valueOf(items.get(0).latitude_view);
			lon_tmp = Double.valueOf(items.get(0).longitude_view);
			ret.clatE6 = (int) (lat_tmp * 1e6);
			ret.clonE6 = (int) (lon_tmp * 1e6);
			ret.latSpanE6 = 1000;
			ret.longSpanE6 = 1000;
		} else {
			ret = null;
		}
		

		return ret;
	}
	
	public static Span calcMyDiarySpan(ArrayList<MyDiary> items){
		return calcMyDiarySpan(items, 0, items.size());
	}
	
	private static Span calcMyDiarySpan(ArrayList<MyDiary> items, int startIndex, int endIndex){
		double lat_min = 999;
		double lat_max = -999;
		double lon_min = 999;
		double lon_max = -999;
		
		double lat_tmp;
		double lon_tmp;
		Span ret = new Span(0, 0, 0, 0);
		

		if(items!=null && items.size()>1)
		{
			for(MyDiary it : items.subList(startIndex, endIndex)){
				Log.e(TAG, "it it.latitude:" + it.latitude_view + " it.longitude:" + it.longitude_view + " pos:" + it.position_view  );
				if(it.latitude_view==null || it.latitude_view.equals("") || it.longitude_view==null || it.longitude_view.equals("")){
					Log.e(TAG, "latitude or longitude is null, continue");
					continue;
				}
				lat_tmp =  Double.valueOf(it.latitude_view);
				lon_tmp = Double.valueOf(it.longitude_view);
				if(lat_min>lat_tmp){
					lat_min = lat_tmp;
				}
				if(lon_min>lon_tmp){
					lon_min=lon_tmp;
				}
				if(lat_max<lat_tmp){
					lat_max = lat_tmp;
				}
				if(lon_max<lon_tmp){
					lon_max = lon_tmp;
				}
				
			}
			
			ret = new Span((int)((lat_max- lat_min)*1e6), 
					(int)((lon_max- lon_min)*1e6),
					(int)((lat_max + lat_min)/2*1e6),
					(int)((lon_max + lon_min)/2*1e6));
		} else if (items.size() == 1 && items.get(0).latitude_view != null
				&& !items.get(0).latitude_view.equals("")
				&& items.get(0).longitude_view != null
				&& !items.get(0).longitude_view.equals("")) {
			lat_tmp =  Double.valueOf(items.get(0).latitude_view);
			lon_tmp = Double.valueOf(items.get(0).longitude_view);
			ret.clatE6 = (int)(lat_tmp*1e6);
			ret.clonE6 = (int)(lon_tmp*1e6);
			ret.latSpanE6 = 1000;
			ret.longSpanE6 = 1000;
		}else{
			ret = null;
		}
		

		return ret;
	}
	
	
	public static Drawable getNumDrawable(Resources res, int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(res, drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint(); 
        paint.setStyle(Style.FILL);  
        paint.setColor(Color.WHITE); 
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(24);
        Rect boundrect = new Rect();
        paint.getTextBounds(text, 0, text.length(), boundrect);
        int txtHeight = boundrect.height();

        Canvas canvas = new Canvas(bm);
//        canvas.drawText(text, bm.getWidth()/2, bm.getHeight()/2, paint);
        canvas.drawText(text, (float) (bm.getWidth() * 20.5 / 49), (float) (bm.getHeight() * 17.5 / 65 + txtHeight / 2), paint);

        return (Drawable)(new BitmapDrawable(res, bm));//modify
    }
	
	
	/**
	 *  显示在地图上显示聚合的信息条目
	 * 
	 * */
	public static Item[] showNearByItems(Context context, Handler handler, Resources res, MapView mapview, MyLocationOverlay myLocationOverlay,ArrayList<MyDiary> list, boolean init){
		if(list==null || list.size()==0){
			Log.e(TAG, "showItems the list is null");
			mapview.getOverlays().clear();
			mapview.refresh();
			return null;
		}
		
		Point p = new Point();
		ConverterTool ct=new ConverterTool();
		
		Drawable marker = res.getDrawable(R.drawable.del_pin);
		
	    MyItemizedOverlay ov = new MyItemizedOverlay(handler, mapview, marker, context);

	    //设置合适的缩放级别
	    Span result_span = calcNearVideoItemSpan(list);
	    GeoPoint gp_center=null;
	    if(result_span!=null){
//	    	gp_center= new GeoPoint(result_span.clatE6,result_span.clonE6);
	    	p = ct.GG2BD((double) (result_span.clonE6 / 1e6), (double) (result_span.clatE6 / 1e6));
	    	gp_center = new GeoPoint((int)(p.getLatitude()*1e6), (int)(p.getLongitude()*1e6));
	    }else{
	    	Log.e(TAG, "result_span is null");
	    	return null;
	    }
	    
	    if(init){
			try {
				mapview.getController().zoomToSpan(result_span.latSpanE6,result_span.longSpanE6);
				mapview.getController().setCenter(gp_center);
				mapview.getController().animateTo(gp_center);
			} catch (NullPointerException e) {
				Log.e(TAG, "Baidu Map Internal NullPointer");
				Toast.makeText(ZApplication.getInstance().getApplicationContext(), "百度地图缩放出错，请刷新重试！", Toast.LENGTH_LONG).show();
	    	}
	    }
	    
	    //mapview.getController().setZoom(19);
	    //得到聚合的Item数组
	    Item[] objs = MapItemHelper.getInstance().getNearByItems(list, (int) mapview.getZoomLevel());
	    
	    //将其加入到List<OverlayItem>
	    //mGeoList.clear();
	    ArrayList<OverlayItem> mGeoList = new ArrayList<OverlayItem>();

	    for(Item obj : objs){
			p = ct.GG2BD((double) (obj.longitudeE6 / 1e6), (double) (obj.latitudeE6 / 1e6));
		   	OverlayItem item= new OverlayItem(new GeoPoint((int)(p.getLatitude()*1e6), (int)(p.getLongitude()*1e6))/*new GeoPoint(obj.latitudeE6, obj.longitudeE6)*/, String.valueOf(obj.num), String.valueOf(obj.num));
		   	item.setMarker(getNumDrawable(res, R.drawable.map_pin, String.valueOf(obj.num)));
		   	
		   	mGeoList.add(item);
	    }

	    Log.e(TAG, "show items num:" + objs.length + " zoomLevel:" + mapview.getZoomLevel());
	    mapview.getOverlays().clear();
	    
	    //加上自己的位置图层
	    mapview.getOverlays().add(myLocationOverlay);
	    //显示MyItemizedOverlay并设置中心
	    mapview.getOverlays().add(ov);
	    //将List<OverlayItem>加入到MyItemizedOverlay
	    ov.addItem(mGeoList);
//	    for(OverlayItem item : mGeoList){
//	    	ov.addItem(item);
//	    }
	    
	    mapview.refresh();

//	    mapview.getController().setCenter(gp_center);
//	    mapview.getController().animateTo(gp_center);
	    return objs;
	}
	
	/**
	 *  显示在地图上显示聚合的信息条目
	 * 
	 * */
	public static Item[] showRecommondItems(Context context, Handler handler, Resources res, MapView mapview, ArrayList<MyDiary> list, boolean init){
		if(list==null || list.size()==0){
			Log.e(TAG, "showItems the list is null");
			return null;
		}
		
		Point p = new Point();
		ConverterTool ct=new ConverterTool();
		
		Drawable marker = res.getDrawable(R.drawable.del_pin);
		
	    MyItemizedOverlay ov = new MyItemizedOverlay(handler, mapview, marker, context);
	    
	    //设置合适的缩放级别
	    Span result_span = MapItemHelper.calcMyDiarySpan(list);
	    GeoPoint gp_center=null;
	    if(result_span!=null){
//	    	gp_center= new GeoPoint(result_span.clatE6,result_span.clonE6);
	    	p = ct.GG2BD((double) (result_span.clonE6 / 1e6), (double) (result_span.clatE6 / 1e6));
	    	gp_center = new GeoPoint((int)(p.getLatitude()*1e6), (int)(p.getLongitude()*1e6));
	    }else{
	    	Log.e(TAG, "result_span is null");
	    	return null;
	    }
	    
	    if(init){
		    mapview.getController().zoomToSpan(result_span.latSpanE6, result_span.longSpanE6);
		    mapview.getController().setCenter(gp_center);
		    mapview.getController().animateTo(gp_center);
	    }


	    
	    //mapview.getController().setZoom(19);
	    //得到聚合的Item数组
	    Item[] objs = MapItemHelper.getInstance().getRecommondItems(list, (int) mapview.getZoomLevel());
	    
	    //将其加入到List<OverlayItem>
	    ArrayList<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	    //mGeoList.clear();


	    for(Item obj : objs){
	    	p = ct.GG2BD((double) (obj.longitudeE6 / 1e6), (double) (obj.latitudeE6 / 1e6));
		   	OverlayItem item= new OverlayItem(new GeoPoint((int)(p.getLatitude()*1e6), (int)(p.getLongitude()*1e6))/*new GeoPoint(obj.latitudeE6, obj.longitudeE6)*/, String.valueOf(obj.num), String.valueOf(obj.num));
		   	item.setMarker(getNumDrawable(res, R.drawable.map_pin, String.valueOf(obj.num)));
		   	mGeoList.add(item);
	    }

	    Log.e(TAG, "show items num:" + objs.length + " zoomLevel:" + mapview.getZoomLevel());
	    
	    mapview.getOverlays().clear();
	    //显示MyItemizedOverlay并设置中心
	    mapview.getOverlays().add(ov);
	    //将List<OverlayItem>加入到MyItemizedOverlay
		ov.addItem(mGeoList);
	    
	    //加上自己的位置图层,recommendvideo不需要
	    // --- mapview.getOverlays().add(myLocationOverlay);
	    mapview.refresh();

//	    mapview.getController().setCenter(gp_center);
//	    mapview.getController().animateTo(gp_center);
	    
	    return objs;
	}
	
    public static boolean locateAvailable(MyDiary item) {
    	boolean ret = true;
    	if(item != null && item.latitude_view != null && item.longitude_view != null) {
    		if("4.9E-324".equalsIgnoreCase(item.latitude_view) ||
    				"4.9E-324".equalsIgnoreCase(item.longitude_view)) {
    			Log.e(TAG, "Baidu got location 4.9E-324, ignore it!");
    			ret = false;
    		}
    		if("0.0".equalsIgnoreCase(item.latitude_view) ||
    				"0.0".equalsIgnoreCase(item.longitude_view)) {
    			Log.e(TAG, "No Geo Information, ignore it!");
    			ret = false;
    		}
    	}
    	return ret;
    }
	
    /**
     * 他人主页判断有无合法经纬度的日记来决定是否显示中国地图
     * @return
     */
    public static boolean needToShowFootmark(ArrayList<MyDiary> list) {
    	if(list != null && list.size() > 0) {
    		for(MyDiary diary : list) {
    			if(locateAvailable(diary)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
}
