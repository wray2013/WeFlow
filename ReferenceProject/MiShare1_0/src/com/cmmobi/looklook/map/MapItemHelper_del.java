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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import cn.zipper.framwork.core.ZApplication;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;


/**
 * MapItemHelper 用于计算和获取聚合后的视频条目
 * 
 * @author zhangwei
 *  
 * */
public class MapItemHelper_del {
	
	private final static String TAG = "MapItemHelper_del";

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
	
	private static MapItemHelper_del ins;
	
	private HashMap<String, ArrayList<MyDiary>> nearVideo_map;

	private HashMap<String, ArrayList<MyDiary>> recommond_map;
	
	private MapItemHelper_del(){
		nearVideo_map = new HashMap<String, ArrayList<MyDiary>>();
		recommond_map = new HashMap<String, ArrayList<MyDiary>>();
	}
	
	public static MapItemHelper_del getInstance(){
		if(ins==null){
			ins = new MapItemHelper_del();
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
			if(it.latitude==null || it.latitude.equals("") || it.longitude==null || it.longitude.equals("") || !locateAvailable(it)){
				Log.e(TAG, "latitude or longitude is null, continue");
				continue;
			}
			latitudeE6 = (int) (Double.valueOf(it.latitude)*1e6);
			longitudeE6 = (int) (Double.valueOf(it.longitude)*1e6);
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
			obj[index].latitudeE6 = (int) (Double.valueOf(near_item.get(0).latitude)*1e6);
			obj[index].longitudeE6 =(int) (Double.valueOf(near_item.get(0).longitude)*1e6);
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
			if(it.latitude==null || it.latitude.equals("") || it.longitude==null || it.longitude.equals("") || !locateAvailable(it)){
				Log.e(TAG, "latitude or longitude is null, continue");
				continue;
			}
			latitudeE6 = (int) (Double.valueOf(it.latitude)*1e6);
			longitudeE6 = (int) (Double.valueOf(it.longitude)*1e6);
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
			obj[index].latitudeE6 = (int) (Double.valueOf(recommend_item.get(0).latitude)*1e6);
			obj[index].longitudeE6 =(int) (Double.valueOf(recommend_item.get(0).longitude)*1e6);
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
				Log.e(TAG, "it it.latitude:" + it.latitude + " it.longitude:" + it.longitude + " pos:" + it.position  );
				if(it.latitude==null || it.latitude.equals("") || it.longitude==null || it.longitude.equals("")
						|| !locateAvailable(it)){
					Log.e(TAG, "latitude or longitude is null, continue");
					continue;
				}
				lat_tmp =  Double.valueOf(it.latitude);
				lon_tmp = Double.valueOf(it.longitude);
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
		} else if (items.size() == 1 && items.get(0).latitude != null
				&& !items.get(0).latitude.equals("")
				&& items.get(0).longitude != null
				&& !items.get(0).longitude.equals("")) {
			lat_tmp = Double.valueOf(items.get(0).latitude);
			lon_tmp = Double.valueOf(items.get(0).longitude);
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
				Log.e(TAG, "it it.latitude:" + it.latitude + " it.longitude:" + it.longitude + " pos:" + it.position  );
				if(it.latitude==null || it.latitude.equals("") || it.longitude==null || it.longitude.equals("")){
					Log.e(TAG, "latitude or longitude is null, continue");
					continue;
				}
				lat_tmp =  Double.valueOf(it.latitude);
				lon_tmp = Double.valueOf(it.longitude);
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
		} else if (items.size() == 1 && items.get(0).latitude != null
				&& !items.get(0).latitude.equals("")
				&& items.get(0).longitude != null
				&& !items.get(0).longitude.equals("")) {
			lat_tmp =  Double.valueOf(items.get(0).latitude);
			lon_tmp = Double.valueOf(items.get(0).longitude);
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
        paint.setTextSize(20); 

        Canvas canvas = new Canvas(bm);
//        canvas.drawText(text, bm.getWidth()/2, bm.getHeight()/2, paint);
        canvas.drawText(text, (float) (bm.getWidth()/2 - 3.5), bm.getHeight()/2 - 5, paint);

        return (Drawable)(new BitmapDrawable(res, bm));//modify
    }
	
	
	/**
	 *  显示在地图上显示聚合的信息条目
	 * 
	 * */
	public static Item[] showNearByItems(Context context, Handler handler, Resources res, MapView mapview, MyLocationOverlay myLocationOverlay,ArrayList<MyDiary> list, boolean init){
		if(list==null || list.size()==0){
			Log.e(TAG, "showItems the list is null");
			return null;
		}
		
		Drawable marker = res.getDrawable(R.drawable.del_pin);
		
	    MyItemizedOverlay ov = new MyItemizedOverlay(handler, mapview, marker, context);

	    //设置合适的缩放级别
	    Span result_span = calcNearVideoItemSpan(list);
	    GeoPoint gp_center=null;
	    if(result_span!=null){
	    	gp_center= new GeoPoint(result_span.clatE6,result_span.clonE6);
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
	    Item[] objs = MapItemHelper_del.getInstance().getNearByItems(list, (int) mapview.getZoomLevel());
	    
	    //将其加入到List<OverlayItem>
	    //mGeoList.clear();
	    ArrayList<OverlayItem> mGeoList = new ArrayList<OverlayItem>();


	    for(Item obj : objs){
		   	OverlayItem item= new OverlayItem(new GeoPoint(obj.latitudeE6, obj.longitudeE6), String.valueOf(obj.num), String.valueOf(obj.num));
		   	item.setMarker(getNumDrawable(res, R.drawable.del_map_pin, String.valueOf(obj.num)));
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
		
		Drawable marker = res.getDrawable(R.drawable.del_pin);
		
	    MyItemizedOverlay ov = new MyItemizedOverlay(handler, mapview, marker, context);
	    


	    //设置合适的缩放级别
	    Span result_span = MapItemHelper_del.calcMyDiarySpan(list);
	    GeoPoint gp_center=null;
	    if(result_span!=null){
	    	gp_center= new GeoPoint(result_span.clatE6,result_span.clonE6);
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
	    Item[] objs = MapItemHelper_del.getInstance().getRecommondItems(list, (int) mapview.getZoomLevel());
	    
	    //将其加入到List<OverlayItem>
	    ArrayList<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	    //mGeoList.clear();


	    for(Item obj : objs){
		   	OverlayItem item= new OverlayItem(new GeoPoint(obj.latitudeE6, obj.longitudeE6), String.valueOf(obj.num), String.valueOf(obj.num));
		   	item.setMarker(getNumDrawable(res, R.drawable.del_map_pin, String.valueOf(obj.num)));
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
    	if(item != null && item.latitude != null && item.longitude != null) {
    		if("4.9E-324".equalsIgnoreCase(item.latitude) ||
    				"4.9E-324".equalsIgnoreCase(item.longitude)) {
    			Log.e(TAG, "Baidu got location 4.9E-324, ignore it!");
    			ret = false;
    		}
    	}
    	return ret;
    }
	
}
